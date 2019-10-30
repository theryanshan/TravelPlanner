package com.travelplanner;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PoiScrollViewConfig {
    private Context mContext;
    private PoisAdapter mPoisAdapter;
    public void setConfig(RecyclerView recyclerView, Context context,
                          List<Poi> pois, List<String> keys) {
        mContext = context;
        mPoisAdapter = new PoisAdapter(pois, keys);
        recyclerView.setLayoutManager(new LinearLayoutManager((context)));
        recyclerView.setAdapter(mPoisAdapter);
    }


    class PoiInfoView extends RecyclerView.ViewHolder {
        private ImageView poiImage;
        private String key;

        public PoiInfoView(ViewGroup parent) {
            super(LayoutInflater.from(mContext).inflate(R.layout.activity_maps, parent,
                    false));
            poiImage = (ImageView) itemView.findViewById(R.id.poi_image_button);
        }

        public void bind(Poi poi, String key) {
            poiImage.setImageURI(Uri.parse(poi.getPoi_image()));
            this.key = key;
        }
    }
    class PoisAdapter extends RecyclerView.Adapter<PoiInfoView> {
        private List<Poi> mPoiList;
        private List<String> mKeys;

        public PoisAdapter(List<Poi> mPoiList, List<String> mKeys) {
            this.mPoiList = mPoiList;
            this.mKeys = mKeys;
        }

        @NonNull
        @Override
        public PoiInfoView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new PoiInfoView(parent);
        }

        @Override
        public void onBindViewHolder(PoiInfoView holder, int position) {
            holder.bind(mPoiList.get(position), mKeys.get(position));
        }

        @Override
        public int getItemCount() {
            return mPoiList.size();
        }
    }

}
