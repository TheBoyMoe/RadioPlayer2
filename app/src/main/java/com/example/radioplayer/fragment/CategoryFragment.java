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
import com.example.radioplayer.event.CategoryOnClickEvent;
import com.example.radioplayer.event.RefreshUIEvent;
import com.example.radioplayer.model.Category;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class CategoryFragment extends BaseFragment{

    private List<Category> mCategoryList = new ArrayList<>();
    private CategoryArrayAdapter mAdapter;

    public CategoryFragment() {}

    public static CategoryFragment newInstance() {
        return new CategoryFragment();
    }


    // populate the fragments arraylist and notify the adapter
    public void setCategoryData(ArrayList<Category> list) {
        mCategoryList.addAll(list);
        Timber.i("Received data set from CategoryDataFragment, size: %d", mCategoryList.size());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final ListView listView = (ListView) inflater.inflate(R.layout.list_view, container, false);
        mAdapter = new CategoryArrayAdapter(mCategoryList);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // post the click event up to the activity - post the position
                RadioPlayerApplication.postToBus(new CategoryOnClickEvent(position));
            }
        });
        return listView;
    }


    // handle data set changed event
    @Subscribe
    public void refreshUi(RefreshUIEvent event) {
        String refreshEvent = event.getRefreshEvent();
        if (refreshEvent.equals(RefreshUIEvent.REFRESH_CATEGORY_LIST_UI)) {
            mAdapter.notifyDataSetChanged();
        }
    }


    // Custom ArrayAdapter and ViewHolder
    private class CategoryArrayAdapter extends ArrayAdapter<Category> {

        public CategoryArrayAdapter(List<Category> list) {
            super(getActivity(), 0, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CategoryViewHolder holder = null;

            if(convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item, null);
                holder = (CategoryViewHolder) convertView.getTag();
            }

            if(holder == null) {
                holder = new CategoryViewHolder(convertView);
                convertView.setTag(holder);
            }

            // bind the category object to the holder
            holder.bindView(getItem(position));

            return convertView;
        }

    }


    private class CategoryViewHolder {

        TextView titleText = null;

        CategoryViewHolder(View row) {
            titleText = (TextView) row.findViewById(R.id.title_text);
        }

        void bindView(Category category) {
            titleText.setText(category.getTitle());
        }

    }


}
