package com.sony.imaging.app.liveviewgrading.menu.layout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout;
import com.sony.imaging.app.base.menu.layout.SpecialScreenView;
import com.sony.imaging.app.liveviewgrading.AppLog;
import com.sony.imaging.app.liveviewgrading.R;
import com.sony.imaging.app.liveviewgrading.menu.controller.ColorGradingController;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class ColorGradingGroupSelectionScreen extends SpecialScreenMenuLayout {
    private static final int ITEM_COUNT_5 = 5;
    private static ArrayList<String> mList = new ArrayList<>();
    private String TAG = AppLog.getClassName();
    private ColorGradingController mController = null;
    private TextView mGuideTxtVw = null;
    private int mSelectedPosition = 0;
    private int mPrevSelectedPosition = 0;
    private ImageView mBackgroundImageView = null;
    private FooterGuide mFooterGuide = null;
    private TextView mScreenTitleTxtVw = null;

    /* loaded from: classes.dex */
    public class ColorGradingAdapter extends SpecialScreenMenuLayout.SpecialBaseMenuAdapter {
        public ColorGradingAdapter(Context context, int ResId, BaseMenuService service) {
            super(context, ResId, service);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout.SpecialBaseMenuAdapter, com.sony.imaging.app.base.menu.layout.BaseMenuAdapter
        public Drawable getMenuItemDrawable(int position) {
            return getMenuItemDrawable(this.mItems.get(position));
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View currentView = super.onCreateView(inflater, container, savedInstanceState);
        this.mAdapter = new ColorGradingAdapter(getActivity(), R.layout.menu_sub_adapter, this.mService);
        this.mGuideTxtVw = (TextView) currentView.findViewById(R.id.guide_view);
        this.mBackgroundImageView = (ImageView) currentView.findViewById(R.id.backgroundImgVw);
        this.mController = ColorGradingController.getInstance();
        this.mFooterGuide = (FooterGuide) currentView.findViewById(R.id.footer_guide);
        if (!this.mController.isShootingScreenOpened()) {
            this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), android.R.string.hour, android.R.string.hour_picker_description));
        } else {
            this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), android.R.string.zen_mode_feature_name, android.R.string.httpErrorRedirectLoop));
        }
        this.mScreenTitleView.setVisibility(4);
        this.mScreenTitleTxtVw = (TextView) currentView.findViewById(R.id.screen_title);
        this.mScreenTitleTxtVw.setText(R.string.STRID_FUNC_LVG_MENU_ITEM_GROUP_SELETION);
        return currentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout
    protected int getLayoutID() {
        return R.layout.colorgrading_menu_group_selection;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.mController = ColorGradingController.getInstance();
        String selectedGroup = this.mController.getLastSelectedGroup();
        this.mSelectedPosition = this.mService.getMenuItemList().indexOf(selectedGroup);
        this.mPrevSelectedPosition = this.mSelectedPosition;
        String itemId = this.mAdapter.getItem(this.mSelectedPosition);
        this.mGuideTxtVw.setText(this.mService.getMenuItemGuideText(itemId));
        this.mBackgroundImageView.setBackgroundResource(getBackgroundDrawable(itemId));
        handleSelection(this.mSelectedPosition);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenView.OnItemSelectedListener
    public void onItemSelected(SpecialScreenView adapterView, View view, int position, long id) {
        super.onItemSelected(adapterView, view, position, id);
        String itemId = this.mAdapter.getItem(position);
        this.mGuideTxtVw.setText(this.mService.getMenuItemGuideText(itemId));
        this.mSelectedPosition = position;
        this.mBackgroundImageView.setBackgroundResource(getBackgroundDrawable(itemId));
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mBackgroundImageView = null;
        this.mGuideTxtVw = null;
        AppLog.exit(this.TAG, AppLog.getMethodName());
        super.onDestroy();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout
    protected ArrayList<String> getOptionMenuItemList(String itemId) {
        return mList;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mSelectedPosition = this.mPrevSelectedPosition;
        if (!this.mController.isShootingScreenOpened()) {
            getActivity().finish();
            AppLog.exit(this.TAG, AppLog.getMethodName());
        } else {
            AppLog.exit(this.TAG, AppLog.getMethodName());
            openPreviousMenu();
        }
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDeleteKeyPushed(KeyEvent event) {
        Log.e("", "INSIDE onDeleteKeyPushed");
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDeleteKeyReleased(KeyEvent event) {
        Log.e("", "INSIDE onDeleteKeyReleased");
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        return super.turnedMainDialNext();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        return super.turnedMainDialPrev();
    }

    private int getBackgroundDrawable(String itemId) {
        int drawable = -1;
        AppLog.enter(this.TAG, AppLog.getMethodName());
        int kikilogSubId = -1;
        if (itemId.equalsIgnoreCase(ColorGradingController.STANDARD)) {
            drawable = R.drawable.p_16_dd_parts_lvg_group_std_image;
            kikilogSubId = 4209;
        } else if (itemId.equalsIgnoreCase(ColorGradingController.CINEMA)) {
            drawable = R.drawable.p_16_dd_parts_lvg_group_cinema_image;
            kikilogSubId = 4210;
        } else if (itemId.equalsIgnoreCase(ColorGradingController.EXTREME)) {
            drawable = R.drawable.p_16_dd_parts_lvg_group_xtreme_image;
            kikilogSubId = 4211;
        }
        this.mController.startKikiLog(kikilogSubId);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return drawable;
    }

    private void handleSelection(int position) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        int mBlock = getMenuItemList().size();
        if (mBlock > 5) {
            mBlock = 5;
        }
        int firstPosition = (position - mBlock) + 1;
        if (firstPosition < 0) {
            firstPosition = 0;
        }
        this.mSpecialScreenView.setSelection(firstPosition, position);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }
}
