var exec = require('cordova/exec');

var SERVICE = "TouchFilter";

var TouchFilter = {

    /**
     * Habilita la protección contra tapjacking.
     */
    enable: function(success, error) {

        exec(
            success || function(){},
            error || function(){},
            SERVICE,
            "enable",
            []
        );

    },

    /**
     * Deshabilita la protección.
     * (Preparado para futuras versiones Java)
     */
    disable: function(success, error) {

        exec(
            success || function(){},
            error || function(){},
            SERVICE,
            "disable",
            []
        );

    },

    /**
     * Devuelve si la protección está habilitada.
     */
    isEnabled: function(success, error) {

        exec(
            success || function(){},
            error || function(){},
            SERVICE,
            "isEnabled",
            []
        );

    },

    /**
     * Obtiene estadísticas del plugin.
     */
    getStatistics: function(success, error) {

        exec(
            success || function(){},
            error || function(){},
            SERVICE,
            "getStatistics",
            []
        );

    }

};


// Registro global
if (window.cordova) {

    window.cordova.plugins = window.cordova.plugins || {};

    window.cordova.plugins.TouchFilter = TouchFilter;

}

module.exports = TouchFilter;
