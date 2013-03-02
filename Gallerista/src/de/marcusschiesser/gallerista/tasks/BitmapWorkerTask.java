package de.marcusschiesser.gallerista.tasks;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;
import de.marcusschiesser.gallerista.R;
import de.marcusschiesser.gallerista.utils.BitmapCacheUtils;
import de.marcusschiesser.gallerista.utils.ExceptionUtils;
import de.marcusschiesser.gallerista.utils.ImageUtils;

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
			if (bitmap == null) {
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
		if (mIoException != null) {
			ExceptionUtils.handleException(mIoException,
					R.string.error_loading_image);
		}
		if (isCancelled()) {
			bitmap = null;
		}

		if (bitmap != null) {
			final BitmapWorkerTask bitmapWorkerTask = mCallback
					.getBitmapWorkerTask();
			if (this == bitmapWorkerTask) {
				mCallback.setImageBitmap(bitmap, mIoException == null);
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
	public static void loadBitmap(Context ctx, URL url,
			final LoadingCallback callback) {
		final Bitmap bitmap = BitmapCacheUtils.getBitmapFromMemCache(url);
		if (bitmap != null) {
			callback.setImageBitmap(bitmap, true);
		} else {
			if (cancelPotentialWork(url, callback)) {
				BitmapWorkerTask task = new BitmapWorkerTask(callback);
				callback.setBitmapWorkerTask(task);
				task.execute(url);
			}
		}
	}

	private static boolean cancelPotentialWork(URL url, LoadingCallback callback) {
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

	public interface LoadingCallback {
		void setImageBitmap(Bitmap bitmap, boolean success);
		void setBitmapWorkerTask(BitmapWorkerTask task);
		BitmapWorkerTask getBitmapWorkerTask();
	}

	public static void loadBitmap(final Context ctx, final URL url,
			final ImageView imageView) {
		loadBitmap(ctx, url, new ImageViewLoadingCallback(ctx, imageView));
	}

	public static abstract class DefaultLoadingCallback implements
			LoadingCallback {
		private BitmapWorkerTask mBitmapWorkerTaskReference = null;

		@Override
		public void setBitmapWorkerTask(BitmapWorkerTask task) {
			mBitmapWorkerTaskReference = task;
		}

		@Override
		public BitmapWorkerTask getBitmapWorkerTask() {
			return mBitmapWorkerTaskReference;
		}

	}

	public static class ImageViewLoadingCallback implements LoadingCallback {
		private final Context mCtx;
		private final WeakReference<ImageView> mImageViewReference;

		public ImageViewLoadingCallback(Context ctx, ImageView imageView) {
			this.mCtx = ctx;
			this.mImageViewReference = new WeakReference<ImageView>(imageView);
		}

		@Override
		public void setImageBitmap(Bitmap bitmap, boolean success) {
			ImageView imageView = mImageViewReference.get();
			if (imageView != null) {
				imageView.setImageBitmap(bitmap);
			}
		}

		@Override
		public BitmapWorkerTask getBitmapWorkerTask() {
			ImageView imageView = mImageViewReference.get();
			if (imageView != null) {
				final Drawable drawable = imageView.getDrawable();
				if (drawable instanceof AsyncDrawable) {
					final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
					return asyncDrawable.getBitmapWorkerTask();
				}
			}
			return null;
		}

		@Override
		public void setBitmapWorkerTask(BitmapWorkerTask task) {
			ImageView imageView = mImageViewReference.get();
			if (imageView != null) {
				final AsyncDrawable asyncDrawable = new AsyncDrawable(mCtx,
						task);
				imageView.setImageDrawable(asyncDrawable);
				asyncDrawable.start();
			}
		}
	}

	private static class AsyncDrawable extends AnimationDrawable {
		private final WeakReference<BitmapWorkerTask> mBitmapWorkerTaskReference;

		public AsyncDrawable(Context ctx, BitmapWorkerTask bitmapWorkerTask) {
			super();
			Resources resources = ctx.getResources();
			// TODO: here we need some nicer images
			addFrame(resources.getDrawable(android.R.drawable.star_off), 500);
			addFrame(resources.getDrawable(android.R.drawable.star_on), 500);
			setOneShot(false);

			mBitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(
					bitmapWorkerTask);
		}

		public BitmapWorkerTask getBitmapWorkerTask() {
			return mBitmapWorkerTaskReference.get();
		}
	}

}
