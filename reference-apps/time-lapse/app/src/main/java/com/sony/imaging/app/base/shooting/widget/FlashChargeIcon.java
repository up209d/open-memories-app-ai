package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.FlashController;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.Oscillator;
import com.sony.imaging.app.util.StringBuilderThreadLocal;

/* loaded from: classes.dex */
public class FlashChargeIcon extends FlashAbstractIcon {
    private static final int CHARGE_DONE = 0;
    private static final int CHARGE_ERROR = 2;
    private static final int CHARGE_ING = 1;
    private static final String LOG_FLASH_CHARGE_STATUS = "Flash charge status is ";
    private static final String LOG_FLASH_CHARGE_STATUS_CHARGING = "charging";
    private static final String LOG_FLASH_CHARGE_STATUS_DONE = "done ";
    private static final String LOG_FLASH_CHARGE_STATUS_ERROR = "error";
    private static final String LOG_FLASH_EMITTION = "Flash Emittion is ";
    private static final String TAG = "FlashChargeIcon";
    private static StringBuilderThreadLocal sStringBuilder = new StringBuilderThreadLocal();
    private String[] TAGS;
    private boolean isEmit;
    private int mChargeIconId;
    private int mChargeOffsetX;
    private int mChargeStatus;
    private boolean mHigh;
    private Oscillator.OnPeriodListener mTimerListener;

    public FlashChargeIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mChargeStatus = 0;
        this.mHigh = false;
        this.isEmit = false;
        this.mChargeOffsetX = 0;
        this.TAGS = new String[]{CameraNotificationManager.FLASH_CHARGE_STATUS, CameraNotificationManager.DEVICE_EXTERNAL_FLASH_CHANGED, CameraNotificationManager.DEVICE_INTERNAL_FLASH_CHANGED, CameraNotificationManager.FLASH_CHANGE, CameraNotificationManager.FLASH_EMITTION, CameraNotificationManager.SCENE_MODE, CameraNotificationManager.REC_MODE_CHANGED};
        this.LOGTAG = TAG;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FlashState);
        setImageResource(typedArray.getResourceId(0, 0));
        this.mChargeIconId = typedArray.getResourceId(1, 0);
        this.mChargeOffsetX = typedArray.getDimensionPixelSize(2, 0);
        setVisibility(4);
        this.mTimerListener = new Oscillator.OnPeriodListener() { // from class: com.sony.imaging.app.base.shooting.widget.FlashChargeIcon.1
            @Override // com.sony.imaging.app.util.Oscillator.OnPeriodListener
            public void onPeriod(int Hz, boolean highlow) {
                if (8 == Hz) {
                    FlashChargeIcon.this.mHigh = highlow;
                    FlashChargeIcon.this.invalidate();
                }
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.FlashAbstractIcon, com.sony.imaging.app.base.shooting.widget.ActiveImage, android.widget.ImageView, android.view.View
    public void onAttachedToWindow() {
        CameraSetting set = CameraSetting.getInstance();
        this.isEmit = set.getFlashEmit();
        this.mChargeStatus = set.getFlashChargeState();
        super.onAttachedToWindow();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    public boolean isVisible() {
        boolean mode = this.mFlashController.isUnavailableSceneFactor(FlashController.FLASHMODE);
        boolean type = this.mFlashController.isUnavailableSceneFactor(FlashController.FLASHTYPE);
        this.mVisible = (mode || type) ? false : true;
        if (!this.mVisible) {
            String value = this.mFlashController.getValue(FlashController.FLASHMODE);
            if (!"off".equals(value)) {
                this.mVisible = true;
            }
        }
        return this.mVisible;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        return new NotificationListener() { // from class: com.sony.imaging.app.base.shooting.widget.FlashChargeIcon.2
            @Override // com.sony.imaging.app.util.NotificationListener
            public void onNotify(String tag) {
                if (CameraNotificationManager.DEVICE_EXTERNAL_FLASH_CHANGED.equals(tag) || CameraNotificationManager.DEVICE_INTERNAL_FLASH_CHANGED.equals(tag) || CameraNotificationManager.REC_MODE_CHANGED.equals(tag)) {
                    FlashChargeIcon.this.updateFlashDeviceStatus(tag);
                } else if (CameraNotificationManager.FLASH_CHARGE_STATUS.equals(tag)) {
                    StringBuilder builder = FlashChargeIcon.sStringBuilder.get();
                    builder.replace(0, builder.length(), FlashChargeIcon.LOG_FLASH_CHARGE_STATUS);
                    int state = ((Integer) CameraNotificationManager.getInstance().getValue(tag)).intValue();
                    if (state == 0) {
                        builder.append(FlashChargeIcon.LOG_FLASH_CHARGE_STATUS_DONE);
                    } else if (state == 1) {
                        builder.append(FlashChargeIcon.LOG_FLASH_CHARGE_STATUS_CHARGING);
                    } else if (state == 2) {
                        builder.append(FlashChargeIcon.LOG_FLASH_CHARGE_STATUS_ERROR);
                    }
                    FlashChargeIcon.this.mChargeStatus = state;
                    Log.i(FlashChargeIcon.TAG, builder.toString());
                } else if (CameraNotificationManager.FLASH_CHANGE.equals(tag)) {
                    FlashChargeIcon.this.updateFlashMode();
                } else if (CameraNotificationManager.FLASH_EMITTION.equals(tag)) {
                    FlashChargeIcon.this.isEmit = CameraSetting.getInstance().getFlashEmit();
                    StringBuilder builder2 = FlashChargeIcon.sStringBuilder.get();
                    builder2.replace(0, builder2.length(), FlashChargeIcon.LOG_FLASH_EMITTION).append(FlashChargeIcon.this.isEmit);
                    Log.i(FlashChargeIcon.TAG, builder2.toString());
                }
                FlashChargeIcon.this.refresh();
            }

            @Override // com.sony.imaging.app.util.NotificationListener
            public String[] getTags() {
                return FlashChargeIcon.this.TAGS;
            }
        };
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        Bitmap chargingImg = BitmapFactory.decodeResource(getResources(), this.mChargeIconId);
        switch (this.mChargeStatus) {
            case 0:
                canvas.drawBitmap(chargingImg, this.mChargeOffsetX, TimeLapseConstants.INVALID_APERTURE_VALUE, (Paint) null);
                break;
            case 1:
                if (this.mHigh) {
                    canvas.drawBitmap(chargingImg, this.mChargeOffsetX, TimeLapseConstants.INVALID_APERTURE_VALUE, (Paint) null);
                    break;
                }
                break;
        }
        canvas.restore();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.FlashAbstractIcon, com.sony.imaging.app.base.shooting.widget.ActiveImage
    public void refresh() {
        updateFlashMode();
        Oscillator.detach(8, this.mTimerListener);
        if (!this.mVisible || !this.isEnable || this.isOff) {
            setVisibility(4);
            return;
        }
        if (this.mChargeStatus == 1) {
            setVisibility(0);
            Oscillator.attach(8, this.mTimerListener);
        } else if (this.isAuto) {
            if (this.isEmit) {
                setVisibility(0);
            } else {
                setVisibility(4);
            }
        } else if (this.mChargeStatus == 0) {
            setVisibility(0);
        } else if (this.mChargeStatus == 2) {
            setVisibility(4);
        }
        invalidate();
    }
}
