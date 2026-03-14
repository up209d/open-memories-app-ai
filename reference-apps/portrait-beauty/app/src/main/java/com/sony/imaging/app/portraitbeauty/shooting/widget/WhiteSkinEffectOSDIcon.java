package com.sony.imaging.app.portraitbeauty.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.imaging.app.portraitbeauty.R;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyBackUpKey;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyUtil;
import com.sony.imaging.app.portraitbeauty.menu.controller.PortraitBeautyWhiteSkinController;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class WhiteSkinEffectOSDIcon extends ActiveImage {
    private final String TAG;

    public WhiteSkinEffectOSDIcon(Context context, AttributeSet attrs) {
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
            PortraitBeautyWhiteSkinController.getInstance();
            int whiteSkinValue = BackUpUtil.getInstance().getPreferenceInt(PortraitBeautyBackUpKey.KEY_LEVEL_OF_WHITE_SKIN, 4);
            setVisibility(0);
            switch (whiteSkinValue) {
                case 1:
                    drawable = R.drawable.p_16_dd_parts_portraitbeauty_whiteskin_level1_s;
                    break;
                case 2:
                    drawable = R.drawable.p_16_dd_parts_portraitbeauty_whiteskin_level2_s;
                    break;
                case 3:
                    drawable = R.drawable.p_16_dd_parts_portraitbeauty_whiteskin_level3_s;
                    break;
                case 4:
                    drawable = R.drawable.p_16_dd_parts_portraitbeauty_whiteskin_level4_s;
                    break;
                case 5:
                    drawable = R.drawable.p_16_dd_parts_portraitbeauty_whiteskin_level5_s;
                    break;
                case 6:
                    drawable = R.drawable.p_16_dd_parts_portraitbeauty_whiteskin_level6_s;
                    break;
                case 7:
                    drawable = R.drawable.p_16_dd_parts_portraitbeauty_whiteskin_level7_s;
                    break;
                default:
                    drawable = R.drawable.p_16_dd_parts_portraitbeauty_whiteskin_off_s;
                    break;
            }
            setImageResource(drawable);
        } else {
            setVisibility(4);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }
}
