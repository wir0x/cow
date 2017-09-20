(function () {

    'use strict';

    angular
        .module('buho-tracking')
        .controller('AppController', AppController)
        .controller('LoginController', LoginController)
        .controller('ErrorController', ErrorController);
})();


function AppController($scope, $rootScope, $window, dataService, securityService, $location, $interval, $translate) {
    var checkTimeOut = $interval(getCheckSessionTime, 60000);

    $scope.$on('$viewContentLoaded', function (event) {
        $window.ga('send', 'pageview',
            {
                page: $location.path(),
                hitCallback: function () {
                    //console.log('ga data sent for page: ' + $location.path());
                    if (securityService.isLoggedIn()) $scope.getShoppingCartItems()
                }
            });

    });

    $scope.getClass = function (path) {
        if ($location.path().substr(0, path.length) == path) {
            return "open-menu"
        } else {
            return ""
        }
    };

    function init() {
        $scope.loginError = false;
        $scope.isLoggedIn = securityService.isLoggedIn();

        if ($scope.isLoggedIn) {
            $scope.currentUser = securityService.getUser();
        }
    }

    $scope.logout = function () {
        securityService.endSession();
        $rootScope.goToPage("/login");
    };

    $scope.hasRole = function (role) {
        if (!$scope.currentUser) {
            return false;
        }
        //console.log("hasRole " +  $scope.currentUser.permission);
        return $scope.currentUser.permission.indexOf(role) > -1;
    };

    $scope.$on('$locationChangeStart', function (event, next) {

        $scope.appConfig.headerVisible = true;

        var page = $location.path().substring(1, $location.path().length);

        if (contains(page, 'error')) {
            return;
        }

        if (angular.equals(page, 'forgot-password')
            || angular.equals(page, 'login')
            || contains(page, 'reset-password')) {
            if (securityService.isLoggedIn()) {
                $rootScope.goToPage("/");
            }
        } else if (securityService.isLoggedIn()) {
            //console.log('$scope.hasRole ', $scope.hasRole());
            if (!$scope.hasRole(page)) {
                if ($scope.hasRole("account-management")) {
                    $rootScope.goToPage("/account-management")

                } else {
                    $rootScope.goToPage("/");
                }
            }
        } else {
            securityService.endSession();
            $scope.currentUser = {};
            $rootScope.goToPage("/login");
        }
        $scope.currentUser = securityService.getUser();
    });

    function contains(str, value) {
        return str.indexOf(value) !== -1;
    }

    $scope.changePasswordUser = function (userSecurity) {
        dataService.changePasswordUser(userSecurity)
            .then(function (data) {
                //console.log("data: ", data);
                setSuccessMessage("Contraseña modificada correctamente.");
                cleanUser();
            }, function (error) {
                //console.log("error: ", error);
                setErrorMessage("Error: " + error.data);
                cleanUser();
            });
    };

    $scope.arePasswordsEquals = function (form) {
        return angular.equals(form.password.$modelValue, form.passwordConfirm.$modelValue);
    };

    $scope.isBtnChangePasswordActive = function (form) {
        if (!form.$valid) {
            return true;
        }

        return !$scope.arePasswordsEquals(form);
    };

    function cleanUser() {
        $scope.user = {};
    }

    init();

    function getCheckSessionTime() {
        dataService.checkSessionTime().success(function (data) {
            if (data.status != true) {
                $interval.cancel(checkTimeOut);
            }
        });
    }

    $scope.shoppingCart = {};
    $scope.account = {};
    $scope.getShoppingCartItems = function () {
        dataService.getShoppingCartItems().success(function (data) {
            //console.log("getShoppingCartItems ", data);
            $scope.shoppingCart.NumOfItems = data.items;
            $scope.account.hasDevice = data.status;

            //console.log("$location.path() ", $location.path());
            if (!$scope.account.hasDevice
                && !$scope.hasRole('account-management')
                && $location.path() != "/user-management"
                && $location.path() != "/state-account") {//account-management
                window.setTimeout(showInstructions, 800);
                $('body').css("overflow", "hidden");
            } else {
                if ($location.path() != "/shopping-cart") $('body').css("overflow", "auto");
            }
            //console.log("hasDevice ", $scope.account.hasDevice);
        });
    };

    var showInstructions = function () {
        $('.new-account-cache ').fadeTo("slow", .8);
        $('.welcome-container ').fadeTo("slow", 1);
    };


    var callTmThread;
    $scope.tMspinner = {};
    $scope.tMspinner.WaitingForPayment = false;
    $scope.callVerificationPaymentThread = function (id) {
        $scope.requestPaymentThread = function () {
            //console.log("thread");
            localStorage.setItem('currentPaymentId', id);
            dataService.requestStatusPayment(id).success(function (response) {
                console.log("requestStatusPayment ", response.status);
                if (response.status == "PROCESSED") {
                    $interval.cancel(callTmThread);
                    console.log(" thread process ");
                    $rootScope.goToPage("/subscriptions-management");
                    var message_payment_success = $translate.instant('payment.success');
                    setSuccessMessage(message_payment_success);
                    $('.modal').modal('hide');
                    if ($location.path() == "/shopping-cart") {
                        $rootScope.goToPage("/shopping-cart");
                    }
                    $scope.tMspinner.WaitingForPayment = false;
                } else if (response.status == "ERROR") {
                    $interval.cancel(callTmThread);
                    var message_payment_error = $translate.instant('payment.error');
                    setErrorMessage(message_payment_error);
                    $('.modal').modal('hide');
                    $scope.tMspinner.WaitingForPayment = false;
                } else if (response.status == "PENDING") {
                    console.log(" thread pending ");
                }
            });
        };
        $scope.tMspinner.WaitingForPayment = true;
        callTmThread = $interval($scope.requestPaymentThread, 8000, 1300);//8000 30
    };

}

function LoginController($scope, $rootScope, dataService, securityService, $timeout,onlineStatus) {
    //console.log("loginCtrl");
    $scope.credentials = {};
    $scope.registerCredentials = {};
    $scope.currentUser = {};
    $scope.appConfig.headerVisible = false;

    $scope.onlineStatus = onlineStatus;

    $scope.$watch('onlineStatus.isOnline()', function (online) {
        $scope.connexionError = !online;
    });

    $scope.login = function () {
        setTimeout(makeLogin(), 7000);
    };

    function makeLogin() {
        dataService.login($scope.credentials)
            .success(function (account) {
                console.log("[LOGIN] -> ", account);
                securityService.initSession(account);
                $scope.credentials = account;
                $rootScope.goToPage("/");
                $scope.getShoppingCartItems();
            })
            .error(function (data, status) {
                if (status == '401') {
                    $scope.loginError = true;
                    $timeout(loginError, 3000);
                } else if (status == '405') {
                    $scope.connexionError = true;
                    $timeout(connexionError, 3000);
                }
            });
    }

    function connexionError() {
        $scope.connexionError = false;
    }

    function loginError() {
        $scope.loginError = false;
    }

    $scope.register = function (form) {
        if (form.$valid) {
            makeRegister();
        }
    };

    function makeRegister() {
        $('.modal').css("z-index", "10 !important");
        if ($scope.registerCredentials.password != $scope.registerCredentials.passwordConfirm) {
            setErrorMessage("Confirmar contraseña, no coincide");
        } else if (Object.keys($scope.registerCredentials).length == 5) {
            dataService.register($scope.registerCredentials).then(function (account) {
                console.log("register ---> ", account)
                securityService.initSession(account);
                $scope.credentials = account;
                $rootScope.goToPage("/");
                $scope.getShoppingCartItems();
            }, function (error) {
                //console.log("createSubscription error");
                setErrorMessage("Error " + error.data)

            })
        }
    }

    $scope.cleanForm = function (form) {
        $scope.registerCredentials.name = null;
        $scope.registerCredentials.email = "";
        $scope.registerCredentials.username = null;
        $scope.registerCredentials.password = null;
        $scope.registerCredentials.passwordConfirm = null;
        form.$setPristine();
    }
}

function ErrorController($scope) {
    $scope.appConfig.headerVisible = false;
}

var setSuccessMessage = function (message, isLoggedIn) {
    // create the notification
    console.log('into login.controller.js');
    var notification = new NotificationFx({
        message: '<span class="icon fa fa-check-circle fa-fw fa-lg fa-2x"></span><p>' + message + '</p>',
        layout: 'bar',
        effect: 'slidetop',
        type: 'success',
        isloggedIn: isLoggedIn != undefined ? isLoggedIn : true
    });
    // show the notification
    notification.show();
};

var notificationWithoutPositions = function (message, isLoggedIn) {
    // create the notification
    console.log('into login.controller.js');
    var notification = new NotificationFx({
        message: '<span class="icon fa fa-info-circle fa-fw fa-lg fa-2x"></span><p><label class="label label-info">' + message + '</label></p>',
        layout: 'bar',
        effect: 'slidetop',
        type: 'default',
        isloggedIn: isLoggedIn != undefined ? isLoggedIn : true
    });
    // show the notification
    notification.show();
};

var notificationSubscriptionExpired = function (message, isLoggedIn) {
    // create the notification
    console.log('into login.controller.js');
    var notification = new NotificationFx({
        message: '<span class="icon fa fa-info-circle fa-fw fa-lg fa-2x "></span><p><label class="label label-danger">' + message + ' adquiera uno desde el menú "Dispositivos"</label></p>',
        layout: 'bar',
        effect: 'slidetop',
        type: 'default',
        isloggedIn: isLoggedIn != undefined ? isLoggedIn : true
    });
    // show the notification
    notification.show();
};

var notificationSubscriptionDisabled = function (message, isLoggedIn) {
    // create the notification
    console.log('into login.controller.js');
    var notification = new NotificationFx({
        message: '<span class="icon fa fa-info-circle fa-fw fa-lg fa-2x "></span><p><label class="label label-warning">' + message + ' adquiera uno desde el menú "Dispositivos"</label></p>',
        layout: 'bar',
        effect: 'slidetop',
        type: 'default',
        isloggedIn: isLoggedIn != undefined ? isLoggedIn : true
    });
    // show the notification
    notification.show();
};

var setErrorMessage = function (message, isLoggedIn) {
    // create the notification
    var notification = new NotificationFx({
        message: '<span class="icon fa fa-times-circle fa-fw fa-lg fa-2x"></span><p>' + message + '</p>',
        layout: 'bar',
        effect: 'slidetop',
        type: 'error',
        isloggedIn: isLoggedIn != undefined ? isLoggedIn : true
    });
    // show the notification
    notification.show();
};

var setWarningMessage = function (message, isLoggedIn) {
    // create the notification
    var notification = new NotificationFx({
        message: '<span class="icon fa fa-exclamation-triangle fa-fw fa-lg fa-2x"></span><p>' + message + '</p>',
        layout: 'bar',
        effect: 'slidetop',
        type: 'warning',
        isloggedIn: isLoggedIn != undefined ? isLoggedIn : true
    });
    // show the notification
    notification.show();
};

var setAlertDownSuccess = function (message) {
    var btnSlide = document.getElementById('notification-trigger-slide');

    btnSlide.disabled = false;

    classie.add(btnSlide, 'active');

    classie.remove(btnSlide, 'active');

    // create the notification
    var notification = new NotificationFx({
        message: '<p>' + message + '</p>',
        layout: 'growl',
        effect: 'slide',
        type: 'notice', // notice, warning or error
        onClose: function () {
            btnSlide.disabled = false;
        }
    });
    notification.show();
    this.disabled = true;
};

