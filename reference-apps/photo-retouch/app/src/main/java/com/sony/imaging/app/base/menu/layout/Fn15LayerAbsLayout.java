package com.sony.imaging.app.base.menu.layout;

import android.os.Bundle;
import android.os.Handler;

/* loaded from: classes.dex */
public abstract class Fn15LayerAbsLayout extends DisplayMenuItemsMenuLayout {
    public static final String BUNDLE_KEY_ACTION = "action";
    public static final int BUNDLE_VALUE_MAIN_NEXT = 1;
    public static final int BUNDLE_VALUE_MAIN_PREV = 2;
    public static final int BUNDLE_VALUE_SUB_NEXT = 3;
    public static final int BUNDLE_VALUE_SUB_PREV = 4;
    private Runnable mDialRunnable;
    private Handler mHandler;

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected boolean isDisplayedAlone() {
        return false;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        closeMenuLayout(new Bundle());
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        closeMenuLayout(new Bundle());
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        closeMenuLayout(new Bundle());
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        closeMenuLayout(new Bundle());
        return super.pushedUpKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        closeMenuLayout(new Bundle());
        return super.pushedDownKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        closeMenuLayout(null);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        closeMenuLayout(new Bundle());
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        final int action = this.data.getInt(BUNDLE_KEY_ACTION);
        this.mHandler = new Handler();
        this.mDialRunnable = new Runnable() { // from class: com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout.1
            @Override // java.lang.Runnable
            public void run() {
                switch (action) {
                    case 1:
                        Fn15LayerAbsLayout.this.turnedMainDialNext();
                        break;
                    case 2:
                        Fn15LayerAbsLayout.this.turnedMainDialPrev();
                        break;
                    case 3:
                        Fn15LayerAbsLayout.this.turnedSubDialNext();
                        break;
                    case 4:
                        Fn15LayerAbsLayout.this.turnedSubDialPrev();
                        break;
                }
                Fn15LayerAbsLayout.this.onInitDialMove();
            }
        };
        this.mHandler.post(this.mDialRunnable);
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        this.mHandler.removeCallbacks(this.mDialRunnable);
        this.mHandler = null;
        super.onPause();
    }

    protected void onInitDialMove() {
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected void onAFMFSwitchChanged() {
        closeMenuLayout(null);
    }
}
