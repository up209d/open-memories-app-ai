package com.sony.imaging.app.srctrl.network.standby.standby;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.srctrl.R;
import com.sony.imaging.app.srctrl.SRCtrl;
import com.sony.imaging.app.srctrl.network.NetworkRootState;
import com.sony.imaging.app.util.PTag;

/* loaded from: classes.dex */
public class NwStandbyLayout extends Layout {
    public static final String PTAG_SMART_REMOTE = "Smart Remote Control";
    private static AnimationDrawable aDrawable;
    ViewTreeObserver.OnGlobalLayoutListener listener = new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.sony.imaging.app.srctrl.network.standby.standby.NwStandbyLayout.1
        @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
        public void onGlobalLayout() {
            NwStandbyLayout.aDrawable.start();
        }
    };

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = obtainViewFromPool(R.layout.nw_layout_standby);
        return view;
    }

    protected int getResumeKeyBeepPattern() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        PTag.end("Smart Remote Control");
        super.onResume();
        ((TextView) getView().findViewById(R.id.nw_parts_cmn_header_0_txt)).setText(SRCtrl.getAppStringId(getActivity()));
        RelativeLayout r1 = (RelativeLayout) getView().findViewById(R.id.nw_parts_standby_1);
        ((TextView) r1.findViewById(R.id.nw_parts_standby_special_2_txt)).setText(getString(R.string.STRID_FUNC_NETWORK_SETTINGS_MSG_WIFI_STANDBY));
        FooterGuide guide = (FooterGuide) getView().findViewById(R.id.nw_parts_cmn_footer_0_footer_guide);
        if (guide != null) {
            guide.setData(new FooterGuideDataResId(getActivity(), android.R.string.hour, android.R.string.hour_picker_description));
        }
        ImageView progressDot = (ImageView) r1.findViewById(R.id.nw_parts_standby_special_1_img);
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
