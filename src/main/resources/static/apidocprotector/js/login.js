window.onload = function() {
    //let apidoc_protector_target = document.getElementById('hidden-apidoc-protector-type').value;
    /*if (window.location.href.search(apidoc_protector_target) !== -1) {
        document.getElementById('error-login').innerHTML = 'Login Failed';
    }*/

    (function(window) {
        'use strict';

        let x_back_denied = {

            //globals
            version: '0.0.1',
            history_api: typeof history.pushState !== 'undefined',

            init: function() {
                window.location.hash = '#x-to-lock';
                x_back_denied.configure();
                if (window.location.href.search("x-expired-session") !== -1) {
                    document.getElementById('error-login').innerHTML = 'Expired Session, please login again';
                }
            },

            configure: function() {
                if (window.location.hash === '#x-to-lock') {
                    if (this.history_api) {
                        history.pushState(null, '', '#x-padlock');
                    } else {
                        window.location.hash = '#x-padlock';
                    }
                }
                x_back_denied.checkCompat();
                x_back_denied.hasChanged();
            },

            checkCompat: function() {
                if (window.addEventListener) {
                    window.addEventListener("hashchange", x_back_denied.hasChanged, false);
                } else if (window.attachEvent) {
                    window.attachEvent("onhashchange", x_back_denied.hasChanged);
                } else {
                    window.onhashchange = x_back_denied.hasChanged;
                }
            },

            hasChanged: function() {
                if (window.location.hash === '#x-to-lock') {
                    window.location.hash = '#x-padlock';
                    alert("Operation not allowed");
                }
            }
        };

        x_back_denied.init();

    }(window));
}