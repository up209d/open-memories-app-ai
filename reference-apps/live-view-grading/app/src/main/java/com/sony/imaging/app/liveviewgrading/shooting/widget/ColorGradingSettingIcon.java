package com.sony.imaging.app.liveviewgrading.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.imaging.app.liveviewgrading.R;
import com.sony.imaging.app.liveviewgrading.menu.controller.ColorGradingController;

/* loaded from: classes.dex */
public class ColorGradingSettingIcon extends ActiveImage {
    public static final String mTag1 = "movieStarted";
    public static final String mTag2 = "movieStopped";
    private LVGActiveImageListener mListener;

    public ColorGradingSettingIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mListener = null;
        this.mListener = new LVGActiveImageListener();
        CameraNotificationManager.getInstance().setNotificationListener(this.mListener);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        setBackgroundResource(R.drawable.p_16_dd_parts_lvg_ee_delete_kye_func_osd);
    }

    /* loaded from: classes.dex */
    class LVGActiveImageListener extends ActiveImage.ActiveImageListener {
        LVGActiveImageListener() {
            super();
        }

        @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener, com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if ("movieStarted".equals(tag)) {
                ColorGradingSettingIcon.this.setVisibility(4);
            } else if ("movieStopped".equals(tag)) {
                ColorGradingSettingIcon.this.setVisibility(0);
            }
            super.onNotify(tag);
        }

        @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener
        public String[] addTags() {
            String[] recordingTags = {"movieStarted", "movieStopped"};
            return recordingTags;
        }
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected boolean isVisible() {
        return ColorGradingController.getInstance().isRecStopped();
    }
}
