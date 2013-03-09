package de.marcusschiesser.wallpapers.utils;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

/**
 * Simple exception handler. Must run in the UI-Thread as
 * Toast messages are send to the user.
 * 
 * @author Marcus
 */
public class ExceptionUtils {
	private static Application application;
	
	/**
	 * Initializes the exception handler with the actual Application 
	 * @param app
	 */
	public static void setApplication(Application app) {
		application = app;
	}
	
	public static void handleException(Throwable tr, int msgId) {
		if(application==null)
			throw new IllegalStateException("You must initialize the ExceptionUtils using setApplication.");
		String msg = application.getResources().getString(msgId);
		Log.e(ExceptionUtils.class.getName(), msg, tr);
		Toast.makeText(application, msgId, Toast.LENGTH_LONG).show();
	}
}
