package com.example.radioplayer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.radioplayer.RadioPlayerApplication;
import com.example.radioplayer.event.CategoryThreadCompletionEvent;
import com.example.radioplayer.event.DataModelUpdateEvent;
import com.example.radioplayer.model.Category;
import com.example.radioplayer.network.CategoryThread;
import com.example.radioplayer.util.Utils;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class CategoryDataFragment extends BaseFragment{

    public static final String CATEGORY_DATA_FRAGMENT_TAG = "category_data_fragment";
    private List<Category> mCategoryList = new ArrayList<>();
    private boolean mIsStarted = false;

    public CategoryDataFragment() {}

    public static CategoryDataFragment newInstance() {
        return new CategoryDataFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        // check that there is network connectivity and that the thread is not already running
        if(Utils.isClientConnected(getActivity())) {
            if(!mIsStarted) {
                mIsStarted = true;
                new CategoryThread("CategoryThread", getActivity()).start();
            }
        }
        // TODO post a message to the user, not connected
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    // return the data model set
    public ArrayList<Category> getCategoryData() {
        return new ArrayList<>(mCategoryList);
    }

    // return an individual data item to the caller
    public Category getCategoryDataItem(int position) {
        return mCategoryList.get(position);
    }


    @Subscribe
    public void getCategoryList(CategoryThreadCompletionEvent event) {
        mIsStarted = false; // thread complete
        mCategoryList = event.getCategoryList();
        Timber.i("Category data set updated, size: %d", mCategoryList.size());
        RadioPlayerApplication
                .postToBus(new DataModelUpdateEvent(DataModelUpdateEvent.CATEGORY_MODEL_DATA));
    }

}
