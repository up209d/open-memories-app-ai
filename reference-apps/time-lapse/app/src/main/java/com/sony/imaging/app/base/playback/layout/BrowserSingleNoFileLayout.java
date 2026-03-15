package com.sony.imaging.app.base.playback.layout;

import android.util.SparseIntArray;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.IModableLayout;
import com.sony.imaging.app.base.common.OnLayoutModeChangeListener;

/* loaded from: classes.dex */
public class BrowserSingleNoFileLayout extends PlayLayoutBase implements IModableLayout {
    private static final int DISP_MODE_NUM = 3;
    private static SparseIntArray mLayoutListForFinder;
    private static SparseIntArray mLayoutListForHdmi;
    private static SparseIntArray mLayoutListForPanel = new SparseIntArray(3);
    private OnLayoutModeChangeListener mListener = new OnLayoutModeChangeListener(this, 1);

    static {
        mLayoutListForPanel.put(1, R.layout.pb_layout_cmn_browser_noimage);
        mLayoutListForPanel.put(5, R.layout.pb_layout_cmn_browser_noimage);
        mLayoutListForPanel.put(3, R.layout.pb_layout_browser_singlepb_noimage_noinfo_panel);
        mLayoutListForFinder = new SparseIntArray(3);
        mLayoutListForFinder.put(1, R.layout.pb_layout_cmn_browser_noimage);
        mLayoutListForFinder.put(5, R.layout.pb_layout_cmn_browser_noimage);
        mLayoutListForFinder.put(3, R.layout.pb_layout_browser_singlepb_noimage_noinfo_evf);
        mLayoutListForHdmi = new SparseIntArray(3);
        mLayoutListForHdmi.put(1, R.layout.pb_layout_cmn_browser_noimage);
        mLayoutListForHdmi.put(5, R.layout.pb_layout_cmn_browser_noimage);
        mLayoutListForHdmi.put(3, R.layout.pb_layout_browser_singlepb_noimage_noinfo_panel);
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getLayoutResource() {
        int device = DisplayModeObserver.getInstance().getActiveDevice();
        int displayMode = DisplayModeObserver.getInstance().getActiveDispMode(1);
        if (device == 0) {
            int id = mLayoutListForPanel.get(displayMode);
            return id;
        }
        if (device == 1) {
            int id2 = mLayoutListForFinder.get(displayMode);
            return id2;
        }
        if (device != 2) {
            return -1;
        }
        int id3 = mLayoutListForHdmi.get(displayMode);
        return id3;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        DisplayModeObserver.getInstance().setNotificationListener(this.mListener);
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        DisplayModeObserver.getInstance().removeNotificationListener(this.mListener);
    }

    @Override // com.sony.imaging.app.base.common.IModableLayout
    public void onLayoutModeChanged(int device, int displayMode) {
        updateView();
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    public int getResumeKeyBeepPattern() {
        return 14;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPbZoomFuncMinus() {
        return transitionIndexPb() ? 1 : -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_FUNCTION);
        return -1;
    }
}
