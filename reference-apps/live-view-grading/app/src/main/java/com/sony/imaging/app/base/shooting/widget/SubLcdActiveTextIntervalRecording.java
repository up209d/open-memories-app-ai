package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.SubLcdManager;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.IntervalRecExecutor;
import com.sony.imaging.app.base.shooting.widget.ShootingSubLcdActiveText;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class SubLcdActiveTextIntervalRecording extends ShootingSubLcdActiveText {
    private static final String TAG = "SubLcdActiveTextIntervalRecording";
    private SubLcdManager.BlinkHandle mHandle;
    private boolean mStopInterval;
    private int mTextWait;

    /* loaded from: classes.dex */
    private class IntervalRecordingCountListener extends ShootingSubLcdActiveText.ActiveTextListener {
        private IntervalRecordingCountListener() {
            super();
        }

        @Override // com.sony.imaging.app.base.shooting.widget.ShootingSubLcdActiveText.ActiveTextListener, com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            Log.e(SubLcdActiveTextIntervalRecording.TAG, tag);
            if (CameraNotificationManager.TAG_INTVAL_REC_STOP_BEGIN.equals(tag)) {
                SubLcdActiveTextIntervalRecording.this.mStopInterval = true;
            }
            super.onNotify(tag);
        }

        @Override // com.sony.imaging.app.base.shooting.widget.ShootingSubLcdActiveText.ActiveTextListener
        public String[] addTags() {
            String[] tags = {CameraNotificationManager.TAG_INTVAL_REC_SHOTS_COUNT_UPDATED, CameraNotificationManager.TAG_INTVAL_REC_STOP_BEGIN};
            return tags;
        }
    }

    public SubLcdActiveTextIntervalRecording(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.IntervalRecordingView);
        this.mTextWait = typedArray.getResourceId(0, 0);
        typedArray.recycle();
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ShootingSubLcdActiveText, com.sony.imaging.app.base.common.widget.SubLcdActiveText
    protected NotificationListener getNotificationListener() {
        NotificationListener notificationListener = new IntervalRecordingCountListener();
        return notificationListener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveText, com.sony.imaging.app.base.common.widget.SubLcdTextView, com.sony.imaging.app.base.common.widget.AbstractSubLCDView, android.view.View
    public void onAttachedToWindow() {
        this.mStopInterval = false;
        setText((CharSequence) null);
        super.onAttachedToWindow();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveText, com.sony.imaging.app.base.common.widget.SubLcdTextView, com.sony.imaging.app.base.common.widget.AbstractSubLCDView, android.view.View
    public void onDetachedFromWindow() {
        if (this.mHandle != null) {
            SubLcdManager.getInstance().stopBlink(this.mHandle);
            this.mHandle = null;
        }
        this.mStopInterval = false;
        super.onDetachedFromWindow();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveText, com.sony.imaging.app.base.common.widget.AbstractSubLCDView
    public void refresh() {
        if (this.mStopInterval) {
            if (this.mHandle == null) {
                this.mHandle = SubLcdManager.getInstance().blinkAll("PTN_SLOW");
            }
            setText(this.mTextWait);
        } else {
            BaseShootingExecutor executer = ExecutorCreator.getInstance().getSequence();
            int shootingCouont = ((IntervalRecExecutor) executer).getIntervalRecShootingCount();
            if (shootingCouont > 0) {
                setText(String.format("%05d", Integer.valueOf(shootingCouont)));
            }
        }
    }
}
