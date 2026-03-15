package com.sony.imaging.app.graduatedfilter.playback.layout;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.playback.contents.ContentInfo;
import com.sony.imaging.app.base.playback.contents.DeleteService;
import com.sony.imaging.app.base.playback.contents.EditService;
import com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase;
import com.sony.imaging.app.base.playback.layout.SelectorIndexLayout;
import com.sony.imaging.app.base.playback.loader.Task;
import com.sony.imaging.app.base.playback.widget.Icon3D;
import com.sony.imaging.app.base.playback.widget.IconAutoFraming;
import com.sony.imaging.app.base.playback.widget.IndexGridAdapter;
import com.sony.imaging.app.base.playback.widget.Protect;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.graduatedfilter.R;
import com.sony.scalar.hardware.avio.DisplayManager;

/* loaded from: classes.dex */
public class DeleteMultipleIndexLayout extends SelectorIndexLayout {
    private static final int[] GRID_CELL_LAYOUT_RESOURCE_ID = {R.id.pb_parts_cmn_header_contents_info};
    private static final int[] GROUP_BAR_LAYOUT_RESOURCE_ID = {R.id.pb_parts_cmn_indexpb_groupbar_header};

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver
    protected String getKeyConvCategory() {
        return ICustomKey.CATEGORY_INDEX;
    }

    @Override // com.sony.imaging.app.base.playback.layout.SelectorIndexLayout
    protected EditService getEditService() {
        return DeleteService.getInstance();
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getLayoutResource() {
        int aspect = DisplayModeObserver.getInstance().getOsdAspect(0);
        return 2 == aspect ? R.layout.pb_layout_deletor_multiple_selecting_indexpb_4x3 : R.layout.pb_layout_deletor_multiple_selecting_indexpb_3x3;
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase
    protected int getScalableLayoutId() {
        DisplayManager.DeviceInfo info = DisplayModeObserver.getInstance().getDeviceInfo(0);
        if (2 == info.aspect) {
            return R.id.indexGridBase;
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getFooterGuideResource() {
        return R.id.footer_guide;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutBase
    public void highlightedItemChanged(int itemKind) {
        super.highlightedItemChanged(itemKind);
        if (itemKind == 1) {
            setFooterGuideData(new FooterGuideDataResId(getActivity().getApplicationContext(), android.R.string.carrier_app_notification_title, android.R.string.httpErrorFile));
        } else {
            setFooterGuideData(new FooterGuideDataResId(getActivity().getApplicationContext(), android.R.string.httpError));
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase
    protected int getGridResourceId() {
        return R.id.indexGrid;
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase
    protected int getScrollBarResourceId() {
        return R.id.indexScrollBar;
    }

    @Override // com.sony.imaging.app.base.playback.layout.SelectorIndexLayout
    protected int getGroupCheckBoxResourceId() {
        return R.id.groupCheckBox;
    }

    @Override // com.sony.imaging.app.base.playback.layout.SelectorIndexLayout, com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase
    public void invalidate() {
        super.invalidate();
        TextView selectedCount = (TextView) getView().findViewById(R.id.countSelections);
        if (selectedCount != null) {
            String text = getResources().getString(android.R.string.whichViewApplication, Integer.valueOf(getEditService().countSelected()));
            selectedCount.setText(text);
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutBase
    protected int[] getGridCellLayoutResourceId() {
        return GRID_CELL_LAYOUT_RESOURCE_ID;
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutBase
    protected int[] getGroupBarLayoutResourceId() {
        return GROUP_BAR_LAYOUT_RESOURCE_ID;
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutBase
    protected int[] getNothingSelectedLayoutResourceId() {
        return GROUP_BAR_LAYOUT_RESOURCE_ID;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.layout.SelectorIndexLayout
    public boolean onContentEntered(int position, int[] result) {
        boolean bResult = super.onContentEntered(position, result);
        if (result[0] == -2) {
            CautionUtilityClass.getInstance().requestTrigger(329);
        }
        if (result[0] == -3) {
            CautionUtilityClass.getInstance().requestTrigger(1740);
        }
        return bResult;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.layout.SelectorIndexLayout
    public boolean onGroupEntered(int[] result) {
        boolean bResult = super.onGroupEntered(result);
        if (result[0] == -2) {
            CautionUtilityClass.getInstance().requestTrigger(329);
        }
        return bResult;
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    public int getResumeKeyBeepPattern() {
        return 11;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        transitionSinglePb();
        return -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.SelectorIndexLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPbZoomFuncPlus() {
        transitionSinglePb();
        return -1;
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
        if (this.mSelectorTrigger != null) {
            this.mSelectorTrigger.onReturn();
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteFuncKey() {
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

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase
    protected IndexGridAdapter getGridAdapter() {
        return new ImageAdapter();
    }

    /* loaded from: classes.dex */
    protected class ImageAdapter extends IndexLayoutGridViewBase.ImageAdapter {
        protected ImageAdapter() {
            super();
        }

        /* loaded from: classes.dex */
        class ViewHolder extends IndexLayoutGridViewBase.ImageAdapter.ViewHolder {
            CheckBox checkbox;
            Protect protect;

            public ViewHolder(int id, View view) {
                super(id, view);
            }

            @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase.ImageAdapter.ViewHolder
            public void setId(int id) {
                if (getId() != id) {
                    if (this.checkbox != null) {
                        this.checkbox.setVisibility(4);
                    }
                    if (this.protect != null) {
                        this.protect.setVisibility(4);
                    }
                }
                super.setId(id);
            }

            @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase.ImageAdapter.ViewHolder
            protected void findViews(View view) {
                this.mImageview = (ImageView) view.findViewById(R.id.gridThumbnail);
                this.checkbox = (CheckBox) view.findViewById(R.id.checkBox);
                this.protect = (Protect) view.findViewById(R.id.protect);
                this.m3dIcon = (Icon3D) view.findViewById(R.id.icon3d);
                this.mAutoFramingIcon = (IconAutoFraming) view.findViewById(R.id.iconautoframing);
            }
        }

        @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase.ImageAdapter
        protected int getCellLayout() {
            return R.layout.pb_parts_cmn_indexpb_cell_deletion;
        }

        @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase.ImageAdapter
        protected IndexLayoutGridViewBase.ImageAdapter.ViewHolder getViewHolder(int position, View view) {
            return new ViewHolder(position, view);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase.ImageAdapter
        public void loadInfoDone(IndexLayoutGridViewBase.ImageAdapter.ViewHolder holder, ContentInfo result, Task task) {
            super.loadInfoDone(holder, result, task);
            if (result != null) {
                ViewHolder theHolder = (ViewHolder) holder;
                int position = task.getPos();
                if (position == theHolder.getId()) {
                    CheckBox checkbox = theHolder.checkbox;
                    if (DeleteMultipleIndexLayout.this.getEditService().isSelectable(position)) {
                        checkbox.setVisibility(0);
                        checkbox.setChecked(DeleteMultipleIndexLayout.this.getEditService().isChecked(position));
                    } else {
                        checkbox.setVisibility(4);
                    }
                    Protect protect = theHolder.protect;
                    if (protect != null) {
                        protect.setContentInfo(result);
                    }
                }
            }
        }
    }
}
