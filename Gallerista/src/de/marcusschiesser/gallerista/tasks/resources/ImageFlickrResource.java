package de.marcusschiesser.gallerista.tasks.resources;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import android.util.Log;
import de.marcusschiesser.gallerista.utils.HttpUtils;
import de.marcusschiesser.gallerista.vo.ImageVO;

/**
 * REST-Client for retrieving public images from flicker by a given text 
 * 
 * TODO: Uses Jacksons Raw-Data-Binding for JSON-Parsing
 * (http://wiki.fasterxml.com/JacksonInFiveMinutes#A.22Raw.22_Data_Binding_Example)
 * Advantage is this class does not have a dependency to Jackson. 
 * Might have an performance impact.
 * This needs to be checked.
 *  
 * @author Marcus
 */
public class ImageFlickrResource implements ImageResource {

	private static final String API_KEY = "621ab6dd6aefd7c44d6837ed6a4eef81";
	private HttpUtils httpUtils;
	
	public ImageFlickrResource() {
		httpUtils = new HttpUtils("api.flickr.com", "services/rest/");
	}
	
	/* (non-Javadoc)
	 * @see de.marcusschiesser.gallerista.tasks.resources.ImageResource#getImages(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ImageVO[] getImages(String text) {
		try {
			Map<String, Object> flickrData = httpUtils.doGet("api_key=" + API_KEY + "&method=flickr.photos.search&format=json&per_page=20&nojsoncallback=1&text=" + text,
					Map.class);
			Map<String, Object> flickrPhotos = (Map<String, Object>) flickrData.get("photos"); 
			List<Object> flickrImages = (List<Object>) flickrPhotos.get("photo");
			ImageVO[] images = new ImageVO[flickrImages.size()];
			int i = 0;
			for(Object obj : flickrImages) {
				final ImageVO imageVO = createImageVO(obj);
				if(imageVO!=null) {
					images[i++] = imageVO;
				}
			}
			return images;
		} catch (IOException e) {
			Log.e(ImageFlickrResource.class.toString(), "i/o error", e);
		}
		return new ImageVO[0];
	}

	@SuppressWarnings("unchecked")
	private ImageVO createImageVO(Object obj) {
		Map<String, Object> imageData = (Map<String, Object>) obj;
		try {
			return new ImageVO(createURL(imageData, 'b'), createURL(imageData, 't'), (String) imageData.get("title"));
		} catch (MalformedURLException e) {
			return null;
		}
	}
	
	private URL createURL(Map<String, Object> imageData, char size) throws MalformedURLException {
		Integer farmId = (Integer) imageData.get("farm");
		String serverId = (String) imageData.get("server");
		String id = (String) imageData.get("id");
		String secret = (String) imageData.get("secret");
		URL url = new URL("http://farm" + farmId + ".staticflickr.com/" + serverId + "/" + id + "_" + secret + "_" + size + ".jpg");
		return url;
	}

}
