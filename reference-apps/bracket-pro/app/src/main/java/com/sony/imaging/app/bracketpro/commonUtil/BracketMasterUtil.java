package com.sony.imaging.app.bracketpro.commonUtil;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.bracketpro.R;
import com.sony.imaging.app.bracketpro.menu.controller.BMMenuController;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class BracketMasterUtil {
    private static boolean mHold = false;

    public static int getBracketTypeString() {
        String mCurrentBracket = BackUpUtil.getInstance().getPreferenceString(BracketMasterBackUpKey.TAG_CURRENT_BRACKET_TYPE, BMMenuController.FocusBracket);
        if (mCurrentBracket.equalsIgnoreCase(BMMenuController.ApertureBracket)) {
            return R.string.STRID_FUNC_APERTUREBRK;
        }
        if (mCurrentBracket.equalsIgnoreCase(BMMenuController.ShutterSpeedBracket)) {
            return R.string.STRID_FUNC_SSBRK;
        }
        if (!mCurrentBracket.equalsIgnoreCase(BMMenuController.FlashBracket)) {
            return R.string.STRID_FUNC_FOCUSBRK;
        }
        return R.string.STRID_FUNC_FLASHBRK;
    }

    public static String getCurrentBracketType() {
        return BackUpUtil.getInstance().getPreferenceString(BracketMasterBackUpKey.TAG_CURRENT_BRACKET_TYPE, BMMenuController.FocusBracket);
    }

    public static void setHoldStatus(boolean holdStatus) {
        mHold = holdStatus;
    }

    public static boolean isHoldFocus() {
        return false;
    }

    public static void disableIrisRing() {
        CameraEx cameraEx;
        if (isIRISRingEnabledDevice() && (cameraEx = ExecutorCreator.getInstance().getSequence().getCameraEx()) != null) {
            cameraEx.disableIrisRing();
            Log.d("Iris Ring", "Iris Ring is disabled!");
        }
    }

    public static void enableIrisRing() {
        CameraEx cameraEx;
        if (isIRISRingEnabledDevice() && (cameraEx = ExecutorCreator.getInstance().getSequence().getCameraEx()) != null) {
            cameraEx.enableIrisRing();
            Log.d("Iris Ring", "Iris Ring is enable");
        }
    }

    public static boolean isIRISRingEnabledDevice() {
        int type = ScalarProperties.getInt("device.iris.ring.type");
        Log.d("Iris Ring", "Iris Type = " + type);
        if (type != 0) {
            return true;
        }
        return false;
    }
}
