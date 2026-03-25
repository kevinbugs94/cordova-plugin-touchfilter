var exec = require('cordova/exec');
exports.enableFilter = function(success, error) {
   exec(success, error, "TouchFilter", "enableFilter", []);
};
exports.isOverlayEnabled = function(success, error) {
   exec(success, error, "TouchFilter", "isOverlayEnabled", []);
};