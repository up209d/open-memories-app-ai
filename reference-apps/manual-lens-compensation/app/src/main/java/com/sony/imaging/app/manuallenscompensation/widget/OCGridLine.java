package com.sony.imaging.app.manuallenscompensation.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.widget.GridLine;
import com.sony.scalar.sysutil.didep.Settings;

/* loaded from: classes.dex */
public class OCGridLine extends GridLine {
    static final String TAG = "MLCGridLine";

    public OCGridLine(Context context) {
        super(context);
    }

    public OCGridLine(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.GridLine
    protected void refresh() {
        int i;
        this.mGridMode = Settings.getGridLine();
        if (this.mGridMode == 0) {
            i = 2;
            this.mGridMode = 2;
        } else {
            i = this.mGridMode;
        }
        this.mGridMode = i;
        setVisibleGridLine();
        invalidate();
    }
}
