package com.sony.imaging.app.srctrl.shooting.layout;

import com.sony.imaging.app.base.menu.layout.MovieModeMenuLayout;
import com.sony.imaging.app.srctrl.webapi.specific.ServerEventHandler;

/* loaded from: classes.dex */
public class MovieModeMenuLayoutEx extends MovieModeMenuLayout {
    private static final String tag = MovieModeMenuLayoutEx.class.getName();

    @Override // com.sony.imaging.app.base.menu.layout.MovieModeMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        ServerEventHandler.getInstance().beginServerStatusChanged();
        super.onResume();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        ServerEventHandler.getInstance().commitServerStatusChanged();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected String getMenuLayoutID() {
        return "ID_MOVIEMODEMENULAYOUT";
    }
}
