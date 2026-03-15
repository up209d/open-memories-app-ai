package com.sony.imaging.app.base.menu.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.FlashController;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import java.util.List;

/* loaded from: classes.dex */
public class Fn15LayerFlashCompensationLayout extends Fn15LayerCompensationLayout {
    public static final String MENU_ID = "ID_FN15LAYERFLASHCOMPENSATIONLAYOUT";
    private static final int RANGE_FOR_GAUGE_GUIDE_2 = 13;
    private static final int RANGE_FOR_GAUGE_GUIDE_3 = 19;
    private static final String STR_ZERO = "0";
    private static final int UNIT = 10;
    private FlashController mController;
    private int mLevelOfZero;
    private int mSlider_Measure;
    private List<String> mSupportedValues;

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerCompensationLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mController = FlashController.getInstance();
        this.mSupportedValues = this.mController.getSupportedValue(FlashController.FLASH_COMPENSATION);
        this.mLevelOfZero = 0;
        if (this.mSupportedValues != null && this.mSupportedValues.size() != 0) {
            this.mLevelOfZero = this.mSupportedValues.indexOf("0");
        }
        View mCurrentView = super.onCreateView(inflater, container, savedInstanceState);
        return mCurrentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerCompensationLayout
    protected int getMenuKind() {
        int device = DisplayModeObserver.getInstance().getActiveDevice();
        if (device == 0 || device == 2 || device != 1) {
            return 2;
        }
        return 3;
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerCompensationLayout
    protected View createCurrentView(LayoutInflater inflater) {
        return obtainViewFromPool(R.layout.menu_fn15layer_flash_comp);
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerCompensationLayout
    protected int getGuideType() {
        int range = getWheelRange();
        switch (range) {
            case 13:
                if (this.mController.getmStep() == 5.0f) {
                    this.mSlider_Measure = 2;
                    break;
                } else {
                    this.mSlider_Measure = 3;
                    break;
                }
            case 19:
                this.mSlider_Measure = 2;
                break;
            default:
                this.mSlider_Measure = 0;
                break;
        }
        return this.mSlider_Measure;
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerCompensationLayout
    protected int getWheelRange() {
        if (this.mSupportedValues == null) {
            return 0;
        }
        int range = this.mSupportedValues.size();
        return range;
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerCompensationLayout
    protected int getCompensationLevel() {
        String str = this.mController.getValue(FlashController.FLASH_COMPENSATION);
        int level = Integer.parseInt(str) + this.mLevelOfZero;
        return level;
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerCompensationLayout
    protected void setCompensationLevel(int level) {
        String setValue = String.valueOf(level - this.mLevelOfZero);
        this.mController.setValue(FlashController.FLASH_COMPENSATION, setValue);
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerCompensationLayout
    protected String getInformationText(int level) {
        if (level == this.mLevelOfZero) {
            return getResources().getString(android.R.string.restr_pin_confirm_pin);
        }
        int index = level - this.mLevelOfZero;
        float value = this.mController.calcFlashCompensationValue(index);
        if (value > TimeLapseConstants.INVALID_APERTURE_VALUE) {
            return String.format(getResources().getString(17041743), Integer.valueOf(((int) value) / 1), Integer.valueOf(((int) (value * 10.0f)) % 10));
        }
        float value2 = Math.abs(value);
        return String.format(getResources().getString(17041717), Integer.valueOf(((int) value2) / 1), Integer.valueOf(((int) (value2 * 10.0f)) % 10));
    }
}
