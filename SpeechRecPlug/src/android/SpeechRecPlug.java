
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import android.util.Log;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


 
public class SpeechRecPlug extends CordovaPlugin {
 
public static final String TAG = "Cool Plugin";
private SpeechRecognizer speech = null;
private Intent recognizerIntent;
private String LOG_TAG = "VoiceRecognitionActivity";
private List<String> previousInterim;
private diff_match_patch diff;
private String display = "Word 'Wikipedia' Recognized - ";

private List<String>adapterList = new ArrayList<String>();
//private ArrayAdapter<String>  adapter;


private static final String INITIALIZE = "init";
private static final String PLAY = "play";
private static final String STOP = "stop";
private static final String SPEECH_SILENCE_GAP="speechSilenceGap";
private static final String LANGUAGE_SPEAK = "languageSpeak";


private AudioManager mAudioManager;
private int mStreamVolume = 0;
private Context context;
private CallbackContext callBackContext;

private int decibelCounter;
private int TimerCounter;
private long timeMilli=0;

private static final String INTERIM_CODE= "interim";
private static final String SILENCE_CODE = "silence!!";

private long silenceDelay=3000;
private boolean isAfterFirstInterim=false;
private boolean isSilenceAlreadyTriggered = false;
private String pastText="";
private Timer t;
private String language = "en";
/**
* Constructor.
*/
public SpeechRecPlug() {} 
 
/**
* Sets the context of the Command. This can then be used to do things like
* get file paths associated with the Activity.
*
* @param cordova The context of the main Activity.
* @param webView The CordovaWebView Cordova is running in.
*/
 
public void initialize(CordovaInterface cordova, CordovaWebView webView)
{
	super.initialize(cordova, webView);
	Log.v(TAG,"Init CoolPlugin");
}
 
public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException
{
         
	final int duration = Toast.LENGTH_SHORT;
	this.callBackContext = callbackContext;
	final AudioRecorder recorder = new AudioRecorder();
	
	// Shows a toast
	Log.v(TAG,"CoolPlugin received:"+ action);
 
    if(INITIALIZE.equals(action))
    {
    	cordova.getActivity().runOnUiThread(new Runnable() {
    		public void run() {
    			init();
    				//Toast toast = Toast.makeText(cordova.getActivity().getApplicationContext(), action, duration);
    				//toast.show();
    				
    		}
    		});
    }
    else if(PLAY.equals(action))
    {
    	cordova.getActivity().runOnUiThread(new Runnable() {
    		public void run() {
    			//startRecording();
    			mStreamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC); // getting system volume into var for later un-muting
    			play(callbackContext);
    			timeMilli = System.currentTimeMillis();
    			timer();
    			
    			//recorder.startRecording();
    				//Toast toast = Toast.makeText(cordova.getActivity().getApplicationContext(), action, duration);
    				//toast.show();
    			
    			
    				
    		}
    		});
    }
    else if(STOP.equals(action))
    {
    	cordova.getActivity().runOnUiThread(new Runnable() {
    		public void run() {
    			
    			t.cancel();
    			stop();
    			
    			mAudioManager = (AudioManager) cordova.getActivity().getSystemService(Context.AUDIO_SERVICE) ;
    			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mStreamVolume, 0); // again setting the system volume back to the original, un-mutting
    			
    			
    			//recorder.stopRecording();
    				//Toast toast = Toast.makeText(cordova.getActivity().getApplicationContext(), action, duration);
    				//toast.show();
    				
    		}
    		});
    }
    else if(SPEECH_SILENCE_GAP.equals(action))
    {
    	Log.i(LOG_TAG, "tiggered");
    	cordova.getActivity().runOnUiThread(new Runnable() {
    		public void run() {
    			
    			try
    			{
    				JSONObject arg_object = args.getJSONObject(0);
    				long silenceDelay = arg_object.getLong("speechSilenceGap");
    				setSilenceDelay(silenceDelay);
    				Log.i(LOG_TAG, "speechDelay: "+silenceDelay);
    			}
    			catch(JSONException e)
    			{
    				Log.i(LOG_TAG, "Json error",e);
    			}
    				
    		}
    		});
    }
    else if(LANGUAGE_SPEAK.equals(action))
    {
    	Log.i(LOG_TAG, "tiggered");
    	cordova.getActivity().runOnUiThread(new Runnable() {
    		public void run() {
    			
    			try
    			{
    				JSONObject arg_object = args.getJSONObject(0);
    				String languageSet = arg_object.getString("languageSpeak");
    				setLanguage(languageSet);
    				Log.i(LOG_TAG, "Language Set: "+language);
    			}
    			catch(JSONException e)
    			{
    				Log.i(LOG_TAG, "Json error",e);
    			}
    				
    		}
    		});
    }
	
 
	return true;
}

private void test()
{
	 String event = String.format("javascript:cordova.fireDocumentEvent('yourEventHere', { 'param1': '%s' });", "some string for param1");
	    this.webView.loadUrl(event);
}

/**
 * Track the period of silence and generate triggers accordingly
 **/
private void timer()
{
	t=new Timer();
    t.scheduleAtFixedRate(new TimerTask() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
        	cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                	
                	/*if(TimerCounter==60)
                	{
                		t.cancel();
                		stop();
                		
                		
                	}
                   
                	Log.i("DECIBAL", String.valueOf(decibelCounter));
                	TimerCounter++;
                	Log.i("TIMER", String.valueOf(TimerCounter));*/
                	if(((System.currentTimeMillis()-timeMilli)>=getSilenceDelay()) && isAfterFirstInterim && isSilenceAlreadyTriggered==false)
                	{
                		JSONArray jsonArray = new JSONArray();
                    	jsonArray.put(makeJsonObject(SILENCE_CODE, null, 0, System.currentTimeMillis()));
                    	
                    	
                		PluginResult result = new PluginResult(Status.OK,jsonArray);
                    	result.setKeepCallback(true);
                    	callBackContext.sendPluginResult(result);
                    	isSilenceAlreadyTriggered=true;
                    	previousDiff="";
                    	previousInterim.clear();
                    	
                    	//Restart
                    	stop();
                    	init();
                    	play(callBackContext);
                    	
                    	
                		
                	}
                	
                	
                	
                	
                	
                }
            });

        }
    }, 1000, 1000);
}

protected void init() {
	
	context=this.cordova.getActivity().getApplicationContext(); 
    mAudioManager = (AudioManager) cordova.getActivity().getSystemService(Context.AUDIO_SERVICE) ;
    

   // createRecog();
    speech = SpeechRecognizer.createSpeechRecognizer(context);

    previousInterim = new ArrayList<String>();

    diff = new diff_match_patch();
    
    //Toast toast = Toast.makeText(cordova.getActivity().getApplicationContext(), "HELLO", Toast.LENGTH_LONG);
	//toast.show();

}

public void play(CallbackContext callbackContext)
{
    //Mute the sound
    mAudioManager = (AudioManager) cordova.getActivity().getSystemService(Context.AUDIO_SERVICE) ;
    
    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0); // setting system volume to

    //Start listening
    speech.stopListening();
    speech.cancel();
    speech.destroy();
    createRecog();
    speech.startListening(recognizerIntent);
    //adapter.clear();
}

public void stop()
{
    speech.stopListening();
    speech.cancel();
    speech.destroy();
    Log.i(LOG_TAG, "SpeechListener Shut Down Complete");
}



private class RecognitionListenerClass implements RecognitionListener
{
	CallbackContext callbackContext;
	
    void onStartOfSpeechTimeout(CallbackContext callbackContext)
    {
    	this.callbackContext = callbackContext;
    	
    };

    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
        play(callbackContext);
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d(LOG_TAG, "FAILED " + errorMessage);
       // returnedText.setText(errorMessage);

        play(callbackContext);

    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.i(LOG_TAG, "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0) {
        Log.i(LOG_TAG, "onPartialResults");
        final ArrayList<String> matches = arg0.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        final float[] scores = arg0.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
        receiveWhatWasHeard(matches, scores);
       

    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.i(LOG_TAG, "onReadyForSpeech");
      //  mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mStreamVolume, 0); // again setting the system volume back to the original, un-mutting
    }

    @Override
    public void onResults(Bundle results) {


    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
        decibelCounter++;
    }
}

private String previousDiff="";
private MediaRecorder mRecorder;

private void receiveWhatWasHeard(ArrayList<String> matches, float[] scores) {


    Log.i(LOG_TAG, matches.get(0));
    
    if(scores==null)
    {
    	Log.i(LOG_TAG, "CONFIDENCE: null");
    }
    else
    {
    	Log.i(LOG_TAG, "CONFIDENCE: "+String.valueOf(scores[0]));
    }
    

    //ArrayList<String> suggestedWords = recognizerIntent.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
    //set the retrieved list to display in the ListView using an ArrayAdapter
   // wordList.setAdapter(new ArrayAdapter<String>(MainActivity.this, R.layout.word, matches));


    if(previousInterim.size()==0)
    {
    	JSONArray jsonArray = new JSONArray();
    	
    	for(int i=0;i<matches.size();i++)
    	{
    		jsonArray.put(makeJsonObject(INTERIM_CODE, matches.get(i), (scores!=null)?scores[i]:0, System.currentTimeMillis()));
    	}
    	
    	Log.i("MATCH",  matches.get(0));
       // returnedText.setText(matches.get(0)); // Commented Temp
    	//PluginResult result = new PluginResult(Status.OK,matches.get(0));
    	PluginResult result = new PluginResult(Status.OK,jsonArray);
 	   result.setKeepCallback(true);
 	   callBackContext.sendPluginResult(result);
 	   isSilenceAlreadyTriggered = false;
 	   
 	//  Log.i("CONFIDENCE", "CONFIDENCE: "+String.valueOf(scores[0]));
 	   
 	  timeMilli = System.currentTimeMillis();
 	  isAfterFirstInterim=true;
    	

        if(matches.get(0).toLowerCase().contains("wikipedia")) {
            final String[] split = matches.get(0).split(" ");

            for (int i = 0; i < split.length; i++) {
                if (split[i].toLowerCase().equals("wikipedia")) {
                    if (scores != null && scores.length > 0) {
                       // adapter.add(display + scores[0]); //Commented Temp
                    } else {
                       // adapter.add(display + "null");
                    }
                    //Toast.makeText(getApplicationContext(), "Word 'Child' Recognized", Toast.LENGTH_LONG);
                }
            }
        }

    }
    else
    {
        if(!((previousInterim.get(previousInterim.size()-1)).equals(matches.get(0))))
        {
            diff.Diff_Timeout = 1.0f;
            diff.Diff_EditCost = 4 ;
            
            Log.i("DIFF-MATCH", matches.get(0));
            Log.i("DIFF-PREV", previousInterim.get(previousInterim.size()-1));
            
            String newText = pastText.replace(matches.get(0), "");

            LinkedList<diff_match_patch.Diff> diffLinkedList = diff.diff_main((previousInterim.get(previousInterim.size()-1)), matches.get(0));
            diff.diff_cleanupSemantic(diffLinkedList);
            String prettyHtml = diff.diff_prettyHtml1(diffLinkedList);
            
            Log.i("DIFF-HTML", prettyHtml);

            if(!prettyHtml.equals(previousDiff))
            {
            	String [] array=prettyHtml.split(" ");
            	String finalTxt="";
            	
            	for(int i=0;i<array.length;i++)
            	{
            		if(previousDiff.contains(array[i]))
            		{
            			finalTxt=prettyHtml.replace(array[i], "");
            		}
            	}
            	
            	Log.i("DIFF-HTML-2", finalTxt);
            	
            	JSONArray jsonArray = new JSONArray();
            	jsonArray.put(makeJsonObject(INTERIM_CODE, finalTxt, (scores!=null)?scores[0]:0, System.currentTimeMillis()));
            	
            	for(int i=0;i<matches.size();i++)
            	{
            		jsonArray.put(makeJsonObject(INTERIM_CODE, matches.get(i), (scores!=null)?scores[i]:0, System.currentTimeMillis()));
            	}
                //returnedText.append(prettyHtml+" "); //commented temp
            	//PluginResult result = new PluginResult(Status.OK,prettyHtml);
            	PluginResult result = new PluginResult(Status.OK,jsonArray);
          	   result.setKeepCallback(true);
          	   callBackContext.sendPluginResult(result);
          	   isSilenceAlreadyTriggered = false;
          	 
          	   timeMilli = System.currentTimeMillis();
          	   isAfterFirstInterim=true;



                if(matches.get(0).toLowerCase().contains("wikipedia")) {
                    String split[] = prettyHtml.split(" ");
                    for (int i = 0; i < split.length; i++) {
                        if (split[i].toLowerCase().equals("wikipedia")) {
                            if (scores != null && scores.length > 0) {
                               // adapter.add(display + scores[0]);
                            } else {
                               // adapter.add(display + "null");
                            }
                        }
                    }
                }
            }
            previousDiff = prettyHtml;
            pastText = matches.get(0);
        }
    }
    previousInterim.add(matches.get(0));

}

private void createRecog()
{
    speech = SpeechRecognizer.createSpeechRecognizer(context);
    speech.setRecognitionListener(new RecognitionListenerClass());
    recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,getLanguage());
    recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,getLanguage());
    recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,context.getPackageName());
    recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
    recognizerIntent.putExtra("android.speech.extra.DICTATION_MODE", true);
    recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
    recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, new Long(20000000));
    recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, new Long(20000000));
    recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, new Long(20000000));
   
}




public  String getErrorText(int errorCode) {
    String message;
    switch (errorCode) {
        case SpeechRecognizer.ERROR_AUDIO:
            message = "Audio recording error";
            break;
        case SpeechRecognizer.ERROR_CLIENT:
            message = "Client side error";
            break;
        case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
            message = "Insufficient permissions";
            break;
        case SpeechRecognizer.ERROR_NETWORK:
            message = "Network error";
            break;
        case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
            message = "Network timeout";
            break;
        case SpeechRecognizer.ERROR_NO_MATCH:
            message = "No match";
            break;
        case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
            message = "RecognitionService busy";
            break;
        case SpeechRecognizer.ERROR_SERVER:
            message = "error from server";
            break;
        case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
            message = "No speech input";
            break;
        default:
            message = "Didn't understand, please try again.";
            break;
    }

    return message;
}

/**
 * makeJsonArray() is only for populating the JsonArray. You have to clear the JSONArray and JSONObject  outside of this method.
 * @param type 
 * 		-the type of the result. Define whether final result or interim
 * @param result 
 * 		-the recognized result
 * @param confidence 
 * 		-the confidence value of the recognized result
 * @param currentTimeMili
 * 		-the time where the word was spoken
 * @return JSONArray
 */
private JSONObject makeJsonObject(String type, String result, float confidence, long currentTimeMili)
{
	JSONObject recogResult = new JSONObject();
	JSONArray jsonArray = new JSONArray();
	//JSONObject jsonObject = new JSONObject();
	
	if(type.equals(INTERIM_CODE))
	{
		try
		{
			recogResult.put("type", type);
			recogResult.put("result", result);
			recogResult.put("confidence", confidence);
			recogResult.put("time", currentTimeMili);
			
			jsonArray.put(recogResult);
			//jsonObject.put("recognitionResults", recogResult);
			
		}
		catch(JSONException  e)
		{
			Log.d(LOG_TAG, "JSON Error", e);
		}
	}
	else if(type.equals(SILENCE_CODE))
	{
		try
		{
			recogResult.put("type", type);			
			jsonArray.put(recogResult);
			//jsonObject.put("recognitionResults", recogResult);
			
		}
		catch(JSONException  e)
		{
			Log.d(LOG_TAG, "JSON Error", e);
		}
	}
	
	
	
	return recogResult;
}


/**
 * Set the value for the authorized silence time between 2 speeches
 * @param silenceDelay
 * 		- authorized silence time between 2 speeches
 */
private void setSilenceDelay(long silenceDelay)
{
	this.silenceDelay = silenceDelay;
}


/**
 * Get the value for the authorized silence time between 2 speeches
 * @return
 * 		-silenceDelay
 */
private long getSilenceDelay()
{
	return silenceDelay;
}

private void setLanguage(String lan)
{
	language = lan;
}

private String getLanguage()
{
	return language;
}


private void startRecording() {
	
	String  mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
    mFileName += "/audiorecordtest.3gp";
	
    mRecorder = new MediaRecorder();
    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
    mRecorder.setOutputFile(mFileName);
    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

    try {
        mRecorder.prepare();
    } catch (IOException e) {
        Log.e(LOG_TAG, "prepare() failed");
    }

    mRecorder.start();
}



}