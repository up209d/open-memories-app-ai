package com.sony.imaging.app.srctrl.liveview;

import android.graphics.Rect;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.DigitalZoomController;
import com.sony.imaging.app.base.shooting.camera.FaceDetectionController;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;

/* loaded from: classes.dex */
public class AFFrameConverter {
    public static final String AF_AREA_MODE_INVALID = "AfAreaModeInvalid";
    public static final int COLOR_BLACK = 6;
    public static final int COLOR_CLEAR = 5;
    public static final int COLOR_GRAY = 1;
    public static final int COLOR_GREEN = 3;
    public static final int COLOR_ORANGE = 2;
    public static final int COLOR_WHITE = 4;
    private static final int FLEXIBLE_FRAME_INFO_INDEX = 2;
    private static final int FLEXIBLE_MOVE_AREA_INDEX = 1;
    private static final int FOCUS_INVISIBLE = 3;
    private static final int FOCUS_LOCK = 1;
    private static final int FOCUS_UNLOCK = 2;
    private static final int ILLUMINATOR_FOCUS_INFO_INDEX = 0;
    private static final String LOG_ERROR_UNKNOWN_INDEX = "Fatal Error:: Unknown index:";
    public static final String SHOW_ILLUMINATOR_INVALID = "ShowIlluminatorInvalid";
    public static final int STATUS_INVISIBLE = 5;
    public static final int STATUS_ON_FOCUS = 3;
    public static final int STATUS_ON_FOCUS_READY = 6;
    public static final int STATUS_SELECTED = 2;
    public static final int STATUS_UN_FOCUS = 1;
    public static final int STATUS_UN_SELECTED = 4;
    private static final String TAG = AFFrameConverter.class.getSimpleName();
    private static AFFrameConverter sInstance;
    private SparseArray<AFFrameInfo> mAfFrameInfoCenterCont;
    private SparseArray<AFFrameInfo> mAfFrameInfoCenterPhase;
    private SparseArray<AFFrameInfo> mAfFrameInfoFlexible;
    private SparseArray<AFFrameInfo> mAfFrameInfoIlluminator;
    private SparseArray<AFFrameInfo> mAfFrameInfoLocal;
    private SparseArray<AFFrameInfo> mAfFrameInfoMulti;
    private SparseArray<AFFrameInfo> mAfFrameInfoReturn;
    private SparseArray<AFFrameInfo> mAfFrameInfoWide;
    private int mFocusReadyFrameIndex_AFLocal;
    private boolean mIsFocusedAsIlluminator;
    private StringBuilder builder_AFMulti = new StringBuilder();
    private int[] mIndex_AFWide = null;
    private int[] mIndex_AFLocal = null;
    private CameraNotificationManager mCameraNotifier = CameraNotificationManager.getInstance();
    private FocusAreaController mFocusAreaController = FocusAreaController.getInstance();
    private FocusModeController mFocusModeController = FocusModeController.getInstance();
    private DigitalZoomController mDigitalZoomController = DigitalZoomController.getInstance();
    private CameraSetting mCameraSetting = CameraSetting.getInstance();
    private String mAfAreaMode = "";
    private boolean mIsDigitalZoom = false;

    private void updateFocusedAsIlluminatorStatus(String tag) {
        if (CameraNotificationManager.DONE_AUTO_FOCUS.equals(tag)) {
            this.mIsFocusedAsIlluminator = false;
            CameraNotificationManager.OnFocusInfo onFocusInfo = (CameraNotificationManager.OnFocusInfo) this.mCameraNotifier.getValue(tag);
            if (onFocusInfo != null) {
                switch (onFocusInfo.status) {
                    case 1:
                        if (onFocusInfo.area != null) {
                            int[] arr$ = onFocusInfo.area;
                            for (int index : arr$) {
                                if (index == 0) {
                                    this.mIsFocusedAsIlluminator = true;
                                    return;
                                }
                            }
                            return;
                        }
                        return;
                    default:
                        return;
                }
            }
        }
    }

    private CameraEx.FocusAreaInfos getCurrentFocusAreaInfos(int viewPattern, String focusAreaMode) {
        String picAsp = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
        int aspect = -1;
        if (PictureSizeController.ASPECT_16_9.equals(picAsp)) {
            aspect = 1;
        } else if (PictureSizeController.ASPECT_3_2.equals(picAsp)) {
            aspect = 0;
        } else if (PictureSizeController.ASPECT_4_3.equals(picAsp) || PictureSizeController.ASPECT_1_1.equals(picAsp)) {
        }
        return this.mFocusAreaController.getFocusAreaInfos(aspect, viewPattern, focusAreaMode);
    }

    public SparseArray<AFFrameInfo> getList(String tag) {
        this.mAfFrameInfoReturn = null;
        updateFocusedAsIlluminatorStatus(tag);
        String strShowIlluminator = isShowIlluminator();
        if (!SHOW_ILLUMINATOR_INVALID.equals(strShowIlluminator)) {
            boolean isShowIlluminator = Boolean.TRUE.toString().equals(strShowIlluminator);
            this.mAfAreaMode = getAfAreaMode(tag, isShowIlluminator);
            if (CameraNotificationManager.FOCUS_CHANGE.equals(tag) || CameraNotificationManager.AUTO_FOCUS_AREA.equals(tag) || CameraNotificationManager.ZOOM_INFO_CHANGED.equals(tag) || CameraNotificationManager.DEVICE_LENS_CHANGED.equals(tag)) {
                update(isShowIlluminator);
            }
            if (CameraNotificationManager.DONE_AUTO_FOCUS.equals(tag)) {
                switchDoneAutoFocus(isShowIlluminator, tag);
            } else if (CameraNotificationManager.START_AUTO_FOCUS.equals(tag)) {
                CameraNotificationManager.FocusLightStateInfo info = (CameraNotificationManager.FocusLightStateInfo) this.mCameraNotifier.getValue(CameraNotificationManager.FOCUS_LIGHT_STATE);
                if (info != null) {
                    if (info.ready) {
                        this.mAfAreaMode = getAfAreaMode(tag, true);
                    }
                    onStartAutoFocus(info.ready || isShowIlluminator);
                } else {
                    Log.e(TAG, "FocusLightStateInfo is null");
                }
            } else if (CameraNotificationManager.AUTO_FOCUS_AREA.equals(tag) || CameraNotificationManager.FOCUS_AREA_INFO.equals(tag)) {
                onFocusAreaInfoChanged(isShowIlluminator);
            } else if (CameraNotificationManager.FOCUS_CHANGE.equals(tag)) {
                onFocusModeChanged(isShowIlluminator);
            } else if (CameraNotificationManager.ZOOM_INFO_CHANGED.equals(tag)) {
                onZoomInfoChanged(isShowIlluminator);
            } else if (CameraNotificationManager.FOCUS_POINT.equals(tag)) {
                onChangeYUV(false);
            } else if (CameraNotificationManager.FACE_DETECTION_MODE.equals(tag)) {
                onFaceDetectionModeChanged(this.mAfAreaMode);
            } else if (DisplayModeObserver.TAG_YUVLAYOUT_CHANGE.equals(tag) || DisplayModeObserver.TAG_DEVICE_CHANGE.equals(tag)) {
                onChangeYUV(true);
            } else {
                Log.e(TAG, "tag=" + tag);
                return this.mAfFrameInfoReturn;
            }
            if (this.mAfFrameInfoReturn != null) {
                for (int i = 0; i < this.mAfFrameInfoReturn.size(); i++) {
                    this.mAfFrameInfoReturn.valueAt(i).setFocusAreaMode(this.mAfAreaMode);
                    this.mAfFrameInfoReturn.valueAt(i).setSensorType(this.mFocusAreaController.getSensorType());
                }
            }
        }
        return this.mAfFrameInfoReturn;
    }

    public void updateFlags(String tag) {
        updateFocusedAsIlluminatorStatus(tag);
        String strShowIlluminator = isShowIlluminator();
        if (!SHOW_ILLUMINATOR_INVALID.equals(strShowIlluminator)) {
            boolean isShowIlluminator = Boolean.TRUE.toString().equals(strShowIlluminator);
            if (CameraNotificationManager.DONE_AUTO_FOCUS.equals(tag)) {
                this.mAfAreaMode = getAfAreaMode(tag, isShowIlluminator);
                CameraNotificationManager.OnFocusInfo onFocusInfo = (CameraNotificationManager.OnFocusInfo) this.mCameraNotifier.getValue(tag);
                if (onFocusInfo != null) {
                    if (1 == onFocusInfo.status) {
                        if (onFocusInfo.area != null) {
                            if (this.mAfAreaMode.equals(FocusAreaController.WIDE)) {
                                this.mIndex_AFWide = onFocusInfo.area;
                                return;
                            } else {
                                if (this.mAfAreaMode.equals(FocusAreaController.LOCAL)) {
                                    this.mIndex_AFLocal = onFocusInfo.area;
                                    return;
                                }
                                return;
                            }
                        }
                        return;
                    }
                    if (onFocusInfo.status == 0) {
                        if (this.mAfAreaMode.equals(FocusAreaController.WIDE)) {
                            this.mIndex_AFWide = null;
                        } else if (this.mAfAreaMode.equals(FocusAreaController.LOCAL)) {
                            this.mIndex_AFLocal = null;
                        }
                    }
                }
            }
        }
    }

    private void onFaceLocked() {
        Log.d(TAG, "onFaceLocked, mAfAreaMode = " + this.mAfAreaMode);
        if (this.mAfAreaMode.equals(FocusAreaController.CENTER_WEIGHTED)) {
            this.mAfFrameInfoCenterCont.get(0).setColor(5);
            this.mAfFrameInfoReturn = this.mAfFrameInfoCenterCont;
            return;
        }
        if (this.mAfAreaMode.equals(FocusAreaController.FIX_CENTER)) {
            this.mAfFrameInfoCenterPhase.get(0).setColor(5);
            this.mAfFrameInfoReturn = this.mAfFrameInfoCenterPhase;
            return;
        }
        if (this.mAfAreaMode.equals("flex-spot")) {
            this.mAfFrameInfoFlexible.get(0).setColor(5);
            this.mAfFrameInfoReturn = this.mAfFrameInfoFlexible;
            return;
        }
        if (this.mAfAreaMode.equals(FocusAreaController.LOCAL)) {
            setAllFrameInvisible(this.mAfFrameInfoLocal);
            this.mAfFrameInfoReturn = this.mAfFrameInfoLocal;
            return;
        }
        if (this.mAfAreaMode.equals(FocusAreaController.MULTI)) {
            setAllFrameInvisible(this.mAfFrameInfoMulti);
            this.mAfFrameInfoReturn = this.mAfFrameInfoMulti;
        } else if (this.mAfAreaMode.equals(FocusAreaController.WIDE)) {
            setAllFrameInvisible(this.mAfFrameInfoWide);
            this.mAfFrameInfoReturn = this.mAfFrameInfoWide;
        } else if (this.mAfAreaMode.equals(FocusAreaController.AF_FIXED_TO_ILLUINATOR_FRAME)) {
            switchIlluminatorVisibility(2, false, this.mAfFrameInfoIlluminator.get(0));
            this.mAfFrameInfoReturn = this.mAfFrameInfoIlluminator;
        }
    }

    private void onFocused(int[] index, boolean illuminator) {
        Log.d(TAG, "onFocused, mAfAreaMode = " + this.mAfAreaMode);
        if (this.mAfAreaMode.equals(FocusAreaController.CENTER_WEIGHTED)) {
            switchFrameVisibility(1, illuminator, this.mAfFrameInfoCenterCont.get(0));
            this.mAfFrameInfoReturn = this.mAfFrameInfoCenterCont;
            return;
        }
        if (this.mAfAreaMode.equals(FocusAreaController.FIX_CENTER)) {
            this.mAfFrameInfoCenterPhase.get(0).setColor(3);
            this.mAfFrameInfoReturn = this.mAfFrameInfoCenterPhase;
            return;
        }
        if (this.mAfAreaMode.equals("flex-spot")) {
            if (illuminator) {
                this.mAfFrameInfoFlexible.get(0).setColor(5);
            } else {
                this.mAfFrameInfoFlexible.get(0).setColor(3);
            }
            this.mAfFrameInfoReturn = this.mAfFrameInfoFlexible;
            return;
        }
        if (this.mAfAreaMode.equals(FocusAreaController.LOCAL)) {
            if (2 == this.mCameraSetting.getCurrentMode()) {
                setAllFrameUnFocus(this.mAfFrameInfoLocal);
            } else {
                setAllFrameInvisible(this.mAfFrameInfoLocal);
            }
            KeyStatus status = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.S1_ON);
            if (1 == status.status) {
                setAllFrameInvisible(this.mAfFrameInfoLocal);
            }
            this.mIndex_AFLocal = index;
            for (int frameIndex : index) {
                boolean isEnable = this.mAfFrameInfoLocal.get(frameIndex).isEnable();
                if (isEnable) {
                    AFFrameInfo f = this.mAfFrameInfoLocal.get(frameIndex);
                    if (f != null) {
                        f.setColor(3);
                    } else {
                        Log.e(TAG, "Unknown index.");
                    }
                    this.mAfFrameInfoLocal.append(frameIndex, f);
                }
            }
            this.mAfFrameInfoReturn = this.mAfFrameInfoLocal;
            return;
        }
        if (this.mAfAreaMode.equals(FocusAreaController.MULTI)) {
            setAllFrameInvisible(this.mAfFrameInfoMulti);
            for (int i : index) {
                AFFrameInfo f2 = this.mAfFrameInfoMulti.get(i);
                if (f2 != null) {
                    f2.setColor(3);
                    this.mAfFrameInfoMulti.append(i, f2);
                } else {
                    Log.e(TAG, this.builder_AFMulti.replace(0, LOG_ERROR_UNKNOWN_INDEX.length(), LOG_ERROR_UNKNOWN_INDEX).append(i).toString());
                }
            }
            this.mAfFrameInfoReturn = this.mAfFrameInfoMulti;
            return;
        }
        if (this.mAfAreaMode.equals(FocusAreaController.WIDE)) {
            if (2 == this.mCameraSetting.getCurrentMode()) {
                setAllFrameUnFocus(this.mAfFrameInfoWide);
            } else {
                setAllFrameInvisible(this.mAfFrameInfoWide);
            }
            this.mIndex_AFWide = index;
            for (int frameIndex2 : index) {
                boolean isEnable2 = this.mAfFrameInfoWide.get(frameIndex2).isEnable();
                if (isEnable2) {
                    AFFrameInfo f3 = this.mAfFrameInfoWide.get(frameIndex2);
                    if (f3 != null) {
                        f3.setColor(3);
                    } else {
                        Log.e(TAG, "Unknown index.");
                    }
                    this.mAfFrameInfoWide.append(frameIndex2, f3);
                }
            }
            this.mAfFrameInfoReturn = this.mAfFrameInfoWide;
            return;
        }
        if (this.mAfAreaMode.equals(FocusAreaController.AF_FIXED_TO_ILLUINATOR_FRAME)) {
            switchIlluminatorVisibility(1, illuminator, this.mAfFrameInfoIlluminator.get(0));
            this.mAfFrameInfoReturn = this.mAfFrameInfoIlluminator;
        }
    }

    private void onNeutral(boolean illuminator) {
        Log.d(TAG, "onNeutral, mAfAreaMode = " + this.mAfAreaMode);
        if (this.mAfAreaMode.equals(FocusAreaController.CENTER_WEIGHTED)) {
            clearLocalData(FocusAreaController.CENTER_WEIGHTED, illuminator);
            this.mAfFrameInfoReturn = this.mAfFrameInfoCenterCont;
            return;
        }
        if (this.mAfAreaMode.equals(FocusAreaController.FIX_CENTER)) {
            clearLocalData(FocusAreaController.FIX_CENTER, illuminator);
            this.mAfFrameInfoReturn = this.mAfFrameInfoCenterPhase;
            return;
        }
        if (this.mAfAreaMode.equals("flex-spot")) {
            clearLocalData("flex-spot", illuminator);
            this.mAfFrameInfoReturn = this.mAfFrameInfoFlexible;
            return;
        }
        if (this.mAfAreaMode.equals(FocusAreaController.LOCAL)) {
            clearLocalData(FocusAreaController.LOCAL, illuminator);
            this.mAfFrameInfoReturn = this.mAfFrameInfoLocal;
            return;
        }
        if (this.mAfAreaMode.equals(FocusAreaController.MULTI)) {
            clearLocalData(FocusAreaController.MULTI, illuminator);
            this.mAfFrameInfoReturn = this.mAfFrameInfoMulti;
        } else if (this.mAfAreaMode.equals(FocusAreaController.WIDE)) {
            clearLocalData(FocusAreaController.WIDE, illuminator);
            this.mAfFrameInfoReturn = this.mAfFrameInfoWide;
        } else if (this.mAfAreaMode.equals(FocusAreaController.AF_FIXED_TO_ILLUINATOR_FRAME)) {
            clearLocalData(FocusAreaController.AF_FIXED_TO_ILLUINATOR_FRAME, illuminator);
            this.mAfFrameInfoReturn = this.mAfFrameInfoIlluminator;
        }
    }

    private void onContinuous(int[] index, boolean illuminator) {
        Log.d(TAG, "onContinuous, mAfAreaMode = " + this.mAfAreaMode);
        if (this.mAfAreaMode.equals(FocusAreaController.CENTER_WEIGHTED)) {
            switchFrameVisibility(1, illuminator, this.mAfFrameInfoCenterCont.get(0));
            this.mAfFrameInfoReturn = this.mAfFrameInfoCenterCont;
            return;
        }
        if (this.mAfAreaMode.equals(FocusAreaController.FIX_CENTER)) {
            this.mAfFrameInfoCenterPhase.get(0).setColor(3);
            this.mAfFrameInfoReturn = this.mAfFrameInfoCenterPhase;
            return;
        }
        if (this.mAfAreaMode.equals("flex-spot")) {
            this.mAfFrameInfoFlexible.get(0).setColor(3);
            this.mAfFrameInfoReturn = this.mAfFrameInfoFlexible;
            return;
        }
        if (this.mAfAreaMode.equals(FocusAreaController.LOCAL)) {
            if (2 == this.mCameraSetting.getCurrentMode()) {
                setAllFrameUnFocus(this.mAfFrameInfoLocal);
            } else {
                setAllFrameInvisible(this.mAfFrameInfoLocal);
            }
            KeyStatus status = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.S1_ON);
            if (1 == status.status) {
                setAllFrameInvisible(this.mAfFrameInfoLocal);
            }
            for (int frameIndex : index) {
                if (this.mAfFrameInfoLocal.get(frameIndex).isEnable()) {
                    this.mAfFrameInfoLocal.get(frameIndex).setColor(3);
                }
            }
            this.mAfFrameInfoReturn = this.mAfFrameInfoLocal;
            return;
        }
        if (this.mAfAreaMode.equals(FocusAreaController.MULTI)) {
            setAllFrameInvisible(this.mAfFrameInfoMulti);
            this.mAfFrameInfoMulti.get(0).setColor(5);
            this.mAfFrameInfoReturn = this.mAfFrameInfoMulti;
            return;
        }
        if (this.mAfAreaMode.equals(FocusAreaController.WIDE)) {
            setAllFrameUnFocus(this.mAfFrameInfoWide);
            for (int frameIndex2 : index) {
                if (this.mAfFrameInfoWide.get(frameIndex2).isEnable()) {
                    this.mAfFrameInfoWide.get(frameIndex2).setColor(3);
                }
            }
            this.mAfFrameInfoReturn = this.mAfFrameInfoWide;
            return;
        }
        if (this.mAfAreaMode.equals(FocusAreaController.AF_FIXED_TO_ILLUINATOR_FRAME)) {
            switchIlluminatorVisibility(1, illuminator, this.mAfFrameInfoIlluminator.get(0));
            this.mAfFrameInfoReturn = this.mAfFrameInfoIlluminator;
        }
    }

    private void onLockWarn(boolean illuminator) {
        Log.d(TAG, "onLockWarn, mAfAreaMode = " + this.mAfAreaMode);
        if (this.mAfAreaMode.equals(FocusAreaController.CENTER_WEIGHTED)) {
            switchFrameVisibility(2, illuminator, this.mAfFrameInfoCenterCont.get(0));
            this.mAfFrameInfoReturn = this.mAfFrameInfoCenterCont;
            return;
        }
        if (this.mAfAreaMode.equals(FocusAreaController.FIX_CENTER)) {
            this.mAfFrameInfoCenterPhase.get(0).setColor(6);
            this.mAfFrameInfoReturn = this.mAfFrameInfoCenterPhase;
            return;
        }
        if (this.mAfAreaMode.equals("flex-spot")) {
            this.mAfFrameInfoFlexible.get(0).setColor(1);
            this.mAfFrameInfoReturn = this.mAfFrameInfoFlexible;
            return;
        }
        if (this.mAfAreaMode.equals(FocusAreaController.LOCAL)) {
            if (2 == this.mCameraSetting.getCurrentMode()) {
                setAllFrameUnFocus(this.mAfFrameInfoLocal);
            } else {
                setAllFrameInvisible(this.mAfFrameInfoLocal);
            }
            KeyStatus status = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.S1_ON);
            if (1 == status.status) {
                setAllFrameInvisible(this.mAfFrameInfoLocal);
            }
            this.mAfFrameInfoLocal.get(this.mFocusReadyFrameIndex_AFLocal).setColor(6);
            this.mIndex_AFLocal = null;
            this.mAfFrameInfoReturn = this.mAfFrameInfoLocal;
            return;
        }
        if (this.mAfAreaMode.equals(FocusAreaController.MULTI)) {
            setAllFrameInvisible(this.mAfFrameInfoMulti);
            this.mAfFrameInfoReturn = this.mAfFrameInfoMulti;
            return;
        }
        if (this.mAfAreaMode.equals(FocusAreaController.WIDE)) {
            setAllFrameUnFocus(this.mAfFrameInfoWide);
            this.mIndex_AFWide = null;
            this.mAfFrameInfoReturn = this.mAfFrameInfoWide;
        } else if (this.mAfAreaMode.equals(FocusAreaController.AF_FIXED_TO_ILLUINATOR_FRAME)) {
            if (this.mFocusAreaController.getSensorType() == 1) {
                switchIlluminatorVisibility(2, illuminator, this.mAfFrameInfoIlluminator.get(0));
            } else if (this.mFocusAreaController.getSensorType() == 2) {
                switchIlluminatorVisibility(3, illuminator, this.mAfFrameInfoIlluminator.get(0));
            }
            this.mAfFrameInfoReturn = this.mAfFrameInfoIlluminator;
        }
    }

    private void onStartAutoFocus(boolean illuminator) {
        Log.d(TAG, "onStartAutoFocus, mAfAreaMode = " + this.mAfAreaMode);
        if (this.mAfAreaMode.equals(FocusAreaController.CENTER_WEIGHTED)) {
            switchFrameVisibility(2, illuminator, this.mAfFrameInfoCenterCont.get(0));
            this.mAfFrameInfoReturn = this.mAfFrameInfoCenterCont;
            return;
        }
        if (this.mAfAreaMode.equals(FocusAreaController.FIX_CENTER)) {
            this.mAfFrameInfoCenterPhase.get(0).setColor(6);
            this.mAfFrameInfoReturn = this.mAfFrameInfoCenterPhase;
            return;
        }
        if (this.mAfAreaMode.equals("flex-spot")) {
            if (illuminator) {
                this.mAfFrameInfoFlexible.get(0).setColor(5);
            } else {
                this.mAfFrameInfoFlexible.get(0).setColor(1);
            }
            this.mAfFrameInfoReturn = this.mAfFrameInfoFlexible;
            return;
        }
        if (this.mAfAreaMode.equals(FocusAreaController.LOCAL)) {
            if (2 == this.mCameraSetting.getCurrentMode()) {
                setAllFrameUnFocus(this.mAfFrameInfoLocal);
            } else {
                setAllFrameUnFocus(this.mAfFrameInfoLocal);
                setAllFrameInvisible(this.mAfFrameInfoLocal);
            }
            KeyStatus status = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.S1_ON);
            if (1 == status.status) {
                setAllFrameInvisible(this.mAfFrameInfoLocal);
            }
            this.mAfFrameInfoLocal.get(this.mFocusReadyFrameIndex_AFLocal).setColor(6);
            this.mIndex_AFLocal = null;
            this.mAfFrameInfoReturn = this.mAfFrameInfoLocal;
            return;
        }
        if (this.mAfAreaMode.equals(FocusAreaController.MULTI)) {
            if (illuminator) {
                this.mAfFrameInfoMulti.get(0).setColor(5);
            } else {
                setAllFrameInvisible(this.mAfFrameInfoMulti);
            }
            this.mAfFrameInfoReturn = this.mAfFrameInfoMulti;
            return;
        }
        if (this.mAfAreaMode.equals(FocusAreaController.WIDE)) {
            if (illuminator) {
                setAllFrameInvisible(this.mAfFrameInfoWide);
            } else {
                setAllFrameUnFocus(this.mAfFrameInfoWide);
            }
            this.mIndex_AFWide = null;
            this.mAfFrameInfoReturn = this.mAfFrameInfoWide;
            return;
        }
        if (this.mAfAreaMode.equals(FocusAreaController.AF_FIXED_TO_ILLUINATOR_FRAME)) {
            switchIlluminatorVisibility(2, illuminator, this.mAfFrameInfoIlluminator.get(0));
            this.mAfFrameInfoReturn = this.mAfFrameInfoIlluminator;
        }
    }

    private void onChangeYUV(boolean displayChanged) {
        Log.d(TAG, "onChangeYUV, mAfAreaMode = " + this.mAfAreaMode);
        if (this.mAfAreaMode.equals(FocusAreaController.CENTER_WEIGHTED)) {
            updateRect_AFCenterWeighted(this.mAfAreaMode);
            return;
        }
        if (this.mAfAreaMode.equals(FocusAreaController.FIX_CENTER)) {
            updateRect_AFFixToCenter(this.mAfAreaMode);
            return;
        }
        if (this.mAfAreaMode.equals("flex-spot")) {
            updateRect_AFFlexible(this.mAfAreaMode, displayChanged);
            return;
        }
        if (this.mAfAreaMode.equals(FocusAreaController.LOCAL)) {
            updateRect_AFLocal(this.mAfAreaMode);
            return;
        }
        if (this.mAfAreaMode.equals(FocusAreaController.MULTI)) {
            updateRect_AFMulti(this.mAfAreaMode);
        } else if (this.mAfAreaMode.equals(FocusAreaController.WIDE)) {
            updateRect_AFWide(this.mAfAreaMode);
        } else if (this.mAfAreaMode.equals(FocusAreaController.AF_FIXED_TO_ILLUINATOR_FRAME)) {
            updateRect_AbstractAFView(this.mAfAreaMode);
        }
    }

    private void onZoomInfoChanged(boolean illuminator) {
        Log.d(TAG, "onZoomInfoChanged, mAfAreaMode = " + this.mAfAreaMode);
        if (this.mAfAreaMode.equals(FocusAreaController.CENTER_WEIGHTED)) {
            if (!updateWithCurrentStatus(illuminator)) {
                this.mAfFrameInfoReturn = this.mAfFrameInfoCenterCont;
                return;
            }
            return;
        }
        if (this.mAfAreaMode.equals(FocusAreaController.FIX_CENTER)) {
            this.mAfFrameInfoReturn = this.mAfFrameInfoCenterPhase;
            return;
        }
        if (this.mAfAreaMode.equals("flex-spot")) {
            if (!updateWithCurrentStatus(illuminator)) {
                this.mAfFrameInfoReturn = this.mAfFrameInfoFlexible;
                return;
            }
            return;
        }
        if (this.mAfAreaMode.equals(FocusAreaController.LOCAL)) {
            this.mAfFrameInfoReturn = this.mAfFrameInfoLocal;
            return;
        }
        if (this.mAfAreaMode.equals(FocusAreaController.MULTI)) {
            if (!updateWithCurrentStatus(illuminator)) {
                this.mAfFrameInfoReturn = this.mAfFrameInfoMulti;
            }
        } else {
            if (this.mAfAreaMode.equals(FocusAreaController.WIDE)) {
                this.mAfFrameInfoReturn = this.mAfFrameInfoWide;
                return;
            }
            if (this.mAfAreaMode.equals(FocusAreaController.AF_FIXED_TO_ILLUINATOR_FRAME)) {
                updateRect_AbstractAFView(this.mAfAreaMode);
                if (isAFFocused()) {
                    switchIlluminatorVisibility(1, illuminator, this.mAfFrameInfoIlluminator.get(0));
                } else {
                    switchIlluminatorVisibility(2, illuminator, this.mAfFrameInfoIlluminator.get(0));
                }
                this.mAfFrameInfoReturn = this.mAfFrameInfoIlluminator;
            }
        }
    }

    private void onFocusAreaInfoChanged(boolean illuminator) {
        Log.d(TAG, "onFocusAreaInfoChanged, mAfAreaMode = " + this.mAfAreaMode);
        if (this.mAfAreaMode.equals(FocusAreaController.CENTER_WEIGHTED)) {
            updateRect_AFCenterWeighted(this.mAfAreaMode);
            switchFrameVisibility(2, illuminator, this.mAfFrameInfoCenterCont.get(0));
            this.mAfFrameInfoReturn = this.mAfFrameInfoCenterCont;
            return;
        }
        if (this.mAfAreaMode.equals(FocusAreaController.FIX_CENTER)) {
            updateRect_AFFixToCenter(this.mAfAreaMode);
            this.mAfFrameInfoCenterPhase.get(0).setColor(6);
            this.mAfFrameInfoReturn = this.mAfFrameInfoCenterPhase;
            return;
        }
        if (this.mAfAreaMode.equals("flex-spot")) {
            updateRect_AFFlexible(this.mAfAreaMode);
            this.mAfFrameInfoFlexible.get(0).setColor(1);
            this.mAfFrameInfoReturn = this.mAfFrameInfoFlexible;
        } else {
            if (this.mAfAreaMode.equals(FocusAreaController.LOCAL)) {
                updateRect_AFLocal(this.mAfAreaMode);
                return;
            }
            if (this.mAfAreaMode.equals(FocusAreaController.MULTI)) {
                updateRect_AFMulti(this.mAfAreaMode);
            } else if (this.mAfAreaMode.equals(FocusAreaController.WIDE)) {
                updateRect_AFWide(this.mAfAreaMode);
            } else if (this.mAfAreaMode.equals(FocusAreaController.AF_FIXED_TO_ILLUINATOR_FRAME)) {
                updateRect_AbstractAFView(this.mAfAreaMode);
            }
        }
    }

    private void onFocusModeChanged(boolean illuminator) {
        Log.d(TAG, "onFocusModeChanged, mAfAreaMode = " + this.mAfAreaMode);
        if (this.mAfAreaMode.equals(FocusAreaController.CENTER_WEIGHTED)) {
            updateRect_AFCenterWeighted(this.mAfAreaMode);
            switchFrameVisibility(2, illuminator, this.mAfFrameInfoCenterCont.get(0));
            this.mAfFrameInfoReturn = this.mAfFrameInfoCenterCont;
            return;
        }
        if (this.mAfAreaMode.equals(FocusAreaController.FIX_CENTER)) {
            updateRect_AFFixToCenter(this.mAfAreaMode);
            this.mAfFrameInfoCenterPhase.get(0).setColor(6);
            this.mAfFrameInfoReturn = this.mAfFrameInfoCenterPhase;
            return;
        }
        if (this.mAfAreaMode.equals("flex-spot")) {
            updateRect_AFFlexible(this.mAfAreaMode);
            this.mAfFrameInfoFlexible.get(0).setColor(1);
            this.mAfFrameInfoReturn = this.mAfFrameInfoFlexible;
        } else {
            if (this.mAfAreaMode.equals(FocusAreaController.LOCAL)) {
                updateRect_AFLocal(this.mAfAreaMode);
                return;
            }
            if (this.mAfAreaMode.equals(FocusAreaController.MULTI)) {
                updateRect_AFMulti(this.mAfAreaMode);
            } else if (this.mAfAreaMode.equals(FocusAreaController.WIDE)) {
                updateRect_AFWide(this.mAfAreaMode);
            } else if (this.mAfAreaMode.equals(FocusAreaController.AF_FIXED_TO_ILLUINATOR_FRAME)) {
                updateRect_AbstractAFView(this.mAfAreaMode);
            }
        }
    }

    private void onFaceDetectionModeChanged(String afAreaMode) {
        Log.d(TAG, "onFaceDetectionModeChanged, mAfAreaMode = " + afAreaMode);
        if (afAreaMode.equals(FocusAreaController.CENTER_WEIGHTED)) {
            this.mAfFrameInfoReturn = this.mAfFrameInfoCenterCont;
            return;
        }
        if (afAreaMode.equals(FocusAreaController.FIX_CENTER)) {
            this.mAfFrameInfoReturn = this.mAfFrameInfoCenterPhase;
            return;
        }
        if (afAreaMode.equals("flex-spot")) {
            this.mAfFrameInfoReturn = this.mAfFrameInfoFlexible;
            return;
        }
        if (afAreaMode.equals(FocusAreaController.LOCAL)) {
            this.mAfFrameInfoReturn = this.mAfFrameInfoLocal;
            return;
        }
        if (afAreaMode.equals(FocusAreaController.MULTI)) {
            if (this.mAfFrameInfoMulti == null) {
                Log.e(TAG, "onFaceDetectionModeChanged mAfFrameInfoMulti = null");
            } else if (this.mAfFrameInfoMulti.valueAt(0) == null) {
                Log.e(TAG, "onFaceDetectionModeChanged ILLUMINATOR_FOCUS_INFO_INDEX = null");
            } else {
                this.mAfFrameInfoMulti.valueAt(0).setColor(5);
            }
            this.mAfFrameInfoReturn = this.mAfFrameInfoMulti;
            return;
        }
        if (afAreaMode.equals(FocusAreaController.WIDE)) {
            this.mAfFrameInfoReturn = this.mAfFrameInfoWide;
        } else if (afAreaMode.equals(FocusAreaController.AF_FIXED_TO_ILLUINATOR_FRAME)) {
            this.mAfFrameInfoReturn = this.mAfFrameInfoIlluminator;
        }
    }

    private void update(boolean illuminator) {
        Log.d(TAG, "update, mAfAreaMode = " + this.mAfAreaMode);
        if (this.mAfAreaMode.equals(FocusAreaController.CENTER_WEIGHTED)) {
            updateRect_AFCenterWeighted(FocusAreaController.CENTER_WEIGHTED);
            switchFrameVisibility(2, illuminator, this.mAfFrameInfoCenterCont.get(0));
            this.mAfFrameInfoReturn = this.mAfFrameInfoCenterCont;
            return;
        }
        if (this.mAfAreaMode.equals(FocusAreaController.FIX_CENTER)) {
            updateRect_AFFixToCenter(FocusAreaController.FIX_CENTER);
            this.mAfFrameInfoCenterPhase.get(0).setColor(6);
            this.mAfFrameInfoReturn = this.mAfFrameInfoCenterPhase;
            return;
        }
        if (this.mAfAreaMode.equals("flex-spot")) {
            updateRect_AFFlexible("flex-spot");
            this.mAfFrameInfoFlexible.get(0).setColor(1);
            this.mAfFrameInfoReturn = this.mAfFrameInfoFlexible;
            return;
        }
        if (this.mAfAreaMode.equals(FocusAreaController.LOCAL)) {
            updateRect_AFLocal(FocusAreaController.LOCAL);
            this.mAfFrameInfoReturn = this.mAfFrameInfoLocal;
            return;
        }
        if (this.mAfAreaMode.equals(FocusAreaController.MULTI)) {
            updateRect_AFMulti(FocusAreaController.MULTI);
            setAllFrameInvisible(this.mAfFrameInfoMulti);
            this.mAfFrameInfoReturn = this.mAfFrameInfoMulti;
        } else if (this.mAfAreaMode.equals(FocusAreaController.WIDE)) {
            updateRect_AFWide(FocusAreaController.WIDE);
            setAllFrameUnFocus(this.mAfFrameInfoWide);
            this.mAfFrameInfoReturn = this.mAfFrameInfoWide;
        } else if (this.mAfAreaMode.equals(FocusAreaController.AF_FIXED_TO_ILLUINATOR_FRAME)) {
            updateRect_AbstractAFView(FocusAreaController.AF_FIXED_TO_ILLUINATOR_FRAME);
            this.mAfFrameInfoIlluminator.get(0).setColor(1);
            this.mAfFrameInfoReturn = this.mAfFrameInfoIlluminator;
        }
    }

    public static AFFrameConverter getInstance() {
        if (sInstance == null) {
            sInstance = new AFFrameConverter();
        }
        return sInstance;
    }

    private AFFrameConverter() {
        this.mIsFocusedAsIlluminator = false;
        this.mAfFrameInfoReturn = null;
        this.mAfFrameInfoIlluminator = null;
        this.mAfFrameInfoCenterCont = null;
        this.mAfFrameInfoFlexible = null;
        this.mAfFrameInfoMulti = null;
        this.mAfFrameInfoCenterPhase = null;
        this.mAfFrameInfoLocal = null;
        this.mAfFrameInfoWide = null;
        this.mAfFrameInfoReturn = new SparseArray<>();
        this.mAfFrameInfoIlluminator = new SparseArray<>();
        updateRect_AbstractAFView(FocusAreaController.AF_FIXED_TO_ILLUINATOR_FRAME);
        this.mAfFrameInfoIlluminator.get(0).setColor(1);
        this.mIsFocusedAsIlluminator = false;
        this.mAfFrameInfoCenterCont = new SparseArray<>();
        updateRect_AFCenterWeighted(FocusAreaController.CENTER_WEIGHTED);
        if (this.mAfFrameInfoCenterCont.get(0) != null) {
            this.mAfFrameInfoCenterCont.get(0).setColor(1);
        }
        this.mAfFrameInfoFlexible = new SparseArray<>();
        updateRect_AFFlexible("flex-spot");
        if (this.mAfFrameInfoFlexible.get(0) != null) {
            this.mAfFrameInfoFlexible.get(0).setColor(1);
        }
        this.mAfFrameInfoMulti = new SparseArray<>();
        updateRect_AFMulti(FocusAreaController.MULTI);
        setAllFrameInvisible(this.mAfFrameInfoMulti);
        this.mAfFrameInfoCenterPhase = new SparseArray<>();
        updateRect_AFFixToCenter(FocusAreaController.FIX_CENTER);
        if (this.mAfFrameInfoCenterPhase.get(0) != null) {
            this.mAfFrameInfoCenterPhase.get(0).setColor(6);
        }
        this.mAfFrameInfoLocal = new SparseArray<>();
        updateRect_AFLocal(FocusAreaController.LOCAL);
        this.mAfFrameInfoWide = new SparseArray<>();
        updateRect_AFWide(FocusAreaController.WIDE);
        setAllFrameUnFocus(this.mAfFrameInfoWide);
    }

    public static void clear() {
        Log.d(TAG, "clear()");
        if (sInstance != null) {
            Log.d(TAG, "cleared");
            sInstance = null;
        }
    }

    private void updateRect_AbstractAFView(String afAreaMode) {
        Rect yuvFocusRect;
        boolean isEnable;
        Log.d(TAG, "updateRect_AbstractAFView, afAreaMode = " + afAreaMode);
        CameraEx.FocusAreaInfos info = getCurrentFocusAreaInfos(4, FocusAreaController.CENTER_WEIGHTED);
        if (info != null) {
            yuvFocusRect = info.rectInfos[0].rect;
            isEnable = info.rectInfos[0].enable;
        } else {
            yuvFocusRect = new Rect(0, 0, 0, 0);
            isEnable = false;
        }
        AFFrameInfo f = this.mAfFrameInfoIlluminator.get(0);
        if (f == null) {
            f = new AFFrameInfo();
            f.setIndex(0);
        }
        f.setFocusRect(yuvFocusRect);
        f.setEnable(isEnable);
        this.mAfFrameInfoIlluminator.append(0, f);
        this.mAfFrameInfoReturn = this.mAfFrameInfoIlluminator;
    }

    private void updateRect_AFCenterWeighted(String afAreaMode) {
        Log.d(TAG, "updateRect_AFCenterWeighted, afAreaMode = " + afAreaMode);
        CameraEx.FocusAreaInfos areaInfos = getCurrentFocusAreaInfos(4, afAreaMode);
        if (areaInfos != null) {
            Rect yuvFocusRect = areaInfos.rectInfos[1].rect;
            AFFrameInfo f = this.mAfFrameInfoCenterCont.get(0);
            if (f == null) {
                f = new AFFrameInfo();
                f.setIndex(0);
            }
            f.setFocusRect(yuvFocusRect);
            f.setEnable(areaInfos.rectInfos[1].enable);
            this.mAfFrameInfoCenterCont.append(0, f);
        }
        this.mAfFrameInfoReturn = this.mAfFrameInfoCenterCont;
    }

    private void updateRect_AFFixToCenter(String afAreaMode) {
        Log.d(TAG, "updateRect_AFFixToCenter, afAreaMode = " + afAreaMode);
        CameraEx.FocusAreaInfos areaInfos = getCurrentFocusAreaInfos(4, afAreaMode);
        if (areaInfos != null) {
            Rect yuvFocusRect = areaInfos.rectInfos[1].rect;
            AFFrameInfo f = this.mAfFrameInfoCenterPhase.get(0);
            if (f == null) {
                f = new AFFrameInfo();
                f.setIndex(0);
            }
            f.setFocusRect(yuvFocusRect);
            f.setEnable(areaInfos.rectInfos[1].enable);
            this.mAfFrameInfoCenterPhase.append(0, f);
        }
        this.mAfFrameInfoReturn = this.mAfFrameInfoCenterPhase;
    }

    private void updateRect_AFFlexible(String afAreaMode) {
        updateRect_AFFlexible(afAreaMode, false);
    }

    private void updateRect_AFFlexible(String afAreaMode, boolean displayChanged) {
        Rect yuvFocusRect;
        Log.d(TAG, "updateRect_AFFlexible, afAreaMode = " + afAreaMode);
        CameraEx.FocusAreaInfos areaInfo = getCurrentFocusAreaInfos(4, afAreaMode);
        if (areaInfo != null) {
            if (displayChanged) {
                CameraSetting.getInstance().updateParameters();
            }
            Pair<Integer, Integer> fPoint = this.mFocusAreaController.getFocusPoint();
            Rect areaRect = areaInfo.rectInfos[1].rect;
            Rect frameRect = areaInfo.rectInfos[2].rect;
            int scalarWidth = frameRect.right - frameRect.left;
            int scalarHeight = frameRect.bottom - frameRect.top;
            int scalarLeft = ((Integer) fPoint.first).intValue() - (scalarWidth / 2);
            int scalarRight = scalarLeft + scalarWidth;
            int scalarTop = ((Integer) fPoint.second).intValue() - (scalarHeight / 2);
            int scalarBottom = scalarTop + scalarHeight;
            if (scalarLeft < areaRect.left) {
                scalarLeft = areaRect.left;
                scalarRight = scalarLeft + scalarWidth;
            }
            if (scalarTop < areaRect.top) {
                scalarTop = areaRect.top;
                scalarBottom = scalarTop + scalarHeight;
            }
            if (scalarRight > areaRect.right) {
                scalarRight = areaRect.right;
                scalarLeft = scalarRight - scalarWidth;
            }
            if (scalarBottom > areaRect.bottom) {
                scalarBottom = areaRect.bottom;
                scalarTop = scalarBottom - scalarHeight;
            }
            boolean isPanelReverse = DisplayModeObserver.getInstance().isPanelReverse();
            if (!isPanelReverse) {
                yuvFocusRect = new Rect(scalarLeft, scalarTop, scalarRight, scalarBottom);
            } else {
                yuvFocusRect = new Rect(-scalarRight, scalarTop, -scalarLeft, scalarBottom);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("yuvFocusRect ->").append(" left:").append(yuvFocusRect.left).append(" Top:").append(yuvFocusRect.top).append(" Right:").append(yuvFocusRect.right).append(" Bottom:").append(yuvFocusRect.bottom).append(" PanelReverse:").append(isPanelReverse);
            Log.v(TAG, sb.toString());
            AFFrameInfo f = this.mAfFrameInfoFlexible.get(0);
            if (f == null) {
                f = new AFFrameInfo();
                f.setIndex(0);
            }
            f.setFocusRect(yuvFocusRect);
            f.setEnable(areaInfo.rectInfos[1].enable);
            this.mAfFrameInfoFlexible.append(0, f);
        }
        this.mAfFrameInfoReturn = this.mAfFrameInfoFlexible;
    }

    private void updateRect_AFMulti(String afAreaMode) {
        Log.d(TAG, "updateRect_AFMulti, afAreaMode = " + afAreaMode);
        CameraEx.FocusAreaInfos areaInfo = getCurrentFocusAreaInfos(4, afAreaMode);
        if (areaInfo != null) {
            CameraEx.FocusAreaRectInfo[] arr$ = areaInfo.rectInfos;
            for (CameraEx.FocusAreaRectInfo rectInfo : arr$) {
                Rect yuvFocusRect = rectInfo.rect;
                AFFrameInfo f = this.mAfFrameInfoMulti.get(rectInfo.index);
                if (f == null) {
                    f = new AFFrameInfo();
                    f.setIndex(rectInfo.index);
                }
                f.setFocusRect(yuvFocusRect);
                f.setEnable(rectInfo.enable);
                if (1 == f.getColor()) {
                    f.setColor(5);
                }
                this.mAfFrameInfoMulti.append(rectInfo.index, f);
            }
        }
        this.mAfFrameInfoReturn = this.mAfFrameInfoMulti;
    }

    private void updateRect_AFWide(String afAreaMode) {
        Log.d(TAG, "updateRect_AFWide, afAreaMode = " + afAreaMode);
        CameraEx.FocusAreaInfos areaInfo = getCurrentFocusAreaInfos(4, afAreaMode);
        if (areaInfo != null) {
            boolean isPF11Over = false;
            if (1 <= CameraSetting.getPfApiVersion()) {
                isPF11Over = true;
            }
            CameraEx.FocusAreaRectInfo[] arr$ = areaInfo.rectInfos;
            for (CameraEx.FocusAreaRectInfo rectInfo : arr$) {
                Rect yuvFocusRect = rectInfo.rect;
                AFFrameInfo f = this.mAfFrameInfoWide.get(rectInfo.index);
                if (f == null) {
                    f = new AFFrameInfo();
                    f.setIndex(rectInfo.index);
                }
                f.setFocusRect(yuvFocusRect);
                boolean enable = true;
                if (isPF11Over) {
                    enable = rectInfo.enable;
                }
                if (enable) {
                    if (2 == this.mCameraSetting.getCurrentMode()) {
                        f.setColor(1);
                    } else {
                        KeyStatus status = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.S1_ON);
                        if (1 == status.status) {
                            f.setColor(5);
                        } else {
                            f.setColor(1);
                        }
                    }
                } else {
                    f.setColor(5);
                }
                if (rectInfo.index == 0) {
                    f.setEnable(false);
                } else {
                    f.setEnable(enable);
                }
                this.mAfFrameInfoWide.append(rectInfo.index, f);
            }
            if (this.mIndex_AFWide != null) {
                int[] arr$2 = this.mIndex_AFWide;
                for (int frameIndex : arr$2) {
                    boolean isEnable = this.mAfFrameInfoWide.get(frameIndex).isEnable();
                    if (isEnable) {
                        AFFrameInfo f2 = this.mAfFrameInfoWide.get(frameIndex);
                        if (f2 != null) {
                            f2.setColor(3);
                        } else {
                            Log.e(TAG, "Unknown index.");
                        }
                        this.mAfFrameInfoWide.append(frameIndex, f2);
                    }
                }
            }
        }
        this.mAfFrameInfoReturn = this.mAfFrameInfoWide;
    }

    private void updateRect_AFLocal(String afAreaMode) {
        Log.d(TAG, "updateRect_AFLocal, afAreaMode = " + afAreaMode);
        CameraEx.FocusAreaInfos areaInfo = getCurrentFocusAreaInfos(4, afAreaMode);
        if (areaInfo != null) {
            boolean isPF11Over = false;
            if (1 <= CameraSetting.getPfApiVersion()) {
                isPF11Over = true;
            }
            CameraEx.FocusAreaRectInfo[] arr$ = areaInfo.rectInfos;
            for (CameraEx.FocusAreaRectInfo rectInfo : arr$) {
                Rect yuvFocusRect = rectInfo.rect;
                AFFrameInfo f = this.mAfFrameInfoLocal.get(rectInfo.index);
                if (f == null) {
                    f = new AFFrameInfo();
                    f.setIndex(rectInfo.index);
                }
                f.setFocusRect(yuvFocusRect);
                boolean enable = true;
                if (isPF11Over) {
                    enable = rectInfo.enable;
                }
                if (enable) {
                    KeyStatus status = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.S1_ON);
                    if (1 == status.status) {
                        f.setColor(5);
                    } else {
                        f.setColor(1);
                    }
                } else {
                    f.setColor(5);
                }
                if (rectInfo.index == 0) {
                    f.setEnable(false);
                } else {
                    f.setEnable(enable);
                }
                this.mAfFrameInfoLocal.append(rectInfo.index, f);
            }
            this.mFocusReadyFrameIndex_AFLocal = this.mFocusAreaController.getFocusIndex();
            Log.d(TAG, "mFocusReadyFrameIndex_AFLocal=" + this.mFocusReadyFrameIndex_AFLocal);
            if (!this.mAfFrameInfoLocal.get(this.mFocusReadyFrameIndex_AFLocal).isEnable()) {
                Log.d(TAG, "mFocusReadyFrameIndex_AFLocal=" + this.mFocusReadyFrameIndex_AFLocal + " is disable");
            }
            if (this.mFocusReadyFrameIndex_AFLocal < 1) {
                Log.e(TAG, "Invalid focus frame index. Sets mFocusReadyFrameIndex_AFLocal to 1");
                this.mFocusReadyFrameIndex_AFLocal = 1;
            }
            this.mAfFrameInfoLocal.get(this.mFocusReadyFrameIndex_AFLocal).setColor(6);
            if (this.mIndex_AFLocal != null) {
                int[] arr$2 = this.mIndex_AFLocal;
                for (int frameIndex : arr$2) {
                    boolean isEnable = this.mAfFrameInfoLocal.get(frameIndex).isEnable();
                    if (isEnable) {
                        AFFrameInfo f2 = this.mAfFrameInfoLocal.get(frameIndex);
                        if (f2 != null) {
                            f2.setColor(3);
                        } else {
                            Log.e(TAG, "Unknown index.");
                        }
                        this.mAfFrameInfoLocal.append(frameIndex, f2);
                    }
                }
            }
        }
        this.mAfFrameInfoReturn = this.mAfFrameInfoLocal;
    }

    private String getAfAreaMode(String tag, boolean isShowIlluminator) {
        Log.v(TAG, "getAfAreaMode tag=" + tag + "  isShowIlluminator=" + isShowIlluminator);
        String focusMode = this.mFocusModeController.getValue();
        CameraEx.LensInfo lensinfo = this.mCameraSetting.getLensInfo();
        if (lensinfo == null) {
            Log.d(TAG, "lensInfo is null");
            clearLocalDataAll(isShowIlluminator);
            return AF_AREA_MODE_INVALID;
        }
        if (lensinfo == null || focusMode == null) {
            return AF_AREA_MODE_INVALID;
        }
        try {
            String afAreaMode = this.mFocusAreaController.getValue();
            CameraEx.FocusAreaInfos fais = this.mCameraSetting.getFocusAreaRectInfos(afAreaMode, -1, 4);
            if (fais == null) {
                Log.e(TAG, "FocusAreaInfos is null");
                return AF_AREA_MODE_INVALID;
            }
            if (FocusModeController.MANUAL.equals(focusMode)) {
                Log.d(TAG, "Focus Mode is MANUAL");
                clearLocalDataAll(isShowIlluminator);
                return AF_AREA_MODE_INVALID;
            }
            if (isShowIlluminator) {
                if (!CameraNotificationManager.ZOOM_INFO_CHANGED.equals(tag)) {
                    return FocusAreaController.AF_FIXED_TO_ILLUINATOR_FRAME;
                }
                if (!this.mIsDigitalZoom) {
                    Log.d(TAG, "Zoom Status is Optical -> Digital");
                    clearLocalDataAll(isShowIlluminator);
                }
                this.mIsDigitalZoom = true;
                return FocusAreaController.AF_FIXED_TO_ILLUINATOR_FRAME;
            }
            if (this.mIsDigitalZoom) {
                Log.d(TAG, "afAreaMode : " + afAreaMode + " -> AfAreaModeInvalid");
                this.mIsDigitalZoom = false;
                return AF_AREA_MODE_INVALID;
            }
            return afAreaMode;
        } catch (IController.NotSupportedException e) {
            Log.e(TAG, "NotSupportedException by FocusAreaController.getValue()");
            return AF_AREA_MODE_INVALID;
        }
    }

    private void switchIlluminatorVisibility(int status, boolean isShowIlluminator, AFFrameInfo afFrameInfo) {
        if (afFrameInfo != null) {
            if (status == 1) {
                if (isShowIlluminator) {
                    afFrameInfo.setColor(3);
                    return;
                } else {
                    afFrameInfo.setColor(5);
                    return;
                }
            }
            if (status == 2) {
                if (isShowIlluminator) {
                    afFrameInfo.setColor(1);
                    return;
                } else {
                    afFrameInfo.setColor(5);
                    return;
                }
            }
            if (status == 3) {
                afFrameInfo.setColor(5);
                return;
            }
            return;
        }
        Log.e(TAG, "afFrameInfo is null.");
    }

    public String isShowIlluminator() {
        String isShowIlluminator = Boolean.toString(false);
        if (this.mIsFocusedAsIlluminator) {
            return Boolean.toString(true);
        }
        if (this.mFocusAreaController.getSensorType() == 2) {
            String digitalZoomMode = this.mDigitalZoomController.getCurrentDigitalZoomMode();
            if (DigitalZoomController.TAG_DIGITAL_ZOOM_MODE_NOT_INITIALIZED.equals(digitalZoomMode)) {
                Log.d(TAG, "digitalZoomMode = " + digitalZoomMode);
                return SHOW_ILLUMINATOR_INVALID;
            }
            if (!"digitalZoomModeNotSet".equals(digitalZoomMode)) {
                return Boolean.toString(true);
            }
            return isShowIlluminator;
        }
        if (this.mFocusAreaController.getSensorType() == 1 && this.mFocusAreaController.isDigitalZoomMagOverAFAreaChangeToCenter()) {
            return Boolean.toString(true);
        }
        return isShowIlluminator;
    }

    private boolean isAFFocused() {
        CameraNotificationManager.OnFocusInfo onFocusInfo = (CameraNotificationManager.OnFocusInfo) this.mCameraNotifier.getValue(CameraNotificationManager.DONE_AUTO_FOCUS);
        return onFocusInfo != null && (onFocusInfo.status == 1 || onFocusInfo.status == 4);
    }

    private void switchFrameVisibility(int status, boolean isShowIlluminator, AFFrameInfo afFrameInfo) {
        if (afFrameInfo != null) {
            if (status == 1) {
                if (isShowIlluminator) {
                    afFrameInfo.setColor(5);
                    return;
                } else {
                    afFrameInfo.setColor(3);
                    return;
                }
            }
            if (status == 2) {
                if (isShowIlluminator) {
                    afFrameInfo.setColor(5);
                    return;
                } else {
                    afFrameInfo.setColor(1);
                    return;
                }
            }
            if (status == 3) {
                afFrameInfo.setColor(5);
                return;
            }
            return;
        }
        Log.e(TAG, "afFrameInfo is null.");
    }

    private void updateFocusAreaVisibility(SparseArray<AFFrameInfo> afFrameInfoAry) {
        try {
            String fdValue = FaceDetectionController.getInstance().getValue();
            if ("off".equals(fdValue)) {
                afFrameInfoAry.valueAt(0).setColor(1);
            } else {
                afFrameInfoAry.valueAt(0).setColor(5);
            }
        } catch (IController.NotSupportedException e) {
            Log.w(TAG, "Cannot get face detection setting.");
        }
    }

    private void setAllFrameInvisible(SparseArray<AFFrameInfo> afFrameInfoAry) {
        if (afFrameInfoAry != null) {
            int size = afFrameInfoAry.size();
            for (int i = 0; i < size; i++) {
                if (afFrameInfoAry.get(i).isEnable()) {
                    afFrameInfoAry.get(i).setColor(5);
                }
            }
            return;
        }
        Log.e(TAG, "afFrameInfo is null.");
    }

    private void setAllFrameUnFocus(SparseArray<AFFrameInfo> afFrameInfoAry) {
        if (afFrameInfoAry != null) {
            int size = afFrameInfoAry.size();
            for (int i = 0; i < size; i++) {
                if (afFrameInfoAry.get(i).isEnable()) {
                    afFrameInfoAry.get(i).setColor(1);
                }
            }
            return;
        }
        Log.e(TAG, "afFrameInfo is null.");
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private boolean switchDoneAutoFocus(boolean illuminator, String tag) {
        boolean ret = false;
        if (CameraNotificationManager.DONE_AUTO_FOCUS.equals(tag)) {
            ret = true;
            this.mIsFocusedAsIlluminator = false;
            CameraNotificationManager.OnFocusInfo onFocusInfo = (CameraNotificationManager.OnFocusInfo) this.mCameraNotifier.getValue(tag);
            if (onFocusInfo != null) {
                switch (onFocusInfo.status) {
                    case 0:
                        onNeutral(illuminator);
                        break;
                    case 1:
                        if (onFocusInfo.area != null) {
                            int[] arr$ = onFocusInfo.area;
                            int len$ = arr$.length;
                            int i$ = 0;
                            while (true) {
                                if (i$ < len$) {
                                    int index = arr$[i$];
                                    if (index != 0) {
                                        i$++;
                                    } else {
                                        this.mIsFocusedAsIlluminator = true;
                                    }
                                }
                            }
                            onFocused(onFocusInfo.area, illuminator);
                            break;
                        } else {
                            onFaceLocked();
                            break;
                        }
                    case 2:
                        onLockWarn(illuminator);
                        break;
                    case 4:
                        if (onFocusInfo.area != null) {
                            onContinuous(onFocusInfo.area, illuminator);
                            break;
                        } else {
                            int[] empty_array = new int[0];
                            onContinuous(empty_array, illuminator);
                            break;
                        }
                }
            }
        }
        return ret;
    }

    public boolean updateWithCurrentStatus(boolean illuminator) {
        Log.d(TAG, "updateWithCurrentStatus, mAfAreaMode = " + this.mAfAreaMode);
        if (this.mAfAreaMode.equals(FocusAreaController.CENTER_WEIGHTED)) {
            updateRect_AFCenterWeighted(this.mAfAreaMode);
        } else if (this.mAfAreaMode.equals(FocusAreaController.FIX_CENTER)) {
            updateRect_AFFixToCenter(this.mAfAreaMode);
        } else if (this.mAfAreaMode.equals("flex-spot")) {
            updateRect_AFFlexible(this.mAfAreaMode);
        } else if (this.mAfAreaMode.equals(FocusAreaController.LOCAL)) {
            updateRect_AFLocal(this.mAfAreaMode);
        } else if (this.mAfAreaMode.equals(FocusAreaController.MULTI)) {
            updateRect_AFMulti(this.mAfAreaMode);
        } else if (this.mAfAreaMode.equals(FocusAreaController.WIDE)) {
            updateRect_AFWide(this.mAfAreaMode);
        } else if (this.mAfAreaMode.equals(FocusAreaController.AF_FIXED_TO_ILLUINATOR_FRAME)) {
            updateRect_AbstractAFView(this.mAfAreaMode);
        }
        return switchDoneAutoFocus(illuminator, CameraNotificationManager.DONE_AUTO_FOCUS);
    }

    private void clearLocalData(String mAfAreaMode, boolean illuminator) {
        Log.d(TAG, "clearLocalData, mAfAreaMode = " + mAfAreaMode);
        if (mAfAreaMode.equals(FocusAreaController.CENTER_WEIGHTED)) {
            switchFrameVisibility(2, illuminator, this.mAfFrameInfoCenterCont.get(0));
            return;
        }
        if (mAfAreaMode.equals(FocusAreaController.FIX_CENTER)) {
            if (this.mAfFrameInfoCenterPhase.get(0) != null) {
                this.mAfFrameInfoCenterPhase.get(0).setColor(6);
                return;
            }
            return;
        }
        if (mAfAreaMode.equals("flex-spot")) {
            if (this.mAfFrameInfoFlexible.get(0) != null) {
                this.mAfFrameInfoFlexible.get(0).setColor(1);
                return;
            }
            return;
        }
        if (mAfAreaMode.equals(FocusAreaController.LOCAL)) {
            setAllFrameUnFocus(this.mAfFrameInfoLocal);
            if (this.mAfFrameInfoLocal.get(this.mFocusReadyFrameIndex_AFLocal) != null) {
                this.mAfFrameInfoLocal.get(this.mFocusReadyFrameIndex_AFLocal).setColor(6);
            }
            this.mIndex_AFLocal = null;
            return;
        }
        if (mAfAreaMode.equals(FocusAreaController.MULTI)) {
            setAllFrameInvisible(this.mAfFrameInfoMulti);
            return;
        }
        if (mAfAreaMode.equals(FocusAreaController.WIDE)) {
            setAllFrameUnFocus(this.mAfFrameInfoWide);
            this.mIndex_AFWide = null;
        } else if (mAfAreaMode.equals(FocusAreaController.AF_FIXED_TO_ILLUINATOR_FRAME)) {
            switchIlluminatorVisibility(2, illuminator, this.mAfFrameInfoIlluminator.get(0));
        }
    }

    private void clearLocalDataAll(boolean isShowIlluminator) {
        Log.d(TAG, "clearLocalDataAll");
        clearLocalData(FocusAreaController.CENTER_WEIGHTED, isShowIlluminator);
        clearLocalData(FocusAreaController.FIX_CENTER, isShowIlluminator);
        clearLocalData("flex-spot", isShowIlluminator);
        clearLocalData(FocusAreaController.LOCAL, isShowIlluminator);
        clearLocalData(FocusAreaController.MULTI, isShowIlluminator);
        clearLocalData(FocusAreaController.WIDE, isShowIlluminator);
        clearLocalData(FocusAreaController.AF_FIXED_TO_ILLUINATOR_FRAME, isShowIlluminator);
    }

    /* loaded from: classes.dex */
    public class AFFrameInfo {
        private int mSensorType;
        private String mFocusAreaMode = null;
        private int mIndex = -1;
        private int mColor = 5;
        private Rect mRect = null;
        private boolean mEnable = false;

        /* renamed from: clone, reason: merged with bridge method [inline-methods] */
        public AFFrameInfo m0clone() {
            AFFrameInfo result = new AFFrameInfo();
            result.mFocusAreaMode = this.mFocusAreaMode;
            result.mSensorType = this.mSensorType;
            result.mIndex = this.mIndex;
            result.mColor = this.mColor;
            result.mRect = this.mRect;
            result.mEnable = this.mEnable;
            return result;
        }

        public AFFrameInfo() {
        }

        public String toString() {
            String strColor;
            StringBuilder sb = new StringBuilder();
            sb.append(AFFrameInfo.class.getSimpleName()).append(":");
            sb.append(" Index=").append(this.mIndex);
            sb.append(" FocusAreaMode=").append(this.mFocusAreaMode);
            switch (this.mColor) {
                case 1:
                    strColor = "GRAY";
                    break;
                case 2:
                    strColor = "ORANGE";
                    break;
                case 3:
                    strColor = "GREEN";
                    break;
                case 4:
                    strColor = "WHITE";
                    break;
                case 5:
                    strColor = "CLEAR";
                    break;
                case 6:
                    strColor = "BLACK";
                    break;
                default:
                    strColor = "Unknown";
                    break;
            }
            sb.append(" Color=").append(strColor);
            sb.append(" Enable=").append(this.mEnable);
            if (this.mRect != null) {
                sb.append(" Rect:").append(" top=").append(this.mRect.top).append(" bottom=").append(this.mRect.bottom).append(" left=").append(this.mRect.left).append(" right=").append(this.mRect.right);
            }
            return sb.toString();
        }

        public void setSensorType(int sensorType) {
            this.mSensorType = sensorType;
        }

        public int getSensorType() {
            return this.mSensorType;
        }

        public void setFocusAreaMode(String focusAreaMode) {
            this.mFocusAreaMode = focusAreaMode;
        }

        public String getFocusAreaMode() {
            return this.mFocusAreaMode;
        }

        public void setIndex(int index) {
            this.mIndex = index;
        }

        public int getIndex() {
            return this.mIndex;
        }

        public void setColor(int status) {
            switch (status) {
                case 1:
                    this.mColor = 1;
                    return;
                case 2:
                    this.mColor = 2;
                    return;
                case 3:
                    this.mColor = 3;
                    return;
                case 4:
                    this.mColor = 4;
                    return;
                case 5:
                    this.mColor = 5;
                    return;
                case 6:
                    this.mColor = 6;
                    return;
                default:
                    this.mColor = 5;
                    Log.e(AFFrameConverter.TAG, "status=" + status + "is Invalid");
                    return;
            }
        }

        public int getColor() {
            return this.mColor;
        }

        public void setEnable(boolean enable) {
            this.mEnable = enable;
        }

        public boolean isEnable() {
            return this.mEnable;
        }

        public void setFocusRect(Rect yuvFocusRect) {
            this.mRect = yuvFocusRect;
        }

        public Rect getFocusRect() {
            return this.mRect;
        }
    }
}
