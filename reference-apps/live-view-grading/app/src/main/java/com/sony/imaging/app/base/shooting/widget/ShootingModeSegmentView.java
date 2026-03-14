package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;

/* loaded from: classes.dex */
public class ShootingModeSegmentView extends ShootingSubLcdActiveText {
    private static final String TAG = "ShootingModeSegmentView";
    int mTextIntervale;
    int mTextLoop;
    int mTextMovie;
    int mTextPhoto;

    public ShootingModeSegmentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShootingModeSegmentView);
        this.mTextPhoto = typedArray.getResourceId(0, 0);
        this.mTextMovie = typedArray.getResourceId(1, 0);
        this.mTextIntervale = typedArray.getResourceId(2, 0);
        this.mTextLoop = typedArray.getResourceId(3, 0);
        typedArray.recycle();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveText, com.sony.imaging.app.base.common.widget.SubLcdTextView, com.sony.imaging.app.base.common.widget.AbstractSubLCDView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveText, com.sony.imaging.app.base.common.widget.SubLcdTextView, com.sony.imaging.app.base.common.widget.AbstractSubLCDView, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveText, com.sony.imaging.app.base.common.widget.AbstractSubLCDView
    public void refresh() {
        int recMode = ExecutorCreator.getInstance().getRecordingMode();
        if (1 == recMode) {
            setText(this.mTextPhoto);
            return;
        }
        if (2 == recMode) {
            setText(this.mTextMovie);
        } else if (4 == recMode) {
            setText(this.mTextIntervale);
        } else if (8 == recMode) {
            setText(this.mTextLoop);
        }
    }
}
