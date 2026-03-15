package com.sony.imaging.app.lightgraffiti.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.widget.MediaInformationView;
import com.sony.imaging.app.lightgraffiti.util.LGUtility;

/* loaded from: classes.dex */
public class LGMediaInformationView extends MediaInformationView {
    public LGMediaInformationView(Context context) {
        super(context);
    }

    public LGMediaInformationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.MediaInformationView
    public void showRemaining(int remaining) {
        LGUtility.getInstance().calculateRemainingMemory();
        super.showRemaining(LGUtility.getInstance().getAvailableRemainingShot());
    }
}
