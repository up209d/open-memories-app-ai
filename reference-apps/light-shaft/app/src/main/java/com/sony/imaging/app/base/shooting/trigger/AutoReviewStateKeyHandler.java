package com.sony.imaging.app.base.shooting.trigger;

import android.app.Activity;
import android.os.Bundle;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.shooting.AutoReviewState;
import com.sony.imaging.app.base.shooting.StateBase;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.PTag;

/* loaded from: classes.dex */
public class AutoReviewStateKeyHandler extends ShootingKeyHandlerBase {
    public static final String BUNDLE_KEY_CODE = "keyCode";
    private static final String NEXT_STATE = "EE";
    private static final String PTAG_TRANSITION_EE = "Transits to EE from AutoReview";
    private static final String PTAG_TRANSITION_PLAYZOOM = "Into playzoom from AuteReview. Start";
    private static final String PTAG_TRANSITION_SIGLEPB = "Transits to SinglePb from AutoReview";

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseKeyHandler
    protected String getKeyConvCategory() {
        return ICustomKey.CATEGORY_SINGLE;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDispFuncKey() {
        moveToPlay(CustomizableFunction.DispChange);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        return pushedDispFuncKey();
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPbZoomFuncMinus() {
        moveToPlay(CustomizableFunction.PbZoomMinus);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        return Environment.getVersionOfHW() == 1 ? pushedPbZoomFuncMinus() : super.pushedDownKey();
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        moveToPlay(CustomizableFunction.MainNext);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        moveToPlay(CustomizableFunction.MainPrev);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPbZoomFuncPlus() {
        PTag.start(PTAG_TRANSITION_PLAYZOOM);
        moveToPlay(CustomizableFunction.PbZoomPlus);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        return Environment.getVersionOfHW() == 1 ? pushedPbZoomFuncPlus() : super.pushedCenterKey();
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        PTag.start(PTAG_TRANSITION_EE);
        StateBase state = (StateBase) this.target;
        state.setNextState(NEXT_STATE, null);
        return 0;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUmRemoteS1Key() {
        PTag.start(PTAG_TRANSITION_EE);
        StateBase state = (StateBase) this.target;
        state.setNextState(NEXT_STATE, null);
        return 0;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        PTag.start(PTAG_TRANSITION_SIGLEPB);
        moveToPlay(AppRoot.USER_KEYCODE.LEFT);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        PTag.start(PTAG_TRANSITION_SIGLEPB);
        moveToPlay(AppRoot.USER_KEYCODE.RIGHT);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        PTag.start(PTAG_TRANSITION_SIGLEPB);
        moveToPlay(AppRoot.USER_KEYCODE.PLAYBACK);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteFuncKey() {
        moveToPlay(CustomizableFunction.Delete);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        moveToPlay(CustomizableFunction.SubNext);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        moveToPlay(CustomizableFunction.SubPrev);
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.ShootingKeyHandlerBase, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedS1Key() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedUmRemoteS1Key() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedS2Key() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedUmRemoteS2Key() {
        return 0;
    }

    private void moveToPlay(int keyCode) {
        StateBase state = (StateBase) this.target;
        Bundle b = new Bundle();
        Bundle data = new Bundle();
        b.putParcelable(AutoReviewState.STATE_NAME, data);
        data.putParcelable(EventParcel.KEY_KEYCODE, new EventParcel(keyCode));
        Activity appRoot = state.getActivity();
        ((BaseApp) appRoot).changeApp(BaseApp.APP_PLAY, b);
    }

    private void moveToPlay(CustomizableFunction keyFunction) {
        StateBase state = (StateBase) this.target;
        Bundle b = new Bundle();
        Bundle data = new Bundle();
        b.putParcelable(AutoReviewState.STATE_NAME, data);
        data.putParcelable(EventParcel.KEY_KEYCODE, new EventParcel(keyFunction));
        Activity appRoot = state.getActivity();
        ((BaseApp) appRoot).changeApp(BaseApp.APP_PLAY, b);
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAELHoldCustomKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAfMfHoldCustomKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        StateBase state = (StateBase) this.target;
        state.setNextState(NEXT_STATE, null);
        return 0;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedFocusModeDial() {
        StateBase state = (StateBase) this.target;
        state.setNextState(NEXT_STATE, null);
        return 0;
    }
}
