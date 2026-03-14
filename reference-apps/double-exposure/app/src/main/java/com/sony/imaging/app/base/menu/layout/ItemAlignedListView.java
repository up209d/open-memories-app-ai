package com.sony.imaging.app.base.menu.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.sony.imaging.app.fw.AppRoot;

/* loaded from: classes.dex */
public class ItemAlignedListView extends ListView {
    private static final int INVALID_LAYOUT_PARAM = -3;
    private static final String TAG = ItemAlignedListView.class.getSimpleName();
    private int mItemHeight;
    private int mItemWidth;

    public ItemAlignedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemAlignedListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override // android.widget.ListView, android.widget.AbsListView
    protected void layoutChildren() {
        super.layoutChildren();
        int pos = getSelectedItemPosition();
        setSelection(pos);
    }

    @Override // android.widget.ListView, android.widget.AbsListView, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        int widthConstraint = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightConstraint = View.MeasureSpec.getMode(heightMeasureSpec);
        ListAdapter adapter = getAdapter();
        int childNum = 0;
        if (adapter != null) {
            childNum = adapter.getCount();
        }
        if (this.mItemWidth != -3) {
            int baseOfChildWidth = (this.mItemWidth + 1) * childNum;
            if (widthConstraint != Integer.MIN_VALUE) {
                width = baseOfChildWidth;
            } else if (baseOfChildWidth < width) {
                width = baseOfChildWidth;
            }
        }
        if (this.mItemHeight != -3) {
            int baseOfChildHeight = (this.mItemHeight + 1) * childNum;
            if (heightConstraint != Integer.MIN_VALUE) {
                height = baseOfChildHeight;
            } else if (baseOfChildHeight < height) {
                height = baseOfChildHeight;
            }
        }
        setMeasuredDimension(View.MeasureSpec.makeMeasureSpec(width, 1073741824), View.MeasureSpec.makeMeasureSpec(height, 1073741824));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override // android.widget.ListView, android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent event) {
        View focused = getFocusedChild();
        requestChildFocus(getChildAt(0), focused);
        return super.dispatchKeyEvent(event);
    }

    @Override // android.widget.AbsListView
    public void setSelectionFromTop(int selectPos, int y) {
        if (selectPos >= 0) {
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                View view = getChildAt(i);
                int j = getPositionForView(view);
                if (view != null) {
                    boolean isSelected = selectPos == j;
                    view.setSelected(isSelected);
                    AdapterView.OnItemSelectedListener l = getOnItemSelectedListener();
                    if (isSelected && l != null) {
                        l.onItemSelected(this, view, j, j);
                    }
                }
            }
            super.setSelectionFromTop(selectPos, y);
        }
    }

    @Override // android.widget.ListView, android.widget.AdapterView
    public void setSelection(int position) {
        int firstPos = getFirstVisiblePosition();
        int lastPos = getLastVisiblePosition();
        View view = null;
        if (position >= firstPos && position <= lastPos) {
            view = getChildAt(position - firstPos);
        }
        int top = 0;
        if (view != null) {
            top = view.getTop();
        }
        setSelectionFromTop(position, top);
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

    private int getOneItemHeight() {
        View firstChild = getChildAt(0);
        if (firstChild == null) {
            Log.e(TAG, "Not found child view.");
            return -1;
        }
        int separatorHeight = getDivider().getIntrinsicHeight();
        int height = firstChild.getHeight();
        if (separatorHeight == -1) {
            separatorHeight = 0;
        }
        return height + separatorHeight;
    }

    @Override // android.widget.ListView, android.widget.AbsListView, android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean ret = false;
        if (getAdapter() == null) {
            return false;
        }
        int curPos = getSelectedItemPosition();
        int childNum = getCount();
        int firstVisiblePos = getFirstVisiblePosition();
        int lastVisiblePos = getLastVisiblePosition();
        if (lastVisiblePos - firstVisiblePos > 5) {
            firstVisiblePos++;
        }
        int visibleNum = (lastVisiblePos - firstVisiblePos) + 1;
        int childHeight = getOneItemHeight();
        if (event.getAction() == 0) {
            switch (event.getScanCode()) {
                case AppRoot.USER_KEYCODE.UP /* 103 */:
                    if (curPos > 0) {
                        int nextPos = curPos - 1;
                        if (nextPos > firstVisiblePos && nextPos < lastVisiblePos) {
                            setSelectionFromTop(nextPos, (nextPos - firstVisiblePos) * childHeight);
                        } else if (nextPos > 0) {
                            setSelectionFromTop(nextPos, childHeight);
                        } else {
                            setSelectionFromTop(nextPos, 0);
                        }
                    } else {
                        setSelectionFromTop(childNum - 1, (visibleNum - 1) * childHeight);
                    }
                    ret = true;
                    break;
                case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                    if (curPos < childNum - 1) {
                        int curPos2 = curPos + 1;
                        if (curPos2 > firstVisiblePos && curPos2 < lastVisiblePos) {
                            setSelectionFromTop(curPos2, (curPos2 - firstVisiblePos) * childHeight);
                        } else if (curPos2 < visibleNum - 2) {
                            setSelectionFromTop(curPos2, curPos2 * childHeight);
                        } else if (curPos2 >= visibleNum - 2 && curPos2 < childNum - 1) {
                            setSelectionFromTop(curPos2, (visibleNum - 2) * childHeight);
                        } else {
                            setSelectionFromTop(curPos2, (visibleNum - 1) * childHeight);
                        }
                    } else {
                        setSelectionFromTop(0, 0);
                    }
                    ret = true;
                    break;
                case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                    AdapterView.OnItemClickListener listener = getOnItemClickListener();
                    if (listener != null) {
                        listener.onItemClick(this, getSelectedView(), curPos, getItemIdAtPosition(curPos));
                    }
                    ret = getAdapter().isEnabled(curPos);
                    break;
            }
        }
        return ret;
    }
}
