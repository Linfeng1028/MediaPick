package com.wlf.mediapick.callback;

import com.wlf.mediapick.entity.MediaEntity;

import java.util.List;

/**
 * 加载图片/视频结束的回调
 */
public interface LoadMediaCallback {
    /**
     * 加载图片/视频成功
     *
     * @param mediaEntityList List<MediaEntity> 对象
     */
    void success(List<MediaEntity> mediaEntityList);
}
