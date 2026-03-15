package com.sony.imaging.app.lightshaft.menu.layout;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.lightshaft.AppLog;
import com.sony.imaging.app.lightshaft.LightShaftBackUpKey;
import com.sony.imaging.app.lightshaft.R;
import com.sony.imaging.app.lightshaft.shooting.ShaftsEffect;
import com.sony.imaging.app.lightshaft.shooting.camera.EffectSelectController;
import com.sony.imaging.app.lightshaft.shooting.camera.EffectSelectOptionController;
import com.sony.imaging.app.lightshaft.shooting.widget.OverHeadShaftView;
import com.sony.imaging.app.lightshaft.shooting.widget.ShaftView;
import com.sony.imaging.app.lightshaft.shooting.widget.VerticalSeekBar;
import com.sony.imaging.app.util.ApoWrapper;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class OptionSettingMenuLayout extends DisplayMenuItemsMenuLayout {
    private static final String TAG = "OptionSettingMenuLayout";
    private FooterGuide mFooterGuide;
    private TextView mGaugetext;
    private OverHeadShaftView mOverHeadView;
    private View mCurrentView = null;
    private int[] mShaftValue = null;
    private ShaftView mShaftView = null;
    private VerticalSeekBar mSlider = null;
    private TextView mOpLeftValue = null;
    private TextView mOpCenterValue = null;
    private TextView mOpRightValue = null;
    private ImageView mOpLeftFocus = null;
    private ImageView mOpLeftSeperator = null;
    private ImageView mOpCenterFocus = null;
    private ImageView mOpRightFocus = null;
    private ImageView mOpLeftIcon = null;
    private ImageView mOpCenterIcon = null;
    private ImageView mOpRightIcon = null;
    private RelativeLayout mRLayout = null;
    private RelativeLayout mBaseLayout = null;
    private final int RANGE = 0;
    private final int STRENGTH = 1;
    private final int LENGTH = 2;
    private final int INVISIBLE_ALPHA = 0;
    private final int VALID_ALPHA = 255;
    private int mLevelOfRange = 8;
    private int mLevelOfLength = 11;
    private int mLevelOfStrength = 7;
    private int mCurrentFocusOption = 0;
    private boolean mRangeAvailable = true;
    private ShaftsEffect.Parameters mOldOptionParam = null;
    private ShaftsEffect.Parameters mParams = null;
    private EffectSelectOptionController mOptionController = null;
    private FocusTouchListener mFocusListener = null;
    private boolean mIsBackButtonPressed = false;
    private TextView mScreenTitleView = null;

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.mCurrentView = obtainViewFromPool(R.layout.menu_ls_options_settings);
        Context context = getActivity().getApplicationContext();
        this.mService = new BaseMenuService(context);
        if (this.mUpdater == null) {
            this.mUpdater = getMenuUpdater();
        }
        return this.mCurrentView;
    }

    private void initializeView() {
        this.mRLayout = (RelativeLayout) this.mCurrentView.findViewById(R.id.mRLayout);
        this.mBaseLayout = (RelativeLayout) this.mCurrentView.findViewById(R.id.iconBase);
        this.mOverHeadView = (OverHeadShaftView) this.mCurrentView.findViewById(R.id.over_head_view);
        this.mOpLeftFocus = (ImageView) this.mCurrentView.findViewById(R.id.optionLeft);
        this.mOpLeftValue = (TextView) this.mCurrentView.findViewById(R.id.optionLeftValue);
        this.mOpLeftIcon = (ImageView) this.mCurrentView.findViewById(R.id.optionLeftImage);
        this.mOpLeftSeperator = (ImageView) this.mCurrentView.findViewById(R.id.leftDevider);
        this.mOpCenterFocus = (ImageView) this.mCurrentView.findViewById(R.id.optionCenter);
        this.mOpCenterValue = (TextView) this.mCurrentView.findViewById(R.id.optionCenterValue);
        this.mOpCenterIcon = (ImageView) this.mCurrentView.findViewById(R.id.optionCenterImage);
        this.mOpRightFocus = (ImageView) this.mCurrentView.findViewById(R.id.optionRight);
        this.mOpRightIcon = (ImageView) this.mCurrentView.findViewById(R.id.optionRightImage);
        this.mOpRightValue = (TextView) this.mCurrentView.findViewById(R.id.optionRightValue);
        checkFlareAvailabilty();
        this.mSlider = (VerticalSeekBar) this.mCurrentView.findViewById(R.id.vertical_Seekbar);
        this.mSlider.setProgressDrawable(null);
        this.mShaftView = (ShaftView) this.mCurrentView.findViewById(R.id.light_shaft);
        this.mScreenTitleView = (TextView) this.mCurrentView.findViewById(R.id.menu_screen_title);
        this.mGaugetext = (TextView) this.mCurrentView.findViewById(R.id.vertical_sb_progresstext);
        this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide);
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        initializeObject();
        initializeView();
        setFooterguide();
        setListener();
        setDefaultValue();
        modifyOption();
        setKeyBeepPattern(0);
        ApoWrapper.setApoType(ApoWrapper.APO_TYPE.SPECIAL);
        this.mIsBackButtonPressed = false;
        EffectSelectController.getInstance().setShootingEnable(false);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        if (event.getScanCode() == 595 || event.getScanCode() == 513) {
            return -1;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void setFooterguide() {
        this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), R.string.STRID_FOOTERGUIDE_ENTER_RETURN, R.string.STRID_FOOTERGUIDE_ENTER_RETURN_SK));
    }

    private void setInformationText(String progress) {
        this.mGaugetext.setText(progress);
    }

    private void initializeObject() {
        String lastSavedOptionParams = LightShaftBackUpKey.getInstance().getLastSavedOptionSettings();
        AppLog.info(TAG, " Option parameters backup values:" + lastSavedOptionParams);
        this.mParams = ShaftsEffect.getInstance().getParameters();
        AppLog.info(TAG, "Option parameters current values(before unflatten):" + this.mParams.flatten());
        this.mParams.unflatten(lastSavedOptionParams);
        AppLog.info(TAG, "Option parameters current values(after unflatten):" + this.mParams.flatten());
        if (this.mOptionController == null) {
            this.mOptionController = EffectSelectOptionController.getInstance();
        }
        if (this.mShaftValue == null) {
            this.mShaftValue = new int[]{2, 4, 6, 8};
        }
    }

    private void setListener() {
        this.mFocusListener = new FocusTouchListener();
        this.mOpLeftFocus.setOnTouchListener(this.mFocusListener);
        this.mOpCenterFocus.setOnTouchListener(this.mFocusListener);
        this.mOpRightFocus.setOnTouchListener(this.mFocusListener);
    }

    private void setDefaultValue() {
        handleFocus();
        getSupportDefaultValue(1);
        getSupportDefaultValue(2);
        if (this.mRangeAvailable) {
            if (ShaftsEffect.getInstance().getParameters().getEffect() == 2) {
                this.mOptionController.setValue(null, EffectSelectOptionController.SHAFT);
                this.mLevelOfRange = this.mParams.getNumberOfShafts();
            } else {
                this.mOptionController.setValue(null, EffectSelectOptionController.RANGE);
                this.mLevelOfRange = setBoundryLimits(this.mParams.getRange() + 1, 1, this.mParams.getLimitRange());
            }
            this.mSlider.setMax(getWheelRange());
            setSelectdText(this.mOpLeftValue, this.mParams.getNumberOfShafts() / 2);
            if (this.mCurrentFocusOption == 0) {
                setInformationText("" + this.mLevelOfRange);
            }
        }
    }

    private void handleFocus() {
        this.mCurrentFocusOption = 0;
        if (ShaftsEffect.getInstance().getParameters().getEffect() == 3) {
            this.mCurrentFocusOption = 1;
        }
    }

    private void getSupportDefaultValue(int option) {
        switch (option) {
            case 1:
                this.mOptionController.setValue(null, EffectSelectOptionController.STRENGTH);
                this.mLevelOfStrength = setBoundryLimits(this.mParams.getStrength(), 0, this.mParams.getLimitStrength());
                setSelectdText(this.mOpCenterValue, this.mLevelOfStrength + 1);
                if (this.mCurrentFocusOption == 1) {
                    setInformationText("" + (this.mLevelOfStrength + 1));
                    return;
                }
                return;
            case 2:
                this.mOptionController.setValue(null, EffectSelectOptionController.LENGTH);
                this.mLevelOfLength = setBoundryLimits(this.mParams.getLength(), 0, this.mParams.getLimitLength());
                setSelectdText(this.mOpRightValue, this.mLevelOfLength + 1);
                if (this.mCurrentFocusOption == 2) {
                    setInformationText("" + (this.mLevelOfLength + 1));
                    return;
                }
                return;
            default:
                AppLog.info(TAG, TAG + option);
                return;
        }
    }

    private void setSelectdText(TextView textView, int value) {
        setBoundryLimits(value, 1, getWheelRange());
        textView.setText("" + value);
        this.mSlider.setProgress(value);
    }

    private void setVisibility(int visibility) {
        this.mOpLeftValue.setVisibility(visibility);
        this.mOpLeftIcon.setVisibility(visibility);
        this.mOpLeftFocus.setVisibility(visibility);
    }

    private void changeValue(int level) {
        updateValueTouch(level);
    }

    private void updateValue(int status) {
        int updateValue;
        switch (this.mCurrentFocusOption) {
            case 0:
                if (ShaftsEffect.getInstance().getParameters().getEffect() == 2) {
                    updateValue = this.mParams.getNumberOfShafts() + (status * 2);
                } else {
                    updateValue = this.mParams.getRange() + status;
                }
                setRange(updateValue);
                return;
            case 1:
                int updateValue2 = this.mParams.getStrength() + status;
                setStrength(updateValue2);
                return;
            case 2:
                int updateValue3 = this.mParams.getLength() + status;
                setLength(updateValue3);
                return;
            default:
                AppLog.info(TAG, TAG + this.mCurrentFocusOption);
                return;
        }
    }

    private void updateValueTouch(int status) {
        switch (this.mCurrentFocusOption) {
            case 0:
                if (ShaftsEffect.getInstance().getParameters().getEffect() == 2) {
                    AppLog.info(TAG, "drag" + status);
                    status = this.mShaftValue[(status * 2) + 2];
                }
                setRange(status);
                return;
            case 1:
                setStrength(status);
                return;
            case 2:
                setLength(status);
                return;
            default:
                AppLog.info(TAG, TAG + this.mCurrentFocusOption);
                return;
        }
    }

    private void setLength(int updatedLength) {
        try {
            this.mLevelOfLength = updatedLength;
            this.mLevelOfLength = setBoundryLimits(updatedLength, 0, this.mParams.getLimitLength());
            if (this.mLevelOfLength == this.mParams.getLimitLength()) {
                this.mParams.setLength(this.mLevelOfLength - 1);
            } else {
                this.mParams.setLength(this.mLevelOfLength);
            }
            this.mOpRightValue.setText(String.valueOf(this.mParams.getLength() + 1));
            setInformationText("" + String.valueOf(this.mParams.getLength() + 1));
            updateShaftView();
        } catch (Exception e) {
            AppLog.info(TAG, "Length Over");
        }
    }

    private void setStrength(int updatedStrength) {
        try {
            this.mLevelOfStrength = setBoundryLimits(updatedStrength, 0, this.mParams.getLimitStrength());
            if (this.mLevelOfLength == this.mParams.getLimitStrength()) {
                this.mParams.setStrength(this.mLevelOfStrength - 1);
            } else {
                this.mParams.setStrength(this.mLevelOfStrength);
            }
            this.mOpCenterValue.setText(String.valueOf(this.mParams.getStrength() + 1));
            setInformationText("" + String.valueOf(this.mParams.getStrength() + 1));
            updateShaftView();
        } catch (Exception e) {
            AppLog.info(TAG, "Strength Over");
        }
    }

    private void setRange(int updatedRange) {
        try {
            if (ShaftsEffect.getInstance().getParameters().getEffect() == 2) {
                this.mLevelOfRange = setBoundryLimitsShaft(updatedRange, 2, this.mParams.getLimitNumberOfShafts());
                this.mParams.setNumberOfShafts(this.mLevelOfRange);
                this.mOpLeftValue.setText(String.valueOf(this.mParams.getNumberOfShafts()));
                setInformationText("" + String.valueOf(this.mParams.getNumberOfShafts()));
            } else {
                this.mLevelOfRange = setBoundryLimits(updatedRange, 0, this.mParams.getLimitRange());
                this.mParams.setRange(this.mLevelOfRange);
                this.mOpLeftValue.setText(String.valueOf(this.mLevelOfRange + 1));
                setInformationText("" + (this.mLevelOfRange + 1));
            }
            updateShaftView();
        } catch (Exception e) {
            AppLog.info(TAG, "Range Over");
        }
    }

    private int setBoundryLimits(int levelOfValue, int minSize, int maxSize) {
        if (levelOfValue < minSize) {
            levelOfValue = minSize;
        }
        return levelOfValue > maxSize + (-1) ? maxSize - 1 : levelOfValue;
    }

    private int setBoundryLimitsShaft(int levelOfValue, int minSize, int maxSize) {
        if (levelOfValue < minSize) {
            levelOfValue = minSize;
        }
        return levelOfValue > maxSize ? maxSize : levelOfValue;
    }

    private void updateShaftView() {
        this.mShaftView.setParameters(this.mParams);
        this.mOverHeadView.setParameters(this.mParams);
    }

    private void checkFlareAvailabilty() {
        if (ShaftsEffect.getInstance().getParameters().getEffect() == 3) {
            this.mRangeAvailable = false;
            if (this.mCurrentFocusOption == 0) {
                this.mCurrentFocusOption = 1;
            }
            setVisibility(8);
            this.mOptionController.setValue(null, EffectSelectOptionController.STRENGTH);
            this.mOpCenterFocus.requestFocus();
        } else {
            setVisibility(0);
            this.mOpLeftFocus.requestFocus();
            this.mRangeAvailable = true;
        }
        rangePositionHandling();
    }

    private int getWheelRange() {
        return this.mOptionController.getSupportedOptionValueRange() - 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class FocusTouchListener implements View.OnTouchListener {
        FocusTouchListener() {
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View v, MotionEvent event) {
            return false;
        }
    }

    private void modifyOption() {
        switch (this.mCurrentFocusOption) {
            case 0:
                if (ShaftsEffect.getInstance().getParameters().getEffect() == 2) {
                    this.mOptionController.setValue(null, EffectSelectOptionController.SHAFT);
                    this.mOptionController.setOptionValue(EffectSelectOptionController.SHAFT, String.valueOf(0));
                    this.mSlider.setMax(getWheelRange());
                    setRange(this.mParams.getNumberOfShafts());
                    this.mSlider.setProgress((this.mParams.getNumberOfShafts() / 2) - 1);
                    AppLog.trace(TAG, "mSlider.GetProgress =" + this.mSlider.getProgress());
                    break;
                } else {
                    this.mOptionController.setValue(null, EffectSelectOptionController.RANGE);
                    this.mSlider.setMax(getWheelRange());
                    setRange(this.mParams.getRange());
                    this.mSlider.setProgress(this.mParams.getRange());
                    break;
                }
            case 1:
                this.mOptionController.setValue(null, EffectSelectOptionController.STRENGTH);
                this.mSlider.setMax(getWheelRange());
                setStrength(this.mParams.getStrength());
                this.mSlider.setProgress(this.mParams.getStrength());
                break;
            case 2:
                this.mOptionController.setValue(null, EffectSelectOptionController.LENGTH);
                this.mSlider.setMax(getWheelRange());
                setLength(this.mParams.getLength());
                this.mSlider.setProgress(this.mParams.getLength());
                break;
            default:
                AppLog.info(TAG, TAG + this.mCurrentFocusOption);
                break;
        }
        updateOptionValue();
        updateTitles();
    }

    private void updateTitles() {
        switch (ShaftsEffect.getInstance().getParameters().getEffect()) {
            case 2:
                updateScreenTitle(R.string.STRID_FUNC_ITEM_NUMOFRAYS, R.string.STRID_FUNC_ITEM_STRENGTH, R.string.STRID_FUNC_ITEM_LENGTH);
                return;
            case 3:
                updateScreenTitle(R.string.STRID_FUNC_ITEM_WIDTH, R.string.STRID_FUNC_ITEM_STRENGTH, R.string.STRID_FUNC_ITEM_LENGTH);
                return;
            default:
                updateScreenTitle(R.string.STRID_FUNC_ITEM_WIDTH, R.string.STRID_FUNC_ITEM_STRENGTH, R.string.STRID_FUNC_ITEM_LENGTH);
                return;
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        switch (this.mCurrentFocusOption) {
            case 0:
                if (ShaftsEffect.getInstance().getParameters().getEffect() == 2) {
                    setGuideResource(guideResources, R.string.STRID_FUNC_ITEM_NUMOFRAYS, R.string.STRID_FUNC_ITEM_NUMOFRAYS_GUIDE, true);
                    return;
                } else {
                    setGuideResource(guideResources, R.string.STRID_FUNC_ITEM_WIDTH, R.string.STRID_FUNC_ITEM_WIDTH_GUIDE, true);
                    return;
                }
            case 1:
                setGuideResource(guideResources, R.string.STRID_FUNC_ITEM_STRENGTH, R.string.STRID_FUNC_ITEM_STRENGTH_GUIDE, true);
                return;
            case 2:
                setGuideResource(guideResources, R.string.STRID_FUNC_ITEM_LENGTH, R.string.STRID_FUNC_ITEM_LENGTH_GUIDE, true);
                return;
            default:
                return;
        }
    }

    private void setGuideResource(ArrayList<Object> guideResources, int guideTitleID, int guideDefi, boolean isAvailble) {
        guideResources.add(getText(guideTitleID));
        guideResources.add(getText(guideDefi));
        guideResources.add(Boolean.valueOf(isAvailble));
    }

    private void updateScreenTitle(int rangeTitleID, int strengthTitleID, int lengthTitleID) {
        String title;
        switch (this.mCurrentFocusOption) {
            case 1:
                title = getResources().getString(strengthTitleID);
                break;
            case 2:
                title = getResources().getString(lengthTitleID);
                break;
            default:
                title = getResources().getString(rangeTitleID);
                break;
        }
        this.mScreenTitleView.setText(title);
    }

    private void updateOptionValue() {
        if (ShaftsEffect.getInstance().getParameters().getEffect() == 2) {
            this.mOpLeftIcon.setImageResource(R.drawable.p_16_dd_parts_icon_numofshafts);
        } else {
            this.mOpLeftIcon.setImageResource(R.drawable.p_16_dd_parts_icon_range);
        }
        this.mOpCenterIcon.setImageResource(R.drawable.p_16_dd_parts_icon_strength);
        this.mOpRightIcon.setImageResource(R.drawable.p_16_dd_parts_icon_length);
        this.mOpLeftValue.setTextColor(getResources().getColor(R.color.RESID_FONTSTYLE_CMN_WHITE_NO_SKIN));
        this.mOpCenterIcon.setAlpha(255);
        this.mOpCenterValue.setTextColor(getResources().getColor(R.color.RESID_FONTSTYLE_CMN_WHITE_NO_SKIN));
        this.mOpLeftIcon.setAlpha(255);
        this.mOpLeftValue.setTextColor(getResources().getColor(R.color.RESID_FONTSTYLE_CMN_WHITE_NO_SKIN));
        this.mOpRightValue.setTextColor(getResources().getColor(R.color.RESID_FONTSTYLE_CMN_WHITE_NO_SKIN));
        this.mOpLeftFocus.setBackgroundResource(0);
        this.mOpCenterFocus.setBackgroundResource(0);
        this.mOpRightFocus.setBackgroundResource(0);
        switch (this.mCurrentFocusOption) {
            case 0:
                this.mOpLeftFocus.setAlpha(255);
                if (ShaftsEffect.getInstance().getParameters().getEffect() == 2) {
                    this.mOpLeftIcon.setImageResource(R.drawable.p_16_dd_parts_icon_numofshafts);
                } else {
                    this.mOpLeftIcon.setImageResource(R.drawable.p_16_dd_parts_icon_range);
                }
                this.mOpLeftFocus.setBackgroundResource(17306069);
                this.mOpLeftValue.setTextColor(getResources().getColor(R.color.RESID_FONTSTYLE_CMN_WHITE_NO_SKIN));
                return;
            case 1:
                this.mOpCenterFocus.setAlpha(255);
                this.mOpCenterIcon.setImageResource(R.drawable.p_16_dd_parts_icon_strength);
                this.mOpCenterValue.setTextColor(getResources().getColor(R.color.RESID_FONTSTYLE_CMN_WHITE_NO_SKIN));
                this.mOpCenterFocus.setBackgroundResource(17306069);
                return;
            case 2:
                this.mOpRightFocus.setAlpha(255);
                this.mOpRightIcon.setImageResource(R.drawable.p_16_dd_parts_icon_length);
                this.mOpRightValue.setTextColor(getResources().getColor(R.color.RESID_FONTSTYLE_CMN_WHITE_NO_SKIN));
                this.mOpRightFocus.setBackgroundResource(17306069);
                return;
            default:
                AppLog.info(TAG, TAG + this.mCurrentFocusOption);
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

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        return pushedDownKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        return pushedUpKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        return pushedLeftKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        return pushedRightKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        setEnableFocus(1);
        modifyOption();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        setEnableFocus(-1);
        modifyOption();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        this.mSlider.moveDown();
        updateValue(-1);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        this.mSlider.moveUp();
        updateValue(1);
        return 1;
    }

    private void setEnableFocus(int movePosition) {
        this.mCurrentFocusOption = (this.mCurrentFocusOption + movePosition) % 3;
        if (this.mRangeAvailable && this.mCurrentFocusOption < 0) {
            this.mCurrentFocusOption = 2;
            return;
        }
        if (!this.mRangeAvailable && this.mCurrentFocusOption <= 0) {
            if (movePosition == 1) {
                this.mCurrentFocusOption = 1;
            } else {
                this.mCurrentFocusOption = 2;
            }
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK2Key() {
        ShaftsEffect.getInstance().setParameters(this.mParams);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        ShaftsEffect.getInstance().setParameters(this.mParams);
        openPreviousMenu();
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        this.mIsBackButtonPressed = true;
        openPreviousMenu();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    public void closeMenuLayout(Bundle bundle) {
        if (bundle == null) {
            updateParameter();
        }
        super.closeMenuLayout(bundle);
    }

    private void updateParameter() {
        ShaftsEffect.getInstance().setParameters(this.mParams);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedModeDial() {
        updateParameter();
        return super.turnedModeDial();
    }

    private void restoreOldValues() {
        this.mParams.setLength(this.mOldOptionParam.getLength());
        if (ShaftsEffect.getInstance().getParameters().getEffect() == 2) {
            this.mParams.setNumberOfShafts(this.mOldOptionParam.getNumberOfShafts());
        } else {
            if (this.mOldOptionParam.getRange() < 0) {
                this.mOldOptionParam.setRange(0);
            }
            this.mParams.setRange(this.mOldOptionParam.getRange());
        }
        this.mParams.setStrength(this.mOldOptionParam.getStrength());
        updateShaftView();
    }

    private void deInitializeView() {
        this.mShaftView = null;
        this.mOverHeadView = null;
        this.mOptionController = null;
        this.mSlider = null;
        this.mParams = null;
        releaseImageViewDrawable(this.mOpLeftFocus);
        releaseImageViewDrawable(this.mOpCenterFocus);
        releaseImageViewDrawable(this.mOpRightFocus);
        releaseImageViewDrawable(this.mOpLeftIcon);
        releaseImageViewDrawable(this.mOpCenterIcon);
        releaseImageViewDrawable(this.mOpRightIcon);
        if (this.mCurrentView != null) {
            this.mCurrentView.destroyDrawingCache();
            this.mCurrentView = null;
        }
        this.mOldOptionParam = null;
        this.mFocusListener = null;
        this.mOpLeftValue = null;
        this.mOpCenterValue = null;
        this.mOpRightValue = null;
        this.mShaftValue = null;
        this.mRLayout = null;
        this.mBaseLayout = null;
        this.mIsBackButtonPressed = false;
        this.mRangeAvailable = true;
        this.mCurrentFocusOption = 0;
        this.mFooterGuide = null;
    }

    private void releaseImageViewDrawable(ImageView imageView) {
        if (imageView != null) {
            if (imageView.getDrawable() != null) {
                imageView.getDrawable().setCallback(null);
            }
            imageView.setImageDrawable(null);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        if (!this.mIsBackButtonPressed) {
            LightShaftBackUpKey.getInstance().saveOptionSettings(this.mParams.flatten());
        }
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (this.mOptionController != null) {
            this.mOptionController.setValue(null, EffectSelectOptionController.RANGE);
        }
        super.onPause();
        deInitializeView();
    }

    private void rangePositionHandling() {
        RelativeLayout.LayoutParams baseicon;
        RelativeLayout.LayoutParams adaptLayout = new RelativeLayout.LayoutParams(192, 66);
        if (this.mRangeAvailable) {
            baseicon = new RelativeLayout.LayoutParams(288, 66);
            baseicon.setMargins(186, 360, 0, 0);
            adaptLayout.setMargins(282, 360, 0, 0);
            this.mOpLeftSeperator.setVisibility(0);
        } else {
            baseicon = new RelativeLayout.LayoutParams(192, 66);
            baseicon.setMargins(240, 360, 0, 0);
            adaptLayout.setMargins(240, 360, 0, 0);
            this.mOpLeftSeperator.setVisibility(4);
        }
        this.mRLayout.setLayoutParams(adaptLayout);
        this.mBaseLayout.setLayoutParams(baseicon);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedGuideFuncKey() {
        return -1;
    }
}
