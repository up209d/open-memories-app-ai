package com.sony.imaging.app.base.menu.layout;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.fw.AppRoot;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public abstract class SimpleMenuLayout extends DisplayMenuItemsMenuLayout implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
    private DisplayMenuItemsMenuLayout.MenuUpdater mMenuUpdater;
    private SimpleMenuAdapter mSimpleMenuAdapter;

    protected abstract int getAdapterLayoutID();

    protected abstract AdapterView getAdapterView();

    protected abstract void setAdapterResources(ArrayList<Object> arrayList, String str);

    protected int getInitialPos() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public DisplayMenuItemsMenuLayout.MenuUpdater getMenuUpdater() {
        if (this.mMenuUpdater == null) {
            this.mMenuUpdater = new DisplayMenuItemsMenuLayout.MenuUpdater(new Handler()) { // from class: com.sony.imaging.app.base.menu.layout.SimpleMenuLayout.1
                @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout.MenuUpdater, java.lang.Runnable
                public void run() {
                    SimpleMenuLayout.this.updateAdapterView(SimpleMenuLayout.this.mService.getSupportedItemList());
                }
            };
        }
        return this.mMenuUpdater;
    }

    protected void updateAdapterView(ArrayList<String> itemIds) {
        AdapterView adapterView = getAdapterView();
        int listPos = getAdapterView().getSelectedItemPosition();
        this.mSimpleMenuAdapter.clear();
        this.mService.updateSettingItemsAvailable(itemIds);
        Iterator i$ = itemIds.iterator();
        while (i$.hasNext()) {
            String itemId = i$.next();
            AdapterItem item = new AdapterItem();
            ArrayList<Object> resources = new ArrayList<>();
            setAdapterResources(resources, itemId);
            item.resources = resources;
            item.valid = this.mService.isMenuItemValid(itemId);
            this.mSimpleMenuAdapter.add(item);
        }
        adapterView.setAdapter(this.mSimpleMenuAdapter);
        adapterView.setSelection(listPos);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mSimpleMenuAdapter = new SimpleMenuAdapter(getActivity(), getAdapterLayoutID());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        AdapterView adapterView = getAdapterView();
        adapterView.setFocusable(false);
        updateAdapterView(this.mService.getSupportedItemList());
        adapterView.setSelection(getInitialPos());
        adapterView.setOnItemClickListener(this);
        adapterView.setOnItemSelectedListener(this);
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        if (this.mMenuUpdater != null) {
            this.mMenuUpdater.cancelMenuUpdater();
        }
        this.mMenuUpdater = null;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        getAdapterView().dispatchKeyEvent(new KeyEvent(0L, 0L, 0, 0, 0, 0, 0, AppRoot.USER_KEYCODE.DOWN));
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        getAdapterView().dispatchKeyEvent(new KeyEvent(0L, 0L, 0, 0, 0, 0, 0, AppRoot.USER_KEYCODE.UP));
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        getAdapterView().dispatchKeyEvent(new KeyEvent(0L, 0L, 0, 0, 0, 0, 0, AppRoot.USER_KEYCODE.DOWN));
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        getAdapterView().dispatchKeyEvent(new KeyEvent(0L, 0L, 0, 0, 0, 0, 0, AppRoot.USER_KEYCODE.UP));
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        ArrayList<String> menuItemList = this.mService.getSupportedItemList();
        int pos = getAdapterView().getSelectedItemPosition();
        if (pos >= 0 && pos < menuItemList.size()) {
            String itemId = menuItemList.get(pos);
            guideResources.add(this.mService.getMenuItemText(itemId));
            guideResources.add(this.mService.getMenuItemGuideText(itemId));
            guideResources.add(Boolean.valueOf(this.mService.isMenuItemValid(itemId)));
        }
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        ArrayList<String> menuItemList = this.mService.getSupportedItemList();
        if (arg2 >= 0 && arg2 < menuItemList.size()) {
            String clickedItemId = menuItemList.get(arg2);
            doItemClickProcessing(clickedItemId);
        }
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        boolean ret = isFunctionGuideShown() ? false : false | getAdapterView().dispatchKeyEvent(event);
        if (ret) {
            return 1;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected void onDeviceStatusChanged() {
        this.mService.setMenuItemId(this.mService.getMenuItemId());
        updateAdapterView(this.mService.getSupportedItemList());
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.fw.Layout
    public void onReopened() {
        super.onReopened();
        this.mService.setMenuItemId(this.mService.getMenuItemId());
        updateAdapterView(this.mService.getSupportedItemList());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void onMediaRemainingChanged() {
        getMenuUpdater().restartMenuUpdater();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected void onFocusModeChanged() {
        getMenuUpdater().restartMenuUpdater();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected void onDigitalZoomOnOff(boolean onoff) {
        getMenuUpdater().restartMenuUpdater();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected void onStreamWriterStatusChanged() {
        getMenuUpdater().restartMenuUpdater();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        openPreviousMenu();
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class AdapterItem {
        protected ArrayList<Object> resources;
        protected boolean valid;

        protected AdapterItem() {
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class SimpleMenuAdapter extends ArrayAdapter<AdapterItem> implements ListAdapter {
        protected static final String ELEMENT_TEXT = "element_text";
        protected static final String VALUE_TEXT = "value_text";
        protected int ELEMENT_TEXT_COLOR;
        protected int ELEMENT_TEXT_INVALID_COLOR;
        protected int VALUE_TEXT_COLOR;
        protected int VALUE_TEXT_INVALID_COLOR;
        protected LayoutInflater mInflater;
        protected int mLayoutResourceId;

        public SimpleMenuAdapter(Context context, int ResId) {
            super(context, ResId);
            this.mLayoutResourceId = ResId;
            this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
            Resources r = SimpleMenuLayout.this.getResources();
            this.ELEMENT_TEXT_COLOR = r.getColor(R.color.RESID_FONTSTYLE_U_CMN);
            this.ELEMENT_TEXT_INVALID_COLOR = r.getColor(R.color.RESID_FONTSTYLE_U_MENU_ITEM_DISABLE);
            this.VALUE_TEXT_COLOR = r.getColor(R.color.RESID_FONTSTYLE_U_MENU_ITEM_VALUE);
            this.VALUE_TEXT_INVALID_COLOR = r.getColor(R.color.RESID_FONTSTYLE_U_MENU_ITEM_DISABLE);
        }

        /* loaded from: classes.dex */
        protected class ViewHolder {
            ArrayList<View> views;

            protected ViewHolder() {
            }
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            ViewGroup view = (ViewGroup) convertView;
            if (view == null) {
                view = (ViewGroup) this.mInflater.inflate(this.mLayoutResourceId, (ViewGroup) null);
                int childCount = view.getChildCount();
                ArrayList<View> views = new ArrayList<>();
                for (int i = 0; i < childCount; i++) {
                    views.add(view.getChildAt(i));
                }
                holder = new ViewHolder();
                holder.views = views;
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            AdapterItem menuItem = getItem(position);
            int i2 = 0;
            Iterator i$ = menuItem.resources.iterator();
            while (i$.hasNext()) {
                Object resource = i$.next();
                View child = holder.views.get(i2);
                if (child instanceof TextView) {
                    ((TextView) child).setText((String) resource);
                    ((TextView) child).setTextColor(menuItem.valid ? this.ELEMENT_TEXT_COLOR : this.ELEMENT_TEXT_INVALID_COLOR);
                } else if (child instanceof ImageView) {
                    ((ImageView) child).setImageDrawable((Drawable) resource);
                    ((ImageView) child).setAlpha(menuItem.valid ? 255 : 128);
                }
                i2++;
            }
            return view;
        }

        @Override // android.widget.BaseAdapter, android.widget.ListAdapter
        public boolean isEnabled(int position) {
            return getItem(position).valid;
        }
    }
}
