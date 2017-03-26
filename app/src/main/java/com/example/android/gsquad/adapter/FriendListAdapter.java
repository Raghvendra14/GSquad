package com.example.android.gsquad.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.android.gsquad.R;
import com.example.android.gsquad.activity.UserProfileActivity;
import com.example.android.gsquad.model.UserBasicInfo;
import com.example.android.gsquad.utils.Constants;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Raghvendra on 19-03-2017.
 */

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {
    private List<UserBasicInfo> mUserBasicInfoList;
    private Context mContext;


    public FriendListAdapter(List<UserBasicInfo> userBasicInfoList, Context context) {
        this.mUserBasicInfoList = userBasicInfoList;
        this.mContext = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_friends, parent, false);
        final ViewHolder vh = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UserProfileActivity.class);
                intent.putExtra(Constants.USER_ID, mUserBasicInfoList.get(vh.getAdapterPosition()).getId());
                intent.putExtra(Constants.CALLING_ACTIVITY, false);
                mContext.startActivity(intent);
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(mContext)
                .load(mUserBasicInfoList.get(position).getPhotoUrl())
                .asBitmap()
                .error(R.drawable.no_image)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(holder.mCircleImageView);
        holder.mTextView.setText(mUserBasicInfoList.get(position).getName());
        holder.mCircleImageView.setContentDescription(holder.mTextView.getText() + " " + mContext.getResources().getString(R.string.profile_content_label));
    }

    @Override
    public int getItemCount() {
        return (null != mUserBasicInfoList ? mUserBasicInfoList.size() : 0);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView mCircleImageView;
        TextView mTextView;

        public ViewHolder(View view) {
            super(view);
            mCircleImageView = (CircleImageView) view.findViewById(R.id.friend_profile_pic);
            mTextView = (TextView) view.findViewById(R.id.friend_name_textview);
        }
    }
}
