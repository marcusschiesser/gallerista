package de.marcusschiesser.gallerista;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

public class AppBarFragment extends Fragment {
	private enum ViewState {
		MENU, SEARCH
	}

	public interface OnSearchListener {
		public void onSearch(String searchText);
	}

	private EditText mSearchText;
	private OnSearchListener mListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.appbar, container, false);
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
		ImageButton searchButton = (ImageButton) getActivity().findViewById(
				R.id.searchButton);
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

		mSearchText.setText("fruits");
		updateList();
	};

	private void updateList() {
		mListener.onSearch(mSearchText.getText().toString());
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
