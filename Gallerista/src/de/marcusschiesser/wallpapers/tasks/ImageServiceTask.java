package de.marcusschiesser.wallpapers.tasks;

import java.io.IOException;

import android.os.AsyncTask;
import de.marcusschiesser.wallpapers.R;
import de.marcusschiesser.wallpapers.tasks.resources.ImageResource;
import de.marcusschiesser.wallpapers.utils.ExceptionUtils;
import de.marcusschiesser.wallpapers.vo.ImageVO;

/**
 * AsyncTask that uses the provided ImageResource
 * to perform a query for images. The images are returned as array of ImageVO values.
 * 
 * @author Marcus
 */
public class ImageServiceTask extends AsyncTask<String, Void, ImageVO[]> {

	private ImageResource mResource;
	private Throwable mException = null;

	protected void init(ImageResource imageResource) {
		mResource = imageResource;
	}

	@Override
	protected ImageVO[] doInBackground(String... param) {
		try {
			return mResource.getImages(param[0]);
		} catch (IOException e) {
			mException = e;
			return null;
		}
	}

	@Override
	protected void onPostExecute(ImageVO[] result) {
		if (mException != null) {
			ExceptionUtils.handleException(mException, R.string.error_calling_flickr);
		}
	}

}
