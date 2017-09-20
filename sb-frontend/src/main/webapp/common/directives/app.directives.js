(function () {

    'use strict';

    angular
        .module('buho-tracking')
        .directive('showtab', showtab)
        .directive('showTooltip', showTooltip)
        .directive('pickColor', pickAColor)
        .directive('iconPicker', iconPicker)
        .directive('datePicker', datePicker)
        .directive('timePicker', timePicker)
        .directive('ngViewFix', ngViewFix)
        .directive('lowercase', lowercase)
        .directive('exportTable', exportTable)
        .directive('exportTable', exportTable)
        .directive('validNumber', validNumber)
        .directive('pwCheck', pwCheck);

    ///////

    function showTooltip(x, y, label, data) {
        $('<div id="flot-tooltip">' + '<b>' + label + ': </b><i>' + data + '</i>' + '</div>').css({
            top: y + 5,
            left: x + 20
        }).appendTo("body").fadeIn(200);
    }


    function showtab() {
        return {
            link: function (scope, element, attrs) {
                element.click(function (e) {
                    e.preventDefault();
                    $(element).tab('show');
                });
            }
        };
    }

// Pick A Color JQuery plugin initialization
    function pickAColor($timeout) {
        return {
            restrict: 'A',
            scope: {
                color: '='
            },
            link: function (scope, element, attrs) {
                scope.isCreated = false;
                scope.$watch("color", function () {
                    if (scope.color != undefined && !scope.isCreated) {
                        scope.isCreated = true;
                        //$(element).pickAColor(scope.color);
                        $(element).pickAColor(scope.$eval(attrs.pickColor));
                    }
                })
            }
        };
    }

// Pick A Color JQuery plugin initialization
    function iconPicker() {
        return {
            require: '^form',
            restrict: 'A',
            link: function (scope, element, attrs, ctrl) {
                $(element).iconpicker(scope.$eval(attrs.iconPicker));
                $(element).on('change', function (e) {
                    scope.$apply(function () {
                        ctrl.$setDirty();
                        scope.device.icon = e.icon;
                    });
                });
            }
        };
    }

    function datePicker() {
        return {
            restrict: 'E',
            template: '<input type="text" class="form-control">',
            replace: true,
            link: function (scope, element, attrs) {
                $(element).datepicker({
                    format: "dd-mm-yyyy",
                    todayHighlight: true,
                    autoclose: true
                });
            }
        };
    }

    function timePicker() {
        return {
            restrict: 'E',
            template: '<input type="date" class="form-control">',
            replace: true,
            link: function (scope, element, attrs) {
                $(element).timepicker({
                    minuteStep: 1,
                    showSeconds: false,
                    showMeridian: true,
                    disableFocus: true,
                    showWidget: true
                });
            }
        };
    }

    function ngViewFix($route) {
        return function () {
            $route.reload();
        };
    }

    function lowercase() {
        return {
            require: 'ngModel',
            link: function (scope, element, attrs, modelCtrl) {
                modelCtrl.$parsers.push(function (input) {
                    return input ? input.toLowerCase() : "";
                });
                element.css("text-transform", "lowercase");
            }
        };
    }

    function exportTable() {
        var link = function ($scope, elm, attr) {
            $scope.$on('export-pdf', function (e, d) {
                elm.tableExport({type: 'pdf', pdfFontSize: '8', pdfLeftMargin: 10, escape: 'false'});
            });
            $scope.$on('export-excel', function (e, d) {
                elm.tableExport({type: 'excel', escape: false});
            });
            $scope.$on('export-doc', function (e, d) {
                elm.tableExport({type: 'doc', escape: false});
            });
        };

        return {
            restrict: 'C',
            link: link
        };
    }

    function validNumber() {
        return {
            require: '?ngModel',
            link: function (scope, element, attrs, ngModelCtrl) {
                if (!ngModelCtrl) {
                    return;
                }

                ngModelCtrl.$parsers.push(function (val) {
                    var clean = val.replace(/[^0-9,]+/g, '');
                    if (val !== clean) {
                        ngModelCtrl.$setViewValue(clean);
                        ngModelCtrl.$render();
                    }
                    return clean;
                });

                element.bind('keypress', function (event) {
                    if (event.keyCode === 32) {
                        event.preventDefault();
                    }
                });
            }
        };
    }

    function pwCheck() {
        return {
            require: 'ngModel',
            scope: {

                reference: '=pwCheck'

            },
            link: function (scope, elm, attrs, ctrl) {
                ctrl.$parsers.unshift(function (viewValue, $scope) {
                    var noMatch = viewValue != scope.reference;
                    ctrl.$setValidity('noMatch', !noMatch);
                    return (noMatch) ? noMatch : !noMatch;
                });

                scope.$watch("reference", function (value) {
                    ctrl.$setValidity('noMatch', value === ctrl.$viewValue);
                });
            }
        }
    }
})();







