package com.sony.imaging.app.timelapse.shooting.state;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.shooting.S1OffEEState;
import com.sony.imaging.app.base.shooting.ShootingMenuState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.timelapse.R;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import com.sony.imaging.app.timelapse.menu.controller.TimelapseExposureModeController;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.timelapse.shooting.controller.TLShootModeSettingController;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class TimelapseShootingMenuState extends ShootingMenuState implements NotificationListener {
    private static final String ID_LASTBASTIONLAYOUT = "LastBastionLayout";
    private final String[] TAGS = {"ListViewForAngleShiftAddOn"};

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState
    public void finishMenuState(Bundle bundle) {
        if (TLCommonUtil.getInstance().getBootFactor() == 0) {
            TLCommonUtil.getInstance().setBootFactor(2);
            AppNameView.setText(getResources().getString(R.string.STRID_FUNC_TIMELAPSE));
            AppNameView.show(true);
        }
        super.finishMenuState(bundle);
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        if (TLCommonUtil.getInstance().isValidThemeForUpdatedOption()) {
            TLCommonUtil.getInstance().resetShutterSpeed();
        }
        CameraNotificationManager.getInstance().setNotificationListener(this);
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        super.onPause();
        updateBackupSettings();
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState
    protected boolean canRemoveState() {
        if (TLCommonUtil.getInstance().getCurrentState() != 7 || !ModeDialDetector.hasModeDial()) {
            return true;
        }
        boolean ret = TimelapseExposureModeController.getInstance().isValidDialPosition(ModeDialDetector.getModeDialPosition());
        return ret;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver
    public boolean handleMessage(Message msg) {
        if (518 == msg.what) {
            return true;
        }
        boolean ret = super.handleMessage(msg);
        return ret;
    }

    @Override // com.sony.imaging.app.base.menu.MenuState
    protected String getLastBastionLayoutName() {
        return ID_LASTBASTIONLAYOUT;
    }

    public void updateBackupSettings() {
        boolean isSilentShutterOn = TLCommonUtil.getInstance().isSilentShutterOn();
        String picEffect = PictureEffectController.getInstance().getValue("PictureEffect");
        TimeLapseConstants.sLastSelectedState = TLCommonUtil.getInstance().getCurrentState();
        if (TimeLapseConstants.sLastSelectedState == 7) {
            String iso_value = ISOSensitivityController.getInstance().getValue();
            BackUpUtil.getInstance().setPreference(TimeLapseConstants.CUSTOM_ISO_VALUE_KEY, iso_value);
            String wbValue = WhiteBalanceController.getInstance().getValue();
            BackUpUtil.getInstance().setPreference(TimeLapseConstants.CUSTOM_WB_KEY, wbValue);
            WhiteBalanceController.WhiteBalanceParam param = (WhiteBalanceController.WhiteBalanceParam) WhiteBalanceController.getInstance().getDetailValue();
            String optValue = (("" + param.getLightBalance()) + "/" + param.getColorComp()) + "/" + param.getColorTemp();
            BackUpUtil.getInstance().setPreference(TimeLapseConstants.CUSTOM_WB_KEY_OPTION_VALUE, optValue);
            if (wbValue.equalsIgnoreCase("auto")) {
                BackUpUtil.getInstance().setPreference(TimeLapseConstants.CUSTOM_AWB_KEY_OPTION_VALUE, optValue);
            }
            if ("off".equals(picEffect)) {
                String creativeStyle = CreativeStyleController.getInstance().getValue(CreativeStyleController.CREATIVESTYLE);
                BackUpUtil.getInstance().setPreference(TimeLapseConstants.CUSTOM_CREACTIVE_STYLE_KEY, creativeStyle);
                CreativeStyleController.CreativeStyleOptions cStyleparam = (CreativeStyleController.CreativeStyleOptions) CreativeStyleController.getInstance().getDetailValue();
                String cStyleoptValue = "" + cStyleparam.contrast;
                BackUpUtil.getInstance().setPreference(TimeLapseConstants.CUSTOM_CREACTIVE_STYLE_KEY_OPTION_VALUE, (cStyleoptValue + "/" + cStyleparam.saturation) + "/" + cStyleparam.sharpness);
            }
        }
        TimeLapseConstants.slastStateExposureCompansasion = ExposureCompensationController.getInstance().getExposureCompensationIndex();
        if (TLShootModeSettingController.getInstance().getCurrentCaptureState() != 0) {
            TimeLapseConstants.TIME_LAPSE_PICTURE_ASPECT_SIZE = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
            TimeLapseConstants.TIME_LAPSE_PICTURE_SIZE = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_IMAGE_SIZE);
            BackUpUtil.getInstance().setPreference(TimeLapseConstants.TIME_LAPSE_PICTURE_ASPECT_SIZE_KEY, TimeLapseConstants.TIME_LAPSE_PICTURE_ASPECT_SIZE);
            BackUpUtil.getInstance().setPreference(TimeLapseConstants.TIME_LAPSE_PICTURE_SIZE_KEY, TimeLapseConstants.TIME_LAPSE_PICTURE_SIZE);
        }
        if (!isSilentShutterOn && TimeLapseConstants.sLastSelectedState == 5) {
            BackUpUtil.getInstance().setPreference(TimeLapseConstants.MINIATURE_PICTURE_EFFECT_KEY, PictureEffectController.getInstance().getValue(PictureEffectController.MODE_MINIATURE));
        }
        TLCommonUtil.getInstance().setValue();
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        TimeLapseConstants.isEVDial = true;
        TimeLapseConstants.slastStateExposureCompansasion = 0;
        onClosed(null);
        return 1;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return this.TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (tag.equalsIgnoreCase("ListViewForAngleShiftAddOn")) {
            Bundle b = new Bundle();
            Bundle data = new Bundle();
            data.putParcelable(EventParcel.KEY_KEYCODE, new EventParcel(CustomizableFunction.PlayIndex));
            b.putParcelable(S1OffEEState.STATE_NAME, data);
            Activity appRoot = getActivity();
            ((BaseApp) appRoot).changeApp(BaseApp.APP_PLAY, b);
        }
    }
}
