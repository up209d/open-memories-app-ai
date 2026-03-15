package com.sony.imaging.app.startrails.shooting.state;

import android.hardware.Camera;
import android.os.Message;
import android.util.Pair;
import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.Definition;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.startrails.common.caution.STInfo;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class LongExposureNRCheckState extends State {
    public static final String TAG = "LongExposureNRCheckState";

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        if (!isLongExposureNROffFromDiadem()) {
            transitNextState();
        } else {
            displayCaution();
        }
    }

    private boolean isLongExposureNROffFromDiadem() {
        boolean isLongExposureNROffFromDiadem;
        Pair<Camera.Parameters, CameraEx.ParametersModifier> supportedParams = CameraSetting.getInstance().getSupportedParameters();
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getParameters();
        if (STUtility.getInstance().isPFverOver2() && true == ((CameraEx.ParametersModifier) supportedParams.second).isSupportedLongExposureNR()) {
            boolean isLongExposureNROffFromDiadem2 = ((CameraEx.ParametersModifier) params.second).getLongExposureNR();
            if (isLongExposureNROffFromDiadem2) {
                Pair<Camera.Parameters, CameraEx.ParametersModifier> params2 = CameraSetting.getInstance().getEmptyParameters();
                ((CameraEx.ParametersModifier) params2.second).setLongExposureNR(false);
                CameraSetting.getInstance().setParameters(params2);
            }
            isLongExposureNROffFromDiadem = false;
        } else if (!STUtility.getInstance().isPFverOver2() && STUtility.getInstance().isLauncherBoot()) {
            isLongExposureNROffFromDiadem = true;
        } else {
            isLongExposureNROffFromDiadem = false;
        }
        AppLog.info(TAG, "isLongExposureNROffFromDiadem  " + isLongExposureNROffFromDiadem);
        return isLongExposureNROffFromDiadem;
    }

    private void transitNextState() {
        setNextState(MFModeCheckState.TAG, this.data);
        Message msg = Message.obtain(getHandler(), Definition.STATE_TREE_CHANGED, 0, 0);
        getHandler().sendMessageAtFrontOfQueue(msg);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        transitNextState();
        return super.pushedS1Key();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        transitNextState();
        return super.pushedCenterKey();
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        CautionUtilityClass.getInstance().executeTerminate();
        Message msg = Message.obtain(getHandler(), Definition.STATE_TREE_CHANGED, 0, 0);
        getHandler().sendMessageAtFrontOfQueue(msg);
    }

    private void displayCaution() {
        IkeyDispatchEach mKey = new IkeyDispatchEach() { // from class: com.sony.imaging.app.startrails.shooting.state.LongExposureNRCheckState.1
            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyDown(int keyCode, KeyEvent event) {
                switch (event.getScanCode()) {
                    case AppRoot.USER_KEYCODE.SK1 /* 229 */:
                    case AppRoot.USER_KEYCODE.MENU /* 514 */:
                        LongExposureNRCheckState.this.getActivity().finish();
                        return 1;
                    case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                    case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                        return 0;
                    default:
                        return 1;
                }
            }
        };
        CautionUtilityClass.getInstance().setDispatchKeyEvent(STInfo.CAUTION_ID_DLAPP_CHANGE_LONGEXPOSURE_NR_OFF, mKey);
        CautionUtilityClass.getInstance().requestTrigger(STInfo.CAUTION_ID_DLAPP_CHANGE_LONGEXPOSURE_NR_OFF);
    }
}
