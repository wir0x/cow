/**
 * notificationFx.js v1.0.0
 * http://www.codrops.com
 *
 * Licensed under the MIT license.
 * http://www.opensource.org/licenses/mit-license.php
 *
 * Copyright 2014, Codrops
 * http://www.codrops.com
 */
;
(function (window) {

    'use strict';

    var docElem = window.document.documentElement,
        support = {animations: Modernizr.cssanimations},
        animEndEventNames = {
            'WebkitAnimation': 'webkitAnimationEnd',
            'OAnimation': 'oAnimationEnd',
            'msAnimation': 'MSAnimationEnd',
            'animation': 'animationend'
        },
    // animation end event name
        animEndEventName = animEndEventNames[Modernizr.prefixed('animation')];

    /**
     * extend obj function
     */
    function extend(a, b) {
        for (var key in b) {
            if (b.hasOwnProperty(key)) {
                a[key] = b[key];
            }
        }
        return a;
    }

    /**
     * NotificationFx function
     */
    function NotificationFx(options) {
        this.options = extend({}, this.options);
        extend(this.options, options);
        this._init();
    }

    /**
     * NotificationFx options
     */
    NotificationFx.prototype.options = {
        // element to which the notification will be appended
        // defaults to the document.body
        //wrapper: document.body,
        wrapper: document.body,
        // the message
        message: 'yo!',
        // layout type: growl|attached|bar|other
        layout: 'growl',
        // effects for the specified layout:
        // for growl layout: scale|slide|genie|jelly
        // for attached layout: flip|bouncyflip
        // for other layout: boxspinner|cornerexpand|loadingcircle|thumbslider
        // ...
        effect: 'slide',
        // notice, warning, error, success
        // will add class ns-type-warning, ns-type-error or ns-type-success
        type: 'error',
        // if the user doesn´t close the notification then we remove it
        // after the following time
        ttl: 4000,
        // callbacks
        isloggedIn: false,
        onClose: function () {
            return false;
        },
        onOpen: function () {
            return false;
        }
    }

    /**
     * init function
     * initialize and cache some vars
     */
    NotificationFx.prototype._init = function () {
        // create HTML structure
        this.ntf = document.createElement('div');
        this.ntf.className = 'ns-box ns-' + this.options.layout + ' ns-effect-' + this.options.effect + ' ns-type-' + this.options.type;
        var strinner = '<div class="ns-box-inner">';
        strinner += this.options.message;
        strinner += '</div>';
        strinner += '<span class="ns-close"></span></div>';
        this.ntf.innerHTML = strinner;

        var islogged = this.options.isloggedIn;

        //var is_chrome = navigator.userAgent.toLowerCase().indexOf('chrome') > -1;
        //var is_explorer = navigator.userAgent.indexOf('MSIE') > -1;
        //var is_firefox = navigator.userAgent.indexOf('Firefox') > -1;
        //var is_safari = navigator.userAgent.indexOf("Safari") > -1;
        //var is_opera = navigator.userAgent.toLowerCase().indexOf("op") > -1;
        //if ((is_chrome)&&(is_safari)) {is_safari=false;}
        //if ((is_chrome)&&(is_opera)) {is_chrome=false;}

        //if ( is_chrome || is_safari || is_opera) {

        var element = this.ntf;
        var navBarHeight = $(".navbar").height();
        if (document.body.scrollTop < 50){
            console.log("scroll less than:");
            element.setAttribute("style", "margin-top:"+(navBarHeight-document.body.scrollTop)+"px");
            setTimeout(function() { checkIfLoggedOut(); }, 100);
        }


        $(window).scroll(function (event) {
            var scroll = $(window).scrollTop();
            if (scroll <= navBarHeight)element.setAttribute("style", "margin-top:"+(navBarHeight-scroll)+"px");
            else element.setAttribute("style", "margin-top:0px");
        });


        // append to body or the element specified in options.wrapper
        this.options.wrapper.insertBefore(this.ntf, this.options.wrapper.firstChild);

        // dismiss after [options.ttl]ms
        var self = this;
        this.dismissttl = setTimeout(function () {
            if (self.active) {
                self.dismiss();
            }
        }, this.options.ttl);

        // init events
        this._initEvents();
    }

    var checkIfLoggedOut = function () {
        if (!$(".navbar").is(":visible")){
            console.log("checkIfLoggedOut: navbar not showing");
            //this.ntf.setAttribute("style", "margin-top:0px !important");
            $(".ns-box").css("margin-top","0px");
        }
    }

    /**
     * init events
     */
    NotificationFx.prototype._initEvents = function () {
        var self = this;
        // dismiss notification
        this.ntf.querySelector('.ns-close').addEventListener('click', function () {
            self.dismiss();
        });
    }

    /**
     * show the notification
     */
    NotificationFx.prototype.show = function () {
        this.active = true;
        classie.remove(this.ntf, 'ns-hide');
        classie.add(this.ntf, 'ns-show');
        this.options.onOpen();
    }

    /**
     * dismiss the notification
     */
    NotificationFx.prototype.dismiss = function () {
        var self = this;
        this.active = false;
        clearTimeout(this.dismissttl);
        classie.remove(this.ntf, 'ns-show');
        setTimeout(function () {
            classie.add(self.ntf, 'ns-hide');

            // callback
            self.options.onClose();
        }, 25);

        // after animation ends remove ntf from the DOM
        var onEndAnimationFn = function (ev) {
            if (support.animations) {
                if (ev.target !== self.ntf) return false;
                this.removeEventListener(animEndEventName, onEndAnimationFn);
            }
            self.options.wrapper.removeChild(this);
        };

        if (support.animations) {
            this.ntf.addEventListener(animEndEventName, onEndAnimationFn);
        }
        else {
            onEndAnimationFn();
        }
    }

    /**
     * add to global namespace
     */
    window.NotificationFx = NotificationFx;

})(window);
