package com.example.radioplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.radioplayer.R;
import com.example.radioplayer.model.Category;

import java.util.Random;

public class GridItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private Integer[] mIcons = {
            R.drawable.icon_adult,
            R.drawable.icon_classical,
            R.drawable.icon_country,
            R.drawable.icon_decades,
            R.drawable.icon_electronic,
            R.drawable.icon_folk,
            R.drawable.icon_international,
            R.drawable.icon_jazz,
            R.drawable.icon_misc,
            R.drawable.icon_pop,
            R.drawable.icon_randb,
            R.drawable.icon_rap,
            R.drawable.icon_reggae,
            R.drawable.icon_rock,
            R.drawable.icon_speech
    };
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

        int num = mGenerator.nextInt(mIcons.length);
        mItemIcon.setImageResource(mIcons[num]);

        // TODO assign icon to category object
        //mItemIcon.setImageResource(item.getIcon());

    }

    @Override
    public void onClick(View v) {
        // TODO
        // propagate the click event to any interested parties
        //RadioPlayerApplication.postToBus(new OnGridItemClickEvent(mPosition));
        // set the active state
       // mItemChoiceManager.onClick(this);
    }



}
