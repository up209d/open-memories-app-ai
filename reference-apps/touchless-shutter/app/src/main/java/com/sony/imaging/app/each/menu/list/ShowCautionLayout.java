package com.sony.imaging.app.each.menu.list;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.menu.HistoryItem;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.menu.layout.MenuLayoutListener;

/* loaded from: classes.dex */
public class ShowCautionLayout extends DisplayMenuItemsMenuLayout implements MenuLayoutListener {
    protected String TAG = "ShowCautionLayout";
    public HistoryItem mLastItemId = null;

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        CautionUtilityClass.getInstance().requestTrigger(132001);
        new Handler().post(new Runnable() { // from class: com.sony.imaging.app.each.menu.list.ShowCautionLayout.1
            @Override // java.lang.Runnable
            public void run() {
                ShowCautionLayout.this.closeMenuLayout(new Bundle());
            }
        });
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected boolean isDisplayedAlone() {
        return false;
    }

    @Override // com.sony.imaging.app.base.menu.layout.MenuLayoutListener
    public void onClosed(Bundle bundle) {
    }

    @Override // com.sony.imaging.app.base.menu.layout.MenuLayoutListener
    public MenuState getState() {
        return null;
    }
}
