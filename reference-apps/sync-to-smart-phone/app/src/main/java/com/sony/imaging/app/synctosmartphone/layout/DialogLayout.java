package com.sony.imaging.app.synctosmartphone.layout;

import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.synctosmartphone.R;
import com.sony.imaging.app.synctosmartphone.commonUtil.ConstantsSync;
import com.sony.imaging.app.synctosmartphone.commonUtil.NetworkStateUtil;
import com.sony.imaging.app.synctosmartphone.commonUtil.SyncBackUpUtil;
import com.sony.imaging.app.synctosmartphone.database.AutoSyncDataBaseUtil;

/* loaded from: classes.dex */
public class DialogLayout extends Layout {
    protected View mCurrentView;
    protected int mCurrentViewId;
    private TextView mLower;
    protected RelativeLayout mSwitchLayout;
    private TextView mUpper;
    private static final String TAG = DialogLayout.class.getSimpleName();
    protected static final SparseArray<Integer> LAYOUT_LIST_FOR_PANEL = new SparseArray<>();
    private boolean mUpperButton = false;
    private int mToggleNumber = 1;
    private String mBeforeState = "";
    private int bootMode = 0;

    static {
        LAYOUT_LIST_FOR_PANEL.append(0, Integer.valueOf(R.layout.id1_1_dialog_layout_panel));
        LAYOUT_LIST_FOR_PANEL.append(1, Integer.valueOf(R.layout.id7_dialog_layout_panel));
        LAYOUT_LIST_FOR_PANEL.append(2, Integer.valueOf(R.layout.id8_dialog_layout_panel));
        LAYOUT_LIST_FOR_PANEL.append(3, Integer.valueOf(R.layout.id9_dialog_layout_panel));
        LAYOUT_LIST_FOR_PANEL.append(4, Integer.valueOf(R.layout.id11_dialog_layout_panel));
        LAYOUT_LIST_FOR_PANEL.append(5, Integer.valueOf(R.layout.id12_dialog_layout_panel));
        LAYOUT_LIST_FOR_PANEL.append(6, Integer.valueOf(R.layout.id15_dialog_layout_panel));
        LAYOUT_LIST_FOR_PANEL.append(7, Integer.valueOf(R.layout.id16_dialog_layout_panel));
        LAYOUT_LIST_FOR_PANEL.append(8, Integer.valueOf(R.layout.id17_dialog_layout_panel));
        LAYOUT_LIST_FOR_PANEL.append(9, Integer.valueOf(R.layout.id19_dialog_layout_panel));
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.mToggleNumber = this.data.getInt(ConstantsSync.DIALOG_ID);
        this.bootMode = this.data.getInt(ConstantsSync.BOOT_MODE);
        if (this.mToggleNumber == 0) {
            this.mBeforeState = this.data.getString(ConstantsSync.BEFORE_STATE);
        }
        createView();
        toggleSelection();
        return this.mSwitchLayout;
    }

    private void createView() {
        this.mCurrentViewId = getLayout();
        if (this.mCurrentView == null) {
            this.mCurrentView = (ViewGroup) obtainViewFromPool(this.mCurrentViewId);
            this.mSwitchLayout = new RelativeLayout(getActivity());
            this.mSwitchLayout.addView(this.mCurrentView);
        }
    }

    public int getLayout() {
        int layout = LAYOUT_LIST_FOR_PANEL.get(this.mToggleNumber).intValue();
        return layout;
    }

    private void toggleSelection() {
        Log.d(TAG, "toggleSelection = ");
        if (3 == this.mToggleNumber) {
            this.mUpperButton = true;
        } else {
            this.mUpperButton = false;
        }
        this.mUpper = (TextView) this.mSwitchLayout.findViewById(R.id.button_upper);
        if (this.mUpper != null) {
            this.mUpper.setSelected(this.mUpperButton);
        }
        this.mLower = (TextView) this.mSwitchLayout.findViewById(R.id.button_lower);
        if (this.mLower != null) {
            this.mLower.setSelected(!this.mUpperButton);
        }
        if (this.mUpper != null && this.mLower != null) {
            this.mUpper.setSelected(this.mUpper.isSelected());
            this.mLower.setSelected(this.mLower.isSelected());
        } else if (this.mUpper != null) {
            this.mUpper.setSelected(true);
        } else if (this.mLower != null) {
            this.mLower.setSelected(true);
        }
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        modifyView();
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void onReopened() {
        super.onReopened();
        Log.d(TAG, "onReopened <<<");
        this.mToggleNumber = this.data.getInt(ConstantsSync.DIALOG_ID);
        this.mSwitchLayout.removeAllViews();
        createView();
        this.mSwitchLayout.addView(obtainViewFromPool(this.mCurrentViewId));
        toggleSelection();
        modifyView();
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        deInitializeView();
        super.onDestroyView();
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
        setSelectedButton();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        Log.d(TAG, "pushedDownKey");
        setSelectedButton();
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
        setSelectedButton();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial1ToLeft() {
        Log.d(TAG, "turnedDial1ToLeft");
        setSelectedButton();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial2ToRight() {
        Log.d(TAG, "turnedDial2ToRight");
        setSelectedButton();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial2ToLeft() {
        Log.d(TAG, "turnedDial2ToLeft");
        setSelectedButton();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial3ToRight() {
        Log.d(TAG, "turnedDial3ToRight");
        setSelectedButton();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial3ToLeft() {
        Log.d(TAG, "turnedDial3ToLeft");
        setSelectedButton();
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

    private void setSelectedButton() {
        if (!NetworkStateUtil.getInstance().isCanceledTransferring() && (this.mToggleNumber == 1 || this.mToggleNumber == 3 || this.mToggleNumber == 4 || this.mToggleNumber == 7)) {
            this.mUpperButton = !this.mUpperButton;
            if (this.mUpper != null) {
                this.mUpper.setSelected(this.mUpperButton);
            }
            if (this.mLower != null) {
                this.mLower.setSelected(this.mUpperButton ? false : true);
            }
        }
        Log.d(TAG, "setSelctedButton mUpperButton = " + this.mUpperButton);
    }

    private void deInitializeView() {
        this.mCurrentView = null;
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        super.closeLayout();
    }

    private void modifyView() {
        int stringId;
        if (6 == this.mToggleNumber) {
            NetworkStateUtil.getInstance().end();
            TextView num_of_transferred = (TextView) getView().findViewById(R.id.number_of_transferred_text);
            TextView message = (TextView) getView().findViewById(R.id.message_text);
            TextView error_message = (TextView) getView().findViewById(R.id.error_message_text);
            int syncErrorCode = SyncBackUpUtil.getInstance().getSyncError(0);
            SyncBackUpUtil.getInstance().getSyncDate(0L);
            int totalCnt = this.data.getInt(ConstantsSync.SYNC_TOTAL_FILE);
            int downloadedCnt = this.data.getInt(ConstantsSync.SYNC_SENT_FILE);
            if (num_of_transferred != null) {
                String reserved = String.format(getString(R.string.STRID_FUNC_AUTOSYNC_TRANSFER_NUM), Integer.valueOf(downloadedCnt), Integer.valueOf(totalCnt));
                num_of_transferred.setText(reserved);
            }
            if (message != null) {
                switch (syncErrorCode) {
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
                    case 8:
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
                message.setText(stringId);
            }
            if (error_message != null) {
                if (syncErrorCode == 0) {
                    if (downloadedCnt == totalCnt) {
                        error_message.setText("");
                        return;
                    } else if (AutoSyncDataBaseUtil.getInstance().getNumOfIllegalDBEntries() > 0) {
                        error_message.setText(17042022);
                        return;
                    } else {
                        if (AutoSyncDataBaseUtil.getInstance().getNumOfIllegalDBFiles() > 0) {
                            error_message.setText(R.string.STRID_CAU_INVALID_OPERATION_NGFILE_ML);
                            return;
                        }
                        return;
                    }
                }
                error_message.setText("");
                return;
            }
            return;
        }
        if (this.mToggleNumber == 0) {
            TextView button = (TextView) getView().findViewById(R.id.button_lower);
            if (this.mBeforeState.equals(ConstantsSync.MAIN_STATE)) {
                button.setText(android.R.string.display_manager_overlay_display_name);
            } else if (this.mBeforeState.equals(ConstantsSync.REGISTERED_STATE)) {
                button.setText(R.string.STRID_AMC_STR_00911);
            }
        }
    }
}
