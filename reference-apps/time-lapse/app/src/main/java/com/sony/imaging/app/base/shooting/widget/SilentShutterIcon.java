package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.SilentShutterController;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class SilentShutterIcon extends ActiveImage {
    private static final String TAG = " SilentShutterIcon";
    private NotificationListener mListener;

    public SilentShutterIcon(Context context) {
        super(context);
        this.mListener = null;
    }

    public SilentShutterIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mListener = null;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        if (this.mListener == null) {
            this.mListener = new ActiveImage.ActiveImageListener() { // from class: com.sony.imaging.app.base.shooting.widget.SilentShutterIcon.1
                private String[] TAGS = {CameraNotificationManager.SILENT_SHUTTER_SETTING_CHANGED};

                @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener, com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    SilentShutterIcon.this.setVisibility(SilentShutterIcon.this.isVisible() ? 0 : 4);
                }

                @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener
                public String[] addTags() {
                    return this.TAGS;
                }
            };
        }
        return this.mListener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    public boolean isVisible() {
        SilentShutterController controller = SilentShutterController.getInstance();
        try {
            String value = controller.getValue(SilentShutterController.TAG_SILENT_SHUTTER);
            if (!"on".equals(value)) {
                return false;
            }
            return true;
        } catch (IController.NotSupportedException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
    }
}
