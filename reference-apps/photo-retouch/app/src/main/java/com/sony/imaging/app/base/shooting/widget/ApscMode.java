package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.ApscModeController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class ApscMode extends ActiveImage {
    private static final String[] LISTENING_TAGS = {CameraNotificationManager.APSC_MODE_CHANGED};

    public ApscMode(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected boolean isVisible() {
        ApscModeController controller = ApscModeController.getInstance();
        if (!controller.isAvailable(ApscModeController.TAG_APSC_MODE_CONDITION)) {
            return false;
        }
        try {
            String value = controller.getValue(ApscModeController.TAG_APSC_MODE_CONDITION);
            if (!"on".equals(value)) {
                return false;
            }
            return true;
        } catch (IController.NotSupportedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* loaded from: classes.dex */
    private class ApscChangedListener extends ActiveImage.ActiveImageListener {
        private ApscChangedListener() {
            super();
        }

        @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener
        public String[] addTags() {
            return ApscMode.LISTENING_TAGS;
        }

        @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener, com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            ApscMode.this.setVisibility(ApscMode.this.isVisible() ? 0 : 4);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        return new ApscChangedListener();
    }
}
