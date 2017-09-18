$(function() {
    var operation = window.location.hash.substr(1);

    var adminViewModel = new AdminViewModel();
    ko.applyBindings(adminViewModel);

    adminViewModel.setUsername();
    if(operation === "") {
        adminViewModel.doFirstStateInHistory();
    } else {
        adminViewModel.history(operation);
    }
});

function AdminViewModel() {

    var self = this;
    var uri = "/exchanger/admin";

    self.username = ko.observable();

    self.folders = ['Documents', 'Users'];
    self.chosenFolderId = ko.observable();

    self.documentsData = ko.observableArray();
    self.chosenDocumentData = ko.observable();

    self.usersData = ko.observable();
    self.chosenUserData = ko.observable();

    // for routes between different contents
    self.operations = ['Documents', 'Users', 'ChosenDocument', 'ChosenUser', 'UploadDoc',
        'EditDoc', 'AddUser', 'EditUser', 'AssignDocuments'];
    self.chosenOperation = ko.observable();
    self.operaionUrls = [
        '/exchanger/info/username',
        '/exchanger/user/list',
        '/exchanger/documents/list',
        '/exchanger/user/create',
        '/exchanger/user/update',
        '/exchanger/user/delete',
        '/exchanger/documents/upload',
        '/exchanger/documents/delete',
        '/exchanger/documents/edit',
        '/exchanger/documents/download'
    ];
    self.currentUrl = ko.observable();


    // Get username

    self.setUsername = function () {
        $.get("/exchanger/info/username", function( data ) {
            self.username(data["message"]);
            console.log("User login as " + self.username());
        });
    };


    // History

    window.onpopstate = function() {
        self.history(history.state.uri);
    };

    self.history = function(operation) {
        if(operation === 'Documents') {
            self.chosenFolderId('Documents');
            self.documentsData(history.state.documents);
        } else if(operation === 'Users') {
            self.chosenFolderId('Users');
            self.usersData(history.state.users);
        } else if(operation === 'ChosenUser') {
            self.chosenFolderId('Users');
            self.chosenUserData(history.state.user);
        } else if(operation === 'ChosenDocument') {
            self.chosenFolderId('Documents');
            self.chosenDocumentData(history.state.document);
        } else if(operation === 'UploadDoc') {
            self.chosenFolderId('Documents');
        } else if(operation === 'EditDoc') {
            self.chosenFolderId('Documents');
            self.chosenDocumentData(history.state.document);
            var docName = history.state.document.name;
            $("#editDocName").attr("value", docName.substr(0, docName.indexOf('.')));
            if(~docName.indexOf(".")) {
                $("#editDocExtension").attr("value", docName.substr(docName.indexOf('.') + 1));
            }
            $("#editDocPath").attr("value", history.state.document.path);
        } else if(operation === 'AddUser') {
            self.chosenFolderId('Users');
        } else if(operation === 'EditUser'){
            self.chosenUserData(history.state.user);
            self.chosenFolderId('Users');
            $("#editUserName").attr("value", self.chosenUserData().login);
            $("#editUserPassword").attr("value", self.chosenUserData().password);
            if(self.chosenUserData().role === "ADMIN") {
                $("#editUserRole").attr("value", "Admin");
            } else if(self.chosenUserData().role === "USER") {
                $("#editUserRole").attr("value", "Client");
            }
        }
        self.chosenOperation(operation);
    };








    // Routing

    self.doFirstStateInHistory = function() {
        self.chosenFolderId('Documents');
        self.chosenOperation('Documents');
        $.get('/exchanger/documents/list', function (data) {
            self.documentsData(data);
            history.replaceState({uri: self.chosenOperation(), documents: data},
                null, uri);
        });
    };

    self.goToFolder = function (folder) {
        self.chosenFolderId(folder);

        if (folder === 'Users') {
            self.chosenOperation('Users');
            $.get('/exchanger/user/list', function (data) {
                self.usersData(data);

                history.pushState({uri: self.chosenOperation(), users: data},
                    null,  uri.concat('#' + self.chosenOperation()));
            });
        } else if (folder === 'Documents') {
            self.chosenOperation('Documents');
            $.get('/exchanger/documents/list', function (data) {
                self.documentsData(data);

                history.pushState({uri: self.chosenOperation(), documents: data},
                    null, uri.concat('#' + self.chosenOperation()));
            });
        }
    };

    self.goToUser = function (user) {
        self.chosenFolderId('Users');
        self.chosenOperation('ChosenUser');

        self.chosenUserData(user);

        history.pushState({uri: self.chosenOperation(), user: user}, null,
            uri.concat('#' + self.chosenOperation()));
        /*$.get("/exchanger/user/get", { login: user.login }, self.chosenUserData);*/
    };

    self.goToDocument = function (document) {
        self.chosenFolderId('Documents');
        self.chosenOperation('ChosenDocument');
        self.chosenDocumentData(document);

        history.pushState({uri: self.chosenOperation(), document: document}, null,
            uri.concat('#' + self.chosenOperation()));
    };


    self.choseUploadDoc = function () {
        self.chosenOperation('UploadDoc');

        history.pushState({uri: self.chosenOperation()}, null, uri.concat('#' + self.chosenOperation()));
    };


    self.choseEditDoc = function () {
        var docName = self.chosenDocumentData()["name"];
        $("#editDocName").attr("value", docName.substr(0, docName.indexOf('.')));
        if(~docName.indexOf(".")) {
            $("#editDocExtension").attr("value", docName.substr(docName.indexOf('.') + 1));
        }
        $("#editDocPath").attr("value", self.chosenDocumentData()["path"]);
        self.chosenOperation('EditDoc');

        history.pushState({uri: self.chosenOperation(), document: self.chosenDocumentData()}, null,
            uri.concat('#' + self.chosenOperation()));
    };

    self.choseAddUser = function () {
        self.chosenOperation('AddUser');

        history.pushState({uri: self.chosenOperation(), user: self.chosenUserData()}, null,
            uri.concat('#' + self.chosenOperation()));
    };

    self.choseEditUser = function () {
        $("#editUserName").attr("value", self.chosenUserData()["login"]);
        $("#editUserPassword").attr("value", self.chosenUserData()["password"]);
        if(self.chosenUserData()["role"] === "ADMIN") {
            $("#editUserRole").attr("value", "Admin");
        } else if(self.chosenUserData()["role"] === "USER") {
            $("#editUserRole").attr("value", "Client");
        }
        self.chosenOperation('EditUser');

        history.pushState({uri: self.chosenOperation(), user: self.chosenUserData()}, null,
            uri.concat('#' + self.chosenOperation()));
    };


    self.choseAssignDocumentsToUser = function() {

        $.get('/exchanger/documents/list', function(data) {
            self.documentsData(data);
            $.ajax("/exchanger/user/docs", {
                data: ko.toJSON({message: self.chosenUserData()["login"]}),
                type: 'post',
                contentType: "application/json",
                success: function (documentArray) {
                    var assignDocumentsToUserTable = document.getElementById('assignDocumentsToUserTable');
                    documentArray.forEach(function(document, i, documentArray) {
                        var index = arrayFirstIndexOf(self.documentsData(), function(item) {
                            return item.id === document.id;
                        });
                        if(index >= 0) {
                            assignDocumentsToUserTable.rows[index+1].cells[0].innerHTML =
                                "<span class='glyphicon glyphicon-ok'></span>";
                        }
                    });
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    alert(textStatus + ': ' + errorThrown);
                }
            });
        });
        self.chosenOperation('AssignDocuments');
    };


    function arrayFirstIndexOf(array, predicate, predicateOwner) {
        for (var i = 0, j = array.length; i < j; i++) {
            if (predicate.call(predicateOwner, array[i])) {
                return i;
            }
        }
        return -1;
    }





    // Operations with users


    // Create User

    self.addUser = function() {
        if(self.chosenOperation() === "AddUser" &&
            (document.getElementById('addUserName').value === "" ||
                document.getElementById('addUserPassword').value === "")) {
            alert("Please fill all inputs")
        } else {
            var role;
            if (document.getElementById('addUserRole').value === "Client") {
                role = "USER";
            } else {
                role = "ADMIN";
            }
            var data = ko.toJSON({
                login: document.getElementById('addUserName').value,
                password: document.getElementById('addUserPassword').value,
                role: role
            });

            $.ajax("/exchanger/user/create", {
                data: data,
                type: 'post',
                contentType: "application/json",
                success: function (data) {
                    document.getElementById('addUserName').value = "";
                    document.getElementById('addUserPassword').value = "";
                    self.goToFolder('Users');
                    function timeoutFunction() {
                        alert(data["message"]);
                    }
                    setTimeout(timeoutFunction,500)
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    alert(textStatus + ': ' + errorThrown);
                }
            });
        }
    };


    // Update User

    self.editUser = function() {
        if (self.chosenOperation() === "EditUser" &&
            (document.getElementById('editUserName').value === "" ||
                document.getElementById('editUserPassword').value === "")) {
            alert("Please fill all inputs")
        } else {
            var role;
            if (document.getElementById('editUserRole').value === "Client") {
                role = "USER";
            } else {
                role = "ADMIN";
            }
            var data = ko.toJSON({
                login: document.getElementById('editUserName').value,
                password: document.getElementById('editUserPassword').value,
                role: role
            });
            $.ajax("/exchanger/user/update", {
                data: data,
                type: 'post',
                contentType: "application/json",
                success: function (data) {
                    self.goToFolder('Users');
                    function timeoutFunction() {
                        alert(data["message"]);
                    }
                    setTimeout(timeoutFunction,500)
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    alert(textStatus + ': ' + errorThrown);
                }
            });
        }
    };


    // Delete User

    self.deleteUser = function () {
        var answer = confirm("Are you sure you want to delete this user?");
        if (answer) {
            $.ajax("/exchanger/user/delete", {
                data: ko.toJSON({
                    message: self.chosenUserData()["login"]
                }),
                type: 'post',
                contentType: "application/json",
                success: function (data) {
                    self.goToFolder('Users');
                    function timeoutFunction() {
                        alert(data["message"]);
                    }
                    setTimeout(timeoutFunction,500)
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    alert(textStatus + ': ' + errorThrown);
                }
            });
        }
    };


    // Find User

    self.findUsers = function () {
        $.ajax("/exchanger/user/find", {
            data: ko.toJSON({
                message: document.getElementById('findUserInput').value
            }),
            type: 'post',
            contentType: "application/json",
            beforeSend: function () {
                document.getElementById('findUserInput').value = "";
            },
            success: self.usersData,
            error: function (jqXHR, textStatus, errorThrown) {
                alert(textStatus + ': ' + errorThrown);
            }
        });
    };


    // Change Assignments to User

    self.changeAssignment = function(documentInfo) {
        var assignDocumentsToUserTable = document.getElementById('assignDocumentsToUserTable');
        $.ajax("/exchanger/user/assign", {
            data: ko.toJSON({
                user: self.chosenUserData(),
                document: documentInfo
            }),
            type: 'post',
            contentType: "application/json",
            success: function (data) {
                if(data.message[0] === '0') {
                    var index = arrayFirstIndexOf(self.documentsData(), function(item) {
                        return item.id === documentInfo.id;
                    });
                    if(data.message[2] === '0') {
                        assignDocumentsToUserTable.rows[index+1].cells[0].innerHTML =
                            "<span class='glyphicon glyphicon-remove'></span>"
                    } else {
                        assignDocumentsToUserTable.rows[index+1].cells[0].innerHTML =
                            "<span class='glyphicon glyphicon-ok'></span>"
                    }
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert(textStatus + ': ' + errorThrown);
            }
        });

    };




    // Operations with Documents

    // Create Document

    self.uploadDocument = function () {
        var file = $('#inputDocument');
        if (!file.val()) {
            event.preventDefault();
            alert("Please choose a document!");
        } else {

            /*            file = file.get(0).files[0];*/
            var formData = new FormData();
            formData.append('file', file.get(0).files[0]);
            formData.append('path', $("#pathDocUpload").val());
            formData.append('fileName', file.get(0).files[0].name);
            $.ajax({
                url: '/exchanger/documents/upload',
                data: formData,
                type: 'POST',
                contentType: false,
                processData: false,
                beforeSend: function () {
                    document.getElementById("formUploadDocument").reset();
                    document.getElementById('pathDocUpload').value = "";
                },
                success: function (data) {
                    self.goToFolder('Documents');
                    function timeoutFunction() {
                        alert(data["message"]);
                    }
                    setTimeout(timeoutFunction,500)
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    alert(textStatus + ': ' + errorThrown);
                }
            });
        }
    };


    // Download Document

    self.downloadDocument = function () {
        /*var link = document.createElement("a");
        link.setAttribute( "href", "/exchanger/documents/download");
        link.setAttribute( "download", self.chosenDocumentData()["name"] );
        document.body.appendChild(link);
        link.click();*/

        $.ajax("/exchanger/documents/download", {
            data: ko.toJSON(self.chosenDocumentData()),
            type: 'post',
            contentType: "application/json",
            success: function (data) {
                var byteCharacters = atob(data);
                var byteNumbers = new Array(byteCharacters.length);
                for (var i = 0; i < byteCharacters.length; i++) {
                    byteNumbers[i] = byteCharacters.charCodeAt(i);
                }
                var byteArray = new Uint8Array(byteNumbers);
                var blob = new Blob([byteArray]);
                var fileName = self.chosenDocumentData()["name"];
                saveAs(blob, fileName);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert(textStatus + ': ' + errorThrown);
            }
        });
    };


    // Edit Document

    self.editDocument = function () {

        var docName = document.getElementById('editDocName').value;

        var editDocExtension = document.getElementById('editDocExtension').value;
        if(editDocExtension !== "" && editDocExtension.charAt(0) !== ".") {
            docName += "." + editDocExtension;
        } else {
            docName += editDocExtension;
        }
        var data = ko.toJSON({
            id: self.chosenDocumentData()["id"],
            name: docName,
            path: document.getElementById('editDocPath').value
        });

        $.ajax("/exchanger/documents/edit", {
            data: data,
            type: 'post',
            contentType: "application/json",
            success: function (data) {
                self.goToFolder('Documents');
                function timeoutFunction() {
                    alert(data["message"]);
                }
                setTimeout(timeoutFunction,500)
            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert(textStatus + ': ' + errorThrown);
            }
        });
    };



    // Delete Document

    self.deleteDocument = function () {
        var answer = confirm("Are you sure you want to delete this document?");
        if (answer) {
            $.ajax("/exchanger/documents/delete", {
                data: ko.toJSON(self.chosenDocumentData()),
                type: 'post',
                contentType: "application/json",
                success: function (data) {
                    goToFolder('Documents');
                    function timeoutFunction() {
                        alert(data["message"]);
                    }
                    setTimeout(timeoutFunction,500)
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    alert(textStatus + ': ' + errorThrown);
                }
            });
        }
    };


    // Find Document

    self.findDocuments = function () {
        $.ajax("/exchanger/documents/find", {
            data: ko.toJSON({
                message: document.getElementById('findDocumentInput').value
            }),
            type: 'post',
            contentType: "application/json",
            beforeSend: function () {
                document.getElementById('findDocumentInput').value = "";
            },
            success: self.documentsData,
            error: function (jqXHR, textStatus, errorThrown) {
                alert(textStatus + ': ' + errorThrown);
            }
        });
    };
}