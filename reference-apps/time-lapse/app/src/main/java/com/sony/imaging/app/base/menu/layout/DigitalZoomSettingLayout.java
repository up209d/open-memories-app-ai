package com.sony.imaging.app.base.menu.layout;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.DigitalZoomController;
import com.sony.imaging.app.base.shooting.movie.MovieShootingExecutor;
import com.sony.imaging.app.util.NotificationListener;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class DigitalZoomSettingLayout extends DisplayMenuItemsMenuLayout {
    public static final String MENU_ID = "ID_DIGITALZOOMSETTINGLAYOUT";
    private static final String TAG = "DigitalZoomSettingLayout";
    private int mCancelZoomMag;
    private DigitalZoomController mDigitalZoomController;
    private FooterGuide mFooterGuide;
    private TextView mRecTextView;
    private View mCurrentView = null;
    private boolean mIsMovieRecording = false;
    private NotificationListener mNotificationListener = new DigitalZoomCompOptLayoutListener();
    private ArrayList<String> mParentItems = new ArrayList<>();

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Context context = getActivity().getApplicationContext();
        this.mService = new BaseMenuService(context);
        this.mCurrentView = obtainViewFromPool(R.layout.menu_digital_zoom_setting);
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected boolean isParentItemAvailable() {
        if (this.mParentItems == null || this.mService == null) {
            return true;
        }
        this.mParentItems.clear();
        this.mParentItems.add(this.mService.getMenuItemId());
        this.mService.updateSettingItemsAvailable(this.mParentItems);
        return this.mService.isMenuItemValid(this.mService.getMenuItemId());
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        this.mCurrentView = null;
        this.mRecTextView = null;
        this.mFooterGuide = null;
        super.onDestroyView();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.mDigitalZoomController = DigitalZoomController.getInstance();
        this.mCancelZoomMag = this.mDigitalZoomController.getCurrentDigitalZoomMagnification();
        this.mDigitalZoomController.createStepZoomArray();
        CameraNotificationManager notifier = CameraNotificationManager.getInstance();
        notifier.setNotificationListener(this.mNotificationListener);
        if (2 == CameraSetting.getInstance().getCurrentMode() && MovieShootingExecutor.isMovieRecording()) {
            this.mIsMovieRecording = true;
        } else {
            this.mIsMovieRecording = false;
        }
        this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.digitalzoom_layout_footer_guide);
        this.mRecTextView = (TextView) this.mCurrentView.findViewById(R.id.footer_rec);
        if (this.mIsMovieRecording) {
            FooterGuideDataResId data = new FooterGuideDataResId(getActivity().getApplicationContext(), android.R.string.autofill_province, android.R.string.autofill_province);
            this.mFooterGuide.setData(data);
            this.mRecTextView.setVisibility(0);
        } else {
            FooterGuideDataResId data2 = new FooterGuideDataResId(getActivity().getApplicationContext(), android.R.string.autofill_prefecture, android.R.string.httpErrorTooManyRequests);
            this.mFooterGuide.setData(data2);
            this.mRecTextView.setVisibility(8);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        CameraNotificationManager notifier = CameraNotificationManager.getInstance();
        notifier.removeNotificationListener(this.mNotificationListener);
        this.mDigitalZoomController = null;
        if (this.mParentItems != null) {
            this.mParentItems.clear();
            this.mParentItems = null;
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        closeMenuLayout(null);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        if (this.mIsMovieRecording) {
            return -1;
        }
        this.mDigitalZoomController.setStepZoomMagnification(DigitalZoomController.STEP_ZOOM_UPPER);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        if (this.mIsMovieRecording) {
            return -1;
        }
        this.mDigitalZoomController.setStepZoomMagnification(DigitalZoomController.STEP_ZOOM_LOWER);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        changeValue(this.mDigitalZoomController.getCurrentDigitalZoomMagnification() - 10);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        changeValue(this.mDigitalZoomController.getCurrentDigitalZoomMagnification() + 10);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        return pushedLeftKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        return pushedRightKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        return pushedLeftKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        return pushedRightKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        this.mDigitalZoomController.setZoomMagnification(this.mCancelZoomMag);
        openPreviousMenu();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        String itemId = this.mService.getMenuItemId();
        if (itemId != null) {
            guideResources.add(this.mService.getMenuItemText(itemId));
            guideResources.add(this.mService.getMenuItemGuideText(itemId));
        }
    }

    protected void changeValue(int value) {
        this.mDigitalZoomController.setZoomMagnification(value);
    }

    /* loaded from: classes.dex */
    private class DigitalZoomCompOptLayoutListener implements NotificationListener {
        private final String[] tags;

        private DigitalZoomCompOptLayoutListener() {
            this.tags = new String[]{CameraNotificationManager.DEVICE_LENS_CHANGED};
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (CameraNotificationManager.DEVICE_LENS_CHANGED.equals(tag)) {
                DigitalZoomSettingLayout.this.closeZoomCompensationLayout();
            }
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeZoomCompensationLayout() {
        closeMenuLayout(null);
    }
}
