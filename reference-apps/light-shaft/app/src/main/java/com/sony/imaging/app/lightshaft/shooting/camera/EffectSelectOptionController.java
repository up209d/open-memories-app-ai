package com.sony.imaging.app.lightshaft.shooting.camera;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.lightshaft.AppLog;
import com.sony.imaging.app.lightshaft.shooting.ShaftsEffect;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class EffectSelectOptionController extends AbstractController {
    public static final String EFFECTSELECT = "EffectSelect";
    public static final String LENGTH = "length";
    public static final String RANGE = "mRange";
    public static final String SHAFT = "shaft";
    public static final String STRENGTH = "strength";
    private static final String TAG = "EffectSelectOptionController";
    private static EffectSelectOptionController mInstance;
    private final Integer[] RANGEVALUES = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32};
    private final Integer[] SHAFTVALUES = {2, 4, 6, 8};
    private String mSelectedValue = RANGE;
    private List<Integer> mSupportedOptionValue = new ArrayList();

    public static EffectSelectOptionController getInstance() {
        if (mInstance == null) {
            createInstance();
        }
        return mInstance;
    }

    private static EffectSelectOptionController createInstance() {
        if (mInstance == null) {
            mInstance = new EffectSelectOptionController();
        }
        return mInstance;
    }

    private EffectSelectOptionController() {
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        this.mSelectedValue = value;
        setSupportedOptionValueArray();
    }

    public void setOptionValue(String tag, String value) throws IllegalArgumentException {
        AppLog.info(TAG, "setOptionValue: " + value);
        ShaftsEffect.Parameters param = ShaftsEffect.getInstance().getParameters();
        if (RANGE.equals(tag)) {
            param.setRange(Integer.parseInt(value));
        } else if (STRENGTH.equals(tag)) {
            param.setStrength(Integer.parseInt(value));
        } else if (LENGTH.equals(tag)) {
            param.setLength(Integer.parseInt(value));
        } else if (SHAFT.equals(tag)) {
            param.setNumberOfShafts(Integer.parseInt(value));
        }
        ShaftsEffect.getInstance().setParameters(param);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        return this.mSelectedValue;
    }

    public int getOptionValue(String value) throws IController.NotSupportedException {
        AppLog.info(TAG, "getOptionValue: " + value);
        ShaftsEffect.Parameters param = ShaftsEffect.getInstance().getParameters();
        if (RANGE.equals(value)) {
            int ret = param.getRange();
            return ret;
        }
        if (STRENGTH.equals(value)) {
            int ret2 = param.getStrength();
            return ret2;
        }
        if (LENGTH.equals(value)) {
            int ret3 = param.getLength();
            return ret3;
        }
        if (!SHAFT.equals(value)) {
            return 0;
        }
        int ret4 = param.getNumberOfShafts();
        return ret4;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        return null;
    }

    public List<Integer> getSupportedOptionValue(String tag) {
        setSupportedOptionValueArray();
        return this.mSupportedOptionValue;
    }

    public int getSupportedOptionValueRange() {
        return this.mSupportedOptionValue.size();
    }

    public List<Integer> getAvailableOptionValue(String tag) {
        return this.mSupportedOptionValue;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        return true;
    }

    public int calcExposureCompensationValue() {
        if (this.mSupportedOptionValue != null) {
            return this.mSupportedOptionValue.size() < 12 ? 2 : 3;
        }
        return 1;
    }

    private void setSupportedOptionValueArray() {
        this.mSupportedOptionValue.clear();
        if (this.mSelectedValue.equals(RANGE) || this.mSelectedValue.equals(SHAFT)) {
            int effect = ShaftsEffect.getInstance().getParameters().getEffect();
            switch (effect) {
                case 1:
                    for (int i = 0; i < this.RANGEVALUES.length; i++) {
                        this.mSupportedOptionValue.add(this.RANGEVALUES[i]);
                    }
                    return;
                case 2:
                    for (int i2 = 0; i2 < this.SHAFTVALUES.length; i2++) {
                        this.mSupportedOptionValue.add(this.SHAFTVALUES[i2]);
                    }
                    return;
                case 3:
                    this.mSupportedOptionValue.clear();
                    return;
                case 4:
                    for (int i3 = 0; i3 < 7; i3++) {
                        this.mSupportedOptionValue.add(this.RANGEVALUES[i3]);
                    }
                    return;
                default:
                    return;
            }
        }
        if (this.mSelectedValue.equals(LENGTH)) {
            for (int i4 = 0; i4 < 11; i4++) {
                this.mSupportedOptionValue.add(this.RANGEVALUES[i4]);
            }
            return;
        }
        if (this.mSelectedValue.equals(STRENGTH)) {
            for (int i5 = 0; i5 < 11; i5++) {
                this.mSupportedOptionValue.add(this.RANGEVALUES[i5]);
            }
        }
    }
}
