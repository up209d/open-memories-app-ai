package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class FocusModeIcon extends ActiveImage {
    private static final String LOG_INVALID = "Invalid value of AutoFocusModeSetting. Set FocusModeIcon to Invisible.";
    private static final String LOG_RESOURCE_NOT_FOUND = "Resource is not found. id = ";
    private static StringBuilder STRBUILD = new StringBuilder();
    private static final String[] TAGS = {CameraNotificationManager.FOCUS_CHANGE, "AutoFocusMode"};
    private String TAG;
    private NotificationListener mListener;
    private TypedArray mTypedArray;

    public FocusModeIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAG = "FocusModeIcon";
        this.mListener = null;
        this.mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.FocusModeIcon);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        if (this.mListener == null) {
            this.mListener = new ActiveImage.ActiveImageListener() { // from class: com.sony.imaging.app.base.shooting.widget.FocusModeIcon.1
                @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener, com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    if (CameraNotificationManager.FOCUS_CHANGE.equals(tag) || "AutoFocusMode".equals(tag)) {
                        FocusModeIcon.this.refresh();
                    } else {
                        super.onNotify(tag);
                    }
                }

                @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener
                public String[] addTags() {
                    return FocusModeIcon.TAGS;
                }
            };
        }
        return this.mListener;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        String fms = FocusModeController.getInstance().getValue();
        Drawable d = null;
        setVisibility(0);
        if ("af-s".equals(fms)) {
            d = obtainDrawable(1);
        } else if ("af-c".equals(fms)) {
            d = obtainDrawable(2);
        } else if (FocusModeController.MANUAL.equals(fms)) {
            d = obtainDrawable(3);
        } else if (FocusModeController.DMF.equals(fms)) {
            d = obtainDrawable(4);
        } else {
            Log.e(this.TAG, LOG_INVALID);
            setVisibility(4);
        }
        setImageDrawable(d);
    }

    private Drawable obtainDrawable(int id) {
        try {
            Drawable d = this.mTypedArray.getDrawable(id);
            return d;
        } catch (Resources.NotFoundException e) {
            Log.i(this.TAG, STRBUILD.replace(0, STRBUILD.length(), LOG_RESOURCE_NOT_FOUND).append(id).toString());
            return null;
        }
    }
}
