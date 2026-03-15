package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.widget.ActiveLayout;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class FocusGuideFocusSetView extends ActiveLayout {
    private ActiveLayout.ActiveLayoutListener mListener;

    public FocusGuideFocusSetView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout
    public boolean isVisible() {
        if (isMovieMode()) {
            return false;
        }
        String fms = FocusModeController.getInstance().getValue();
        if (!FocusModeController.MANUAL.equals(fms) && !FocusModeController.SMF.equals(fms)) {
            return false;
        }
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout
    protected void refresh() {
        setVisibility(isVisible() ? 0 : 4);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout
    protected NotificationListener getNotificationListener() {
        if (this.mListener == null) {
            this.mListener = new ActiveLayout.ActiveLayoutListener() { // from class: com.sony.imaging.app.base.shooting.widget.FocusGuideFocusSetView.1
                @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout.ActiveLayoutListener, com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    if (CameraNotificationManager.FOCUS_CHANGE.equals(tag) || "AutoFocusMode".equals(tag)) {
                        super.onNotify(tag);
                        FocusGuideFocusSetView.this.refresh();
                    }
                }

                @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout.ActiveLayoutListener
                public String[] addTags() {
                    return new String[]{CameraNotificationManager.FOCUS_CHANGE, "AutoFocusMode"};
                }
            };
        }
        return this.mListener;
    }

    protected boolean isMovieMode() {
        return Environment.isMovieAPISupported() && 2 == ExecutorCreator.getInstance().getRecordingMode();
    }
}
