package com.sony.imaging.app.srctrl.network.waiting.waiting;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.srctrl.R;
import com.sony.imaging.app.srctrl.SRCtrl;
import com.sony.imaging.app.srctrl.network.NetworkRootState;
import com.sony.imaging.app.srctrl.util.SRCtrlConstants;
import com.sony.imaging.app.srctrl.util.UtilPFWorkaround;

/* loaded from: classes.dex */
public class NwWaitingLayout extends Layout {
    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = obtainViewFromPool(R.layout.nw_layout_waiting);
        return view;
    }

    protected int getResumeKeyBeepPattern() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        String deviceName;
        super.onResume();
        ((TextView) getView().findViewById(R.id.nw_parts_cmn_header_0_txt)).setText(SRCtrl.getAppStringId(getActivity()));
        ((TextView) getView().findViewById(R.id.nw_parts_wait_0_txt)).setText(R.string.STRID_FUNC_SENDTOSMARTPHONE_MSG_WAITING2);
        RelativeLayout r2 = (RelativeLayout) getView().findViewById(R.id.nw_parts_wait_2);
        ((TextView) r2.findViewById(R.id.nw_parts_wait_special_1_txt)).setText(getString(R.string.STRID_CMN_SSID));
        ((TextView) r2.findViewById(R.id.nw_parts_wait_special_1_txt)).setTypeface(Typeface.UNIVERS);
        ((TextView) r2.findViewById(R.id.nw_parts_wait_special_2_txt)).setText(NetworkRootState.getMySsid());
        RelativeLayout r3 = (RelativeLayout) getView().findViewById(R.id.nw_parts_wait_3);
        ((TextView) r2.findViewById(R.id.nw_parts_wait_special_1_txt)).setTypeface(Typeface.DEFAULT);
        ((TextView) r3.findViewById(R.id.nw_parts_wait_special_1_txt)).setText(getString(R.string.STRID_CMN_PASSWORD));
        ((TextView) r3.findViewById(R.id.nw_parts_wait_special_2_txt)).setText(NetworkRootState.getMyPsk());
        RelativeLayout r4 = (RelativeLayout) getView().findViewById(R.id.nw_parts_wait_4);
        ((TextView) r2.findViewById(R.id.nw_parts_wait_special_1_txt)).setTypeface(Typeface.DEFAULT);
        ((TextView) r4.findViewById(R.id.nw_parts_wait_special_1_txt)).setText(getString(R.string.STRID_CMN_WIFI_DEVICENAME_DSLR));
        if (UtilPFWorkaround.isDeviceNameModifyNeeded()) {
            String ssid = NetworkRootState.getMySsid();
            int ssidPrefixIndex = ssid.indexOf(SRCtrlConstants.SSID_STRING_DIRECT) + SRCtrlConstants.SSID_STRING_DIRECT.length();
            deviceName = ssid.substring(ssidPrefixIndex + 5, ssid.length());
        } else {
            deviceName = NetworkRootState.getMyDeviceName();
        }
        ((TextView) r4.findViewById(R.id.nw_parts_wait_special_2_txt)).setText(deviceName);
        FooterGuide guide = (FooterGuide) getView().findViewById(R.id.nw_parts_cmn_footer_0_footer_guide);
        if (guide != null) {
            guide.setData(new FooterGuideDataResId(getActivity(), R.string.STRID_FUNC_FOOTER_GUIDE_GOTO_QRCODE, R.string.STRID_FUNC_FOOTER_GUIDE_GOTO_QRCODE_NEX));
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
