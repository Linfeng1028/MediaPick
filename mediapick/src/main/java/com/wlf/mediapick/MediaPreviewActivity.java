package com.wlf.mediapick;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.wlf.mediapick.adapter.MediaPreviewAdapter;
import com.wlf.mediapick.entity.MediaEntity;
import com.wlf.mediapick.entity.MediaPickConfig;
import com.wlf.mediapick.manager.MediaPickManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.animation.ObjectAnimator.ofFloat;
import static com.wlf.mediapick.entity.MediaPickConstants.EXTRA_DEFAULT_POSITION;
import static com.wlf.mediapick.entity.MediaPickConstants.EXTRA_SELECT_RESULT;

public class MediaPreviewActivity extends AppCompatActivity implements MediaPickManager.OnSelectItemChangeListener {
    // TODO 为了确认是同一个对象，就先直接引用地址
    private static List<MediaEntity> sAllMedias;
    private int mDefaultPosition;
    private MediaPreviewAdapter mAdapter;
    private MediaPickManager mManager = MediaPickManager.getInstance();
    private boolean mShowBar = true;
    private MediaPickConfig mConfig = MediaPickConfig.getInstance();

    private TextView mPositionTv;
    private ViewPager mViewPager;
    private ImageView mSelectIv;
    private TextView mConfirmTv;
    private RelativeLayout mTopBarRl;
    private RelativeLayout mBottomBarRl;

    public static void launchMediaPreviewActivity(Activity activity, List<MediaEntity> allMedias, int defaultPosition, int requestCode) {
        Intent intent = new Intent(activity, MediaPreviewActivity.class);
        sAllMedias = allMedias;
        intent.putExtra(EXTRA_DEFAULT_POSITION, defaultPosition);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_activity_media_preview);
        handleIntent();
        setStatusBarColor();
        initView();
        initViewPager();
        initListener();
        onSelectItemChange(sAllMedias.get(mDefaultPosition));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void handleIntent() {
        mDefaultPosition = getIntent().getIntExtra(EXTRA_DEFAULT_POSITION, 0);
    }

    /**
     * 修改状态栏颜色
     */
    private void setStatusBarColor() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.media_status_bar));
    }

    private void initView() {
        mViewPager = findViewById(R.id.view_pager);
        mSelectIv = findViewById(R.id.iv_select);
        mConfirmTv = findViewById(R.id.tv_confirm);
        mTopBarRl = findViewById(R.id.rl_top_bar);
        mBottomBarRl = findViewById(R.id.rl_bottom_bar);
        mPositionTv = findViewById(R.id.tv_position);

        mPositionTv.setText(String.format(Locale.getDefault(), getString(R.string.media_position_format), mDefaultPosition + 1, sAllMedias.size()));
    }

    private void initViewPager() {
        mAdapter = new MediaPreviewAdapter(this, sAllMedias);
        mViewPager.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new MediaPreviewAdapter.OnItemClickListener() {
            @Override
            public void playVideo(MediaEntity entity) {
                VideoActivity.startWithPath(MediaPreviewActivity.this, entity.getPath());
            }

            @Override
            public void updateStatusBar() {
                if (mShowBar) {
                    hideBar();
                } else {
                    showBar();
                }
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                MediaEntity bean = sAllMedias.get(position);
                mSelectIv.setSelected(bean.getIndex() > 0);
                mPositionTv.setText(String.format(Locale.getDefault(), getString(R.string.media_position_format), position + 1, sAllMedias.size()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setCurrentItem(mDefaultPosition);
    }

    private void initListener() {
        mManager.addOnSelectItemChangeListener(this);
    }

    @Override
    public void onSelectItemChange(MediaEntity item) {
        mSelectIv.setSelected(item.getIndex() > 0);
        int selectItemCount = mManager.getSelectItemList().size();
        if (selectItemCount > 0) {
            mConfirmTv.setText(String.format(Locale.getDefault(), getString(R.string.media_send_select_format), selectItemCount));
        } else {
            mConfirmTv.setText(getString(R.string.media_send));
        }
    }

    public void onFinish(View view) {
        mManager.removeOnSelectItemChangeListener(this);
        finish();
    }

    public void onSelect(View view) {
        int selectItemPosition = mViewPager.getCurrentItem();
        MediaEntity selectItem = sAllMedias.get(selectItemPosition);

        if (!mSelectIv.isSelected()) {
            // 图片未选中
            if (!mManager.addSelectItemAndSetIndex(selectItem)) {
                Toast.makeText(this, String.format(Locale.getDefault(),
                        getString(R.string.media_max_send_images_or_videos_format), mConfig.maxPickNum), Toast.LENGTH_SHORT).show();
            }
        } else {
            // 大于该图片序号所有图片，序号-1
            MediaEntity item;
            for (int i = selectItem.getIndex(); i < mManager.getSelectItemList().size(); i++) {
                item = mManager.getSelectItemList().get(i);
                mManager.setNewIndexForSelectItem(item, i);
            }
            mManager.removeSelectItemAndSetIndex(selectItem);
        }
    }

    public void onSend(View view) {
        Intent intent = getIntent();
        if (mManager.getSelectItemList().size() > 0) {
            intent.putParcelableArrayListExtra(EXTRA_SELECT_RESULT, (ArrayList<? extends Parcelable>) mManager.getSelectItemList());
        } else {
            int currentPosition = mViewPager.getCurrentItem();
            MediaEntity currentEItem = sAllMedias.get(currentPosition);
            List<MediaEntity> list = new ArrayList<>();
            list.add(currentEItem);
            intent.putParcelableArrayListExtra(EXTRA_SELECT_RESULT, (ArrayList<? extends Parcelable>) list);
        }
        setResult(RESULT_OK, intent);
        mManager.removeOnSelectItemChangeListener(this);
        finish();
    }

    /**
     * 隐藏头部和尾部栏
     */
    private void hideBar() {
        mShowBar = false;
        ObjectAnimator animator = ObjectAnimator.ofFloat(mTopBarRl, "translationY",
                0, -mTopBarRl.getHeight()).setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mTopBarRl.setVisibility(View.GONE);
            }
        });
        animator.start();
        ofFloat(mBottomBarRl, "translationY", 0, mBottomBarRl.getHeight())
                .setDuration(300).start();
    }

    /**
     * 显示头部和尾部栏
     */
    private void showBar() {
        mShowBar = true;
        ObjectAnimator animator = ofFloat(mTopBarRl, "translationY",
                mTopBarRl.getTranslationY(), 0).setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (mTopBarRl != null) {
                    mTopBarRl.setVisibility(View.VISIBLE);
                }
            }
        });
        animator.start();
        ofFloat(mBottomBarRl, "translationY", mBottomBarRl.getTranslationY(), 0)
                .setDuration(300).start();
    }
}
