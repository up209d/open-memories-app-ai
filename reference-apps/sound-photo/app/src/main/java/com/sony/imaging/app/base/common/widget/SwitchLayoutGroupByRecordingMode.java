package com.sony.imaging.app.base.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.shooting.camera.AntiHandBlurController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class SwitchLayoutGroupByRecordingMode extends RelativeLayout {
    private static final String[] tags = {CameraNotificationManager.REC_MODE_CHANGED};
    private final String MOVIE;
    private final String STILL;
    private String mRecModeAttribute;
    private RecModeChangeListener mRecModeChangeListener;

    public SwitchLayoutGroupByRecordingMode(Context context) {
        super(context);
        this.STILL = AntiHandBlurController.STILL;
        this.MOVIE = "movie";
    }

    public SwitchLayoutGroupByRecordingMode(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.STILL = AntiHandBlurController.STILL;
        this.MOVIE = "movie";
        initAttribute(context, attrs);
    }

    public SwitchLayoutGroupByRecordingMode(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.STILL = AntiHandBlurController.STILL;
        this.MOVIE = "movie";
        initAttribute(context, attrs);
    }

    protected void initAttribute(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VisibleRecMode);
        this.mRecModeAttribute = a.getString(0);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mRecModeChangeListener == null) {
            this.mRecModeChangeListener = new RecModeChangeListener();
            CameraNotificationManager.getInstance().setNotificationListener(this.mRecModeChangeListener);
        }
        refresh();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        if (this.mRecModeChangeListener != null) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mRecModeChangeListener);
            this.mRecModeChangeListener = null;
        }
        super.onDetachedFromWindow();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refresh() {
        int mCurrentRecMode = CameraSetting.getInstance().getCurrentMode();
        if (mCurrentRecMode == 1 && this.mRecModeAttribute.equals(AntiHandBlurController.STILL)) {
            setVisibility(0);
        } else if (mCurrentRecMode == 2 && this.mRecModeAttribute.equals("movie")) {
            setVisibility(0);
        } else {
            setVisibility(8);
        }
    }

    /* loaded from: classes.dex */
    private class RecModeChangeListener implements NotificationListener {
        private RecModeChangeListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return SwitchLayoutGroupByRecordingMode.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            SwitchLayoutGroupByRecordingMode.this.refresh();
        }
    }
}
