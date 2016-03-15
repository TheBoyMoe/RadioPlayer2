package com.example.radioplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.radioplayer.R;
import com.example.radioplayer.model.Station;

import java.util.List;

public class ListItemAdapter extends RecyclerView.Adapter<ListItemViewHolder>{

    private List<Station> mList;
    private Context mContext;
    private ListItemViewHolder mViewHolder;

    public ListItemAdapter(List<Station> list, Context context) {
        mList = list;
        mContext = context;
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.list_item, parent, false);
        mViewHolder = new ListItemViewHolder(view);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder holder, int position) {
        Station item = mList.get(position);
        mViewHolder.bindStationItem(item, mContext, position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
