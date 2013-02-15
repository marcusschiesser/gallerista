package de.marcusschiesser.gallerista.tasks;

import java.io.IOException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class BitmapWorkerTask extends AsyncTask<URL, Void, Bitmap> {

	protected Bitmap doInBackground(URL... param) {
		try {
			return BitmapFactory.decodeStream(param[0].openConnection().getInputStream());
		} catch (IOException e) {
			Log.e(BitmapWorkerTask.class.getName(), "error cannot load bitmap");
			return null;
		}
	}

}
