package com.sony.imaging.app.base.shooting.camera;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.BaseBackUpKey;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class DriveModeController extends ShootingModeController {
    public static final String BRACKET = "bracket";
    public static final String BURST = "burst";
    public static final String BURST_SPEED_HIGH = "high";
    public static final String BURST_SPEED_LOW = "low";
    public static final String BURST_SPEED_MID = "middle";
    static final int DEFALUT_I_VALUE = -1;
    static final String DEFALUT_S_VALUE = "";
    public static final String DRIVEMODE = "drivemode";
    public static final String FPS = "fps";
    private static final String LOG_STR_ERR_INVALID_TAG = ": Invalid tag. ";
    private static final String LOG_STR_PF_ERR1 = "drive mode is null in camera...";
    public static final String MOTION_SHOT = "motionshot";
    public static final String REMOTE = "remote";
    public static final String REMOTE_OFF = "remote-off";
    public static final String REMOTE_ON = "remote-on";
    public static final String SELF = "self";
    public static final String SELF_TIMER = "selftimer";
    public static final String SELF_TIMER_10S = "selftimer-10s";
    public static final String SELF_TIMER_2S = "selftimer-2s";
    public static final String SELF_TIMER_BURST = "selftimer-burst";
    public static final String SELF_TIMER_BURST_10S_3SHOT = "selftimer-burst-10s-3shot";
    public static final String SELF_TIMER_BURST_10S_5SHOT = "selftimer-burst-10s-5shot";
    public static final String SELF_TIMER_OFF = "selftimer-0s";
    private static final String SET_BRACKET_MODE_FUNCTION = "setBracketMode";
    private static final String SET_BURST_FUNCTION = "setBurstDriveSpeed";
    private static final String SET_DRIVE_MODE_FUNCTION = "setDriveMode";
    private static final String SET_EXPOSURE_BRACKET_MODE_FUNCTION = "setExposureBracketMode";
    private static final String SET_EXPOSURE_BRACKET_PERIOD_FUNCTION = "setExposureBracketPeriod";
    private static final String SET_MOTION_SHOT_FUNCTION = "setMotionShotMode";
    private static final String SET_REMOTE_FUNCTION = "setRemoteControlMode";
    private static final String SET_SELFTIMER_FUNCTION = "setSelfTimer";
    public static final String SINGLE = "single";
    public static final String SPEED_PRIORITY_BURST = "speed-prior-burst";
    protected static final int S_10S = 10;
    protected static final int S_2S = 2;
    protected static final int S_3SHOT = 3;
    protected static final int S_5SHOT = 5;
    protected static final int S_OFF = 0;
    private static final String TAG = "DriveModeController";
    public static final int TEMP_SELFTIMER_OFF = 0;
    public static final int TEMP_SELFTIMER_ON = 1;
    private static DriveModeController mInstance;
    private static final String myName = DriveModeController.class.getSimpleName();
    protected int mPreviousSelfTimer = -1;
    private CameraSetting mCamSet = CameraSetting.getInstance();
    private BackUpUtil mBackupUtil = BackUpUtil.getInstance();

    public static final String getName() {
        return myName;
    }

    public static DriveModeController getInstance() {
        if (mInstance == null) {
            new DriveModeController();
        }
        return mInstance;
    }

    private static void setController(DriveModeController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected DriveModeController() {
        setController(this);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> emptyParam = this.mCamSet.getEmptyParameters();
        CameraEx.ParametersModifier params = (CameraEx.ParametersModifier) emptyParam.second;
        if (tag.equals(DRIVEMODE)) {
            setDriveModeWithOption(value);
            value = getDriveModeForMenu(value);
            if (!Environment.isNewBizDeviceActionCam()) {
                params.setSelfTimer(0);
            }
            setMotionShotMode(params, false);
            if (value.equals("selftimer")) {
                params.setDriveMode(SINGLE);
                params.setSelfTimer(this.mBackupUtil.getPreferenceInt(BaseBackUpKey.ID_DRIVEMODE_SELFTIMER_TIME, 10));
            } else if (value.equals(SELF_TIMER_BURST) && isAvailable(SELF_TIMER_BURST)) {
                params.setDriveMode(BURST);
                params.setSelfTimer(10);
                params.setNumOfBurstPicture(this.mBackupUtil.getPreferenceInt(BaseBackUpKey.ID_DRIVEMODE_SELFTIMER_BURST_SHOT_NUM, 3));
            } else if (value.equals(SPEED_PRIORITY_BURST)) {
                params.setDriveMode(SPEED_PRIORITY_BURST);
            } else if (value.equals(BURST)) {
                params.setDriveMode(BURST);
            } else if (MOTION_SHOT.equals(value)) {
                setMotionShotMode(params, true);
            } else {
                params.setDriveMode(SINGLE);
            }
        } else if (tag.equals("selftimer")) {
            params.setDriveMode(SINGLE);
            if (value.equals(SELF_TIMER_2S)) {
                params.setSelfTimer(2);
                this.mBackupUtil.setPreference(BaseBackUpKey.ID_DRIVEMODE_SELFTIMER_TIME, 2);
            } else if (value.equals(SELF_TIMER_10S)) {
                params.setSelfTimer(10);
                this.mBackupUtil.setPreference(BaseBackUpKey.ID_DRIVEMODE_SELFTIMER_TIME, 10);
            }
        } else if (SELF.equals(tag)) {
            if (SELF_TIMER_2S.equals(value)) {
                params.setSelfTimer(2);
            } else if (SELF_TIMER_10S.equals(value)) {
                params.setSelfTimer(10);
            } else if (SELF_TIMER_OFF.equals(value)) {
                params.setSelfTimer(0);
            }
        } else if (tag.equals(SELF_TIMER_BURST) && isAvailable(SELF_TIMER_BURST)) {
            params.setDriveMode(BURST);
            if (value.equals(SELF_TIMER_BURST_10S_3SHOT)) {
                params.setSelfTimer(10);
                params.setNumOfBurstPicture(3);
                this.mBackupUtil.setPreference(BaseBackUpKey.ID_DRIVEMODE_SELFTIMER_BURST_SHOT_NUM, 3);
            } else if (value.equals(SELF_TIMER_BURST_10S_5SHOT)) {
                params.setSelfTimer(10);
                params.setNumOfBurstPicture(5);
                this.mBackupUtil.setPreference(BaseBackUpKey.ID_DRIVEMODE_SELFTIMER_BURST_SHOT_NUM, 5);
            }
        } else if (tag.equals(BURST)) {
            params.setDriveMode(BURST);
            if (value.equals(BURST_SPEED_HIGH)) {
                params.setBurstDriveSpeed(BURST_SPEED_HIGH);
                this.mBackupUtil.setPreference(BaseBackUpKey.ID_DRIVE_MODE_BURST_SPEED, BURST_SPEED_HIGH);
            } else if (value.equals(BURST_SPEED_MID)) {
                params.setBurstDriveSpeed(BURST_SPEED_MID);
                this.mBackupUtil.setPreference(BaseBackUpKey.ID_DRIVE_MODE_BURST_SPEED, BURST_SPEED_MID);
            } else if (value.equals("low")) {
                params.setBurstDriveSpeed("low");
                this.mBackupUtil.setPreference(BaseBackUpKey.ID_DRIVE_MODE_BURST_SPEED, "low");
            }
        } else if (FPS.equals(tag)) {
            params.setDriveMode(BURST);
            if (value.equals(BURST_SPEED_HIGH)) {
                params.setBurstDriveSpeed(BURST_SPEED_HIGH);
            } else if (value.equals(BURST_SPEED_MID)) {
                params.setBurstDriveSpeed(BURST_SPEED_MID);
            } else if (value.equals("low")) {
                params.setBurstDriveSpeed("low");
            }
        } else if (REMOTE.equals(tag)) {
            if (REMOTE_ON.equals(value)) {
                params.setRemoteControlMode(true);
            } else if (REMOTE_OFF.equals(value)) {
                params.setRemoteControlMode(false);
            }
        }
        if (DRIVEMODE.equals(tag) || "selftimer".equals(tag) || SELF_TIMER_BURST.equals(tag) || BURST.equals(tag)) {
            params.setBurstDriveButtonReleaseBehave(getReleaseButtonBehave(value));
        }
        this.mCamSet.setParameters(emptyParam);
        CameraNotificationManager.getInstance().requestNotify(CameraNotificationManager.DRIVE_MODE);
    }

    protected boolean isMotionShotMode() {
        if (!Environment.isNewBizDeviceActionCam() || 9 > CameraSetting.getPfApiVersion()) {
            return false;
        }
        boolean ret = ((CameraEx.ParametersModifier) this.mCamSet.getParameters().second).getMotionShotMode();
        return ret;
    }

    protected void setMotionShotMode(CameraEx.ParametersModifier params, boolean b) {
        if (isAvailable(MOTION_SHOT)) {
            params.setMotionShotMode(b);
        }
    }

    public String getValue() {
        String value = null;
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getParameters();
        CameraEx.ParametersModifier params = (CameraEx.ParametersModifier) p.second;
        String drive = params.getDriveMode();
        int time = params.getSelfTimer();
        if (drive == null) {
            Log.e(TAG, LOG_STR_PF_ERR1);
            return SINGLE;
        }
        if (isMovieMode()) {
            value = SINGLE;
        } else if (isMotionShotMode()) {
            value = MOTION_SHOT;
        } else if (drive.equals(SINGLE)) {
            if (Environment.isNewBizDeviceActionCam()) {
                value = SINGLE;
            } else if (time == 0) {
                value = SINGLE;
            } else if (time == 2) {
                value = SELF_TIMER_2S;
            } else if (time == 10) {
                value = SELF_TIMER_10S;
            }
        } else if (drive.equals(BURST)) {
            if (Environment.isNewBizDeviceActionCam()) {
                value = BURST;
            } else {
                int burst_num = params.getNumOfBurstPicture();
                if (time == 0) {
                    value = BURST;
                    if (isSupported(BURST)) {
                        String speed = params.getBurstDriveSpeed();
                        if (isSupported(BURST_SPEED_HIGH) && speed.equals(BURST_SPEED_HIGH)) {
                            value = BURST_SPEED_HIGH;
                        } else if (isSupported(BURST_SPEED_MID) && speed.equals(BURST_SPEED_MID)) {
                            value = BURST_SPEED_MID;
                        } else if (isSupported("low") && speed.equals("low")) {
                            value = "low";
                        }
                    }
                } else if (time == 10) {
                    if (burst_num == 5) {
                        value = SELF_TIMER_BURST_10S_5SHOT;
                    } else if (burst_num == 3) {
                        value = SELF_TIMER_BURST_10S_3SHOT;
                    }
                }
            }
        } else if (BRACKET.equals(drive)) {
            value = BRACKET;
        } else if (SPEED_PRIORITY_BURST.equals(drive)) {
            value = SPEED_PRIORITY_BURST;
        }
        if (value == null) {
            Log.w(TAG, "Not Supproted in this Camera, yet");
            value = SINGLE;
        }
        return value;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        if (isMovieMode()) {
            return null;
        }
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getParameters();
        CameraEx.ParametersModifier params = (CameraEx.ParametersModifier) p.second;
        if (tag.equals(DRIVEMODE)) {
            String value = getDriveModeForMenu(getValue());
            return value;
        }
        if (tag.equals("selftimer")) {
            int time = this.mBackupUtil.getPreferenceInt(BaseBackUpKey.ID_DRIVEMODE_SELFTIMER_TIME, -1);
            if (time == 2) {
                return SELF_TIMER_2S;
            }
            if (time != 10) {
                return null;
            }
            return SELF_TIMER_10S;
        }
        if (tag.equals(SELF_TIMER_BURST)) {
            int shot = this.mBackupUtil.getPreferenceInt(BaseBackUpKey.ID_DRIVEMODE_SELFTIMER_BURST_SHOT_NUM, -1);
            if (shot == 3) {
                return SELF_TIMER_BURST_10S_3SHOT;
            }
            if (shot != 5) {
                return null;
            }
            return SELF_TIMER_BURST_10S_5SHOT;
        }
        if (tag.equals(BURST)) {
            String speed = this.mBackupUtil.getPreferenceString(BaseBackUpKey.ID_DRIVE_MODE_BURST_SPEED, "");
            return speed;
        }
        if (FPS.equals(tag)) {
            String value2 = params.getBurstDriveSpeed();
            return value2;
        }
        if (SELF.equals(tag)) {
            int time2 = params.getSelfTimer();
            if (time2 == 2) {
                return SELF_TIMER_2S;
            }
            if (time2 == 10) {
                return SELF_TIMER_10S;
            }
            if (time2 != 0) {
                return null;
            }
            return SELF_TIMER_OFF;
        }
        if (!REMOTE.equals(tag) || !Environment.isNewBizDeviceActionCam()) {
            return null;
        }
        if (isRemoteControl()) {
            return REMOTE_ON;
        }
        return REMOTE_OFF;
    }

    private void setDriveModeWithOption(String v) {
        if (v.equals(SELF_TIMER_2S)) {
            this.mBackupUtil.setPreference(BaseBackUpKey.ID_DRIVEMODE_SELFTIMER_TIME, 2);
        } else if (v.equals(SELF_TIMER_10S)) {
            this.mBackupUtil.setPreference(BaseBackUpKey.ID_DRIVEMODE_SELFTIMER_TIME, 10);
        }
        if (v.equals(BURST_SPEED_HIGH)) {
            this.mBackupUtil.setPreference(BaseBackUpKey.ID_DRIVE_MODE_BURST_SPEED, BURST_SPEED_HIGH);
        } else if (v.equals(BURST_SPEED_MID)) {
            this.mBackupUtil.setPreference(BaseBackUpKey.ID_DRIVE_MODE_BURST_SPEED, BURST_SPEED_MID);
        } else if (v.equals("low")) {
            this.mBackupUtil.setPreference(BaseBackUpKey.ID_DRIVE_MODE_BURST_SPEED, "low");
        }
        if (v.equals(SELF_TIMER_BURST_10S_3SHOT)) {
            this.mBackupUtil.setPreference(BaseBackUpKey.ID_DRIVEMODE_SELFTIMER_BURST_SHOT_NUM, 3);
        } else if (v.equals(SELF_TIMER_BURST_10S_5SHOT)) {
            this.mBackupUtil.setPreference(BaseBackUpKey.ID_DRIVEMODE_SELFTIMER_BURST_SHOT_NUM, 5);
        }
    }

    private String getDriveModeForMenu(String v) {
        if (v.equals(SELF_TIMER_2S) || v.equals(SELF_TIMER_10S)) {
            return "selftimer";
        }
        if (v.equals(BURST_SPEED_HIGH) || v.equals(BURST_SPEED_MID) || v.equals("low")) {
            return BURST;
        }
        if (!v.equals(SELF_TIMER_BURST_10S_3SHOT) && !v.equals(SELF_TIMER_BURST_10S_5SHOT)) {
            return v;
        }
        return SELF_TIMER_BURST;
    }

    public void setBackUpkey() {
        String preference_s;
        int preference_i;
        int preference_i2;
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getParameters();
        CameraEx.ParametersModifier params = (CameraEx.ParametersModifier) p.second;
        int time = this.mBackupUtil.getPreferenceInt(BaseBackUpKey.ID_DRIVEMODE_SELFTIMER_TIME, -1);
        if (time == -1) {
            int selftimer = params.getSelfTimer();
            if (2 == selftimer) {
                preference_i2 = 2;
            } else if (10 == selftimer) {
                preference_i2 = 10;
            } else if (isSupported(SELF_TIMER_10S)) {
                preference_i2 = 10;
            } else if (isSupported(SELF_TIMER_2S)) {
                preference_i2 = 2;
            } else {
                preference_i2 = 0;
                Log.w(TAG, "selftimer error");
            }
            this.mBackupUtil.setPreference(BaseBackUpKey.ID_DRIVEMODE_SELFTIMER_TIME, Integer.valueOf(preference_i2));
        }
        int shot = this.mBackupUtil.getPreferenceInt(BaseBackUpKey.ID_DRIVEMODE_SELFTIMER_BURST_SHOT_NUM, -1);
        if (shot == -1) {
            int numofburstpicture = params.getNumOfBurstPicture();
            if (5 == numofburstpicture) {
                preference_i = 5;
            } else if (3 == numofburstpicture) {
                preference_i = 3;
            } else if (isSupported(SELF_TIMER_BURST_10S_3SHOT)) {
                preference_i = 3;
            } else if (isSupported(SELF_TIMER_BURST_10S_5SHOT)) {
                preference_i = 5;
            } else {
                preference_i = 3;
                Log.w(TAG, "selftimer burst error");
            }
            this.mBackupUtil.setPreference(BaseBackUpKey.ID_DRIVEMODE_SELFTIMER_BURST_SHOT_NUM, Integer.valueOf(preference_i));
        }
        String speed = this.mBackupUtil.getPreferenceString(BaseBackUpKey.ID_DRIVE_MODE_BURST_SPEED, "");
        if (speed.equals("")) {
            speed = params.getBurstDriveSpeed();
        }
        if (isSupported(speed)) {
            preference_s = speed;
        } else {
            List<String> speeds = params.getSupportedBurstDriveSpeeds();
            if (speeds == null) {
                preference_s = "low";
            } else if (isSupported(BURST_SPEED_HIGH)) {
                preference_s = BURST_SPEED_HIGH;
            } else if (isSupported(BURST_SPEED_MID)) {
                preference_s = BURST_SPEED_MID;
            } else if (isSupported("low")) {
                preference_s = "low";
            } else {
                preference_s = BURST_SPEED_HIGH;
                Log.w(TAG, "burst error");
            }
        }
        this.mBackupUtil.setPreference(BaseBackUpKey.ID_DRIVE_MODE_BURST_SPEED, preference_s);
    }

    public List<String> getSupportedValue() {
        int mode = CameraSetting.getInstance().getCurrentMode();
        return getSupportedValue(DRIVEMODE, mode);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ShootingModeController
    public List<String> getSupportedValue(String tag, int mode) {
        if (!Environment.isMovieAPISupported()) {
            mode = 1;
        }
        List<String> list = new ArrayList<>();
        if (tag.equals(DRIVEMODE)) {
            if (isSupported(SINGLE, mode)) {
                list.add(SINGLE);
            }
            if (isSupported(BURST, mode)) {
                list.add(BURST);
            }
            if (isSupported("selftimer", mode)) {
                list.add("selftimer");
            }
            if (isSupported(SELF_TIMER_BURST, mode)) {
                list.add(SELF_TIMER_BURST);
            }
            if (isSupported(SPEED_PRIORITY_BURST, mode)) {
                list.add(SPEED_PRIORITY_BURST);
            }
            if (isSupported(MOTION_SHOT)) {
                list.add(MOTION_SHOT);
            }
        } else if (tag.equals("selftimer") && !Environment.isNewBizDeviceActionCam()) {
            if (isSupported(SELF_TIMER_10S, mode)) {
                list.add(SELF_TIMER_10S);
            }
            if (isSupported(SELF_TIMER_2S, mode)) {
                list.add(SELF_TIMER_2S);
            }
        } else if (SELF.equals(tag) && Environment.isNewBizDeviceActionCam()) {
            if (isSupported(SELF_TIMER_10S, mode)) {
                list.add(SELF_TIMER_10S);
            }
            if (isSupported(SELF_TIMER_2S, mode)) {
                list.add(SELF_TIMER_2S);
            }
            if (isSupported(SELF_TIMER_OFF, mode)) {
                list.add(SELF_TIMER_OFF);
            }
        } else if (tag.equals(SELF_TIMER_BURST) && !Environment.isNewBizDeviceActionCam()) {
            if (isSupported(SELF_TIMER_BURST_10S_3SHOT, mode)) {
                list.add(SELF_TIMER_BURST_10S_3SHOT);
            }
            if (isSupported(SELF_TIMER_BURST_10S_5SHOT, mode)) {
                list.add(SELF_TIMER_BURST_10S_5SHOT);
            }
        } else if (BURST.equals(tag) && !Environment.isNewBizDeviceActionCam()) {
            if (isSupported(BURST_SPEED_HIGH, mode)) {
                list.add(BURST_SPEED_HIGH);
            }
            if (isSupported(BURST_SPEED_MID, mode)) {
                list.add(BURST_SPEED_MID);
            }
            if (isSupported("low", mode)) {
                list.add("low");
            }
        } else if (FPS.equals(tag) && Environment.isNewBizDeviceActionCam()) {
            if (isSupported(BURST_SPEED_HIGH, mode)) {
                list.add(BURST_SPEED_HIGH);
            }
            if (isSupported(BURST_SPEED_MID, mode)) {
                list.add(BURST_SPEED_MID);
            }
            if (isSupported("low", mode)) {
                list.add("low");
            }
        } else if (REMOTE.equals(tag) && Environment.isNewBizDeviceActionCam() && isSupported(REMOTE)) {
            list.add(REMOTE_ON);
            list.add(REMOTE_OFF);
        }
        return list;
    }

    public List<String> getAvailableValue() {
        return getAvailableValue(CameraNotificationManager.DRIVE_MODE);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        int currentMode = CameraSetting.getInstance().getCurrentMode();
        List<String> list = new ArrayList<>();
        List<String> supported = getSupportedValue(tag, currentMode);
        for (String v : supported) {
            if (isAvailable(v)) {
                list.add(v);
            }
        }
        return list;
    }

    protected boolean isSupported(String value) {
        int mode = CameraSetting.getInstance().getCurrentMode();
        return isSupported(value, mode);
    }

    protected boolean isSupported(String value, int mode) {
        if (!Environment.isMovieAPISupported()) {
            mode = 1;
        }
        CameraEx.ParametersModifier params = (CameraEx.ParametersModifier) this.mCamSet.getSupportedParameters(mode).second;
        List<String> drivemodes = params.getSupportedDriveModes();
        if (drivemodes == null) {
            drivemodes = new ArrayList<>();
        }
        List<Integer> times = params.getSupportedSelfTimers();
        if (times == null) {
            times = new ArrayList<>();
        }
        int burst_num = params.getMaxNumOfBurstPicture();
        if (value.equals(SINGLE)) {
            boolean ret = drivemodes.contains(SINGLE);
            return ret;
        }
        if (value.equals(BURST)) {
            boolean ret2 = drivemodes.contains(BURST);
            return ret2;
        }
        if (SPEED_PRIORITY_BURST.equals(value) && !Environment.isNewBizDeviceActionCam()) {
            boolean ret3 = drivemodes.contains(SPEED_PRIORITY_BURST);
            return ret3;
        }
        if ("selftimer".equals(value) && !Environment.isNewBizDeviceActionCam()) {
            return drivemodes.contains(SINGLE) && times.contains(2) && times.contains(10);
        }
        if (SELF_TIMER_BURST.equals(value) && !Environment.isNewBizDeviceActionCam()) {
            return drivemodes.contains(BURST) && times.contains(2) && times.contains(10);
        }
        if (value.equals(SELF_TIMER_2S)) {
            boolean ret4 = times.contains(2);
            return ret4;
        }
        if (value.equals(SELF_TIMER_10S)) {
            boolean ret5 = times.contains(10);
            return ret5;
        }
        if (SELF_TIMER_OFF.equals(value) && Environment.isNewBizDeviceActionCam()) {
            boolean ret6 = times.contains(0);
            return ret6;
        }
        if (SELF_TIMER_BURST_10S_3SHOT.equals(value) && !Environment.isNewBizDeviceActionCam()) {
            return times.contains(10) && 3 <= burst_num;
        }
        if (SELF_TIMER_BURST_10S_5SHOT.equals(value) && !Environment.isNewBizDeviceActionCam()) {
            return times.contains(10) && 5 <= burst_num;
        }
        if (value.equals(BURST_SPEED_HIGH)) {
            List<String> speeds = params.getSupportedBurstDriveSpeeds();
            if (speeds == null || 1 >= speeds.size()) {
                return false;
            }
            boolean ret7 = speeds.contains(BURST_SPEED_HIGH);
            return ret7;
        }
        if (value.equals(BURST_SPEED_MID)) {
            List<String> speeds2 = params.getSupportedBurstDriveSpeeds();
            if (speeds2 == null || 1 >= speeds2.size()) {
                return false;
            }
            boolean ret8 = speeds2.contains(BURST_SPEED_MID);
            return ret8;
        }
        if (value.equals("low")) {
            List<String> speeds3 = params.getSupportedBurstDriveSpeeds();
            if (speeds3 == null || 1 >= speeds3.size()) {
                return false;
            }
            boolean ret9 = speeds3.contains("low");
            return ret9;
        }
        if (MOTION_SHOT.equals(value) && Environment.isNewBizDeviceActionCam() && 9 <= CameraSetting.getPfApiVersion()) {
            boolean ret10 = params.isSupportedMotionShotMode();
            return ret10;
        }
        if (REMOTE.equals(value) && Environment.isNewBizDeviceActionCam()) {
            boolean ret11 = params.isSupportedRemoteControlMode();
            return ret11;
        }
        if (REMOTE_ON.equals(value) && Environment.isNewBizDeviceActionCam()) {
            boolean ret12 = params.isSupportedRemoteControlMode();
            return ret12;
        }
        if (!REMOTE_OFF.equals(value) || !Environment.isNewBizDeviceActionCam()) {
            return false;
        }
        boolean ret13 = params.isSupportedRemoteControlMode();
        return ret13;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public boolean isUnavailableSceneFactor(String tag) {
        if (DRIVEMODE.equals(tag)) {
            return isUnavailableAPISceneFactor(SET_DRIVE_MODE_FUNCTION, null, SET_SELFTIMER_FUNCTION, null);
        }
        StringBuilder mStringBuilder = StringBuilderThreadLocal.getScratchBuilder();
        int len = mStringBuilder.length();
        mStringBuilder.replace(0, len, tag).append(LOG_STR_ERR_INVALID_TAG);
        Log.w(TAG, mStringBuilder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(mStringBuilder);
        return false;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String value) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getParameters();
        if (DRIVEMODE.equals(value) && 2 == CameraSetting.getInstance().getCurrentMode()) {
            return false;
        }
        if (value == null || value.equals(DRIVEMODE)) {
            if (Environment.isNewBizDeviceActionCam()) {
                boolean ret = AvailableInfo.isAvailable(SET_DRIVE_MODE_FUNCTION, null);
                return ret;
            }
            boolean ret2 = AvailableInfo.isAvailable(SET_DRIVE_MODE_FUNCTION, null, SET_SELFTIMER_FUNCTION, null, SET_REMOTE_FUNCTION, null);
            return ret2;
        }
        if (FPS.equals(value)) {
            if (!Environment.isNewBizDeviceActionCam() || SINGLE.equals(getValue(DRIVEMODE))) {
                return false;
            }
            boolean ret3 = AvailableInfo.isAvailable((Camera.Parameters) p.first, (CameraEx.ParametersModifier) p.second, SET_DRIVE_MODE_FUNCTION, BURST);
            return ret3;
        }
        if (SELF.equals(value)) {
            if (Environment.isNewBizDeviceActionCam()) {
                return AvailableInfo.isAvailable((Camera.Parameters) p.first, (CameraEx.ParametersModifier) p.second, SET_SELFTIMER_FUNCTION, 2) && AvailableInfo.isAvailable((Camera.Parameters) p.first, (CameraEx.ParametersModifier) p.second, SET_SELFTIMER_FUNCTION, 10) && AvailableInfo.isAvailable((Camera.Parameters) p.first, (CameraEx.ParametersModifier) p.second, SET_SELFTIMER_FUNCTION, 0);
            }
            return false;
        }
        if (!isSupported(value)) {
            return false;
        }
        if (value.equals(SINGLE)) {
            boolean ret4 = AvailableInfo.isAvailable((Camera.Parameters) p.first, (CameraEx.ParametersModifier) p.second, SET_DRIVE_MODE_FUNCTION, SINGLE, SET_SELFTIMER_FUNCTION, 0);
            return ret4;
        }
        if (value.equals(BURST)) {
            if (Environment.isNewBizDeviceActionCam()) {
                boolean ret5 = AvailableInfo.isAvailable((Camera.Parameters) p.first, (CameraEx.ParametersModifier) p.second, SET_DRIVE_MODE_FUNCTION, BURST);
                return ret5;
            }
            boolean ret6 = AvailableInfo.isAvailable((Camera.Parameters) p.first, (CameraEx.ParametersModifier) p.second, SET_DRIVE_MODE_FUNCTION, BURST, SET_SELFTIMER_FUNCTION, 0);
            return ret6;
        }
        if (value.equals(SPEED_PRIORITY_BURST)) {
            boolean ret7 = AvailableInfo.isAvailable((Camera.Parameters) p.first, (CameraEx.ParametersModifier) p.second, SET_DRIVE_MODE_FUNCTION, SPEED_PRIORITY_BURST, SET_SELFTIMER_FUNCTION, 0);
            return ret7;
        }
        if (value.equals("selftimer")) {
            return AvailableInfo.isAvailable((Camera.Parameters) p.first, (CameraEx.ParametersModifier) p.second, SET_DRIVE_MODE_FUNCTION, SINGLE, SET_SELFTIMER_FUNCTION, 2) && AvailableInfo.isAvailable((Camera.Parameters) p.first, (CameraEx.ParametersModifier) p.second, SET_DRIVE_MODE_FUNCTION, SINGLE, SET_SELFTIMER_FUNCTION, 10);
        }
        if (value.equals(SELF_TIMER_BURST)) {
            return AvailableInfo.isAvailable((Camera.Parameters) p.first, (CameraEx.ParametersModifier) p.second, SET_DRIVE_MODE_FUNCTION, BURST, SET_SELFTIMER_FUNCTION, 2) && AvailableInfo.isAvailable((Camera.Parameters) p.first, (CameraEx.ParametersModifier) p.second, SET_DRIVE_MODE_FUNCTION, BURST, SET_SELFTIMER_FUNCTION, 10);
        }
        if (value.equals(SELF_TIMER_2S)) {
            boolean ret8 = AvailableInfo.isAvailable((Camera.Parameters) p.first, (CameraEx.ParametersModifier) p.second, SET_SELFTIMER_FUNCTION, 2);
            return ret8;
        }
        if (value.equals(SELF_TIMER_10S)) {
            boolean ret9 = AvailableInfo.isAvailable((Camera.Parameters) p.first, (CameraEx.ParametersModifier) p.second, SET_SELFTIMER_FUNCTION, 10);
            return ret9;
        }
        if (SELF_TIMER_OFF.equals(value)) {
            boolean ret10 = AvailableInfo.isAvailable((Camera.Parameters) p.first, (CameraEx.ParametersModifier) p.second, SET_SELFTIMER_FUNCTION, 0);
            return ret10;
        }
        if (value.equals(SELF_TIMER_BURST_10S_3SHOT)) {
            boolean ret11 = AvailableInfo.isAvailable((Camera.Parameters) p.first, (CameraEx.ParametersModifier) p.second, SET_SELFTIMER_FUNCTION, 10);
            return ret11;
        }
        if (value.equals(SELF_TIMER_BURST_10S_5SHOT)) {
            boolean ret12 = AvailableInfo.isAvailable((Camera.Parameters) p.first, (CameraEx.ParametersModifier) p.second, SET_SELFTIMER_FUNCTION, 10);
            return ret12;
        }
        if (value.equals(BURST_SPEED_HIGH)) {
            boolean ret13 = AvailableInfo.isAvailable((Camera.Parameters) p.first, (CameraEx.ParametersModifier) p.second, SET_BURST_FUNCTION, BURST_SPEED_HIGH);
            return ret13;
        }
        if (value.equals(BURST_SPEED_MID)) {
            boolean ret14 = AvailableInfo.isAvailable((Camera.Parameters) p.first, (CameraEx.ParametersModifier) p.second, SET_BURST_FUNCTION, BURST_SPEED_MID);
            return ret14;
        }
        if (value.equals("low")) {
            boolean ret15 = AvailableInfo.isAvailable((Camera.Parameters) p.first, (CameraEx.ParametersModifier) p.second, SET_BURST_FUNCTION, "low");
            return ret15;
        }
        if (MOTION_SHOT.equals(value)) {
            boolean ret16 = AvailableInfo.isAvailable((Camera.Parameters) p.first, (CameraEx.ParametersModifier) p.second, SET_MOTION_SHOT_FUNCTION, null);
            return ret16;
        }
        if (REMOTE.equals(value)) {
            if (!Environment.isNewBizDeviceActionCam()) {
                return false;
            }
            boolean ret17 = AvailableInfo.isAvailable((Camera.Parameters) p.first, (CameraEx.ParametersModifier) p.second, SET_REMOTE_FUNCTION, null);
            return ret17;
        }
        if (REMOTE_ON.equals(value)) {
            if (!Environment.isNewBizDeviceActionCam()) {
                return false;
            }
            boolean ret18 = AvailableInfo.isAvailable((Camera.Parameters) p.first, (CameraEx.ParametersModifier) p.second, SET_REMOTE_FUNCTION, true);
            return ret18;
        }
        if (!REMOTE_OFF.equals(value) || !Environment.isNewBizDeviceActionCam()) {
            return false;
        }
        boolean ret19 = AvailableInfo.isAvailable((Camera.Parameters) p.first, (CameraEx.ParametersModifier) p.second, SET_REMOTE_FUNCTION, false);
        return ret19;
    }

    public boolean isSelfTimer() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getParameters();
        CameraEx.ParametersModifier params = (CameraEx.ParametersModifier) p.second;
        int time = params.getSelfTimer();
        return time == 10 || time == 2;
    }

    public boolean isRemoteControl() {
        BaseShootingExecutor se = ExecutorCreator.getInstance().getSequence();
        if (se != null && 1 == se.getTempRemoteControl()) {
            boolean isRemote = se.getOriginalRemoteControl();
            return isRemote;
        }
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = this.mCamSet.getParameters();
        CameraEx.ParametersModifier params = (CameraEx.ParametersModifier) p.second;
        boolean isRemote2 = params.getRemoteControlMode();
        return isRemote2;
    }

    public boolean isSupportedRemoteControl() {
        return ((CameraEx.ParametersModifier) CameraSetting.getInstance().getSupportedParameters().second).isSupportedRemoteControlMode();
    }

    public void setTempSelfTimer(int status) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> set = this.mCamSet.getEmptyParameters();
        Pair<Camera.Parameters, CameraEx.ParametersModifier> current = this.mCamSet.getParameters();
        CameraEx.ParametersModifier params = (CameraEx.ParametersModifier) current.second;
        if (status == 1) {
            this.mPreviousSelfTimer = params.getSelfTimer();
            if (10 != this.mPreviousSelfTimer) {
                ((CameraEx.ParametersModifier) set.second).setSelfTimer(2);
            }
        } else if (status == 0 && -1 != this.mPreviousSelfTimer) {
            ((CameraEx.ParametersModifier) set.second).setSelfTimer(this.mPreviousSelfTimer);
            this.mPreviousSelfTimer = -1;
        }
        CameraSetting.getInstance().setParameters(set);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetInitParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        Log.i(TAG, "onGetInitParameters");
        String value = null;
        if (1 == CameraSetting.getInstance().getCurrentMode() && getValue().equals(BRACKET)) {
            ((CameraEx.ParametersModifier) params.second).setSelfTimer(0);
            ((CameraEx.ParametersModifier) params.second).setDriveMode(SINGLE);
            value = SINGLE;
        }
        if (Environment.getVersionOfHW() < 2) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> current = this.mCamSet.getParameters();
            if (true == ((CameraEx.ParametersModifier) current.second).getRemoteControlMode()) {
                ((CameraEx.ParametersModifier) params.second).setSelfTimer(0);
                ((CameraEx.ParametersModifier) params.second).setDriveMode(SINGLE);
                ((CameraEx.ParametersModifier) params.second).setRemoteControlMode(false);
                value = SINGLE;
            }
        }
        if (1 == this.mCamSet.getCurrentMode()) {
            setBackUpkey();
            if (value == null) {
                value = getValue();
            }
            ((CameraEx.ParametersModifier) params.second).setBurstDriveButtonReleaseBehave(getReleaseButtonBehave(value));
        }
    }

    public boolean isBlackoutFreeShooting() {
        return isParameterBlackoutFreeShooting(this.mCamSet.getParameters());
    }

    public static boolean isParameterBlackoutFreeShooting(Pair<Camera.Parameters, CameraEx.ParametersModifier> p) {
        if (Environment.getVersionOfHW() == 1) {
            return false;
        }
        String drive = ((CameraEx.ParametersModifier) p.second).getDriveMode();
        if (BURST.equals(drive) || SPEED_PRIORITY_BURST.equals(drive)) {
            return true;
        }
        String mode = ((Camera.Parameters) p.first).getSceneMode();
        return ExposureModeController.CONT_PRIORITY_AE.equals(mode);
    }

    protected String getReleaseButtonBehave(String driveMode) {
        if (!SELF_TIMER_BURST.equals(driveMode) && !SELF_TIMER_BURST_10S_3SHOT.equals(driveMode) && !SELF_TIMER_BURST_10S_5SHOT.equals(driveMode) && ((!Environment.isNewBizDeviceActionCam() || !BURST.equals(driveMode)) && !MOTION_SHOT.equals(driveMode))) {
            return "stop";
        }
        return "continue";
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ShootingModeController, com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public int getCautionIndex(String itemId) {
        if (!isMovieMode()) {
            return 0;
        }
        return -1;
    }
}
