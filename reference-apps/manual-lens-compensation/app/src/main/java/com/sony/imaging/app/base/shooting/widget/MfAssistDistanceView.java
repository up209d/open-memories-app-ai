package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.FocusMagnificationController;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCConstants;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class MfAssistDistanceView extends RelativeLayout {
    private static int DISPLAYED_TIME = OCConstants.TIME_GUIDE;
    private static final int DISTANCE_X_OFFSET = 16;
    private static final int DISTANCE_Y_OFFSET = 30;
    private static final int FAR_X_OFFSET = 256;
    private static final int FAR_Y_OFFSET = 0;
    private static final int NEAR_Y_OFFSET = 0;
    private static final int NERA_X_OFFSET = 0;
    static final String TAG = "MfAssistDistanceView";
    private static final int VALUE_CURRENT_DISTANCE_WIDTH = 20;
    private static final int VALUE_DISTANCE_HEIGHT = 32;
    private static final int VALUE_DISTANCE_WIDTH = 256;
    private static final int VALUE_FAR_HEIGHT = 32;
    private static final int VALUE_FAR_WIDTH = 32;
    private static final int VALUE_NEAR_HEIGHT = 32;
    private static final int VALUE_NEAR_WIDTH = 32;
    protected Runnable disappear;
    private Context mContext;
    private CurrentDistanceValue mCurrentDistance;
    private Bitmap mCurrentDistanceIcon;
    private ImageView mDistanceBar;
    private ImageView mFarDirection;
    private Handler mHandler;
    private ImageView mNearDirection;

    public MfAssistDistanceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mDistanceBar = null;
        this.mCurrentDistance = null;
        this.mNearDirection = null;
        this.mFarDirection = null;
        this.mCurrentDistanceIcon = null;
        this.disappear = new Runnable() { // from class: com.sony.imaging.app.base.shooting.widget.MfAssistDistanceView.1
            @Override // java.lang.Runnable
            public void run() {
                MfAssistDistanceView.this.setVisibility(4);
            }
        };
        this.mContext = context;
        this.mHandler = new Handler();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initImageView();
        setVisibility(4);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        this.mHandler.removeCallbacks(this.disappear);
        super.onDetachedFromWindow();
    }

    public void onNotify(String tag) {
        if (CameraNotificationManager.FOCUS_POSITION_CHANGED.equals(tag)) {
            refresh();
        }
    }

    protected void refresh() {
        this.mHandler.removeCallbacks(this.disappear);
        setVisibility(0);
        invalidate();
        this.mHandler.postDelayed(this.disappear, DISPLAYED_TIME);
    }

    protected void initImageView() {
        if (this.mDistanceBar == null) {
            RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(256, 32);
            layout.leftMargin = 16;
            layout.topMargin = 30;
            this.mDistanceBar = new ImageView(this.mContext);
            this.mDistanceBar.setImageDrawable(getResources().getDrawable(17305609));
            addView(this.mDistanceBar, layout);
        }
        if (this.mCurrentDistance == null) {
            RelativeLayout.LayoutParams layout2 = new RelativeLayout.LayoutParams(256, 32);
            layout2.leftMargin = 16;
            layout2.topMargin = 30;
            this.mCurrentDistance = new CurrentDistanceValue(this.mContext);
            addView(this.mCurrentDistance, layout2);
        }
        if (Environment.getVersionPfAPI() >= 3) {
            if (this.mNearDirection == null) {
                RelativeLayout.LayoutParams layout3 = new RelativeLayout.LayoutParams(32, 32);
                layout3.leftMargin = 0;
                layout3.topMargin = 0;
                this.mNearDirection = new ImageView(this.mContext);
                this.mNearDirection.setImageDrawable(getResources().getDrawable(17307662));
                addView(this.mNearDirection, layout3);
            }
            if (this.mFarDirection == null) {
                RelativeLayout.LayoutParams layout4 = new RelativeLayout.LayoutParams(32, 32);
                layout4.leftMargin = 256;
                layout4.topMargin = 0;
                this.mFarDirection = new ImageView(this.mContext);
                this.mFarDirection.setImageDrawable(getResources().getDrawable(17307666));
                addView(this.mFarDirection, layout4);
            }
        }
        if (this.mCurrentDistanceIcon == null) {
            this.mCurrentDistanceIcon = BitmapFactory.decodeResource(this.mContext.getResources(), 17305294);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class CurrentDistanceValue extends ImageView {
        public CurrentDistanceValue(Context context) {
            super(context);
        }

        @Override // android.widget.ImageView, android.view.View
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.save();
            FocusMagnificationController controller = FocusMagnificationController.getInstance();
            Pair<Integer, Integer> range = controller.getFocusDriveRange();
            int max = ((Integer) range.first).intValue();
            int min = ((Integer) range.second).intValue();
            int index = FocusMagnificationController.getInstance().getFocusDriveCurrentPosition();
            float scale = 236.0f / (max - min);
            float posiX = scale * (index - min);
            canvas.drawBitmap(MfAssistDistanceView.this.mCurrentDistanceIcon, posiX, 0.0f, (Paint) null);
            canvas.restore();
            Log.d(MfAssistDistanceView.TAG, "onDraw current=" + index + ", posiX=" + posiX);
        }
    }
}
