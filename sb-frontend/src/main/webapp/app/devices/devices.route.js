(function () {

    'use strict';

    angular
        .module('buho-tracking')
        .config(['$routeProvider', function ($routeProvider) {

            $routeProvider
                .when("/", {
                    redirectTo: 'current-position'
                })
                .when("/device-config", {
                    templateUrl: "app/devices/configuration/device-config.html",
                    controller: "DeviceConfigController",
                    title: 'Device Configuration'
                })
                .when("/device-management", {
                    templateUrl: "app/devices/management/device-management.html",
                    controller: "DeviceManagementController",
                    title: 'Device Management'
                })
                .when("/devices-status", {
                    templateUrl: "app/devices/status/devices-status.html",
                    controller: "DevicesStatusController",
                    title: 'Device Status'
                })
                .when("/add-device", {
                    templateUrl: "app/devices/registration/device-registration.html",
                    controller: "DeviceRegistrationController",
                    title: 'Add Device'
                })
                .otherwise({
                    redirectTo: '/error-404',
                    templateUrl: "common/errors/error-404.html"
                });
        }]);
})();