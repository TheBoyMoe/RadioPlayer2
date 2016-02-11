package com.example.radioplayer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.radioplayer.R;
import com.example.radioplayer.RadioPlayerApplication;
import com.example.radioplayer.data.StationDataCache;
import com.example.radioplayer.event.MessageEvent;
import com.example.radioplayer.event.OnClickEvent;
import com.example.radioplayer.event.StationThreadCompletionEvent;
import com.example.radioplayer.model.Station;
import com.example.radioplayer.network.StationThread;
import com.example.radioplayer.util.Utils;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import timber.log.Timber;

/**
 * References:
 * [1] https://guides.codepath.com/android/Implementing-Pull-to-Refresh-Guide
 */
public class StationFragment extends BaseFragment{

    public static final String BUNDLE_CATEGORY_ID = "category_id";
    private static final String BUNDLE_PAGE_NUMBER = "page_number";
    private List<Station> mStationList = new LinkedList<>();
    private StationArrayAdapter mAdapter;
    private Long mCategoryId;
    private boolean mIsStarted = false;
    private ListView mListView;
    private SwipeRefreshLayout mRefreshLayout;
    private int mPageCount = 0;

    public StationFragment() {}

    public static StationFragment newInstance(Long categoryId) {
        StationFragment fragment = new StationFragment();
        Bundle args = new Bundle();
        args.putLong(BUNDLE_CATEGORY_ID, categoryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retrieve the categoryId & execute the background thread to download the station list
        mCategoryId = getArguments().getLong(BUNDLE_CATEGORY_ID);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.station_list_view, container, false);
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mListView = (ListView) view.findViewById(R.id.list_view);

        mAdapter = new StationArrayAdapter(mStationList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RadioPlayerApplication.postToBus(new OnClickEvent(OnClickEvent.STATION_ON_CLICK_EVENT, position));
            }
        });

        mRefreshLayout.setColorSchemeResources(
                R.color.color_swipe_1,
                R.color.color_swipe_2,
                R.color.color_swipe_3,
                R.color.color_swipe_4
        );

        if(savedInstanceState != null) {
            // retrieve page number from the bundle
            mPageCount = savedInstanceState.getInt(BUNDLE_PAGE_NUMBER);
            // retrieve the station list from the cache on rotation
            setStationList();
        } else {
            // first time in, download station list
            downloadStationData();
        }

        // setup refresh listener which triggers another data download
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO - increment counter and download the next page
                downloadStationData();
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_PAGE_NUMBER, mPageCount);
    }

    private void downloadStationData() {
        if(Utils.isClientConnected(getActivity())) {
            if(!mIsStarted) {
                mIsStarted = true;
                ++mPageCount;
                new StationThread("StationThread", getActivity(), mCategoryId, mPageCount).start();
            }
        } else {
            Timber.i("Client not connected");
            RadioPlayerApplication.postToBus(new MessageEvent("Not connected, check connection"));
        }
    }


    @Subscribe
    public void refreshUi(StationThreadCompletionEvent event) {
        if(event.isThreadComplete()) {
            mIsStarted = false;
            // signal refreshing complete
            mRefreshLayout.setRefreshing(false);
            // refresh the station list with the most up-to-date list from the cache
            mStationList.clear();
            // TODO reverse order of list
            setStationList();
        }
    }

    private void setStationList() {
        List<Station> list = new LinkedList<>(StationDataCache.getStationDataCache().getStationList());
        Collections.reverse(list);
        mStationList.addAll(list);
        mAdapter.notifyDataSetChanged();
    }


    // Custom ArrayAdapter and ViewHolder
    private class StationArrayAdapter extends ArrayAdapter<Station> {


        public StationArrayAdapter(List<Station> list) {
            super(getActivity(), 0, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            StationViewHolder holder = null;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item, null);
                holder = (StationViewHolder) convertView.getTag();
            }
            if(holder == null) {
                holder = new StationViewHolder(convertView);
                convertView.setTag(holder);
            }

            // bind the station object to the holder
            holder.bindView(getItem(position));

            return convertView;
        }
    }

    private class StationViewHolder {

        TextView titleText = null;

        StationViewHolder(View row) {
            titleText = (TextView) row.findViewById(R.id.title_text);
        }

        void bindView(Station station) {
            titleText.setText(station.getName());
        }

    }

}
