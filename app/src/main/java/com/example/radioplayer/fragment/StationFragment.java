package com.example.radioplayer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.util.LinkedList;
import java.util.List;

import timber.log.Timber;

/**
 * References:
 * [1] http://stackoverflow.com/questions/26543131/how-to-implement-endless-list-with-recyclerview
 * [2] http://androhub.com/load-more-items-on-scroll-android/
 *
 */
public class StationFragment extends BaseFragment{

    private static final String BUNDLE_PAGE_NUMBER = "page_number";
    private List<Station> mStationList = new LinkedList<>();
    private ListItemAdapter mAdapter;
    private Long mCategoryId;
    private int mIcon;
    private boolean mIsStarted = false;
    private int mPageCount = 0;
    private RecyclerView mRecyclerView;

    private int mPreviousTotal, mVisibleThreshold, mFirstVisibleItem, mVisibleItemCount, mTotalItemCount;
    private boolean mLoading = true;

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
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new CustomItemDecoration(getResources().getDimensionPixelSize(R.dimen.dimen_space)));
        mAdapter = new ListItemAdapter(mStationList, getActivity(), mIcon);
        if(isAdded())
            mRecyclerView.setAdapter(mAdapter);

        if(savedInstanceState != null) {
            // retrieve page number from the bundle
            mPageCount = savedInstanceState.getInt(BUNDLE_PAGE_NUMBER);
            // retrieve the station list from the cache on rotation
            setStationList();
        } else {
            // first time in, download station list
            downloadStationData();
        }

        // Impl OnScrollListener
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                mVisibleItemCount = mRecyclerView.getChildCount();
                mTotalItemCount = layoutManager.getItemCount();
                mFirstVisibleItem = layoutManager.findFirstVisibleItemPosition();

                if (mLoading) {
                    if (mTotalItemCount > mPreviousTotal) {
                        mLoading = false;
                        mPreviousTotal = mTotalItemCount;
                    }
                }
                if (!mLoading && (mTotalItemCount - mVisibleItemCount)
                        <= (mFirstVisibleItem + mVisibleThreshold)) {
                    // End has been reached
                    Timber.i("End of the line, %d stations found", mStationList.size());

                    // Do something
                    downloadStationData();
                    mLoading = true;
                }
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
            // refresh the station list with the most up-to-date list from the cache
            mAdapter.clear();
            setStationList();
            if(mStationList.size() > 20)
                Utils.showSnackbar(mRecyclerView, "Found " + mStationList.size() + " stations so far");
        }
        if(event.isDownloadComplete()) {
            Utils.showSnackbar(mRecyclerView, "Found, " + mStationList.size() + " stations in total");
        }
    }


    private void setStationList() {
        // pass a copy of the station list to the adapter
        List<Station> list = new LinkedList<>(StationDataCache.getStationDataCache().getStationList());
        mAdapter.addAll(list);
        mAdapter.notifyDataSetChanged();
    }


}
