package com.sony.imaging.app.soundphoto.menu.base.layout;

import com.sony.imaging.app.base.menu.layout.PageMenuLayout;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class SPPageMenuLayout extends PageMenuLayout {
    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected String getMenuLayoutID() {
        return PageMenuLayout.MENU_ID;
    }

    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppNameView.show(false);
        super.onResume();
        ArrayList<String> menuItemIds = this.mService.getMenuItemList();
        if (menuItemIds != null && menuItemIds.size() > 5) {
            menuItemIds.remove(5);
            updatePageViewItems(menuItemIds);
            this.mPageListView.invalidateViews();
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.fw.Layout
    public void onReopened() {
        super.onReopened();
        ArrayList<String> menuItemIds = this.mService.getMenuItemList();
        if (menuItemIds != null && menuItemIds.size() > 5) {
            menuItemIds.remove(5);
            updatePageViewItems(menuItemIds);
            this.mPageListView.invalidateViews();
        }
    }
}
