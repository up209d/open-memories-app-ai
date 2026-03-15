package com.sony.imaging.app.each.menu.list;

import android.util.Log;
import com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout;
import com.sony.imaging.app.base.menu.layout.list.DefaultListMenuItem;
import com.sony.imaging.app.base.menu.layout.list.MenuGuideLayout;
import com.sony.imaging.app.base.menu.layout.list.ValueSelectorLayout;
import com.sony.imaging.app.fw.ContainerState;
import com.sony.imaging.app.util.ApoWrapper;

/* loaded from: classes.dex */
public class ListMenuStateSample extends ContainerState implements AbstractListMenuLayout.MenuActionListener {
    public static final String ID_MAIN_LIST_LAYOUT = "LIST";
    public static final String ID_MENU_GUIDE = "MENU_GUIDE";
    public static final String ID_VALUE_SELECTOR = "VALUE_SELECTOR";
    private static final String TAG = ListMenuStateSample.class.getSimpleName();

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        Log.d(TAG, "onResume");
        openLayout(ID_MAIN_LIST_LAYOUT);
        ((AbstractListMenuLayout) getLayout(ID_MAIN_LIST_LAYOUT)).registerMenuActionListener(this);
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        Log.d(TAG, "onPause");
        closeLayout(ID_MAIN_LIST_LAYOUT);
    }

    @Override // com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        return ApoWrapper.APO_TYPE.NONE;
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout.MenuActionListener
    public void onRequestSelectorOpen(DefaultListMenuItem item, int itemIndex, int parentDisplayingPosition) {
        openLayout(ID_VALUE_SELECTOR);
        ValueSelectorLayout layout = (ValueSelectorLayout) getLayout(ID_VALUE_SELECTOR);
        layout.registerMenuActionListener(this);
        layout.setMenuItemData(item, itemIndex, parentDisplayingPosition);
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout.MenuActionListener
    public void onValueSelected(int itemIndex, int valueIndex) {
        AbstractListMenuLayout mainLayout = (AbstractListMenuLayout) getLayout(ID_MAIN_LIST_LAYOUT);
        mainLayout.onValueSelected(itemIndex, valueIndex);
        closeLayout(ID_VALUE_SELECTOR);
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout.MenuActionListener
    public void onSelectorClosed(int itemIndex) {
        closeLayout(ID_VALUE_SELECTOR);
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout.MenuActionListener
    public void onGuideClosed() {
        closeLayout(ID_MENU_GUIDE);
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout.MenuActionListener
    public void onRequestGuideOpen(int titleStringId, int contentStringId) {
        openLayout(ID_MENU_GUIDE);
        MenuGuideLayout layout = (MenuGuideLayout) getLayout(ID_MENU_GUIDE);
        layout.registerMenuActionListener(this);
        layout.setGuideLayoutData(titleStringId, contentStringId);
    }
}
