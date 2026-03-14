package com.sony.imaging.app.doubleexposure.caution;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionLayout;
import com.sony.imaging.app.base.caution.CautionProcessingFunction;
import com.sony.imaging.app.doubleexposure.R;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureUtil;

/* loaded from: classes.dex */
public class DoubleExposureCautionLayout extends CautionLayout {
    private final String TAG = AppLog.getClassName();
    private View mCurrentView = null;
    private TextView mTxtVwOk = null;
    private TextView mTxtVwCancel = null;

    @Override // com.sony.imaging.app.base.caution.CautionLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        CautionProcessingFunction.setLayoutFactory(new CautionLayoutFactory());
        this.mCurrentView = super.onCreateView(inflater, container, savedInstanceState);
        if (131227 == this.cautionfunc.getCurrentCautionId()) {
            this.mTxtVwOk = (TextView) this.mCurrentView.findViewById(R.id.button_upper);
            if (this.mTxtVwOk != null) {
                this.mTxtVwOk.setText(android.R.string.display_manager_overlay_display_name);
                this.mTxtVwOk.setSelected(true);
            }
            this.mTxtVwCancel = (TextView) this.mCurrentView.findViewById(R.id.button_lower);
            if (this.mTxtVwCancel != null) {
                this.mTxtVwCancel.setText(android.R.string.config_customAdbWifiNetworkConfirmationSecondaryUserComponent);
                this.mTxtVwCancel.setSelected(1 == 0);
            }
            DoubleExposureUtil.getInstance().setButtonOk(this.mTxtVwOk);
            DoubleExposureUtil.getInstance().setButtonCancel(this.mTxtVwCancel);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.base.caution.CautionLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mCurrentView = null;
        this.mTxtVwOk = null;
        this.mTxtVwCancel = null;
        DoubleExposureUtil.getInstance().setButtonOk(null);
        DoubleExposureUtil.getInstance().setButtonCancel(null);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        super.onDestroy();
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

    /* loaded from: classes.dex */
    private class CautionLayoutFactory implements CautionProcessingFunction.LayoutFactory {
        private CautionLayoutFactory() {
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.LayoutFactory
        public int getLayout(int layoutType, int cautionId, CautionProcessingFunction ref) {
            AppLog.enter(DoubleExposureCautionLayout.this.TAG, AppLog.getMethodName());
            int retVal = -1;
            if (131227 == cautionId) {
                retVal = R.layout.aspect_ratio_changing_caution_layout;
            }
            AppLog.exit(DoubleExposureCautionLayout.this.TAG, AppLog.getMethodName());
            return retVal;
        }
    }
}
