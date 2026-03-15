package com.sony.imaging.app.smoothreflection.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.widget.MediaInformationView;
import com.sony.imaging.app.smoothreflection.shooting.SmoothReflectionCompositProcess;

/* loaded from: classes.dex */
public class SmoothReflectionImageInfoView extends MediaInformationView {
    public SmoothReflectionImageInfoView(Context context) {
        super(context);
    }

    public SmoothReflectionImageInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.MediaInformationView
    public void showRemaining(int remaining) {
        super.showRemaining(SmoothReflectionCompositProcess.mRemaining);
    }
}
