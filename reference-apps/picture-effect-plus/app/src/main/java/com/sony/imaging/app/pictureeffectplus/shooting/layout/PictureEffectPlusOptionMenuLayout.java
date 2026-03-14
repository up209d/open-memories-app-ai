package com.sony.imaging.app.pictureeffectplus.shooting.layout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.base.menu.layout.BaseMenuAdapter;
import com.sony.imaging.app.base.menu.layout.SpecialScreenArea;
import com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout;
import com.sony.imaging.app.base.menu.layout.SpecialScreenView;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.pictureeffectplus.AppLog;
import com.sony.imaging.app.pictureeffectplus.R;
import com.sony.imaging.app.pictureeffectplus.shooting.PictureEffectEEState;
import com.sony.imaging.app.pictureeffectplus.shooting.camera.PictureEffectPlusController;
import com.sony.imaging.app.util.PTag;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class PictureEffectPlusOptionMenuLayout extends SpecialScreenMenuLayout {
    private static final long DELAY_TIME = 350;
    private static final String MODE_PART_COLOR_CH0 = "part-color-ch0";
    private static final String MODE_PART_COLOR_CH1 = "part-color-ch1";
    private static final String MODE_PART_COLOR_PLUS = "part-color-plus";
    private static final int PART_COLOR_STATUS_OFF = 0;
    private static final int PART_COLOR_STATUS_ON = 1;
    protected static final StringBuilder STRBUILD = new StringBuilder();
    private static final String TAG = "PictureEffectPlusOptionMenuLayout";
    private static final String TAG_GET_MENU_ITEM_LIST = "getMenuItemList optionMenuLayout";
    private static final String TAG_ON_CREATE_VIEW = "onCreateView optionMenuLayout";
    private static final String TAG_ON_ITEM_SELECTED = "onItemSelected optionMenuLayout";
    private static final String TAG_ON_MENU_ITEM_CLICK = "onMenuItemClick optionMenuLayout";
    private static final String TAG_ON_MENU_ITEM_SELECTED = "onMenuItemSelected optionMenuLayout";
    private static final String TAG_ON_RESUME = "onResume optionMenuLayout";
    private static final String TAG_ON_S1_PUSH = "PictureEffectPlusOptionMenuLayout pushedSK1Key";
    private PictureEffectPlusController mController;
    ArrayList<String> mList;
    private ViewGroup mCurrentView = null;
    private ImageView mBackgroundImageView = null;
    private TextView mGuideTextView = null;
    protected String FUNC_NAME = "";
    private String mSelectedItemId = "part-color-plus";
    public String SELECT_EFFECT_ID = null;
    private int mEffectPosition = 0;
    private int mCurrentSelectedPosition = 0;
    private String mPreviousSelectedeffect = null;
    private FooterGuide mFooterGuide = null;
    private TextView mScreenTitle = null;
    private final int ITEM_COUNT_5 = 5;
    private SpecialScreenArea mViewArea = null;

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PTag.start(TAG_ON_CREATE_VIEW);
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mCurrentView = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        Context context = getActivity().getApplicationContext();
        this.mSpecialScreenView = (SpecialScreenView) this.mCurrentView.findViewById(R.id.listview);
        this.mViewArea = (SpecialScreenArea) this.mCurrentView.findViewById(R.id.listviewarea);
        this.mService = new BaseMenuService(context);
        this.mService.setMenuItemId(PictureEffectPlusController.PICTUREEFFECTPLUS);
        this.mOptionService = new BaseMenuService(context);
        this.mAdapter = new PictureEffectSpecialBaseMenuAdapter(context, R.layout.menu_sub_adapter, this.mService);
        this.mOptionAdapter = new SpecialScreenMenuLayout.SpecialBaseMenuAdapter(context, R.layout.menu_sub_adapter, this.mOptionService);
        this.mScreenTitleView = (TextView) this.mCurrentView.findViewById(R.id.menu_screen_title);
        this.mScreenTitle = (TextView) this.mCurrentView.findViewById(R.id.screen_title);
        this.mScreenTitleView.setVisibility(8);
        this.mBackgroundImageView = (ImageView) this.mCurrentView.findViewById(R.id.sceneselection_icon);
        this.mGuideTextView = (TextView) this.mCurrentView.findViewById(R.id.guide_view);
        this.mController = PictureEffectPlusController.getInstance();
        PictureEffectEEState.setIsMenuStateAdd(false);
        this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide);
        if (!this.mController.isAppTopOpenFromMenu() && !this.mController.isShootingScreenOpened()) {
            this.mFooterGuide.setData(new FooterGuideDataResId(context, android.R.string.hour, android.R.string.hour_picker_description));
        } else {
            this.mFooterGuide.setData(new FooterGuideDataResId(context, android.R.string.zen_mode_feature_name, android.R.string.httpErrorRedirectLoop));
        }
        PictureEffectPlusController.getInstance().checkPartColorSettings();
        Log.i(TAG, "onCreateView");
        AppLog.exit(TAG, AppLog.getMethodName());
        PTag.end(TAG_ON_CREATE_VIEW);
        return this.mCurrentView;
    }

    /* loaded from: classes.dex */
    public class PictureEffectSpecialBaseMenuAdapter extends SpecialScreenMenuLayout.SpecialBaseMenuAdapter {
        public PictureEffectSpecialBaseMenuAdapter(Context context, int ResId, BaseMenuService service) {
            super(context, ResId, service);
        }

        @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout.SpecialBaseMenuAdapter, com.sony.imaging.app.base.menu.layout.BaseMenuAdapter, android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            BaseMenuAdapter.ViewHolder holder = (BaseMenuAdapter.ViewHolder) view.getTag();
            holder.iv.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_special_button));
            return view;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout.SpecialBaseMenuAdapter, com.sony.imaging.app.base.menu.layout.BaseMenuAdapter
        public Drawable getMenuItemDrawable(int position) {
            BaseMenuService baseMenuService = this.mService;
            String itemId = this.mItems.get(position);
            Drawable d = this.mService.getMenuItemDrawable(itemId);
            return d;
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout
    protected int getLayoutID() {
        return R.layout.pictureeffectplus_menu_scn_option;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        PTag.start(TAG_ON_RESUME);
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mSpecialScreenView = (SpecialScreenView) this.mCurrentView.findViewById(R.id.listview);
        super.onResume();
        this.mScreenTitle.setText(R.string.STRID_FUNC_EFFECT_MASTER);
        this.mUpdater.setDelayTime(DELAY_TIME);
        this.mController = PictureEffectPlusController.getInstance();
        this.mPreviousSelectedeffect = this.mController.getBackupEffectValue();
        this.mBackgroundImageView.setBackgroundResource(getBackgroundDrawable(this.mPreviousSelectedeffect));
        this.SELECT_EFFECT_ID = this.mPreviousSelectedeffect;
        this.mSelectedItemId = this.mPreviousSelectedeffect;
        this.mEffectPosition = this.mService.getMenuItemList().indexOf(this.mPreviousSelectedeffect);
        this.mItemNameView.setText(this.mService.getMenuItemText(this.mPreviousSelectedeffect));
        this.mGuideTextView.setText(this.mService.getMenuItemGuideText(this.mPreviousSelectedeffect));
        handleSelection(this.mEffectPosition);
        PictureEffectPlusController.getInstance().checkPartColorSettings();
        this.mController.forceEffectOptionSetting();
        getMenuData().getPreviousMenuLayoutId();
        this.mList = this.mService.getSupportedItemList();
        AppLog.exit(TAG, AppLog.getMethodName());
        PTag.end(TAG_ON_RESUME);
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenView.OnItemSelectedListener
    public void onItemSelected(SpecialScreenView parent, View view, int position, long id) {
        PTag.start(TAG_ON_ITEM_SELECTED);
        super.onItemSelected(parent, view, position, id);
        AppLog.enter(TAG, AppLog.getMethodName());
        int pos = this.mSpecialScreenView.getSelectedItemPosition();
        this.mEffectPosition = pos;
        this.mCurrentSelectedPosition = pos;
        this.mSelectedItemId = this.mList.get(pos);
        this.SELECT_EFFECT_ID = this.mSelectedItemId;
        this.mItemNameView.setText(this.mService.getMenuItemText(this.mSelectedItemId));
        this.mGuideTextView.setText(this.mService.getMenuItemGuideText(this.mSelectedItemId));
        this.mBackgroundImageView.setBackgroundResource(getBackgroundDrawable(this.mSelectedItemId));
        AppLog.exit(TAG, AppLog.getMethodName());
        PTag.end(TAG_ON_ITEM_SELECTED);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK1Key() {
        PTag.start(TAG_ON_S1_PUSH);
        closeMenuLayout(null);
        AppRoot root = (AppRoot) getActivity();
        root.finish();
        PTag.end(TAG_ON_S1_PUSH);
        this.FUNC_NAME = "pushedSK1Key";
        STRBUILD.replace(0, STRBUILD.length(), this.FUNC_NAME);
        Log.i(TAG, STRBUILD.toString());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (!this.mPreviousSelectedeffect.equals(this.mSelectedItemId)) {
            this.mController.setValue(PictureEffectPlusController.PICTUREEFFECTPLUS, this.mPreviousSelectedeffect);
        }
        if (!this.mController.isAppTopOpenFromMenu() && !this.mController.isShootingScreenOpened()) {
            getActivity().finish();
        } else {
            cancelSetValue();
            openPreviousMenu();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void cancelSetValue() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mUpdater != null) {
            this.mUpdater.cancelMenuUpdater();
        }
        if (this.mPreviousSelectedeffect != null) {
            if (this.mService != null) {
                this.mService.execCurrentMenuItem(this.mPreviousSelectedeffect, false);
            } else {
                return;
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mSelectedItemId.length() > 0) {
            if (this.mSelectedItemId.equals("part-color-plus")) {
                if (this.mController.getPartColorCurrentPlate() == 0) {
                    this.mSelectedItemId = "part-color-ch0";
                } else if (this.mController.getPartColorCurrentPlate() == 1) {
                    this.mSelectedItemId = "part-color-ch0";
                } else if (this.mController.getPartColorCurrentPlate() == 2) {
                    this.mSelectedItemId = "part-color-ch1";
                } else if (this.mController.getPartColorCurrentPlate() == 3) {
                    this.mSelectedItemId = "part-color-ch1";
                }
                this.mController.setPrevSelectedPlate(this.mController.getPartColorCurrentPlate());
            } else if (this.mSelectedItemId.equals(PictureEffectPlusController.MODE_MINIATURE_PLUS)) {
                String plateOneEffect = this.mController.getBackupEffectOptionValue(PictureEffectPlusController.MODE_MINIATURE_AREA);
                if (plateOneEffect.equals("auto")) {
                    this.mController.setSelectedPlate(0);
                }
                if (this.mController.getSelectedPlate() == 0) {
                    this.mSelectedItemId = PictureEffectPlusController.MODE_MINIATURE_AREA;
                } else {
                    this.mSelectedItemId = PictureEffectPlusController.MODE_MINIATURE_EFFECT;
                }
                this.mController.setPrevSelectedPlateForMiniAndToy(this.mController.getSelectedPlate());
            } else if (this.mSelectedItemId.equals(PictureEffectPlusController.MODE_TOY_CAMERA_PLUS)) {
                if (this.mController.getSelectedPlate() == 0) {
                    this.mSelectedItemId = PictureEffectPlusController.MODE_TOY_CAMERA_COLOR;
                } else {
                    this.mSelectedItemId = PictureEffectPlusController.MODE_TOY_CAMERA_DARKNESS;
                }
                this.mController.setPrevSelectedPlateForMiniAndToy(this.mController.getSelectedPlate());
            }
            this.mController.keepCurrentEffectOptionSetting(PictureEffectPlusController.EFFECT_SET_STATUS);
            this.mController.setComingFromApplicationSettings(false);
            doItemClickProcessing(this.mSelectedItemId);
        }
        AppLog.enter(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mController.forceEffectOptionSetting();
        if (PictureEffectPlusController.getInstance().getBackupEffectValue().equals("part-color-plus")) {
            PictureEffectPlusController.getInstance().setCustomSet2CustomOnBackupOptionValue();
            PictureEffectPlusController.getInstance().setPartColorStatus(1);
        } else {
            PictureEffectPlusController.getInstance().setPartColorStatus(0);
        }
        PictureEffectPlusController.getInstance().setSADriveMode();
        super.closeLayout();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mSpecialScreenView != null) {
            super.onPause();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void doItemClickProcessing(String itemid) {
        if (this.mUpdater != null) {
            this.mUpdater.cancelMenuUpdater();
        }
        if (itemid.equals("ExitApplication")) {
            BaseApp baseApp = (BaseApp) getActivity();
            baseApp.finish();
            return;
        }
        if (this.mService != null) {
            if (!this.mService.isMenuItemValid(itemid)) {
                requestCautionTrigger(itemid);
                return;
            }
            String execType = this.mService.getMenuItemExecType(itemid);
            String nextFragmentID = this.mService.getMenuItemNextMenuID(itemid);
            if (MenuTable.NEXT_LAYOUT.equals(execType) || MenuTable.NEXT_LAYOUT_WITHOUT_SET.equals(execType)) {
                if (MenuTable.NEXT_LAYOUT.equals(execType)) {
                    this.mService.execCurrentMenuItem(this.SELECT_EFFECT_ID);
                }
                if (nextFragmentID != null) {
                    PTag.start("Menu open next MenuLayout");
                    openNextMenu(itemid, nextFragmentID);
                    return;
                } else {
                    Log.e(TAG, "Can't open the next MenuLayout");
                    return;
                }
            }
            if (MenuTable.NEXT_STATE.equals(execType)) {
                PTag.start("MenuItem Clicked(NEXT_STATE)");
                openNextState(nextFragmentID, null);
            } else if (MenuTable.SET_VALUE.equals(execType) || MenuTable.SET_VALUE_ONLY_CLICK.equals(execType)) {
                PTag.start("MenuItem Clicked(SET_VALUE or SET_VALUE_ONLY_CLICK)");
                this.mService.execCurrentMenuItem(itemid);
                postSetValue();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void onMenuItemClick(String itemid) {
        PTag.start(TAG_ON_MENU_ITEM_CLICK);
        PictureEffectPlusController.getInstance().setPartColorStatus(0);
        if ("part-color-plus".equals(itemid) || "part-color-ch0".equals(itemid) || "part-color-ch1".equals(itemid)) {
            PictureEffectPlusController.getInstance().setPartColorStatus(1);
        }
        if (this.mUpdater != null) {
            this.mUpdater.finishMenuUpdater();
        }
        PictureEffectPlusController.getInstance().keepCurrentEffectOptionSetting(PictureEffectPlusController.EFFECT_SET_STATUS);
        super.onMenuItemClick(itemid);
        PTag.end(TAG_ON_MENU_ITEM_CLICK);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void onMenuItemSelected(String itemid) {
        PTag.start(TAG_ON_MENU_ITEM_SELECTED);
        this.mGuideTextView.setVisibility(0);
        super.onMenuItemSelected(itemid);
        this.mController.forceEffectOptionSetting();
        this.FUNC_NAME = "onMenuItemSelected";
        STRBUILD.replace(0, STRBUILD.length(), this.FUNC_NAME);
        Log.i(TAG, STRBUILD.toString());
        Log.i(TAG, "onMenuItemSelected");
        PTag.end(TAG_ON_MENU_ITEM_SELECTED);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected String getMenuLayoutID() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return "ID_PICTUREEFFECTPLUSOPTIONMENULAYOUT";
    }

    private int getBackgroundDrawable(String itemId) {
        int drawable = -1;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (itemId.equalsIgnoreCase("part-color-plus")) {
            drawable = R.drawable.p_16_dd_parts_pe_image_part_color_plus;
        } else if (itemId.equalsIgnoreCase(PictureEffectPlusController.MODE_MINIATURE_PLUS)) {
            drawable = R.drawable.p_16_dd_parts_pe_image_miniature_plus;
        } else if (itemId.equalsIgnoreCase(PictureEffectPlusController.MODE_TOY_CAMERA_PLUS)) {
            drawable = R.drawable.p_16_dd_parts_pe_image_toycamera_plus;
        } else if (itemId.equalsIgnoreCase("watercolor")) {
            drawable = R.drawable.p_16_dd_parts_pe_image_watercolor;
        } else if (itemId.equalsIgnoreCase("illust") || itemId.equalsIgnoreCase(PictureEffectPlusController.ILLUST_HIGH) || itemId.equalsIgnoreCase(PictureEffectPlusController.ILLUST_LOW) || itemId.equalsIgnoreCase(PictureEffectPlusController.ILLUST_MID)) {
            drawable = R.drawable.p_16_dd_parts_pe_image_illustration;
        } else if (itemId.equalsIgnoreCase(PictureEffectController.MODE_SOFT_FOCUS) || itemId.equalsIgnoreCase(PictureEffectController.SOFT_FOCUS_HIGH) || itemId.equalsIgnoreCase(PictureEffectController.SOFT_FOCUS_MEDIUM) || itemId.equalsIgnoreCase(PictureEffectController.SOFT_FOCUS_LOW)) {
            drawable = R.drawable.p_16_dd_parts_pe_image_soft_focus;
        } else if (itemId.equalsIgnoreCase(PictureEffectController.MODE_SOFT_HIGH_KEY) || itemId.equalsIgnoreCase(PictureEffectPlusController.SOFT_HIGH_KEY_BLUE) || itemId.equalsIgnoreCase(PictureEffectPlusController.SOFT_HIGH_KEY_GREEN) || itemId.equalsIgnoreCase(PictureEffectPlusController.SOFT_HIGH_KEY_PINK)) {
            drawable = R.drawable.p_16_dd_parts_pe_image_soft_high_key_plus;
        } else if (itemId.equalsIgnoreCase(PictureEffectController.MODE_HDR_ART) || itemId.equalsIgnoreCase(PictureEffectController.HDR_ART_HIGH) || itemId.equalsIgnoreCase(PictureEffectController.HDR_ART_LOW) || itemId.equalsIgnoreCase(PictureEffectController.HDR_ART_MEDIUM)) {
            drawable = R.drawable.p_16_dd_parts_pe_image_hdr;
        } else if (itemId.equalsIgnoreCase(PictureEffectController.MODE_ROUGH_MONO)) {
            drawable = R.drawable.p_16_dd_parts_pe_image_high_contrast_mono;
        } else if (itemId.equalsIgnoreCase(PictureEffectController.MODE_RETRO_PHOTO)) {
            drawable = R.drawable.p_16_dd_parts_pe_image_retro;
        } else if (itemId.equalsIgnoreCase(PictureEffectController.MODE_POSTERIZATION) || itemId.equalsIgnoreCase(PictureEffectController.POSTERIZATION_COLOR) || itemId.equalsIgnoreCase(PictureEffectController.POSTERIZATION_MONO)) {
            drawable = R.drawable.p_16_dd_parts_pe_image_posterization;
        } else if (itemId.equalsIgnoreCase(PictureEffectController.MODE_POP_COLOR)) {
            drawable = R.drawable.p_16_dd_parts_pe_image_pop_color;
        } else if (itemId.equalsIgnoreCase(PictureEffectController.MODE_RICH_TONE_MONOCHROME)) {
            drawable = R.drawable.p_16_dd_parts_pe_image_rich_tone_mono;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return drawable;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedGuideFuncKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
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

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        this.mScreenTitleView = null;
        this.mItemNameView = null;
        this.mGuideTextView = null;
        this.mBackgroundImageView = null;
        this.mSpecialScreenView = null;
        this.mCurrentView = null;
        super.onDestroy();
    }

    private void handleSelection(int position) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int mBlock = getMenuItemList().size();
        if (mBlock > 5) {
            mBlock = 5;
        }
        int firstPosition = (position - mBlock) + 1;
        if (firstPosition < 0) {
            firstPosition = 0;
        }
        this.mSpecialScreenView.setSelection(firstPosition, position);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedModeDial() {
        return super.turnedModeDial();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout
    protected ArrayList<String> getOptionMenuItemList(String itemId) {
        return null;
    }
}
