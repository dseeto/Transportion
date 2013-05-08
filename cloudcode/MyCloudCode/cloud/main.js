// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
var datejs = require('cloud/date.js');
var Segment = Parse.Object.extend("SeparatedSegments");
var MergedSegment = Parse.Object.extend("MergedSegments");
var Update = Parse.Object.extend("updates");
var Stats = Parse.Object.extend("Stats");
var statsCalculator = require('cloud/statsCalculator.js');

Parse.Cloud.define("hello", function(request, response) {
  response.success("Hello world!");
});

Parse.Cloud.afterSave("Segments", function(request) {
    segments = request.object;
    segmentsHash = {
        userID: segments.get("userID"),
        mode: segments.get("mode"),
        distance: segments.get("distance"),
        interval: segments.get("interval"),
        timestamp: segments.get("timestamp"),
    };
    
    saveSegments(segmentsHash);
});

function saveSegments(request) {
    userID = request.userID;
    modes = request.mode;
    distances = request.distance;
    intervals = request.interval;
    timestamps = request.timestamp;
    
    if (modes.length != distances.length || distances.length != intervals.length || intervals.length != timestamps.length) {
        errorString = "saveSegments: strings do not all have the same number of elements. ";
        errorString = errorString + "modes is length " + modes.length + ", ";
        errorString = errorString + "distances is length " + distances.length + ", ";
        errorString = errorString + "intervals is length " + intervals.length + ", ";
        errorString = errorString + "timestamps is length " + timestamps.length + ", ";
        console.log(errorString);
        response.error(errorString);
        return;
    }
    
    listOfSegments = []
    for (i = 0; i < modes.length; i++) {
        mode = modes[i];
        distance = parseFloat(distances[i]);
        interval = parseInt(intervals[i], 10);
        timestamp = timestamps[i]
        
        var segment = new Segment();
        segment.set("mode", mode);
        segment.set("distance", distance);
        segment.set("interval", interval);
        segment.set("timestamp", timestamp);
        segment.set("userID", userID);
        
        listOfSegments.push(segment);
        
        console.log("saveSegments: created segment " + JSON.stringify(segment.toJSON()));
    }
    
    Parse.Object.saveAll(listOfSegments, {
        success: function(list) {
            for (var i = 0; i < list.length; i++) {
                result = "saveSegments: successfully saved " + JSON.stringify(list[i].toJSON());
                console.log(result);
            }
            
            Parse.Cloud.run('mergeSegments', {}, {
                success: function(result) {
                    console.log("mergeSegments succeeded: " + JSON.stringify(result));
                },
                error: function(error) {
                    console.log("mergeSegments failed " + JSON.stringify(error));
                }
            });
        },
        error: function(error) {
            result = "saveSegments: saveList failed with error " + JSON.stringify(error)
            console.log(result);
        },
    });
};

function getTodayWithOffset() {
    todaySevenAM = datejs.add(datejs.today(), "hours", 7);
    yesterdaySevenAM = datejs.add(datejs.add(datejs.today(), "days", -1), "hours", 7);
    
    if (datejs.toString(datejs.now()) > datejs.toString(todaySevenAM)) {
        return todaySevenAM;
    }
    else {
        return yesterdaySevenAM;
    }
}

Parse.Cloud.define("mergeSegments", function(request, response) {
    today = getTodayWithOffset();
    yesterday = datejs.add(getTodayWithOffset(), "days", -1);
    todayLater = datejs.add(getTodayWithOffset(), "hours", 1);
    yesterdayLater = datejs.add(datejs.add(getTodayWithOffset(), "days", -1), "hours", 1);
    
    today = datejs.toString(today);
    yesterday = datejs.toString(yesterday);
    todayLater = datejs.toString(todayLater);
    yesterdayLater = datejs.toString(yesterdayLater);
    
    userID = request.params.userID;
    
    var query = new Parse.Query(Update);
    query.equalTo("userID", userID);
    query.find({
        // find the last update time
        success: function(updatesList) {
            console.log("find update list: result is " + updatesList.toString());
            if (updatesList.length == 0 || updatesList[0].get("timestamp") < today) {
                console.log("updatesList.length is 0, or its timestamp is before today");
                console.log("updatesList.length is " + updatesList.length);
                if (updatesList.length > 0) console.log("updatesList[0] timestamp is " + updatesList[0].get("timestamp"));
                //never merged before for this user, or haven't merged since yesterday, so merge
                console.log("finding segments between " + today + " and " + yesterday);
                var queryYesterday = new Parse.Query(Segment);
                queryYesterday.equalTo("userID", userID);
                queryYesterday.lessThan("timestamp", today);
                queryYesterday.greaterThanOrEqualTo("timestamp", yesterday);
                queryYesterday.find({
                    success: function(unmergedSegments) {
                        console.log("find unmerged segments success, result is " + unmergedSegments.toString());
                        console.log("find unmerged segments success, unmergedSegments.length is " + unmergedSegments.length);
                        if (unmergedSegments.length > 1) {
                            console.log("unmergedSegments.length > 1");
                            console.log("unmerged segments length is " + unmergedSegments.length);
                            // more than 1 segment yesterday, so we can still merge
                            // add all the segments into the correct list
                            segmentArrays = {car: [], bike: [], walk: []};
                            for (var i = 0; i < unmergedSegments.length; i++) {
                                segmentArrays[unmergedSegments[i].get("mode")].push(unmergedSegments[i])
                            }
                            
                            // remove the modes which only have 1 entry
                            console.log("segmentArrays[car].length is " + segmentArrays["car"].length);
                            if (segmentArrays["car"].length < 2) delete segmentArrays["car"];
                            console.log("segmentArrays[bike].length is " + segmentArrays["bike"].length);
                            if (segmentArrays["bike"].length < 2) delete segmentArrays["bike"];
                            console.log("segmentArrays[walk].length is " + segmentArrays["walk"].length);
                            if (segmentArrays["walk"].length < 2) delete segmentArrays["walk"];
                            
                            // if hash is empty, that means all modes only had 1 entry, no need merge
                            if (Object.keys(segmentArrays).length == 0) {
                                newUpdate = new Update();
                                newUpdate.set("userID", userID);
                                newUpdate.set("timestamp", todayLater);
                                newUpdate.save(null, {
                                    success: function(update) {
                                        console.log("saveNewUpdate: successfully saved " + JSON.stringify(update.toJSON()));
                                        // delete old update from the Updated table
                                        if (updatesList.length == 0) {
                                            console.log("nothing to merge, there's not more than 1 segment yesterday, no previous update found");
                                            response.success("nothing to merge, there's not more than 1 segment yesterday, no previous update found");
                                            return;
                                        }
                                        console.log("updatesList length is " + updatesList.length);
                                        console.log("updatesList[0] is " + JSON.stringify(updatesList[0].toJSON()));
                                        updatesList[0].destroy({
                                            success: function(update) {
                                                console.log("deleteUpdateList: successfully deleted " + JSON.stringify(update.toJSON()));
                                                response.success("nothing to merge, there's not more than 1 segment yesterday");
                                            },
                                            error: function(update, error) {
                                                resultString = "deleteUpdateList: error deleting " + JSON.stringify(update.toJSON()) + " with error " + JSON.stringify(error);
                                                console.log(resultString);  
                                                response.success("nothing to merge, there's not more than 1 segment yesterday");
                                            }
                                        });
                                    },
                                    error: function(update, error) {
                                        resultString = "saveNewUpdate: error saving " + JSON.stringify(update.toJSON()) + " with error " + JSON.stringify(error);
                                        console.log(resultString);
                                    },
                                });
                                return;
                            }
                            
                            // merge all the segments
                            mergedSegments = []
                            for (i in segmentArrays) {
                                segmentArray = segmentArrays[i];
                                console.log("segmentArray in segmentArrays is " + segmentArray.toString());
                                interval = 0;
                                distance = 0;
                                for (j in segmentArray) {
                                    segment = segmentArray[j];
                                    console.log("segment in segmentArray is " + JSON.stringify(segment.toJSON()));
                                    interval = interval + segment.get("interval");
                                    distance = distance + segment.get("distance");
                                }
                                var mergedSegment = new Segment();
                                mergedSegment.set("mode", segmentArray[0].get("mode"));
                                mergedSegment.set("distance", distance);
                                mergedSegment.set("interval", interval);
                                mergedSegment.set("timestamp", yesterdayLater);
                                mergedSegment.set("userID", segmentArray[0].get("userID"));
                                
                                mergedSegments.push(mergedSegment);
                            }
                            
                            // save the merged segments
                            Parse.Object.saveAll(mergedSegments, {
                                success: function(list) {
                                    for (var i = 0; i < list.length; i++) {
                                        result = "saveMergedSegments: successfully saved " + JSON.stringify(list[i].toJSON());
                                        console.log(result);
                                    }
                                    
                                    // save new update entry to update table
                                    newUpdate = new Update();
                                    newUpdate.set("userID", userID);
                                    newUpdate.set("timestamp", todayLater);
                                    newUpdate.save(null, {
                                        success: function(update) {
                                            console.log("saveNewUpdate: successfully saved " + JSON.stringify(update.toJSON()));
                                            // delete old update from the Updated table
                                            if (updatesList.length == 0) {
                                                deleteSegmentsSequentially(unmergedSegments, response);
                                                return;
                                            }
                                            updatesList[0].destroy({
                                                success: function(update) {
                                                    console.log("deleteUpdateList: successfully deleted " + JSON.stringify(update.toJSON()));
                                                    // delete the unmerged segments    
                                                    deleteSegmentsSequentially(unmergedSegments, response);
                                                },
                                                error: function(update, error) {
                                                    resultString = "deleteUpdateList: error deleting " + JSON.stringify(update.toJSON()) + " with error " + JSON.stringify(error);
                                                    console.log(resultString);
                                                    // delete the unmerged segments    
                                                    deleteSegmentsSequentially(unmergedSegments, response);
                                                }
                                            });
                                        },
                                        error: function(update, error) {
                                            resultString = "saveNewUpdate: error saving " + JSON.stringify(update.toJSON()) + " with error " + JSON.stringify(error);
                                            console.log(resultString);
                                            // delete the unmerged segments    
                                            deleteSegmentsSequentially(unmergedSegments, response);
                                        },
                                    });
                                    
                                },
                                error: function(error) {
                                    result = "saveMergedSegments: saveList failed with error " + JSON.stringify(error)
                                    console.log(result);
                                    response.error(result);
                                },
                            });
                        }
                        else {
                            console.log("unmerged segments length is 0");
                            // save new update entry to update table
                            newUpdate = new Update();
                            newUpdate.set("userID", userID);
                            newUpdate.set("timestamp", todayLater);
                            newUpdate.save(null, {
                                success: function(update) {
                                    console.log("saveNewUpdate: successfully saved " + JSON.stringify(update.toJSON()));
                                    // delete old update from the Updated table
                                    if (updatesList.length == 0) {
                                        console.log("nothing to merge, there's not more than 1 segment yesterday, no previous update found");
                                        response.success("nothing to merge, there's not more than 1 segment yesterday, no previous update found");
                                        return;
                                    }
                                    console.log("updatesList[0] is " + JSON.stringify(updatesList[0].toJSON()));
                                    updatesList[0].destroy({
                                        success: function(update) {
                                            console.log("deleteUpdateList: successfully deleted " + JSON.stringify(update.toJSON()));
                                            response.success("nothing to merge, there's not more than 1 segment yesterday");
                                        },
                                        error: function(update, error) {
                                            resultString = "deleteUpdateList: error deleting " + JSON.stringify(update.toJSON()) + " with error " + JSON.stringify(error);
                                            console.log(resultString);  
                                            response.success("nothing to merge, there's not more than 1 segment yesterday");
                                        }
                                    });
                                },
                                error: function(update, error) {
                                    resultString = "saveNewUpdate: error saving " + JSON.stringify(update.toJSON()) + " with error " + JSON.stringify(error);
                                    console.log(resultString);
                                },
                            });
                        }
                    },
                    error: function (error) {
                        response.error("mergeSegments failed on getting yesterday's segmets: " + error);
                    }
                });
            }
            else {
                // already merged, so don't merge
                response.success("nothing to merge, already merged today");
            }
        },
        error: function(error) {
            response.error("mergeSegments failed on getting last update time: " + JSON.stringify(error));
        }
    });
});

function deleteSegmentsSequentially(segments, response) {
    // if length is 0, we succeeded
    if (segments.length == 0) {
        response.success("deleteSegments: segment list to delete is length 0, assumed done!");
        return;
    }
    
    segment = segments.shift();
    
    segment.destroy({
        success: function(segment) {
            console.log("deleteSegments: sucessfully deleted " + JSON.stringify(segment.toJSON()));
            deleteSegmentsSequentially(segments, response);
        },
        error: function(segment, error) {
            resultString = "deleteSegments: error deleting " + JSON.stringify(segment.toJSON()) + " with error " + JSON.stringify(error);
            console.log(resultString);
            segments.unshift(segment);
            deleteSegmentsSequentially(segments);
        }
    });
};

Parse.Cloud.define("getMyStats", function(request, response) {
    todaySevenAM = datejs.add(datejs.today(), "hours", 7);
    yesterdaySevenAM = datejs.add(datejs.add(datejs.today(), "days", -1), "hours", 7);
    twoDaysAgoSevenAM = datejs.add(datejs.add(datejs.today(), "days", -2), "hours", 7);
    
    today = null;
    yesterday = null;
    
    console.log("now is " + datejs.toString(datejs.now()));
    
    if (datejs.toString(datejs.now()) > datejs.toString(todaySevenAM)) {
        today = todaySevenAM;
        yesterday = yesterdaySevenAM;
    }
    else {
        today = yesterdaySevenAM;
        yesterday = twoDaysAgoSevenAM;
    }
    
    yesterday = datejs.add(today, "days", -1);
    lastWeek = datejs.add(today, "days", -7);
    lastMonth = datejs.add(today, "days", -30);
    lastYear = datejs.add(today, "days", -365);
    
    userID = request.params.userID;
    
    var queryYesterday = new Parse.Query(Segment);
    queryYesterday.equalTo("userID", userID);
    queryYesterday.greaterThan("timestamp", lastYear);
    queryYesterday.find({
        success: function(segments) {
            console.log("getLastYearsSegments: succeeded. There are a total of " + segments.length + " segments");
            stats = statsCalculator.getStats(segments);
            
        },
        error: function(error) {
            console.log("getLastYearsSegments: failed on error " + JSON.stringify(error));
            response.error(error);
        }
    });
});

Parse.Cloud.define("getFriends", function(request, response) {
    userIDList = request.params.userIDs
    
    var query = new Parse.Query(Stats);
    query.find({
        success: function(usersList) {
            friendsList = [];
            for (var i = 0; i < usersList.length; i++) {
                userID = usersList[i].get("userID");
                if (userIDList.indexOf(userID) > -1) {
                    friendsList.push(usersList[i]);
                }
            }
            response.success(friendsList);
        },
        error: function(error) {
            response.error(JSON.stringify(error));
        }
    });
    
});

Parse.Cloud.define("getFriendStats", function(request, response) {
    userID = request.params.userID;
    
    var query = new Parse.Query(Stats);
    query.equalTo("userID", userID);
    query.find({
        success: function(statsList) { 
            if (statsList.length == 0) {
                response.error("no stats found");
                return;
            }
            response.success(statsList[0].get("stats"));
        },
        error: function(error) {
            response.error(JSON.stringify(error));
        }
    });
});