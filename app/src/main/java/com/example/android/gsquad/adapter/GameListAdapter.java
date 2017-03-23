package com.example.android.gsquad.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.gsquad.R;
import com.example.android.gsquad.model.Cover;
import com.example.android.gsquad.model.Game;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Raghvendra on 18-03-2017.
 */

public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.ViewHolder> {
    private List<Game> gamesList;
    private Context mContext;
    private int mSelectedItemsId;

    public GameListAdapter(List<Game> data, Context context) {
        this.gamesList = data;
        this.mContext = context;
        mSelectedItemsId = -1;
    }

    @Override
    public GameListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_games, parent, false);
        final ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Cover mCover = gamesList.get(position).getCover();
        if (mCover != null) {
            Glide.with(mContext)
                    .load("https:" + mCover.getUrl())
                    .asBitmap()
                    .error(R.drawable.no_image)
                    .into(holder.mThumbnailView);
        }
        holder.mTitleView.setText(gamesList.get(position).getName());
        holder.mThumbnailView.setContentDescription(holder.mTitleView.getText());
        if (mSelectedItemsId == position) {
            holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public int getItemCount() {
        return (null != gamesList ? gamesList.size() : 0);
    }

    /*
    *  Methods required to do selections, remove selections, etc.
    */

    // Toggle selection methods
    public void toggleSelection(int position) {
        selectView(position);
    }

    // Remove selected selections
    public void removeSelections() {
        mSelectedItemsId = -1;
        notifyDataSetChanged();
    }

    // Put or delete selected position into SparseBooleanArray
    private void selectView(int position) {
        if (mSelectedItemsId == position) {
            mSelectedItemsId = -1;
        } else {
            mSelectedItemsId = position;
        }
        notifyDataSetChanged();
    }


    // Return all selected ids
    public int getSelectedId() {
        return mSelectedItemsId;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView mThumbnailView;
        TextView mTitleView;

        public ViewHolder(View view) {
            super(view);
            mThumbnailView = (CircleImageView) view.findViewById(R.id.game_thumbnail);
            mTitleView = (TextView) view.findViewById(R.id.game_title);
        }
    }
}
