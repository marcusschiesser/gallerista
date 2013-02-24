package de.marcusschiesser.gallerista.tasks;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;
import de.marcusschiesser.gallerista.R;
import de.marcusschiesser.gallerista.utils.BitmapCacheUtils;
import de.marcusschiesser.gallerista.utils.ExceptionUtils;

/**
 * AsyncTask that loads the Bitmap of an image which is referenced by its URL.
 * After loading the Bitmap is stored in the provided ImageView.
 * If multiple tasks are started for one ImageView, the last one can update the
 * ImageView. That way it supports ImageViews stored in a GridView which a 
 * shared for multiple positions.
 * While loading the Task shows a AnimationDrawable in the ImageView.
 * Previously loaded images are not loaded twice but retrieved from a cache.
 * To initiate a new task and thereby use this class use the loadBitmap method.
 * 
 * @author Marcus
 */
public class BitmapWorkerTask extends AsyncTask<URL, Void, Bitmap> {

	private final WeakReference<ImageView> mImageViewReference;
	private URL mUrl;
	private IOException mIoException;

	private BitmapWorkerTask(ImageView imageView) {
		// Use a WeakReference to ensure the ImageView can be garbage collected
		mImageViewReference = new WeakReference<ImageView>(imageView);
		mIoException = null;
	}

	// Decode image in background.
	@Override
	protected Bitmap doInBackground(URL... param) {
		mUrl = param[0];
		try {
			final Bitmap bitmap = BitmapFactory.decodeStream(mUrl
					.openConnection().getInputStream());
			if(bitmap!=null) {
				BitmapCacheUtils.addBitmapToMemoryCache(mUrl, bitmap);
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
		if(mIoException!=null) {
			ExceptionUtils.handleException(mIoException, R.string.error_loading_image);
		}
		if (isCancelled()) {
			bitmap = null;
		}

		if (mImageViewReference != null && bitmap != null) {
			final ImageView imageView = mImageViewReference.get();
			final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
			if (this == bitmapWorkerTask && imageView != null) {
				imageView.setImageBitmap(bitmap);
			}
		}
	}

	/**
	 * Loads an image from the provided url to the provided ImageView.
	 * @param ctx
	 * @param url
	 * @param imageView
	 */
	public static void loadBitmap(Context ctx, URL url, ImageView imageView) {
		final Bitmap bitmap = BitmapCacheUtils.getBitmapFromMemCache(url);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else {
			if (cancelPotentialWork(url, imageView)) {
				final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
				final AsyncDrawable asyncDrawable = new AsyncDrawable(ctx, task);
				imageView.setImageDrawable(asyncDrawable);
				asyncDrawable.start();
				task.execute(url);
			}
		}
	}

	private static boolean cancelPotentialWork(URL url, ImageView imageView) {
		final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

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

	private static class AsyncDrawable extends AnimationDrawable {
		private final WeakReference<BitmapWorkerTask> mBitmapWorkerTaskReference;

		public AsyncDrawable(Context ctx, BitmapWorkerTask bitmapWorkerTask) {
			super();
			Resources resources = ctx.getResources();
			// TODO: here we need some nicer images
			addFrame(
					resources
							.getDrawable(android.R.drawable.star_off),
					500);
			addFrame(
					resources
							.getDrawable(android.R.drawable.star_on),
					500);
			setOneShot(false);
			
			mBitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(
					bitmapWorkerTask);
		}

		public BitmapWorkerTask getBitmapWorkerTask() {
			return mBitmapWorkerTaskReference.get();
		}
	}

}
