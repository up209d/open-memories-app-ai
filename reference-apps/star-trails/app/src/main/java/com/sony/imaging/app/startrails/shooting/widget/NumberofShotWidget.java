package com.sony.imaging.app.startrails.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.widget.DigitView;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STConstants;
import com.sony.imaging.app.startrails.util.ThemeParameterSettingUtility;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class NumberofShotWidget extends DigitView {
    private static final String TAG = "NumberofShotWidget";
    private NumberOfShotChangeListener mNumberOfShotChangeListener;

    public NumberofShotWidget(Context context) {
        this(context, null);
    }

    public NumberofShotWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        AppLog.info(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.widget.DigitView, com.sony.imaging.app.base.shooting.widget.ActiveText
    protected NotificationListener getNotificationListener() {
        if (this.mNumberOfShotChangeListener == null) {
            this.mNumberOfShotChangeListener = new NumberOfShotChangeListener();
        }
        return this.mNumberOfShotChangeListener;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.DigitView, com.sony.imaging.app.base.shooting.widget.ActiveText
    protected void refresh() {
        AppLog.enter(TAG, AppLog.getMethodName());
        setText(String.valueOf(ThemeParameterSettingUtility.getInstance().getNumberOfShot() - STConstants.sCaptureImageCounter));
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    /* loaded from: classes.dex */
    class NumberOfShotChangeListener implements NotificationListener {
        private String[] TAGS = {STConstants.TIMER_UPDATE};

        NumberOfShotChangeListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            NumberofShotWidget.this.refresh();
            AppLog.enter(NumberofShotWidget.TAG, AppLog.getMethodName());
            NumberofShotWidget.this.setText(String.valueOf(ThemeParameterSettingUtility.getInstance().getNumberOfShot() - STConstants.sCaptureImageCounter));
            AppLog.exit(NumberofShotWidget.TAG, AppLog.getMethodName());
        }
    }
}
