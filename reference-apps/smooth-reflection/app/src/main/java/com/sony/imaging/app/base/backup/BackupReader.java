package com.sony.imaging.app.base.backup;

import android.util.Log;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.sysutil.didep.Settings;

/* loaded from: classes.dex */
public class BackupReader {
    private static final int DELETE_CONF_SUPPORTED_VERSION = 2;
    private static final int INDEX_PLAYBACK_DISP_NUMBER_12 = 12;
    private static final String MSG_PREFIX_DATE_FORMAT = "DateFormat setting = ";
    private static final String MSG_PREFIX_DELETE_CONFIRMATION = "Delete Confirmation setting = ";
    private static final String MSG_PREFIX_INDEX_NUM = "getIndexThumbNum = ";
    private static final String MSG_PREFIX_VPICDISPLAY = "AutoRotation setting = ";
    private static final String MSG_PREFIX_WIDEDISPLAY = "WideImageLayout setting = ";
    private static final String TAG = "BackupReader";
    private static StringBuilder mStrBuilder = new StringBuilder();
    private static IndexPlaybackMode mIndexPlaybackmode = IndexPlaybackMode.INVALID;
    private static RecordedDateMode mRecordedDateMode = RecordedDateMode.INVALID;
    private static VPicDisplay mVPicDisplay = VPicDisplay.INVALID;
    private static WideDisplay mWideDisplay = WideDisplay.INVALID;
    private static ConfirmationCusorPosition mDeleteConfirmationCursor = ConfirmationCusorPosition.INVALID;

    /* loaded from: classes.dex */
    public enum ConfirmationCusorPosition {
        OK,
        CANCEL,
        INVALID
    }

    /* loaded from: classes.dex */
    public enum DispState {
        OFF,
        ON,
        HISTOGRAM
    }

    /* loaded from: classes.dex */
    public enum IndexPlaybackMode {
        SIX,
        TWELVE,
        INVALID
    }

    /* loaded from: classes.dex */
    public enum RecordedDateMode {
        YY_MM_DD,
        DD_MM_YY,
        MM_DD_YY_ENG,
        MM_DD_YY_NUM,
        INVALID
    }

    /* loaded from: classes.dex */
    public enum VPicDisplay {
        PORTRAIT,
        LANDSCAPE,
        INVALID
    }

    /* loaded from: classes.dex */
    public enum WideDisplay {
        FULL,
        STANDARD,
        INVALID
    }

    public static void reset() {
        resetIndexPlaybackMode();
        resetRecordedDateMode();
        resetVPicDisplay();
        resetWideDisplay();
        resetConfirmationCusorPosition();
    }

    public static IndexPlaybackMode getIndexPlaybackMode() {
        if (IndexPlaybackMode.INVALID == mIndexPlaybackmode) {
            int num = Settings.getIndexThumbNum();
            Log.i(TAG, mStrBuilder.replace(0, mStrBuilder.length(), MSG_PREFIX_INDEX_NUM).append(num).toString());
            mIndexPlaybackmode = num == 12 ? IndexPlaybackMode.TWELVE : IndexPlaybackMode.SIX;
        }
        return mIndexPlaybackmode;
    }

    public static void resetIndexPlaybackMode() {
        mIndexPlaybackmode = IndexPlaybackMode.INVALID;
    }

    @Deprecated
    public static void setIndexPlaybackMode(IndexPlaybackMode indexPlaybackMode) {
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:4:0x0026. Please report as an issue. */
    public static RecordedDateMode getRecordedDateMode() {
        if (RecordedDateMode.INVALID == mRecordedDateMode) {
            int dateFormat = Settings.getDateFormat();
            Log.i(TAG, mStrBuilder.replace(0, mStrBuilder.length(), MSG_PREFIX_DATE_FORMAT).append(dateFormat).toString());
            switch (dateFormat) {
                case 0:
                    mRecordedDateMode = RecordedDateMode.YY_MM_DD;
                    break;
                case 1:
                    mRecordedDateMode = RecordedDateMode.MM_DD_YY_ENG;
                    break;
                case 2:
                    mRecordedDateMode = RecordedDateMode.MM_DD_YY_NUM;
                    break;
                case 3:
                    mRecordedDateMode = RecordedDateMode.DD_MM_YY;
                    break;
                default:
                    return null;
            }
        }
        return mRecordedDateMode;
    }

    public static void resetRecordedDateMode() {
        mRecordedDateMode = RecordedDateMode.INVALID;
    }

    @Deprecated
    public static void setRecordedDateMode(RecordedDateMode recordedDateMode) {
    }

    public static VPicDisplay getVPicDisplay() {
        if (VPicDisplay.INVALID == mVPicDisplay) {
            int autoRotate = Settings.getAutoRotate();
            Log.i(TAG, mStrBuilder.replace(0, mStrBuilder.length(), MSG_PREFIX_VPICDISPLAY).append(autoRotate).toString());
            mVPicDisplay = autoRotate == 1 ? VPicDisplay.LANDSCAPE : VPicDisplay.PORTRAIT;
        }
        return mVPicDisplay;
    }

    public static void resetVPicDisplay() {
        mVPicDisplay = VPicDisplay.INVALID;
    }

    @Deprecated
    public static void setVPicDisplay(VPicDisplay vPicDisplay) {
    }

    public static WideDisplay getWideDisplay() {
        if (WideDisplay.INVALID == mWideDisplay) {
            int wideImage = Settings.getWideImageLayout();
            Log.i(TAG, mStrBuilder.replace(0, mStrBuilder.length(), MSG_PREFIX_WIDEDISPLAY).append(wideImage).toString());
            mWideDisplay = wideImage == 2 ? WideDisplay.FULL : WideDisplay.STANDARD;
        }
        return mWideDisplay;
    }

    public static void resetWideDisplay() {
        mWideDisplay = WideDisplay.INVALID;
    }

    @Deprecated
    public static void setWideDisplay(WideDisplay wideDisplay) {
    }

    public static ConfirmationCusorPosition getDeleteConfirmationDisplay() {
        if (Environment.getVersionPfAPI() < 2) {
            return ConfirmationCusorPosition.CANCEL;
        }
        if (ConfirmationCusorPosition.INVALID == mDeleteConfirmationCursor) {
            int cursorPosition = Settings.getDeleteConfirmationMode();
            Log.i(TAG, mStrBuilder.replace(0, mStrBuilder.length(), MSG_PREFIX_DELETE_CONFIRMATION).append(cursorPosition).toString());
            mDeleteConfirmationCursor = cursorPosition == 0 ? ConfirmationCusorPosition.OK : ConfirmationCusorPosition.CANCEL;
        }
        return mDeleteConfirmationCursor;
    }

    public static void resetConfirmationCusorPosition() {
        mDeleteConfirmationCursor = ConfirmationCusorPosition.INVALID;
    }
}
