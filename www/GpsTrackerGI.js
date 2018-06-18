var exec = require('cordova/exec');


var gpstrackergi = {
    getCoors : function (arg0, success, error) {
        exec(success, error, "gpstrackergi", "getCoors", [arg0]);
    },
	startService : function (arg0, success, error) {
        exec(success, error, "gpstrackergi", "startService", [arg0]);
    },
    stopService: function (arg0, success, error) {
        exec(success, error, "gpstrackergi", "stopService", [arg0])
    },
    echojs : function(arg0, success, error) {
        if (arg0 && typeof(arg0) === 'string' && arg0.length > 0) {
            success(arg0);
        } else {
            error('Empty message!');
        }
    }
};

module.exports = gpstrackergi;