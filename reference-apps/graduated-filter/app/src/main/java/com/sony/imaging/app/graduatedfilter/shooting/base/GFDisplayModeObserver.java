package com.sony.imaging.app.graduatedfilter.shooting.base;

import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.util.GyroscopeObserver;
import com.sony.scalar.sysutil.ScalarProperties;
import com.sony.scalar.sysutil.didep.Settings;

/* loaded from: classes.dex */
public class GFDisplayModeObserver extends DisplayModeObserver {
    public static final int DISPMODE_REC_APP_INFO_OFF = 12;
    private static int[][] DISPMODE_ROUND_TABLE = {new int[]{6, 1, 1}, new int[]{0, 1, 1}, new int[]{2, 1, 1}, new int[]{7, 1, 1}, new int[]{8, 1, 3}};
    private static GFDisplayModeObserver mTheInstance = null;
    private int[][] mActiveDisplayModeIndex;
    private final int[][][] mDispModeList;
    private final int[][] DISPMODE_LIST_FOR_REC = {new int[]{1, 12, 5, 4}, new int[]{1, 12, 5, 4}, new int[]{1, 12, 5, 4}};
    private final int[][] DISPMODE_LIST_FOR_REC_NO_DL = {new int[]{1, 12, 5}, new int[]{1, 12, 5}, new int[]{1, 12, 5}};
    private final int[][] DISPMODE_LIST_FOR_PLAY = {new int[]{1, 5, 3}, new int[]{1, 5, 3}, new int[]{1, 5, 3}};
    private final int[][][] DISPMODE_LIST = {this.DISPMODE_LIST_FOR_REC, this.DISPMODE_LIST_FOR_PLAY};
    private final int[][][] DISPMODE_LIST_NO_DL = {this.DISPMODE_LIST_FOR_REC_NO_DL, this.DISPMODE_LIST_FOR_PLAY};

    public static GFDisplayModeObserver getInstance() {
        if (mTheInstance == null) {
            mTheInstance = new GFDisplayModeObserver();
        }
        return mTheInstance;
    }

    public GFDisplayModeObserver() {
        if (GyroscopeObserver.getInstance().hasDigitalLevel()) {
            this.mDispModeList = this.DISPMODE_LIST;
        } else {
            this.mDispModeList = this.DISPMODE_LIST_NO_DL;
        }
        initializeActiveDisplayModeIndex();
    }

    private void initializeActiveDisplayModeIndex() {
        int dispModePbS = Settings.getDispMode(2);
        int indexRecLCD = indexOf(this.mDispModeList[0][0], 1);
        int indexRecEVF = indexOf(this.mDispModeList[0][1], 1);
        int dispModeCountLCD = this.mDispModeList[0][0].length;
        int indexRecLCD2 = gfSkipDigitalLevel(0, 0, dispModeCountLCD, indexRecLCD);
        int dispModeCountEVF = this.mDispModeList[0][1].length;
        int indexRecEVF2 = gfSkipDigitalLevel(0, 1, dispModeCountEVF, indexRecEVF);
        int indexPbS = indexOf(this.mDispModeList[1][0], dispModePbS);
        if (-1 == indexPbS) {
            indexPbS = indexOf(this.mDispModeList[1][0], 1);
        }
        this.mActiveDisplayModeIndex = new int[][]{new int[]{indexRecLCD2, indexRecEVF2, indexRecLCD2}, new int[]{indexPbS, indexPbS, indexPbS}};
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

    private static int roundDisplayMode(int dispMode) {
        int[][] arr$ = DISPMODE_ROUND_TABLE;
        for (int[] item : arr$) {
            if (dispMode == item[0]) {
                return item[1];
            }
        }
        return dispMode;
    }

    @Override // com.sony.imaging.app.base.common.DisplayModeObserver
    public int getActiveDispMode(int appMode) {
        int activeDevice = DisplayModeObserver.getInstance().getActiveDevice();
        if (activeDevice == -1) {
            return -1;
        }
        int dispmode = getDispMode(appMode, activeDevice);
        return dispmode;
    }

    @Override // com.sony.imaging.app.base.common.DisplayModeObserver
    public void toggleDisplayMode(int appMode) {
        int activeDevice = DisplayModeObserver.getInstance().getActiveDevice();
        int dispModeCount = this.mDispModeList[appMode][activeDevice].length;
        int dispModeIndex = gfSkipDigitalLevel(appMode, activeDevice, dispModeCount, (this.mActiveDisplayModeIndex[appMode][activeDevice] + 1) % dispModeCount);
        if (1 == appMode && isPbHistogramDisabled()) {
            int dispmode = this.mDispModeList[appMode][activeDevice][dispModeIndex];
            if (5 == dispmode) {
                dispModeIndex = (dispModeIndex + 1) % dispModeCount;
            }
        }
        this.mActiveDisplayModeIndex[appMode][activeDevice] = dispModeIndex;
        if (1 == appMode) {
            this.mActiveDisplayModeIndex[1][0] = dispModeIndex;
            this.mActiveDisplayModeIndex[1][1] = dispModeIndex;
        }
        if (activeDevice == 0 || 2 == activeDevice) {
            this.mActiveDisplayModeIndex[appMode][0] = dispModeIndex;
            this.mActiveDisplayModeIndex[appMode][2] = dispModeIndex;
        }
        int dispMode = getDispMode(appMode, activeDevice);
        if (12 == dispMode) {
            DisplayModeObserver.getInstance().setDisplayMode(appMode, 3);
        } else {
            DisplayModeObserver.getInstance().setDisplayMode(appMode, dispMode);
        }
        notify(DisplayModeObserver.TAG_DISPMODE_CHANGE);
    }

    private int getDispMode(int appMode, int device) {
        int index = this.mActiveDisplayModeIndex[appMode][device];
        int dispmode = this.mDispModeList[appMode][device][index];
        return dispmode;
    }

    private int gfSkipDigitalLevel(int appMode, int activeDevice, int dispModeCount, int dispModeIndex) {
        if (3 <= CameraSetting.getPfApiVersion() && appMode == 0) {
            int isSupporeted = ScalarProperties.getInt("device.digital.level");
            if (isSupporeted == 0) {
                int dispmode = this.mDispModeList[appMode][activeDevice][dispModeIndex];
                switch (dispmode) {
                    case 1:
                    case 5:
                    case 12:
                        return dispModeIndex;
                    case 4:
                        return (dispModeIndex + 1) % dispModeCount;
                    default:
                        return indexOf(this.mDispModeList[appMode][activeDevice], 1);
                }
            }
            return dispModeIndex;
        }
        return dispModeIndex;
    }

    @Override // com.sony.imaging.app.base.common.DisplayModeObserver
    public void setDisplayMode(int appMode, int dispMode) {
        int activeDevice = DisplayModeObserver.getInstance().getActiveDevice();
        int[] dispModes = this.mDispModeList[appMode][activeDevice];
        int length = dispModes.length;
        int index = -1;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            if (dispMode != dispModes[i]) {
                i++;
            } else {
                index = i;
                break;
            }
        }
        if (index > -1) {
            setDisplayModeIndex(appMode, index);
        }
    }

    @Override // com.sony.imaging.app.base.common.DisplayModeObserver
    public void setDisplayModeIndex(int appMode, int dispModeIndex) {
        int activeDevice = DisplayModeObserver.getInstance().getActiveDevice();
        this.mActiveDisplayModeIndex[appMode][activeDevice] = dispModeIndex;
        if (1 == appMode) {
            this.mActiveDisplayModeIndex[1][0] = dispModeIndex;
            this.mActiveDisplayModeIndex[1][1] = dispModeIndex;
        }
        if (activeDevice == 0 || 2 == activeDevice) {
            this.mActiveDisplayModeIndex[appMode][0] = dispModeIndex;
            this.mActiveDisplayModeIndex[appMode][2] = dispModeIndex;
        }
        notify(DisplayModeObserver.TAG_DISPMODE_CHANGE);
    }
}
