package com.sony.imaging.app.base.shooting.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView;
import com.sony.imaging.app.base.shooting.camera.WindNoiseReductionController;

/* loaded from: classes.dex */
public class RotationalSubLcdTextWindNoiseReduction extends RotationalSubLcdTextView {
    private static final String TAG = RotationalSubLcdTextWindNoiseReduction.class.getSimpleName();

    public RotationalSubLcdTextWindNoiseReduction(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RotationalSubLcdTextWindNoiseReduction(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RotationalSubLcdTextWindNoiseReduction(Context context) {
        super(context);
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView, com.sony.imaging.app.base.common.widget.Rotational
    public boolean isValidValue() {
        String value = WindNoiseReductionController.getInstance().getValue("AudioWindNoiseReduction");
        return "on".equals(value);
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView
    protected String makeText() {
        String value = WindNoiseReductionController.getInstance().getValue("AudioWindNoiseReduction");
        return "on".equals(value) ? getResources().getString(R.string.permlab_activityRecognition) : "";
    }
}
