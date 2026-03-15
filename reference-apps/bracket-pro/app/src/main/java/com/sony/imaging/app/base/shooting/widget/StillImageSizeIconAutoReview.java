package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.StringBuilderThreadLocal;

/* loaded from: classes.dex */
public class StillImageSizeIconAutoReview extends StillImageSizeIcon {
    private static final String LOG_MSG_INVALID_ARGUMENT = "Invalid argument. ";
    private static final String LOG_MSG_VALUE = "value = ";
    public static final int SIZE_L = 0;
    public static final int SIZE_M = 1;
    public static final int SIZE_S = 2;
    public static final int SIZE_VGA = 3;
    private String TAG;

    public StillImageSizeIconAutoReview(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAG = "StillImageIconAutoReview";
    }

    @Override // com.sony.imaging.app.base.shooting.widget.StillImageSizeIcon, com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        return null;
    }

    public void setValue(long value) {
        int resId = 0;
        if (value == 0) {
            resId = this.mTypedArray.getResourceId(ICONS.get(PictureSizeController.SIZE_L).intValue(), 17305973);
        } else if (value == 1) {
            resId = this.mTypedArray.getResourceId(ICONS.get(PictureSizeController.SIZE_M).intValue(), 17305977);
        } else if (value == 2) {
            resId = this.mTypedArray.getResourceId(ICONS.get(PictureSizeController.SIZE_S).intValue(), 17305965);
        } else if (value == 3) {
            resId = this.mTypedArray.getResourceId(ICONS.get(PictureSizeController.SIZE_VGA).intValue(), 17306091);
        } else {
            StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
            builder.replace(0, builder.length(), LOG_MSG_INVALID_ARGUMENT).append(LOG_MSG_VALUE).append(value);
            Log.w(this.TAG, builder.toString());
            StringBuilderThreadLocal.releaseScratchBuilder(builder);
        }
        if (resId != 0) {
            setImageResource(resId);
        }
    }
}
