(function () {

    'use strict';

    angular
        .module('buho-tracking')
        .constant('buhoConfig', {
            //"buhoMapStyle": [
            //    {
            //        "featureType": "landscape.man_made",
            //        "elementType": "geometry",
            //        "stylers": [
            //            {
            //                "color": "#f7f1df"
            //            }
            //        ]
            //    },
            //    {
            //        "featureType": "landscape.natural",
            //        "elementType": "geometry",
            //        "stylers": [
            //            {
            //                "color": "#d0e3b4"
            //            }
            //        ]
            //    },
            //    {
            //        "featureType": "landscape.natural.terrain",
            //        "elementType": "geometry",
            //        "stylers": [
            //            {
            //                "visibility": "off"
            //            }
            //        ]
            //    },
            //    {
            //        "featureType": "poi",
            //        "elementType": "labels",
            //        "stylers": [
            //            {
            //                "visibility": "off"
            //            }
            //        ]
            //    },
            //    {
            //        "featureType": "poi.business",
            //        "elementType": "all",
            //        "stylers": [
            //            {
            //                "visibility": "on"
            //            }
            //        ]
            //    },
            //    {
            //        "featureType": "poi.medical",
            //        "elementType": "geometry",
            //        "stylers": [
            //            {
            //                "color": "#fbd3da"
            //            }
            //        ]
            //    },
            //    {
            //        "featureType": "poi.park",
            //        "elementType": "geometry",
            //        "stylers": [
            //            {
            //                "color": "#bde6ab"
            //            }
            //        ]
            //    },
            //    {
            //        "featureType": "road",
            //        "elementType": "geometry.stroke",
            //        "stylers": [
            //            {
            //                "visibility": "on"
            //            }
            //        ]
            //    },
            //    {
            //        "featureType": "road",
            //        "elementType": "labels",
            //        "stylers": [
            //            {
            //                "visibility": "on"
            //            }
            //        ]
            //    },
            //    {
            //        "featureType": "road.highway",
            //        "elementType": "geometry.fill",
            //        "stylers": [
            //            {
            //                "color": "#ffe15f"
            //            }
            //        ]
            //    },
            //    {
            //        "featureType": "road.highway",
            //        "elementType": "geometry.stroke",
            //        "stylers": [
            //            {
            //                "color": "#efd151"
            //            }
            //        ]
            //    },
            //    {
            //        "featureType": "road.arterial",
            //        "elementType": "geometry.fill",
            //        "stylers": [
            //            {
            //                "color": "#ffffff"
            //            }
            //        ]
            //    },
            //    {
            //        "featureType": "road.local",
            //        "elementType": "geometry.fill",
            //        "stylers": [
            //            {
            //                "color": "black"
            //            }
            //        ]
            //    },
            //    {
            //        "featureType": "transit.station.airport",
            //        "elementType": "geometry.fill",
            //        "stylers": [
            //            {
            //                "color": "#cfb2db"
            //            }
            //        ]
            //    },
            //    {
            //        "featureType": "water",
            //        "elementType": "geometry",
            //        "stylers": [
            //            {
            //                "color": "#a2daf2"
            //            }
            //        ]
            //    }
            //],
            "buhoMapStyle": [{
                "featureType": "landscape",
                "stylers": [{"hue": "#FFBB00"}, {"saturation": 43.400000000000006}, {"lightness": 37.599999999999994}, {"gamma": 1}]
            }, {
                "featureType": "road.highway",
                "stylers": [{"hue": "#FFC200"}, {"saturation": -61.8}, {"lightness": 45.599999999999994}, {"gamma": 1}]
            }, {
                "featureType": "road.arterial",
                "stylers": [{"hue": "#FF0300"}, {"saturation": -100}, {"lightness": 51.19999999999999}, {"gamma": 1}]
            }, {
                "featureType": "road.local",
                "stylers": [{"hue": "#FF0300"}, {"saturation": -100}, {"lightness": 52}, {"gamma": 1}]
            }, {
                "featureType": "water",
                "stylers": [{"hue": "#0078FF"}, {"saturation": -13.200000000000003}, {"lightness": 2.4000000000000057}, {"gamma": 1}]
            }, {
                "featureType": "poi",
                "stylers": [{"hue": "#00FF6A"}, {"saturation": -1.0989010989011234}, {"lightness": 11.200000000000017}, {"gamma": 1}]
            }],
            "latitudeMap": -17.8000,
            "longitudeMap": -63.1833,
            "swissbytes": {
                latitude: -17.786971, longitude: -63.212735
            },
            "dateFormat": 'DD-MM-YYYY',
            "timeOutCurrentPosition": 20000, // ms
            "buhoMapStyleOld": [{"stylers": [{"hue": "#00A6F9"}, {"gamma": 1}]}],
            "zoomRanges": [550000, 500000, 450000, 350000, 250000, 150000, 85000, 40000, 20000, 15000, 9000, 5000, 2000, 1000, 600, 350, 100, 50, 20, 10, 5, 1]
        })
    ;
})();



