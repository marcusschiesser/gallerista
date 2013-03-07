package de.marcusschiesser.gallerista;

import com.google.analytics.tracking.android.EasyTracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import de.marcusschiesser.gallerista.adapters.ImageAdapter;
import de.marcusschiesser.gallerista.tasks.ImageServiceTask;
import de.marcusschiesser.gallerista.tasks.resources.ImageFlickrResource;
import de.marcusschiesser.gallerista.ui.AppBarFragment;
import de.marcusschiesser.gallerista.ui.AppBarFragment.OnSearchListener;
import de.marcusschiesser.gallerista.ui.ImageViewActivity;
import de.marcusschiesser.gallerista.utils.BitmapCacheUtils;
import de.marcusschiesser.gallerista.utils.ExceptionUtils;
import de.marcusschiesser.gallerista.vo.ImageVO;

/**
 * Main activity of the app which handles the image grid and communicates with
 * the @see {@link AppBarFragment}
 * 
 * @author Marcus
 */
public class Gallerista extends FragmentActivity implements OnSearchListener {

	private GridView mImageGrid;
	private ImageAdapter mImageAdapter;
	private ImageServiceTask mActualTask;
	private String mKeyword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// Initiallize exception handler
		ExceptionUtils.setApplication(getApplication());
		// init cache
		BitmapCacheUtils.initCache(getApplicationContext(),
				getApplicationInfo().packageName);

		mImageGrid = (GridView) findViewById(R.id.main_image_grid);

		mImageGrid
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View v,
							int position, long id) {
						ImageVO image = mImageAdapter.getItem(position);
						Toast.makeText(Gallerista.this, image.getTitle(),
								Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(Gallerista.this,
								ImageViewActivity.class);
						intent.putExtra(ImageViewActivity.EXTRA_SELECTED_IMAGE,
								image);
						intent.putExtra(ImageViewActivity.EXTRA_KEYWORD, mKeyword);
						startActivity(intent);
					}
				});
		mImageAdapter = new ImageAdapter(Gallerista.this);
		mImageGrid.setAdapter(mImageAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	@Override
	public void onSearch(String searchText) {
		if (searchText != null && searchText.trim().length() > 0) {
			final AppBarFragment appBarFragment = (AppBarFragment) getSupportFragmentManager()
					.findFragmentById(R.id.appbar);

			mActualTask = new ImageServiceTask() {
				{
					// TODO: implement other image resources, e.g. for picasa
					// and select the right one
					// dynamically according to user preferences
					init(new ImageFlickrResource());
				}

				@Override
				protected void onPreExecute() {
					appBarFragment.setVisibilityProgressBar(View.VISIBLE);
				}

				@Override
				protected void onPostExecute(ImageVO[] result) {
					if (this == mActualTask) {
						// we only want the result of the last task requested by
						// the user
						super.onPostExecute(result);
						appBarFragment.setVisibilityProgressBar(View.GONE);
						mImageAdapter.setResult(result);
					}
				}
			};

			mActualTask.execute(searchText);
			mKeyword = searchText;
		}
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
