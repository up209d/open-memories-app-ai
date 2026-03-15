package com.sony.imaging.app.lightgraffiti.shooting.layout;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.lightgraffiti.R;
import com.sony.imaging.app.lightgraffiti.caution.LGInfo;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.lightgraffiti.util.LGConstants;

/* loaded from: classes.dex */
public class LGDiscardLayout extends Layout {
    private static final String TAG = LGDiscardLayout.class.getSimpleName();
    private TextView mCancel;
    private TextView mOk;

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = obtainViewFromPool(R.layout.lightgraffiti_layout_discard_lighttrail_dialogue);
        this.mOk = (TextView) v.findViewById(R.id.button_upper);
        if (this.mOk != null) {
            this.mOk.setText(android.R.string.phoneTypeOther);
            this.mOk.setSelected(false);
        }
        this.mCancel = (TextView) v.findViewById(R.id.button_lower);
        if (this.mCancel != null) {
            this.mCancel.setText(android.R.string.config_customAdbWifiNetworkConfirmationSecondaryUserComponent);
            this.mCancel.setSelected(0 == 0);
        }
        return v;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        FooterGuide guide = (FooterGuide) getView().findViewById(R.id.cmn_footer_guide);
        if (guide != null) {
            guide.setData(new FooterGuideDataResId(getActivity(), android.R.string.zen_mode_feature_name, android.R.string.httpErrorRedirectLoop));
        }
        TextView label = (TextView) getView().findViewById(R.id.dialog_message);
        if (label != null) {
            label.setText(R.string.STRID_FUNC_LIGHTGRAFFITI_DISCARD_DIALOGUE_TO_THE_FIRST_FROM_SECOND);
        }
        setKeyBeepPattern(5);
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
            CameraNotificationManager.getInstance().requestNotify(LGConstants.SHOOTING_2ND_CLOSE);
        }
        closeLayout();
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
        boolean handled = false;
        int scanCode = event.getScanCode();
        if ((scanCode == 515 || scanCode == 643 || scanCode == 616) && LGStateHolder.SHOOTING_2ND.equals(LGStateHolder.getInstance().getShootingStage())) {
            Log.d(TAG, "Movie Rec Key Pushed: " + scanCode);
            closeLayout();
            CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_CANNOT_REC_MOVIE);
            handled = true;
        }
        if (handled) {
            return 1;
        }
        int ret = super.onKeyDown(keyCode, event);
        if (ret == 0) {
            closeLayout();
            return ret;
        }
        return ret;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRShutterKey() {
        closeLayout();
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIR2SecKey() {
        closeLayout();
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        return super.pushedPlayBackKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedModeDial() {
        CautionUtilityClass.getInstance().requestTrigger(LGInfo.CAUTION_ID_DLAPP_FUNC_INV_APP);
        return super.turnedModeDial();
    }
}
