    var FCM = require('fcm-node');
    var serverKey = 'AAAALWe_bTo:APA91bHzwpdBtswfdrkov_6_OCHddTgFubCkfKEwg5P51En4yvpWio8eToTHXb0spI-SGv1VSs53O6qteEPZ1Gxg6mUuqFii0uetSbrxgDKlPD8ekNjiJjlbNxF39EdtxIFCVU_X2DQv'; //put your server key here 
    var fcm = new FCM(serverKey);
 
    var message = { //this may vary according to the message type (single recipient, multicast, topic, et cetera) 
        registration_ids: ['cOFI4UGjhQc:APA91bFAoZjVjA7wQydoah2LwSj1vj2KgdusL0s89TlZsIxbebCbiRGHPkK-fP5OBSTdUcwnvmeAuL-x4DUTSgBl90TUjIy7-nub6n-tCA6tjNBNlon5bTxT9Urpbcxu-cjmqctqR50A','fj9N5DbiLRc:APA91bFJDQTMljUyEjIt5nFcmYo4dhiQZjneyOkJrCOwSSVBv_3LRtrX0QC3coAFA-BvYX5qgBR2oOPL5jGju5ussjmiVKOskwNL1wTBtzpEYH2AfBuyouChGcb4rWh90v9qIybTMNZe'], 
        
        
        notification: {
            title: 'MIAServer', 
            body: 'Hola Lucy soy Firebase' 
        },
        
        data: {  //you can send only notification or only data(or include both) 
            my_key: 'my value',
            my_another_key: 'my another value'
        }
    };
    
    fcm.send(message, function(err, response){
        if (err) {
            console.log("Something has gone wrong!"+err);
        } else {
            console.log("Successfully sent with response: ", response);
        }
    });