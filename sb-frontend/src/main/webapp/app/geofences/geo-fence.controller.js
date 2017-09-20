angular
    .module('buho-tracking')
    .controller('GeoFenceController', GeoFenceController);

function GeoFenceController($scope, dataService, $log, $modal, $window, $translate, buhoConfig) {
    console.log("GeoFenceController");
    var polygon, drawingManager, map;
    var latitude = buhoConfig.latitudeMap;
    var longitude = buhoConfig.longitudeMap;
    var polygons = [];
    var zoomMin = 16;
    var zoomDef = 6;

    function initialize() {
        loadUserDevices();
        setupMap();
        setupDrawingManager();
        hideDrawingControl(drawingManager);
    }

    function getPolygonsPath(polygon) {
        console.log("getPolygonsPath");
        var coordinates = [];
        var paths = polygon.getPaths();

        for (var j = 0; j < paths.length; j++) {
            for (var k = 0; k < paths.getAt(j).length; k++) {
                coordinates.push({
                    latitude: paths.getAt(j).getAt(k).lat().toString(),
                    longitude: paths.getAt(j).getAt(k).lng().toString()
                });
            }
        }
        console.log("coordinates ", JSON.stringify(coordinates));
        return coordinates;
    }

    function hideDrawingControl(drawingManager) {
        //console.log("hideDrawingControl ");
        drawingManager.setOptions({
            drawingControl: false
        });
    }

    var setupDrawingManager = function () {
        console.log("setupDrawingManager");
        drawingManager = new google.maps.drawing.DrawingManager({

            drawingControl: true,
            drawingControlOptions: {
                position: google.maps.ControlPosition.TOP_CENTER,
                drawingModes: [google.maps.drawing.OverlayType.POLYGON]
            }
        });
        drawingManager.setMap(map);

        google.maps.event.addDomListener(drawingManager, 'polygoncomplete', function (e) {
            console.log("Event: polygoncomplete.", polygon);
            polygon = e;

            hideDrawingControl(drawingManager);
            showDeleteControl(map);
            addPolygonListeners(polygon);
            selectPolygon(polygon);
            drawingManager.setDrawingMode(null);
            setCoordinateList(getPolygonsPath(polygon));

        });
    };

    function selectPolygon(polygon) {
        polygon.setOptions({draggable: true, editable: true});
    }

    function unSelectPolygon(polygon) {
        polygon.setOptions({draggable: false, editable: false});
    }

    function addPolygonListeners(polygon) {
        google.maps.event.addDomListener(polygon, 'click', function () {
            console.log("click");
            var polygon = this;
            selectPolygon(polygon);
            setCoordinateList(getPolygonsPath(polygon));
        });
        google.maps.event.clearListeners(map, 'bounds_changed');
        var polygonPath = polygon.getPath();
        google.maps.event.addListener(polygonPath, 'set_at', function () {
            console.log("set_at");
            if (polygon != undefined) {
                selectPolygon(polygon);
                setCoordinateList(getPolygonsPath(polygon));
            }
        });

        google.maps.event.addListener(polygonPath, 'insert_at', function () {
            console.log("insert_at");
            if (polygon != undefined) {
                selectPolygon(polygon);
                setCoordinateList(getPolygonsPath(polygon));
            }
        });
    }

    function setCoordinateList(path) {
        console.log("setCoordinateList");

        $scope.selectedFence.coordinateList = path;

        for (var i = 0; i < $scope.fences.length; i++) {
            if ($scope.fences[i].id == $scope.selectedFence.id) {
                $scope.fences[i].coordinateList = $scope.selectedFence.coordinateList;
                console.log("coordinates fence ", $scope.fences[i].coordinateList);

                console.log("current Form ", $scope.currentForm);

                //Show the undo button on vertex position change
                if ($(".reload-" + $scope.fences[i].id).css('visibility', 'hidden')) {
                    $(".reload-" + $scope.fences[i].id).css('visibility', 'visible');
                }


                if ($scope.currentForm.name != undefined) {
                    console.log("current Form name ", $scope.currentForm);
                    $scope.currentForm.name.$setViewValue($scope.currentForm.name.$viewValue);
                    $scope.$apply();
                } else {
                    console.log("current Form name ", $scope.currentForm.name);
                }
            }
        }
    }

    function deepCopy(obj) {
        if (Object.prototype.toString.call(obj) === '[object Array]') {
            var out = [], i = 0, len = obj.length;
            for (; i < len; i++) {
                out[i] = arguments.callee(obj[i]);
            }
            return out;
        }
        if (typeof obj === 'object') {
            var out = {}, i;
            for (i in obj) {
                out[i] = arguments.callee(obj[i]);
            }
            return out;
        }
        return obj;
    }

    $scope.loadCurrentFence = function (fence, deviceId) {
        console.log('loadCurrentFence' + fence.id);
        loaderOnMap();
        cleanFenceOnMap();

        for (var i = 0; i < $scope.fences.length; i++) {
            if ($scope.fences[i].id == fence.id) {
                $scope.fences[i] = $scope.oldfences[i];
                drawPolygonOnMap($scope.fences[i]);
                checkAllDays($scope.fences[i]) ? $scope.fences[i].alldays = true : $scope.fences[i].alldays = false;
            }
        }
        $scope.oldfences = deepCopy($scope.oldfences);
        setTimeout(function () {
            $('#collapse-' + fence.id).addClass('in');
            $('#collapse-header-' + fence.id).removeClass('collapsed');
        }, 1);
        loaderOnMap();
    };

    function createPolygon(fence, status) {
        var _polygon = getPolygonOfCoordinates(fence.coordinateList);
        var poly = new google.maps.Polygon({
            paths: _polygon,
            editable: true,
            draggable: true,
            fillColor: fence.status ? "#00FF00" : "#FF0000"
        });
        return poly;
    }


    var setupMap = function () {
        var mapOptions = {
            center: new google.maps.LatLng(latitude, longitude),
            zoom: zoomDef,
            mapTypeId: google.maps.MapTypeId.ROADMAP
        };
        map = new google.maps.Map(document.getElementById("map-canvas"),
            mapOptions);
    };

    function showDeleteControl(map) {
        var deleteControlDiv = document.createElement('div');
        var deleteControl = new DeleteControl(deleteControlDiv, map);

        deleteControlDiv.index = 1;
        map.controls[google.maps.ControlPosition.TOP_RIGHT].push(deleteControlDiv);
    }

    function hideDeleteControl(map) {
        map.controls[google.maps.ControlPosition.TOP_RIGHT].clear();
    }

    function DeleteControl(controlDiv, map) {
        controlDiv.style.padding = '5px';

        var controlUI = document.createElement('div');
        controlUI.style.backgroundColor = 'white';
        controlUI.style.borderStyle = 'solid';
        controlUI.style.borderWidth = '1px';
        controlUI.style.cursor = 'pointer';
        controlUI.style.textAlign = 'center';
        controlUI.title = 'Eliminar Cerca';
        controlDiv.appendChild(controlUI);

        var controlText = document.createElement('div');
        controlText.style.fontFamily = 'Arial,sans-serif';
        controlText.style.fontSize = '12px';
        controlText.style.paddingLeft = '4px';
        controlText.style.paddingRight = '4px';
        controlText.innerHTML = 'Eliminar Cerca';
        controlUI.appendChild(controlText);

        google.maps.event.addDomListener(controlUI, 'click', function () {

            hideDeleteControl(map);
            showDrawingControl(drawingManager);

            if (polygon != undefined) {
                polygon.setMap(null);
                polygon = undefined;
                var coord = [];
                setCoordinateList(coord);
            }
        });
    }

    function showDrawingControl(drawingManager) {
        console.log("showDrawingControl");
        drawingManager.setOptions({
            drawingMode: google.maps.drawing.OverlayType.POLYGON,
            drawingControl: true,
            drawingModes: [google.maps.drawing.OverlayType.POLYGON]
        });
    }

    initialize();

    $scope.reDraw = function (fence) {
        console.log("reDrawingManager");
        drawPolygonOnMap(fence);
    };

    $scope.prepareNewFence = function (form) {
        $log.info('prepareNewFence');

        if ($scope.fences && $scope.fences.length > 0
            && $scope.fences[$scope.fences.length - 1].id == undefined) {
            return;
        }

        var fence = {
            deviceId: $scope.fence.deviceId,
            hasCredit: true,
            status: false,
            name: "",
            monday: false,
            tuesday: false,
            wednesday: false,
            thursday: false,
            friday: false,
            saturday: false,
            sunday: false,
            entireDay: false,
            startTime: "00:00",
            endTime: "23:59",
            enteringZone: false,
            leavingZone: false,
            emails: "",
            cellphones: "",
            coordinateList: []
        };

        //collapse all ui
        for (var i = 0; i < $scope.fences.length; i++) {
            $("#collapse-header-" + $scope.fences[i].id).addClass("collapsed");
            $("#collapse-" + $scope.fences[i].id).removeClass("in");
        }

        $scope.fences.push(fence);
        $scope.loadPolygon(fence, form);
        //drawPolygonOnMap(fence)

    };

    $scope.onMasterChange = function () {
        console.log("selected fences", $scope.selectedFence)
        $scope.selectedFence.monday = true;
        $scope.selectedFence.tuesday = true;
        $scope.selectedFence.wednesday = true;
        $scope.selectedFence.thursday = true;
        $scope.selectedFence.friday = true;
        $scope.selectedFence.saturday = true;
        $scope.selectedFence.sunday = true;

    };

    $scope.prepareEditFence = function (fence) {
        console.log("prepareEditFence", fence.name);
        $scope.fence = fence;
    };

    $scope.findFencesByDeviceId = function (deviceId) {
        hideDeleteControl(map);
        hideDrawingControl(drawingManager);
        $log.info("findFencesByDeviceId ", deviceId);

        if (polygon != undefined) {
            if (polygon.length != 0) {
                polygon.setMap(null);
            }
        }
        dataService.findFencesByDeviceId(deviceId).success(function (data, status, headers, config) {
            console.log("polygon ", polygon);
            console.log("findFencesByDeviceId ", data);
            $scope.fences = data;
            $scope.oldfences = deepCopy(data);

            for (var i = 0; i < $scope.fences.length; i++) {
                var obj = $scope.fences[i];
                checkAllDays(obj) ? obj.alldays = true : obj.alldays = false
            }

            if ($scope.fences.length == 0) {
                map.setZoom(zoomDef);
                console.log('remove polygon');
                removePolygons();
                loaderOnMap();
                return;
            }
            $scope.controlForm.$setPristine();
            removePolygons();
            drawAllFences();
        });
    };

    function getPolygonOfCoordinates(coordiantes) {
        var bounds = new google.maps.LatLngBounds();
        var _coordinates = [];
        for (var i = 0; i < coordiantes.length; i++) {
            var latLng = new google.maps.LatLng(coordiantes[i].latitude, coordiantes[i].longitude);
            _coordinates.push(latLng);
            bounds.extend(latLng);
        }
        map.fitBounds(bounds);
        if (map.getZoom() > zoomMin) {
            map.setZoom(zoomMin);
        }

        return _coordinates;
    }

    function hasCoordinates(fence) {
        return fence.coordinateList.length > 0;
    }

    function cleanFenceOnMap() {
        //console.log("cleanFenceOnMap");

        for (var i = 0; i < $scope.fences.length; i++) {
            //console.log("close collapse");

            if (hasCoordinates($scope.fences[i])) {
                if (polygon != undefined) {
                    polygon.setMap(null);
                    polygon = undefined;
                }

            } else {
                $scope.fences[i].coordinateList = [];
                //console.log("map", map);
                //console.log("fence no has coordinates");
            }
            hideDeleteControl(map);
            hideDrawingControl(drawingManager);
        }
    }

    function drawPolygonOnMap(fence) {
        console.log("draw polygon ", fence);
        polygon = createPolygon(fence);
        polygon.setMap(map);
        addPolygonListeners(polygon);
        showDeleteControl(map);
    }

    function isEmpty(element) {
        return element.length == 0;
    }

    function adjustFencesOnMap(fences) {
        var bounds = new google.maps.LatLngBounds();
        var path = [];

        for (var i = 0; i < fences.length; i++) {
            path = [];
            var coordinates = fences[i].coordinateList;

            if (isEmpty(coordinates) && fences[i].id) {
                map.setCenter(new google.maps.LatLng(latitude, longitude));
                map.setZoom(zoomDef);
                return;
            }
            console.log("coordinate ", coordinates);

            for (var j = 0; j < coordinates.length; j++) {
                path.push(new google.maps.LatLng(coordinates[j].latitude, coordinates[j].longitude));
                bounds.extend(path[path.length - 1])
            }
            polygons.push(new google.maps.Polygon({
                paths: path,
                strokeOpacity: 0.7,
                strokeWeight: 1,
                fillColor: fences[i].status ? '#00FF00' : '#FF0000',
                fillOpacity: 0.35
            }));
            polygons[polygons.length - 1].setMap(map);
        }
        map.fitBounds(bounds);
    }

    function drawAllFences() {
        console.log("draw all fences");
        var fences = $scope.fences;
        if (fences.length > 0) {
            adjustFencesOnMap(fences);
        } else {
            map.setCenter(new google.maps.LatLng(latitude, longitude));
            map.setZoom(zoomDef);
        }
        if (map.getZoom() > zoomMin) {
            map.setZoom(zoomMin);
        }
        loaderOnMap();
    }

    function loaderOnMap() {
        //console.log('current display overmap=' + $("#over_map").css('display'));
        if ($("#over_map").css('display') == 'none') {
            $("#over_map").css('display', 'block');
            //console.log('nuevo display =' + $("#over_map").css('display'));
        } else {
            $("#over_map").css('display', 'none');
            console.log('else display overmap=' + $("#over_map").css('display'));
            google.maps.event.addListener(map, 'idle', function () {
                $("#over_map").css('display', 'none');
                //console.log('mapa display overmap=' + $("#over_map").css('display'));
            });
        }
    }

    $scope.loadPolygon = function (fence, form) {
        $log.info('loadPolygon', fence);
        $scope.currentForm = form;
        $scope.selectedFence = fence;

        cleanFenceOnMap();
        removePolygons();

        if (fence.id == undefined) {
            if (!$('#collapse-').hasClass("in")) {
                hasCoordinates(fence) ? drawPolygonOnMap(fence) : showDrawingControl(drawingManager);
            }
            else {
                hideDrawingControl(drawingManager);
            }
        }
        else {
            if (!$('#collapse-' + fence.id).hasClass("in")) {
                $log.info("open collapse");
                hasCoordinates(fence) ? drawPolygonOnMap(fence) : showDrawingControl(drawingManager);
            }
            else {
                drawAllFences();
            }
        }
    };

    function removePolygons() {
        for (var i = 0; i < polygons.length; i++) {
            var polygon_ = polygons[i];
            polygon_.setMap(null);
        }
        polygons = [];
    }

    $scope.deleteFence = function (fence) {
        console.log("delete fence", fence.id);
        if (fence.id) {
            dataService.deleteFence(fence.id)
                .then(function (data) {
                    console.log("data: ", data);
                    cleanFenceOnMap();

                    $scope.fences.splice($scope.fences.indexOf(fence), 1);
                    var message_good = $translate.instant('control.message.good');
                    var message_success = $translate.instant('control.message.delete.success');
                    setSuccessMessage("<b>" + message_good + fence.name + message_success + "</b>");

                    $scope.fences.length == 0 ? map.setZoom(zoomDef) : drawAllFences();

                }, function (error) {
                    console.log("error: ", error);
                    setErrorMessage(error.data);
                });
        } else {
            var success_remove = $translate.instant('control.fence.success.delete');
            setSuccessMessage(success_remove);
            cleanFenceOnMap();
            $scope.fences.splice($scope.fences.indexOf(fence), 1);
        }
        $('body,html').animate({scrollTop: 0}, 450);
    };

    $scope.$on('$locationChangeStart', function (event, next) {
        var isDirty = false;
        for (var i = 0; i < $scope.fences; i++) {
            if ($("#fenceForm-" + $scope.fences[i].id).find(".ng-dirty")) {
                isDirty = true;
            }
        }

        if (isDirty) {
            event.preventDefault();
            var modalInstance = $modal.open({
                templateUrl: 'views/dialogs/device-config-dialog.html',
                controller: 'deviceConfigDialogCtrl'
            });

            modalInstance.result.then(function () {
                console.log("save items");
                dataService.updateFence($scope.fences)
                    .then(function (data) {
                        console.log("data: ", data);
                        var message_good = $translate.instant('control.message.good');
                        var fence_names = $translate.instant('control.fences');
                        var message_success = $translate.instant('control.message.success.fence');
                        setSuccessMessage("<b>" + message_good + fence_names + message_success + "</b>");
                        $scope.controlForm.$setPristine();
                        $window.location.href = next;

                    }, function (error) {
                        console.log("error: ", error);
                        setErrorMessage(error.data);
                        return error;
                    });
            }, function () {
                console.log("lose changes");
                $scope.controlForm.$setPristine();
                $window.location.href = next;
            });
        }
    });

    $scope.registerFence = function (fence, form) {
        $log.info("fenceId:", fence.id);
        if (fence.id) {
            updateFence(fence, form);
            $('body,html').animate({scrollTop: 0}, 500);
        } else {
            createFence(fence, form);
            $('body,html').animate({scrollTop: 0}, 500);
        }
    };

    var createFence = function (fence, form) {
        console.log("register fence", fence);
        var key = "alldays";
        delete fence[key];

        loaderOnMap();
        dataService.registerFence(fence)
            .then(function (data) {
                console.log("data: ", data);
                fence.id = data;
                form.$setPristine();

                var message_good = $translate.instant('control.message.good');
                var message_success = $translate.instant('control.message.success.fence');
                setSuccessMessage("<b>" + message_good + fence.name + message_success + "</b>");

            }, function (error) {
                console.log("error: ", error);
                setErrorMessage(error.data);
            });
        loaderOnMap();
    };

    var updateFence = function (fence, form) {
        console.log("update fence", fence);

        var key = "alldays";
        delete fence[key];

        var arrayFences = [fence];
        dataService.updateFence(arrayFences)
            .then(function (data) {
                console.log("data: ", data);
                checkAllDays(fence) ? fence.alldays = true : fence.alldays = false;
                form.$setPristine();
                var message_good = $translate.instant('control.message.good');
                var message_success = $translate.instant('control.message.success.fence');
                setSuccessMessage("<b>" + message_good + fence.name + message_success + "</b>");

            }, function (error) {
                console.log("error: ", error);
                setErrorMessage(error.data);
            });
    };

    function loadUserDevices() {
        console.log("init");
        dataService.findDevices().success(function (data, status, headers, config) {
            $scope.devices = data;

            if ($scope.devices.length > 0) {
                $scope.fence = {};
                $scope.fence.deviceId = $scope.devices[0].id;
                $scope.findFencesByDeviceId($scope.fence.deviceId);
            }
        });
    }

    function checkAllDays(fence) {
        if (fence.monday && fence.tuesday && fence.wednesday && fence.thursday && fence.friday && fence.saturday && fence.sunday) {
            return true;
        } else {
            return false;
        }
    }

    $scope.blockSpace = function (e) {
        console.log("event ", e);
        var key_code = e.which;
        if (key_code == 32) {
            e.preventDefault();
        }
    };

    $scope.allowOnlyNumbers = function (e) {
        var a = [];
        var key_code = e.which;

        for (var i = 48; i < 58; i++) {
            a.push(i);
        }

        if (!(a.indexOf(key_code) >= 0 || key_code == 44)) {
            e.preventDefault();
        }
    }
}





