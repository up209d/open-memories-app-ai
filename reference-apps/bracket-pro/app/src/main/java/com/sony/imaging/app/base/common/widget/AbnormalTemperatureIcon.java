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
public class AbnormalTemperatureIcon extends ImageView implements AbstractRelativeLayoutGroup.IVisibilityChange {
    protected IVisibilityChanged mNotifyTarget;
    private Temperature mTemp;
    private Temperature.StatusCallback tempCallback;

    public AbnormalTemperatureIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTemp = null;
        this.tempCallback = new Temperature.StatusCallback() { // from class: com.sony.imaging.app.base.common.widget.AbnormalTemperatureIcon.1
            public void onTempStatusChanged() {
                AbnormalTemperatureIcon.this.refreshIcon();
            }
        };
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AbnormalTemperatureIcon);
        Drawable d = typedArray.getDrawable(0);
        setImageDrawable(d);
        setVisibility(4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshIcon() {
        boolean visible = false;
        Temperature.Status tempStatus = this.mTemp.getStatus();
        int battStatus = tempStatus.get(1);
        int boxStatus = tempStatus.get(2);
        int tempFactor = tempStatus.getTempInhFactors();
        if (boxStatus == 3 || boxStatus == 2) {
            visible = true;
        }
        if (battStatus == 3 || battStatus == 2) {
            visible = true;
        }
        if (tempFactor == 1 || tempFactor == 2) {
            visible = true;
        }
        if (visible) {
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
        setVisibility(4);
        this.mTemp.release();
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
