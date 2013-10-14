package be.redkite.fueltracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import be.redkite.fueltracker.FillContract.FillEntry;

public class FillsOpenHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "Fills.db";    
	public static final String SQL_CREATE_ENTRIES =
			"CREATE TABLE " + FillEntry.TABLE_NAME + " (" +
					FillEntry.COLUMN_NAME_DATE + " TEXT, " +
					FillEntry.COLUMN_NAME_ODOMETER + " TEXT, " +
					FillEntry.COLUMN_NAME_TRIP + " TEXT, " +
					FillEntry.COLUMN_NAME_VOLUME + " TEXT, " +
					FillEntry.COLUMN_NAME_FULLTANK + " BOOL, " +
					FillEntry.COLUMN_NAME_CONSO100 + " TEXT, " +
					FillEntry.COLUMN_NAME_PRICE + " TEXT, " +
					FillEntry.COLUMN_NAME_PRICEVOLUME + " TEXT, " +
					FillEntry.COLUMN_NAME_NOTE + " TEXT);";
	public static final String SQL_DELETE_ENTRIES =
			"DROP TABLE IF EXISTS " + FillEntry.TABLE_NAME;

	public FillsOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ENTRIES);
		System.out.println("ARG3");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// This database is only a cache for online data, so its upgrade policy is
		// to simply to discard the data and start over
		db.execSQL(SQL_DELETE_ENTRIES);
		onCreate(db);
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}
}
