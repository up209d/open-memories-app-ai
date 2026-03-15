package com.sony.imaging.app.base.shooting;

import android.os.Bundle;
import com.sony.imaging.app.base.shooting.camera.FaceDetectionController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class CustomWhiteBalanceControllerState extends StateBase {
    public static final String CUSTOM_WB_INFO = "CUSTOM_WB_INFO";
    private static final String CWB_EE_STATE = "CWBEE";

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        FaceDetectionController.getInstance().setFaceFrameRendering(false);
        ExecutorCreator creator = ExecutorCreator.getInstance();
        creator.stableSequence();
        BaseShootingExecutor executor = creator.getSequence();
        CameraEx cameraEx = executor.getCameraEx();
        cameraEx.startCustomWhiteBalanceCapture();
        Bundle bundle = new Bundle();
        bundle.putString(WhiteBalanceController.BUNDLE_RESET_ITEMID, this.data.getString(WhiteBalanceController.BUNDLE_RESET_ITEMID));
        addChildState(CWB_EE_STATE, bundle);
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        ExecutorCreator creator = ExecutorCreator.getInstance();
        CameraEx cameraEx = creator.getSequence().getCameraEx();
        cameraEx.stopCustomWhiteBalanceCapture();
        creator.updateSequence();
        super.onPause();
        FaceDetectionController.getInstance().setFaceFrameRendering(true);
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
