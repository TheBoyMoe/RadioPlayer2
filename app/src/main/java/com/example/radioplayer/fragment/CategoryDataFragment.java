package com.example.radioplayer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.radioplayer.R;
import com.example.radioplayer.RadioPlayerApplication;
import com.example.radioplayer.event.CategoryThreadCompletionEvent;
import com.example.radioplayer.event.DataModelUpdateEvent;
import com.example.radioplayer.event.MessageEvent;
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
        } else {
            // post a message to the user, not connected
            Timber.i("Client not connected");
            RadioPlayerApplication.postToBus(new MessageEvent("Not connected, check connection"));
        }

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
        setCategoryIcon();
        Timber.i("Category thread complete, category data model updated");
        RadioPlayerApplication
                .postToBus(new DataModelUpdateEvent(DataModelUpdateEvent.CATEGORY_MODEL_DATA));
    }

    private void setCategoryIcon() {
        for (int i = 0; i < mCategoryList.size(); i++) {
            Category item = mCategoryList.get(i);
            String title = item.getTitle().toLowerCase();
            if(title.contains("classical"))
                item.setIcon(R.drawable.icon_classical);
            else if(title.contains("adult"))
                item.setIcon(R.drawable.icon_adult);
            else if(title.contains("country"))
                item.setIcon(R.drawable.icon_country);
            else if(title.contains("decades"))
                item.setIcon(R.drawable.icon_decades);
            else if(title.contains("electronic"))
                item.setIcon(R.drawable.icon_electronic);
            else if(title.contains("folk"))
                item.setIcon(R.drawable.icon_folk);
            else if(title.contains("international"))
                item.setIcon(R.drawable.icon_international);
            else if(title.contains("jazz"))
                item.setIcon(R.drawable.icon_jazz);
            else if(title.contains("misc"))
                item.setIcon(R.drawable.icon_misc);
            else if(title.contains("pop"))
                item.setIcon(R.drawable.icon_pop);
            else if(title.contains("urban"))
                item.setIcon(R.drawable.icon_randb);
            else if(title.contains("rap"))
                item.setIcon(R.drawable.icon_rap);
            else if(title.contains("reggae"))
                item.setIcon(R.drawable.icon_reggae);
            else if(title.contains("rock"))
                item.setIcon(R.drawable.icon_rock);
            else if(title.contains("speech"))
                item.setIcon(R.drawable.icon_speech);
            else
                item.setIcon(R.drawable.icon_pop);
        }
    }

}
