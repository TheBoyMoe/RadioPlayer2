package com.example.radioplayer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.radioplayer.R;

public class StationFragment extends BaseFragment{

    private static final String CATEGORY_ID = "category_id";

    public StationFragment() {}

    public static StationFragment newInstance(Long categoryId) {
        StationFragment fragment = new StationFragment();
        Bundle args = new Bundle();
        args.putLong(CATEGORY_ID, categoryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.category_id, container, false);
        TextView categoryId = (TextView) view.findViewById(R.id.category_id_text);
        categoryId.setText(String.valueOf(getArguments().getLong(CATEGORY_ID)));

        return view;
    }


}
