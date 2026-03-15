package com.sony.imaging.app.base.menu.layout;

import android.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import com.sony.imaging.app.fw.AppRoot;

/* loaded from: classes.dex */
public class CursorableGridView extends GridView {
    private static final String CANNOT_SELECT_MSG = "Cannot select anything. Do nothing.";
    private static final String TAG = CursorableGridView.class.getSimpleName();
    private int mColumnNum;

    public CursorableGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        int[] attrsArray = {R.attr.numColumns};
        TypedArray a = context.obtainStyledAttributes(attrs, attrsArray);
        this.mColumnNum = a.getInt(0, 6);
    }

    public int getColumnNum() {
        return this.mColumnNum;
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() != 0) {
            return false;
        }
        boolean ret = onKeyDown(event.getKeyCode(), event);
        return ret;
    }

    @Override // android.widget.GridView, android.widget.AbsListView
    protected void layoutChildren() {
        super.layoutChildren();
        int pos = getSelectedItemPosition();
        setSelection(pos);
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

    @Override // android.widget.GridView, android.widget.AdapterView
    public void setSelection(int position) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            if (view != null) {
                int j = getPositionForView(view);
                boolean selected = position == j;
                view.setSelected(selected);
                AdapterView.OnItemSelectedListener l = getOnItemSelectedListener();
                if (selected && l != null) {
                    l.onItemSelected(this, view, j, view.getId());
                }
            }
        }
        super.setSelection(position);
    }

    @Override // android.widget.GridView, android.widget.AbsListView, android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        ListAdapter adapter = getAdapter();
        if (adapter != null && adapter.getCount() > 1) {
            int nextPos = getSelectedItemPosition();
            int key = event.getScanCode();
            do {
                nextPos = getNextPosition(nextPos, key);
                if (nextPos == -1) {
                    break;
                }
            } while (!adapter.isEnabled(nextPos));
            if (nextPos == -1) {
                return false;
            }
            setSelection(nextPos);
            return true;
        }
        Log.w(TAG, CANNOT_SELECT_MSG);
        return false;
    }

    protected int getNextPosition(int curPos, int keycode) {
        int ct = getCount();
        int LAST_POS = ct - 1;
        if (curPos < 0 || curPos > LAST_POS) {
            curPos = 0;
        }
        switch (keycode) {
            case AppRoot.USER_KEYCODE.UP /* 103 */:
                if (curPos == 0) {
                    return LAST_POS;
                }
                if (curPos - this.mColumnNum >= 0) {
                    int nextPos = curPos - this.mColumnNum;
                    return nextPos;
                }
                int nextPos2 = curPos;
                while (this.mColumnNum + nextPos2 < ct) {
                    nextPos2 += this.mColumnNum;
                }
                return nextPos2 - 1;
            case 104:
            case 107:
            default:
                return -1;
            case AppRoot.USER_KEYCODE.LEFT /* 105 */:
                if (curPos - 1 < 0) {
                    return LAST_POS;
                }
                int nextPos3 = curPos - 1;
                return nextPos3;
            case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
                if (curPos + 1 >= ct) {
                    return 0;
                }
                int nextPos4 = curPos + 1;
                return nextPos4;
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                if (curPos == LAST_POS) {
                    return 0;
                }
                if (this.mColumnNum + curPos >= ct) {
                    int nextPos5 = ((this.mColumnNum + curPos) % ct) + 1;
                    return nextPos5;
                }
                int nextPos6 = curPos + this.mColumnNum;
                return nextPos6;
        }
    }
}
