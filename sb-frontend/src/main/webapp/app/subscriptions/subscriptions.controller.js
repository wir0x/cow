(function () {
    'use strict';

    angular
        .module('buho-tracking')
        .controller('SubscriptionsController', SubscriptionsController);


    function SubscriptionsController($scope, dataService, $translate) {
        console.log("subscriptionCtrl");

        init();

        $scope.setDeviceList = function (account) {

            $scope.devices = account.deviceList;
        };

        $scope.deleteSubscription = function (subscription) {
            console.log("delete subscription: ", subscription)
            if (subscription.deviceId) {
                dataService.deleteSubscription(subscription.id).then(function (data) {
                    console.log("data ", data);
                    $scope.subscriptions.splice($scope.subscriptions.indexOf(subscription), 1);
                    var message_good = $translate.instant('subscription.delete.success');
                    setSuccessMessage(message_good);

                }, function (error) {
                    console.log(" error ", error);
                    var message_wrong = $translate.instant('subscription.delete.error');
                    setErrorMessage(message_wrong);
                })
            }
        };

        $scope.adjustDates = function (servicePlanId) {
            console.log("service plan id ", servicePlanId);
            switch (servicePlanId) {
                case 1:
                    $scope.subscription.endDate = moment().add(1, 'months').endOf('month').format('DD-MM-YYYY');
                    break;
                case 2:
                    $scope.subscription.endDate = moment().add(3, 'months').endOf('month').format('DD-MM-YYYY');
                    break;
                case 3:
                    $scope.subscription.endDate = moment().add(6, 'months').endOf('month').format('DD-MM-YYYY');
                    break;
                case 4:
                    $scope.subscription.endDate = moment().add(12, 'months').endOf('month').format('DD-MM-YYYY');
                    break;
            }
        };


        $scope.setLastDayOfMonth = function () {
            $scope.subscription.endDate = moment($scope.subscription.endDate, 'DD-MM-YYYY').endOf('months').format("DD-MM-YYYY");
            var _startDate = moment($scope.subscription.startDate, "DD-MM-YYYY").format('YYYY-MM-DD');
            var _endDate = moment($scope.subscription.endDate, "DD-MM-YYYY").format('YYYY-MM-DD');
            validateDates(_startDate, _endDate);
        };

        $scope.getDevicesByAccount = function (account) {
            console.log("getDevicesByAccount: ", account)
            if (account == null) {
                $scope.devices = {};
                return;
            }
            dataService.findDevicesByAccountId(account.id).success(function (data) {
                console.log("findDevicesByAccountId", data);
                $scope.devices = data;
            });
        };

        $scope.isValidSubscriptionForm = function (form) {
            return !form.$valid;
        };

        $scope.prepareNewSubscription = function () {
            console.log("prepareNewSubscription");
            $scope.subscription = {};
            $scope.subscription.startDate = moment().format("DD-MM-YYYY");
            $scope.subscription.endDate = moment().format("DD-MM-YYYY");
            $scope.subscription.maxSms = 100;
            $scope.subscription.endDate = moment().add(1, 'months').endOf('month').format('DD-MM-YYYY');
        };

        $scope.prepareEditSubscription = function (subscription) {
            console.log("prepareEditSubscription ", subscription);
            $scope.subscription = subscription;
        };

        $scope.setFormatStartDate = function () {
            $scope.subscription.startDate = moment($scope.subscription.startDate, 'DD-MM-YYYY').format('DD-MM-YYYY');
        };

        $scope.setFormatEndDate = function () {
            $scope.subscription.endDate = moment($scope.subscription.endDate, 'DD-MM-YYYY').format('DD-MM-YYYY');
            console.log("endDate ", $scope.subscription.endDate);
            console.log("startDate ", $scope.subscription.startDate);

            var _startDate = moment($scope.subscription.startDate, "DD-MM-YYYY").format('YYYY-MM-DD');
            var _endDate = moment($scope.subscription.endDate, "DD-MM-YYYY").format('YYYY-MM-DD');

            validateDates(_startDate, _endDate);
        };

        $scope.registerNewSubscription = function () {
            console.log("registerNewSubscription");
            var subscriptionDto = {
                id: $scope.subscription.id,
                deviceId: $scope.subscription.device.id,
                accountName: $scope.subscription.account.nameAccount,
                deviceName: $scope.subscription.device.name,
                maxSms: $scope.subscription.maxSms,
                startDate: $scope.subscription.startDate,
                endDate: $scope.subscription.endDate,
                servicePlanId: $scope.subscription.servicePlanId
            };
            dataService.registerNewSubscription(subscriptionDto).then(
                function (data) {
                    console.log("response: ", data);
                    $scope.subscription.id = data;
                    subscriptionDto.id = data;
                    console.log("subscription: ", $scope.subscription);
                    var success = $translate.instant('subscription.success.register.message');
                    setSuccessMessage(success);
                    $scope.subscriptions.push(subscriptionDto);

                }, function (error) {
                    console.log("error ", error.data);
                    var wrong = $translate.instant('subscription.error.register.message');
                    setErrorMessage(wrong + "\n" + error.data);
                }
            )
        };

        $scope.updateSubscription = function () {
            console.log("updateSubscription", $scope.subscription);

            dataService.updateSubscription($scope.subscription).then(function () {
                var success = $translate.instant('subscription.success.update.message');
                setSuccessMessage(success);

            }, function (error) {
                var wrong = $translate.instant('subscription.error.update.message', error.data);
                setErrorMessage(wrong);
            })
        };

        function validateDates(startDate, endDate) {
            if (new Date(endDate) > new Date(startDate)) {

                $scope.isValidForm = false;
            } else {
                $scope.incorrectDates = true;
                $scope.isValidForm = true;
            }
        }

        function init() {
            $scope.isValidForm = false;
            dataService.findAllServicePlan().success(function (data) {
                $scope.servicePlanList = data;
            });

            dataService.findAllAccounts().success(function (data) {
                $scope.accounts = data;
            });

            dataService.findAllSubscription().success(function (data) {
                $scope.subscriptions = data;
            });
        }
    }
})();



