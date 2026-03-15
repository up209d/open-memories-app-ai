package com.sony.imaging.app.base.menu.layout;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.fw.AppRoot;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class SetMenuLayout extends DisplayMenuItemsMenuLayout {
    protected static final int ITEM_HEIGHT = 48;
    protected static final int ITEM_WIDTH = 473;
    public static final String MENU_ID = "ID_SETMENULAYOUT";
    private static final String TAG = SetMenuLayout.class.getSimpleName();
    private ItemListAdapter mAdapter;
    private ItemAlignedListView mItemListView;
    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() { // from class: com.sony.imaging.app.base.menu.layout.SetMenuLayout.1
        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            ArrayList<String> itemList = SetMenuLayout.this.mService.getSupportedItemList();
            String clickedItemId = itemList.get(arg2);
            SetMenuLayout.this.doItemClickProcessing(clickedItemId);
        }
    };
    private TextView mScreenTitleView;
    private RelativeLayout mSetMenuView;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void postSetValue() {
        openPreviousMenu();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Context context = getActivity().getApplicationContext();
        View createdView = obtainViewFromPool(R.layout.menu_set);
        this.mSetMenuView = (RelativeLayout) createdView;
        this.mItemListView = (ItemAlignedListView) createdView.findViewById(R.id.itemAlignedlistview);
        this.mService = new BaseMenuService(context);
        this.mScreenTitleView = (TextView) createdView.findViewById(R.id.menu_screen_title);
        this.mAdapter = new ItemListAdapter(context, R.layout.menu_set_adapter);
        return createdView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mSetMenuView = null;
        this.mItemListView = null;
        this.mService = null;
        this.mScreenTitleView = null;
        this.mAdapter = null;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        boolean ret = isFunctionGuideShown() ? false : false | this.mItemListView.dispatchKeyEvent(event);
        if (ret) {
            return 1;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        closeMenuLayout(new Bundle());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.mItemListView.setFocusable(false);
        ArrayList<String> menuItemIds = this.mService.getSupportedItemList();
        this.mService.updateValueItemsAvailable(menuItemIds);
        this.mAdapter.clear();
        int initPos = 0;
        int i = 0;
        String parentItemId = this.mService.getMenuItemId();
        String curValue = this.mService.getCurrentValue(parentItemId);
        Iterator i$ = menuItemIds.iterator();
        while (i$.hasNext()) {
            String itemId = i$.next();
            MenuItem item = new MenuItem();
            item.selected = false;
            item.icon = this.mService.getMenuItemDrawable(itemId);
            item.text = (String) this.mService.getMenuItemText(itemId);
            item.valid = this.mService.isMenuItemValid(itemId);
            this.mAdapter.add(item);
            if (itemId.equals(curValue)) {
                initPos = i;
                item.selected = true;
            }
            i++;
        }
        this.mItemListView.setAdapter((ListAdapter) this.mAdapter);
        int dividerHeight = this.mItemListView.getDivider().getIntrinsicHeight();
        this.mItemListView.setSelectionFromTop(initPos, (dividerHeight + ITEM_HEIGHT) * 2);
        this.mItemListView.setOnItemClickListener(this.mOnItemClickListener);
        String title = (String) this.mService.getMenuItemText(parentItemId);
        this.mScreenTitleView.setText(title);
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        this.mItemListView.setOnItemClickListener(null);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        boolean handled = this.mItemListView.dispatchKeyEvent(new KeyEvent(0L, 0L, 0, 0, 0, 0, 0, AppRoot.USER_KEYCODE.DOWN));
        return handled ? 1 : 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        boolean handled = this.mItemListView.dispatchKeyEvent(new KeyEvent(0L, 0L, 0, 0, 0, 0, 0, AppRoot.USER_KEYCODE.UP));
        return handled ? 1 : 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        int pos = this.mItemListView.getSelectedItemPosition();
        ArrayList<String> itemIdList = this.mService.getSupportedItemList();
        if (pos >= 0 && pos < itemIdList.size()) {
            String itemId = itemIdList.get(pos);
            guideResources.add(this.mService.getMenuItemText(itemId));
            guideResources.add(this.mService.getMenuItemGuideText(itemId));
            guideResources.add(Boolean.valueOf(this.mService.isMenuItemValid(itemId)));
        }
    }

    /* loaded from: classes.dex */
    protected class MenuItem {
        public Drawable icon;
        public boolean selected;
        public String text;
        public boolean valid;

        protected MenuItem() {
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class ItemListAdapter extends ArrayAdapter<MenuItem> implements ListAdapter {
        private static final String HEDDER_ICON_IMAGEVIEW = "hedder_icon_imageview";
        private static final String ICON_IMAGEVIEW = "icon_imageview";
        private static final String ITEM_TEXT = "item_textview";
        private int ITEM_TEXT_COLOR;
        private int ITEM_TEXT_INVALID_COLOR;
        LayoutInflater mInflater;
        private int mLayoutResourceId;

        public ItemListAdapter(Context context, int ResId) {
            super(context, ResId);
            this.mLayoutResourceId = ResId;
            this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
            Resources r = SetMenuLayout.this.getResources();
            this.ITEM_TEXT_COLOR = r.getColor(R.color.RESID_FONTSTYLE_U_CMN);
            this.ITEM_TEXT_INVALID_COLOR = r.getColor(R.color.RESID_FONTSTYLE_U_MENU_ITEM_DISABLE);
        }

        /* loaded from: classes.dex */
        private class ViewHolder {
            ImageView hedderIconImage;
            ImageView iconImage;
            TextView itemText;

            private ViewHolder() {
            }
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            View view = convertView;
            if (view == null) {
                view = this.mInflater.inflate(this.mLayoutResourceId, (ViewGroup) null);
                view.setLayoutParams(new AbsListView.LayoutParams(SetMenuLayout.ITEM_WIDTH, SetMenuLayout.ITEM_HEIGHT));
                holder = new ViewHolder();
                holder.hedderIconImage = (ImageView) view.findViewWithTag(HEDDER_ICON_IMAGEVIEW);
                holder.iconImage = (ImageView) view.findViewWithTag(ICON_IMAGEVIEW);
                holder.itemText = (TextView) view.findViewWithTag(ITEM_TEXT);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            boolean existSelected = false;
            int count = getCount();
            int i = 0;
            while (true) {
                if (i >= count) {
                    break;
                }
                MenuItem item = getItem(i);
                if (!item.selected) {
                    i++;
                } else {
                    existSelected = true;
                    break;
                }
            }
            MenuItem menuItem = getItem(position);
            if (!existSelected || holder.hedderIconImage == null) {
                Log.w(SetMenuLayout.TAG, "Hedder icon image view not found. Set to invisible.");
                holder.hedderIconImage.setVisibility(8);
            } else {
                int id = menuItem.selected ? 17306712 : 17307388;
                holder.hedderIconImage.setImageResource(id);
                Log.d(SetMenuLayout.TAG, SetMenuLayout.TAG);
            }
            if (holder.iconImage != null) {
                if (menuItem.icon != null) {
                    holder.iconImage.setVisibility(0);
                    holder.iconImage.setImageDrawable(menuItem.icon);
                } else {
                    holder.iconImage.setVisibility(8);
                }
            }
            if (holder.itemText != null) {
                holder.itemText.setText(menuItem.text);
                if (menuItem.valid) {
                    holder.itemText.setTextColor(this.ITEM_TEXT_COLOR);
                } else {
                    holder.itemText.setTextColor(this.ITEM_TEXT_INVALID_COLOR);
                }
            }
            return view;
        }

        @Override // android.widget.BaseAdapter, android.widget.ListAdapter
        public boolean isEnabled(int position) {
            return getItem(position).valid;
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected boolean isDisplayedAlone() {
        return false;
    }
}
