angular.module('starter.controllers', [])

.controller('MainCtrl', function($scope, $ionicPlatform, $localStorage, $http, $timeout,$ionicSideMenuDelegate,$window,$ionicModal,$cordovaAppRate,$cordovaSocialSharing) {

  ionic.Platform.ready(function(){
  /**/
  //AppRate.preferences.storeAppURL.ios = '1151606636';
  AppRate.preferences.storeAppURL.android = 'market://details?id=com.jarvisfilms.audible';
  AppRate.preferences.usesUntilPrompt = 5;

    $cordovaAppRate.promptForRating(false).then(function (result) {
        // success
    });
    
});

$scope.share=function(){
$timeout(function(){
  $cordovaSocialSharing
    .share("a free app that transcribes, speaks and translates live speech", "Audible: A Free App for the Deaf", "http://www.jarvisfilms.com/img/Audible.png", "http://www.jarvisfilms.com") // Share via native share sheet
    .then(function(result) {
      // Success!
    }, function(err) {
      // An error occured. Show a message to the user
    });


},300)
}



$ionicPlatform.registerBackButtonAction(function (event) {
}, 100);


if ($localStorage.options){
	$scope.options=$localStorage.options;
}else{
$scope.options={};
$scope.options.rate=".9";
$scope.options.lang="en-US";
$scope.options.trans="en-US";
$localStorage.options=$scope.options;
}

$ionicModal.fromTemplateUrl('my-modal.html', {
    scope: $scope,
    animation: 'slide-in-up'
  }).then(function(modal) {
    $scope.modal = modal;
    
  });
	 	 $scope.openModal = function() {
	  			$scope.modal.show();
		  };
		  $scope.closeModal = function() {
		    	$scope.modal.hide();
		  };
		  // Cleanup the modal when we're done with it!
		  $scope.$on('$destroy', function() {
		   		$scope.modal.remove();
  });


$ionicPlatform.on('pause', function(){
$scope.stopRecognition();
})

$ionicPlatform.on('resume', function(){
$scope.record=false;
$scope.speaking=false;
});

window.addEventListener('keypress', function (e) {
if(!$scope.input){$scope.input=''}
if ($scope.input=="No Text to Speak!"){$scope.input=''};

if (e.keyCode==13){   //return=enter=13
//$scope.speaking=true;
if (!$scope.input.length){return}
$scope.$apply(function(){
	$scope.speaking=true;
})
$scope.speak();
return;
}

if (e.keyCode==8 || e.keyCode==92){   //backspace=8
if (!$scope.input.length){return}
	$scope.$apply(function(){
$scope.input=$scope.input.substring(0, $scope.input.length - 1);
	})
return;	
}

$scope.$apply(function(){
	 $scope.input=$scope.input+String.fromCharCode(e.keyCode);
})

}, false);


$scope.record=false;
$scope.speaking=false;
$scope.output=false;
$scope.keyboardout=false;

//---------------------------------------------------------
$scope.setlanguage=function(){
	$scope.clearoutput();

if ($scope.options.lang=='en-US'){
$scope.langtoset=$scope.options.lang;
}else{
$scope.langtoset=$scope.options.lang.substring(0, 2);
}
SpeechRecPlug.setLanguage(function(){},function(){},$scope.langtoset);
}

$scope.optionschange=function(){

	if ($scope.record){
	$scope.stopRecognition()		
			};
$ionicSideMenuDelegate.toggleLeft();


if (!$localStorage.options){

	// prompt for pwat
//-----------------------------
$scope.options.rate=".9";
$scope.options.lang="en-US";
$scope.options.trans="en-US";
$localStorage.options=$scope.options;
}else{

$localStorage.options=$scope.options;

}
}


$scope.keyboardbutton=function(){
if ($scope.input=="No Text to Speak!"){$scope.input=''};

	if (!cordova.plugins.Keyboard.isVisible){

window.setTimeout(function () { 

document.getElementById("keyboard_input").focus();
}, 0); 

}	
}

$scope.phoneflare=function(){
  $window.open('https://play.google.com/store/apps/details?id=com.jarvisfilms.smstactics&hl=en','_blank');
}

$scope.synonymy=function(){
  $window.open('https://play.google.com/store/apps/details?id=com.jarvisfilms.smstactics&hl=en','_blank');
}

$scope.birdsupstairs=function(){
  $window.open('https://play.google.com/store/apps/details?id=com.jarvisfilms.smstactics&hl=en','_blank');
}

$scope.smstactics=function(){
  $window.open('https://play.google.com/store/apps/details?id=com.jarvisfilms.smstactics&hl=en','_blank');
}

$scope.sugarsweet=function(){
  $window.open('https://play.google.com/store/apps/details?id=com.jarvisfilms.smstactics&hl=en','_blank');
}

$scope.wordunk=function(){
  $window.open('https://play.google.com/store/apps/details?id=com.jarvisfilms.smstactics&hl=en','_blank');
}

$scope.iconic=function(){
  $window.open('https://play.google.com/store/apps/details?id=com.jarvisfilms.smstactics&hl=en','_blank');
}

$scope.tmm=function(){
  $window.open('https://play.google.com/store/apps/details?id=com.jarvisfilms.smstactics&hl=en','_blank');
}



$scope.inputfunc=function(){
//alert($scope.input)
}

$scope.clearspeak=function(){

	$scope.input='';
}

$scope.clearoutput=function(){

	var div = document.getElementById("test");
	div.innerHTML='';
	$scope.output=false;
}

$scope.interrupt=function(){


	$scope.speaking=true;
	rec=false;
			if ($scope.record){
				$scope.stopRecognition()
				rec=true;
			};

	TTS
        .speak('', function () {
$scope.$apply(function(){
            $scope.speaking=false;
			if (rec){$scope.startRecognition()};
});
        	alert('interuppt')
        }, function (error) {
            alert(error);
        });

}


$scope.speak=function(){

if (typeof $scope.input === "undefined" || !$scope.input.length){
	$scope.input="No Text to Speak!";

	return;
}

if ($scope.input=="No Text to Speak!"){return};


	if ($scope.options.lang!=="en-US"){
			if(!navigator.onLine){alert('no internet connection');return}

// tweak lang codes if necessary
$scope.source=$scope.options.lang.substring(0, 2); // autodetect by eliminating from call
$scope.resultlang=$scope.options.trans.substring(0, 2);

$http.get("https://www.googleapis.com/language/translate/v2?q="+encodeURIComponent($scope.input)+"&source="+$scope.source+"&target="+$scope.resultlang+"&key=**API KEY**")
.then(function(response){

			$scope.speaktext=$scope.input;
			$scope.locale=$scope.options.lang;
			$scope.tts(response.data.data.translations[0].translatedText,$scope.options.lang);
})

	}else{$scope.speaktext=$scope.input;
		$scope.locale='en-US';
		$scope.tts($scope.speaktext,$scope.locale);
}
}

$scope.tts=function(spktxt,loc){

	$scope.speaking=true;
	rec=false;
			if ($scope.record){
				$scope.stopRecognition()
				rec=true;
			};

			// if TTS is undefined? then popup? 
	    TTS
        .speak({
            text: spktxt,
            locale: loc, // pass loc
            rate: parseFloat($scope.options.rate)
        }, function () {
        	$scope.$apply(function(){
				
    		$scope.speaking=false;
    		$scope.input='';
			if (rec){$scope.startRecognition()};
});
        }, function (error) {
        	/*
        	$scope.speaking=false;
        	$scope.input='';
			if (rec){$scope.startRecognition()};
			*/
		//error
            alert(error); 
        });

}

$scope.stopRecognition = function(){

//$scope.clearoutput(); //

	if(typeof $scope.timeout !== "undefined"){
		$timeout.cancel($scope.timeout);
	}
		$scope.record=false;
	    SpeechRecPlug.stopRecognition();
	}

$scope.startRecognition = function(){
/*
// ------------ TEST ------------------
var div = document.getElementById("test");
body=div.innerHTML;
	$scope.output=true;
	
	div.innerHTML="T WAS the best of times, it was the worst of times, it was the age of wisdom, it was the age of foolishness, it was the epoch of belief, it was the epoch of incredulity, it was the season of Light, it was the season of Darkness, it was the spring of hope, it was the winter of despair, we had everything before us, we had nothing before us, we were all going direct to Heaven, we were all going direct the other way- in short, the period was so far like the present period, that some of its noisiest authorities insisted on its being received, for good or for evil, in the superlative degree of comparison onlyT WAS the best of times, it was the worst of times, it was the age of wisdom, it was the age of foolishness, it was the epoch of belief, it was the epoch of incredulity, it was the season of Light, it was the season of Darkness, it was the spring of hope, it was the winter of despair, we had everything before us, we had nothing before us, we were all going direct to Heaven, we were all going direct the other way- in short, the period was so far like the present period, that some of its noisiest authorities insisted on its being received, for good or for evil, in the superlative degree of comparison onlyT WAS the best of times, it was the worst of times, it was the age of wisdom, it was the age of foolishness, it was the epoch of belief, it was the epoch of incredulity, it was the season of Light, it was the season of Darkness, it was the spring of hope, it was the winter of despair, we had everything before us, we had nothing before us, we were all going direct to Heaven, we were all going direct the other way- in short, the period was so far like the present period, that some of its noisiest authorities insisted on its being received, for good or for evil, in the superlative degree of comparison onlyT WAS the best of times, it was the worst of times, it was the age of wisdom, it was the age of foolishness, it was the epoch of belief, it was the epoch of incredulity, it was the season of Light, it was the season of Darkness, it was the spring of hope, it was the winter of despair, we had everything before us, we had nothing before us, we were all going direct to Heaven, we were all going direct the other way- in short, the period was so far like the present period, that some of its noisiest authorities insisted on its being received, for good or for evil, in the superlative degree of comparison onlyT WAS the best of times, it was the worst of times, it was the age of wisdom, it was the age of foolishness, it was the epoch of belief, it was the epoch of incredulity, it was the season of Light, it was the season of Darkness, it was the spring of hope, it was the winter of despair, we had everything before us, we had nothing before us, we were all going direct to Heaven, we were all going direct the other way- in short, the period was so far like the present period, that some of its noisiest authorities insisted on its being received, for good or for evil, in the superlative degree of comparison onlyT WAS the best of times, it was the worst of times, it was the age of wisdom, it was the age of foolishness, it was the epoch of belief, it was the epoch of incredulity, it was the season of Light, it was the season of Darkness, it was the spring of hope, it was the winter of despair, we had everything before us, we had nothing before us, we were all going direct to Heaven, we were all going direct the other way- in short, the period was so far like the present period, that some of its noisiest authorities insisted on its being received, for good or for evil, in the superlative degree of comparison onlyT WAS the best of times, it was the worst of times, it was the age of wisdom, it was the age of foolishness, it was the epoch of belief, it was the epoch of incredulity, it was the season of Light, it was the season of Darkness, it was the spring of hope, it was the winter of despair, we had everything before us, we had nothing before us, we were all going direct to Heaven, we were all going direct the other way- in short, the period was so far like the present period, that some of its noisiest authorities insisted on its being received, for good or for evil, in the superlative degree of comparison onlyT WAS the best of times, it was the worst of times, it was the age of wisdom, it was the age of foolishness, it was the epoch of belief, it was the epoch of incredulity, it was the season of Light, it was the season of Darkness, it was the spring of hope, it was the winter of despair, we had everything before us, we had nothing before us, we were all going direct to Heaven, we were all going direct the other way- in short, the period was so far like the present period, that some of its noisiest authorities insisted on its being received, for good or for evil, in the superlative degree of comparison onlyT WAS the best of times, it was the worst of times, it was the age of wisdom, it was the age of foolishness, it was the epoch of belief, it was the epoch of incredulity, it was the season of Light, it was the season of Darkness, it was the spring of hope, it was the winter of despair, we had everything before us, we had nothing before us, we were all going direct to Heaven, we were all going direct the other way- in short, the period was so far like the present period, that some of its noisiest authorities insisted on its being received, for good or for evil, in the superlative degree of comparison onlyT WAS the best of times, it was the worst of times, it was the age of wisdom, it was the age of foolishness, it was the epoch of belief, it was the epoch of incredulity, it was the season of Light, it was the season of Darkness, it was the spring of hope, it was the winter of despair, we had everything before us, we had nothing before us, we were all going direct to Heaven, we were all going direct the other way- in short, the period was so far like the present period, that some of its noisiest authorities insisted on its being received, for good or for evil, in the superlative degree of comparison onlyT WAS the best of times, it was the worst of times, it was the age of wisdom, it was the age of foolishness, it was the epoch of belief, it was the epoch of incredulity, it was the season of Light, it was the season of Darkness, it was the spring of hope, it was the winter of despair, we had everything before us, we had nothing before us, we were all going direct to Heaven, we were all going direct the other way- in short, the period was so far like the present period, that some of its noisiest authorities insisted on its being received, for good or for evil, in the superlative degree of comparison onlyT WAS the best of times, it was the worst of times, it was the age of wisdom, it was the age of foolishness, it was the epoch of belief, it was the epoch of incredulity, it was the season of Light, it was the season of Darkness, it was the spring of hope, it was the winter of despair, we had everything before us, we had nothing before us, we were all going direct to Heaven, we were all going direct the other way- in short, the period was so far like the present period, that some of its noisiest authorities insisted on its being received, for good or for evil, in the superlative degree of comparison onlyT WAS the best of times, it was the worst of times, it was the age of wisdom, it was the age of foolishness, it was the epoch of belief, it was the epoch of incredulity, it was the season of Light, it was the season of Darkness, it was the spring of hope, it was the winter of despair, we had everything before us, we had nothing before us, we were all going direct to Heaven, we were all going direct the other way- in short, the period was so far like the present period, that some of its noisiest authorities insisted on its being received, for good or for evil, in the superlative degree of comparison onlyT WAS the best of times, it was the worst of times, it was the age of wisdom, it was the age of foolishness, it was the epoch of belief, it was the epoch of incredulity, it was the season of Light, it was the season of Darkness, it was the spring of hope, it was the winter of despair, we had everything before us, we had nothing before us, we were all going direct to Heaven, we were all going direct the other way- in short, the period was so far like the present period, that some of its noisiest authorities insisted on its being received, for good or for evil, in the superlative degree of comparison onlyT WAS the best of times, it was the worst of times, it was the age of wisdom, it was the age of foolishness, it was the epoch of belief, it was the epoch of incredulity, it was the season of Light, it was the season of Darkness, it was the spring of hope, it was the winter of despair, we had everything before us, we had nothing before us, we were all going direct to Heaven, we were all going direct the other way- in short, the period was so far like the present period, that some of its noisiest authorities insisted on its being received, for good or for evil, in the superlative degree of comparison onlyT WAS the best of times, it was the worst of times, it was the age of wisdom, it was the age of foolishness, it was the epoch of belief, it was the epoch of incredulity, it was the season of Light, it was the season of Darkness, it was the spring of hope, it was the winter of despair, we had everything before us, we had nothing before us, we were all going direct to Heaven, we were all going direct the other way- in short, the period was so far like the present period, that some of its noisiest authorities insisted on its being received, for good or for evil, in the superlative degree of comparison only";
return;
// ------------------------------------
*/
				if(!navigator.onLine){alert('No Internet Connection');return}

				//alert(navigator.connection.type); // &&&&&&######
					// event listener for offline necessary? or does what is below work?

		SpeechRecPlug.init();
		$scope.record=true;

	    SpeechRecPlug.startRecognition(function(msg){
	    	if(!navigator.onLine){alert('No Internet Connection');
		$scope.$apply(function(){
				$scope.record=false;
		});
	    	return}
	    	


	 var div = document.getElementById("test");
     
     if( new String(msg[0].type).valueOf()=="interim")
	 {

	$scope.$apply(function(){
		$scope.output=true;
	});

	body=div.innerHTML;

	body=body.replace(/<b>/,"");
	body=body.replace(/<\/b>/,"");

	if (msg[0].confidence!==0){
		conf='<span style="font-size:8px">%'+Math.floor(100*msg[0].confidence)+' </span>';
	}else{conf=''}



	div.innerHTML = body + "<b>"+msg[0].result + "</b> "+conf;
	/*
	div.innerHTML = div.innerHTML + ""+msg[0].result + " ";
		*/
		
	
	 }
     else if( new String( msg[0].type).valueOf()=="silence!!")
    	 {
//---------------------------------
$scope.timeelapsed=0;
starttimer();

  // because of JS closures, $scope from the outer context will be included in countdown()'s context.
    function starttimer() {
   
if ($scope.timeelapsed>30){ //&&&&&
	
$scope.stopRecognition();
$timeout.cancel($scope.timeout);
$scope.timeelapsed=0;
return;
      }
    $scope.timeelapsed=$scope.timeelapsed+1;
    $scope.timeout = $timeout(starttimer, 1000);
    
  }; 

//----------------------------------
	if ($scope.options.trans==$scope.options.lang){

		div.innerHTML = div.innerHTML +"<br><br>";

	}else{

	if(!navigator.onLine){alert('no internet connection');div.innerHTML = div.innerHTML +"<br><br>";return}

$scope.source=$scope.options.lang.substring(0, 2); // autodetect by eliminating from call
$scope.resultlang=$scope.options.trans.substring(0, 2);

//&&&&&
$scope.i = div.innerHTML.lastIndexOf('<br><br>');
if ($scope.i==-1){$scope.i=0}else{$scope.i=$scope.i+8}


$http.get("https://www.googleapis.com/language/translate/v2?q="+encodeURIComponent(div.innerHTML.substr($scope.i))+"&source="+$scope.source+"&target="+$scope.resultlang+"&key=AIzaSyAhPZ_-mcpgQAadbJf0noMNMINhc6Z0Tgw")
.then(function(response){
		
			div.innerHTML = div.innerHTML.substr(0,$scope.i)+response.data.data.translations[0].translatedText,$scope.locale;
			div.innerHTML = div.innerHTML +"<br><br>";
})

	}


    	 	
    	 	
    	 
    	 }

alt=''

    if (msg[1]){

    	reg=new RegExp(msg[0].result,'i');
    	

    		if (!reg.test(msg[1].result)){

alt=' '+msg[1].result;

if (msg[1].confidence!==0){
alt=alt+'<span style="font-size:8px"> '+Math.floor(100*msg[1].confidence)+'%</span>'

}

   }else{
   //alert(msg[1].result)
   	// confirmation -->first alt equals interim
   }
    }


    if (msg[2]){

    	if (!reg.test(msg[2].result)&&!reg.test(alt)&&alt.length>2){

    		alt=alt+', '+msg[2].result;


if (msg[2].confidence!==0){
alt=alt+'<span style="font-size:8px">%'+Math.floor(100*msg[2].confidence)+' </span>'

}

    	}

    }

div.innerHTML = div.innerHTML + '<span style="font-size:12px;color:rgb(0,148,135)">'+alt+ "</span> "

	    });
	}
	

    });
