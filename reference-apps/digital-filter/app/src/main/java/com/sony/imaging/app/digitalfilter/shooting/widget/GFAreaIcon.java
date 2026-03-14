package com.sony.imaging.app.digitalfilter.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFEEAreaController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFFilterSetController;

/* loaded from: classes.dex */
public class GFAreaIcon extends ActiveImage {
    private static final String TAG = AppLog.getClassName();

    public GFAreaIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        AppLog.enter(TAG, AppLog.getMethodName());
        int drawable = -1;
        if (GFFilterSetController.getInstance().need3rdShooting()) {
            if (GFEEAreaController.getInstance().isLand()) {
                drawable = R.drawable.p_16_dd_parts_skyhdr_info_1st_3area;
            } else if (GFEEAreaController.getInstance().isSky()) {
                drawable = R.drawable.p_16_dd_parts_skyhdr_info_2nd_3area;
            } else if (GFEEAreaController.getInstance().isLayer3()) {
                drawable = R.drawable.p_16_dd_parts_skyhdr_info_3rd_3area;
            }
        } else if (GFEEAreaController.getInstance().isLand()) {
            drawable = R.drawable.p_16_dd_parts_skyhdr_info_1st_2area;
        } else if (GFEEAreaController.getInstance().isSky()) {
            drawable = R.drawable.p_16_dd_parts_skyhdr_info_2nd_2area;
        }
        if (drawable != -1) {
            setImageResource(drawable);
            setVisibility(0);
        } else {
            setVisibility(4);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }
}
