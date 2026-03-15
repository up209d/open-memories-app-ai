package com.sony.imaging.app.startrails.util;

import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.NotificationManager;

/* loaded from: classes.dex */
public class ThemeChangeListener extends NotificationManager {
    public static final String TAG_CHANGE = "ThemeChangeListener.Changed";
    private static String TAG = "ThemeChangeListener";
    private static ThemeChangeListener mTheInstance = new ThemeChangeListener();
    static NotificationListener mListener = new ThemeChangeNotifier();

    public static ThemeChangeListener getInstance() {
        return mTheInstance;
    }

    public void initialize() {
        CameraNotificationManager.getInstance().setNotificationListener(mListener);
    }

    public void terminate() {
        if (mListener != null) {
            CameraNotificationManager.getInstance().removeNotificationListener(mListener);
        }
    }

    protected ThemeChangeListener() {
    }

    @Override // com.sony.imaging.app.util.NotificationManager
    public Object getValue(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        Object ret = null;
        if (tag.equals(STConstants.THEME_TAG_UPDATED)) {
            Integer themeValue = (Integer) getValue(STConstants.THEME_TAG_UPDATED);
            if (themeValue.intValue() != -1 || themeValue.intValue() != 0) {
                ret = themeValue;
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void updateMenuRecommandedSetting() {
        AppLog.enter(TAG, AppLog.getMethodName());
        STUtility.getInstance().setUpdateThemeProperty(false);
        switch (STUtility.getInstance().getCurrentTrail()) {
            case 1:
                ThemeRecomandedMenuSetting.getInstance().updateDarkNightThemeProperty();
                break;
            case 2:
                ThemeRecomandedMenuSetting.getInstance().updateCustomtThemeProperty();
                STUtility.getInstance().setDarkNightIsoBackupNone();
                break;
            default:
                ThemeRecomandedMenuSetting.getInstance().updateBrightNightThemeProperty();
                STUtility.getInstance().setDarkNightIsoBackupNone();
                break;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    /* loaded from: classes.dex */
    static class ThemeChangeNotifier implements NotificationListener {
        private static final String[] tags = {STConstants.THEME_TAG_UPDATED};

        ThemeChangeNotifier() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            AppLog.info(ThemeChangeListener.TAG_CHANGE, "onNotify: " + tag);
            if (STConstants.THEME_TAG_UPDATED.equals(tag)) {
                ThemeChangeListener.updateMenuRecommandedSetting();
            }
        }
    }

    public void updateThemeProperty() {
        if (!STUtility.getInstance().isPlayBackKeyPressed() && STUtility.getInstance().isUpdateThemePropertyRequired()) {
            updateMenuRecommandedSetting();
        }
    }
}
