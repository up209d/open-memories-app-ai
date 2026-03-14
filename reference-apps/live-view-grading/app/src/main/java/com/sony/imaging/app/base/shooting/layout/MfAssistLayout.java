package com.sony.imaging.app.base.shooting.layout;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.IModableLayout;
import com.sony.imaging.app.base.common.OnLayoutModeChangeListener;
import com.sony.imaging.app.base.playback.widget.TouchArea;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.FocusMagnificationController;
import com.sony.imaging.app.base.shooting.widget.FocusMagnificationGuide;
import com.sony.imaging.app.base.shooting.widget.FocusMagnificationGuide_1_0;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.RelativeLayoutGroup;
import com.sony.scalar.hardware.avio.DisplayManager;

/* loaded from: classes.dex */
public class MfAssistLayout extends Layout implements IModableLayout {
    protected static final int ACTIVE_GUIDE_TYPE_GUIDE_1_0 = 2;
    protected static final int ACTIVE_GUIDE_TYPE_GUIDE_PIXEL = 1;
    protected static final int ACTIVE_GUIDE_TYPE_UNKNOWN = 0;
    private static final String[] TAGS = {CameraNotificationManager.FOCUS_MAGNIFICATION_CHANGED};
    protected OnLayoutModeChangeListener mDispModeListener;
    protected FocusMagnificationGuide mGuide;
    protected FocusMagnificationGuide_1_0 mGuide_1_0;
    protected ViewGroup mCurrentLayout = null;
    protected TouchArea mTouchArea = null;
    protected NotificationListener mCameraListener = null;
    protected int mActiveGuide = 0;

    public MfAssistLayout() {
        this.mDispModeListener = null;
        this.mDispModeListener = new OnLayoutModeChangeListener(this, 0);
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayoutGroup batteryIconGroup;
        super.onCreateView(inflater, container, savedInstanceState);
        int device = DisplayModeObserver.getInstance().getActiveDevice();
        int layoutId = getLayout(device);
        if (-1 != layoutId) {
            this.mCurrentLayout = (ViewGroup) obtainViewFromPool(layoutId);
        }
        if (this.mCurrentLayout != null) {
            this.mTouchArea = (TouchArea) this.mCurrentLayout.findViewById(R.id.touchArea);
            if (this.mTouchArea != null) {
                this.mTouchArea.setTouchAreaListener(getOnTouchAreaListener());
            }
            this.mGuide = (FocusMagnificationGuide) this.mCurrentLayout.findViewById(R.id.magnification_position_view);
            this.mGuide_1_0 = (FocusMagnificationGuide_1_0) this.mCurrentLayout.findViewById(R.id.magnification_position_view_1_0);
            updateArrow();
            updateGuide();
            if ((device == 0 || device == 2) && (batteryIconGroup = (RelativeLayoutGroup) this.mCurrentLayout.findViewById(R.id.battery_icon_on)) != null) {
                int dispmode = DisplayModeObserver.getInstance().getActiveDispMode(0);
                if (1 == dispmode) {
                    batteryIconGroup.setVisibility(0);
                } else {
                    batteryIconGroup.setVisibility(4);
                }
            }
        }
        return this.mCurrentLayout;
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        if (this.mTouchArea != null) {
            this.mTouchArea.setTouchAreaListener(null);
        }
        this.mTouchArea = null;
        this.mCurrentLayout = null;
        this.mGuide = null;
        this.mGuide_1_0 = null;
        super.onDestroyView();
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.mActiveGuide = 0;
        DisplayModeObserver.getInstance().setNotificationListener(this.mDispModeListener);
        this.mCameraListener = getCameraNotificationListener();
        if (this.mCameraListener != null) {
            CameraNotificationManager.getInstance().setNotificationListener(this.mCameraListener);
        }
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (this.mCameraListener != null) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mCameraListener);
            this.mCameraListener = null;
        }
        DisplayModeObserver.getInstance().removeNotificationListener(this.mDispModeListener);
        super.onPause();
    }

    protected int getLayout(int device) {
        if (device == 0) {
            int layout = R.layout.shooting_main_sid_mf_assist_panel;
            return layout;
        }
        if (device == 1) {
            int layout2 = R.layout.shooting_main_sid_mf_assist_evf;
            return layout2;
        }
        if (device != 2) {
            return -1;
        }
        int layout3 = R.layout.shooting_main_sid_mf_assist_panel;
        return layout3;
    }

    protected void updateArrow() {
        FocusMagnificationController controller = FocusMagnificationController.getInstance();
        ImageView left = (ImageView) this.mCurrentLayout.findViewById(R.id.left_arrow);
        ImageView right = (ImageView) this.mCurrentLayout.findViewById(R.id.right_arrow);
        ImageView up = (ImageView) this.mCurrentLayout.findViewById(R.id.up_arrow);
        ImageView down = (ImageView) this.mCurrentLayout.findViewById(R.id.down_arrow);
        Pair<Integer, Integer> max = controller.getNumberOfMovingSteps();
        Pair<Integer, Integer> current = controller.getMagnifyingPosition();
        boolean isPanelReverse = DisplayModeObserver.getInstance().isPanelReverse();
        int ratio = controller.getMagnificationRatio();
        boolean isMovableMagnifyPoint = controller.isAvailableMagnificationPointMove();
        if (ratio == 1 || !controller.isMagnifying() || !isMovableMagnifyPoint) {
            left.setVisibility(4);
            right.setVisibility(4);
            up.setVisibility(4);
            down.setVisibility(4);
            return;
        }
        if (left != null) {
            if (((Integer) max.first).intValue() == (isPanelReverse ? ((Integer) current.first).intValue() : -((Integer) current.first).intValue())) {
                left.setVisibility(4);
            } else {
                left.setVisibility(0);
            }
        }
        if (right != null) {
            if (((Integer) max.first).intValue() == (isPanelReverse ? -((Integer) current.first).intValue() : ((Integer) current.first).intValue())) {
                right.setVisibility(4);
            } else {
                right.setVisibility(0);
            }
        }
        if (up != null) {
            if (((Integer) max.second).intValue() == (-((Integer) current.second).intValue())) {
                up.setVisibility(4);
            } else {
                up.setVisibility(0);
            }
        }
        if (down != null) {
            if (max.second == current.second) {
                down.setVisibility(4);
            } else {
                down.setVisibility(0);
            }
        }
    }

    protected void updateGuide() {
        FocusMagnificationController controller = FocusMagnificationController.getInstance();
        if (controller.isMagnifying()) {
            int ratio = controller.getMagnificationRatio();
            if (ratio == 1) {
                if (this.mGuide != null) {
                    this.mGuide.setVisibility(4);
                }
                if (this.mGuide_1_0 != null) {
                    this.mGuide_1_0.setVisibility(0);
                }
                this.mActiveGuide = 2;
                return;
            }
            if (this.mGuide != null) {
                this.mGuide.setVisibility(0);
            }
            if (this.mGuide_1_0 != null) {
                this.mGuide_1_0.setVisibility(4);
            }
            this.mActiveGuide = 1;
            return;
        }
        if (this.mGuide != null) {
            this.mGuide.setVisibility(4);
        }
        if (this.mGuide_1_0 != null) {
            this.mGuide_1_0.setVisibility(4);
        }
        this.mActiveGuide = 0;
    }

    protected TouchArea.OnTouchAreaListener getOnTouchAreaListener() {
        return new MfAssistTouchListner();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class MfAssistTouchListner implements TouchArea.OnTouchAreaListener {
        protected MfAssistTouchListner() {
        }

        @Override // com.sony.imaging.app.base.playback.widget.TouchArea.OnTouchAreaListener
        public boolean onTouchDown(MotionEvent e) {
            return false;
        }

        @Override // com.sony.imaging.app.base.playback.widget.TouchArea.OnTouchAreaListener
        public boolean onTouchUp(MotionEvent e, boolean isReleasedInside) {
            DisplayManager.VideoRect yuvArea = (DisplayManager.VideoRect) DisplayModeObserver.getInstance().getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
            int x = Math.round(e.getRawX());
            int y = Math.round(e.getRawY());
            if (x >= yuvArea.pxLeft && x <= yuvArea.pxRight && y >= yuvArea.pxTop && y <= yuvArea.pxBottom) {
                FocusMagnificationController controller = FocusMagnificationController.getInstance();
                controller.rescheduleTimeout();
                if (controller.moveToCoordinateOnEE(x, y)) {
                    BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_TAP);
                    return true;
                }
                return true;
            }
            return true;
        }

        @Override // com.sony.imaging.app.base.playback.widget.TouchArea.OnTouchAreaListener
        public boolean onFlick(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return true;
        }

        @Override // com.sony.imaging.app.base.playback.widget.TouchArea.OnTouchAreaListener
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return true;
        }
    }

    @Override // com.sony.imaging.app.base.common.IModableLayout
    public void onLayoutModeChanged(int device, int displayMode) {
        updateView();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class MfAssistListener implements NotificationListener {
        /* JADX INFO: Access modifiers changed from: protected */
        public MfAssistListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return MfAssistLayout.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            FocusMagnificationController controller = FocusMagnificationController.getInstance();
            if (CameraNotificationManager.FOCUS_MAGNIFICATION_CHANGED.equals(tag) && controller.isMagnifying()) {
                MfAssistLayout.this.updateArrow();
                MfAssistLayout.this.updateGuide();
            }
            if (MfAssistLayout.this.mActiveGuide == 2) {
                if (MfAssistLayout.this.mGuide_1_0 != null) {
                    MfAssistLayout.this.mGuide_1_0.onNotify(tag);
                }
            } else if (MfAssistLayout.this.mActiveGuide == 1 && MfAssistLayout.this.mGuide != null) {
                MfAssistLayout.this.mGuide.onNotify(tag);
            }
        }
    }

    protected NotificationListener getCameraNotificationListener() {
        if (FocusMagnificationController.isSupportedByPf()) {
            return new MfAssistListener();
        }
        return null;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int movedAelFocusSlideKey() {
        FocusMagnificationController.getInstance().stop();
        return 0;
    }
}
