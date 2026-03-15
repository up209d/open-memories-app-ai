package com.sony.imaging.app.startrails.exit.layout;

import com.sony.imaging.app.base.common.layout.ExitScreenLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class STExitScreenLayout extends ExitScreenLayout implements NotificationListener {
    private static String[] TAGS = {CameraNotificationManager.DEVICE_LENS_AFMF_SW_CHANGED};

    @Override // com.sony.imaging.app.base.common.layout.ExitScreenLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        CameraNotificationManager.getInstance().setNotificationListener(this);
        super.onResume();
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        super.onPause();
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (tag.equals(CameraNotificationManager.DEVICE_LENS_AFMF_SW_CHANGED)) {
            pushedMenuKey();
        }
    }
}
