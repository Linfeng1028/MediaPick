package com.wlf.mediapick.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wlf.mediapick.R;
import com.wlf.mediapick.entity.MediaEntity;
import com.wlf.mediapick.entity.MediaPickConfig;
import com.wlf.mediapick.manager.MediaPickManager;
import com.wlf.mediapick.utils.DensityUtils;
import com.wlf.mediapick.utils.MediaUtils;
import com.wlf.mediapick.utils.TimeUtils;

import java.util.List;
import java.util.Locale;

public class MediaLocalAdapter extends RecyclerView.Adapter<MediaLocalAdapter.ViewHolder> {

    public static int size;
    private Context mContext;
    private List<MediaEntity> mAllMediaList;
    private OnItemClickListener mOnItemClickListener;
    private MediaPickManager mManager;

    public MediaLocalAdapter(Context context, List<MediaEntity> data) {
        mContext = context;
        mAllMediaList = data;
        size = MediaUtils.getDisplayWidth(mContext) / 4 - DensityUtils.dp2px(mContext, 2);
        mManager = MediaPickManager.getInstance();
    }

    public void setNewData(List<MediaEntity> data) {
        mAllMediaList = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.media_recycle_item_media_local, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MediaEntity item = mAllMediaList.get(position);
        Glide.with(mContext)
                .load(item.getUri())
                .override(size)
                .centerInside()
                .placeholder(R.drawable.media_ic_place_holder)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.mMediaIv);
        if (item.isVideo()) {
            holder.mDurationTv.setVisibility(View.VISIBLE);
            holder.mDurationTv.setText(TimeUtils.makeTimeString(mContext, item.getDuration()));
        } else {
            holder.mDurationTv.setVisibility(View.GONE);
        }
        if (item.getIndex() != 0) {
            holder.mIndexTv.setText(String.valueOf(item.getIndex()));
            holder.mIndexTv.setSelected(true);
        } else {
            holder.mIndexTv.setText("");
            holder.mIndexTv.setSelected(false);
        }

        holder.mIndexTv.setOnClickListener((v) -> {
            if (item.getIndex() == 0) {
                if (!mManager.addSelectItemAndSetIndex(item)) {
                    Toast.makeText(mContext, String.format(Locale.getDefault(),
                            mContext.getString(R.string.media_max_send_images_or_videos_format), MediaPickConfig.getInstance().maxPickNum), Toast.LENGTH_SHORT).show();
                }
            } else {
                // 大于该图片序号所有图片，序号-1
                int selectMediaIndex = mAllMediaList.get(position).getIndex();
                MediaEntity entity;
                for (int i = selectMediaIndex; i < mManager.getSelectItemList().size(); i++) {
                    entity = mManager.getSelectItemList().get(i);
                    mManager.setNewIndexForSelectItem(entity, i);
                }
                mManager.removeSelectItemAndSetIndex(item);
            }
        });
        holder.mMediaIv.setOnClickListener((v) -> mOnItemClickListener.onItemClick(position));
    }

    @Override
    public int getItemCount() {
        return mAllMediaList == null ? 0 : mAllMediaList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mMediaIv;
        TextView mDurationTv;
        TextView mIndexTv;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mMediaIv = itemView.findViewById(R.id.iv_media);
            mDurationTv = itemView.findViewById(R.id.tv_duration);
            mIndexTv = itemView.findViewById(R.id.tv_index);
        }
    }
}
