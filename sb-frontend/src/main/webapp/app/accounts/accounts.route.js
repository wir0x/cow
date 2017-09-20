(function () {
    'use strict';

    angular
        .module('buho-tracking')
        .config(['$routeProvider', function ($routeProvider) {
            $routeProvider
                .when("/", {
                    redirectTo: 'current-position'
                })
                .when("/account-management", {
                    templateUrl: "app/accounts/management/account-management.html",
                    controller: "AccountManagementController",
                    title: 'Account Management'
                })
                .when("/state-account", {
                    templateUrl: "app/accounts/status/account-status.html",
                    controller: "AccountStatusController",
                    title: 'State Account'
                })
                .otherwise({
                    redirectTo: '/error-404',
                    templateUrl: "common/erros/error-404.html",
                    title: 'Page Not found'
                });
        }])
})();
