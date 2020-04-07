package com.wlf.demo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.wlf.mediapick.MediaPicker;
import com.wlf.mediapick.entity.MediaEntity;
import com.wlf.mediapick.entity.MediaPickConstants;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MediaPicker.preload(this);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.image) {
            MediaPicker.create(this)
                    .setMaxPickNum(6)
                    .setMediaType(MediaPickConstants.MEDIA_TYPE_IMAGE)
                    .forResult(1);
        } else if (view.getId() == R.id.video) {
            MediaPicker.create(this)
                    .setMaxPickNum(9)
                    .setMediaType(MediaPickConstants.MEDIA_TYPE_VIDEO)
                    .forResult(1);
        } else if (view.getId() == R.id.image_and_video) {
            MediaPicker.create(this)
                    .setMaxPickNum(12)
                    .setMediaType(MediaPickConstants.MEDIA_TYPE_IMAGE_AND_VIDEO)
                    .forResult(1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            List<MediaEntity> list = MediaPicker.obtainMediaResults(data);
            if (list != null) {
                Log.d(TAG, list.toString());
            }
        }
    }
}
