package com.sony.imaging.app.base.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.TemperatureManager;
import com.sony.imaging.app.base.common.TemperatureNotificationManager;
import com.sony.imaging.app.util.AbstractRelativeLayoutGroup;
import com.sony.imaging.app.util.IVisibilityChanged;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class AbnormalTemperatureIcon extends ImageView implements AbstractRelativeLayoutGroup.IVisibilityChange {
    protected IVisibilityChanged mNotifyTarget;
    private NotificationListener mTempListener;

    public AbnormalTemperatureIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTempListener = null;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AbnormalTemperatureIcon);
        Drawable d = typedArray.getDrawable(0);
        setImageDrawable(d);
        setVisibility(4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshIcon() {
        if (TemperatureManager.getInstance().isOverheating()) {
            setVisibility(0);
        } else {
            setVisibility(4);
        }
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onAttachedToWindow() {
        if (this.mTempListener == null) {
            this.mTempListener = new TemperatureChangedListener();
        }
        TemperatureNotificationManager.getInstance().setNotificationListener(this.mTempListener);
        refreshIcon();
        super.onAttachedToWindow();
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDetachedFromWindow() {
        setVisibility(4);
        if (this.mTempListener != null) {
            TemperatureNotificationManager.getInstance().removeNotificationListener(this.mTempListener);
            this.mTempListener = null;
        }
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

    /* loaded from: classes.dex */
    private class TemperatureChangedListener implements NotificationListener {
        private final String[] tags;

        private TemperatureChangedListener() {
            this.tags = new String[]{TemperatureNotificationManager.TEMP_STATUS_CHANGED, TemperatureNotificationManager.TEMP_COUNTDOWN_INFO_CHANGED};
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            AbnormalTemperatureIcon.this.refreshIcon();
        }
    }
}
