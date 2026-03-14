package com.sony.imaging.app.digitalfilter.menu.layout;

import android.content.Context;
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
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.sony.imaging.app.base.common.widget.BatteryIcon;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.layout.BeltView;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout;
import com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout;
import com.sony.imaging.app.base.menu.layout.SelectableView;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFFilterSetController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomKeyMgr;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class GFSetting15LayerFilterSetLayout extends Fn15LayerAbsLayout {
    protected static final int MAIN_BELT_HEIGHT = 60;
    protected static final int MAIN_BELT_WIDTH = 70;
    public static final String MENU_ID = "ID_GFSETTING15LAYERFILTERSETLAYOUT";
    protected static final int OPTION_BELT_HEIGHT = 35;
    protected static final int OPTION_BELT_WIDTH = 70;
    private static final String TAG = Fn15LayerMenuLayout.class.getSimpleName();
    protected static ImageView mBackGround;
    protected static List<String> mItemIdListForCancel;
    protected static TextView mItemTextView;
    protected static Fn15LayerAdapter mMainBeltAdapter;
    protected static BeltView mMainBeltView;
    protected static ImageView mOptionArrowLeft;
    protected static ImageView mOptionArrowRight;
    protected static Fn15LayerAdapter mOptionBeltAdapter;
    protected static BeltView mOptionBeltView;
    private static DisplayMenuItemsMenuLayout.MenuUpdater mOptionUpdater;
    private AdapterView.OnItemSelectedListener mOnItemSelectedListenerForMainBelt = new AdapterView.OnItemSelectedListener() { // from class: com.sony.imaging.app.digitalfilter.menu.layout.GFSetting15LayerFilterSetLayout.1
        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            AdapterItem item = (AdapterItem) arg0.getItemAtPosition(arg2);
            GFSetting15LayerFilterSetLayout.mItemTextView.setText(GFSetting15LayerFilterSetLayout.this.mService.getMenuItemText(item.itemId));
            GFSetting15LayerFilterSetLayout.mItemTextView.setVisibility(0);
            GFSetting15LayerFilterSetLayout.this.mUpdater.restartMenuUpdater();
            GFSetting15LayerFilterSetLayout.this.onMainBeltItemSelected((BeltView) arg0, arg1, arg2, arg3);
        }

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    };
    private AdapterView.OnItemSelectedListener mOnItemSelectedListenerForOptionBelt = new AdapterView.OnItemSelectedListener() { // from class: com.sony.imaging.app.digitalfilter.menu.layout.GFSetting15LayerFilterSetLayout.2
        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            AdapterItem item = (AdapterItem) arg0.getItemAtPosition(arg2);
            GFSetting15LayerFilterSetLayout.mItemTextView.setText(GFSetting15LayerFilterSetLayout.this.mService.getMenuItemText(item.itemId));
            GFSetting15LayerFilterSetLayout.mItemTextView.setVisibility(0);
            GFSetting15LayerFilterSetLayout.mOptionUpdater.restartMenuUpdater();
            AdapterItem mainItem = (AdapterItem) GFSetting15LayerFilterSetLayout.mMainBeltView.getSelectedItem();
            mainItem.itemId = item.itemId;
            mainItem.selected = GFSetting15LayerFilterSetLayout.this.mService.getMenuItemSelectedDrawable(item.itemId);
            mainItem.unselected = GFSetting15LayerFilterSetLayout.this.mService.getMenuItemDrawable(item.itemId);
            GFSetting15LayerFilterSetLayout.mMainBeltView.requestLayout();
            GFSetting15LayerFilterSetLayout.this.onOptionBeltItemSelected((BeltView) arg0, arg1, arg2, arg3);
        }

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    };
    private DisplayMenuItemsMenuLayout.MenuUpdater mUpdater = null;

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = obtainViewFromPool(getLayoutID());
        mMainBeltAdapter = new Fn15LayerAdapter(getActivity(), 0, 70, MAIN_BELT_HEIGHT);
        mOptionBeltAdapter = new Fn15LayerAdapter(getActivity(), 0, 70, 35);
        mMainBeltView = (BeltView) view.findViewById(R.id.main_belt_view);
        mOptionBeltView = (BeltView) view.findViewById(R.id.option_belt_view);
        mOptionArrowLeft = (ImageView) view.findViewById(R.id.left_arrow_option);
        mOptionArrowRight = (ImageView) view.findViewById(R.id.right_arrow_option);
        mItemTextView = (TextView) view.findViewById(R.id.item_text);
        mBackGround = (ImageView) view.findViewById(R.id.back_ground_image);
        this.mService = new BaseMenuService(getActivity());
        return view;
    }

    protected void onMainBeltItemSelected(BeltView arg0, View arg1, int arg2, long arg3) {
    }

    protected void onOptionBeltItemSelected(BeltView arg0, View arg1, int arg2, long arg3) {
    }

    protected int getLayoutID() {
        return R.layout.menu_fn15layer;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.fw.Layout
    public void onReopened() {
        super.onReopened();
    }

    protected void cancelValue() {
        for (String itemId : mItemIdListForCancel) {
            this.mService.execCurrentMenuItem(itemId);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        cancelValue();
        return super.pushedMenuKey();
    }

    public int getMinItemListSize() {
        return 2;
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        int action = this.data.getInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION);
        mItemIdListForCancel = new ArrayList();
        ArrayList<String> itemList = this.mService.getSupportedItemList(1);
        if (itemList == null || itemList.size() < getMinItemListSize()) {
            openPreviousMenu();
            return;
        }
        updateMainBelt(itemList);
        String curItemId = this.mService.getCurrentValue(this.mService.getMenuItemId());
        mItemIdListForCancel.add(curItemId);
        int pos = 0;
        Iterator i$ = itemList.iterator();
        while (i$.hasNext()) {
            String itemId = i$.next();
            if (itemId.equals(curItemId)) {
                break;
            } else {
                pos++;
            }
        }
        mMainBeltView.setSelection(pos, false);
        mMainBeltView.setOnItemSelectedListener(this.mOnItemSelectedListenerForMainBelt);
        mMainBeltView.setFocusable(false);
        if (this.mService.hasSupportedSubArray(curItemId)) {
            String optionCurrentItemId = this.mService.getCurrentValue(curItemId);
            mItemIdListForCancel.add(optionCurrentItemId);
            if (action == 3 || action == 4) {
                ArrayList<String> optionList = this.mService.getSupportedItemList(curItemId);
                int optionPos = 0;
                Iterator i$2 = optionList.iterator();
                while (i$2.hasNext()) {
                    String itemId2 = i$2.next();
                    if (itemId2.equals(optionCurrentItemId)) {
                        break;
                    } else {
                        optionPos++;
                    }
                }
                if (PictureEffectController.MODE_MINIATURE.equals(this.mService.getMenuItemValue(curItemId)) || DriveModeController.BURST.equals(this.mService.getMenuItemValue(curItemId))) {
                    this.mService.updateValueItemsAvailable(optionList, 1);
                }
                updateOptionBelt(optionList);
                mOptionBeltView.setSelection(optionPos, false);
                curItemId = optionCurrentItemId;
            } else {
                setOptionBeltViewVisibility(false);
            }
        } else {
            setOptionBeltViewVisibility(false);
        }
        mOptionBeltView.setOnItemSelectedListener(this.mOnItemSelectedListenerForOptionBelt);
        mOptionBeltView.setFocusable(false);
        mItemTextView.setText(this.mService.getMenuItemText(curItemId));
        mItemTextView.setVisibility(4);
        mOptionUpdater = new DisplayMenuItemsMenuLayout.MenuUpdater(new Handler()) { // from class: com.sony.imaging.app.digitalfilter.menu.layout.GFSetting15LayerFilterSetLayout.3
            @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout.MenuUpdater, java.lang.Runnable
            public void run() {
                if (GFSetting15LayerFilterSetLayout.mOptionBeltView != null) {
                    AdapterItem optionItem = (AdapterItem) GFSetting15LayerFilterSetLayout.mOptionBeltView.getSelectedItem();
                    if (optionItem.valid) {
                        GFSetting15LayerFilterSetLayout.this.mService.execCurrentMenuItem(optionItem.itemId, false);
                        GFSetting15LayerFilterSetLayout.this.postSetValue();
                    }
                }
            }
        };
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        if (this.mUpdater != null) {
            this.mUpdater.cancelMenuUpdater();
        }
        if (mOptionUpdater != null) {
            mOptionUpdater.cancelMenuUpdater();
        }
        if (mMainBeltView != null) {
            revertAplha(mMainBeltView);
            mMainBeltView.setOnItemSelectedListener(null);
            mMainBeltView.setAdapter((ListAdapter) null);
        }
        if (mOptionBeltView != null) {
            revertAplha(mOptionBeltView);
            mOptionBeltView.setOnItemSelectedListener(null);
            mOptionBeltView.setAdapter((ListAdapter) null);
        }
    }

    private void revertAplha(BeltView v) {
        ListAdapter adapter = v.getAdapter();
        if (adapter != null) {
            int count = adapter.getCount();
            for (int i = 0; i < count; i++) {
                AdapterItem item = (AdapterItem) adapter.getItem(i);
                if (item.selected != null) {
                    item.selected.setAlpha(BatteryIcon.BATTERY_STATUS_CHARGING);
                }
                if (item.unselected != null) {
                    item.unselected.setAlpha(BatteryIcon.BATTERY_STATUS_CHARGING);
                }
            }
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        mMainBeltAdapter = null;
        mOptionBeltAdapter = null;
        mMainBeltView = null;
        mOptionBeltView = null;
        mItemTextView = null;
        mBackGround = null;
        mOptionArrowLeft = null;
        mOptionArrowRight = null;
        mOptionUpdater = null;
        mItemIdListForCancel = null;
        super.onDestroyView();
    }

    protected void setOptionBeltViewVisibility(boolean visibility) {
        if (visibility) {
            mOptionBeltView.setVisibility(0);
            mOptionArrowLeft.setVisibility(0);
            mOptionArrowRight.setVisibility(0);
            mBackGround.setImageResource(17303934);
            return;
        }
        mOptionBeltView.setVisibility(8);
        mOptionArrowLeft.setVisibility(8);
        mOptionArrowRight.setVisibility(8);
        mBackGround.setImageResource(android.R.drawable.ic_bluetooth_share_icon);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        boolean handled = mMainBeltView.dispatchKeyEvent(new KeyEvent(0L, 0L, 0, 0, 0, 0, 0, AppRoot.USER_KEYCODE.RIGHT));
        if (handled) {
            setOptionBeltViewVisibility(false);
            mBackGround.setImageResource(android.R.drawable.ic_bluetooth_share_icon);
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        boolean handled = mMainBeltView.dispatchKeyEvent(new KeyEvent(0L, 0L, 0, 0, 0, 0, 0, AppRoot.USER_KEYCODE.LEFT));
        if (handled) {
            setOptionBeltViewVisibility(false);
            mBackGround.setImageResource(android.R.drawable.ic_bluetooth_share_icon);
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        mMainBeltView.dispatchKeyEvent(new KeyEvent(0L, 0L, 0, 0, 0, 0, 0, AppRoot.USER_KEYCODE.RIGHT));
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        mMainBeltView.dispatchKeyEvent(new KeyEvent(0L, 0L, 0, 0, 0, 0, 0, AppRoot.USER_KEYCODE.LEFT));
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialNext() {
        mMainBeltView.dispatchKeyEvent(new KeyEvent(0L, 0L, 0, 0, 0, 0, 0, AppRoot.USER_KEYCODE.RIGHT));
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialPrev() {
        mMainBeltView.dispatchKeyEvent(new KeyEvent(0L, 0L, 0, 0, 0, 0, 0, AppRoot.USER_KEYCODE.LEFT));
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        AdapterItem item = (AdapterItem) mMainBeltView.getSelectedItem();
        if (!item.valid) {
            requestCautionTrigger(item.mainContainerId);
            return 1;
        }
        if (mOptionBeltView.getVisibility() == 0) {
            item = (AdapterItem) mOptionBeltView.getSelectedItem();
        }
        if (item.valid) {
            int ret = super.pushedCenterKey();
            return ret;
        }
        requestCautionTrigger(item.itemId);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        AdapterItem item;
        if (mOptionBeltView.getVisibility() == 0) {
            item = (AdapterItem) mOptionBeltView.getSelectedItem();
        } else {
            item = (AdapterItem) mMainBeltView.getSelectedItem();
        }
        guideResources.add(this.mService.getMenuItemText(item.itemId));
        guideResources.add(this.mService.getMenuItemGuideText(item.itemId));
        guideResources.add(Boolean.valueOf(this.mService.isMenuItemValid(item.itemId)));
    }

    private void updateMainBelt(ArrayList<String> itemList) {
        int curPos = mMainBeltView.getSelectedItemPosition();
        if (curPos < 0) {
            curPos = 0;
        }
        mMainBeltAdapter.clear();
        this.mService.updateValueItemsAvailable(itemList);
        Iterator i$ = itemList.iterator();
        while (i$.hasNext()) {
            String curItemId = i$.next();
            AdapterItem item = new AdapterItem();
            item.mainContainerId = curItemId;
            String itemId = curItemId;
            List<String> optionList = this.mService.getSupportedItemList(itemId, 1);
            if (optionList != null && optionList.size() > 0) {
                itemId = this.mService.getCurrentValue(itemId);
            }
            if (curItemId.equals(GFFilterSetController.TWO_AREAS)) {
                item.selected = getResources().getDrawable(R.drawable.p_16_dd_parts_skyhdr_2area_selected);
            } else if (curItemId.equals(GFFilterSetController.THREE_AREAS)) {
                item.selected = getResources().getDrawable(R.drawable.p_16_dd_parts_skyhdr_3area_selected);
            } else {
                item.selected = this.mService.getDrawable(itemId, "FnSelectedIconRes");
            }
            if (item.selected == null) {
                item.selected = this.mService.getMenuItemSelectedDrawable(itemId);
            }
            item.unselected = this.mService.getDrawable(itemId, "FnIconRes");
            if (item.unselected == null) {
                item.unselected = this.mService.getMenuItemDrawable(itemId);
            }
            item.valid = this.mService.isMenuItemValid(curItemId);
            item.itemId = itemId;
            mMainBeltAdapter.add(item);
        }
        mMainBeltView.setAdapter((ListAdapter) mMainBeltAdapter);
        mMainBeltView.setSelection(curPos, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateOptionBelt(ArrayList<String> itemList) {
        int curPos = mOptionBeltView.getSelectedItemPosition();
        if (curPos < 0) {
            curPos = 0;
        }
        mOptionBeltAdapter.clear();
        int itemNum = 0;
        Iterator i$ = itemList.iterator();
        while (i$.hasNext()) {
            String itemId = i$.next();
            AdapterItem item = new AdapterItem();
            item.selected = this.mService.getMenuItemFnOptionDrawable(itemId);
            item.unselected = item.selected;
            item.itemId = itemId;
            boolean parentValid = this.mService.isMenuItemValid(this.mService.getParentItemId(itemId));
            if (parentValid) {
                parentValid = this.mService.isMenuItemValid(itemId);
            }
            item.valid = parentValid;
            mOptionBeltAdapter.add(item);
            itemNum++;
        }
        mOptionBeltView.setAdapter((ListAdapter) mOptionBeltAdapter);
        mOptionBeltView.setSelection(curPos, false);
        setOptionBeltViewVisibility(true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class AdapterItem {
        String itemId;
        String mainContainerId;
        Drawable selected;
        Drawable unselected;
        boolean valid;

        protected AdapterItem() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class Fn15LayerAdapter extends ArrayAdapter<AdapterItem> {
        private static final int PADDING_BETWEEN_ITEM = 10;
        private Context mContext;
        private int mHeight;
        private int mWidth;

        public Fn15LayerAdapter(Context context, int textViewResourceId, int width, int height) {
            super(context, textViewResourceId);
            this.mContext = context;
            this.mWidth = width;
            this.mHeight = height;
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
            int i = BatteryIcon.BATTERY_STATUS_CHARGING;
            View view = convertView;
            if (view == null) {
                view = new SelectableView(this.mContext) { // from class: com.sony.imaging.app.digitalfilter.menu.layout.GFSetting15LayerFilterSetLayout.Fn15LayerAdapter.1
                    @Override // android.widget.RelativeLayout, android.view.View
                    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        int width = View.MeasureSpec.makeMeasureSpec(Fn15LayerAdapter.this.mWidth + 10, 1073741824);
                        int height = View.MeasureSpec.makeMeasureSpec(Fn15LayerAdapter.this.mHeight, 1073741824);
                        setMeasuredDimension(width, height);
                        super.onMeasure(width, height);
                    }
                };
                view.setLayoutParams(new AbsListView.LayoutParams(this.mWidth, this.mHeight));
                holder = new ViewHolder();
                holder.selectableView = (SelectableView) view;
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            AdapterItem item = getItem(position);
            if (holder != null) {
                if (item.selected != null) {
                    item.selected.setAlpha(item.valid ? 255 : 128);
                }
                if (item.unselected != null) {
                    Drawable drawable = item.unselected;
                    if (!item.valid) {
                        i = 128;
                    }
                    drawable.setAlpha(i);
                }
                holder.selectableView.setSelectedDrawable(item.selected);
                holder.selectableView.setUnSelectedDrawable(item.unselected);
            }
            return view;
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected boolean isAELCancel() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public DisplayMenuItemsMenuLayout.MenuUpdater getMenuUpdater() {
        if (this.mUpdater == null) {
            this.mUpdater = new DisplayMenuItemsMenuLayout.MenuUpdater(new Handler()) { // from class: com.sony.imaging.app.digitalfilter.menu.layout.GFSetting15LayerFilterSetLayout.4
                @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout.MenuUpdater, java.lang.Runnable
                public void run() {
                    if (GFSetting15LayerFilterSetLayout.mMainBeltView != null) {
                        ArrayList<String> itemIdList = GFSetting15LayerFilterSetLayout.this.mService.getSupportedItemList(1);
                        String itemId = itemIdList.get(GFSetting15LayerFilterSetLayout.mMainBeltView.getSelectedItemPosition());
                        if (GFSetting15LayerFilterSetLayout.this.mService.isMenuItemValid(itemId)) {
                            GFSetting15LayerFilterSetLayout.this.mService.execCurrentMenuItem(itemId, false);
                            GFSetting15LayerFilterSetLayout.this.postSetValue();
                        }
                        CustomKeyMgr ckm = CustomKeyMgr.getInstance();
                        if (ckm.isCustomFuncKeyExist("Menu", CustomizableFunction.SubNext) && GFSetting15LayerFilterSetLayout.this.mService.hasSubArray(itemId)) {
                            ArrayList<String> optionList = GFSetting15LayerFilterSetLayout.this.mService.getSupportedItemList(itemId, 1);
                            if (optionList != null && optionList.size() > 1) {
                                String value = GFSetting15LayerFilterSetLayout.this.mService.getMenuItemValue(itemId);
                                if (PictureEffectController.MODE_MINIATURE.equals(value) || DriveModeController.BURST.equals(value)) {
                                    GFSetting15LayerFilterSetLayout.this.mService.updateValueItemsAvailable(optionList, 1);
                                }
                                GFSetting15LayerFilterSetLayout.this.updateOptionBelt(optionList);
                                String optionCurrentItemId = GFSetting15LayerFilterSetLayout.this.mService.getCurrentValue(itemId);
                                int optionPos = 0;
                                Iterator i$ = optionList.iterator();
                                while (i$.hasNext()) {
                                    String optionItemId = i$.next();
                                    if (optionItemId.equals(optionCurrentItemId)) {
                                        break;
                                    } else {
                                        optionPos++;
                                    }
                                }
                                GFSetting15LayerFilterSetLayout.mOptionBeltView.setSelection(optionPos, false);
                                return;
                            }
                            GFSetting15LayerFilterSetLayout.this.setOptionBeltViewVisibility(false);
                            return;
                        }
                        GFSetting15LayerFilterSetLayout.this.setOptionBeltViewVisibility(false);
                    }
                }
            };
        }
        return this.mUpdater;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected void onDeviceStatusChanged() {
        closeMenuLayout(null);
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected void onDigitalZoomOnOff(boolean onoff) {
        closeMenuLayout(new Bundle());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void postSetValue() {
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int movedAelFocusSlideKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAFMFKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int code = event.getScanCode();
        if (GFConstants.Setting15LayerCustomizableFunction.contains(func)) {
            int ret = super.onConvertedKeyDown(event, func);
            return ret;
        }
        if (GFCommonUtil.getInstance().isTriggerMfAssist(code)) {
            return 0;
        }
        return -1;
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
        int code = event.getScanCode();
        switch (code) {
            case 103:
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                if (isFunctionGuideShown()) {
                    return 1;
                }
                closeMenuLayout(new Bundle());
                return 0;
            default:
                int ret = super.onKeyDown(keyCode, event);
                return ret;
        }
    }
}
