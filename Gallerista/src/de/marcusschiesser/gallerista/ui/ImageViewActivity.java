package de.marcusschiesser.gallerista.ui;

import java.io.IOException;

import android.app.Activity;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import de.marcusschiesser.gallerista.R;
import de.marcusschiesser.gallerista.tasks.BitmapWorkerTask;
import de.marcusschiesser.gallerista.utils.ExceptionUtils;
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
		final Bundle extras = getIntent().getExtras();
		final ImageVO image = (ImageVO) extras
				.getSerializable(EXTRA_SELECTED_IMAGE);
		final ImageView imageView = (ImageView) findViewById(R.id.imageView);
		final Button wallpaperButton = (Button) findViewById(R.id.setWallpaperButton);
		wallpaperButton.setVisibility(View.GONE);
		BitmapWorkerTask.loadBitmap(this, image.getURL(),
				new BitmapWorkerTask.ImageViewLoadingCallback(this, imageView, R.drawable.spinner_76_inner_holo) {
					@Override
					public void setImageBitmap(Bitmap bitmap) {
						super.setImageBitmap(bitmap);
						if (bitmap != null)
							wallpaperButton.setVisibility(View.VISIBLE);
					}
				});
		wallpaperButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BitmapWorkerTask.loadBitmap(ImageViewActivity.this,
						image.getURL(),
						new BitmapWorkerTask.DefaultLoadingCallback() {
							@Override
							public void setImageBitmap(Bitmap bitmap) {
								if (bitmap != null) {
									WallpaperManager myWallpaperManager = WallpaperManager
											.getInstance(getApplicationContext());
									try {
										myWallpaperManager.setBitmap(bitmap);
										Toast.makeText(
												getApplicationContext(),
												R.string.image_view_wallpaper_changed,
												Toast.LENGTH_LONG).show();
									} catch (IOException e) {
										ExceptionUtils.handleException(e,
												R.string.error_wallpaper);
									}
								}
							}
						});

			}
		});
	}

}
