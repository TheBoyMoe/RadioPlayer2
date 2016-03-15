package com.example.radioplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.view.View;

import com.example.radioplayer.R;
import com.example.radioplayer.data.StationDataCache;
import com.example.radioplayer.event.DataModelUpdateEvent;
import com.example.radioplayer.event.MessageEvent;
import com.example.radioplayer.event.OnClickEvent;
import com.example.radioplayer.fragment.CategoryDataFragment;
import com.example.radioplayer.fragment.CategoryFragment;
import com.example.radioplayer.fragment.StationFragment;
import com.example.radioplayer.model.Category;
import com.example.radioplayer.util.Constants;
import com.example.radioplayer.util.Utils;
import com.squareup.otto.Subscribe;

import java.util.List;

public class MainActivity extends BaseActivity {

    private CategoryDataFragment mCategoryDataFragment;
    private CategoryFragment mCategoryFragment;
    private StationFragment mStationFragment;
    private CoordinatorLayout mCoordinatorLayout;
    private Long mCategoryId;
    private int mCategoryIcon;
    private boolean mDualPane;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        setToolbarOnActivity(R.id.toolbar);

        // load the category data fragment - fragment retained on device rotation
        mCategoryDataFragment =
                (CategoryDataFragment) getSupportFragmentManager().findFragmentByTag(CategoryDataFragment.CATEGORY_DATA_FRAGMENT_TAG);
        if(mCategoryDataFragment == null) {
            mCategoryDataFragment = CategoryDataFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(mCategoryDataFragment, CategoryDataFragment.CATEGORY_DATA_FRAGMENT_TAG)
                    .commit();
        }

        // category fragment inflated via xml
        mCategoryFragment =
                (CategoryFragment) getSupportFragmentManager().findFragmentById(R.id.category_grid_fragment);

        // this call occurs before onCreate in CategoryDataFragment (and thus thread) are called - list empty
        if(mCategoryDataFragment != null && mCategoryFragment != null) {
            mCategoryFragment.setCategoryData(mCategoryDataFragment.getCategoryData());
        }

        // Check if the station list view exists
        View stationList = findViewById(R.id.station_fragment_container);
        mDualPane = stationList != null && stationList.getVisibility() == View.VISIBLE;

        if(savedInstanceState != null) {
            mCategoryId = savedInstanceState.getLong(Constants.KEY_CATEGORY_ID);
            mCategoryIcon = savedInstanceState.getInt(Constants.KEY_CATEGORY_ICON);
        } else {
            if(mDualPane) {
                // first time in - download the station list for the first category in the grid - Adult Contemporary
                mCategoryId = 71l;
                mCategoryIcon = R.drawable.icon_adult;
            }
        }

        // instantiate the station UI on tablet device
        if(mDualPane) {
            // set the initial grid item to selected
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            for (int i = 0; i < fragments.size(); i++) {
                Fragment fragment = fragments.get(i);
                if(fragment instanceof CategoryFragment){
                    CategoryFragment gridItemFragment = (CategoryFragment) fragment;
                    gridItemFragment.isDualPane(true);
                }
            }

            // add the station UI fragment
            mStationFragment = (StationFragment) getSupportFragmentManager().findFragmentById(R.id.station_fragment_container);
            if(mStationFragment == null) {
                mStationFragment = StationFragment.newInstance(mCategoryId, mCategoryIcon);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.station_fragment_container, mStationFragment)
                        .commit();
            }
        }
    }




    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mCategoryId != null) {
            outState.putLong(Constants.KEY_CATEGORY_ID, mCategoryId);
            outState.putInt(Constants.KEY_CATEGORY_ICON,mCategoryIcon);
        }
    }


    // handle click events on both category and station items
    @Subscribe
    public void getOnClickEvent(OnClickEvent event) {

        // TODO add activity transitions - check Utility.launchActivity() in RadioPLayerUI
        // deal with clicks to category items
        if(event.getClickEvent().equals(OnClickEvent.GRID_ITEM_CLICK_EVENT)) {
            Category item = mCategoryDataFragment.getCategoryDataItem(event.getPosition());
            mCategoryId = item.getId();
            mCategoryIcon = item.getIcon();
            String categoryTitle = item.getTitle();

            // clear the data cache - downloading new category station list
            StationDataCache.getStationDataCache().clearDataCache();

            // on tablets load the station list fragment
            if(mDualPane) {
                // add the fragment if it does not already exist, otherwise replace it.
                mStationFragment = StationFragment.newInstance(mCategoryId, mCategoryIcon);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.station_fragment_container, mStationFragment)
                        .commit();
            } else {
                // on phone launch the station activity
                Intent intent = new Intent(this, StationActivity.class);
                intent.putExtra(Constants.KEY_CATEGORY_ID, mCategoryId);
                intent.putExtra(Constants.KEY_CATEGORY_ICON, mCategoryIcon);
                intent.putExtra(Constants.KEY_CATEGORY_TITLE, categoryTitle);
                startActivity(intent);
            }

        }
        // handle clicks to station items
        else if(event.getClickEvent().equals(OnClickEvent.LIST_ITEM_CLICK_EVENT)) {
                int position = event.getPosition();
                Intent intent = new Intent(this, RadioPlayerActivity.class);
                intent.putExtra(Constants.KEY_QUEUE_POSITION, position);
                startActivity(intent);
        }

    }


    // handle data model update events
    @Subscribe
    public void dataModelUpdate(DataModelUpdateEvent event) {
        String update = event.getDataModel();
        if(update.equals(DataModelUpdateEvent.CATEGORY_MODEL_DATA)) {
            // fetch the data model from the model fragment and update category fragment's data model
            if(mCategoryDataFragment != null && mCategoryFragment != null) {
                mCategoryFragment.setCategoryData(mCategoryDataFragment.getCategoryData());

                // detach and re-attach the fragment to ensure the items are always displayed when first launched
//                getSupportFragmentManager().beginTransaction()
//                    .detach(mCategoryFragment)
//                    .attach(mCategoryFragment)
//                    .commit();
            }
        }
    }


    // handle message events
    @Subscribe
    public void getMessageEvent(MessageEvent event) {
        Utils.showSnackbar(mCoordinatorLayout, event.getMessage());
    }


}
