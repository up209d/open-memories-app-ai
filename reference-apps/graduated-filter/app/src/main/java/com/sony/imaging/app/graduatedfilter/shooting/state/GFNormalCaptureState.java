package com.sony.imaging.app.graduatedfilter.shooting.state;

import android.util.Log;
import com.sony.imaging.app.base.shooting.NormalCaptureState;
import com.sony.imaging.app.base.shooting.StateBase;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.RunStatus;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.graduatedfilter.common.GFCommonUtil;
import com.sony.imaging.app.graduatedfilter.common.GFConstants;
import com.sony.imaging.app.graduatedfilter.shooting.GFCompositProcess;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFImageAdjustmentController;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFImageSavingController;
import com.sony.imaging.app.graduatedfilter.shooting.layout.BlackMuteLayout;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class GFNormalCaptureState extends NormalCaptureState implements NotificationListener {
    private static final String NEXT_ADJUST_STATE = "Adjustment";
    private static final String NEXT_PROCESSING_STATE = "Development";
    private static final String TAG = AppLog.getClassName();
    private static boolean isOpenedProcessingLayout = false;
    private static final String[] TAGS = {GFConstants.REQUEST_ADJUSTMENT, GFConstants.REQUEST_AUTOREVIEW, GFConstants.START_PROCESSING, GFConstants.END_PROCESSING};

    @Override // com.sony.imaging.app.base.shooting.NormalCaptureState, com.sony.imaging.app.base.shooting.CaptureStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        if (GFImageSavingController.COMPOSIT.equalsIgnoreCase(GFImageSavingController.getInstance().getValue(GFImageSavingController.SAVE))) {
            GFCommonUtil.getInstance().setVirtualMediaIds();
            GFCommonUtil.getInstance().setActualMediaStatus(false);
        } else {
            GFCommonUtil.getInstance().setActualMediaStatus(true);
        }
        CameraNotificationManager.getInstance().setNotificationListener(this);
        super.onResume();
        isOpenedProcessingLayout = false;
        openLayout(BlackMuteLayout.TAG);
    }

    @Override // com.sony.imaging.app.base.shooting.CaptureStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        int runStatus = RunStatus.getStatus();
        AppLog.info(TAG, "isOpenedProcessingLayout: " + isOpenedProcessingLayout);
        if (isOpenedProcessingLayout && 2 == runStatus) {
            GFCompositProcess.storeCompositImage();
        }
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        closeLayout("ProcessingLayout");
        closeLayout(BlackMuteLayout.TAG);
        GFCommonUtil.getInstance().setInvalidShutter(true);
        super.onPause();
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        Log.i(TAG, "onNotify tag = " + tag);
        if (GFConstants.REQUEST_ADJUSTMENT.equalsIgnoreCase(tag)) {
            setNextState(NEXT_ADJUST_STATE, null);
            return;
        }
        if (GFConstants.REQUEST_AUTOREVIEW.equals(tag)) {
            setNextState(NEXT_PROCESSING_STATE, null);
            return;
        }
        if (GFConstants.START_PROCESSING.equals(tag)) {
            closeLayout(StateBase.DEFAULT_LAYOUT);
            isOpenedProcessingLayout = true;
            openLayout("ProcessingLayout");
        } else if (GFConstants.END_PROCESSING.equals(tag)) {
            closeLayout("ProcessingLayout");
            boolean isSkipAdjustment = GFImageAdjustmentController.ON.equalsIgnoreCase(GFImageAdjustmentController.getInstance().getValue(GFImageAdjustmentController.ADJUSTMENT));
            boolean isEffectImageOnly = GFImageSavingController.COMPOSIT.equalsIgnoreCase(GFImageSavingController.getInstance().getValue(GFImageSavingController.SAVE));
            if (!isSkipAdjustment || !isEffectImageOnly) {
                isOpenedProcessingLayout = false;
            }
        }
    }
}
