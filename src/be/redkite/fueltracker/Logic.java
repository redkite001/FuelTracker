package be.redkite.fueltracker;

import be.redkite.fueltracker.FillReaderContract.FillEntry;
import android.R.string;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class Logic {

	/***
	 * Singleton part
	 */
	private static Logic instance;

	private Logic() {
		db = mDbHelper.getWritableDatabase();
	}

	public static Logic getInstance() {
		if (instance == null)
			instance = new Logic();

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
		values.put(FillEntry.COLUMN_NAME_ODOMETER, Double.toString(trip));
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
}
