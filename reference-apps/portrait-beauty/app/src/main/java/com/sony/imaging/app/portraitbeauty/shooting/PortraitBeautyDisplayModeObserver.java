package com.sony.imaging.app.portraitbeauty.shooting;

import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyUtil;
import com.sony.imaging.app.util.GyroscopeObserver;
import com.sony.scalar.hardware.avio.DisplayManager;
import com.sony.scalar.sysutil.didep.Settings;

/* loaded from: classes.dex */
public class PortraitBeautyDisplayModeObserver extends DisplayModeObserver {
    private static final String TAG = "PortraitBeautyDisplayModeObserver";
    private int[][] mActiveDisplayModeIndex;
    private int[][][] mDispModeList;
    private static int[][] DISPMODE_ROUND_TABLE = {new int[]{6, 1}, new int[]{0, 1}, new int[]{2, 1}, new int[]{7, 1}, new int[]{8, 3}};
    private static PortraitBeautyDisplayModeObserver mTheInstance = new PortraitBeautyDisplayModeObserver();
    private final int[][] DISPMODE_LIST_FOR_REC = {new int[]{1, 3, 5, 4}, new int[]{1, 3, 5, 4}, new int[]{1, 3, 5, 4}};
    private final int[][] DISPMODE_LIST_FOR_PLAY = {new int[]{1, 3}, new int[]{1, 3}, new int[]{1, 3}};
    private final int[][][] DISPMODE_LIST = {this.DISPMODE_LIST_FOR_REC, this.DISPMODE_LIST_FOR_PLAY};
    private DisplayManager mDisplayManager = null;

    public static PortraitBeautyDisplayModeObserver getInstance() {
        return mTheInstance;
    }

    public PortraitBeautyDisplayModeObserver() {
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
            indexRecEVF = indexOf(this.mDispModeList[0][1], 1);
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
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                int index = i;
                return index;
            }
        }
        return -1;
    }

    private int getDispMode(int appMode, int device) {
        int index = this.mActiveDisplayModeIndex[appMode][device];
        int dispmode = this.mDispModeList[appMode][device][index];
        return dispmode;
    }

    @Override // com.sony.imaging.app.base.common.DisplayModeObserver
    public int getActiveDispMode(int appMode) {
        int portraitBeautyActiveDevice = getInstance().getActiveDevice();
        if (portraitBeautyActiveDevice == -1) {
            return -1;
        }
        if (true == PortraitBeautyUtil.bIsAdjustModeGuide) {
            return 3;
        }
        int dispmode = getDispMode(appMode, portraitBeautyActiveDevice);
        return dispmode;
    }

    @Override // com.sony.imaging.app.base.common.DisplayModeObserver
    public void toggleDisplayMode(int appMode) {
        int portraitBeautyActiveDevice = getInstance().getActiveDevice();
        int dispModeCount = this.mDispModeList[appMode][portraitBeautyActiveDevice].length;
        int dispModeIndex = (this.mActiveDisplayModeIndex[appMode][portraitBeautyActiveDevice] + 1) % dispModeCount;
        setDisplayModeIndex(appMode, skipDigitalLevel(appMode, portraitBeautyActiveDevice, dispModeCount, dispModeIndex));
    }

    @Override // com.sony.imaging.app.base.common.DisplayModeObserver
    public void setDisplayModeIndex(int appMode, int dispModeIndex) {
        int portraitBeautyActiveDevice = getInstance().getActiveDevice();
        this.mActiveDisplayModeIndex[appMode][portraitBeautyActiveDevice] = dispModeIndex;
        if (1 == appMode) {
            this.mActiveDisplayModeIndex[1][0] = dispModeIndex;
            this.mActiveDisplayModeIndex[1][1] = dispModeIndex;
        }
        if (portraitBeautyActiveDevice == 0 || 2 == portraitBeautyActiveDevice) {
            this.mActiveDisplayModeIndex[appMode][0] = dispModeIndex;
            this.mActiveDisplayModeIndex[appMode][2] = dispModeIndex;
        }
        int dispMode = getDispMode(0, portraitBeautyActiveDevice);
        DisplayModeObserver.getInstance().setDisplayMode(0, dispMode);
        notify(DisplayModeObserver.TAG_DISPMODE_CHANGE);
    }

    @Override // com.sony.imaging.app.base.common.DisplayModeObserver
    public void setDisplayMode(int appMode, int dispMode) {
        int portraitBeautyActiveDevice = getInstance().getActiveDevice();
        int[] dispModes = this.mDispModeList[appMode][portraitBeautyActiveDevice];
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
        } else {
            AppLog.enter(TAG, "DisplayMode not set");
        }
    }

    @Override // com.sony.imaging.app.base.common.DisplayModeObserver
    public boolean isPbHistogramDisabled() {
        return true;
    }
}
