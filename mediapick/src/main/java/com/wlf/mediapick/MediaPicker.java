package com.wlf.mediapick;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.wlf.mediapick.entity.MediaEntity;
import com.wlf.mediapick.entity.MediaPickConfig;
import com.wlf.mediapick.entity.MediaPickConstants;

import java.util.ArrayList;
import java.util.List;

import static com.wlf.mediapick.entity.MediaPickConstants.EXTRA_SELECT_RESULT;

public class MediaPicker {

    private Activity mActivity;
    private MediaPickConfig config = MediaPickConfig.getInstance();

    private MediaPicker(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public static MediaPicker create(Activity activity) {
        return new MediaPicker(activity);
    }

    /**
     * 解析返回的资源结果
     *
     * @param data onActivityResult返回的Intent对象
     * @return 资源列表
     */
    public static List<MediaEntity> obtainMediaResults(Intent data) {
        if (data != null) {
            List<MediaEntity> list = data.getParcelableArrayListExtra(EXTRA_SELECT_RESULT);
            return list != null ? list : new ArrayList<>();
        }
        return new ArrayList<>();
    }

    /**
     * 预加载图片和视频
     *
     * @param context Context对象
     */
    public static void preload(Context context) {
        LoadMediaUtils.preload(context);
    }

    /**
     * 清除缓存
     *
     * @param context Context对象
     */
    public static void clearCache(Context context) {
        LoadMediaUtils.clearCache(context);
    }

    /**
     * 设置最大选择数量
     *
     * @param maxPickNum 最大选择数
     * @return MediaPicker 对象
     */
    public MediaPicker setMaxPickNum(int maxPickNum) {
        config.maxPickNum = maxPickNum;
        return this;
    }

    /**
     * 设置选择的媒体类型
     *
     * @param mediaType 媒体类型
     * @return MediaPicker 对象
     */
    public MediaPicker setMediaType(@MediaPickConstants.MediaType int mediaType) {
        config.mediaType = mediaType;
        return this;
    }

    /**
     * 启动资源选择器并返回值
     *
     * @param requestCode requestCode
     */
    public void forResult(int requestCode) {
        Intent intent = new Intent(mActivity, MediaPickActivity.class);
        mActivity.startActivityForResult(intent, requestCode);
    }
}
