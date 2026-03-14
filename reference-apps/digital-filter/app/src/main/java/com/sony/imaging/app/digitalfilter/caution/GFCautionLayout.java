package com.sony.imaging.app.digitalfilter.caution;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionLayout;
import com.sony.imaging.app.base.caution.CautionProcessingFunction;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.common.GFKikiLogUtil;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFAELController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFWhiteBalanceController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomKeyMgr;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.HoldKeyServer;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.fw.IKeyFunction;
import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
public class GFCautionLayout extends CautionLayout {
    private static View mView = null;
    private static TextView mTextView = null;
    private static TextView mTextOk = null;
    private static TextView mTextCancel = null;
    private static int mCautionID = 0;
    public static boolean isPushedOK = false;
    private static final List<Integer> mLayoutTyep04woBG = Arrays.asList(Integer.valueOf(GFInfo.CAUTION_ID_DLAPP_RESET_PARAM), Integer.valueOf(GFInfo.CAUTION_ID_DLAPP_REMAINING_MEMORY_LESS), Integer.valueOf(GFInfo.CAUTION_ID_DLAPP_BOUNDARY_INIT_MSG));

    private void setTextView() {
        if (131203 == mCautionID) {
            mTextView.setText(R.string.STRID_CAUTION_SKYND_COMFIRM_CURRENT_THEME_RESET);
        } else if (131250 == mCautionID) {
            int remainigShot = GFCommonUtil.getInstance().getAvailableRemainingShot();
            GFCommonUtil.getInstance().setOkFocusedOnRemainingMemoryCaution(false);
            String cautionText = String.format(getResources().getString(R.string.STRID_FUNC_TIMELAPSE_CNF_1, Integer.valueOf(remainigShot)), new Object[0]);
            mTextView.setText(cautionText);
        }
    }

    @Override // com.sony.imaging.app.base.caution.CautionLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CautionProcessingFunction.setLayoutFactory(new CautionLayoutFactory());
        mView = super.onCreateView(inflater, container, savedInstanceState);
        mTextView = (TextView) mView.findViewById(R.id.caution_text);
        mCautionID = this.cautionfunc.getCurrentCautionId();
        isPushedOK = false;
        if (131078 == mCautionID) {
            mTextView.setText(17042272);
        } else if (mLayoutTyep04woBG.contains(Integer.valueOf(mCautionID))) {
            setTextView();
            mTextOk = (TextView) mView.findViewById(R.id.button_ok);
            mTextCancel = (TextView) mView.findViewById(R.id.button_cancel);
            mTextOk.setSelected(false);
            mTextCancel.setSelected(true);
        } else if (131205 == mCautionID) {
            mTextView.setText(R.string.STRID_FUNC_SKYND_INVALID_FUNC);
        } else if (131226 == mCautionID) {
            TextView title = (TextView) mView.findViewById(R.id.reason_title);
            TextView value = (TextView) mView.findViewById(R.id.reason_value);
            title.setText(R.string.STRID_FUNC_DF_FILTERSETS);
            value.setText(R.string.STRID_FUNC_DF_2AREA);
        } else if (131214 == mCautionID) {
            if (GFWhiteBalanceController.getInstance().getSupportedValue(WhiteBalanceController.WHITEBALANCE).contains(WhiteBalanceController.UNDERWATER_AUTO)) {
                mTextView.setText(R.string.STRID_CAUTION_DF_AWB_BY_FLASH);
            } else {
                mTextView.setText(R.string.STRID_CAUTION_DF_AWB_BY_FLASH2);
            }
        } else if (131227 == mCautionID) {
            mTextView.setText(R.string.STRID_FUNC_DF_TOUCHLESS_SHOOTING_CAUTION);
        }
        return mView;
    }

    @Override // com.sony.imaging.app.base.caution.CautionLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        if (!isPushedOK && 131250 == mCautionID) {
            HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.S1_ON, 0);
        }
        super.onDestroyView();
        System.gc();
    }

    @Override // com.sony.imaging.app.base.caution.CautionLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        mTextView = null;
        mTextOk = null;
        mTextCancel = null;
        mView = null;
    }

    private void toggleButtons() {
        boolean isOk = !mTextOk.isSelected();
        mTextOk.setSelected(isOk);
        mTextCancel.setSelected(isOk ? false : true);
        GFCommonUtil.getInstance().setOkFocusedOnRemainingMemoryCaution(isOk);
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:4:0x0016. Please report as an issue. */
    @Override // com.sony.imaging.app.base.caution.CautionLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        if (131250 == mCautionID) {
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
                    toggleButtons();
                    return 1;
                case AppRoot.USER_KEYCODE.LEFT /* 105 */:
                case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
                    return 1;
                case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                    if (131250 == mCautionID && GFCommonUtil.getInstance().isOkFocusedOnRemainingMemroryCaution()) {
                        isPushedOK = true;
                    }
                    break;
                default:
                    int ret = super.onKeyDown(keyCode, event);
                    return ret;
            }
        } else {
            if (1399 == mCautionID && event.getScanCode() == 232) {
                CautionProcessingFunction.executeTerminate();
                return 1;
            }
            if (131203 == mCautionID || 131228 == mCautionID) {
                switch (code) {
                    case 103:
                    case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
                    case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
                    case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
                    case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
                        toggleButtons();
                        return 1;
                    case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                    case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
                    case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
                    case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
                    case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                        toggleButtons();
                        return 1;
                    case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                        if (mTextOk.isSelected()) {
                            if (131203 == mCautionID) {
                                GFCommonUtil.getInstance().resetParameters();
                                CameraNotificationManager.getInstance().requestNotify(GFConstants.RESET_COLOR_SETTING);
                                GFKikiLogUtil.getInstance().countParamReset();
                            } else if (131228 == mCautionID) {
                                CameraNotificationManager.getInstance().requestNotify(GFConstants.RESET_BOUNDARY_SETTING);
                            }
                        }
                        int ret2 = super.onKeyDown(keyCode, event);
                        return ret2;
                    case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                        return -1;
                    default:
                        int ret3 = super.onKeyDown(keyCode, event);
                        return ret3;
                }
            }
            switch (code) {
                case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                    return -1;
                default:
                    int ret4 = super.onKeyDown(keyCode, event);
                    return ret4;
            }
        }
    }

    @Override // com.sony.imaging.app.base.caution.CautionLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        ICustomKey key = CustomKeyMgr.getInstance().get(code);
        IKeyFunction func = key.getAssigned("Menu");
        if ((func.equals(CustomizableFunction.AelHold) || code == 638) && GFAELController.getInstance().isAELockByAELHold()) {
            GFAELController.getInstance().holdAELock(false);
        }
        if (131203 == mCautionID) {
            return -1;
        }
        return super.onKeyUp(keyCode, event);
    }

    /* loaded from: classes.dex */
    private class CautionLayoutFactory implements CautionProcessingFunction.LayoutFactory {
        private CautionLayoutFactory() {
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.LayoutFactory
        public int getLayout(int layoutType, int cautionId, CautionProcessingFunction ref) {
            if (GFCautionLayout.mLayoutTyep04woBG.contains(Integer.valueOf(cautionId))) {
                return R.layout.layout_type_04_wo_background_remaining_memory;
            }
            if (131203 != cautionId) {
                return -1;
            }
            return R.layout.layout_type_04_wo_background_for_app;
        }
    }
}
