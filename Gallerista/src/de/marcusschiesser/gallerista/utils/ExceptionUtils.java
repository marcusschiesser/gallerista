package de.marcusschiesser.gallerista.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class ExceptionUtils {
	public static void handleException(Context ctx, Throwable tr, String msg) {
		Log.e(ExceptionUtils.class.getName(), msg, tr);
		Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
	}
}
