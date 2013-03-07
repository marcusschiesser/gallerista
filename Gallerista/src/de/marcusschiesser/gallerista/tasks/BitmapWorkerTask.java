package de.marcusschiesser.gallerista.tasks;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
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

	public interface LoadingCallback {
		void setImageBitmap(Bitmap bitmap);

		void setBitmapWorkerTask(BitmapWorkerTask task);

		BitmapWorkerTask getBitmapWorkerTask();
	}

	public static void loadBitmap(final Context ctx, final URL url,
			final ImageView imageView) {
		loadBitmap(ctx, url, new ImageViewLoadingCallback(ctx, imageView, R.drawable.spinner_48_inner_holo));
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
		private final WeakReference<ImageView> mImageViewReference;
		private final Context mContext;
		private final ScaleType mScaleType;
		private final int mProgressDrawableId;

		public ImageViewLoadingCallback(Context ctx, ImageView imageView, int progressDrawableId) {
			this.mImageViewReference = new WeakReference<ImageView>(imageView);
			mContext = ctx;
			mScaleType = imageView.getScaleType();
			mProgressDrawableId = progressDrawableId;
		}

		@Override
		public void setImageBitmap(Bitmap bitmap) {
			ImageView imageView = mImageViewReference.get();
			if (imageView != null) {
				imageView.clearAnimation();
				imageView.setImageBitmap(bitmap);
				imageView.setScaleType(mScaleType);
			}
		}

		@Override
		public BitmapWorkerTask getBitmapWorkerTask() {
			ImageView imageView = mImageViewReference.get();
			if (imageView != null) {
				final Drawable tag = imageView.getDrawable();
				if (tag instanceof WeakReferenceDrawable<?>) {
					@SuppressWarnings("unchecked")
					final WeakReferenceDrawable<BitmapWorkerTask> asyncDrawable = (WeakReferenceDrawable<BitmapWorkerTask>) tag;
					return asyncDrawable.get();
				}
			}
			return null;
		}

		@Override
		public void setBitmapWorkerTask(BitmapWorkerTask task) {
			ImageView imageView = mImageViewReference.get();
			if (imageView != null) {
				final Resources resources = mContext.getResources();
				final Drawable progressDrawable = resources
						.getDrawable(mProgressDrawableId);
				imageView.setScaleType(ScaleType.CENTER);
				imageView.setImageDrawable(new WeakReferenceDrawable<BitmapWorkerTask>(progressDrawable, task));
				Animation rotationAnim = AnimationUtils.loadAnimation(mContext,
						R.anim.clockwise_rotation);
				imageView.startAnimation(rotationAnim);
			}
		}
	}

	private static class WeakReferenceDrawable<T> extends LayerDrawable {
		private final WeakReference<T> mWeakReference;

		private static Drawable[] createDrawableArray(Drawable d) {
			Drawable[] drawables = new Drawable[1];
			drawables[0] = d;
			return drawables;
		}

		public WeakReferenceDrawable(Drawable progressDrawable, T reference) {
			super(createDrawableArray(progressDrawable));
			mWeakReference = new WeakReference<T>(reference);
		}

		public T get() {
			return mWeakReference.get();
		}

	}

}
