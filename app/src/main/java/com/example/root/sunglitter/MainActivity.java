package com.example.root.sunglitter;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    public String FORECASTFRAGMENT_TAG = "FORECAST FRAGMENT";

    private static final String LOG_TAG = MainActivity.class.getName();

    private String mLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("MainActivity", "onCreate");
        mLocation = new String(Utility.getPreferredLocation(this));
        if (savedInstanceState == null) {
            Log.i(LOG_TAG, "Adding framgnet");
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.MainActivityContainer, new ForecastFragment(), FORECASTFRAGMENT_TAG)
                    .commit();
        }
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
        if (!mLocation.equals(curLocation))
        {
            Log.i(LOG_TAG, "Location change detected");
            ForecastFragment ff = (ForecastFragment) getSupportFragmentManager().findFragmentByTag(FORECASTFRAGMENT_TAG);
            ff.onLocationChanged();
            mLocation = curLocation;
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
        }

        return super.onOptionsItemSelected(item);
    }
}
