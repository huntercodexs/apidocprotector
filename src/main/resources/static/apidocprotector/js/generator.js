window.onload = function() {
    (function(window) {
        'use strict';

        let x_back_denied = {

            //globals
            version: '0.0.1',
            history_api: typeof history.pushState !== 'undefined',

            init: function() {
                window.location.hash = '#x-to-lock';
                x_back_denied.configure();
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
                    window.addEventListener('hashchange', x_back_denied.hasChanged, false);
                } else if (window.attachEvent) {
                    window.attachEvent('onhashchange', x_back_denied.hasChanged);
                } else {
                    window.onhashchange = x_back_denied.hasChanged;
                }
            },

            hasChanged: function() {
                if (window.location.hash === '#x-to-lock') {
                    window.location.hash = '#x-padlock';
                    alert('Operation not allowed');
                }
            }
        };

        x_back_denied.init();

    }(window));

}

//let bt = document.getElementById('bt-signup');
//bt.addEventListener('click', function(e){
//    e.preventDefault();
//    e.stopPropagation();
//    alert('TEST');
//    return false;
//});
