package de.marcusschiesser.gallerista.tasks;

import android.os.AsyncTask;
import de.marcusschiesser.gallerista.tasks.resources.ImageResource;
import de.marcusschiesser.gallerista.vo.ImageVO;

public class ImageServiceTask extends
		AsyncTask<String, Void, ImageVO[]> {
			
	private ImageResource mResource;
	
	protected void setImageResource(ImageResource imageResource) {
		mResource = imageResource;
	}

	protected ImageVO[] doInBackground(String... param) {
		return mResource.getImages(param[0]);
	}
	 
}
