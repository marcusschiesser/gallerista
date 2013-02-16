package de.marcusschiesser.gallerista;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import de.marcusschiesser.gallerista.AppBarFragment.OnSearchListener;
import de.marcusschiesser.gallerista.adapters.ImageAdapter;
import de.marcusschiesser.gallerista.tasks.ImageServiceTask;
import de.marcusschiesser.gallerista.vo.ImageVO;

public class Gallerista extends FragmentActivity implements OnSearchListener {

	private GridView mImageGrid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mImageGrid = (GridView) findViewById(R.id.main_image_grid);

		mImageGrid
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View v,
							int position, long id) {
						Toast.makeText(Gallerista.this, "" + position,
								Toast.LENGTH_SHORT).show();
					}
				});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gallerista, menu);
		return true;
	}

	@Override
	public void onSearch(String searchText) {
		if (searchText!=null && searchText.trim().length() > 0) {
			ImageServiceTask task = new ImageServiceTask() {
				@Override
				protected void onPreExecute() {
				}

				@Override
				protected void onPostExecute(ImageVO[] result) {
					if (result != null) {
						mImageGrid.setAdapter(new ImageAdapter(Gallerista.this,
								result));
					} else {
						mImageGrid.setAdapter(null);
					}
				}
			};

			task.execute(searchText);
		}
	}

}
