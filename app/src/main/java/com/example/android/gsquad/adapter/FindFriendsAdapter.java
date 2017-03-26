package com.example.android.gsquad.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.gsquad.R;
import com.example.android.gsquad.activity.MapActivity;
import com.example.android.gsquad.activity.UserProfileActivity;
import com.example.android.gsquad.model.UserBasicInfo;
import com.example.android.gsquad.utils.Constants;
import com.example.android.gsquad.utils.Utils;
import com.google.android.gms.maps.model.LatLng;

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
    private List<Boolean> mShowLocationList;
    private Map<String, LatLng> mNearbyUserLatLngs;
    private LatLng mCurrentUserLatLng;
    private Context mContext;

    public FindFriendsAdapter (List<UserBasicInfo> userBasicInfoList, Map<String, Double> nearbyUserDistance,
                               List<Boolean> showLocationList, Map<String, LatLng> nearbyUserLatLngs,
                               LatLng currentUserLatLng, Context context) {
        this.mUserBasicInfoList = userBasicInfoList;
        this.mNearbyUserDistance = nearbyUserDistance;
        this.mShowLocationList = showLocationList;
        this.mNearbyUserLatLngs = nearbyUserLatLngs;
        this.mCurrentUserLatLng = currentUserLatLng;
        this.mContext = context;
    }


    @Override
    public FindFriendsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_nearby_people, parent, false);
        final ViewHolder vh = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UserProfileActivity.class);
                intent.putExtra(Constants.USER_ID, mUserBasicInfoList.get(vh.getAdapterPosition()).getId());
                intent.putExtra(Constants.CALLING_ACTIVITY, true);
                mContext.startActivity(intent);
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(FindFriendsAdapter.ViewHolder holder, int position) {
        final String picUrl = mUserBasicInfoList.get(position).getPhotoUrl();
        final LatLng userLatLng = mNearbyUserLatLngs.get(mUserBasicInfoList.get(position).getId());
        final String userName = mUserBasicInfoList.get(position).getName();
        Glide.with(mContext)
                .load(picUrl)
                .asBitmap()
                .error(R.drawable.no_image)
                .into(holder.mCircularImageView);

        holder.mTextView.setText(mUserBasicInfoList.get(position).getName());
        if (mShowLocationList.get(position)) {
            double distanceInKilometers = Utils.getDistanceInKilometers(mNearbyUserDistance.get(mUserBasicInfoList
                    .get(position).getId()));
            String distanceString = String.format(Locale.ENGLISH, "%.1f %s", distanceInKilometers,
                    mContext.getResources().getString(R.string.distance_label));
            holder.mDistanceTextView.setText(distanceString);
            holder.mMapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MapActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(Constants.CURRENT_LATLNG, mCurrentUserLatLng);
                    bundle.putParcelable(Constants.USER_LATLNG, userLatLng);
                    bundle.putString(Constants.USER_NAME, userName);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            });
        } else {
            holder.mDistanceTextView.setVisibility(View.INVISIBLE);
            holder.mMapButton.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return (null != mUserBasicInfoList ? mUserBasicInfoList.size() : 0);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView mCircularImageView;
        TextView mTextView;
        TextView mDistanceTextView;
        Button mMapButton;
        public ViewHolder(View view) {
            super(view);
            mCircularImageView = (CircleImageView) view.findViewById(R.id.nearby_people_image_view);
            mTextView = (TextView) view.findViewById(R.id.nearby_people_text_view);
            mDistanceTextView = (TextView) view.findViewById(R.id.distance_text_view);
            mMapButton = (Button) view.findViewById(R.id.map_launcher_button);
        }
    }
}
