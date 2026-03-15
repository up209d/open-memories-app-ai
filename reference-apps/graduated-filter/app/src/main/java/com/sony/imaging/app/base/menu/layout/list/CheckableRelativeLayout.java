package com.sony.imaging.app.base.menu.layout.list;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/* loaded from: classes.dex */
public class CheckableRelativeLayout extends RelativeLayout implements FakeCheckable {
    private static final int[] CHECKED_STATE_SET = {R.attr.state_checked};
    private boolean isChecked;

    public CheckableRelativeLayout(Context context) {
        super(context);
        this.isChecked = false;
    }

    public CheckableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.isChecked = false;
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.FakeCheckable
    public boolean isFakeChecked() {
        return this.isChecked;
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.FakeCheckable
    public void setFakeChecked(boolean checked) {
        if (this.isChecked != checked) {
            this.isChecked = checked;
            refreshDrawableState();
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.FakeCheckable
    public void fakeToggle() {
        if (isFakeChecked()) {
            setFakeChecked(false);
        } else {
            setFakeChecked(true);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isFakeChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }
}
