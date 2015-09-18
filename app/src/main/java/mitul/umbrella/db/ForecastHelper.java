package mitul.umbrella.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mitul on 18/09/15.
 */
public class ForecastHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "temperatures.db";
    public static final String TABLE_TEMPERATURES = "TEMPERATURES";
    public static final String COLUMN_ID = "_ID";
    public static final String COLUMN_TEMPERATURE = "TEMPERATURE";
    public static final String COLUMN_SUMMARY = "SUMMARY";
    private static final int DB_VERSION = 1;

    private static final String DB_CREATE =
            "CREATE TABLE " + TABLE_TEMPERATURES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TEMPERATURE + " REAL, " +
                    COLUMN_SUMMARY+ " TEXT)";

    public ForecastHelper(Context context) {
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
