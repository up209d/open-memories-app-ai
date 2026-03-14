package com.sony.imaging.app.pictureeffectplus.shooting.layout;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.HistoryItem;
import com.sony.imaging.app.base.menu.layout.PageMenuLayout;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.pictureeffectplus.AppLog;
import com.sony.imaging.app.pictureeffectplus.PictureEffectPlusBackUpKey;
import com.sony.imaging.app.pictureeffectplus.R;
import com.sony.imaging.app.pictureeffectplus.menu.layout.VerticalScrollMenuLayout;
import com.sony.imaging.app.pictureeffectplus.menu.layout.VerticalScrollView;
import com.sony.imaging.app.pictureeffectplus.shooting.camera.PictureEffectPlusController;
import com.sony.imaging.app.pictureeffectplus.shooting.widget.UpdateGaugeText;
import com.sony.imaging.app.pictureeffectplus.shooting.widget.VerticalSeekBar;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class PictureEffectPlusEEDoubleIconSubMenuLayout extends VerticalScrollMenuLayout implements UpdateGaugeText {
    protected static final String DEFTYPE_STRING = "string";
    public static final int PART_COLOR_STATUS_ON = 1;
    protected static final StringBuilder STRBUILD = new StringBuilder();
    private static final String TAG = AppLog.getClassName();
    private static String mPreviousEffectOnFirstPlate = null;
    private static String mPreviousEffectOnSecondPlate = null;
    protected String FUNC_NAME = "";
    private String mSelectedItemId = null;
    private ViewGroup mCurrentView = null;
    private Context mContext = null;
    private PictureEffectPlusController mController = null;
    private int mPlateSecondSelectedPostion = 0;
    private int mPlateOneSelectedPosition = 0;
    private String mCurrentPlateOneSelectedItemId = null;
    private String mCurrentSelectedEffect = null;
    private String mCurrentPlateSecondSelectedItemId = null;
    private ViewGroup mFourPlateRelativeLayout = null;
    private ViewGroup mFourPlateSecondPartRelativeLayout = null;
    private ViewGroup mTwoPlateRelativeLayout = null;
    private ImageView mFirstImgVw = null;
    private ImageView mSecondImgVw = null;
    private ImageView mFirstFocusImgVw = null;
    private ImageView mSecondFocusImgVw = null;
    private ImageView mPartColorFirstPlate = null;
    private ImageView mPartColorSecondPlate = null;
    private ImageView mPartColorThirdPlate = null;
    private ImageView mPartColorFourthPlate = null;
    private ImageView mVerticalGaugeBarBckImgVw = null;
    private ImageView mPartColorFirstFocusPlate = null;
    private ImageView mPartColorSecondFocusPlate = null;
    private ImageView mPartColorThirdFocusPlate = null;
    private ImageView mPartColorFourthFocusPlate = null;
    private TextView mGaugeBarHighTxtVw = null;
    private TextView mGaugeBarLowTxtVw = null;
    private ImageView mGaugeBarImgVw = null;
    private TextView mGaugetext = null;
    private VerticalSeekBar mVerticalSeekBar = null;
    private final int ITEM_COUNT_5 = 5;
    private boolean isMenuKeyPressed = false;
    private FooterGuide mFooterGuide = null;
    private String lastItemID = null;
    private TextView mCurSelectedItemTxtVw = null;
    private int mFirstPlateGaugeBarValue = 0;
    private int mSecondPlateGaugeBarValue = 0;
    private int mPreviousPlateSelected = 0;
    private final int MOVE_UP = 1;
    private final int MOVE_DOWN = 0;
    private int STEP = 32;
    private final int INVISIBLE_ALPHA = 128;
    private final int VISIBLE_ALPHA = 255;
    private final int CUSTOM_SET_POSITION = 5;
    private int mFirstPlateColorPositionOnGaugeBar = -1;
    private int mSecondPlateColorPositionOnGaugeBar = -1;

    @Override // com.sony.imaging.app.pictureeffectplus.menu.layout.VerticalScrollMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mCurrentView = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        this.mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.pictureeffectplus_double_icon_menu_sub);
        this.mContext = getActivity().getApplicationContext();
        this.mVerticalScrollView = (VerticalScrollView) this.mCurrentView.findViewById(R.id.verticalScrollView);
        this.mVerticalScrollView.setVisibility(4);
        this.mService = new BaseMenuService(this.mContext);
        this.lastItemID = this.mService.getMenuItemId();
        this.mCurSelectedItemTxtVw = (TextView) this.mCurrentView.findViewById(R.id.menu_item);
        this.mController = PictureEffectPlusController.getInstance();
        this.mFirstImgVw = (ImageView) this.mCurrentView.findViewById(R.id.first_color_plate);
        this.mSecondImgVw = (ImageView) this.mCurrentView.findViewById(R.id.second_color_plate);
        this.mFourPlateRelativeLayout = (ViewGroup) this.mCurrentView.findViewById(R.id.four_plate_layout);
        this.mTwoPlateRelativeLayout = (ViewGroup) this.mCurrentView.findViewById(R.id.two_plate_layout);
        this.mFourPlateSecondPartRelativeLayout = (ViewGroup) this.mCurrentView.findViewById(R.id.four_plate_layout_second_part);
        this.mScreenTitleView = (TextView) this.mCurrentView.findViewById(R.id.menu_screen_title);
        this.mVerticalSeekBar = (VerticalSeekBar) this.mCurrentView.findViewById(R.id.vertical_Seekbar);
        this.mVerticalGaugeBarBckImgVw = (ImageView) this.mCurrentView.findViewById(R.id.vsb_src);
        this.mVerticalSeekBar.setListener(this);
        this.mGaugeBarImgVw = (ImageView) this.mCurrentView.findViewById(R.id.vsb_src);
        this.mGaugeBarHighTxtVw = (TextView) this.mCurrentView.findViewById(R.id.gauge_high);
        this.mGaugeBarLowTxtVw = (TextView) this.mCurrentView.findViewById(R.id.gauge_low);
        this.mGaugetext = (TextView) this.mCurrentView.findViewById(R.id.vertical_sb_progresstext);
        this.mCurrentSelectedEffect = this.mController.getBackupEffectValue();
        if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_PART_COLOR_PLUS)) {
            this.mTwoPlateRelativeLayout.setVisibility(8);
            this.mFourPlateRelativeLayout.setVisibility(0);
            this.mFourPlateSecondPartRelativeLayout.setVisibility(0);
            this.mPartColorFirstPlate = (ImageView) this.mCurrentView.findViewById(R.id.first_color_four_plate);
            this.mPartColorSecondPlate = (ImageView) this.mCurrentView.findViewById(R.id.second_color_four_plate);
            this.mPartColorThirdPlate = (ImageView) this.mCurrentView.findViewById(R.id.first_color_gauge_bar_four_plate);
            this.mPartColorFourthPlate = (ImageView) this.mCurrentView.findViewById(R.id.second_color_gauge_bar_four_plate);
            this.mPartColorFirstFocusPlate = (ImageView) this.mCurrentView.findViewById(R.id.first_color_four_focus_plate);
            this.mPartColorSecondFocusPlate = (ImageView) this.mCurrentView.findViewById(R.id.second_color_four_focus_plate);
            this.mPartColorThirdFocusPlate = (ImageView) this.mCurrentView.findViewById(R.id.first_color_gauge_bar_four_focus_plate);
            this.mPartColorFourthFocusPlate = (ImageView) this.mCurrentView.findViewById(R.id.second_color_gauge_bar_four_focus_plate);
            this.mGaugeBarImgVw = (ImageView) this.mCurrentView.findViewById(R.id.vsb_src);
        } else {
            this.mFourPlateRelativeLayout.setVisibility(8);
            this.mFourPlateSecondPartRelativeLayout.setVisibility(8);
            this.mTwoPlateRelativeLayout.setVisibility(0);
            this.mFirstImgVw = (ImageView) this.mCurrentView.findViewById(R.id.first_color_plate);
            this.mSecondImgVw = (ImageView) this.mCurrentView.findViewById(R.id.second_color_plate);
            this.mVerticalSeekBar.setVisibility(8);
            this.mGaugetext.setVisibility(8);
            this.mGaugeBarHighTxtVw.setVisibility(8);
            this.mGaugeBarLowTxtVw.setVisibility(8);
            this.mVerticalGaugeBarBckImgVw.setVisibility(8);
            this.mGaugeBarImgVw.setVisibility(8);
            this.mFirstFocusImgVw = (ImageView) this.mCurrentView.findViewById(R.id.first_color_focus_plate);
            this.mSecondFocusImgVw = (ImageView) this.mCurrentView.findViewById(R.id.second_color_focus_plate);
        }
        this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide);
        this.mFooterGuide.setData(new FooterGuideDataResId(this.mContext, android.R.string.zen_mode_feature_name, android.R.string.httpErrorRedirectLoop));
        this.mVerticalScrollView.setOnTouchListener(new View.OnTouchListener() { // from class: com.sony.imaging.app.pictureeffectplus.shooting.layout.PictureEffectPlusEEDoubleIconSubMenuLayout.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        setServiceForSelectedEffectAndPlate();
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.pictureeffectplus.menu.layout.VerticalScrollMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        CameraSetting mCamSet = CameraSetting.getInstance();
        mCamSet.getEmptyParameters();
        this.mCurrentSelectedEffect = PictureEffectPlusController.getInstance().getBackupEffectValue();
        setServiceForSelectedEffectAndPlate();
        if (this.mCurrentSelectedEffect.equalsIgnoreCase(PictureEffectPlusController.MODE_PART_COLOR_PLUS)) {
            this.mScreenTitleView.setText(R.string.STRID_FUNC_MLTPARTCLR);
            if (this.mController.getPartColorCurrentPlate() == 1 || this.mController.getPartColorCurrentPlate() == 3) {
                this.mVerticalSeekBar.setVisibility(0);
                this.mVerticalGaugeBarBckImgVw.setVisibility(0);
                this.mGaugetext.setVisibility(0);
                this.mGaugeBarImgVw.setVisibility(0);
                this.mVerticalScrollView.setVisibility(4);
                this.mCurSelectedItemTxtVw.setVisibility(4);
                this.mGaugeBarHighTxtVw.setVisibility(0);
                this.mGaugeBarLowTxtVw.setVisibility(0);
            } else {
                this.mVerticalScrollView.setVisibility(0);
                this.mCurSelectedItemTxtVw.setVisibility(0);
                this.mVerticalSeekBar.setVisibility(4);
                this.mGaugeBarHighTxtVw.setVisibility(4);
                this.mGaugeBarLowTxtVw.setVisibility(4);
                this.mVerticalGaugeBarBckImgVw.setVisibility(4);
                this.mGaugeBarImgVw.setVisibility(4);
                this.mGaugetext.setVisibility(4);
            }
        } else if (this.mCurrentSelectedEffect.equalsIgnoreCase(PictureEffectPlusController.MODE_MINIATURE_PLUS)) {
            this.mCurSelectedItemTxtVw.setVisibility(0);
            this.mVerticalScrollView.setVisibility(0);
            this.mScreenTitleView.setText(R.string.STRID_FUNC_MINIATPLUS);
        } else if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_TOY_CAMERA_PLUS)) {
            this.mCurSelectedItemTxtVw.setVisibility(0);
            this.mVerticalScrollView.setVisibility(0);
            this.mScreenTitleView.setText(R.string.STRID_FUNC_TOYCAMPLUS);
        }
        super.onResume();
        setDefaultSettings();
        loadUI();
        removeFocus();
        this.FUNC_NAME = "onResume";
        STRBUILD.replace(0, STRBUILD.length(), this.FUNC_NAME);
        Log.i(TAG, STRBUILD.toString());
        Log.i(TAG, "onResume");
    }

    @Override // com.sony.imaging.app.pictureeffectplus.menu.layout.VerticalScrollMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mService == null) {
            this.mService = new BaseMenuService(this.mContext);
        }
        if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_PART_COLOR_PLUS)) {
            int currentSelectedPlate = this.mController.getPartColorCurrentPlate();
            if (currentSelectedPlate == 0) {
                currentSelectedPlate--;
            }
            if (currentSelectedPlate != 0) {
                currentSelectedPlate--;
            }
            if (currentSelectedPlate < 0) {
                currentSelectedPlate = 3;
            }
            this.mController.setPartColorCurrentPlate(currentSelectedPlate);
            handleCircularForPartColorPlusColor(null, currentSelectedPlate);
        } else {
            handleCircularForMiniAndToyColor(null);
        }
        removeFocus();
        setFocusableImageOnPlate();
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.pictureeffectplus.menu.layout.VerticalScrollMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mService == null) {
            this.mService = new BaseMenuService(this.mContext);
        }
        if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_PART_COLOR_PLUS)) {
            int currentSelectedPlate = this.mController.getPartColorCurrentPlate();
            if (currentSelectedPlate == 3) {
                currentSelectedPlate++;
            }
            if (currentSelectedPlate != 3) {
                currentSelectedPlate++;
            }
            if (currentSelectedPlate > 3) {
                currentSelectedPlate = 0;
            }
            this.mController.setPartColorCurrentPlate(currentSelectedPlate);
            handleCircularForPartColorPlusColor(null, currentSelectedPlate);
        } else {
            handleCircularForMiniAndToyColor(null);
        }
        removeFocus();
        setFocusableImageOnPlate();
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.pictureeffectplus.menu.layout.VerticalScrollMenuLayout, android.widget.AdapterView.OnItemSelectedListener
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_PART_COLOR_PLUS)) {
            if (this.mController.getPartColorCurrentPlate() == 0) {
                this.mController.setPartColorCh(0);
            } else if (this.mController.getPartColorCurrentPlate() == 2) {
                this.mController.setPartColorCh(1);
            } else if (this.mController.getPartColorCurrentPlate() == 1) {
                this.mController.setPartColorCh(0);
                position = this.mFirstPlateColorPositionOnGaugeBar;
            } else if (this.mController.getPartColorCurrentPlate() == 3) {
                this.mController.setPartColorCh(1);
                position = this.mSecondPlateColorPositionOnGaugeBar;
            }
        }
        Log.i(TAG, "Inside OnItemSelected &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& position " + position);
        super.onItemSelected(parent, view, position, id);
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mSelectedItemId = this.mAdapter.getItem(position);
        if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_PART_COLOR_PLUS)) {
            if (this.mController.getPartColorCurrentPlate() == 0) {
                this.mPlateOneSelectedPosition = position;
                this.mCurrentPlateOneSelectedItemId = this.mAdapter.getItem(position);
                this.mCurSelectedItemTxtVw.setText(this.mService.getMenuItemText(this.mCurrentPlateOneSelectedItemId));
                this.mVerticalSeekBar.setFirstPlateSelectedPosition(position);
                AppLog.enter(TAG, AppLog.getMethodName() + "Plate One Position ::::" + this.mPlateOneSelectedPosition);
            } else if (this.mController.getPartColorCurrentPlate() == 2) {
                this.mPlateSecondSelectedPostion = position;
                this.mCurrentPlateSecondSelectedItemId = this.mAdapter.getItem(position);
                this.mCurSelectedItemTxtVw.setText(this.mService.getMenuItemText(this.mCurrentPlateSecondSelectedItemId));
                this.mVerticalSeekBar.setSecondPlateSelectedPosition(position);
                AppLog.enter(TAG, AppLog.getMethodName() + "Plate Second Position ::::" + this.mPlateSecondSelectedPostion);
            }
            setBackgroundImageOnPlate(this.mController.getPartColorCurrentPlate(), this.mSelectedItemId);
            setImageVwOnGaugePlate();
            changeFooterGuide(position);
        } else {
            if (this.mController.getSelectedPlate() == 0) {
                this.mPlateOneSelectedPosition = position;
                this.mCurrentPlateOneSelectedItemId = this.mAdapter.getItem(position);
                this.mCurSelectedItemTxtVw.setText(this.mService.getMenuItemText(this.mCurrentPlateOneSelectedItemId));
                AppLog.enter(TAG, AppLog.getMethodName() + "Plate One Position ::::" + this.mPlateOneSelectedPosition);
            } else if (this.mController.getSelectedPlate() == 1) {
                this.mPlateSecondSelectedPostion = position;
                this.mCurrentPlateSecondSelectedItemId = this.mAdapter.getItem(position);
                this.mCurSelectedItemTxtVw.setText(this.mService.getMenuItemText(this.mCurrentPlateSecondSelectedItemId));
                AppLog.enter(TAG, AppLog.getMethodName() + "Plate Second Position ::::" + this.mPlateSecondSelectedPostion);
            }
            setBackgroundImageOnPlate(this.mController.getSelectedPlate(), this.mSelectedItemId);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void changeFooterGuide(int position) {
        this.mFooterGuide.setVisibility(0);
        if (position == 5) {
            this.mFooterGuide.setData(new FooterGuideDataResId(this.mContext, R.string.STRID_FOOTERGUIDE_CUSTOMSET_RETURN_BY_MENU, R.string.STRID_FOOTERGUIDE_CUSTOMSET_RETURN_BY_SK1));
        } else {
            this.mFooterGuide.setData(new FooterGuideDataResId(this.mContext, android.R.string.zen_mode_feature_name, android.R.string.httpErrorRedirectLoop));
        }
    }

    @Override // com.sony.imaging.app.pictureeffectplus.menu.layout.VerticalScrollMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        super.onPause();
    }

    @Override // com.sony.imaging.app.pictureeffectplus.menu.layout.VerticalScrollMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mFirstImgVw.setFocusable(false);
        this.mSecondImgVw.setFocusable(false);
        AppLog.exit(TAG, AppLog.getMethodName());
        return super.pushedUpKey();
    }

    @Override // com.sony.imaging.app.pictureeffectplus.menu.layout.VerticalScrollMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mFirstImgVw.setFocusable(false);
        this.mSecondImgVw.setFocusable(false);
        AppLog.exit(TAG, AppLog.getMethodName());
        return super.pushedDownKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.isMenuKeyPressed) {
            this.mController.setPreviousEffectOptionSetting();
            if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_PART_COLOR_PLUS)) {
                this.mController.setPartColorCurrentPlate(this.mController.getPrevSelectedPlate());
            } else {
                this.mController.setSelectedPlate(this.mController.getPreSelectedPlateForMiniAndToy());
            }
        }
        if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_PART_COLOR_PLUS)) {
            this.mController.setCustomSet2CustomOnBackupOptionValue();
        }
        super.closeLayout();
        AppLog.exit(TAG, AppLog.getMethodName());
        Log.i(TAG, "closeLayout");
    }

    @Override // com.sony.imaging.app.pictureeffectplus.menu.layout.VerticalScrollMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mUpdater != null) {
            this.mUpdater.cancelMenuUpdater();
        }
        this.isMenuKeyPressed = true;
        if (this.mController.isComeFromColorAdjustment()) {
            if (!this.mController.isComingFromApplicationSettings()) {
                HistoryItem historyItem = findPrevScreen();
                this.mService.pushMenuHistory(historyItem);
                openPreviousMenu();
            } else {
                this.mController.setComingFromApplicationSettings(false);
                HistoryItem historyItem2 = new HistoryItem("Main1", PageMenuLayout.MENU_ID);
                this.mService.pushMenuHistory(historyItem2);
                openPreviousMenu();
                this.mController.setComingFromApplicationSettings(false);
            }
            this.mController.setComeFromColorAdjustment(false);
        } else {
            if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_PART_COLOR_PLUS)) {
                this.mController.setPartColorCurrentPlate(this.mPreviousPlateSelected);
            } else {
                this.mController.setSelectedPlate(this.mPreviousPlateSelected);
            }
            if (!this.mController.isComingFromApplicationSettings()) {
                openPreviousMenu();
                return 1;
            }
            HistoryItem historyItem3 = new HistoryItem(this.lastItemID, PageMenuLayout.MENU_ID);
            this.mService.pushMenuHistory(historyItem3);
            openPreviousMenu();
            this.mController.setComingFromApplicationSettings(false);
        }
        AppLog.enter(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(TAG, AppLog.getMethodName());
        deInitialize();
        super.onDestroy();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void loadUI() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_PART_COLOR_PLUS)) {
            this.mCurrentPlateOneSelectedItemId = PictureEffectPlusController.getInstance().getBackupEffectOptionValue(PictureEffectPlusController.MODE_PART_COLOR_CH0);
            this.mCurrentPlateSecondSelectedItemId = PictureEffectPlusController.getInstance().getBackupEffectOptionValue(PictureEffectPlusController.MODE_PART_COLOR_CH1);
            this.mPlateOneSelectedPosition = this.mService.getMenuItemList().indexOf(this.mCurrentPlateOneSelectedItemId);
            this.mPlateSecondSelectedPostion = this.mService.getMenuItemList().indexOf(this.mCurrentPlateSecondSelectedItemId);
            Log.e(TAG, "In LoadUI() First Plate ItemId :: " + this.mCurrentPlateOneSelectedItemId + "Second Plate Item Id ::: " + this.mCurrentPlateSecondSelectedItemId);
        } else if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_MINIATURE_PLUS)) {
            this.mCurrentPlateOneSelectedItemId = PictureEffectPlusController.getInstance().getBackupEffectOptionValue(PictureEffectPlusController.MODE_MINIATURE_AREA);
            this.mCurrentPlateSecondSelectedItemId = PictureEffectPlusController.getInstance().getBackupEffectOptionValue(PictureEffectPlusController.MODE_MINIATURE_EFFECT);
            if (this.mCurrentPlateOneSelectedItemId.equalsIgnoreCase("auto")) {
                this.mSecondImgVw.setAlpha(128);
            } else {
                this.mSecondImgVw.setAlpha(255);
            }
        } else if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_TOY_CAMERA_PLUS)) {
            this.mCurrentPlateOneSelectedItemId = PictureEffectPlusController.getInstance().getBackupEffectOptionValue(PictureEffectPlusController.MODE_TOY_CAMERA_COLOR);
            this.mCurrentPlateSecondSelectedItemId = PictureEffectPlusController.getInstance().getBackupEffectOptionValue(PictureEffectPlusController.MODE_TOY_CAMERA_DARKNESS);
            this.mSecondImgVw.setAlpha(255);
        }
        if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_PART_COLOR_PLUS)) {
            if (this.mController.getPartColorCurrentPlate() == 0) {
                this.mPartColorFirstPlate.setImageResource(getNonSelectedDrawableOnFirstPartPlusPlate(this.mCurrentPlateOneSelectedItemId));
                this.mPlateOneSelectedPosition = this.mService.getMenuItemList().indexOf(this.mCurrentPlateOneSelectedItemId);
                this.mCurSelectedItemTxtVw.setText(this.mService.getMenuItemText(this.mCurrentPlateOneSelectedItemId));
                handleSelection(this.mPlateOneSelectedPosition);
                setBackGroundImageOnSecondPlate();
            } else if (this.mController.getPartColorCurrentPlate() == 2) {
                this.mPlateSecondSelectedPostion = this.mService.getMenuItemList().indexOf(this.mCurrentPlateSecondSelectedItemId);
                this.mCurSelectedItemTxtVw.setText(this.mService.getMenuItemText(this.mCurrentPlateSecondSelectedItemId));
                handleSelection(this.mPlateSecondSelectedPostion);
                setBackGroundImageOnFirstPlate();
                this.mPartColorThirdPlate.setImageResource(getNonSelectedDrawableOnSecondPartPlusPlate(this.mCurrentPlateSecondSelectedItemId));
            } else if (this.mController.getPartColorCurrentPlate() == 1) {
                if (this.mPlateOneSelectedPosition == 6) {
                    this.mVerticalSeekBar.setVisibility(4);
                    this.mGaugetext.setVisibility(4);
                    this.mGaugeBarHighTxtVw.setVisibility(4);
                    this.mGaugeBarLowTxtVw.setVisibility(4);
                    this.mVerticalGaugeBarBckImgVw.setVisibility(4);
                } else {
                    this.mVerticalSeekBar.setVisibility(0);
                    this.mGaugetext.setVisibility(0);
                    this.mGaugeBarHighTxtVw.setVisibility(0);
                    this.mGaugeBarLowTxtVw.setVisibility(0);
                    this.mVerticalGaugeBarBckImgVw.setVisibility(0);
                    this.mController.setPartColorCh(0);
                    this.mController.setColorCaptureStatus(false);
                    this.mController.copyTargetColor2AdjustColor();
                    this.mFirstPlateGaugeBarValue = this.mController.getCurrentSaturation(this.STEP);
                    if (this.mFirstPlateGaugeBarValue < 1) {
                        this.mFirstPlateGaugeBarValue = 1;
                    }
                    this.mVerticalSeekBar.setProgressLevel(this.mFirstPlateGaugeBarValue);
                    this.mGaugetext.setText("" + this.mFirstPlateGaugeBarValue);
                }
                setBackGroundImageOnFirstPlate();
                setBackGroundImageOnSecondPlate();
            } else if (this.mController.getPartColorCurrentPlate() == 3) {
                if (this.mPlateSecondSelectedPostion == 6) {
                    this.mVerticalSeekBar.setVisibility(4);
                    this.mGaugetext.setVisibility(4);
                    this.mGaugeBarHighTxtVw.setVisibility(4);
                    this.mGaugeBarLowTxtVw.setVisibility(4);
                    this.mVerticalGaugeBarBckImgVw.setVisibility(4);
                } else {
                    this.mVerticalSeekBar.setVisibility(0);
                    this.mGaugetext.setVisibility(0);
                    this.mGaugeBarHighTxtVw.setVisibility(0);
                    this.mGaugeBarLowTxtVw.setVisibility(0);
                    this.mVerticalGaugeBarBckImgVw.setVisibility(0);
                    this.mController.setPartColorCh(1);
                    this.mController.setColorCaptureStatus(false);
                    this.mController.copyTargetColor2AdjustColor();
                    this.mSecondPlateGaugeBarValue = this.mController.getCurrentSaturation(this.STEP);
                    if (this.mSecondPlateGaugeBarValue < 1) {
                        this.mSecondPlateGaugeBarValue = 1;
                    }
                    this.mVerticalSeekBar.setProgressLevel(this.mSecondPlateGaugeBarValue);
                    this.mGaugetext.setText("" + this.mSecondPlateGaugeBarValue);
                }
                setBackGroundImageOnFirstPlate();
                setBackGroundImageOnSecondPlate();
            }
            this.mVerticalSeekBar.setFirstPlateSelectedPosition(this.mPlateOneSelectedPosition);
            this.mVerticalSeekBar.setSecondPlateSelectedPosition(this.mPlateSecondSelectedPostion);
            setInitiallyImageOnGaugeBarPlate();
        } else if (this.mController.getSelectedPlate() == 0) {
            if (this.mCurrentSelectedEffect.equalsIgnoreCase(PictureEffectPlusController.MODE_MINIATURE_PLUS)) {
                this.mFirstImgVw.setImageResource(getNonSelectedDrawableForMiniature(this.mCurrentPlateOneSelectedItemId));
            } else if (this.mCurrentSelectedEffect.equalsIgnoreCase(PictureEffectPlusController.MODE_TOY_CAMERA_PLUS)) {
                this.mFirstImgVw.setImageResource(getNonSelectedDrawableForToyCameraEffect(this.mCurrentPlateOneSelectedItemId));
            }
            setBackGroundImageOnSecondPlate();
            this.mPlateOneSelectedPosition = this.mService.getMenuItemList().indexOf(this.mCurrentPlateOneSelectedItemId);
            this.mCurSelectedItemTxtVw.setText(this.mService.getMenuItemText(this.mCurrentPlateOneSelectedItemId));
            handleSelection(this.mPlateOneSelectedPosition);
        } else if (this.mController.getSelectedPlate() == 1) {
            this.mPlateSecondSelectedPostion = this.mService.getMenuItemList().indexOf(this.mCurrentPlateSecondSelectedItemId);
            this.mCurSelectedItemTxtVw.setText(this.mService.getMenuItemText(this.mCurrentPlateSecondSelectedItemId));
            handleSelection(this.mPlateSecondSelectedPostion);
            setBackGroundImageOnFirstPlate();
            if (this.mCurrentSelectedEffect.equalsIgnoreCase(PictureEffectPlusController.MODE_MINIATURE_PLUS)) {
                this.mSecondImgVw.setImageResource(getNonSelectedDrawableForMiniature(this.mCurrentPlateSecondSelectedItemId));
            } else if (this.mCurrentSelectedEffect.equalsIgnoreCase(PictureEffectPlusController.MODE_TOY_CAMERA_PLUS)) {
                this.mSecondImgVw.setImageResource(getNonSelectedDrawableForToyCameraEffect(this.mCurrentPlateSecondSelectedItemId));
            }
        }
        if (!this.mController.isComeFromColorAdjustment() && !this.mController.isComeFromColorIndication()) {
            mPreviousEffectOnFirstPlate = this.mCurrentPlateOneSelectedItemId;
            mPreviousEffectOnSecondPlate = this.mCurrentPlateSecondSelectedItemId;
            this.mController.setComeFromColorIndication(false);
        }
        setFocusableImageOnPlate();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void setFocusableImageOnPlate() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_PART_COLOR_PLUS)) {
            this.mPartColorFirstFocusPlate.setBackgroundResource(0);
            this.mPartColorSecondFocusPlate.setBackgroundResource(0);
            this.mPartColorThirdFocusPlate.setBackgroundResource(0);
            this.mPartColorFourthFocusPlate.setBackgroundResource(0);
            switch (this.mController.getPartColorCurrentPlate()) {
                case 0:
                    this.mPartColorFirstFocusPlate.setBackgroundResource(R.drawable.p_bt_x_48_f_43);
                    break;
                case 1:
                    this.mPartColorSecondFocusPlate.setBackgroundResource(R.drawable.p_bt_x_48_f_43);
                    break;
                case 2:
                    this.mPartColorThirdFocusPlate.setBackgroundResource(R.drawable.p_bt_x_48_f_43);
                    break;
                case 3:
                    this.mPartColorFourthFocusPlate.setBackgroundResource(R.drawable.p_bt_x_48_f_43);
                    break;
            }
        } else if (this.mController.getSelectedPlate() == 0) {
            this.mFirstFocusImgVw.setBackgroundResource(R.drawable.p_bt_x_48_f_43);
            this.mSecondFocusImgVw.setBackgroundResource(0);
        } else {
            this.mFirstFocusImgVw.setBackgroundResource(0);
            this.mSecondFocusImgVw.setBackgroundResource(R.drawable.p_bt_x_48_f_43);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void setBackGroundImageOnSecondPlate() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_PART_COLOR_PLUS)) {
            this.mCurrentPlateSecondSelectedItemId = PictureEffectPlusController.getInstance().getBackupEffectOptionValue(PictureEffectPlusController.MODE_PART_COLOR_CH1);
            this.mPartColorThirdPlate.setImageResource(getNonSelectedDrawableOnSecondPartPlusPlate(this.mCurrentPlateSecondSelectedItemId));
        } else if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_MINIATURE_PLUS)) {
            this.mCurrentPlateSecondSelectedItemId = PictureEffectPlusController.getInstance().getBackupEffectOptionValue(PictureEffectPlusController.MODE_MINIATURE_EFFECT);
            this.mSecondImgVw.setImageResource(getNonSelectedDrawableForMiniature(this.mCurrentPlateSecondSelectedItemId));
        } else if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_TOY_CAMERA_PLUS)) {
            this.mCurrentPlateSecondSelectedItemId = PictureEffectPlusController.getInstance().getBackupEffectOptionValue(PictureEffectPlusController.MODE_TOY_CAMERA_DARKNESS);
            this.mSecondImgVw.setImageResource(getNonSelectedDrawableForToyCameraDarkness(this.mCurrentPlateSecondSelectedItemId));
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private int getNonSelectedDrawableOnSecondPartPlusPlate(String itemId) {
        int resourse = -1;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (itemId.equals(PictureEffectController.PART_COLOR_RED)) {
            resourse = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_partr_normal;
        } else if (itemId.equals(PictureEffectPlusController.PART_COLOR_GREEN)) {
            resourse = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_partg_normal;
        } else if (itemId.equals(PictureEffectPlusController.PART_COLOR_BLUE)) {
            resourse = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_partb_normal;
        } else if (itemId.equals(PictureEffectController.PART_COLOR_YELLOW)) {
            resourse = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_party_normal;
        } else if (itemId.equals(PictureEffectPlusController.PART_COLOR_CUSTOM)) {
            resourse = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_part_custom_normal;
        } else if (itemId.equals(PictureEffectPlusController.PART_COLOR_CUSTOM_SET)) {
            resourse = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_part_set_normal;
        } else if (itemId.equals(PictureEffectPlusController.PART_COLOR_NON_SET)) {
            resourse = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_part_noset_normal;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return resourse;
    }

    private int getNonSelectedDrawableOnFirstPartPlusPlate(String itemId) {
        int resourse = -1;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (itemId.equals(PictureEffectPlusController.PART_COLOR_RED_CH0)) {
            resourse = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_partr_normal;
        } else if (itemId.equals(PictureEffectPlusController.PART_COLOR_GREEN_CH0)) {
            resourse = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_partg_normal;
        } else if (itemId.equals(PictureEffectPlusController.PART_COLOR_BLUE_CH0)) {
            resourse = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_partb_normal;
        } else if (itemId.equals(PictureEffectPlusController.PART_COLOR_YELLOW_CH0)) {
            resourse = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_party_normal;
        } else if (itemId.equals(PictureEffectPlusController.PART_COLOR_CUSTOM_CH0)) {
            resourse = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_part_custom_normal;
        } else if (itemId.equals(PictureEffectPlusController.PART_COLOR_CUSTOM_SET_CH0)) {
            resourse = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_part_set_normal;
        } else if (itemId.equals(PictureEffectPlusController.PART_COLOR_NON_SET_CH0)) {
            resourse = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_part_noset_normal;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return resourse;
    }

    private int getNonSelectedDrawableForToyCameraDarkness(String itemId) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int res = -1;
        if (itemId.equals(PictureEffectPlusController.TOY_CAMERA_MID)) {
            res = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_toy_mid;
        } else if (itemId.equals(PictureEffectPlusController.TOY_CAMERA_LOW)) {
            res = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_toy_low_normal;
        } else if (itemId.equals(PictureEffectPlusController.TOY_CAMERA_HIGH)) {
            res = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_toy_high_normal;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return res;
    }

    private int getNonSelectedDrawableForToyCameraEffect(String itemId) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int res = -1;
        if (itemId.equals(PictureEffectPlusController.TOY_CAMERA_MID)) {
            res = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_toy_mid;
        } else if (itemId.equals(PictureEffectPlusController.TOY_CAMERA_LOW)) {
            res = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_toy_low_normal;
        } else if (itemId.equals(PictureEffectPlusController.TOY_CAMERA_HIGH)) {
            res = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_toy_high_normal;
        } else if (itemId.equals(PictureEffectPlusController.TOY_CAMERA_NORMAL)) {
            res = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_toy_normal;
        } else if (itemId.equals(PictureEffectPlusController.TOY_CAMERA_COOL)) {
            res = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_toy_cool_normal;
        } else if (itemId.equals(PictureEffectPlusController.TOY_CAMERA_WARM)) {
            res = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_toy_warm_normal;
        } else if (itemId.equals(PictureEffectPlusController.TOY_CAMERA_GREEN)) {
            res = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_toy_g_normal;
        } else if (itemId.equals(PictureEffectPlusController.TOY_CAMERA_MAGENTA)) {
            res = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_toy_m_normal;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return res;
    }

    private int getNonSelectedDrawableForMiniature(String itemId) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int res = -1;
        if (itemId.equals("auto")) {
            res = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_mini_auto_normal;
        } else if (itemId.equals(PictureEffectController.MINIATURE_UPPER)) {
            res = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_mini_h1_normal;
        } else if (itemId.equals(PictureEffectController.MINIATURE_HCENTER)) {
            res = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_mini_h2_normal;
        } else if (itemId.equals(PictureEffectController.MINIATURE_LOWER)) {
            res = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_mini_h3_normal;
        } else if (itemId.equals(PictureEffectController.MINIATURE_RIGHT)) {
            res = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_mini_v1_normal;
        } else if (itemId.equals(PictureEffectController.MINIATURE_VCENTER)) {
            res = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_mini_v2_normal;
        } else if (itemId.equals(PictureEffectController.MINIATURE_LEFT)) {
            res = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_mini_v3_normal;
        } else if (itemId.equals(PictureEffectPlusController.MINIATURE_NORMAL)) {
            res = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_mini_default_normal;
        } else if (itemId.equals(PictureEffectPlusController.MINIATURE_RETRO)) {
            res = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_rtro_normal;
        } else if (itemId.equals("normal")) {
            res = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_toy_normal;
        } else if (itemId.equals("cool")) {
            res = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_toy_cool_normal;
        } else if (itemId.equals("warm")) {
            res = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_toy_warm_normal;
        } else if (itemId.equals("green")) {
            res = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_toy_g_normal;
        } else if (itemId.equals("magenta")) {
            res = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_toy_m_normal;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return res;
    }

    private void setBackGroundImageOnFirstPlate() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_PART_COLOR_PLUS)) {
            this.mCurrentPlateOneSelectedItemId = PictureEffectPlusController.getInstance().getBackupEffectOptionValue(PictureEffectPlusController.MODE_PART_COLOR_CH0);
            this.mPartColorFirstPlate.setImageResource(getNonSelectedDrawableOnFirstPartPlusPlate(this.mCurrentPlateOneSelectedItemId));
        } else if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_MINIATURE_PLUS)) {
            this.mCurrentPlateOneSelectedItemId = PictureEffectPlusController.getInstance().getBackupEffectOptionValue(PictureEffectPlusController.MODE_MINIATURE_AREA);
            this.mFirstImgVw.setImageResource(getNonSelectedDrawableForMiniature(this.mCurrentPlateOneSelectedItemId));
        } else if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_TOY_CAMERA_PLUS)) {
            this.mCurrentPlateOneSelectedItemId = PictureEffectPlusController.getInstance().getBackupEffectOptionValue(PictureEffectPlusController.MODE_TOY_CAMERA_COLOR);
            this.mFirstImgVw.setImageResource(getNonSelectedDrawableForToyCameraColor(this.mCurrentPlateOneSelectedItemId));
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private int getNonSelectedDrawableForToyCameraColor(String itemId) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int res = -1;
        if (itemId.equals(PictureEffectPlusController.TOY_CAMERA_NORMAL)) {
            res = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_toy_normal;
        } else if (itemId.equals(PictureEffectPlusController.TOY_CAMERA_COOL)) {
            res = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_toy_cool_normal;
        } else if (itemId.equals(PictureEffectPlusController.TOY_CAMERA_WARM)) {
            res = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_toy_warm_normal;
        } else if (itemId.equals(PictureEffectPlusController.TOY_CAMERA_GREEN)) {
            res = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_toy_g_normal;
        } else if (itemId.equals(PictureEffectPlusController.TOY_CAMERA_MAGENTA)) {
            res = R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_toy_m_normal;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return res;
    }

    private void setBackgroundImageOnPlate(int plate, String itemId) {
        AppLog.enter(TAG, AppLog.getMethodName());
        switch (plate) {
            case 0:
                if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_PART_COLOR_PLUS)) {
                    this.mPartColorFirstPlate.setImageResource(getNonSelectedDrawableOnFirstPartPlusPlate(itemId));
                    break;
                } else {
                    if (this.mCurrentSelectedEffect.equalsIgnoreCase(PictureEffectPlusController.MODE_MINIATURE_PLUS)) {
                        this.mFirstImgVw.setImageResource(getNonSelectedDrawableForMiniature(itemId));
                    } else if (this.mCurrentSelectedEffect.equalsIgnoreCase(PictureEffectPlusController.MODE_TOY_CAMERA_PLUS)) {
                        this.mFirstImgVw.setImageResource(getNonSelectedDrawableForToyCameraEffect(itemId));
                    }
                    if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_MINIATURE_PLUS)) {
                        if (this.mCurrentPlateOneSelectedItemId.equals("auto")) {
                            this.mSecondImgVw.setImageResource(R.drawable.p_16_dd_parts_multieffect_icon_pictureeffect_mini_default_normal);
                            this.mSecondImgVw.setAlpha(128);
                            break;
                        } else {
                            BackUpUtil mBackupUtil = BackUpUtil.getInstance();
                            String effect = mBackupUtil.getPreferenceString(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_MINIATURE_EFFECT_BEFORE_AUTO, PictureEffectPlusController.MINIATURE_NORMAL);
                            this.mSecondImgVw.setImageResource(getNonSelectedDrawableForMiniature(effect));
                            this.mSecondImgVw.setAlpha(255);
                            break;
                        }
                    }
                }
                break;
            case 1:
                if (!this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_PART_COLOR_PLUS)) {
                    if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_MINIATURE_PLUS)) {
                        this.mSecondImgVw.setImageResource(getNonSelectedDrawableForMiniature(itemId));
                        break;
                    } else if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_TOY_CAMERA_PLUS)) {
                        this.mSecondImgVw.setImageResource(getNonSelectedDrawableForToyCameraEffect(itemId));
                        break;
                    }
                }
                break;
            case 2:
                if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_PART_COLOR_PLUS)) {
                    this.mPartColorThirdPlate.setImageResource(getNonSelectedDrawableOnSecondPartPlusPlate(itemId));
                    break;
                }
                break;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void removeFocus() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mFirstImgVw.setFocusable(false);
        this.mSecondImgVw.setFocusable(false);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void setDefaultSettings() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_PART_COLOR_PLUS)) {
            this.mCurrentPlateOneSelectedItemId = PictureEffectPlusController.PART_COLOR_RED_CH0;
            this.mCurrentPlateSecondSelectedItemId = PictureEffectPlusController.PART_COLOR_NON_SET;
        } else if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_MINIATURE_PLUS)) {
            this.mCurrentPlateOneSelectedItemId = PictureEffectController.MINIATURE_HCENTER;
            this.mCurrentPlateSecondSelectedItemId = PictureEffectPlusController.MINIATURE_NORMAL;
        } else if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_TOY_CAMERA_PLUS)) {
            this.mCurrentPlateOneSelectedItemId = PictureEffectPlusController.TOY_CAMERA_NORMAL;
            this.mCurrentPlateSecondSelectedItemId = PictureEffectPlusController.TOY_CAMERA_MID;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void deInitialize() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mService = null;
        this.mAdapter = null;
        this.mFirstImgVw.setImageDrawable(null);
        this.mSecondImgVw.setImageDrawable(null);
        this.mFirstImgVw = null;
        this.mSecondImgVw = null;
        this.mVerticalGaugeBarBckImgVw = null;
        this.mPartColorFirstPlate = null;
        this.mPartColorSecondPlate = null;
        this.mPartColorThirdPlate = null;
        this.mPartColorFourthPlate = null;
        this.mVerticalSeekBar = null;
        this.mCurSelectedItemTxtVw = null;
        this.mGaugeBarImgVw = null;
        this.mPartColorFirstFocusPlate = null;
        this.mPartColorSecondFocusPlate = null;
        this.mPartColorThirdFocusPlate = null;
        this.mPartColorFourthFocusPlate = null;
        this.mFourPlateRelativeLayout = null;
        this.mFourPlateSecondPartRelativeLayout = null;
        this.isMenuKeyPressed = false;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void handleSelection(int position) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mVerticalScrollView.setSelection(position);
        this.mInitPos = position;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int scanCode = event.getScanCode();
        boolean handle = false;
        switch (scanCode) {
            case AppRoot.USER_KEYCODE.UP /* 103 */:
                if (this.mController.getPartColorCurrentPlate() == 1) {
                    if ((this.mPlateOneSelectedPosition != 6 || !this.mCurrentPlateOneSelectedItemId.equals(PictureEffectPlusController.PART_COLOR_NON_SET_CH0)) && (this.mPlateOneSelectedPosition != 5 || !this.mCurrentPlateOneSelectedItemId.equals(PictureEffectPlusController.PART_COLOR_CUSTOM_SET_CH0))) {
                        handle = handleUpDownForGaugeBar(false, 1);
                        break;
                    }
                } else if (this.mController.getPartColorCurrentPlate() == 3 && ((this.mPlateSecondSelectedPostion != 6 || !this.mCurrentPlateSecondSelectedItemId.equals(PictureEffectPlusController.PART_COLOR_NON_SET)) && (this.mPlateSecondSelectedPostion != 5 || !this.mCurrentPlateSecondSelectedItemId.equals(PictureEffectPlusController.PART_COLOR_CUSTOM_SET)))) {
                    handle = handleUpDownForGaugeBar(false, 1);
                    break;
                }
                break;
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                if (this.mController.getPartColorCurrentPlate() == 1) {
                    if ((this.mPlateOneSelectedPosition != 6 || !this.mCurrentPlateOneSelectedItemId.equals(PictureEffectPlusController.PART_COLOR_NON_SET_CH0)) && (this.mPlateOneSelectedPosition != 5 || !this.mCurrentPlateOneSelectedItemId.equals(PictureEffectPlusController.PART_COLOR_CUSTOM_SET_CH0))) {
                        handle = handleUpDownForGaugeBar(false, 0);
                        break;
                    }
                } else if (this.mController.getPartColorCurrentPlate() == 3 && ((this.mPlateSecondSelectedPostion != 6 || !this.mCurrentPlateSecondSelectedItemId.equals(PictureEffectPlusController.PART_COLOR_NON_SET)) && (this.mPlateSecondSelectedPostion != 5 || !this.mCurrentPlateSecondSelectedItemId.equals(PictureEffectPlusController.PART_COLOR_CUSTOM_SET)))) {
                    handle = handleUpDownForGaugeBar(false, 0);
                    break;
                }
                break;
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_PART_COLOR_PLUS)) {
                    switch (this.mController.getPartColorCurrentPlate()) {
                        case 1:
                            if (this.mCurrentPlateOneSelectedItemId.equalsIgnoreCase(PictureEffectPlusController.PART_COLOR_NON_SET_CH0) || this.mCurrentPlateOneSelectedItemId.equalsIgnoreCase(PictureEffectPlusController.PART_COLOR_CUSTOM_SET_CH0)) {
                                if (!getFunctionGuideView().isShown()) {
                                    handle = true;
                                    break;
                                }
                            } else if (!getFunctionGuideView().isShown()) {
                                String itemId = this.mCurrentPlateOneSelectedItemId;
                                doItemClickProcessing(itemId);
                                handle = true;
                                break;
                            }
                            break;
                        case 3:
                            if (this.mCurrentPlateSecondSelectedItemId.equalsIgnoreCase(PictureEffectPlusController.PART_COLOR_NON_SET) || this.mCurrentPlateSecondSelectedItemId.equalsIgnoreCase(PictureEffectPlusController.PART_COLOR_CUSTOM_SET)) {
                                if (!getFunctionGuideView().isShown()) {
                                    handle = true;
                                    break;
                                }
                            } else if (!getFunctionGuideView().isShown()) {
                                String itemId2 = this.mCurrentPlateSecondSelectedItemId;
                                doItemClickProcessing(itemId2);
                                handle = true;
                                break;
                            }
                            break;
                    }
                }
                break;
        }
        if (handle) {
            return 1;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void handleCircularForMiniAndToyColor(List<String> list) {
        int currentSelectedPlate;
        AppLog.enter(TAG, AppLog.getMethodName());
        int currentSelectedPlate2 = this.mController.getSelectedPlate();
        if (!this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_MINIATURE_PLUS)) {
            if (currentSelectedPlate2 == 1) {
                currentSelectedPlate = 0;
            } else {
                currentSelectedPlate = 1;
            }
        } else if (this.mCurrentPlateOneSelectedItemId.equalsIgnoreCase("auto")) {
            currentSelectedPlate = 0;
        } else if (currentSelectedPlate2 == 1) {
            currentSelectedPlate = 0;
        } else {
            currentSelectedPlate = 1;
        }
        this.mController.setSelectedPlate(currentSelectedPlate);
        switch (currentSelectedPlate) {
            case 0:
                if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_MINIATURE_PLUS)) {
                    this.mService.setMenuItemId(PictureEffectPlusController.MODE_MINIATURE_AREA);
                    list = this.mService.getMenuItemList();
                    break;
                } else if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_TOY_CAMERA_PLUS)) {
                    this.mService.setMenuItemId(PictureEffectPlusController.MODE_TOY_CAMERA_COLOR);
                    list = this.mService.getMenuItemList();
                    break;
                }
                break;
            case 1:
                if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_MINIATURE_PLUS)) {
                    this.mService.setMenuItemId(PictureEffectPlusController.MODE_MINIATURE_EFFECT);
                    list = this.mService.getMenuItemList();
                    break;
                } else if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_TOY_CAMERA_PLUS)) {
                    this.mService.setMenuItemId(PictureEffectPlusController.MODE_TOY_CAMERA_DARKNESS);
                    list = this.mService.getMenuItemList();
                    break;
                }
                break;
        }
        AppLog.info(TAG, AppLog.getMethodName() + "List for Current Selected Plate:::" + list.toString());
        if (this.mAdapter != null) {
            this.mAdapter.setMenuItemList(null);
        }
        this.mAdapter.setMenuItemList(this.mService.getMenuItemList());
        this.mVerticalScrollView.setAdapter(this.mAdapter);
        if (currentSelectedPlate == 0) {
            this.mPlateOneSelectedPosition = list.indexOf(this.mCurrentPlateOneSelectedItemId);
            this.mCurSelectedItemTxtVw.setText(this.mService.getMenuItemText(this.mCurrentPlateOneSelectedItemId));
            handleSelection(this.mPlateOneSelectedPosition);
            AppLog.info(TAG, AppLog.getMethodName() + "Plate One Selected Postion ::::" + this.mPlateOneSelectedPosition);
            setBackgroundImageOnPlate(this.mController.getSelectedPlate(), this.mAdapter.getItem(this.mPlateOneSelectedPosition));
            setBackGroundImageOnSecondPlate();
        } else {
            if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_MINIATURE_PLUS)) {
                BackUpUtil mBackupUtil = BackUpUtil.getInstance();
                String effect = mBackupUtil.getPreferenceString(PictureEffectPlusBackUpKey.ID_PICTUREEFFECTPLUS_MINIATURE_EFFECT_BEFORE_AUTO, PictureEffectPlusController.MINIATURE_NORMAL);
                this.mCurrentPlateSecondSelectedItemId = effect;
            }
            this.mPlateSecondSelectedPostion = list.indexOf(this.mCurrentPlateSecondSelectedItemId);
            AppLog.info(TAG, AppLog.getMethodName() + "Plate Second Selected Postion ::::" + this.mPlateSecondSelectedPostion);
            this.mCurSelectedItemTxtVw.setText(this.mService.getMenuItemText(this.mCurrentPlateSecondSelectedItemId));
            handleSelection(this.mPlateSecondSelectedPostion);
            setBackgroundImageOnPlate(this.mController.getSelectedPlate(), this.mService.getMenuItemList().get(this.mPlateSecondSelectedPostion));
            setBackGroundImageOnFirstPlate();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void handleCircularForPartColorPlusColor(List<String> list, int currentSelectedPlate) {
        AppLog.enter(TAG, AppLog.getMethodName());
        switch (this.mController.getPartColorCurrentPlate()) {
            case 0:
                this.mService.setMenuItemId(PictureEffectPlusController.MODE_PART_COLOR_CH0);
                this.mController.setPartColorCh(0);
                list = this.mService.getMenuItemList();
                this.mVerticalScrollView.setVisibility(0);
                this.mCurSelectedItemTxtVw.setVisibility(0);
                this.mVerticalSeekBar.setVisibility(4);
                this.mGaugeBarHighTxtVw.setVisibility(4);
                this.mGaugeBarLowTxtVw.setVisibility(4);
                this.mVerticalGaugeBarBckImgVw.setVisibility(4);
                this.mGaugeBarImgVw.setVisibility(4);
                this.mGaugetext.setVisibility(4);
                break;
            case 1:
                AppLog.info(TAG, "hide the mScreenView and show gauge bar now current Selected Plate ::::" + currentSelectedPlate);
                this.mVerticalScrollView.setVisibility(4);
                this.mVerticalScrollView.getSelectedItemPosition();
                this.mCurSelectedItemTxtVw.setVisibility(4);
                this.mVerticalSeekBar.setVisibility(0);
                this.mGaugeBarHighTxtVw.setVisibility(0);
                this.mGaugeBarLowTxtVw.setVisibility(0);
                this.mVerticalGaugeBarBckImgVw.setVisibility(0);
                this.mGaugeBarImgVw.setVisibility(0);
                this.mGaugetext.setVisibility(0);
                if (this.mPlateOneSelectedPosition == 6 && this.mCurrentPlateOneSelectedItemId.equals(PictureEffectPlusController.PART_COLOR_NON_SET_CH0)) {
                    this.mVerticalSeekBar.setVisibility(4);
                    this.mGaugetext.setVisibility(4);
                    this.mGaugeBarHighTxtVw.setVisibility(4);
                    this.mGaugeBarLowTxtVw.setVisibility(4);
                    this.mVerticalGaugeBarBckImgVw.setVisibility(4);
                } else if (this.mPlateOneSelectedPosition == 5 && this.mCurrentPlateOneSelectedItemId.equals(PictureEffectPlusController.PART_COLOR_CUSTOM_SET_CH0)) {
                    this.mVerticalSeekBar.setVisibility(4);
                    this.mGaugetext.setVisibility(4);
                    this.mGaugeBarHighTxtVw.setVisibility(4);
                    this.mGaugeBarLowTxtVw.setVisibility(4);
                    this.mVerticalGaugeBarBckImgVw.setVisibility(4);
                }
                this.mController.setPartColorCh(0);
                this.mController.setColorCaptureStatus(false);
                this.mController.copyTargetColor2AdjustColor();
                int level = this.mController.getCurrentSaturation(this.STEP);
                AppLog.info(TAG, "Saturation Level :: " + level + " For Color  " + this.mCurrentPlateOneSelectedItemId);
                this.mFirstPlateGaugeBarValue = level;
                this.mVerticalSeekBar.setProgressLevel(this.mFirstPlateGaugeBarValue);
                this.mGaugetext.setText("" + this.mVerticalSeekBar.getProgress());
                break;
            case 2:
                this.mService.setMenuItemId(PictureEffectPlusController.MODE_PART_COLOR_CH1);
                list = this.mService.getMenuItemList();
                this.mVerticalScrollView.setVisibility(0);
                this.mCurSelectedItemTxtVw.setVisibility(0);
                this.mVerticalSeekBar.setVisibility(4);
                this.mGaugeBarHighTxtVw.setVisibility(4);
                this.mGaugeBarLowTxtVw.setVisibility(4);
                this.mVerticalGaugeBarBckImgVw.setVisibility(4);
                this.mGaugeBarImgVw.setVisibility(4);
                this.mGaugetext.setVisibility(4);
                this.mController.setPartColorCh(1);
                break;
            case 3:
                AppLog.info(TAG, "hide the mScreenView and show gauge bar now current Selected Plate ::::" + currentSelectedPlate);
                this.mVerticalScrollView.setVisibility(4);
                this.mCurSelectedItemTxtVw.setVisibility(4);
                this.mVerticalSeekBar.setVisibility(0);
                this.mGaugeBarHighTxtVw.setVisibility(0);
                this.mGaugeBarLowTxtVw.setVisibility(0);
                this.mVerticalGaugeBarBckImgVw.setVisibility(0);
                this.mGaugeBarImgVw.setVisibility(0);
                this.mGaugetext.setVisibility(0);
                if (this.mPlateSecondSelectedPostion == 6 && this.mCurrentPlateSecondSelectedItemId.equals(PictureEffectPlusController.PART_COLOR_NON_SET)) {
                    this.mVerticalSeekBar.setVisibility(4);
                    this.mGaugeBarHighTxtVw.setVisibility(4);
                    this.mGaugeBarLowTxtVw.setVisibility(4);
                    this.mVerticalGaugeBarBckImgVw.setVisibility(4);
                    this.mGaugetext.setVisibility(4);
                } else if (this.mPlateSecondSelectedPostion == 5 && this.mCurrentPlateSecondSelectedItemId.equals(PictureEffectPlusController.PART_COLOR_CUSTOM_SET)) {
                    this.mVerticalSeekBar.setVisibility(4);
                    this.mGaugeBarHighTxtVw.setVisibility(4);
                    this.mGaugeBarLowTxtVw.setVisibility(4);
                    this.mVerticalGaugeBarBckImgVw.setVisibility(4);
                    this.mGaugetext.setVisibility(4);
                }
                this.mController.setPartColorCh(1);
                this.mController = PictureEffectPlusController.getInstance();
                this.mController.setColorCaptureStatus(false);
                this.mController.copyTargetColor2AdjustColor();
                int level2 = this.mController.getCurrentSaturation(this.STEP);
                AppLog.info(TAG, "Saturation Level :: " + level2 + " For Color  " + this.mCurrentPlateSecondSelectedItemId);
                this.mSecondPlateGaugeBarValue = level2;
                this.mVerticalSeekBar.setProgressLevel(this.mSecondPlateGaugeBarValue);
                this.mGaugetext.setText("" + this.mSecondPlateGaugeBarValue);
                break;
        }
        if (this.mController.getPartColorCurrentPlate() == 0 || this.mController.getPartColorCurrentPlate() == 2) {
            AppLog.info(TAG, AppLog.getMethodName() + "List for 0th Plate:::" + list.toString());
            if (this.mAdapter != null) {
                this.mAdapter.setMenuItemList(null);
            }
            this.mAdapter.setMenuItemList((ArrayList) list);
            this.mVerticalScrollView.setAdapter(this.mAdapter);
            AppLog.info(TAG, AppLog.getMethodName() + "Plate One Selected Postion ::::" + this.mPlateOneSelectedPosition);
            if (this.mController.getPartColorCurrentPlate() == 0) {
                if (this.mPlateSecondSelectedPostion == 5) {
                    this.mCurrentPlateSecondSelectedItemId = PictureEffectPlusController.PART_COLOR_CUSTOM;
                    this.mPlateSecondSelectedPostion = 4;
                    this.mPartColorThirdPlate.setImageResource(getNonSelectedDrawableOnSecondPartPlusPlate(this.mCurrentPlateSecondSelectedItemId));
                    this.mPartColorFourthPlate.setImageResource(R.drawable.p_16_dd_parts_pe_adjust_normal);
                    this.mController.setValue(PictureEffectPlusController.MODE_PART_COLOR_CH1, PictureEffectPlusController.PART_COLOR_CUSTOM);
                    changeFooterGuide(this.mPlateSecondSelectedPostion);
                }
                if (this.mPlateOneSelectedPosition == 5) {
                    changeFooterGuide(this.mPlateOneSelectedPosition);
                }
                this.mPlateOneSelectedPosition = list.indexOf(this.mCurrentPlateOneSelectedItemId);
                this.mCurSelectedItemTxtVw.setText(this.mService.getMenuItemText(this.mCurrentPlateOneSelectedItemId));
                handleSelection(this.mPlateOneSelectedPosition);
                setBackgroundImageOnPlate(this.mController.getPartColorCurrentPlate(), this.mAdapter.getItem(this.mPlateOneSelectedPosition));
            } else if (this.mController.getPartColorCurrentPlate() == 2) {
                if (this.mPlateOneSelectedPosition == 5) {
                    this.mPlateOneSelectedPosition = 4;
                    this.mCurrentPlateOneSelectedItemId = PictureEffectPlusController.PART_COLOR_CUSTOM_CH0;
                    this.mPartColorFirstPlate.setImageResource(getNonSelectedDrawableOnFirstPartPlusPlate(this.mCurrentPlateOneSelectedItemId));
                    this.mPartColorSecondPlate.setImageResource(R.drawable.p_16_dd_parts_pe_adjust_normal);
                    this.mController.setValue(PictureEffectPlusController.MODE_PART_COLOR_CH0, PictureEffectPlusController.PART_COLOR_CUSTOM_CH0);
                    changeFooterGuide(this.mPlateOneSelectedPosition);
                }
                if (this.mPlateSecondSelectedPostion == 5) {
                    changeFooterGuide(this.mPlateSecondSelectedPostion);
                }
                this.mPlateSecondSelectedPostion = list.indexOf(this.mCurrentPlateSecondSelectedItemId);
                this.mCurSelectedItemTxtVw.setText(this.mService.getMenuItemText(this.mCurrentPlateSecondSelectedItemId));
                handleSelection(this.mPlateSecondSelectedPostion);
                setBackgroundImageOnPlate(this.mController.getPartColorCurrentPlate(), this.mAdapter.getItem(this.mPlateSecondSelectedPostion));
            }
        } else if (this.mController.getPartColorCurrentPlate() == 1 || this.mController.getPartColorCurrentPlate() == 3) {
            if (this.mController.getPartColorCurrentPlate() == 1) {
                if (this.mPlateSecondSelectedPostion == 5) {
                    this.mPlateSecondSelectedPostion = 4;
                    this.mCurrentPlateSecondSelectedItemId = PictureEffectPlusController.PART_COLOR_CUSTOM;
                    this.mPartColorFourthPlate.setImageResource(R.drawable.p_16_dd_parts_pe_adjust_normal);
                    this.mController.setValue(PictureEffectPlusController.MODE_PART_COLOR_CH1, PictureEffectPlusController.PART_COLOR_CUSTOM);
                    changeFooterGuide(this.mPlateSecondSelectedPostion);
                }
                if (this.mPlateOneSelectedPosition == 5) {
                    this.mFooterGuide.setData(new FooterGuideDataResId(this.mContext, android.R.string.zen_mode_feature_name, android.R.string.httpErrorRedirectLoop));
                }
                this.mPlateOneSelectedPosition = getFirstPlatePosition(this.mCurrentPlateOneSelectedItemId);
                handleSelection(this.mPlateOneSelectedPosition);
                this.mFirstPlateColorPositionOnGaugeBar = this.mPlateOneSelectedPosition;
                this.mSecondPlateColorPositionOnGaugeBar = this.mPlateSecondSelectedPostion;
            }
            if (this.mController.getPartColorCurrentPlate() == 3) {
                if (this.mPlateOneSelectedPosition == 5) {
                    this.mPlateOneSelectedPosition = 4;
                    this.mCurrentPlateOneSelectedItemId = PictureEffectPlusController.PART_COLOR_CUSTOM_CH0;
                    this.mPartColorSecondPlate.setImageResource(R.drawable.p_16_dd_parts_pe_adjust_normal);
                    this.mController.setValue(PictureEffectPlusController.MODE_PART_COLOR_CH0, PictureEffectPlusController.PART_COLOR_CUSTOM_CH0);
                    changeFooterGuide(this.mPlateOneSelectedPosition);
                }
                if (this.mPlateSecondSelectedPostion == 5) {
                    this.mFooterGuide.setData(new FooterGuideDataResId(this.mContext, android.R.string.zen_mode_feature_name, android.R.string.httpErrorRedirectLoop));
                }
                this.mPlateSecondSelectedPostion = getSecondPlatePosition(this.mCurrentPlateSecondSelectedItemId);
                handleSelection(this.mPlateSecondSelectedPostion);
                this.mFirstPlateColorPositionOnGaugeBar = this.mPlateOneSelectedPosition;
                this.mSecondPlateColorPositionOnGaugeBar = this.mPlateSecondSelectedPostion;
            }
            setNonSelectedImageOnBothPlateForPartColorPlus();
        }
        setImageVwOnGaugePlate();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private int getFirstPlatePosition(String itemId) {
        if (itemId.equals(PictureEffectPlusController.PART_COLOR_RED_CH0)) {
            return 0;
        }
        if (itemId.equals(PictureEffectPlusController.PART_COLOR_GREEN_CH0)) {
            return 1;
        }
        if (itemId.equals(PictureEffectPlusController.PART_COLOR_BLUE_CH0)) {
            return 2;
        }
        if (itemId.equals(PictureEffectPlusController.PART_COLOR_YELLOW_CH0)) {
            return 3;
        }
        if (itemId.equals(PictureEffectPlusController.PART_COLOR_CUSTOM_CH0)) {
            return 4;
        }
        if (itemId.equals(PictureEffectPlusController.PART_COLOR_CUSTOM_SET_CH0)) {
            return 5;
        }
        if (!itemId.equals(PictureEffectPlusController.PART_COLOR_NON_SET_CH0)) {
            return -1;
        }
        return 6;
    }

    private int getSecondPlatePosition(String itemId) {
        if (itemId.equals(PictureEffectController.PART_COLOR_RED)) {
            return 0;
        }
        if (itemId.equals(PictureEffectPlusController.PART_COLOR_GREEN)) {
            return 1;
        }
        if (itemId.equals(PictureEffectPlusController.PART_COLOR_BLUE)) {
            return 2;
        }
        if (itemId.equals(PictureEffectController.PART_COLOR_YELLOW)) {
            return 3;
        }
        if (itemId.equals(PictureEffectPlusController.PART_COLOR_CUSTOM)) {
            return 4;
        }
        if (itemId.equals(PictureEffectPlusController.PART_COLOR_CUSTOM_SET)) {
            return 5;
        }
        if (!itemId.equals(PictureEffectPlusController.PART_COLOR_NON_SET)) {
            return -1;
        }
        return 6;
    }

    private void checkForCustomSetSelection() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mCurrentPlateOneSelectedItemId.equals(PictureEffectPlusController.PART_COLOR_CUSTOM_SET_CH0)) {
            this.mCurrentPlateOneSelectedItemId = PictureEffectPlusController.PART_COLOR_CUSTOM_CH0;
        }
        if (this.mCurrentPlateSecondSelectedItemId.equals(PictureEffectPlusController.PART_COLOR_CUSTOM_SET)) {
            this.mCurrentPlateSecondSelectedItemId = PictureEffectPlusController.PART_COLOR_CUSTOM;
        }
        AppLog.enter(TAG, AppLog.getMethodName());
    }

    private void setServiceForSelectedEffectAndPlate() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mCurrentSelectedEffect.equalsIgnoreCase(PictureEffectPlusController.MODE_PART_COLOR_PLUS)) {
            this.mPreviousPlateSelected = this.mController.getPartColorCurrentPlate();
            if (this.mController.getPartColorCurrentPlate() == 0) {
                this.mService.setMenuItemId(PictureEffectPlusController.MODE_PART_COLOR_CH0);
            } else if (this.mController.getPartColorCurrentPlate() == 2) {
                this.mService.setMenuItemId(PictureEffectPlusController.MODE_PART_COLOR_CH1);
            } else if (this.mController.getPartColorCurrentPlate() == 1) {
                this.mService.setMenuItemId(PictureEffectPlusController.MODE_PART_COLOR_CH0);
            } else if (this.mController.getPartColorCurrentPlate() == 3) {
                this.mService.setMenuItemId(PictureEffectPlusController.MODE_PART_COLOR_CH1);
            }
        } else if (this.mCurrentSelectedEffect.equalsIgnoreCase(PictureEffectPlusController.MODE_MINIATURE_PLUS)) {
            this.mPreviousPlateSelected = this.mController.getSelectedPlate();
            if (this.mController.getSelectedPlate() == 0) {
                this.mService.setMenuItemId(PictureEffectPlusController.MODE_MINIATURE_AREA);
            } else if (this.mController.getSelectedPlate() == 1) {
                this.mService.setMenuItemId(PictureEffectPlusController.MODE_MINIATURE_EFFECT);
            }
        } else if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_TOY_CAMERA_PLUS)) {
            this.mPreviousPlateSelected = this.mController.getSelectedPlate();
            if (this.mController.getSelectedPlate() == 0) {
                this.mService.setMenuItemId(PictureEffectPlusController.MODE_TOY_CAMERA_COLOR);
            } else if (this.mController.getSelectedPlate() == 1) {
                this.mService.setMenuItemId(PictureEffectPlusController.MODE_TOY_CAMERA_DARKNESS);
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void setNonSelectedImageOnBothPlateForPartColorPlus() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mService != null) {
            this.mPartColorFirstPlate.setImageResource(getNonSelectedDrawableOnFirstPartPlusPlate(this.mCurrentPlateOneSelectedItemId));
            this.mPartColorThirdPlate.setImageResource(getNonSelectedDrawableOnSecondPartPlusPlate(this.mCurrentPlateSecondSelectedItemId));
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.pictureeffectplus.menu.layout.VerticalScrollMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_PART_COLOR_PLUS)) {
            if (this.mController.getPartColorCurrentPlate() == 1) {
                if ((this.mPlateOneSelectedPosition != 6 || !this.mCurrentPlateOneSelectedItemId.equals(PictureEffectPlusController.PART_COLOR_NON_SET_CH0)) && (this.mPlateOneSelectedPosition != 5 || !this.mCurrentPlateOneSelectedItemId.equals(PictureEffectPlusController.PART_COLOR_CUSTOM_SET_CH0))) {
                    handleUpDownForGaugeBar(true, 1);
                    return 1;
                }
            } else if (this.mController.getPartColorCurrentPlate() == 3 && ((this.mPlateSecondSelectedPostion != 6 || !this.mCurrentPlateSecondSelectedItemId.equals(PictureEffectPlusController.PART_COLOR_NON_SET)) && (this.mPlateSecondSelectedPostion != 5 || !this.mCurrentPlateSecondSelectedItemId.equals(PictureEffectPlusController.PART_COLOR_CUSTOM_SET)))) {
                handleUpDownForGaugeBar(true, 1);
                return 1;
            }
            return super.turnedMainDialPrev();
        }
        return super.turnedMainDialPrev();
    }

    @Override // com.sony.imaging.app.pictureeffectplus.menu.layout.VerticalScrollMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_PART_COLOR_PLUS)) {
            if (this.mController.getPartColorCurrentPlate() == 1) {
                if ((this.mPlateOneSelectedPosition != 6 || !this.mCurrentPlateOneSelectedItemId.equals(PictureEffectPlusController.PART_COLOR_NON_SET_CH0)) && (this.mPlateOneSelectedPosition != 5 || !this.mCurrentPlateOneSelectedItemId.equals(PictureEffectPlusController.PART_COLOR_CUSTOM_SET_CH0))) {
                    handleUpDownForGaugeBar(true, 0);
                    return 1;
                }
            } else if (this.mController.getPartColorCurrentPlate() == 3 && ((this.mPlateSecondSelectedPostion != 6 || !this.mCurrentPlateSecondSelectedItemId.equals(PictureEffectPlusController.PART_COLOR_NON_SET)) && (this.mPlateSecondSelectedPostion != 5 || !this.mCurrentPlateSecondSelectedItemId.equals(PictureEffectPlusController.PART_COLOR_CUSTOM_SET)))) {
                handleUpDownForGaugeBar(true, 0);
                return 1;
            }
            return super.turnedMainDialNext();
        }
        return super.turnedMainDialNext();
    }

    @Override // com.sony.imaging.app.pictureeffectplus.shooting.widget.UpdateGaugeText
    public void setGaugeText(int level) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_PART_COLOR_PLUS)) {
            if (level < 1) {
                level = 1;
            }
            if (this.mController.getPartColorCurrentPlate() == 1) {
                this.mController.setPartColorCh(0);
                this.mFirstPlateGaugeBarValue = level;
                this.mController.setChangedSaturationValue(level, this.STEP);
                this.mController.setChangedColor();
            } else if (this.mController.getPartColorCurrentPlate() == 3) {
                this.mController.setPartColorCh(1);
                this.mSecondPlateGaugeBarValue = level;
                this.mController.setChangedSaturationValue(level, this.STEP);
                this.mController.setChangedColor();
            }
            this.mGaugetext.setText("" + level);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private boolean handleUpDownForGaugeBar(boolean handle, int move) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_PART_COLOR_PLUS)) {
            if (this.mController.getPartColorCurrentPlate() == 1) {
                this.mController.setPartColorCh(0);
                if ((this.mPlateOneSelectedPosition != 6 || !this.mCurrentPlateOneSelectedItemId.equals(PictureEffectPlusController.PART_COLOR_NON_SET_CH0)) && (this.mPlateOneSelectedPosition != 5 || !this.mCurrentPlateOneSelectedItemId.equals(PictureEffectPlusController.PART_COLOR_CUSTOM_SET_CH0))) {
                    if (move == 1) {
                        this.mFirstPlateGaugeBarValue = this.mVerticalSeekBar.moveUp();
                    } else if (move == 0 && this.mVerticalSeekBar.getProgress() > 1) {
                        this.mFirstPlateGaugeBarValue = this.mVerticalSeekBar.moveDown();
                    }
                    if (this.mFirstPlateGaugeBarValue < 1) {
                        this.mFirstPlateGaugeBarValue = 1;
                    }
                    this.mGaugetext.setText("" + this.mFirstPlateGaugeBarValue);
                    AppLog.info(TAG, AppLog.getMethodName() + "Before Setting Saturation check saturation :" + this.mFirstPlateGaugeBarValue);
                    this.mController.setColorCaptureStatus(false);
                    this.mController.setChangedSaturationValue(this.mFirstPlateGaugeBarValue, this.STEP);
                    this.mController.setChangedColor();
                    handle = true;
                }
            } else if (this.mController.getPartColorCurrentPlate() == 3) {
                this.mController.setPartColorCh(1);
                if ((this.mPlateSecondSelectedPostion != 6 || !this.mCurrentPlateSecondSelectedItemId.equals(PictureEffectPlusController.PART_COLOR_NON_SET)) && (this.mPlateSecondSelectedPostion != 5 || !this.mCurrentPlateSecondSelectedItemId.equals(PictureEffectPlusController.PART_COLOR_CUSTOM_SET))) {
                    if (move == 1) {
                        this.mSecondPlateGaugeBarValue = this.mVerticalSeekBar.moveUp();
                    } else if (move == 0 && this.mVerticalSeekBar.getProgress() > 1) {
                        this.mSecondPlateGaugeBarValue = this.mVerticalSeekBar.moveDown();
                    }
                    if (this.mSecondPlateGaugeBarValue < 1) {
                        this.mSecondPlateGaugeBarValue = 1;
                    }
                    this.mGaugetext.setText("" + this.mSecondPlateGaugeBarValue);
                    AppLog.info(TAG, AppLog.getMethodName() + "Before Setting Saturation check saturation :" + this.mSecondPlateGaugeBarValue);
                    this.mController.setColorCaptureStatus(false);
                    this.mController.setChangedSaturationValue(this.mSecondPlateGaugeBarValue, this.STEP);
                    this.mController.setChangedColor();
                    handle = true;
                }
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return handle;
    }

    private void setInitiallyImageOnGaugeBarPlate() {
        if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_PART_COLOR_PLUS)) {
            if (!this.mCurrentPlateOneSelectedItemId.equals(PictureEffectPlusController.PART_COLOR_NON_SET_CH0)) {
                this.mPartColorSecondPlate.setImageResource(R.drawable.p_16_dd_parts_pe_adjust_normal);
            } else {
                this.mPartColorSecondPlate.setImageResource(R.drawable.p_16_dd_parts_pe_adjust_disable_normal);
            }
            if (!this.mCurrentPlateSecondSelectedItemId.equals(PictureEffectPlusController.PART_COLOR_NON_SET)) {
                this.mPartColorFourthPlate.setImageResource(R.drawable.p_16_dd_parts_pe_adjust_normal);
            } else {
                this.mPartColorFourthPlate.setImageResource(R.drawable.p_16_dd_parts_pe_adjust_disable_normal);
            }
        }
    }

    private void setImageVwOnGaugePlate() {
        if (this.mCurrentSelectedEffect.equals(PictureEffectPlusController.MODE_PART_COLOR_PLUS)) {
            switch (this.mController.getPartColorCurrentPlate()) {
                case 0:
                case 1:
                    if (this.mCurrentPlateOneSelectedItemId.equals(PictureEffectPlusController.PART_COLOR_NON_SET_CH0)) {
                        this.mPartColorSecondPlate.setImageResource(R.drawable.p_16_dd_parts_pe_adjust_disable_normal);
                        return;
                    } else if (this.mCurrentPlateOneSelectedItemId.equals(PictureEffectPlusController.PART_COLOR_CUSTOM_SET_CH0)) {
                        this.mPartColorSecondPlate.setImageResource(R.drawable.p_16_dd_parts_pe_adjust_disable_normal);
                        return;
                    } else {
                        this.mPartColorSecondPlate.setImageResource(R.drawable.p_16_dd_parts_pe_adjust_normal);
                        return;
                    }
                case 2:
                case 3:
                    if (this.mCurrentPlateSecondSelectedItemId.equals(PictureEffectPlusController.PART_COLOR_NON_SET)) {
                        this.mPartColorFourthPlate.setImageResource(R.drawable.p_16_dd_parts_pe_adjust_disable_normal);
                        return;
                    } else if (this.mCurrentPlateSecondSelectedItemId.equals(PictureEffectPlusController.PART_COLOR_CUSTOM_SET)) {
                        this.mPartColorFourthPlate.setImageResource(R.drawable.p_16_dd_parts_pe_adjust_disable_normal);
                        return;
                    } else {
                        this.mPartColorFourthPlate.setImageResource(R.drawable.p_16_dd_parts_pe_adjust_normal);
                        return;
                    }
                default:
                    return;
            }
        }
    }

    private HistoryItem findPrevScreen() {
        HistoryItem Obj = this.mService.popMenuHistory();
        while (Obj != null) {
            if (Obj.layoutId.equals("ID_PICTUREEFFECTPLUSOPTIONMENULAYOUT")) {
                HistoryItem returnHisItem = new HistoryItem(PictureEffectPlusController.PICTUREEFFECTPLUS, "ID_PICTUREEFFECTPLUSOPTIONMENULAYOUT");
                return returnHisItem;
            }
            Obj = this.mService.popMenuHistory();
        }
        return null;
    }
}
