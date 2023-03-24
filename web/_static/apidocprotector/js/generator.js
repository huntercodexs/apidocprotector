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

let btsave = document.getElementById('bt-save');

btsave.addEventListener('click', function(e) {
    e.preventDefault();
    e.stopPropagation();

    let form = document.getElementById('form-generator');
    let name = document.getElementById('name').value;
    let username = document.getElementById('username').value;
    let email = document.getElementById('email').value;
    let password = document.getElementById('password').value;
    let role = document.getElementById('role').value;

    let check = /^[\w\.-]+@([\w\-]+\.)+[a-zA-Z]{2,4}$/;

    if (email.search(check) == -1) {
        alert("The mail is not valid");
        return false;
    }

    if (name != "" && username != "" && email != "" && password != "" && role != "") {
        form.submit();
    } else {
        alert("Fill in all fields please.")
        return false;
    }

});

let message = document.getElementById('post-message');

if (message.innerHTML != "") {
    setTimeout(function() {
        console.log("cleanning text...");
        message.innerHTML = "";
    }, 5000);
}