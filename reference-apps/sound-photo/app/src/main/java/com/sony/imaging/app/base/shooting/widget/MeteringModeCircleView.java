package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.MeteringController;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class MeteringModeCircleView extends ActiveImage {
    private static final int API_VERSION_SUPPORTING_SPOT_CIRCLE_FOCUS_LINK = 17;
    private static final String TAG = "MeteringModeCircleView";
    private NotificationListener mMeteringModeListener;
    private TypedArray mTypedArray;

    public MeteringModeCircleView(Context context) {
        this(context, null);
    }

    public MeteringModeCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.SpotMeteringArea);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        if (this.mMeteringModeListener == null) {
            this.mMeteringModeListener = new NotificationListener() { // from class: com.sony.imaging.app.base.shooting.widget.MeteringModeCircleView.1
                private String[] TAGS = {"MeteringMode", CameraNotificationManager.METERING_MODE_SPOT_SIZE, "MeteringModePoint", CameraNotificationManager.METERING_MODE_SPOT_CIRCLE_CHANGE};

                @Override // com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    MeteringModeCircleView.this.refresh();
                }

                @Override // com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return this.TAGS;
                }
            };
        }
        return this.mMeteringModeListener;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        String meteringmode = MeteringController.getInstance().getValue(null);
        updateCircle(meteringmode);
    }

    private void updateCircle(String mode) {
        if (MeteringController.MENU_ITEM_ID_METERING_CENTER_SPOT.equals(mode)) {
            if (17 <= Environment.getVersionPfAPI()) {
                String spotSize = MeteringController.getInstance().getValue(MeteringController.MENU_ITEM_ID_METERING_CENTER_SPOT);
                Bitmap bmp = null;
                if (MeteringController.STANDARD.equals(spotSize) || MeteringController.NONE.equals(spotSize)) {
                    bmp = BitmapFactory.decodeResource(getResources(), this.mTypedArray.getResourceId(0, android.R.drawable.ic_qs_dnd));
                } else if (MeteringController.LARGE.equals(spotSize)) {
                    bmp = BitmapFactory.decodeResource(getResources(), this.mTypedArray.getResourceId(1, android.R.drawable.spinner_background_holo_dark));
                }
                if (bmp != null) {
                    int resImageWidth = bmp.getWidth();
                    int resImageHeight = bmp.getHeight();
                    setImageBitmap(bmp);
                    setCirclePositionChange(resImageWidth, resImageHeight);
                }
            }
            setVisibility(0);
            return;
        }
        setVisibility(4);
    }

    private void setCirclePositionChange(int resImageWidth, int resImageHeight) {
        CameraEx.MeteringModeInfo info = CameraSetting.getInstance().getMeteringModeInfo();
        RelativeLayout.LayoutParams pUp = new RelativeLayout.LayoutParams(resImageWidth, resImageHeight);
        if (info != null) {
            int spotX = info.spotX - (resImageWidth / 2);
            int spotY = info.spotY - (resImageHeight / 2);
            pUp.leftMargin = spotX;
            pUp.topMargin = spotY;
            setLayoutParams(pUp);
            return;
        }
        Log.w(TAG, "MeteringModeInfo is NULL");
    }
}
