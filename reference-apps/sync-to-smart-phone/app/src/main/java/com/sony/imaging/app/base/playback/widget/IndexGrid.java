package com.sony.imaging.app.base.playback.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.OverScroller;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.playback.widget.FolderBar;
import com.sony.imaging.app.base.playback.widget.IndexScrollBar;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.PTag;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class IndexGrid extends AdapterView<IndexGridAdapter> implements GestureDetector.OnGestureListener, IndexScrollBar.IndexScroller, FolderBar.OnTouchListener {
    private static final int DEFAULT_COLUMN_COUNT = 3;
    private static final int DEFAULT_ROW_COUNT = 3;
    private static final boolean MEASURE_TIME = false;
    private static final String METHOD_NAME_SET_FOCUSABLE = "setFocusable";
    private static final String MSG_CHILD_VIEW_MEASURE_CLOSE = "] measure ";
    private static final String MSG_CHILD_VIEW_MEASURE_OPEN = "child[";
    private static final String MSG_COMMON_KEY = "commonKey ";
    private static final String MSG_DETACHED_ON_SCROLL = "detached onScrolling";
    private static final String MSG_END_SCROLL_THUMB = "end scroll thumb";
    private static final String MSG_FLING_THREAD = "Fling Thread";
    private static final String MSG_GROUPBAR = "mGroupBar (";
    private static final String MSG_LAYOUT_CHILDREN = "layoutChildren : ";
    private static final String MSG_NO_CONTENTS = "No Contents!!";
    private static final String MSG_ON_DOWN = "onDown ";
    private static final String MSG_ON_FLING = "onFling ";
    private static final String MSG_ON_SCROLL = "onScroll ";
    private static final String MSG_ON_SHOW_PRESS = "onShowPress ";
    private static final String MSG_ON_SINGLE_TAP = "onSingleTapUp ";
    private static final String MSG_ON_TOUCH_EVENT = "onTouchEvent ";
    private static final String MSG_SELECTOR_BOUNDS = "selector (";
    private static final String MSG_SET_SELECTION = "setSelection : ";
    private static final String MSG_SET_SELECTION_POSITION_CHANGED = "setSelection : positionChanged";
    private static final String MSG_START_SCROLL = "scroll page by touch gesture";
    private static final int PAGE_SCROLL_DURATION = 200;
    public static final int POSITION_GROUP_BAR = -2;
    private static final String TAG = "IndexGrid";
    protected IndexGridAdapter mAdapter;
    private int mCacheColorHint;
    private int mColumnCount;
    private int mColumnGap;
    private FlingRunnable mFlingRunnable;
    protected boolean mForceLayoutChildren;
    private GestureDetector mGestureDetector;
    private int mGpBarCenterOffsetY;
    private int mGpBarHeight;
    private int mGpBarOffsetX;
    private int mGpBarOffsetY;
    private int mGpBarWidth;
    private int mGridHeight;
    private int mGridOffsetX;
    private int mGridOffsetY;
    private int mGridWidth;
    private FolderBar mGroupBar;
    private int mLineGap;
    private int mOldItemSelectedPosition;
    final RecycleBin mRecycler;
    private int mRowCount;
    private int mScrollOffset;
    private IndexScrollBar mScrollbar;
    private Drawable mSelector;

    /* loaded from: classes.dex */
    public interface RecyclerListener {
        void onMovedToScrapHeap(View view);
    }

    public IndexGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mForceLayoutChildren = false;
        this.mCacheColorHint = 0;
        this.mRecycler = new RecycleBin();
        this.mScrollOffset = 0;
        this.mOldItemSelectedPosition = -1;
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.IndexGrid);
        this.mGridOffsetX = attr.getDimensionPixelSize(0, 0);
        this.mGridOffsetY = attr.getDimensionPixelSize(1, 0);
        this.mGridWidth = attr.getDimensionPixelSize(2, 0);
        this.mGridHeight = attr.getDimensionPixelSize(3, 0);
        this.mGpBarOffsetX = attr.getDimensionPixelSize(4, 0);
        this.mGpBarOffsetY = attr.getDimensionPixelSize(5, 0);
        this.mGpBarWidth = attr.getDimensionPixelSize(6, 0);
        this.mGpBarHeight = attr.getDimensionPixelSize(7, 0);
        this.mGpBarCenterOffsetY = attr.getDimensionPixelSize(8, 0);
        int groupbar_beep_timing = attr.getInt(9, 0);
        String groupbar_center_beep = attr.getString(10);
        String groupbar_upper_beep = attr.getString(11);
        String groupbar_lower_beep = attr.getString(12);
        this.mColumnCount = attr.getInt(13, 3);
        this.mRowCount = attr.getInt(14, 3);
        this.mLineGap = attr.getDimensionPixelSize(15, 0);
        this.mColumnGap = attr.getDimensionPixelSize(16, 0);
        attr.recycle();
        this.mGestureDetector = new GestureDetector(context, this);
        this.mGestureDetector.setIsLongpressEnabled(false);
        setClickable(false);
        setWillNotDraw(false);
        setAlwaysDrawnWithCacheEnabled(false);
        setDrawingCacheBackgroundColor(this.mCacheColorHint);
        this.mSelector = getResources().getDrawable(17306034);
        this.mGroupBar = new FolderBar(context);
        Drawable d = getResources().getDrawable(R.drawable.folder_bar);
        this.mGroupBar.setImageDrawable(d);
        this.mGroupBar.setFocusable(false);
        this.mGroupBar.setFocusableInTouchMode(false);
        this.mGroupBar.setInsensitiveHeight(this.mGpBarCenterOffsetY);
        this.mGroupBar.setTouchListener(this);
        this.mGroupBar.setBeepPattern(groupbar_beep_timing, groupbar_center_beep, groupbar_upper_beep, groupbar_lower_beep);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(this.mGpBarWidth, this.mGpBarHeight);
        addViewInLayout(this.mGroupBar, -1, params);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        this.mScrollOffset = 0;
        if (this.mGroupBar != null) {
            this.mGroupBar.setSelected(false);
        }
        this.mForceLayoutChildren = true;
        super.onAttachedToWindow();
    }

    @Override // android.widget.AdapterView, android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        this.mRecycler.scrapActiveViews();
        this.mRecycler.clear();
        if (this.mFlingRunnable != null) {
            if (this.mFlingRunnable.isScrolling()) {
                Log.i(TAG, MSG_DETACHED_ON_SCROLL);
                showSelector(true);
            }
            this.mFlingRunnable.abort();
        }
        this.mOldItemSelectedPosition = -1;
        this.mScrollOffset = 0;
        super.onDetachedFromWindow();
    }

    @Override // android.widget.AdapterView
    public IndexGridAdapter getAdapter() {
        return this.mAdapter;
    }

    @Override // android.widget.AdapterView
    public void setAdapter(IndexGridAdapter adapter) {
        this.mRecycler.clear();
        this.mAdapter = adapter;
        this.mForceLayoutChildren = true;
        requestLayout();
    }

    @Override // android.widget.AdapterView
    public int getCount() {
        if (this.mAdapter != null) {
            return this.mAdapter.getCount();
        }
        return 0;
    }

    @Override // android.widget.AdapterView
    public View getSelectedView() {
        if (this.mAdapter == null) {
            return null;
        }
        int position = this.mAdapter.getPosition();
        View activeView = this.mRecycler.findActiveView(position);
        if (activeView == null) {
            return this.mAdapter.getView(position, null, this);
        }
        return activeView;
    }

    @Override // android.widget.AdapterView
    public void setSelection(int position) {
        int current = getSelectedItemPosition();
        getAdapter().setPosition(position);
        View currentView = this.mRecycler.findActiveView(current);
        View nextView = this.mRecycler.findActiveView(position);
        if (currentView != null && nextView != null) {
            nextView.setSelected(true);
            if (this.mSelector != null) {
                this.mSelector.setBounds(nextView.getLeft(), nextView.getTop(), nextView.getRight(), nextView.getBottom());
            }
            currentView.setSelected(false);
            invokeSelectedNotification();
            return;
        }
        Log.i(TAG, MSG_SET_SELECTION_POSITION_CHANGED);
        positionChanged();
    }

    protected void positionChanged() {
        layoutChildren();
        if (this.mScrollbar != null) {
            this.mScrollbar.adjust();
        }
        invokeSelectedNotification();
    }

    public void showSelector(boolean isShown) {
        if (this.mSelector != null) {
            this.mSelector.setVisible(isShown, false);
        }
    }

    @Override // android.view.View
    public void setFocusable(boolean focusable) {
        try {
            Method superMethod = ViewGroup.class.getDeclaredMethod(METHOD_NAME_SET_FOCUSABLE, Boolean.class);
            superMethod.invoke(this, Boolean.valueOf(focusable));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
        } catch (NoSuchMethodException e3) {
            e3.printStackTrace();
        } catch (SecurityException e4) {
            e4.printStackTrace();
        } catch (InvocationTargetException e5) {
            e5.printStackTrace();
        }
    }

    @Override // android.widget.AdapterView, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed || this.mForceLayoutChildren) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                getChildAt(i).forceLayout();
            }
            this.mRecycler.markChildrenDirty();
            layoutChildren();
        }
    }

    public void layoutChildren() {
        layoutChildren(0);
    }

    public void layoutChildren(int offsetY) {
        invalidate();
        if (this.mAdapter == null) {
            removeAllViewsInLayout();
            this.mForceLayoutChildren = false;
            return;
        }
        detachAllViewsFromParent();
        ViewGroup.LayoutParams params = this.mGroupBar.getLayoutParams();
        addViewInLayout(this.mGroupBar, -1, params);
        int widthMeasureSpec = ViewGroup.getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(getWidth(), 1073741824), 0, params.width);
        int heightMeasureSpec = ViewGroup.getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(getHeight(), 1073741824), 0, params.height);
        this.mGroupBar.measure(widthMeasureSpec, heightMeasureSpec);
        this.mGroupBar.layout(this.mGpBarOffsetX, this.mGpBarOffsetY, this.mGpBarOffsetX + this.mGroupBar.getMeasuredWidth(), this.mGpBarOffsetY + this.mGroupBar.getMeasuredHeight());
        Log.d(TAG, LogHelper.getScratchBuilder(MSG_GROUPBAR).append(this.mGpBarOffsetX).append(", ").append(this.mGpBarOffsetY).append(", ").append(this.mGroupBar.getMeasuredWidth()).append(", ").append(this.mGroupBar.getMeasuredHeight()).append(LogHelper.MSG_CLOSE_BRACKET).toString());
        int blockW = (this.mGridWidth - (this.mColumnGap * (this.mColumnCount - 1))) / this.mColumnCount;
        int blockH = this.mGridHeight / this.mRowCount;
        int countInPage = getCountInPage();
        int y = this.mGridOffsetY;
        int rowCount = this.mRowCount;
        int singleOffset = offsetY % blockH;
        int offsetRow = offsetY / blockH;
        if (singleOffset != 0) {
            rowCount++;
            if (offsetY > 0) {
                offsetRow++;
                y -= blockH - singleOffset;
            } else {
                y += singleOffset;
            }
        }
        int currentPosition = this.mAdapter.getPosition();
        int max = this.mAdapter.getCount();
        int firstPosition = ((currentPosition / countInPage) * countInPage) - (this.mColumnCount * offsetRow);
        int i = 0;
        int position = firstPosition;
        while (i < rowCount && position < max) {
            int j = 0;
            int x = this.mGridOffsetX;
            while (j < this.mColumnCount && position < max) {
                if (position >= 0) {
                    boolean recycled = false;
                    View child = this.mRecycler.getActiveView(position);
                    if (child == null) {
                        View scrapView = this.mRecycler.getScrapView(position);
                        child = this.mAdapter.getView(position, scrapView, this);
                        if (child != null) {
                            if (scrapView != child) {
                                if (scrapView != null) {
                                    this.mRecycler.addScrapView(scrapView);
                                }
                                if (this.mCacheColorHint != 0) {
                                    child.setDrawingCacheBackgroundColor(this.mCacheColorHint);
                                }
                            } else {
                                recycled = true;
                            }
                        }
                    } else {
                        recycled = true;
                    }
                    ViewGroup.LayoutParams params2 = child.getLayoutParams();
                    if (recycled) {
                        attachViewToParent(child, -1, params2);
                        cleanupLayoutState(child);
                        child.offsetLeftAndRight(x - child.getLeft());
                        child.offsetTopAndBottom(y - child.getTop());
                    } else {
                        addViewInLayout(child, -1, params2);
                        int widthMeasureSpec2 = ViewGroup.getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(blockW, 1073741824), 0, blockW);
                        int heightMeasureSpec2 = ViewGroup.getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(blockH, 1073741824), 0, blockH);
                        child.measure(widthMeasureSpec2, heightMeasureSpec2);
                        Log.d(TAG, LogHelper.getScratchBuilder(MSG_CHILD_VIEW_MEASURE_OPEN).append(x).append(", ").append(y).append(MSG_CHILD_VIEW_MEASURE_CLOSE).append(x).append(", ").append(y).append(", ").append(child.getMeasuredWidth() + x).append(", ").append(child.getMeasuredHeight() + y).toString());
                        child.layout(x, y, child.getMeasuredWidth() + x, child.getMeasuredHeight() + y);
                    }
                    if (position == currentPosition) {
                        child.setSelected(true);
                        if (this.mSelector != null) {
                            Log.d(TAG, LogHelper.getScratchBuilder(MSG_SELECTOR_BOUNDS).append(child.getLeft()).append(", ").append(child.getTop()).append(", ").append(child.getWidth()).append(", ").append(child.getHeight()).append(LogHelper.MSG_CLOSE_BRACKET).toString());
                            this.mSelector.setBounds(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
                        }
                    } else {
                        child.setSelected(false);
                    }
                }
                j++;
                x += blockW;
                position++;
            }
            i++;
            y += blockH;
        }
        this.mRecycler.scrapActiveViews();
        this.mRecycler.fillActiveViews(getChildCount(), firstPosition);
        this.mForceLayoutChildren = false;
    }

    public void invalidateViews() {
        this.mRecycler.scrapActiveViews();
        layoutChildren();
        invalidate();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.mSelector != null && this.mSelector.isVisible() && !this.mSelector.getBounds().isEmpty() && !isGroupBarSelected()) {
            this.mSelector.draw(canvas);
        }
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (commonKey(keyCode, 1, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        if (commonKey(keyCode, repeatCount, event)) {
            return true;
        }
        return super.onKeyMultiple(keyCode, repeatCount, event);
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (commonKey(keyCode, 1, event)) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    public boolean canMoveCursor() {
        if (this.mAdapter == null) {
            return false;
        }
        return this.mFlingRunnable == null || !this.mFlingRunnable.isScrolling();
    }

    protected boolean commonKey(int keyCode, int count, KeyEvent event) {
        int selection;
        Log.d(TAG, LogHelper.getScratchBuilder(MSG_COMMON_KEY).append(keyCode).append(", ").append(event.getAction()).toString());
        int action = event.getAction();
        if (action == 1) {
            return false;
        }
        switch (keyCode) {
            case 0:
                switch (event.getScanCode()) {
                    case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
                    case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
                        boolean result = moveFocus(66);
                        return result;
                    case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
                    case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
                        boolean result2 = moveFocus(17);
                        return result2;
                    case AppRoot.USER_KEYCODE.DIAL1_STATUS /* 524 */:
                    case AppRoot.USER_KEYCODE.DIAL2_STATUS /* 527 */:
                    default:
                        return false;
                    case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
                        boolean result3 = scrollNextPage();
                        return result3;
                    case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
                        boolean result4 = scrollPreviousPage();
                        return result4;
                }
            case 19:
                boolean result5 = moveFocus(33);
                return result5;
            case 20:
                boolean result6 = moveFocus(130);
                return result6;
            case 21:
                boolean result7 = moveFocus(17);
                return result7;
            case 22:
                boolean result8 = moveFocus(66);
                return result8;
            case 23:
                if (!canMoveCursor() || event.getRepeatCount() != 0) {
                    return false;
                }
                AdapterView.OnItemClickListener listener = getOnItemClickListener();
                if (listener != null) {
                    View v = null;
                    boolean isBarSelected = isGroupBarSelected();
                    if (!isBarSelected) {
                        selection = getSelectedItemPosition();
                        v = getSelectedView();
                    } else {
                        selection = -2;
                    }
                    if (selection >= 0 || selection == -2) {
                        listener.onItemClick(this, v, selection, getAdapter().getItemId(selection));
                    }
                }
                return true;
            default:
                return false;
        }
    }

    public boolean scrollNextPage() {
        if (!canMoveCursor()) {
            return false;
        }
        int countInPage = getCountInPage();
        int currentPosition = this.mAdapter.getPosition();
        int page = (currentPosition / countInPage) + 1;
        int nextPosition = countInPage * page;
        if (nextPosition >= this.mAdapter.getCount()) {
            this.mRecycler.scrapActiveViews();
            this.mAdapter.changeGroup(1, -1);
            positionChanged();
        } else {
            pageScroll(true, nextPosition);
        }
        selectGroupBar(false);
        return true;
    }

    public boolean scrollPreviousPage() {
        if (!canMoveCursor()) {
            return false;
        }
        int countInPage = getCountInPage();
        int currentPosition = this.mAdapter.getPosition();
        int page = (currentPosition / countInPage) - 1;
        int nextPosition = countInPage * page;
        if (nextPosition < 0) {
            this.mRecycler.scrapActiveViews();
            this.mAdapter.changeGroupInLastLine(-1, 0, countInPage);
            positionChanged();
        } else {
            pageScroll(false, nextPosition);
        }
        selectGroupBar(false);
        return true;
    }

    public boolean moveFocus(int direction) {
        if (!canMoveCursor()) {
            return false;
        }
        if (isGroupBarSelected()) {
            return moveFromGroupBar(direction);
        }
        int countInPage = getCountInPage();
        int currentPosition = this.mAdapter.getPosition();
        int count = this.mAdapter.getCount();
        switch (direction) {
            case 17:
                if (currentPosition % this.mColumnCount == 0) {
                    selectGroupBar(true);
                    break;
                } else {
                    setSelection(currentPosition - 1);
                    break;
                }
            case 33:
                int currentIndex = positionToChildIndex(currentPosition);
                int nextPosition = currentPosition - this.mColumnCount;
                if (nextPosition < 0) {
                    this.mRecycler.scrapActiveViews();
                    this.mAdapter.changeGroupInLastLine(-1, currentPosition % this.mColumnCount, this.mColumnCount);
                    positionChanged();
                    break;
                } else if (currentIndex < this.mColumnCount) {
                    pageScroll(false, nextPosition);
                    break;
                } else {
                    setSelection(nextPosition);
                    break;
                }
            case 66:
                int nextPosition2 = currentPosition + 1;
                if (nextPosition2 >= count) {
                    selectGroupBar(true);
                    this.mRecycler.scrapActiveViews();
                    this.mAdapter.changeGroup(1, -1);
                    positionChanged();
                    break;
                } else if (nextPosition2 % countInPage == 0) {
                    selectGroupBar(true);
                    pageScroll(true, nextPosition2);
                    break;
                } else if (nextPosition2 % this.mColumnCount == 0) {
                    selectGroupBar(true);
                    break;
                } else {
                    setSelection(nextPosition2);
                    break;
                }
            case 130:
                int currentRow = currentPosition / this.mColumnCount;
                int lastRow = (count - 1) / this.mColumnCount;
                if (currentRow == lastRow) {
                    this.mRecycler.scrapActiveViews();
                    this.mAdapter.changeGroup(1, currentPosition % this.mColumnCount);
                    positionChanged();
                    break;
                } else {
                    int nextPosition3 = currentPosition + this.mColumnCount;
                    if (nextPosition3 >= count) {
                        nextPosition3 = count - 1;
                    }
                    int currentPage = currentPosition / countInPage;
                    int nextPage = nextPosition3 / countInPage;
                    if (currentPage != nextPage) {
                        pageScroll(true, nextPosition3);
                        break;
                    } else {
                        setSelection(nextPosition3);
                        break;
                    }
                }
        }
        return true;
    }

    public boolean isGroupBarSelected() {
        return this.mGroupBar != null && this.mGroupBar.isSelected();
    }

    protected void selectGroupBar(boolean isSelected) {
        if (this.mGroupBar.isSelected() != isSelected) {
            this.mGroupBar.setSelected(isSelected);
            invalidate();
            invokeSelectedNotification();
        }
    }

    protected void invokeSelectedNotification() {
        AdapterView.OnItemSelectedListener listener = getOnItemSelectedListener();
        if (listener != null) {
            boolean isScrolling = (this.mScrollbar != null && this.mScrollbar.isScrolling()) || (this.mFlingRunnable != null && this.mFlingRunnable.isScrolling());
            int selection = -1;
            IndexGridAdapter adapter = getAdapter();
            if (isGroupBarSelected()) {
                selection = -2;
            } else if (!isScrolling) {
                selection = getSelectedItemPosition();
            }
            if (this.mOldItemSelectedPosition != selection) {
                if (selection != -1) {
                    listener.onItemSelected(this, getSelectedView(), selection, adapter.getItemId(selection));
                } else {
                    listener.onNothingSelected(this);
                }
                this.mOldItemSelectedPosition = selection;
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:3:0x000c, code lost:            return true;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected boolean moveFromGroupBar(int r8) {
        /*
            r7 = this;
            r6 = 1
            r4 = 0
            r5 = -1
            com.sony.imaging.app.base.playback.widget.IndexGridAdapter r3 = r7.mAdapter
            int r2 = r3.getPosition()
            switch(r8) {
                case 17: goto Ld;
                case 33: goto L4c;
                case 66: goto L3d;
                case 130: goto L5f;
                default: goto Lc;
            }
        Lc:
            return r6
        Ld:
            r7.selectGroupBar(r4)
            int r1 = r7.positionToChildIndex(r2)
            if (r2 != 0) goto L25
            com.sony.imaging.app.base.playback.widget.IndexGrid$RecycleBin r3 = r7.mRecycler
            r3.scrapActiveViews()
            com.sony.imaging.app.base.playback.widget.IndexGridAdapter r3 = r7.mAdapter
            r4 = -2
            r3.changeGroup(r5, r4)
            r7.positionChanged()
            goto Lc
        L25:
            if (r1 != 0) goto L2d
            int r3 = r2 + (-1)
            r7.pageScroll(r4, r3)
            goto Lc
        L2d:
            int r3 = r7.mColumnCount
            int r0 = r2 % r3
            int r3 = r7.mColumnCount
            int r3 = r3 + (-1)
            if (r3 == r0) goto Lc
            int r3 = r2 + (-1)
            r7.setSelection(r3)
            goto Lc
        L3d:
            r7.selectGroupBar(r4)
            int r3 = r7.mColumnCount
            int r0 = r2 % r3
            if (r0 == 0) goto Lc
            int r3 = r2 + 1
            r7.setSelection(r3)
            goto Lc
        L4c:
            com.sony.imaging.app.base.playback.widget.IndexGrid$RecycleBin r3 = r7.mRecycler
            r3.scrapActiveViews()
            com.sony.imaging.app.base.playback.widget.IndexGridAdapter r3 = r7.mAdapter
            r3.changeGroup(r5, r5)
            com.sony.imaging.app.base.playback.widget.FolderBar r3 = r7.mGroupBar
            r3.setImagePressed(r4)
            r7.positionChanged()
            goto Lc
        L5f:
            com.sony.imaging.app.base.playback.widget.IndexGrid$RecycleBin r3 = r7.mRecycler
            r3.scrapActiveViews()
            com.sony.imaging.app.base.playback.widget.IndexGridAdapter r3 = r7.mAdapter
            r3.changeGroup(r6, r5)
            com.sony.imaging.app.base.playback.widget.FolderBar r3 = r7.mGroupBar
            r4 = 2
            r3.setImagePressed(r4)
            r7.positionChanged()
            goto Lc
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sony.imaging.app.base.playback.widget.IndexGrid.moveFromGroupBar(int):boolean");
    }

    @Override // android.widget.AdapterView
    public int getSelectedItemPosition() {
        if (this.mAdapter == null) {
            return -1;
        }
        return this.mAdapter.getPosition();
    }

    public int getCountInPage() {
        return this.mColumnCount * this.mRowCount;
    }

    public int getStartOffset() {
        int current = getSelectedItemPosition();
        return current - positionToChildIndex(current);
    }

    public int getPageCount() {
        int count;
        if (this.mAdapter == null || (count = this.mAdapter.getCount()) == 0) {
            return 0;
        }
        return ((count - 1) / getCountInPage()) + 1;
    }

    public int positionToChildIndex(int position) {
        int index = position % getCountInPage();
        return index;
    }

    public View getChildByAdapterPosition(int position) {
        if (this.mAdapter == null) {
            return null;
        }
        View activeView = this.mRecycler.findActiveView(position);
        if (activeView == null) {
            return this.mAdapter.getView(position, null, this);
        }
        return activeView;
    }

    public void setRecyclerListener(RecyclerListener listener) {
        this.mRecycler.mRecyclerListener = listener;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class RecycleBin {
        private int mFirstActivePosition;
        private RecyclerListener mRecyclerListener;
        private View[] mActiveViews = new View[0];
        private ArrayList<View> mScrapViews = new ArrayList<>();

        RecycleBin() {
        }

        public void markChildrenDirty() {
            ArrayList<View> scrap = this.mScrapViews;
            int scrapCount = scrap.size();
            for (int i = 0; i < scrapCount; i++) {
                scrap.get(i).forceLayout();
            }
        }

        public boolean shouldRecycleViewType(int viewType) {
            return viewType >= 0;
        }

        void clear() {
            ArrayList<View> scrap = this.mScrapViews;
            int scrapCount = scrap.size();
            for (int i = 0; i < scrapCount; i++) {
                IndexGrid.this.removeDetachedView(scrap.remove((scrapCount - 1) - i), false);
            }
        }

        void fillActiveViews(int childCount, int firstActivePosition) {
            int j;
            if (this.mActiveViews.length < childCount) {
                this.mActiveViews = new View[childCount];
            }
            this.mFirstActivePosition = firstActivePosition;
            View[] activeViews = this.mActiveViews;
            int i = 0;
            int j2 = 0;
            while (i < childCount) {
                View child = IndexGrid.this.getChildAt(i);
                if (IndexGrid.this.mGroupBar != child) {
                    j = j2 + 1;
                    activeViews[j2] = child;
                } else {
                    j = j2;
                }
                i++;
                j2 = j;
            }
        }

        View getActiveView(int position) {
            int index = position - this.mFirstActivePosition;
            View[] activeViews = this.mActiveViews;
            if (index < 0 || index >= activeViews.length) {
                return null;
            }
            View match = activeViews[index];
            activeViews[index] = null;
            return match;
        }

        View findActiveView(int position) {
            int index = position - this.mFirstActivePosition;
            View[] activeViews = this.mActiveViews;
            if (index < 0 || index >= activeViews.length) {
                return null;
            }
            return activeViews[index];
        }

        View getScrapView(int position) {
            ArrayList<View> scrapViews = this.mScrapViews;
            int size = scrapViews.size();
            if (size > 0) {
                return scrapViews.remove(size - 1);
            }
            return null;
        }

        void addScrapView(View scrap) {
            this.mScrapViews.add(scrap);
            if (this.mRecyclerListener != null) {
                this.mRecyclerListener.onMovedToScrapHeap(scrap);
            }
        }

        void scrapActiveViews() {
            View[] activeViews = this.mActiveViews;
            boolean hasListener = this.mRecyclerListener != null;
            ArrayList<View> scrapViews = this.mScrapViews;
            int count = activeViews.length;
            for (int i = count - 1; i >= 0; i--) {
                View victim = activeViews[i];
                if (victim != null) {
                    scrapViews.add(victim);
                    if (hasListener) {
                        this.mRecyclerListener.onMovedToScrapHeap(victim);
                    }
                }
                activeViews[i] = null;
            }
            pruneScrapViews();
        }

        private void pruneScrapViews() {
            int maxViews = this.mActiveViews.length;
            ArrayList<View> scrapViews = this.mScrapViews;
            int size = scrapViews.size();
            int extras = size - maxViews;
            int j = 0;
            int size2 = size - 1;
            while (j < extras) {
                IndexGrid.this.removeDetachedView(scrapViews.remove(size2), false);
                j++;
                size2--;
            }
        }

        void reclaimScrapViews(List<View> views) {
            views.addAll(this.mScrapViews);
        }

        void setCacheColorHint(int color) {
            ArrayList<View> scrap = this.mScrapViews;
            int scrapCount = scrap.size();
            for (int i = 0; i < scrapCount; i++) {
                scrap.get(i).setDrawingCacheBackgroundColor(color);
            }
            View[] activeViews = this.mActiveViews;
            for (View victim : activeViews) {
                if (victim != null) {
                    victim.setDrawingCacheBackgroundColor(color);
                }
            }
        }
    }

    void pageScroll(boolean isDown, int nextPosition) {
        if (this.mFlingRunnable == null) {
            this.mFlingRunnable = new FlingRunnable();
        }
        this.mFlingRunnable.startScroll(isDown ? this.mGridHeight : -this.mGridHeight, 200, nextPosition);
    }

    void scrollToFitPage(boolean isDown, int offset) {
        int distance;
        int page;
        if (this.mFlingRunnable == null) {
            this.mFlingRunnable = new FlingRunnable();
        }
        this.mScrollOffset = offset;
        if (offset > 0) {
            distance = offset % this.mGridHeight != 0 ? this.mGridHeight - (offset % this.mGridHeight) : 0;
            page = (-((int) Math.ceil(offset / this.mGridHeight))) + (getSelectedItemPosition() / getCountInPage());
        } else {
            distance = offset % this.mGridHeight != 0 ? -(this.mGridHeight + (offset % this.mGridHeight)) : 0;
            page = ((int) Math.ceil(Math.abs(offset) / this.mGridHeight)) + (getSelectedItemPosition() / getCountInPage());
        }
        this.mFlingRunnable.startScroll(-distance, 200, getCountInPage() * page);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class FlingRunnable implements Runnable {
        private int mLastFlingY;
        private final OverScroller mScroller;
        private int mNextPosition = -1;
        private boolean isStarting = false;

        FlingRunnable() {
            this.mScroller = new OverScroller(IndexGrid.this.getContext());
        }

        protected void start(int initialVelocity) {
            this.isStarting = true;
            int initialY = initialVelocity < 0 ? Integer.MAX_VALUE : 0;
            this.mLastFlingY = initialY;
            this.mScroller.fling(0, initialY, 0, initialVelocity, 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
            IndexGrid.this.post(this);
            IndexGrid.this.invokeSelectedNotification();
        }

        protected void startScroll(int distance, int duration, int nextPosition) {
            this.isStarting = true;
            this.mNextPosition = nextPosition;
            if (distance == 0) {
                endFling();
                return;
            }
            int initialY = distance < 0 ? Integer.MAX_VALUE : 0;
            this.mLastFlingY = initialY;
            this.mScroller.startScroll(0, initialY, 0, distance, duration);
            IndexGrid.this.post(this);
            IndexGrid.this.showSelector(false);
            IndexGrid.this.invokeSelectedNotification();
        }

        protected void abort() {
            if (this.mScroller != null && !this.mScroller.isFinished()) {
                this.mScroller.abortAnimation();
            }
            IndexGrid.this.removeCallbacks(this);
            this.isStarting = false;
            this.mNextPosition = -1;
        }

        private void endFling() {
            IndexGrid.this.removeCallbacks(this);
            this.isStarting = false;
            if (-1 != this.mNextPosition) {
                IndexGrid.this.setSelection(this.mNextPosition);
                this.mNextPosition = -1;
            }
            IndexGrid.this.mScrollOffset = 0;
            IndexGrid.this.showSelector(true);
            IndexGrid.this.invalidate();
        }

        protected boolean isScrolling() {
            return this.isStarting || !(this.mScroller == null || this.mScroller.isFinished());
        }

        @Override // java.lang.Runnable
        public void run() {
            Log.d(IndexGrid.TAG, IndexGrid.MSG_FLING_THREAD);
            if (IndexGrid.this.getCount() == 0 || ((IndexGrid.this.mGroupBar == null && IndexGrid.this.getChildCount() == 0) || (IndexGrid.this.mGroupBar != null && IndexGrid.this.getChildCount() == 1))) {
                Log.w(IndexGrid.TAG, IndexGrid.MSG_NO_CONTENTS);
                endFling();
                return;
            }
            OverScroller scroller = this.mScroller;
            boolean more = scroller.computeScrollOffset();
            int y = scroller.getCurrY();
            int delta = (this.mLastFlingY - y) + IndexGrid.this.mScrollOffset;
            IndexGrid.this.layoutChildren(delta);
            if (more) {
                IndexGrid.this.post(this);
            } else {
                endFling();
            }
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        int position;
        if (!isClickable() && !isLongClickable()) {
            return false;
        }
        boolean result = this.mGestureDetector.onTouchEvent(ev);
        Log.d(TAG, LogHelper.getScratchBuilder(MSG_ON_TOUCH_EVENT).append(ev.getAction()).append(LogHelper.MSG_COLON).append(result).toString());
        if (!result && ev.getActionMasked() == 1 && (position = pointToPosition((int) ev.getX(), (int) ev.getY())) >= 0 && position == getSelectedItemPosition()) {
            AdapterView.OnItemClickListener listener = getOnItemClickListener();
            View v = getSelectedView();
            listener.onItemClick(this, v, position, getAdapter().getItemId(position));
            return true;
        }
        return result;
    }

    public int pointToPosition(int x, int y) {
        Rect frame = new Rect();
        int count = getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            View child = getChildAt(i);
            if (child.getVisibility() == 0) {
                child.getHitRect(frame);
                if (frame.contains(x, y)) {
                    if (this.mGroupBar == child) {
                        return -2;
                    }
                    return getStartOffset() + (i - 1);
                }
            }
        }
        return -1;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onDown(MotionEvent e) {
        Log.i(TAG, LogHelper.getScratchBuilder(MSG_ON_DOWN).append(e).toString());
        int position = pointToPosition((int) e.getX(), (int) e.getY());
        if (position >= 0) {
            selectGroupBar(false);
            setSelection(position);
            return true;
        }
        return true;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.i(TAG, LogHelper.getScratchBuilder(MSG_ON_FLING).append(velocityY).toString());
        if (Math.abs(velocityX) < Math.abs(velocityY)) {
            PTag.start(MSG_START_SCROLL);
            if (velocityY < 0.0f) {
                return scrollNextPage();
            }
            return scrollPreviousPage();
        }
        return false;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public void onLongPress(MotionEvent e) {
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.i(TAG, LogHelper.getScratchBuilder(MSG_ON_SCROLL).append(distanceY).toString());
        return false;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public void onShowPress(MotionEvent e) {
        Log.d(TAG, LogHelper.getScratchBuilder(MSG_ON_SHOW_PRESS).append(e).toString());
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d(TAG, LogHelper.getScratchBuilder(MSG_ON_SINGLE_TAP).append(e).toString());
        return false;
    }

    @Override // com.sony.imaging.app.base.playback.widget.IndexScrollBar.IndexScroller
    public int getRange() {
        return getPageCount() * this.mGridHeight;
    }

    @Override // com.sony.imaging.app.base.playback.widget.IndexScrollBar.IndexScroller
    public int getExtent() {
        return this.mGridHeight;
    }

    @Override // com.sony.imaging.app.base.playback.widget.IndexScrollBar.IndexScroller
    public int getOffset() {
        int pageCount = getPageCount();
        if (pageCount == 0) {
            return 0;
        }
        int offset = (getSelectedItemPosition() / getCountInPage()) * this.mGridHeight;
        return offset - this.mScrollOffset;
    }

    @Override // com.sony.imaging.app.base.playback.widget.IndexScrollBar.IndexScroller
    public void scrollThumb(int distance, boolean isReleased, int from, int now) {
        int range = getRange();
        int offset = (getSelectedItemPosition() / getCountInPage()) * this.mGridHeight;
        int extent = getExtent();
        if (distance + offset < 0) {
            distance = -offset;
        } else if (distance + offset > range - extent) {
            distance = (range - extent) - offset;
        }
        int distance2 = -distance;
        this.mScrollOffset = distance2;
        if (!isReleased) {
            showSelector(false);
            layoutChildren(distance2);
            this.mScrollbar.adjust();
            invokeSelectedNotification();
            return;
        }
        Log.i(TAG, MSG_END_SCROLL_THUMB);
        selectGroupBar(false);
        scrollToFitPage(false, distance2);
    }

    @Override // com.sony.imaging.app.base.playback.widget.IndexScrollBar.IndexScroller
    public void resisterScrollBar(IndexScrollBar scrollBar) {
        this.mScrollbar = scrollBar;
        scrollBar.adjust();
    }

    @Override // com.sony.imaging.app.base.playback.widget.FolderBar.OnTouchListener
    public boolean onTouchDown(int pushedArea) {
        invalidate();
        invokeSelectedNotification();
        return true;
    }

    @Override // com.sony.imaging.app.base.playback.widget.FolderBar.OnTouchListener
    public boolean onTouchUp(int upArea, int downArea) {
        if (upArea != downArea) {
            return false;
        }
        switch (upArea) {
            case 0:
                this.mRecycler.scrapActiveViews();
                this.mAdapter.changeGroup(-1, -1);
                positionChanged();
                return true;
            case 1:
                AdapterView.OnItemClickListener listener = getOnItemClickListener();
                if (listener != null) {
                    listener.onItemClick(this, null, -2, 0L);
                }
                return true;
            case 2:
                this.mRecycler.scrapActiveViews();
                this.mAdapter.changeGroup(1, -1);
                positionChanged();
                return true;
            default:
                return false;
        }
    }
}
