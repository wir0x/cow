(function () {

    'use strict';

    angular
        .module('buho-tracking')
        .controller('ResetPasswordController', ResetPasswordController);

    function ResetPasswordController($scope, $routeParams, $rootScope, dataService, $log, $translate) {
        console.log("resetPasswordCtrl: ", $routeParams.token);
        $scope.appConfig.headerVisible = false;
        $scope.passwordtoken = {};
        $scope.passwordtoken.token = $routeParams.token;
        function init() {
            dataService.getPasswordToken($routeParams.token)
                .then(function (data) {
                    console.log("token is active");
                }, function (error) {
                    console.log("error: ", error);
                    $rootScope.goToPage("/login");
                    setErrorMessage(error.data, false);
                });
        }

        $scope.resetPassword = function () {
            console.log("token:", $scope.passwordtoken.token);
            console.log("password:", $scope.passwordtoken.passwordNew);
            console.log("password Confirm:", $scope.passwordtoken.passwordConfirm);
            dataService.resetPassword($scope.passwordtoken)
                .then(function (data) {
                    var update_success = $translate.instant('lbl.success.update.password');
                    setSuccessMessage(update_success, false);
                    $rootScope.goToPage("/login");
                }, function (error) {
                    console.log("error: ", error);
                    setErrorMessage(error.data, false);
                });
        };


        $scope.isBtnResetActive = function (form) {
            if (!form.$valid) return true;

            if ($scope.isLoading) return true;

            return !$scope.arePasswordsEquals();
        };

        $scope.arePasswordsEquals = function () {
            $log.info("are equals", $scope.passwordtoken.password, $scope.passwordtoken.passwordConfirm);
            return (angular.equals($scope.passwordtoken.password, $scope.passwordtoken.passwordConfirm));
        };

        init();
    }
})();
