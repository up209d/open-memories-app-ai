package com.sony.imaging.app.startrails.playback.layout;

import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.contents.DeleteService;
import com.sony.imaging.app.base.playback.contents.EditService;
import com.sony.imaging.app.base.playback.layout.SelectorSingleLayout;
import com.sony.imaging.app.startrails.R;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;

/* loaded from: classes.dex */
public class DeleteMultipleSingleLayout extends SelectorSingleLayout {
    private static final int DISP_MODE_NUM = 3;
    private static final int[] IMAGE_ERR_LAYOUT_RESOURCE_ID;
    private static final int[] IMAGE_OK_LAYOUT_RESOURCE_ID_HISTGRAM;
    private static final int[] IMAGE_OK_LAYOUT_RESOURCE_ID_INFO_OFF;
    private static final int[] IMAGE_OK_LAYOUT_RESOURCE_ID_INFO_ON;
    private static final SparseIntArray LAYOUT_LIST_FINDER;
    private static final SparseIntArray LAYOUT_LIST_HDMI;
    private static final SparseIntArray LAYOUT_LIST_PANEL = new SparseIntArray(3);

    @Override // com.sony.imaging.app.base.playback.layout.SelectorSingleLayout
    protected EditService getEditService() {
        return DeleteService.getInstance();
    }

    static {
        LAYOUT_LIST_PANEL.put(1, R.layout.pb_layout_deletor_multiple_selecting_singlepb_info);
        LAYOUT_LIST_PANEL.put(5, R.layout.pb_layout_deletor_multiple_selecting_singlepb_histo);
        LAYOUT_LIST_PANEL.put(3, R.layout.pb_layout_deletor_multiple_selecting_singlepb_noinfo);
        LAYOUT_LIST_FINDER = new SparseIntArray(3);
        LAYOUT_LIST_FINDER.put(1, R.layout.pb_layout_deletor_multiple_selecting_singlepb_info);
        LAYOUT_LIST_FINDER.put(5, R.layout.pb_layout_deletor_multiple_selecting_singlepb_histo);
        LAYOUT_LIST_FINDER.put(3, R.layout.pb_layout_deletor_multiple_selecting_singlepb_noinfo);
        LAYOUT_LIST_HDMI = new SparseIntArray(3);
        LAYOUT_LIST_HDMI.put(1, R.layout.pb_layout_deletor_multiple_selecting_singlepb_info);
        LAYOUT_LIST_HDMI.put(5, R.layout.pb_layout_deletor_multiple_selecting_singlepb_histo);
        LAYOUT_LIST_HDMI.put(3, R.layout.pb_layout_deletor_multiple_selecting_singlepb_noinfo);
        IMAGE_OK_LAYOUT_RESOURCE_ID_INFO_ON = new int[]{R.id.pb_parts_cmn_singlepb_metadata, R.id.pb_parts_cmn_header_contents_info};
        IMAGE_OK_LAYOUT_RESOURCE_ID_INFO_OFF = new int[0];
        IMAGE_OK_LAYOUT_RESOURCE_ID_HISTGRAM = new int[]{R.id.pb_parts_cmn_singlepb_histo_metadata, R.id.pb_parts_cmn_singlepb_histo, R.id.pb_parts_cmn_header_contents_info};
        IMAGE_ERR_LAYOUT_RESOURCE_ID = new int[]{R.id.pb_parts_cmn_singlepb_image_err};
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getLayoutResource() {
        int device = DisplayModeObserver.getInstance().getActiveDevice();
        int displayMode = DisplayModeObserver.getInstance().getActiveDispMode(1);
        if (device == 0) {
            int id = LAYOUT_LIST_PANEL.get(displayMode);
            return id;
        }
        if (device == 1) {
            int id2 = LAYOUT_LIST_FINDER.get(displayMode);
            return id2;
        }
        if (device != 2) {
            return -1;
        }
        int id3 = LAYOUT_LIST_HDMI.get(displayMode);
        return id3;
    }

    @Override // com.sony.imaging.app.base.playback.layout.SelectorSingleLayout
    protected int getCheckBoxResourceId() {
        return R.id.checkBox;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getFooterGuideResource() {
        return R.id.footer_guide;
    }

    @Override // com.sony.imaging.app.base.playback.layout.SingleLayoutBase, com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        setFooterGuideData(new FooterGuideDataResId(getActivity().getApplicationContext(), android.R.string.whichViewApplicationNamed, android.R.string.httpErrorFile));
        super.onResume();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.layout.SelectorSingleLayout, com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    public void updateDisplay() {
        super.updateDisplay();
        TextView selectedCount = (TextView) getView().findViewById(R.id.countSelections);
        if (selectedCount != null) {
            String text = getResources().getString(android.R.string.whichViewApplication, Integer.valueOf(getEditService().countSelected()));
            selectedCount.setText(text);
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.SelectorSingleLayout
    public int selectContent() {
        int result = super.selectContent();
        if (result == -2) {
            CautionUtilityClass.getInstance().requestTrigger(329);
        } else if (result == -3) {
            CautionUtilityClass.getInstance().requestTrigger(1740);
        }
        invalidate();
        return result;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    public int getResumeKeyBeepPattern() {
        return 10;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        return selectContent() == 0 ? 1 : -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        if (this.mSelectorTrigger != null) {
            this.mSelectorTrigger.onReturn();
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        EditService service = getEditService();
        if (service.countSelected() > 0) {
            if (this.mSelectorTrigger != null) {
                this.mSelectorTrigger.onOk();
            }
            return 1;
        }
        CautionUtilityClass.getInstance().requestTrigger(1403);
        return -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteFuncKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.playback.widget.TouchArea.OnTouchAreaListener
    public boolean onTouchUp(MotionEvent e, boolean isReleasedInside) {
        String id;
        if (isReleasedInside) {
            ContentsManager mgr = ContentsManager.getInstance();
            EditService service = getEditService();
            int file = mgr.getContentsPosition();
            if (service.isChecked(file)) {
                id = BeepUtilityRsrcTable.BEEP_ID_OFF;
            } else {
                id = BeepUtilityRsrcTable.BEEP_ID_ON;
            }
            if (selectContent() == 0) {
                BeepUtility.getInstance().playBeep(id);
                return true;
            }
            return true;
        }
        return true;
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    protected int[] getImageOkLayoutResourceId() {
        int displayMode = DisplayModeObserver.getInstance().getActiveDispMode(1);
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

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    protected int[] getImageErrLayoutResourceId() {
        return IMAGE_ERR_LAYOUT_RESOURCE_ID;
    }
}
