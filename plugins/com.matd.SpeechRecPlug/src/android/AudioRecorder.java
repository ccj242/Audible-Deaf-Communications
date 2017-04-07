import java.io.FileDescriptor;
import java.io.IOException;
import java.io.File;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

public class AudioRecorder {
	
	 private static final String LOG_TAG = "AudioRecoder";
	private MediaRecorder mRecorder=new MediaRecorder();;
	String file;
	
	public AudioRecorder()
	{
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyFolder/";
		File dir = new File(path);
		if(!dir.exists())
		    dir.mkdirs();
	//	String myfile = path + "filename" + ".mp4";
		
		file=path + "filename" + ".3gp";
	}

	public void startRecording() {
		
		
	   
	        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mRecorder.setOutputFile(file);
	        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

	        try {
	            mRecorder.prepare();
	            Log.e(LOG_TAG, "prepare() success");
	        } catch (IOException e) {
	           // Log.e(LOG_TAG, "prepare() failed");
	            Log.d(LOG_TAG, "prepare() failed", e);
	        }

	        mRecorder.start();
	        Log.e(LOG_TAG, "Working");
	    }
	
	public void stopRecording() {
        mRecorder.stop();
        mRecorder.reset();
        mRecorder.release();
        mRecorder = null;
    }


}
