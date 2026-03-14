package com.sony.imaging.app.digitalfilter.menu.layout;

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
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.menu.layout.ItemAlignedListView;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.caution.GFInfo;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.common.GFKikiLogUtil;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFEEAreaController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFWhiteBalanceController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFWhiteBalanceLimitController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.RunStatus;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class GFWhiteBalanceLimitSetMenuLayout extends DisplayMenuItemsMenuLayout {
    protected static final int ITEM_HEIGHT = 48;
    protected static final int ITEM_WIDTH = 473;
    public static final String MENU_ID = "ID_GFWHITEBALANCELIMITSETMENULAYOUT";
    private static ItemListAdapter mAdapter;
    private static ItemAlignedListView mItemListView;
    private static TextView mScreenTitleView;
    private static final String TAG = GFWhiteBalanceLimitSetMenuLayout.class.getSimpleName();
    private static String mPrevItemid = null;
    private static boolean isCanceled = false;
    private static AdapterView.OnItemClickListener mOnItemClickListener = null;

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
        mItemListView = (ItemAlignedListView) createdView.findViewById(R.id.itemAlignedlistview);
        this.mService = new BaseMenuService(context);
        mScreenTitleView = (TextView) createdView.findViewById(R.id.menu_screen_title);
        mAdapter = new ItemListAdapter(context, R.layout.menu_set_adapter);
        isCanceled = false;
        return createdView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        mItemListView = null;
        this.mService = null;
        mScreenTitleView = null;
        mAdapter = null;
        mPrevItemid = null;
        mOnItemClickListener = null;
        super.onDestroyView();
        System.gc();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        boolean ret = isFunctionGuideShown() ? false : false | mItemListView.dispatchKeyEvent(event);
        if (ret) {
            return 1;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        if (!GFCommonUtil.getInstance().isLayerSetting()) {
            return super.pushedFnKey();
        }
        CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_INVALID_FUNCTION_IN_APPSETTING);
        return -1;
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
        isCanceled = true;
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        mItemListView.setFocusable(false);
        ArrayList<String> menuItemIds = this.mService.getSupportedItemList();
        this.mService.updateValueItemsAvailable(menuItemIds);
        mAdapter.clear();
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
            if (GFWhiteBalanceController.getInstance().isSupportedABGM()) {
                if (itemId.equals(GFWhiteBalanceLimitController.CTEMP_AWB)) {
                    item.text = getResources().getString(R.string.STRID_FUNC_SKYND_WB_CTEMP_AWB);
                } else if (itemId.equals(GFWhiteBalanceLimitController.CTEMP)) {
                    item.text = getResources().getString(R.string.STRID_FUNC_SKYND_WB_CTEMP);
                }
            }
            item.valid = this.mService.isMenuItemValid(itemId);
            mAdapter.add(item);
            if (itemId.equals(curValue)) {
                initPos = i;
                item.selected = true;
                mPrevItemid = itemId;
            }
            i++;
        }
        mItemListView.setAdapter((ListAdapter) mAdapter);
        int dividerHeight = mItemListView.getDivider().getIntrinsicHeight();
        mItemListView.setSelectionFromTop(initPos, (dividerHeight + ITEM_HEIGHT) * 2);
        mOnItemClickListener = new AdapterView.OnItemClickListener() { // from class: com.sony.imaging.app.digitalfilter.menu.layout.GFWhiteBalanceLimitSetMenuLayout.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                ArrayList<String> itemList = GFWhiteBalanceLimitSetMenuLayout.this.mService.getSupportedItemList();
                String clickedItemId = itemList.get(arg2);
                GFWhiteBalanceLimitSetMenuLayout.this.doItemClickProcessing(clickedItemId);
            }
        };
        mItemListView.setOnItemClickListener(mOnItemClickListener);
        String title = (String) this.mService.getMenuItemText(parentItemId);
        mScreenTitleView.setText(title);
        GFKikiLogUtil.getInstance().countMenuSettings(parentItemId);
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        mItemListView.setOnItemClickListener(null);
        int runStatus = RunStatus.getStatus();
        if (2 != runStatus && !isCanceled) {
            CameraNotificationManager.getInstance().requestNotify(GFConstants.WB_LIMIT_CHANGED);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected boolean isAELCancel() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void doItemClickProcessing(String itemid) {
        if (!mPrevItemid.equals(itemid)) {
            if (GFWhiteBalanceLimitController.CTEMP_AWB.equals(itemid)) {
                GFBackUpKey.getInstance().limitToAWB();
                setWhiteBalance();
            } else if (GFWhiteBalanceLimitController.CTEMP.equals(itemid)) {
                GFBackUpKey.getInstance().limitToCTemp();
                setWhiteBalance();
            }
        }
        super.doItemClickProcessing(itemid);
    }

    private void setWhiteBalance() {
        int layer = 0;
        if (GFCommonUtil.getInstance().isLayerSetting()) {
            if (GFCommonUtil.getInstance().isSky()) {
                layer = 1;
            } else if (GFCommonUtil.getInstance().isLayer3()) {
                layer = 2;
            }
        } else if (GFEEAreaController.getInstance().isSky()) {
            layer = 1;
        } else if (GFEEAreaController.getInstance().isLayer3()) {
            layer = 2;
        }
        GFCommonUtil.getInstance().setWhiteBalance(layer);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        int pos = mItemListView.getSelectedItemPosition();
        ArrayList<String> itemIdList = this.mService.getSupportedItemList();
        if (pos >= 0 && pos < itemIdList.size()) {
            String itemId = itemIdList.get(pos);
            if (itemId.equals(GFWhiteBalanceLimitController.CTEMP_AWB)) {
                if (GFWhiteBalanceController.getInstance().isSupportedABGM()) {
                    guideResources.add(getText(R.string.STRID_FUNC_SKYND_WB_CTEMP_AWB));
                    guideResources.add(getText(R.string.STRID_FUNC_SKYND_WB_CTEMP_AWB_GUIDE));
                } else {
                    guideResources.add(getText(R.string.STRID_FUNC_SKYND_WB_CTEMP_AWB_S));
                    guideResources.add(getText(R.string.STRID_FUNC_DF_WB_CTEMP_AWB_GUIDE_S));
                }
            } else if (itemId.equals(GFWhiteBalanceLimitController.CTEMP)) {
                if (GFWhiteBalanceController.getInstance().isSupportedABGM()) {
                    guideResources.add(getText(R.string.STRID_FUNC_SKYND_WB_CTEMP));
                    guideResources.add(getText(R.string.STRID_FUNC_SKYND_WB_CTEMP_GUIDE));
                } else {
                    guideResources.add(getText(R.string.STRID_FUNC_SKYND_WB_CTEMP_S));
                    guideResources.add(getText(R.string.STRID_FUNC_DF_WB_CTEMP_GUIDE_S));
                }
            } else {
                guideResources.add(this.mService.getMenuItemText(itemId));
                guideResources.add(this.mService.getMenuItemGuideText(itemId));
            }
            guideResources.add(Boolean.valueOf(this.mService.isMenuItemValid(itemId)));
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        boolean handled = mItemListView.dispatchKeyEvent(new KeyEvent(0L, 0L, 0, 0, 0, 0, 0, AppRoot.USER_KEYCODE.DOWN));
        return handled ? 1 : 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        boolean handled = mItemListView.dispatchKeyEvent(new KeyEvent(0L, 0L, 0, 0, 0, 0, 0, 103));
        return handled ? 1 : 0;
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
            Resources r = GFWhiteBalanceLimitSetMenuLayout.this.getResources();
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
                view.setLayoutParams(new AbsListView.LayoutParams(GFWhiteBalanceLimitSetMenuLayout.ITEM_WIDTH, GFWhiteBalanceLimitSetMenuLayout.ITEM_HEIGHT));
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
                Log.w(GFWhiteBalanceLimitSetMenuLayout.TAG, "Hedder icon image view not found. Set to invisible.");
                holder.hedderIconImage.setVisibility(8);
            } else {
                int id = menuItem.selected ? 17306712 : 17307388;
                holder.hedderIconImage.setImageResource(id);
                Log.d(GFWhiteBalanceLimitSetMenuLayout.TAG, GFWhiteBalanceLimitSetMenuLayout.TAG);
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
