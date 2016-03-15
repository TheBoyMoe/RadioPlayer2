package com.example.radioplayer.fragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.example.radioplayer.R;
import com.example.radioplayer.adapter.AutofitRecyclerView;
import com.example.radioplayer.adapter.CustomItemDecoration;
import com.example.radioplayer.adapter.GridItemAdapter;
import com.example.radioplayer.event.RefreshUIEvent;
import com.example.radioplayer.model.Category;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class CategoryFragment extends BaseFragment{

    private List<Category> mCategoryList = new ArrayList<>();
    private GridItemAdapter mAdapter;
    private int mChoiceMode;
    private boolean mIsDualPane;

    public CategoryFragment() {}

    public static CategoryFragment newInstance() {
        return new CategoryFragment();
    }

    // method only called if a fragment is inflated from xml
    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GridItemFragment, 0, 0);
        mChoiceMode = typedArray.getInt(R.styleable.GridItemFragment_android_choiceMode, AbsListView.CHOICE_MODE_NONE);
        boolean autoSelectView = typedArray.getBoolean(R.styleable.GridItemFragment_autoSelectView, false);
        typedArray.recycle();
    }

    // populate the fragments arraylist and notify the adapter
    public void setCategoryData(ArrayList<Category> list) {
        mCategoryList.addAll(list);
        Timber.i("Category data set: %s", mCategoryList);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        AutofitRecyclerView recyclerView = (AutofitRecyclerView) inflater.inflate(R.layout.grid_recycler, container, false);
        recyclerView.addItemDecoration(new CustomItemDecoration(getResources().getDimensionPixelSize(R.dimen.dimen_space)));
        recyclerView.setHasFixedSize(true);

        mAdapter = new GridItemAdapter(mCategoryList, getActivity(), mChoiceMode);
        recyclerView.setAdapter(mAdapter);

        if(savedInstanceState != null) {
            mAdapter.onRestoreInstanceState(savedInstanceState);
        }
        return recyclerView;
    }


    // handle data set changed event
    @Subscribe
    public void refreshUi(RefreshUIEvent event) {
        String refreshEvent = event.getRefreshEvent();
        if (refreshEvent.equals(RefreshUIEvent.REFRESH_CATEGORY_LIST_UI)) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState == null) {
            // set the initial checked state on position 0
            if(mIsDualPane) {
                mAdapter.setInitialView(0);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // save the checked state
        mAdapter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    public void isDualPane(boolean value) {
        mIsDualPane = value;
    }



    // Custom ArrayAdapter and ViewHolder
//    private class CategoryArrayAdapter extends ArrayAdapter<Category> {
//
//        public CategoryArrayAdapter(List<Category> list) {
//            super(getActivity(), 0, list);
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            CategoryViewHolder holder = null;
//
//            if(convertView == null) {
//                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item, null);
//                holder = (CategoryViewHolder) convertView.getTag();
//            }
//
//            if(holder == null) {
//                holder = new CategoryViewHolder(convertView);
//                convertView.setTag(holder);
//            }
//
//            // bind the category object to the holder
//            holder.bindView(getItem(position));
//
//            return convertView;
//        }
//
//    }


//    private class CategoryViewHolder {
//
//        TextView titleText = null;
//
//        CategoryViewHolder(View row) {
//            titleText = (TextView) row.findViewById(R.id.title_text);
//        }
//
//        void bindView(Category category) {
//            titleText.setText(category.getTitle());
//        }
//
//    }


}
