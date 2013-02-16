package de.marcusschiesser.gallerista.utils;

import java.net.URL;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class BitmapCacheUtils {
	private static LruCache<URL, Bitmap> mMemoryCache;

	static {
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		// Use 1/8th of the available memory for this memory cache.
		final int cacheSize = maxMemory / 8;

		mMemoryCache = new LruCache<URL, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(URL key, Bitmap bitmap) {
				// The cache size will be measured in kilobytes rather than
				// number of items.
				return bitmap.getHeight() * bitmap.getWidth() / 4 / 1024;
			}
		};
	}
	
	public static void addBitmapToMemoryCache(URL key, Bitmap bitmap) {
	    if (getBitmapFromMemCache(key) == null) {
	        mMemoryCache.put(key, bitmap);
	    }
	}

	public static Bitmap getBitmapFromMemCache(URL key) {
	    return mMemoryCache.get(key);
	}
}
