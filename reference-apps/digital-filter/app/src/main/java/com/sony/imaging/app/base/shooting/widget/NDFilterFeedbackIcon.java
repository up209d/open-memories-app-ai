package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.NDfilterController;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class NDFilterFeedbackIcon extends ActiveImage {
    private static String TAG = "NDFilterFeedbackIcon";

    public NDFilterFeedbackIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        Drawable drw = null;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NDFilterFeedbackIcon);
        try {
            drw = typedArray.getDrawable(0);
        } catch (Exception e) {
            setVisibility(4);
        }
        setImageDrawable(drw);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        return new NotificationListener() { // from class: com.sony.imaging.app.base.shooting.widget.NDFilterFeedbackIcon.1
            private String[] TAGS = {CameraNotificationManager.NDFILTER_STATUS_CHANGED};

            @Override // com.sony.imaging.app.util.NotificationListener
            public void onNotify(String tag) {
                NDFilterFeedbackIcon.this.setVisibility(NDFilterFeedbackIcon.this.isVisible() ? 0 : 4);
                NDFilterFeedbackIcon.this.refresh();
            }

            @Override // com.sony.imaging.app.util.NotificationListener
            public String[] getTags() {
                return this.TAGS;
            }
        };
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected boolean isVisible() {
        if (6 > CameraSetting.getPfApiVersion() || NDfilterController.getInstance().getSupportedValueList() == null) {
            return false;
        }
        String value = NDfilterController.getInstance().getValue(NDfilterController.STATUS_ID_NDFILTER);
        if ("on".equals(value)) {
            return true;
        }
        return false;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
    }
}
