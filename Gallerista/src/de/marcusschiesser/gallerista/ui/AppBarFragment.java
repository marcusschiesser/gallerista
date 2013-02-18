package de.marcusschiesser.gallerista.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import de.marcusschiesser.gallerista.R;

/**
 * Fragment that takes care about the different states of the app bar.
 * Supports two states: SEARCH and MENU. Search shows a 
 * EditText for typing in a search value whereas MENU displays 
 * the menu buttons and the application title.
 * 
 * @author Marcus
 */
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
			throw new ClassCastException(activity.toString()
					+ " must implement OnSearchListener");
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mSearchText = (EditText) getActivity().findViewById(R.id.searchText);
		ImageButton searchButton = (ImageButton) getActivity().findViewById(
				R.id.searchButton);
		ImageButton backButton = (ImageButton) getActivity().findViewById(
				R.id.backButton);
		ImageButton aboutButton = (ImageButton) getActivity().findViewById(
				R.id.aboutButton);
		mProgressBar = (ProgressBar) getActivity().findViewById(
				R.id.appbar_search_progressBar);

		bindSearchListeners(mSearchText);
		bindButtonListeners(searchButton, backButton, aboutButton);
		setVisibilityProgressBar(View.GONE);

		// restore last state of fragment, if available
		if (savedInstanceState == null) {
			showAboutDialog();
		} else {
			String searchText = (String) savedInstanceState
					.getString(SEARCH_TEXT);
			mSearchText.setText(searchText);
			updateList();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(SEARCH_TEXT, mSearchText.getText().toString());
	}

	private void bindButtonListeners(ImageButton searchButton,
			ImageButton backButton, ImageButton aboutButton) {
		searchButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setViewState(ViewState.SEARCH);
			}
		});
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setViewState(ViewState.MENU);
			}
		});
		aboutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showAboutDialog();
			}

		});
	}

	private void showAboutDialog() {
		DialogFragment aboutDialog = new AboutDialogFragment();
		aboutDialog.show(getFragmentManager(), "dialog");
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
		searchText
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (event != null
								&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
							hideSoftInputKeyboard();
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
		switch (menu) {
		case MENU:
			hideSoftInputKeyboard();
			menuView.setVisibility(View.VISIBLE);
			searchView.setVisibility(View.GONE);
			break;
		case SEARCH:
			menuView.setVisibility(View.GONE);
			searchView.setVisibility(View.VISIBLE);
			break;
		}
	}

	private void hideSoftInputKeyboard() {
		InputMethodManager in = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		in.hideSoftInputFromWindow(mSearchText.getApplicationWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public static class AboutDialogFragment extends DialogFragment {

		public static AboutDialogFragment newInstance(int title) {
			AboutDialogFragment frag = new AboutDialogFragment();
			return frag;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.dialog_about_title);
			builder.setMessage(R.string.dialog_about_message);
			builder.setPositiveButton(R.string.dialog_button_ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
			return builder.create();
		}

	}
}
