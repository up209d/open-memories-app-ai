package com.sony.imaging.app.graduatedfilter.caution;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionLayout;
import com.sony.imaging.app.base.caution.CautionProcessingFunction;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.graduatedfilter.R;
import com.sony.imaging.app.graduatedfilter.common.GFCommonUtil;
import com.sony.imaging.app.graduatedfilter.common.GFConstants;
import com.sony.imaging.app.graduatedfilter.common.GFKikiLogUtil;

/* loaded from: classes.dex */
public class GFCautionLayout extends CautionLayout {
    private static View mView = null;
    private static TextView mTextView = null;
    private static TextView mTextOk = null;
    private static TextView mTextCancel = null;
    private static int mCautionID = 0;

    @Override // com.sony.imaging.app.base.caution.CautionLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CautionProcessingFunction.setLayoutFactory(new CautionLayoutFactory());
        mView = super.onCreateView(inflater, container, savedInstanceState);
        mTextView = (TextView) mView.findViewById(R.id.caution_text);
        mCautionID = this.cautionfunc.getCurrentCautionId();
        if (131078 == mCautionID) {
            mTextView.setText(17042272);
        } else if (131203 == mCautionID) {
            mTextView.setText(R.string.STRID_CAUTION_SKYND_COMFIRM_CURRENT_THEME_RESET);
            mTextOk = (TextView) mView.findViewById(R.id.button_ok);
            mTextCancel = (TextView) mView.findViewById(R.id.button_cancel);
            mTextOk.setSelected(false);
            mTextCancel.setSelected(true);
        } else if (131205 == mCautionID) {
            mTextView.setText(R.string.STRID_CAUTION_SKYND_SKYSETTINGS_INVALID);
        }
        return mView;
    }

    @Override // com.sony.imaging.app.base.caution.CautionLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
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
    }

    @Override // com.sony.imaging.app.base.caution.CautionLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        if (131203 == mCautionID) {
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
                case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                    if (mTextOk.isSelected()) {
                        GFCommonUtil.getInstance().resetParameters();
                        CameraNotificationManager.getInstance().requestNotify(GFConstants.RESET_COLOR_SETTING);
                        if (code == 516) {
                            GFCommonUtil.getInstance().needCTempSetting();
                            GFCommonUtil.getInstance().setCommonCameraSettings();
                            GFCommonUtil.getInstance().setBaseCameraSettings();
                        }
                        GFKikiLogUtil.getInstance().countParamReset();
                    }
                    int ret = super.onKeyDown(keyCode, event);
                    return ret;
                case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                    return -1;
                default:
                    int ret2 = super.onKeyDown(keyCode, event);
                    return ret2;
            }
        }
        switch (code) {
            case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                return -1;
            default:
                int ret3 = super.onKeyDown(keyCode, event);
                return ret3;
        }
    }

    /* loaded from: classes.dex */
    private class CautionLayoutFactory implements CautionProcessingFunction.LayoutFactory {
        private CautionLayoutFactory() {
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.LayoutFactory
        public int getLayout(int layoutType, int cautionId, CautionProcessingFunction ref) {
            if (131203 != cautionId) {
                return -1;
            }
            return R.layout.layout_type_04_wo_background_for_app;
        }
    }
}
