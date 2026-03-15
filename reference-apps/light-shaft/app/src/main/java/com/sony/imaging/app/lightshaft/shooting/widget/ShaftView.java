package com.sony.imaging.app.lightshaft.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.lightshaft.AppLog;
import com.sony.imaging.app.lightshaft.R;
import com.sony.imaging.app.lightshaft.shooting.ShaftsEffect;
import com.sony.imaging.app.lightshaft.shooting.camera.EffectSelectController;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.avio.DisplayManager;

/* loaded from: classes.dex */
public class ShaftView extends View {
    private static final String TAG = "ShaftView";
    private EffectView mEffectShaftView;
    protected NotificationListener mListener;
    protected ShaftsEffect.Parameters mParams;

    public ShaftView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mEffectShaftView = null;
        Log.d(TAG, "INSIDE ShaftView Constructor");
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShaftView);
        try {
            EffectSelectController.getInstance().setShootingEnable(typedArray.getBoolean(0, false));
            Log.d(TAG, "INSIDE ShaftView , is_shooting: " + typedArray.getBoolean(0, false));
        } catch (NullPointerException e) {
            AppLog.error(TAG, "NullPointerException occuered");
        }
        typedArray.recycle();
        this.mListener = new NotificationListener() { // from class: com.sony.imaging.app.lightshaft.shooting.widget.ShaftView.1
            private final String[] TAG = {ShaftsEffect.TAG_CHANGE};

            @Override // com.sony.imaging.app.util.NotificationListener
            public String[] getTags() {
                return this.TAG;
            }

            @Override // com.sony.imaging.app.util.NotificationListener
            public void onNotify(String tag) {
                ShaftView.this.refresh();
            }
        };
        this.mParams = null;
    }

    public void setParameters(ShaftsEffect.Parameters p) {
        this.mParams = p;
        AppLog.info(TAG, "Parameters : " + p);
        getEffectViewInstance();
        this.mEffectShaftView.setUpdatedParameter(p);
        refresh();
    }

    private void getEffectViewInstance() {
        switch (this.mParams.getEffect()) {
            case 2:
                if (this.mEffectShaftView == null || !(this.mEffectShaftView instanceof StarShaftEffectView)) {
                    this.mEffectShaftView = new StarShaftEffectView();
                    return;
                }
                return;
            case 3:
                if (this.mEffectShaftView == null || !(this.mEffectShaftView instanceof FlareEffectView)) {
                    this.mEffectShaftView = new FlareEffectView();
                    return;
                }
                return;
            case 4:
                if (this.mEffectShaftView == null || !(this.mEffectShaftView instanceof BeamEffectView)) {
                    this.mEffectShaftView = new BeamEffectView();
                    return;
                }
                return;
            default:
                if (this.mEffectShaftView == null || !(this.mEffectShaftView instanceof AngelLadderView)) {
                    this.mEffectShaftView = new AngelLadderView();
                    return;
                }
                return;
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        DisplayModeObserver disp = DisplayModeObserver.getInstance();
        DisplayManager.VideoRect rect = (DisplayManager.VideoRect) disp.getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
        Rect clip = new Rect(rect.pxLeft, rect.pxTop, rect.pxRight, rect.pxBottom);
        AppLog.info(TAG, "clip: " + clip.toString());
        clip.offset(-getLeft(), -getTop());
        AppLog.info(TAG, "clip: " + clip.toString());
        canvas.clipRect(clip);
        if (this.mParams != null) {
            canvas.save();
            drawSelectedEffect(canvas);
            canvas.restore();
        }
        super.onDraw(canvas);
    }

    private void drawSelectedEffect(Canvas canvas) {
        this.mEffectShaftView.setResources(getResources());
        this.mEffectShaftView.drawBasicEffectView(canvas);
        if (!EffectSelectController.getInstance().isShootingEnable()) {
            Log.d(TAG, "INSIDE drawSelectedEffect() drawSelectedEffect");
            this.mEffectShaftView.setEffectProperty();
        }
    }

    protected void refresh() {
        invalidate();
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setParameters(ShaftsEffect.getInstance().getParameters());
        ShaftsEffect.getInstance().setNotificationListener(this.mListener);
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        if (this.mEffectShaftView != null) {
            this.mEffectShaftView.onDetachedWindow();
            this.mEffectShaftView = null;
        }
        ShaftsEffect.getInstance().removeNotificationListener(this.mListener);
        super.onDetachedFromWindow();
    }
}
