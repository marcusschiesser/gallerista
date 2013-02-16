package de.marcusschiesser.gallerista.vo;

import java.io.Serializable;
import java.net.URL;

public class ImageVO implements Serializable {
	private static final long serialVersionUID = 8594894128306553971L;
	private URL mURL;
	private String mTitle;
	
	public ImageVO(URL URL, String title) {
		super();
		this.mURL = URL;
		this.mTitle = title;
	}

	public URL getURL() {
		return mURL;
	}

	public String getTitle() {
		return mTitle;
	}
	
}
