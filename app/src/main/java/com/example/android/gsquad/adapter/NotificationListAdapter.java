package com.example.android.gsquad.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.android.gsquad.R;
import com.example.android.gsquad.model.UserBasicInfo;

import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Raghvendra on 22-03-2017.
 */

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.ViewHolder> {
    private List<UserBasicInfo> mUserBasicInfoList;
    private List<Boolean> mIsCurrentUserSender;
    private Context mContext;
    private String mCurrentUserId;

    public  NotificationListAdapter(List<UserBasicInfo> userBasicInfoList, List<Boolean> isCurrentUserSenderList,
                                    Context context, String currentUserId) {
        this.mUserBasicInfoList = userBasicInfoList;
        mIsCurrentUserSender = isCurrentUserSenderList;
        this.mContext = context;
        this.mCurrentUserId = currentUserId;

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_notifications, parent, false);
        final ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String photoUrl = mUserBasicInfoList.get(position).getPhotoUrl();
        String username = mUserBasicInfoList.get(position).getName();
        String message;
        if (mIsCurrentUserSender.get(position)) {
            message = String.format(Locale.ENGLISH, "%s %s", username, mContext.getResources()
                    .getString(R.string.send_invite_label));
            holder.mActionLinearLayout.setVisibility(View.GONE);
        } else {
            message = String.format(Locale.ENGLISH, "%s %s", username, mContext.getResources()
                    .getString(R.string.receive_invite_label));
            holder.mActionLinearLayout.setVisibility(View.VISIBLE);
            holder.mAcceptActionImageView.getDrawable().mutate().setColorFilter(
                    mContext.getResources().getColor(android.R.color.holo_green_dark), PorterDuff.Mode.SRC_IN);
            holder.mDeclineActionImageView.getDrawable().mutate().setColorFilter(
                    mContext.getResources().getColor(android.R.color.holo_red_dark), PorterDuff.Mode.SRC_IN);
        }
        Glide.with(mContext)
                .load(photoUrl)
                .asBitmap()
                .error(R.drawable.no_image)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(holder.mCircleImageView);

        holder.mNotificationStatus.setText(message);
    }

    @Override
    public int getItemCount() {
        return (null != mUserBasicInfoList ? mUserBasicInfoList.size() : 0);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView mCircleImageView;
        TextView mNotificationStatus;
        LinearLayout mActionLinearLayout;
        ImageView mAcceptActionImageView;
        ImageView mDeclineActionImageView;

        public ViewHolder(final View view) {
            super(view);
            mCircleImageView = (CircleImageView) view.findViewById(R.id.user_profile_pic);
            mNotificationStatus = (TextView) view.findViewById(R.id.notification_status_text_view);
            mActionLinearLayout = (LinearLayout) view.findViewById(R.id.action_layout);
            mAcceptActionImageView = (ImageView) view.findViewById(R.id.accept_action_icon);
            mDeclineActionImageView = (ImageView) view.findViewById(R.id.decline_action_icon);
            mAcceptActionImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Accept the request.
                    Toast.makeText(view.getContext(), "Invite accepted", Toast.LENGTH_SHORT).show();
                }
            });

            mDeclineActionImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Decline the request.
                    Toast.makeText(view.getContext(), "Invite declined", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
