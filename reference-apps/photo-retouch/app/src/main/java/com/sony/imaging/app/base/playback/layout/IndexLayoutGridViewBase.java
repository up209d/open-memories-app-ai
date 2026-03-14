package com.sony.imaging.app.base.playback.layout;

import android.R;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import com.sony.imaging.app.base.backup.BackupReader;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.IModableLayout;
import com.sony.imaging.app.base.common.OnLayoutModeChangeListener;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.playback.contents.ContentInfo;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.loader.ContentInfoLoader;
import com.sony.imaging.app.base.playback.loader.ContentInfoTask;
import com.sony.imaging.app.base.playback.loader.Loader;
import com.sony.imaging.app.base.playback.loader.Task;
import com.sony.imaging.app.base.playback.loader.ThumbnailLoader;
import com.sony.imaging.app.base.playback.loader.ThumbnailTask;
import com.sony.imaging.app.base.playback.widget.Icon3D;
import com.sony.imaging.app.base.playback.widget.IconAutoFraming;
import com.sony.imaging.app.base.playback.widget.IndexGrid;
import com.sony.imaging.app.base.playback.widget.IndexGridAdapter;
import com.sony.imaging.app.base.playback.widget.IndexScrollBar;
import com.sony.imaging.app.base.playback.widget.ScalableLayout;
import com.sony.imaging.app.util.PTag;
import com.sony.scalar.sysutil.didep.Gpelibrary;

/* loaded from: classes.dex */
public abstract class IndexLayoutGridViewBase extends IndexLayoutBase implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener, IModableLayout {
    private static final String MSG_END_DRAW_THUMBNAIL = "draw thumbnail on indexPb";
    private static final String MSG_END_GET_VIEW = "getView end";
    private static final String MSG_END_LOAD_IMAGE = "loadImageDone end";
    private static final String MSG_END_LOAD_INFO = "loadInfoDone end";
    private static final String MSG_INFO_FROM_CACHE = "content info cached ";
    private static final String MSG_ON_LAYOUT_MODE_CHANGED = "onLayoutModeChanged : ";
    private static final String MSG_ON_MOVED_TO_SCRAP = "onMovedToScrapHeap : ";
    private static final String MSG_START_GET_VIEW = "getView start ";
    private static final String MSG_START_LOAD_IMAGE = "loadImageDone start";
    private static final String MSG_START_LOAD_INFO = "loadInfoDone start";
    protected static final float VSCALE_EVF_FOR_WIDE_MAIN_PANEL = 0.75f;
    protected static final float VSCALE_NONE = 1.0f;
    private static Rect mScratchRect = new Rect();
    protected IndexGrid mIndexGrid;
    private final OnLayoutModeChangeListener mListener = new OnLayoutModeChangeListener(this, 1);
    protected ScalableLayout mScalableLayout;

    protected abstract int getGridResourceId();

    protected abstract int getScrollBarResourceId();

    protected int getScalableLayoutId() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    public int getResumeKeyBeepPattern() {
        return 8;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        this.mIndexGrid = (IndexGrid) view.findViewById(getGridResourceId());
        this.mScalableLayout = (ScalableLayout) view.findViewById(getScalableLayoutId());
        return view;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        this.mIndexGrid = null;
        this.mScalableLayout = null;
        super.onDestroyView();
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        IndexGrid grid = this.mIndexGrid;
        if (grid != null) {
            grid.setAdapter(getGridAdapter());
            grid.setOnItemClickListener(this);
            grid.setOnItemSelectedListener(this);
            grid.setRecyclerListener((ImageAdapter) grid.getAdapter());
            IndexScrollBar scrollBar = (IndexScrollBar) getView().findViewById(getScrollBarResourceId());
            if (scrollBar != null) {
                scrollBar.setScroller(grid);
            }
        }
        DisplayModeObserver.getInstance().setNotificationListener(this.mListener);
        updateScale();
        Gpelibrary.changeFrameBufferPixel(Gpelibrary.GS_FRAMEBUFFER_TYPE.ABGR8888);
        invalidate();
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        IndexGrid grid = this.mIndexGrid;
        if (grid != null) {
            ImageAdapter adapter = (ImageAdapter) grid.getAdapter();
            if (adapter != null) {
                adapter.close();
            }
            grid.setAdapter((IndexGridAdapter) null);
        }
        Gpelibrary.changeFrameBufferPixel(Gpelibrary.GS_FRAMEBUFFER_TYPE.RGBA4444);
        DisplayModeObserver.getInstance().removeNotificationListener(this.mListener);
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void onReopened() {
        invalidate();
    }

    public void invalidate() {
        IndexGrid grid = this.mIndexGrid;
        if (grid != null) {
            grid.invalidate();
            grid.invalidateViews();
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutBase
    public int getHighlightedItemKind() {
        if (this.mIndexGrid != null) {
            if (this.mIndexGrid.isGroupBarSelected()) {
                return 2;
            }
            return 1;
        }
        return 0;
    }

    protected boolean onContentEntered(int position) {
        return false;
    }

    protected boolean onGroupEntered() {
        return false;
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        highlightedItemChanged(position >= 0 ? 1 : 2);
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onNothingSelected(AdapterView<?> parent) {
        highlightedItemChanged(0);
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        boolean result = false;
        IndexGrid grid = this.mIndexGrid;
        if (grid != null) {
            if (grid.isGroupBarSelected()) {
                result = onGroupEntered();
            } else {
                result = onContentEntered(grid.getSelectedItemPosition());
            }
        }
        return result ? 1 : -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        return (this.mIndexGrid == null || !this.mIndexGrid.moveFocus(17)) ? -1 : 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        return (this.mIndexGrid == null || !this.mIndexGrid.moveFocus(66)) ? -1 : 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        return (this.mIndexGrid == null || !this.mIndexGrid.moveFocus(33)) ? -1 : 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        return (this.mIndexGrid == null || !this.mIndexGrid.moveFocus(130)) ? -1 : 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        return (this.mIndexGrid == null || !this.mIndexGrid.moveFocus(17)) ? -1 : 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        return (this.mIndexGrid == null || !this.mIndexGrid.moveFocus(66)) ? -1 : 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        return (this.mIndexGrid == null || !this.mIndexGrid.scrollPreviousPage()) ? -1 : 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        return (this.mIndexGrid == null || !this.mIndexGrid.scrollNextPage()) ? -1 : 1;
    }

    protected IndexGridAdapter getGridAdapter() {
        return new IndexGridAdapter();
    }

    @Override // com.sony.imaging.app.base.common.IModableLayout
    public void onLayoutModeChanged(int device, int displayMode) {
        Log.i(this.TAG, LogHelper.getScratchBuilder(MSG_ON_LAYOUT_MODE_CHANGED).append(device).append(LogHelper.MSG_COMMA).append(displayMode).toString());
        updateScale();
    }

    protected void updateScale() {
        if (this.mScalableLayout != null) {
            float scale = VSCALE_NONE;
            DisplayModeObserver observer = DisplayModeObserver.getInstance();
            int panelAspect = observer.getOsdAspect(0);
            if (2 == panelAspect) {
                int currentAspect = observer.getActiveDeviceOsdAspect();
                if (3 == currentAspect) {
                    scale = 0.75f;
                }
            }
            this.mScalableLayout.setScaleHeight(scale);
        }
    }

    /* loaded from: classes.dex */
    protected abstract class ImageAdapter extends IndexGridAdapter implements IndexGrid.RecyclerListener {
        private Handler mHandler;
        protected ThumbnailLoader mImageLoader;
        protected ContentInfoLoader mInfoLoader;

        protected abstract int getCellLayout();

        protected abstract ViewHolder getViewHolder(int i, View view);

        /* JADX INFO: Access modifiers changed from: protected */
        /* loaded from: classes.dex */
        public abstract class ViewHolder {
            protected Icon3D m3dIcon;
            protected IconAutoFraming mAutoFramingIcon;
            protected int mId = -1;
            protected ImageView mImageview;

            protected abstract void findViews(View view);

            public ViewHolder(int id, View view) {
                findViews(view);
                setId(id);
            }

            public void setId(int id) {
                if (this.mId != id) {
                    if (this.mImageview != null) {
                        this.mImageview.setVisibility(4);
                    }
                    if (this.m3dIcon != null) {
                        this.m3dIcon.setVisibility(4);
                    }
                    if (this.mAutoFramingIcon != null) {
                        this.mAutoFramingIcon.setVisibility(4);
                    }
                    this.mId = id;
                }
            }

            public int getId() {
                return this.mId;
            }
        }

        public ImageAdapter() {
            this.mHandler = null;
            this.mInfoLoader = null;
            this.mImageLoader = null;
            this.mHandler = new Handler();
            this.mInfoLoader = new ContentInfoLoader();
            this.mImageLoader = new ThumbnailLoader();
        }

        public void close() {
            if (this.mInfoLoader != null) {
                this.mInfoLoader.terminate();
                this.mInfoLoader = null;
            }
            if (this.mImageLoader != null) {
                this.mImageLoader.terminate();
                this.mImageLoader = null;
            }
            this.mHandler.removeCallbacksAndMessages(null);
        }

        @Override // com.sony.imaging.app.base.playback.widget.IndexGrid.RecyclerListener
        public void onMovedToScrapHeap(View view) {
            ViewHolder holder = null;
            if (view != null) {
                holder = (ViewHolder) view.getTag();
            }
            if (holder != null) {
                Log.d(IndexLayoutGridViewBase.this.TAG, LogHelper.getScratchBuilder(IndexLayoutGridViewBase.MSG_ON_MOVED_TO_SCRAP).append(holder.mId).toString());
                if (this.mInfoLoader != null) {
                    this.mInfoLoader.removeQueueItem(holder.mId);
                }
                if (this.mImageLoader != null) {
                    this.mImageLoader.removeQueueItem(holder.mId);
                }
                holder.setId(-1);
            }
        }

        @Override // com.sony.imaging.app.base.playback.widget.IndexGridAdapter, android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            Log.d(IndexLayoutGridViewBase.this.TAG, LogHelper.getScratchBuilder(IndexLayoutGridViewBase.MSG_START_GET_VIEW).append(Thread.currentThread()).toString());
            ContentsManager mgr = ContentsManager.getInstance();
            if (convertView != null) {
                holder = (ViewHolder) convertView.getTag();
                holder.setId(position);
            } else {
                LayoutInflater inflater = LayoutInflater.from(IndexLayoutGridViewBase.this.getActivity());
                convertView = inflater.inflate(getCellLayout(), parent, false);
                holder = getViewHolder(position, convertView);
                convertView.setTag(holder);
            }
            ContentInfo info = mgr.getContentInfoFromCache(mgr.getContentsIdAt(position));
            ContentInfoTask task = new ContentInfoTask(position);
            if (info != null) {
                Log.i(IndexLayoutGridViewBase.this.TAG, LogHelper.getScratchBuilder(IndexLayoutGridViewBase.MSG_INFO_FROM_CACHE).append(position).toString());
                loadInfoDone(holder, info, task);
            } else {
                InfoCb cb = new InfoCb(holder);
                if (this.mInfoLoader != null) {
                    this.mInfoLoader.apply(task, cb);
                }
            }
            Log.d(IndexLayoutGridViewBase.this.TAG, IndexLayoutGridViewBase.MSG_END_GET_VIEW);
            return convertView;
        }

        /* loaded from: classes.dex */
        private class InfoCb implements Loader.LoadedCallback {
            private ViewHolder mHolder;

            InfoCb(ViewHolder holder) {
                this.mHolder = holder;
            }

            @Override // com.sony.imaging.app.base.playback.loader.Loader.LoadedCallback
            public void run(final Object result, final Task task) {
                ImageAdapter.this.mHandler.post(new Runnable() { // from class: com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase.ImageAdapter.InfoCb.1
                    @Override // java.lang.Runnable
                    public void run() {
                        Log.d(IndexLayoutGridViewBase.this.TAG, LogHelper.getScratchBuilder(IndexLayoutGridViewBase.MSG_START_LOAD_INFO).append(Thread.currentThread()).toString());
                        ImageAdapter.this.loadInfoDone(InfoCb.this.mHolder, (ContentInfo) result, task);
                        Log.d(IndexLayoutGridViewBase.this.TAG, IndexLayoutGridViewBase.MSG_END_LOAD_INFO);
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public class ThumbCb implements Loader.LoadedCallback {
            private ViewHolder mHolder;

            ThumbCb(ViewHolder holder) {
                this.mHolder = holder;
            }

            @Override // com.sony.imaging.app.base.playback.loader.Loader.LoadedCallback
            public void run(final Object result, final Task task) {
                ImageAdapter.this.mHandler.post(new Runnable() { // from class: com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase.ImageAdapter.ThumbCb.1
                    @Override // java.lang.Runnable
                    public void run() {
                        Log.d(IndexLayoutGridViewBase.this.TAG, LogHelper.getScratchBuilder(IndexLayoutGridViewBase.MSG_START_LOAD_IMAGE).append(Thread.currentThread()).toString());
                        ImageAdapter.this.loadImageDone(ThumbCb.this.mHolder, (Bitmap) result, task);
                        Log.d(IndexLayoutGridViewBase.this.TAG, IndexLayoutGridViewBase.MSG_END_LOAD_IMAGE);
                    }
                });
            }
        }

        protected void loadInfoDone(ViewHolder holder, ContentInfo result, Task task) {
            int position;
            if (result != null && (position = task.getPos()) == holder.mId) {
                ContentsManager.ThumbnailOption option = new ContentsManager.ThumbnailOption();
                option.rotateIt = BackupReader.getVPicDisplay() == BackupReader.VPicDisplay.PORTRAIT;
                option.clipByAspect = true;
                int aspect = DisplayModeObserver.getInstance().getOsdAspect(0);
                option.postScale = 2 == aspect ? ContentsManager.SCALE_WIDE_PANEL_PIXEL : ContentsManager.SCALE_SQUARE_PIXEL;
                ThumbnailTask image = new ThumbnailTask(position, option);
                ThumbCb cb = new ThumbCb(holder);
                if (this.mImageLoader != null) {
                    this.mImageLoader.apply(image, cb);
                }
                Icon3D icon3d = holder.m3dIcon;
                if (icon3d != null) {
                    icon3d.setContentInfo(result);
                }
                IconAutoFraming iconAutoFraming = holder.mAutoFramingIcon;
                if (iconAutoFraming != null) {
                    iconAutoFraming.setContentInfo(result);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void loadImageDone(ViewHolder holder, Bitmap result, Task task) {
            int position = task.getPos();
            if (position == holder.mId) {
                ImageView imageView = holder.mImageview;
                if (result == null) {
                    imageView.setImageResource(R.drawable.jog_tab_bar_right_decline);
                } else {
                    imageView.setImageBitmap(result);
                }
                imageView.setVisibility(0);
                if (IndexLayoutGridViewBase.this.mIndexGrid != null && IndexLayoutGridViewBase.this.mScalableLayout != null) {
                    imageView.getDrawingRect(IndexLayoutGridViewBase.mScratchRect);
                    if (IndexLayoutGridViewBase.this.mScalableLayout.getScaledRect(IndexLayoutGridViewBase.mScratchRect)) {
                        IndexLayoutGridViewBase.this.mIndexGrid.invalidate(IndexLayoutGridViewBase.mScratchRect.left, IndexLayoutGridViewBase.mScratchRect.top, IndexLayoutGridViewBase.mScratchRect.right, IndexLayoutGridViewBase.mScratchRect.bottom);
                    }
                }
            }
            if (this.mInfoLoader != null && this.mInfoLoader.isEmpty() && this.mImageLoader != null && this.mImageLoader.isEmpty()) {
                PTag.end(IndexLayoutGridViewBase.MSG_END_DRAW_THUMBNAIL);
            }
        }
    }
}
