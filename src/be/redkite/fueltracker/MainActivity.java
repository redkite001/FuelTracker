package be.redkite.fueltracker;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import be.redkite.fueltracker.FillContract.FillEntry;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener, EraseDBDialogFragment.OnDialogAcceptedListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;
	FillListFragment m_fillListFragment;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	static final int ADD_NEW_FILL_REQUEST = 1;

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
		case R.id.action_cleardb:
			clearDB();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ADD_NEW_FILL_REQUEST) {
			if (resultCode == RESULT_OK) {
				refreshData();
			}
		}
	}

	private void openNewFill() {
		Intent intent = new Intent(this, NewFillActivity.class);
		startActivityForResult(intent, ADD_NEW_FILL_REQUEST);
	}	

	private void openSettings() {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}

	private void clearDB() {
		DialogFragment newFragment = new EraseDBDialogFragment();
		newFragment.show(getSupportFragmentManager(), "cleardb");
	}

	public void onDialogAcceptedListener() {
		refreshData();
	}

	private void refreshData() {
		m_fillListFragment.refresh();
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
				return m_fillListFragment = new FillListFragment();
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

		public static SimpleCursorAdapter m_adapter;
		public static Cursor m_cursor;

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			m_cursor = Logic.getInstance(getActivity()).getFills();
			String[] from = { "odometer", "price" };
			int[] to = { android.R.id.text1, android.R.id.text2 };

			m_adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_2, m_cursor, from, to);
			m_adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
				public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
					if (view.getId() == android.R.id.text1) {
						
						String title = cursor.getString(cursor.getColumnIndexOrThrow(FillEntry.COLUMN_NAME_DATE))
								+ " - "
								+ cursor.getString(cursor.getColumnIndexOrThrow(FillEntry.COLUMN_NAME_PRICE)) + " €";

						((TextView)view).setText(title);
						return true;

					} else if (view.getId() == android.R.id.text2){
						double conso_100 = UtilClass.parseDoubleSafely(cursor.getString(cursor.getColumnIndexOrThrow(FillEntry.COLUMN_NAME_CONSO100)));
						DecimalFormat df = new DecimalFormat("#.##");
						String conso_100_str = df.format(conso_100);
						
						String subtitle = cursor.getString(cursor.getColumnIndexOrThrow(FillEntry.COLUMN_NAME_ODOMETER)) + " km"
								+ " - "
								+ cursor.getString(cursor.getColumnIndexOrThrow(FillEntry.COLUMN_NAME_TRIP)) + " km"
								+ " - "
								+ cursor.getString(cursor.getColumnIndexOrThrow(FillEntry.COLUMN_NAME_VOLUME)) + " l"
								+ " - "
								+ conso_100_str + " l/100";
						
						((TextView)view).setText(subtitle);
						return true;
					}
					return false;}

			});


			setListAdapter(m_adapter);

			registerForContextMenu(getListView());
		}

		@Override
		public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
			super.onCreateContextMenu(menu, v, menuInfo);

			MenuInflater inflater = this.getActivity().getMenuInflater();
			inflater.inflate(R.menu.fill_list_item, menu);
		}

		@Override
		public boolean onContextItemSelected(MenuItem item) {

			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			switch (item.getItemId()) {

			case R.id.action_edit:
				System.out.println("RowID : " + info.id);
				System.out.println("Position : " + info.position);
				return true;

			case R.id.action_delete:
				Logic.getInstance(getActivity()).removeFill(info.id);
				refresh();
				return true;

			default:
				return super.onContextItemSelected(item);
			}
		}

		public void refresh() {
			m_cursor.requery();
		}
	} 
}
