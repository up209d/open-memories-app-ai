package com.sony.imaging.app.lightgraffiti.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.shooting.widget.ExposureModeIconView;
import com.sony.imaging.app.lightgraffiti.R;

/* loaded from: classes.dex */
public class LGExposureModeIconView extends ExposureModeIconView {
    private static final String TAG = LGExposureModeIconView.class.getSimpleName();

    public LGExposureModeIconView(Context context) {
        super(context);
    }

    public LGExposureModeIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        context.obtainStyledAttributes(attrs, R.styleable.ExposureModeIconView);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ExposureModeIconView
    protected void updateIcon() {
        Log.d(TAG, "updateIcon()");
        setImageDrawable(null);
    }
}
