package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class StillImageAspectratioIcon extends ActiveImage {
    private static final String LOG_MSG_ASPECT = "aspect = ";
    private static final String LOG_MSG_ILLEGAL_SETTING = "Illegal setting. ";
    private static final StringBuilder STRBUILD = new StringBuilder();
    private String TAG;
    private NotificationListener mPictureSizeListener;
    private TypedArray mTypedArray;

    public StillImageAspectratioIcon(Context context) {
        this(context, null);
    }

    public StillImageAspectratioIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAG = "StillImageIcon";
        this.mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.StillImageAspectratioIcon);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        if (this.mPictureSizeListener == null) {
            this.mPictureSizeListener = new NotificationListener() { // from class: com.sony.imaging.app.base.shooting.widget.StillImageAspectratioIcon.1
                private String[] TAGS = {CameraNotificationManager.PICTURE_SIZE, CameraNotificationManager.REC_MODE_CHANGED};

                @Override // com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    if (CameraNotificationManager.REC_MODE_CHANGED.equals(tag)) {
                        StillImageAspectratioIcon.this.setVisibility(StillImageAspectratioIcon.this.isVisible() ? 0 : 4);
                    }
                    StillImageAspectratioIcon.this.refresh();
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
        String aspect_rate = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
        int resId = 0;
        if (PictureSizeController.ASPECT_3_2.equals(aspect_rate)) {
            resId = this.mTypedArray.getResourceId(0, 17305632);
        } else if (PictureSizeController.ASPECT_16_9.equals(aspect_rate)) {
            resId = this.mTypedArray.getResourceId(1, 17303924);
        } else if (PictureSizeController.ASPECT_4_3.equals(aspect_rate)) {
            resId = this.mTypedArray.getResourceId(2, 17306269);
        } else if (PictureSizeController.ASPECT_1_1.equals(aspect_rate)) {
            resId = this.mTypedArray.getResourceId(3, 17306178);
        }
        if (resId == 0 && this.mFnMode) {
            resId = 17305755;
        }
        if (resId == 0) {
            STRBUILD.replace(0, STRBUILD.length(), LOG_MSG_ILLEGAL_SETTING).append(LOG_MSG_ASPECT).append(aspect_rate);
            Log.w(this.TAG, STRBUILD.toString());
        }
        setImageResource(resId);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected boolean isVisible() {
        return 1 == CameraSetting.getInstance().getCurrentMode();
    }
}
