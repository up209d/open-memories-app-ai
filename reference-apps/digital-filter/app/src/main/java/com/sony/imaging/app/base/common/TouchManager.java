package com.sony.imaging.app.base.common;

import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.sysutil.ScalarProperties;
import com.sony.scalar.sysutil.didep.Settings;
import java.util.HashSet;
import java.util.Set;

/* loaded from: classes.dex */
public class TouchManager {
    private DeviceChangedListener mDeviceChangedListener;
    private final boolean mIsTouchSupported;
    private Set<Layout> mTouchableLayouts;
    private static TouchManager sInstance = new TouchManager();
    private static final String[] TAGS = {DisplayModeObserver.TAG_DEVICE_CHANGE};

    public TouchManager() {
        this.mIsTouchSupported = ScalarProperties.getInt("input.tp.supported") == 1;
        this.mTouchableLayouts = new HashSet();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class DeviceChangedListener implements NotificationListener {
        private DeviceChangedListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            TouchNotificationManager.getInstance().requestNotify(TouchNotificationManager.TAG_TOUCH_PANEL_ENABLED);
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return TouchManager.TAGS;
        }
    }

    public static TouchManager getInstance() {
        return sInstance;
    }

    public void setTouchPanelEnabled(boolean enabled) {
        if (this.mIsTouchSupported) {
            boolean current = Settings.getTouchPanelEnabled() == 1;
            if (current != enabled) {
                if (enabled) {
                    Settings.setTouchPanelEnabled(1);
                } else {
                    Settings.setTouchPanelEnabled(0);
                }
                TouchNotificationManager.getInstance().requestNotify(TouchNotificationManager.TAG_TOUCH_PANEL_ENABLED);
            }
        }
    }

    public boolean isTouchPanelEnabled() {
        return this.mIsTouchSupported && Settings.getTouchPanelEnabled() == 1 && DisplayModeObserver.getInstance().getActiveDevice() == 0;
    }

    public void addTouchableLayout(Layout layout) {
        boolean added = this.mTouchableLayouts.add(layout);
        if (added && this.mTouchableLayouts.size() == 1) {
            TouchNotificationManager.getInstance().requestNotify(TouchNotificationManager.TAG_TOUCHABLE_SCREEN);
        }
    }

    public void removeTouchableLayout(Layout layout) {
        boolean removed = this.mTouchableLayouts.remove(layout);
        if (removed && this.mTouchableLayouts.isEmpty()) {
            TouchNotificationManager.getInstance().requestNotify(TouchNotificationManager.TAG_TOUCHABLE_SCREEN);
        }
    }

    public boolean isTouchableScreen() {
        return !this.mTouchableLayouts.isEmpty();
    }

    public boolean isDeviceSupportedTouch() {
        return this.mIsTouchSupported;
    }

    public void resume() {
        if (this.mDeviceChangedListener == null) {
            this.mDeviceChangedListener = new DeviceChangedListener();
            DisplayModeObserver.getInstance().setNotificationListener(this.mDeviceChangedListener);
        }
    }

    public void pause() {
        if (this.mDeviceChangedListener != null) {
            DisplayModeObserver.getInstance().removeNotificationListener(this.mDeviceChangedListener);
            this.mDeviceChangedListener = null;
        }
        this.mTouchableLayouts.clear();
    }
}
