package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.AntiHandBlurController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.widget.ShootingSubLcdActiveIcon;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class SteadyShotSubLCDIcon extends ShootingSubLcdActiveIcon {
    private static final String TAG = "SteadyShotSubLCDIcon";
    private final String[] TAGS;

    public SteadyShotSubLCDIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAGS = new String[]{"AntiHandBlur"};
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveIcon, com.sony.imaging.app.base.common.widget.AbstractSubLCDView
    public boolean isVisible() {
        int recordingMode = ExecutorCreator.getInstance().getRecordingMode();
        if (recordingMode == 2 || recordingMode == 8) {
            String value = AntiHandBlurController.getInstance().getValue("movie");
            if ("off".equals(value)) {
                return false;
            }
            return true;
        }
        Log.d(TAG, "When ShootingMode is Still, SteadyshootIcon is not supported.");
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveIcon, com.sony.imaging.app.base.common.widget.AbstractSubLCDView
    public void refresh() {
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ShootingSubLcdActiveIcon, com.sony.imaging.app.base.common.widget.SubLcdActiveIcon
    protected NotificationListener getNotificationListener() {
        if (this.mListener == null) {
            this.mListener = new CameraNotificationListener();
        }
        return this.mListener;
    }

    /* loaded from: classes.dex */
    protected class CameraNotificationListener extends ShootingSubLcdActiveIcon.ActiveIconListener {
        protected CameraNotificationListener() {
            super();
        }

        @Override // com.sony.imaging.app.base.shooting.widget.ShootingSubLcdActiveIcon.ActiveIconListener
        public String[] addTags() {
            return SteadyShotSubLCDIcon.this.TAGS;
        }
    }
}
