package com.sony.imaging.app.portraitbeauty.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.imaging.app.portraitbeauty.R;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyBackUpKey;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyUtil;
import com.sony.imaging.app.portraitbeauty.menu.controller.PortraitBeautySoftSkinController;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class SoftSkinEffectOSDIcon extends ActiveImage {
    private final String TAG;

    public SoftSkinEffectOSDIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAG = AppLog.getClassName();
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        int drawable;
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (!PortraitBeautyUtil.bIsAdjustModeGuide) {
            PortraitBeautySoftSkinController.getInstance();
            String softSkinValue = BackUpUtil.getInstance().getPreferenceString(PortraitBeautyBackUpKey.KEY_LEVEL_OF_SOFT_SKIN, PortraitBeautySoftSkinController.SOFTSKIN_MID);
            setVisibility(0);
            if (PortraitBeautySoftSkinController.SOFTSKIN_HIGH.equals(softSkinValue)) {
                drawable = R.drawable.p_16_dd_parts_portraitbeauty_softskin_high_s;
            } else if (PortraitBeautySoftSkinController.SOFTSKIN_MID.equals(softSkinValue)) {
                drawable = R.drawable.p_16_dd_parts_portraitbeauty_softskin_mid_s;
            } else if (PortraitBeautySoftSkinController.SOFTSKIN_LOW.equals(softSkinValue)) {
                drawable = R.drawable.p_16_dd_parts_portraitbeauty_softskin_low_s;
            } else {
                drawable = R.drawable.p_16_dd_parts_portraitbeauty_softskin_off_s;
            }
            setImageResource(drawable);
        } else {
            setVisibility(4);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }
}
