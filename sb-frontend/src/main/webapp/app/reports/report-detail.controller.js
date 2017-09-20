(function () {
    'use strict';

    angular
        .module('buho-tracking')
        .controller('ReportDetailController', ReportDetailController);

    function ReportDetailController($scope, dataService, $translate) {
        console.log("detailedReportCtrl");

        init();

        var mdlMap = function () {
            return {
                control: {},
                center: {
                    latitude: 0,
                    longitude: 0
                },
                options: {
                    scrollwheel: false,
                    maxZoom: 20,
                    minZoom: 2
                },
                zoom: 2,
                dragging: false,
                bounds: {},
                positions: [],
                events: {
                    idle: function (map, eventName, model) {
                    }
                }
            }
        };

        function init() {
            $scope.startDate = moment().format('DD-MM-YYYY');

            $scope.report = {};
            $scope.checkboxModel = {
                stop: false,
                stretch: true
            };
        }

        /**
         * Get all devices.
         */
        dataService.findDevices().success(function (data) {
            console.log("findDevices: ", data);
            if (data.length > 0) {
                $scope.devices = data;
                $scope.deviceSelected = data[0];
            } else {
                $scope.deviceSelected = {};
            }


        });

        function reportDetailed(deviceId, date) {
            dataService.reportDetailed(deviceId, date).success(function (data) {
                console.log("REPORT DETAILED: ", data);
                $scope.report = data;
                renderOnMap();
            });
        }

        $scope.generateReport = function () {
            if ($scope.deviceSelected.id != undefined) {
                var deviceId = $scope.deviceSelected.id;
                var date = $scope.startDate;
                reportDetailed(deviceId, date)

            } else {
                var errorMessage = $translate.instant("you.need.select.some.device");
                setWarningMessage(errorMessage);
            }
        };

        function renderOnMap() {
            if ($scope.report.travels.length == 0) {
                return;
            }

            for (var i = 0; i < $scope.report.travels.length; i++) {
                var positions = $scope.report.travels[i].positions;

                $scope.report.travels[i].maps = mdlMap();
                $scope.report.travels[i].markers = mdlMarker(positions);
                $scope.report.travels[i].polyLine = mdlPolyLine(positions);
            }
        }


        function getCoordinate(positions) {
            var coordinates = [];

            for (var i = 0; i < positions.length; i++) {
                coordinates[i] = {
                    latitude: positions[i].latitude,
                    longitude: positions[i].longitude
                }
            }

            return coordinates;
        }

        function mdlPolyLine(positions) {
            return {
                path: getCoordinate(positions),
                stroke: {color: '#000000', weight: 4, opacity: 0.7},
                editable: false,
                draggable: false,
                geodesic: false,
                visible: true
            }
        }

        function mdlMarker(data) {
            var markers = [];
            var marker = {
                id: data[0].date,
                coords: {
                    latitude: data[0].latitude,
                    longitude: data[0].longitude
                },
                options: {
                    labelContent: "<i class='fa fa-map-marker fa-2x'></i>",
                    labelStyle: {color: "#2ecc71"},
                    labelAnchor: "8 10",
                    icon: " "
                }
            };
            markers.push(marker);

            marker = {
                id: data[data.length - 1].date,
                coords: {
                    latitude: data[data.length - 1].latitude,
                    longitude: data[data.length - 1].longitude
                },
                options: {
                    labelContent: "<i class='fa fa-map-marker fa-2x'></i>",
                    labelStyle: {color: "crimson"},
                    labelAnchor: "8 10",
                    icon: " "
                }
            };

            markers.push(marker);
            return markers;
        }
    }
})();