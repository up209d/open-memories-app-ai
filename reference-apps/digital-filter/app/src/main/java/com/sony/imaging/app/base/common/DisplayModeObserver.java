package com.sony.imaging.app.base.common;

import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.fw.RunStatus;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.GyroscopeObserver;
import com.sony.imaging.app.util.NotificationManager;
import com.sony.imaging.app.util.PTag;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.avio.DisplayManager;
import com.sony.scalar.sysutil.ScalarProperties;
import com.sony.scalar.sysutil.didep.Settings;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/* loaded from: classes.dex */
public class DisplayModeObserver extends NotificationManager {
    public static final int APP_MODE_PLAY = 1;
    public static final int APP_MODE_REC = 0;
    public static final int BURN_IN_OLEAD_ARISE = 1;
    public static final int BURN_IN_OLEAD_CLEAR = 0;
    public static final int DEVICE_ID_FINDER = 1;
    public static final int DEVICE_ID_HDMI = 2;
    public static final int DEVICE_ID_NONE = -1;
    public static final int DEVICE_ID_PANEL = 0;
    public static final int DISPMODE_NONE = -1;
    public static final int DISPMODE_PLAY_HISTGRAM = 5;
    public static final int DISPMODE_PLAY_INFO_OFF = 3;
    public static final int DISPMODE_PLAY_INFO_ON = 1;
    public static final int DISPMODE_REC_DIGITAL_LEVEL = 4;
    public static final int DISPMODE_REC_HISTOGRAM = 5;
    public static final int DISPMODE_REC_INFO_OFF = 3;
    public static final int DISPMODE_REC_INFO_ON = 1;
    private static final String LOG_MSG_4K_DEVICE_CONNECTED = "Display supports 4k connected.";
    private static final String LOG_MSG_4K_DEVICE_DISCONNECTED = "Display supports 4k disconnected.";
    private static final String LOG_MSG_CANNOTGETLENSINFO = "onDeviceStatusChanged ";
    private static final String LOG_MSG_COMMA = ", ";
    private static final String LOG_MSG_DISPMODEINDEXES = "DispMode indexes = ";
    private static final String LOG_MSG_DISPMODES = "DispModes ";
    private static final String LOG_MSG_ERROR_STR1 = "unkown value is coming";
    private static final String LOG_MSG_EVENTID = "eventId = ";
    private static final String LOG_MSG_EVF = "EVF = ";
    private static final String LOG_MSG_FAILED_TO_GET_VIDEO_RECT = "getDisplayedVideoRect returns null";
    private static final String LOG_MSG_FOURCE_STOP_4K_ON_PAUSE = "call stopQfhdFormatOutput on pause";
    private static final String LOG_MSG_GETACTIVEDEVICE = "getActiveDevice = ";
    private static final String LOG_MSG_ILLEGALDEVICE = "Illegal device is returned from platform. device is treated as panel. device = ";
    private static final String LOG_MSG_LCD = "LCD = ";
    private static final String LOG_MSG_MINUS = " - ";
    private static final String LOG_MSG_NO_EXIST_DISPLAYMODE = "display mode does not exist ";
    private static final String LOG_MSG_ONLAYOUTCHANGED = "onLayoutChanged ";
    private static final String LOG_MSG_PBS = "PbS = ";
    private static final String LOG_MSG_RECT = "rect = ";
    private static final String LOG_MSG_ROUND_BRACKET_L = " ( ";
    private static final String LOG_MSG_ROUND_BRACKET_R = " ) ";
    private static final String LOG_MSG_START_QFHD_FORMAT = "call startQfhdFormatOutput current status : ";
    private static final String LOG_MSG_STOP_QFHD_FORMAT = "call stopQfhdFormatOutput current status : ";
    public static final int PF_VER_4K_HDMI_SUPPORTED = 7;
    public static final String PTAG_DISPCHANGE_COMMON = "disp change";
    private static final String PTAG_DISPCHANGE_DEVICE = "disp change panel evf";
    private static final String PTAG_DISPCHANGE_DISPMODE = "disp change disp mode";
    public static final int STATUS_4K_CHANGING = 4;
    public static final int STATUS_4K_OFF = 1;
    public static final int STATUS_4K_ON = 2;
    public static final int STATUS_4K_STARTING = 6;
    public static final int STATUS_4K_STOPPING = 5;
    private static final String TAG = "DisplayModeObserver";
    public static final String TAG_4K_PLAYBACK_STATUS_CHANGE = "com.sony.imaging.app.base.common.DisplayModeObserver.4kPlaybackChange";
    public static final String TAG_DEVICE_CHANGE = "com.sony.imaging.app.base.common.DisplayModeObserver.DeviceChange";
    public static final String TAG_DISPMODE_CHANGE = "com.sony.imaging.app.base.common.DisplayModeObserver.DispModeChange";
    public static final String TAG_OLEADSCREEN_CHANGE = "com.sony.imaging.app.base.common.DisplayModeObserver.OledScreenChange";
    public static final String TAG_VIEW_PATTERN_CHANGE = "com.sony.imaging.app.base.common.DisplayModeObserver.ViewPatternChange";
    public static final String TAG_YUVLAYOUT_CHANGE = "com.sony.imaging.app.base.common.DisplayModeObserver.YUVChange";
    public static final int VIEW_PATTERN_OSD_NORMAL = 4;
    public static final int VIEW_PATTERN_REVERSE_OSD180 = 1;
    private int mActiveDevice;
    private String[] mActiveDevices;
    private int[][] mActiveDisplayModeIndex;
    private final int[][][] mDispModeList;
    private OnVideoLayoutStatusListener mLayoutStatusListener;
    private static final String[] DEVICE_STRINGS_LIST = {"DEVICE_ID_PANEL", "DEVICE_ID_FINDER", "DEVICE_ID_HDMI"};
    private static DisplayManager.VideoRect mYUVRect = null;
    private static int[][] DISPMODE_ROUND_TABLE = {new int[]{6, 1}, new int[]{0, 1}, new int[]{2, 1}, new int[]{7, 1}, new int[]{8, 3}};
    private static DisplayModeObserver mTheInstance = new DisplayModeObserver();
    private final int[][] DISPMODE_LIST_FOR_REC = {new int[]{1, 3, 5, 4}, new int[]{1, 3, 5, 4}, new int[]{1, 3, 5, 4}};
    private final int[][] DISPMODE_LIST_FOR_REC_NO_DL = {new int[]{1, 3, 5}, new int[]{1, 5}, new int[]{1, 3, 5}};
    private final int[][] DISPMODE_LIST_FOR_PLAY = {new int[]{1, 5, 3}, new int[]{1, 5, 3}, new int[]{1, 5, 3}};
    private final int[][][] DISPMODE_LIST = {this.DISPMODE_LIST_FOR_REC, this.DISPMODE_LIST_FOR_PLAY};
    private final int[][][] DISPMODE_LIST_NO_DL = {this.DISPMODE_LIST_FOR_REC_NO_DL, this.DISPMODE_LIST_FOR_PLAY};
    private DisplayManager mDisplayManager = null;
    private int mOleadScreenStatus = 0;
    private boolean mIs4kOutputSupported = false;
    MessageQueue.IdleHandler mIdleHandler = new MyIdleHandler();
    protected int m4kOutputStatus = 1;
    protected boolean mIsDialogShown4kOutputConnected = false;
    protected int mCautionIdOn4kOutpuConnected = -1;
    private OnDisplayEventListener mDispEventListener = new OnDisplayEventListener();
    private DisplayManager.OnScreenDisplayListener mScreenDisplayListener = new OnOleadScreenDisplayListener();

    /* JADX INFO: Access modifiers changed from: protected */
    public DisplayModeObserver() {
        this.mLayoutStatusListener = null;
        this.mLayoutStatusListener = new OnVideoLayoutStatusListener();
        if (GyroscopeObserver.getInstance().hasDigitalLevel()) {
            this.mDispModeList = this.DISPMODE_LIST;
        } else {
            this.mDispModeList = this.DISPMODE_LIST_NO_DL;
        }
        initializeActiveDisplayModeIndex();
    }

    private void initializeActiveDisplayModeIndex() {
        int dispModeLCD = Settings.getDispMode(0);
        int dispModeEVF = Settings.getDispMode(1);
        int dispModePbS = Settings.getDispMode(2);
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), LOG_MSG_DISPMODES).append(LOG_MSG_LCD).append(dispModeLCD).append(", ").append(LOG_MSG_EVF).append(dispModeEVF).append(", ").append(LOG_MSG_PBS).append(dispModePbS).append(", ");
        Log.i(TAG, builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        int indexRecLCD = indexOf(this.mDispModeList[0][0], roundDisplayMode(dispModeLCD));
        int indexRecEVF = indexOf(this.mDispModeList[0][1], roundDisplayMode(dispModeEVF));
        int dispModeCountLCD = this.mDispModeList[0][0].length;
        int indexRecLCD2 = skipDigitalLevel(0, 0, dispModeCountLCD, indexRecLCD);
        int dispModeCountEVF = this.mDispModeList[0][1].length;
        int indexRecEVF2 = skipDigitalLevel(0, 1, dispModeCountEVF, indexRecEVF);
        int indexPbS = indexOf(this.mDispModeList[1][0], dispModePbS);
        if (-1 == indexPbS) {
            indexPbS = indexOf(this.mDispModeList[1][0], 1);
        }
        builder.replace(0, builder.length(), LOG_MSG_DISPMODEINDEXES).append(LOG_MSG_LCD).append(indexRecLCD2).append(", ").append(LOG_MSG_EVF).append(indexRecEVF2).append(", ").append(LOG_MSG_PBS).append(indexPbS).append(", ");
        Log.i(TAG, builder.toString());
        this.mActiveDisplayModeIndex = new int[][]{new int[]{indexRecLCD2, indexRecEVF2, indexRecLCD2}, new int[]{indexPbS, indexPbS, indexPbS}};
    }

    private void updataActiveDisplayModeWithCommonSettings() {
        int dispModeLCD = CommonSettings.getInstance().getCommonSettingInt(CommonSettings.KEY_DISP_MODE_LCD, -1);
        if (-1 != dispModeLCD) {
            int index = indexOf(this.mDispModeList[0][0], roundDisplayMode(dispModeLCD));
            this.mActiveDisplayModeIndex[0][0] = index;
            this.mActiveDisplayModeIndex[0][2] = index;
        } else {
            CommonSettings.getInstance().putCommonSettingInt(CommonSettings.KEY_DISP_MODE_LCD, getDispMode(0, 0));
        }
        int dispModeEVF = CommonSettings.getInstance().getCommonSettingInt(CommonSettings.KEY_DISP_MODE_EVF, -1);
        if (-1 != dispModeEVF) {
            this.mActiveDisplayModeIndex[0][1] = indexOf(this.mDispModeList[0][1], roundDisplayMode(dispModeEVF));
        } else {
            CommonSettings.getInstance().putCommonSettingInt(CommonSettings.KEY_DISP_MODE_EVF, getDispMode(0, 1));
        }
        int dispModePbS = CommonSettings.getInstance().getCommonSettingInt(CommonSettings.KEY_DISP_MODE_PBS, -1);
        if (-1 != dispModePbS) {
            int index2 = indexOf(this.mDispModeList[1][0], dispModePbS);
            if (-1 == index2) {
                index2 = indexOf(this.mDispModeList[1][0], 1);
            }
            this.mActiveDisplayModeIndex[1][0] = index2;
            this.mActiveDisplayModeIndex[1][1] = index2;
            this.mActiveDisplayModeIndex[1][2] = index2;
            return;
        }
        CommonSettings.getInstance().putCommonSettingInt(CommonSettings.KEY_DISP_MODE_PBS, getDispMode(1, 0));
    }

    private void writeDisplayModeToCommonSettings() {
        CommonSettings.getInstance().putCommonSettingInt(CommonSettings.KEY_DISP_MODE_LCD, getDispMode(0, 0));
        CommonSettings.getInstance().putCommonSettingInt(CommonSettings.KEY_DISP_MODE_EVF, getDispMode(0, 1));
        CommonSettings.getInstance().putCommonSettingInt(CommonSettings.KEY_DISP_MODE_PBS, getDispMode(1, 0));
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

    public static DisplayModeObserver getInstance() {
        return mTheInstance;
    }

    public void resume() {
        resume(false);
    }

    public void resume(boolean is4kOutputSupported) {
        this.mIs4kOutputSupported = is4kOutputSupported && is4kInformationSupportedByPF();
        this.m4kOutputStatus = 1;
        this.mIsDialogShown4kOutputConnected = false;
        this.mCautionIdOn4kOutpuConnected = -1;
        this.mDisplayManager = new DisplayManager();
        this.mDisplayManager.setDisplayStatusListener(this.mDispEventListener);
        this.mDisplayManager.setLayoutStatusListener(this.mLayoutStatusListener);
        this.mDisplayManager.setOnScreenDisplayListener(this.mScreenDisplayListener);
        this.mActiveDevice = obtainActiveDeviceId();
        this.mActiveDevices = this.mDisplayManager.getActiveDevices();
        updataActiveDisplayModeWithCommonSettings();
        mYUVRect = this.mDisplayManager.getDisplayedVideoRect();
        if (mYUVRect == null) {
            Log.w(TAG, LOG_MSG_FAILED_TO_GET_VIDEO_RECT);
            try {
                Constructor<DisplayManager.VideoRect> constructor = DisplayManager.VideoRect.class.getDeclaredConstructor(Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE);
                constructor.setAccessible(true);
                mYUVRect = constructor.newInstance(0, 0, 0, 0);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e2) {
                e2.printStackTrace();
            } catch (InstantiationException e3) {
                e3.printStackTrace();
            } catch (NoSuchMethodException e4) {
                e4.printStackTrace();
            } catch (SecurityException e5) {
                e5.printStackTrace();
            } catch (InvocationTargetException e6) {
                e6.printStackTrace();
            }
        }
        if (mYUVRect != null) {
            StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
            builder.replace(0, builder.length(), LOG_MSG_ONLAYOUTCHANGED).append(LOG_MSG_RECT).append(" ( ").append(mYUVRect.pxLeft).append(", ").append(mYUVRect.pxTop).append(" ) ").append(" - ").append(" ( ").append(mYUVRect.pxRight).append(", ").append(mYUVRect.pxBottom).append(" ) ");
            Log.i(TAG, builder.toString());
            StringBuilderThreadLocal.releaseScratchBuilder(builder);
        }
    }

    public void pause() {
        if (1 != this.m4kOutputStatus && 5 != this.m4kOutputStatus) {
            Log.i(TAG, LOG_MSG_FOURCE_STOP_4K_ON_PAUSE);
            this.mDisplayManager.stopQfhdFormatOutput();
            this.m4kOutputStatus = 1;
        }
        this.mDisplayManager.releaseDisplayStatusListener();
        this.mDisplayManager.releaseLayoutStatusListener();
        this.mDisplayManager.releaseOnScreenDisplayListener();
        this.mDisplayManager.finish();
        this.mDisplayManager = null;
        this.mOleadScreenStatus = 0;
        writeDisplayModeToCommonSettings();
        Looper.myQueue().removeIdleHandler(this.mIdleHandler);
    }

    @Override // com.sony.imaging.app.util.NotificationManager
    public Object getValue(String tag) {
        if (!tag.equals(TAG_YUVLAYOUT_CHANGE)) {
            return null;
        }
        Object result = mYUVRect;
        return result;
    }

    public int getActiveDevice() {
        return this.mActiveDevice;
    }

    public int getActiveDispMode(int appMode) {
        if (this.mActiveDevice == -1) {
            return -1;
        }
        int dispmode = getDispMode(appMode, this.mActiveDevice);
        return dispmode;
    }

    public int getOleadScreenStatus() {
        return this.mOleadScreenStatus;
    }

    private int getDispMode(int appMode, int device) {
        int index = this.mActiveDisplayModeIndex[appMode][device];
        int dispmode = this.mDispModeList[appMode][device][index];
        if (1 == appMode && isPbHistogramDisabled() && 5 == dispmode) {
            return 1;
        }
        return dispmode;
    }

    public void toggleDisplayMode(int appMode) {
        PTag.start(PTAG_DISPCHANGE_DISPMODE);
        int dispModeCount = this.mDispModeList[appMode][this.mActiveDevice].length;
        int dispModeIndex = skipDigitalLevel(appMode, this.mActiveDevice, dispModeCount, (this.mActiveDisplayModeIndex[appMode][this.mActiveDevice] + 1) % dispModeCount);
        if (1 == appMode && isPbHistogramDisabled()) {
            int dispmode = this.mDispModeList[appMode][this.mActiveDevice][dispModeIndex];
            if (5 == dispmode) {
                dispModeIndex = (dispModeIndex + 1) % dispModeCount;
            }
        }
        setDisplayModeIndex(appMode, dispModeIndex);
    }

    public void setDisplayMode(int appMode, int dispMode) {
        int[] dispModes = this.mDispModeList[appMode][this.mActiveDevice];
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
            return;
        }
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), LOG_MSG_NO_EXIST_DISPLAYMODE).append(appMode).append(" - ").append(dispMode);
        Log.w(TAG, builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
    }

    public void setDisplayModeIndex(int appMode, int dispModeIndex) {
        this.mActiveDisplayModeIndex[appMode][this.mActiveDevice] = dispModeIndex;
        if (1 == appMode) {
            this.mActiveDisplayModeIndex[1][0] = dispModeIndex;
            this.mActiveDisplayModeIndex[1][1] = dispModeIndex;
        }
        if (this.mActiveDevice == 0 || 2 == this.mActiveDevice) {
            this.mActiveDisplayModeIndex[appMode][0] = dispModeIndex;
            this.mActiveDisplayModeIndex[appMode][2] = dispModeIndex;
        }
        notify(TAG_DISPMODE_CHANGE);
    }

    public boolean isPbHistogramDisabled() {
        return 1 != this.m4kOutputStatus;
    }

    public void controlGraphicsOutputAll(boolean onoff) {
        this.mDisplayManager.controlGraphicsOutputAll(onoff);
    }

    public void switchDisplayOutputTo(int deviceId) {
        String deviceIdStr;
        int len = DEVICE_STRINGS_LIST.length;
        if (deviceId >= 0 && deviceId < len) {
            deviceIdStr = DEVICE_STRINGS_LIST[deviceId];
        } else {
            deviceIdStr = "DEVICE_ID_NONE";
        }
        this.mDisplayManager.switchDisplayOutputTo(deviceIdStr);
    }

    private static int retrieveDeviceId(String deviceIdStr) {
        int length = DEVICE_STRINGS_LIST.length;
        for (int i = 0; i < length; i++) {
            if (DEVICE_STRINGS_LIST[i].equals(deviceIdStr)) {
                int id = i;
                return id;
            }
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int obtainActiveDeviceId() {
        String device = this.mDisplayManager.getActiveDevice();
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), LOG_MSG_GETACTIVEDEVICE).append(device);
        Log.i(TAG, builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        int deviceId = retrieveDeviceId(device);
        if (-1 == deviceId) {
            builder.replace(0, builder.length(), LOG_MSG_ILLEGALDEVICE).append(device);
            Log.w(TAG, builder.toString());
            return 0;
        }
        return deviceId;
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

    public DisplayManager.DeviceStatus getActiveDeviceStatus() {
        DisplayManager.DeviceStatus status = this.mDisplayManager.getActiveDeviceStatus();
        return status;
    }

    public DisplayManager.DeviceInfo getDeviceInfo(String deviceId) {
        return this.mDisplayManager.getDeviceInfo(deviceId);
    }

    public DisplayManager.DeviceInfo getDeviceInfo(int deviceId) {
        if (-1 == this.mActiveDevice) {
            return null;
        }
        return this.mDisplayManager.getDeviceInfo(DEVICE_STRINGS_LIST[deviceId]);
    }

    public DisplayManager.DeviceInfo getActiveDeviceInfo() {
        return getDeviceInfo(this.mActiveDevice);
    }

    public int getOsdAspect(String deviceId) {
        if ("DEVICE_ID_HDMI".equals(deviceId)) {
            deviceId = "DEVICE_ID_PANEL";
        }
        DisplayManager.DeviceInfo info = this.mDisplayManager.getDeviceInfo(deviceId);
        if (info == null) {
            return 0;
        }
        return info.aspect;
    }

    public int getOsdAspect(int deviceId) {
        if (-1 == deviceId) {
            return 0;
        }
        if (2 == deviceId) {
            deviceId = 0;
        }
        DisplayManager.DeviceInfo info = this.mDisplayManager.getDeviceInfo(DEVICE_STRINGS_LIST[deviceId]);
        if (info != null) {
            return info.aspect;
        }
        return 0;
    }

    public int getActiveDeviceOsdAspect() {
        return getOsdAspect(this.mActiveDevice);
    }

    public boolean isPanelReverse() {
        int device = getActiveDevice();
        DisplayManager.DeviceStatus deviceStatus = getActiveDeviceStatus();
        if (device == -1) {
            device = 0;
        }
        int panelViewPattern = deviceStatus == null ? 4 : deviceStatus.viewPattern;
        return device == 0 && panelViewPattern == 1;
    }

    public int getOLEDBurningColor() {
        return -2130706433;
    }

    /* loaded from: classes.dex */
    class MyIdleHandler implements MessageQueue.IdleHandler {
        MyIdleHandler() {
        }

        @Override // android.os.MessageQueue.IdleHandler
        public boolean queueIdle() {
            DisplayModeObserver.this.notifyViewPattern();
            return false;
        }
    }

    @Deprecated
    public void notifyViewPattern() {
        notifySync(TAG_VIEW_PATTERN_CHANGE);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class OnDisplayEventListener implements DisplayManager.DisplayEventListener {
        OnDisplayEventListener() {
        }

        public void onDeviceStatusChanged(int eventId) {
            StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
            builder.replace(0, builder.length(), DisplayModeObserver.LOG_MSG_CANNOTGETLENSINFO).append(DisplayModeObserver.LOG_MSG_EVENTID).append(eventId);
            Log.i(DisplayModeObserver.TAG, builder.toString());
            StringBuilderThreadLocal.releaseScratchBuilder(builder);
            if (4096 == eventId) {
                int newDevice = DisplayModeObserver.this.obtainActiveDeviceId();
                String[] newDevices = DisplayModeObserver.this.mDisplayManager.getActiveDevices();
                if (DisplayModeObserver.this.mActiveDevice != newDevice || ((newDevices == null && DisplayModeObserver.this.mActiveDevices != null) || (newDevices != null && !newDevices.equals(DisplayModeObserver.this.mActiveDevices)))) {
                    DisplayModeObserver.this.mActiveDevice = newDevice;
                    DisplayModeObserver.this.mActiveDevices = newDevices;
                    PTag.start(DisplayModeObserver.PTAG_DISPCHANGE_DEVICE);
                    if (DisplayModeObserver.this.mIs4kOutputSupported) {
                        if (DisplayModeObserver.this.is4kDeviceAvailable()) {
                            Log.i(DisplayModeObserver.TAG, DisplayModeObserver.LOG_MSG_4K_DEVICE_CONNECTED);
                            if (DisplayModeObserver.this.mIsDialogShown4kOutputConnected && 1 == DisplayModeObserver.this.m4kOutputStatus && 4 == RunStatus.getStatus()) {
                                CautionUtilityClass.getInstance().requestTrigger(-1 == DisplayModeObserver.this.mCautionIdOn4kOutpuConnected ? 2913 : DisplayModeObserver.this.mCautionIdOn4kOutpuConnected);
                            }
                        } else if (2 == DisplayModeObserver.this.m4kOutputStatus || 6 == DisplayModeObserver.this.m4kOutputStatus) {
                            Log.i(DisplayModeObserver.TAG, DisplayModeObserver.LOG_MSG_4K_DEVICE_DISCONNECTED);
                            DisplayModeObserver.this.stop4kPlayback();
                        }
                    }
                    DisplayModeObserver.this.notify(DisplayModeObserver.TAG_DEVICE_CHANGE);
                    return;
                }
                return;
            }
            if (16384 == eventId) {
                DisplayModeObserver.this.notify(DisplayModeObserver.TAG_VIEW_PATTERN_CHANGE);
                Looper.myQueue().addIdleHandler(DisplayModeObserver.this.mIdleHandler);
                return;
            }
            if (DisplayModeObserver.is4kInformationSupportedByPF()) {
                int current = DisplayModeObserver.this.m4kOutputStatus;
                if (24576 == eventId) {
                    if (6 == DisplayModeObserver.this.m4kOutputStatus) {
                        DisplayManager.DeviceStatus status = DisplayModeObserver.this.getHdmiDeviceStatus();
                        if (status != null && status.format == 6) {
                            DisplayModeObserver.this.m4kOutputStatus = 2;
                        } else {
                            Log.w(DisplayModeObserver.TAG, "EVENT_START_QFHD_FORMAT_OUTPUT format is NOT QFHD");
                        }
                    } else {
                        Log.i(DisplayModeObserver.TAG, "EVENT_START_QFHD_FORMAT_OUTPUT when not starting : " + current);
                    }
                } else if (24832 == eventId) {
                    if (5 == DisplayModeObserver.this.m4kOutputStatus) {
                        DisplayModeObserver.this.m4kOutputStatus = 1;
                    } else {
                        Log.i(DisplayModeObserver.TAG, "EVENT_STOP_QFHD_FORMAT_OUTPUT when not stopping : " + current);
                    }
                }
                if (current != DisplayModeObserver.this.m4kOutputStatus) {
                    DisplayModeObserver.this.notify(DisplayModeObserver.TAG_4K_PLAYBACK_STATUS_CHANGE);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class OnVideoLayoutStatusListener implements DisplayManager.VideoLayoutStatusListener {
        OnVideoLayoutStatusListener() {
        }

        public void onLayoutChanged(DisplayManager.VideoRect rect) {
            StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
            builder.replace(0, builder.length(), DisplayModeObserver.LOG_MSG_ONLAYOUTCHANGED).append(DisplayModeObserver.LOG_MSG_RECT).append(" ( ").append(rect.pxLeft).append(", ").append(rect.pxTop).append(" ) ").append(" - ").append(" ( ").append(rect.pxRight).append(", ").append(rect.pxBottom).append(" ) ");
            Log.i(DisplayModeObserver.TAG, builder.toString());
            StringBuilderThreadLocal.releaseScratchBuilder(builder);
            DisplayManager.VideoRect unused = DisplayModeObserver.mYUVRect = rect;
            DisplayModeObserver.this.notify(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
        }
    }

    /* loaded from: classes.dex */
    class OnOleadScreenDisplayListener implements DisplayManager.OnScreenDisplayListener {
        OnOleadScreenDisplayListener() {
        }

        public void onPreventBurnInOLED(int arise) {
            if (arise == 0 || arise == 1) {
                DisplayModeObserver.this.notify("com.sony.imaging.app.base.common.DisplayModeObserver.OledScreenChange");
                DisplayModeObserver.this.mOleadScreenStatus = arise;
            } else {
                Log.e(DisplayModeObserver.TAG, DisplayModeObserver.LOG_MSG_ERROR_STR1);
            }
        }
    }

    protected int skipDigitalLevel(int appMode, int activeDevice, int dispModeCount, int dispModeIndex) {
        if (3 <= CameraSetting.getPfApiVersion() && appMode == 0) {
            int isSupporeted = ScalarProperties.getInt("device.digital.level");
            if (isSupporeted == 0) {
                int dispmode = this.mDispModeList[appMode][activeDevice][dispModeIndex];
                switch (dispmode) {
                    case 1:
                    case 3:
                    case 5:
                        return dispModeIndex;
                    case 2:
                    default:
                        return indexOf(this.mDispModeList[appMode][activeDevice], 1);
                    case 4:
                        return (dispModeIndex + 1) % dispModeCount;
                }
            }
            return dispModeIndex;
        }
        return dispModeIndex;
    }

    public static boolean is4kInformationSupportedByPF() {
        return 7 <= Environment.getVersionPfAPI();
    }

    public int get4kOutputStatus() {
        return this.m4kOutputStatus;
    }

    public boolean isChanging4kOutputMode() {
        return 4 == (this.m4kOutputStatus & 4);
    }

    public boolean start4kPlayback() {
        if (this.mIs4kOutputSupported) {
            StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
            builder.replace(0, builder.length(), LOG_MSG_START_QFHD_FORMAT).append(this.m4kOutputStatus);
            Log.d(TAG, builder.toString());
            StringBuilderThreadLocal.releaseScratchBuilder(builder);
            if (2 != this.m4kOutputStatus && 6 != this.m4kOutputStatus) {
                try {
                    this.mDisplayManager.startQfhdFormatOutput();
                    this.m4kOutputStatus = 6;
                    notify(TAG_4K_PLAYBACK_STATUS_CHANGE);
                    return true;
                } catch (RuntimeException e) {
                    Log.w(TAG, "RuntimeException occurs on startQfhdFormatOutput");
                    DisplayManager.DeviceStatus status = getHdmiDeviceStatus();
                    if (status.format == 6) {
                        this.m4kOutputStatus = 2;
                        notify(TAG_4K_PLAYBACK_STATUS_CHANGE);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean stop4kPlayback() {
        if (this.mIs4kOutputSupported) {
            StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
            builder.replace(0, builder.length(), LOG_MSG_STOP_QFHD_FORMAT).append(this.m4kOutputStatus);
            Log.d(TAG, builder.toString());
            StringBuilderThreadLocal.releaseScratchBuilder(builder);
            if (1 != this.m4kOutputStatus && 5 != this.m4kOutputStatus) {
                this.mDisplayManager.stopQfhdFormatOutput();
                this.m4kOutputStatus = 5;
                notify(TAG_4K_PLAYBACK_STATUS_CHANGE);
                return true;
            }
        }
        return false;
    }

    public void showDialogOn4kOutputConnected(boolean showIt) {
        this.mIsDialogShown4kOutputConnected = showIt;
    }

    public void setCautionIdOn4kOutpuConnected(int id) {
        this.mCautionIdOn4kOutpuConnected = id;
    }

    public void resetCautionIdOn4kOutpuConnected() {
        this.mCautionIdOn4kOutpuConnected = -1;
    }

    public DisplayManager.DeviceStatus getHdmiDeviceStatus() {
        DisplayManager.DeviceStatus[] status = this.mDisplayManager.getAllDevicesStatus();
        if (status != null) {
            int c = status.length;
            for (int i = 0; i < c; i++) {
                if ("DEVICE_ID_HDMI".equals(status[i].id) && status[i].active) {
                    return status[i];
                }
            }
        }
        return null;
    }

    public boolean is4kDeviceAvailable() {
        DisplayManager.DeviceStatus status;
        if (!this.mIs4kOutputSupported || (status = getHdmiDeviceStatus()) == null) {
            return false;
        }
        return status.qfhdFormatSupported;
    }
}
