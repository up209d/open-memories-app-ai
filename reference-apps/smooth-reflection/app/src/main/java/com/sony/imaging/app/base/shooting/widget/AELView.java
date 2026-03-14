package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.shooting.camera.AELController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.PTag;

/* loaded from: classes.dex */
public class AELView extends ActiveImage implements NotificationListener {
    private static final String LOG_DISPLAYED_AEL_ICON = "Displayed AEL Icon.";
    private static final String LOG_FAIL_TO_GET_DRAWABLE = "Fail to get drawable";
    private static final String TAG = "AELView";

    public AELView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AELView);
        try {
            setImageDrawable(typedArray.getDrawable(0));
        } catch (NullPointerException e) {
            Log.d(TAG, LOG_FAIL_TO_GET_DRAWABLE);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage, android.widget.ImageView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        if (AELController.getInstance().getAELockButtonState()) {
            setVisibility(0);
        } else {
            setVisibility(8);
        }
        PTag.end(LOG_DISPLAYED_AEL_ICON);
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return new String[]{CameraNotificationManager.AE_LOCK_BUTTON};
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        refresh();
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        return this;
    }
}
