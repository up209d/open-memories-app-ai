package com.sony.imaging.app.base.shooting.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.OnLayoutModeChangeListener;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.FocusMagnificationController;
import com.sony.imaging.app.base.shooting.layout.MfAssistLayout;
import com.sony.imaging.app.base.shooting.widget.MfAssistDistanceView;
import com.sony.imaging.app.base.shooting.widget.MfAssistMagnificationRatio;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class FocusAdjustmentLayout extends MfAssistLayout {
    private static final String TAG = "FocusAdjustmentLayout";
    protected MfAssistDistanceView mDistance;

    public FocusAdjustmentLayout() {
        this.mDispModeListener = new OnLayoutModeChangeListener(this, 0);
    }

    @Override // com.sony.imaging.app.base.shooting.layout.MfAssistLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        updateRatioIcon();
        this.mDistance = (MfAssistDistanceView) this.mCurrentLayout.findViewById(R.id.magnification_distance);
        return view;
    }

    @Override // com.sony.imaging.app.base.shooting.layout.MfAssistLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        this.mDistance = null;
        super.onDestroyView();
    }

    @Override // com.sony.imaging.app.base.shooting.layout.MfAssistLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.mDistance.setVisibility(4);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.layout.MfAssistLayout
    public int getLayout(int device) {
        if (device == 0) {
            int layout = R.layout.shooting_main_sid_mf_assist_panel_control_wheel;
            return layout;
        }
        if (device == 1) {
            int layout2 = R.layout.shooting_main_sid_mf_assist_evf_control_wheel;
            return layout2;
        }
        if (device != 2) {
            return -1;
        }
        int layout3 = R.layout.shooting_main_sid_mf_assist_panel_control_wheel;
        return layout3;
    }

    protected void updateRatioIcon() {
        boolean isMagnifying = FocusMagnificationController.getInstance().isMagnifying();
        ImageView focusmagIcon = (ImageView) this.mCurrentLayout.findViewById(R.id.focusmag_icon);
        if (focusmagIcon != null) {
            if (isMagnifying) {
                focusmagIcon.setVisibility(0);
            } else {
                focusmagIcon.setVisibility(4);
            }
        }
        MfAssistMagnificationRatio ratioView = (MfAssistMagnificationRatio) this.mCurrentLayout.findViewById(R.id.magnification_ratio_view);
        if (ratioView != null) {
            if (isMagnifying) {
                ratioView.setVisibility(0);
            } else {
                ratioView.setVisibility(4);
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.layout.MfAssistLayout
    protected NotificationListener getCameraNotificationListener() {
        if (!FocusMagnificationController.isSupportedByPf()) {
            return null;
        }
        if (this.mCameraListener == null) {
            this.mCameraListener = new MfAssistLayout.MfAssistListener() { // from class: com.sony.imaging.app.base.shooting.layout.FocusAdjustmentLayout.1
                private final String[] TAGS = {CameraNotificationManager.FOCUS_MAGNIFICATION_CHANGED, CameraNotificationManager.FOCUS_POSITION_CHANGED};

                @Override // com.sony.imaging.app.base.shooting.layout.MfAssistLayout.MfAssistListener, com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return this.TAGS;
                }

                @Override // com.sony.imaging.app.base.shooting.layout.MfAssistLayout.MfAssistListener, com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    if (CameraNotificationManager.FOCUS_MAGNIFICATION_CHANGED.equals(tag)) {
                        super.onNotify(tag);
                        FocusAdjustmentLayout.this.updateRatioIcon();
                    } else if (CameraNotificationManager.FOCUS_POSITION_CHANGED.equals(tag) && FocusAdjustmentLayout.this.mDistance != null) {
                        FocusAdjustmentLayout.this.mDistance.onNotify(tag);
                    }
                }
            };
        }
        return this.mCameraListener;
    }
}
