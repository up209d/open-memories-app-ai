package com.sony.imaging.app.base.shooting.layout;

import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.IModableLayout;
import com.sony.imaging.app.base.common.OnLayoutModeChangeListener;
import com.sony.imaging.app.base.common.TouchManager;
import com.sony.imaging.app.base.playback.widget.TouchArea;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.widget.AppIconView;
import com.sony.imaging.app.base.shooting.widget.DigitView;
import com.sony.imaging.app.base.shooting.widget.ExposureModeIconView;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.PTag;
import com.sony.scalar.hardware.avio.DisplayManager;

/* loaded from: classes.dex */
public class StableLayout extends Layout implements IModableLayout, IStableLayout {
    protected static final SparseArray<Integer> LAYOUT_LIST_FOR_FINDER;
    protected static final SparseArray<Integer> LAYOUT_LIST_FOR_HDMI;
    protected static final SparseArray<Integer> LAYOUT_LIST_FOR_PANEL = new SparseArray<>();
    private static final String MSG_ADD_STABLE_LAYOUT = "add stable OSDs";
    private static final int USER_CHANGABLE_VIEW_COUNT = 4;
    public static final int USER_CHANGING_APERTURE = 0;
    public static final int USER_CHANGING_EXPOSURE = 2;
    public static final int USER_CHANGING_ISOSENSITIVITY = 3;
    public static final int USER_CHANGING_OTHER = -1;
    public static final int USER_CHANGING_SHUTTERSPEED = 1;
    protected IEETouchedListener mEETouchedListener;
    protected View mCurrentView = null;
    protected ViewGroup mCurrentBaseView = null;
    protected View mCurrentBaseFooterView = null;
    protected View mCurrentMainView = null;
    protected TouchArea mTouchArea = null;
    protected int mCurrentBaseViewId = -1;
    protected int mCurrentMainViewId = -1;
    protected int mCurrentBaseFooterViewId = -1;
    protected DelayAttachView idleHandler = new DelayAttachView();
    private OnLayoutModeChangeListener mListener = new OnLayoutModeChangeListener(this, 0);
    protected DigitView[] mUserChangableViews = new DigitView[4];

    /* loaded from: classes.dex */
    public interface IEETouchedListener {
        boolean onEEDoubleTap(MotionEvent motionEvent);

        boolean onEETouchDown(MotionEvent motionEvent);

        void onEETouchUp(MotionEvent motionEvent, boolean z);
    }

    @Override // com.sony.imaging.app.base.shooting.layout.IStableLayout
    public void setEETappedListener(IEETouchedListener listener) {
        this.mEETouchedListener = listener;
        if (listener != null) {
            TouchManager.getInstance().addTouchableLayout(this);
        } else {
            TouchManager.getInstance().removeTouchableLayout(this);
        }
    }

    static {
        LAYOUT_LIST_FOR_PANEL.append(1, Integer.valueOf(R.layout.shooting_main_sid_info_on_panel));
        LAYOUT_LIST_FOR_PANEL.append(3, Integer.valueOf(R.layout.shooting_main_sid_info_off_panel));
        LAYOUT_LIST_FOR_PANEL.append(5, Integer.valueOf(R.layout.shooting_main_sid_histogram_panel));
        LAYOUT_LIST_FOR_PANEL.append(4, Integer.valueOf(R.layout.shooting_main_sid_digitallevel_panel));
        LAYOUT_LIST_FOR_FINDER = new SparseArray<>();
        LAYOUT_LIST_FOR_FINDER.append(1, Integer.valueOf(R.layout.shooting_main_sid_basic_info_evf));
        LAYOUT_LIST_FOR_FINDER.append(3, Integer.valueOf(R.layout.shooting_main_sid_info_off_evf));
        LAYOUT_LIST_FOR_FINDER.append(5, Integer.valueOf(R.layout.shooting_main_sid_histogram_evf));
        LAYOUT_LIST_FOR_FINDER.append(4, Integer.valueOf(R.layout.shooting_main_sid_digitallevel_evf));
        LAYOUT_LIST_FOR_HDMI = LAYOUT_LIST_FOR_PANEL;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        DisplayModeObserver.getInstance().setNotificationListener(this.mListener);
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        DisplayModeObserver.getInstance().removeNotificationListener(this.mListener);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        super.onDestroy();
        setEETappedListener(null);
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        createView();
        return this.mCurrentView;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void detachView() {
        if (this.mCurrentBaseView != null) {
            if (this.mCurrentBaseFooterView != null) {
                this.mCurrentBaseView.removeView(this.mCurrentBaseFooterView);
                for (int i = 0; i < 4; i++) {
                    this.mUserChangableViews[i] = null;
                }
            }
            if (this.mCurrentMainView != null) {
                this.mCurrentBaseView.removeView(this.mCurrentMainView);
            }
        }
        Looper.myQueue().removeIdleHandler(this.idleHandler);
        if (this.mTouchArea != null) {
            this.mTouchArea.setTouchAreaListener(null);
            this.mTouchArea.setTouchAreaDoubleTapListener(null);
            this.mTouchArea = null;
        }
        this.mCurrentMainViewId = -1;
        this.mCurrentBaseViewId = -1;
        this.mCurrentBaseFooterViewId = -1;
        this.mCurrentMainView = null;
        this.mCurrentBaseView = null;
        this.mCurrentBaseFooterView = null;
        this.mCurrentView = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class DelayAttachView implements MessageQueue.IdleHandler {
        /* JADX INFO: Access modifiers changed from: protected */
        public DelayAttachView() {
        }

        @Override // android.os.MessageQueue.IdleHandler
        public boolean queueIdle() {
            PTag.start(StableLayout.MSG_ADD_STABLE_LAYOUT);
            StableLayout stableLayout = StableLayout.this;
            View mainView = StableLayout.this.obtainViewFromPool(StableLayout.this.mCurrentMainViewId);
            stableLayout.mCurrentMainView = mainView;
            StableLayout.this.mCurrentBaseView.addView(mainView, 0);
            PTag.end(StableLayout.MSG_ADD_STABLE_LAYOUT);
            return false;
        }
    }

    protected void createView() {
        int device = DisplayModeObserver.getInstance().getActiveDevice();
        int displayMode = DisplayModeObserver.getInstance().getActiveDispMode(0);
        int baseViewId = -1;
        int mainViewId = -1;
        int baseFooterViewId = -1;
        int touchAreaId = -1;
        if (Environment.isNewBizDeviceActionCam()) {
            baseViewId = R.layout.shooting_base_sid_basic_info_panel;
            baseFooterViewId = R.layout.shooting_base_sid_basic_footer_panel;
            mainViewId = getLayout(device, displayMode);
            touchAreaId = R.id.touchArea;
        } else if (device == 0) {
            baseViewId = R.layout.shooting_base_sid_basic_info_panel;
            baseFooterViewId = R.layout.shooting_base_sid_basic_footer_panel;
            mainViewId = getLayout(device, displayMode);
            touchAreaId = R.id.touchArea;
        } else if (device == 1) {
            baseViewId = R.layout.shooting_base_sid_basic_info_evf;
            baseFooterViewId = R.layout.shooting_base_sid_basic_footer_evf;
            mainViewId = getLayout(device, displayMode);
        } else if (device == 2) {
            baseViewId = R.layout.shooting_base_sid_basic_info_panel;
            baseFooterViewId = R.layout.shooting_base_sid_basic_footer_panel;
            mainViewId = getLayout(device, displayMode);
            touchAreaId = R.id.touchArea;
        }
        if (-1 != baseViewId && -1 != baseFooterViewId && -1 != mainViewId && (baseViewId != this.mCurrentBaseViewId || baseFooterViewId != this.mCurrentBaseFooterViewId || mainViewId != this.mCurrentMainViewId)) {
            detachView();
            this.mCurrentBaseViewId = baseViewId;
            this.mCurrentMainViewId = mainViewId;
            this.mCurrentBaseFooterViewId = baseFooterViewId;
            this.mCurrentView = obtainViewFromPool(baseViewId);
            this.mCurrentBaseView = (ViewGroup) this.mCurrentView;
            View baseFooterView = obtainViewFromPool(baseFooterViewId);
            this.mCurrentBaseFooterView = baseFooterView;
            this.mCurrentBaseView.addView(baseFooterView);
            if (baseFooterView != null) {
                this.mUserChangableViews[0] = (DigitView) baseFooterView.findViewById(R.id.aperture_view);
                this.mUserChangableViews[1] = (DigitView) baseFooterView.findViewById(R.id.shutterspeed_view);
                this.mUserChangableViews[2] = (DigitView) baseFooterView.findViewById(R.id.exposure_and_meteredmanual_view);
                this.mUserChangableViews[3] = (DigitView) baseFooterView.findViewById(R.id.iso_sensitivity_view);
            }
            Looper.myQueue().addIdleHandler(this.idleHandler);
        }
        ExposureModeIconView icon1 = (ExposureModeIconView) this.mCurrentView.findViewById(R.id.i10_1);
        ExposureModeIconView icon1_movie = (ExposureModeIconView) this.mCurrentView.findViewById(R.id.i10_1_movie);
        AppIconView icon2 = (AppIconView) this.mCurrentView.findViewById(R.id.app_icon);
        if (device == 1) {
            icon1.setAutoDisappear(false);
            if (1 == displayMode) {
                icon2.setAutoDisappear(false);
            } else {
                icon2.setAutoDisappear(true);
            }
        } else if (1 == displayMode) {
            icon1.setAutoDisappear(false);
            icon1_movie.setAutoDisappear(false);
            icon2.setAutoDisappear(false);
        } else {
            icon1.setAutoDisappear(true);
            icon1_movie.setAutoDisappear(true);
            icon2.setAutoDisappear(true);
        }
        if (-1 != touchAreaId) {
            this.mTouchArea = (TouchArea) this.mCurrentView.findViewById(touchAreaId);
            if (this.mTouchArea != null) {
                TouchArea.OnTouchAreaListener touchListener = getOnTouchListener();
                this.mTouchArea.setTouchAreaListener(touchListener);
                if (touchListener instanceof TouchArea.OnTouchAreaDoubleTapListener) {
                    this.mTouchArea.setTouchAreaDoubleTapListener((TouchArea.OnTouchAreaDoubleTapListener) touchListener);
                }
            }
        }
    }

    protected int getLayout(int device, int dispmode) {
        if (Environment.isNewBizDeviceActionCam()) {
            switch (ExecutorCreator.getInstance().getRecordingMode()) {
                case 1:
                    int layout = R.layout.shooting_base_sid_sublcd_photo;
                    return layout;
                case 2:
                    int layout2 = R.layout.shooting_base_sid_sublcd_movie;
                    return layout2;
                case 3:
                case 5:
                case 6:
                case 7:
                default:
                    return -1;
                case 4:
                    int layout3 = R.layout.shooting_base_sid_sublcd_photo;
                    return layout3;
                case 8:
                    int layout4 = R.layout.shooting_base_sid_sublcd_photo;
                    return layout4;
            }
        }
        if (device == 0) {
            int layout5 = LAYOUT_LIST_FOR_PANEL.get(dispmode).intValue();
            return layout5;
        }
        if (device == 1) {
            int layout6 = LAYOUT_LIST_FOR_FINDER.get(dispmode).intValue();
            return layout6;
        }
        if (device != 2) {
            return -1;
        }
        int layout7 = LAYOUT_LIST_FOR_HDMI.get(dispmode).intValue();
        return layout7;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PTag.end(DisplayModeObserver.PTAG_DISPCHANGE_COMMON);
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        detachView();
    }

    @Override // com.sony.imaging.app.base.shooting.layout.IStableLayout
    public void setUserChanging(int whatUserChanging) {
        int i = 0;
        while (i < 4) {
            if (this.mUserChangableViews[i] != null) {
                boolean changing = i == whatUserChanging;
                this.mUserChangableViews[i].setUserChanging(changing);
            }
            i++;
        }
    }

    @Override // com.sony.imaging.app.base.common.IModableLayout
    public void onLayoutModeChanged(int device, int displayMode) {
        updateView();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class OnEETouchListener implements TouchArea.OnTouchAreaListener, TouchArea.OnTouchAreaDoubleTapListener {
        protected OnEETouchListener() {
        }

        @Override // com.sony.imaging.app.base.playback.widget.TouchArea.OnTouchAreaListener
        public boolean onTouchDown(MotionEvent e) {
            if (StableLayout.this.mEETouchedListener == null) {
                return false;
            }
            DisplayManager.VideoRect yuvArea = (DisplayManager.VideoRect) DisplayModeObserver.getInstance().getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
            int x = Math.round(e.getRawX());
            int y = Math.round(e.getRawY());
            boolean isInside = x >= yuvArea.pxLeft && x <= yuvArea.pxRight && y >= yuvArea.pxTop && y <= yuvArea.pxBottom;
            if (isInside) {
                return StableLayout.this.mEETouchedListener.onEETouchDown(e);
            }
            return false;
        }

        @Override // com.sony.imaging.app.base.playback.widget.TouchArea.OnTouchAreaListener
        public boolean onTouchUp(MotionEvent e, boolean isReleasedInside) {
            if (StableLayout.this.mEETouchedListener != null) {
                DisplayManager.VideoRect yuvArea = (DisplayManager.VideoRect) DisplayModeObserver.getInstance().getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
                int x = Math.round(e.getRawX());
                int y = Math.round(e.getRawY());
                boolean isReleasedInside2 = x >= yuvArea.pxLeft && x <= yuvArea.pxRight && y >= yuvArea.pxTop && y <= yuvArea.pxBottom;
                StableLayout.this.mEETouchedListener.onEETouchUp(e, isReleasedInside2);
            }
            return true;
        }

        @Override // com.sony.imaging.app.base.playback.widget.TouchArea.OnTouchAreaListener
        public boolean onFlick(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

        @Override // com.sony.imaging.app.base.playback.widget.TouchArea.OnTouchAreaListener
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return true;
        }

        @Override // com.sony.imaging.app.base.playback.widget.TouchArea.OnTouchAreaDoubleTapListener
        public boolean onDoubleTap(MotionEvent e) {
            if (StableLayout.this.mEETouchedListener == null) {
                return false;
            }
            DisplayManager.VideoRect yuvArea = (DisplayManager.VideoRect) DisplayModeObserver.getInstance().getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
            int x = Math.round(e.getRawX());
            int y = Math.round(e.getRawY());
            boolean isInside = x >= yuvArea.pxLeft && x <= yuvArea.pxRight && y >= yuvArea.pxTop && y <= yuvArea.pxBottom;
            if (isInside) {
                return StableLayout.this.mEETouchedListener.onEEDoubleTap(e);
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public TouchArea.OnTouchAreaListener getOnTouchListener() {
        return new OnEETouchListener();
    }
}
