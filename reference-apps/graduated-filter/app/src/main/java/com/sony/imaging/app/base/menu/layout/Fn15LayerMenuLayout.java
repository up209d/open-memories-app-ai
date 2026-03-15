package com.sony.imaging.app.base.menu.layout;

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
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.widget.BatteryIcon;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomKeyMgr;
import com.sony.imaging.app.fw.CustomizableFunction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class Fn15LayerMenuLayout extends Fn15LayerAbsLayout {
    protected static final int MAIN_BELT_HEIGHT = 60;
    protected static final int MAIN_BELT_WIDTH = 70;
    public static final String MENU_ID = "ID_FN15LAYERMENULAYOUT";
    protected static final int OPTION_BELT_HEIGHT = 35;
    protected static final int OPTION_BELT_WIDTH = 70;
    private static final String TAG = Fn15LayerMenuLayout.class.getSimpleName();
    protected ImageView mBackGround;
    protected List<String> mItemIdListForCancel;
    protected TextView mItemTextView;
    protected Fn15LayerAdapter mMainBeltAdapter;
    protected BeltView mMainBeltView;
    protected ImageView mOptionArrowLeft;
    protected ImageView mOptionArrowRight;
    protected Fn15LayerAdapter mOptionBeltAdapter;
    protected BeltView mOptionBeltView;
    private DisplayMenuItemsMenuLayout.MenuUpdater mOptionUpdater;
    AdapterView.OnItemSelectedListener mOnItemSelectedListenerForMainBelt = new AdapterView.OnItemSelectedListener() { // from class: com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout.1
        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            AdapterItem item = (AdapterItem) arg0.getItemAtPosition(arg2);
            Fn15LayerMenuLayout.this.mItemTextView.setText(Fn15LayerMenuLayout.this.mService.getMenuItemText(item.itemId));
            Fn15LayerMenuLayout.this.mItemTextView.setVisibility(0);
            Fn15LayerMenuLayout.this.mUpdater.restartMenuUpdater();
            Fn15LayerMenuLayout.this.onMainBeltItemSelected((BeltView) arg0, arg1, arg2, arg3);
        }

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    };
    AdapterView.OnItemSelectedListener mOnItemSelectedListenerForOptionBelt = new AdapterView.OnItemSelectedListener() { // from class: com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout.2
        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            AdapterItem item = (AdapterItem) arg0.getItemAtPosition(arg2);
            Fn15LayerMenuLayout.this.mItemTextView.setText(Fn15LayerMenuLayout.this.mService.getMenuItemText(item.itemId));
            Fn15LayerMenuLayout.this.mItemTextView.setVisibility(0);
            Fn15LayerMenuLayout.this.mOptionUpdater.restartMenuUpdater();
            AdapterItem mainItem = (AdapterItem) Fn15LayerMenuLayout.this.mMainBeltView.getSelectedItem();
            mainItem.itemId = item.itemId;
            mainItem.selected = Fn15LayerMenuLayout.this.mService.getMenuItemSelectedDrawable(item.itemId);
            mainItem.unselected = Fn15LayerMenuLayout.this.mService.getMenuItemDrawable(item.itemId);
            Fn15LayerMenuLayout.this.mMainBeltView.requestLayout();
            Fn15LayerMenuLayout.this.onOptionBeltItemSelected((BeltView) arg0, arg1, arg2, arg3);
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
        this.mMainBeltAdapter = new Fn15LayerAdapter(getActivity(), 0, 70, MAIN_BELT_HEIGHT);
        this.mOptionBeltAdapter = new Fn15LayerAdapter(getActivity(), 0, 70, 35);
        this.mMainBeltView = (BeltView) view.findViewById(R.id.main_belt_view);
        this.mOptionBeltView = (BeltView) view.findViewById(R.id.option_belt_view);
        this.mOptionArrowLeft = (ImageView) view.findViewById(R.id.left_arrow_option);
        this.mOptionArrowRight = (ImageView) view.findViewById(R.id.right_arrow_option);
        this.mItemTextView = (TextView) view.findViewById(R.id.item_text);
        this.mBackGround = (ImageView) view.findViewById(R.id.back_ground_image);
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
        for (String itemId : this.mItemIdListForCancel) {
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
        this.mItemIdListForCancel = new ArrayList();
        ArrayList<String> itemList = this.mService.getSupportedItemList(1);
        if (itemList == null || itemList.size() < getMinItemListSize() || !isKeyHandled(action)) {
            openPreviousMenu();
            return;
        }
        updateMainBelt(itemList);
        String curItemId = this.mService.getCurrentValue(this.mService.getMenuItemId());
        this.mItemIdListForCancel.add(curItemId);
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
        this.mMainBeltView.setSelection(pos, false);
        this.mMainBeltView.setOnItemSelectedListener(this.mOnItemSelectedListenerForMainBelt);
        this.mMainBeltView.setFocusable(false);
        if (this.mService.hasSupportedSubArray(curItemId)) {
            String optionCurrentItemId = this.mService.getCurrentValue(curItemId);
            this.mItemIdListForCancel.add(optionCurrentItemId);
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
                this.mOptionBeltView.setSelection(optionPos, false);
                curItemId = optionCurrentItemId;
            } else {
                setOptionBeltViewVisibility(false);
            }
        } else {
            setOptionBeltViewVisibility(false);
        }
        this.mOptionBeltView.setOnItemSelectedListener(this.mOnItemSelectedListenerForOptionBelt);
        this.mOptionBeltView.setFocusable(false);
        this.mItemTextView.setText(this.mService.getMenuItemText(curItemId));
        this.mItemTextView.setVisibility(4);
        this.mOptionUpdater = new DisplayMenuItemsMenuLayout.MenuUpdater(new Handler()) { // from class: com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout.3
            @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout.MenuUpdater, java.lang.Runnable
            public void run() {
                if (Fn15LayerMenuLayout.this.mOptionBeltView != null) {
                    AdapterItem optionItem = (AdapterItem) Fn15LayerMenuLayout.this.mOptionBeltView.getSelectedItem();
                    if (optionItem.valid) {
                        Fn15LayerMenuLayout.this.mService.execCurrentMenuItem(optionItem.itemId, false);
                        Fn15LayerMenuLayout.this.postSetValue();
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
        if (this.mOptionUpdater != null) {
            this.mOptionUpdater.cancelMenuUpdater();
        }
        if (this.mMainBeltView != null) {
            revertAplha(this.mMainBeltView);
            this.mMainBeltView.setOnItemSelectedListener(null);
            this.mMainBeltView.setAdapter((ListAdapter) null);
        }
        if (this.mOptionBeltView != null) {
            revertAplha(this.mOptionBeltView);
            this.mOptionBeltView.setOnItemSelectedListener(null);
            this.mOptionBeltView.setAdapter((ListAdapter) null);
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
        this.mMainBeltAdapter = null;
        this.mOptionBeltAdapter = null;
        this.mMainBeltView = null;
        this.mOptionBeltView = null;
        this.mItemTextView = null;
        this.mBackGround = null;
        this.mOptionArrowLeft = null;
        this.mOptionArrowRight = null;
        super.onDestroyView();
    }

    public boolean isKeyHandled(int key) {
        String curItemId = this.mService.getCurrentValue(this.mService.getMenuItemId());
        switch (key) {
            case 3:
            case 4:
                if (this.mService.hasSupportedSubArray(curItemId)) {
                    return true;
                }
                return false;
            default:
                return true;
        }
    }

    protected void setOptionBeltViewVisibility(boolean visibility) {
        if (visibility) {
            this.mOptionBeltView.setVisibility(0);
            this.mOptionArrowLeft.setVisibility(0);
            this.mOptionArrowRight.setVisibility(0);
            this.mBackGround.setImageResource(17303934);
            return;
        }
        this.mOptionBeltView.setVisibility(8);
        this.mOptionArrowLeft.setVisibility(8);
        this.mOptionArrowRight.setVisibility(8);
        this.mBackGround.setImageResource(android.R.drawable.ic_bluetooth_share_icon);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        boolean handled = this.mMainBeltView.dispatchKeyEvent(new KeyEvent(0L, 0L, 0, 0, 0, 0, 0, AppRoot.USER_KEYCODE.RIGHT));
        if (handled) {
            setOptionBeltViewVisibility(false);
            this.mBackGround.setImageResource(android.R.drawable.ic_bluetooth_share_icon);
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        boolean handled = this.mMainBeltView.dispatchKeyEvent(new KeyEvent(0L, 0L, 0, 0, 0, 0, 0, AppRoot.USER_KEYCODE.LEFT));
        if (handled) {
            setOptionBeltViewVisibility(false);
            this.mBackGround.setImageResource(android.R.drawable.ic_bluetooth_share_icon);
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        if (this.mOptionBeltView != null && this.mOptionBeltView.getVisibility() == 0) {
            this.mOptionBeltView.dispatchKeyEvent(new KeyEvent(0L, 0L, 0, 0, 0, 0, 0, AppRoot.USER_KEYCODE.RIGHT));
            return 1;
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        if (this.mOptionBeltView != null && this.mOptionBeltView.getVisibility() == 0) {
            this.mOptionBeltView.dispatchKeyEvent(new KeyEvent(0L, 0L, 0, 0, 0, 0, 0, AppRoot.USER_KEYCODE.LEFT));
            return 1;
        }
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialNext() {
        this.mMainBeltView.dispatchKeyEvent(new KeyEvent(0L, 0L, 0, 0, 0, 0, 0, AppRoot.USER_KEYCODE.RIGHT));
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialPrev() {
        this.mMainBeltView.dispatchKeyEvent(new KeyEvent(0L, 0L, 0, 0, 0, 0, 0, AppRoot.USER_KEYCODE.LEFT));
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        AdapterItem item = (AdapterItem) this.mMainBeltView.getSelectedItem();
        if (!item.valid) {
            requestCautionTrigger(item.mainContainerId);
            return 1;
        }
        if (this.mOptionBeltView.getVisibility() == 0) {
            item = (AdapterItem) this.mOptionBeltView.getSelectedItem();
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
        if (this.mOptionBeltView.getVisibility() == 0) {
            item = (AdapterItem) this.mOptionBeltView.getSelectedItem();
        } else {
            item = (AdapterItem) this.mMainBeltView.getSelectedItem();
        }
        guideResources.add(this.mService.getMenuItemText(item.itemId));
        guideResources.add(this.mService.getMenuItemGuideText(item.itemId));
        guideResources.add(Boolean.valueOf(this.mService.isMenuItemValid(item.itemId)));
    }

    private void updateMainBelt(ArrayList<String> itemList) {
        int curPos = this.mMainBeltView.getSelectedItemPosition();
        if (curPos < 0) {
            curPos = 0;
        }
        this.mMainBeltAdapter.clear();
        this.mService.updateValueItemsAvailable(itemList);
        int itemNum = 0;
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
            item.selected = this.mService.getDrawable(itemId, "FnSelectedIconRes");
            if (item.selected == null) {
                item.selected = this.mService.getMenuItemSelectedDrawable(itemId);
            }
            item.unselected = this.mService.getDrawable(itemId, "FnIconRes");
            if (item.unselected == null) {
                item.unselected = this.mService.getMenuItemDrawable(itemId);
            }
            item.valid = this.mService.isMenuItemValid(curItemId);
            item.itemId = itemId;
            this.mMainBeltAdapter.add(item);
            itemNum++;
        }
        this.mMainBeltView.setAdapter((ListAdapter) this.mMainBeltAdapter);
        this.mMainBeltView.setSelection(curPos, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateOptionBelt(ArrayList<String> itemList) {
        int curPos = this.mOptionBeltView.getSelectedItemPosition();
        if (curPos < 0) {
            curPos = 0;
        }
        this.mOptionBeltAdapter.clear();
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
            this.mOptionBeltAdapter.add(item);
            itemNum++;
        }
        this.mOptionBeltView.setAdapter((ListAdapter) this.mOptionBeltAdapter);
        this.mOptionBeltView.setSelection(curPos, false);
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
                view = new SelectableView(this.mContext) { // from class: com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout.Fn15LayerAdapter.1
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

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected DisplayMenuItemsMenuLayout.MenuUpdater getMenuUpdater() {
        if (this.mUpdater == null) {
            this.mUpdater = new DisplayMenuItemsMenuLayout.MenuUpdater(new Handler()) { // from class: com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout.4
                @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout.MenuUpdater, java.lang.Runnable
                public void run() {
                    if (Fn15LayerMenuLayout.this.mMainBeltView != null) {
                        ArrayList<String> itemIdList = Fn15LayerMenuLayout.this.mService.getSupportedItemList(1);
                        String itemId = itemIdList.get(Fn15LayerMenuLayout.this.mMainBeltView.getSelectedItemPosition());
                        if (Fn15LayerMenuLayout.this.mService.isMenuItemValid(itemId)) {
                            Fn15LayerMenuLayout.this.mService.execCurrentMenuItem(itemId, false);
                            Fn15LayerMenuLayout.this.postSetValue();
                        }
                        CustomKeyMgr ckm = CustomKeyMgr.getInstance();
                        if (ckm.isCustomFuncKeyExist("Menu", CustomizableFunction.SubNext) && Fn15LayerMenuLayout.this.mService.hasSubArray(itemId)) {
                            ArrayList<String> optionList = Fn15LayerMenuLayout.this.mService.getSupportedItemList(itemId, 1);
                            if (optionList != null && optionList.size() > 1) {
                                String value = Fn15LayerMenuLayout.this.mService.getMenuItemValue(itemId);
                                if (PictureEffectController.MODE_MINIATURE.equals(value) || DriveModeController.BURST.equals(value)) {
                                    Fn15LayerMenuLayout.this.mService.updateValueItemsAvailable(optionList, 1);
                                }
                                Fn15LayerMenuLayout.this.updateOptionBelt(optionList);
                                String optionCurrentItemId = Fn15LayerMenuLayout.this.mService.getCurrentValue(itemId);
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
                                Fn15LayerMenuLayout.this.mOptionBeltView.setSelection(optionPos, false);
                                return;
                            }
                            Fn15LayerMenuLayout.this.setOptionBeltViewVisibility(false);
                            return;
                        }
                        Fn15LayerMenuLayout.this.setOptionBeltViewVisibility(false);
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
}
