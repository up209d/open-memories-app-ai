package com.sony.imaging.app.portraitbeauty.shooting.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.widget.ActiveText;
import com.sony.imaging.app.portraitbeauty.R;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class AdjustModeMessageWidget extends ActiveText {
    Handler mHandler;

    public AdjustModeMessageWidget(Context context) {
        super(context);
        this.mHandler = null;
    }

    public AdjustModeMessageWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mHandler = null;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveText
    protected void refresh() {
        setText(getResources().getString(R.string.STRID_FUNC_SELFIE_ADJUSTVISUALLY_STEP1));
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveText
    protected NotificationListener getNotificationListener() {
        return null;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveText
    protected boolean isVisible() {
        return true;
    }
}
