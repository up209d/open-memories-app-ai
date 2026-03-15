package com.sony.imaging.app.base.playback.layout;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase;
import com.sony.imaging.app.base.playback.loader.Task;
import com.sony.imaging.app.base.playback.widget.Icon3D;
import com.sony.imaging.app.base.playback.widget.IconAutoFraming;
import com.sony.imaging.app.base.playback.widget.IndexGrid;
import com.sony.imaging.app.base.playback.widget.IndexGridAdapter;
import com.sony.imaging.app.util.BeepUtility;

/* loaded from: classes.dex */
public class BrowserIndexLayout extends IndexLayoutGridViewBase {
    private static final String MSG_ON_ITEM_SELECTED = "onItemSelected : ";
    private static final int[] GRID_CELL_LAYOUT_RESOURCE_ID = {R.id.pb_parts_cmn_header_contents_info, R.id.pb_parts_cmn_layout_play_dayinfo_bottom_nocard};
    private static final int[] GROUP_BAR_LAYOUT_RESOURCE_ID = {R.id.pb_parts_cmn_indexpb_groupbar_header, R.id.footer_guide};
    private static final int[] UNSELECTED_LAYOUT_RESOURCE_ID = {R.id.pb_parts_cmn_indexpb_groupbar_header};

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setFooterGuideData(new FooterGuideDataResId(getActivity().getApplicationContext(), android.R.string.httpError));
        return view;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getLayoutResource() {
        int aspect = DisplayModeObserver.getInstance().getOsdAspect(0);
        return 2 == aspect ? R.layout.pb_layout_browser_indexpb_image_4x3 : R.layout.pb_layout_browser_indexpb_image_3x3;
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase
    protected int getScalableLayoutId() {
        int aspect = DisplayModeObserver.getInstance().getOsdAspect(0);
        if (2 == aspect) {
            return R.id.indexGridBase;
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getFooterGuideResource() {
        return R.id.footer_guide;
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase
    protected int getGridResourceId() {
        return R.id.indexGrid;
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase
    protected int getScrollBarResourceId() {
        return R.id.indexScrollBar;
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
        return UNSELECTED_LAYOUT_RESOURCE_ID;
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Log.d(this.TAG, LogHelper.getScratchBuilder(MSG_ON_ITEM_SELECTED).append(arg2).append(", ").append(arg3).toString());
        if (arg2 != -2 && onContentEntered(arg2)) {
            BeepUtility.getInstance().playBeep("BEEP_ID_TAP");
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase
    protected boolean onContentEntered(int position) {
        return transitionSinglePb();
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    public int getResumeKeyBeepPattern() {
        return 17;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteFuncKey() {
        return (getHighlightedItemKind() == 1 && transitionDeleteThis()) ? 1 : -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_FUNCTION);
        return -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPbZoomFuncPlus() {
        IndexGrid grid = this.mIndexGrid;
        if (grid != null && !grid.isGroupBarSelected()) {
            thinZoomKey();
            transitionSinglePb();
        }
        return 0 != 0 ? 1 : -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase
    protected IndexGridAdapter getGridAdapter() {
        return new ImageAdapter();
    }

    /* loaded from: classes.dex */
    protected class ImageAdapter extends IndexLayoutGridViewBase.ImageAdapter {

        /* loaded from: classes.dex */
        protected class ViewHolder extends IndexLayoutGridViewBase.ImageAdapter.ViewHolder {
            protected ViewHolder(int id, View view) {
                super(id, view);
            }

            @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase.ImageAdapter.ViewHolder
            protected void findViews(View view) {
                this.mImageview = (ImageView) view.findViewById(R.id.gridThumbnail);
                this.m3dIcon = (Icon3D) view.findViewById(R.id.icon3d);
                this.mAutoFramingIcon = (IconAutoFraming) view.findViewById(R.id.iconautoframing);
            }
        }

        public ImageAdapter() {
            super();
        }

        @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase.ImageAdapter
        protected int getCellLayout() {
            return R.layout.pb_parts_cmn_indexpb_cell;
        }

        @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase.ImageAdapter
        protected IndexLayoutGridViewBase.ImageAdapter.ViewHolder getViewHolder(int position, View view) {
            return new ViewHolder(position, view);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase.ImageAdapter
        public void loadImageDone(IndexLayoutGridViewBase.ImageAdapter.ViewHolder holder, Bitmap result, Task task) {
            super.loadImageDone(holder, result, task);
        }
    }
}
