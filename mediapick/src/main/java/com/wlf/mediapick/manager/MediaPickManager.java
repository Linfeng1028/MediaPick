package com.wlf.mediapick.manager;

import com.wlf.mediapick.entity.MediaEntity;
import com.wlf.mediapick.entity.MediaPickConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理图片/视频的选择和移除
 */
public class MediaPickManager {

    private static List<MediaEntity> sSelectItemList;
    private static List<OnSelectItemChangeListener> sOnSelectItemChangeListenerList = new ArrayList<>();
    private static MediaPickConfig config = MediaPickConfig.getInstance();

    public static MediaPickManager getInstance() {
        if (sSelectItemList == null) {
            sSelectItemList = new ArrayList<>();
        }
        if (sOnSelectItemChangeListenerList == null) {
            sOnSelectItemChangeListenerList = new ArrayList<>();
        }
        return SingleTonHolder.INSTANCE;
    }

    public List<MediaEntity> getSelectItemList() {
        return sSelectItemList;
    }

    /**
     * 将资源添加到选中列表中，并刷新界面
     *
     * @param item 需要选中的图片
     * @return true 选中成功; false 选中失败
     */
    public boolean addSelectItemAndSetIndex(MediaEntity item) {
        if (sSelectItemList.size() >= config.maxPickNum) {
            // 最多可选择 maxPickNum 张图片/视频
            return false;
        }
        item.setIndex(sSelectItemList.size() + 1);
        sSelectItemList.add(item);
        notifySelectItemChange(item);
        return true;
    }

    public void removeSelectItemAndSetIndex(MediaEntity item) {
        item.setIndex(0);
        sSelectItemList.remove(item);
        notifySelectItemChange(item);
    }

    public void setNewIndexForSelectItem(MediaEntity item, int newIndex) {
        item.setIndex(newIndex);
        notifySelectItemChange(item);
    }

    public void addOnSelectItemChangeListener(OnSelectItemChangeListener onSelectItemChangeListener) {
        if (!sOnSelectItemChangeListenerList.contains(onSelectItemChangeListener)) {
            sOnSelectItemChangeListenerList.add(onSelectItemChangeListener);
        }
    }

    public void removeOnSelectItemChangeListener(OnSelectItemChangeListener onSelectItemChangeListener) {
        sOnSelectItemChangeListenerList.remove(onSelectItemChangeListener);
    }

    public void destroy() {
        for (OnSelectItemChangeListener listener : sOnSelectItemChangeListenerList) {
            removeOnSelectItemChangeListener(listener);
        }
        sOnSelectItemChangeListenerList = null;
        for (MediaEntity entity : sSelectItemList) {
            entity.setIndex(0);
        }
        sSelectItemList = null;
    }

    private void notifySelectItemChange(MediaEntity item) {
        for (OnSelectItemChangeListener listener : sOnSelectItemChangeListenerList) {
            listener.onSelectItemChange(item);
        }
    }

    public interface OnSelectItemChangeListener {
        void onSelectItemChange(MediaEntity item);
    }

    private static class SingleTonHolder {
        private static final MediaPickManager INSTANCE = new MediaPickManager();
    }
}
