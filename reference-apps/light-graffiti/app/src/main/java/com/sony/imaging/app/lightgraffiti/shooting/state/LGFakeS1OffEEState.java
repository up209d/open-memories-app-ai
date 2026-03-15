package com.sony.imaging.app.lightgraffiti.shooting.state;

import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.HoldKeyServer;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.lightgraffiti.util.LGConstants;
import com.sony.imaging.app.lightgraffiti.util.LGUtility;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class LGFakeS1OffEEState extends State implements NotificationListener, LGStateHolder.ValueChangedListener {
    private static String[] TAGS = {LGConstants.SHOOTING_GUIDE_DETAIL_OPEN_EVENT, LGConstants.DISCARD_LAYOUT_OPEN_EVENT};
    private static boolean isOpendIntroGuide_2nd = false;

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        if (LGStateHolder.getInstance().isLendsProblem()) {
            LGStateHolder.getInstance().setLensProblemFlag(false);
            setNextState("FakeLensProblem", null);
            return;
        }
        openLayout("LGFakeS1OffEELayout");
        openLayout(LGS1OffEEState.LAYOUT_EE_GUIDE_EASY);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED, 0);
        isOpendIntroGuide_2nd = false;
        boolean isFirstOpen = LGUtility.getInstance().isFirstTimeOpen();
        String stage = LGStateHolder.getInstance().getShootingStage();
        if (stage.equals(LGStateHolder.SHOOTING_2ND) && isFirstOpen) {
            isOpendIntroGuide_2nd = true;
            BackUpUtil.getInstance().setPreference(LGConstants.BACKUP_KEY_2ND_INTRODUCTION_LAYOUT_OPEN_KEY, true);
            openLayout(LGConstants.LAYOUT_EE_GUIDE_DETAIL);
        }
        CameraNotificationManager.getInstance().setNotificationListener(this);
        LGStateHolder.getInstance().setValueChangedListener(this);
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        closeLayout("LGFakeS1OffEELayout");
        closeLayout(LGS1OffEEState.LAYOUT_EE_GUIDE_EASY);
        closeLayout("FakeDiscardLayout");
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        LGStateHolder.getInstance().removeValueChangedListener(this);
        if (isOpendIntroGuide_2nd) {
            closeLayout(LGConstants.LAYOUT_EE_GUIDE_DETAIL);
        }
        if (getLayout(LGConstants.LAYOUT_EE_GUIDE_DETAIL) != null) {
            getLayout(LGConstants.LAYOUT_EE_GUIDE_DETAIL).closeLayout();
        }
        if (getLayout("FakeDiscardLayout") != null) {
            getLayout("FakeDiscardLayout").closeLayout();
        }
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (tag.equals(LGConstants.SHOOTING_GUIDE_DETAIL_OPEN_EVENT)) {
            openLayout(LGConstants.LAYOUT_EE_GUIDE_DETAIL);
        } else if (tag.equalsIgnoreCase(LGConstants.DISCARD_LAYOUT_OPEN_EVENT)) {
            openLayout("FakeDiscardLayout", this.data);
        }
    }

    @Override // com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder.ValueChangedListener
    public void onValueChanged(String tag) {
        if (tag.equals(LGStateHolder.SHOOTING_STAGE) && !LGStateHolder.getInstance().getShootingStage().equals(LGStateHolder.SHOOTING_2ND)) {
            closeLayout("LGFakeS1OffEELayout");
            closeLayout(LGS1OffEEState.LAYOUT_EE_GUIDE_EASY);
            CameraNotificationManager.getInstance().removeNotificationListener(this);
            if (isOpendIntroGuide_2nd) {
                closeLayout(LGConstants.LAYOUT_EE_GUIDE_DETAIL);
            }
            removeState();
        }
    }
}
