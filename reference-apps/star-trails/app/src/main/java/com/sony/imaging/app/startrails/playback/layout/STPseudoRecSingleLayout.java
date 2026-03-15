package com.sony.imaging.app.startrails.playback.layout;

import android.util.SparseIntArray;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.layout.PseudoRecSingleLayout;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.startrails.R;
import com.sony.imaging.app.startrails.common.STDisplayModeObserver;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.widget.OptimizedImageView;

/* loaded from: classes.dex */
public class STPseudoRecSingleLayout extends PseudoRecSingleLayout {
    private static final int DISP_MODE_NUM = 3;
    private static final int[] IMAGE_OK_LAYOUT_RESOURCE_ID_HISTGRAM;
    private static final int[] IMAGE_OK_LAYOUT_RESOURCE_ID_INFO_OFF;
    private static final int[] IMAGE_OK_LAYOUT_RESOURCE_ID_INFO_ON;
    private static SparseIntArray mLayoutListForFinder;
    private static SparseIntArray mLayoutListForHdmi;
    private static SparseIntArray mLayoutListForPanel;

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    protected void updateVPicDisplay(OptimizedImageView img, ContentsManager mgr) {
        img.setDisplayRotationAngle(0);
    }

    static {
        int i = R.layout.pb_layout_cmn_singlepb_image_histo_legacy_emnt;
        mLayoutListForPanel = new SparseIntArray(3);
        mLayoutListForPanel.put(1, R.layout.st_pb_layout_cmn_singlepb_image_info);
        mLayoutListForPanel.put(5, 1 == Environment.getVersionOfHW() ? R.layout.pb_layout_cmn_singlepb_image_histo_legacy_emnt : R.layout.pb_layout_cmn_singlepb_image_histo);
        mLayoutListForPanel.put(3, R.layout.pb_layout_cmn_singlepb_image_noinfo);
        mLayoutListForFinder = new SparseIntArray(3);
        mLayoutListForFinder.put(1, R.layout.st_pb_layout_cmn_singlepb_image_info);
        mLayoutListForFinder.put(5, 1 == Environment.getVersionOfHW() ? R.layout.pb_layout_cmn_singlepb_image_histo_legacy_emnt : R.layout.pb_layout_cmn_singlepb_image_histo);
        mLayoutListForFinder.put(3, R.layout.pb_layout_cmn_singlepb_image_noinfo);
        mLayoutListForHdmi = new SparseIntArray(3);
        mLayoutListForHdmi.put(1, R.layout.st_pb_layout_cmn_singlepb_image_info);
        SparseIntArray sparseIntArray = mLayoutListForHdmi;
        if (1 != Environment.getVersionOfHW()) {
            i = R.layout.pb_layout_cmn_singlepb_image_histo;
        }
        sparseIntArray.put(5, i);
        mLayoutListForHdmi.put(3, R.layout.pb_layout_cmn_singlepb_image_noinfo);
        IMAGE_OK_LAYOUT_RESOURCE_ID_INFO_ON = new int[]{R.id.pb_parts_cmn_singlepb_metadata, R.id.pb_parts_cmn_header_contents_info, R.id.pb_parts_cmn_bottom_date};
        IMAGE_OK_LAYOUT_RESOURCE_ID_INFO_OFF = new int[0];
        IMAGE_OK_LAYOUT_RESOURCE_ID_HISTGRAM = new int[]{R.id.pb_parts_cmn_singlepb_histo_metadata, R.id.pb_parts_cmn_singlepb_histo, R.id.pb_parts_cmn_header_contents_info, R.id.pb_parts_cmn_bottom_date};
    }

    @Override // com.sony.imaging.app.base.playback.layout.PseudoRecSingleLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getLayoutResource() {
        int device = STDisplayModeObserver.getInstance().getActiveDevice();
        int displayMode = STDisplayModeObserver.getInstance().getActiveDispMode(1);
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

    @Override // com.sony.imaging.app.base.playback.layout.SingleLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.SingleLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PseudoRecSingleLayout, com.sony.imaging.app.base.playback.layout.SingleLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPbZoomFuncMinus() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PseudoRecSingleLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteFuncKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PseudoRecSingleLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        transitionShooting(new EventParcel(AppRoot.USER_KEYCODE.SK1));
        return -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PseudoRecSingleLayout, com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    public boolean previousContents() {
        pushedPbZoomFuncPlus();
        return false;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PseudoRecSingleLayout, com.sony.imaging.app.base.playback.layout.SingleLayoutBase, com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        ApoWrapper.setApoType(ApoWrapper.APO_TYPE.NONE);
    }

    @Override // com.sony.imaging.app.base.playback.layout.PseudoRecSingleLayout, com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    public boolean nextContents() {
        pushedPbZoomFuncPlus();
        return false;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PseudoRecSingleLayout
    protected void displayCaution() {
        ContentsManager mgr = ContentsManager.getInstance();
        if (!mgr.isVirtualEmpty()) {
            CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_FUNCTION);
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.PseudoRecSingleLayout, com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    protected int[] getImageOkLayoutResourceId() {
        int displayMode = STDisplayModeObserver.getInstance().getActiveDispMode(1);
        switch (displayMode) {
            case 1:
                return IMAGE_OK_LAYOUT_RESOURCE_ID_INFO_ON;
            case 2:
            case 4:
            default:
                return null;
            case 3:
                return IMAGE_OK_LAYOUT_RESOURCE_ID_INFO_OFF;
            case 5:
                return IMAGE_OK_LAYOUT_RESOURCE_ID_HISTGRAM;
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.SingleLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDispFuncKey() {
        STDisplayModeObserver.getInstance().toggleDisplayMode(1);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        transitionShooting(new EventParcel(AppRoot.USER_KEYCODE.PLAYBACK));
        return -1;
    }
}
