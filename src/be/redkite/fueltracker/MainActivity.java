package be.redkite.fueltracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(
					actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_new:
			openNewFill();
			return true;
		case R.id.action_settings:
			openSettings();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void openSettings() {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}

	private void openNewFill() {
		Intent intent = new Intent(this, NewFillActivity.class);
		startActivity(intent);
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return new FillListFragment();
			case 1:
				return new DummySectionFragment();
			case 2:
				return new DummySectionFragment();
			}
			return null;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_fills).toUpperCase(l);
			case 1:
				return getString(R.string.title_monthly).toUpperCase(l);
			case 2:
				return getString(R.string.title_stats).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_dummy, container, false);
			TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(69));
			return rootView;
		}
	}

	public static class FillListFragment extends ListFragment {

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			ArrayList<Map<String, String>> list = Logic.getInstance(getActivity()).getPrettyFills();
			String[] from = { "title", "subtitle" };
			int[] to = { android.R.id.text1, android.R.id.text2 };

			//SimpleAdapter adapter = new SimpleAdapter(getActivity(), list,
			//		android.R.layout.simple_list_item_2, from, to);
			//setListAdapter(adapter);
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			super.onListItemClick(l, v, position, id);
			HashMap<String, String> item = (HashMap<String, String>) getListAdapter().getItem(position);
		    Toast.makeText(getActivity(), item.get("title") + " selected", Toast.LENGTH_LONG).show();
		}
	} 
	/*
	public static class FillListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

		// This is the Adapter being used to display the list's data
		SimpleCursorAdapter mAdapter;

		// These are the Fill rows that we will retrieve
		static final String[] PROJECTION = new String[] {FillReaderContract.FillEntry._ID,
			FillReaderContract.FillEntry.COLUMN_NAME_ODOMETER};

		// This is the select criteria
		//        static final String SELECTION = "((" + 
		//                ContactsContract.Data.DISPLAY_NAME + " NOTNULL) AND (" +
		//                ContactsContract.Data.DISPLAY_NAME + " != '' ))";

		public FillListFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

			// For the cursor adapter, specify which columns go into which views
			String[] fromColumns = {FillReaderContract.FillEntry.COLUMN_NAME_ODOMETER};
			int[] toViews = {android.R.id.text1};

			// Create an empty adapter we will use to display the loaded data.
			// We pass null for the cursor, then update it in onLoadFinished()
			mAdapter = new SimpleCursorAdapter(this, 
					android.R.layout.two_line_list_item, null,
					fromColumns, toViews, 0);

			// Prepare the loader.  Either re-connect with an existing one,
			// or start a new one.
			getLoaderManager().initLoader(0, null, this);

			View rootView = inflater.inflate(R.layout.fragment_fill_list, container, false);
			ListView fillList = (ListView) rootView.findViewById(R.id.fill_list_view);
			fillList.setAdapter(mAdapter);

			return  rootView;
		}

		// Called when a new Loader needs to be created
		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			// Now create and return a CursorLoader that will take care of
			// creating a Cursor for the data being displayed.
			return new CursorLoader(this, FillReaderContract.FillEntry._ID,
					PROJECTION, SELECTION, null, null);
		}

		// Called when a previously created loader has finished loading
		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			// Swap the new cursor in.  (The framework will take care of closing the
			// old cursor once we return.)
			mAdapter.swapCursor(data);
		}

		// Called when a previously created loader is reset, making the data unavailable
		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
			// This is called when the last Cursor provided to onLoadFinished()
			// above is about to be closed.  We need to make sure we are no
			// longer using it.
			mAdapter.swapCursor(null);			
		}
	}*/

}
