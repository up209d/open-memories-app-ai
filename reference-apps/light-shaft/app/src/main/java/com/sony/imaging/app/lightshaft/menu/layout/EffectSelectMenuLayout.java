package com.sony.imaging.app.lightshaft.menu.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.HistoryItem;
import com.sony.imaging.app.base.menu.MenuDataParcelable;
import com.sony.imaging.app.base.menu.layout.SpecialScreenArea;
import com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout;
import com.sony.imaging.app.base.menu.layout.SpecialScreenView;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.lightshaft.AppContext;
import com.sony.imaging.app.lightshaft.AppLog;
import com.sony.imaging.app.lightshaft.LightShaftBackUpKey;
import com.sony.imaging.app.lightshaft.R;
import com.sony.imaging.app.lightshaft.shooting.ShaftsEffect;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class EffectSelectMenuLayout extends SpecialScreenMenuLayout {
    private static final String BACK = "back";
    private static final String TAG = "EffectSelectMenuLayout";
    private ImageView mBlack;
    private int mCurrentSelectedState;
    private int mLastStoredState;
    ViewGroup mCurrentView = null;
    private ImageView mBackgroundImageView = null;
    private TextView mGuideTextView = null;
    private HistoryItem mLastItemId = null;
    private FooterGuide mFooterGuide = null;

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.menu_ls_effectselect);
        initializeView();
        if (this.mBlack == null) {
            this.mBlack = new ImageView(AppContext.getAppContext());
            this.mBlack.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            this.mBlack.setImageResource(android.R.color.black);
            this.mCurrentView.addView(this.mBlack, 0);
        }
        return this.mCurrentView;
    }

    private void initializeView() {
        this.mSpecialScreenView = (SpecialScreenView) this.mCurrentView.findViewById(R.id.listview);
        this.mViewArea = (SpecialScreenArea) this.mCurrentView.findViewById(R.id.listviewarea);
        this.mScreenTitleView = (TextView) this.mCurrentView.findViewById(R.id.menu_screen_title);
        this.mScreenTitleView.setText("Effect Select");
        this.mItemNameView = (TextView) this.mCurrentView.findViewById(R.id.menu_item_name);
        this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide);
        this.mBackgroundImageView = (ImageView) this.mCurrentView.findViewById(R.id.sceneselection_icon);
        this.mGuideTextView = (TextView) this.mCurrentView.findViewById(R.id.guide_view);
        if (this.mItemNameView != null) {
            this.mItemNameView.setGravity(3);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        getLastStoredValues();
        setFooterGuide();
        update(this.mCurrentSelectedState - 1);
    }

    private void setFooterGuide() {
        if (this.mLastItemId == null) {
            this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), android.R.string.hour, android.R.string.hour_picker_description));
        } else {
            this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), android.R.string.zen_mode_feature_name, android.R.string.httpErrorRedirectLoop));
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        this.mSpecialScreenView = null;
        this.mService = null;
        this.mAdapter = null;
        this.mScreenTitleView = null;
        if (this.mBackgroundImageView != null) {
            this.mBackgroundImageView.setImageResource(0);
        }
        this.mBackgroundImageView = null;
        this.mGuideTextView = null;
        this.mBlack = null;
        super.onDestroyView();
    }

    protected void update(int position) {
        AppLog.enter(TAG, "started time for update theme selection view");
        this.mScreenTitleView.setText("");
        String itemid = this.mAdapter.getItem(position);
        BackUpUtil.getInstance().setPreference(LightShaftBackUpKey.SELECTED_EFFECT, itemid);
        AppLog.info(TAG, "doItemClickProcessing itemID= " + itemid);
        if (this.mItemNameView != null) {
            this.mItemNameView.setText(this.mService.getMenuItemText(itemid));
            this.mItemNameView.setVisibility(0);
        }
        if (this.mGuideTextView != null) {
            this.mGuideTextView.setText(this.mService.getMenuItemGuideText(itemid));
            this.mGuideTextView.setVisibility(0);
        }
        setBackgroundImage(this.mBackgroundImageView, position);
        AppLog.exit(TAG, "started time for update theme selection view");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void requestCautionTrigger(String itemId) {
        CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_FUNCTION);
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        return pushedUpKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        return pushedDownKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        if (this.mLastItemId == null) {
            getActivity().finish();
            return 1;
        }
        int returnState = super.pushedMenuKey();
        return returnState;
    }

    private void setPreviousMenuID() {
        if (this.data.getString(ExposureModeController.EXPOSURE_MODE) != null) {
            this.mService.popMenuHistory();
            this.mLastItemId = null;
        } else {
            this.mLastItemId = this.mService.popMenuHistory();
            this.mService.pushMenuHistory(this.mLastItemId);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        deinitializeView();
    }

    private void deinitializeView() {
        if (this.mCurrentView != null) {
            this.mCurrentView.destroyDrawingCache();
        }
        this.mBackgroundImageView.setBackgroundResource(0);
        this.mBackgroundImageView.setImageBitmap(null);
        releaseImageViewDrawable(this.mBackgroundImageView);
        this.mFooterGuide = null;
        this.mGuideTextView = null;
        this.mItemNameView = null;
        this.mLastItemId = null;
        this.mCurrentView = null;
        this.mViewArea = null;
        releaseImageViewDrawable(this.mBlack);
        System.gc();
    }

    private void releaseImageViewDrawable(ImageView imageView) {
        if (imageView != null) {
            if (imageView.getDrawable() != null) {
                imageView.getDrawable().setCallback(null);
            }
            imageView.setImageDrawable(null);
        }
    }

    private void getLastStoredValues() {
        MenuDataParcelable parcelable = (MenuDataParcelable) this.data.getParcelable(MenuDataParcelable.KEY);
        if (!BACK.equals(parcelable.getItemId())) {
            this.mLastStoredState = ShaftsEffect.getInstance().getParameters().getEffect();
            this.mCurrentSelectedState = this.mLastStoredState;
        }
        setPreviousMenuID();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenView.OnItemSelectedListener
    public void onItemSelected(SpecialScreenView adapterView, View view, int position, long id) {
        update(position);
        super.onItemSelected(adapterView, view, position, id);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedGuideFuncKey() {
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout
    public void cancelValue() {
        update(this.mLastStoredState - 1);
        super.cancelValue();
    }

    private void setBackgroundImage(ImageView imageView, int itemid) {
        int effect = itemid + 1;
        switch (effect) {
            case 1:
                imageView.setImageResource(R.drawable.s_16_dd_parts_image_ray);
                return;
            case 2:
                imageView.setImageResource(R.drawable.s_16_dd_parts_image_star);
                return;
            case 3:
                imageView.setImageResource(R.drawable.s_16_dd_parts_image_flare);
                return;
            case 4:
                imageView.setImageResource(R.drawable.s_16_dd_parts_image_beam);
                return;
            default:
                imageView.setImageResource(R.drawable.s_16_dd_parts_image_ray);
                return;
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onShuttleTurnedToLeft(KeyEvent event) {
        return pushedUpKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onShuttleTurnedToRight(KeyEvent event) {
        return pushedDownKey();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void doItemClickProcessing(String itemid) {
        AppLog.info(TAG, "doItemClickProcessing itemID= " + itemid);
        super.doItemClickProcessing(itemid);
        int effect = ShaftsEffect.getInstance().getParameters().getEffect();
        this.mLastStoredState = effect;
        this.mCurrentSelectedState = effect;
    }
}
