(function () {

    'use strict';

    angular
        .module('buho-tracking')
        .controller('AccountManagementController', AccountManagementController);

    //////////

    function AccountManagementController($scope, $log, dataService, $translate) {

        initializer();

        $scope.prepareNewAccount = prepareNewAccount;
        $scope.prepareEditAccount = prepareEditAccount;
        $scope.registerNewAccount = registerNewAccount;
        $scope.changePasswordUserAccountManagement = changePasswordUserAccountManagement;
        $scope.arePasswordsEquals = arePasswordsEquals;
        $scope.isValid = isValid;
        $scope.isValidAccountForm = isValidAccountForm;
        $scope.updateAccount = updateAccount;
        $scope.deleteAccount = deleteAccount;
        $scope.loadDevices = loadDevices;
        $scope.addDevice = addDevice;
        $scope.removeDevice = removeDevice;
        $scope.saveDeviceAssigned = saveDeviceAssigned;
        $scope.updateStatusName = updateStatusName;
        $scope.allowOnlyNumbers = allowOnlyNumbers;
        $scope.blockSpace = blockSpace;

        function initializer() {
            $scope.devicesAccount = [];
            $scope.freeDevices = [];
            $scope.device = {};
            $scope.account = {};
            dataService.findAllAccounts().success(function (data) {
                $scope.accounts = data;
            });
            dataService.findFreeDevices().success(function (data) {
                $scope.freeDevices = data;
                $scope.loading = false;
            });
        }


        function prepareNewAccount() {
            console.log("prepareNewAccount");
            $scope.account = {};
            $scope.account.status = true;
        }

        function prepareEditAccount(account) {
            console.log('account: ', account);
            $scope.account = account;
        }

        function registerNewAccount() {
            $scope.account.statusName = "Habilitado";
            dataService.createAccount($scope.account).then(
                function (data) {
                    console.log("data ", data);
                    $scope.account.id = data[0];
                    $scope.account.user.id = data[1];
                    var accountName = $translate.instant('device.management.account.label'),
                        successMessage = $translate.instant('account.management.register.account.message.success');
                    setSuccessMessage(accountName + $scope.account.nameAccount + successMessage);
                    $scope.accounts.push($scope.account);

                }, function (error) {
                    setErrorMessage("Error al crear cuenta");
                    console.log("registerNewAccount Error: ", error);
                })
        }

        function changePasswordUserAccountManagement(user) {
            user.id = $scope.account.user.id;
            var request = {
                userId: user.id,
                newPassword: user.passwordConfirm,
                confirmPassword: user.passwordConfirm
            };
            console.log("changePasswordUserAccountManagement", user);
            dataService.resetPasswordUser(request)
                .then(function () {
                    var message = $translate.instant('account.management.reset.password.message.success');
                    setSuccessMessage(message);
                }, function (error) {
                    var errorMessage = $translate.instant("account.management.update.password.message.error");
                    setErrorMessage(errorMessage + "\n" + error.data);
                    console.log("changePasswordUserAccountManagement Error: ", error);
                });
        }

        function arePasswordsEquals(form) {
            return angular.equals(form.password.$modelValue, form.passwordConfirm.$modelValue);
        }

        function isValid(form) {
            if (!$scope.arePasswordsEquals(form)) {
                return true;
            }

            return !form.$valid;
        }

        function isValidAccountForm(form) {
            return !form.$valid;
        }

        function updateAccount(form) {
            dataService.updateAccount($scope.account.id, $scope.account).then(function () {
                var accountName = $translate.instant('device.management.account.label'),
                    successMessage = $translate.instant('account.management.update.account.message.success');
                setSuccessMessage(accountName + $scope.account.nameAccount + successMessage)

            }, function (error) {
                console.error("update account: ", error);
                var errorMessage = $translate.instant('account.management.update.account.message.error');
                setErrorMessage(errorMessage);
            })
        }

        function deleteAccount() {
            dataService.deleteAccount($scope.account.id)
                .then(function () {
                        var accountName = $translate.instant('device.management.account.label');
                        $scope.accounts.splice($scope.accounts.indexOf($scope.account), 1);
                        setSuccessMessage(accountName + $scope.account.nameAccount + " eliminada correctamente")
                    }, function (error) {
                        var errorMessage = $translate.instant('account.management.update.account.message.error');
                        setErrorMessage(errorMessage);
                        console.log(error);
                    }
                )
        }

        function loadDevices(account) {
            $log.info('loadFreeDevices', account);
            $scope.devicesAccount = [];
            $scope.account = account;
            $scope.loading = true;
            loadAccountDevices();
        }

        function loadAccountDevices() {
            dataService.findDevicesByAccountId($scope.account.id).success(
                function (data) {
                    console.log("find device by account", data);
                    $scope.devicesAccount = data;
                    $scope.loading = false;
                });
        }

        function addDevice(device) {
            $log.info('addDevice', device);

            var _device = {
                id: device.id,
                imei: device.imei,
                phoneNumber: device.phoneNumber,
                battery: device.battery,
                name: device.name,
                accountId: $scope.account.id,
                accountName: $scope.account.nameAccount,
                status: device.status
            };

            if (_device.name != undefined) {
                $scope.devicesAccount.push(_device);
                $scope.freeDevices.splice($scope.freeDevices.indexOf(device), 1);
            }
        }

        function removeDevice(device) {
            $log.info('removeDevice');

            var _device = {
                id: device.id,
                imei: device.imei,
                phoneNumber: device.phoneNumber,
                battery: device.battery,
                name: device.name,
                accountId: null,
                accountName: null,
                status: device.status
            };

            $scope.freeDevices.push(_device);
            $scope.devicesAccount.splice($scope.devicesAccount.indexOf(device), 1);
        }

        function saveDeviceAssigned(account) {
            console.log('free devices', $scope.freeDevices);
            console.log('account ', $scope.devicesAccount);
            console.log(' device account', $scope.account);

            dataService.updateDeviceAssignment($scope.devicesAccount, $scope.account.id).then(function () {
                var successMessage = $translate.instant('account.management.update.account.message.success');
                setSuccessMessage(successMessage)
            }, function (error) {
                var errorMessage = $translate.instant('account.management.update.account.message.error');
                setErrorMessage(errorMessage + error.data);
                $log.error("updateAccount Error: ", error);
            })
        }

        function updateStatusName() {
            if ($scope.account.status) {
                $scope.account.statusName = "Habilitado"
            } else {
                $scope.account.statusName = "Inactivo"
            }
        }

        function allowOnlyNumbers(e) {
            console.log(e);
            var a = [];
            var key_code = e.which;

            for (var i = 48; i < 58; i++) {
                a.push(i);
            }

            if (!(a.indexOf(key_code) >= 0 || key_code == 44)) {
                e.preventDefault();
            }
        }

        function blockSpace(e) {
            console.log("event ", e);
            var key_code = e.which;
            if (key_code == 32) {
                e.preventDefault();
            }
        }

    }
})();



