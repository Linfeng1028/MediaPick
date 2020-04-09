package com.wlf.mediapick;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.wlf.mediapick.callback.LoadMediaCallback;
import com.wlf.mediapick.entity.MediaEntity;
import com.wlf.mediapick.entity.MediaPickConstants;
import com.wlf.mediapick.utils.SdkUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 加载图片/视频的工具类
 */
public class LoadMediaUtils {
    /**
     * 单核线程池，用来加载图片/视频
     */
    private static ExecutorService sLoadMediaService = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

    /**
     * 本地图片列表
     */
    private static List<MediaEntity> sImageList;
    /**
     * 本地视频列表
     */
    private static List<MediaEntity> sVideoList;
    /**
     * 本地图片和视频列表
     */
    private static List<MediaEntity> sImageAndVideoList;
    /**
     * 对本地图片/视频的监听，用来更新资源
     */
    private static MediaObserver sObserver;

    /**
     * 预加载图片和视频
     *
     * @param context Context 对象
     */
    static void preload(Context context) {
        if (sObserver == null) {
            sObserver = new MediaObserver(context.getApplicationContext());
            context.getApplicationContext().getContentResolver().registerContentObserver(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, sObserver);
            context.getApplicationContext().getContentResolver().registerContentObserver(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, false, sObserver);
        }
        sLoadMediaService.execute(() -> {
            sImageList = loadImage(context);
            sVideoList = loadVideo(context);
            sImageAndVideoList = getImagesAndVideos();
        });
    }

    /**
     * 从SDCard加载图片/视频
     *
     * @param context   Context 对象
     * @param mediaType 需要加载的资源类型
     * @param callback  加载图片/视频的回调
     */
    static void loadMediaFromSDCard(Context context, @MediaPickConstants.MediaType int mediaType, LoadMediaCallback callback) {
        sLoadMediaService.execute(() -> {
            switch (mediaType) {
                case MediaPickConstants.MEDIA_TYPE_IMAGE:
                    if (sImageList == null) {
                        sImageList = loadImage(context);
                    }
                    if (callback != null) {
                        callback.success(sImageList);
                    }
                    return;
                case MediaPickConstants.MEDIA_TYPE_VIDEO:
                    if (sVideoList == null) {
                        sVideoList = loadVideo(context);
                    }
                    if (callback != null) {
                        callback.success(sVideoList);
                    }
                    return;
                case MediaPickConstants.MEDIA_TYPE_IMAGE_AND_VIDEO:
                    if (sImageAndVideoList == null) {
                        sImageList = loadImage(context);
                        sVideoList = loadVideo(context);
                        sImageAndVideoList = getImagesAndVideos();
                    }
                    if (callback != null) {
                        callback.success(sImageAndVideoList);
                    }
                    return;
            }
        });
    }

    /**
     * 加载图片
     *
     * @return 本地图片列表
     */
    private static List<MediaEntity> loadImage(Context context) {
        List<MediaEntity> images = new ArrayList<>();

        final String[] IMAGE_PROJECTION = {     //查询图片需要的数据列
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,   //图片的显示名称   aaa.jpg
                MediaStore.Images.Media.DATA,           //图片的真实路径   /storage/emulated/0/pp/downloader/wallpaper/aaa.jpg
                MediaStore.Images.Media.SIZE,           //图片的大小      long型  132492
                MediaStore.Images.Media.MIME_TYPE,      //图片的类型      image/jpeg
                MediaStore.Images.Media.DATE_ADDED,     //图片被添加的时间  long型  1450518608
                MediaStore.MediaColumns.WIDTH,          //图片的宽
                MediaStore.MediaColumns.HEIGHT};        //图片的高

        try (Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                IMAGE_PROJECTION, null, null, IMAGE_PROJECTION[5] + " DESC")) {
            while (cursor != null && cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                String imageName = cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                long imageSize = cursor.getLong(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[3]));
                String imageMimeType = cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[4]));
                long imageAddTime = cursor.getLong(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[5]));
                int imageWidth = cursor.getInt(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[6]));
                int imageHeight = cursor.getInt(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[7]));

                Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(id));

                if (!SdkUtils.hasQ()) {
                    if (TextUtils.isEmpty(imagePath)) {
                        continue;
                    }

                    File file = new File(imagePath);
                    if (!file.exists() || file.length() <= 0) {
                        continue;
                    }
                }

                MediaEntity item = new MediaEntity();
                item.setName(imageName);
                item.setPath(imagePath);
                item.setUri(uri);
                item.setSize(imageSize);
                item.setMimeType(imageMimeType);
                item.setAddTime(imageAddTime);
                item.setWidth(imageWidth);
                item.setHeight(imageHeight);
                images.add(item);
            }
        }

        sortMediaByAddTime(images);
        return images;
    }

    /**
     * 加载视频
     *
     * @return 本地视频列表
     */
    private static List<MediaEntity> loadVideo(Context context) {
        List<MediaEntity> videos = new ArrayList<>();

        final String[] VIDEO_PROJECTION = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.DURATION,
                MediaStore.MediaColumns.WIDTH,
                MediaStore.MediaColumns.HEIGHT};

        try (Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                VIDEO_PROJECTION, null, null, VIDEO_PROJECTION[5] + " DESC")) {
            while (cursor != null && cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[0]));
                String videoName = cursor.getString(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[1]));
                String videoPath = cursor.getString(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[2]));
                long videoSize = cursor.getLong(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[3]));
                String videoMimeType = cursor.getString(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[4]));
                long videoAddTime = cursor.getLong(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[5]));
                int videoDuration = cursor.getInt(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[6]));
                int videoWidth = cursor.getInt(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[7]));
                int videoHeight = cursor.getInt(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[8]));

                Uri uri = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, String.valueOf(id));

                if (!SdkUtils.hasQ()) {
                    if (TextUtils.isEmpty(videoPath)) {
                        continue;
                    }

                    File file = new File(videoPath);
                    if (!file.exists() || file.length() <= 0) {
                        continue;
                    }
                }

                MediaEntity item = new MediaEntity();
                item.setName(videoName);
                item.setPath(videoPath);
                item.setUri(uri);
                item.setSize(videoSize);
                item.setMimeType(videoMimeType);
                item.setAddTime(videoAddTime);
                item.setDuration(videoDuration);
                item.setWidth(videoWidth);
                item.setHeight(videoHeight);
                videos.add(item);
            }
        }
        sortMediaByAddTime(videos);
        return videos;
    }

    /**
     * 获取图片和视频
     *
     * @return 图片和视频列表
     */
    private static List<MediaEntity> getImagesAndVideos() {
        List<MediaEntity> imagesAndVideos = new ArrayList<>();
        imagesAndVideos.addAll(sImageList);
        imagesAndVideos.addAll(sVideoList);
        sortMediaByAddTime(imagesAndVideos);
        return imagesAndVideos;
    }

    /**
     * 对资源按添加时间排序
     *
     * @param medias 需要被排序的图片/视频
     */
    private static void sortMediaByAddTime(List<MediaEntity> medias) {
        // https://stackoverflow.com/questions/6626437/why-does-my-compare-method-throw-exception-comparison-method-violates-its-gen
        Collections.sort(medias, (o1, o2) -> {
            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1 == null) {
                return 1;
            }
            if (o2 == null) {
                return -1;
            }
            if (o1.getAddTime() > o2.getAddTime()) {
                return -1;
            }
            if (o2.getAddTime() > o1.getAddTime()) {
                return 1;
            }
            return 0;
        });
    }

    /**
     * 清除缓存
     */
    static void clearCache(Context context) {
        if (sObserver != null) {
            context.getApplicationContext().getContentResolver().unregisterContentObserver(sObserver);
            sObserver = null;
        }
        sImageList = null;
        sVideoList = null;
        sImageAndVideoList = null;
    }

    private static class MediaObserver extends ContentObserver {

        private Context context;

        MediaObserver(Context appContext) {
            super(null);
            context = appContext;
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            preload(context);
        }
    }
}
