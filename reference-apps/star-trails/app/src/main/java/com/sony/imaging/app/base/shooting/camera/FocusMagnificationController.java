package com.sony.imaging.app.base.shooting.camera;

import android.graphics.Rect;
import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.startrails.util.STConstants;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.avio.DisplayManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class FocusMagnificationController extends ShootingModeController {
    private static final int API_VER_SUPPORTS_MAGNIFICATION_IN_AUTOFOCUS_SETTINGS = 15;
    private static final int COORDINATE_CENTER = 0;
    private static final int DEFAULT_STEP_H_11 = 8;
    private static final int DEFAULT_STEP_H_169 = 12;
    private static final int DEFAULT_STEP_H_32 = 12;
    private static final int DEFAULT_STEP_H_43 = 10;
    private static final int DEFAULT_STEP_V_11 = 12;
    private static final int DEFAULT_STEP_V_169 = 10;
    private static final int DEFAULT_STEP_V_32 = 12;
    private static final int DEFAULT_STEP_V_43 = 12;
    private static final int FOCUS_DRIVE_MIN = 0;
    private static final String INH_ID_FOCUS_MAGNIFICATION = "INH_FEATURE_CAM_MSGID_SET_CAM_EXPAND_FOCUS_MODE";
    public static final String ITEM_ID_FOCUS_MAGNIFICATION = "FocusMagnifier";
    public static final int MAGNIFICATION_RATIO_1_0 = 1;
    public static final int MAGNIFICATION_RATIO_NO_ZOOM = 100;
    public static final int MOVING_STEP_USE_DEFAULT = 0;
    private static final String MSG_CANCEL_TIMER = "cancelTimeout";
    private static final String MSG_GET_TIMEOUT_IN_SETTINGS = "getTimeoutDurationInSettings: ";
    private static final String MSG_IS_AVAILABLE_IN_SETTINGS = "isAvailableInSettings: ";
    private static final String MSG_MOVE_BY_FIXED_MAGNIFICATION_POINT = "moveStepsBy but NOT Magnification Point Fixed";
    private static final String MSG_MOVE_BY_WITHOUT_MAGNIGYING = "moveStepsBy but NOT magnifying";
    private static final String MSG_SCHEDULE_TIMER = "scheduleTimeout";
    private static final String MSG_SET_MAGNIFICATION_NOT_MAGNIFYING = "setMagnifyingPosition ignored because not magnifying";
    private static final int NORMALIZED_SIZE_OF_PF_EE = 2000;
    public static final String OFF = "off";
    public static final String ON = "on";
    private static final float POWER_TO_16_9_FROM_3_2 = 1.1851852f;
    private static final float POWER_TO_1_1_FROM_3_2 = 1.5f;
    private static final float POWER_TO_4_3_FROM_3_2 = 1.125f;
    private static final int SECOND_TO_MS = 1000;
    private static final String TAG = "FocusMagnificationController";
    public static final String TAG_ACTUAL_MAGNIFICATION_RATIO = "actual_magnification_ratio";
    public static final String TAG_IS_MFASSIST_AVAILABLE_IN_SETTINGS = "available_in_settings";
    public static final String TAG_MAGNIFICATION_RATIO = "magnification_ratio";
    public static final String TAG_TIMEOUT_DURATION = "timeout_duration";
    public static final int TIMEOUT_DURATION_AS_IN_SETTINGS = -1;
    public static final int TIMEOUT_DURATION_INFINIT = 0;
    private static final Integer ARBITARY_RATIO = new Integer(0);
    private static FocusMagnificationController mInstance = null;
    private static final String myName = FocusMagnificationController.class.getSimpleName();
    private static final String[] tags = {"AutoFocusMode", CameraNotificationManager.FOCUS_CHANGE, CameraNotificationManager.REC_MODE_CHANGED, CameraNotificationManager.DEVICE_LENS_CHANGED, CameraNotificationManager.ZOOM_INFO_CHANGED};
    protected int mTimeoutDuration = -1;
    protected int mNumOfStepH = 0;
    protected int mNumOfStepV = 0;
    private AvailableInfo.IInhFactorChange mInhFactorListener = new AvailableInfo.IInhFactorChange() { // from class: com.sony.imaging.app.base.shooting.camera.FocusMagnificationController.1
        @Override // com.sony.imaging.app.util.AvailableInfo.IInhFactorChange
        public void onInhFactorChanged(String factorID, int value) {
            CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.FOCUS_MAGNIFICATION_AVAILABILITY_CHANGED);
        }
    };
    private NotificationListener mListener = new NotificationListener() { // from class: com.sony.imaging.app.base.shooting.camera.FocusMagnificationController.2
        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (CameraNotificationManager.ZOOM_INFO_CHANGED.equals(tag)) {
                boolean currentIsAvailableInDigitalZoom = FocusMagnificationController.this.isAvailableInDigitalZoomInner();
                if (currentIsAvailableInDigitalZoom != FocusMagnificationController.this.mIsAvailableInDigitalZoom) {
                    FocusMagnificationController.this.mIsAvailableInDigitalZoom = currentIsAvailableInDigitalZoom;
                    CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.FOCUS_MAGNIFICATION_AVAILABILITY_CHANGED);
                    return;
                }
                return;
            }
            CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.FOCUS_MAGNIFICATION_AVAILABILITY_CHANGED);
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return FocusMagnificationController.tags;
        }
    };
    private boolean mIsAvailableInDigitalZoom = false;
    protected CameraSetting mCamSet = CameraSetting.getInstance();

    public static final String getName() {
        return myName;
    }

    protected FocusMagnificationController() {
        setController(this);
    }

    public static FocusMagnificationController getInstance() {
        if (mInstance == null) {
            new FocusMagnificationController();
        }
        return mInstance;
    }

    private static void setController(FocusMagnificationController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    public static boolean isSupportedByPf() {
        return 1 <= CameraSetting.getPfApiVersion();
    }

    public boolean isSupported() {
        List<Integer> supported;
        return isSupportedByPf() && (supported = getSupportedValueInt(TAG_MAGNIFICATION_RATIO)) != null && supported.size() > 0;
    }

    public boolean isAvailableInSettings() {
        if (!isSupportedByPf()) {
            return false;
        }
        boolean result = ((CameraEx.ParametersModifier) this.mCamSet.getParameters().second).getPreviewMagnificationOnManualFocus();
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), MSG_IS_AVAILABLE_IN_SETTINGS).append(result);
        Log.d(TAG, builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        return result;
    }

    public int getTimeoutDurationInSettings() {
        if (!isSupportedByPf()) {
            return 0;
        }
        int result = ((CameraEx.ParametersModifier) this.mCamSet.getParameters().second).getPreviewMagnificationTimeOnManualFocus() * SECOND_TO_MS;
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), MSG_GET_TIMEOUT_IN_SETTINGS).append(result);
        Log.d(TAG, builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        return result;
    }

    public int getTimeoutDuration() {
        int result = this.mTimeoutDuration;
        if (-1 == result) {
            return getTimeoutDurationInSettings();
        }
        return result;
    }

    public void setTimeoutDuration(int duration) {
        this.mTimeoutDuration = duration;
        if (this.mCamSet.mFocusMagnificationRatio != 0 && this.mCamSet.mMfAssistTimeoutTask != null) {
            rescheduleTimeout();
        }
    }

    public void resetTimeoutDuration() {
        setTimeoutDuration(-1);
    }

    public int getMagnificationRatio() {
        return this.mCamSet.mFocusMagnificationRatio;
    }

    public void setMagnificationRatio(int ratio) {
        start(ratio);
    }

    public int getActualMagnificationRatio() {
        return this.mCamSet.mFocusMagnificationActualRatio;
    }

    public boolean isMagnifying() {
        return this.mCamSet.mFocusMagnificationRatio != 0;
    }

    public int toggleMagnificationRatio() {
        int index;
        Integer ratio;
        List<Integer> supported = getSupportedValueInt(TAG_MAGNIFICATION_RATIO);
        if (supported == null || supported.size() == 0) {
            return -1;
        }
        int index2 = supported.indexOf(Integer.valueOf(this.mCamSet.mFocusMagnificationRatio));
        if (-1 == index2) {
            index = 0;
        } else {
            index = index2 + 1;
            if (supported.size() <= index) {
                if (!this.mCamSet.mUseMagnifyRatio1_0) {
                    index = 0;
                } else {
                    index = -1;
                }
            }
        }
        if (index >= 0) {
            ratio = supported.get(index);
        } else {
            ratio = 1;
        }
        setValue(TAG_MAGNIFICATION_RATIO, ratio.toString());
        return ratio.intValue();
    }

    public boolean isArbitaryRatioSupported() {
        boolean result = ((CameraEx.ParametersModifier) this.mCamSet.getSupportedParameters().second).getSupportedPreviewMagnification().contains(ARBITARY_RATIO);
        return result;
    }

    public Rect calcMagnifyingPositionRect(int width, int height) {
        int ratio = this.mCamSet.mFocusMagnificationActualRatio;
        if (ratio <= 0) {
            return null;
        }
        Pair<Float, Float> frame = getMagnifyingFrameSize(ratio);
        float ratioW = width / 2000.0f;
        float ratioH = height / 2000.0f;
        float halfFrameW = (((Float) frame.first).floatValue() * ratioW) / 2.0f;
        float halfFrameH = (((Float) frame.second).floatValue() * ratioH) / 2.0f;
        float x = ((DisplayModeObserver.getInstance().isPanelReverse() ? -((Integer) this.mCamSet.mMagnifyingPosition.first).intValue() : ((Integer) this.mCamSet.mMagnifyingPosition.first).intValue()) + 1000.0f) * ratioW;
        float y = (((Integer) this.mCamSet.mMagnifyingPosition.second).intValue() + 1000.0f) * ratioH;
        Rect r = new Rect(Math.round(x - halfFrameW), Math.round(y - halfFrameH), Math.round(x + halfFrameW), Math.round(y + halfFrameH));
        return r;
    }

    public Rect calcMagnifyingPositionRectRatio1_0(int width, int height) {
        int lowestRatio = 0;
        List<Integer> supported = getSupportedValueInt(TAG_MAGNIFICATION_RATIO);
        if (supported != null && supported.size() > 0) {
            lowestRatio = this.mCamSet.getFocusMagnificationRatio(supported.get(0).intValue());
        }
        if (lowestRatio <= 0) {
            return null;
        }
        Pair<Float, Float> frame = getMagnifyingFrameSize(lowestRatio);
        float ratioW = width / 2000.0f;
        float ratioH = height / 2000.0f;
        float halfFrameW = (((Float) frame.first).floatValue() * ratioW) / 2.0f;
        float halfFrameH = (((Float) frame.second).floatValue() * ratioH) / 2.0f;
        float x = (1000.0f + (DisplayModeObserver.getInstance().isPanelReverse() ? -((Integer) this.mCamSet.mMagnifyingPosition.first).intValue() : ((Integer) this.mCamSet.mMagnifyingPosition.first).intValue())) * ratioW;
        float y = (1000.0f + ((Integer) this.mCamSet.mMagnifyingPosition.second).intValue()) * ratioH;
        Rect r = new Rect(Math.round(x - halfFrameW), Math.round(y - halfFrameH), Math.round(x + halfFrameW), Math.round(y + halfFrameH));
        return r;
    }

    public Pair<Integer, Integer> getMagnifyingPosition() {
        if (isSupportedByPf()) {
            return convertToStep(this.mCamSet.mMagnifyingPosition);
        }
        return null;
    }

    protected void setMagnifyingPosition(Pair<Integer, Integer> position) {
        if (isSupportedByPf()) {
            Pair<Integer, Integer> position2 = convertToPfCoordinate(position);
            if (this.mCamSet.mFocusMagnificationRatio != 0) {
                this.mCamSet.startFocusMagnification(this.mCamSet.mFocusMagnificationRatio, position2);
            } else {
                Log.w(TAG, MSG_SET_MAGNIFICATION_NOT_MAGNIFYING);
            }
        }
    }

    public void resetMagnifyingPosition() {
        if (isMagnifying()) {
            setMagnifyingPosition(new Pair<>(0, 0));
        } else {
            this.mCamSet.mMagnifyingPosition = null;
            this.mCamSet.mMfLastMagnifyingPosition = null;
        }
    }

    public boolean moveToCoordinateOnEE(int x, int y) {
        Pair<Integer, Integer> position = convertCoordinateOnEEToApp(x, y);
        if (position == null || (this.mCamSet.mMagnifyingPosition != null && position.equals(convertToStep(this.mCamSet.mMagnifyingPosition)))) {
            return false;
        }
        setMagnifyingPosition(position);
        return true;
    }

    public boolean moveStepsBy(int dx, int dy) {
        if (isSupportedByPf()) {
            if (this.mCamSet.mMagnifyingPosition == null || this.mCamSet.mFocusMagnificationRatio == 0) {
                Log.w(TAG, MSG_MOVE_BY_WITHOUT_MAGNIGYING);
                return false;
            }
            if (!isAvailableMagnificationPointMove()) {
                Log.w(TAG, MSG_MOVE_BY_FIXED_MAGNIFICATION_POINT);
                return false;
            }
            Pair<Integer, Integer> current = convertToStep(this.mCamSet.mMagnifyingPosition);
            int x = ((Integer) current.first).intValue() + dx;
            int y = ((Integer) current.second).intValue() + dy;
            int max = getNumOfStepsH();
            int x2 = Math.min(Math.max(x, -max), max);
            int max2 = getNumOfStepsV();
            int y2 = Math.min(Math.max(y, -max2), max2);
            if (x2 != ((Integer) current.first).intValue() || y2 != ((Integer) current.second).intValue()) {
                setMagnifyingPosition(new Pair<>(Integer.valueOf(x2), Integer.valueOf(y2)));
                return true;
            }
        }
        return false;
    }

    public void setNumberOfMovingSteps(Pair<Integer, Integer> steps) {
        this.mNumOfStepH = ((Integer) steps.first).intValue();
        this.mNumOfStepV = ((Integer) steps.second).intValue();
    }

    public Pair<Integer, Integer> getNumberOfMovingSteps() {
        return Pair.create(Integer.valueOf(getNumOfStepsH()), Integer.valueOf(getNumOfStepsV()));
    }

    protected int getNumOfStepsH() {
        int h = this.mNumOfStepH;
        if (h == 0) {
            String aspect = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
            if (PictureSizeController.ASPECT_3_2.equals(aspect) || PictureSizeController.ASPECT_16_9.equals(aspect)) {
                return 12;
            }
            if (PictureSizeController.ASPECT_4_3.equals(aspect)) {
                return 10;
            }
            if (PictureSizeController.ASPECT_1_1.equals(aspect)) {
                return 8;
            }
            return 12;
        }
        return h;
    }

    protected int getNumOfStepsV() {
        int v = this.mNumOfStepV;
        if (v == 0) {
            String aspect = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
            if (PictureSizeController.ASPECT_3_2.equals(aspect)) {
                return 12;
            }
            if (PictureSizeController.ASPECT_16_9.equals(aspect)) {
                return 10;
            }
            if (!PictureSizeController.ASPECT_4_3.equals(aspect) && PictureSizeController.ASPECT_1_1.equals(aspect)) {
                return 12;
            }
            return 12;
        }
        return v;
    }

    public Pair<Integer, Integer> convertCoordinateOnEEToApp(int x, int y) {
        List<Integer> supported;
        DisplayManager.VideoRect yuvArea = (DisplayManager.VideoRect) DisplayModeObserver.getInstance().getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
        int magnificationRatio = this.mCamSet.mFocusMagnificationActualRatio;
        if (magnificationRatio == 0) {
            magnificationRatio = 100;
        } else if (magnificationRatio == 100 && (supported = getSupportedValueInt(TAG_MAGNIFICATION_RATIO)) != null && supported.size() > 0) {
            magnificationRatio = this.mCamSet.getFocusMagnificationRatio(supported.get(0).intValue());
        }
        int displayRatio = Math.max(magnificationRatio, DigitalZoomController.getInstance().getCurrentDigitalZoomMagnification());
        int yuvCenterX = (yuvArea.pxLeft + yuvArea.pxRight) >> 1;
        int yuvCenterY = (yuvArea.pxTop + yuvArea.pxBottom) >> 1;
        int yuvWidth = yuvArea.pxRight - yuvArea.pxLeft;
        int yuvHeight = yuvArea.pxBottom - yuvArea.pxTop;
        float power = 2000.0f / (displayRatio / 100.0f);
        int diffX = (int) (((x - yuvCenterX) * power) / yuvWidth);
        int diffY = (int) (((y - yuvCenterY) * power) / yuvHeight);
        if (DisplayModeObserver.getInstance().isPanelReverse()) {
            diffX = -diffX;
        }
        Pair<Integer, Integer> current = this.mCamSet.mMagnifyingPosition;
        return convertToStep((100 == magnificationRatio || current == null) ? Pair.create(Integer.valueOf(diffX), Integer.valueOf(diffY)) : Pair.create(Integer.valueOf(((Integer) current.first).intValue() + diffX), Integer.valueOf(((Integer) current.second).intValue() + diffY)));
    }

    protected Pair<Float, Float> getMagnifyingFrameSize(int ratio) {
        if (100 >= ratio || Integer.MAX_VALUE == ratio) {
            ratio = 100;
        }
        float size = 200000 / ratio;
        float w = size;
        float h = size;
        String aspect = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
        if (!PictureSizeController.ASPECT_3_2.equals(aspect)) {
            if (PictureSizeController.ASPECT_16_9.equals(aspect)) {
                h *= POWER_TO_16_9_FROM_3_2;
            } else if (PictureSizeController.ASPECT_4_3.equals(aspect)) {
                w *= POWER_TO_4_3_FROM_3_2;
            } else if (PictureSizeController.ASPECT_1_1.equals(aspect)) {
                w *= POWER_TO_1_1_FROM_3_2;
            }
        }
        return Pair.create(Float.valueOf(w), Float.valueOf(h));
    }

    protected Pair<Float, Float> getDistancePerUnitStep() {
        Pair<Float, Float> frame;
        int lowestRatio = 0;
        List<Integer> supported = getSupportedValueInt(TAG_MAGNIFICATION_RATIO);
        if (supported != null && supported.size() > 0) {
            lowestRatio = this.mCamSet.getFocusMagnificationRatio(supported.get(0).intValue());
        }
        if (lowestRatio == 0) {
            frame = Pair.create(Float.valueOf(STConstants.INVALID_APERTURE_VALUE), Float.valueOf(STConstants.INVALID_APERTURE_VALUE));
        } else {
            frame = getMagnifyingFrameSize(lowestRatio);
        }
        int half_step_h = getNumOfStepsH();
        int half_step_v = getNumOfStepsV();
        return Pair.create(Float.valueOf((2000.0f - ((Float) frame.first).floatValue()) / (half_step_h * 2)), Float.valueOf((2000.0f - ((Float) frame.second).floatValue()) / (half_step_v * 2)));
    }

    public Pair<Integer, Integer> convertToStep(Pair<Integer, Integer> coordinate) {
        if (coordinate == null) {
            return null;
        }
        Pair<Float, Float> distance = getDistancePerUnitStep();
        float distance_h = ((Float) distance.first).floatValue();
        float distance_v = ((Float) distance.second).floatValue();
        int indexX = ((Integer) coordinate.first).intValue() >= 0 ? Math.round(((Integer) coordinate.first).intValue() / distance_h) : -Math.round((-((Integer) coordinate.first).intValue()) / distance_h);
        int max = getNumOfStepsH();
        int indexX2 = Math.min(Math.max(indexX, -max), max);
        int max2 = getNumOfStepsV();
        int indexY = ((Integer) coordinate.second).intValue() >= 0 ? Math.round(((Integer) coordinate.second).intValue() / distance_v) : -Math.round((-((Integer) coordinate.second).intValue()) / distance_v);
        return new Pair<>(Integer.valueOf(indexX2), Integer.valueOf(Math.min(Math.max(indexY, -max2), max2)));
    }

    public Pair<Integer, Integer> convertToPfCoordinate(Pair<Integer, Integer> step) {
        Pair<Float, Float> distance = getDistancePerUnitStep();
        return new Pair<>(Integer.valueOf((int) (((Float) distance.first).floatValue() * ((Integer) step.first).intValue())), Integer.valueOf((int) (((Float) distance.second).floatValue() * ((Integer) step.second).intValue())));
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        if (TAG_TIMEOUT_DURATION.equals(tag)) {
            setTimeoutDuration(Integer.parseInt(value));
        } else if (TAG_MAGNIFICATION_RATIO.equals(tag)) {
            setMagnificationRatio(Integer.parseInt(value));
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        if (TAG_IS_MFASSIST_AVAILABLE_IN_SETTINGS.equals(tag)) {
            return isAvailableInSettings() ? "on" : "off";
        }
        if (TAG_TIMEOUT_DURATION.equals(tag)) {
            String value = Integer.toString(getTimeoutDuration());
            return value;
        }
        if (TAG_MAGNIFICATION_RATIO.equals(tag)) {
            String value2 = Integer.toString(getMagnificationRatio());
            return value2;
        }
        if (!TAG_ACTUAL_MAGNIFICATION_RATIO.equals(tag)) {
            return null;
        }
        String value3 = Integer.toString(getActualMagnificationRatio());
        return value3;
    }

    public boolean isFocusRingRotating() {
        if (isSupportedByPf()) {
            return this.mCamSet.mIsFocusRingRotating;
        }
        return false;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ShootingModeController
    public List<String> getSupportedValue(String tag, int mode) {
        List<Integer> int_list;
        List<String> list = null;
        if (ITEM_ID_FOCUS_MAGNIFICATION.equals(tag)) {
            tag = TAG_MAGNIFICATION_RATIO;
        }
        if (!Environment.isMovieAPISupported()) {
            mode = 1;
        }
        if (!FocusModeController.isFocusShiftByControlWheel() && (int_list = getSupportedValueInt(tag, mode)) != null) {
            list = new ArrayList<>(int_list.size());
            Iterator<Integer> it = int_list.iterator();
            while (it.hasNext()) {
                list.add(it.next().toString());
            }
        }
        return list;
    }

    public List<Integer> getSupportedValueInt(String tag) {
        int currentMode = CameraSetting.getInstance().getCurrentMode();
        return getSupportedValueInt(tag, currentMode);
    }

    public List<Integer> getSupportedValueInt(String tag, int mode) {
        if (isSupportedByPf() && TAG_MAGNIFICATION_RATIO.equals(tag)) {
            if (!Environment.isMovieAPISupported()) {
                mode = 1;
            }
            List<Integer> supportedRatio = ((CameraEx.ParametersModifier) this.mCamSet.getSupportedParameters(mode).second).getSupportedPreviewMagnification();
            if (supportedRatio != null && supportedRatio.size() > 0) {
                if (supportedRatio.remove(ARBITARY_RATIO)) {
                    Log.d(TAG, "getSupportedValueInt 0 is removed");
                }
                Collections.sort(supportedRatio);
                return supportedRatio;
            }
        }
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        int currentMode = CameraSetting.getInstance().getCurrentMode();
        return getSupportedValue(tag, currentMode);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        boolean available = false;
        if (isSupportedByPf()) {
            if (isMovieMode()) {
                return false;
            }
            AvailableInfo.update();
            available = !AvailableInfo.isInhibition(INH_ID_FOCUS_MAGNIFICATION);
            if (ITEM_ID_FOCUS_MAGNIFICATION.equals(tag) && available && 15 > CameraSetting.getPfApiVersion()) {
                int sensorType = FocusAreaController.getInstance().getSensorType();
                String focusMode = FocusModeController.getInstance().getValue();
                available = sensorType != 2 || FocusModeController.MANUAL.equals(focusMode);
            }
        }
        return available;
    }

    public void startAtCoordinateOnEE(int ratio, int x, int y) {
        if (isSupportedByPf()) {
            Pair<Integer, Integer> position = convertCoordinateOnEEToApp(x, y);
            this.mCamSet.startFocusMagnification(ratio, convertToPfCoordinate(position));
        }
    }

    public void startAtCoordinateOnEE(int x, int y) {
        if (isSupportedByPf()) {
            Pair<Integer, Integer> position = convertCoordinateOnEEToApp(x, y);
            if (this.mCamSet.mFocusMagnificationRatio == 0) {
                this.mCamSet.mIsDigitalZoomAtMagnifyStarting = DigitalZoomController.getInstance().isDigitalZoomStatus();
            }
            int ratio = getNextRatio(this.mCamSet.mFocusMagnificationRatio);
            if (ratio != 0) {
                this.mCamSet.startFocusMagnification(ratio, convertToPfCoordinate(position));
            }
        }
    }

    public void startAtCoordinateOnEEDirectly(int x, int y) {
        if (isSupportedByPf()) {
            DisplayManager.VideoRect yuvArea = (DisplayManager.VideoRect) DisplayModeObserver.getInstance().getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
            int yuvWidth = yuvArea.pxRight - yuvArea.pxLeft;
            int yuvHeight = yuvArea.pxBottom - yuvArea.pxTop;
            int yuvCenterX = (yuvArea.pxLeft + yuvArea.pxRight) >> 1;
            int yuvCenterY = (yuvArea.pxTop + yuvArea.pxBottom) >> 1;
            int currentRatio = this.mCamSet.mFocusMagnificationActualRatio;
            if (currentRatio == 0) {
                currentRatio = 100;
            }
            float power = 100.0f / Math.max(currentRatio, DigitalZoomController.getInstance().getCurrentDigitalZoomMagnification());
            int scalarX = (int) ((((x - yuvCenterX) * power) * 2000.0f) / yuvWidth);
            int scalarY = (int) ((((y - yuvCenterY) * power) * 2000.0f) / yuvHeight);
            if (DisplayModeObserver.getInstance().isPanelReverse()) {
                scalarX = -scalarX;
            }
            if (this.mCamSet.mFocusMagnificationRatio == 0) {
                this.mCamSet.mIsDigitalZoomAtMagnifyStarting = DigitalZoomController.getInstance().isDigitalZoomStatus();
            }
            int nextRatio = getNextRatio(this.mCamSet.mFocusMagnificationRatio);
            if (nextRatio != 0) {
                this.mCamSet.startFocusMagnification(nextRatio, adjustMagnifyingPositionInBound(scalarX, scalarY));
            }
        }
    }

    public void start(int ratio) {
        start(ratio, null);
    }

    public void start(int ratio, Pair<Integer, Integer> position) {
        if (this.mCamSet.mFocusMagnificationRatio == 0) {
            if (ratio == 1) {
                this.mCamSet.mUseMagnifyRatio1_0 = true;
            } else {
                this.mCamSet.mUseMagnifyRatio1_0 = false;
            }
        }
        startEx(ratio, position);
    }

    protected void startEx(int ratio, Pair<Integer, Integer> position) {
        if (position == null) {
            if (isMagnifying()) {
                position = this.mCamSet.mMagnifyingPosition;
            } else {
                int sensorType = FocusAreaController.getInstance().getSensorType();
                String focusMode = ((Camera.Parameters) this.mCamSet.getParameters().first).getFocusMode();
                if (this.mCamSet.mIsDigitalZoomAtMagnifyStarting) {
                    if (sensorType == 2 && FocusModeController.DMF.equals(focusMode)) {
                        position = this.mCamSet.mFocusedPositionOnRingRotated;
                    } else {
                        position = movePositionInDigitalZoom();
                    }
                } else if (sensorType == 2) {
                    if (FocusModeController.DMF.equals(focusMode)) {
                        position = this.mCamSet.mFocusedPositionOnRingRotated;
                    } else {
                        position = this.mCamSet.mMfLastMagnifyingPosition;
                    }
                } else if (sensorType == 1) {
                    position = this.mCamSet.mMfLastMagnifyingPosition;
                }
            }
            if (position == null) {
                position = new Pair<>(0, 0);
            }
        }
        this.mCamSet.startFocusMagnification(ratio, position);
    }

    public void stop() {
        if (isSupportedByPf()) {
            this.mCamSet.stopFocusMagnification();
        }
    }

    public boolean isStarting() {
        return this.mCamSet.mIsFocusMagnificationStarting;
    }

    public void scheduleTimeout() {
        int duration = getTimeoutDuration();
        if (duration != 0 && duration > 0) {
            Log.d(TAG, MSG_SCHEDULE_TIMER);
            this.mCamSet.scheduleMfAssistTimeout(duration);
        }
    }

    public void rescheduleTimeout() {
        cancelTimeout();
        scheduleTimeout();
    }

    public void cancelTimeout() {
        Log.d(TAG, MSG_CANCEL_TIMER);
        this.mCamSet.cancelMfAssistTimeout();
    }

    public void startFocusMagnifier() {
        if (this.mCamSet.mFocusMagnificationRatio == 0) {
            this.mCamSet.mIsDigitalZoomAtMagnifyStarting = DigitalZoomController.getInstance().isDigitalZoomStatus();
        }
        if (!this.mCamSet.mIsDigitalZoomAtMagnifyStarting) {
            this.mCamSet.mUseMagnifyRatio1_0 = true;
        } else {
            this.mCamSet.mUseMagnifyRatio1_0 = false;
        }
        int startRatio = getNextRatio(this.mCamSet.mFocusMagnificationRatio);
        if (startRatio != 0) {
            startEx(startRatio, null);
        }
    }

    public void startMfAssist() {
        if (this.mCamSet.mFocusMagnificationRatio == 0) {
            this.mCamSet.mIsDigitalZoomAtMagnifyStarting = DigitalZoomController.getInstance().isDigitalZoomStatus();
        }
        this.mCamSet.mUseMagnifyRatio1_0 = false;
        int startRatio = getNextRatio(this.mCamSet.mFocusMagnificationRatio);
        if (startRatio != 0) {
            startEx(startRatio, null);
        }
    }

    public boolean nextMagnify(boolean cyclicMagnify) {
        Integer nextRatio;
        Integer.valueOf(0);
        if (cyclicMagnify) {
            nextRatio = Integer.valueOf(getNextRatio(this.mCamSet.mFocusMagnificationRatio));
        } else {
            Integer.valueOf(0);
            Integer startRatio = Integer.valueOf(getNextRatio(0));
            nextRatio = Integer.valueOf(getNextRatio(this.mCamSet.mFocusMagnificationRatio));
            if (startRatio.equals(nextRatio)) {
                nextRatio = 0;
            }
        }
        if (nextRatio.intValue() == 0) {
            return false;
        }
        setValue(TAG_MAGNIFICATION_RATIO, nextRatio.toString());
        return true;
    }

    protected int getNextRatio(int startRatio) {
        int index;
        int ratio = 0;
        List<Integer> supported = getSupportedValueInt(TAG_MAGNIFICATION_RATIO);
        if (supported == null) {
            return 0;
        }
        if (startRatio == 0) {
            if (this.mCamSet.mUseMagnifyRatio1_0) {
                return 1;
            }
            if (!this.mCamSet.mIsDigitalZoomAtMagnifyStarting) {
                int ratio2 = supported.get(0).intValue();
                return ratio2;
            }
            int digitalZoomRatio = DigitalZoomController.getInstance().getCurrentDigitalZoomMagnification();
            Iterator i$ = supported.iterator();
            while (true) {
                if (!i$.hasNext()) {
                    break;
                }
                Integer v = i$.next();
                int actualRatio = this.mCamSet.getFocusMagnificationRatio(v.intValue());
                if (actualRatio > digitalZoomRatio) {
                    ratio = v.intValue();
                    break;
                }
            }
            if (ratio == 0) {
                int ratio3 = supported.get(supported.size() - 1).intValue();
                return ratio3;
            }
            return ratio;
        }
        int index2 = supported.indexOf(Integer.valueOf(startRatio));
        if (-1 == index2) {
            index = 0;
        } else {
            index = index2 + 1;
            if (supported.size() <= index) {
                index = 0;
            }
        }
        if (index == 0 && this.mCamSet.mUseMagnifyRatio1_0 && startRatio != 1) {
            return 1;
        }
        int ratio4 = supported.get(index).intValue();
        return ratio4;
    }

    protected Pair<Integer, Integer> movePositionInDigitalZoom() {
        if (!this.mCamSet.mIsDigitalZoomAtMagnifyStarting || this.mCamSet.mFocusMagnificationRatio != 0) {
            return null;
        }
        float currentPosiX = STConstants.INVALID_APERTURE_VALUE;
        float currentPosiY = STConstants.INVALID_APERTURE_VALUE;
        if (this.mCamSet.mMfLastMagnifyingPosition != null) {
            currentPosiX = ((Integer) this.mCamSet.mMfLastMagnifyingPosition.first).intValue();
            currentPosiY = ((Integer) this.mCamSet.mMfLastMagnifyingPosition.second).intValue();
        }
        if (this.mCamSet.mChangeZoomRatio) {
            this.mCamSet.mChangeZoomRatio = false;
            boolean movePositionCenter = false;
            int digitalZoomRatio = DigitalZoomController.getInstance().getCurrentDigitalZoomMagnification();
            float zoomRatio = digitalZoomRatio / 100.0f;
            float zoomW = 2000.0f / zoomRatio;
            float zoomH = 2000.0f / zoomRatio;
            float zoomX = (-zoomW) / 2.0f;
            float zoomY = (-zoomH) / 2.0f;
            if (zoomX > currentPosiX || currentPosiX > zoomX + zoomW) {
                movePositionCenter = true;
            }
            if (zoomY > currentPosiY || currentPosiY > zoomY + zoomH) {
                movePositionCenter = true;
            }
            if (movePositionCenter) {
                currentPosiX = STConstants.INVALID_APERTURE_VALUE;
                currentPosiY = STConstants.INVALID_APERTURE_VALUE;
            }
        }
        Pair<Integer, Integer> position = new Pair<>(Integer.valueOf(Math.round(currentPosiX)), Integer.valueOf(Math.round(currentPosiY)));
        return position;
    }

    public boolean isAvailableInDigitalZoom() {
        return this.mIsAvailableInDigitalZoom;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isAvailableInDigitalZoomInner() {
        if (!isSupportedByPf()) {
            return false;
        }
        if (!DigitalZoomController.getInstance().isDigitalZoomStatus()) {
            return true;
        }
        List<Integer> supported = getSupportedValueInt(TAG_MAGNIFICATION_RATIO);
        if (supported == null || supported.size() <= 0) {
            return false;
        }
        int digitalZoomRatio = DigitalZoomController.getInstance().getCurrentDigitalZoomMagnification();
        int maxFocusMagnifierActualRatio = this.mCamSet.getFocusMagnificationRatio(supported.get(supported.size() - 1).intValue());
        if (digitalZoomRatio > maxFocusMagnifierActualRatio) {
            return false;
        }
        return true;
    }

    public Pair<Integer, Integer> getFocusDriveRange() {
        Pair<Integer, Integer> range = new Pair<>(Integer.valueOf(this.mCamSet.getFocusMaxPosition()), 0);
        return range;
    }

    public int getFocusDriveCurrentPosition() {
        return this.mCamSet.getFocusCurrentPosition();
    }

    public void moveFocusDriveNear() {
        this.mCamSet.moveFocusDrive(CameraSetting.FOCUS_DRIVE_DIRECTION_NEAR);
        Pair<Integer, Integer> range = new Pair<>(Integer.valueOf(this.mCamSet.getFocusMaxPosition()), 0);
        if (((Integer) range.second).intValue() >= this.mCamSet.getFocusCurrentPosition()) {
            CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.FOCUS_POSITION_CHANGED);
        }
    }

    public void moveFocusDriveFar() {
        this.mCamSet.moveFocusDrive(CameraSetting.FOCUS_DRIVE_DIRECTION_FAR);
        Pair<Integer, Integer> range = new Pair<>(Integer.valueOf(this.mCamSet.getFocusMaxPosition()), 0);
        if (((Integer) range.first).intValue() <= this.mCamSet.getFocusCurrentPosition()) {
            CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.FOCUS_POSITION_CHANGED);
        }
    }

    public boolean isAvailableMagnificationPointMove() {
        return this.mCamSet.isAvailableMagnificationPointMove();
    }

    public boolean setMagnifyingPositionDirectly(int x, int y) {
        DisplayManager.VideoRect yuvArea = (DisplayManager.VideoRect) DisplayModeObserver.getInstance().getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
        int yuvWidth = yuvArea.pxRight - yuvArea.pxLeft;
        int yuvHeight = yuvArea.pxBottom - yuvArea.pxTop;
        int yuvCenterX = (yuvArea.pxLeft + yuvArea.pxRight) >> 1;
        int yuvCenterY = (yuvArea.pxTop + yuvArea.pxBottom) >> 1;
        int scalarX = (int) (((x - yuvCenterX) * 2000.0f) / yuvWidth);
        int scalarY = (int) (((y - yuvCenterY) * 2000.0f) / yuvHeight);
        if (DisplayModeObserver.getInstance().isPanelReverse()) {
            scalarX = -scalarX;
        }
        return setMagnifyingPositionDirectlyInner(scalarX, scalarY);
    }

    public boolean moveMagnifyingPositionDirectly(int dx, int dy) {
        DisplayManager.VideoRect yuvArea = (DisplayManager.VideoRect) DisplayModeObserver.getInstance().getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
        int yuvWidth = yuvArea.pxRight - yuvArea.pxLeft;
        int yuvHeight = yuvArea.pxBottom - yuvArea.pxTop;
        int ratio = this.mCamSet.mFocusMagnificationActualRatio;
        if (ratio == 0) {
            ratio = 100;
        }
        float power = 100.0f / Math.max(ratio, DigitalZoomController.getInstance().getCurrentDigitalZoomMagnification());
        int scalarX = (int) (((dx * power) * 2000.0f) / yuvWidth);
        int scalarY = (int) (((dy * power) * 2000.0f) / yuvHeight);
        if (DisplayModeObserver.getInstance().isPanelReverse()) {
            scalarX = -scalarX;
        }
        int baseX = 0;
        int baseY = 0;
        if (this.mCamSet.mMagnifyingPosition != null) {
            baseX = ((Integer) this.mCamSet.mMagnifyingPosition.first).intValue();
            baseY = ((Integer) this.mCamSet.mMagnifyingPosition.second).intValue();
        }
        return setMagnifyingPositionDirectlyInner(baseX + scalarX, baseY + scalarY);
    }

    private boolean setMagnifyingPositionDirectlyInner(int scalarX, int scalarY) {
        if (!isSupportedByPf()) {
            return false;
        }
        if (this.mCamSet.mFocusMagnificationRatio == 0) {
            Log.w(TAG, MSG_SET_MAGNIFICATION_NOT_MAGNIFYING);
            return false;
        }
        Pair<Integer, Integer> newMagnifyingPosition = adjustMagnifyingPositionInBound(scalarX, scalarY);
        if (newMagnifyingPosition.equals(this.mCamSet.mMagnifyingPosition)) {
            return false;
        }
        this.mCamSet.startFocusMagnification(this.mCamSet.mFocusMagnificationRatio, newMagnifyingPosition);
        return true;
    }

    private Pair<Integer, Integer> adjustMagnifyingPositionInBound(int scalarX, int scalarY) {
        Pair<Float, Float> distance = getDistancePerUnitStep();
        int maxH = (int) (((Float) distance.first).floatValue() * getNumOfStepsH());
        int maxV = (int) (((Float) distance.second).floatValue() * getNumOfStepsV());
        return Pair.create(Integer.valueOf(Math.min(Math.max(scalarX, -maxH), maxH)), Integer.valueOf(Math.min(Math.max(scalarY, -maxV), maxV)));
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetInitParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        super.onGetInitParameters(params);
        CameraNotificationManager.getInstance().setNotificationListener(this.mListener);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetTermParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        super.onGetTermParameters(params);
        CameraNotificationManager.getInstance().removeNotificationListener(this.mListener);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onCameraSet() {
        super.onCameraSet();
        AvailableInfo.addInhFactorListener(INH_ID_FOCUS_MAGNIFICATION, this.mInhFactorListener);
        this.mIsAvailableInDigitalZoom = isAvailableInDigitalZoomInner();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onCameraRemoving() {
        super.onCameraRemoving();
        AvailableInfo.removeInhFactorListener(this.mInhFactorListener);
    }
}
