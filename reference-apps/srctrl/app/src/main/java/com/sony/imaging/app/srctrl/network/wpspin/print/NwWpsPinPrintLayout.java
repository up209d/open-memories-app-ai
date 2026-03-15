package com.sony.imaging.app.srctrl.network.wpspin.print;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.srctrl.R;
import com.sony.imaging.app.srctrl.SRCtrl;
import com.sony.imaging.app.srctrl.network.NetworkRootState;

/* loaded from: classes.dex */
public class NwWpsPinPrintLayout extends Layout {
    private static final String TAG = NwWpsPinPrintLayout.class.getSimpleName();

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = obtainViewFromPool(R.layout.nw_layout_pin);
        return view;
    }

    protected int getResumeKeyBeepPattern() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        ((TextView) getView().findViewById(R.id.nw_parts_cmn_header_0_txt)).setText(SRCtrl.getAppStringId(getActivity()));
        String pin = NetworkRootState.getWpsPin();
        Log.v(TAG, pin);
        ((ImageView) getView().findViewById(R.id.nw_parts_pin_2_img)).setVisibility(8);
        TextView pinText = (TextView) getView().findViewById(R.id.nw_parts_pin_1_txt_l);
        pinText.setText(pin);
        pinText.setVisibility(0);
        ((TextView) getView().findViewById(R.id.nw_parts_pin_1_txt_m)).setVisibility(8);
        ((EditText) getView().findViewById(R.id.nw_parts_pin_2_edittxt)).setVisibility(8);
        ((TextView) getView().findViewById(R.id.nw_parts_pin_3_txt)).setText(R.string.STRID_INFO_PIN_CODE);
        ((TextView) getView().findViewById(R.id.nw_parts_pin_4_txt)).setText(NetworkRootState.getDirectTargetDeviceName());
        ((TextView) getView().findViewById(R.id.nw_parts_pin_5_txt)).setText(R.string.STRID_FUNC_SENDTOSMARTPHONE_MSG_DIRECT_PIN_OUTPUT_DSLR);
        Button button = (Button) getView().findViewById(R.id.nw_parts_cmn_button_0_button);
        if (button != null) {
            button.setVisibility(8);
        }
        FooterGuide guide = (FooterGuide) getView().findViewById(R.id.nw_parts_cmn_footer_0_footer_guide);
        if (guide != null) {
            guide.setData(new FooterGuideDataResId(getActivity(), android.R.string.zen_mode_feature_name, android.R.string.httpErrorRedirectLoop));
        }
        setKeyBeepPattern(getResumeKeyBeepPattern());
    }

    protected String getLogTag() {
        return getClass().getSimpleName();
    }

    protected NetworkRootState getRootContainer() {
        return (NetworkRootState) getData(NetworkRootState.PROP_ID_APP_ROOT);
    }
}
