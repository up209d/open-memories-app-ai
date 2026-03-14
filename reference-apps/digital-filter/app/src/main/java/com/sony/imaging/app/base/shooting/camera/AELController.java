package com.sony.imaging.app.base.shooting.camera;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import java.util.List;

/* loaded from: classes.dex */
public class AELController extends AbstractController {
    private static final String AEL_API_NAME = "setAutoExposureLock";
    private static final int AEL_INH_SUPPORTER_VER = 2;
    private static final String LOG_MSG_GETAUTOEXPOSURELOCK = "getAutoExposureLock = ";
    private static final String LOG_MSG_HOLDAELOCK = "holdAELock control = ";
    private static final String LOG_MSG_REPRESSAELOCK = "repressAELock ";
    private static final String LOG_MSG_SETAUTOEXPOSURELOCK = "setAutoExposureLock = ";
    private static final String TAG = "AELController";
    private static AELController mInstance;
    private static final String myName = AELController.class.getSimpleName();
    CameraNotificationManager mNotifier = CameraNotificationManager.getInstance();
    CameraSetting mCamSet = CameraSetting.getInstance();

    public static final String getName() {
        return myName;
    }

    public static AELController getInstance() {
        if (mInstance == null) {
            mInstance = new AELController();
        }
        return mInstance;
    }

    private static void setController(AELController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AELController() {
        setController(this);
    }

    public boolean isAELock() {
        return CameraSetting.mAELState;
    }

    public boolean getAELockButtonState() {
        String ael = ((CameraEx.ParametersModifier) this.mCamSet.getParameters().second).getAutoExposureLock();
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), LOG_MSG_GETAUTOEXPOSURELOCK).append(ael);
        Log.i(TAG, builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        return ael.equals("locked");
    }

    public boolean isAELockByAELHold() {
        return getAELockButtonState() && 2 == CameraSetting.mAELButtonCtrlState;
    }

    public void setS1AEL(boolean b) {
        CameraSetting.mS1OnAELock = b;
        updateAELState();
        this.mNotifier.requestNotify(CameraNotificationManager.AE_LOCK);
    }

    public void repressAELock() {
        Log.d(TAG, LOG_MSG_REPRESSAELOCK);
        switch (CameraSetting.mAELButtonCtrlState) {
            case 1:
                CameraSetting.mAELButtonCtrlState = 3;
                setValue("locked");
                break;
            case 2:
                CameraSetting.mAELButtonCtrlState = 3;
                break;
            case 3:
                CameraSetting.mAELButtonCtrlState = 1;
                setValue("unlocked");
                break;
        }
        updateAELState();
    }

    public void holdAELock(boolean control) {
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), LOG_MSG_HOLDAELOCK).append(control);
        Log.d(TAG, builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        switch (CameraSetting.mAELButtonCtrlState) {
            case 1:
                if (control) {
                    CameraSetting.mAELButtonCtrlState = 2;
                    setValue("locked");
                    break;
                }
                break;
            case 2:
                if (!control) {
                    CameraSetting.mAELButtonCtrlState = 1;
                    setValue("unlocked");
                    break;
                }
                break;
            case 3:
                if (control) {
                    CameraSetting.mAELButtonCtrlState = 2;
                    break;
                }
                break;
        }
        updateAELState();
    }

    private void updateAELState() {
        boolean z = true;
        if (!CameraSetting.mS1OnAELock && CameraSetting.mAELButtonCtrlState == 1) {
            z = false;
        }
        CameraSetting.mAELState = z;
    }

    public void cancelAELock() {
        if (1 != CameraSetting.mAELButtonCtrlState) {
            setValue("unlocked");
        }
        CameraSetting.mAELButtonCtrlState = 1;
        CameraSetting.mS1OnAELock = false;
        CameraSetting.mAELState = false;
        this.mNotifier.requestNotify(CameraNotificationManager.AE_LOCK);
    }

    private void setValue(String onoff) {
        CameraSetting cameraSetting = CameraSetting.getInstance();
        Pair<Camera.Parameters, CameraEx.ParametersModifier> supported = cameraSetting.getSupportedParameters();
        List<String> list = ((CameraEx.ParametersModifier) supported.second).getSupportedAutoExposureLocks();
        if (list != null && list.contains(onoff)) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> p = cameraSetting.getEmptyParameters();
            ((CameraEx.ParametersModifier) p.second).setAutoExposureLock(onoff);
            StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
            builder.replace(0, builder.length(), LOG_MSG_SETAUTOEXPOSURELOCK).append(onoff);
            Log.i(TAG, builder.toString());
            StringBuilderThreadLocal.releaseScratchBuilder(builder);
            CameraSetting.getInstance().setParameters(p);
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String value) {
        if (Environment.getVersionPfAPI() < 2) {
            String exp = ((Camera.Parameters) this.mCamSet.getParameters().first).getSceneMode();
            if (exp.equals("manual-exposure")) {
                return false;
            }
            return true;
        }
        AvailableInfo.update();
        boolean ret = AvailableInfo.isAvailable(AEL_API_NAME, null);
        return ret;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        return null;
    }
}
