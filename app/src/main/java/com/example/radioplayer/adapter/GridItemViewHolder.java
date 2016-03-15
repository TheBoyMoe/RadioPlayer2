package com.example.radioplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.radioplayer.R;
import com.example.radioplayer.RadioPlayerApplication;
import com.example.radioplayer.event.OnClickEvent;
import com.example.radioplayer.model.Category;

import java.util.Random;

public class GridItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private TextView mItemTitle;
    private ImageView mItemIcon;
    private ItemChoiceManager mItemChoiceManager;
    private int mPosition;

    private Random mGenerator = new Random();

    public GridItemViewHolder(View itemView, ItemChoiceManager choiceManager) {
        super(itemView);
        itemView.setOnClickListener(this);
        mItemChoiceManager = choiceManager;

        mItemTitle = (TextView) itemView.findViewById(R.id.item_title);
        mItemIcon = (ImageView) itemView.findViewById(R.id.item_icon);
    }

    public void bindModelItem(Category item, Context context, int position) {
        mPosition = position;

        // populate the holder elements
        mItemTitle.setText(item.getTitle());
        mItemIcon.setImageResource(item.getIcon());

    }

    @Override
    public void onClick(View v) {
        // propagate the click event to any interested parties
        RadioPlayerApplication.postToBus(new OnClickEvent(OnClickEvent.GRID_ITEM_CLICK_EVENT, mPosition));
        // set the active state
        mItemChoiceManager.onClick(this);
    }



}
