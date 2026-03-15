package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.PictureQualityController;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.util.NotificationListener;
import java.util.HashMap;

/* loaded from: classes.dex */
public class StillImageSizeIcon extends ActiveImage {
    protected static final HashMap<String, Integer> ICONS = new HashMap<>();
    private NotificationListener mPictureSizeListener;
    protected TypedArray mTypedArray;

    static {
        ICONS.put(PictureSizeController.SIZE_L, 0);
        ICONS.put(PictureSizeController.SIZE_M, 1);
        ICONS.put(PictureSizeController.SIZE_S, 2);
        ICONS.put(PictureSizeController.SIZE_VGA, 3);
    }

    public StillImageSizeIcon(Context context) {
        this(context, null);
    }

    public StillImageSizeIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.StillImageSizeIcon);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        if (this.mPictureSizeListener == null) {
            this.mPictureSizeListener = new NotificationListener() { // from class: com.sony.imaging.app.base.shooting.widget.StillImageSizeIcon.1
                private String[] TAGS = {CameraNotificationManager.PICTURE_SIZE, CameraNotificationManager.REC_MODE_CHANGED, CameraNotificationManager.PICTURE_QUALITY};

                @Override // com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    if (CameraNotificationManager.REC_MODE_CHANGED.equals(tag)) {
                        StillImageSizeIcon.this.setVisibility(StillImageSizeIcon.this.isVisible() ? 0 : 4);
                    }
                    StillImageSizeIcon.this.refresh();
                }

                @Override // com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return this.TAGS;
                }
            };
        }
        return this.mPictureSizeListener;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        Integer id;
        int resId = 0;
        try {
            String imagesize = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_IMAGE_SIZE);
            String quality = PictureQualityController.getInstance().getValue(null);
            if (!PictureQualityController.PICTURE_QUALITY_RAW.equals(quality) && (id = ICONS.get(imagesize)) != null) {
                resId = this.mTypedArray.getResourceId(id.intValue(), 17305977);
            }
            if (resId == 0) {
                if (this.mFnMode) {
                    resId = 17303951;
                }
            }
        } catch (IController.NotSupportedException e) {
            e.printStackTrace();
        }
        setImageResource(resId);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    public boolean isVisible() {
        return 1 == CameraSetting.getInstance().getCurrentMode();
    }
}
