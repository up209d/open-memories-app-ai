package com.sony.imaging.app.digitalfilter.menu.layout;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.menu.layout.ItemAlignedListView;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.caution.GFInfo;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFEEAreaController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFShootingOrderController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomKeyMgr;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class GFFilterShootingMenuLayout extends DisplayMenuItemsMenuLayout {
    public static final String MENU_ID = "ID_GFFILTERSHOOTINGMENULAYOUT";
    private static ItemListAdapter mItemListAdapter;
    private static BaseMenuService mItemListService;
    protected ItemAlignedListView mItemListView;
    private AdapterView.OnItemClickListener mOnItemClickListenerForItemListView = new AdapterView.OnItemClickListener() { // from class: com.sony.imaging.app.digitalfilter.menu.layout.GFFilterShootingMenuLayout.1
        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            ArrayList<String> menuItemList = GFFilterShootingMenuLayout.this.getDisplayedItemList();
            if (arg2 >= 0 && arg2 < menuItemList.size()) {
                String clickedItemId = menuItemList.get(arg2);
                GFFilterShootingMenuLayout.this.doItemClickProcessing(clickedItemId);
            }
        }
    };
    private static final String TAG = GFFilterShootingMenuLayout.class.getSimpleName();
    private static String mPrevMenu = null;
    private static boolean isLayerSetting = false;

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View createdView = obtainViewFromPool(R.layout.menu_shooting);
        this.mItemListView = (ItemAlignedListView) createdView.findViewById(R.id.itemlistview);
        mItemListAdapter = new ItemListAdapter(getActivity(), R.layout.menu_shooting_adapter);
        Context context = getActivity().getApplicationContext();
        this.mService = new BaseMenuService(context);
        mItemListService = new BaseMenuService(context);
        isLayerSetting = GFCommonUtil.getInstance().isLayerSetting();
        return createdView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected boolean isAELCancel() {
        return false;
    }

    public static String getPrevMenu() {
        return mPrevMenu;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        return -1;
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
        this.mItemListView.dispatchKeyEvent(new KeyEvent(0L, 0L, 0, 0, 0, 0, 0, AppRoot.USER_KEYCODE.DOWN));
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        this.mItemListView.dispatchKeyEvent(new KeyEvent(0L, 0L, 0, 0, 0, 0, 0, 103));
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        if (!isLayerSetting) {
            return super.pushedFnKey();
        }
        CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_INVALID_FUNCTION_IN_APPSETTING);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        openPreviousMenu();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        Bundle bundle = this.data;
        if (bundle != null) {
            mPrevMenu = bundle.getString("PrevMenu");
        }
        this.mItemListView.setFocusable(false);
        int listPos = BackUpUtil.getInstance().getPreferenceInt(MENU_ID, 0);
        if (listPos < 0) {
            listPos = 0;
        }
        ArrayList<String> listItemIds = getDisplayedItemList();
        updateListViewItems(listItemIds);
        this.mItemListView.setSelection(listPos);
        this.mItemListView.setOnItemClickListener(this.mOnItemClickListenerForItemListView);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ArrayList<String> getDisplayedItemList() {
        ArrayList<String> listItemIds = new ArrayList<>();
        listItemIds.add(GFShootingOrderController.ORDER);
        listItemIds.add(GFEEAreaController.EE);
        return listItemIds;
    }

    protected void updateListViewItems(ArrayList<String> itemIds) {
        int listPos = this.mItemListView.getSelectedItemPosition();
        mItemListAdapter.clear();
        mItemListService.updateSettingItemsAvailable(itemIds);
        Iterator i$ = itemIds.iterator();
        while (i$.hasNext()) {
            String itemId = i$.next();
            MenuItem item = new MenuItem();
            item.element = (String) mItemListService.getMenuItemText(itemId);
            String curItemValueId = mItemListService.getCurrentValue(itemId);
            if (curItemValueId.equals(GFShootingOrderController.ORDER_123)) {
                item.value = getString(R.string.STRID_FUNC_DF_SHOOTING_ORDER_123_S);
            } else if (curItemValueId.equals(GFShootingOrderController.ORDER_132)) {
                item.value = getString(R.string.STRID_FUNC_DF_SHOOTING_ORDER_132_S);
            } else if (curItemValueId.equals(GFShootingOrderController.ORDER_213)) {
                item.value = getString(R.string.STRID_FUNC_DF_SHOOTING_ORDER_213_S);
            } else if (curItemValueId.equals(GFShootingOrderController.ORDER_231)) {
                item.value = getString(R.string.STRID_FUNC_DF_SHOOTING_ORDER_231_S);
            } else if (curItemValueId.equals(GFShootingOrderController.ORDER_312)) {
                item.value = getString(R.string.STRID_FUNC_DF_SHOOTING_ORDER_312_S);
            } else if (curItemValueId.equals(GFShootingOrderController.ORDER_321)) {
                item.value = getString(R.string.STRID_FUNC_DF_SHOOTING_ORDER_321_S);
            } else {
                item.value = (String) mItemListService.getMenuItemText(curItemValueId);
            }
            item.valid = true;
            mItemListAdapter.add(item);
        }
        this.mItemListView.setAdapter((ListAdapter) mItemListAdapter);
        this.mItemListView.setSelection(listPos);
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.fw.Layout
    public void onReopened() {
        super.onReopened();
        ArrayList<String> listItemIds = getDisplayedItemList();
        updateListViewItems(listItemIds);
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mService = null;
        mItemListService = null;
        this.mItemListView = null;
        mItemListAdapter = null;
        mPrevMenu = null;
        System.gc();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        int listPos = this.mItemListView.getSelectedItemPosition();
        BackUpUtil.getInstance().setPreference(MENU_ID, Integer.valueOf(listPos));
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        guideResources.clear();
        int listPos = this.mItemListView.getSelectedItemPosition();
        String itemId = getDisplayedItemList().get(listPos);
        String guideTitleID = (String) mItemListService.getMenuItemText(itemId);
        String guideDefi = (String) this.mService.getMenuItemGuideText(itemId);
        guideResources.add(guideTitleID);
        guideResources.add(guideDefi);
        guideResources.add(true);
    }

    private void openOptionMenu() {
        openNextMenu("FilterShootingSet", MENU_ID);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int ret;
        if (!GFCommonUtil.getInstance().isLayerSetting()) {
            if (GFConstants.Setting15LayerCustomizableFunction.contains(func)) {
                ret = super.onConvertedKeyDown(event, func);
            } else if (!CustomizableFunction.Unchanged.equals(func)) {
                ret = -1;
            } else {
                ret = super.onConvertedKeyDown(event, func);
            }
        } else {
            int code = event.getScanCode();
            if (GFConstants.Setting15LayerCustomizableFunction.contains(func)) {
                ret = super.onConvertedKeyDown(event, func);
            } else {
                if (GFCommonUtil.getInstance().isTriggerMfAssist(code)) {
                    return 0;
                }
                ret = -1;
            }
        }
        return ret;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyUp(KeyEvent event, IKeyFunction func) {
        if (CustomizableFunction.Unchanged.equals(func)) {
            return super.onConvertedKeyUp(event, func);
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        boolean ret = false;
        int code = event.getScanCode();
        ICustomKey key = CustomKeyMgr.getInstance().get(code);
        IKeyFunction func = key.getAssigned("Menu");
        boolean isGuide = func.equals(CustomizableFunction.Guide);
        if (isGuide && code != 595) {
            return super.pushedGuideFuncKey();
        }
        boolean isMfAssist = func.equals(CustomizableFunction.MfAssist);
        if (isMfAssist && code == 595) {
            return super.onKeyDown(keyCode, event);
        }
        switch (code) {
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                if (isFunctionGuideShown() || mPrevMenu == null || !mPrevMenu.equals("FilterSet")) {
                    ret = false | this.mItemListView.dispatchKeyEvent(event);
                    break;
                } else {
                    openPreviousMenu();
                    ret = true;
                    break;
                }
                break;
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_TELE /* 610 */:
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_WIDE /* 611 */:
            case 645:
            case AppRoot.USER_KEYCODE.LENS_FOCUS_HOLD /* 653 */:
                ret = true;
                break;
            default:
                if (!isFunctionGuideShown()) {
                    ret = false | this.mItemListView.dispatchKeyEvent(event);
                    break;
                }
                break;
        }
        if (ret) {
            return 1;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        if (code == 653) {
            return -1;
        }
        return super.onKeyUp(keyCode, event);
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
            Resources r = GFFilterShootingMenuLayout.this.getResources();
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
