cordova.define("com.devexpress.plugins.devextremeaddon.DevExtremeAddon", function(require, exports, module) { 
var devextremeaddon = {
    setup: function () {
        cordova.exec(null, null, "DevExtremeAddon", "setup", []);
    }
}

module.exports = devextremeaddon;
});
