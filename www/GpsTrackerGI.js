var exec = require('cordova/exec');


var GpsTrackerGI = {
    getCoors : function (arg0, success, error) {
        exec(success, error, "GpsTrackerGI", "getCoors", [arg0]);
    },
	startService : function (arg0, success, error) {
        exec(success, error, "GpsTrackerGI", "startService", [arg0]);
    },
    stopService: function (arg0, success, error) {
        exec(success, error, "GpsTrackerGI", "stopService", [arg0])
    },
    echojs : function(arg0, success, error) {
        if (arg0 && typeof(arg0) === 'string' && arg0.length > 0) {
            success(arg0);
        } else {
            error('Empty message!');
        }
    }
};

module.exports = GpsTrackerGI;