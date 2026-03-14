package com.sony.imaging.app.digitalfilter.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFFilterSetController;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class GFFilterSetIcon extends ActiveImage implements NotificationListener {
    private static final String TAG = AppLog.getClassName();

    public GFFilterSetIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage, android.widget.ImageView, android.view.View
    public void onAttachedToWindow() {
        CameraNotificationManager.getInstance().setNotificationListener(this);
        super.onAttachedToWindow();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage, android.widget.ImageView, android.view.View
    public void onDetachedFromWindow() {
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        super.onDetachedFromWindow();
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        int drawable = -1;
        boolean isLand = GFCommonUtil.getInstance().isLand();
        boolean isSky = GFCommonUtil.getInstance().isSky();
        boolean isLayer3 = GFCommonUtil.getInstance().isLayer3();
        if (GFFilterSetController.getInstance().need3rdShooting()) {
            if (isLand) {
                drawable = R.drawable.p_16_dd_parts_skyhdr_filter_set_3area_1st;
            } else if (isSky) {
                drawable = R.drawable.p_16_dd_parts_skyhdr_filter_set_3area_2nd;
            } else if (isLayer3) {
                drawable = R.drawable.p_16_dd_parts_skyhdr_filter_set_3area_3rd;
            }
        } else if (isLand) {
            drawable = R.drawable.p_16_dd_parts_skyhdr_filter_set_2area_1st;
        } else {
            drawable = R.drawable.p_16_dd_parts_skyhdr_filter_set_2area_2nd;
        }
        if (drawable != -1) {
            setImageResource(drawable);
        }
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return new String[]{GFConstants.FILTER_SET_CHANGED};
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        refresh();
    }
}
