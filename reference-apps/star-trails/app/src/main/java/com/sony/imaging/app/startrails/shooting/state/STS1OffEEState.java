package com.sony.imaging.app.startrails.shooting.state;

import com.sony.imaging.app.base.shooting.S1OffEEState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.startrails.util.STBackUpKey;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class STS1OffEEState extends S1OffEEState implements NotificationListener {
    public static String TAG = "STS1OffEEState";
    public static String INTRODUCTION_LAYOUT = "INTRODUCTION_LAYOUT";
    public static Boolean isOpenedIntroGuide = false;
    private static String[] TAGS = {CameraNotificationManager.DEVICE_LENS_AFMF_SW_CHANGED};

    @Override // com.sony.imaging.app.base.shooting.S1OffEEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        STUtility.checkIRISState();
        super.onResume();
        CameraNotificationManager.getInstance().setNotificationListener(this);
        isOpenedIntroGuide = false;
        Boolean isFirstOpen = Boolean.valueOf(BackUpUtil.getInstance().getPreferenceBoolean(STBackUpKey.INTRODUCTION_LAYOUT_OPEN_KEY, false));
        if (!isFirstOpen.booleanValue()) {
            BackUpUtil.getInstance().setPreference(STBackUpKey.INTRODUCTION_LAYOUT_OPEN_KEY, true);
            openLayout(INTRODUCTION_LAYOUT);
            isOpenedIntroGuide = true;
        }
        STUtility.getInstance().setIsEEStateBoot(false);
    }

    @Override // com.sony.imaging.app.base.shooting.S1OffEEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        STUtility.checkIRISState();
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        if (isOpenedIntroGuide.booleanValue()) {
            closeLayout(INTRODUCTION_LAYOUT);
        }
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        STUtility.checkIRISState();
        super.onDestroy();
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (tag.equals(CameraNotificationManager.DEVICE_LENS_AFMF_SW_CHANGED)) {
            setNextState(STMFModeCheckState.TAG, null);
        }
    }
}
