(function () {

    'use strict';

    angular
        .module('buho-tracking')
        .controller('ReportsController', ReportsController);

    function ReportsController($scope, $log, dataService, Excel, $timeout) {
        function init() {
            $scope.startDate = moment().format('DD-MM-YYYY');
            $scope.endDate = moment().format('DD-MM-YYYY');
            $scope.selectedDevice = {};
            $scope.currentPage = 1;
            $scope.numPerPage = 10;
            $scope.maxSize = 5;
            loadDevices();
        }

        var loadDevices = function () {
            dataService.findDevices().success(function (data) {
                $scope.deviceList = data;
                $log.info("deviceList:", $scope.deviceList);
            });
        };

        $scope.searchReport = function () {
            if ($scope.deviceList.length == 0) {
                var message = $translate.instant('msg.war.not.has.device');
                setWarningMessage(message);
                return;
            }

            if ($scope.selectedDevice.id) {
                dataService.findAllPositionsBetweenDatesByDevice($scope.selectedDevice.id, $scope.startDate, $scope.endDate).success(function (data, status, headers, config) {
                    $scope.positions = data;
                    $log.info("positionByDevice:", $scope.positions);
                    $log.info("positionByDevice length", $scope.positions.length);
                    $scope.$watch('currentPage + numPerPage', function () {
                        var begin = (($scope.currentPage - 1) * $scope.numPerPage)
                            , end = begin + $scope.numPerPage;
                        if ($scope.positions != null) {
                            $scope.filteredPositions = $scope.positions.slice(begin, end);
                        }
                    });
                    $scope.reportGenerated = true;
                });
            } else {
                var errorMessage = "Selecciona un dispositivo primero";
                setWarningMessage(errorMessage);
            }
        };

        $scope.exportToExcel = function (tableId) { // ex: '#my-table'
            var exportHref = Excel.tableToExcel(tableId, 'reporte de posiciones');
            $timeout(function () {
                location.href = exportHref;
            }, 100); // trigger download
        }

        $scope.exportToPDF = function () {
            $('#table-report-devices').tableExport(
                {
                    type: 'pdf',
                    pdfFontSize: 10,
                    escape: 'false'
                });
        };

        $scope.printSelection = function () {
            var printContent = document.getElementById("table-report-devices");

            var num;
            var uniqueName = new Date();

            var windowName = 'Print' + uniqueName.getTime();
            var printWindow = window.open(num, windowName);

            var cssReference = document.createElement("link");
            cssReference.href = "../css/buho-styles.css";
            cssReference.rel = "stylesheet";
            cssReference.type = "text/css";
            printWindow.document.getElementsByTagName('head')[0].appendChild(cssReference);
            printWindow.document.write(printContent.outerHTML);

            printWindow.document.close();
            printWindow.focus();
            printWindow.print();
            printWindow.close();
        };

        $scope.printAllTable = function () {
            var printContent = document.getElementById("table-report-devices-hidden");

            var num;
            var uniqueName = new Date();

            var windowName = 'Print' + uniqueName.getTime();
            var printWindow = window.open(num, windowName);

            var cssReference = document.createElement("link");
            cssReference.href = "../css/buho-styles.css";
            cssReference.rel = "stylesheet";
            cssReference.type = "text/css";
            printWindow.document.getElementsByTagName('head')[0].appendChild(cssReference);
            printWindow.document.write(printContent.outerHTML);

            printWindow.document.close();
            printWindow.focus();
            printWindow.print();
            printWindow.close();
        };


        init();
        console.log("reportsCtrl");
    }

})();



