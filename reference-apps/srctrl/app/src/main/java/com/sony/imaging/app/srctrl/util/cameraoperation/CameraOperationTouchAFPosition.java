package com.sony.imaging.app.srctrl.util.cameraoperation;

import android.graphics.Rect;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.FaceDetectionController;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.fw.RunStatus;
import com.sony.imaging.app.srctrl.util.OperationReceiver;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.imaging.app.srctrl.webapi.specific.ServerEventHandler;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.avio.DisplayManager;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.TouchAFCurrentPositionParams;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.TouchAFPositionParams;
import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
public class CameraOperationTouchAFPosition {
    protected static final int FLEXIBLE_FRAME_INFO_INDEX = 2;
    protected static final int FLEXIBLE_MOVE_AREA_INDEX = 1;
    private static final String tag = CameraOperationTouchAFPosition.class.getName();
    private static final CameraOperationTouchAFPosition s_TouchAFPosition = new CameraOperationTouchAFPosition();
    private TouchAFCurrentPositionParams mCurrentPosition = new TouchAFCurrentPositionParams();
    private String mPreviousFocusArea = null;
    private String mPreviousFocusMode = null;
    private String mPreviousFaceDetectionMode = null;

    private CameraOperationTouchAFPosition() {
        this.mCurrentPosition.set = false;
        this.mCurrentPosition.touchCoordinates = new double[0];
    }

    public static final boolean isSet() {
        return s_TouchAFPosition.mCurrentPosition.set.booleanValue();
    }

    public static final TouchAFCurrentPositionParams get() {
        TouchAFCurrentPositionParams clone;
        synchronized (s_TouchAFPosition) {
            clone = new TouchAFCurrentPositionParams();
            clone.set = s_TouchAFPosition.mCurrentPosition.set;
            clone.touchCoordinates = Arrays.copyOf(s_TouchAFPosition.mCurrentPosition.touchCoordinates, s_TouchAFPosition.mCurrentPosition.touchCoordinates.length);
        }
        return clone;
    }

    public static void enableSetTouchAFPosition(boolean set) {
        synchronized (s_TouchAFPosition) {
            s_TouchAFPosition.mCurrentPosition.set = Boolean.valueOf(set);
            boolean toBeNotified = ParamsGenerator.updateTouchAFPostionParams(set);
            if (toBeNotified) {
                ServerEventHandler.getInstance().onServerStatusChanged();
            }
        }
    }

    public static Boolean set(Double X, Double Y, CameraNotificationListener notificationListener) {
        boolean z;
        synchronized (s_TouchAFPosition) {
            FocusAreaController fac = FocusAreaController.getInstance();
            FocusModeController fmc = FocusModeController.getInstance();
            if (4 != RunStatus.getStatus()) {
                Log.e(tag, "CAMERA STATUS ERROR: Camera is not running (Status=" + RunStatus.getStatus() + ") in " + Thread.currentThread().getStackTrace()[2].toString());
                z = Boolean.FALSE;
            } else if (fmc.isFocusControl()) {
                Log.e(tag, "isFocusControl == true. disables Touch AF.");
                z = Boolean.FALSE;
            } else if (X != null && Y != null && CameraSetting.getInstance().isFocusHold()) {
                Log.e(tag, "isFocusHold == true. disables Touch AF.");
                z = Boolean.FALSE;
            } else {
                Log.v(tag, "Cancel AF once before start.");
                FocusModeController.getInstance().resetQuickAutoFocus("af_woaf");
                ExecutorCreator.getInstance().getSequence().getCamera().cancelAutoFocus();
                String fm = fmc.getValue();
                if (FocusModeController.MANUAL.equals(fm)) {
                    Log.v(tag, "Drive mode is MF.  Touch AF was canceled.");
                    Log.v(tag, "Leave touch AF mode.");
                    leaveTouchAFMode();
                    z = false;
                } else if (X == null || Y == null) {
                    Log.v(tag, "Cancel touch AF position.");
                    leaveTouchAFMode();
                    z = true;
                } else {
                    double x = X.doubleValue();
                    double y = Y.doubleValue();
                    if (DisplayModeObserver.getInstance().isPanelReverse()) {
                        x = 100.0d - x;
                    }
                    int scalar_x = (int) net2scalar(x);
                    int scalar_y = (int) net2scalar(y);
                    try {
                        if (s_TouchAFPosition.mPreviousFocusArea == null) {
                            String focusArea = fac.getValue();
                            s_TouchAFPosition.mPreviousFocusArea = focusArea;
                            fac.setValue("flex-spot");
                        }
                        if (s_TouchAFPosition.mPreviousFocusMode == null) {
                            s_TouchAFPosition.mPreviousFocusMode = fmc.getValue();
                            List<String> availablefocusMode = fmc.getAvailableValue();
                            if (availablefocusMode != null && availablefocusMode.contains(FocusModeController.DMF)) {
                                Log.v(tag, "Set Focus Mode from " + s_TouchAFPosition.mPreviousFocusMode + "to DMF forcibly for touch AF.");
                                fmc.setValue(FocusModeController.DMF);
                            } else if (availablefocusMode != null && availablefocusMode.contains("af-s")) {
                                Log.v(tag, "Set Focus Mode from " + s_TouchAFPosition.mPreviousFocusMode + "to AF-S forcibly for touch AF.");
                                fmc.setValue("af-s");
                            } else {
                                Log.v(tag, "Focus Mode is Not Changed. " + s_TouchAFPosition.mPreviousFocusMode);
                            }
                        }
                        if (s_TouchAFPosition.mPreviousFaceDetectionMode == null) {
                            FaceDetectionController fdc = FaceDetectionController.getInstance();
                            String faceDetectionMode = fdc.getValue();
                            if ("off" == faceDetectionMode) {
                                Log.v(tag, "Face Detection mode has been already set to OFF.");
                            } else {
                                s_TouchAFPosition.mPreviousFaceDetectionMode = faceDetectionMode;
                                Log.v(tag, "Set Face Detection mode to OFF.");
                                fdc.setValue("off");
                            }
                        }
                        CameraEx.FocusAreaInfos areaInfo = FocusAreaController.getInstance().getCurrentFocusAreaInfos("flex-spot");
                        if (areaInfo != null) {
                            Rect scalarAreaRect = areaInfo.rectInfos[1].rect;
                            Rect scalarFrameRect = areaInfo.rectInfos[2].rect;
                            Log.v(tag, "before scalar_x:scalar_y -> " + scalar_x + ":" + scalar_y);
                            if (scalar_x < scalarAreaRect.left + (scalarFrameRect.right / 2)) {
                                scalar_x = scalarAreaRect.left + (scalarFrameRect.right / 2);
                            }
                            if (scalar_x > scalarAreaRect.right - (scalarFrameRect.right / 2)) {
                                scalar_x = scalarAreaRect.right - (scalarFrameRect.right / 2);
                            }
                            if (scalar_y < scalarAreaRect.top + (scalarFrameRect.bottom / 2)) {
                                scalar_y = scalarAreaRect.top + (scalarFrameRect.bottom / 2);
                            }
                            if (scalar_y > scalarAreaRect.bottom - (scalarFrameRect.bottom / 2)) {
                                scalar_y = scalarAreaRect.bottom - (scalarFrameRect.bottom / 2);
                            }
                            Log.v(tag, "after  scalar_x:scalar_y -> " + scalar_x + ":" + scalar_y);
                            fac.setFocusPoint(scalar_x, scalar_y);
                            CameraNotificationManager notificationManager = CameraNotificationManager.getInstance();
                            Log.v(tag, "Register to notificationManager.");
                            notificationListener.registerTo(notificationManager);
                            Log.v(tag, "Start AF for TouchAF");
                            fmc.setQuickAutoFocus("af_woaf");
                            ExecutorCreator.getInstance().getSequence().getCamera().autoFocus(null);
                            Log.v(tag, "Transitting to S1OnEEStateForTouchAF...");
                            boolean check = OperationReceiver.changeToS1OnStateForTouchAF();
                            if (!check) {
                                Log.e(tag, "State transition to S1OnEEStateForTouchAF failed.  Unregister from notificationManager.");
                                notificationListener.unregister();
                                Log.v(tag, "Leave touch AF mode.");
                                leaveTouchAFMode();
                            }
                            z = Boolean.valueOf(check);
                        } else {
                            Log.e(tag, "areaInfo is null.");
                            leaveTouchAFMode();
                            z = false;
                        }
                    } catch (IController.NotSupportedException e) {
                        e.printStackTrace();
                        Log.e(tag, "Unregister from notificationManager.");
                        notificationListener.unregister();
                        Log.v(tag, "Leave touch AF mode.");
                        leaveTouchAFMode();
                        z = false;
                    }
                }
            }
        }
        return z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void leaveTouchAFMode() {
        leaveTouchAFMode(true);
    }

    public static void leaveTouchAFMode(boolean goToS1Off) {
        synchronized (s_TouchAFPosition) {
            if (4 != RunStatus.getStatus()) {
                Log.e(tag, "CAMERA STATUS ERROR: Camera is not running (Status=" + RunStatus.getStatus() + ") in " + Thread.currentThread().getStackTrace()[2].toString());
            } else {
                Log.v(tag, "Cancel AF once for cancel in leaveTouchAFMode.");
                FocusModeController.getInstance().resetQuickAutoFocus("af_woaf");
                ExecutorCreator.getInstance().getSequence().getCamera().cancelAutoFocus();
            }
            resetFocusSettings();
            if (goToS1Off) {
                Log.v(tag, "Transitting to S1OffEEState...");
                OperationReceiver.changeToS1OffState();
            }
        }
    }

    public static void resetFocusSettings() {
        synchronized (s_TouchAFPosition) {
            FocusAreaController fac = FocusAreaController.getInstance();
            FocusModeController fmc = FocusModeController.getInstance();
            if (s_TouchAFPosition.mPreviousFocusArea != null) {
                Log.v(tag, "Revert Focus Area to " + s_TouchAFPosition.mPreviousFocusArea);
                fac.setValue(s_TouchAFPosition.mPreviousFocusArea);
                s_TouchAFPosition.mPreviousFocusArea = null;
            }
            if (s_TouchAFPosition.mPreviousFocusMode != null) {
                Log.v(tag, "Revert AF Mode to " + s_TouchAFPosition.mPreviousFocusMode);
                fmc.setValue(s_TouchAFPosition.mPreviousFocusMode);
                s_TouchAFPosition.mPreviousFocusMode = null;
            }
            if (s_TouchAFPosition.mPreviousFaceDetectionMode != null) {
                Log.v(tag, "Revert Face Detection Mode to " + s_TouchAFPosition.mPreviousFaceDetectionMode);
                FaceDetectionController fdc = FaceDetectionController.getInstance();
                fdc.setValue(s_TouchAFPosition.mPreviousFaceDetectionMode);
                s_TouchAFPosition.mPreviousFaceDetectionMode = null;
            }
            enableSetTouchAFPosition(false);
        }
    }

    /* loaded from: classes.dex */
    public static class CameraNotificationListener implements NotificationListener {
        protected static final int ILLUMINATOR_FOCUS_INFO_INDEX = 0;
        private CameraNotificationManager mCameraNotifier;
        public boolean mReturned = false;
        public final TouchAFPositionParams params = new TouchAFPositionParams();

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public enum AFType {
            FLEXIBLE_SPOT,
            WIDE,
            FACE
        }

        public CameraNotificationListener() {
            this.params.AFType = "";
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return new String[]{CameraNotificationManager.DONE_AUTO_FOCUS};
        }

        private static String getAFTypeStr(CameraNotificationManager.OnFocusInfo onFocusInfo) {
            String afTypeStr;
            AFType afType = AFType.FLEXIBLE_SPOT;
            if (onFocusInfo.area != null) {
                int[] arr$ = onFocusInfo.area;
                int len$ = arr$.length;
                int i$ = 0;
                while (true) {
                    if (i$ >= len$) {
                        break;
                    }
                    int index = arr$[i$];
                    if (index != 0) {
                        i$++;
                    } else {
                        afType = AFType.WIDE;
                        break;
                    }
                }
            }
            switch (afType) {
                case FLEXIBLE_SPOT:
                    afTypeStr = "Touch";
                    break;
                default:
                    afTypeStr = "Wide";
                    break;
            }
            Log.v(CameraOperationTouchAFPosition.tag, "Current AFType is " + afTypeStr + LogHelper.MSG_OPEN_BRACKET + afType + LogHelper.MSG_CLOSE_BRACKET);
            return afTypeStr;
        }

        private void onFocusSucceeded(CameraNotificationManager.OnFocusInfo onFocusInfo) {
            synchronized (CameraOperationTouchAFPosition.s_TouchAFPosition) {
                synchronized (this.params) {
                    this.params.AFResult = true;
                    this.params.AFType = getAFTypeStr(onFocusInfo);
                }
                CameraOperationTouchAFPosition.enableSetTouchAFPosition(true);
            }
        }

        private void onFocusFailed(CameraNotificationManager.OnFocusInfo onFocusInfo) {
            synchronized (this.params) {
                this.params.AFResult = false;
                this.params.AFType = getAFTypeStr(onFocusInfo);
            }
            Log.v(CameraOperationTouchAFPosition.tag, "[TAPO] Leave touch AF mode.");
            CameraOperationTouchAFPosition.leaveTouchAFMode();
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            synchronized (this) {
                if (this.mReturned) {
                    Log.v(tag, "Already processed.  Return.");
                    return;
                }
                if (this.mCameraNotifier == null) {
                    Log.v(tag, "Notifier is null.  Return.");
                    return;
                }
                if (CameraNotificationManager.DONE_AUTO_FOCUS.equals(tag)) {
                    CameraNotificationManager.OnFocusInfo onFocusInfo = (CameraNotificationManager.OnFocusInfo) this.mCameraNotifier.getValue(tag);
                    if (onFocusInfo != null) {
                        switch (onFocusInfo.status) {
                            case 1:
                            case 4:
                                Log.v(tag, "Handle a focus success event.");
                                onFocusSucceeded(onFocusInfo);
                                break;
                            case 2:
                                Log.v(tag, "Handle a focus failure event.");
                                onFocusFailed(onFocusInfo);
                                break;
                            case 3:
                            default:
                                Log.e(tag, "Unknown focus result: " + onFocusInfo.status);
                                return;
                        }
                    }
                    Log.v(tag, "Unregister myself.");
                    unregister();
                    this.mReturned = true;
                    Log.v(tag, "Notify other threads.");
                    notifyAll();
                }
            }
        }

        public void registerTo(CameraNotificationManager notificationManager) {
            synchronized (this) {
                this.mCameraNotifier = notificationManager;
                this.mCameraNotifier.setNotificationListener(this);
            }
        }

        public void unregister() {
            synchronized (this) {
                if (this.mCameraNotifier != null) {
                    this.mCameraNotifier.removeNotificationListener(this);
                    this.mCameraNotifier = null;
                }
            }
        }
    }

    private static double net2scalar(double net_pos) {
        return ((2000.0d * net_pos) / 100.0d) - 1000.0d;
    }

    private static double scalar2net(double scalar_pos) {
        return ((1000.0d + scalar_pos) * 100.0d) / 2000.0d;
    }

    protected static CameraEx.FocusAreaInfos getFocusAreaInfos(int aspect, int viewPattern, String focusAreaMode) {
        CameraEx.FocusAreaInfos[] areaInfos = CameraSetting.getInstance().getFocusAreaRectInfos(aspect, viewPattern);
        if (areaInfos == null) {
            return null;
        }
        for (CameraEx.FocusAreaInfos areaInfo : areaInfos) {
            if (areaInfo.focusAreaMode.equals(focusAreaMode)) {
                return areaInfo;
            }
        }
        return null;
    }

    public static CameraEx.FocusAreaInfos getCurrentFocusAreaInfos(DisplayModeObserver displayModeNotifier, String focusAreaMode) {
        DisplayManager.DeviceStatus deviceStatus = displayModeNotifier.getActiveDeviceStatus();
        int viewPattern = deviceStatus == null ? 4 : deviceStatus.viewPattern;
        String picAsp = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
        int imagerAsp = -1;
        if (PictureSizeController.ASPECT_16_9.equals(picAsp)) {
            imagerAsp = 1;
        } else if (PictureSizeController.ASPECT_3_2.equals(picAsp)) {
            imagerAsp = 0;
        }
        return getFocusAreaInfos(imagerAsp, viewPattern, focusAreaMode);
    }

    public static Rect getAreaRectOnScalarCordinate(CameraEx.FocusAreaInfos flexSpotForcusAreaInfo, Pair<Integer, Integer> fPoint) {
        Rect areaRect = flexSpotForcusAreaInfo.rectInfos[1].rect;
        Rect frameRect = flexSpotForcusAreaInfo.rectInfos[2].rect;
        int imagerWidth = frameRect.right - frameRect.left;
        int imagerHeight = frameRect.bottom - frameRect.top;
        int imagerLeft = ((Integer) fPoint.first).intValue() - (imagerWidth / 2);
        int imagerRight = imagerLeft + imagerWidth;
        int imagerTop = ((Integer) fPoint.second).intValue() - (imagerHeight / 2);
        int imagerBottom = imagerTop + imagerHeight;
        if (imagerLeft < areaRect.left) {
            imagerLeft = areaRect.left;
            imagerRight = imagerLeft + imagerWidth;
        }
        if (imagerTop < areaRect.top) {
            imagerTop = areaRect.top;
            imagerBottom = imagerTop + imagerHeight;
        }
        if (imagerRight > areaRect.right) {
            imagerRight = areaRect.right;
            imagerLeft = imagerRight - imagerWidth;
        }
        if (imagerBottom > areaRect.bottom) {
            imagerBottom = areaRect.bottom;
            imagerTop = imagerBottom - imagerHeight;
        }
        return new Rect(imagerLeft, imagerTop, imagerRight, imagerBottom);
    }
}
