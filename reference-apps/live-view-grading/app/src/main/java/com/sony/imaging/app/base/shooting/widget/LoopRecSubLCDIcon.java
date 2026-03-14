package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;

/* loaded from: classes.dex */
public class LoopRecSubLCDIcon extends ShootingSubLcdActiveIcon {
    private static String TAG = "LoopRecSubLCDIcon";

    public LoopRecSubLCDIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveIcon, com.sony.imaging.app.base.common.widget.AbstractSubLCDView
    public void refresh() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveIcon, com.sony.imaging.app.base.common.widget.AbstractSubLCDView
    public boolean isVisible() {
        boolean result;
        boolean loopRecModeSupported = ExecutorCreator.isLoopRecEnable();
        if (!loopRecModeSupported) {
            return false;
        }
        int recordingMode = ExecutorCreator.getInstance().getRecordingMode();
        if (8 == recordingMode) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }
}
