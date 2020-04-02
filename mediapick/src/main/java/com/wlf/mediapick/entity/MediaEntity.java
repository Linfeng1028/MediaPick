package com.wlf.mediapick.entity;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * 图片和视频的实体类
 */
public class MediaEntity implements Parcelable {
    /**
     * 名称
     */
    private String name;
    /**
     * 文件路径
     */
    private String path;
    /**
     * 文件uri
     */
    private Uri uri;
    /**
     * 文件大小
     */
    private long size;
    /**
     * MimeType类型
     */
    private String mimeType;
    /**
     * 文件生成时间
     */
    private long addTime;
    /**
     * 选中的序号
     */
    private int index;
    /**
     * 视频时长
     */
    private long duration;
    /**
     * 图片/视频的宽
     */
    private int width;
    /**
     * 图片/视频的高
     */
    private int height;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Uri getUri() {
        return uri;
    }

    public MediaEntity setUri(Uri uri) {
        this.uri = uri;
        return this;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isVideo() {
        return !TextUtils.isEmpty(getMimeType()) && getMimeType().toLowerCase().startsWith("video");
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "MediaEntity{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", uri=" + uri +
                ", size=" + size +
                ", mimeType='" + mimeType + '\'' +
                ", addTime=" + addTime +
                ", index=" + index +
                ", duration=" + duration +
                ", width=" + width +
                ", height=" + height +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeParcelable(this.uri, flags);
        dest.writeLong(this.size);
        dest.writeString(this.mimeType);
        dest.writeLong(this.addTime);
        dest.writeInt(this.index);
        dest.writeLong(this.duration);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
    }

    public MediaEntity() {
    }

    protected MediaEntity(Parcel in) {
        this.name = in.readString();
        this.path = in.readString();
        this.uri = in.readParcelable(Uri.class.getClassLoader());
        this.size = in.readLong();
        this.mimeType = in.readString();
        this.addTime = in.readLong();
        this.index = in.readInt();
        this.duration = in.readLong();
        this.width = in.readInt();
        this.height = in.readInt();
    }

    public static final Creator<MediaEntity> CREATOR = new Creator<MediaEntity>() {
        @Override
        public MediaEntity createFromParcel(Parcel source) {
            return new MediaEntity(source);
        }

        @Override
        public MediaEntity[] newArray(int size) {
            return new MediaEntity[size];
        }
    };
}
