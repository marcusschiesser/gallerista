package de.marcusschiesser.gallerista;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AppBarFragment extends Fragment {
	private enum ViewState {
		MENU, SEARCH
	}

	public interface OnSearchListener {
		public void onSearch(String searchText);
	}

	private static final String SEARCH_TEXT = "SEARCH_TEXT";

	private EditText mSearchText;
	private OnSearchListener mListener;
	private ProgressBar mProgressBar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.app_bar, container, false);
	}
	
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSearchListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnSearchListener");
        }
    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mSearchText = (EditText) getActivity().findViewById(R.id.searchText);
		ImageButton searchButton = (ImageButton) getActivity().findViewById(
				R.id.searchButton);
		mProgressBar = (ProgressBar) getActivity().findViewById(R.id.appbar_search_progressBar);
		bindSearchListeners(mSearchText);
		bindButtonListeners(searchButton);
		setVisibilityProgressBar(View.GONE);

		// restore last state of fragment, if available
		String searchText = (savedInstanceState == null) ? null
				: (String) savedInstanceState
						.getString(SEARCH_TEXT);
		if(searchText!=null) {
			mSearchText.setText(searchText);
		} else {
			// Come on, who doesn't like some fresh fruits?
			mSearchText.setText("fruits");
		}
		updateList();
	}
	
    @Override
	public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SEARCH_TEXT, mSearchText.getText().toString());
    }
    
	private void bindButtonListeners(ImageButton searchButton) {
		searchButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setViewState(ViewState.SEARCH);
			}
		});
		ImageButton backButton = (ImageButton) getActivity().findViewById(
				R.id.backButton);
		backButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setViewState(ViewState.MENU);
			}
		});
	}

	private void bindSearchListeners(final EditText searchText) {
		searchText.addTextChangedListener(new TextWatcher() {
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
		searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			    if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
		            InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		            in.hideSoftInputFromWindow(searchText.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
		         }
				return false;
			}
		});
	};

	private void updateList() {
		mListener.onSearch(mSearchText.getText().toString());
	}
	
	public void setVisibilityProgressBar(int v) {
		mProgressBar.setVisibility(v);
	}

	private void setViewState(ViewState menu) {
		View menuView = getView().findViewById(R.id.layoutStateMenu);
		View searchView = getView().findViewById(R.id.layoutStateSearch);
		menuView.setVisibility(menu == ViewState.MENU ? View.VISIBLE
				: View.GONE);
		searchView.setVisibility(menu == ViewState.SEARCH ? View.VISIBLE
				: View.GONE);
	}

}
