package com.sony.imaging.app.base.shooting.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView;
import com.sony.scalar.sysutil.didep.Temperature;

/* loaded from: classes.dex */
public class RotationalSubLcdTextAbnormalTemperature extends RotationalSubLcdTextView {
    private static final String TAG = "RotationalSubLcdTextAbnormalTemperature";
    private Context mContext;
    private Temperature mTemp;
    private Temperature.StatusCallback tempCallback;

    public RotationalSubLcdTextAbnormalTemperature(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mTemp = null;
        this.mContext = null;
        this.tempCallback = new Temperature.StatusCallback() { // from class: com.sony.imaging.app.base.shooting.widget.RotationalSubLcdTextAbnormalTemperature.1
            public void onTempStatusChanged() {
                RotationalSubLcdTextAbnormalTemperature.this.refreshIcon();
            }
        };
        this.mContext = context;
    }

    public RotationalSubLcdTextAbnormalTemperature(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTemp = null;
        this.mContext = null;
        this.tempCallback = new Temperature.StatusCallback() { // from class: com.sony.imaging.app.base.shooting.widget.RotationalSubLcdTextAbnormalTemperature.1
            public void onTempStatusChanged() {
                RotationalSubLcdTextAbnormalTemperature.this.refreshIcon();
            }
        };
        this.mContext = context;
    }

    public RotationalSubLcdTextAbnormalTemperature(Context context) {
        super(context);
        this.mTemp = null;
        this.mContext = null;
        this.tempCallback = new Temperature.StatusCallback() { // from class: com.sony.imaging.app.base.shooting.widget.RotationalSubLcdTextAbnormalTemperature.1
            public void onTempStatusChanged() {
                RotationalSubLcdTextAbnormalTemperature.this.refreshIcon();
            }
        };
        this.mContext = context;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshIcon() {
        boolean visible = false;
        Temperature.Status tempStatus = this.mTemp.getStatus();
        int battStatus = tempStatus.get(1);
        int boxStatus = tempStatus.get(2);
        if (boxStatus == 3 || boxStatus == 2) {
            visible = true;
        }
        if (battStatus == 3 || battStatus == 2) {
            visible = true;
        }
        if (!visible) {
            stop();
        }
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView, com.sony.imaging.app.base.common.widget.Rotational
    public void start() {
        this.mTemp.setStatusCallback(this.tempCallback);
        super.start();
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView, com.sony.imaging.app.base.common.widget.Rotational
    public void stop() {
        this.mTemp.setStatusCallback((Temperature.StatusCallback) null);
        super.stop();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView, com.sony.imaging.app.base.common.widget.SubLcdTextView, com.sony.imaging.app.base.common.widget.AbstractSubLCDView, android.view.View
    public void onAttachedToWindow() {
        this.mTemp = new Temperature();
        super.onAttachedToWindow();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView, com.sony.imaging.app.base.common.widget.SubLcdTextView, com.sony.imaging.app.base.common.widget.AbstractSubLCDView, android.view.View
    public void onDetachedFromWindow() {
        if (this.mTemp != null) {
            this.mTemp.release();
        }
        super.onDetachedFromWindow();
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView, com.sony.imaging.app.base.common.widget.Rotational
    public boolean isValidValue() {
        boolean visible = false;
        if (this.mTemp == null) {
            return false;
        }
        Temperature.Status tempStatus = this.mTemp.getStatus();
        int battStatus = tempStatus.get(1);
        int boxStatus = tempStatus.get(2);
        if (boxStatus == 3 || boxStatus == 2) {
            visible = true;
        }
        if (battStatus == 3 || battStatus == 2) {
            return true;
        }
        return visible;
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView
    protected String makeText() {
        String ret = this.mContext.getString(R.string.phoneTypeCompanyMain);
        return ret;
    }
}
