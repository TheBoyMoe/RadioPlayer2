package com.example.radioplayer.network;

import android.content.Context;
import android.util.Log;

import com.example.radioplayer.R;
import com.example.radioplayer.model.Category;
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

        HttpURLConnection con = null;

        try {
            // connect to the remote server and download the stream
            String token = mContext.getResources().getString(R.string.dirble_api_key);
            URL url = new URL(BASE_URL + token);
            con = (HttpURLConnection) url.openConnection();
            InputStream is = con.getInputStream();
            BufferedReader reader =  new BufferedReader(new InputStreamReader(is));

            // use gson to parse the json and instantiate the object collection
            Category[] array = new Gson().fromJson(reader, Category[].class);
            List<Category> categories = new ArrayList<>(Arrays.asList(array));
            Log.i("DEBUG", "Category list: " + categories);

            reader.close();

        } catch (MalformedURLException e) {
            Timber.e("Malformed url: %s", e.getMessage());
        } catch (IOException e) {
            Timber.e("Connection failure: %s", e.getMessage());
        } finally {
            if(con != null)
                con.disconnect();
        }

    }
}
