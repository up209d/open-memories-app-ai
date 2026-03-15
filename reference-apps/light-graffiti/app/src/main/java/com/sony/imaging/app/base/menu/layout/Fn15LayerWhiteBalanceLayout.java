package com.sony.imaging.app.base.menu.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;

/* loaded from: classes.dex */
public class Fn15LayerWhiteBalanceLayout extends Fn15LayerMenuLayout {
    public static final String MENU_ID = "ID_FN15LAYERWHITEBALANCELAYOUT";
    private TextView mAbGmTextView;
    private StringBuilder mBuilder = new StringBuilder();
    private TextView mTempTextView;

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup currentView = (ViewGroup) obtainViewFromPool(R.layout.menu_fn15layer_whitebalance);
        this.mAbGmTextView = (TextView) currentView.findViewById(R.id.wb_fn_text_abgm);
        this.mTempTextView = (TextView) currentView.findViewById(R.id.wb_fn_text_color);
        return view;
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout
    protected int getLayoutID() {
        return R.layout.menu_fn15layer_whitebalance;
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout, com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        postSetValue();
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void postSetValue() {
        WhiteBalanceController wbc = WhiteBalanceController.getInstance();
        String value = wbc.getValue();
        if ("custom1".equals(value) || "custom2".equals(value) || "custom3".equals(value)) {
            wbc.setCustomWhiteBalance(value);
        }
        this.mBuilder.replace(0, this.mBuilder.length(), makeWBOptionText(value, WhiteBalanceController.LIGHT));
        this.mBuilder.append(ExposureModeController.SOFT_SNAP);
        this.mAbGmTextView.setText(this.mBuilder.append(makeWBOptionText(value, WhiteBalanceController.COMP)));
        if (WhiteBalanceController.COLOR_TEMP.equals(value) || WhiteBalanceController.CUSTOM.equals(value) || "custom1".equals(value) || "custom2".equals(value) || "custom3".equals(value)) {
            this.mTempTextView.setText(makeWBOptionText(value, WhiteBalanceController.TEMP));
            this.mTempTextView.setVisibility(0);
        } else {
            this.mTempTextView.setVisibility(4);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        this.mAbGmTextView = null;
        this.mTempTextView = null;
        super.onDestroyView();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        this.mBuilder.setLength(0);
        this.mBuilder.trimToSize();
        super.onDestroy();
    }

    protected String makeWBOptionText(String value, String option) {
        String ret = "";
        if (WhiteBalanceController.CUSTOM_SET.equals(value)) {
            return "";
        }
        WhiteBalanceController.WhiteBalanceParam wbparam = (WhiteBalanceController.WhiteBalanceParam) WhiteBalanceController.getInstance().getDetailValue();
        if (wbparam != null) {
            if (WhiteBalanceController.TEMP.equals(option)) {
                ret = wbparam.getColorTemp() + WhiteBalanceController.DISP_TEXT_K;
            } else if (WhiteBalanceController.LIGHT.equals(option)) {
                if (wbparam.getLightBalance() > 0) {
                    ret = WhiteBalanceController.DISP_TEXT_AB_PLUS + Math.abs(wbparam.getLightBalance());
                } else if (wbparam.getLightBalance() == 0) {
                    ret = WhiteBalanceController.DISP_TEXT_AB_ZERO + Math.abs(wbparam.getLightBalance());
                } else {
                    ret = WhiteBalanceController.DISP_TEXT_AB_MINUS + Math.abs(wbparam.getLightBalance());
                }
            } else if (WhiteBalanceController.COMP.equals(option)) {
                if (wbparam.getColorComp() > 0) {
                    ret = WhiteBalanceController.DISP_TEXT_GM_PLUS + Math.abs(wbparam.getColorComp());
                } else if (wbparam.getColorComp() == 0) {
                    ret = WhiteBalanceController.DISP_TEXT_GM_ZERO + Math.abs(wbparam.getColorComp());
                } else {
                    ret = WhiteBalanceController.DISP_TEXT_GM_MINUS + Math.abs(wbparam.getColorComp());
                }
            }
            return ret;
        }
        return "No Parameters";
    }
}
