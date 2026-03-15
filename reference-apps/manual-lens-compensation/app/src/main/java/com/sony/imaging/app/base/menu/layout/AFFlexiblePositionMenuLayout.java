package com.sony.imaging.app.base.menu.layout;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.DigitalZoomController;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.base.shooting.widget.AFFlexiblePositionSetting;
import com.sony.imaging.app.base.shooting.widget.AFFlexibleSpotVariableSizePositionSetting;
import com.sony.imaging.app.base.shooting.widget.AbstractAFView;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class AFFlexiblePositionMenuLayout extends BaseMenuLayout implements AFFlexiblePositionSetting.PositionListener {
    private Pair<Integer, Integer> mCancelRect;
    private View mCurrentView;
    private NotificationListener mNotificationListener = new AFFlexiblePositionMenuLayoutListener();
    private boolean mIsSupportedFlexibleSpotSize = false;
    List<String> mSupportSizes = null;

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (isPFverOver2()) {
            this.mSupportSizes = new ArrayList();
            this.mSupportSizes = ((CameraEx.ParametersModifier) CameraSetting.getInstance().getSupportedParameters().second).getSupportedFocusAreaFlexibleSpotSizes();
            if (this.mSupportSizes != null) {
                this.mIsSupportedFlexibleSpotSize = true;
            } else {
                this.mIsSupportedFlexibleSpotSize = false;
            }
        }
        if (this.mIsSupportedFlexibleSpotSize) {
            AFFlexibleSpotVariableSizePositionSetting spot_size_variable_view = (AFFlexibleSpotVariableSizePositionSetting) obtainViewFromPool(R.layout.af_flexible_spot_variable_size_position_set);
            spot_size_variable_view.setPositionListener(this);
            this.mCurrentView = spot_size_variable_view;
        } else {
            AFFlexiblePositionSetting spot_size_fixed_view = (AFFlexiblePositionSetting) obtainViewFromPool(R.layout.af_flexible_spot_position_set);
            spot_size_fixed_view.setPositionListener(this);
            this.mCurrentView = spot_size_fixed_view;
        }
        this.mCancelRect = FocusAreaController.getInstance().getFocusPoint();
        return this.mCurrentView;
    }

    public static boolean isPFverOver2() {
        return 2 <= CameraSetting.getPfApiVersion();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        this.mCurrentView = null;
        super.onDestroyView();
    }

    @Override // com.sony.imaging.app.base.shooting.widget.AFFlexiblePositionSetting.PositionListener
    public void onPositionChanged(Rect rect) {
        FocusAreaController fas = FocusAreaController.getInstance();
        Rect imagerRect = AbstractAFView.convertOSDtoScalar(rect);
        fas.setFocusPoint(imagerRect.centerX(), imagerRect.centerY());
        fas.setValue(FocusAreaController.FLEX);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        if (this.mCancelRect != null) {
            FocusAreaController fas = FocusAreaController.getInstance();
            fas.setFocusPoint(((Integer) this.mCancelRect.first).intValue(), ((Integer) this.mCancelRect.second).intValue());
            fas.setValue(FocusAreaController.FLEX);
            this.mCancelRect = null;
        }
        openPreviousMenu();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        closeMenuLayout(null);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        if (DigitalZoomController.getInstance().isAvailable(DigitalZoomController.TAG_DIGITAL_ZOOM_TYPE)) {
            DigitalZoomController.getInstance().setDigitalZoomOFFTemporarily(true);
        }
        CameraNotificationManager notifier = CameraNotificationManager.getInstance();
        notifier.setNotificationListener(this.mNotificationListener);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        if (DigitalZoomController.getInstance().isAvailable(DigitalZoomController.TAG_DIGITAL_ZOOM_TYPE)) {
            DigitalZoomController.getInstance().setDigitalZoomOFFTemporarily(false);
        }
        CameraNotificationManager notifier = CameraNotificationManager.getInstance();
        notifier.removeNotificationListener(this.mNotificationListener);
        this.mSupportSizes = null;
    }

    /* loaded from: classes.dex */
    private class AFFlexiblePositionMenuLayoutListener implements NotificationListener {
        private final String[] tags;

        private AFFlexiblePositionMenuLayoutListener() {
            this.tags = new String[]{CameraNotificationManager.DEVICE_LENS_CHANGED};
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (CameraNotificationManager.DEVICE_LENS_CHANGED.equals(tag)) {
                AFFlexiblePositionMenuLayout.this.closeAFFlexiblePositionMenuLayout();
            }
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeAFFlexiblePositionMenuLayout() {
        closeMenuLayout(null);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver
    protected String getKeyConvCategory() {
        return ICustomKey.CATEGORY_FOCUS_SETTING;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected boolean isAELCancel() {
        return false;
    }
}
