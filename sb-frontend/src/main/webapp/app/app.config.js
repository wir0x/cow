(function () {

    'use strict';

    angular
        .module('buho-tracking')
        .config(configBarProvider)
        .config(configTranslate)
        .config(['uiGmapGoogleMapApiProvider', configGoogleMapsApi])
        .config(configHttpProvider)
        .run(configApp)
        .run(gotPage);

    ////////

    function configApp($location, $rootScope) {
        $rootScope.appConfig = {};
        $rootScope.appConfig.headerVisible = true;
        $rootScope.appConfig.footerVisible = true;

        $rootScope.$on('$routeChangeSuccess', function (event, current, previous) {
            $rootScope.title = current.$$route.title;
        });
    }

    function gotPage($rootScope, $location, $window) {
        $rootScope.goToPage = function (page) {
            $location.path(page, true);
        };

        $rootScope.goBack = function () {
            $window.history.back();
        }
    }

    function configBarProvider(cfpLoadingBarProvider) {
        cfpLoadingBarProvider.includeBar = true;
        cfpLoadingBarProvider.includeSpinner = true;
        cfpLoadingBarProvider.latencyThreshold = 100;
    }

    function configTranslate($translateProvider) {
        $translateProvider.preferredLanguage(window.mydefaultlanguage.languageCode);
        $translateProvider.translations(window.mydefaultlanguage.languageCode, window.translationsContent);
    }

    function configGoogleMapsApi(GoogleMapApiProvider) {
        GoogleMapApiProvider.configure({
            key: 'AIzaSyDTVFGPLegGdOPlvUnvtvaTkVN1mhxa1IU',
            v: '3.20',
            libraries: 'geometry, drawing'
            //china: true
        });
    }

    function configHttpProvider($httpProvider) {
        $httpProvider.defaults.headers.common['Cache-Control'] = 'no-cache';
        $httpProvider.defaults.headers.common['Pragma'] = 'no-cache';
        $httpProvider.defaults.headers.common['If-Modified-Since'] = '0';
    }
})();
