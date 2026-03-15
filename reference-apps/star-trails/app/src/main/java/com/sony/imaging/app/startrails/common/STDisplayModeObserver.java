package com.sony.imaging.app.startrails.common;

import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.imaging.app.util.GyroscopeObserver;
import com.sony.scalar.sysutil.ScalarProperties;
import com.sony.scalar.sysutil.didep.Settings;

/* loaded from: classes.dex */
public class STDisplayModeObserver extends DisplayModeObserver {
    public static final int DISPMODE_REC_STARTRAILS = 11;
    public static final int DISPMODE_REC_STARTRAILS_MUTE = 12;
    public static final String SKIP_CAPTURING_DISP_MODE_TAG = "skipCapturingDisplayMode";
    public static final String SKIP_DIGITAL_LEVEL_TAG = "skipDigitalLevel";
    private int[][] mActiveDisplayModeIndex;
    private int[][][] mDispModeList;
    private static String TAG = AppLog.getClassName();
    private static int[][] DISPMODE_ROUND_TABLE = {new int[]{6, 11, 1}, new int[]{0, 11, 1}, new int[]{2, 11, 1}, new int[]{7, 11, 1}, new int[]{8, 11, 3}};
    private static STDisplayModeObserver mTheInstance = new STDisplayModeObserver();
    private final int[][] DISPMODE_LIST_FOR_REC = {new int[]{11, 1, 3, 4, 5, 12}, new int[]{5, 1, 3, 4}, new int[]{11, 1, 3, 4, 5, 12}};
    private final int[][] DISPMODE_LIST_FOR_PLAY = {new int[]{1, 3}, new int[]{1, 3}, new int[]{1, 3}};
    private final int[][][] DISPMODE_LIST = {this.DISPMODE_LIST_FOR_REC, this.DISPMODE_LIST_FOR_PLAY};

    public static STDisplayModeObserver getInstance() {
        return mTheInstance;
    }

    protected STDisplayModeObserver() {
        if (GyroscopeObserver.getInstance().hasDigitalLevel()) {
            this.mDispModeList = this.DISPMODE_LIST;
        }
        initializeActiveDisplayModeIndex();
    }

    private void initializeActiveDisplayModeIndex() {
        AppLog.enter(TAG, AppLog.getMethodName());
        int dispModeEVF = Settings.getDispMode(1);
        int dispModePbS = Settings.getDispMode(2);
        int indexRecLCD = indexOf(this.mDispModeList[0][0], roundDisplayMode(0));
        int indexRecEVF = indexOf(this.mDispModeList[0][1], roundDisplayMode(dispModeEVF));
        int indexPbS = indexOf(this.mDispModeList[1][0], dispModePbS);
        if (-1 == indexPbS) {
            indexPbS = indexOf(this.mDispModeList[1][0], 1);
        }
        if (-1 == indexRecEVF) {
            indexRecEVF = indexOf(this.mDispModeList[0][1], 5);
        }
        this.mActiveDisplayModeIndex = new int[][]{new int[]{indexRecLCD, indexRecEVF, indexRecLCD}, new int[]{indexPbS, indexPbS, indexPbS}};
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private static int roundDisplayMode(int dispMode) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int[][] arr$ = DISPMODE_ROUND_TABLE;
        for (int[] item : arr$) {
            if (dispMode == item[0]) {
                return item[1];
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return dispMode;
    }

    private static int indexOf(int[] array, int value) {
        int index = -1;
        AppLog.enter(TAG, "indexOf()");
        int i = 0;
        while (true) {
            if (i >= array.length) {
                break;
            }
            if (array[i] != value) {
                i++;
            } else {
                index = i;
                break;
            }
        }
        AppLog.exit(TAG, "indexOf() index = " + index);
        return index;
    }

    @Override // com.sony.imaging.app.base.common.DisplayModeObserver
    public int getActiveDispMode(int appMode) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int dispmode = -1;
        int stActiveDevice = DisplayModeObserver.getInstance().getActiveDevice();
        if (stActiveDevice != -1) {
            dispmode = getDispMode(appMode, stActiveDevice);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return dispmode;
    }

    private int getDispMode(int appMode, int device) {
        int index = this.mActiveDisplayModeIndex[appMode][device];
        if (STUtility.getInstance().isCapturingStarted() && device == 1) {
            index = 1;
            AppLog.info(TAG, "getDispMode 1");
        }
        AppLog.info(TAG, "getDispMode " + index);
        int dispmode = this.mDispModeList[appMode][device][index];
        return dispmode;
    }

    @Override // com.sony.imaging.app.base.common.DisplayModeObserver
    public void toggleDisplayMode(int appMode) {
        int newDispMode;
        int stActiveDevice = DisplayModeObserver.getInstance().getActiveDevice();
        int dispModeCount = this.mDispModeList[appMode][stActiveDevice].length;
        int[] iArr = this.mActiveDisplayModeIndex[appMode];
        int newDispMode2 = (this.mActiveDisplayModeIndex[appMode][stActiveDevice] + 1) % dispModeCount;
        iArr[stActiveDevice] = newDispMode2;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (STUtility.getInstance().isCapturingStarted() && STUtility.getInstance().getCaptureStatus()) {
            newDispMode = manageCustomDisplayMode(appMode, stActiveDevice, dispModeCount, newDispMode2);
        } else {
            newDispMode = skipSTDigitalLevel(appMode, stActiveDevice, dispModeCount, newDispMode2);
        }
        if (1 == appMode) {
            this.mActiveDisplayModeIndex[1][0] = newDispMode;
            this.mActiveDisplayModeIndex[1][1] = newDispMode;
        }
        if (stActiveDevice == 0 || 2 == stActiveDevice) {
            this.mActiveDisplayModeIndex[appMode][0] = newDispMode;
            this.mActiveDisplayModeIndex[appMode][2] = newDispMode;
        } else if (1 == stActiveDevice) {
            this.mActiveDisplayModeIndex[appMode][1] = newDispMode;
        }
        int dispMode = getDispMode(appMode, stActiveDevice);
        if (11 == dispMode) {
            DisplayModeObserver.getInstance().setDisplayMode(appMode, 1);
        } else {
            DisplayModeObserver.getInstance().setDisplayMode(appMode, dispMode);
        }
        notify(DisplayModeObserver.TAG_DISPMODE_CHANGE);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    protected int skipSTDigitalLevel(int appMode, int activeDevice, int dispModeCount, int dispModeIndex) {
        AppLog.enter("skipDigitalLevel", "getPfApiVersion = " + String.valueOf(CameraSetting.getPfApiVersion()));
        AppLog.info("skipDigitalLevel", "dispModeIndex = " + String.valueOf(dispModeIndex));
        int dispmode = this.mDispModeList[appMode][activeDevice][dispModeIndex];
        if (12 == dispmode) {
            AppLog.info("skipCapturingDisplayMode", "dispmode = DISPMODE_REC_STARTRAILS_MUTE " + String.valueOf(dispmode));
            return 0;
        }
        if (2 <= CameraSetting.getPfApiVersion()) {
            int isSupporeted = ScalarProperties.getInt("device.digital.level");
            AppLog.info("skipDigitalLevel", "isSupporeted = " + String.valueOf(isSupporeted));
            if (isSupporeted == 0) {
                switch (dispmode) {
                    case 1:
                    case 3:
                    case 5:
                    case 11:
                        break;
                    case 2:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                    default:
                        dispModeIndex = indexOf(this.mDispModeList[appMode][activeDevice], 11);
                        break;
                    case 4:
                        AppLog.info("skipDigitalLevel", "dispmode = " + String.valueOf(dispmode));
                        dispModeIndex = (dispModeIndex + 1) % dispModeCount;
                        break;
                }
                AppLog.info("skipDigitalLevel", "dispmode = " + String.valueOf(dispmode));
            }
        }
        AppLog.exit("skipDigitalLevel", "dispModeIndex = " + String.valueOf(dispModeIndex));
        return dispModeIndex;
    }

    protected int manageCustomDisplayMode(int appMode, int activeDevice, int dispModeCount, int dispModeIndex) {
        AppLog.enter("skipCapturingDisplayMode", "getPfApiVersion = " + String.valueOf(CameraSetting.getPfApiVersion()));
        AppLog.info("skipCapturingDisplayMode", "dispModeIndex = " + String.valueOf(dispModeIndex));
        int dispmode = this.mDispModeList[appMode][activeDevice][dispModeIndex];
        if (STUtility.getInstance().isCapturingStarted()) {
            switch (dispmode) {
                case 1:
                case 11:
                case 12:
                    break;
                case 2:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                default:
                    dispModeIndex = indexOf(this.mDispModeList[appMode][activeDevice], 11);
                    break;
                case 3:
                case 4:
                case 5:
                    dispModeIndex = 5;
                    break;
            }
        } else {
            switch (dispmode) {
                case 1:
                case 3:
                case 4:
                case 5:
                case 11:
                    break;
                case 2:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                default:
                    dispModeIndex = indexOf(this.mDispModeList[appMode][activeDevice], 11);
                    break;
                case 12:
                    AppLog.info("skipCapturingDisplayMode", "dispmode = " + String.valueOf(dispmode));
                    dispModeIndex = (dispModeIndex + 1) % dispModeCount;
                    break;
            }
        }
        AppLog.info("skipCapturingDisplayMode", "dispmode = " + String.valueOf(dispmode));
        AppLog.exit("skipCapturingDisplayMode", "dispModeIndex = " + String.valueOf(dispModeIndex));
        return dispModeIndex;
    }

    public void setStarTrailDisplayMode() {
        int stActiveDevice = DisplayModeObserver.getInstance().getActiveDevice();
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mActiveDisplayModeIndex[0][0] = 0;
        this.mActiveDisplayModeIndex[0][2] = 0;
        this.mActiveDisplayModeIndex[0][1] = 1;
        int dispMode = getDispMode(0, stActiveDevice);
        if (11 == dispMode) {
            DisplayModeObserver.getInstance().setDisplayMode(0, 1);
        } else {
            DisplayModeObserver.getInstance().setDisplayMode(0, dispMode);
        }
        notify(DisplayModeObserver.TAG_DISPMODE_CHANGE);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void setStartTrailsPlayBackDisplayMode() {
        int stActiveDevice = DisplayModeObserver.getInstance().getActiveDevice();
        AppLog.enter(TAG, AppLog.getMethodName());
        int dispMode = getDispMode(1, stActiveDevice);
        if (5 == dispMode) {
            DisplayModeObserver.getInstance().setDisplayMode(1, 1);
        } else {
            DisplayModeObserver.getInstance().setDisplayMode(1, dispMode);
        }
        notify(DisplayModeObserver.TAG_DISPMODE_CHANGE);
        AppLog.exit(TAG, AppLog.getMethodName());
    }
}
