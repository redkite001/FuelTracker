package be.redkite.fueltracker;

import android.provider.BaseColumns;

public final class FillReaderContract {
	
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public FillReaderContract() {}

    /* Inner class that defines the table contents */
    public static abstract class FillEntry implements BaseColumns {
        public static final String TABLE_NAME = "fills";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_ODOMETER = "odometer";
        public static final String COLUMN_NAME_TRIP = "trip";
        public static final String COLUMN_NAME_VOLUME = "volume";
        public static final String COLUMN_NAME_FULLTANK = "full_tank";
        public static final String COLUMN_NAME_CONSO100 = "conso_100";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_PRICEVOLUME = "price_volume";
        public static final String COLUMN_NAME_NOTE = "note";
    }
}
