(function () {

    'use strict';

    angular
        .module('SecurityServiceModule', [])
        .provider("authdata", authData)
        .factory('securityService', securityService)
        .factory('authHttpResponseInterceptor', authHttpResponseInterceptor)
        .config(configHttpProvider);

    ////////

    function configHttpProvider($httpProvider) {
        $httpProvider.interceptors.push('authHttpResponseInterceptor');
    }

    function authData() {
        var userRoles = {
            public_: 1, // 001
            user: 2, // 010
            admin: 4  // 100
        };

        var accessLevels = {
            public_: userRoles.public_ | // 111
            userRoles.user |
            userRoles.admin,
            anon: userRoles.public_,  // 001
            user: userRoles.user |   // 110
            userRoles.admin,
            admin: userRoles.admin    // 100
        };

        return {
            userRoles: userRoles,
            accessLevels: accessLevels,

            $get: function () {
                return {
                    userRoles: userRoles,
                    accessLevels: accessLevels
                }
            }
        }
    }

    function securityService($rootScope, authdata) {

        var user = undefined;
        var role = undefined;

        var getToken = function () {
            return localStorage.getItem('token');
        };

        var getRoleStorage = function () {
            return localStorage.getItem('role');
        };


        var getRole = function () {
            var roleStorage = getRoleStorage();

            if (isValid(role)) {
                // if role is valid verify if was updated on storage
                if (isValid(roleStorage) && (role != roleStorage)) {
                    role = roleStorage;
                }
            } else {
                if (isValid(roleStorage)) {
                    role = getRoleStorage();
                } else {
                    role = authdata.userRoles.public_;
                }
            }
            return role;
        };

        function isValid(param) {
            return (param != undefined && param != null)
        }

        return {
            initSession: function (response) {
                //console.log("[INFO] Initializing user session.");
                //console.log("[INFO] Token is :" + response.authctoken);
                //console.log("[INFO] Token Stored in session storage.");
                // persist token, user id to the storage
                localStorage.setItem('token', response.authctoken);
                delete response.authctoken;
                localStorage.setItem('user', JSON.stringify(response));
                localStorage.setItem('role', authdata.userRoles.user);
                //console.log('initSession');
                role = authdata.userRoles.user;
                //console.log("[INFO] ", authdata);
                $rootScope.$broadcast("LoggedIn");
            },

            endSession: function () {
                console.log("[INFO] Ending User Session.");
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                localStorage.setItem('role', authdata.userRoles.public_);

                user = undefined;
                role = authdata.userRoles.public_;
                //console.log("[INFO] Token removed from session storage.");
                $rootScope.$broadcast("LoggedOut");

            },

            getToken: getToken,

            getUser: function () {
                if (user == undefined) {
                    user = JSON.parse(localStorage.getItem('user'));
                }

                return user;
            },

            secureRequest: function (requestConfig) {
                var token = getToken();

                if (token != null && token != '' && token != 'undefined') {
                    //console.log("[INFO] Securing request.");
                    //console.log("[INFO] Setting x-session-token header: " + token);
                    requestConfig.headers['Authorization'] = 'Token ' + token;
                    //console.log("headers: ",  requestConfig.headers);
                }
            },

            isLoggedIn: function (user) {//isLoggedIn
                if (user == undefined)
                    user = JSON.parse(localStorage.getItem('user'));

                if (role == undefined)
                    role = getRole();

                return (user != undefined && role == authdata.userRoles.user);
            },
            //
            // isLoggedIn: function () {//isLoggedIn
            //     if (user == undefined) {
            //         user = JSON.parse(localStorage.getItem('user'));
            //     }
            //
            //     //if (role == undefined)
            //     //    role = getRole();
            //
            //     return user != undefined;
            // },

            authorize: function (accessLevel, role) {
                if (role === undefined) {
                    role = getRole();
                }

                if (accessLevel === undefined)
                    accessLevel = authdata.accessLevels.public_;

                return accessLevel & role;
            },

            getUserId: function () {
                var userId = 0;
                if (user == undefined) {
                    user = JSON.parse(localStorage.getItem('user'));
                }

                if (user != undefined)
                    userId = user.id;

                return userId;
            }
        }
    }

    function authHttpResponseInterceptor($q, $location, securityService) {
        return {
            'request': function (config) {
                securityService.secureRequest(config);
                return config || $q.when(config);
            },

            'response': function (response) {
                console.log('response intercepted');

                return response || $q.when(response);
            },

            'responseError': function (rejection) {
                console.log("Server Response Status");

                if (rejection.status == 400) {
                    console.log("[ERROR] Bad request response from the server.");
                } else if (rejection.status === 401) {
                    console.log("[INFO] Unauthorized response.");
                    //console.log('request intercepted');
                    securityService.endSession();
                    $location.path('/login');
                    //MessageService.setMessages(["Please, provide your credentials."]);
                } else if (rejection.status == 403) {
                    console.log("[ERROR] Forbidden: No body can access.");
                } else if (rejection.status == 404) {
                    console.log("[ERROR] Not found.");
                } else if (rejection.status == 409) {
                    console.log("[ERROR] Conflict.");
                } else if (rejection.status == 440) {
                    console.log("[ERROR] Your session has expired.");
                    securityService.endSession();
                    $location.path('/login');
                    setWarningMessage('Por motivos de seguridad su sesión ha finalizado!', false);
                } else if (rejection.status == 500) {
                    console.log("[ERROR] Internal server error.");
                } else {
                    console.log("[ERROR] Unexpected error from server.");
                }
                return $q.reject(rejection);
            }
        }
    }
})();

