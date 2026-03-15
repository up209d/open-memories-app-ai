package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.FocusMagnificationController;

/* loaded from: classes.dex */
public class FocusMagnificationGuide_1_0 extends View {
    static final String TAG = "FocusMagnificationGuide_1_0";

    public FocusMagnificationGuide_1_0(Context context) {
        this(context, null);
    }

    public FocusMagnificationGuide_1_0(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void onNotify(String tag) {
        if (CameraNotificationManager.FOCUS_MAGNIFICATION_CHANGED.equals(tag)) {
            refresh();
        }
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        refresh();
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    protected void refresh() {
        invalidate();
    }

    @Override // android.view.View
    protected void onDraw(Canvas c) {
        Rect guideRect = refreshGuideFrame();
        if (guideRect != null) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), 17306066);
            NinePatchDrawable assistArea = new NinePatchDrawable(bitmap, bitmap.getNinePatchChunk(), null, null);
            assistArea.setBounds(guideRect);
            assistArea.draw(c);
            Log.d(TAG, " onDraw " + guideRect.toShortString());
        }
    }

    protected Rect refreshGuideFrame() {
        View parentView = (View) getParent();
        int width = parentView.getWidth();
        int height = parentView.getHeight();
        if (width == 0 || height == 0) {
            return null;
        }
        return FocusMagnificationController.getInstance().calcMagnifyingPositionRectRatio1_0(width, height);
    }
}
