package com.sony.imaging.app.portraitbeauty.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.imaging.app.portraitbeauty.R;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyBackUpKey;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyUtil;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class DefocusEffectOSDIcon extends ActiveImage {
    private final String TAG;

    public DefocusEffectOSDIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAG = AppLog.getClassName();
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (!PortraitBeautyUtil.bIsAdjustModeGuide) {
            setVisibility(0);
            String defocusValue = BackUpUtil.getInstance().getPreferenceString(PortraitBeautyBackUpKey.KEY_LEVEL_OF_DEFOCUS, "-1");
            int drawable = setImage(Integer.parseInt(defocusValue));
            setImageResource(drawable);
        } else {
            setVisibility(4);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private int setImage(int number) {
        switch (number) {
            case 1:
                return R.drawable.p_16_dd_parts_portraitbeauty_bkgrounddefocus_level1_s;
            case 2:
                return R.drawable.p_16_dd_parts_portraitbeauty_bkgrounddefocus_level2_s;
            case 3:
                return R.drawable.p_16_dd_parts_portraitbeauty_bkgrounddefocus_level3_s;
            case 4:
                return R.drawable.p_16_dd_parts_portraitbeauty_bkgrounddefocus_level4_s;
            case 5:
                return R.drawable.p_16_dd_parts_portraitbeauty_bkgrounddefocus_level5_s;
            case 6:
                return R.drawable.p_16_dd_parts_portraitbeauty_bkgrounddefocus_level6_s;
            case 7:
                return R.drawable.p_16_dd_parts_portraitbeauty_bkgrounddefocus_level7_s;
            case 8:
                return R.drawable.p_16_dd_parts_portraitbeauty_bkgrounddefocus_level8_s;
            case 9:
                return R.drawable.p_16_dd_parts_portraitbeauty_bkgrounddefocus_level9_s;
            case 10:
                return R.drawable.p_16_dd_parts_portraitbeauty_bkgrounddefocus_level10_s;
            default:
                return R.drawable.p_16_dd_parts_portraitbeauty_bkgrounddefocus_auto_s;
        }
    }
}
