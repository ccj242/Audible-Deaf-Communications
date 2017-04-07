cordova.define("com.matd.SpeechRecPlug.SpeechRecPlug", function(require, exports, module) { var exec = require('cordova/exec');

function SpeechRecPlug() { 
 console.log("SpeechRecPlug.js: is created");
}

SpeechRecPlug.prototype.init = function(){
 console.log("SpeechRecPlug.js: showToast");

 exec(function(result){
     /*alert("OK" + reply);*/
   },
  function(result){
    /*alert("Error" + reply);*/
   },"SpeechRecPlug","init",[]);
}

SpeechRecPlug.prototype.startRecognition = function(callback){
	 console.log("SpeechRecPlug.js: showToast");

	 exec(function(result){
	     //alert(result);
		 callback(result);
		 //return result;
	   },
	  function(result){
	    /*alert("Error" + reply);*/
	   },"SpeechRecPlug","play",[]);
	}


SpeechRecPlug.prototype.stopRecognition = function(){
	 console.log("SpeechRecPlug.js: showToast");

	 exec(function(result){
	     /*alert("OK" + reply);*/
	   },
	  function(result){
	    /*alert("Error" + reply);*/
	   },"SpeechRecPlug","stop",[]);
	}

SpeechRecPlug.prototype.setSpeechSilenceGap = function(successCallback,errorCallback, speechSilenceGap) {
        cordova.exec(
        		 successCallback, // success callback function
                 errorCallback, // error callback function
                 'SpeechRecPlug', // mapped to our native Java class called "Calendar" 
            'speechSilenceGap', // with this action name
            [{                  // and this array of custom arguments to create our entry
                "speechSilenceGap": speechSilenceGap
            }]
        ); 
     }

SpeechRecPlug.prototype.setLanguage = function(successCallback,errorCallback, languageSpeak) {
    cordova.exec(
    		 successCallback, // success callback function
             errorCallback, // error callback function
             'SpeechRecPlug', // mapped to our native Java class called "Calendar" 
        'languageSpeak', // with this action name
        [{                  // and this array of custom arguments to create our entry
            "languageSpeak": languageSpeak
        }]
    ); 
 }

SpeechRecPlug.prototype.test = function(){
alert("started");
}


 var SpeechRecPlug = new SpeechRecPlug();
 module.exports = SpeechRecPlug;

});
