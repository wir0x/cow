(function () {
    'use strict';

    angular
        .module('buho-tracking')
        .controller('AccountStatusController', AccountStatusController);

    function AccountStatusController($scope, dataService, $translate, $rootScope, $location, $timeout) {
        console.log("stateAccountCtrl");

        $rootScope.location = $location;
        $scope.fromStateAccount = true;
        $scope.paymentHistorial = {};
        $scope.paymentHistorialSuccess = [];
        $scope.paymentHistorialFailed = [];
        $scope.checkboxes = [];
        $scope.rowToDeleteObj = {};
        $scope.rowToDelete = [];
        $scope.rowToDeleteUi = [];
        $scope.copyPaymentHistorial = {};
        $scope.checkAlls = false;

        init();

        function init() {
            dataService.findBillStatus().success(function (data) {
                console.log("findBillStatus", data);
                $scope.userBill = data;
            });

            dataService.getSubscriptionsList().success(function (data, status, headers, config) {
                $scope.subscriptions = data;
                console.log("data ", data);
                $scope.subscriptionsActives = [];
                $scope.subscriptionsPending = [];
                $scope.subscriptionsInactives = [];


                //Se define las subscriptions activas
                var arrayLength = data.length;

                for (var i = 0; i < arrayLength; i++) {
                    if (data[i].subscriptionStatus == "ENABLED" && data[i].deviceStatus == "ENABLED") {
                        $scope.subscriptionsActives.push(data[i]);
                    } else if (data[i].subscriptionStatus == "DISABLED" ||
                        data[i].subscriptionStatus == "ENABLED" && data[i].deviceStatus == "PENDING") {
                        $scope.subscriptionsPending.push(data[i]);
                    } else if (data[i].subscriptionStatus == "EXPIRED") {
                        $scope.subscriptionsInactives.push(data[i]);
                    } else {
                        console.log("On device not appear check the code");
                    }
                }
            });


            dataService.paymentHistorial().success(function (data) {
                console.log("paymentHistorial ", data);
                $scope.paymentHistorial = data;
                console.log("paymentHistorial ", $scope.paymentHistorial.length);
                $timeout(function () {
                    $('body').css("height", $('.main-box').height() + 70);
                    $('#content-wrapper').css("height", $('.main-box').height() + 70);
                });

            });

        }

        $scope.deleteRow = function () {
            var len = $scope.paymentHistorial.length;
            for (var i = 0; i < len; i++) {
                if ($scope.checkboxes[$scope.paymentHistorial[i].id] == true) {
                    var obj = {
                        id: $scope.paymentHistorial[i].id,
                        deviceName: $scope.paymentHistorial[i].deviceName,
                        line: $scope.paymentHistorial[i].line,
                        amount: $scope.paymentHistorial[i].amount,
                        status: $scope.paymentHistorial[i].status,
                        creationDate: $scope.paymentHistorial[i].creationDate
                    };

                    $scope.rowToDelete.push(obj);
                    $scope.rowToDeleteUi.push($scope.paymentHistorial[i].id);
                }
            }

            console.log("$scope.rowToDelete--> ", $scope.rowToDelete);
            console.log("$scope.rowToDelete.length ", $scope.rowToDelete.length);
            if ($scope.rowToDelete.length > 0) {
                dataService.deletePaymentHistorial($scope.rowToDelete)
                    .then(function () {
                            console.log("success ");
                            deleteFromList();
                        }, function (error) {
                            console.log(error);
                        }
                    );
            }
        };

        var deleteFromList = function () {
            var len = $scope.paymentHistorial.length;
            console.log("delete from list", len);
            clone($scope.copyPaymentHistorial, $scope.paymentHistorial);
            for (var i = 0; i < len; i++) {
                if ($scope.rowToDeleteUi.contains($scope.copyPaymentHistorial[i].id)) {
                    console.log("$scope.rowToDelete contains ", $scope.copyPaymentHistorial[i].id);
                    $scope.paymentHistorial.splice($scope.paymentHistorial.indexOf($scope.copyPaymentHistorial[i]), 1);
                }
            }
            $scope.rowToDelete = [];
        };

        function clone(destination, source) {
            for (var property in source) {
                if (typeof source[property] === "object" && source[property] !== null && destination[property]) {
                    clone(destination[property], source[property]);
                } else {
                    destination[property] = source[property];
                }
            }
        }

        Array.prototype.contains = function (obj) {
            var i = this.length;
            while (i--) {
                if (this[i] === obj) {
                    return true;
                }
            }
            return false;
        };


        $scope.checkAll = function () {
            $scope.rowToDelete = [];
            var len = $scope.paymentHistorial.length;
            if ($scope.checkAlls) {
                $scope.checkAlls = false;
                for (var i = 0; i < len; i++) {
                    $scope.checkboxes[$scope.paymentHistorial[i].id] = false;
                }
            } else {
                $scope.checkAlls = true;
                for (var i = 0; i < len; i++) {
                    $scope.checkboxes[$scope.paymentHistorial[i].id] = true;
                }
            }
        };


        $scope.changeCheckBox = function (id) {
            console.log("changeCheckBox ", $scope.checkboxes);
            $scope.rowToDelete = [];
            $scope.checkAlls = false;
        };

        $scope.saveNewBillsData = function () {
            console.log("saveNewBillsData", $scope.userBill);
            var send = $scope.userBill;
            dataService.updateBillStatus(send)
                .then(function () {
                    console.log("saveNewBillsData fine");
                    var successMessage = $translate.instant('account.management.update.data.message.success');
                    setSuccessMessage(successMessage);
                }, function (error) {
                    var errorMessage = $translate.instant("account.management.update.data.message.error");
                    setErrorMessage(errorMessage + "\n" + error.data);
                    console.log("changePasswordUserAccountManagement Error: ", error);
                });
        };
    }
})();


