package com.sony.imaging.app.photoretouch.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.common.widget.BaseButton;

/* loaded from: classes.dex */
public class EachButton extends BaseButton {
    private Callback mCallback;

    /* loaded from: classes.dex */
    public interface Callback {
        void onDown(EachButton eachButton);

        void onLongPress(EachButton eachButton);

        void onUp(EachButton eachButton);
    }

    public EachButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mCallback = null;
    }

    public void setEventListener(Callback c) {
        this.mCallback = c;
    }

    @Override // com.sony.imaging.app.base.common.widget.BaseButton
    public void actionTouchDown() {
        if (this.mCallback != null) {
            this.mCallback.onDown(this);
        }
    }

    @Override // com.sony.imaging.app.base.common.widget.BaseButton
    public void actionTouchUp() {
        if (this.mCallback != null) {
            this.mCallback.onUp(this);
        }
    }

    @Override // com.sony.imaging.app.base.common.widget.BaseButton
    public void actionTouchLongPress() {
        if (this.mCallback != null) {
            this.mCallback.onLongPress(this);
        }
    }
}
