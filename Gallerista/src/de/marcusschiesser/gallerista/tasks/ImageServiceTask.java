package de.marcusschiesser.gallerista.tasks;

import android.os.AsyncTask;
import de.marcusschiesser.gallerista.tasks.resources.ImageFlickrResource;
import de.marcusschiesser.gallerista.tasks.resources.ImageResource;
import de.marcusschiesser.gallerista.vo.ImageVO;

public class ImageServiceTask extends
		AsyncTask<String, Void, ImageVO[]> {
			
	// TODO: implement other image resources, e.g. for picasa and select the right one
	// 		 dynamically according to user preferences
	private ImageResource resource = new ImageFlickrResource();

	protected ImageVO[] doInBackground(String... param) {
		return resource.getImages(param[0]);
	}
	 
}
