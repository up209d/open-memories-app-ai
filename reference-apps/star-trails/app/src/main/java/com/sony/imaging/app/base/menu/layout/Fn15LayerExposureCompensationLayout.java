package com.sony.imaging.app.base.menu.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.widget.CompensationWheelAndInfoView;
import com.sony.imaging.app.startrails.util.STConstants;
import java.util.List;

/* loaded from: classes.dex */
public class Fn15LayerExposureCompensationLayout extends Fn15LayerCompensationLayout {
    public static final String MENU_ID = "ID_FN15LAYEREXPOSURECOMPENSATIONLAYOUT";
    private static final String STR_ZERO = "0";
    private ExposureCompensationController mController;
    private int mLevelOfZero;

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerCompensationLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mController = ExposureCompensationController.getInstance();
        List<String> support = this.mController.getSupportedValueInMode(1);
        if (support != null && support.size() != 0) {
            this.mLevelOfZero = support.indexOf("0");
        }
        View mCurrentView = super.onCreateView(inflater, container, savedInstanceState);
        return mCurrentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerCompensationLayout
    protected int getMenuKind() {
        List<String> support = this.mController.getSupportedValue("ExposureCompensation");
        if (support != null && support.size() != 0) {
            return 1;
        }
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerCompensationLayout
    protected View createCurrentView(LayoutInflater inflater) {
        return obtainViewFromPool(R.layout.menu_fn15layer_exposure_comp);
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerCompensationLayout
    protected int getGuideType() {
        List<String> supported;
        if (!getMovieMode()) {
            supported = this.mController.getSupportedValue(null);
        } else {
            supported = this.mController.getSupportedValueInMode(1);
        }
        if (supported == null) {
            return 0;
        }
        float range = Math.abs(this.mController.calcExposureCompensationValue(Integer.parseInt(supported.get(0))));
        int guideType = CompensationWheelAndInfoView.getGaugeGuideType(range);
        return guideType;
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerCompensationLayout
    protected int getWheelRange() {
        List<String> supported;
        if (!getMovieMode()) {
            supported = this.mController.getSupportedValue(null);
        } else {
            supported = this.mController.getSupportedValueInMode(1);
        }
        if (supported == null) {
            return 0;
        }
        int supportedCount = supported.size();
        return supportedCount;
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerCompensationLayout
    protected int getCompensationLevel() {
        try {
            String str = this.mController.getValue(null);
            int level = Integer.parseInt(str) + this.mLevelOfZero;
            return level;
        } catch (IController.NotSupportedException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerCompensationLayout
    protected void setCompensationLevel(int level) {
        this.mController.setValue(null, Integer.toString(level - this.mLevelOfZero));
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerCompensationLayout
    protected String getInformationText(int level) {
        if (level == this.mLevelOfZero) {
            return getResources().getString(android.R.string.restr_pin_enter_new_pin);
        }
        int index = level - this.mLevelOfZero;
        float value = this.mController.calcExposureCompensationValue(index);
        if (value > STConstants.INVALID_APERTURE_VALUE) {
            return String.format(getResources().getString(17041720), Integer.valueOf(((int) value) / 1), Integer.valueOf(((int) (value * 10.0f)) % 10));
        }
        float value2 = Math.abs(value);
        return String.format(getResources().getString(17041719), Integer.valueOf(((int) value2) / 1), Integer.valueOf(((int) (value2 * 10.0f)) % 10));
    }
}
