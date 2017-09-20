(function () {
    'use strict';

    angular
        .module('buho-tracking')
        .controller('DevicesStatusController', DevicesStatusController);

    function DevicesStatusController($scope, dataService, DTOptionsBuilder) {
        console.log("devicesStatusCtrl.....");

        initializer();

        $scope.dtOptions = DTOptionsBuilder.newOptions()
            .withDisplayLength(10)
            .withOption('bLengthChange', false);


        function initializer() {
            getDevicesStatus();
        }

        function getDevicesStatus() {
            dataService.getDeviceStatus().success(function (data) {
                $scope.devicesStatus = data;
                console.log("", $scope.devicesStatus);
            })
        }
    }
})();


