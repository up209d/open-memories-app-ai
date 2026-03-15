package com.sony.imaging.app.lightgraffiti.shooting.state;

import android.os.Bundle;
import android.util.Log;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.NormalCaptureState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.FaceDetectionController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.lightgraffiti.LightGraffitiApp;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.lightgraffiti.util.AppLog;
import com.sony.imaging.app.lightgraffiti.util.LGConstants;
import com.sony.imaging.app.util.AppInfo;

/* loaded from: classes.dex */
public class LGNormalCaptureState extends NormalCaptureState implements LGStateHolder.ValueChangedListener {
    public static final String ID_LG_LAYOUT_SHOOTING_GUIDE = "ID_LG_LAYOUT_SHOOTING_GUIDE";
    private final String TAG = LGNormalCaptureState.class.getSimpleName();

    public void onLensProblemDetected() {
        Log.d(this.TAG, "onLensProblemDetected");
        if (LGStateHolder.getInstance().isShootingStage().booleanValue()) {
            CameraNotificationManager.getInstance().requestNotify(LGConstants.SHOOTING_2ND_CLOSE);
        } else {
            LGStateHolder.getInstance().setLensProblemFlag(true);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.NormalCaptureState, com.sony.imaging.app.base.shooting.CaptureStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        Log.d(this.TAG, AppLog.getMethodName());
        FaceDetectionController.getInstance().setFaceFrameRendering(false);
        AppInfo.notifyAppInfo(getActivity().getApplicationContext(), getActivity().getPackageName(), getActivity().getClass().getName(), AppInfo.LARGE_CATEGORY_REC, AppInfo.SMALL_CATEGORY_STILL, LightGraffitiApp.PULLING_BACK_KEYS_INVALID, BaseApp.RESUME_KEYS_FOR_SHOOTING);
        DisplayModeObserver.getInstance().setDisplayMode(1, 1);
        Log.d(this.TAG, "onResume() with shooting stage " + LGStateHolder.getInstance().getShootingStage());
        if (LGStateHolder.getInstance().getShootingStage().equals(LGStateHolder.SELFTIMER_COUNTING)) {
            addChildState("LGFakeS1OnEEState", (Bundle) null);
        } else if (LGStateHolder.getInstance().getShootingStage().equals(LGStateHolder.SHOOTING_1ST) || LGStateHolder.getInstance().getShootingStage().equals(LGStateHolder.SHOOTING_3RD_AFTER_SHOOT) || LGStateHolder.getInstance().getShootingStage().equals(LGStateHolder.SHOOTING_3RD_BEFORE_SHOOT)) {
            addChildState("LGFakeS1OnEEState", (Bundle) null);
        } else if (LGStateHolder.getInstance().isExposingStage().booleanValue()) {
            openLayout(ID_LG_LAYOUT_SHOOTING_GUIDE);
        } else if (LGStateHolder.getInstance().getShootingStage().equals(LGStateHolder.PROCESSING)) {
            Log.d(this.TAG, "LGProcessingLayout waked.");
            closeLayout(ID_LG_LAYOUT_SHOOTING_GUIDE);
            openLayout("LGProcessingLayout");
        }
        LGStateHolder.getInstance().setValueChangedListener(this);
        super.onResume();
    }

    @Override // com.sony.imaging.app.base.shooting.CaptureStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        Log.d(this.TAG, AppLog.getMethodName());
        if (getLayout(ID_LG_LAYOUT_SHOOTING_GUIDE) != null) {
            getLayout(ID_LG_LAYOUT_SHOOTING_GUIDE).closeLayout();
        }
        if (getLayout("LGProcessingLayout") != null) {
            getLayout("LGProcessingLayout").closeLayout();
        }
        LGStateHolder.getInstance().removeValueChangedListener(this);
        ExecutorCreator.getInstance().getSequence().cancelAutoFocus();
        super.onPause();
    }

    @Override // com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder.ValueChangedListener
    public void onValueChanged(String tag) {
        Log.d(this.TAG, "onValueChanged() with shooting stage " + LGStateHolder.getInstance().getShootingStage());
        if (tag.equals(LGStateHolder.SHOOTING_STAGE)) {
            if (LGStateHolder.getInstance().getShootingStage().equals(LGStateHolder.SELFTIMER_COUNTING)) {
                addChildState("LGFakeS1OnEEState", (Bundle) null);
                return;
            }
            if (LGStateHolder.getInstance().isExposingStage().booleanValue()) {
                openLayout(ID_LG_LAYOUT_SHOOTING_GUIDE);
                return;
            }
            if (LGStateHolder.getInstance().getShootingStage().equals(LGStateHolder.SHOOTING_2ND)) {
                closeLayout(ID_LG_LAYOUT_SHOOTING_GUIDE);
                closeLayout("LGProcessingLayout");
                addChildState("LGFakeS1OffEEState", (Bundle) null);
            } else if (LGStateHolder.getInstance().getShootingStage().equals(LGStateHolder.PROCESSING)) {
                Log.d(this.TAG, "LGProcessingLayout waked.");
                closeLayout(ID_LG_LAYOUT_SHOOTING_GUIDE);
                openLayout("LGProcessingLayout");
            } else if (LGStateHolder.getInstance().getShootingStage().equals(LGStateHolder.CONFIRM_REVIEW)) {
                closeLayout("LGProcessingLayout");
                addChildState("LGConfirmReviewState", (Bundle) null);
            }
        }
    }
}
