package com.sony.imaging.app.graduatedfilter.menu.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout;
import com.sony.imaging.app.base.shooting.camera.DROAutoHDRController;
import com.sony.imaging.app.base.shooting.camera.FlashController;
import com.sony.imaging.app.base.shooting.camera.MeteringController;
import com.sony.imaging.app.graduatedfilter.GFBackUpKey;
import com.sony.imaging.app.graduatedfilter.R;
import com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.graduatedfilter.shooting.widget.BorderView;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class GFSpecialScreenMenuLayout extends SpecialScreenMenuLayout implements NotificationListener {
    public static final String MENU_ID = "ID_GFSPECIALSCREENMENULAYOUT";
    public static boolean isCanceled = false;
    private static BorderView mBorderView = null;
    private static final String[] tags = {DisplayModeObserver.TAG_DEVICE_CHANGE, DisplayModeObserver.TAG_YUVLAYOUT_CHANGE, DisplayModeObserver.TAG_VIEW_PATTERN_CHANGE, DisplayModeObserver.TAG_DISPMODE_CHANGE, "com.sony.imaging.app.base.common.DisplayModeObserver.OledScreenChange"};

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View currentView = super.onCreateView(inflater, container, savedInstanceState);
        mBorderView = (BorderView) currentView.findViewById(R.id.border_view);
        return currentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        isCanceled = false;
        super.onResume();
        DisplayModeObserver.getInstance().setNotificationListener(this);
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        DisplayModeObserver.getInstance().removeNotificationListener(this);
        mBorderView = null;
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        isCanceled = true;
        return super.pushedMenuKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        String itemID = this.mService.getMenuItemId();
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        if (itemID.equalsIgnoreCase(DROAutoHDRController.MENU_ITEM_ID_DROHDR)) {
            if (!isCanceled) {
                params.setDRO(DROAutoHDRController.getInstance().getValue());
            }
        } else if (itemID.equalsIgnoreCase(FlashController.FLASHMODE)) {
            if (!isCanceled) {
                String mode = FlashController.getInstance().getSettingValue(FlashController.FLASHMODE);
                params.setFlashMode(mode);
            }
        } else if (itemID.equalsIgnoreCase("MeteringMode")) {
            String meteringID = MeteringController.getInstance().getValue("MeteringMode");
            params.setMeteringMode(meteringID);
            if (Environment.isMeteringSpotSizeAPISupported() && meteringID.equals(MeteringController.MENU_ITEM_ID_METERING_CENTER_SPOT)) {
                String value = MeteringController.getInstance().getValue(meteringID);
                GFBackUpKey.getInstance().saveMeteringSpotValue(value, GFEffectParameters.Parameters.getEffect());
            }
        }
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected String getMenuLayoutID() {
        return MENU_ID;
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
