package com.example.radioplayer.network;

import android.content.Context;
import android.net.Uri;

import com.example.radioplayer.R;
import com.example.radioplayer.RadioPlayerApplication;
import com.example.radioplayer.data.StationDataCache;
import com.example.radioplayer.event.MessageEvent;
import com.example.radioplayer.event.StationThreadCompletionEvent;
import com.example.radioplayer.model.Station;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

public class StationThread extends Thread{

    private static final String BASE_URL = "http://api.dirble.com/v2/category/";
    private static final String QUERY = "/stations?";
    private static final String PAGE_PARAM = "page";
    private static final String RESULTS_PER_PAGE = "per_page";
    private static final String TOKEN_PARAM = "token";

    private Context mContext;
    private Long mCategoryId;
    private int mPage = 1;
    private int mResultsPerPage = 20;

    // build the url so that you can pass in the category id and the page number
    // private static final String STATION_URL =
    //    "http://api.dirble.com/v2/category/5/stations?page=1&per_page=2&token=xxxx-xxxxxx-xxxxxx";

    // TODO pagination
    public StationThread(String threadName, Context context, Long categoryId) {
        super(threadName);
        mContext = context;
        mCategoryId = categoryId;
    }

    @Override
    public void run() {
        Timber.i("Executing station thread");

        HttpURLConnection con = null;
        URL url = null;

        // build station uri
        String token = mContext.getResources().getString(R.string.dirble_api_key);
        Uri stationUri = Uri.parse(BASE_URL + mCategoryId + QUERY).buildUpon()
                .appendQueryParameter(PAGE_PARAM, String.valueOf(mPage))
                .appendQueryParameter(RESULTS_PER_PAGE, String.valueOf(mResultsPerPage))
                .appendQueryParameter(TOKEN_PARAM, token)
                .build();

        try {
            url = new URL(stationUri.toString());
            Timber.i("Url: %s", url);
            con = (HttpURLConnection) url.openConnection();
            InputStream in = con.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            Station[] data = new Gson().fromJson(reader, Station[].class);
            if(data != null) {
                List<Station> stationList = Arrays.asList(data);
                Timber.i("Station list: %s", stationList.toString());

                // stash the station list in the data cache
                StationDataCache.getStationDataCache().setStationList(new ArrayList<>(stationList));
                // let the station fragment know the station list has been updated
                RadioPlayerApplication.postToBus(new StationThreadCompletionEvent(true));

                //RadioPlayerApplication.postToBus(new StationThreadCompletionEvent(stationList));
            } else {
                Timber.i("No results received from remote server");
                // post message to bus - display snackbar to user
                RadioPlayerApplication.postToBus(new MessageEvent("No results available"));
            }

            reader.close();

        } catch (MalformedURLException e) {
            Timber.e("Malformed url: %s", e.getMessage());
        } catch (IOException e) {
            Timber.e("Connection failure: %s", e.getMessage());
        } finally {
            if(con != null) {
                con.disconnect();
            }
        }

    }
}
