package com.sony.imaging.app.digitalfilter.shooting.trigger;

import android.os.Bundle;
import com.sony.imaging.app.base.shooting.StateBase;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.trigger.CustomWhiteBalanceExposureKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFLinkUtil;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFWhiteBalanceController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class GFCustomWhiteBalanceExposureKeyHandler extends CustomWhiteBalanceExposureKeyHandler {
    private static final String BUNDLE_KEY_CODE = "keyCode";
    private static final String COMPENSATION = "colorCompensation";
    private static final String ITEM_ID = "ItemId";
    private static final String LIGHT_BALANCE = "lightBalance";
    private static final String MENU_STATE = "EE";
    private static final String NEXT_STATE = "EE";
    private static final String TEMPERATURE = "colorTemperature";

    @Override // com.sony.imaging.app.base.shooting.trigger.CustomWhiteBalanceExposureKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        if (Environment.isCustomWBOnePush()) {
            stateChange(null);
        } else {
            setCustomWhiteBalanceAndStateChange(null);
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.CustomWhiteBalanceExposureKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        Bundle bundle = new Bundle();
        bundle.putInt("keyCode", AppRoot.USER_KEYCODE.S1_ON);
        setCustomWhiteBalanceAndStateChange(bundle);
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.CustomWhiteBalanceExposureKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        setCustomWhiteBalanceAndStateChange(null);
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.CustomWhiteBalanceExposureKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        setCustomWhiteBalanceAndStateChange(null);
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.CustomWhiteBalanceExposureKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedModeDial() {
        saveCWB();
        int code = ModeDialDetector.getModeDialPosition();
        if (code != -1) {
            Bundle bundle = new Bundle();
            bundle.putString("ItemId", ExposureModeController.EXPOSURE_MODE);
            StateBase state = (StateBase) this.target;
            state.setNextState("EE", bundle);
            ExecutorCreator.getInstance().updateSequence();
            return 1;
        }
        return 1;
    }

    private void setCustomWhiteBalanceAndStateChange(Bundle bundle) {
        saveCWB();
        stateChange(null);
    }

    private void stateChange(Bundle bundle) {
        StateBase state = (StateBase) this.target;
        state.setNextState("EE", bundle);
    }

    private void saveCWB() {
        WhiteBalanceController wbc = WhiteBalanceController.getInstance();
        String wbMode = wbc.getValue();
        wbc.saveCustomWhiteBalance(wbMode);
        int settingLayer = GFWhiteBalanceController.getInstance().getCustomWhiteBalanceSettingLayer();
        StateBase state = (StateBase) this.target;
        int colorTemp = state.data.getInt(TEMPERATURE);
        int colorComp = state.data.getInt(COMPENSATION);
        int lightBalance = state.data.getInt(LIGHT_BALANCE);
        String sOption = "" + lightBalance + "/" + colorComp + "/" + colorTemp;
        AppLog.info("saveCWB", "sOption: " + sOption);
        GFLinkUtil.getInstance().saveLinkedCWB(wbMode, sOption, settingLayer);
    }
}
