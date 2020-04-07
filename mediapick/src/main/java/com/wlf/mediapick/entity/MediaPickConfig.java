package com.wlf.mediapick.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 图片/视频配置属性的实体类
 */
public class MediaPickConfig implements Parcelable {
    /**
     * 图片/视频可选择的最大数量，默认值为9；小于等于0时不限数量
     */
    public int maxPickNum;

    /**
     * 选择媒体类型
     */
    @MediaPickConstants.MediaType
    public int mediaType;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.maxPickNum);
        dest.writeInt(this.mediaType);
    }

    private MediaPickConfig() {
        maxPickNum = 9;
        mediaType = MediaPickConstants.MEDIA_TYPE_IMAGE_AND_VIDEO;
    }

    protected MediaPickConfig(Parcel in) {
        this.maxPickNum = in.readInt();
        this.mediaType = in.readInt();
    }

    public static final Creator<MediaPickConfig> CREATOR = new Creator<MediaPickConfig>() {
        @Override
        public MediaPickConfig createFromParcel(Parcel source) {
            return new MediaPickConfig(source);
        }

        @Override
        public MediaPickConfig[] newArray(int size) {
            return new MediaPickConfig[size];
        }
    };

    public static MediaPickConfig getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final MediaPickConfig INSTANCE = new MediaPickConfig();
    }
}
