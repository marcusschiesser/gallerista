package de.marcusschiesser.gallerista.tasks;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;

import de.marcusschiesser.gallerista.utils.BitmapCacheUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class BitmapWorkerTask extends AsyncTask<URL, Void, Bitmap> {

	private final WeakReference<ImageView> imageViewReference;
	private URL url;

	private BitmapWorkerTask(ImageView imageView) {
		// Use a WeakReference to ensure the ImageView can be garbage collected
		imageViewReference = new WeakReference<ImageView>(imageView);
	}

	// Decode image in background.
	@Override
	protected Bitmap doInBackground(URL... param) {
		url = param[0];
		try {
			final Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection()
					.getInputStream());
			BitmapCacheUtils.addBitmapToMemoryCache(url, bitmap);
			return bitmap;
		} catch (IOException e) {
			Log.e(BitmapWorkerTask.class.getName(), "error cannot load bitmap");
			return null;
		}
	}

	// Once complete, see if ImageView is still around and set bitmap.
	@Override
	protected void onPostExecute(Bitmap bitmap) {
		if (isCancelled()) {
			bitmap = null;
		}

		if (imageViewReference != null && bitmap != null) {
			final ImageView imageView = imageViewReference.get();
			final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
			if (this == bitmapWorkerTask && imageView != null) {
				imageView.setImageBitmap(bitmap);
			}
		}
	}

	public static void loadBitmap(URL url, ImageView imageView) {
		final Bitmap bitmap = BitmapCacheUtils.getBitmapFromMemCache(url);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else {
			if (cancelPotentialWork(url, imageView)) {
				final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
				final AsyncDrawable asyncDrawable = new AsyncDrawable(task);
				imageView.setImageDrawable(asyncDrawable);
				task.execute(url);
			}
		}
	}

	private static boolean cancelPotentialWork(URL url, ImageView imageView) {
		final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

		if (bitmapWorkerTask != null) {
			final URL bitmapUrl = bitmapWorkerTask.url;
			if (bitmapUrl != url) {
				// Cancel previous task
				bitmapWorkerTask.cancel(true);
			} else {
				// The same work is already in progress
				return false;
			}
		}
		// No task associated with the ImageView, or an existing task was
		// cancelled
		return true;
	}

	private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
		}
		return null;
	}

	private static class AsyncDrawable extends ColorDrawable {
		private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

		public AsyncDrawable(BitmapWorkerTask bitmapWorkerTask) {
			super(Color.RED);
			bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(
					bitmapWorkerTask);
		}

		public BitmapWorkerTask getBitmapWorkerTask() {
			return bitmapWorkerTaskReference.get();
		}
	}

}
