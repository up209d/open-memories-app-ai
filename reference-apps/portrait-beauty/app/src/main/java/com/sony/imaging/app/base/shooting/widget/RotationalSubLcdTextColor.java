package com.sony.imaging.app.base.shooting.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView;
import com.sony.imaging.app.base.shooting.camera.ProColorController;

/* loaded from: classes.dex */
public class RotationalSubLcdTextColor extends RotationalSubLcdTextView {
    private static final String CONTROLLER_TAG = "ProColor";
    private static final String TAG = RotationalSubLcdTextColor.class.getSimpleName();
    public Context mContext;

    public RotationalSubLcdTextColor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = null;
        this.mContext = context;
    }

    public RotationalSubLcdTextColor(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = null;
        this.mContext = context;
    }

    public RotationalSubLcdTextColor(Context context) {
        super(context);
        this.mContext = null;
        this.mContext = context;
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView, com.sony.imaging.app.base.common.widget.Rotational
    public boolean isValidValue() {
        String value = ProColorController.getInstance().getValue("ProColor");
        return value != null && value.equals(ProColorController.PRO_COLOR_MODE_NEUTRAL);
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView
    protected String makeText() {
        String ret = this.mText;
        String value = ProColorController.getInstance().getValue("ProColor");
        if (value != null && value.equals(ProColorController.PRO_COLOR_MODE_NEUTRAL)) {
            return this.mContext.getResources().getString(R.string.notification_channel_voice_mail);
        }
        return ret;
    }
}
