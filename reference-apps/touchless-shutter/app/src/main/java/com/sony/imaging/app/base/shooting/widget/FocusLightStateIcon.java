package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class FocusLightStateIcon extends ActiveImage {
    private boolean mIsGone;
    private NotificationListener mListener;
    private TypedArray mTypedArray;

    public FocusLightStateIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mListener = null;
        this.mIsGone = false;
        this.mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.FocusLightStateIcon);
        setImageDrawable(this.mTypedArray.getDrawable(0));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage, android.widget.ImageView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isPFverOver2()) {
            int category = ScalarProperties.getInt("model.category");
            if (category == 0) {
                this.mIsGone = true;
            }
        }
        refresh();
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        if (this.mListener == null) {
            this.mListener = new NotificationListener() { // from class: com.sony.imaging.app.base.shooting.widget.FocusLightStateIcon.1
                private final String[] tags = {CameraNotificationManager.FOCUS_LIGHT_STATE, CameraNotificationManager.REC_MODE_CHANGED};

                @Override // com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    FocusLightStateIcon.this.refresh();
                }

                @Override // com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return this.tags;
                }
            };
        }
        return this.mListener;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        int visibility;
        if (this.mIsGone) {
            visibility = 8;
        } else if (2 == CameraSetting.getInstance().getCurrentMode()) {
            visibility = 8;
        } else {
            CameraNotificationManager.FocusLightStateInfo info = (CameraNotificationManager.FocusLightStateInfo) this.mNotifier.getValue(CameraNotificationManager.FOCUS_LIGHT_STATE);
            if (info != null) {
                visibility = (info.on || info.ready) ? 0 : 8;
            } else {
                visibility = 8;
            }
        }
        setVisibility(visibility);
        invalidate();
    }

    public static boolean isPFverOver2() {
        if (2 > CameraSetting.getPfApiVersion()) {
            return false;
        }
        return true;
    }
}
