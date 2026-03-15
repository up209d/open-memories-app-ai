package com.sony.imaging.app.base.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.widget.InhWatch;
import com.sony.imaging.app.util.AbstractRelativeLayoutGroup;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.IVisibilityChanged;

/* loaded from: classes.dex */
public class FinderErrorIcon extends ImageView implements InhWatch.InhWatchInterface, AbstractRelativeLayoutGroup.IVisibilityChange {
    private static final String INH_1 = "INH_FEATURE_EVF_DEVICE_ERROR_CAUTION_ON";
    private static final String INH_2 = "INH_FACTOR_EXTEVF_DEVICE_ERROR";
    private static final String TAG = "FinderErrorIcon";
    private boolean isRepeat;
    private int mCallbackId;
    private InhWatch mInhWatch;
    protected IVisibilityChanged mNotifyTarget;
    private TypedArray mTypedArray;

    public FinderErrorIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.isRepeat = true;
        this.mCallbackId = -1;
        this.mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.FinderErrorIcon);
        setVisibility(4);
        this.mInhWatch = InhWatch.getInstance();
    }

    @Override // com.sony.imaging.app.base.common.widget.InhWatch.InhWatchInterface
    public void onCallback() {
        if (this.isRepeat) {
            refreshIcon();
        }
    }

    private void refreshIcon() {
        Drawable d = null;
        boolean finderErrorInh = AvailableInfo.isInhibition(INH_1);
        boolean finderErrorFactor = AvailableInfo.isFactor(INH_2);
        if (!finderErrorInh) {
            setVisibility(4);
        } else if (finderErrorFactor) {
            setVisibility(0);
            d = this.mTypedArray.getDrawable(0);
        } else {
            setVisibility(4);
            d = null;
        }
        if (d != null) {
            setImageDrawable(d);
        }
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onAttachedToWindow() {
        this.isRepeat = true;
        this.mCallbackId = this.mInhWatch.addCallback(this, 1, TAG);
        this.mInhWatch.startCallback(this.mCallbackId);
        super.onAttachedToWindow();
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDetachedFromWindow() {
        this.isRepeat = false;
        setVisibility(4);
        this.mInhWatch.stopCallback(this.mCallbackId);
        this.mInhWatch.removeCallback(this.mCallbackId);
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
