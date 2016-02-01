package com.example.radioplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.radioplayer.R;
import com.example.radioplayer.RadioPlayerApplication;
import com.example.radioplayer.event.CategoryOnClickEvent;
import com.example.radioplayer.event.DataModelUpdateEvent;
import com.example.radioplayer.event.RefreshUIEvent;
import com.example.radioplayer.fragment.CategoryFragment;
import com.example.radioplayer.fragment.ModelFragment;
import com.example.radioplayer.fragment.StationFragment;
import com.example.radioplayer.model.Category;
import com.squareup.otto.Subscribe;

public class MainActivity extends AppCompatActivity {

    private static final String MODEL_FRAGMENT_TAG = "model_fragment";

    private ModelFragment mModelFragment;
    private CategoryFragment mCategoryFragment;
    private CoordinatorLayout mCoordinatorLayout;
    private boolean mDualPane;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mModelFragment =
                (ModelFragment) getFragmentManager().findFragmentByTag(MODEL_FRAGMENT_TAG);
        if(mModelFragment == null) {
            mModelFragment = ModelFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .add(mModelFragment, MODEL_FRAGMENT_TAG)
                    .commit();
        }

        mCategoryFragment =
                (CategoryFragment) getFragmentManager().findFragmentById(R.id.category_fragment_container);
        if(mCategoryFragment == null) {
            mCategoryFragment = CategoryFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .add(R.id.category_fragment_container, mCategoryFragment)
                    .commit();
        }

        // this call occurs before onCreate in ModelFragment (and thus thread) are called - list empty
        if(mModelFragment != null && mCategoryFragment != null) {
            mCategoryFragment.setModelData(mModelFragment.getModel());
        }

        // Check if the station list view exists
        View stationList = findViewById(R.id.station_fragment_container);
        mDualPane = stationList != null && stationList.getVisibility() == View.VISIBLE;

    }


    @Override
    protected void onResume() {
        super.onResume();
        RadioPlayerApplication.getInstance().getBus().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        RadioPlayerApplication.getInstance().getBus().unregister(this);
    }

    @Subscribe
    public void hasCategoryDataModelBeenUpdated(DataModelUpdateEvent event) {
        if(event.isDataModelUpdate()
                && event.getDataModel().equals(DataModelUpdateEvent.CATEGORY_MODEL_DATA)) {
            // fetch the data model from the model fragment and update category fragment's data model
            if(mModelFragment != null && mCategoryFragment != null) {
                mCategoryFragment.setModelData(mModelFragment.getModel());
                RadioPlayerApplication.postToBus(new RefreshUIEvent(true));
            }
        }
    }

    // impl Category list onListItemClick - fetch the position & object
    @Subscribe
    public void onCategoryClickEvent(CategoryOnClickEvent event) {
        Category item = mModelFragment.getDataModelItem(event.getPosition());
        // on tablets load the station list fragment
        if(mDualPane) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.station_fragment_container, StationFragment.newInstance(item.getId()))
                    .commit();
        } else {
            // on phone launch the station activity
            Intent intent = new Intent(this, StationActivity.class);
            intent.putExtra(StationActivity.CATEGORY_ID_EXTRA, item.getId());
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //noinspection SimplifiableIfStatement
        if (item.getItemId() == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




}
