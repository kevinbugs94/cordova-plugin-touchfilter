var exec = require('cordova/exec');

var TouchFilter = {
enable: function(success, error) {
exec(success, error, "TouchFilter", "enable", []);
}
};

module.exports = TouchFilter;
