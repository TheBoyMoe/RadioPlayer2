package com.example.radioplayer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import java.util.List;

import timber.log.Timber;

public class StationFragment extends BaseFragment{

    public static final String BUNDLE_CATEGORY_ID = "category_id";
    private List<Station> mStationList = new ArrayList<>();
    private StationArrayAdapter mAdapter;
    private Long mCategoryId;
    private boolean mIsStarted = false;

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

        ListView listView = (ListView) inflater.inflate(R.layout.list_view, container, false);
        mAdapter = new StationArrayAdapter(mStationList);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RadioPlayerApplication.postToBus(new OnClickEvent(OnClickEvent.STATION_ON_CLICK_EVENT, position));
            }
        });

        if(savedInstanceState != null) {
            // retrieve the station list from the cache
            mStationList.addAll(StationDataCache.getStationDataCache().getStationList());
            mAdapter.notifyDataSetChanged();
        } else {
            // first time in, download station list
            if(Utils.isClientConnected(getActivity())) {
                if(!mIsStarted) {
                    mIsStarted = true;
                    new StationThread("StationThread", getActivity(), mCategoryId).start();
                }
            } else {
                Timber.i("Client not connected");
                RadioPlayerApplication.postToBus(new MessageEvent("Not connected, check connection"));
            }
        }
        return listView;
    }


    @Subscribe
    public void refreshUi(StationThreadCompletionEvent event) {
        if(event.isThreadComplete()) {
            mIsStarted = false;
            // refresh the station list with the most up-to-date list from the cache
            mStationList.clear();
            mStationList.addAll(StationDataCache.getStationDataCache().getStationList());
            mAdapter.notifyDataSetChanged();
        }
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
