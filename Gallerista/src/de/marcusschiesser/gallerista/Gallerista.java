package de.marcusschiesser.gallerista;

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
import de.marcusschiesser.gallerista.vo.ImageVO;

public class Gallerista extends FragmentActivity implements OnSearchListener {

	private GridView mImageGrid;
	private ImageAdapter mImageAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mImageGrid = (GridView) findViewById(R.id.main_image_grid);

		mImageGrid
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View v,
							int position, long id) {
						ImageVO image = mImageAdapter.getItem(position);
						Toast.makeText(Gallerista.this, image.getTitle(),
								Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(Gallerista.this, ImageViewActivity.class);
						intent.putExtra(ImageViewActivity.EXTRA_SELECTED_IMAGE, image);
						startActivity(intent);
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	@Override
	public void onSearch(String searchText) {
		if (searchText!=null && searchText.trim().length() > 0) {
			final AppBarFragment appBarFragment = (AppBarFragment)
	                getSupportFragmentManager().findFragmentById(R.id.appbar);

			ImageServiceTask task = new ImageServiceTask() {
				{
					// TODO: implement other image resources, e.g. for picasa and select the right one
					// 		 dynamically according to user preferences
					setImageResource(new ImageFlickrResource(getApplicationContext()));
				}
				
				@Override
				protected void onPreExecute() {
					appBarFragment.setVisibilityProgressBar(View.VISIBLE);
					mImageGrid.setAdapter(null);
				}

				@Override
				protected void onPostExecute(ImageVO[] result) {
					appBarFragment.setVisibilityProgressBar(View.GONE);
					if (result != null && result.length>0) {
						mImageAdapter = new ImageAdapter(Gallerista.this,
								result);
						mImageGrid.setAdapter(mImageAdapter);
					} else {
						mImageGrid.setAdapter(null);
					}
				}
			};

			task.execute(searchText);
		}
	}

}
