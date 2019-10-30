package com.travelplanner;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private Context mContext;
    private ArrayList<Poi> mList;

    public RecyclerViewAdapter(Context mContext, ArrayList<Poi> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView poi_image;
        private TextView poi_name;
        private TextView poi_address;
        private RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            poi_image = itemView.findViewById(R.id.poi_image);
            poi_name = itemView.findViewById(R.id.poi_name);
            poi_address = itemView.findViewById(R.id.poi_address);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        Poi poi = mList.get(position);

        Glide.with(mContext)
                .load(poi.getPoi_image())
                .into(holder.poi_image);

        holder.poi_name.setText(poi.getPoi_name());
        holder.poi_address.setText(poi.getPoi_address());

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked on:" + poi.getPoi_name());

                Toast.makeText(mContext, poi.getPoi_name(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
