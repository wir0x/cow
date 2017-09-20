(function () {
    'use strict';

    //test app
    angular.module("buho-tracking")
        .controller('SubscriptionsManagementController', SubscriptionsManagementController);

    function SubscriptionsManagementController($scope, $log, dataService, $translate, $timeout, $rootScope, $route) {
        console.log("subscriptionsManagementCtrl");

        $scope.maxSize = 5;
        $scope.numPerActivesPage = 5;
        $scope.currentActivePage = 1;
        $scope.numPerPendingPage = 5;
        $scope.currentPendingPage = 1;
        $scope.numPerInactivesPage = 5;
        $scope.currentInactivePage = 1;

        $scope.listOfShoppingCartItems = {};
        $scope.pendingTableOpenClose = true;
        $scope.activeTableOpenClose = true;
        $scope.inactiveTableOpenClose = true;

        $scope.activeOrderBy = "-deviceName";
        $scope.pendingOrderBy = "-deviceName";
        $scope.inactiveOrderBy = "-deviceName";

        $scope.deviceName = $translate.instant('configDevice.device.namePlaceholder');
        $scope.status = $translate.instant('lbl.status');
        $scope.subscriptionsActives = [];
        $scope.subscriptionsPending = [];
        $scope.subscriptionsInactives = [];
        $scope.emptySubscriptionList = false;
        $scope.list = {};

        $scope.acceptField = {};
        $scope.acceptField = false;
        $scope.currentIndex;

        var items = [];


        init();


        function init() {
            dataService.findAllServicePlan().success(function (data) {

                $scope.servicePlanList = data;

                dataService.getSubscriptionsList().success(function (data) {
                    console.log("Subscriptions data....: ", data);

                    $scope.subscriptions = data;
                    //Se define las subscriptions activas
                    var arrayLength = $scope.subscriptions.length;
                    if (arrayLength != 0) {
                        for (var i = 0; i < arrayLength; i++) {
                            if ($scope.subscriptions[i].subscriptionStatus == "ENABLED" && $scope.subscriptions[i].deviceStatus == "ENABLED") {
                                $scope.subscriptionsActives.push($scope.subscriptions[i]);
                            } else if ($scope.subscriptions[i].subscriptionStatus == "DISABLED" ||
                                $scope.subscriptions[i].subscriptionStatus == "ENABLED" && $scope.subscriptions[i].deviceStatus == "PENDING") {
                                $scope.subscriptionsPending.push($scope.subscriptions[i]);
                            } else if ($scope.subscriptions[i].subscriptionStatus == "EXPIRED") {
                                $scope.subscriptionsInactives.push($scope.subscriptions[i]);
                            } else {
                                console.log("On device not appear check the code");
                            }
                        }
                        setPagination();
                        showMaxOption();
                    } else {
                        $scope.emptySubscriptionList = true;
                    }

                    $timeout(function () {
                        $('body').css("height", $('.jumbotron').height() + 50);
                        $('#content-wrapper').css("height", $('.jumbotron').height() + 50);
                    });

                    $log.info("subscriptions", $scope.subscriptions);
                });

            });

        }

        var showMaxOption = function () {
            if ($scope.subscriptionsActives.length > $scope.maxSize) {
                $scope.showMaxActiveTable = true;
            } else {
                $scope.showMaxActiveTable = false;
            }
            if ($scope.subscriptionsPending.length > $scope.maxSize) {
                $scope.showMaxPendingTable = true;
            } else {
                $scope.showMaxPendingTable = false;
            }
            if ($scope.subscriptionsInactives.length > $scope.maxSize) {
                $scope.showMaxInactiveTable = true;
            } else {
                $scope.showMaxInactiveTable = false;
            }
        };


        $scope.planSelected = function (deviceId, planId) {

            var currentRadioButton = document.getElementById("optionsRadios-" + deviceId + "-" + planId);
            if ($scope.listOfShoppingCartItems.hasOwnProperty(deviceId) && $scope.listOfShoppingCartItems[deviceId] == planId) {
                currentRadioButton.checked = false;
                delete $scope.listOfShoppingCartItems[deviceId];
            } else if ($scope.listOfShoppingCartItems.hasOwnProperty(deviceId)) {
                delete $scope.listOfShoppingCartItems[deviceId];
                $scope.listOfShoppingCartItems[deviceId] = planId;
            } else {
                $scope.listOfShoppingCartItems[deviceId] = planId;
            }

            console.log($scope.listOfShoppingCartItems);
            if (Object.keys($scope.listOfShoppingCartItems).length != 0) {
                showAddItemButton();
            } else {
                hideAddItemButton();
            }
        };


        $scope.addSelectionToShoppingCart = function () {
            if (!$scope.tMspinner.WaitingForPayment) { //Avoid add item to shopping cart if payment is in process
                dataService.isPaymentProcessing().success(function (statusPay) {
                    console.log("isPaymentProcessing", statusPay);
                    if (statusPay.isInProcess) {
                        $('#goToShoppingCart').modal('toggle');
                        $scope.tMspinner.WaitingForPayment = true;
                        console.log("recall the thread");
                        $scope.callVerificationPaymentThread(statusPay.id); // si un pago
                    } else {
                        $('#goToShoppingCart').modal('toggle');
                        $scope.tMspinner.WaitingForPayment = false;
                        for (var key in $scope.listOfShoppingCartItems) {
                            if ($scope.listOfShoppingCartItems.hasOwnProperty(key)) {
                                $scope.list["deviceId"] = key;
                                $scope.list["planId"] = $scope.listOfShoppingCartItems[key];
                                items.push($scope.list);
                                $scope.list = {};
                            }
                        }
                        console.log("items.length" + items.length);
                        console.log("items.key" + JSON.stringify(items));

                        $scope.numOfItemsAdded = Object.keys($scope.listOfShoppingCartItems).length;
                        if (items.length > 0) {
                            addSelection();
                        }
                    }
                });
            } else {
                $('#goToShoppingCart').modal('toggle');
            }
        };

        var addSelection = function () {
            var message;
            if (Object.keys($scope.listOfShoppingCartItems).length == 1) {
                message = Object.keys($scope.listOfShoppingCartItems).length + ' dispositivo ha sido <br> agregado al carrito'
            } else {
                message = Object.keys($scope.listOfShoppingCartItems).length + ' dispositivos han sido <br> agregado al carrito'
            }
            dataService.addItemsToShoppingCart(items).then(function (data) {
                console.log("addToShoppingCart Done")
                $scope.getShoppingCartItems(); // restore the shopping-cart nav-bar
                //$scope.showNotification(message);
                $scope.listOfShoppingCartItems = {};
                $('input[type=radio]').each(function () {
                    var isChecked = this.checked ? true : false;
                    if (isChecked) {
                        $(this).closest("tbody").addClass('opacity-blue');
                        $(this).parent().parent().html('Agregado al carrito');
                        $(this).parent().css('text-align', 'left !important');
                        $(this).parent().css('background-color', 'aliceblue !important');
                    }
                });
                hideAddItemButton();
                items = [];
            }, function (error) {
                console.log("addToShoppingCart error");
                setErrorMessage("Error " + error.data)
            });

            $scope.getShoppingCartItems();
        };

        var showAddItemButton = function () {
            $(".addToShoppingCartButton").css("opacity", "1");
            $(".addToShoppingCartButton").css("filter", "alpha(opacity=100)");
            $(".addToShoppingCartButton").css("pointer-events", "visible");
        };

        var hideAddItemButton = function () {
            $(".addToShoppingCartButton").css("opacity", ".4");
            $(".addToShoppingCartButton").css("filter", "alpha(opacity=40)");
            $(".addToShoppingCartButton").css("pointer-events", "none");
        };

        var setPagination = function () {
            //Pagination por activas
            $scope.$watch('currentActivesPage + numPerActivesPage', function () {
                var begin = (($scope.currentActivePage - 1) * $scope.numPerActivesPage)
                    , end = begin + $scope.numPerActivesPage;
                if ($scope.subscriptionsActives != null) {
                    $scope.filteredSubscriptionsActives = $scope.subscriptionsActives.slice(begin, end);
                }
                console.log("currentActivePage " + $scope.filteredSubscriptionsActives);
            });

            //Pagination por pendientes
            $scope.$watch('currentPendingPage + numPerPendingPage', function () {
                var begin = (($scope.currentPendingPage - 1) * $scope.numPerPendingPage)
                    , end = begin + $scope.numPerPendingPage;
                if ($scope.subscriptionsPending != null) {
                    $scope.filteredSubscriptionsPending = $scope.subscriptionsPending.slice(begin, end);
                }
            });

            //Pagination por inactivas
            $scope.$watch('currentInactivePage + numPerInactivesPage', function () {
                var begin = (($scope.currentInactivePage - 1) * $scope.numPerInactivesPage)
                    , end = begin + $scope.numPerInactivesPage;
                if ($scope.subscriptionsInactives != null) {
                    $scope.filteredSubscriptionsInactives = $scope.subscriptionsInactives.slice(begin, end);
                }
            });
        };

        $scope.showHidePendingTable = function () {
            $scope.pendingTableOpenClose = !$scope.pendingTableOpenClose;
        };

        $scope.showHideActiveTable = function () {
            $scope.activeTableOpenClose = !$scope.activeTableOpenClose;
        };

        $scope.showHideInactiveTable = function () {
            $scope.inactiveTableOpenClose = !$scope.inactiveTableOpenClose;
        };


        //SORTING THE THREE TABLES

        $scope.sortByDeviceNameActive = function () {
            console.log("sortByDeviceName " + $scope.activeOrderBy);
            if ($scope.activeOrderBy == "-deviceName") {
                $scope.activeOrderBy = "deviceName";
            } else if ($scope.activeOrderBy == "deviceName") {
                $scope.activeOrderBy = "-deviceName";
            } else {
                $scope.activeOrderBy = "-deviceName";
            }
        };

        $scope.sortByFromActive = function () {
            console.log("sortByDeviceFrom " + $scope.activeOrderBy);
            if ($scope.activeOrderBy == "-subscriptionInit") {
                $scope.activeOrderBy = "subscriptionInit";
            } else if ($scope.activeOrderBy == "subscriptionInit") {
                $scope.activeOrderBy = "-subscriptionInit";
            } else {
                $scope.activeOrderBy = "-subscriptionInit";
            }
        };

        $scope.sortByToActive = function () {
            if ($scope.activeOrderBy == "-subscriptionEnd") {
                $scope.activeOrderBy = "subscriptionEnd";
            } else if ($scope.activeOrderBy == "subscriptionEnd") {
                $scope.activeOrderBy = "-subscriptionEnd";
            } else {
                $scope.activeOrderBy = "-subscriptionEnd";
            }
        };

        $scope.sortByDeviceNamePending = function () {
            if ($scope.pendingOrderBy == "-deviceName") {
                $scope.pendingOrderBy = "deviceName";
            } else if ($scope.pendingOrderBy == "deviceName") {
                $scope.pendingOrderBy = "-deviceName";
            } else {
                $scope.pendingOrderBy = "-deviceName";
            }
        };

        $scope.sortByFromPending = function () {
            if ($scope.pendingOrderBy == "-subscriptionInit") {
                $scope.pendingOrderBy = "subscriptionInit";
            } else if ($scope.pendingOrderBy == "subscriptionInit") {
                $scope.pendingOrderBy = "-subscriptionInit";
            } else {
                $scope.pendingOrderBy = "-subscriptionInit";
            }
        };

        $scope.sortByToPending = function () {
            if ($scope.pendingOrderBy == "-subscriptionEnd") {
                $scope.pendingOrderBy = "subscriptionEnd";
            } else if ($scope.pendingOrderBy == "subscriptionEnd") {
                $scope.pendingOrderBy = "-subscriptionEnd";
            } else {
                $scope.pendingOrderBy = "-subscriptionEnd";
            }
        };

        $scope.showNotification = function (message) {
            var bttnSlide = document.getElementById('notification-trigger-slide');
            // make sure..
            bttnSlide.disabled = false;

            classie.add(bttnSlide, 'active');

            classie.remove(bttnSlide, 'active');

            // create the notification
            //message : '<p><br>'+message+'</p> ',
            var notification = new NotificationFx({
                message: '<p><br>' + message + '</p> ',
                layout: 'growl',
                effect: 'slide',
                type: 'notice', // notice, warning or error
                onClose: function () {
                    bttnSlide.disabled = false;
                }
            });
            //<i class="fa '+ icon +' "  style="color:#'+color+';" ></i>

            // show the notification
            notification.show();

            // disable the button (for demo purposes only)
            this.disabled = true;
        };

        $scope.goToShoppingCart = function () {
            $('#goToShoppingCart').modal('toggle');
            $rootScope.goToPage("/shopping-cart");
        };

        $scope.device = {};

        $scope.sendToTransferDevice = function (sendToTransferDevice) {
            var transferObj = {};
            transferObj.deviceId = $scope.suscriptionSelected.deviceId;
            transferObj.emailAccount = sendToTransferDevice;
            dataService.postTransferDevice(transferObj).then(function (data) {
                $scope.acceptField = false;
                $scope.acceptrefreshUiField = false;
                refreshUi();
                setSuccessMessage("Dispositivo <b> " + $scope.suscriptionSelected.deviceName + "</b> transferido exitosamente ");

            }, function (error) {
                setWarningMessage(error.data);
                console.error("Error!!", error);
            });
        };

        $scope.openModal = function (subscription, index) {
            $scope.suscriptionSelected = subscription;
            console.log("open Modal", index);
            $scope.index = index;
            $scope.emailNewAccount = "";
            $('#modal_transfer_device').modal('toggle');
        };

        $scope.onDeviceCLick = function (subscription) {
            console.log("onDeviceCLick " + subscription);
            $('#device-info').modal('toggle');

            $scope.device.id = subscription.deviceId;
            $scope.device.imei = subscription.imei;
            $scope.device.name = subscription.deviceName;
            $scope.device.icon = subscription.deviceIcon;
            $scope.device.color = subscription.deviceColor;
            $scope.device.battery = subscription.battery;
            $scope.device.sos = subscription.sos;
            $scope.device.sosMails = subscription.sosMails;
            $scope.device.sosCellphones = subscription.sosCellphones;
            $scope.device.batteryMails = subscription.batteryMails;
            $scope.device.batteryCellphones = subscription.batteryCellphones;
            $scope.device.hasDropAlarm = subscription.hasDropAlarm;
            $scope.device.cellphonesDropAlarm = subscription.cellphonesDropAlarm;
            $scope.device.emailsDropAlarm = subscription.emailsDropAlarm;
        };

        $scope.bolShowAlertas = false;
        $scope.showAlertas = function () {
            $scope.bolShowAlertas = true;


        };

        $scope.closeAlertas = function () {
            $scope.bolShowAlertas = false;
        };

        $scope.gotToShoppingCart = function () {
            $rootScope.goToPage("/shopping-cart");
        };

        $scope.updateDevice = function () {
            var arrayDevices = [$scope.device];
            console.log("update device: ", $scope.device);
            dataService.updateDevice(arrayDevices).then(function (data) {
                $('#device-info').modal('toggle');
                var message_good = $translate.instant('control.message.good');
                var message_success = $translate.instant('control.message.success.fence');
                setSuccessMessage("<b>" + message_good + $scope.device.name + message_success + "</b>");
                //form.$setPristine();

                //update the ui
                for (var key in $scope.subscriptions) {
                    console.log('update el device: ', $scope.subscriptions[key]);
                    if ($scope.subscriptions[key].deviceId == $scope.device.id) {
                        console.log('update el devices: ', $scope.subscriptions[key]);
                        $scope.subscriptions[key].deviceName = $scope.device.name;
                        $scope.subscriptions[key].deviceIcon = $scope.device.icon;
                        $scope.subscriptions[key].deviceColor = $scope.device.color;
                        $scope.subscriptions[key].battery = $scope.device.battery;
                        $scope.subscriptions[key].sos = $scope.device.sos;
                        $scope.subscriptions[key].sosMails = $scope.device.sosMails;
                        $scope.subscriptions[key].sosCellphones = $scope.device.sosCellphones;
                        $scope.subscriptions[key].batteryMails = $scope.device.batteryMails;
                        $scope.subscriptions[key].batteryCellphones = $scope.device.batteryCellphones;
                        $scope.subscriptions[key].hasDropAlarm = $scope.device.hasDropAlarm;
                        $scope.subscriptions[key].cellphonesDropAlarm = $scope.device.cellphonesDropAlarm;
                        $scope.subscriptions[key].emailsDropAlarm = $scope.device.emailsDropAlarm;
                    }
                }

                $scope.bolShowAlertas = false;
            }, function (error) {
                console.log("Error saving device: ", error);
                setErrorMessage(error.data);
            });
        };


        $scope.onDeleteSubscriptionCLick = function (subscription, $index) {
            console.log("onDeleteSubCLick ", subscription.deviceId);
            $('#delete-subscription').modal('toggle');
            $scope.currentDeviceIdToDelete = subscription.deviceId;
            $scope.currentIndex = $index;
        };

        $scope.deleteDevice = function () {
            console.log("deleteDevice", $scope.currentDeviceIdToDelete);
            var idToDelete = $scope.currentDeviceIdToDelete;
            $('#delete-subscription').modal('toggle');
            console.log("idToDelete", idToDelete);
            if ($scope.acceptField) {
                dataService.unlinkDevice(idToDelete)
                    .then(function () {
                        console.log("saveNewBillsData fine");
                        $scope.acceptField = false;
                        $scope.acceptrefreshUiField = false;
                        refreshUi();
                        var successMessage = $translate.instant('subscription.delete.subscription.success');
                        setSuccessMessage(successMessage);
                    }, function (error) {
                        var errorMessage = $translate.instant("subscription.delete.subscription.error");
                        setErrorMessage(errorMessage + "\n" + error.data);
                        console.log("changePasswordUserAccountManagement Error: ", error);
                    });
            }
        };


        $scope.closeDeleteAlertas = function () {
            $scope.acceptField = false;
            hideDeleteItemButton();
        };

        $scope.checkBoxAccept = function () {
            console.log("$scope.acceptField.checked " + $scope.acceptField);
            if ($scope.acceptField) {
                $scope.acceptField = false;
                hideDeleteItemButton();
            } else {
                $scope.acceptField = true;
                showDeleteItemButton();
            }
        };

        var refreshUi = function () {
            $route.reload();
        };

        var showDeleteItemButton = function () {
            $(".deleteItemButton").css("opacity", "1");
            $(".deleteItemButton").css("filter", "alpha(opacity=100)");
            $(".deleteItemButton").css("pointer-events", "visible");
        };

        var hideDeleteItemButton = function () {
            $(".deleteItemButton").css("opacity", ".4");
            $(".deleteItemButton").css("filter", "alpha(opacity=40)");
            $(".deleteItemButton").css("pointer-events", "none");
        };
    }
})();




