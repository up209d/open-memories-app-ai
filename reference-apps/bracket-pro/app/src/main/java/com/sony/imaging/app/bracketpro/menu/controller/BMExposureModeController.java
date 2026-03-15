package com.sony.imaging.app.bracketpro.menu.controller;

import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.bracketpro.AppLog;
import com.sony.imaging.app.bracketpro.caution.CautionInfo;
import com.sony.imaging.app.bracketpro.commonUtil.BracketMasterBackUpKey;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class BMExposureModeController extends ExposureModeController {
    private static BMExposureModeController mInstance;
    private static final String TAG = AppLog.getClassName();
    static String mCurrentBracket = BackUpUtil.getInstance().getPreferenceString(BracketMasterBackUpKey.TAG_CURRENT_BRACKET_TYPE, BMMenuController.FocusBracket);
    private static final ArrayList<String> SCN_SELECTION_LIST = new ArrayList<>();

    static {
        SCN_SELECTION_LIST.add("portrait");
        SCN_SELECTION_LIST.add("landscape");
        SCN_SELECTION_LIST.add(ExposureModeController.SPORTS);
        SCN_SELECTION_LIST.add("sunset");
        SCN_SELECTION_LIST.add(CreativeStyleController.NIGHT);
        SCN_SELECTION_LIST.add("night-portrait");
        SCN_SELECTION_LIST.add(ExposureModeController.MACRO);
        SCN_SELECTION_LIST.add("hand-held-twilight");
        SCN_SELECTION_LIST.add("anti-motion-blur");
    }

    public static BMExposureModeController getInstance() {
        if (mInstance == null) {
            createInstance();
        }
        mCurrentBracket = BMMenuController.getInstance().getSelectedBracket();
        return mInstance;
    }

    private static BMExposureModeController createInstance() {
        if (mInstance == null) {
            mInstance = new BMExposureModeController();
        }
        return mInstance;
    }

    public void initializeAgain() {
        createSupportedList(ModeDialDetector.getSupportedModeDials());
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController, com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        mCurrentBracket = BackUpUtil.getInstance().getPreferenceString(BracketMasterBackUpKey.TAG_CURRENT_BRACKET_TYPE, BMMenuController.FocusBracket);
        AppLog.enter(TAG, AppLog.getMethodName());
        List<String> availables = new ArrayList<>();
        if (tag.equals(ExposureModeController.EXPOSURE_MODE)) {
            if (mCurrentBracket.equals(BMMenuController.ShutterSpeedBracket)) {
                availables.add("Shutter");
            } else if (mCurrentBracket.equals(BMMenuController.ApertureBracket)) {
                availables.add("Aperture");
            } else if (mCurrentBracket.equals(BMMenuController.FocusBracket)) {
                availables.add(ExposureModeController.PROGRAM_AUTO_MODE);
                availables.add("Aperture");
                availables.add("Shutter");
                availables.add(ExposureModeController.MANUAL_MODE);
            } else if (mCurrentBracket.equals(BMMenuController.FlashBracket)) {
                availables.add(ExposureModeController.PROGRAM_AUTO_MODE);
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        if (availables.isEmpty()) {
            return null;
        }
        return availables;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController, com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        List<String> supported = new ArrayList<>();
        if (!ModeDialDetector.hasModeDial()) {
            if (tag.equals(ExposureModeController.EXPOSURE_MODE)) {
                mCurrentBracket = BackUpUtil.getInstance().getPreferenceString(BracketMasterBackUpKey.TAG_CURRENT_BRACKET_TYPE, BMMenuController.FocusBracket);
                if (mCurrentBracket.equalsIgnoreCase(BMMenuController.FocusBracket)) {
                    supported.add(ExposureModeController.PROGRAM_AUTO_MODE);
                    supported.add("Aperture");
                    supported.add("Shutter");
                    supported.add(ExposureModeController.MANUAL_MODE);
                } else if (mCurrentBracket.equalsIgnoreCase(BMMenuController.ApertureBracket)) {
                    supported.add("Aperture");
                } else if (mCurrentBracket.equalsIgnoreCase(BMMenuController.ShutterSpeedBracket)) {
                    supported.add("Shutter");
                } else if (mCurrentBracket.equalsIgnoreCase(BMMenuController.FlashBracket)) {
                    supported.add(ExposureModeController.PROGRAM_AUTO_MODE);
                }
            }
            if (supported.isEmpty()) {
                return null;
            }
        } else {
            AppLog.info(TAG, AppLog.getClassName() + " tag is " + tag);
            supported = super.getSupportedValue(tag);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return supported;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController
    public boolean isValidDialPosition() {
        AppLog.enter(TAG, AppLog.getMethodName());
        int code = ModeDialDetector.getModeDialPosition();
        if (-1 != code) {
            String expMode = ExposureModeController.scancode2Value(code);
            boolean isValid = isValidValue(expMode);
            AppLog.enter(TAG, AppLog.getMethodName());
            return isValid;
        }
        return false;
    }

    public boolean isValidDialPosition(int mModeDialPosition) {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean isValid = false;
        if (mCurrentBracket.equalsIgnoreCase(BMMenuController.FocusBracket)) {
            if (537 == mModeDialPosition || 538 == mModeDialPosition || 539 == mModeDialPosition || 540 == mModeDialPosition) {
                isValid = true;
            }
        } else if (mCurrentBracket.equalsIgnoreCase(BMMenuController.ApertureBracket) && 539 == mModeDialPosition) {
            isValid = true;
        } else if (mCurrentBracket.equalsIgnoreCase(BMMenuController.ShutterSpeedBracket) && 538 == mModeDialPosition) {
            isValid = true;
        } else if (mCurrentBracket.equalsIgnoreCase(BMMenuController.FlashBracket) && 537 == mModeDialPosition) {
            isValid = true;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return isValid;
    }

    public boolean isValidExpoMode(String expMode) {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean isValid = false;
        if (mCurrentBracket.equalsIgnoreCase(BMMenuController.FocusBracket)) {
            if (ExposureModeController.MANUAL_MODE.equals(expMode) || ExposureModeController.PROGRAM_AUTO_MODE.equals(expMode) || "Shutter".equals(expMode) || "Aperture".equals(expMode)) {
                isValid = true;
            }
        } else if (mCurrentBracket.equalsIgnoreCase(BMMenuController.ApertureBracket) && "Aperture".equals(expMode)) {
            isValid = true;
        } else if (mCurrentBracket.equalsIgnoreCase(BMMenuController.ShutterSpeedBracket) && "Shutter".equals(expMode)) {
            isValid = true;
        } else if (mCurrentBracket.equalsIgnoreCase(BMMenuController.FlashBracket) && ExposureModeController.PROGRAM_AUTO_MODE.equals(expMode)) {
            isValid = true;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return isValid;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController
    public int getCautionId() {
        if (mCurrentBracket.equalsIgnoreCase(BMMenuController.FocusBracket)) {
            return CautionInfo.CAUTION_ID_DLAPP_PASM_CLOSE_KEY;
        }
        if (mCurrentBracket.equalsIgnoreCase(BMMenuController.ApertureBracket)) {
            return CautionInfo.CAUTION_ID_DLAPP_A_CLOSE_KEY;
        }
        if (mCurrentBracket.equalsIgnoreCase(BMMenuController.ShutterSpeedBracket)) {
            return CautionInfo.CAUTION_ID_DLAPP_S_CLOSE_KEY;
        }
        if (!mCurrentBracket.equalsIgnoreCase(BMMenuController.FlashBracket)) {
            return 0;
        }
        return CautionInfo.CAUTION_ID_DLAPP_P_CLOSE_KEY;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.ExposureModeController, com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        String curGroup = BackUpUtil.getInstance().getPreferenceString(BracketMasterBackUpKey.TAG_CURRENT_BRACKET_TYPE, BMMenuController.FocusBracket);
        if (!ModeDialDetector.hasModeDial()) {
            if (curGroup.equals(BMMenuController.FocusBracket)) {
                return true;
            }
            return false;
        }
        boolean ret = super.isAvailable(tag);
        return ret;
    }

    public void setModeDialValue() {
        String value;
        switch (ModeDialDetector.getModeDialPosition()) {
            case AppRoot.USER_KEYCODE.MODE_DIAL_PROGRAM /* 537 */:
                value = ExposureModeController.PROGRAM_AUTO_MODE;
                break;
            case AppRoot.USER_KEYCODE.MODE_DIAL_AES /* 538 */:
                value = "Shutter";
                break;
            case AppRoot.USER_KEYCODE.MODE_DIAL_AEA /* 539 */:
                value = "Aperture";
                break;
            case AppRoot.USER_KEYCODE.MODE_DIAL_MANUAL /* 540 */:
                value = ExposureModeController.MANUAL_MODE;
                break;
            default:
                value = ExposureModeController.INTELLIGENT_AUTO_MODE;
                break;
        }
        setValue(ExposureModeController.EXPOSURE_MODE, value);
    }
}
