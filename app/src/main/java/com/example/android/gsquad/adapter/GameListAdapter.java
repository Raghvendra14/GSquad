package com.example.android.gsquad.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.gsquad.R;
import com.example.android.gsquad.model.Cover;
import com.example.android.gsquad.model.Game;

import java.util.List;

/**
 * Created by Raghvendra on 18-03-2017.
 */

public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.ViewHolder> {
    private List<Game> gamesList;
    private Context mContext;
    private SparseBooleanArray mSelectedItemsIds;

    public GameListAdapter(List<Game> data, Context context) {
        this.gamesList = data;
        this.mContext = context;
        mSelectedItemsIds = new SparseBooleanArray();
    }

    @Override
    public GameListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_games, parent, false);
        final ViewHolder vh = new ViewHolder(view);
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO: Add code to select the game
//                // Select item on single click
//                new AddGameActivity().onListItemSelect(vh.getAdapterPosition());
//            }
//        });
//        view.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                // Select item on long click
//                new AddGameActivity().onListItemSelect(vh.getAdapterPosition());
//                return true;
//            }
//        });
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
        if (mSelectedItemsIds.get(position)) {
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
        selectView(position, !mSelectedItemsIds.get(position));
    }

    // Remove selected selections
    public void removeSelections() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    // Put or delete selected position into SparseBooleanArray
    public void selectView(int position, boolean value) {
        if (value) {
            mSelectedItemsIds.put(position, value);
        } else {
            mSelectedItemsIds.delete(position);
        }
        notifyDataSetChanged();
    }

    // Get total selected count
    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    // Return all selected ids
    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mThumbnailView;
        TextView mTitleView;

        public ViewHolder(View view) {
            super(view);
            mThumbnailView = (ImageView) view.findViewById(R.id.game_thumbnail);
            mTitleView = (TextView) view.findViewById(R.id.game_title);
        }
    }
}
