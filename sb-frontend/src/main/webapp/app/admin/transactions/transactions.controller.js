(function () {
    'use strict';

    angular
        .module('buho-tracking')
        .controller('TransactionsController', TransactionsController);

    function TransactionsController($scope, dataService) {

        $scope.setDeviceList = setDeviceList;
        $scope.findTransactions = findTransactions;
        $scope.transaction = {
            account: 0,
            device: 0,
            status: "ALL"
        };

        /** CALL SERVICE:  **/
        dataService.findAllAccountAndDeviceList().success(function (data) {
            console.info("accounts: ", data);
            $scope.accounts = data;
        });

        dataService.findAllStatusPayment().success(function (data) {
            console.info("status: ", data);
            $scope.statusPayment = data;
        });

        /** $SCOPES: **/
        function setDeviceList(account) {
            if (!account) {
                return $scope.devices = [];
            }
            $scope.devices = account.deviceList;
        }

        function findTransactions() {
            console.log("transaction->: ", $scope.transaction);

            var accountId = !$scope.transaction.account || $scope.transaction.account.accountId == undefined ? 0 : $scope.transaction.account.accountId;
            var deviceId = !$scope.transaction.device || $scope.transaction.device.id == undefined ? 0 : $scope.transaction.device.id;
            var status = !$scope.transaction.status || $scope.transaction.status.status == undefined ? "ALL" : $scope.transaction.status.status;

            dataService.findTransaction(accountId, deviceId, status).success(function (data) {
                console.log("transactions: ", data);
                if (data.isEmpty) {
                    setWarningMessage("No existe historial de pagos")
                }
                $scope.transactions = data;
            })
        }
    }
})();


