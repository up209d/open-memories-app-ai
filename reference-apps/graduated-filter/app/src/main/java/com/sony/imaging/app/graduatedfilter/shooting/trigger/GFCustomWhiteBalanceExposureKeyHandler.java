package com.sony.imaging.app.graduatedfilter.shooting.trigger;

import android.os.Bundle;
import com.sony.imaging.app.base.shooting.StateBase;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.trigger.CustomWhiteBalanceExposureKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.graduatedfilter.GFBackUpKey;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.graduatedfilter.common.GFCommonUtil;
import com.sony.imaging.app.graduatedfilter.common.GFConstants;
import com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters;
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
        if (GFCommonUtil.getInstance().isFilterSetting()) {
            transitToSettingMenuState();
        } else {
            stateChange(null);
        }
    }

    private void stateChange(Bundle bundle) {
        StateBase state = (StateBase) this.target;
        state.setNextState("EE", bundle);
    }

    private void saveCWB() {
        WhiteBalanceController wbc = WhiteBalanceController.getInstance();
        String wbMode = wbc.getValue();
        wbc.saveCustomWhiteBalance(wbMode);
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        params.setWBMode(true, wbMode);
        StateBase state = (StateBase) this.target;
        int colorTemp = state.data.getInt(TEMPERATURE);
        int colorComp = state.data.getInt(COMPENSATION);
        int lightBalance = state.data.getInt(LIGHT_BALANCE);
        String sOption = "" + lightBalance + "/" + colorComp + "/" + colorTemp;
        AppLog.info("saveCWB", "sOption: " + sOption);
        GFBackUpKey.getInstance().saveWBOption(wbMode, sOption, true, 0);
        GFBackUpKey.getInstance().saveWBOption(wbMode, sOption, false, 0);
        GFBackUpKey.getInstance().saveWBOption(wbMode, sOption, true, 1);
        GFBackUpKey.getInstance().saveWBOption(wbMode, sOption, false, 1);
        GFBackUpKey.getInstance().saveWBOption(wbMode, sOption, true, 2);
        GFBackUpKey.getInstance().saveWBOption(wbMode, sOption, false, 2);
        GFBackUpKey.getInstance().saveWBOption(wbMode, sOption, true, 3);
        GFBackUpKey.getInstance().saveWBOption(wbMode, sOption, false, 3);
        GFBackUpKey.getInstance().saveWBOption(wbMode, sOption, true, 4);
        GFBackUpKey.getInstance().saveWBOption(wbMode, sOption, false, 4);
    }

    private int transitToSettingMenuState() {
        Bundle bundle = new Bundle();
        bundle.putString("ItemId", GFConstants.APPSETTING);
        State state = (State) this.target;
        state.setNextState("EE", bundle);
        return 1;
    }
}
