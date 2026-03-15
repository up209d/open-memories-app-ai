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
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGMenuDataInitializer;
import com.sony.imaging.app.lightgraffiti.shooting.state.LGDiscardLightTrailDialogueState;
import com.sony.imaging.app.lightgraffiti.util.LGConstants;
import com.sony.imaging.app.lightgraffiti.util.LGUtility;

/* loaded from: classes.dex */
public class LGDiscardLightTrailDialogueLayout extends Layout {
    private static final String TAG = LGDiscardLightTrailDialogueLayout.class.getSimpleName();
    private Callback callback;
    TextView mCancel;
    TextView mOk;

    /* loaded from: classes.dex */
    public interface Callback {
        void onClose();

        void onFinish();
    }

    public void setCallback(Callback c) {
        if (c != null) {
            Log.d(TAG, "Set call back: " + c.getClass().getSimpleName());
        } else {
            Log.d(TAG, "Set call back: null");
        }
        this.callback = c;
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = obtainViewFromPool(R.layout.lightgraffiti_layout_discard_lighttrail_dialogue);
        this.mOk = (TextView) v.findViewById(R.id.button_upper);
        if (this.mOk != null) {
            this.mOk.setText(android.R.string.display_manager_overlay_display_name);
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
        if (LGDiscardLightTrailDialogueState.showDiscardCaution) {
            this.data.putString(LGConstants.TRANSIT_FROM_WHERE, LGConstants.TRANSIT_FROM_3RD_BY_PB);
            LGDiscardLightTrailDialogueState.showDiscardCaution = false;
        }
        TextView label = (TextView) getView().findViewById(R.id.dialog_message);
        if (label != null) {
            if (this.data.getString(LGConstants.TRANSIT_FROM_WHERE).equals(LGConstants.TRANSIT_FROM_3RD_BY_CENTER)) {
                label.setText(R.string.STRID_FUNC_LIGHTGRAFFITI_DISCARD_DIALOGUE_TO_THE_FIRST_END_SHOOTING);
            } else if (this.data.getString(LGConstants.TRANSIT_FROM_WHERE).equals(LGConstants.TRANSIT_FROM_3RD_BY_PB)) {
                if (LGStateHolder.getInstance().getShootingStage().equals(LGStateHolder.SHOOTING_3RD_BEFORE_SHOOT)) {
                    label.setText(R.string.STRID_FUNC_LIGHTGRAFFITI_DISCARD_DIALOGUE_TO_THE_FIRST_END_SHOOTING);
                } else {
                    label.setText(R.string.STRID_FUNC_LIGHTGRAFFITI_DISCARD_DIALOGUE_TO_THE_FIRST_RECOMMEND_REVIEW);
                }
            } else if (this.data.getString(LGConstants.TRANSIT_FROM_WHERE).equals(LGConstants.TRANSIT_FROM_2ND)) {
                label.setText(R.string.STRID_FUNC_LIGHTGRAFFITI_DISCARD_DIALOGUE_TO_THE_FIRST_FROM_SECOND);
            } else if (this.data.getString(LGConstants.TRANSIT_FROM_WHERE).equals(LGConstants.TRANSIT_FROM_3RD_BY_APPTOP)) {
                label.setText(R.string.STRID_FUNC_LIGHTGRAFFITI_DISCARD_DIALOGUE_TO_THE_FIRST_END_SHOOTING);
            } else if (this.data.getString(LGConstants.TRANSIT_FROM_WHERE).equals(LGConstants.TRANSIT_FROM_3RD_BY_AUTO_REVIEW)) {
                label.setText(R.string.STRID_FUNC_LIGHTGRAFFITI_DISCARD_DIALOGUE_TO_THE_FIRST_RECOMMEND_REVIEW);
            }
        }
        setKeyBeepPattern(5);
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        LGDiscardLightTrailDialogueState.showDiscardCaution = false;
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

    private void onLensProblem() {
        LGStateHolder.getInstance().prepareShootingStage1st();
        String stage = LGStateHolder.getInstance().getShootingStage();
        LGMenuDataInitializer.initMenuData(stage);
        this.callback.onClose();
        closeLayout();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        Log.d(TAG, "attachedLens");
        LGUtility.getInstance().isLensAttachEventReady = true;
        onLensProblem();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        Log.d(TAG, "detachedLens");
        onLensProblem();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        if (this.mOk != null && this.mOk.isSelected()) {
            if (this.data.getString(LGConstants.TRANSIT_FROM_WHERE).equals(LGConstants.TRANSIT_FROM_3RD_BY_CENTER) || this.data.getString(LGConstants.TRANSIT_FROM_WHERE).equals(LGConstants.TRANSIT_FROM_3RD_BY_PB) || this.data.getString(LGConstants.TRANSIT_FROM_WHERE).equals(LGConstants.TRANSIT_FROM_3RD_BY_AUTO_REVIEW)) {
                LGStateHolder.getInstance().prepareShootingStage1st();
                String stage = LGStateHolder.getInstance().getShootingStage();
                LGMenuDataInitializer.initMenuData(stage);
            } else if (this.data.getString(LGConstants.TRANSIT_FROM_WHERE).equals(LGConstants.TRANSIT_FROM_2ND)) {
                CameraNotificationManager.getInstance().requestNotify(LGConstants.SHOOTING_2ND_CLOSE);
            } else if (this.data.getString(LGConstants.TRANSIT_FROM_WHERE).equals(LGConstants.TRANSIT_FROM_3RD_BY_APPTOP)) {
                LGStateHolder.getInstance().prepareBackToApptop();
                String stage2 = LGStateHolder.getInstance().getShootingStage();
                LGMenuDataInitializer.initMenuData(stage2);
            }
            this.callback.onFinish();
            closeLayout();
            return 1;
        }
        if (this.mCancel != null && this.mCancel.isSelected()) {
            this.callback.onClose();
            closeLayout();
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        this.callback.onClose();
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
                this.callback.onClose();
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
            this.callback.onClose();
            CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_CANNOT_REC_MOVIE);
            handled = true;
        }
        if (handled) {
            return 1;
        }
        int ret = super.onKeyDown(keyCode, event);
        if (ret == 0) {
            this.callback.onClose();
            return ret;
        }
        return ret;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRShutterKey() {
        if (!this.data.getString(LGConstants.TRANSIT_FROM_WHERE).equals(LGConstants.TRANSIT_FROM_2ND)) {
            return super.pushedIRShutterKey();
        }
        this.callback.onClose();
        closeLayout();
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIR2SecKey() {
        if (!this.data.getString(LGConstants.TRANSIT_FROM_WHERE).equals(LGConstants.TRANSIT_FROM_2ND)) {
            return super.pushedIR2SecKey();
        }
        this.callback.onClose();
        closeLayout();
        return -1;
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        LGDiscardLightTrailDialogueState.showDiscardCaution = false;
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        if (this.data.getString(LGConstants.TRANSIT_FROM_WHERE).equals(LGConstants.TRANSIT_FROM_3RD_BY_CENTER)) {
            return -1;
        }
        return super.pushedPlayBackKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedModeDial() {
        CautionUtilityClass.getInstance().requestTrigger(LGInfo.CAUTION_ID_DLAPP_FUNC_INV_APP);
        return super.turnedModeDial();
    }
}
