package com.sony.imaging.app.graduatedfilter.menu.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.menu.layout.FlashCompensationMenuLayout;
import com.sony.imaging.app.base.shooting.camera.FlashController;
import com.sony.imaging.app.graduatedfilter.R;
import com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.graduatedfilter.shooting.widget.BorderView;
import com.sony.imaging.app.util.NotificationListener;
import java.util.List;

/* loaded from: classes.dex */
public class GFFlashCompensationMenuLayout extends FlashCompensationMenuLayout implements NotificationListener {
    private static final String STR_ZERO = "0";
    private static BorderView mBorderView = null;
    private static final String[] tags = {DisplayModeObserver.TAG_DEVICE_CHANGE, DisplayModeObserver.TAG_YUVLAYOUT_CHANGE, DisplayModeObserver.TAG_VIEW_PATTERN_CHANGE, DisplayModeObserver.TAG_DISPMODE_CHANGE, "com.sony.imaging.app.base.common.DisplayModeObserver.OledScreenChange"};
    private int mLevelOfZero;

    @Override // com.sony.imaging.app.base.menu.layout.FlashCompensationMenuLayout, com.sony.imaging.app.base.menu.layout.CompensationMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mLevelOfZero = 0;
        List<String> support = FlashController.getInstance().getSupportedValue(FlashController.FLASH_COMPENSATION);
        if (support != null && support.size() != 0) {
            this.mLevelOfZero = support.indexOf("0");
        }
        View currentView = super.onCreateView(inflater, container, savedInstanceState);
        mBorderView = (BorderView) currentView.findViewById(R.id.border_view);
        return currentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.CompensationMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        DisplayModeObserver.getInstance().setNotificationListener(this);
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        DisplayModeObserver.getInstance().removeNotificationListener(this);
        mBorderView = null;
        super.onPause();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.FlashCompensationMenuLayout, com.sony.imaging.app.base.menu.layout.CompensationMenuLayout
    public void setCompensationLevel(int level) {
        String setValue = String.valueOf(level - this.mLevelOfZero);
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        params.setFlashComp(setValue);
        super.setCompensationLevel(level);
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
