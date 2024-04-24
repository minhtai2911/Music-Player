package com.example.musicplayer.activity;


import static com.example.musicplayer.activity.MainActivity.swapSongInQueue;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.QueuePlayingAdapter;


public class QueuePlayingActivity extends AppCompatActivity {
    RecyclerView recyclerViewQueue;
    QueuePlayingAdapter queuePlayingAdapter;
    ImageView backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queueplaying);
        initView();
        overridePendingTransition(R.anim.anim_intent_in, R.anim.anim_intent_out);
    }


    private void initView() {
        recyclerViewQueue = findViewById(R.id.recyclerview_queuePlaying);
        queuePlayingAdapter = new QueuePlayingAdapter(QueuePlayingActivity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(QueuePlayingActivity.this);
        recyclerViewQueue.setLayoutManager(linearLayoutManager);
        recyclerViewQueue.setAdapter(queuePlayingAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerViewQueue);
        backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(ItemTouchHelper.UP|ItemTouchHelper.DOWN,
                    ItemTouchHelper.START| ItemTouchHelper.END);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            swapSongInQueue(fromPosition, toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition,toPosition);
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            queuePlayingAdapter.deleteSong(viewHolder.getAdapterPosition());
        }
    };
}
