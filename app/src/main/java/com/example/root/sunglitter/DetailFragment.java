package com.example.root.sunglitter;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.root.sunglitter.data.WeatherContract;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = DetailFragment.class.getName();

    public static DetailFragment createInstance(Uri uri) {
        DetailFragment ndf = new DetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("URI", uri.toString());
        ndf.setArguments(bundle);
        return ndf;
    }

    private static final int DETAIL_LOADER = 1;

    private Uri mForecastUri;

    private ViewHolder viewHolder;

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
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_WEATHER_CONDITION_ID = 5;
    static final int COL_WIND_SPEED = 6;
    static final int COL_WIND_DIRECTION = 7;
    static final int COL_HUMIDITY = 8;
    static final int COL_PRESSURE = 9;

    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().getString("URI") != null)
            mForecastUri = Uri.parse(getArguments().getString("URI"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        viewHolder = new ViewHolder(rootView);
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        return rootView;
    }

    public void onLocationChanged(String location) {
        Uri uri = mForecastUri;
        if (null != uri) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(location, date);
            mForecastUri = updatedUri;
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        if (mForecastUri == null)
            return null;
        return new CursorLoader(this.getActivity(), mForecastUri, FORECAST_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursor.moveToFirst();
        if (cursor.isAfterLast())
            Log.e(LOG_TAG, "Cursor requested for details but got nothing");
        else {
            viewHolder.descriptionView.setText(cursor.getString(COL_WEATHER_DESC));
            viewHolder.dateView.setText(Utility.formatDate(cursor.getLong(COL_WEATHER_DATE)));
            viewHolder.lowTempView.setText(Utility.formatTemperature(getContext(), cursor.getDouble(COL_WEATHER_MIN_TEMP)));
            viewHolder.highTempView.setText(Utility.formatTemperature(getContext(), cursor.getDouble(COL_WEATHER_MAX_TEMP)));
            viewHolder.windView.setText(Utility.getFormattedWind(getContext(), cursor.getFloat(COL_WIND_SPEED), cursor.getFloat(COL_WIND_DIRECTION)));
            viewHolder.pressureView.setText(getContext().getString(R.string.format_pressure, cursor.getFloat(COL_PRESSURE)));
            viewHolder.humidityView.setText(getContext().getString(R.string.format_humidity, cursor.getFloat(COL_HUMIDITY)));
            viewHolder.iconView.setImageBitmap(BitmapFactory.decodeResource(getResources(),Utility.getArtResourceForWeatherCondition(cursor.getInt(COL_WEATHER_CONDITION_ID))));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public static class ViewHolder {
        public final TextView dateView;
        public final TextView highTempView;
        public final TextView lowTempView;
        public final TextView humidityView;
        public final TextView pressureView;
        public final TextView windView;
        public final TextView descriptionView;
        public final ImageView iconView;

        public ViewHolder(View view) {
            dateView = (TextView) view.findViewById(R.id.detail_date_textview);
            highTempView = (TextView) view.findViewById(R.id.detail_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.detail_low_textview);
            humidityView = (TextView) view.findViewById(R.id.detail_humidity_textview);
            pressureView = (TextView) view.findViewById(R.id.detail_pressure_textview);
            windView = (TextView) view.findViewById(R.id.detail_wind_textview);
            descriptionView = (TextView) view.findViewById(R.id.detail_description_textview);
            iconView = (ImageView) view.findViewById(R.id.detail_icon);
        }
    }
}
