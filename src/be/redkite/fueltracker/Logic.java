package be.redkite.fueltracker;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import be.redkite.fueltracker.FillContract.FillEntry;

public class Logic {

	/***
	 * Singleton part
	 */
	private static Logic instance;

	private Logic(Context context) {
		mDbHelper = new FillsOpenHelper(context);
		db = mDbHelper.getReadableDatabase();
	}

	public static Logic getInstance(Context context) {
		if (instance == null)
			instance = new Logic(context);

		return instance;
	}

	/***
	 * DB manipulation part
	 */
	FillsOpenHelper mDbHelper;
	SQLiteDatabase db;

	public boolean addNewFill(int year, int month, int day, double odometer, double trip, double volume, boolean fullTank, double price, String note) {
		boolean success = true;
		double price_volume = price / volume;
		double conso_100 = 0.0;

		// Handle case when not filling totally the car
		// - If "noFullTank" was checked, we don't fill conso_100 value
		// - Else we check if last fill was a "notFullTank", and if so, we have to summ all previous consecutive "not full tank" fills
		//   and we use this sum with the sum of km made since first "notFullTank" for setting all previous and current conso_100.
		if (!fullTank) conso_100 = 0.0;
		else if (/*isLastFillFull()*/ true) conso_100 = (volume / trip) * 100;
		else {
			/*	        double summOffills = getSumOfLastFillsNotFull();
	        int kmBeforeFirstKmOfConsecutiveNotFullFill = getKmBeforeFirstKmOfConsecutiveNotFullFill();

	        conso_100 = QString::number(((fill.toDouble() + summOffills)/(km - kmBeforeFirstKmOfConsecutiveNotFullFill)) * 100);

	        QSqlQuery query("UPDATE fills SET conso_100=" + conso_100 + " WHERE km>" + QString::number(kmBeforeFirstKmOfConsecutiveNotFullFill));
	        if (!query.exec())
	            qDebug() << query.lastError();*/
		}

		/*		QSqlQuery query1("INSERT INTO fills (date, km, trip, fill, not_full_tank, conso_100, price, price_volume, notes) "
				"VALUES (:date, :km, :trip, :fill, :not_full_tank, :conso_100, :price, :price_volume, :notes)");
		query1.bindValue(":date", QDate(year, month, day).toString(Qt::ISODate));
		query1.bindValue(":km", km);
		query1.bindValue(":trip", trip);
		query1.bindValue(":fill", fill);
		query1.bindValue(":not_full_tank", notFullTank);
		query1.bindValue(":conso_100", conso_100);
		query1.bindValue(":price", price);
		query1.bindValue(":price_volume", price_volume);
		query1.bindValue(":notes", notes);
		if (!query1.exec())
			qDebug() << query1.lastError();*/

		ContentValues values = new ContentValues();
		values.put(FillEntry.COLUMN_NAME_DATE, String.format("%04d%02d%02d", year, month, day));
		values.put(FillEntry.COLUMN_NAME_ODOMETER, Double.toString(odometer));
		values.put(FillEntry.COLUMN_NAME_TRIP, Double.toString(trip));
		values.put(FillEntry.COLUMN_NAME_VOLUME, Double.toString(volume));
		values.put(FillEntry.COLUMN_NAME_FULLTANK, fullTank);
		values.put(FillEntry.COLUMN_NAME_CONSO100, Double.toString(conso_100));
		values.put(FillEntry.COLUMN_NAME_PRICE, Double.toString(price));
		values.put(FillEntry.COLUMN_NAME_PRICEVOLUME, Double.toString(price_volume));
		values.put(FillEntry.COLUMN_NAME_NOTE, note);

		// Insert the new row, returning the primary key value of the new row
		long newRowId;
		newRowId = db.insert(
				FillEntry.TABLE_NAME,
				null,
				values);

		return success;
	}

	public Cursor getFills() {
		String[] projection = {
				"rowid as _id",
				FillEntry.COLUMN_NAME_DATE,
				FillEntry.COLUMN_NAME_PRICE,
				FillEntry.COLUMN_NAME_ODOMETER,
				FillEntry.COLUMN_NAME_TRIP,
				FillEntry.COLUMN_NAME_VOLUME,
				FillEntry.COLUMN_NAME_CONSO100
		};

		String sortOrder =
				FillEntry.COLUMN_NAME_DATE + " DESC";

		Cursor cur = null;
		try {
			cur = db.query(
					FillEntry.TABLE_NAME,                     // The table to query
					projection,                               // The columns to return
					null,                                     // The columns for the WHERE clause
					null,                                     // The values for the WHERE clause
					null,                                     // don't group the rows
					null,                                     // don't filter by row groups
					sortOrder                                 // The sort order
					);
		} catch (SQLiteException e) {
			// TODO Found a better way to handle non-existing table!
			// Table not found... it is most probably empty
			db.execSQL(FillsOpenHelper.SQL_CREATE_ENTRIES);
		}

		return cur;
	}

	public ArrayList<Map<String, String>> getPrettyFills() {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

		try {
			Cursor cur = getFills();
			if (cur != null) {
				cur.moveToFirst();
				while (cur.isAfterLast() == false) {
					double conso_100 = UtilClass.parseDoubleSafely(cur.getString(cur.getColumnIndexOrThrow(FillEntry.COLUMN_NAME_CONSO100)));
					DecimalFormat df = new DecimalFormat("#.##");
					String conso_100_str = df.format(conso_100);

					String title = cur.getString(cur.getColumnIndexOrThrow(FillEntry.COLUMN_NAME_DATE))
							+ " - "
							+ cur.getString(cur.getColumnIndexOrThrow(FillEntry.COLUMN_NAME_PRICE)) + " €";
					String subtitle = cur.getString(cur.getColumnIndexOrThrow(FillEntry.COLUMN_NAME_ODOMETER)) + " km"
							+ " - "
							+ cur.getString(cur.getColumnIndexOrThrow(FillEntry.COLUMN_NAME_TRIP)) + " km"
							+ " - "
							+ cur.getString(cur.getColumnIndexOrThrow(FillEntry.COLUMN_NAME_VOLUME)) + " l"
							+ " - "
							+ conso_100_str + " l/100";

					HashMap<String, String> item = new HashMap<String, String>();
					item.put("_id", Long.toString(cur.getLong(cur.getColumnIndexOrThrow("rowid"))));
					item.put("title", title);
					item.put("subtitle", subtitle);
					list.add(item);

					cur.moveToNext();
				}
				cur.close();
			}
		} catch (SQLiteException e) {
			// TODO Found a better way to handle non-existing table!
			// Table not found... it is most probably empty
			db.execSQL(FillsOpenHelper.SQL_CREATE_ENTRIES);
		}

		return list;
	}

	public void removeFill(long rowid) {
		db.execSQL("DELETE FROM " + FillContract.FillEntry.TABLE_NAME + " WHERE rowID =" + rowid);
		System.out.println(rowid);
	}

	public void clearDB() {
		db.execSQL(FillsOpenHelper.SQL_DELETE_ENTRIES);
	}
}
