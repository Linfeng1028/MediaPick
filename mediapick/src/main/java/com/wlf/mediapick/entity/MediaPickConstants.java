package com.wlf.mediapick.entity;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 常量类
 */
public class MediaPickConstants {
    /**
     * 图片/视频选择的结果
     */
    public static final String EXTRA_SELECT_RESULT = "select_result";
    /**
     * 初始位置
     */
    public static final String EXTRA_DEFAULT_POSITION = "default_position";
    /**
     * 视频文件的路径
     */
    public static final String EXTRA_VIDEO_PATH = "video_path";

    /**
     * 选择图片
     */
    public static final int MEDIA_TYPE_IMAGE = 1;
    /**
     * 选择视频
     */
    public static final int MEDIA_TYPE_VIDEO = 2;
    /**
     * 选择图片和视频
     */
    public static final int MEDIA_TYPE_IMAGE_AND_VIDEO = 3;

    @IntDef({MEDIA_TYPE_IMAGE, MEDIA_TYPE_VIDEO, MEDIA_TYPE_IMAGE_AND_VIDEO})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MediaType {

    }
}
