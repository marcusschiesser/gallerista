package de.marcusschiesser.wallpapers.tasks.loader;

import java.io.IOException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;
import de.marcusschiesser.wallpapers.R;
import de.marcusschiesser.wallpapers.utils.BitmapCacheUtils;
import de.marcusschiesser.wallpapers.utils.ExceptionUtils;
import de.marcusschiesser.wallpapers.utils.ImageUtils;

/**
 * AsyncTask that loads the Bitmap of an image which is referenced by its URL.
 * After loading the Bitmap is stored in the provided ImageView. If multiple
 * tasks are started for one ImageView, the last one can update the ImageView.
 * That way it supports ImageViews stored in a GridView which a shared for
 * multiple positions. While loading the Task shows a AnimationDrawable in the
 * ImageView. Previously loaded images are not loaded twice but retrieved from a
 * cache. To initiate a new task and thereby use this class use the loadBitmap
 * method.
 * 
 * @author Marcus
 */
public class BitmapWorkerTask extends AsyncTask<URL, Void, Bitmap> {

	private final LoadingCallback mCallback;
	private URL mUrl;
	private IOException mIoException;

	private BitmapWorkerTask(final LoadingCallback callback) {
		if (callback == null)
			throw new IllegalArgumentException("LoadingCallback must be set.");
		mCallback = callback;
		mIoException = null;
	}

	// Decode image in background.
	@Override
	protected Bitmap doInBackground(URL... param) {
		mUrl = param[0];
		try {
			Bitmap bitmap = BitmapCacheUtils.getBitmapFromDiskCache(mUrl);
			if (bitmap == null && !isCancelled()) {
				bitmap = ImageUtils.readBitmapFromURL(mUrl);
				if (bitmap != null) {
					BitmapCacheUtils.addBitmapToCache(mUrl, bitmap);
				}
			}
			return bitmap;
		} catch (IOException e) {
			mIoException = e;
			return null;
		}
	}

	// Once complete, see if ImageView is still around and set bitmap.
	@Override
	protected void onPostExecute(Bitmap bitmap) {
		if (isCancelled()) {
			bitmap.recycle();
		} else {
			final BitmapWorkerTask bitmapWorkerTask = mCallback
					.getBitmapWorkerTask();
			if (this == bitmapWorkerTask) {
				if (mIoException != null) {
					ExceptionUtils.handleException(mIoException,
							R.string.error_loading_image);
					bitmap = null;
				}
				mCallback.setImageBitmap(bitmap);
			}
		}
	}

	/**
	 * Loads an image from the provided url to the provided ImageView.
	 * 
	 * @param ctx
	 * @param url
	 * @param imageView
	 */
	public static void loadBitmap(final Context ctx, final URL url,
			final LoadingCallback callback) {
		final Bitmap bitmap = BitmapCacheUtils.getBitmapFromMemCache(url);
		if (bitmap != null) {
			callback.setImageBitmap(bitmap);
		} else {
			if (cancelPotentialWork(url, callback)) {
				BitmapWorkerTask task = new BitmapWorkerTask(callback);
				callback.setBitmapWorkerTask(task);
				task.execute(url);
			}
		}
	}

	private static boolean cancelPotentialWork(final URL url, final LoadingCallback callback) {
		final BitmapWorkerTask bitmapWorkerTask = callback
				.getBitmapWorkerTask();

		if (bitmapWorkerTask != null) {
			final URL bitmapUrl = bitmapWorkerTask.mUrl;
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

	public static void loadBitmap(final Context ctx, final URL url,
			final ImageView imageView) {
		loadBitmap(ctx, url, new ImageViewLoadingCallback(ctx, imageView, R.drawable.spinner_48_inner_holo));
	}

}
