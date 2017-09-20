(function () {

    'use strict';

    angular
        .module('buho-tracking')
        .config(['$routeProvider', function ($routeProvider) {

            $routeProvider
                .when("/", {
                    redirectTo: 'current-position'
                })
                .when("/reports", {
                    templateUrl: "app/reports/reports.html",
                    controller: "ReportsController",
                    title: 'Reports'
                })
                .when("/detailed-report", {
                    templateUrl: "app/reports/detailed-report.html",
                    controller: "ReportDetailController",
                    title: 'Report Detailed'
                })
                .otherwise({
                    redirectTo: '/error-404',
                    templateUrl: "common/errors/error-404.html"
                });
        }]);
})();