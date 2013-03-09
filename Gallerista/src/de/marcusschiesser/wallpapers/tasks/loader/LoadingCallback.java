package de.marcusschiesser.wallpapers.tasks.loader;

import android.graphics.Bitmap;

public interface LoadingCallback {
	void setImageBitmap(Bitmap bitmap);

	void setBitmapWorkerTask(BitmapWorkerTask task);

	BitmapWorkerTask getBitmapWorkerTask();
}