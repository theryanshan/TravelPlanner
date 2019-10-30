package com.travelplanner;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

public class ListActivity extends AppCompatActivity {

    private static final String TAG = "ListActivity";

    private ArrayList<Sight> sightsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Log.d(TAG, "onCreate: started.");

        initImageBitmaps();
    }

    private void initImageBitmaps(){

        Log.d(TAG, "initImageBitmaps: preparing bitmaps.");

        sightsList = new ArrayList<>();

        sightsList.add(new Sight("https://lh5.googleusercontent.com/p/AF1QipMOj6JVpBmVhD8OtWqyjQdyWiFrL4bINQLGbkxB=w408-h272-k-no",
                "Eiffel Tower" ));
        sightsList.add(new Sight("https://lh5.googleusercontent.com/p/AF1QipOHsCei7y32AP7cyaAd147_9_6LeEcWMzatt4Fh=w408-h544-k-no",
                "Golden Gate Park" ));
        sightsList.add(new Sight("https://lh5.googleusercontent.com/p/AF1QipMHa-S-Zn8BITCSBVqk5m-fWQBB6weD2jBzCqQM=w408-h696-k-no",
                "Statue of Liberty" ));
        sightsList.add(new Sight("https://lh5.googleusercontent.com/p/AF1QipOEBZASPVo4C_PXLTsxObDqt6lvl_XqHD0c7hEU=w408-h306-k-no",
                "Ferry Building" ));
        sightsList.add(new Sight("https://lh5.googleusercontent.com/p/AF1QipNaHyF0wO-4HZgZlcSvii9KqihAVwll7ZMbfGGJ=w408-h725-k-no",
                "Trinity Church" ));
        sightsList.add(new Sight("https://lh5.googleusercontent.com/p/AF1QipNnGjrU44Rnr1tT6EkGjEEuXjLtSMuKWLDqiKC_=w408-h544-k-no",
                "Charging Bull" ));

        initRecyclerView();
    }

    private void initRecyclerView(){

        Log.d(TAG, "initRecyclerView: init recycler view.");

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.Adapter adapter = new RecyclerViewAdapter(this, sightsList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView.ItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.START | ItemTouchHelper.END) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder dragged,
                                  @NonNull RecyclerView.ViewHolder target) {

                int position_dragged = dragged.getAdapterPosition();
                int position_target = target.getAdapterPosition();

                Collections.swap(sightsList, position_dragged, position_target);
                adapter.notifyItemMoved(position_dragged, position_target);
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                int position = viewHolder.getAdapterPosition();
                sightsList.remove(position);
                adapter.notifyItemRemoved(position);
            }
        });

        helper.attachToRecyclerView(recyclerView);
    }
}
