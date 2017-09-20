angular
    .module('buho-tracking')
    .controller('UserManagementController', UserManagementController);

function UserManagementController($scope, $modal, $window, dataService, $translate, $timeout) {
    $scope.isNewUser = false;
    $scope.changePassword = {};

    init();

    function init() {
        dataService.findAllGroups().success(function (data) {
            $scope.groups = data;
        });

        dataService.findAdminDevices().success(function (data) {
            console.log("devices: ", data);
            $scope.devices = data;
            $scope.devicesDeepCopy = deepCopy(data);

            dataService.findAllUsers().success(function (data) {
                $scope.users = data;
                console.log("users: ", $scope.users);
                $scope.firstIdFromList = $scope.users[0].id;
                console.log("firstIdFromList: ", $scope.firstIdFromList);
                $scope.userDeepCopy = deepCopy(data);

                var i, deepCopylistOfDId,
                    listOfDevicesId = [];
                $scope.listOfViewsByUserId = {};

                for (i = 0; i < $scope.users.length; i++) {
                    $scope.listOfViewsByUserId[$scope.users[i].id] = $scope.users[i].devices;
                }

                for (i = 0; i < $scope.devices.length; i++) {
                    listOfDevicesId.push($scope.devices[i].id);
                }

                $scope.devicesNameById = {};
                for (i = 0; i < $scope.devices.length; i++) {
                    $scope.devicesNameById[$scope.devices[i].id] = [$scope.devices[i].name,
                        $scope.devices[i].icon, $scope.devices[i].color];
                }

                var iscontain = false;
                $scope.listOfUnViewsByUserId = {};
                for (var key in $scope.listOfViewsByUserId) {

                    deepCopylistOfDId = deepCopy(listOfDevicesId);

                    var theArray = $scope.listOfViewsByUserId[key];

                    for (i = 0; i < listOfDevicesId.length; i++) {

                        var theDeviceId = listOfDevicesId[i];

                        for (var a = 0; a < theArray.length; a++) {
                            if (theArray[a] == theDeviceId) {
                                iscontain = true;
                            }
                        }
                        if (iscontain) {
                            removeA(deepCopylistOfDId, theDeviceId);
                        }

                        $scope.listOfUnViewsByUserId[key] = deepCopylistOfDId;
                        iscontain = false;
                    }
                }

                // Draw the body after angular finish to draw the list
                $timeout(function () {
                    $('body').css("height", $('#row-container').height() + 70);
                    $('#content-wrapper').css("height", $('#row-container').height() + 70);
                });
            });
        });
    }

    $scope.addToView = function (userId, deviceId) {
        console.log("addToView");
        console.log("addToView userId" + userId);
        console.log("addToView deviceId" + deviceId);
        $scope.listOfViewsByUserId[userId].unshift(deviceId);
        removeA($scope.listOfUnViewsByUserId[userId], deviceId);
    };

    $scope.addToUnView = function (userId, deviceId) {
        console.log("addToUnView userId " + userId);
        console.log("addToUnView deviceId " + deviceId);
        $scope.listOfUnViewsByUserId[userId].unshift(deviceId);
        removeA($scope.listOfViewsByUserId[userId], deviceId);
    };

    $scope.OnDragStart = function ($event) {
        event.dataTransfer.setData("text/plain", event.target.textContent);
        return true;
    };

    $scope.prepareNewUser = function () {
        if ($scope.users && $scope.users.length > 0 && $scope.users[$scope.users.length - 1].id == undefined) {
            return;
        }

        $scope.user = {};
        $scope.user.devices = [];
        $scope.user.roleId = $scope.groups[$scope.groups.length - 1].id;
        $scope.listOfUnViewsByUserId = $scope.devices;

        //collapse all ui
        for (var i = 0; i < $scope.users.length; i++) {
            $("#collapse-header-" + $scope.users[i].id).addClass("collapsed");
            $("#collapse-" + $scope.users[i].id).removeClass("in");
        }

        $scope.users.push($scope.user);
        $scope.isNewUser = true;
        $scope.userManagementForm.$setDirty();
    };

    $scope.deleteUser = function (user) {
        console.log("delete user", user.id);
        if (user.id) {
            dataService.deleteUser(user.id)
                .then(function (data) {
                    console.log("data: ", data);
                    $scope.users.splice($scope.users.indexOf(user), 1);
                    var message_good = $translate.instant('control.message.good');
                    var message_success = $translate.instant('control.message.delete.success');
                    setSuccessMessage("<b>" + message_good + user.name + message_success + "</b>");

                }, function (error) {
                    console.log("error: ", error);
                    setErrorMessage(error.data);
                });
            $('body,html').animate({scrollTop: 0}, 500);
        } else {
            var successMessage = $translate.instant('userManagement.user.delete.message.success');
            setSuccessMessage(successMessage);
            $scope.users.splice($scope.users.indexOf(user), 1);
        }
    };

    $scope.changeAdminPasswordUser = function (form, userSecurity) {
        var request = {
            userId: userSecurity.id,
            newPassword: form.newPassword.$viewValue,
            confirmPassword: form.passwordConfirm.$viewValue
        };
        dataService.changeAdminPasswordUser(request)
            .then(function (data) {
                cleanUser(form);
                setSuccessMessage("Contraseña modificada correctamente.");
            }, function (error) {
                console.error(error);
                setErrorMessage("Ocurrio un error al realizar un cambio:");
                cleanUser();
            });
    };

    $scope.$on('$locationChangeStart', function (event, next) {
        if ($scope.userManagementForm.$dirty) {
            event.preventDefault();
            var modalInstance = $modal.open({
                templateUrl: 'views/dialogs/device-config-dialog.html',
                controller: 'deviceConfigDialogCtrl'
            });

            modalInstance.result.then(function () {
                console.log("save items");
                dataService.updateUser($scope.users).then(function (data) {
                    console.log("data: ", data);
                    var message_good = $translate.instant('control.message.good'),
                        names = $translate.instant('lbl.users'),
                        message_success = $translate.instant('control.message.success.fence');
                    setSuccessMessage("<b>" + message_good + names + message_success + "</b>");
                    $scope.userManagementForm.$setPristine();
                    $window.location.href = next;
                }, function (error) {
                    console.log("error: ", error);
                    setErrorMessage(error.data);
                });
            }, function () {
                $scope.userManagementForm.$setPristine();
                $window.location.href = next;
            });
        }
    });

    $scope.undoCurrentUser = function (user) {
        var i, j;
        for (i = 0; i < $scope.users.length; i++) {
            if ($scope.users[i].id == user.id) {
                $scope.users[i] = $scope.userDeepCopy[i];
            }
        }

        for (j = 0; j < $scope.devices.length; j++) {
            if ($scope.devices[j].id == user.id) {
                $scope.devices[j] = $scope.devicesDeepCopy[j];
            }
        }
    };

    $scope.prepareEditUser = function (user) {
        $scope.user = user;
    };

    $scope.registerUser = function (user, form) {
        if (user.id) {
            updateUser(user, form);
            $('body,html').animate({scrollTop: 0}, 500);
        } else {
            createUser(user, form);
            $('body,html').animate({scrollTop: 0}, 500);
        }
    };

    $scope.isSelectedAll = function (devices) {
        return devices ? devices.length === $scope.devices.length : false;
    };

    $scope.selectAll = function ($event, devices) {
        var checkbox = $event.target;
        var action = (checkbox.checked ? 'add' : 'remove');
        for (var i = 0; i < $scope.devices.length; i++) {
            var device = $scope.devices[i];
            updateSelected(action, device.id, devices);
        }
    };

    $scope.close = function (form) {
        cleanUser(form);
    };

    $scope.arePasswordsEquals = function (form) {
        if (!form.newPassword || !form.passwordConfirm) {
            return;
        }
        return angular.equals(form.newPassword.$modelValue, form.passwordConfirm.$modelValue);
    };

    $scope.isBtnNewUserActive = function (form) {
        if (!form.$valid) {
            return true;
        }

        if (!form.newPassword || !form.passwordConfirm) {
            return true;
        }

        if (!$scope.arePasswordsEquals(form)) {
            return true;
        }

        return false;
    };

    $scope.isBtnChangePasswordActive = function (form) {
        if (!form.$valid) {
            return true;
        }

        if (!$scope.arePasswordsEquals(form)) {
            return true;
        }

        return false;
    };

    $scope.isFormValid = function (form) {
        if ($scope.isBtnNewUserActive(form)
            && !$scope.arePasswordsEquals(form)) {
            return true;
        }
        console.log("");
        return !form.$valid;
    };

    $scope.isUserFormValid = function (form) {
        return form.$valid;
    };

    $scope.blockSpace = function (e) {
        var key_code = e.which;
        if (key_code == 32) {
            e.preventDefault();
        }
    };

    var createUser = function (form) {
        dataService.registerUser($scope.user)
            .then(function (data) {
                console.log("data: ", data);
                $scope.user.id = data;
                var successMessage = $translate.instant('user.created.successfully'),
                    message_success = $translate.instant('control.message.success.fence');
                init();


                setSuccessMessage("<b>" + $scope.user.name + "</b>" + successMessage);
                // we need to get the count as setPristine can take a bit of time to propagate
                var dirtyCount = $("#userForm").find(".ng-dirty").length;
                if ($("#userManagementForm").find(".ng-dirty").length <= dirtyCount) {
                    $scope.userManagementForm.$setPristine();
                }
            }, function (error) {
                console.log("error: ", error);
                setWarningMessage(error.data);
            });
    };

    var updateUser = function (user, form) {
        console.log("update user", user);
        var arrayUsers = [user];
        dataService.updateUser(arrayUsers)
            .then(function (data) {
                var message_good = $translate.instant('control.message.good');
                var message_success = $translate.instant('control.message.success.fence');
                setSuccessMessage("<b>" + message_good + user.name + message_success + "</b>");
                form.$setPristine();

                // we need to get the count as setPristine can take a bit of time to propagate
                var dirtyCount = $("#userForm-" + user.id).find(".ng-dirty").length;
                if ($("#userManagementForm").find(".ng-dirty").length <= dirtyCount) {
                    $scope.userManagementForm.$setPristine();
                }
            }, function (error) {
                console.log("Error....: ", error.data.errorDescription);
                if (error.status == 422) {
                    setWarningMessage(error.data.errorDescription);
                }

            });
    };

    var updateSelected = function (action, id, devices) {
        if (action === 'add' && devices.indexOf(id) === -1) {
            devices.push(id);
        }
        if (action === 'remove' && devices.indexOf(id) !== -1) {
            devices.splice(devices.indexOf(id), 1);
        }
    };

    function cleanUser(form) {
        $scope.user = {};
        form.newPassword.$pristine = true;
        form.passwordConfirm.$pristine = true;
        init();
    }

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

    function removeA(arr) {
        var what, a = arguments, L = a.length, ax;
        while (L > 1 && arr.length) {
            what = a[--L];
            while ((ax = arr.indexOf(what)) !== -1) {
                arr.splice(ax, 1);
            }
        }
        return arr;
    }
}





