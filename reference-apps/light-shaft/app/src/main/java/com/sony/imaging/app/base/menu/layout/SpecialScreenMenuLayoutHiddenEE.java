package com.sony.imaging.app.base.menu.layout;

import android.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/* loaded from: classes.dex */
public class SpecialScreenMenuLayoutHiddenEE extends SpecialScreenMenuLayout {
    public static final String MENU_ID = "ID_SPECIALSCREENMENULAYOUTHIDDENEE";
    private ImageView mBlack;

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if ((view instanceof ViewGroup) && this.mBlack == null) {
            this.mBlack = new ImageView(getActivity());
            this.mBlack.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            this.mBlack.setImageResource(R.color.black);
            ((ViewGroup) view).addView(this.mBlack, 0);
        }
        return view;
    }
}
