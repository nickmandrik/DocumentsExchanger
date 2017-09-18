
/*
self.header_csrf = ko.observable();
self.value_csrf = ko.observable();
*/

function getThisUrl(){
    var loc = document.location;
    return loc.pathname.substr(11);
}


function login() {
    /*var encodedPassword = md5(document.getElementById('password').value);*/
    var data = ko.toJSON({
        username: document.getElementById('username').value,
        password: document.getElementById('password').value
    });

    $(".alert").remove();
    document.getElementById('username').value = "";
    document.getElementById('password').value = "";
    document.getElementById('username').focus();

    $.ajax("/exchanger/login_user", {
        /*
        beforeSend: function(xhr) {
            xhr.setRequestHeader($('meta[name="_csrf_header"]'), $('meta[name="_csrf"]').attr('content'))
        },
        */
        data: data,
        type: "post",
        contentType: "application/json",
        success: function(data) {
            // result -> failure to login
            if(data["result"] === "failure") {
                console.log("Failure to login.");

                $('#beforeAlert').after(
                    '<div class="alert alert-danger">' + data["error"] + '</div>');
            }
            // result -> login is successful
            else if (data["result"] === "success") {
                console.log("Login is successful. Role: " + data["role"]);

                // container contains client or admin part of app
                var userContentContainer = $('#userContentContainer');
                // loading client page
                var targetUrl = "";
                if (data["role"] === "client") {
                    targetUrl = "/exchanger/client";
                    history.pushState({uri: targetUrl}, "Documents exchanger", targetUrl);


                    jQuery.getScript("../assets/libs/sockjs/sockjs-0.3.4.js")
                        .done(function (script, textStatus) {
                            jQuery.getScript("/pages/util/js/client.js");
                            jQuery.getScript("../assets/libs/filesaver/FileSaver.min.js");
                        });
                    jQuery.getScript("../assets/libs/stomp/stomp.js");


                    // load client content
                    userContentContainer.load("/exchanger/client #userContentContainer > *");

                    history.replaceState({uri: targetUrl}, null, targetUrl);
                    document.title = "Documents exchanger";
                }

                // loading admin page
                if (data["role"] === "admin") {
                    targetUrl = "/exchanger/admin";
                    history.replaceState({uri: targetUrl}, null, targetUrl);
                    document.title = "Admin console for exchanger";

                    if (!$("link[href='/pages/util/css/folders.css']").length)
                        $('<link href="/pages/util/css/folders.css" rel="stylesheet">').appendTo("head");
                    userContentContainer.load("/exchanger/admin #userContentContainer > *", function() {
                        jQuery.getScript("../assets/libs/bootstrap/js/bootstrap-filestyle.min.js");
                        jQuery.getScript("../assets/libs/filesaver/FileSaver.min.js");
                        jQuery.getScript("/pages/util/js/admin.js");
                    });

                }
            }
        },
        error: function(data) {
            var dataJSON = data.responseJSON;
            console.log(data.responseJSON);
            alert(dataJSON["result"]);
        }
    });
    return false;
}


