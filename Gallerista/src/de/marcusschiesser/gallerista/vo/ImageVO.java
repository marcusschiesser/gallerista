package de.marcusschiesser.gallerista.vo;

import java.net.URL;

public class ImageVO {
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
