package com.sony.imaging.app.graduatedfilter.menu.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.menu.layout.FnMenuLayout;
import com.sony.imaging.app.graduatedfilter.R;
import com.sony.imaging.app.graduatedfilter.shooting.widget.BorderView;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class GFFnMenuLayout extends FnMenuLayout implements NotificationListener {
    private static BorderView mBorderView = null;
    private static final String[] tags = {DisplayModeObserver.TAG_DEVICE_CHANGE, DisplayModeObserver.TAG_YUVLAYOUT_CHANGE, DisplayModeObserver.TAG_VIEW_PATTERN_CHANGE, DisplayModeObserver.TAG_DISPMODE_CHANGE, "com.sony.imaging.app.base.common.DisplayModeObserver.OledScreenChange"};

    @Override // com.sony.imaging.app.base.menu.layout.FnMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View currentView = super.onCreateView(inflater, container, savedInstanceState);
        mBorderView = (BorderView) currentView.findViewById(R.id.border_view);
        return currentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.FnMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        System.gc();
        super.onResume();
        DisplayModeObserver.getInstance().setNotificationListener(this);
    }

    @Override // com.sony.imaging.app.base.menu.layout.FnMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        DisplayModeObserver.getInstance().removeNotificationListener(this);
        mBorderView = null;
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.FnMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        System.gc();
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return tags;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        mBorderView.invalidate();
    }
}
