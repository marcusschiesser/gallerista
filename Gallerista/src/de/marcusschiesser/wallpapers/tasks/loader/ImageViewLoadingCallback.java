package de.marcusschiesser.wallpapers.tasks.loader;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import de.marcusschiesser.wallpapers.R;

public class ImageViewLoadingCallback implements LoadingCallback {
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