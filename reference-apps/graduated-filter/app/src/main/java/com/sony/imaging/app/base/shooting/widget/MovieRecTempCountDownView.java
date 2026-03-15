package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.widget.TextView;
import com.sony.imaging.app.base.common.TemperatureManager;
import com.sony.imaging.app.base.common.TemperatureNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.movie.MovieShootingExecutor;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.sysutil.didep.Temperature;

/* loaded from: classes.dex */
public class MovieRecTempCountDownView extends TextView {
    private RecCountDownTimer cdt;
    private String mFormatTime;
    private TempInfoChangedListener mTempInfoNotificationListener;

    public MovieRecTempCountDownView(Context context) {
        super(context);
        this.mTempInfoNotificationListener = null;
        this.cdt = null;
        this.mFormatTime = null;
        if (TemperatureManager.isTempCounDownSupportedPF()) {
            this.mFormatTime = getResources().getString(17041543);
        }
    }

    public MovieRecTempCountDownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTempInfoNotificationListener = null;
        this.cdt = null;
        this.mFormatTime = null;
        if (TemperatureManager.isTempCounDownSupportedPF()) {
            this.mFormatTime = getResources().getString(17041543);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshIcon() {
        if (1 == CameraSetting.getInstance().getCurrentMode()) {
            setVisibility(4);
            return;
        }
        boolean isMovieRecording = MovieShootingExecutor.isMovieRecording();
        Temperature.CountDownInfo tempInfo = TemperatureManager.getInstance().getCountDownInfo();
        boolean enabled = tempInfo.enabled;
        if (enabled && isMovieRecording && this.cdt == null) {
            setVisibility(0);
            this.cdt = new RecCountDownTimer(TemperatureManager.getInstance().getCountDownTime(), 100L);
            this.cdt.start();
        }
        if (!isMovieRecording) {
            stopCountDown();
        }
    }

    private void stopCountDown() {
        if (this.cdt != null) {
            this.cdt.cancel();
            this.cdt = null;
            setVisibility(4);
            setText((CharSequence) null);
        }
    }

    @Override // android.widget.TextView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (TemperatureManager.isTempCounDownSupportedPF()) {
            refreshIcon();
            if (this.mTempInfoNotificationListener == null) {
                this.mTempInfoNotificationListener = new TempInfoChangedListener();
            }
            TemperatureNotificationManager.getInstance().setNotificationListener(this.mTempInfoNotificationListener);
        }
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        stopCountDown();
        if (this.mTempInfoNotificationListener != null) {
            TemperatureNotificationManager.getInstance().removeNotificationListener(this.mTempInfoNotificationListener);
            this.mTempInfoNotificationListener = null;
        }
        super.onDetachedFromWindow();
    }

    /* loaded from: classes.dex */
    private class TempInfoChangedListener implements NotificationListener {
        private final String[] tags;

        private TempInfoChangedListener() {
            this.tags = new String[]{TemperatureNotificationManager.TEMP_COUNTDOWN_START, TemperatureNotificationManager.TEMP_COUNTDOWN_STOP, TemperatureNotificationManager.TEMP_COUNTDOWN_INFO_CHANGED};
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            MovieRecTempCountDownView.this.refreshIcon();
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }
    }

    /* loaded from: classes.dex */
    public class RecCountDownTimer extends CountDownTimer {
        public RecCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override // android.os.CountDownTimer
        public void onFinish() {
        }

        @Override // android.os.CountDownTimer
        public void onTick(long millisUntilFinished) {
            String countDownTime = String.format(MovieRecTempCountDownView.this.mFormatTime, Integer.valueOf((int) Math.floor(millisUntilFinished / 1000)));
            MovieRecTempCountDownView.this.setText(countDownTime);
        }
    }
}
