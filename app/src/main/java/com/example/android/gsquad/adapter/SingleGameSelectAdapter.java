package com.example.android.gsquad.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.gsquad.R;
import com.example.android.gsquad.activity.FindFriendsActivity;
import com.example.android.gsquad.model.Cover;
import com.example.android.gsquad.model.Game;
import com.example.android.gsquad.utils.Constants;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Raghvendra on 19-03-2017.
 */

public class SingleGameSelectAdapter extends RecyclerView.Adapter<SingleGameSelectAdapter.ViewHolder> {
    private Context mContext;
    private List<Game> mGameList;

    public SingleGameSelectAdapter(List<Game> gameList, Context context) {
        this.mGameList = gameList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_games, parent, false);
        final ViewHolder vh = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FindFriendsActivity.class);
                intent.putExtra(Constants.GAME_ID, mGameList.get(vh.getAdapterPosition()).getId());
                mContext.startActivity(intent);
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Cover cover = mGameList.get(position).getCover();
        if (cover != null) {
            Glide.with(mContext)
                    .load("https:" + cover.getUrl())
                    .asBitmap()
                    .error(R.drawable.no_image)
                    .into(holder.mCircleImageView);
        }
        holder.mTextView.setText(mGameList.get(position).getName());
        holder.mCircleImageView.setContentDescription(holder.mTextView.getText());
    }

    @Override
    public int getItemCount() {
        return (null != mGameList ? mGameList.size() : 0);
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
