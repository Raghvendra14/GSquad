package com.example.android.gsquad.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.gsquad.R;
import com.example.android.gsquad.model.Cover;
import com.example.android.gsquad.model.GameDetails;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Raghvendra on 19-03-2017.
 */

public class GameDetailsListAdapter extends RecyclerView.Adapter<GameDetailsListAdapter.ViewHolder> {
    private Context mContext;
    private List<GameDetails> mGameDetails;

    public GameDetailsListAdapter(List<GameDetails> gameDetails, Context context) {
        this.mGameDetails = gameDetails;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_games, parent, false);
        final ViewHolder vh = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO : Add details game activity
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Cover cover = mGameDetails.get(position).getCover();
        if (cover != null) {
            Glide.with(mContext)
                    .load("https:" + cover.getUrl())
                    .asBitmap()
                    .error(R.drawable.no_image)
                    .into(holder.mCircleImageView);
        }
        holder.mTextView.setText(mGameDetails.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return (null != mGameDetails ? mGameDetails.size() : 0);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView mCircleImageView;
        TextView mTextView;

        public ViewHolder(View view) {
            super(view);
            mCircleImageView = (CircleImageView) view.findViewById(R.id.game_thumbnail);
            mTextView = (TextView) view.findViewById(R.id.game_title);
        }
    }
}
