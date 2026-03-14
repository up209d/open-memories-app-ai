package com.sony.imaging.app.base.menu.layout.list;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ListAdapter;
import android.widget.ListView;

/* loaded from: classes.dex */
public final class MenuListView extends ListView {
    private static final String TAG = MenuListView.class.getSimpleName();
    private OverScrollListener listener;

    /* loaded from: classes.dex */
    public interface OverScrollListener {
        void onOverScrolled(int i, int i2, boolean z, boolean z2);
    }

    public MenuListView(Context context) {
        super(context);
        this.listener = null;
    }

    public MenuListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.listener = null;
    }

    public MenuListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.listener = null;
    }

    @Override // android.widget.ListView, android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent event) {
        return false;
    }

    @Override // android.widget.AbsListView
    public void setSelectionFromTop(int position, int y) {
        refreshCheckedState(position);
        super.setSelectionFromTop(position, y);
    }

    protected void refreshCheckedState(int position) {
        Log.d(TAG, "refleshCheckedState for " + position);
        ListAdapter adapter = getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            setCheckedStateAtPosition(adapter, i, false);
        }
        setCheckedStateAtPosition(adapter, position, true);
        ((AbstractListMenuAdapter) adapter).notifyDataSetChanged();
    }

    private void setCheckedStateAtPosition(ListAdapter adapter, int position, boolean checked) {
        ((DefaultListMenuItem) adapter.getItem(position)).setFakeChecked(checked);
    }

    @Override // android.view.View
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, 2, isTouchEvent);
    }

    @Override // android.widget.AbsListView, android.view.View
    public void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        if (this.listener != null) {
            this.listener.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        }
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    public void registerOverScrollListener(OverScrollListener listener) {
        this.listener = listener;
    }
}
