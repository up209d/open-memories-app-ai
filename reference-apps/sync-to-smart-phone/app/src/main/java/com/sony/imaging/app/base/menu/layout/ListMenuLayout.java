package com.sony.imaging.app.base.menu.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class ListMenuLayout extends SimpleMenuLayout {
    public static final String MENU_ID = "ID_LISTMENULAYOUT";
    private static final String TAG = ListMenuLayout.class.getSimpleName();
    protected ItemAlignedListView mItemListView;
    protected AppNameView mScreenTitleView;

    @Override // com.sony.imaging.app.base.menu.layout.SimpleMenuLayout
    protected int getAdapterLayoutID() {
        return R.layout.menu_page_adapter;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SimpleMenuLayout
    protected AdapterView getAdapterView() {
        return this.mItemListView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SimpleMenuLayout
    protected void setAdapterResources(ArrayList<Object> resources, String itemId) {
        resources.add(this.mService.getMenuItemText(itemId));
        String valueItemId = this.mService.getCurrentValue(itemId);
        if (valueItemId != null) {
            resources.add(this.mService.getMenuItemText(valueItemId));
        } else {
            resources.add("");
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.SimpleMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View createdView = obtainViewFromPool(R.layout.menu_list);
        this.mItemListView = (ItemAlignedListView) createdView.findViewById(R.id.itemlistview);
        this.mScreenTitleView = (AppNameView) createdView.findViewById(R.id.menu_screen_title);
        return createdView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SimpleMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.mScreenTitleView.setVisibility(0);
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mItemListView = null;
        this.mScreenTitleView = null;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SimpleMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        this.mScreenTitleView.setVisibility(4);
    }
}
