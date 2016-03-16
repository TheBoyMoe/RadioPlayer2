package com.example.radioplayer.data;

import com.example.radioplayer.model.Station;

import java.util.LinkedList;
import java.util.List;

import timber.log.Timber;

/**
 * Singleton data cache used to hold the station list, accessible anywhere within the app
 */

public class StationDataCache {

    private static StationDataCache sDataCache;
    private List<Station> mList = new LinkedList<>();

    private StationDataCache() {}

    public static StationDataCache getStationDataCache() {
        if(sDataCache == null) {
            sDataCache = new StationDataCache();
        }
        return sDataCache;
    }

    public List<Station> getStationList() {
        return mList;
    }

    public void setStationList(LinkedList <Station> list) {
        mList.addAll(list); // add to the current cache
        Timber.i("CachedList: %s", mList); // DEBUG
    }

    public Station getStation(int position) {
        return mList.get(position);
    }

    public void clearDataCache() {
        if(mList.size() > 0) {
            mList.clear();
            Timber.i("Clearing data cache");
        }
    }

}
