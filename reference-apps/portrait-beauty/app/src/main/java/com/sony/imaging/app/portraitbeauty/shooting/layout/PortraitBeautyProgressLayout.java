package com.sony.imaging.app.portraitbeauty.shooting.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.portraitbeauty.R;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyConstants;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class PortraitBeautyProgressLayout extends Layout {
    private NotificationListener progressListener = new NotificationListener() { // from class: com.sony.imaging.app.portraitbeauty.shooting.layout.PortraitBeautyProgressLayout.1
        private final String[] tags = {PortraitBeautyConstants.PROCESSING_PROGRESS_START, PortraitBeautyConstants.PROCESSING_PROGRESS_STOP};

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (tag.equals(PortraitBeautyConstants.PROCESSING_PROGRESS_START)) {
                PortraitBeautyProgressLayout.this.getView().setVisibility(0);
            } else if (tag.equals(PortraitBeautyConstants.PROCESSING_PROGRESS_STOP)) {
                PortraitBeautyProgressLayout.this.getView().setVisibility(4);
            }
        }
    };

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = obtainViewFromPool(R.layout.shooting_main_progress);
        return view;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        getView().setVisibility(4);
        CameraNotificationManager.getInstance().setNotificationListener(this.progressListener);
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        getView().setVisibility(4);
        CameraNotificationManager.getInstance().removeNotificationListener(this.progressListener);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        if (getView().getVisibility() == 0) {
            return -1;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        if (getView().getVisibility() == 0) {
            return -1;
        }
        return super.onKeyUp(keyCode, event);
    }
}
