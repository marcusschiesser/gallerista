package de.marcusschiesser.wallpapers.tasks.loader;

import java.lang.ref.WeakReference;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class WeakReferenceDrawable<T> extends Drawable {
	private final WeakReference<T> mWeakReference;
	private final Drawable mDrawable;

	public WeakReferenceDrawable(Drawable progressDrawable, T reference) {
		super();
		mDrawable = progressDrawable;
		mWeakReference = new WeakReference<T>(reference);
	}

	public T get() {
		return mWeakReference.get();
	}

	@Override
	public void draw(Canvas canvas) {
		mDrawable.draw(canvas);
	}

	@Override
	public int getOpacity() {
		return mDrawable.getOpacity();
	}

	@Override
	public void setAlpha(int alpha) {
		mDrawable.setAlpha(alpha);
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		mDrawable.setColorFilter(cf);
	}

	@Override
	public void setBounds(Rect bounds){
		mDrawable.setBounds(bounds);
	}
	
	@Override
	public void setBounds(int l, int t, int r, int b) {
		mDrawable.setBounds(l, t, r, b);
	}

	@Override
	public int getIntrinsicHeight() {
		return mDrawable.getIntrinsicHeight();
	}
	
	@Override
	public int getIntrinsicWidth() {
		return mDrawable.getIntrinsicWidth();
	}
	
	@Override
	public int getMinimumHeight() {
		return mDrawable.getMinimumHeight();
	}
	
	@Override
	public boolean getPadding(Rect padding) {
		return mDrawable.getPadding(padding);
	}
	
}