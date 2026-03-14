package com.sony.imaging.app.doubleexposure.shooting.wigdet;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.widget.ActiveText;
import com.sony.imaging.app.doubleexposure.R;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureConstant;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureUtil;
import com.sony.imaging.app.doubleexposure.menu.controller.ThemeSelectionController;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class ThemeGuideText extends ActiveText {
    private final String TAG;

    public ThemeGuideText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAG = AppLog.getClassName();
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveText
    protected void refresh() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        String currentShootingScreen = DoubleExposureUtil.getInstance().getCurrentShootingScreen();
        String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
        if (selectedTheme != null && "Manual".equalsIgnoreCase(selectedTheme)) {
            setVisibility(4);
            setText("");
        } else {
            setVisibility(0);
            if (ThemeSelectionController.SOFTFILTER.equalsIgnoreCase(selectedTheme)) {
                if (currentShootingScreen != null && DoubleExposureConstant.FIRST_SHOOTING.equalsIgnoreCase(currentShootingScreen)) {
                    setText(R.string.STRID_FUNC_MLE_SOFTFILTER_1ST_SHOOTING_GUIDE);
                } else {
                    setText(R.string.STRID_FUNC_MLE_SOFTFILTER_2ND_SHOOTING_GUIDE);
                }
            } else if (ThemeSelectionController.SIL.equalsIgnoreCase(selectedTheme)) {
                if (currentShootingScreen != null && DoubleExposureConstant.FIRST_SHOOTING.equalsIgnoreCase(currentShootingScreen)) {
                    setText(R.string.STRID_FUNC_MLE_SIL_1ST_SHOOTING_GUIDE);
                } else {
                    setText(R.string.STRID_FUNC_MLE_SIL_2ND_SHOOTING_GUIDE);
                }
            } else if (ThemeSelectionController.SKY.equalsIgnoreCase(selectedTheme)) {
                if (currentShootingScreen != null && DoubleExposureConstant.FIRST_SHOOTING.equalsIgnoreCase(currentShootingScreen)) {
                    setText(R.string.STRID_FUNC_MLE_SKY_1ST_SHOOTING_GUIDE);
                } else {
                    setText(R.string.STRID_FUNC_MLE_CMN_2ND_SHOOTING_GUIDE);
                }
            } else if (ThemeSelectionController.TEXTURE.equalsIgnoreCase(selectedTheme)) {
                if (currentShootingScreen != null && DoubleExposureConstant.FIRST_SHOOTING.equalsIgnoreCase(currentShootingScreen)) {
                    setText(R.string.STRID_FUNC_MLE_TEXTURE_1ST_SHOOTING_GUIDE);
                } else {
                    setText(R.string.STRID_FUNC_MLE_CMN_2ND_SHOOTING_GUIDE);
                }
            } else if ("Rotation".equalsIgnoreCase(selectedTheme)) {
                if (currentShootingScreen != null && DoubleExposureConstant.FIRST_SHOOTING.equalsIgnoreCase(currentShootingScreen)) {
                    setText(R.string.STRID_FUNC_MLE_ROTATE_1ST_SHOOTING_GUIDE);
                } else {
                    setText(R.string.STRID_FUNC_MLE_CMN_2ND_SHOOTING_GUIDE);
                }
            } else if (ThemeSelectionController.MIRROR.equalsIgnoreCase(selectedTheme)) {
                if (currentShootingScreen != null && DoubleExposureConstant.FIRST_SHOOTING.equalsIgnoreCase(currentShootingScreen)) {
                    setText(R.string.STRID_FUNC_MLE_MIRROR_1ST_SHOOTING_GUIDE);
                } else {
                    setText(R.string.STRID_FUNC_MLE_MIRROR_2ND_SHOOTING_GUIDE);
                }
            }
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveText
    protected NotificationListener getNotificationListener() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return null;
    }
}
