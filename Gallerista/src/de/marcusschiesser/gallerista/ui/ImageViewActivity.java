package de.marcusschiesser.gallerista.ui;

import java.io.IOException;

import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Log;

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
import de.marcusschiesser.gallerista.tasks.loader.BitmapWorkerTask;
import de.marcusschiesser.gallerista.tasks.loader.DefaultLoadingCallback;
import de.marcusschiesser.gallerista.tasks.loader.ImageViewLoadingCallback;
import de.marcusschiesser.gallerista.utils.ExceptionUtils;
import de.marcusschiesser.gallerista.vo.ImageVO;

/**
 * Activity to show the selected image in a fullscreen window
 * 
 * @author Marcus
 */
public class ImageViewActivity extends Activity {
	public static final String EXTRA_SELECTED_IMAGE = "SELECTED_IMAGE";
	public static final String EXTRA_KEYWORD = "KEYWORD";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.image_view);
		final Bundle extras = getIntent().getExtras();
		final ImageVO image = (ImageVO) extras
				.getSerializable(EXTRA_SELECTED_IMAGE);
		final String keyword = extras.getString(EXTRA_KEYWORD);
		final ImageView imageView = (ImageView) findViewById(R.id.imageView);
		final Button wallpaperButton = (Button) findViewById(R.id.setWallpaperButton);
		final AdView mAdView = (AdView) findViewById(R.id.ad);
		final AdRequest adRequest = new AdRequest();
		adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
		adRequest.addTestDevice("A6484DCC280FCE68E364A869D6B1C8D8");
		Log.d("Keyword for ad is: " + keyword);
		adRequest.addKeyword(keyword);
		mAdView.loadAd(adRequest);
		wallpaperButton.setVisibility(View.GONE);
		BitmapWorkerTask.loadBitmap(this, image.getURL(),
				new ImageViewLoadingCallback(this, imageView,
						R.drawable.spinner_76_inner_holo) {
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
						new DefaultLoadingCallback() {
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

	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(this); // Add this method.
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this); // Add this method.
	}
}
