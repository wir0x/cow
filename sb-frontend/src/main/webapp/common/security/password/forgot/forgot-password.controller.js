(function () {
    'use strict';


    angular
        .module('buho-tracking')
        .controller('ForgotPasswordController', ForgotPasswordController);

    function ForgotPasswordController($scope, $rootScope, dataService, $translate) {
        console.log("forgotPasswordCtrl");
        $scope.appConfig.headerVisible = false;
        $scope.isLoading = false;

        $scope.forgotPassword = function (email) {
            $scope.isLoading = true;
            dataService.passwordRecovery(email)
                .then(function (data) {
                    $scope.isLoading = false;
                    var send_mail = $translate.instant('lbl.forgot.pass.send.mail');
                    var restore_pass = $translate.instant('lbl.forgot.pass.confirm');
                    setSuccessMessage(send_mail + data + restore_pass, false);
                    $rootScope.goToPage("/login");
                }, function (error) {
                    $scope.isLoading = false;
                    setSuccessMessage(error.data, false);
                    $rootScope.goToPage("/login");
                });
        };

        $scope.isBtnActive = function (form) {
            if (!form.$valid) {
                return true;
            }

            return !!$scope.isLoading;
        }
    }
})();