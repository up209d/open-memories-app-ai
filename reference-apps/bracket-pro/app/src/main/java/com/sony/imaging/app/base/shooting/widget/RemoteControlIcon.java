package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;

/* loaded from: classes.dex */
public class RemoteControlIcon extends ImageView {
    private boolean mIsTouchPanelModel;

    public RemoteControlIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        refresh();
    }

    public void refresh() {
        int visibility = 4;
        if (DriveModeController.getInstance().isRemoteControl()) {
            visibility = 0;
        }
        setVisibility(visibility);
    }
}
