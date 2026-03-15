package com.sony.imaging.app.manuallenscompensation.caution.layout;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionLayout;
import com.sony.imaging.app.base.caution.CautionProcessingFunction;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.manuallenscompensation.R;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil;

/* loaded from: classes.dex */
public class OCCautionLayout extends CautionLayout {
    private static final String TAG = "OCCautionLayout";
    private TextView mTextView = null;
    private View mView;

    @Override // com.sony.imaging.app.base.caution.CautionLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CautionProcessingFunction.setLayoutFactory(new CautionLayoutFactory());
        this.mView = super.onCreateView(inflater, container, savedInstanceState);
        if (131219 == this.cautionfunc.getCurrentCautionId()) {
            this.mTextView = (TextView) this.mView.findViewById(R.id.caution_text);
            String cautionText = OCUtil.getInstance().getLastExportedProfileText();
            this.mTextView.setText(cautionText);
            Log.d(TAG, "Caution 131219");
        }
        return this.mView;
    }

    @Override // com.sony.imaging.app.base.caution.CautionLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        this.mTextView = null;
    }

    @Override // com.sony.imaging.app.base.caution.CautionLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mView = null;
        this.mTextView = null;
    }

    @Override // com.sony.imaging.app.base.caution.CautionLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        int ret = super.onKeyUp(keyCode, event);
        if (131201 == this.cautionfunc.getCurrentCautionId()) {
            int scanCode = event.getScanCode();
            if (scanCode == 772 || scanCode == 516) {
                return 0;
            }
            return ret;
        }
        if (131074 == this.cautionfunc.getCurrentCautionId()) {
            int scanCode2 = event.getScanCode();
            if (scanCode2 == 772 || scanCode2 == 516) {
                return 0;
            }
            if (scanCode2 == 530 || scanCode2 == 786) {
                CautionUtilityClass.getInstance().disapperTrigger(Info.CAUTION_ID_DLAPP_INVALID_FUNCTION);
                return 0;
            }
            return ret;
        }
        return ret;
    }

    /* loaded from: classes.dex */
    private class CautionLayoutFactory implements CautionProcessingFunction.LayoutFactory {
        private CautionLayoutFactory() {
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.LayoutFactory
        public int getLayout(int layoutType, int cautionId, CautionProcessingFunction ref) {
            if (131219 != cautionId) {
                return -1;
            }
            return R.layout.layout_type_06_w_background_export_complete;
        }
    }
}
