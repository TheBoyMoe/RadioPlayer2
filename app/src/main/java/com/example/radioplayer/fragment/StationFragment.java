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
import com.example.radioplayer.event.OnClickEvent;
import com.example.radioplayer.event.RefreshUIEvent;
import com.example.radioplayer.model.Station;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class StationFragment extends BaseFragment{

    private List<Station> mStationList = new ArrayList<>();
    private StationArrayAdapter mAdapter;

    public StationFragment() {}

    public static StationFragment newInstance() {
        return new StationFragment();
    }

    public void setStationData(List<Station> list) {
        // TODO when category id the same don't clear list
        mStationList.clear();
        mStationList.addAll(list);
        Timber.i("Received data set from StationDataFragment, size: %d", mStationList.size());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // build the UI
        ListView listView = (ListView) inflater.inflate(R.layout.list_view, container, false);
        mAdapter = new StationArrayAdapter(mStationList);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RadioPlayerApplication.postToBus(new OnClickEvent(OnClickEvent.STATION_ON_CLICK_EVENT, position));
            }
        });

        return listView;
    }

    // handle data set changed event - update UI
    @Subscribe
    public void refreshUi(RefreshUIEvent event) {
        String refreshEvent = event.getRefreshEvent();
        if(refreshEvent.equals(RefreshUIEvent.REFRESH_STATION_LIST_UI))
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
