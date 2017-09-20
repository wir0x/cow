(function () {

    'use strict';

    angular
        .module('buho-tracking')
        .controller('ContactUsController', ContactUsController);


    function ContactUsController(dataService, $translate, securityService, buhoConfig) {
        var vm = this;
        vm.sendMessage = sendMessage;
        vm.from = securityService.getUser().email;
        vm.disableButton = true;
        vm.validFrom = true;
        vm.validMessage = true;
        vm.c = true;
        setupMap();

        /** Functions implementation **/

        function setupMap() {
            var swissBytes = new google.maps.LatLng(buhoConfig.swissbytes.latitude, buhoConfig.swissbytes.longitude);
            var mapOptions = {
                center: swissBytes,
                zoom: 16,
                mapTypeId: google.maps.MapTypeId.ROADMAP,
                scrollwheel: false
            };
            var marker = new google.maps.Marker({
                position: swissBytes,
                map: new google.maps.Map(document.getElementById("map-canvas"), mapOptions),
                title: 'SWISSBYTES!'
            });
            var infoWindow = new google.maps.InfoWindow({
                content: "SwissBytes"
            });
            google.maps.event.addListener(marker, 'click', function () {
                infoWindow.open(map, marker);
            });
        }

        function sendMessage() {
            validateFormFields();
            var contact = {from: vm.from, title: vm.title, message: vm.message};

            if (!vm.formValid) {
                dataService.sendMessage(contact).then(success, error);
            }
        }

        function validateFormFields() {
            if (vm.from == "")
                vm.validFrom = true;

            else if (vm.message == "")
                vm.validMessage = true;

            else if (vm.title == "")
                vm.validTitle = true;

            if (!vm.validFrom && !vm.validMessage && !vm.validTitle)
                vm.formValid = false;
        }

        function success(data) {
            console.log('success: ', data);
            vm.message = undefined;
            vm.title = undefined;
            setSuccessMessage($translate.instant('contact.success.message'));
        }

        function error(error) {
            setWarningMessage("Algo salio mal: " + error.data)
        }
    }

})();