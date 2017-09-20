angular
    .module('buho-tracking')
    .controller('DeviceConfigController', DeviceConfigController)
    .controller('DeviceConfigDialogController', DeviceConfigDialogController);


function DeviceConfigController($scope, dataService, $modal, $window, $translate) {
    console.log("deviceConfigCtrl");

    dataService.findDevices().success(function (data) {
        console.log(data);
        $scope.devices = data;
        $scope.copyDevices = deepCopy(data);
    });

    function deepCopy(obj) {
        if (Object.prototype.toString.call(obj) === '[object Array]') {
            var out = [], i = 0, len = obj.length;
            for (; i < len; i++) {
                out[i] = arguments.callee(obj[i]);
            }
            return out;
        }
        if (typeof obj === 'object') {
            var out = {}, i;
            for (i in obj) {
                out[i] = arguments.callee(obj[i]);
            }
            return out;
        }
        return obj;
    }

    $scope.undoCurrentDevice = function (device) {
        console.log("undoCurrentDevice");
        for (var i = 0; i < $scope.devices.length; i++) {
            if ($scope.devices[i].id == device.id) {

                //Undo nombre del dispositivo input
                $('#name-' + device.id).val($scope.copyDevices[i].name);
                //Undo el nombre que aparece en el panel-title del accordion
                $('#label-' + device.id).text($scope.copyDevices[i].name);

                //Undo el color del color picker
                $('#color-' + device.id).attr('color', '#' + $scope.copyDevices[i].color);
                $('#color-' + device.id).attr('value', $scope.copyDevices[i].color);

                //Undo el icono y color que aparece en el color.picker
                $('#icon-' + device.id + ' > i').attr('class', 'fa ' + $scope.copyDevices[i].icon);
                $('#icon-' + device.id + ' > input').attr('value', $scope.copyDevices[i].icon);
                //Undo el icono y color que aparece en el panel-title
                $('#iconLogo-' + device.id).attr('class', 'fa ' + $scope.copyDevices[i].icon);
                $('#iconLogo-' + device.id).attr('style', 'color: #' + $scope.copyDevices[i].color);


                var span = $('#color-' + device.id).nextAll('div.input-group-btn').first().children().children().first();
                $(span).css('background-color', '#' + $scope.copyDevices[i].color);
                $('#icon-' + device.id).val($scope.copyDevices[i].icon);

                if ($scope.copyDevices[i].sosMails != '[object Object]') {
                    $('#emailAlerts-' + device.id).val($scope.copyDevices[i].sosMails);
                } else {
                    $('#emailAlerts-' + device.id).val('');
                }
                if ($scope.copyDevices[i].sosCellphones != '[object Object]') {
                    $('#smsAlerts-' + device.id).val($scope.copyDevices[i].sosCellphones);
                } else {
                    $('#smsAlerts-' + device.id).val('');
                }
                if ($scope.copyDevices[i].batteryMails != '[object Object]') {
                    $('#batteryMails-' + device.id).val($scope.copyDevices[i].batteryMails);
                } else {
                    $('#batteryMails-' + device.id).val('');
                }
                if ($scope.copyDevices[i].batteryCellphones != '[object Object]') {
                    $('#batteryCellphones-' + device.id).val($scope.copyDevices[i].batteryCellphones);
                } else {
                    $('#batteryCellphones-' + device.id).val('');
                }
            }

            //Renova el content del panel-title cuando el usuario entra un nuevo input
            $('#name-' + device.id).on('input', function () {
                $('#label-' + device.id).text($('#name-' + device.id).val());
            });

            //Renova el content del logo a dentro de panel-title cuando el usuario entra un nuevo input
            $('#color-' + device.id).change(function () {
                console.log('color change');
                $('#iconLogo-' + device.id).attr('style', 'color: #' + device.color);
            });
        }
    };

    $scope.updateDevice = function (device, form) {
        console.log("update device: " + device.color);
        console.log(device);
        var arrayDevices = [device];
        dataService.updateDevice(arrayDevices)
            .then(function (data) {
                var message_good = $translate.instant('control.message.good');
                var message_success = $translate.instant('control.message.success.fence');
                setSuccessMessage("<b>" + message_good + device.name + message_success + "</b>");
                form.$setPristine();

                // we need to get the count as setPristine can take a bit of time to propagate
                var dirtyCount = $("#deviceForm-" + device.id).find(".ng-dirty").length;
                if ($("#deviceConfigForm").find(".ng-dirty").length <= dirtyCount) {
                    $scope.deviceConfigForm.$setPristine();
                }
            }, function (error) {
                console.log("Error saving device: ", error);
                setErrorMessage(error.data);
            });
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

    $scope.blockSpace = function (e) {
        console.log("event ", e);
        var key_code = e.which;
        if (key_code == 32) {
            e.preventDefault();
        }
    };


    var updateAllDevices = function () {
        console.log("update device: " + $scope.devices);
        dataService.updateDevice($scope.devices)
            .then(function (data) {
                var message_good = $translate.instant('control.message.good');
                var message_success = $translate.instant('control.message.delete.success');
                setSuccessMessage("<b>" + message_good + device.name + message_success + "</b>");

                // we need to get the count as setPristine can take a bit of time to propagate
                $scope.deviceConfigForm.$setPristine();

            }, function (error) {
                console.log("Error saving devices: ", error);
                setErrorMessage(error.data);
            });
    };

    $scope.$on('$locationChangeStart', function (event, next) {
        if ($scope.deviceConfigForm.$dirty) {
            event.preventDefault();
            var modalInstance = $modal.open({
                templateUrl: 'views/dialogs/device-config-dialog.html',
                controller: 'deviceConfigDialogCtrl'
            });

            modalInstance.result.then(function () {
                console.log("save items");
                dataService.updateDevice($scope.devices)
                    .then(function (data) {
                        var message_good = $translate.instant('control.message.good');
                        var fence_names = $translate.instant('lbl.devices');
                        var message_success = $translate.instant('control.message.success.fence');
                        setSuccessMessage("<b>" + message_good + fence_names + message_success + "</b>");

                        // we need to get the count as setPristine can take a bit of time to propagate
                        $scope.deviceConfigForm.$setPristine();
                        $window.location.href = next;

                    }, function (error) {
                        console.log("Error saving devices: ", error);
                        setErrorMessage(error.data);
                    });
            }, function () {
                console.log("lose changes");
                $scope.deviceConfigForm.$setPristine();
                $window.location.href = next;
            });
        }
    });
}

function DeviceConfigDialogController($scope, $modalInstance) {
    $scope.saveChanges = function () {
        $modalInstance.close();
    };

    $scope.loseChanges = function () {
        $modalInstance.dismiss('cancel');
    };
}

