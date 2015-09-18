package mitul.umbrella.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

import mitul.umbrella.CurrentWeather;

/**
 * Created by mitul on 18/09/15.
 */
public class ForecastDataSource {
    private SQLiteDatabase mDatabase;
    private ForecastHelper mForecastHelper;
    private Context mContext;

    public ForecastDataSource(Context context){
        mContext = context;
        mForecastHelper = new ForecastHelper(mContext);
    }

    //open
    public void open() throws SQLException{
        mDatabase = mForecastHelper.getWritableDatabase();
    }

    public void close() {
        mDatabase.close();
    }

    //close

    //insert

    public void inserForecast(CurrentWeather forecast){
        mDatabase.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(ForecastHelper.COLUMN_TEMPERATURE, forecast.getTemperature());
            values.put(ForecastHelper.COLUMN_SUMMARY, forecast.getSummary());
            mDatabase.insert(ForecastHelper.TABLE_TEMPERATURES, null, values);
            mDatabase.setTransactionSuccessful();
        }
        finally {
            mDatabase.endTransaction();
        }
    }

    //select
    public Cursor selectAllTemperatures(){
        Cursor cursor = mDatabase.query(
                ForecastHelper.TABLE_TEMPERATURES, // table
                new String[] { ForecastHelper.COLUMN_TEMPERATURE,ForecastHelper.COLUMN_SUMMARY }, // column names
                null, // where clause
                null, // where params
                null, // groupby
                null, // having
                null  // orderby
        );
        return cursor;

    }
    // update

    //delete

}
