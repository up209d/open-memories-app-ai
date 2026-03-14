package com.sony.imaging.app.liveviewgrading.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.imaging.app.liveviewgrading.R;
import com.sony.imaging.app.liveviewgrading.menu.controller.ColorGradingController;

/* loaded from: classes.dex */
public class ColorGradingPresetIcon extends ActiveImage {
    private ColorGradingController mController;

    public ColorGradingPresetIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mController = null;
        this.mController = ColorGradingController.getInstance();
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        String curGroup = null;
        if (this.mController != null) {
            curGroup = this.mController.getLastSelectedGroup();
        }
        if (curGroup.equals(ColorGradingController.STANDARD)) {
            String presetGrpFirst = this.mController.getGroup1SelectedPreset();
            if (presetGrpFirst.equalsIgnoreCase(ColorGradingController.CLEAR)) {
                setBackgroundResource(R.drawable.p_16_dd_parts_lvg_preset_std_clear_ee_osd);
                return;
            }
            if (presetGrpFirst.equalsIgnoreCase(ColorGradingController.VIVID)) {
                setBackgroundResource(R.drawable.p_16_dd_parts_lvg_preset_std_vivid_ee_osd);
                return;
            } else if (presetGrpFirst.equalsIgnoreCase(ColorGradingController.MONOCHROME)) {
                setBackgroundResource(R.drawable.p_16_dd_parts_lvg_preset_std_mono_ee_osd);
                return;
            } else {
                if (presetGrpFirst.equalsIgnoreCase(ColorGradingController.BOLD)) {
                    setBackgroundResource(R.drawable.p_16_dd_parts_lvg_preset_std_bold_ee_osd);
                    return;
                }
                return;
            }
        }
        if (curGroup.equals(ColorGradingController.CINEMA)) {
            String presetGrpSecond = this.mController.getGroup2SelectedPreset();
            if (presetGrpSecond.equalsIgnoreCase(ColorGradingController.COAST_SIDE_LIGHT)) {
                setBackgroundResource(R.drawable.p_16_dd_parts_lvg_preset_cinema_coast_side_light_ee_osd);
                return;
            }
            if (presetGrpSecond.equalsIgnoreCase(ColorGradingController.SILKY)) {
                setBackgroundResource(R.drawable.p_16_dd_parts_lvg_preset_cinema_silky_ee_osd);
                return;
            } else if (presetGrpSecond.equalsIgnoreCase(ColorGradingController.MISTY_BLUE)) {
                setBackgroundResource(R.drawable.p_16_dd_parts_lvg_preset_cinema_misty_blue_ee_osd);
                return;
            } else {
                if (presetGrpSecond.equalsIgnoreCase(ColorGradingController.VELVETY_DEW)) {
                    setBackgroundResource(R.drawable.p_16_dd_parts_lvg_preset_cinema_velvety_dew_ee_osd);
                    return;
                }
                return;
            }
        }
        if (curGroup.equals(ColorGradingController.EXTREME)) {
            String presetGrpThird = this.mController.getGroup3SelectedPreset();
            if (presetGrpThird.equalsIgnoreCase(ColorGradingController._180)) {
                setBackgroundResource(R.drawable.p_16_dd_parts_lvg_preset_xtreme_180_ee_osd);
                return;
            }
            if (presetGrpThird.equalsIgnoreCase(ColorGradingController.SURF_TRIP)) {
                setBackgroundResource(R.drawable.p_16_dd_parts_lvg_preset_xtreme_surf_trip_ee_osd);
            } else if (presetGrpThird.equalsIgnoreCase(ColorGradingController.BIG_AIR)) {
                setBackgroundResource(R.drawable.p_16_dd_parts_lvg_preset_xtreme_big_air_ee_osd);
            } else if (presetGrpThird.equalsIgnoreCase(ColorGradingController.SNOW_TRICKS)) {
                setBackgroundResource(R.drawable.p_16_dd_parts_lvg_preset_xtreme_snow_tricks_ee_osd);
            }
        }
    }
}
