package com.sony.imaging.app.lightgraffiti.menu.layout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import com.sony.imaging.app.lightgraffiti.R;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGAppTopController;
import com.sony.imaging.app.lightgraffiti.util.AppLog;
import com.sony.imaging.app.lightgraffiti.util.LGUtility;

/* loaded from: classes.dex */
public class LGPaintingDurationLayout extends SpecialScreenMenuLayout {
    private static final int ITEM_COUNT_4 = 4;
    private String TAG = LGPaintingDurationLayout.class.getSimpleName();
    private LGAppTopController mController = null;
    ViewGroup mCurrentView = null;
    private FooterGuide mFooterGuide = null;
    private TextView mGuideTxtVw = null;
    private ImageView mBackgroundImageView = null;
    private TextView mScreenTitleTxtVw = null;
    private int mSelectedPosition = 0;
    private int mPrevSelectedPosition = 0;

    /* loaded from: classes.dex */
    public class LightgrafittiAdapter extends SpecialScreenMenuLayout.SpecialBaseMenuAdapter {
        public LightgrafittiAdapter(Context context, int ResId, BaseMenuService service) {
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
        AppLog.enter(this.TAG, "onCreateView");
        View currentView = super.onCreateView(inflater, container, savedInstanceState);
        this.mAdapter = new LightgrafittiAdapter(getActivity(), R.layout.menu_sub_adapter, this.mService);
        this.mGuideTxtVw = (TextView) currentView.findViewById(R.id.guide_view);
        this.mBackgroundImageView = (ImageView) currentView.findViewById(R.id.backgroundImgVw);
        this.mController = LGAppTopController.getInstance();
        this.mFooterGuide = (FooterGuide) currentView.findViewById(R.id.footer_guide);
        if (!this.mController.isShootingScreenOpened()) {
            this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), android.R.string.hour, android.R.string.hour_picker_description));
        } else {
            this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), android.R.string.zen_mode_feature_name, android.R.string.httpErrorRedirectLoop));
        }
        this.mScreenTitleView.setVisibility(4);
        this.mScreenTitleTxtVw = (TextView) currentView.findViewById(R.id.screen_title);
        this.mScreenTitleTxtVw.setText(R.string.STRID_FUNC_LIGHTGRAFFITI_SET_PAINTING_DURATION);
        return currentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout
    protected int getLayoutID() {
        return R.layout.lightgraffiti_layout_menu_painting_duration_selection;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.mController = LGAppTopController.getInstance();
        String selectedDuration = this.mController.getLastSelectedDurationTime();
        this.mSelectedPosition = this.mService.getMenuItemList().indexOf(selectedDuration);
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

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        this.mBackgroundImageView.setBackgroundResource(0);
        this.mBackgroundImageView.setBackgroundDrawable(null);
        LGUtility.getInstance().releaseImageViewDrawable(this.mBackgroundImageView);
        this.mBackgroundImageView = null;
        super.onPause();
    }

    private int getBackgroundDrawable(String itemId) {
        int drawable = -1;
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (itemId.equalsIgnoreCase(LGAppTopController.DURATION_TIME_5)) {
            drawable = R.drawable.p_16_dd_parts_lightgraffiti_theme_5sec_image;
        } else if (itemId.equalsIgnoreCase(LGAppTopController.DURATION_TIME_10)) {
            drawable = R.drawable.p_16_dd_parts_lightgraffiti_theme_10sec_image;
        } else if (itemId.equalsIgnoreCase(LGAppTopController.DURATION_TIME_20)) {
            drawable = R.drawable.p_16_dd_parts_lightgraffiti_theme_20sec_image;
        } else if (itemId.equalsIgnoreCase(LGAppTopController.DURATION_TIME_30)) {
            drawable = R.drawable.p_16_dd_parts_lightgraffiti_theme_30sec_image;
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return drawable;
    }

    private void handleSelection(int position) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        int mBlock = getMenuItemList().size();
        if (mBlock > 4) {
            mBlock = 4;
        }
        int firstPosition = (position - mBlock) + 1;
        if (firstPosition < 0) {
            firstPosition = 0;
        }
        this.mSpecialScreenView.setSelection(firstPosition, position);
        AppLog.exit(this.TAG, AppLog.getMethodName());
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
        AppLog.enter(this.TAG, "INSIDE onDeleteKeyPushed");
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDeleteKeyReleased(KeyEvent event) {
        AppLog.enter(this.TAG, "INSIDE onDeleteKeyReleased");
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

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        pushedCenterKey();
        return super.pushedS1Key();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        if (!LGUtility.getInstance().isShutterSpeedUninitialized() || !LGUtility.getInstance().isLauncherBoot()) {
            return super.onKeyDown(keyCode, event);
        }
        AppLog.info(this.TAG, "ShutterSpeed is uninitialized.");
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        if (!LGUtility.getInstance().isShutterSpeedUninitialized() || !LGUtility.getInstance().isLauncherBoot()) {
            return super.onKeyUp(keyCode, event);
        }
        AppLog.info(this.TAG, "ShutterSpeed is uninitialized.");
        return 1;
    }
}
