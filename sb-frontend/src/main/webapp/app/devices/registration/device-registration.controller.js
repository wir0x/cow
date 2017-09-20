(function () {
    'use strict';

    angular.module("buho-tracking")
        .controller('DeviceRegistrationController', DeviceRegistrationController);

    function DeviceRegistrationController($scope, $log, dataService, $translate, $timeout, $window, $rootScope, $route) {
        console.log("deviceAddCtrl");

        var self = this;
        self.payement = false;
        self.minTelfNum = 6;
        self.showColorIconForm = false;
        self.showGreeting = false;
        self.email = "";
        $scope.showRequiredFieldMessage = false;
        $scope.smartphone = {name: "", generatedId: ""};

        $scope.response = [];
        $scope.paymentMadeByAdmin = 0;
        $scope.showConfirmarBtn = false;
        $scope.firstFormAllreadySend = false;


        dataService.findAllServicePlan().success(function (data) {
            console.log("servicesPlanList " + data[1].name);
            $scope.servicePlanList = data;
        });

        $(document).keypress(function (e) {
            $scope.showRequiredFieldMessage = false;
        });

        $scope.view = true;
        $scope.subscription = {};
        $scope.subscriptionId;
        $scope.createSubscription = function (subscription) {
            console.log("createSubscription ", subscription);
            // Create user object to send to the server
            $scope.subscription = subscription;

            if (!$scope.subscription.telf || !$scope.subscription.name || $scope.subscription.telf.length < self.minTelfNum) {
                $scope.showRequiredFieldMessage = true;
                console.log("subscription empty");
            } else {
                $scope.subscriptionToSend = {
                    username: $scope.subscription.telf,
                    name: $scope.subscription.name,
                    userPay: $scope.paymentMadeByAdmin,
                    accessViewer: $scope.view,
                    invitationMails: $scope.subscription.email,
                    invitationNumber: "12345678"
                };
                console.log("subscription to send ", $scope.subscriptionToSend);
                dataService.createSubscription($scope.subscriptionToSend).then(function (data) {
                    console.log("data ", data);
                    $scope.response = data;
                    $scope.subscriptionId = data.subscriptionId;
                    $scope.showNotification('Subscripcion creada');
                    $scope.firstFormAllreadySend = true;
                }, function (error) {
                    console.log("createSubscription error");
                    setErrorMessage("Error " + error.data)
                })
            }
        };


        $scope.trackerConfirm = false;
        $scope.updateSubscription = function () {
            $scope.trackerConfirm = true;
            $scope.allReadyConfirm = true;
            console.log("subscription ", $scope.subscription);
            $scope.subscriptionToSend['id'] = $scope.subscriptionId;
            $scope.subscriptionToSend['userPay'] = true;
            console.log("subscriptionToSend ", $scope.subscriptionToSend);
            dataService.updateSubscriptionUserPay($scope.subscriptionToSend)
                .then(function () {
                        console.log("success ");
                    }, function (error) {
                        console.log(error);
                    }
                );
        };

        $scope.updateEmail = {};
        $scope.updateMail = function () {
            $scope.updateEmail["subscriptionId"] = $scope.subscriptionId;
            $scope.updateEmail["email"] = $scope.subscription.email;
            dataService.updateEmailInvitation($scope.updateEmail).then(function (data) {
                console.log("success mail");
                $('#send-email').modal('toggle');
                $scope.subscription.email = "";
                setSuccessMessage('Email mandado existosamente');
            }, function (error) {
                setErrorMessage("Error " + error.data)
                $('#send-email').modal('toggle');
                $timeout(function () {
                    $('#send-email').modal('toggle');
                }, 1500);
            })
        };

        $scope.allReadyConfirm = false;
        $scope.adminConfirm = false;
        $scope.list = {};
        var items = [];
        $scope.addToShoppingCart = function () {
            $scope.list["deviceId"] = $scope.response.deviceId;
            $scope.list["planId"] = curPlanId;
            items.push($scope.list);
            console.log("items ", items);
            dataService.addItemsToShoppingCart(items).then(function (data) {
                $scope.adminConfirm = true;
                $scope.allReadyConfirm = true;
                $scope.getShoppingCartItems();
                //var msg_deviceAddedToShoppingCart = $translate.geti
                $scope.showNotification('');
            }, function (error) {
                console.log("addToShoppingCart error");
                setErrorMessage("Error " + error.data)
            });
            $scope.list = {};
            items = [];
        };

// get the selected plan by user
        var curPlanId;
        $scope.selectedPlan = function (planId) {
            curPlanId = planId;
            console.log("curPlanId " + curPlanId);
            $scope.showConfirmarBtn = true;
        };


        self.doGreeting = function () {
            console.log("greating show");
            $scope.msg = "hi";
            self.showGreeting = true;
            $timeout(function () {
                self.showGreeting = false;
            }, 3000);
        };

        $scope.blockSpace = function (e) {
            console.log("event ", e);
            var key_code = e.which;
            if (key_code == 32) {
                e.preventDefault();
            }
        };


        $scope.emptyVal = function (subscription) {
            if (subscription.mailCheckbox) {
                subscription.email = "";
            }
        };

        $scope.hideWarningMessage = function () {
            self.showRequiredFieldMessage = false;
        };

        $scope.createOtherSubscription = function (subscription) {
            $route.reload();
        };

        $scope.notifyByMail = function () {
            $('#send-email').modal('toggle');
        };


        $scope.showNotification = function (message) {
            var bttnSlide = document.getElementById('notification-trigger-slide');
            // make sure..
            bttnSlide.disabled = false;

            classie.add(bttnSlide, 'active');

            classie.remove(bttnSlide, 'active');

            // create the notification
            var notification = new NotificationFx({
                message: '<p style="margin-top: -5px"><br>' + message + '</p> ',
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

        $scope.validateFields = function (form) {
            var trackerId = $('#tracker-id');
            var trackerName = $('#tracker-name');
            form.tracker_id.$valid ? trackerId.addClass("has-success") : trackerId.removeClass("has-success");
            form.tracker_name.$valid ? trackerName.addClass("has-success") : trackerName.removeClass("has-success");
        };

        $scope.linkingSmartphone = function (smartphone, form) {

            if (form.$valid) {

                dataService.linkingSmartphone(smartphone).then(function () {

                    var successMessage = $translate.instant('tracker.success.linked');
                    setSuccessMessage(smartphone.name + successMessage)

                }, function error(data) {

                    if (data.status == 406) {
                        var generatedIdItsInUse = $translate.instant('tracker.it.is.linked');
                        setWarningMessage(generatedIdItsInUse);
                    }

                    if (data.status == 404) {
                        var trackerIdNotFound = $translate.instant('tracker.id.not.found');
                        setWarningMessage(trackerIdNotFound);
                    }
                })
            }
        }
    }
})();





