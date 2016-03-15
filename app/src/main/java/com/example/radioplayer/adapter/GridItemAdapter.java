package com.example.radioplayer.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.radioplayer.R;
import com.example.radioplayer.model.Category;

import java.util.List;


public class GridItemAdapter extends RecyclerView.Adapter<GridItemViewHolder>{

    private List<Category> mList;
    private Context mContext;
    private ItemChoiceManager mItemChoiceManager;

    public GridItemAdapter(List<Category> list, Context context, int choiceMode) {
        mList = list;
        mContext = context;
        mItemChoiceManager = new ItemChoiceManager(this);
        mItemChoiceManager.setChoiceMode(choiceMode);
    }

    @Override
    public GridItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.grid_item, parent, false);
        return new GridItemViewHolder(view, mItemChoiceManager);
    }

    @Override
    public void onBindViewHolder(GridItemViewHolder holder, int position) {
        Category item = mList.get(position);
        holder.bindModelItem(item, mContext, position);

        mItemChoiceManager.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    // helper methods which forward on the calls to the ItemChoiceManager
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mItemChoiceManager.onRestoreInstanceState(savedInstanceState);
    }

    public void onSaveInstanceState(Bundle outState) {
        mItemChoiceManager.onSaveInstanceState(outState);
    }

    public int getSelectedItemPosition() {
        return mItemChoiceManager.getSelectedItemPosition();
    }

    public void selectView(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof GridItemViewHolder) {
            GridItemViewHolder gvh = (GridItemViewHolder) viewHolder;
            gvh.onClick(gvh.itemView);
        }
    }

    public void setInitialView(int position) {
        mItemChoiceManager.setInitialCheckedState(position);
    }

}
