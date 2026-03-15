package com.sony.imaging.app.lightgraffiti.shooting;

import android.util.Log;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGSelfTimerController;
import com.sony.imaging.app.lightgraffiti.util.LGConstants;
import com.sony.imaging.app.lightgraffiti.util.LGPreviewEffect;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.hardware.DeviceBuffer;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class LGStateHolder {
    public static final String SHOOTING_STAGE = "About shooting stage";
    private PrepareCallback prepareCallback;
    private int shootingStage;
    public static final String SHOOTING_1ST = "1st shooting";
    public static final String SELFTIMER_COUNTING = "Selftimer counting";
    public static final String EXPOSING_1ST = "1st exposure";
    public static final String PROCESSING = "Processing...";
    public static final String SHOOTING_2ND = "2nd shooting";
    public static final String EXPOSING_2ND = "2nd exposure";
    public static final String CONFIRM_REVIEW = "Confirm/Review";
    public static final String SHOOTING_3RD_BEFORE_SHOOT = "3rd shooting before shoot";
    public static final String EXPOSING_3RD = "3rd exposure";
    public static final String SHOOTING_3RD_AFTER_SHOOT = "3rd shooting after shoot";
    private static final String[] SHOOTING_STAGE_ARRAY = {SHOOTING_1ST, SELFTIMER_COUNTING, EXPOSING_1ST, PROCESSING, SHOOTING_2ND, SELFTIMER_COUNTING, EXPOSING_2ND, PROCESSING, CONFIRM_REVIEW, SHOOTING_3RD_BEFORE_SHOOT, SELFTIMER_COUNTING, EXPOSING_3RD, PROCESSING, SHOOTING_3RD_AFTER_SHOOT, SELFTIMER_COUNTING, EXPOSING_3RD, PROCESSING};
    private static LGStateHolder instance = null;
    private static String TAG = LGStateHolder.class.getSimpleName();
    private static boolean isShootingEnable = false;
    private boolean isLensProblem = false;
    private int stepsForward = 0;
    private Boolean previousExec = false;
    private Boolean isProcessInProcessing = false;
    private Boolean isRemoteShutter = false;
    private Boolean isStartFocusing = false;
    private OptimizedImage yc_1 = null;
    private OptimizedImage yc_2 = null;
    private OptimizedImage yc_3 = null;
    private DeviceBuffer db_2 = null;
    private DeviceBuffer db_mini = null;
    private DeviceBuffer db_review = null;
    private HashMap<String, ValueChangedListener> mValueChangedListeners = new HashMap<>();

    /* loaded from: classes.dex */
    public interface PrepareCallback {
        void onPrepare();
    }

    /* loaded from: classes.dex */
    public interface ValueChangedListener {
        void onValueChanged(String str);
    }

    public void setLensProblemFlag(boolean lens) {
        this.isLensProblem = lens;
    }

    public boolean isLendsProblem() {
        return this.isLensProblem;
    }

    public void setPrepareCallback(PrepareCallback c) {
        this.prepareCallback = c;
    }

    public String getShootingStage() {
        Log.d(TAG, "Get shooting stage : " + SHOOTING_STAGE_ARRAY[this.shootingStage]);
        return SHOOTING_STAGE_ARRAY[this.shootingStage];
    }

    private void setShootingStage(String shootingStage) {
        for (int i = 0; i < SHOOTING_STAGE_ARRAY.length; i++) {
            if (SHOOTING_STAGE_ARRAY[i].equals(shootingStage)) {
                this.shootingStage = i;
            }
        }
        Log.d(TAG, "Set shooting stage " + getShootingStage() + " as next.");
        synchronized (this.mValueChangedListeners) {
            for (Map.Entry<String, ValueChangedListener> e : this.mValueChangedListeners.entrySet()) {
                Log.d(TAG, "onValueChanged() called for " + e.getKey());
                e.getValue().onValueChanged(SHOOTING_STAGE);
            }
        }
    }

    public Boolean isShootingStage() {
        if (!getShootingStage().equals(SHOOTING_1ST) && !getShootingStage().equals(SHOOTING_2ND) && !isShootingStage3rd()) {
            return false;
        }
        return true;
    }

    public Boolean isExposingStage() {
        if (!getShootingStage().equals(EXPOSING_1ST) && !getShootingStage().equals(EXPOSING_2ND) && !getShootingStage().equals(EXPOSING_3RD)) {
            return false;
        }
        return true;
    }

    public void setShootingStageNext() {
        Log.d(TAG, "setShootingStageNext()");
        Log.d(TAG, "Present shooting stage is " + getShootingStage());
        try {
            if (isShootingStage().booleanValue() && LGSelfTimerController.getInstance().getValue(LGConstants.BACKUP_KEY_LAST_SELECTED_SELF_TIMER).equals(LGSelfTimerController.SELF_TIMER_OFF)) {
                this.stepsForward = 2;
                this.shootingStage += this.stepsForward;
            } else if (this.shootingStage == SHOOTING_STAGE_ARRAY.length - 1) {
                for (int i = 0; i < SHOOTING_STAGE_ARRAY.length; i++) {
                    if (SHOOTING_STAGE_ARRAY[i].equals(SHOOTING_3RD_AFTER_SHOOT)) {
                        this.shootingStage = i;
                    }
                }
            } else if (!this.isProcessInProcessing.booleanValue() && isExposingStage().booleanValue()) {
                this.stepsForward = 2;
                this.shootingStage += this.stepsForward;
                if (this.shootingStage == SHOOTING_STAGE_ARRAY.length) {
                    for (int i2 = 0; i2 < SHOOTING_STAGE_ARRAY.length; i2++) {
                        if (SHOOTING_STAGE_ARRAY[i2].equals(SHOOTING_3RD_AFTER_SHOOT)) {
                            this.shootingStage = i2;
                        }
                    }
                }
            } else {
                this.stepsForward = 1;
                this.shootingStage++;
            }
        } catch (IController.NotSupportedException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Set shooting stage " + getShootingStage() + " as next.");
        synchronized (this.mValueChangedListeners) {
            for (Map.Entry<String, ValueChangedListener> e2 : this.mValueChangedListeners.entrySet()) {
                Log.d(TAG, "onValueChanged() called for " + e2.getKey());
                e2.getValue().onValueChanged(SHOOTING_STAGE);
            }
        }
        this.previousExec = false;
    }

    public String getShootingStageNext() {
        Log.d(TAG, "getShootingStageNext()");
        Log.d(TAG, "Present shooting stage is " + getShootingStage());
        int shootingStageTemp = this.shootingStage;
        try {
            if (isShootingStage().booleanValue() && LGSelfTimerController.getInstance().getValue(LGConstants.BACKUP_KEY_LAST_SELECTED_SELF_TIMER).equals(LGSelfTimerController.SELF_TIMER_OFF)) {
                this.stepsForward = 2;
                shootingStageTemp += this.stepsForward;
            } else if (shootingStageTemp == SHOOTING_STAGE_ARRAY.length - 1) {
                for (int i = 0; i < SHOOTING_STAGE_ARRAY.length; i++) {
                    if (SHOOTING_STAGE_ARRAY[i].equals(SHOOTING_3RD_AFTER_SHOOT)) {
                        shootingStageTemp = i;
                    }
                }
            } else if (!this.isProcessInProcessing.booleanValue() && isExposingStage().booleanValue()) {
                this.stepsForward = 2;
                shootingStageTemp += this.stepsForward;
                if (shootingStageTemp == SHOOTING_STAGE_ARRAY.length) {
                    for (int i2 = 0; i2 < SHOOTING_STAGE_ARRAY.length; i2++) {
                        if (SHOOTING_STAGE_ARRAY[i2].equals(SHOOTING_3RD_AFTER_SHOOT)) {
                            shootingStageTemp = i2;
                        }
                    }
                }
            } else {
                this.stepsForward = 1;
                shootingStageTemp++;
            }
        } catch (IController.NotSupportedException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Get shooting stage next is " + SHOOTING_STAGE_ARRAY[shootingStageTemp] + ".");
        return SHOOTING_STAGE_ARRAY[shootingStageTemp];
    }

    public void setShootingStagePrevious() {
        Log.d(TAG, "setShootingStagePrevious()");
        if (!this.previousExec.booleanValue()) {
            if (getShootingStage().equals(SELFTIMER_COUNTING)) {
                this.shootingStage -= this.stepsForward;
                synchronized (this.mValueChangedListeners) {
                    for (Map.Entry<String, ValueChangedListener> e : this.mValueChangedListeners.entrySet()) {
                        Log.d(TAG, "onValueChanged() called for " + e.getKey());
                        e.getValue().onValueChanged(SHOOTING_STAGE);
                    }
                }
            } else if (getShootingStage().equals(CONFIRM_REVIEW)) {
                setShootingStage(SHOOTING_2ND);
            } else {
                Log.d(TAG, "Cannot oparate previous in this stage!");
            }
        } else {
            Log.d(TAG, "Cannot be done previous operation in a row.");
        }
        this.previousExec = true;
    }

    public boolean isShootingStage3rd() {
        if (!SHOOTING_3RD_BEFORE_SHOOT.equals(SHOOTING_STAGE_ARRAY[this.shootingStage]) && !SHOOTING_3RD_AFTER_SHOOT.equals(SHOOTING_STAGE_ARRAY[this.shootingStage])) {
            return false;
        }
        return true;
    }

    public boolean isShootingEnable() {
        return isShootingEnable;
    }

    public void setShootingEnable(boolean isEnable) {
        isShootingEnable = isEnable;
    }

    public void setValueChangedListener(ValueChangedListener listener) {
        Log.d(TAG, "Set " + listener.getClass().getName() + " as ValueChangedListener");
        synchronized (this.mValueChangedListeners) {
            this.mValueChangedListeners.put(listener.getClass().getName(), listener);
        }
    }

    public void removeValueChangedListener(ValueChangedListener listener) {
        Log.d(TAG, "Remove " + listener.getClass().getName() + " as ValueChangedListener");
        synchronized (this.mValueChangedListeners) {
            this.mValueChangedListeners.remove(listener.getClass().getName());
        }
    }

    public OptimizedImage getYc_1() {
        return this.yc_1;
    }

    public void setYc_1(OptimizedImage yc_1) {
        this.yc_1 = yc_1;
    }

    public OptimizedImage getYc_2() {
        return this.yc_2;
    }

    public void setYc_2(OptimizedImage yc_2) {
        this.yc_2 = yc_2;
    }

    public OptimizedImage getYc_3() {
        return this.yc_3;
    }

    public void setYc_3(OptimizedImage yc_3) {
        this.yc_3 = yc_3;
    }

    public DeviceBuffer getDb_2() {
        return this.db_2;
    }

    public void setDb_2(DeviceBuffer db_2) {
        this.db_2 = db_2;
    }

    public DeviceBuffer getDb_mini() {
        return this.db_mini;
    }

    public void setDb_mini(DeviceBuffer db_mini) {
        this.db_mini = db_mini;
    }

    public DeviceBuffer getDb_review() {
        return this.db_review;
    }

    public void setDb_review(DeviceBuffer db_review) {
        this.db_review = db_review;
    }

    public Boolean getIsProcessInProcessing() {
        return this.isProcessInProcessing;
    }

    public void setIsProcessInProcessing(Boolean isProcessInProcessing) {
        this.isProcessInProcessing = isProcessInProcessing;
    }

    public Boolean getIsRemoteShutter() {
        return this.isRemoteShutter;
    }

    public void setIsRemoteShutter(Boolean isRemoteShutter) {
        this.isRemoteShutter = isRemoteShutter;
    }

    public Boolean getIsStartFocusing() {
        return this.isStartFocusing;
    }

    public void setIsStartFocusing(Boolean isStartFocusing) {
        this.isStartFocusing = isStartFocusing;
    }

    private LGStateHolder() {
        this.shootingStage = 0;
        Log.d(TAG, "LGStateHolder initialized. Shooting stage is " + getShootingStage());
        this.shootingStage = 0;
    }

    public static LGStateHolder getInstance() {
        if (instance == null) {
            instance = new LGStateHolder();
        }
        return instance;
    }

    public void prepareShootingStage2nd() {
        if (this.yc_2 != null) {
            this.yc_2.release();
            this.yc_2 = null;
        }
        if (this.db_mini != null) {
            Log.d(TAG, "db_mini release.");
            this.db_mini.release();
            if (this.db_mini.isValid()) {
                Log.d(TAG, "!! CAUTION !! db_mini is NOT released succesfully!");
            }
            this.db_mini = null;
        }
        setShootingStage(SHOOTING_2ND);
    }

    public void prepareShootingStage1st() {
        CameraNotificationManager.getInstance().requestNotify(LGConstants.SHOOTING_1ST_OPEN);
        if (this.yc_1 != null) {
            this.yc_1.release();
            this.yc_1 = null;
        }
        if (this.yc_2 != null) {
            this.yc_2.release();
            this.yc_2 = null;
        }
        if (this.yc_3 != null) {
            this.yc_3.release();
            this.yc_3 = null;
        }
        if (this.db_2 != null) {
            this.db_2.release();
            this.db_2 = null;
        }
        if (this.db_mini != null) {
            this.db_mini.release();
            this.db_mini = null;
        }
        if (this.db_review != null) {
            this.db_review.release();
            this.db_review = null;
        }
        LGPreviewEffect.getInstance().stopPreviewEffect();
        setShootingStage(SHOOTING_1ST);
        this.prepareCallback.onPrepare();
    }

    public void endShootingStage() {
        LGPreviewEffect.getInstance().stopPreviewEffect();
        if (this.yc_1 != null) {
            this.yc_1.release();
            this.yc_1 = null;
        }
        if (this.yc_2 != null) {
            this.yc_2.release();
            this.yc_2 = null;
        }
        if (this.yc_3 != null) {
            this.yc_3.release();
            this.yc_3 = null;
        }
        if (this.db_2 != null) {
            this.db_2.release();
            this.db_2 = null;
        }
        if (this.db_mini != null) {
            this.db_mini.release();
            this.db_mini = null;
        }
        if (this.db_review != null) {
            this.db_review.release();
            this.db_review = null;
        }
        setShootingStage(SHOOTING_1ST);
    }

    public void prepareBackToApptop() {
        if (this.yc_1 != null) {
            this.yc_1.release();
            this.yc_1 = null;
        }
        if (this.yc_2 != null) {
            this.yc_2.release();
            this.yc_2 = null;
        }
        if (this.yc_3 != null) {
            this.yc_3.release();
            this.yc_3 = null;
        }
        if (this.db_2 != null) {
            this.db_2.release();
            this.db_2 = null;
        }
        if (this.db_mini != null) {
            this.db_mini.release();
            this.db_mini = null;
        }
        if (this.db_review != null) {
            this.db_review.release();
            this.db_review = null;
        }
        setShootingStage(SHOOTING_1ST);
    }

    public void release() {
        Log.d(TAG, "LGStateHolder release process. OptimizedImazes and ... released.");
        if (this.yc_1 != null) {
            this.yc_1.release();
            this.yc_1 = null;
        }
        if (this.yc_2 != null) {
            this.yc_2.release();
            this.yc_2 = null;
        }
        if (this.yc_3 != null) {
            this.yc_3.release();
            this.yc_3 = null;
        }
        if (this.db_2 != null) {
            this.db_2.release();
            this.db_2 = null;
        }
        if (this.db_mini != null) {
            this.db_mini.release();
            this.db_mini = null;
        }
        if (this.db_review != null) {
            this.db_review.release();
            this.db_review = null;
        }
        this.mValueChangedListeners.clear();
    }

    public void memStat() {
        if (this.yc_1 != null) {
            Log.d(TAG, "YC1 is used.");
        }
        if (this.yc_2 != null) {
            Log.d(TAG, "YC2 is used.");
        }
        if (this.yc_3 != null) {
            Log.d(TAG, "YC3 is used.");
        }
        if (this.db_2 != null) {
            Log.d(TAG, "DB2 is used.");
        }
        if (this.db_mini != null) {
            Log.d(TAG, "DB mini is used.");
        }
        if (this.db_review != null) {
            Log.d(TAG, "DB review is used.");
        }
    }
}
