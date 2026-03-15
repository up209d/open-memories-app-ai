package com.sony.imaging.app.base.menu.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.base.shooting.widget.AFLocalPositionSetting;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class AFLocalPositionMenuLayout extends BaseMenuLayout implements AFLocalPositionSetting.PositionListener {
    private int mCancelIndex = 0;
    private NotificationListener mNotificationListener = new AFLocalPositionMenuLayoutListener();

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        AFLocalPositionSetting currentView = (AFLocalPositionSetting) obtainViewFromPool(R.layout.af_local_position_setting);
        currentView.setPositionListener(this);
        this.mCancelIndex = FocusAreaController.getInstance().getFocusIndex();
        return currentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        CameraNotificationManager notifier = CameraNotificationManager.getInstance();
        notifier.setNotificationListener(this.mNotificationListener);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        CameraNotificationManager notifier = CameraNotificationManager.getInstance();
        notifier.removeNotificationListener(this.mNotificationListener);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.AFLocalPositionSetting.PositionListener
    public void onPositionChanged(int index) {
        FocusAreaController fas = FocusAreaController.getInstance();
        fas.setFocusIndex(index);
        fas.setValue(FocusAreaController.LOCAL);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        FocusAreaController fas = FocusAreaController.getInstance();
        fas.setFocusIndex(this.mCancelIndex);
        fas.setValue(FocusAreaController.LOCAL);
        this.mCancelIndex = 0;
        openPreviousMenu();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        closeMenuLayout(null);
        return 1;
    }

    /* loaded from: classes.dex */
    private class AFLocalPositionMenuLayoutListener implements NotificationListener {
        private final String[] tags;

        private AFLocalPositionMenuLayoutListener() {
            this.tags = new String[]{CameraNotificationManager.DEVICE_LENS_CHANGED};
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (CameraNotificationManager.DEVICE_LENS_CHANGED.equals(tag)) {
                AFLocalPositionMenuLayout.this.closeAFLocalPositionMenuLayout();
            }
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeAFLocalPositionMenuLayout() {
        closeMenuLayout(null);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver
    protected String getKeyConvCategory() {
        return ICustomKey.CATEGORY_FOCUS_SETTING;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected boolean isAELCancel() {
        return false;
    }
}
