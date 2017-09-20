(function () {

    'use strict';

    angular
        .module('buho-tracking')
        .factory('dataService', dataService)
        .factory('onlineStatus', onlineStatus)
        .factory('Excel', Excel);

    //////

    function dataService($http, $location) {
        var domainUrl = $location.protocol() + "://" + $location.host() + ":" + $location.port() + "/";
        var rootRestfulPath = domainUrl + 'rest/';
        //var rootRestfulPath = 'http://192.168.0.141:8080/rest/';

        return {
            login: login,
            passwordRecovery: passwordRecovery,
            getPasswordToken: getPasswordToken,
            checkSessionTime: checkSessionTime,
            getCurrentPosition: getCurrentPosition,
            postTransferDevice: postTransferDevice,
            findAllAccountAndDeviceList: findAllAccountAndDeviceList,
            getDeviceStatus: getDeviceStatus,
            findAllStatusPayment: findAllStatusPayment,
            resetPassword: resetPassword,
            getDateServer: getDateServer,
            findLastPosition: findLastPosition,
            historical: historical,
            findDeviceType: findDeviceType,
            findAllPositionsBetweenDatesByDevice: findAllPositionsBetweenDatesByDevice,
            reportDetailed: reportDetailed,
            findFencesByDeviceId: findFencesByDeviceId,
            registerFence: registerFence,
            updateFence: updateFence,
            deleteFence: deleteFence,
            deletePositions: deletePositions,
            findDevices: findDevices,

            findFreeDevices: function () {
                return $http.get(rootRestfulPath + 'device/backend/list/free');
            },

            findDevicesByAccountId: function (accountId) {
                return $http.get(rootRestfulPath + 'device/backend/list/' + accountId);
            },

            updateDeviceAssignment: function (devices, accountId) {
                return $http.put(rootRestfulPath + 'device/backend/update/' + accountId, devices).then(function (response) {
                    return response.data;
                });
            },

            findAdminDevices: function () {
                return $http.get(rootRestfulPath + 'device/list/admin');
            },

            updateDevice: function (device) {
                return $http.put(rootRestfulPath + 'device/update', device).then(function (response) {
                    return response.data;
                });
            },

            findAllDevicesForBackend: function () {
                return $http.get(rootRestfulPath + 'device/backend/list');
            },

            registerDeviceForBackend: function (device) {
                return $http.post(rootRestfulPath + 'device/backend/create', device).then(function (response) {
                    return response.data;
                });
            },

            deleteDeviceForBackend: function (deviceId) {
                return $http.delete(rootRestfulPath + 'device/backend/delete/' + deviceId).then(function (response) {
                    return response.data;
                });
            },

            findAllAccounts: function () {
                return $http.get(rootRestfulPath + 'accounts');
            },

            createAccount: function (account) {
                return $http.post(rootRestfulPath + 'accounts', account).then(function (response) {
                    return response.data;
                });
            },

            findAllSubscription: function () {
                return $http.get(rootRestfulPath + 'subscription/list')
            },

            registerNewSubscription: function (subscription) {
                return $http.post(rootRestfulPath + 'subscription/create', subscription).then(function (response) {
                    return response.data;
                });
            },

            updateSubscription: function (subscription) {
                return $http.put(rootRestfulPath + 'subscription/update', subscription).then(function (response) {
                    return response.data;
                })
            },
            updateSubscriptionUserPay: function (subscription) {
                return $http.put(rootRestfulPath + 'subscription/update/user-pay', subscription).then(function (response) {
                    return response.data;
                })
            },

            deleteSubscription: function (subscriptionId) {
                return $http.delete(rootRestfulPath + 'subscription/delete/' + subscriptionId).then(function (response) {
                    return response.data;
                });
            },

            resetPasswordUser: function (user) {
                return $http.put(rootRestfulPath + 'users/reset/password', user).then(function (response) {
                    return response.data;
                });
            },

            updateAccount: function (id, account) {
                return $http.put(rootRestfulPath + 'accounts/' + id, account).then(function (response) {
                    return response.data;
                });
            },

            deleteAccount: function (id) {
                return $http.delete(rootRestfulPath + 'accounts/' + id).then(function (response) {
                    return response.data;
                });
            },

            findAllUsers: function () {
                return $http.get(rootRestfulPath + 'users');
            },

            registerUser: function (user) {
                return $http.post(rootRestfulPath + 'users', user).then(function (response) {
                    return response.data;
                });
            },

            updateUser: function (user) {
                return $http.put(rootRestfulPath + 'users', user).then(function (response) {
                    return response.data;
                });
            },

            deleteUser: function (userId) {
                return $http.delete(rootRestfulPath + 'users/' + userId).then(function (response) {
                    return response.data;
                });
            },

            changePasswordUser: function (userSecurity) {
                return $http.put(rootRestfulPath + 'users/password', userSecurity).then(function (response) {
                    return response.data;
                });
            },

            changeAdminPasswordUser: function (userSecurity) {
                return $http.put(rootRestfulPath + 'users/reset/password', userSecurity).then(function (response) {
                    return response.data;
                });
            },

            sendMessage: function (contact) {
                return $http.post(rootRestfulPath + 'contact/send-mail', contact).then(function (response) {
                    return response.data;
                });
            },

            validationTM: function (subscriptionPayDto) {
                return $http.post(rootRestfulPath + 'subscription/validate/tigo-money', subscriptionPayDto).then(function (response) {
                    return response.data;
                });
            },

            findAllServicePlan: function () {
                return $http.get(rootRestfulPath + 'subscription/service-plan');
            },

            findAllGroups: function () {
                return $http.get(rootRestfulPath + 'group/list');
            },

            createSubscription: function (subscription) {
                return $http.post(rootRestfulPath + 'subscription/create-subscription', subscription).then(function (response) {
                    return response.data;
                })
            },

            getSubscriptionsList: function () {
                return $http.get(rootRestfulPath + 'subscription/management/list');
            },

            addItemsToShoppingCart: function (items) {
                return $http.post(rootRestfulPath + 'subscription/shopping-cart', items).then(function (response) {
                    return response.data;
                })
            },

            findShoppingCartList: function () {
                return $http.get(rootRestfulPath + "subscription/shopping-cart");
            },

            deleteItemFromShoppingCart: function (subscriptionId) {
                return $http.delete(rootRestfulPath + 'subscription/shopping-cart/' + subscriptionId).then(function (response) {
                    return response.data;
                });
            },

            tigoMoneyPayment: function (billData) {
                return $http.post(rootRestfulPath + 'subscription/smart/create/web/', billData).then(function (response) {
                    return response.data;
                })
            },

            getShoppingCartItems: function () {
                return $http.get(rootRestfulPath + "subscription/shopping-cart/items");
            },

            requestStatusPayment: function (requestId) {
                return $http.get(rootRestfulPath + "subscription/pay/status/" + requestId);
            },

            isPaymentProcessing: function () {
                return $http.get(rootRestfulPath + "subscription/pay/check-status");
            },

            findBillStatus: function () {
                return $http.get(rootRestfulPath + "accounts/info");
            },

            updateBillStatus: function (billStatus) {
                return $http.put(rootRestfulPath + "accounts/info", billStatus).then(function (response) {
                    return response.data;
                });
            },

            unlinkDevice: function (deviceId) {
                return $http.put(rootRestfulPath + 'device/unlink/' + deviceId).then(function (response) {
                    return response.data;
                });
            },

            paymentHistorial: function () {
                return $http.get(rootRestfulPath + "payments");
            },

            register: function (registerFrom) {
                return $http.post(rootRestfulPath + 'authenticate/create/web/account', registerFrom).then(function (response) {
                    return response.data;
                });
            },

            linkingSmartphone: function (addSmartphone) {
                return $http.post(rootRestfulPath + 'device/smartphone/linking', addSmartphone).then(function (response) {
                    return response.data;
                })
            },

            deletePaymentHistorial: function (historials) {
                return $http.put(rootRestfulPath + 'payments/', historials).then(function (response) {
                    return response.data;
                });
            },
            updateEmailInvitation: function (email) {
                return $http.post(rootRestfulPath + 'mails/invitation/', email).then(function (response) {
                    return response.data;
                });
            },
            findTransaction: function (accountId, deviceId, status) {
                return $http.get(rootRestfulPath + 'payments/' + accountId + '/' + deviceId + '/' + status);
            }
        };

        function login(credentials) {
            var config = {
                url: rootRestfulPath + "authenticate",
                method: 'post',
                data: credentials,
                headers: {
                    "Content-Type": "application/x-authc-username-password-web+json;application/json;charset=UTF-8"
                }
            };
            return $http(config);
        }

        function passwordRecovery(token) {
            return $http.get(rootRestfulPath + "authenticate/password-token/" + token);
        }

        function getPasswordToken(token) {
            return $http.get(rootRestfulPath + "authenticate/password-token/" + token);
        }

        function checkSessionTime(token) {
            return $http.get(rootRestfulPath + "authenticate/check-session-time");
        }

        function getCurrentPosition() {
            return $http.get(rootRestfulPath + 'devices/positions/current');
        }

        function postTransferDevice(transferObj) {
            return $http.post(rootRestfulPath + 'device/transfer', transferObj).then(function (response) {
                return response;
            });
        }

        function findAllAccountAndDeviceList() {
            return $http.get(rootRestfulPath + "accounts/transactions");
        }

        function getDeviceStatus() {
            return $http.get(rootRestfulPath + "device/status");
        }

        function findAllStatusPayment() {
            return $http.get(rootRestfulPath + "accounts/transactions/status-payment");
        }

        function resetPassword(passwordtoken) {
            return $http.put(rootRestfulPath + "authenticate/reset-password", passwordtoken).then(function (response) {
                return response.data;
            });
        }

        function getDateServer() {
            return $http.get(rootRestfulPath + "position/string-date-server");
        }

        function findLastPosition() {
            return $http.get(rootRestfulPath + 'position/list/last/');
        }

        function historical(devicesId, date) {
            return $http.get(rootRestfulPath + 'position/' + devicesId + '/' + date);
        }

        function findDeviceType() {
            return $http.get(rootRestfulPath + 'device/backend/list/device-type');
        }

        function findAllPositionsBetweenDatesByDevice(deviceId, starDate, endDate) {
            return $http.get(rootRestfulPath + 'position/list-report-by-device/' + deviceId + '/' + starDate + '/' + endDate);
        }

        function reportDetailed(deviceId, date) {
            return $http.get(rootRestfulPath + 'position/' + deviceId + '/report/' + date + '/detailed');
        }

        function findFencesByDeviceId(deviceId) {
            if (!deviceId) {
                return [];
            }
            return $http.get(rootRestfulPath + 'fences/devices/' + deviceId);
        }

        function registerFence(fence) {
            return $http.post(rootRestfulPath + 'fences', fence).then(function (response) {
                return response.data;
            });
        }

        function updateFence(fence) {
            return $http.put(rootRestfulPath + 'fences', fence).then(function (response) {
                return response.data;
            });
        }

        function deleteFence(fenceId) {
            return $http.delete(rootRestfulPath + 'fences/' + fenceId).then(function (response) {
                return response.data;
            });
        }

        function deletePositions(deviceId) {
            return $http.delete(rootRestfulPath + 'position/backend/delete/' + deviceId).then(function (response) {
                return response.data;
            });
        }

        function findDevices() {
            return $http.get(rootRestfulPath + 'device/list');
        }

        return {


        }
    }

    function onlineStatus($window, $rootScope) {
        var onlineStatus = {};

        onlineStatus.onLine = $window.navigator.onLine;

        onlineStatus.isOnline = function () {
            return onlineStatus.onLine;
        };

        $window.addEventListener("online", function () {
            onlineStatus.onLine = true;
            $rootScope.$digest();
        }, true);

        $window.addEventListener("offline", function () {
            onlineStatus.onLine = false;
            $rootScope.$digest();
        }, true);

        return onlineStatus;
    }

    function Excel($window) {
        var uri = 'data:application/vnd.ms-excel;base64,',
            template = '<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40"><head><!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet><x:Name>{worksheet}</x:Name><x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions></x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]--></head><body><table>{table}</table></body></html>',
            base64 = function (s) {
                return $window.btoa(unescape(encodeURIComponent(s)));
            },
            format = function (s, c) {
                return s.replace(/{(\w+)}/g, function (m, p) {
                    return c[p];
                })
            };
        return {
            tableToExcel: function (tableId, worksheetName) {
                var table = $(tableId),
                    ctx = {worksheet: worksheetName, table: table.html()},
                    href = uri + base64(format(template, ctx));
                return href;
            }
        };
    }

})();




