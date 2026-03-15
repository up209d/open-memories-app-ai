package com.sony.imaging.app.lightshaft.shooting.camera;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.lightshaft.AppLog;
import com.sony.imaging.app.lightshaft.LightShaftBackUpKey;
import com.sony.imaging.app.lightshaft.shooting.ShaftsEffect;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class EffectSelectController extends AbstractController {
    public static final String ANGEL = "angel";
    public static final String BEAM = "beam";
    public static final String EFFECTSELECT = "ApplicationTop";
    public static final String FLARE = "flare";
    public static final String STAR = "star";
    private static final String TAG = "EffectSelectController";
    private static EffectSelectController mInstance;
    private static ArrayList<String> mSupportedValue;
    boolean isSetting1Displaying = false;
    private boolean isShootingEnable = false;

    public boolean isShootingEnable() {
        return this.isShootingEnable;
    }

    public void setShootingEnable(boolean isShootingEnable) {
        this.isShootingEnable = isShootingEnable;
    }

    public boolean isSetting1Displaying() {
        return this.isSetting1Displaying;
    }

    public void setAngleChanging(boolean isAngleChanging) {
        this.isSetting1Displaying = isAngleChanging;
    }

    public static EffectSelectController getInstance() {
        if (mInstance == null) {
            createInstance();
        }
        return mInstance;
    }

    private static EffectSelectController createInstance() {
        if (mInstance == null) {
            mInstance = new EffectSelectController();
        }
        return mInstance;
    }

    protected EffectSelectController() {
        mSupportedValue = new ArrayList<>();
        mSupportedValue.add(ANGEL);
        mSupportedValue.add(STAR);
        mSupportedValue.add(FLARE);
        mSupportedValue.add(BEAM);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.info(TAG, "doItemClickProcessing itemID= " + value);
        AppLog.info(TAG, "setValue: " + value);
        ShaftsEffect.Parameters param = ShaftsEffect.getInstance().getParameters();
        if (ANGEL.equals(value)) {
            param.setEffect(1);
            BackUpUtil.getInstance().setPreference(LightShaftBackUpKey.SELECTED_EFFECT, value);
        } else if (STAR.equals(value)) {
            param.setEffect(2);
            BackUpUtil.getInstance().setPreference(LightShaftBackUpKey.SELECTED_EFFECT, value);
        } else if (FLARE.equals(value)) {
            param.setEffect(3);
            BackUpUtil.getInstance().setPreference(LightShaftBackUpKey.SELECTED_EFFECT, value);
        } else if (BEAM.equals(value)) {
            param.setEffect(4);
            BackUpUtil.getInstance().setPreference(LightShaftBackUpKey.SELECTED_EFFECT, value);
        }
        AppLog.info(TAG, "doItemClickProcessing setValue = " + value);
        param.unflatten(LightShaftBackUpKey.getInstance().getLastSavedOptionSettings());
        ShaftsEffect.getInstance().setParameters(param);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        int effect = ShaftsEffect.getInstance().getParameters().getEffect();
        switch (effect) {
            case 2:
                return STAR;
            case 3:
                return FLARE;
            case 4:
                return BEAM;
            default:
                return ANGEL;
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        return mSupportedValue;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        return mSupportedValue;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        return true;
    }
}
