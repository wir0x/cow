(function () {

    'use strict';

    angular
        .module('buho-tracking')
        .config(['$routeProvider', function ($routeProvider) {

            $routeProvider
                .when("/", {
                    redirectTo: 'current-position'
                })
                .when("/subscription", {
                    templateUrl: "app/subscriptions/subscriptions.html",
                    controller: "SubscriptionsController",
                    title: 'Subscription'
                })
                .when("/subscriptions-management", {
                    templateUrl: "app/subscriptions/management/subscriptions-management.html",
                    controller: "SubscriptionsManagementController",
                    title: 'Subscriptions Management'
                })
                .when("/shopping-cart", {
                    templateUrl: "app/subscriptions/shoppingcart/shopping-cart.html",
                    controller: "ShoppingCartController",
                    title: 'Shopping Cart'
                })
                .otherwise({
                    redirectTo: '/error-404',
                    templateUrl: "common/errors/error-404.html"
                });
        }]);
})();