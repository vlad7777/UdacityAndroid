/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.root.sunglitter;

import android.annotation.TargetApi;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.root.sunglitter.data.WeatherContract;

import java.util.Calendar;

public class TestFetchWeatherTask extends AndroidTestCase{
    static final int WEATHER = 100;
    static final int WEATHER_WITH_LOCATION = 101;
    static final int WEATHER_WITH_LOCATION_AND_DATE = 102;
    static final int WEATHER_BEFORE_DATE = 200;
    static final int LOCATION = 300;

    /*
        Students: uncomment testAddLocation after you have written the AddLocation function.
        This test will only run on API level 11 and higher because of a requirement in the
        content provider.
     */
    @TargetApi(11)
    public void testAddLocation() {
        UriMatcher res = new UriMatcher(UriMatcher.NO_MATCH);

        // 2) Use the addURI function to match each of the types.  Use the constants from
        // WeatherContract to help define the types to the UriMatcher.
        res.addURI(WeatherContract.CONTENT_AUTHORITY, WeatherContract.PATH_WEATHER + "/delete/before/#", WEATHER_BEFORE_DATE);
        res.addURI(WeatherContract.CONTENT_AUTHORITY, WeatherContract.PATH_WEATHER, WEATHER);
        res.addURI(WeatherContract.CONTENT_AUTHORITY, WeatherContract.PATH_WEATHER + "/*" , WEATHER_WITH_LOCATION);
        res.addURI(WeatherContract.CONTENT_AUTHORITY, WeatherContract.PATH_WEATHER + "/*/#", WEATHER_WITH_LOCATION_AND_DATE);
        res.addURI(WeatherContract.CONTENT_AUTHORITY, WeatherContract.PATH_LOCATION, LOCATION);

        //Log.i("AFDJO", WeatherContract.CONTENT_AUTHORITY + "/" + WeatherContract.PATH_WEATHER + "/delete/before/#");
        Uri uri = WeatherContract.WeatherEntry.buildWeatherDeleteWithEndDate(Calendar.getInstance().getTimeInMillis());
        assertEquals(WEATHER_BEFORE_DATE, res.match(uri));

        // 3) Return the new matcher!
    }
}
