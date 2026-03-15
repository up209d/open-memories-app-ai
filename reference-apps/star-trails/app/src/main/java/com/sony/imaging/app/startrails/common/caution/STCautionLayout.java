package com.sony.imaging.app.startrails.common.caution;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.backup.BackupReader;
import com.sony.imaging.app.base.caution.CautionLayout;
import com.sony.imaging.app.base.caution.CautionProcessingFunction;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.startrails.R;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STUtility;

/* loaded from: classes.dex */
public class STCautionLayout extends CautionLayout {
    private static final int REMAINING_MAX = 9999;
    private static String TAG = "STCautionLayout";
    private View mView;
    private TextView mTextView = null;
    private TextView mTextOk = null;
    private TextView mTextCancel = null;

    @Override // com.sony.imaging.app.base.caution.CautionLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CautionProcessingFunction.setLayoutFactory(new CautionLayoutFactory());
        this.mView = super.onCreateView(inflater, container, savedInstanceState);
        AppLog.enter(TAG, AppLog.getMethodName());
        if (131205 == this.cautionfunc.getCurrentCautionId()) {
            STUtility.getInstance().calculateRemainingMemory();
            int remainigShot = Math.min(STUtility.getInstance().getAvailableRemainingShot(), REMAINING_MAX);
            boolean isOk = BackupReader.getDeleteConfirmationDisplay() == BackupReader.ConfirmationCusorPosition.OK;
            this.mTextView = (TextView) this.mView.findViewById(R.id.caution_text);
            this.mTextOk = (TextView) this.mView.findViewById(R.id.button_ok);
            this.mTextCancel = (TextView) this.mView.findViewById(R.id.button_cancel);
            STUtility.getInstance().setOkFocusedOnRemainingMemoryCaution(isOk);
            if (this.mTextOk != null) {
                this.mTextOk.setSelected(isOk);
            }
            if (this.mTextCancel != null) {
                this.mTextCancel.setSelected(!isOk);
            }
            String cautionText = String.format(getResources().getString(R.string.STRID_FUNC_TIMELAPSE_CNF_1, Integer.valueOf(remainigShot)), new Object[0]);
            this.mTextView.setText(cautionText);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mView;
    }

    @Override // com.sony.imaging.app.base.caution.CautionLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        this.mTextView = null;
        this.mTextOk = null;
        this.mTextCancel = null;
    }

    private void toggleOKCancelFocusForRemainingMemory() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mTextOk != null && this.mTextCancel != null) {
            this.mTextOk.setSelected(!this.mTextOk.isSelected());
            this.mTextCancel.setSelected(this.mTextCancel.isSelected() ? false : true);
            STUtility.getInstance().setOkFocusedOnRemainingMemoryCaution(this.mTextOk.isSelected());
        } else if (this.mTextOk != null) {
            this.mTextOk.setSelected(true);
            this.mTextOk.requestFocus();
            STUtility.getInstance().setOkFocusedOnRemainingMemoryCaution(true);
        } else if (this.mTextCancel != null) {
            this.mTextCancel.setSelected(true);
            this.mTextCancel.requestFocus();
            STUtility.getInstance().setOkFocusedOnRemainingMemoryCaution(false);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.caution.CautionLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int returnState = 1;
        if (131205 == this.cautionfunc.getCurrentCautionId()) {
            switch (event.getScanCode()) {
                case 103:
                case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
                case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
                case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
                case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
                    toggleOKCancelFocusForRemainingMemory();
                    break;
                case AppRoot.USER_KEYCODE.LEFT /* 105 */:
                case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
                    break;
                case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
                case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
                case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
                case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                    toggleOKCancelFocusForRemainingMemory();
                    break;
                case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                    STUtility.getInstance().setPlayBackKeyPressed(true);
                    returnState = 0;
                    break;
                default:
                    returnState = super.onKeyDown(keyCode, event);
                    break;
            }
        } else if ((131211 == this.cautionfunc.getCurrentCautionId() || 131209 == this.cautionfunc.getCurrentCautionId()) && event.getScanCode() == 616) {
            returnState = 0;
        } else if (1399 == this.cautionfunc.getCurrentCautionId() && event.getScanCode() == 232) {
            CautionProcessingFunction.executeTerminate();
            returnState = 1;
        } else {
            returnState = super.onKeyDown(keyCode, event);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return returnState;
    }

    @Override // com.sony.imaging.app.base.caution.CautionLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int ret = super.onKeyUp(keyCode, event);
        int scanCode = event.getScanCode();
        if (scanCode == 772 || scanCode == 516) {
            ret = 0;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return ret;
    }

    @Override // com.sony.imaging.app.base.caution.CautionLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mView = null;
        this.mTextView = null;
        this.mTextOk = null;
        this.mTextCancel = null;
    }

    /* loaded from: classes.dex */
    private class CautionLayoutFactory implements CautionProcessingFunction.LayoutFactory {
        private CautionLayoutFactory() {
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.LayoutFactory
        public int getLayout(int layoutType, int cautionId, CautionProcessingFunction ref) {
            if (131205 == cautionId) {
                return R.layout.layout_type_04_wo_background_remaining_memory;
            }
            if (3289 != cautionId || !STUtility.getInstance().getCaptureStatus()) {
                return -1;
            }
            STUtility.getInstance().setCaptureStatus(false);
            return R.layout.layout_type_06_w_background;
        }
    }
}
