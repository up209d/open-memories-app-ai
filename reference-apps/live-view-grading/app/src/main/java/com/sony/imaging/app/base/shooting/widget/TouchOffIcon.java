package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.sony.scalar.sysutil.ScalarProperties;
import com.sony.scalar.sysutil.didep.Settings;

/* loaded from: classes.dex */
public class TouchOffIcon extends ImageView {
    private boolean mIsTouchPanelModel;

    public TouchOffIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        int isSupporeted = ScalarProperties.getInt("input.tp.type");
        this.mIsTouchPanelModel = isSupporeted == 1;
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        refresh();
    }

    public void refresh() {
        int visibility = 4;
        if (this.mIsTouchPanelModel) {
            if (Settings.getTouchPanelEnabled() != 1) {
                visibility = 4;
            } else {
                visibility = 0;
            }
        }
        setVisibility(visibility);
    }
}
