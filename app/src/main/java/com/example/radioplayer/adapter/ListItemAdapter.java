package com.example.radioplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.radioplayer.R;
import com.example.radioplayer.model.Station;

import java.util.List;

public class  ListItemAdapter extends RecyclerView.Adapter<ListItemViewHolder>{

    private List<Station> mList;
    private Context mContext;
    private int mIcon;

    public ListItemAdapter(List<Station> list, Context context, int icon) {
        mList = list;
        mContext = context;
        mIcon = icon;
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder holder, int position) {
        Station item = mList.get(position);
        holder.bindStationItem(item, mContext, position, mIcon);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void clear() {
        mList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Station> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }


}
