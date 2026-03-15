package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.MicRefLevelController;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class MicRefLevelIcon extends ActiveImage {
    private static final String[] TAGS = {CameraNotificationManager.MIC_REF_LEVEL_CHANGED};

    public MicRefLevelIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MicRefLevel);
        Drawable d = null;
        try {
            d = typedArray.getDrawable(0);
        } catch (Exception e) {
        }
        setImageDrawable(d);
        typedArray.recycle();
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected boolean isVisible() {
        if (!Environment.isMovieAPISupported()) {
            return false;
        }
        MicRefLevelController controller = MicRefLevelController.getInstance();
        return !controller.isUnavailableSceneFactor(MicRefLevelController.REF_LEVEL) && "low".equals(controller.getValue(MicRefLevelController.REF_LEVEL));
    }

    /* loaded from: classes.dex */
    protected class Listener extends ActiveImage.ActiveImageListener {
        protected Listener() {
            super();
        }

        @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener
        public String[] addTags() {
            return MicRefLevelIcon.TAGS;
        }

        @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener, com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (CameraNotificationManager.MIC_REF_LEVEL_CHANGED.equals(tag)) {
                MicRefLevelIcon.this.setVisibility(MicRefLevelIcon.this.isVisible() ? 0 : 4);
            } else {
                super.onNotify(tag);
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        return new Listener();
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
    }
}
