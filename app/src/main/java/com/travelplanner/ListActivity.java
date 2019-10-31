package com.travelplanner;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    private static final String TAG = "ListActivity";

    private ArrayList<Poi> mList;
    private ArrayList<String> keys;
    protected DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Log.d(TAG, "onCreate: started.");

        fetchPoiDB();
    }

    private void fetchPoiDB(){

        Log.d(TAG, "fetchPoiDB: fetch poi from database.");

        mList = new ArrayList<>();
        keys = new ArrayList<>();

        database = FirebaseDatabase.getInstance().getReference("Selected");

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot poiSnapshot : dataSnapshot.getChildren()){
                    Poi poi = poiSnapshot.getValue(Poi.class);
                    mList.add(poi);
                    keys.add(poiSnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: Error occured");
            }
        });

        initRecyclerView();
    }

    private void initRecyclerView(){

        Log.d(TAG, "initRecyclerView: init recycler view.");

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.Adapter adapter = new RecyclerViewAdapter(this, mList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView.ItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.START | ItemTouchHelper.END) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder dragged,
                                  @NonNull RecyclerView.ViewHolder target) {

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                int position = viewHolder.getAdapterPosition();
                mList.remove(position);
                adapter.notifyItemRemoved(position);
                deleteItem(position);
            }
        });
        helper.attachToRecyclerView(recyclerView);
    }

    public void deleteItem(int position) {
        String key = keys.get(position);
        database = FirebaseDatabase.getInstance().getReference().child("Selected");
        database.child(key).removeValue();
    }
}
