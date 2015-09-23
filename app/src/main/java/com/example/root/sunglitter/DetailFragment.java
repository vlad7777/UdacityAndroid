package com.example.root.sunglitter;

import android.content.Intent;
import android.database.Cursor;
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

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = DetailFragment.class.getName();

    private static final int DETAIL_LOADER = 1;

    private Uri mForecastUri;

    private TextView mTextView;
    private ViewHolder viewHolder;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            TextView text = (TextView) rootView.findViewById(R.id.TextViewDetail);
            if (text == null)
            {
                Log.e("TextView", "not found");
            }
            else {
                mTextView = text;
            }
        }
        if (this == null)
            Log.e(LOG_TAG, "FRIGGIN' NULL");
        mForecastUri = Uri.parse(intent.getDataString());
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        viewHolder = new ViewHolder(rootView);
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        return new CursorLoader(this.getActivity(), mForecastUri, ForecastAdapter.FORECAST_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        viewHolder.descriptionView.setText(cursor.getString(ForecastAdapter.COL_WEATHER_DESC));
        viewHolder.dateView.setText(cursor.getString(ForecastAdapter.COL_WEATHER_DATE));
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
