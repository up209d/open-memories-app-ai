package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.common.widget.SubLcdActiveText;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.NotificationManager;
import java.util.ArrayList;
import java.util.Arrays;

/* loaded from: classes.dex */
public abstract class ShootingSubLcdActiveText extends SubLcdActiveText {
    private static final String[] NOTIFIER_TAGS = {CameraNotificationManager.SCENE_MODE, CameraNotificationManager.REC_MODE_CHANGED};

    public ShootingSubLcdActiveText(Context context) {
        this(context, null);
    }

    public ShootingSubLcdActiveText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveText
    protected NotificationManager getNotificationManager() {
        return CameraNotificationManager.getInstance();
    }

    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveText
    protected NotificationListener getNotificationListener() {
        if (this.mListener == null) {
            this.mListener = new ActiveTextListener();
        }
        return this.mListener;
    }

    /* loaded from: classes.dex */
    protected class ActiveTextListener implements NotificationListener {
        /* JADX INFO: Access modifiers changed from: protected */
        public ActiveTextListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            boolean visible = ShootingSubLcdActiveText.this.isVisible();
            ShootingSubLcdActiveText.this.setOwnVisible(visible);
            if (visible) {
                ShootingSubLcdActiveText.this.refresh();
            }
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public final String[] getTags() {
            ArrayList<String> tags = new ArrayList<>();
            tags.addAll(Arrays.asList(ShootingSubLcdActiveText.NOTIFIER_TAGS));
            if (addTags() != null) {
                tags.addAll(Arrays.asList(addTags()));
            }
            return (String[]) tags.toArray(new String[(addTags() == null ? 0 : addTags().length) + ShootingSubLcdActiveText.NOTIFIER_TAGS.length]);
        }

        public String[] addTags() {
            return null;
        }
    }
}
