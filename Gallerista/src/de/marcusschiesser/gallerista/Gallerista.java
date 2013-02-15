package de.marcusschiesser.gallerista;

import de.marcusschiesser.gallerista.adapters.ImageAdapter;
import de.marcusschiesser.gallerista.tasks.ImageServiceTask;
import de.marcusschiesser.gallerista.vo.ImageVO;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

public class Gallerista extends Activity {

	private enum ViewState {
		MENU, SEARCH
	}

	private EditText mSearchText;
	private GridView mImageGrid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mSearchText = (EditText) findViewById(R.id.searchText);
		mSearchText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				updateList();
			}
		});
		ImageButton searchButton = (ImageButton) findViewById(R.id.searchButton);
		searchButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setViewState(ViewState.SEARCH);
			}
		});
		ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
		backButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setViewState(ViewState.MENU);
			}
		});
		mImageGrid = (GridView) findViewById(R.id.main_image_grid);

		mImageGrid
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View v,
							int position, long id) {
						Toast.makeText(Gallerista.this, "" + position,
								Toast.LENGTH_SHORT).show();
					}
				});
		
		mSearchText.setText("fruits");
		updateList();
	}

	private void setViewState(ViewState menu) {
		View menuView = findViewById(R.id.layoutStateMenu);
		View searchView = findViewById(R.id.layoutStateSearch);
		menuView.setVisibility(menu == ViewState.MENU ? View.VISIBLE
				: View.GONE);
		searchView.setVisibility(menu == ViewState.SEARCH ? View.VISIBLE
				: View.GONE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gallerista, menu);
		return true;
	}

	public void updateList() {
		String start = mSearchText.getText().toString();
		if (start.trim().length() > 0) {
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

			task.execute(start);
		}
	}

}
