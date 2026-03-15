package com.sony.imaging.app.startrails.menu.base.layout;

import android.util.Log;
import com.sony.imaging.app.base.menu.layout.PageMenuLayout;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class STPageMenuLayout extends PageMenuLayout {
    private static final String TAG = STPageMenuLayout.class.getSimpleName();

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected String getMenuLayoutID() {
        return PageMenuLayout.MENU_ID;
    }

    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppNameView.show(false);
        super.onResume();
        ArrayList<String> menuItemIds = this.mService.getMenuItemList();
        ArrayList<String> supportedItemIds = new ArrayList<>();
        Iterator i$ = menuItemIds.iterator();
        while (i$.hasNext()) {
            String pageId = i$.next();
            ArrayList<String> list = this.mService.getSupportedItemList(pageId);
            if (list != null && list.size() != 0) {
                supportedItemIds.add(pageId);
            }
        }
        updatePageViewItems(supportedItemIds);
        this.mPageListView.invalidateViews();
        Log.i(TAG, "onResume");
    }

    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.fw.Layout
    public void onReopened() {
        super.onReopened();
        ArrayList<String> menuItemIds = this.mService.getMenuItemList();
        ArrayList<String> supportedItemIds = new ArrayList<>();
        Iterator i$ = menuItemIds.iterator();
        while (i$.hasNext()) {
            String pageId = i$.next();
            ArrayList<String> list = this.mService.getSupportedItemList(pageId);
            if (list != null && list.size() != 0) {
                supportedItemIds.add(pageId);
            }
        }
        updatePageViewItems(supportedItemIds);
        this.mPageListView.invalidateViews();
        Log.i(TAG, "onReopend");
    }
}
