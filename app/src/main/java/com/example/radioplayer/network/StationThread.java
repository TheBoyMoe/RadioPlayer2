package com.example.radioplayer.network;

import android.content.Context;
import android.net.Uri;

import com.example.radioplayer.R;
import com.example.radioplayer.RadioPlayerApplication;
import com.example.radioplayer.data.StationDataCache;
import com.example.radioplayer.event.MessageEvent;
import com.example.radioplayer.event.StationThreadCompletionEvent;
import com.example.radioplayer.model.Station;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.LinkedList;

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

    // build the url so that you can pass in the category id and the page number
    // private static final String STATION_URL =
    //    "http://api.dirble.com/v2/category/5/stations?page=1&per_page=2&token=xxxx-xxxxxx-xxxxxx";

    public StationThread(String threadName, Context context, Long categoryId, int pageNumber) {
        super(threadName);
        mContext = context;
        mCategoryId = categoryId;
        mPage = pageNumber;
    }

    @Override
    public void run() {
        Timber.i("Executing station thread");
        int resultsPerPage = 20;

        // build station uri
        String token = mContext.getResources().getString(R.string.dirble_api_key);
        Uri stationUri = Uri.parse(BASE_URL + mCategoryId + QUERY).buildUpon()
                .appendQueryParameter(PAGE_PARAM, String.valueOf(mPage))
                .appendQueryParameter(RESULTS_PER_PAGE, String.valueOf(resultsPerPage))
                .appendQueryParameter(TOKEN_PARAM, token)
                .build();
        Timber.i("Url: %s", stationUri);

        try {
            OkHttpClient client = new OkHttpClient();
            client.networkInterceptors().add(new StethoInterceptor()); // intercept network traffic
            Request request = new Request.Builder().url(stationUri.toString()).build();
            Response response = client.newCall(request).execute();

            if(response.isSuccessful()) {
                Reader in = response.body().charStream();
                BufferedReader reader =  new BufferedReader(in);

                Station[] data = new Gson().fromJson(reader, Station[].class);
                if(data != null) {

                    if(data.length == 0 && mPage > 1) {
                        Timber.i("END OF THE LINE!!!");
                        RadioPlayerApplication.postToBus(new StationThreadCompletionEvent(true, true));
                    } else {
                        // stash the station list in the data cache
                        StationDataCache.getStationDataCache().setStationList(new LinkedList<>(Arrays.asList(data)));
                        // let the station fragment know the station list has been updated
                        RadioPlayerApplication.postToBus(new StationThreadCompletionEvent(true, false));
                    }

                } else {
                    Timber.i("No results received from remote server");
                    // post message to bus - display snackbar to user
                    RadioPlayerApplication.postToBus(new MessageEvent("No results available"));
                }
                reader.close();
            } else {
                Timber.e("Http response: %s", response.toString());
            }

        } catch (Exception e) {
            Timber.e("Exception parsing json: %s", e.getMessage());
        }

    }
}
