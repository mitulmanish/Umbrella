package mitul.umbrella;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;

import mitul.umbrella.db.ForecastDataSource;
import mitul.umbrella.db.ForecastHelper;


public class ViewForecastActivity extends Activity {
    protected ForecastDataSource mDataSource;
    protected ArrayList<Double> mTemperatures;
    protected ArrayList<String> mSummary;
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_forecast);
        mDataSource = new ForecastDataSource(ViewForecastActivity.this);
        mListView = (ListView) findViewById(R.id.list1);
        mTemperatures = new ArrayList<Double>();
        mSummary = new ArrayList<String>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mDataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Cursor cursor = mDataSource.selectAllTemperatures();
        updateList(cursor);
    }

    private void updateList(Cursor cursor) {
        mTemperatures.clear();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            int i = cursor.getColumnIndex(ForecastHelper.COLUMN_TEMPERATURE);
            mTemperatures.add(cursor.getDouble(i));
            mSummary.add(cursor.getString(cursor.getColumnIndex(ForecastHelper.COLUMN_SUMMARY)));
            Toast.makeText(ViewForecastActivity.this,
                    String.valueOf(cursor.getColumnIndex(ForecastHelper.COLUMN_SUMMARY)),
                            Toast.LENGTH_LONG).show();
            cursor.moveToNext();
        }
        //ArrayAdapter<Double> adapter = new ArrayAdapter<Double>(ViewForecastActivity.this,
               // android.R.layout.simple_list_item_1,
                //mTemperatures);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ViewForecastActivity.this,
                android.R.layout.simple_list_item_1,
                mSummary);

        mListView.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDataSource.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_forecast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
