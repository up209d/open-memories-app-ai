package com.sony.imaging.app.base.menu.layout;

import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.R;

/* loaded from: classes.dex */
public class MovieModeMenuLayout extends SpecialScreenMenuLayout {
    private static final Drawable INDETERMINATE_BG_DRAWABLE = new ColorDrawable(-16777216);
    private ImageView mBackgroundImageView = null;
    private TextView mGuideTextView = null;

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup currentView = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        this.mBackgroundImageView = (ImageView) currentView.findViewById(R.id.sceneselection_icon);
        this.mGuideTextView = (TextView) currentView.findViewById(R.id.guide_view);
        this.mBackgroundImageView.setBackgroundDrawable(INDETERMINATE_BG_DRAWABLE);
        return currentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout
    protected int getLayoutID() {
        return R.layout.menu_scn_option;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        update(this.mSpecialScreenView.getSelectedItemPosition());
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        this.mBackgroundImageView = null;
        this.mGuideTextView = null;
        super.onDestroyView();
    }

    protected void update(int position) {
        String itemid = (String) this.mSpecialScreenView.getItemAtPosition(position);
        if (this.mItemNameView != null) {
            this.mItemNameView.setText(this.mService.getMenuItemText(itemid));
            this.mItemNameView.setVisibility(0);
        }
        String optionStr = this.mService.getMenuItemOptionStr(itemid);
        if (optionStr != null && optionStr.length() > 0 && this.mBackgroundImageView != null) {
            Resources res = getActivity().getApplicationContext().getResources();
            int resId = res.getIdentifier(optionStr, null, getActivity().getPackageName());
            this.mBackgroundImageView.setBackgroundResource(resId);
        }
        if (this.mGuideTextView != null) {
            this.mGuideTextView.setText(this.mService.getMenuItemGuideText(itemid));
            this.mGuideTextView.setVisibility(0);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenView.OnItemSelectedListener
    public void onItemSelected(SpecialScreenView parent, View view, int position, long id) {
        if (this.mGuideTextView != null) {
            this.mGuideTextView.setVisibility(4);
        }
        this.mBackgroundImageView.setBackgroundDrawable(INDETERMINATE_BG_DRAWABLE);
        update(position);
        super.onItemSelected(parent, view, position, id);
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        return pushedDownKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        return pushedUpKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedGuideFuncKey() {
        return -1;
    }
}
