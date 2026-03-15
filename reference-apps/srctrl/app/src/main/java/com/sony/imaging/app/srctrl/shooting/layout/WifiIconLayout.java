package com.sony.imaging.app.srctrl.shooting.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.srctrl.R;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class WifiIconLayout extends Layout {
    private DisplayModeObserver mDisplayObserver = DisplayModeObserver.getInstance();
    private NotificationListener mListener = new NotificationListener() { // from class: com.sony.imaging.app.srctrl.shooting.layout.WifiIconLayout.1
        private String[] TAGS = {DisplayModeObserver.TAG_DEVICE_CHANGE, "com.sony.imaging.app.base.common.DisplayModeObserver.OledScreenChange"};

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            WifiIconLayout.this.updateView();
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.TAGS;
        }
    };

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        int device = DisplayModeObserver.getInstance().getActiveDevice();
        return device == 1 ? obtainViewFromPool(R.layout.srctrl_parts_cmn_wifi_img_evf) : obtainViewFromPool(R.layout.srctrl_parts_cmn_wifi_img_panel);
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        if (this.mListener != null) {
            this.mDisplayObserver.setNotificationListener(this.mListener);
        }
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (this.mListener != null) {
            this.mDisplayObserver.removeNotificationListener(this.mListener);
        }
        super.onPause();
    }
}
