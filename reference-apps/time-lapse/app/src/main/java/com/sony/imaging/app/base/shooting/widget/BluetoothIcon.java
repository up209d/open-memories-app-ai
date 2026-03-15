package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.BluetoothController;
import com.sony.imaging.app.base.common.widget.BluetoothNotificationManager;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class BluetoothIcon extends ImageView {
    private static final String TAG = BluetoothIcon.class.getSimpleName();
    private static final String[] TAGS = {BluetoothNotificationManager.TAG_BLUETOOTH_CHANGED};
    private BluetoothInfoChangedListener mBluetoothInfoChangedListener;
    private TypedArray mTypedArray;

    public BluetoothIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.BluetoothIcon);
        setVisibility(4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void RefreshBluetoothIcon() {
        Drawable draw = null;
        if (BluetoothNotificationManager.getInstance().getValue(BluetoothNotificationManager.BLUETOOTH_POWER) == BluetoothNotificationManager.Status.PowerOn) {
            if (BluetoothNotificationManager.getInstance().getValue(BluetoothNotificationManager.BLUETOOTH_STATE) == BluetoothNotificationManager.Status.Connect) {
                draw = this.mTypedArray.getDrawable(0);
            } else if (BluetoothNotificationManager.getInstance().getValue(BluetoothNotificationManager.BLUETOOTH_STATE) == BluetoothNotificationManager.Status.Disconnect) {
                draw = this.mTypedArray.getDrawable(1);
            }
        }
        if (draw != null) {
            setVisibility(0);
            setImageDrawable(draw);
        } else {
            setVisibility(4);
        }
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (BluetoothController.getInstance().isDeviceSupportedBluetooth()) {
            this.mBluetoothInfoChangedListener = new BluetoothInfoChangedListener();
            BluetoothNotificationManager.getInstance().setNotificationListener(this.mBluetoothInfoChangedListener);
            RefreshBluetoothIcon();
        }
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (BluetoothController.getInstance().isDeviceSupportedBluetooth()) {
            BluetoothNotificationManager.getInstance().removeNotificationListener(this.mBluetoothInfoChangedListener);
            this.mBluetoothInfoChangedListener = null;
        }
    }

    /* loaded from: classes.dex */
    class BluetoothInfoChangedListener implements NotificationListener {
        BluetoothInfoChangedListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return BluetoothIcon.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (tag.equals(BluetoothNotificationManager.TAG_BLUETOOTH_CHANGED)) {
                BluetoothIcon.this.RefreshBluetoothIcon();
            }
        }
    }
}
