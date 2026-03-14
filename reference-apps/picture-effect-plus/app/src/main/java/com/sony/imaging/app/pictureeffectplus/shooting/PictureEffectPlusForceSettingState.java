package com.sony.imaging.app.pictureeffectplus.shooting;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.ForceSettingState;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.pictureeffectplus.AppLog;
import com.sony.imaging.app.pictureeffectplus.shooting.camera.PictureEffectPlusController;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class PictureEffectPlusForceSettingState extends ForceSettingState {
    private static final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.base.shooting.ForceSettingState
    protected boolean isForceSetting() {
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.ForceSettingState
    protected Pair<Camera.Parameters, CameraEx.ParametersModifier> onSetForceSetting(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        AppLog.enter(TAG, AppLog.getMethodName());
        Log.i(TAG, "onSetForceSetting");
        String backup_effect = PictureEffectPlusController.getInstance().getBackupEffectValue();
        if (backup_effect.equals(PictureEffectPlusController.MODE_PART_COLOR_PLUS)) {
            ((CameraEx.ParametersModifier) params.second).setPictureEffect(PictureEffectController.MODE_PART_COLOR);
        } else if (backup_effect.equals(PictureEffectPlusController.MODE_MINIATURE_PLUS)) {
            String backup_effect_option = PictureEffectPlusController.getInstance().getBackupEffectOptionValue(PictureEffectPlusController.MODE_MINIATURE_EFFECT);
            if ("cool".equals(backup_effect_option) || "warm".equals(backup_effect_option) || "normal".equals(backup_effect_option) || "green".equals(backup_effect_option) || "magenta".equals(backup_effect_option)) {
                ((CameraEx.ParametersModifier) params.second).setPictureEffect(PictureEffectController.MODE_TOY_CAMERA);
            } else if (PictureEffectPlusController.MINIATURE_RETRO.equals(backup_effect_option)) {
                ((CameraEx.ParametersModifier) params.second).setPictureEffect(PictureEffectController.MODE_RETRO_PHOTO);
            } else {
                ((CameraEx.ParametersModifier) params.second).setPictureEffect(PictureEffectController.MODE_MINIATURE);
            }
        } else if (backup_effect.equals(PictureEffectPlusController.MODE_TOY_CAMERA_PLUS)) {
            ((CameraEx.ParametersModifier) params.second).setPictureEffect(PictureEffectController.MODE_TOY_CAMERA);
        } else {
            ((CameraEx.ParametersModifier) params.second).setPictureEffect(backup_effect);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return params;
    }

    @Override // com.sony.imaging.app.base.shooting.ForceSettingState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.onResume();
        PictureEffectPlusController.getInstance().forceEffectSetting();
        PictureEffectPlusController.getInstance().forceEffectOptionSetting();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.ForceSettingState, com.sony.imaging.app.base.shooting.camera.ForceSetting.FinishCallback
    public void onCallback() {
        AppLog.enter(TAG, AppLog.getMethodName());
        setNextState("ExposureModeCheck", null);
        AppLog.exit(TAG, AppLog.getMethodName());
    }
}
