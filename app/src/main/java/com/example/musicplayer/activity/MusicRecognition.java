package com.example.musicplayer.activity;

import static com.example.musicplayer.activity.MainActivity.getAllSongs;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.musicplayer.R;
import com.example.musicplayer.viewmodel.MusicRecognitionViewModel;
//import com.example.musicplayer.viewmodel.MusicRecognitionViewModel;

public class MusicRecognition extends AppCompatActivity {
    public static final int REQUEST_CODE = 1;
    private ObjectAnimator idleFloatingActionButtonObjectAnimator;
    private ObjectAnimator idleRecordingButtonObjectAnimator;
    private ImageButton btnRecord;
    private MusicRecognitionViewModel musicRecognitionViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_musicrecognise);
        permission();
        musicRecognitionViewModel = new MusicRecognitionViewModel();
        MusicRecord();
    }


    private void permission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MusicRecognition.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE);
        } else {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                ActivityCompat.requestPermissions(MusicRecognition.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE);
            }
        }
    }

    private void MusicRecord() {
        btnRecord = findViewById(R.id.shazam_record);
        btnRecord.setOnClickListener(v -> {
//            MusicRecognitionViewModel.start();

            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED) {
                musicRecognitionViewModel.start();
            } else {
                ActivityCompat.requestPermissions(
                        this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE
                );
            }

        });

        idleFloatingActionButtonObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(

                btnRecord,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0F, 1.4F),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0F, 1.4F)
        );
        idleFloatingActionButtonObjectAnimator.setDuration(2000L);
        idleFloatingActionButtonObjectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        idleFloatingActionButtonObjectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        idleFloatingActionButtonObjectAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        idleFloatingActionButtonObjectAnimator.start();
        idleRecordingButtonObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(
                btnRecord,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0F, 1.4F),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0F, 1.4F)
        );
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        idleFloatingActionButtonObjectAnimator.cancel();
        idleRecordingButtonObjectAnimator.cancel();}

}
