(function () {
    'use strict';

    angular
        .module("buho-tracking")
        .controller('ShoppingCartController', ShoppingCartController);

    function ShoppingCartController($scope, dataService, $rootScope) {
        console.log("shoppingCartCtrl");
        $scope.selectedOption = [];
        $scope.totalToPay = 0;
        $scope.totalMonth = 0;
        $scope.userBill = {};
        $scope.accountValid = false;
        $scope.isShoppingCartEmpty = false;
        $scope.tMspinner.Validate = false;
        $scope.userBill.saveBillsData = false;
        $('body').css('overflow', 'hidden');
        $(document).keypress(function (e) {
            $scope.accountValid = false;
        });
        $scope.listOfSelectedPlanes = {};

        init();

        function init() {
            dataService.findBillStatus().success(function (data) {
                console.log("findBillStatus", data);
                $scope.userBill = data;
            });

            dataService.findAllServicePlan().success(function (data) {
                console.log("servicePlans: ", data);
                $scope.servicePlans = data;
            });

            dataService.findShoppingCartList().success(function (data) {
                console.log("findShoppingCartList ", data);
                $scope.shoppingCart = data;
                $scope.calculateTotal();
            });
        }

        $scope.openModalTm = function () {
            if (!$scope.tMspinner.WaitingForPayment) {
                dataService.isPaymentProcessing().success(function (statusPay) {
                    console.log("statusPay ", statusPay);
                    if (statusPay.isInProcess == true) {
                        $scope.tMspinner.WaitingForPayment = true;
                        $('#tigoMoney').modal('toggle');
                        console.log("isPaymentProcessing from server  true");
                        console.log("recall the thread");
                        $scope.callVerificationPaymentThread(statusPay.id); // si un pago se esta processando recall the thread
                    } else {
                        $scope.tMspinner.WaitingForPayment = false;
                        $('#tigoMoney').modal('toggle');
                        console.log("isPaymentProcessing from server false");
                    }
                });
            } else {
                $('#tigoMoney').modal('toggle');
                console.log("isPaymentProcessing! false");
            }
        };


        $scope.tigoMoneyPayment = function () {
            $scope.tMspinner.Validate = true;
            if ($scope.userBill.saveBillsData) {
                saveBills();
            }

            dataService.isPaymentProcessing().success(function (statusPay) {
                console.log('isPaymentProcessing', statusPay);
                if (!statusPay.isInProcess) {
                    var scl = $scope.shoppingCart;
                    console.log("$scope.shoppingCart: ", $scope.shoppingCart);
                    var subscriptionPay = {deviceId: "", subscriptionId: "", servicePlanId: ""};
                    var subscriptionPayList = [];
                    for (var i = 0; i < scl.length; i++) {

                        subscriptionPay.deviceId = scl[i].device.id;
                        subscriptionPay.subscriptionId = scl[i].id;
                        subscriptionPay.servicePlanId = scl[i].servicePlan.id;
                        subscriptionPayList[i] = subscriptionPay;
                    }

                    var subscriptionPayTMDto = {
                        tigoPhoneNumber: $scope.userBill.phoneNumber,
                        tigoIdCardNumber: $scope.userBill.documentNumber,
                        nit: $scope.userBill.nit,
                        businessName: $scope.userBill.socialReason,
                        subscriptionPayList: subscriptionPayList
                    };

                    console.log("subscriptionPayTMDto ", subscriptionPayTMDto);


                    dataService.validationTM(subscriptionPayTMDto).then(function (id) {
                        console.log("validationTMId: ", id);
                        $scope.tMspinner.Validate = false;
                        $scope.callVerificationPaymentThread(id);
                    }, function (error) {
                        console.log("error: ", error);
                        $scope.tMspinner.Validate = false;
                        setErrorMessage(error.data);
                    });
                } else {
                    $scope.tMspinner.Validate = false;
                    $('#tigoMoney').modal('hide');
                }
            });
        };

        var saveBills = function () {
            console.log("saveNewBillsData", $scope.userBill);
            var newBill = $scope.userBill;
            dataService.updateBillStatus(newBill)
                .then(function () {
                    console.log("saveNewBillsData done");
                }, function (error) {
                    console.log("changePasswordUserAccountManagement Error: ", error);
                });
        };

        var createListOfSelectedPlans = function () {
            for (var key in $scope.shoppingCartItems) {
                if ($scope.shoppingCartItems.hasOwnProperty(key)) {
                    $scope.listOfSelectedPlanes[$scope.shoppingCartItems[key].subscriptionId] =
                        $scope.planes[$scope.shoppingCartItems[key].servicePlanId - 2];
                    console.log("subscription selected is ", $scope.shoppingCartItems[key].subscriptionId);
                }
            }
            $('body').css("overflow-y", "hidden !important");
        };


        $scope.calculateTotal = function () {
            $scope.totalMonth = 0;
            $scope.totalToPay = 0;
            var sc = $scope.shoppingCart;
            for (var i = 0; i < sc.length; i++) {
                $scope.totalMonth += getPlanById(sc[i].servicePlan.price).durationMonths;
                $scope.totalToPay += sc[i].servicePlan.price;
            }
        };

        function getPlanById(price) {
            var sp = $scope.servicePlans;

            for (var i = 0; i < sp.length; i++)
                if (price == sp[i].price)
                    return sp[i];
            return null;
        }

        $scope.deleteItem = function (index, subscriptionId, deviceName) {
            if (!$scope.tMspinner.WaitingForPayment) {
                dataService.isPaymentProcessing().success(function (statusPay) {
                    if (statusPay.isInProcess) {
                        console.log("isPaymentProcessing true");
                        $scope.tMspinner.WaitingForPayment = true;
                    } else {
                        console.log("isPaymentProcessing false");
                        $scope.tMspinner.WaitingForPayment = false;
                        $scope.shoppingCart.splice(index, 1);
                        dataService.deleteItemFromShoppingCart(subscriptionId).then(function () {
                                console.log("delete Done ");
                                $scope.listOfSelectedPlanes = {};
                                createListOfSelectedPlans();
                                $scope.calculateTotal();
                                $scope.getShoppingCartItems();
                                if ($scope.shoppingCart.NumOfItems == 1) {
                                    $rootScope.goToPage("/add-device");
                                    $scope.isShoppingCartEmpty = true;
                                }
                                setAlertDownSuccess("Dispositivo <br/>" + deviceName + " <br/> borrado del carrito.");
                            }, function (error) {
                                //var errorMessage = $translate.instant('account.management.update.account.message.error');
                                //setErrorMessage(errorMessage);
                                console.log(error);
                            }
                        );
                    }
                });

            } else {
                console.log("paymentInProcessFromThread " + $scope.tMspinner.WaitingForPayment);
            }
        };

    }
})();
