package de.marcusschiesser.gallerista.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import de.marcusschiesser.cache.BitmapValueCodec;
import de.marcusschiesser.cache.FlickrURLEncoder;
import de.marcusschiesser.cache.GenericDiskLruCache;
import de.marcusschiesser.gallerista.R;

/**
 * Helper class to store Bitmaps by their URL in a least-recently-used cache The
 * cache uses memory as 1st-level and falls back to a disk-cache as 2nd-level. To
 * use the cache, the method initCache must be called first. 
 * 
 * @author Marcus
 */
public class BitmapCacheUtils {
	private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
	private static final int MEM_CACHE_SIZE_OF_AVAILABLE_MEM = 8;

	private static LruCache<URL, Bitmap> mMemoryCache;
	private static GenericDiskLruCache<URL, Bitmap> mDiskCache;
	private static AsyncTask<Void, Void, Void> mInitTask = null;

	/**
	 * Initializes the cache. The disk cache is stored in a sub-directory
	 * on the systems cache dir with the name given by cacheName.
	 * Can be called from the UI-thread as it starts its own AsyncTask.
	 * 
	 * @param context
	 * @param cacheName
	 * @throws IOException
	 */
	public static void initCache(final Context context, final String cacheName) {
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		// Use 1/8th of the available memory for this memory cache.
		final int cacheSize = maxMemory / MEM_CACHE_SIZE_OF_AVAILABLE_MEM;

		mMemoryCache = new LruCache<URL, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(URL key, Bitmap bitmap) {
				// The cache size will be measured in kilobytes rather than
				// number of items.
				return bitmap.getHeight() * bitmap.getWidth() * 4 / 1024;
			}
		};
		mInitTask = new AsyncTask<Void, Void, Void>() {
			private Throwable mIoException = null;

			@Override
			protected Void doInBackground(Void... params) {
				try {
					final String cachePath = context.getCacheDir().getPath();
					final File cacheDir = new File(cachePath + File.separator
							+ cacheName);
					mDiskCache = new GenericDiskLruCache<URL,Bitmap>(cacheDir,
							DISK_CACHE_SIZE, new FlickrURLEncoder(), new BitmapValueCodec());
				} catch (IOException e) {
					mIoException = e;
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				if (mIoException != null) {
					ExceptionUtils.handleException(mIoException,
							R.string.error_init_cache);
				}
			}
		}.execute();
	}

	public static Bitmap getBitmapFromMemCache(URL key) {
		return mMemoryCache.get(key);
	}

	/**
	 * Adds the provided Bitmap instance to the mem and to the disk cache.
	 * Ignores the disk cache, if it has not been initialized yet. It is a
	 * blocking I/O call, so must be called outside from the UI-thread.
	 * 
	 * @param url
	 * @param bitmap
	 * @throws IOException
	 */
	public static void addBitmapToCache(URL url, Bitmap bitmap)
			throws IOException {
		waitForInit();

		// Add to memory cache
		if (getBitmapFromMemCache(url) == null) {
			mMemoryCache.put(url, bitmap);
		}

		// Also add to disk cache, if available
		if (mDiskCache != null && mDiskCache.getValue(url) == null) {
			mDiskCache.putValue(url, bitmap);
		}
	}

	/**
	 * If a bitmap is stored in the cache under the provided url, this method
	 * returns its instance. Returns null on a cache fail. Waits till
	 * initialization of the cache has finished.
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static Bitmap getBitmapFromDiskCache(URL url) throws IOException {
		waitForInit();
		if (mDiskCache != null) {
			return mDiskCache.getValue(url);
		}
		return null;
	}

	/**
	 * Actual thread waits till the init task has been finished. Throws an
	 * {@link IllegalStateException} if cache never has been initialized.
	 */
	private static void waitForInit() {
		if (mInitTask == null) {
			throw new IllegalStateException(
					"Don't forget to initialize the cache before using it by calling initCache.");
		}
		try {
			mInitTask.get();
		} catch (InterruptedException e) {
		} catch (ExecutionException e) {
		}
	}

	public static void clearCache() throws IOException {
		if(mInitTask!=null) {
			waitForInit();
		}
		if(mDiskCache!=null) {
			mDiskCache.delete();
		}
		mDiskCache=null;
		mInitTask=null;
		mMemoryCache=null;
	}

}
