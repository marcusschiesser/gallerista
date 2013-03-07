package de.marcusschiesser.gallerista.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import de.marcusschiesser.gallerista.R;
import de.marcusschiesser.gallerista.tasks.BitmapWorkerTask;
import de.marcusschiesser.gallerista.vo.ImageVO;

/**
 * ImageAdapter for a GridView that shows the images in the provided
 * array of ImageVO values. Images are loaded using the 
 * @see BitmapWorkerTask
 * 
 * @author Marcus
 */
public class ImageAdapter extends BaseAdapter implements ListAdapter {
	private Context mContext;
	private ImageVO[] mImages;
	private int mColumnWidth;

	public ImageAdapter(Context c, ImageVO[] images) {
		mContext = c;
		mImages = images;
		final Resources resources = mContext.getResources();
		final int columns = resources.getInteger(R.integer.nr_columns);
		mColumnWidth = resources.getDisplayMetrics().widthPixels / columns;
	}

	public int getCount() {
		return mImages.length;
	}

	public ImageVO getItem(int position) {
		return mImages[position];
	}

	public long getItemId(int position) {
		return 0;
	}

	// create a new ImageView for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {
		final ImageView imageView;
		if (convertView == null) { // if it's not recycled, initialize some
									// attributes
			imageView = new ImageView(mContext);
			imageView.setLayoutParams(new GridView.LayoutParams(mColumnWidth, mColumnWidth));
		} else {
			imageView = (ImageView) convertView;
		}
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.clearAnimation();

		ImageVO image = mImages[position];
		BitmapWorkerTask.loadBitmap(mContext, image.getThumbnailURL(), imageView);
		
		return imageView;
	}

}
