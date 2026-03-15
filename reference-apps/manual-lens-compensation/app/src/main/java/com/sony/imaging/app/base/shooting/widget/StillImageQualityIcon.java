package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.PictureQualityController;
import com.sony.imaging.app.base.shooting.camera.RawRecModeController;
import com.sony.imaging.app.util.NotificationListener;
import java.util.HashMap;

/* loaded from: classes.dex */
public class StillImageQualityIcon extends ActiveImage {
    private static final HashMap<String, Integer> ICONS = new HashMap<>();
    private static final Integer ICON_RAW_UNCOMPRESS;
    private static final Integer ICON_RAW_UNCOMPRESS_JPEG;
    private NotificationListener mPictureQualityListener;
    private TypedArray mTypedArray;

    static {
        ICONS.put(PictureQualityController.PICTURE_QUALITY_RAW, 0);
        ICONS.put(PictureQualityController.PICTURE_QUALITY_RAWJPEG, 1);
        ICONS.put(PictureQualityController.PICTURE_QUALITY_FINE, 4);
        ICONS.put(PictureQualityController.PICTURE_QUALITY_STANDARD, 5);
        ICONS.put(PictureQualityController.PICTURE_QUALITY_EXTRAFINE, 6);
        ICON_RAW_UNCOMPRESS = 2;
        ICON_RAW_UNCOMPRESS_JPEG = 3;
    }

    public StillImageQualityIcon(Context context) {
        this(context, null);
    }

    public StillImageQualityIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.StillImageQualityIcon);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        if (this.mPictureQualityListener == null) {
            this.mPictureQualityListener = new NotificationListener() { // from class: com.sony.imaging.app.base.shooting.widget.StillImageQualityIcon.1
                private String[] TAGS = {CameraNotificationManager.PICTURE_QUALITY, CameraNotificationManager.REC_MODE_CHANGED};

                @Override // com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    if (CameraNotificationManager.REC_MODE_CHANGED.equals(tag)) {
                        StillImageQualityIcon.this.setVisibility(StillImageQualityIcon.this.isVisible() ? 0 : 4);
                    }
                    StillImageQualityIcon.this.refresh();
                }

                @Override // com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return this.TAGS;
                }
            };
        }
        return this.mPictureQualityListener;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        int resId = 0;
        try {
            String quality = PictureQualityController.getInstance().getValue(null);
            if (quality != null) {
                Integer id = ICONS.get(quality);
                if (!this.mFnMode && "uncompress".equals(RawRecModeController.getInstance().getValue(null))) {
                    if (PictureQualityController.PICTURE_QUALITY_RAW.equals(quality)) {
                        id = ICON_RAW_UNCOMPRESS;
                    }
                    if (PictureQualityController.PICTURE_QUALITY_RAWJPEG.equals(quality)) {
                        id = ICON_RAW_UNCOMPRESS_JPEG;
                    }
                }
                if (id != null) {
                    resId = this.mTypedArray.getResourceId(id.intValue(), android.R.drawable.immersive_cling_bg_circ);
                }
            }
            if (resId == 0) {
                if (this.mFnMode) {
                    resId = android.R.drawable.dropdown_ic_arrow_normal_holo_dark;
                }
            }
        } catch (IController.NotSupportedException e) {
            e.printStackTrace();
        }
        setImageResource(resId);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected boolean isVisible() {
        return 1 == CameraSetting.getInstance().getCurrentMode();
    }
}
