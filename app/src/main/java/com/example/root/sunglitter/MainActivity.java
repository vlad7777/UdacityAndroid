package com.example.root.sunglitter;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.root.sunglitter.sync.SunshineSyncAdapter;


public class MainActivity extends ActionBarActivity implements ForecastFragment.Callback {

    public String FORECASTFRAGMENT_TAG = "FORECAST FRAGMENT";
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private static final String LOG_TAG = MainActivity.class.getName();

    private String mLocation;
    private boolean mTwoPane = false;

    @Override
    public void onItemSelected(Uri uri) {
        if (mTwoPane) {
                getSupportFragmentManager().beginTransaction().replace(R.id.weather_detail_container, DetailFragment.createInstance(uri)).commit();
        } else {
                Intent intent = new Intent(this, DetailActivity.class);
                intent.setData(uri);
                startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("MainActivity", "onCreate");
        mLocation = Utility.getPreferredLocation(this);
        if (findViewById(R.id.weather_detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.weather_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG).commit();
            }
        } else {
            mTwoPane = false;
            //this.getSupportActionBar().setElevation(0f);
        }
        ForecastFragment forecastFragment = (ForecastFragment)this.getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
        forecastFragment.setmTwoPanes(mTwoPane);
        SunshineSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.i("MainActivity", "onPause");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.i("MainActivity", "onResume");
        String curLocation = Utility.getPreferredLocation(this);
        Log.i(LOG_TAG, mLocation);
        Log.i(LOG_TAG, curLocation);
        if (curLocation != null && !mLocation.equals(curLocation))
        {
            Log.i(LOG_TAG, "Location change detected");
            ForecastFragment ff = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            ff.updateWeather();
            mLocation = curLocation;
            DetailFragment df = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if (null != df) {
                df.onLocationChanged(mLocation);
            }
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.i("MainActivity", "onStop");
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.i("MainActivity", "onStart");
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.i("MainActivity", "onDestroy");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.test_activity) {
            startActivity(new Intent(this, Test.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
