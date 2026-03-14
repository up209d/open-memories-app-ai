package com.sony.imaging.app.base.menu.layout.list;

import com.sony.imaging.app.base.R;

/* loaded from: classes.dex */
public abstract class DefaultListMenuLayout extends AbstractListMenuLayout {
    protected static final int DEFAULT_DELAY_GUIDE_OPEN = 1200;
    protected static final int DEFAULT_DIVIDER_HEIGHT = 0;
    protected static final int DEFAULT_NUM_DISPLAYED_ITEMS = 5;
    protected static final int DEFAULT_ROW_HEIGHT = 82;

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout
    protected void setInitialSoftKeyArea() {
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout
    protected int getMaxNumOfDisplayedItems() {
        return 5;
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout
    protected int getRowHeight() {
        return DEFAULT_ROW_HEIGHT;
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout
    protected int getDividerHeight() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout
    protected int getTitleBarTextViewId() {
        return R.id.headerTitle;
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout
    protected int getMainLayoutId() {
        return R.layout.layout_listmenu_main;
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout
    protected int getListviewId() {
        return R.id.list_menu_listview;
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout
    protected int getGuideOpenDelay() {
        return DEFAULT_DELAY_GUIDE_OPEN;
    }
}
