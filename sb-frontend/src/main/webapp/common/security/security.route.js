(function () {

    'use strict';

    angular
        .module('buho-tracking')
        .config(['$routeProvider', function ($routeProvider) {
            $routeProvider
                .when("/", {
                    redirectTo: 'current-position'
                })
                .when("/login", {
                    templateUrl: "common/security/login/login.html",
                    controller: "LoginController",
                    title: 'Iniciar Sesion'
                })
                .when("/forgot-password", {
                    templateUrl: "common/security/password/forgot/forgot-password.html",
                    controller: "ForgotPasswordController",
                    title: 'Forgot password'
                })
                .when("/reset-password/:token", {
                    templateUrl: "common/security/password/reset/reset-password.html",
                    controller: "ResetPasswordController",
                    title: 'Reset password'
                })
                .otherwise({
                    redirectTo: '/error-404'
                });
        }]);
})();