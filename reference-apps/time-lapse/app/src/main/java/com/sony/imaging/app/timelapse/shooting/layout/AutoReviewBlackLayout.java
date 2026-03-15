package com.sony.imaging.app.timelapse.shooting.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.timelapse.R;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class AutoReviewBlackLayout extends Layout implements NotificationListener {
    public static final String TAG = "AutoReviewBlackLayout";
    private static String[] TAGS = {TimeLapseConstants.TIMER_UPDATE};
    private View mCurrentView = null;
    private boolean isBlockedShutterKey = false;

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.mCurrentView = obtainViewFromPool(R.layout.tl_mute_shooting_main_sid_info_on_panel_evf);
        setVisibility(4);
        this.isBlockedShutterKey = false;
        return this.mCurrentView;
    }

    private void setVisibility(int visibbilityMode) {
        if (this.mCurrentView != null) {
            this.mCurrentView.setVisibility(visibbilityMode);
        }
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        CameraNotificationManager.getInstance().setNotificationListener(this);
        setVisibility(4);
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void onReopened() {
        super.onReopened();
        updateView();
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        if (this.mCurrentView != null) {
            this.mCurrentView.destroyDrawingCache();
            this.mCurrentView = null;
        }
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        this.isBlockedShutterKey = false;
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        clear();
        System.gc();
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (TLCommonUtil.getInstance().isTestShot()) {
            this.isBlockedShutterKey = true;
            setVisibility(0);
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        return this.isBlockedShutterKey ? -1 : 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        return this.isBlockedShutterKey ? -1 : 0;
    }
}
