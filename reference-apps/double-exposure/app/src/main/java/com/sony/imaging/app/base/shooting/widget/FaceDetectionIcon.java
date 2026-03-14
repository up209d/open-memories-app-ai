package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.FaceDetectionController;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class FaceDetectionIcon extends ActiveImage {
    private static String TAG = "FaceDetectionIcon";
    private static final String[] TAGS = {CameraNotificationManager.FACE_DETECTION_MODE};
    private Drawable mIconOff;
    private Drawable mIconOn;
    private NotificationListener mListener;

    public FaceDetectionIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mListener = null;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FaceDetectionIcon);
        this.mIconOn = a.getDrawable(0);
        this.mIconOff = a.getDrawable(1);
        a.recycle();
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        if (this.mListener == null) {
            this.mListener = new ActiveImage.ActiveImageListener() { // from class: com.sony.imaging.app.base.shooting.widget.FaceDetectionIcon.1
                @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener, com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    FaceDetectionIcon.this.refresh();
                }

                @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener
                public String[] addTags() {
                    return FaceDetectionIcon.TAGS;
                }
            };
        }
        return this.mListener;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        try {
            String value = FaceDetectionController.getInstance().getValue();
            Drawable d = null;
            if ("auto".equals(value)) {
                d = this.mIconOn;
            } else if ("off".equals(value)) {
                d = this.mIconOff;
            } else {
                Log.e(TAG, "Fatal error in FocusModeIcon");
            }
            setImageDrawable(d);
        } catch (IController.NotSupportedException e) {
            Log.w(TAG, "Not supported value. So, make icon of face mode invisible.");
            setVisibility(4);
        }
    }
}
