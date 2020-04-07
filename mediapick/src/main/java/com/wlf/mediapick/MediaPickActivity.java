package com.wlf.mediapick;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wlf.mediapick.adapter.MediaLocalAdapter;
import com.wlf.mediapick.entity.MediaEntity;
import com.wlf.mediapick.entity.MediaPickConfig;
import com.wlf.mediapick.manager.MediaPickManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.wlf.mediapick.entity.MediaPickConstants.EXTRA_SELECT_RESULT;
import static com.wlf.mediapick.entity.MediaPickConstants.MEDIA_TYPE_IMAGE;
import static com.wlf.mediapick.entity.MediaPickConstants.MEDIA_TYPE_IMAGE_AND_VIDEO;
import static com.wlf.mediapick.entity.MediaPickConstants.MEDIA_TYPE_VIDEO;

@RuntimePermissions
public class MediaPickActivity extends AppCompatActivity implements
        MediaPickManager.OnSelectItemChangeListener, MediaLocalAdapter.OnItemClickListener {

    private static final int REQUEST_CODE_GET_MEDIA_LIST = 1;

    private static final String TAG = "MediaPickActivity";

    private MediaLocalAdapter mMediaAdapter;
    private MediaPickConfig mConfig = MediaPickConfig.getInstance();
    private List<MediaEntity> mMediaList = new ArrayList<>();
    private MediaPickManager mManger = MediaPickManager.getInstance();

    private RecyclerView mRecyclerView;
    private TextView mConfirmTv;
    private TextView mPreviewTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_activity_media_pick);
        initView();
        initRecyclerView();
        initListener();
        MediaPickActivityPermissionsDispatcher.loadLocalMediaWithPermissionCheck(MediaPickActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mManger.destroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_GET_MEDIA_LIST && data != null) {
            Intent intent = getIntent();
            intent.putParcelableArrayListExtra(EXTRA_SELECT_RESULT, (ArrayList<? extends Parcelable>) MediaPicker.obtainMediaResults(data));
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.recycler_view);
        mConfirmTv = findViewById(R.id.tv_confirm);
        mPreviewTv = findViewById(R.id.tv_preview);
        TextView titleTv = findViewById(R.id.tv_title);

        setStatusBarColor();
        if (mConfig.mediaType == MEDIA_TYPE_IMAGE_AND_VIDEO) {
            titleTv.setText(getString(R.string.media_image_and_video));
        } else if (mConfig.mediaType == MEDIA_TYPE_IMAGE) {
            titleTv.setText(getString(R.string.media_image));
        } else if (mConfig.mediaType == MEDIA_TYPE_VIDEO) {
            titleTv.setText(getString(R.string.media_video));
        }
    }

    /**
     * 修改状态栏颜色
     */
    private void setStatusBarColor() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.media_status_bar));
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        mRecyclerView.setHasFixedSize(true);
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setSupportsChangeAnimations(false);
        mRecyclerView.setItemAnimator(itemAnimator); // 防止item更新时发生闪烁
        mMediaAdapter = new MediaLocalAdapter(this, mMediaList);
        mRecyclerView.setAdapter(mMediaAdapter);
    }

    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE})
    void loadLocalMedia() {
        LoadMediaUtils.loadMediaFromSDCard(this, mConfig.mediaType, mediaEntityList ->
                runOnUiThread(() -> {
                    mMediaList.clear();
                    mMediaList.addAll(mediaEntityList);
                    if (mConfig.maxPickNum <= 0) {
                        mConfig.maxPickNum = mediaEntityList.size();
                    }
                    setNewData();
                }));
    }

    private void initListener() {
        mManger.addOnSelectItemChangeListener(this);
        mMediaAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MediaPickActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    public void onPreview(View view) {
        List<MediaEntity> selectItems = new ArrayList<>(mManger.getSelectItemList());
        MediaPreviewActivity.launchMediaPreviewActivity(this, selectItems, 0, REQUEST_CODE_GET_MEDIA_LIST);
    }

    public void onFinish(View view) {
        finish();
    }

    public void onSend(View view) {
        Intent intent = getIntent();
        intent.putParcelableArrayListExtra(EXTRA_SELECT_RESULT, (ArrayList<? extends Parcelable>) mManger.getSelectItemList());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onSelectItemChange(MediaEntity item) {
        int position = mMediaList.indexOf(item);
        mMediaAdapter.notifyItemChanged(position);
        int selectItemCount = mManger.getSelectItemList().size();
        if (selectItemCount > 0) {
            mConfirmTv.setText(String.format(Locale.getDefault(), getString(R.string.media_send_select_of_all_format), selectItemCount, mConfig.maxPickNum));
            mConfirmTv.setEnabled(true);
            mPreviewTv.setText(String.format(Locale.getDefault(), getString(R.string.media_preview_select_format), selectItemCount));
            mPreviewTv.setTextColor(getColor(R.color.media_white));
            mPreviewTv.setEnabled(true);
        } else {
            mConfirmTv.setText(getString(R.string.media_send));
            mConfirmTv.setEnabled(false);
            mPreviewTv.setText(getString(R.string.media_preview));
            mPreviewTv.setTextColor(getColor(R.color.media_preview_un_enable));
            mPreviewTv.setEnabled(false);
        }
    }

    @Override
    public void onItemClick(int position) {
        MediaPreviewActivity.launchMediaPreviewActivity(this, mMediaList, position, REQUEST_CODE_GET_MEDIA_LIST);
    }

    private void setNewData() {
        mMediaAdapter.setNewData(mMediaList);
        mRecyclerView.scrollToPosition(0);
    }
}
