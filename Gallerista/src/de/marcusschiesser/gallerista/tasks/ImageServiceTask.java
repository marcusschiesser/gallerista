package de.marcusschiesser.gallerista.tasks;

import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;
import de.marcusschiesser.gallerista.R;
import de.marcusschiesser.gallerista.tasks.resources.ImageResource;
import de.marcusschiesser.gallerista.utils.ExceptionUtils;
import de.marcusschiesser.gallerista.vo.ImageVO;

/**
 * AsyncTask that uses the provided ImageResource
 * to perform a query for images. The images are returned as array of ImageVO values.
 * 
 * @author Marcus
 */
public class ImageServiceTask extends AsyncTask<String, Void, ImageVO[]> {

	private ImageResource mResource;
	private Context mContext;
	private Throwable mException = null;

	protected void init(ImageResource imageResource, Context ctx) {
		mResource = imageResource;
		mContext = ctx;
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
			String msg = mContext.getResources().getString(
					R.string.error_calling_flickr);
			ExceptionUtils.handleException(mContext, mException, msg);
		}
	}

}
