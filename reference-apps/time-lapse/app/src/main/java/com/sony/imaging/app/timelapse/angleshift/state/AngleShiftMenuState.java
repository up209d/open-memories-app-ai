package com.sony.imaging.app.timelapse.angleshift.state;

import android.os.Bundle;
import android.view.KeyEvent;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.shooting.S1OffEEState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.timelapse.angleshift.common.AngleShiftConstants;
import com.sony.imaging.app.timelapse.angleshift.common.AngleShiftSetting;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.timelapse.playback.TimeLapsePlayRootContainer;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class AngleShiftMenuState extends MenuState implements NotificationListener {
    private final String[] TAGS = {AngleShiftConstants.TAG_AS_EXIT};

    @Override // com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        System.gc();
        CameraNotificationManager.getInstance().setNotificationListener(this);
        super.onResume();
    }

    @Override // com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        super.onPause();
        System.gc();
    }

    @Override // com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State
    public Integer getCautionMode() {
        return 13;
    }

    @Override // com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        return ApoWrapper.APO_TYPE.NONE;
    }

    @Override // com.sony.imaging.app.base.menu.MenuState
    protected void finishMenuState(Bundle bundle) {
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.MenuState
    protected boolean canRemoveState() {
        return true;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        switch (code) {
            case AppRoot.USER_KEYCODE.SK2 /* 513 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                openMenuLayout("AngleShiftFrameSelection", "ID_ANGLESHIFTFRAMESELECTIONLAYOUT");
                AngleShiftSetting.getInstance().setSelectedFrame(AngleShiftConstants.START_FRAMESETTING);
                return 1;
            case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
            case AppRoot.USER_KEYCODE.UM_MOVIE_REC /* 616 */:
            case AppRoot.USER_KEYCODE.MOVIE_REC_2ND /* 637 */:
            case AppRoot.USER_KEYCODE.IR_MOVIE_REC /* 643 */:
                openRecDisabledCaution();
                return -1;
            default:
                int ret = super.onKeyDown(keyCode, event);
                return ret;
        }
    }

    public void openRecDisabledCaution() {
        CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_CANNOT_REC_MOVIE);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        if (getLayout("ID_ANGLESHIFTFRAMESELECTIONLAYOUT") != null) {
            getLayout("ID_ANGLESHIFTFRAMESELECTIONLAYOUT").closeLayout();
        }
        if (getLayout("ID_ANGLESHIFTFRAMESETTINGLAYOUT") != null) {
            getLayout("ID_ANGLESHIFTFRAMESETTINGLAYOUT").closeLayout();
        }
        super.onPause();
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return this.TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (tag.equalsIgnoreCase(AngleShiftConstants.TAG_AS_EXIT)) {
            exitAngleShiftAddon();
        }
    }

    private void exitAngleShiftAddon() {
        Bundle bundle = new Bundle();
        if (TLCommonUtil.getInstance().getAngleShiftBootFrom().equalsIgnoreCase(AngleShiftConstants.PB_INDEX)) {
            Bundle b = new Bundle();
            b.putParcelable(EventParcel.KEY_KEYCODE, new EventParcel(CustomizableFunction.PlayIndex));
            bundle.putParcelable(S1OffEEState.STATE_NAME, b);
        } else {
            TimeLapsePlayRootContainer.isTimeLapseAngleShift = true;
        }
        setNextState(BaseApp.APP_PLAY, bundle);
    }
}
