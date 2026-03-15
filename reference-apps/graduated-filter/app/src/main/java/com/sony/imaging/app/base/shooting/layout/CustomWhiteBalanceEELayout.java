package com.sony.imaging.app.base.shooting.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class CustomWhiteBalanceEELayout extends Layout {
    protected View mCurrentView = null;
    private NotificationListener mDeviceChangeListener = new NotificationListener() { // from class: com.sony.imaging.app.base.shooting.layout.CustomWhiteBalanceEELayout.1
        private String[] TAGS = {DisplayModeObserver.TAG_DEVICE_CHANGE};

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            CustomWhiteBalanceEELayout.this.updateView();
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.TAGS;
        }
    };

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        DisplayModeObserver.getInstance().setNotificationListener(this.mDeviceChangeListener);
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        DisplayModeObserver.getInstance().removeNotificationListener(this.mDeviceChangeListener);
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        createView();
        return this.mCurrentView;
    }

    private void createView() {
        int device = DisplayModeObserver.getInstance().getActiveDevice();
        if (device == 1) {
            this.mCurrentView = obtainViewFromPool(R.layout.cwb_ee_evf);
        } else {
            this.mCurrentView = obtainViewFromPool(R.layout.cwb_ee);
        }
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mCurrentView = null;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver
    protected String getKeyConvCategory() {
        return "Menu";
    }
}
