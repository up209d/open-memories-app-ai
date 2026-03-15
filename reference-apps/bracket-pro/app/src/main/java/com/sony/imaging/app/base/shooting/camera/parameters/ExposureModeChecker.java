package com.sony.imaging.app.base.shooting.camera.parameters;

import android.hardware.Camera;
import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.bracketpro.menu.layout.BracketMasterSubMenu;
import java.util.StringTokenizer;

/* loaded from: classes.dex */
public class ExposureModeChecker extends StringValuesChecker {
    public ExposureModeChecker(String key) {
        super(key);
    }

    protected boolean isPhaseDetectAF() {
        if (FocusAreaController.getInstance().getSensorType() == 1) {
            String focusMode = FocusModeController.getInstance().getValue();
            if ("auto".equals(focusMode) || "af-c".equals(focusMode) || "af-s".equals(focusMode)) {
                return true;
            }
        }
        return false;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.parameters.StringValuesChecker, com.sony.imaging.app.base.shooting.camera.parameters.ISupportedParameterChecker
    public void check(String value, Camera.Parameters supported, Camera.Parameters to, Camera.Parameters from) {
        if (value != null) {
            boolean isSupported = false;
            StringBuilder builder = getBuilder();
            boolean isAutoSupported = false;
            boolean isPAutoSupported = false;
            String supportedValues = supported.get(builder.replace(0, builder.length(), this.mKey).append("-values").toString());
            StringTokenizer tokenizer = new StringTokenizer(supportedValues, ",");
            while (true) {
                if (!tokenizer.hasMoreElements()) {
                    break;
                }
                String elem = tokenizer.nextToken();
                if (value.equals(elem)) {
                    isSupported = true;
                    to.set(this.mKey, value);
                    break;
                } else if ("auto".equals(elem)) {
                    isAutoSupported = true;
                } else if (BracketMasterSubMenu.PROGRAM_AUTO.equals(elem)) {
                    isPAutoSupported = true;
                }
            }
            if (!isSupported) {
                if (BracketMasterSubMenu.APERTURE_PRIORITY.equals(value) || BracketMasterSubMenu.SHUTTER_SPEED.equals(value) || BracketMasterSubMenu.MANUAL_EXPOSURE.equals(value)) {
                    if (isPAutoSupported) {
                        to.set(this.mKey, BracketMasterSubMenu.PROGRAM_AUTO);
                    } else if (isAutoSupported) {
                        to.set(this.mKey, "auto");
                    }
                } else if ("picture-effect".equals(value)) {
                    if (isPAutoSupported) {
                        to.set(this.mKey, BracketMasterSubMenu.PROGRAM_AUTO);
                    }
                } else if (isAutoSupported) {
                    to.set(this.mKey, "auto");
                }
            }
            builder.replace(0, builder.length(), this.mKey).append(AbstractSupportedChecker.COLON).append(value).append(" -> ").append(isSupported);
            Log.i(TAG, builder.toString());
        }
    }
}
