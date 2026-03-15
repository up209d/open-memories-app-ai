package com.sony.imaging.app.srctrl.util;

import android.util.Log;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.sysutil.ScalarProperties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class ModelInfo {
    private static final String UI_MODEL_NAME_KV = "DSC-15-03";
    private String modelName = null;
    private static final String TAG = ModelInfo.class.getSimpleName();
    private static final List<String> movieRecNotSupportedModelName = Collections.unmodifiableList(new ArrayList<String>() { // from class: com.sony.imaging.app.srctrl.util.ModelInfo.1
        {
            add("ILCE-7");
            add("ILCE-7R");
            add("ILCE-5000");
            add("ILCE-6000");
        }
    });
    private static ModelInfo modelInfo = new ModelInfo();

    private ModelInfo() {
        Log.v(TAG, "ModelInfo created");
    }

    public static ModelInfo getInstance() {
        return modelInfo;
    }

    public boolean isMovieRecSupported() {
        if (!Environment.isMovieAPISupported()) {
            return false;
        }
        if (this.modelName == null) {
            Log.e(TAG, "ModelInfo is NULL");
            return false;
        }
        if (movieRecNotSupportedModelName.contains(this.modelName)) {
            return false;
        }
        String UiModelName = ScalarProperties.getString("ui.model.mame");
        return !"DSC-15-03".equals(UiModelName);
    }

    public boolean isNfcSupported() {
        if (6 > Environment.getVersionPfAPI() || 1 != ScalarProperties.getInt("device.nfc.supported")) {
            return false;
        }
        return true;
    }

    public boolean isNeedDisplayOwnAirPlaneMode() {
        if (!ScalarProperties.getString(UtilPFWorkaround.PROP_MODEL_NAME).equals("NEX-5T")) {
            return false;
        }
        return true;
    }

    public void set(String name) {
        this.modelName = name;
        Log.v(TAG, "ModelName = " + this.modelName);
    }

    public String get() {
        return this.modelName;
    }
}
