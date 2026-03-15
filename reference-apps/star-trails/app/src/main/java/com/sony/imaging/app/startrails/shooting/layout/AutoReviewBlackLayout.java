package com.sony.imaging.app.startrails.shooting.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.startrails.R;
import com.sony.imaging.app.startrails.util.STConstants;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class AutoReviewBlackLayout extends Layout implements NotificationListener {
    public static final String TAG = "AutoReviewBlackLayout";
    private static String[] TAGS = {STConstants.TIMER_UPDATE};
    private View mCurrentView = null;

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.mCurrentView = obtainViewFromPool(R.layout.star_trails_mute_shooting_main_sid_info_on_panel_evf);
        setVisibility(4);
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
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (STUtility.getInstance().isPreTakePictureTestShot()) {
            setVisibility(0);
        }
    }
}
