package com.sony.imaging.app.digitalfilter.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.shooting.camera.TouchLessShutterController;

/* loaded from: classes.dex */
public class TouchLessIcon extends ActiveImage {
    private static final String TAG = "TouchLessIcon";

    public TouchLessIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected boolean isVisible() {
        return TouchLessShutterController.getInstance().getValue(null).equals("On");
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        setImageResource(R.drawable.p_16_dd_parts_skyhdr_touchless_shutter);
    }
}
