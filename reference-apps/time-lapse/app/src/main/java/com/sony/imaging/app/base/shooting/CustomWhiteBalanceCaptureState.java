package com.sony.imaging.app.base.shooting;

import android.os.Bundle;
import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.HoldKeyServer;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.hardware.CameraEx;
import java.util.Random;

/* loaded from: classes.dex */
public class CustomWhiteBalanceCaptureState extends StateBase {
    private static final String LOG_CUSTOM_WHITEBALANCE_ON_CAPTURE = "CustomWhiteBalanceCallback.onCapture colorCompensation";
    private static final String LOG_STR_QEMU_ERROR = "QEMU_CustomWhiteBalanceCaptureState";
    private static final String RETURN_STATE = "EE";
    private static final String TAG = "CustomWhiteBalanceCaptureState";
    private CameraEx.CustomWhiteBalanceCallback CWBshutterCallback = new CameraEx.CustomWhiteBalanceCallback() { // from class: com.sony.imaging.app.base.shooting.CustomWhiteBalanceCaptureState.1
        public void onCapture(CameraEx.CustomWhiteBalanceInfo cwbinfo, CameraEx cameraex) {
            Log.i(CustomWhiteBalanceCaptureState.TAG, CustomWhiteBalanceCaptureState.LOG_CUSTOM_WHITEBALANCE_ON_CAPTURE);
            if (!Environment.isCustomWBOnePush()) {
                Bundle info = new Bundle();
                info.putInt("colorCompensation", cwbinfo.colorCompensation);
                info.putInt("colorTemperature", cwbinfo.colorTemperature);
                info.putBoolean("inRange", cwbinfo.inRange);
                info.putInt("lightBalance", cwbinfo.lightBalance);
                CustomWhiteBalanceCaptureState.this.setNextState("CWBExposure", info);
                CustomWhiteBalanceCaptureState.this.setData("CUSTOM_WB_INFO", cwbinfo);
                return;
            }
            CustomWhiteBalanceCaptureState.this.setNextState("CWBExposure", new Bundle());
        }
    };

    @Override // com.sony.imaging.app.fw.KeyReceiver
    protected String getKeyConvCategory() {
        return "Menu";
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED, 2);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AF_MF, 1);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED, 1);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.IRIS_DIAL_CHANGED, 1);
        if (4 == Environment.DEVICE_TYPE) {
            Bundle cwbdata = new Bundle();
            Random rnd = new Random();
            int num101 = rnd.nextInt(14);
            cwbdata.putInt("colorCompensation", num101 - 7);
            cwbdata.putInt("colorTemperature", num101);
            cwbdata.putInt("lightBalance", num101 - 7);
            cwbdata.putBoolean("inRange", rnd.nextBoolean());
            setNextState("CWBExposure", cwbdata);
            Log.e("QEMU", LOG_STR_QEMU_ERROR);
            return;
        }
        BaseShootingExecutor executor = ExecutorCreator.getInstance().getSequence();
        try {
            executor.captureCustomWhiteBalance(this.CWBshutterCallback);
        } catch (Exception e) {
            e.printStackTrace();
            setNextState("EE", null);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED, 0);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AF_MF, 0);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED, 0);
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.IRIS_DIAL_CHANGED, 0);
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase
    protected int getMyBeepPattern() {
        return 2;
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        return ApoWrapper.APO_TYPE.SPECIAL;
    }
}
