(function () {

    'use strict';

    angular
        .module('buho-tracking')
        .config(['$routeProvider', function ($routeProvider) {

            $routeProvider
                .when("/", {
                    redirectTo: 'current-position'
                })
                .when("/current-position", {
                    templateUrl: "app/currentposition/current-position.html",
                    controller: "CurrentPositionController",
                    title: 'Current Position'
                })
                .when("/history", {
                    templateUrl: "app/historical/historical.html",
                    controller: "HistoricalController",
                    title: 'Historical'
                })
                .when("/control", {
                    templateUrl: "app/geofences/geo-fence.html",
                    controller: "GeoFenceController",
                    title: 'GeoFences'
                })
                .when("/user-management", {
                    templateUrl: "app/users/user-management.html",
                    controller: "UserManagementController",
                    title: 'User Management'
                })
                .when("/contact", {
                    templateUrl: "app/admin/contact/contact-us.html",
                    controller: "ContactUsController",
                    controllerAs: "contact",
                    title: 'Contact'
                })
                .when("/transactions", {
                    templateUrl: "app/admin/transactions/transactions.html",
                    controller: "TransactionsController",
                    title: 'Transactions'
                })
                .when("/error-404", {
                    templateUrl: "common/errors/error-404.html",
                    controller: "errorCtrl",
                    title: 'Page Not Found'
                })
                .when("/error-401", {
                    templateUrl: "common/errors/error-401.html",
                    controller: "errorCtrl",
                    title: 'Page Not Authorized'
                })
                .otherwise({
                    redirectTo: '/error-404',
                    templateUrl: "common/errors/error-404.html"
                });
        }]);
})();