// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
var name = require('cloud/date.js');
var Segment = Parse.Object.extend("segments");
var Update = Parse.Object.extend("updates");

Parse.Cloud.define("hello", function(request, response) {
  response.success("Hello world!");
});

Parse.Cloud.afterSave("segments", function(request) {
    segments = request.object;
    segmentsHash = {
        userID: object.get("userID"),
        mode: object.get("mode"),
        distance: object.get("distance"),
        interval: object.get("interval"),
        timestamp: object.get("timestamp"),
        count: object.get("count")
    };
    
    Parse.Cloud.run('hello', segmentsHash, {
        success: function(result) {
            response.success(result);
        },
        error: function(error) {
            response.error(error);
        }
    });
});

Parse.Cloud.define("saveSegments", function (request, response) {
    userID = request.params.userID;
    modes = request.params.mode.split(",");
    distances = request.params.distance.split(",");
    intervals = request.params.interval.split(",");
    timestamps = request.params.timestamp.split(",");
    count = request.params.count;
    
    if (modes.length != distances.length || distances.length != intervals.length || intervals.length != timestamps.length || timestamps.length != count) {
        errorString = "saveSegments: strings do not all have the same number of elements. ";
        errorString = errorString + "modes is length " + modes.length + ", ";
        errorString = errorString + "distances is length " + distances.length + ", ";
        errorString = errorString + "intervals is length " + intervals.length + ", ";
        errorString = errorString + "timestamps is length " + timestamps.length + ", ";
        errorString = errorString + "count is " + count;
        console.log(errorString);
        response.error(errorString);
        return;
    }
    
    for (i = 0; i < count; i++) {
        mode = modes[i];
        distance = float(distances[i]);
        interval = parseInt(intervals[i], 10);
        timestamp = timestamps[i]
        
        var segment = new Segment();
        segme
        segment.set("mode", mode);
        segment.set("distance", distance);
        segment.set("interval", interval);
        segment.set("timestamp", timestamp);
        
        segment.save(null, {
            success: function(segment) {
            },
            error: function(segment, error) {
                errorString = "saveSegments: segment save failed on error " + error;
                console.log(errorString);
                response.error(errorString);
                return;
            }
        });
    }
    
    response.success("saveSegment: all segments given to db for saving. DONE!");
});

Parse.Cloud.define("mergeSegments", function(request, response) {
    today = Date.today().toString("yyyy-MM-dd HH:mm:ss");
    yesterday = Date.today().addDays(-1);
    userID = request.params.userID;
    
    var query = new Parse.Query(Update);
    query.equalTo("userID", userID);
    query.find({
        success: function(results) {
            if (results.length == 0) {
                //never merged before for this user, so merge
            }
            else if (results[0].get("timestamp") < today) {
                //last merge was before today, so merge
            }
            else {
                // last merge was today, so don't merge
            }
        },
        error: function(error) {
            response.error("mergeSegments failed on getting last update time: " + error);
        }
    });
    
    var query = new Parse.Query(Segment);
    query.equalTo("userID", userID);
    query.lessThan("timestamp", today);
    query.greaterThanOrEqualTo("timestamp", yesterday);
});