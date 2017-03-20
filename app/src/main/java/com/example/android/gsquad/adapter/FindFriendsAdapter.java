package com.example.android.gsquad.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.gsquad.R;
import com.example.android.gsquad.model.UserBasicInfo;
import com.example.android.gsquad.utils.Utils;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Raghvendra on 19-03-2017.
 */

public class FindFriendsAdapter extends RecyclerView.Adapter<FindFriendsAdapter.ViewHolder> {
    private List<UserBasicInfo> mUserBasicInfoList;
    private Map<String, Double> mNearbyUserDistance;
    private Context mContext;

    public FindFriendsAdapter (List<UserBasicInfo> userBasicInfoList, Map<String, Double> nearbyUserDistance,
                               Context context) {
        this.mUserBasicInfoList = userBasicInfoList;
        this.mNearbyUserDistance = nearbyUserDistance;
        this.mContext = context;
    }


    @Override
    public FindFriendsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_nearby_people, parent, false);
        final ViewHolder vh = new ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(FindFriendsAdapter.ViewHolder holder, int position) {
        String picUrl = mUserBasicInfoList.get(position).getPhotoUrl();
        Glide.with(mContext)
                .load(picUrl)
                .asBitmap()
                .error(R.drawable.no_image)
                .into(holder.mCircularImageView);

        holder.mTextView.setText(mUserBasicInfoList.get(position).getName());
        double distanceInKilometers = Utils.getDistanceInKilometers(mNearbyUserDistance.get(mUserBasicInfoList
                .get(position).getId()));
        String distanceString = String.format(Locale.ENGLISH, "%.2f %s", distanceInKilometers,
                mContext.getResources().getString(R.string.distance_label));
        holder.mDistanceTextView.setText(distanceString);
    }

    @Override
    public int getItemCount() {
        return (null != mUserBasicInfoList ? mUserBasicInfoList.size() : 0);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView mCircularImageView;
        TextView mTextView;
        TextView mDistanceTextView;
        public ViewHolder(View view) {
            super(view);
            mCircularImageView = (CircleImageView) view.findViewById(R.id.nearby_people_image_view);
            mTextView = (TextView) view.findViewById(R.id.nearby_people_text_view);
            mDistanceTextView = (TextView) view.findViewById(R.id.distance_text_view);
        }
    }
}
