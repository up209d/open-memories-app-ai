package com.sony.imaging.app.liveviewgrading.menu.layout;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.liveviewgrading.R;
import com.sony.imaging.app.liveviewgrading.menu.controller.ColorGradingController;
import com.sony.imaging.app.liveviewgrading.menu.controller.LVGParameterValueController;

/* loaded from: classes.dex */
public class ColorGradingCurrentPresetResetConfirmationScreen extends Layout {
    private Callback callback;
    String itemId = null;
    TextView mCancel;
    TextView mOk;

    /* loaded from: classes.dex */
    public interface Callback {
        void onClose();

        void onFinish();
    }

    public void setCallback(Callback c) {
        this.callback = c;
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = obtainViewFromPool(R.layout.playback_exit_layout);
        this.mOk = (TextView) v.findViewById(R.id.button_upper);
        if (this.mOk != null) {
            this.mOk.setText(android.R.string.device_ownership_relinquished);
            this.mOk.setSelected(true);
        }
        this.mCancel = (TextView) v.findViewById(R.id.button_lower);
        if (this.mCancel != null) {
            this.mCancel.setText(android.R.string.config_customAdbWifiNetworkConfirmationSecondaryUserComponent);
            this.mCancel.setSelected(1 == 0);
        }
        return v;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        Bundle bundle = this.data;
        if (bundle != null) {
            this.itemId = bundle.getString("ID");
        }
        FooterGuide guide = (FooterGuide) getView().findViewById(R.id.cmn_footer_guide);
        if (guide != null) {
            guide.setData(new FooterGuideDataResId(getActivity(), android.R.string.zen_mode_feature_name, android.R.string.httpErrorRedirectLoop));
        }
        TextView label = (TextView) getView().findViewById(R.id.dialog_message);
        if (label != null && this.itemId != null) {
            if (this.itemId.equals("CURRENT_RESET")) {
                label.setText(getResources().getString(R.string.STRID_CAUTION_LVG_CONFIRM_CURRENT_PRESET_RESET));
            } else if (this.itemId.equals("ALL_RESET")) {
                label.setText(getResources().getString(R.string.STRID_CAUTION_LVG_CONFIRM_ALL_PRESETS_RESET));
            }
        }
        setKeyBeepPattern(5);
        ColorGradingController.getInstance().setPlayBackKeyPressedOnMenu(false);
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mOk = null;
        this.mCancel = null;
    }

    private void toggleSelection() {
        if (this.mOk != null && this.mCancel != null) {
            this.mOk.setSelected(!this.mOk.isSelected());
            this.mCancel.setSelected(this.mCancel.isSelected() ? false : true);
        } else if (this.mOk != null) {
            this.mOk.setSelected(true);
        } else if (this.mCancel != null) {
            this.mCancel.setSelected(true);
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        toggleSelection();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        toggleSelection();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        if (this.mOk != null && this.mOk.isSelected()) {
            if (this.itemId.equals("CURRENT_RESET")) {
                LVGParameterValueController.getInstance().resetCurrentPreset();
            } else if (this.itemId.equals("ALL_RESET")) {
                LVGParameterValueController.getInstance().resetAllPresets();
            }
            closeLayout();
            Log.e("", "RESET");
            return 1;
        }
        if (this.mCancel != null && this.mCancel.isSelected()) {
            closeLayout();
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        closeLayout();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        return pushedUpKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        return pushedDownKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        return pushedUpKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        return pushedDownKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialPrev() {
        return pushedUpKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialNext() {
        return pushedDownKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver
    protected String getKeyConvCategory() {
        return ICustomKey.CATEGORY_MENU;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        closeLayout();
        ColorGradingController.getInstance().setPlayBackKeyPressedOnMenu(false);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        switch (code) {
            case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                closeLayout();
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int ret = super.onKeyDown(keyCode, event);
        if (ret == 0) {
            closeLayout();
        }
        return ret;
    }
}
