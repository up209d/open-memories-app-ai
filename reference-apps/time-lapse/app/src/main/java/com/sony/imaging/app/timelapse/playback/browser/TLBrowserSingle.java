package com.sony.imaging.app.timelapse.playback.browser;

import android.os.Bundle;
import com.sony.imaging.app.base.playback.browser.BrowserSingle;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.timelapse.angleshift.common.AngleShiftConstants;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class TLBrowserSingle extends BrowserSingle implements NotificationListener {
    private final String[] TAGS = {"ANGLE_SHIFT_ADDON"};

    @Override // com.sony.imaging.app.base.playback.browser.BrowserSingle, com.sony.imaging.app.base.playback.base.SingleStateBase, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase, com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        CameraNotificationManager.getInstance().setNotificationListener(this);
        super.onResume();
    }

    @Override // com.sony.imaging.app.base.playback.browser.BrowserSingle, com.sony.imaging.app.base.playback.base.SingleStateBase, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        transitToAngleShiftAddon();
        return 1;
    }

    private void transitToAngleShiftAddon() {
        TLCommonUtil.getInstance().setAngleShiftBoot(false);
        TLCommonUtil.getInstance().setAngleShiftBootFrom(AngleShiftConstants.PB_SINGLE);
        Bundle bundle = new Bundle();
        setNextState("Edit", bundle);
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return this.TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (tag.equalsIgnoreCase("ANGLE_SHIFT_ADDON")) {
            transitToAngleShiftAddon();
        }
    }
}
