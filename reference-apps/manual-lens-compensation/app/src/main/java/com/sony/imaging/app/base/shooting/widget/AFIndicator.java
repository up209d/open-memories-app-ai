package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.Oscillator;
import com.sony.imaging.app.util.PTag;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class AFIndicator extends ActiveLayout implements Oscillator.OnPeriodListener {
    private static final String LOG_DISPLAYED_ONFOCUS = "Display on Focus";
    private static final String LOG_UNKNOWN_STATUS = "###ERROR: Unknown status";
    private static String mFocusMode;
    private String TAG;
    protected ImageView arc;
    private NotificationListener mNotificationListener;
    private TypedArray mTypedArray;
    protected ImageView round;

    public AFIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAG = "AFIndicator";
        this.mNotificationListener = null;
        this.mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.AFIndicator);
        this.round = new ImageView(context, attrs);
        this.arc = new ImageView(context, attrs);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout
    protected NotificationListener getNotificationListener() {
        if (this.mNotificationListener == null) {
            this.mNotificationListener = new NotificationListener() { // from class: com.sony.imaging.app.base.shooting.widget.AFIndicator.1
                private final String[] tags = {CameraNotificationManager.FOCUS_CHANGE, "AutoFocusMode", CameraNotificationManager.DONE_AUTO_FOCUS, CameraNotificationManager.START_AUTO_FOCUS, CameraNotificationManager.SCENE_MODE, CameraNotificationManager.REC_MODE_CHANGED};

                @Override // com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    if (CameraNotificationManager.DONE_AUTO_FOCUS.equals(tag)) {
                        AFIndicator.this.refresh((CameraNotificationManager.OnFocusInfo) AFIndicator.this.mNotifier.getValue(tag));
                        return;
                    }
                    if (CameraNotificationManager.START_AUTO_FOCUS.equals(tag)) {
                        AFIndicator.this.startAutoFocus();
                        return;
                    }
                    if ("AutoFocusMode".equals(tag) || CameraNotificationManager.FOCUS_CHANGE.equals(tag) || CameraNotificationManager.SCENE_MODE.equals(tag) || CameraNotificationManager.REC_MODE_CHANGED.equals(tag)) {
                        String unused = AFIndicator.mFocusMode = FocusModeController.getInstance().getValue();
                        AFIndicator.this.refresh(null);
                    }
                }

                @Override // com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return this.tags;
                }
            };
        }
        return this.mNotificationListener;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.round.setImageResource(this.mTypedArray.getResourceId(0, 0));
        this.arc.setImageResource(this.mTypedArray.getResourceId(1, 0));
        RelativeLayout.LayoutParams pRound = new RelativeLayout.LayoutParams(-2, -2);
        RelativeLayout.LayoutParams pArc = new RelativeLayout.LayoutParams(-2, -2);
        pRound.addRule(10);
        pRound.addRule(9);
        pArc.addRule(10);
        pArc.addRule(9);
        addView(this.round, pRound);
        addView(this.arc, pArc);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mFocusMode = FocusModeController.getInstance().getValue();
        CameraNotificationManager.OnFocusInfo info = (CameraNotificationManager.OnFocusInfo) this.mNotifier.getValue(CameraNotificationManager.DONE_AUTO_FOCUS);
        if (info != null) {
            refresh(info);
        } else {
            this.round.setVisibility(8);
            this.arc.setVisibility(8);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startAutoFocus() {
        this.round.setVisibility(8);
        arcVisibilityChange();
        blink(false);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout
    @Deprecated
    protected void refresh() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refresh(CameraNotificationManager.OnFocusInfo info) {
        if (CameraSetting.getPfApiVersion() < 2) {
            if (FocusModeController.MANUAL.equals(mFocusMode) || mFocusMode == null) {
                this.round.setVisibility(8);
                this.arc.setVisibility(8);
                blink(false);
                return;
            }
        } else if (2 <= CameraSetting.getPfApiVersion()) {
            CameraEx.LensInfo lensinfo = CameraSetting.getInstance().getLensInfo();
            int sensortype = FocusAreaController.getInstance().getSensorType();
            if (lensinfo == null || mFocusMode == null) {
                this.round.setVisibility(8);
                this.arc.setVisibility(8);
                blink(false);
                return;
            } else if (FocusModeController.MANUAL.equals(mFocusMode) && sensortype == 2) {
                this.round.setVisibility(8);
                this.arc.setVisibility(8);
                blink(false);
                return;
            }
        }
        if (info != null) {
            switch (info.status) {
                case 0:
                    this.round.setVisibility(8);
                    this.arc.setVisibility(8);
                    blink(false);
                    break;
                case 1:
                case 4:
                    this.round.setVisibility(0);
                    arcVisibilityChange();
                    blink(false);
                    break;
                case 2:
                    this.round.setVisibility(0);
                    this.arc.setVisibility(8);
                    blink(true);
                    break;
                case 3:
                default:
                    Log.e(this.TAG, LOG_UNKNOWN_STATUS);
                    break;
            }
            PTag.end(LOG_DISPLAYED_ONFOCUS);
        }
    }

    public void arcVisibilityChange() {
        int sensorType = FocusAreaController.getInstance().getSensorType();
        int shootingmode = CameraSetting.getInstance().getCurrentMode();
        if (1 == shootingmode) {
            if ("af-c".equals(mFocusMode)) {
                this.arc.setVisibility(0);
                return;
            } else {
                this.arc.setVisibility(8);
                return;
            }
        }
        if (2 == shootingmode) {
            if (2 == sensorType) {
                this.arc.setVisibility(8);
            } else if (1 == sensorType) {
                this.arc.setVisibility(0);
            }
        }
    }

    public void blink(boolean enable) {
        if (enable) {
            Oscillator.attach(8, this);
        } else {
            Oscillator.detach(8, this);
            setVisibility(0);
        }
    }

    @Override // com.sony.imaging.app.util.Oscillator.OnPeriodListener
    public void onPeriod(int Hz, boolean highlow) {
        if (8 == Hz) {
            int visibility = highlow ? 0 : 4;
            setVisibility(visibility);
        }
    }
}
