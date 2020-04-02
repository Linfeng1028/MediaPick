package com.wlf.mediapick;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import static com.wlf.mediapick.entity.MediaPickConstants.EXTRA_VIDEO_PATH;

public class VideoActivity extends Activity {
    private VideoView mVideoView;
    private String mVideoPath;
    private ImageView mPlayIv;

    public static void startWithPath(Context context, String filePath) {
        if (TextUtils.isEmpty(filePath))
            return;
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra(EXTRA_VIDEO_PATH, filePath);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_activity_video);

        handleIntent();
        initView();
        initVideoView();
        mVideoView.start();
    }

    private void handleIntent() {
        mVideoPath = getIntent().getStringExtra(EXTRA_VIDEO_PATH);
    }

    private void initView() {
        mVideoView = findViewById(R.id.video_view);
        mPlayIv = findViewById(R.id.iv_play);
    }

    private void initVideoView() {
        mVideoView.setVideoPath(mVideoPath);
        mVideoView.setMediaController(new MediaController(this));
        mVideoView.setOnPreparedListener(mp -> mPlayIv.setVisibility(View.GONE));
        mVideoView.setOnCompletionListener(mp -> mPlayIv.setVisibility(View.VISIBLE));
    }

    public void onPlay(View view) {
        mPlayIv.setVisibility(View.GONE);
        mVideoView.start();
    }
}
