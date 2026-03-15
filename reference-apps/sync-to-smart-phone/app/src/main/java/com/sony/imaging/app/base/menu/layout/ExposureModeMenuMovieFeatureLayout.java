package com.sony.imaging.app.base.menu.layout;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;

/* loaded from: classes.dex */
public class ExposureModeMenuMovieFeatureLayout extends SpecialScreenMenuLayout {
    private static final String INTELLIGENT_AUTO_ICON = "android:drawable/p_16_dd_parts_intelligentautomode_intelligentauto_menulist_n";

    /* loaded from: classes.dex */
    public class ExposureModeMenuMovieFeatureAdapter extends SpecialScreenMenuLayout.SpecialBaseMenuAdapter {
        public ExposureModeMenuMovieFeatureAdapter(Context context, int ResId, BaseMenuService service) {
            super(context, ResId, service);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout.SpecialBaseMenuAdapter, com.sony.imaging.app.base.menu.layout.BaseMenuAdapter
        public Drawable getMenuItemDrawable(int position) {
            super.getMenuItemDrawable(position);
            String itemId = this.mItems.get(position);
            if (itemId.equals(ExposureModeController.INTELLIGENT_AUTO_MODE)) {
                Resources resources = getResources();
                int resId = resources.getIdentifier(ExposureModeMenuMovieFeatureLayout.INTELLIGENT_AUTO_ICON, null, getContext().getPackageName());
                resources.getDrawable(resId);
            }
            return super.getMenuItemDrawable(position);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View currentView = super.onCreateView(inflater, container, savedInstanceState);
        this.mAdapter = new ExposureModeMenuMovieFeatureAdapter(getActivity(), R.layout.menu_sub_adapter, this.mService);
        return currentView;
    }
}
