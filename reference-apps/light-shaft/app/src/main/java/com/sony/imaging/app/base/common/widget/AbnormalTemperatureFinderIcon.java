package com.sony.imaging.app.base.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.util.AbstractRelativeLayoutGroup;
import com.sony.imaging.app.util.IVisibilityChanged;
import com.sony.scalar.sysutil.didep.Temperature;

/* loaded from: classes.dex */
public class AbnormalTemperatureFinderIcon extends ImageView implements AbstractRelativeLayoutGroup.IVisibilityChange {
    private static final String TAG = "AbnormalTemperatureFinderIcon";
    private Drawable mHeat;
    private Drawable mHeatDown;
    protected IVisibilityChanged mNotifyTarget;
    private Temperature mTemp;
    private Temperature.StatusCallback tempCallback;

    public AbnormalTemperatureFinderIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTemp = null;
        this.tempCallback = new Temperature.StatusCallback() { // from class: com.sony.imaging.app.base.common.widget.AbnormalTemperatureFinderIcon.1
            public void onTempStatusChanged() {
                AbnormalTemperatureFinderIcon.this.refreshIcon();
            }
        };
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AbnormalTemperatureFinderIcon);
        this.mHeat = typedArray.getDrawable(0);
        this.mHeatDown = typedArray.getDrawable(1);
        setVisibility(4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshIcon() {
        boolean visible;
        Drawable d = null;
        Temperature.Status tempStatus = this.mTemp.getStatus();
        int tempFactor = tempStatus.getTempInhFactors();
        if (tempFactor == 4) {
            d = this.mHeat;
            visible = true;
        } else if (tempFactor == 8) {
            d = this.mHeatDown;
            visible = true;
        } else {
            visible = false;
        }
        if (visible) {
            setImageDrawable(d);
            setVisibility(0);
        } else {
            setVisibility(4);
        }
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onAttachedToWindow() {
        this.mTemp = new Temperature();
        this.mTemp.setStatusCallback(this.tempCallback);
        refreshIcon();
        super.onAttachedToWindow();
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDetachedFromWindow() {
        this.mTemp.release();
        this.mNotifyTarget = null;
        super.onDetachedFromWindow();
    }

    @Override // com.sony.imaging.app.util.AbstractRelativeLayoutGroup.IVisibilityChange
    public void setCallback(IVisibilityChanged target) {
        this.mNotifyTarget = target;
    }

    @Override // android.widget.ImageView, android.view.View
    public void setVisibility(int visibility) {
        int current = getVisibility();
        super.setVisibility(visibility);
        if (this.mNotifyTarget != null && visibility != current) {
            this.mNotifyTarget.onVisibilityChanged();
        }
    }
}
