package de.marcusschiesser.gallerista.utils;

import java.io.IOException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageUtils {

	public static Bitmap readBitmapFromURL(URL url) throws IOException {
		return BitmapFactory.decodeStream(url
			.openConnection().getInputStream());
	}

}
