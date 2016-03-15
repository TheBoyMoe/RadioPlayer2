package com.example.radioplayer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.radioplayer.R;
import com.example.radioplayer.RadioPlayerApplication;
import com.example.radioplayer.adapter.CustomItemDecoration;
import com.example.radioplayer.adapter.ListItemAdapter;
import com.example.radioplayer.data.StationDataCache;
import com.example.radioplayer.event.MessageEvent;
import com.example.radioplayer.event.StationThreadCompletionEvent;
import com.example.radioplayer.model.Station;
import com.example.radioplayer.network.StationThread;
import com.example.radioplayer.util.Constants;
import com.example.radioplayer.util.Utils;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * References:
 * [1] https://guides.codepath.com/android/Implementing-Pull-to-Refresh-Guide
 */
public class StationFragment extends BaseFragment implements AdapterView.OnItemClickListener{

    public static final String BUNDLE_CATEGORY_ID = "category_id";
    private static final String BUNDLE_PAGE_NUMBER = "page_number";
    private List<Station> mStationList = new ArrayList<>();
    private ListItemAdapter mAdapter;
    private Long mCategoryId;
    private int mIcon;
    private boolean mIsStarted = false;
    //private ListView mListView;
    private SwipeRefreshLayout mRefreshLayout;
    private int mPageCount = 0;
    private RecyclerView mRecyclerView;

    public StationFragment() {}

    public static StationFragment newInstance(Long categoryId, int icon) {
        // add category icon to the bundle
        StationFragment fragment = new StationFragment();
        Bundle args = new Bundle();
        args.putLong(Constants.KEY_CATEGORY_ID, categoryId);
        args.putInt(Constants.KEY_CATEGORY_ICON, icon);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retrieve the categoryId & execute the background thread to download the station list
        mCategoryId = getArguments().getLong(Constants.KEY_CATEGORY_ID);
        mIcon = getArguments().getInt(Constants.KEY_CATEGORY_ICON);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.list_recycler, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new CustomItemDecoration(getResources().getDimensionPixelSize(R.dimen.dimen_space)));
        mAdapter = new ListItemAdapter(mStationList, getActivity(), mIcon);
        mRecyclerView.setAdapter(mAdapter);

        // TODO impl pulldown to refresh
        // configure the pulldown icon
//        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
//        mRefreshLayout.setColorSchemeResources(
//                R.color.color_swipe_1,
//                R.color.color_swipe_2,
//                R.color.color_swipe_3,
//                R.color.color_swipe_4
//        );

        if(savedInstanceState != null) {
            // retrieve page number from the bundle
            mPageCount = savedInstanceState.getInt(BUNDLE_PAGE_NUMBER);
            // retrieve the station list from the cache on rotation
            setStationList();
        } else {
            // first time in, download station list
            downloadStationData();
        }

        // TODO impl for recycler
        // setup refresh listener which triggers another data download
//        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                downloadStationData();
//            }
//        });

        return view;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO
        // reverse the item position clicked on to match the adapter
        //position = (mStationList.size() - 1) - position;
        //RadioPlayerApplication.postToBus(new OnClickEvent(OnClickEvent.LIST_ITEM_CLICK_EVENT, position));
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
            // TODO signal refreshing complete
           // mRefreshLayout.setRefreshing(false);
            // refresh the station list with the most up-to-date list from the cache
            mStationList.clear();
            setStationList();
        }
        if(event.isDownloadComplete()) {
            Utils.showSnackbar(mRecyclerView, "No more stations found, " + mStationList.size() + " found in total");
        }
    }


    private void setStationList() {
        // pass a copy of the station list to the adapter
        List<Station> list = new ArrayList<>(StationDataCache.getStationDataCache().getStationList());
        mStationList.addAll(list);
        mAdapter.notifyDataSetChanged();
    }


}
