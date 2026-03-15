package com.sony.imaging.app.base.shooting.layout;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.IModableLayout;
import com.sony.imaging.app.base.common.widget.AutoSwitchLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.layout.StableLayout;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class SubLcdStableLayout extends Layout implements IModableLayout, IStableLayout {
    public static final String KEY_START_FROM_TOP = "Key_StartFromTop";
    protected static String[] tags = {CameraNotificationManager.REC_MODE_CHANGED};
    protected NotificationListener mListener;
    private SparseArray<Integer> mMainLayoutList;
    private View mCurrentView = null;
    private AutoSwitchLayout mCurrent14SegLayout = null;
    private int mCurrentRecMode = -1;
    protected AutoSwitchLayout mSegmentModeView = null;

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class RecModeListener implements NotificationListener {
        protected RecModeListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return SubLcdStableLayout.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (SubLcdStableLayout.this.mCurrentRecMode != ExecutorCreator.getInstance().getRecordingMode()) {
                SubLcdStableLayout.this.updateView();
            }
        }
    }

    protected NotificationListener getListener() {
        return new RecModeListener();
    }

    public SubLcdStableLayout() {
        this.mMainLayoutList = null;
        this.mMainLayoutList = new SparseArray<>();
        this.mMainLayoutList.append(1, Integer.valueOf(R.layout.shooting_base_sid_sublcd_photo));
        this.mMainLayoutList.append(2, Integer.valueOf(R.layout.shooting_base_sid_sublcd_movie));
        this.mMainLayoutList.append(8, Integer.valueOf(R.layout.shooting_base_sid_sublcd_loop));
        this.mMainLayoutList.append(4, Integer.valueOf(R.layout.shooting_base_sid_sublcd_interval));
    }

    @Override // com.sony.imaging.app.base.shooting.layout.IStableLayout
    public void setUserChanging(int whatUserChanging) {
    }

    @Override // com.sony.imaging.app.base.shooting.layout.IStableLayout
    public void setEETappedListener(StableLayout.IEETouchedListener listener) {
    }

    @Override // com.sony.imaging.app.base.common.IModableLayout
    public void onLayoutModeChanged(int device, int displayMode) {
        updateView();
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        createView();
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        destroyView();
        super.onDestroyView();
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        if (this.mCurrentRecMode != -1 && this.mCurrentRecMode != ExecutorCreator.getInstance().getRecordingMode()) {
            updateView();
            return;
        }
        int startViewID = getStartViewID(isStartFromTop());
        if (this.mCurrent14SegLayout != null) {
            this.mCurrent14SegLayout.init();
            if (startViewID != -1) {
                this.mCurrent14SegLayout.setStartPosition(startViewID);
            }
            this.mCurrent14SegLayout.start();
        }
        this.mListener = getListener();
        if (this.mListener != null) {
            CameraNotificationManager.getInstance().setNotificationListener(this.mListener);
        }
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (this.mListener != null) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mListener);
            this.mListener = null;
        }
        if (this.mCurrent14SegLayout != null) {
            this.mCurrent14SegLayout.stop();
        }
    }

    private void createView() {
        int layout;
        this.mCurrentRecMode = ExecutorCreator.getInstance().getRecordingMode();
        switch (this.mCurrentRecMode) {
            case 1:
            case 2:
            case 4:
            case 8:
                layout = this.mMainLayoutList.get(this.mCurrentRecMode).intValue();
                break;
            case 3:
            case 5:
            case 6:
            case 7:
            default:
                layout = -1;
                this.mCurrentRecMode = -1;
                break;
        }
        this.mCurrentView = obtainViewFromPool(layout);
        if (this.mCurrentView != null) {
            this.mCurrent14SegLayout = (AutoSwitchLayout) this.mCurrentView.findViewById(R.id.segment_autoswitch);
        }
    }

    private void destroyView() {
        this.mCurrentView = null;
        this.mCurrent14SegLayout = null;
        this.mCurrentRecMode = -1;
    }

    private int getStartViewID(boolean flag) {
        if (!flag) {
            int ret = R.id.segment_stable_start;
            return ret;
        }
        int ret2 = R.id.segment_top;
        return ret2;
    }

    private boolean isStartFromTop() {
        Boolean isStartFromTop = (Boolean) ((AppRoot) getActivity()).getData(KEY_START_FROM_TOP);
        ((AppRoot) getActivity()).removeData(KEY_START_FROM_TOP);
        if (isStartFromTop == null) {
            return false;
        }
        return isStartFromTop.booleanValue();
    }
}
