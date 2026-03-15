package com.sony.imaging.app.base.shooting.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView;
import com.sony.imaging.app.base.shooting.camera.FlipController;

/* loaded from: classes.dex */
public class RotationalSubLcdTextFlip extends RotationalSubLcdTextView {
    private static final String TAG = RotationalSubLcdTextFlip.class.getSimpleName();

    public RotationalSubLcdTextFlip(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RotationalSubLcdTextFlip(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RotationalSubLcdTextFlip(Context context) {
        super(context);
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView, com.sony.imaging.app.base.common.widget.Rotational
    public boolean isValidValue() {
        String value = FlipController.getInstance().getValue(FlipController.FLIP);
        return "On".equals(value);
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView
    protected String makeText() {
        String value = FlipController.getInstance().getValue(FlipController.FLIP);
        return "On".equals(value) ? getResources().getString(R.string.media_route_controller_disconnect) : "";
    }
}
