package com.sony.imaging.app.base.playback.layout;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.BaseProperties;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.IModableLayout;
import com.sony.imaging.app.base.common.OnLayoutModeChangeListener;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.widget.GraphView;
import com.sony.imaging.app.base.playback.widget.TouchArea;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.PTag;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.meta.Histogram;
import com.sony.scalar.widget.OptimizedImageView;

/* loaded from: classes.dex */
public abstract class SingleLayoutBase extends OptimizedImageLayoutBase implements IModableLayout, TouchArea.OnTouchAreaListener {
    protected static final int API_VER_SUPPORT_DOWN_KEY_ASSIGN = 4;
    private static final int ASPECT_16_9 = 17;
    private static final int DISPLAY_LAYOUT_EMT = 0;
    private static final int DISPLAY_LAYOUT_P1_169 = 2;
    private static final int DISPLAY_LAYOUT_P1_43 = 1;
    private static final String MSG_CANNOT_TRANSITON_TO_INDEX = "onKeyDwn:DPAD_DOWN INVALID because initialQuery has not completed";
    private static final String MSG_OPT_IMG_VIEW_LAYOUT = "OptLayout ";
    private static final String MSG_REGISTER_HISTO_LISTENER_VIEW_NOT_EXIST = "registerHistogramEventListener View is null";
    private static final String MSG_SET_PIVOT = "setPivot ";
    private static final String MSG_UNREGISTER_HISTO_LISTENER_VIEW_NOT_EXIST = "unregisterHistogramEventListener View is null";
    private static final int PRECISION = 10;
    private static final Rect YUV_LAYOUT_NORMAL = new Rect(0, 0, AppRoot.USER_KEYCODE.WATER_HOUSING, 480);
    private static final Rect[] YUV_LAYOUT_TABLE_HISTOGRAM = {new Rect(64, 48, 348, 294), new Rect(104, 72, 392, 260), new Rect(140, 72, 356, 260)};
    private static Rect mScratchRect = new Rect();
    protected OnLayoutModeChangeListener mListener = new OnLayoutModeChangeListener(this, 1);

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PTag.end(DisplayModeObserver.PTAG_DISPCHANGE_COMMON);
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        DisplayModeObserver.getInstance().setNotificationListener(this.mListener);
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        DisplayModeObserver.getInstance().removeNotificationListener(this.mListener);
        unregisterHistogramEventListener(this.mOptImgView);
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void onReopened() {
        invalidate();
    }

    @Override // com.sony.imaging.app.base.common.IModableLayout
    public void onLayoutModeChanged(int device, int displayMode) {
        this.mMuteOptimizedImageViewOnPause = false;
        updateView();
    }

    protected void registerHistogramEventListener(OptimizedImageView imgView) {
        if (imgView == null) {
            Log.w(this.TAG, MSG_REGISTER_HISTO_LISTENER_VIEW_NOT_EXIST);
            return;
        }
        int displayMode = DisplayModeObserver.getInstance().getActiveDispMode(1);
        if (displayMode == 5) {
            imgView.setOnHistogramEventListener(new OptimizedImageView.onHistogramEventListener() { // from class: com.sony.imaging.app.base.playback.layout.SingleLayoutBase.1
                public void onHistogram(int errCd, Histogram histogram) {
                    View containerView = SingleLayoutBase.this.getView();
                    GraphView yGraphView = (GraphView) containerView.findViewById(R.id.yGraphView);
                    GraphView rGraphView = (GraphView) containerView.findViewById(R.id.rGraphView);
                    GraphView gGraphView = (GraphView) containerView.findViewById(R.id.gGraphView);
                    GraphView bGraphView = (GraphView) containerView.findViewById(R.id.bGraphView);
                    if (histogram != null && yGraphView != null && rGraphView != null && gGraphView != null && bGraphView != null) {
                        yGraphView.setHistogram(histogram.Y);
                        rGraphView.setHistogram(histogram.R);
                        gGraphView.setHistogram(histogram.G);
                        bGraphView.setHistogram(histogram.B);
                    }
                }
            });
        }
    }

    protected void unregisterHistogramEventListener(OptimizedImageView imgView) {
        if (imgView == null) {
            Log.w(this.TAG, MSG_UNREGISTER_HISTO_LISTENER_VIEW_NOT_EXIST);
        } else {
            imgView.setOnHistogramEventListener((OptimizedImageView.onHistogramEventListener) null);
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    public void updateOptimizedImage(OptimizedImage image) {
        if (image != null) {
            registerHistogramEventListener(this.mOptImgView);
        } else {
            unregisterHistogramEventListener(this.mOptImgView);
        }
        super.updateOptimizedImage(image);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    public void updateOptimizedImageLayoutParam(OptimizedImageView imgView, ContentsManager mgr) {
        Rect targetLayout;
        super.updateOptimizedImageLayoutParam(imgView, mgr);
        DisplayModeObserver displayModeObserver = DisplayModeObserver.getInstance();
        int displayMode = displayModeObserver.getActiveDispMode(1);
        if (displayMode != 5) {
            targetLayout = YUV_LAYOUT_NORMAL;
        } else if (Environment.getVersionOfHW() == 1) {
            targetLayout = YUV_LAYOUT_TABLE_HISTOGRAM[0];
        } else {
            int aspect = displayModeObserver.getActiveDeviceOsdAspect();
            if (2 == aspect) {
                targetLayout = YUV_LAYOUT_TABLE_HISTOGRAM[2];
            } else {
                targetLayout = YUV_LAYOUT_TABLE_HISTOGRAM[1];
            }
        }
        if (targetLayout != null) {
            imgView.getHitRect(mScratchRect);
            Log.i(this.TAG, LogHelper.getScratchBuilder(MSG_OPT_IMG_VIEW_LAYOUT).append(mScratchRect).append(" -> ").append(targetLayout).toString());
            if (!targetLayout.equals(mScratchRect)) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) imgView.getLayoutParams();
                params.leftMargin = targetLayout.left;
                params.topMargin = targetLayout.top;
                params.width = targetLayout.width();
                params.height = targetLayout.height();
                Point p = new Point(params.width >> 1, params.height >> 1);
                Log.i(this.TAG, LogHelper.getScratchBuilder(MSG_SET_PIVOT).append(p.x).append(", ").append(p.y).toString());
                imgView.setPivot(p);
                imgView.setLayoutParams(params);
            }
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDispFuncKey() {
        DisplayModeObserver.getInstance().toggleDisplayMode(1);
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        return pushedDispFuncKey();
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPbZoomFuncMinus() {
        ContentsManager mgr = ContentsManager.getInstance();
        boolean isQueryDone = mgr.isInitialQueryDone();
        if (isQueryDone) {
            thinZoomKey();
            transitionIndexPb();
            return 1;
        }
        Log.i(this.TAG, MSG_CANNOT_TRANSITON_TO_INDEX);
        return -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        if (Environment.getVersionOfHW() == 1) {
            return pushedPbZoomFuncMinus();
        }
        if (BaseProperties.isDownKeyAssignedToIndexTransition()) {
            return pushedPbZoomFuncMinus();
        }
        return super.pushedDownKey();
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        return previousContents() ? 1 : -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        return nextContents() ? 1 : -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        return previousContents() ? 1 : -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        return nextContents() ? 1 : -1;
    }

    @Override // com.sony.imaging.app.base.playback.widget.TouchArea.OnTouchAreaListener
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override // com.sony.imaging.app.base.playback.widget.TouchArea.OnTouchAreaListener
    public boolean onTouchDown(MotionEvent e) {
        return false;
    }

    @Override // com.sony.imaging.app.base.playback.widget.TouchArea.OnTouchAreaListener
    public boolean onFlick(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (velocityX < 0.0f) {
            nextContents();
            return true;
        }
        if (velocityX > 0.0f) {
            previousContents();
            return true;
        }
        return true;
    }
}
