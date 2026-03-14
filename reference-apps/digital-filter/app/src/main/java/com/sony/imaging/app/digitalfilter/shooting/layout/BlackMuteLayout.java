package com.sony.imaging.app.digitalfilter.shooting.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class BlackMuteLayout extends Layout implements NotificationListener {
    public static final String TAG = "AutoReviewBlackLayout";
    private static String[] TAGS = {GFConstants.MUTE_SCREEN};

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View currentView = obtainViewFromPool(R.layout.shooting_processing);
        return currentView;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        CameraNotificationManager.getInstance().setNotificationListener(this);
        getView().setVisibility(4);
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void onReopened() {
        super.onReopened();
        updateView();
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        getView().setVisibility(4);
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        super.onPause();
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        getView().setVisibility(0);
    }
}
