package be.redkite.fueltracker;

import android.R.string;
//import android.database.sqlite;

public class Logic {
	
	public boolean addNewFill(int year, int month, int day, double odometer, double trip, double volume, boolean fullTank, double price, string notes) {
		boolean success = true;
		double price_litre = price / volume;
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
	    
	    
		
		return success;
	}
}
