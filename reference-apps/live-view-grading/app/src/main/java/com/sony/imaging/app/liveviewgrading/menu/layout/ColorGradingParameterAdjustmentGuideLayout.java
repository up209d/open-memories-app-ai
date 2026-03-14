package com.sony.imaging.app.liveviewgrading.menu.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.menu.layout.BaseMenuLayout;
import com.sony.imaging.app.liveviewgrading.R;

/* loaded from: classes.dex */
public class ColorGradingParameterAdjustmentGuideLayout extends BaseMenuLayout {
    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View currentView = (ViewGroup) obtainViewFromPool(R.layout.parameter_adjustment_guide_layout);
        TextView functionName = (TextView) currentView.findViewById(R.id.function_name);
        TextView functionGuide = (TextView) currentView.findViewById(R.id.guide);
        Bundle bundle = this.data;
        if (bundle != null) {
            String funcName = bundle.getString("param_name");
            String funcGuide = bundle.getString("param_guide");
            functionName.setText(funcName);
            functionGuide.setText(funcGuide);
        }
        return currentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        openMenuLayout("ID_COLORGRADINGPRESETADJUSTMENTSCREEN", this.data);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        openMenuLayout("ID_COLORGRADINGPRESETADJUSTMENTSCREEN", this.data);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK1Key() {
        return pushedMenuKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDeleteKeyPushed(KeyEvent event) {
        return pushedMenuKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDeleteKeyReleased(KeyEvent event) {
        return -1;
    }
}
