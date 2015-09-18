package mitul.umbrella;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;

import mitul.umbrella.db.ForecastDataSource;


public class MainActivity extends Activity {
    protected ForecastDataSource mDataSource;
    public static final String TAG = MainActivity.class.getSimpleName();
    EditText movie;
    private CurrentWeather mCurrentWeather;
    TextView  mTemperatureLabel;
    TextView mTimeLabel;
    TextView mHumidityValue;
    TextView  mPrecipValue;
    TextView  mSummaryLabel;
    ImageView mIconImageView;
    Button insertButton;
    Button selectButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDataSource = new ForecastDataSource(MainActivity.this);


        double latitude = 37.8136;
        double longitude = 144.9631;
        getActionBar().hide();
        String apiKey = getString(R.string.api_key);
        String forecastUrl = getString(R.string.forecast_url) + apiKey + "/" + latitude + "," + longitude;
        mTemperatureLabel =(TextView) findViewById(R.id.temperatureLabel);
        mTimeLabel = (TextView) findViewById(R.id.timeLabel);
        mHumidityValue = (TextView) findViewById(R.id.humidityValue);
        mPrecipValue = (TextView) findViewById(R.id.precipValue);
        mSummaryLabel = (TextView) findViewById(R.id.summaryLabel);
        mIconImageView = (ImageView) findViewById(R.id.iconImageView);
        insertButton = (Button) findViewById(R.id.insertButton);
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadForecastData();
            }
        });
        selectButton = (Button) findViewById(R.id.selectButton);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ViewForecastActivity.class));
            }
        });
        //String movie_name = "We Are Your Friends";
        //String movieUrl = "http://www.omdbapi.com/?t=" + movie_name + "&y=&plot=full&r=json";
        check_network(forecastUrl);
    }

    private void loadForecastData() {
        Log.v(TAG,"-------Trying----------");
        mDataSource.inserForecast(mCurrentWeather);
        Log.v(TAG, "-------Trying----------");
    }

    @Override
    protected void onResume(){
        super.onResume();
        try {
            mDataSource.open();
            Log.v(TAG,"---------- Database succesfully created -------------");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    @Override
    protected void onPause(){
        super.onPause();
        mDataSource.close();
    }
    private void check_network(String forecastUrl) {

        if (isNetworkAvailable()) {
            OkHttpClient client = new OkHttpClient();
            //Request request = new Request.Builder().url(forecastUrl).build();
            Request request = new Request.Builder().url(forecastUrl).build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }
                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        String data = response.body().string();
                        Log.v(TAG, data);
                        if (response.isSuccessful()) {
                            mCurrentWeather = getCurrentDetails(data);
                            runOnUiThread(new Runnable() {
                                @Override
                               public void run() {updateDisplay();
                                }
                           });

                        } else {
                            Log.e(TAG,"Something went wrong");
                        }
                    }
                    catch (IOException e) {
                        Log.e(TAG, "Exception caught ", e);
                    }
                    catch (JSONException e){
                        Log.e(TAG, "Exception caught ", e);
                    }

                }
            });
           // String u = "http://www.keenthemes.com/preview/metronic/theme/assets/global/plugins/jcrop/demos/demo_files/image1.jpg";
           // Picasso.with(MainActivity.this).load(u).into((ImageView)findViewById(R.id.imageView));
        }
        else{
            //Toast.makeText(this,"Network not available",Toast.LENGTH_LONG);
            Log.v(TAG, "Network not available");
        }
    }

    private CurrentWeather getCurrentDetails(String data) throws JSONException {
        JSONObject forecast = new JSONObject(data);
        String timezone = forecast.getString("timezone");
        Log.i(TAG, "From JSON: " + timezone);
        JSONObject currently = forecast.getJSONObject("currently");
        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setHumidity(currently.getDouble("humidity"));
        currentWeather.setTime(currently.getLong("time"));
        currentWeather.setIcon(currently.getString("icon"));
        currentWeather.setPrecipChance(currently.getDouble("precipProbability"));
        currentWeather.setSummary(currently.getString("summary"));
        currentWeather.setTemperature(currently.getDouble("temperature"));
        currentWeather.setTimeZone(timezone);
        Log.d(TAG, currentWeather.getFormattedTime());
        return currentWeather;
    }

    private void updateDisplay() {
        mTemperatureLabel.setText(mCurrentWeather.getTemperature()+ "");
        mTimeLabel.setText("At " +mCurrentWeather.getFormattedTime() + " it will be");
        mHumidityValue.setText(mCurrentWeather.getHumidity() + "");
        mPrecipValue.setText(mCurrentWeather.getPrecipChance() + "%");
        mSummaryLabel.setText(mCurrentWeather.getSummary());
        Drawable drawable = getResources().getDrawable(mCurrentWeather.getIconId());
        mIconImageView.setImageDrawable(drawable);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            return true;
        }
        else{
            return false;
        }
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
