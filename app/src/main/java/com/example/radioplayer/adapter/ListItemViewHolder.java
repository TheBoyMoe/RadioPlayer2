package com.example.radioplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.radioplayer.R;
import com.example.radioplayer.RadioPlayerApplication;
import com.example.radioplayer.event.OnClickEvent;
import com.example.radioplayer.model.Station;

public class ListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private TextView mItemTitle;
    private ImageView mItemIcon;
    private int mPosition;

    public ListItemViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        mItemTitle = (TextView) itemView.findViewById(R.id.item_title);
        mItemIcon = (ImageView) itemView.findViewById(R.id.item_icon);
    }

    public void bindStationItem(Station item, Context context, int position, int icon) {
        mPosition = position;
        mItemTitle.setText(item.getName());

        // TODO use picasso to download and set icon

        // if not available use the category icon
        mItemIcon.setImageResource(icon);
    }

    @Override
    public void onClick(View v) {
        // propagate the click upto the hosting activity
        RadioPlayerApplication.postToBus(new OnClickEvent(OnClickEvent.LIST_ITEM_CLICK_EVENT, mPosition));
    }
}
