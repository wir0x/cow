(function () {
    'use strict';

    angular
        .module('buho-tracking')
        .controller('DeviceManagementController', DeviceManagementController);

    function DeviceManagementController($scope, $log, dataService, $translate) {
        console.log("deviceManagementCtrl");

        initializer();

        $scope.prepareNewDevice = function () {
            $scope.device = {};
            getDeviceTypeList();
        };

        $scope.deletePositions = function (device) {
            console.log("removePosition ", device);
            dataService.deletePositions(device.id).then(function (data) {
                console.log("data ", data);
                var successMessage = $translate.instant('device.management.success.delete.positions');
                setSuccessMessage(successMessage);
            })
        };

        $scope.prepareEditDevice = function (device) {
            console.log("prepareEditDevice ", device);
            getDeviceTypeList();
            $scope.device = device;
        };

        $scope.updateDataDevice = function () {
            findDeviceType();
            $scope.device.deviceTypeId = $scope.deviceType.id;
            $scope.device.nameDeviceType = $scope.deviceType.description;
            $scope.device.hasBattery = $scope.deviceType.battery;
            $scope.device.sosAlarm = $scope.deviceType.alarmSOS;
            $scope.device.batteryAlarm = $scope.deviceType.alarmBattery;
        };

        $scope.registerDevice = function () {
            console.log("$scope.devices ", $scope.device);
            var deviceId = $scope.device.id;
            dataService.registerDeviceForBackend($scope.device).then(
                function (data) {
                    console.log("data ", data);
                    $scope.device.id = data;
                    $log.info("deviceId", deviceId);

                    if (deviceId == undefined) {
                        $scope.devices.push($scope.device);
                    }

                    var deviceLabel = $translate.instant('device.management.company.message.device');
                    var successMessage = $translate.instant('device.management.register.device.message.success');
                    setSuccessMessage(deviceLabel + $scope.device.imei + successMessage);
                }, function (error) {

                    var errorMessage = $translate.instant('device.management.register.device.message.error');
                    setErrorMessage(errorMessage);
                    console.log('registerDevice Error: ' + error)
                })
        };


        $scope.isValidDeviceForm = function (form) {
            return !form.$valid;
        };

        $scope.blockSpace = function (e) {
            console.log("event ", e);
            var key_code = e.which;
            if (key_code == 32) {
                e.preventDefault();
            }
        };

        $scope.allowOnlyNumbers = function (e) {
            var a = [];
            var key_code = e.which;

            for (var i = 48; i < 58; i++) {
                a.push(i);
            }

            if (!(a.indexOf(key_code) >= 0 || key_code == 44)) {
                e.preventDefault();
            }
        };

        $scope.deleteDeviceForBackend = function (device) {
            dataService.deleteDeviceForBackend(device.id)
                .then(function () {
                    $scope.devices.splice($scope.devices.indexOf(device), 1);
                    var successMessage = $translate.instant('device.management.delete.device.message.success');
                    setSuccessMessage(successMessage);
                }, function (error) {
                    console.log("deleteDeviceForBackend Error: ", error);
                    var errorMessage = $translate.instant('device.management.delete.device.message.error');
                    setWarningMessage(error.data);

                });
        };

        function getDeviceTypeList() {
            dataService.findDeviceType().success(function (data) {
                $scope.deviceTypeList = data;
                console.log("$scope.deviceTypeList", $scope.deviceTypeList);
                //$scope.device.deviceTypeId = $scope.deviceTypeList[0].id;
            });
        }

        function findDeviceType() {
            var dtl = $scope.deviceTypeList;
            for (var i = 0; i < dtl.length; i++) {
                if (dtl[i].id == $scope.device.deviceTypeId) {
                    console.log("device type ", dtl[i]);
                    $scope.deviceType = dtl[i];
                }
            }
        }

        function initializer() {
            dataService.findAllDevicesForBackend().success(function (data) {
                $scope.devices = data;
                console.log("devices", $scope.devices);
            });
        }
    }
})();



