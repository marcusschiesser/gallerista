package de.marcusschiesser.gallerista.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import de.marcusschiesser.gallerista.R;
import de.marcusschiesser.gallerista.tasks.BitmapWorkerTask;
import de.marcusschiesser.gallerista.vo.ImageVO;

/**
 * Activity to show the selected image in a fullscreen window
 * 
 * @author Marcus
 */
public class ImageViewActivity extends Activity {
	public static final String EXTRA_SELECTED_IMAGE = "SELECTED_IMAGE";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.image_view);
		Bundle extras = getIntent().getExtras();
		ImageVO image = (ImageVO) extras.getSerializable(EXTRA_SELECTED_IMAGE);
		ImageView imageView = (ImageView) findViewById(R.id.imageView);
		BitmapWorkerTask.loadBitmap(this, image.getURL(), imageView);
	}
}
