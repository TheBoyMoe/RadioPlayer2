package com.example.radioplayer.data;

import com.example.radioplayer.model.Station;

import java.util.ArrayList;

/**
 * Singleton data cache used to hold the station list, accessible anywhere within the app
 */

public class StationDataCache {

    private static StationDataCache sDataCache;
    private ArrayList<Station> mList;

    private StationDataCache() {}

    public static StationDataCache getStationDataCache() {
        if(sDataCache == null) {
            sDataCache = new StationDataCache();
        }
        return sDataCache;
    }

    public ArrayList<Station> getStationList() {
        return mList;
    }

    public void setStationList(ArrayList<Station> list) {
        mList = list;
    }

    public Station getStation(int position) {
        return mList.get(position);
    }

}
