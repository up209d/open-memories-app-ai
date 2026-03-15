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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.base.BaseBackUpKey;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.startrails.menu.controller.ThemeSelectionController;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class PageMenuLayout extends DisplayMenuItemsMenuLayout {
    public static final String MENU_ID = "ID_PAGEMENULAYOUT";
    private static final String TAG = PageMenuLayout.class.getSimpleName();
    private CameraNotificationListener mCameraNotificationListener;
    private ItemListAdapter mItemListAdapter;
    private BaseMenuService mItemListService;
    protected ItemAlignedListView mItemListView;
    private DisplayMenuItemsMenuLayout.MenuUpdater mMenuUpdater;
    private PageListAdapter mPageListAdapter;
    protected HorizonalListView mPageListView;
    protected RelativeLayout mPageMenuView;
    AdapterView.OnItemSelectedListener mOnItemSelectedListenerForPageListView = new AdapterView.OnItemSelectedListener() { // from class: com.sony.imaging.app.base.menu.layout.PageMenuLayout.1
        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            ArrayList<String> pageList = PageMenuLayout.this.mService.getMenuItemList();
            PageMenuLayout.this.mItemListService.setMenuItemId(pageList.get(arg2));
            ArrayList<String> listItemIds = PageMenuLayout.this.getDisplayedItemList();
            PageMenuLayout.this.updateListViewItems(listItemIds);
            PageMenuLayout.this.mItemListView.setSelection(0);
        }

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    };
    AdapterView.OnItemClickListener mOnItemClickListenerForItemListView = new AdapterView.OnItemClickListener() { // from class: com.sony.imaging.app.base.menu.layout.PageMenuLayout.2
        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            ArrayList<String> menuItemList = PageMenuLayout.this.getDisplayedItemList();
            if (arg2 >= 0 && arg2 < menuItemList.size()) {
                String clickedItemId = menuItemList.get(arg2);
                if (PageMenuLayout.this.mItemListService.isMenuItemValid(clickedItemId)) {
                    PageMenuLayout.this.doItemClickProcessing(clickedItemId);
                } else {
                    PageMenuLayout.this.requestCautionTrigger(clickedItemId);
                }
            }
        }
    };

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected DisplayMenuItemsMenuLayout.MenuUpdater getMenuUpdater() {
        if (this.mMenuUpdater == null) {
            this.mMenuUpdater = new DisplayMenuItemsMenuLayout.MenuUpdater(new Handler()) { // from class: com.sony.imaging.app.base.menu.layout.PageMenuLayout.3
                @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout.MenuUpdater, java.lang.Runnable
                public void run() {
                    PageMenuLayout.this.updateListViewItems(PageMenuLayout.this.getDisplayedItemList());
                }
            };
        }
        return this.mMenuUpdater;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Context context = getActivity().getApplicationContext();
        View createdView = obtainViewFromPool(getLayoutID());
        this.mPageMenuView = (RelativeLayout) createdView;
        this.mItemListView = (ItemAlignedListView) createdView.findViewById(R.id.itemlistview);
        this.mPageListView = (HorizonalListView) createdView.findViewById(R.id.pagelistview);
        this.mService = new BaseMenuService(context);
        this.mItemListService = new BaseMenuService(context);
        this.mPageListAdapter = new PageListAdapter(getActivity(), -1);
        this.mItemListAdapter = new ItemListAdapter(getActivity(), R.layout.menu_page_adapter);
        return createdView;
    }

    protected int getLayoutID() {
        return R.layout.menu_page;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        boolean ret = false;
        if (!isFunctionGuideShown()) {
            boolean ret2 = false | this.mItemListView.dispatchKeyEvent(event);
            ret = ret2 | this.mPageListView.dispatchKeyEvent(event);
        }
        if (ret) {
            return 1;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        this.mItemListView.dispatchKeyEvent(new KeyEvent(0L, 0L, 0, 0, 0, 0, 0, AppRoot.USER_KEYCODE.DOWN));
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        this.mItemListView.dispatchKeyEvent(new KeyEvent(0L, 0L, 0, 0, 0, 0, 0, 103));
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        this.mPageListView.dispatchKeyEvent(new KeyEvent(0L, 0L, 0, 0, 0, 0, 0, AppRoot.USER_KEYCODE.RIGHT));
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        this.mPageListView.dispatchKeyEvent(new KeyEvent(0L, 0L, 0, 0, 0, 0, 0, AppRoot.USER_KEYCODE.LEFT));
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.mPageListView.setFocusable(false);
        this.mItemListView.setFocusable(false);
        int pagePos = BackUpUtil.getInstance().getPreferenceInt(BaseBackUpKey.ID_MENU_POSITION_GLOBAL_MENU_PAGE, 0);
        int listPos = BackUpUtil.getInstance().getPreferenceInt(BaseBackUpKey.ID_MENU_POSITION_GLOBAL_MENU_LIST, 0);
        if (pagePos < 0) {
            pagePos = 0;
        }
        if (listPos < 0) {
            listPos = 0;
        }
        ArrayList<String> menuItemIds = this.mService.getMenuItemList();
        updatePageViewItems(menuItemIds);
        this.mPageListView.setSelection(pagePos);
        this.mPageListView.setOnItemSelectedListener(this.mOnItemSelectedListenerForPageListView);
        ArrayList<String> pageItemIds = this.mService.getMenuItemList();
        this.mItemListService.setMenuItemId(pageItemIds.get(pagePos));
        this.mCameraNotificationListener = new CameraNotificationListener();
        updateListViewItems(getDisplayedItemList());
        this.mItemListView.setSelection(listPos);
        this.mItemListView.setOnItemClickListener(this.mOnItemClickListenerForItemListView);
    }

    protected ArrayList<String> getDisplayedItemList() {
        ArrayList<String> itemIds = this.mItemListService.getSupportedItemList();
        ArrayList<String> copyList = new ArrayList<>(itemIds);
        Iterator i$ = copyList.iterator();
        while (i$.hasNext()) {
            String itemId = i$.next();
            boolean isDisplayed = this.mItemListService.getBoolean(itemId, "DisplayedInMenu", true);
            if (!isDisplayed) {
                itemIds.remove(itemId);
            }
        }
        return itemIds;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updatePageViewItems(ArrayList<String> itemIds) {
        int pagePos = this.mPageListView.getSelectedItemPosition();
        this.mPageListAdapter.clear();
        Iterator i$ = itemIds.iterator();
        while (i$.hasNext()) {
            String itemId = i$.next();
            PageItem item = new PageItem();
            item.unselected = this.mService.getMenuItemDrawable(itemId);
            item.selected = this.mService.getMenuItemSelectedDrawable(itemId);
            this.mPageListAdapter.add(item);
        }
        this.mPageListView.setAdapter((ListAdapter) this.mPageListAdapter);
        this.mPageListView.setSelection(pagePos);
    }

    protected void updateListViewItems(ArrayList<String> itemIds) {
        String valueText;
        int listPos = this.mItemListView.getSelectedItemPosition();
        this.mItemListAdapter.clear();
        this.mItemListService.updateSettingItemsAvailable(itemIds);
        CameraNotificationManager camNotifier = CameraNotificationManager.getInstance();
        if (camNotifier != null && this.mCameraNotificationListener != null && this.mCameraNotificationListener.getTags() != null) {
            camNotifier.removeNotificationListener(this.mCameraNotificationListener);
        }
        ArrayList<String> notifyTags = new ArrayList<>();
        Iterator i$ = itemIds.iterator();
        while (i$.hasNext()) {
            String itemId = i$.next();
            MenuItem item = new MenuItem();
            item.element = (String) this.mItemListService.getMenuItemText(itemId);
            item.value = null;
            if (!"ApplicationSettings".equals(itemId) && !ThemeSelectionController.APP_TOP.equals(itemId)) {
                String curItemValueId = itemId;
                do {
                    curItemValueId = this.mItemListService.getCurrentValue(curItemValueId);
                    valueText = (String) this.mItemListService.getMenuItemText(curItemValueId);
                    if (valueText != null) {
                        break;
                    }
                } while (this.mItemListService.hasSubArray(curItemValueId));
                if (valueText == null) {
                    valueText = curItemValueId;
                }
                item.value = valueText;
            }
            item.valid = this.mItemListService.isMenuItemValid(itemId);
            this.mItemListAdapter.add(item);
            String notifyTag = this.mItemListService.getMenuItemNotifyTag(itemId);
            if (notifyTag != null) {
                notifyTags.add(notifyTag);
            }
        }
        if (camNotifier != null && !notifyTags.isEmpty()) {
            this.mCameraNotificationListener.setTag((String[]) notifyTags.toArray(new String[0]));
            camNotifier.setNotificationListener(this.mCameraNotificationListener);
        } else {
            this.mCameraNotificationListener.setTag(null);
        }
        this.mItemListView.setAdapter((ListAdapter) this.mItemListAdapter);
        this.mItemListView.setSelection(listPos);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class CameraNotificationListener implements NotificationListener {
        private String[] mTags;

        private CameraNotificationListener() {
        }

        void setTag(String[] tags) {
            this.mTags = tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.mTags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            PageMenuLayout.this.getMenuUpdater().restartMenuUpdater();
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected void onDeviceStatusChanged() {
        int pagePos = this.mPageListView.getSelectedItemPosition();
        ArrayList<String> pageItemIds = this.mService.getMenuItemList();
        this.mItemListService.setMenuItemId(pageItemIds.get(pagePos));
        updateListViewItems(getDisplayedItemList());
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.fw.Layout
    public void onReopened() {
        super.onReopened();
        int pagePos = this.mPageListView.getSelectedItemPosition();
        ArrayList<String> pageItemIds = this.mService.getMenuItemList();
        updatePageViewItems(pageItemIds);
        this.mItemListService.setMenuItemId(pageItemIds.get(pagePos));
        updateListViewItems(getDisplayedItemList());
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mPageMenuView = null;
        this.mItemListView = null;
        this.mPageListView = null;
        this.mService = null;
        this.mItemListService = null;
        this.mPageListAdapter = null;
        this.mItemListAdapter = null;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        int pagePos = this.mPageListView.getSelectedItemPosition();
        int listPos = this.mItemListView.getSelectedItemPosition();
        BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_MENU_POSITION_GLOBAL_MENU_PAGE, Integer.valueOf(pagePos));
        BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_MENU_POSITION_GLOBAL_MENU_LIST, Integer.valueOf(listPos));
        if (this.mCameraNotificationListener.getTags() != null) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mCameraNotificationListener);
        }
        this.mCameraNotificationListener = null;
        if (this.mMenuUpdater != null) {
            this.mMenuUpdater.cancelMenuUpdater();
        }
        this.mMenuUpdater = null;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        closeMenuLayout(null);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected void onDigitalZoomOnOff(boolean onoff) {
        getMenuUpdater().restartMenuUpdater();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    public void onMediaRemainingChanged() {
        getMenuUpdater().restartMenuUpdater();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected void onFocusModeChanged() {
        getMenuUpdater().restartMenuUpdater();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected void onStreamWriterStatusChanged() {
        getMenuUpdater().restartMenuUpdater();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        int pos = this.mItemListView.getSelectedItemPosition();
        ArrayList<String> itemIdList = getDisplayedItemList();
        if (pos >= 0 && pos < itemIdList.size()) {
            String itemId = itemIdList.get(pos);
            guideResources.add(this.mItemListService.getMenuItemText(itemId));
            guideResources.add(this.mItemListService.getMenuItemGuideText(itemId));
            guideResources.add(Boolean.valueOf(this.mItemListService.isMenuItemValid(itemId)));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MenuItem {
        String element;
        boolean valid;
        String value;

        private MenuItem() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class PageItem {
        Drawable selected;
        Drawable unselected;

        private PageItem() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class PageListAdapter extends ArrayAdapter<PageItem> {
        private Context mContext;

        public PageListAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
            this.mContext = context;
        }

        /* loaded from: classes.dex */
        private class ViewHolder {
            SelectableView selectableView;

            private ViewHolder() {
            }
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            View view = convertView;
            if (view == null) {
                view = new SelectableView(this.mContext);
                view.setLayoutParams(new AbsListView.LayoutParams(36, 44));
                holder = new ViewHolder();
                holder.selectableView = (SelectableView) view;
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            PageItem pageItem = getItem(position);
            if (holder != null) {
                holder.selectableView.setSelectedDrawable(pageItem.selected);
                holder.selectableView.setUnSelectedDrawable(pageItem.unselected);
            }
            return view;
        }
    }

    /* loaded from: classes.dex */
    public class ItemListAdapter extends ArrayAdapter<MenuItem> implements ListAdapter {
        private static final String ELEMENT_TEXT = "element_text";
        private static final String VALUE_TEXT = "value_text";
        private int ELEMENT_TEXT_COLOR;
        private int ELEMENT_TEXT_INVALID_COLOR;
        private int VALUE_TEXT_COLOR;
        private int VALUE_TEXT_INVALID_COLOR;
        LayoutInflater mInflater;
        private int mLayoutResourceId;

        public ItemListAdapter(Context context, int ResId) {
            super(context, ResId);
            this.mLayoutResourceId = ResId;
            this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
            Resources r = PageMenuLayout.this.getResources();
            this.ELEMENT_TEXT_COLOR = r.getColor(R.color.RESID_FONTSTYLE_U_CMN);
            this.ELEMENT_TEXT_INVALID_COLOR = r.getColor(R.color.RESID_FONTSTYLE_U_MENU_ITEM_DISABLE);
            this.VALUE_TEXT_COLOR = r.getColor(R.color.RESID_FONTSTYLE_U_MENU_ITEM_VALUE);
            this.VALUE_TEXT_INVALID_COLOR = r.getColor(R.color.RESID_FONTSTYLE_U_MENU_ITEM_DISABLE);
        }

        /* loaded from: classes.dex */
        private class ViewHolder {
            TextView elementText;
            TextView valueText;

            private ViewHolder() {
            }
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            View view = convertView;
            if (view == null) {
                view = this.mInflater.inflate(this.mLayoutResourceId, (ViewGroup) null);
                view.setLayoutParams(new AbsListView.LayoutParams(AppRoot.USER_KEYCODE.SMART_TELECON, 48));
                holder = new ViewHolder();
                holder.elementText = (TextView) view.findViewWithTag(ELEMENT_TEXT);
                holder.valueText = (TextView) view.findViewWithTag(VALUE_TEXT);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            MenuItem menuItem = getItem(position);
            if (holder.elementText != null) {
                holder.elementText.setText(menuItem.element);
                if (menuItem.valid) {
                    holder.elementText.setTextColor(this.ELEMENT_TEXT_COLOR);
                } else {
                    holder.elementText.setTextColor(this.ELEMENT_TEXT_INVALID_COLOR);
                }
            }
            if (holder.valueText != null) {
                holder.valueText.setText(menuItem.value);
                if (menuItem.valid) {
                    holder.valueText.setTextColor(this.VALUE_TEXT_COLOR);
                } else {
                    holder.valueText.setTextColor(this.VALUE_TEXT_INVALID_COLOR);
                }
            }
            return view;
        }

        @Override // android.widget.BaseAdapter, android.widget.ListAdapter
        public boolean isEnabled(int position) {
            return getItem(position).valid;
        }
    }
}
