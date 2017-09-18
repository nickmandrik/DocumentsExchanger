$(function() {

    var socket = new SockJS('/exchanger/socket');
    var stompClient = Stomp.over(socket);
    stompClient.debug = null;
    var appModel = new AssignmentModel(stompClient);
    ko.applyBindings(appModel);
    appModel.connect();
    appModel.pushNotification("Dashboard Count results take a 2-3 second simulated delay. Notifications will appear.");

    /*var thisUri = getThisUrl()?getThisUrl():'/exchanger/client';
    history.replaceState({uri:thisUri}, "Documents exchanger", thisUri);*/
});


function AssignmentModel(stompClient) {
    var self = this;

    self.username = ko.observable();
    self.documents = ko.observable(new DocumentQueueModel());
    self.notifications = ko.observableArray();
    self.chosenDocument = ko.observable();


    self.connect = function(){
        stompClient.connect({}, function(frame) {

            console.log('Connected ' + frame);
            self.username(frame.headers['user-name']);

            stompClient.subscribe("/exchanger/doclist", function(message) {
                self.documents().loadDocumentsDetails(JSON.parse(message.body));
            });

        }, function(error) {
            console.log("STOMP protocol error " + error);
        });
    };


    self.refreshDocuments = function() {
        stompClient.subscribe("/exchanger/doclist", function(message) {
            self.documents().refreshData(JSON.parse(message.body));
        });
    };

    self.pushNotification = function(text) {
        self.notifications.push({notification: text});
        if (self.notifications().length > 5) {
            self.notifications.shift();
        }
    };

    self.downloadDocument = function (document) {
        stompClient.subscribe("/exchanger/download/" + document["id"], function(message) {
            /*function timeoutFunction() {
                alert("Download is begin. Please wait...");
            }
            setTimeout(timeoutFunction,4);*/
            var byteCharacters = atob(JSON.parse(message.body)["payload"]);
            /*var byteCharacters = JSON.parse(message.body)["payload"];*/
            var byteNumbers = new Array(byteCharacters.length);
            for (var i = 0; i < byteCharacters.length; i++) {
                byteNumbers[i] = byteCharacters.charCodeAt(i);
            }
            var byteArray = new Uint8Array(byteNumbers);
            var blob = new Blob([byteArray]);
            var fileName = document["name"];
            saveAs(blob, fileName);
        });
    }

}

//Counter feed model and its properties
function DocumentQueueModel() {
    var self = this;

    self.rows = ko.observableArray();

    self.total = ko.computed(function() {
        var result = 0;
        for ( var i = 0; i < self.rows().length; i++) {
            result ++;
        }
        return result;
    });
    var rowLookup = {};

    self.loadDocumentsDetails = function(data) {
        for ( var i = 0; i < data.length; i++) {
            var row = new DocumentRow(data[i]);
            self.rows.push(row);
            rowLookup[row.id] = row;
        }
    };

    self.refreshData = function(data) {
        if (rowLookup[data.id]) {
            rowLookup[data.id].refresh(data);
        }else{
            var row = new DocumentRow(data);
            self.rows.push(row);
            rowLookup[row.id] = row;
        }
    };
}

//Document Row Details
function DocumentRow(data) {
    var self = this;

    self.id = data.id;
    self.path = data.path;
    self.name = data.name;

    self.refresh = function(newData){
        self.id = newData.id;
        self.path = newData.path;
        self.name = newData.name;
    };
}