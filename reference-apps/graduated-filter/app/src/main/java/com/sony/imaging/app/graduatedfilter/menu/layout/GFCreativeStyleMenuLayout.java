package com.sony.imaging.app.graduatedfilter.menu.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.menu.layout.CreativeStyleMenuLayout;
import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.graduatedfilter.GFBackUpKey;
import com.sony.imaging.app.graduatedfilter.R;
import com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.graduatedfilter.shooting.widget.BorderView;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class GFCreativeStyleMenuLayout extends CreativeStyleMenuLayout implements NotificationListener {
    private static BorderView mBorderView = null;
    public static boolean isOptionSetting = false;
    public static boolean isCanceled = false;
    private static final String[] tags = {DisplayModeObserver.TAG_DEVICE_CHANGE, DisplayModeObserver.TAG_YUVLAYOUT_CHANGE, DisplayModeObserver.TAG_VIEW_PATTERN_CHANGE, DisplayModeObserver.TAG_DISPMODE_CHANGE, "com.sony.imaging.app.base.common.DisplayModeObserver.OledScreenChange"};

    @Override // com.sony.imaging.app.base.menu.layout.CreativeStyleMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public ViewGroup onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup currentView = super.onCreateView(inflater, container, savedInstanceState);
        mBorderView = (BorderView) currentView.findViewById(R.id.border_view);
        isOptionSetting = false;
        isCanceled = false;
        return currentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.CreativeStyleMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        DisplayModeObserver.getInstance().setNotificationListener(this);
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        DisplayModeObserver.getInstance().removeNotificationListener(this);
        mBorderView = null;
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        params.setCreativeStyle(CreativeStyleController.getInstance().getValue(CreativeStyleController.CREATIVESTYLE));
        CreativeStyleController.CreativeStyleOptions option = (CreativeStyleController.CreativeStyleOptions) CreativeStyleController.getInstance().getDetailValue();
        String value = "" + option.contrast + "/" + option.saturation + "/" + option.sharpness;
        GFBackUpKey.getInstance().saveCSOption(CreativeStyleController.getInstance().getValue(CreativeStyleController.CREATIVESTYLE), value, GFEffectParameters.Parameters.getEffect());
        params.setCreativeStyleOption(value);
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.base.menu.layout.CreativeStyleMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        isOptionSetting = false;
        isCanceled = true;
        return super.pushedMenuKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.CreativeStyleMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK1Key() {
        return pushedMenuKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.CreativeStyleMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        int ret = super.pushedLeftKey();
        if (this.mShadeArea.getVisibility() != 0) {
            CreativeStyleController.CreativeStyleOptions option = (CreativeStyleController.CreativeStyleOptions) CreativeStyleController.getInstance().getDetailValue();
            String value = "" + option.contrast + "/" + option.saturation + "/" + option.sharpness;
            GFBackUpKey.getInstance().saveCSOption(CreativeStyleController.getInstance().getValue(CreativeStyleController.CREATIVESTYLE), value, GFEffectParameters.Parameters.getEffect());
            isOptionSetting = false;
        } else {
            isOptionSetting = true;
        }
        return ret;
    }

    @Override // com.sony.imaging.app.base.menu.layout.CreativeStyleMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        int ret = super.pushedRightKey();
        if (this.mShadeArea.getVisibility() != 0) {
            CreativeStyleController.CreativeStyleOptions option = (CreativeStyleController.CreativeStyleOptions) CreativeStyleController.getInstance().getDetailValue();
            String value = "" + option.contrast + "/" + option.saturation + "/" + option.sharpness;
            GFBackUpKey.getInstance().saveCSOption(CreativeStyleController.getInstance().getValue(CreativeStyleController.CREATIVESTYLE), value, GFEffectParameters.Parameters.getEffect());
            isOptionSetting = false;
        } else {
            isOptionSetting = true;
        }
        return ret;
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
