(function () {

    'use strict';

    angular
        .module('buho-tracking')
        .controller('CurrentPositionController', CurrentPositionController);


    function CurrentPositionController($scope, $interval, dataService, buhoConfig) {
        var maxZoom = 17,
            minZoom = 5;
        $scope.positions = [];

        init();

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
                    minZoom: 2
                },
                zoom: minZoom,
                dragging: false,
                bounds: {},
                positions: [],
                infoWindow: {},
                events: {
                    idle: function (map, eventName, model) {
                    }
                },
                markerEvents: {
                    click: function (gMarker, eventName, model) {
                        model.doShow = true;
                        $scope.map.infoWindow = model;
                        openInfoAccordion(model);
                    }
                }
            }
        });

        $scope.closeInfoWindow = function () {
            return $scope.map.infoWindow.doShow = false;
        };

        $scope.openAccordion = function (elem) {
            $('#collapse-' + elem.device.id).css('display', 'block');
            if (!elem.position) return;
            if ($('#collapse-' + elem.device.id).hasClass('in')) {
                autoCenter();
            } else {
                $scope.map.center = {latitude: elem.position.latitude, longitude: elem.position.longitude};
                $scope.map.zoom = maxZoom;
            }
        };

        $scope.createScrolling = function () {
            $scope.rowToDelete = [];
            var len = $scope.paymentHistorial.length;
            if ($scope.checkAlls) {
                $scope.checkAlls = false;
                for (var i = 0; i < len; i++) {
                    $scope.checkboxes[$scope.paymentHistorial[i].id] = false;
                }
            } else {
                $scope.checkAlls = true;
                for (var j = 0; j < len; j++) {
                    $scope.checkboxes[$scope.paymentHistorial[j].id] = true;
                }
            }
        };

        $scope.$on("$destroy", function () {
            if (intervalCurrentPosition) {
                $interval.cancel(intervalCurrentPosition);
            }
        });

        var closeAllAccordions = function () {
            var length = $scope.rcp.length,
                i, id;
            for (i = 0; i < length; i++) {
                id = $scope.rcp[i].device.id;
                $("#collapse-" + id).removeClass("in");
                $("#collapse-" + id).css('height', '2px');
                $("#collapse-" + id).css('display', 'none');
                $("#collapse-header-" + id).addClass("collapsed");
            }
        };

        var openCollapse = function (elem) {
            var deviceCollapse = "#collapse-" + elem.device.id;
            $(deviceCollapse).addClass("in");
            $(deviceCollapse).css('height', '');
            $(deviceCollapse).css('display', 'block');
            $("#collapse-header-" + elem.device.id).removeClass("collapsed");
        };

        var hideLoaderOnMap = function () {
            $("#over_map").css('display', 'none');
        };

        var intervalCurrentPosition = $interval(loadLastPositions, buhoConfig.timeOutCurrentPosition);

        function loadLastPositions() {
            dataService.getCurrentPosition().success(function (data) {
                $scope.rcp = data;
                loadInvisibleMarkers();
                loadMarkers();
            });
        }

        function loadMarkers() {
            $scope.markers = [];
            var length = $scope.rcp.length,
                icon,
                i;
            for (i = 0; i < length; i++) {
                icon = "<i title='" + $scope.rcp[i].device.name + "' class='fa " + $scope.rcp[i].device.icon + " fa-2x'>";
                if ($scope.rcp[i].position != null) {
                    if (!$scope.rcp[i].position.actual)
                        icon += "</i><i title='Ultima posición' class='fa fa-clock-o red '></i>";
                    var marker = {
                        id: $scope.rcp[i].device.id,
                        coords: {
                            latitude: $scope.rcp[i].position.latitude,
                            longitude: $scope.rcp[i].position.longitude
                        },
                        options: {
                            labelContent: icon,
                            labelStyle: {color: "#" + $scope.rcp[i].device.color},
                            labelAnchor: "8 10",
                            icon: " "
                        }
                    };
                    $scope.markers.push(marker);
                }

            }
            hideLoaderOnMap();
        }

        function loadInvisibleMarkers() {
            $scope.map.optionsMarker = {
                labelContent: "<i class='fa fa-bullseye fa-4x'></i>",
                labelClass: "labelInvisible",
                labelAnchor: "14 22",
                icon: " "
            };
        }

        function openInfoAccordion(elem) {
            closeAllAccordions();
            openCollapse(elem)
        }

        function autoCenter() {
            var bounds = new google.maps.LatLngBounds(),
                length = $scope.rcp.length,
                i;
            for (i = 0; i < length; i++) {
                if ($scope.rcp[i].position)
                    bounds.extend(new google.maps.LatLng($scope.rcp[i].position.latitude, $scope.rcp[i].position.longitude));
            }
            $scope.map.control.getGMap().fitBounds(bounds);
            if ($scope.map.control.getGMap().getZoom() > maxZoom) {
                $scope.map.zoom = maxZoom;
            }
        }

        function refillPositions() {
            var length = $scope.rcp.length,
                i, j = 0;
            console.log('length: ', $scope.rcp.length);
            for (i = 0; i < length; i++) {

                if ($scope.rcp[i].position) {
                    $scope.positions.push($scope.rcp[i].position);
                    $scope.positions[j].device = $scope.rcp[i].device;
                    j++;
                } else {
                    console.log("NULL -> ", $scope.rcp[i].position)
                }
            }
            loadInvisibleMarkers();
        }

        function init() {
            dataService.getCurrentPosition().success(function (data) {
                console.log("callback::: ", data);
                $scope.rcp = data;
                refillPositions();
                autoCenter();
                loadMarkers();
                $('body').css("height", $('.cp-side-menu').height());
                $('#content-wrapper').css("height", $('.cp-side-menu').height());
            });

            // Add scrolling on map
            // the map follow the user scroll when numbers of devices
            // not fit the screen
            var is_safari = navigator.userAgent.indexOf("Safari") > -1,
                is_chrome = navigator.userAgent.indexOf('Chrome') > -1,
                is_firefox = navigator.userAgent.indexOf('Firefox') > -1;
            if ((is_chrome) && (is_safari)) {
                is_safari = false;
            }
            if (is_safari || is_firefox) {
                $(window).scroll(function (event) {
                    if ($(window).width() >= 1080) {
                        var scroll = $(window).scrollTop();
                        clearTimeout($.data(this, 'scrollTimer'));
                        $.data(this, 'scrollTimer', setTimeout(function () {
                            if (scroll < 53) {
                                $('.wrapper-map').animate({'marginTop': "0px"}, 500);
                            } else {
                                $('.wrapper-map').animate({'marginTop': scroll - 53 + "px"}, 500);
                            }
                        }, 250));
                        console.log("width ", $(window).width());
                        console.log("scroll ", scroll);
                    }
                });
            } else {
                $(window).scroll(function (event) {
                    var scroll = $(window).scrollTop();
                    if ($(window).width() >= 1080) {
                        if (scroll > 53) $('.wrapper-map').css("margin-top", scroll - 53 + "px");
                        if (scroll == 0) $('.wrapper-map').css("margin-top", "0px");
                    }
                });
            }

        }

    }

})();


