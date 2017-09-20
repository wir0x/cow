(function () {
    'use strict';

    angular
        .module('buho-tracking')
        .controller('HistoricalController', HistoricalController);

    function HistoricalController($log, $scope, dataService, buhoConfig) {
        var zoomMin = 16;

        init();

        $scope.format = 'dd-MM-yyyy';
        $scope.altInputFormats = ['M!/d!/yyyy'];
        $scope.opened = true;
        $scope.open = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.opened = true;
        };


        angular.extend($scope, {
            map: {
                control: {},
                center: {
                    latitude: buhoConfig.latitudeMap,
                    longitude: buhoConfig.longitudeMap
                },
                options: {
                    panControl: false,
                    maxZoom: 20,
                    minZoom: 5
                },
                zoom: 3,
                dragging: false,
                bounds: {},
                positions: [],
                openedPositionsWindow: {},
                events: {
                    idle: function (map, eventName, model) {
                        $scope.$watch('$scope.historic.positions', function (e) {
                            if ($scope.historic.positions != null) {
                                summaryPosition(map);
                            }
                        });
                    }
                },
                markerEvents: {
                    click: function (gMarker, eventName, model) {
                        model.doShow = model.doShow ? false : true;
                        $scope.map.openedPositionsWindow = model;
                    }
                }
            }
        });

        $scope.loadHistorical = function () {
            var deviceId = $scope.device.id,
                date = $scope.date;
            getHistorical(deviceId, date)
        };

        $scope.historyYesterday = function (deviceId, date) {
            dataService.getDateServer().success(function (data) {
                $scope.date = moment(data, buhoConfig.dateFormat).add(-1).format(buhoConfig.dateFormat);
                getHistorical(deviceId, $scope.date);
            });
        };

        $scope.historyToday = function (deviceId, date) {
            dataService.getDateServer().success(function (data) {
                $scope.date = data;
                getHistorical(deviceId, $scope.date);
            });
        };

        $scope.historyPreviousDate = function () {
            $scope.date = moment($scope.date, buhoConfig.dateFormat).add(-1, 'days').format(buhoConfig.dateFormat);
            getHistorical($scope.device.id, $scope.date);
        };

        $scope.historyNextDate = function () {
            $scope.date = moment($scope.date, buhoConfig.dateFormat).add(1, 'days').format(buhoConfig.dateFormat);
            getHistorical($scope.device.id, $scope.date);
        };

        function getHistorical(deviceId, date) {
            dataService.historical(deviceId, date).success(function (data) {
                $log.info("data historic: ", data);
                $scope.historic = data;

                if (!$scope.historic.positions) {
                    cleanMap();
                    $scope.infoHistoric = {};
                    return;
                }

                $scope.hasPositions = true;
                loadSideBarInformation();
                drawPolyLine();
                autoCenter();
            });
        }

        function summaryPosition(map) {
            console.log("summaryPosition");
            var zoomFilter = filterPositionByZoom(map);
            var areaFilter = filterPositionByArea(zoomFilter, map);
            loadMarkers(areaFilter)
        }

        function init() {
            $scope.historical = {};
            $scope.hasPositions = true;
            dataService.getDateServer().success(function (data) {
                $scope.date = data;
                dataService.findDevices().success(function (data) {
                    $scope.devices = data;
                    if ($scope.devices && $scope.devices.length > 0) {
                        $scope.device = $scope.devices[0];
                        getHistorical($scope.device.id, $scope.date)
                    }
                });
            });
        }

        function loadMarkers(positions) {
            $scope.markers = [];
            var i;
            for (i = 0; i < positions.length; i++) {
                $scope.markers.push({
                    id: positions[i].id,
                    coords: {
                        latitude: positions[i].latitude,
                        longitude: positions[i].longitude
                    },
                    options: {
                        labelContent: positions[i].id,
                        labelAnchor: "16 2",
                        labelClass: 'label-marker',
                        icon: " "
                    }
                });
            }
            $scope.map.positions = [];
            $scope.map.positions = positions;
            $scope.map.optionsMarker = {
                labelContent: "<i class='fa " + $scope.device.icon + " fa-2x'></i>",
                labelStyle: {color: "#" + $scope.device.color},
                labelAnchor: "8 22",
                icon: " "
            };
        }

        function autoCenter() {
            var positions = $scope.historic.positions,
                bounds = new google.maps.LatLngBounds(),
                i;
            for (i = 0; i < positions.length; i++) {
                bounds.extend(new google.maps.LatLng(positions[i].latitude, positions[i].longitude));
            }
            $scope.map.control.getGMap().fitBounds(bounds);
            if ($scope.map.control.getGMap().getZoom() > zoomMin) {
                $scope.map.zoom = zoomMin;
            }
        }

        function loadSideBarInformation() {
            $scope.infoHistoric = {
                icon: $scope.device.icon,
                color: $scope.device.color,
                name: $scope.device.name,
                initTraveled: $scope.historic.firstPosition,
                endTraveled: $scope.historic.lastPosition,
                maxSpeed: $scope.historic.maxSpeed,
                avgSpeed: $scope.historic.avgSpeed,
                idleTime: $scope.historic.lastPosition,
                distance: $scope.historic.distanceTraveled,
                date: $scope.historic.firstPosition,
                time: $scope.historic.firstPosition
            };
        }

        function filterPositionByZoom(map) {
            var range = buhoConfig.zoomRanges[map.getZoom()],
                positions = $scope.historic.positions,
                objFilter = [positions[0]],
                distanceTwoPosition,
                distance = 0,
                i;
            for (i = 0; i < positions.length - 1; i++) {
                distanceTwoPosition = getDistance(positions[i], positions[i + 1]);
                $scope.map.positions.idKey = positions[i].id;
                if (distance >= range) {
                    objFilter.push(positions[i]);
                    distance = 0;
                }
                distance += distanceTwoPosition;
            }
            objFilter.push(positions[positions.length - 1]);
            return objFilter;
        }

        function drawPolyLine() {
            $scope.polylines = [];
            var positions = $scope.historic.positions,
                coordinates = [],
                i;
            for (i = 0; i < positions.length; i++) {
                coordinates.push({
                    latitude: positions[i].latitude,
                    longitude: positions[i].longitude
                });
            }
            $scope.polylines = [{
                id: 1,
                path: coordinates,
                stroke: {color: '#000000', weight: 7, opacity: 0.7},
                editable: false,
                draggable: false,
                geodesic: false,
                visible: true
            }];
        }

        function filterPositionByArea(positions) {
            var filterArea = [],
                ne = $scope.map.control.getGMap().getBounds().getNorthEast(),
                sw = $scope.map.control.getGMap().getBounds().getSouthWest(),
                latPath, lngPath,
                i;
            for (i = 0; i < positions.length; i++) {
                latPath = positions[i].latitude;
                lngPath = positions[i].longitude;
                if (latPath <= ne.lat() && latPath >= sw.lat()) {
                    if (lngPath <= ne.lng() && lngPath >= sw.lng()) {
                        filterArea.push(positions[i]);
                    }
                }
            }
            return filterArea;
        }

        var rad = function (x) {
            return x * Math.PI / 180;
        };

        var getDistance = function (p1, p2) {
            var R = 6378137, // Earth’s mean radius in meter
                dLat = rad(p2.latitude - p1.latitude),
                dLong = rad(p2.longitude - p1.longitude),
                a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(rad(p1.latitude)) * Math.cos(rad(p2.latitude)) * Math.sin(dLong / 2) * Math.sin(dLong / 2),
                c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            return (R * c);
        };

        var cleanMap = function () {
            $scope.hasPositions = false;
            $scope.polylines = [];
            $scope.map.positions = [];
            $scope.markers = [];
            $scope.map.center = {latitude: buhoConfig.latitudeMap, longitude: buhoConfig.longitudeMap};
            $scope.map.zoom = 3;
        }
    }
})();



