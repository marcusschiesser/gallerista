package de.marcusschiesser.wallpapers.vo;

import java.io.Serializable;
import java.net.URL;

/**
 * Value object that stores the title, url and url of the thumbnail
 * of an image.
 * It is designed to be platform independent so other
 * backends but Flickr can be used.
 * 
 * @author Marcus
 */
public class ImageVO implements Serializable {
	private static final long serialVersionUID = 8594894128306553971L;
	private URL mThumbnailURL;
	private URL mURL;
	private String mTitle;
	
	public ImageVO(URL url, URL thumbnailURL, String title) {
		super();
		this.mThumbnailURL = thumbnailURL;
		this.mURL = url;
		this.mTitle = title;
	}

	public URL getThumbnailURL() {
		return mThumbnailURL;
	}

	public String getTitle() {
		return mTitle;
	}
	
	public URL getURL() {
		return mURL;
	}

}
