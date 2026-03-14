package com.sony.imaging.app.liveviewgrading.menu.controller;

import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class ColorGradingCreativeStyleController extends CreativeStyleController {
    private static final String TAG = "ColorGradingCreativeStyleController";

    @Override // com.sony.imaging.app.base.shooting.camera.CreativeStyleController, com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetInitParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        Log.i(TAG, "onGetInitParameters");
    }

    @Override // com.sony.imaging.app.base.shooting.camera.CreativeStyleController, com.sony.imaging.app.base.shooting.camera.AbstractController
    public void onGetTermParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        Log.i(TAG, "onGetTermParameters");
    }

    @Override // com.sony.imaging.app.base.shooting.camera.CreativeStyleController, com.sony.imaging.app.base.menu.IController
    public void setValue(String itemId, String value) {
        Log.i(TAG, "setValue");
    }

    @Override // com.sony.imaging.app.base.shooting.camera.CreativeStyleController, com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public void setDetailValue(Object obj) {
        Log.i(TAG, "setDetailValue");
    }

    @Override // com.sony.imaging.app.base.shooting.camera.CreativeStyleController
    public void setDetailValue(String mode, Object obj, Pair<Camera.Parameters, CameraEx.ParametersModifier> p) {
        Log.i(TAG, "setDetailValue");
    }

    @Override // com.sony.imaging.app.base.shooting.camera.CreativeStyleController
    protected synchronized void updateDetailValue() {
        Log.i(TAG, "synchronized updateDetailValue");
    }

    @Override // com.sony.imaging.app.base.shooting.camera.CreativeStyleController
    protected boolean updateDetailValue(Pair<Camera.Parameters, CameraEx.ParametersModifier> param) {
        Log.i(TAG, "updateDetailValue");
        return false;
    }
}
