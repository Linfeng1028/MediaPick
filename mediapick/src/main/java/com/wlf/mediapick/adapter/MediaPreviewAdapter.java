package com.wlf.mediapick.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.wlf.mediapick.R;
import com.wlf.mediapick.entity.MediaEntity;

import java.util.List;

public class MediaPreviewAdapter extends PagerAdapter {

    private Context mContext;
    private List<MediaEntity> mMediaList;
    private OnItemClickListener mOnItemClickListener;

    public MediaPreviewAdapter(Context context, List<MediaEntity> mediaBeans) {
        mContext = context;
        mMediaList = mediaBeans;
    }

    @Override
    public int getCount() {
        return mMediaList == null ? 0 : mMediaList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        MediaEntity entity = mMediaList.get(position);
        View view;
        if (entity.isVideo()) {
            view = View.inflate(mContext, R.layout.media_pager_item_preview_video, null);
            ImageView videoIv = view.findViewById(R.id.iv_video);
            Glide.with(mContext)
                    .load(entity.getUri())
                    .into(videoIv);
            container.addView(view);
            ImageView playIv = view.findViewById(R.id.iv_play);
            playIv.setOnClickListener((v) -> {
                mOnItemClickListener.playVideo(entity);
            });
            videoIv.setOnClickListener((v) -> {
                mOnItemClickListener.updateStatusBar();
            });
        } else {
            view = View.inflate(mContext, R.layout.media_pager_item_preview_image, null);
            PhotoView imageView = view.findViewById(R.id.photoView);
            Glide.with(mContext)
                    .load(entity.getUri())
                    .into(imageView);
            container.addView(view);
            imageView.setOnClickListener((v) -> {
                mOnItemClickListener.updateStatusBar();
            });
        }
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void playVideo(MediaEntity entity);

        void updateStatusBar();
    }
}
