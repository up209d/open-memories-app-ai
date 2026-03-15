package com.sony.imaging.app.base.shooting;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.caution.CautionProcessingFunction;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;

/* loaded from: classes.dex */
public class ExposureModeCheckState extends State {
    private static final String DATA_TAG_ITEMID = "ItemId";
    private static final String TAG = "ShaftExposureModeCheckState";
    private CautionUtilityClass.triggerCautionCallback mCallback = null;

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        ExposureModeController emc = ExposureModeController.getInstance();
        String expMode = emc.getValue(null);
        if (ModeDialDetector.hasModeDial()) {
            Log.i(TAG, "this set has modedial");
            if (!emc.isValidDialPosition()) {
                if (emc.getCautionId() != 65535) {
                    ExecutorCreator.getInstance().stableSequence((BaseShootingExecutor.ReleasedListener) null);
                    CautionUtilityClass.getInstance().setDispatchKeyEvent(emc.getCautionId(), getKeyHandler());
                    CautionUtilityClass.getInstance().requestTrigger(emc.getCautionId());
                    return;
                }
                return;
            }
            transitState(null);
            return;
        }
        if (!emc.isAvailable(expMode)) {
            ExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, expMode);
        }
        transitState(null);
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        ExposureModeController emc = ExposureModeController.getInstance();
        CautionUtilityClass.getInstance().disapperTrigger(emc.getCautionId());
        CautionUtilityClass.getInstance().setDispatchKeyEvent(emc.getCautionId(), null);
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        Bundle b = new Bundle();
        Bundle data = new Bundle();
        data.putParcelable(EventParcel.KEY_KEYCODE, new EventParcel(AppRoot.USER_KEYCODE.PLAYBACK));
        b.putParcelable(S1OffEEState.STATE_NAME, data);
        Activity appRoot = getActivity();
        ((BaseApp) appRoot).changeApp(BaseApp.APP_PLAY, b);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayIndexKey() {
        Bundle b = new Bundle();
        Bundle data = new Bundle();
        data.putParcelable(EventParcel.KEY_KEYCODE, new EventParcel(CustomizableFunction.PlayIndex));
        b.putParcelable(S1OffEEState.STATE_NAME, data);
        Activity appRoot = getActivity();
        ((BaseApp) appRoot).changeApp(BaseApp.APP_PLAY, b);
        return 1;
    }

    void transitState(String itemId) {
        Bundle b = this.data;
        if (b == null) {
            b = new Bundle();
        }
        b.putString("ItemId", itemId);
        setNextState(getNextState(), b);
        ExecutorCreator.getInstance().updateSequence();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getNextState() {
        return "EE";
    }

    protected String getDefaultExposureMode() {
        return "ProgramAuto";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public IkeyDispatchEach getKeyHandler() {
        InvalidExposureModeKeyHandler changeModeKeyHandler = new InvalidExposureModeKeyHandler();
        return changeModeKeyHandler;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class InvalidExposureModeKeyHandler extends IkeyDispatchEach {
        public InvalidExposureModeKeyHandler() {
        }

        public InvalidExposureModeKeyHandler(CautionProcessingFunction p, Layout l) {
            super(p, l);
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPlayBackKey() {
            CautionUtilityClass.getInstance().executeTerminate();
            BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELECT);
            ExposureModeCheckState.this.pushedPlayBackKey();
            return 1;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedMovieRecKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedUmRemoteRecKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedIRRecKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedShootingModeKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedFnKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedModeDial() {
            if (ModeDialDetector.getModeDialPosition() != -1) {
                CautionUtilityClass.getInstance().executeTerminate();
                ExposureModeCheckState.this.transitState(ExposureModeController.EXPOSURE_MODE);
                return 0;
            }
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedMenuKey() {
            ExposureModeCheckState.this.getActivity().finish();
            BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELECT);
            return 1;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedEVDial() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedFocusModeDial() {
            return 0;
        }
    }
}
