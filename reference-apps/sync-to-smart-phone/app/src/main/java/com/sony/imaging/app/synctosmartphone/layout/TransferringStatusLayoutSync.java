package com.sony.imaging.app.synctosmartphone.layout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.shooting.widget.AppNameConstantView;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.synctosmartphone.R;
import com.sony.imaging.app.synctosmartphone.commonUtil.AppLog;
import com.sony.imaging.app.synctosmartphone.commonUtil.ConstantsSync;
import com.sony.imaging.app.synctosmartphone.commonUtil.SyncBackUpUtil;
import com.sony.imaging.app.synctosmartphone.database.AutoSyncDataBaseUtil;
import com.sony.scalar.lib.ssdpdevice.SsdpDevice;
import com.sony.scalar.sysutil.PlainTimeZone;
import com.sony.scalar.sysutil.TimeUtil;

/* loaded from: classes.dex */
public class TransferringStatusLayoutSync extends Layout {
    private static final String TAG = TransferringStatusLayoutSync.class.getSimpleName();
    private long lastSyncTime;
    TextView mButtonText;
    protected View mCurrentView;
    protected AppNameConstantView mScreenTitleView;
    private long numOfImages;
    private int syncErrorCode;

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.syncErrorCode = 0;
        this.syncErrorCode = SyncBackUpUtil.getInstance().getSyncError(this.syncErrorCode);
        this.lastSyncTime = 0L;
        this.lastSyncTime = SyncBackUpUtil.getInstance().getSyncDate(this.lastSyncTime);
        AutoSyncDataBaseUtil dbUtil = AutoSyncDataBaseUtil.getInstance();
        boolean bret = dbUtil.isNeedUpdateDatabase();
        if (bret) {
            dbUtil.updateDatabase(false);
        } else {
            dbUtil.importDatabase();
        }
        long lastStartTime = SyncBackUpUtil.getInstance().getStartUTC(0L);
        this.numOfImages = dbUtil.getNumberOfTransferReservationFiles(lastStartTime);
        if (this.mCurrentView == null) {
            if (this.syncErrorCode != 0) {
                this.mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.transferring_status_layout_panel);
            } else if (0 < this.numOfImages) {
                this.mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.transferring_status_layout_panel);
            } else {
                this.mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.transferring_status_2_layout_panel);
            }
        }
        createView();
        this.mButtonText = (TextView) this.mCurrentView.findViewById(R.id.button_text);
        this.mScreenTitleView = (AppNameConstantView) this.mCurrentView.findViewById(R.id.menu_screen_title);
        Log.d(TAG, "onCreateView = " + (this.mCurrentView != null));
        return this.mCurrentView;
    }

    private void createView() {
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        int stringId;
        super.onResume();
        this.mScreenTitleView.setVisibility(0);
        TextView transferred_date = (TextView) getView().findViewById(R.id.transferred_date_text);
        if (transferred_date != null) {
            if (this.syncErrorCode != 0) {
                transferred_date.setText(getLocalTimeString(this.lastSyncTime));
            } else {
                transferred_date.setText("");
            }
        }
        TextView message = (TextView) getView().findViewById(R.id.message_text);
        if (message != null) {
            if (this.syncErrorCode != 0) {
                switch (this.syncErrorCode) {
                    case 1:
                        stringId = R.string.STRID_FUNC_AUTOSYNC_ERROR_NOT_CONNECT;
                        break;
                    case 2:
                        stringId = R.string.STRID_FUNC_AUTOSYNC_ERROR_MEDIA_FULL;
                        break;
                    case 3:
                        stringId = R.string.STRID_FUNC_AUTOSYNC_ERROR_BAT_EXHAUSTED;
                        break;
                    case 4:
                        stringId = R.string.STRID_FUNC_AUTOSYNC_ERROR_TRANSFER_HALT;
                        break;
                    case 5:
                        stringId = R.string.STRID_FUNC_AUTOSYNC_ERROR_NO_RESPONCE;
                        break;
                    case 6:
                        stringId = R.string.STRID_FUNC_AUTOSYNC_ERROR_UNREGISTERED_SMARTPHONE_CONNECTED;
                        break;
                    case 9:
                        stringId = R.string.STRID_SCALARA_DLSYS_AIRPLAIN_MODE_ERROR;
                        break;
                    case ConstantsSync.CAUTION_HALT_DONE /* 257 */:
                        stringId = R.string.STRID_FUNC_AUTOSYNC_CAUTION_HALT_DONE;
                        break;
                    default:
                        stringId = R.string.STRID_FUNC_AUTOSYNC_TRANSFER_DONE;
                        break;
                }
                if (stringId != 0) {
                    message.setText(stringId);
                }
            } else if (0 < this.numOfImages) {
                message.setText(R.string.STRID_FUNC_AUTOSYNC_STATUS_EXIST_IMAGE);
            } else {
                message.setText(R.string.STRID_FUNC_AUTOSYNC_STATUS_NO_EXIST_IMAGE);
            }
        }
        TextView num_of_transferred = (TextView) getView().findViewById(R.id.number_of_transferred_text);
        if (num_of_transferred != null) {
            num_of_transferred.setText("");
        }
        TextView num_of_untransferred = (TextView) getView().findViewById(R.id.number_of_untransferred_text);
        if (num_of_untransferred != null) {
            if (0 < this.numOfImages) {
                String reserved = String.format(getString(R.string.STRID_FUNC_AUTOSYNC_RESERVED_IMAGE), Long.valueOf(this.numOfImages));
                num_of_untransferred.setText(reserved);
            } else {
                num_of_untransferred.setText("");
            }
        }
        this.mButtonText.setSelected(true);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        Log.d(TAG, "pushedS1Key");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        Log.d(TAG, "pushedS2Key");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        Log.d(TAG, "pushedMenuKey");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        Log.d(TAG, "pushedMovieRecKey");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        Log.d(TAG, "pushedCenterKey");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        Log.d(TAG, "pushedUpKey");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        Log.d(TAG, "pushedDownKey");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        Log.d(TAG, "pushedRightKey");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        Log.d(TAG, "pushedLeftKey");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial1ToRight() {
        Log.d(TAG, "turnedDial1ToRight");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial1ToLeft() {
        Log.d(TAG, "turnedDial1ToLeft");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial2ToRight() {
        Log.d(TAG, "turnedDial2ToRight");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial2ToLeft() {
        Log.d(TAG, "turnedDial2ToLeft");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial3ToRight() {
        Log.d(TAG, "turnedDial3ToRight");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial3ToLeft() {
        Log.d(TAG, "turnedDial3ToLeft");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteKey() {
        Log.d(TAG, "pushedDeleteKey");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        Log.d(TAG, "pushedPlayBackKey");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        Log.d(TAG, "pushedFnKey");
        return 0;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        this.mScreenTitleView.setVisibility(4);
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        this.mCurrentView = null;
        super.onDestroyView();
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        super.closeLayout();
    }

    private String getLocalTimeString(long utcTime) {
        AppLog.enter(TAG, AppLog.getMethodName());
        PlainTimeZone p = TimeUtil.getCurrentTimeZone();
        int diffGMT = p.gmtDiff;
        int diffSummerTime = p.summerTimeDiff;
        long localTime = utcTime + (diffGMT * 60 * SsdpDevice.RETRY_INTERVAL) + (diffSummerTime * 60 * SsdpDevice.RETRY_INTERVAL);
        AppLog.checkIf(TAG, "UTC(millis)      : " + utcTime);
        AppLog.checkIf(TAG, "localTime(millis): " + localTime);
        AppLog.exit(TAG, AppLog.getMethodName());
        return AutoSyncDataBaseUtil.getInstance().getFormatedTimeStamp(localTime);
    }
}
