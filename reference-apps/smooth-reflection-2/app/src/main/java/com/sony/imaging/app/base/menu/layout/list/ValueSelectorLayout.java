package com.sony.imaging.app.base.menu.layout.list;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class ValueSelectorLayout extends DefaultListMenuLayout {
    protected static final int DEFAULT_VALUE_SELECTOR_RIGHT_MARGIN = -9;
    protected static final int DEFAULT_VALUE_SELECTOR_TOP_MARGIN = 65;
    public static final String TAG = ValueSelectorLayout.class.getSimpleName();
    protected boolean delayedItemSet = false;
    protected DefaultListMenuItem item = null;
    protected int itemIndex = -1;
    protected Integer defaultSelectedPosition = null;
    protected int parentDisplayingPosition = 0;

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout
    protected ArrayList<DefaultListMenuItem> getMenuItems() {
        ValueSelectorItem valueItem;
        if (this.item != null) {
            List<ValueSelectorContent> values = this.item.getValueSelectorContents();
            int selectedIndex = this.item.getSelectedItemIndex();
            ArrayList<DefaultListMenuItem> itemsList = new ArrayList<>();
            int i = 0;
            while (i < values.size()) {
                ValueSelectorContent value = values.get(i);
                if (value.isGuideEnabled()) {
                    valueItem = new ValueSelectorItem(value.getValueStringId(), i == selectedIndex, value.getGuideTitleStringId(), value.getGuideContentStringId());
                } else {
                    valueItem = new ValueSelectorItem(value.getValueStringId(), i == selectedIndex);
                }
                itemsList.add(valueItem);
                i++;
            }
            return itemsList;
        }
        this.delayedItemSet = true;
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        View view = getView().findViewById(getBackgroundWrapperViewId());
        view.setOnClickListener(null);
        super.onResume();
        adjustInflatePosition(this.parentDisplayingPosition, getMenuItems().size());
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout
    protected void setAdapterOnListview() {
        ValueSelectorAdapter adapter = new ValueSelectorAdapter(getActivity().getApplicationContext(), getMenuItems(), this);
        setAdapter(adapter);
    }

    public void setMenuItemData(DefaultListMenuItem item, int itemIndex, int parentDisplayingPosition) {
        this.item = item;
        this.itemIndex = itemIndex;
        this.parentDisplayingPosition = parentDisplayingPosition;
        this.defaultSelectedPosition = Integer.valueOf(item.getSelectedItemIndex());
        if (this.delayedItemSet) {
            setAdapterOnListview();
            refreshAdapterData();
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.DefaultListMenuLayout, com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout
    protected int getMainLayoutId() {
        return R.layout.layout_listmenu_selector;
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.DefaultListMenuLayout, com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout
    protected int getListviewId() {
        return R.id.value_selector_listview;
    }

    protected int getBackgroundWrapperViewId() {
        return R.id.selector_wrapper;
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout
    protected int getTitleBarStringId() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout
    protected int getInitialSelectedPosition() {
        if (this.defaultSelectedPosition != null) {
            return this.defaultSelectedPosition.intValue();
        }
        Log.d(TAG, "defaultSelected is null.");
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout
    protected boolean isInfiniteScrollEnabled() {
        return false;
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout
    protected boolean isInfiniteScrollByTouchEnabled() {
        return false;
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout
    protected int onKeyDownEx(int keyCode, int scanCode, KeyEvent event) {
        Log.d(TAG, "onKeyDownEx: " + scanCode);
        switch (scanCode) {
            case AppRoot.USER_KEYCODE.SK1 /* 229 */:
                AbstractListMenuLayout.MenuActionListener menuActionListener = getMenuActionListener();
                if (menuActionListener != null) {
                    BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_BACK_CANCEL);
                    menuActionListener.onSelectorClosed(this.itemIndex);
                    return 1;
                }
                return -1;
            default:
                return 0;
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout, com.sony.imaging.app.base.menu.layout.list.AbstractListMenuAdapter.ListMenuSelectedListener
    public void onItemSelected(int index, boolean isEnabled) {
        super.onItemSelected(index, isEnabled);
        AbstractListMenuLayout.MenuActionListener menuActionListener = getMenuActionListener();
        if (menuActionListener != null) {
            menuActionListener.onValueSelected(this.itemIndex, index);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout
    protected int onKeyUpEx(int keyCode, int scanCode, KeyEvent event) {
        return -1;
    }

    protected void adjustInflatePosition(int basePosition, int numItems) {
        int marginStep;
        MenuListView valueSelectorView = (MenuListView) getActivity().findViewById(R.id.value_selector_listview);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) valueSelectorView.getLayoutParams();
        if (5 - numItems < basePosition) {
            marginStep = 5 - numItems;
        } else {
            marginStep = basePosition;
        }
        if (marginStep < 0) {
            marginStep = 0;
        }
        Log.d(TAG, "Inflate MarginStep: " + marginStep);
        int topMargin = (marginStep * 82) + DEFAULT_VALUE_SELECTOR_TOP_MARGIN;
        params.setMargins(0, topMargin, DEFAULT_VALUE_SELECTOR_RIGHT_MARGIN, 0);
        valueSelectorView.setLayoutParams(params);
    }
}
