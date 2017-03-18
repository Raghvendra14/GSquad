package com.example.android.gsquad;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.gsquad.model.Cover;
import com.example.android.gsquad.model.Game;

import java.util.List;

/**
 * Created by Raghvendra on 18-03-2017.
 */

public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.ViewHolder> {
    private List<Game> gamesList;
    private Context mContext;

    public GameListAdapter(List<Game> data, Context context) {
        this.gamesList = data;
        this.mContext = context;
    }

    @Override
    public GameListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_games, parent, false);
        final ViewHolder vh = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Add code to select the game
            }
        });
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
    }

    @Override
    public int getItemCount() {
        return gamesList.size();
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
