package com.example.root.sunglitter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.root.sunglitter.data.WeatherContract;
import com.example.root.sunglitter.service.SunglitterService;
import com.example.root.sunglitter.sync.SunshineSyncAdapter;
import com.example.root.sunglitter.sync.SunshineSyncService;


/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;

    private static final int FORECAST_LOADER = 0;
    private int mPosition = -1;
    private boolean mTwoPanes = false;

    public ForecastFragment() {
    }

    private ForecastAdapter adapter;
    private ListView mListView;
    private static final String LOG_TAG = ForecastFragment.class.getName();

    public interface Callback {
        void onItemSelected(Uri dateUri);
    }

    @Override
    public void onCreate(Bundle s) {
        super.onCreate(s);
        setHasOptionsMenu(true);

        Log.i("onCreate", "menu started");
    }

    public void updateWeather() {
        /*Intent alarmIntent = new Intent(getActivity(), SunglitterService.AlarmReceiver.class);
        alarmIntent.putExtra(SunglitterService.LOCATION_QUERY_EXTRA,
                Utility.getPreferredLocation(getActivity()));
        //getActivity().startService(intent);
        AlarmManager alarmMgr;
        PendingIntent pendingIntent;

        alarmMgr = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent pIntent = new Intent(getActivity(), SunglitterService.AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
        Log.i(LOG_TAG, "Timer Set");
        alarmMgr.set(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() +
                        5 * 1000, pendingIntent);*/
        SunshineSyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        adapter = new ForecastAdapter(getActivity(), null, 0);
        adapter.setTwoPanes(mTwoPanes);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mPosition = ListView.INVALID_POSITION;
        if (savedInstanceState != null)
            mPosition = savedInstanceState.getInt("Position", 0);

        // Get a reference to the ListView, and attach this adapter to it.
        mListView = (ListView) rootView.findViewById(R.id.listview_forecast);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    String locationSetting = Utility.getPreferredLocation(getActivity());
                    Long date = cursor.getLong(COL_WEATHER_DATE);
                    Uri uri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(locationSetting, date);
                    ((Callback) getActivity()).onItemSelected(uri);
                    mPosition = position;
                }
            }
        });
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("Position", mPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        String locationSetting = Utility.getPreferredLocation(getActivity());
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(locationSetting, System.currentTimeMillis());
        Log.i(LOG_TAG, "New loader is being sent to look at " + weatherLocationUri.toString());
        return new CursorLoader(getActivity(), weatherLocationUri, FORECAST_COLUMNS, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.i(LOG_TAG, "A cursor has been received");
        Log.i(LOG_TAG, "" + cursor.getCount());
        adapter.swapCursor(cursor);
        if (mPosition != ListView.INVALID_POSITION) {
            mListView.smoothScrollToPosition(mPosition);
            mListView.setItemChecked(mPosition, true);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("menu", "initialised");
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            Log.i("refresh", "clicked");
            updateWeather();
            return true;
        } else if (id == R.id.action_show_on_map) {
            Log.i("map", "clicked");
            showMap();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showMap() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = sharedPref.getString("location", "Brest,Belarus");
        Uri uri = Uri.parse("geo:0,0?q=" + location);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    public void setmTwoPanes(boolean val) {
        mTwoPanes = val;
        if (this.adapter != null)
            adapter.setTwoPanes(val);
    }

}

