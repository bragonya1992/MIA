var oneSignal = require('onesignal')('ZDA1ZTU4NDUtNjFjZC00ZTFhLWJiMGEtMzdlZGYyNjlmNjkz', '606aa01a-676b-4a6c-89da-37da13078997', true);

var arra =[];
arra.push("8a4b00f3-c1ca-4d92-82a8-b0f47a0081a5");
console.log(arra);
    var data = { //this may vary according to the message type (single recipient, multicast, topic, et cetera) 
        customkey: "hola brayan" 
        
    };
oneSignal.createNotification("hola",data, arra)