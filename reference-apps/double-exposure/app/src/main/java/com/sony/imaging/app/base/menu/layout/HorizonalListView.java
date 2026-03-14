package com.sony.imaging.app.base.menu.layout;

import android.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.sony.imaging.app.fw.AppRoot;

/* loaded from: classes.dex */
public class HorizonalListView extends ListView {
    private static final int INVALID_LAYOUT_PARAM = -3;
    private static final String TAG = HorizonalListView.class.getSimpleName();
    private int mItemHeight;
    private int mItemWidth;
    private AbsListView.LayoutParams mLayoutParams;
    View[] mScrapViews;
    Drawable mSelectedBackgroundDrawable;
    private int mVisibleItemNum;

    public HorizonalListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mItemWidth = -3;
        this.mItemHeight = -3;
        this.mVisibleItemNum = -1;
        TypedArray tArray = context.obtainStyledAttributes(attrs, new int[]{R.attr.background});
        this.mSelectedBackgroundDrawable = tArray.getDrawable(0);
        TypedArray tArray2 = context.obtainStyledAttributes(attrs, com.sony.imaging.app.base.R.styleable.BeltView);
        this.mVisibleItemNum = tArray2.getInt(0, this.mVisibleItemNum);
    }

    @Override // android.widget.AdapterView
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        this.mScrapViews = new View[adapter == null ? 0 : adapter.getCount()];
    }

    @Override // android.widget.ListView, android.widget.AbsListView
    protected void layoutChildren() {
        ListAdapter adapter = getAdapter();
        if (adapter == null) {
            Log.e(TAG, "Adapter is null. You should do setAdapter.");
            return;
        }
        int itemCount = adapter.getCount();
        if (itemCount == 0) {
            removeAllViewsInLayout();
            invalidate();
            return;
        }
        detachAllViewsFromParent();
        if (this.mLayoutParams == null) {
            this.mLayoutParams = new AbsListView.LayoutParams(-2, -2);
        }
        int left = 0;
        int measuredWidth = View.MeasureSpec.getSize(getMeasuredWidth());
        int measuredHeight = View.MeasureSpec.getSize(getMeasuredHeight());
        for (int i = 0; i < itemCount; i++) {
            View child = obtainView(i);
            child.setLayoutParams(this.mLayoutParams);
            addViewInLayout(child, -1, this.mLayoutParams, true);
            measureChild(child, measuredWidth / itemCount, measuredHeight);
            if (i == 0) {
                left += getChildrenDrawOffSet();
            }
            child.layout(left, 0, View.MeasureSpec.getSize(child.getMeasuredWidth()) + left, View.MeasureSpec.getSize(child.getMeasuredHeight()));
            left += child.getMeasuredWidth();
        }
        int pos = getSelectedItemPosition();
        int count = getChildCount();
        int i2 = 0;
        while (i2 < count) {
            View view = getChildAt(i2);
            view.setSelected(pos == i2);
            i2++;
        }
        invalidate();
    }

    View obtainView(int position) {
        ListAdapter adapter = getAdapter();
        View child = adapter.getView(position, this.mScrapViews[position], this);
        this.mScrapViews[position] = child;
        return child;
    }

    protected int getChildrenDrawOffSet() {
        return 0;
    }

    @Override // android.widget.AbsListView
    public void setSelectionFromTop(int selectPos, int y) {
        if (selectPos >= 0) {
            int oldPos = getSelectedItemPosition();
            if (oldPos != selectPos) {
                int count = getCount();
                int i = 0;
                while (i < count) {
                    View view = getChildAt(i);
                    if (view != null) {
                        boolean b = selectPos == i;
                        view.setSelected(b);
                        AdapterView.OnItemSelectedListener listener = getOnItemSelectedListener();
                        if (b && listener != null) {
                            listener.onItemSelected(this, view, i, i);
                        }
                    }
                    i++;
                }
                super.setSelectionFromTop(selectPos, y);
            }
        }
    }

    @Override // android.widget.ListView, android.widget.AbsListView, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        View.MeasureSpec.getSize(widthMeasureSpec);
        View.MeasureSpec.getSize(heightMeasureSpec);
        View.MeasureSpec.getMode(widthMeasureSpec);
        View.MeasureSpec.getMode(heightMeasureSpec);
        View child = obtainView(0);
        ListAdapter adapter = getAdapter();
        int childNum = adapter != null ? adapter.getCount() : 0;
        measureChild(child, widthMeasureSpec, heightMeasureSpec);
        if (this.mVisibleItemNum != -1) {
            childNum = this.mVisibleItemNum;
        }
        int width = View.MeasureSpec.makeMeasureSpec(child.getMeasuredWidth() * childNum, 1073741824);
        int height = View.MeasureSpec.makeMeasureSpec(child.getMeasuredHeight(), 1073741824);
        setMeasuredDimension(width, height);
        super.onMeasure(width, height);
    }

    @Override // android.widget.ListView, android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() != 0) {
            return false;
        }
        boolean ret = onKeyDown(event.getKeyCode(), event);
        return ret;
    }

    @Override // android.widget.ListView, android.widget.AbsListView, android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean ret = false;
        ListAdapter adapter = getAdapter();
        if (adapter == null) {
            return false;
        }
        int curPos = getSelectedItemPosition();
        int childNum = getCount();
        int lastIndex = childNum - 1;
        switch (event.getScanCode()) {
            case AppRoot.USER_KEYCODE.LEFT /* 105 */:
                setSelectionFromTop(curPos > 0 ? curPos - 1 : lastIndex, 0);
                ret = true;
                break;
            case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
                setSelectionFromTop(curPos < lastIndex ? curPos + 1 : 0, 0);
                ret = true;
                break;
        }
        return ret;
    }

    @Override // android.widget.AbsListView
    public boolean isSmoothScrollbarEnabled() {
        return false;
    }

    @Override // android.view.View
    public boolean isVerticalScrollBarEnabled() {
        return false;
    }

    @Override // android.view.View
    public boolean isHorizontalScrollBarEnabled() {
        return false;
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override // android.view.View
    public boolean isInTouchMode() {
        return false;
    }

    @Override // android.widget.AbsListView, android.view.ViewTreeObserver.OnTouchModeChangeListener
    public void onTouchModeChanged(boolean isInTouchMode) {
        super.onTouchModeChanged(false);
    }
}
