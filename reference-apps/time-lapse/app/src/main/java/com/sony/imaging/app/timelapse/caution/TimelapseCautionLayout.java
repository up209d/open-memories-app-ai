package com.sony.imaging.app.timelapse.caution;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.backup.BackupReader;
import com.sony.imaging.app.base.caution.CautionLayout;
import com.sony.imaging.app.base.caution.CautionProcessingFunction;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.HoldKeyServer;
import com.sony.imaging.app.timelapse.R;
import com.sony.imaging.app.timelapse.angleshift.common.AngleShiftConstants;
import com.sony.imaging.app.timelapse.angleshift.common.AngleShiftImageEditor;
import com.sony.imaging.app.timelapse.angleshift.common.AngleShiftSetting;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;

/* loaded from: classes.dex */
public class TimelapseCautionLayout extends CautionLayout {
    private static final int REMAINING_MAX = 9999;
    private static boolean isPushedOK = false;
    private View mView;
    private TextView mTextView = null;
    private TextView mTextOk = null;
    private TextView mTextCancel = null;

    @Override // com.sony.imaging.app.base.caution.CautionLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.gc();
        CautionProcessingFunction.setLayoutFactory(new CautionLayoutFactory());
        this.mView = super.onCreateView(inflater, container, savedInstanceState);
        this.mTextView = (TextView) this.mView.findViewById(R.id.caution_text);
        this.mTextOk = (TextView) this.mView.findViewById(R.id.button_ok);
        this.mTextCancel = (TextView) this.mView.findViewById(R.id.button_cancel);
        boolean isOk = BackupReader.getDeleteConfirmationDisplay() == BackupReader.ConfirmationCusorPosition.OK;
        int cautionId = this.cautionfunc.getCurrentCautionId();
        isPushedOK = false;
        if (131205 == cautionId) {
            TLCommonUtil.getInstance().calculateRemainingMemory();
            int remainigShot = Math.min(TLCommonUtil.getInstance().getAvailableRemainingShot(), REMAINING_MAX);
            TLCommonUtil.getInstance().setOkFocusedOnRemainingMemoryCaution(isOk);
            if (this.mTextOk != null) {
                this.mTextOk.setSelected(isOk);
            }
            if (this.mTextCancel != null) {
                this.mTextCancel.setSelected(!isOk);
            }
            String cautionText = String.format(getResources().getString(R.string.STRID_FUNC_TIMELAPSE_CNF_1, Integer.valueOf(remainigShot)), new Object[0]);
            this.mTextView.setText(cautionText);
        } else if (131223 == cautionId || 131225 == cautionId) {
            this.mTextOk.setSelected(isOk);
            this.mTextCancel.setSelected(isOk ? false : true);
            if (131225 == cautionId) {
                this.mTextOk.setText(R.string.STRID_FUNC_SELFIE_CL_ADJUST_CANCELSCREEN_CONFIRM);
                this.mTextCancel.setText(R.string.STRID_FUNC_SELFIE_CL_ADJUST_CANCELSCREEN_CANCEL);
            }
        } else if (131220 == cautionId) {
            String cautionText2 = String.format(getResources().getString(R.string.STRID_CAU_TIMELAPSE_EFFECT_REMAINING_CAPACITY, Integer.valueOf(AngleShiftImageEditor.getInstance().getRemainingSpace())), new Object[0]);
            this.mTextView.setText(cautionText2);
        } else if (131228 == cautionId) {
            String cautionText3 = getResources().getString(R.string.STRID_CAU_TIMELAPSE_ANGLE_SHIFT_INPUT);
            this.mTextView.setText(cautionText3);
        }
        return this.mView;
    }

    @Override // com.sony.imaging.app.base.caution.CautionLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        int cautionId = this.cautionfunc.getCurrentCautionId();
        if (!isPushedOK) {
            if (131218 == cautionId) {
                CameraNotificationManager.getInstance().requestNotify(AngleShiftConstants.TAG_AS_CANCEL_PREVIEW);
            } else if (131227 == cautionId) {
                CameraNotificationManager.getInstance().requestNotify(AngleShiftConstants.TAG_AS_CANCEL_SAVING_BY_ERROR);
            } else if (131225 == cautionId) {
                CameraNotificationManager.getInstance().requestNotify(AngleShiftConstants.TAG_AS_CONTINUE_SAVING);
                isPushedOK = true;
            }
        }
        super.onPause();
        this.mTextView = null;
        this.mTextOk = null;
        this.mTextCancel = null;
    }

    private void toggleOKCancelFocusForRemainingMemory() {
        if (this.mTextOk != null && this.mTextCancel != null) {
            this.mTextOk.setSelected(!this.mTextOk.isSelected());
            this.mTextCancel.setSelected(this.mTextCancel.isSelected() ? false : true);
            TLCommonUtil.getInstance().setOkFocusedOnRemainingMemoryCaution(this.mTextOk.isSelected());
        } else if (this.mTextOk != null) {
            this.mTextOk.setSelected(true);
            this.mTextOk.requestFocus();
            TLCommonUtil.getInstance().setOkFocusedOnRemainingMemoryCaution(true);
        } else if (this.mTextCancel != null) {
            this.mTextCancel.setSelected(true);
            this.mTextCancel.requestFocus();
            TLCommonUtil.getInstance().setOkFocusedOnRemainingMemoryCaution(false);
        }
    }

    private void toggleOKCancelFocusForASReset() {
        this.mTextOk.setSelected(!this.mTextOk.isSelected());
        this.mTextCancel.setSelected(this.mTextCancel.isSelected() ? false : true);
    }

    private void angleShiftHandleCenterKey() {
        int cautionId = this.cautionfunc.getCurrentCautionId();
        if (131223 == cautionId) {
            if (this.mTextOk.isSelected()) {
                AngleShiftSetting.getInstance().setAngleShiftCustomDefaultRect(true);
                AngleShiftSetting.getInstance().resetAngleShiftCustomRect();
                CameraNotificationManager.getInstance().requestNotify(AngleShiftConstants.TAG_AS_ROTATE);
                return;
            }
            return;
        }
        if (131218 == cautionId) {
            CameraNotificationManager.getInstance().requestNotify(AngleShiftConstants.TAG_AS_CANCEL_PREVIEW);
            isPushedOK = true;
        } else if (131227 == cautionId) {
            CameraNotificationManager.getInstance().requestNotify(AngleShiftConstants.TAG_AS_CANCEL_SAVING_BY_ERROR);
            isPushedOK = true;
        } else if (131225 == cautionId) {
            if (this.mTextOk.isSelected()) {
                CameraNotificationManager.getInstance().requestNotify(AngleShiftConstants.TAG_AS_CANCEL_SAVING_BY_MENU);
            } else {
                CameraNotificationManager.getInstance().requestNotify(AngleShiftConstants.TAG_AS_CONTINUE_SAVING);
            }
            isPushedOK = true;
        }
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:4:0x0016. Please report as an issue. */
    @Override // com.sony.imaging.app.base.caution.CautionLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int cautionId = this.cautionfunc.getCurrentCautionId();
        if (131205 == cautionId) {
            switch (event.getScanCode()) {
                case 103:
                case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
                case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
                case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
                case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
                    toggleOKCancelFocusForRemainingMemory();
                    return 1;
                case AppRoot.USER_KEYCODE.LEFT /* 105 */:
                case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
                    return 1;
                case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
                case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
                case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
                case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                    toggleOKCancelFocusForRemainingMemory();
                    return 1;
                case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                    if (TLCommonUtil.getInstance().isOkFocusedOnRemainingMemroryCaution()) {
                        isPushedOK = true;
                    }
                default:
                    int returnState = super.onKeyDown(keyCode, event);
                    return returnState;
            }
        } else {
            if (131223 == cautionId || 131218 == cautionId) {
                switch (event.getScanCode()) {
                    case 103:
                    case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                    case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
                    case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
                    case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
                    case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
                    case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
                    case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
                    case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
                    case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                        if (131223 != cautionId) {
                            return 1;
                        }
                        toggleOKCancelFocusForASReset();
                        return 1;
                    case AppRoot.USER_KEYCODE.LEFT /* 105 */:
                    case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
                        return 1;
                    case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                        angleShiftHandleCenterKey();
                        break;
                }
                int returnState2 = super.onKeyDown(keyCode, event);
                return returnState2;
            }
            if (131225 == cautionId || 131227 == cautionId) {
                switch (event.getScanCode()) {
                    case 103:
                    case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                    case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
                    case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
                    case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
                    case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
                    case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
                    case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
                    case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
                    case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                        if (131225 != cautionId) {
                            return 1;
                        }
                        toggleOKCancelFocusForASReset();
                        return 1;
                    case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                        angleShiftHandleCenterKey();
                        int returnState3 = super.onKeyDown(keyCode, event);
                        return returnState3;
                    default:
                        return 1;
                }
            }
            if (131220 == cautionId) {
                if (event.getRepeatCount() != 0 || !AngleShiftImageEditor.getInstance().checkBatteryStatus()) {
                    return 1;
                }
                int returnState4 = super.onKeyDown(keyCode, event);
                return returnState4;
            }
            if (1399 == cautionId && event.getScanCode() == 232) {
                CautionProcessingFunction.executeTerminate();
                return 1;
            }
            int returnState5 = super.onKeyDown(keyCode, event);
            return returnState5;
        }
    }

    @Override // com.sony.imaging.app.base.caution.CautionLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        int ret = super.onKeyUp(keyCode, event);
        int scanCode = event.getScanCode();
        if (scanCode == 772 || scanCode == 516) {
            return 0;
        }
        return ret;
    }

    @Override // com.sony.imaging.app.base.caution.CautionLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mTextView = null;
        this.mTextOk = null;
        this.mTextCancel = null;
        this.mView = null;
        if (!isPushedOK && 131205 == this.cautionfunc.getCurrentCautionId()) {
            HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.S1_ON, 0);
        }
        System.gc();
    }

    /* loaded from: classes.dex */
    private class CautionLayoutFactory implements CautionProcessingFunction.LayoutFactory {
        private CautionLayoutFactory() {
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.LayoutFactory
        public int getLayout(int layoutType, int cautionId, CautionProcessingFunction ref) {
            if (131205 != cautionId && 131223 != cautionId && 131225 != cautionId) {
                return -1;
            }
            return R.layout.layout_type_04_wo_background_remaining_memory;
        }
    }
}
