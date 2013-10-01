package be.redkite.fueltracker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import be.redkite.fueltracker.DatePickerFragment.OnDateSelectedListener;

import android.os.Bundle;
import android.app.Activity;
import android.app.DialogFragment;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.support.v4.app.NavUtils;

public class NewFillActivity extends Activity implements OnDateSelectedListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_new_fill);

		// Show the Up button in the action bar.
		setupActionBar();
		
		// Initialize the text of the date picker button
		Calendar c = Calendar.getInstance();
		onDateSelected(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

		// Override OnEditorActionListener of last input field
		EditText editText = (EditText)findViewById(R.id.notes);
		editText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					addNewFill();
					handled = true;
				}
				return handled;
			}
		});
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_fill, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_done:
			addNewFill();
		}
		return super.onOptionsItemSelected(item);
	}

	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getFragmentManager(), "datePicker");
	}

	public void addNewFill() {
	    
		this.finish();
	}

	@Override
	public void onDateSelected(int year, int month, int day) {
		Calendar c = Calendar.getInstance();
		c.set(year, month, day);
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy");
		
		Button button = (Button)findViewById(R.id.date);
		button.setText(dateFormat.format(c.getTime()));
	}
}
