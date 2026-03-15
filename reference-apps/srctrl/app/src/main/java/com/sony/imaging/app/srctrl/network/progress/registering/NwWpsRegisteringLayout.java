package com.sony.imaging.app.srctrl.network.progress.registering;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.srctrl.R;
import com.sony.imaging.app.srctrl.SRCtrl;
import com.sony.imaging.app.srctrl.network.NetworkRootState;

/* loaded from: classes.dex */
public class NwWpsRegisteringLayout extends Layout {
    private static AnimationDrawable aDrawable;
    ViewTreeObserver.OnGlobalLayoutListener listener = new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.sony.imaging.app.srctrl.network.progress.registering.NwWpsRegisteringLayout.1
        @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
        public void onGlobalLayout() {
            NwWpsRegisteringLayout.aDrawable.start();
        }
    };

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = obtainViewFromPool(R.layout.nw_layout_progress);
        return view;
    }

    protected int getResumeKeyBeepPattern() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        ((TextView) getView().findViewById(R.id.nw_parts_cmn_header_0_txt)).setText(SRCtrl.getAppStringId(getActivity()));
        ((TextView) getView().findViewById(R.id.nw_parts_prog_1_txt)).setVisibility(8);
        ImageView progressDot = (ImageView) getView().findViewById(R.id.nw_parts_prog_2_img);
        ((TextView) getView().findViewById(R.id.nw_parts_prog_3_txt)).setVisibility(8);
        if (NetworkRootState.getDirectTargetDeviceName() == null) {
            ((TextView) getView().findViewById(R.id.nw_parts_prog_4_txt)).setVisibility(8);
        } else {
            ((TextView) getView().findViewById(R.id.nw_parts_prog_4_txt)).setVisibility(0);
            ((TextView) getView().findViewById(R.id.nw_parts_prog_4_txt)).setText(NetworkRootState.getDirectTargetDeviceName());
        }
        ((TextView) getView().findViewById(R.id.nw_parts_prog_5_txt)).setText(R.string.STRID_AMC_STR_06084);
        Button button = (Button) getView().findViewById(R.id.nw_parts_cmn_button_0_button);
        if (button != null) {
            button.setFocusable(false);
            button.setText(android.R.string.config_customAdbWifiNetworkConfirmationSecondaryUserComponent);
            button.setVisibility(0);
        }
        FooterGuide guide = (FooterGuide) getView().findViewById(R.id.nw_parts_cmn_footer_0_footer_guide);
        if (guide != null) {
            guide.setData(new FooterGuideDataResId(getActivity(), android.R.string.years, 17042388));
        }
        aDrawable = (AnimationDrawable) getActivity().getResources().getDrawable(R.drawable.progress_dot_animation);
        progressDot.setImageDrawable(aDrawable);
        setKeyBeepPattern(getResumeKeyBeepPattern());
        ViewTreeObserver vtObserver = getView().getViewTreeObserver();
        vtObserver.addOnGlobalLayoutListener(this.listener);
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        getView().getViewTreeObserver().removeGlobalOnLayoutListener(this.listener);
        aDrawable.stop();
        aDrawable = null;
        super.onPause();
    }

    protected String getLogTag() {
        return getClass().getSimpleName();
    }

    protected NetworkRootState getRootContainer() {
        return (NetworkRootState) getData(NetworkRootState.PROP_ID_APP_ROOT);
    }
}
