package com.sony.imaging.app.timelapse.shooting.base;

import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.timelapse.AppLog;
import com.sony.imaging.app.util.GyroscopeObserver;
import com.sony.scalar.sysutil.ScalarProperties;
import com.sony.scalar.sysutil.didep.Settings;

/* loaded from: classes.dex */
public class TimeLapseDisplayModeObserver extends DisplayModeObserver {
    public static final int DISPMODE_REC_TIMELAPSE = 11;
    public static final String SKIP_DIGITAL_LEVEL_TAG = "skipDigitalLevel";
    private int[][] mActiveDisplayModeIndex;
    private int[][][] mDispModeList;
    private static int[][] DISPMODE_ROUND_TABLE = {new int[]{6, 11, 1}, new int[]{0, 11, 1}, new int[]{2, 11, 1}, new int[]{7, 11, 1}, new int[]{8, 11, 3}};
    private static TimeLapseDisplayModeObserver mTheInstance = new TimeLapseDisplayModeObserver();
    private final int[][] DISPMODE_LIST_FOR_REC = {new int[]{11, 1, 3, 4, 5}, new int[]{5, 1, 3, 4}, new int[]{11, 1, 3, 4, 5}};
    private final int[][] DISPMODE_LIST_FOR_PLAY = {new int[]{1, 3}, new int[]{1, 3}, new int[]{1, 3}};
    private final int[][][] DISPMODE_LIST = {this.DISPMODE_LIST_FOR_REC, this.DISPMODE_LIST_FOR_PLAY};

    public static TimeLapseDisplayModeObserver getInstance() {
        return mTheInstance;
    }

    protected TimeLapseDisplayModeObserver() {
        if (GyroscopeObserver.getInstance().hasDigitalLevel()) {
            this.mDispModeList = this.DISPMODE_LIST;
        }
        initializeActiveDisplayModeIndex();
    }

    private void initializeActiveDisplayModeIndex() {
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
    }

    private static int roundDisplayMode(int dispMode) {
        int[][] arr$ = DISPMODE_ROUND_TABLE;
        for (int[] item : arr$) {
            if (dispMode == item[0]) {
                return item[1];
            }
        }
        return dispMode;
    }

    private static int indexOf(int[] array, int value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                int index = i;
                return index;
            }
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.common.DisplayModeObserver
    public int getActiveDispMode(int appMode) {
        int tlActiveDevice = DisplayModeObserver.getInstance().getActiveDevice();
        if (tlActiveDevice == -1) {
            return -1;
        }
        int dispmode = getDispMode(appMode, tlActiveDevice);
        return dispmode;
    }

    private int getDispMode(int appMode, int device) {
        int index = this.mActiveDisplayModeIndex[appMode][device];
        int dispmode = this.mDispModeList[appMode][device][index];
        return dispmode;
    }

    @Override // com.sony.imaging.app.base.common.DisplayModeObserver
    public void toggleDisplayMode(int appMode) {
        int tlActiveDevice = DisplayModeObserver.getInstance().getActiveDevice();
        int dispModeCount = this.mDispModeList[appMode][tlActiveDevice].length;
        int[] iArr = this.mActiveDisplayModeIndex[appMode];
        int newDispMode = (this.mActiveDisplayModeIndex[appMode][tlActiveDevice] + 1) % dispModeCount;
        iArr[tlActiveDevice] = newDispMode;
        int newDispMode2 = skipTLDigitalLevel(appMode, tlActiveDevice, dispModeCount, newDispMode);
        if (1 == appMode) {
            this.mActiveDisplayModeIndex[1][0] = newDispMode2;
            this.mActiveDisplayModeIndex[1][1] = newDispMode2;
        }
        if (tlActiveDevice == 0 || 2 == tlActiveDevice) {
            this.mActiveDisplayModeIndex[appMode][0] = newDispMode2;
            this.mActiveDisplayModeIndex[appMode][2] = newDispMode2;
        }
        if (1 == tlActiveDevice) {
            this.mActiveDisplayModeIndex[appMode][1] = newDispMode2;
        }
        int dispMode = getDispMode(appMode, tlActiveDevice);
        if (11 == dispMode) {
            DisplayModeObserver.getInstance().setDisplayMode(appMode, 1);
        } else {
            DisplayModeObserver.getInstance().setDisplayMode(appMode, dispMode);
        }
        notify(DisplayModeObserver.TAG_DISPMODE_CHANGE);
    }

    protected int skipTLDigitalLevel(int appMode, int activeDevice, int dispModeCount, int dispModeIndex) {
        AppLog.enter(SKIP_DIGITAL_LEVEL_TAG, "getPfApiVersion = " + String.valueOf(CameraSetting.getPfApiVersion()));
        AppLog.info(SKIP_DIGITAL_LEVEL_TAG, "dispModeIndex = " + String.valueOf(dispModeIndex));
        if (2 <= CameraSetting.getPfApiVersion()) {
            int isSupporeted = ScalarProperties.getInt("device.digital.level");
            AppLog.info(SKIP_DIGITAL_LEVEL_TAG, "isSupporeted = " + String.valueOf(isSupporeted));
            if (isSupporeted == 0) {
                int dispmode = this.mDispModeList[appMode][activeDevice][dispModeIndex];
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
                        AppLog.info(SKIP_DIGITAL_LEVEL_TAG, "dispmode = " + String.valueOf(dispmode));
                        dispModeIndex = (dispModeIndex + 1) % dispModeCount;
                        break;
                }
                AppLog.info(SKIP_DIGITAL_LEVEL_TAG, "dispmode = " + String.valueOf(dispmode));
            }
        }
        AppLog.exit(SKIP_DIGITAL_LEVEL_TAG, "dispModeIndex = " + String.valueOf(dispModeIndex));
        return dispModeIndex;
    }
}
