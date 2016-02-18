package com.example.radioplayer.network;

import android.content.Context;

import com.example.radioplayer.R;
import com.example.radioplayer.RadioPlayerApplication;
import com.example.radioplayer.event.CategoryThreadCompletionEvent;
import com.example.radioplayer.event.MessageEvent;
import com.example.radioplayer.model.Category;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

public class CategoryThread extends Thread{

    // download the list of primary categories
    // http://api.dirble.com/v2/categories/primary?token=xxxxxxxxxx-xxxxxxx
    private static final String BASE_URL = "http://api.dirble.com/v2/categories/primary?token=";
    private Context mContext;

    public CategoryThread(String threadName, Context context) {
        super(threadName);
        mContext = context;
    }


    @Override
    public void run() {
        Timber.i("Executing category thread");

        try {
            String token = mContext.getResources().getString(R.string.dirble_api_key);

            // connect to the remote server and download the stream using OkHttp client
            OkHttpClient client = new OkHttpClient();
            client.networkInterceptors().add(new StethoInterceptor()); // intercept network traffic
            Request request = new Request.Builder().url(BASE_URL + token).build();
            Response response = client.newCall(request).execute();

            if(response.isSuccessful()) {
                Reader in = response.body().charStream();
                BufferedReader reader =  new BufferedReader(in);

                // use gson to parse the json and instantiate the object collection
                Category[] array = new Gson().fromJson(reader, Category[].class);

                if(array != null) {
                    List<Category> categories = new ArrayList<>(Arrays.asList(array));
                    RadioPlayerApplication.postToBus(new CategoryThreadCompletionEvent(categories));
                    Timber.i("Category list: %s", categories.toString());
                } else {
                    Timber.i("No results received from remote server");
                    // post message to bus - display snackbar to user
                    RadioPlayerApplication.postToBus(new MessageEvent("No results received"));
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
