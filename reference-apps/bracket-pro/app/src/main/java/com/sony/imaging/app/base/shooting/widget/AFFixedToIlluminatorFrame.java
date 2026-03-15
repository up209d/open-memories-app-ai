package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;

/* loaded from: classes.dex */
public class AFFixedToIlluminatorFrame extends AbstractAFView {
    public AFFixedToIlluminatorFrame(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    protected String getAfAreaMode() {
        try {
            String currentAFareaMode = FocusAreaController.getInstance().getValue();
            return currentAFareaMode;
        } catch (IController.NotSupportedException e) {
            return FocusAreaController.FIX_CENTER;
        }
    }
}
