package com.travelplanner;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

public class PoiScrollViewAdapter extends RecyclerView.Adapter<PoiScrollViewAdapter.ViewHolder> {

    private static final String TAG = "PoiScrollViewAdapter";

    protected DatabaseReference database;
    private Context mContext;
    private ArrayList<Poi> mList;

    public PoiScrollViewAdapter(Context mContext, ArrayList<Poi> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView poi_scroll_image;
        private RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            poi_scroll_image = itemView.findViewById(R.id.poi_scroll_image);
            parentLayout = itemView.findViewById(R.id.poi_layout);
        }
    }

    private void writeSelected(String rank, String name, String address, String image) {
        Poi poi = new Poi(name, address, image);
        database.child(rank).setValue(poi);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_poi_item, parent, false);
        return new PoiScrollViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PoiScrollViewAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        Poi poi = mList.get(position);
        Glide.with(mContext)
                .load(poi.getPoi_image())
                .into(holder.poi_scroll_image);

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database = FirebaseDatabase.getInstance().getReference().child("Selected");
                database.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            long count = dataSnapshot.getChildrenCount() - 1;
                            count++;
                            writeSelected(Long.toString(count), poi.getPoi_name(),
                                    poi.getPoi_address(), poi.getPoi_image());

                            FirebaseDatabase.getInstance().getReference().child("Count").setValue(count);
                            Toast.makeText(mContext, poi.getPoi_name() +
                                            " is added!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            }
        });
    }

}
