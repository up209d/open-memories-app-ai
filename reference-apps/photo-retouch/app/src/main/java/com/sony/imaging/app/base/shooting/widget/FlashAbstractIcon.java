package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.FlashController;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class FlashAbstractIcon extends ActiveImage {
    private static final String LOG_DEVICE_EXTERNAL = "external strobe ";
    private static final String LOG_DEVICE_INTERNAL = "internal strobe ";
    private static final String LOG_DEVICE_STATUS = "device status is ";
    private static final String LOG_MSG_DEVICE = "  FlashDevice is ";
    private static final String LOG_MSG_REFRESH = " refresh : ";
    private static final String LOG_MSG_VISIBLE = "  Exclusive controll by exposure is ";
    protected String LOGTAG;
    protected StringBuilder builder;
    protected boolean isAuto;
    protected boolean isEnable;
    private boolean isExternalEnable;
    protected boolean isHss;
    private boolean isInternalEnable;
    protected boolean isOff;
    protected FlashController mFlashController;
    protected boolean mVisible;

    /* loaded from: classes.dex */
    enum DEVICE {
        INTERNAL,
        EXTERNAL
    }

    public FlashAbstractIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.LOGTAG = "FlashAbstractIcon";
        this.isHss = false;
        this.mVisible = false;
        this.isEnable = false;
        this.isInternalEnable = false;
        this.isExternalEnable = false;
        this.isAuto = false;
        this.isOff = false;
        this.builder = new StringBuilder();
        this.mFlashController = FlashController.getInstance();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage, android.widget.ImageView, android.view.View
    public void onAttachedToWindow() {
        Log.d(this.LOGTAG, "onAttachedToWindow");
        reset();
        updateFlashMode();
        updateFlashDeviceStatus(null);
        super.onAttachedToWindow();
    }

    private void reset() {
        this.isHss = false;
        this.mVisible = false;
        this.isAuto = false;
        this.isOff = false;
        this.isExternalEnable = false;
        this.isInternalEnable = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateFlashDeviceStatus(String tag) {
        if (!isMovieMode()) {
            CameraSetting set = CameraSetting.getInstance();
            if (CameraNotificationManager.DEVICE_EXTERNAL_FLASH_CHANGED.equals(tag)) {
                this.isExternalEnable = set.getFlashExternalEnable();
                CameraEx.ExternalFlashInfo info = set.getExternalFlashInfo();
                if (info != null) {
                    this.isHss = info.hssStatus;
                }
                this.builder.replace(0, this.builder.length(), LOG_DEVICE_EXTERNAL);
                this.builder.append(LOG_DEVICE_STATUS).append(this.isExternalEnable);
            } else if (CameraNotificationManager.DEVICE_INTERNAL_FLASH_CHANGED.equals(tag)) {
                this.isInternalEnable = set.getFlashInternalEnable();
                this.builder.replace(0, this.builder.length(), LOG_DEVICE_INTERNAL);
                this.builder.append(LOG_DEVICE_STATUS).append(this.isInternalEnable);
            } else {
                this.isExternalEnable = set.getFlashExternalEnable();
                CameraEx.ExternalFlashInfo info2 = set.getExternalFlashInfo();
                if (info2 != null) {
                    this.isHss = info2.hssStatus;
                }
                this.isInternalEnable = set.getFlashInternalEnable();
                this.builder.replace(0, this.builder.length(), LOG_DEVICE_EXTERNAL);
                this.builder.append(LOG_DEVICE_STATUS).append(this.isExternalEnable);
                this.builder.append(LOG_DEVICE_INTERNAL);
                this.builder.append(LOG_DEVICE_STATUS).append(this.isInternalEnable);
            }
        } else {
            this.isInternalEnable = false;
            this.isExternalEnable = false;
            this.isHss = false;
        }
        this.isEnable = this.isExternalEnable || this.isInternalEnable;
        Log.i(this.LOGTAG, this.builder.toString());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateFlashMode() {
        String value = this.mFlashController.getValue();
        if ("off".equals(value)) {
            this.isOff = true;
            this.isAuto = false;
            return;
        }
        this.isOff = false;
        if ("auto".equals(value)) {
            this.isAuto = true;
        } else {
            this.isAuto = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    public void refresh() {
        Log.i(this.LOGTAG, this.builder.replace(0, this.builder.length(), LOG_MSG_REFRESH).append(LOG_MSG_DEVICE).append(this.isEnable).append(LOG_MSG_VISIBLE).append(this.mVisible).toString());
    }

    protected boolean isMovieMode() {
        if (!Environment.isMovieAPISupported() || 2 != CameraSetting.getInstance().getCurrentMode()) {
            return false;
        }
        return true;
    }
}
