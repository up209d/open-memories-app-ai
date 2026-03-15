package com.sony.imaging.app.startrails.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.widget.MediaInformationView;
import com.sony.imaging.app.startrails.util.STUtility;

/* loaded from: classes.dex */
public class STMediaInformationView extends MediaInformationView {
    public STMediaInformationView(Context context) {
        super(context);
    }

    public STMediaInformationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.MediaInformationView
    public void showRemaining(int remaining) {
        STUtility.getInstance().calculateRemainingMemory();
        super.showRemaining(STUtility.getInstance().getAvailableRemainingShot());
    }
}
