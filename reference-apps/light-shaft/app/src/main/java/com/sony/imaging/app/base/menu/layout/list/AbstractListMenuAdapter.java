package com.sony.imaging.app.base.menu.layout.list;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public abstract class AbstractListMenuAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<DefaultListMenuItem> items;
    private ListMenuSelectedListener listener;
    private Resources res;

    /* loaded from: classes.dex */
    public interface ListMenuSelectedListener {
        void onItemFocused(int i);

        void onItemReleased(int i);

        void onItemSelected(int i, boolean z);

        void onItemTouched(int i);
    }

    protected abstract int getRowViewLayoutId();

    protected abstract View getYourView(int i, View view, ViewGroup viewGroup);

    public AbstractListMenuAdapter(Context context, ArrayList<DefaultListMenuItem> items, ListMenuSelectedListener listener) {
        this.res = context.getResources();
        this.items = items;
        this.inflater = (LayoutInflater) context.getSystemService("layout_inflater");
        this.listener = listener;
    }

    @Override // android.widget.Adapter
    public final int getCount() {
        return this.items.size();
    }

    @Override // android.widget.Adapter
    public final DefaultListMenuItem getItem(int position) {
        return this.items.get(position);
    }

    public final boolean removeItem(int position) {
        if (this.items.size() - 1 < position) {
            return false;
        }
        this.items.remove(position);
        return true;
    }

    public final void addItem(DefaultListMenuItem item) {
        this.items.add(item);
    }

    public final boolean addItem(DefaultListMenuItem item, int position) {
        if (this.items.size() < position) {
            return false;
        }
        this.items.add(position, item);
        return true;
    }

    @Override // android.widget.Adapter
    public final long getItemId(int position) {
        return position;
    }

    @Override // android.widget.Adapter
    public final View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = this.inflater.inflate(getRowViewLayoutId(), (ViewGroup) null);
        }
        DefaultListMenuItem item = this.items.get(position);
        try {
            ((FakeCheckable) view).setFakeChecked(item.isFakeChecked());
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        setDefaultListeners(view, position);
        return getYourView(position, view, parent);
    }

    private void setDefaultListeners(View view, final int position) {
        view.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.sony.imaging.app.base.menu.layout.list.AbstractListMenuAdapter.1
            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    AbstractListMenuAdapter.this.getListMenuSelectedListener().onItemFocused(position);
                }
            }
        });
        view.setOnTouchListener(new View.OnTouchListener() { // from class: com.sony.imaging.app.base.menu.layout.list.AbstractListMenuAdapter.2
            /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
            /* JADX WARN: Code restructure failed: missing block: B:3:0x0008, code lost:            return false;     */
            @Override // android.view.View.OnTouchListener
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct code enable 'Show inconsistent code' option in preferences
            */
            public boolean onTouch(android.view.View r5, android.view.MotionEvent r6) {
                /*
                    r4 = this;
                    r3 = 0
                    int r0 = r6.getAction()
                    switch(r0) {
                        case 0: goto L9;
                        case 1: goto L19;
                        case 2: goto L8;
                        case 3: goto L19;
                        default: goto L8;
                    }
                L8:
                    return r3
                L9:
                    r1 = 1
                    r5.setPressed(r1)
                    com.sony.imaging.app.base.menu.layout.list.AbstractListMenuAdapter r1 = com.sony.imaging.app.base.menu.layout.list.AbstractListMenuAdapter.this
                    com.sony.imaging.app.base.menu.layout.list.AbstractListMenuAdapter$ListMenuSelectedListener r1 = r1.getListMenuSelectedListener()
                    int r2 = r2
                    r1.onItemTouched(r2)
                    goto L8
                L19:
                    r5.setPressed(r3)
                    com.sony.imaging.app.base.menu.layout.list.AbstractListMenuAdapter r1 = com.sony.imaging.app.base.menu.layout.list.AbstractListMenuAdapter.this
                    com.sony.imaging.app.base.menu.layout.list.AbstractListMenuAdapter$ListMenuSelectedListener r1 = r1.getListMenuSelectedListener()
                    int r2 = r2
                    r1.onItemReleased(r2)
                    goto L8
                */
                throw new UnsupportedOperationException("Method not decompiled: com.sony.imaging.app.base.menu.layout.list.AbstractListMenuAdapter.AnonymousClass2.onTouch(android.view.View, android.view.MotionEvent):boolean");
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final ListMenuSelectedListener getListMenuSelectedListener() {
        return this.listener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final Resources getResources() {
        return this.res;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final List<DefaultListMenuItem> getMenuItems() {
        return this.items;
    }
}
