package com.sony.imaging.app.manuallenscompensation.menu.layout;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.MenuDataParcelable;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.manuallenscompensation.OpticalCompensation;
import com.sony.imaging.app.manuallenscompensation.R;
import com.sony.imaging.app.manuallenscompensation.caution.layout.OCInfo;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppContext;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCBackUpKey;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCConstants;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil;
import com.sony.imaging.app.manuallenscompensation.database.LensCompensationParameter;
import com.sony.imaging.app.manuallenscompensation.shooting.controller.OCController;
import com.sony.imaging.app.manuallenscompensation.widget.LevelGauge;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class LensEditMenuLayout extends DisplayMenuItemsMenuLayout implements View.OnFocusChangeListener, View.OnTouchListener {
    private static final String BACK = "back";
    public static final String ID_MENU_GUIDE = "MENU_GUIDE";
    private static final String TAG = "LensEditMenuLayout";
    private LevelGauge mChromaticBlue;
    private ImageButton mChromaticLayout;
    private LevelGauge mChromaticRed;
    protected View mCurrentView;
    private LevelGauge mDistortion;
    private ImageButton mDistortionLayout;
    private TextView mFValueViewFirst;
    private TextView mFValueViewSecond;
    private TextView mFocalLengthView;
    private int mFocusedItem;
    private TextView mLensNameView;
    private ImageView mMinus_level1;
    private ImageView mMinus_level1PE;
    private LensCompensationParameter mOldParam;
    private LensCompensationParameter mParam;
    private ImageView mPlus_level1;
    private ImageView mPlus_level1PE;
    private ImageView mSave;
    private LevelGauge mShadingBlue;
    private LevelGauge mShadingBluePE;
    private ImageButton mShadingLayout;
    private LevelGauge mShadingRed;
    private LevelGauge mShadingRedPE;
    private LevelGauge mShadingWhite;
    private LevelGauge mShadingWhitePE;
    private TextView mShooting_Shading;
    private TextView mShooting_Shading_pe;
    private OnButtonClickListener mOnButtonClickListener = null;
    private EditTextInputListener mEditTextInputListener = null;
    private String mPreviousMenuLayoutID = "";
    private int mCancelGridBackup = 0;
    int mSelectionPosition = -1;
    private ArrayList<Integer> mScanCodeArrayList = null;
    private final String TOUCH_DISSABLED_ON_VIEW = "Touch operation disabled on view which ID is= ";
    private boolean isKeyBoardOpened = false;

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (this.mCurrentView == null) {
            this.mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.menu_lens_edit);
        }
        createView();
        return this.mCurrentView;
    }

    private void createView() {
        this.mLensNameView = (TextView) this.mCurrentView.findViewById(R.id.LensName);
        this.mLensNameView.setOnKeyListener(new View.OnKeyListener() { // from class: com.sony.imaging.app.manuallenscompensation.menu.layout.LensEditMenuLayout.1
            @Override // android.view.View.OnKeyListener
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == 0 && keyCode == 23) {
                    LensEditMenuLayout.this.isKeyBoardOpened = true;
                    return false;
                }
                return false;
            }
        });
        this.mFocalLengthView = (TextView) this.mCurrentView.findViewById(R.id.FocalLength);
        this.mFocalLengthView.setOnKeyListener(new View.OnKeyListener() { // from class: com.sony.imaging.app.manuallenscompensation.menu.layout.LensEditMenuLayout.2
            @Override // android.view.View.OnKeyListener
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == 0 && keyCode == 23) {
                    LensEditMenuLayout.this.isKeyBoardOpened = true;
                    return false;
                }
                return false;
            }
        });
        this.mFValueViewFirst = (TextView) this.mCurrentView.findViewById(R.id.FValueOne);
        this.mFValueViewFirst.setOnKeyListener(new View.OnKeyListener() { // from class: com.sony.imaging.app.manuallenscompensation.menu.layout.LensEditMenuLayout.3
            @Override // android.view.View.OnKeyListener
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == 0 && keyCode == 23) {
                    LensEditMenuLayout.this.isKeyBoardOpened = true;
                    return false;
                }
                return false;
            }
        });
        this.mFValueViewSecond = (TextView) this.mCurrentView.findViewById(R.id.FValueSecond);
        this.mShadingLayout = (ImageButton) this.mCurrentView.findViewById(R.id.light);
        this.mChromaticLayout = (ImageButton) this.mCurrentView.findViewById(R.id.color);
        this.mDistortionLayout = (ImageButton) this.mCurrentView.findViewById(R.id.distortion);
        setShadingIcon();
        this.mChromaticRed = (LevelGauge) this.mCurrentView.findViewById(R.id.chromatic_red);
        this.mChromaticBlue = (LevelGauge) this.mCurrentView.findViewById(R.id.chromatic_blue);
        this.mDistortion = (LevelGauge) this.mCurrentView.findViewById(R.id.distortion_white);
        this.mSave = (ImageView) this.mCurrentView.findViewById(R.id.saveImageButton);
        FooterGuide guide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide);
        if (guide != null) {
            guide.setBackgroundResource(17304085);
            guide.setData(new FooterGuideDataResId(getActivity(), android.R.string.zen_mode_feature_name, android.R.string.httpErrorFailedSslHandshake));
        }
        touchDisbale();
    }

    private void touchDisbale() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mLensNameView.setOnTouchListener(this);
        this.mFocalLengthView.setOnTouchListener(this);
        this.mFValueViewFirst.setOnTouchListener(this);
        this.mFValueViewSecond.setOnTouchListener(this);
        this.mShooting_Shading.setOnTouchListener(this);
        this.mShadingLayout.setOnTouchListener(this);
        this.mChromaticLayout.setOnTouchListener(this);
        this.mDistortionLayout.setOnTouchListener(this);
        this.mShadingWhite.setOnTouchListener(this);
        this.mShadingRed.setOnTouchListener(this);
        this.mShadingBlue.setOnTouchListener(this);
        this.mChromaticRed.setOnTouchListener(this);
        this.mChromaticBlue.setOnTouchListener(this);
        this.mDistortion.setOnTouchListener(this);
        this.mSave.setOnTouchListener(this);
        this.mPlus_level1.setOnTouchListener(this);
        this.mMinus_level1.setOnTouchListener(this);
        this.mShadingWhitePE.setOnTouchListener(this);
        this.mShadingRedPE.setOnTouchListener(this);
        this.mShadingBluePE.setOnTouchListener(this);
        this.mPlus_level1PE.setOnTouchListener(this);
        this.mMinus_level1PE.setOnTouchListener(this);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void setShadingIcon() {
        this.mShadingWhite = (LevelGauge) this.mCurrentView.findViewById(R.id.shading_white);
        this.mShadingRed = (LevelGauge) this.mCurrentView.findViewById(R.id.shading_red);
        this.mShadingBlue = (LevelGauge) this.mCurrentView.findViewById(R.id.shading_blue);
        this.mPlus_level1 = (ImageView) this.mCurrentView.findViewById(R.id.plus_level1);
        this.mMinus_level1 = (ImageView) this.mCurrentView.findViewById(R.id.minus_level1);
        this.mShadingWhitePE = (LevelGauge) this.mCurrentView.findViewById(R.id.shading_white_pe);
        this.mShadingRedPE = (LevelGauge) this.mCurrentView.findViewById(R.id.shading_red_pe);
        this.mShadingBluePE = (LevelGauge) this.mCurrentView.findViewById(R.id.shading_blue_pe);
        this.mPlus_level1PE = (ImageView) this.mCurrentView.findViewById(R.id.plus_level1_pe);
        this.mMinus_level1PE = (ImageView) this.mCurrentView.findViewById(R.id.minus_level1_pe);
        this.mShooting_Shading = (TextView) this.mCurrentView.findViewById(R.id.shooting_Shading);
        this.mShooting_Shading_pe = (TextView) this.mCurrentView.findViewById(R.id.shooting_Shading_pe);
        if (OCController.getInstance().isSupportPictureEffect()) {
            this.mShadingWhite.setVisibility(0);
            this.mShadingRed.setVisibility(0);
            this.mShadingBlue.setVisibility(0);
            this.mPlus_level1.setVisibility(0);
            this.mMinus_level1.setVisibility(0);
            this.mShooting_Shading.setVisibility(0);
            this.mShadingWhitePE.setVisibility(4);
            this.mShadingRedPE.setVisibility(4);
            this.mShadingBluePE.setVisibility(4);
            this.mPlus_level1PE.setVisibility(4);
            this.mMinus_level1PE.setVisibility(4);
            this.mShooting_Shading_pe.setVisibility(4);
            return;
        }
        this.mShadingWhite.setVisibility(4);
        this.mShadingRed.setVisibility(4);
        this.mShadingBlue.setVisibility(4);
        this.mPlus_level1.setVisibility(4);
        this.mMinus_level1.setVisibility(4);
        this.mShooting_Shading.setVisibility(4);
        this.mShadingWhitePE.setVisibility(0);
        this.mShadingRedPE.setVisibility(0);
        this.mShadingBluePE.setVisibility(0);
        this.mPlus_level1PE.setVisibility(0);
        this.mMinus_level1PE.setVisibility(0);
        this.mShooting_Shading_pe.setVisibility(0);
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.fw.Layout
    public void onReopened() {
        onResume();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.isKeyBoardOpened = false;
        OCUtil.getInstance().setCloseMenuLayoutExecuted(false);
        MenuDataParcelable parcelable = (MenuDataParcelable) this.data.getParcelable(MenuDataParcelable.KEY);
        if (this.data.containsKey("ItemId")) {
            String itemID = this.data.getString("ItemId");
            if (ExposureModeController.EXPOSURE_MODE.equals(itemID)) {
                this.data.remove("ItemId");
            }
        }
        if (parcelable != null && !BACK.equals(parcelable.getItemId())) {
            this.mPreviousMenuLayoutID = parcelable.getPreviousMenuLayoutId();
            if (this.mPreviousMenuLayoutID.equalsIgnoreCase(LensListMenuLayout.TAG)) {
                this.mCancelGridBackup = BackUpUtil.getInstance().getPreferenceInt(OCBackUpKey.KEY_BKUP_GRIDLINE_STATE, 0);
                this.mParam = (LensCompensationParameter) this.data.getParcelable(LensCompensationParameter.ID_PARAMETER);
            }
            if (this.mParam == null) {
                this.mParam = OCUtil.getInstance().getLensParameterObject();
                this.mSelectionPosition = BackUpUtil.getInstance().getPreferenceInt(OCBackUpKey.KEY_BKUP_SELECTED_POSITION, -1);
            } else {
                this.mSelectionPosition = this.data.getInt(LensListMenuLayout.TAG, -1);
            }
            this.mOldParam = LensCompensationParameter.createCopiedObject(this.mParam);
        }
        initilizeObjects();
        setValueOnResume();
        OCUtil.getInstance().updateParamObjectWithChangedParameters(this.mParam);
    }

    private void initializeEditListener() {
        if (this.mEditTextInputListener == null) {
            this.mEditTextInputListener = new EditTextInputListener();
        }
        this.mLensNameView.setOnEditorActionListener(this.mEditTextInputListener);
        this.mFocalLengthView.setOnEditorActionListener(this.mEditTextInputListener);
        this.mFValueViewFirst.setOnEditorActionListener(this.mEditTextInputListener);
    }

    private void initilizeObjects() {
        if (this.data != null) {
            this.mParam = (LensCompensationParameter) this.data.getParcelable(LensCompensationParameter.ID_PARAMETER);
            if (this.mParam == null) {
                this.mParam = LensCompensationParameter.createLensCompensationParameter();
                handleParamFromGlobalMenu();
            } else {
                this.mSelectionPosition = this.data.getInt(LensListMenuLayout.TAG, -1);
            }
            int selection = this.data.getInt(OCConstants.SETTING_KIND, 0);
            this.mLensNameView.setText(this.mParam.mLensName);
            setInitialFocus(selection);
        }
        OCUtil.getInstance().printLensParametersLogs(this.mParam);
        if (!this.mParam.mFocalLength.equalsIgnoreCase(String.valueOf(0))) {
            this.mFocalLengthView.setText(this.mParam.mFocalLength);
        } else {
            this.mFocalLengthView.setText("");
        }
        String fValueIntPart = "";
        String fValueDecPart = "";
        if (this.mParam.mFValue.contains(".")) {
            int dotIndex = this.mParam.mFValue.indexOf(".");
            fValueIntPart = this.mParam.mFValue.substring(0, dotIndex);
            fValueDecPart = this.mParam.mFValue.substring(dotIndex + 1);
        }
        if (!" ".equals(fValueIntPart)) {
            this.mFValueViewFirst.setText(fValueIntPart);
        } else {
            this.mFValueViewFirst.setText("");
        }
        if (!" ".equals(fValueDecPart)) {
            this.mFValueViewSecond.setText(fValueDecPart);
        } else {
            this.mFValueViewSecond.setText("");
        }
        initializeEventListener();
    }

    private void initializeEventListener() {
        if (this.mOnButtonClickListener == null) {
            this.mOnButtonClickListener = new OnButtonClickListener();
        }
        this.mShadingLayout.setOnClickListener(this.mOnButtonClickListener);
        this.mChromaticLayout.setOnClickListener(this.mOnButtonClickListener);
        this.mDistortionLayout.setOnClickListener(this.mOnButtonClickListener);
        this.mShadingLayout.setOnFocusChangeListener(this);
        this.mChromaticLayout.setOnFocusChangeListener(this);
        this.mDistortionLayout.setOnFocusChangeListener(this);
        this.mLensNameView.setOnFocusChangeListener(this);
        this.mFocalLengthView.setOnFocusChangeListener(this);
        this.mFValueViewFirst.setOnFocusChangeListener(this);
        this.mFValueViewSecond.setOnFocusChangeListener(this);
        this.mSave.setOnFocusChangeListener(this);
        this.mSave.setOnClickListener(this.mOnButtonClickListener);
        initializeEditListener();
    }

    void handleParamFromGlobalMenu() {
        if (this.mPreviousMenuLayoutID.equalsIgnoreCase("ID_PAGEMENULAYOUT")) {
            this.mParam = OCUtil.getInstance().getLensParameterObject();
        }
    }

    private void setInitialFocus(int selection) {
        switch (selection) {
            case 3:
            case 4:
                this.mChromaticLayout.requestFocus();
                return;
            case 5:
                this.mDistortionLayout.requestFocus();
                return;
            case 6:
            case 7:
            case 8:
            case 9:
            default:
                this.mShadingLayout.requestFocus();
                return;
            case 10:
                this.mSave.requestFocus();
                return;
            case 11:
                this.mLensNameView.requestFocus();
                return;
            case 12:
                this.mFocalLengthView.requestFocus();
                return;
            case 13:
                this.mFValueViewFirst.requestFocus();
                return;
            case 14:
                this.mFValueViewSecond.requestFocus();
                return;
        }
    }

    private void setValueOnResume() {
        int mSBright;
        int mSRed;
        int mSBlue;
        this.mParam.applyCompensationParameter();
        OCController controller = OCController.getInstance();
        if (OCController.getInstance().isSupportPictureEffect()) {
            mSBright = controller.getLensCorrectionLevel(OCController.LIGHT_VIGNETTING);
            mSRed = controller.getLensCorrectionLevel(OCController.RED_COLOR_VIGNETTING);
            mSBlue = controller.getLensCorrectionLevel(OCController.BLUE_COLOR_VIGNETTING);
        } else {
            mSBright = this.mParam.getLevel(OCController.LIGHT_VIGNETTING);
            mSRed = this.mParam.getLevel(OCController.RED_COLOR_VIGNETTING);
            mSBlue = this.mParam.getLevel(OCController.BLUE_COLOR_VIGNETTING);
        }
        int mChromaRed = controller.getLensCorrectionLevel(OCController.RED_CHROMATIC_ABERRATION);
        int mChromaBlue = controller.getLensCorrectionLevel(OCController.BLUE_CHROMATIC_ABERRATION);
        int mDistWhite = controller.getLensCorrectionLevel(OCController.DISTORTION);
        AppLog.info(TAG, "mShadingBright" + mSBright + "..");
        this.mShadingWhite.setProgress(mSBright + 16);
        this.mShadingRed.setProgress(mSRed + 16);
        this.mShadingBlue.setProgress(mSBlue + 16);
        this.mChromaticRed.setProgress(mChromaRed + 16);
        this.mChromaticBlue.setProgress(mChromaBlue + 16);
        this.mDistortion.setProgress(mDistWhite + 16);
        this.mShadingWhitePE.setProgress(mSBright + 16);
        this.mShadingRedPE.setProgress(mSRed + 16);
        this.mShadingBluePE.setProgress(mSBlue + 16);
        Context context = getActivity().getApplicationContext();
        this.mService = new BaseMenuService(context);
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (this.mShadingLayout != null) {
            this.mShadingLayout.setOnClickListener(null);
            this.mChromaticLayout.setOnClickListener(null);
            this.mDistortionLayout.setOnClickListener(null);
            this.mShadingLayout.setOnFocusChangeListener(null);
            this.mChromaticLayout.setOnFocusChangeListener(null);
            this.mDistortionLayout.setOnFocusChangeListener(null);
        }
        if (this.mFValueViewFirst != null) {
            this.mFValueViewFirst.setText("");
            this.mFValueViewSecond.setText("");
            this.mLensNameView.setOnEditorActionListener(null);
            this.mFocalLengthView.setOnEditorActionListener(null);
            this.mFValueViewFirst.setOnEditorActionListener(null);
        }
        detachSoftKeyBoard();
        super.onPause();
    }

    private void getUpdatedTextInfo() {
        if (this.mParam != null) {
            String text = this.mLensNameView.getText().toString();
            this.mParam.mLensName = text;
            String text2 = this.mFocalLengthView.getText().toString();
            this.mParam.mFocalLength = text2;
            this.mParam.mFValue = convertUIFValueToDBvalue();
        }
    }

    private String convertUIFValueToDBvalue() {
        String fValueInt = this.mFValueViewFirst.getText().toString();
        String fValueDec = this.mFValueViewSecond.getText().toString();
        String fValue = OCConstants.BLANK_FVALUE_STR;
        try {
            boolean isIntegerBlank = fValueInt.isEmpty();
            boolean isDecimalBlank = fValueDec.isEmpty();
            if (isIntegerBlank && isDecimalBlank) {
                fValue = OCConstants.BLANK_FVALUE_STR;
            } else if (!isIntegerBlank && isDecimalBlank) {
                fValue = fValueInt.concat(".").concat(" ");
            } else if (isIntegerBlank && !isDecimalBlank) {
                fValue = " ".concat(".").concat(fValueDec);
            } else if (!isIntegerBlank && !isDecimalBlank) {
                fValue = fValueInt.concat(".").concat(fValueDec);
            }
        } catch (Exception exp) {
            AppLog.error(TAG, AppLog.getMethodName() + ",Excpetion Message: " + exp.getMessage());
        }
        return fValue;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        deInitializeView();
        super.onDestroyView();
    }

    private void deInitializeView() {
        this.mCurrentView = null;
        this.mLensNameView = null;
        this.mFocalLengthView = null;
        this.mFValueViewFirst = null;
        this.mFValueViewSecond = null;
        this.mShooting_Shading = null;
        this.mShooting_Shading_pe = null;
        this.mShadingLayout = null;
        this.mChromaticLayout = null;
        this.mDistortionLayout = null;
        this.mShadingWhite = null;
        this.mShadingRed = null;
        this.mShadingBlue = null;
        this.mChromaticRed = null;
        this.mChromaticBlue = null;
        this.mDistortion = null;
        this.mPlus_level1 = null;
        this.mMinus_level1 = null;
        this.mShadingWhitePE = null;
        this.mShadingRedPE = null;
        this.mShadingBluePE = null;
        this.mPlus_level1PE = null;
        this.mMinus_level1PE = null;
        this.mParam = null;
        this.mOnButtonClickListener = null;
        this.mEditTextInputListener = null;
        this.mScanCodeArrayList = null;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        this.mParam = this.mOldParam;
        this.mParam.applyCompensationParameter();
        backTransition();
        return 1;
    }

    private void backTransition() {
        BackUpUtil.getInstance().setPreference(OCBackUpKey.KEY_BKUP_GRIDLINE_STATE, Integer.valueOf(this.mCancelGridBackup));
        openPreviousMenu();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    public void closeMenuLayout(Bundle bundle) {
        AppLog.info(TAG, "method Name: " + AppLog.getMethodName());
        if (OCUtil.getInstance().checkIfMenuStateClosed(bundle)) {
            if (this.mParam != null) {
                getUpdatedTextInfo();
                OCUtil.getInstance().setLensCorrectionParameter(this.mParam);
            }
            if (!this.mPreviousMenuLayoutID.equalsIgnoreCase("ID_PAGEMENULAYOUT") && this.mParam != null) {
                BackUpUtil.getInstance().setPreference(OCBackUpKey.KEY_BKUP_PREV_RECORD_ID, Integer.valueOf(this.mParam.getId()));
                if (-1 != this.mSelectionPosition) {
                    BackUpUtil.getInstance().setPreference(OCBackUpKey.KEY_BKUP_SELECTED_POSITION, Integer.valueOf(this.mSelectionPosition));
                }
                AppNameView.setText(this.mParam.mLensName);
                AppNameView.show(true);
            }
        }
        OCUtil.getInstance().setCloseMenuLayoutExecuted(true);
        super.closeMenuLayout(bundle);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.base.menu.MenuStateClosedListener
    public void onStateClosed(Bundle bundle) {
        if (bundle == null) {
            OpticalCompensation.sIsFirstTimeLaunched = false;
            if (-1 != this.mSelectionPosition) {
                BackUpUtil.getInstance().setPreference(OCBackUpKey.KEY_BKUP_SELECTED_POSITION, Integer.valueOf(this.mSelectionPosition));
            }
        }
        super.onStateClosed(bundle);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        if (!OCUtil.getInstance().isCloseMenuLayoutExecuted() && this.mParam != null) {
            getUpdatedTextInfo();
            OCUtil.getInstance().setLensCorrectionParameter(this.mParam);
        }
        super.closeLayout();
        detachSoftKeyBoard();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        View v = focusSearch(17);
        if (v != null) {
            v.requestFocus();
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        View v = focusSearch(66);
        if (v != null) {
            v.requestFocus();
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        View v = focusSearch(33);
        if (v != null) {
            v.requestFocus();
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        View v = focusSearch(130);
        if (v != null) {
            v.requestFocus();
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int result = super.onConvertedKeyDown(event, func);
        CustomizableFunction customFunc = (CustomizableFunction) func;
        AppLog.info(TAG, "Checking KeyHandling:::::   onConvertedKeyDown(), func type:" + func.getType());
        switch (customFunc) {
            case MainNext:
                pushedDownKey();
                return 1;
            case MainPrev:
                pushedUpKey();
                return 1;
            case SubNext:
                pushedRightKey();
                return result;
            case SubPrev:
                pushedLeftKey();
                return result;
            case DoNothing:
                return -1;
            default:
                AppLog.info(TAG, "default section of onKeyDown(), event key code:" + event.getKeyCode());
                return result;
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedGuideFuncKey() {
        super.pushedGuideFuncKey();
        if (!isFunctionGuideShown()) {
            setInitialFocus(this.mFocusedItem);
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        if (this.mDistortionLayout.isFocused()) {
            guideResources.add(getText(R.string.STRID_FUNC_OPTICAL_COMPENSATION_DISTORTION_ABERRATION));
            guideResources.add(getText(R.string.STRID_FUNC_OPTICAL_COMPENSATION_DISTORTION_ABERRATION_GUIDE));
            guideResources.add(true);
            return;
        }
        if (this.mChromaticLayout.isFocused()) {
            guideResources.add(getText(R.string.STRID_FUNC_OPTICAL_COMPENSATION_CHROMATIC_ABERRATION));
            guideResources.add(getText(R.string.STRID_FUNC_OPTICAL_COMPENSATION_CHROMATIC_ABERRATION_GUIDE));
            guideResources.add(true);
            return;
        }
        if (this.mShadingLayout.isFocused()) {
            guideResources.add(getText(R.string.STRID_FUNC_OPTICAL_COMPENSATION_PERIPHERAL_SHADING));
            guideResources.add(getText(R.string.STRID_FUNC_OPTICAL_COMPENSATION_PERIPHERAL_SHADING_GUIDE));
            guideResources.add(true);
            return;
        }
        if (this.mLensNameView.isFocused()) {
            guideResources.add(getText(R.string.STRID_FUNC_AP_EDIT_BTN));
            guideResources.add(getText(R.string.STRID_FUNC_OPTICAL_COMPENSATION_INPUT_LENS_NAME_GUIDE));
            guideResources.add(true);
        } else if (this.mFocalLengthView.isFocused()) {
            guideResources.add(getText(R.string.STRID_FUNC_AP_EDIT_BTN));
            guideResources.add(getText(R.string.STRID_FUNC_OPTICAL_COMPENSATION_INPUT_FOCAL_LENGTH_GUIDE));
            guideResources.add(true);
        } else if (this.mFValueViewFirst.isFocused() || this.mFValueViewSecond.isFocused()) {
            guideResources.add(getText(R.string.STRID_FUNC_AP_EDIT_BTN));
            guideResources.add(getText(R.string.STRID_FUNC_OPTICAL_COMPENSATION_INPUT_FVALUE_GUIDE));
            guideResources.add(true);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int scanCode = event.getScanCode();
        if (scanCode == 0) {
            scanCode = event.getKeyCode();
        }
        if (isFunctionGuideShown()) {
            setInitialFocus(this.mFocusedItem);
        }
        switch (scanCode) {
            case 103:
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
                pushedUpKey();
                return 1;
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                pushedDownKey();
                return 1;
            case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
            case AppRoot.USER_KEYCODE.S2_ON /* 518 */:
                closeSoftKeyBoard(scanCode);
                return 1;
            case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
            case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                return -1;
            case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                int result = super.onKeyDown(keyCode, event);
                closeMenuLayout(null);
                return result;
            default:
                int result2 = super.onKeyDown(keyCode, event);
                return result2;
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        int scanCode = event.getScanCode();
        if (scanCode == 530 || scanCode == 786) {
            return -1;
        }
        int ret = super.onKeyUp(keyCode, event);
        return ret;
    }

    private void closeSoftKeyBoard(int scancode) {
        if (this.mScanCodeArrayList == null) {
            this.mScanCodeArrayList = new ArrayList<>();
            this.mScanCodeArrayList.add(Integer.valueOf(AppRoot.USER_KEYCODE.S1_ON));
            this.mScanCodeArrayList.add(517);
            this.mScanCodeArrayList.add(Integer.valueOf(AppRoot.USER_KEYCODE.S2_ON));
        }
        if (this.mScanCodeArrayList.contains(Integer.valueOf(scancode))) {
            detachSoftKeyBoard();
            closeMenuLayout(null);
        }
    }

    private void detachSoftKeyBoard() {
        if (this.isKeyBoardOpened) {
            InputMethodManager inputMethodManager = (InputMethodManager) AppContext.getAppContext().getSystemService("input_method");
            inputMethodManager.toggleSoftInput(1, 0);
            if (!isFunctionGuideShown()) {
                inputMethodManager.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    public void editParamters(int settingKind) {
        OCUtil.getInstance().setCloseMenuLayoutExecuted(true);
        openNextMenuLayout(this.mParam, settingKind);
    }

    private void openNextMenuLayout(LensCompensationParameter param, int settingKind) {
        String nextMenu = this.mService.getMenuItemNextMenuID(OCConstants.MENU_SETTING_KIND_ADJUST);
        if (this.data == null) {
            this.data = new Bundle();
        }
        getUpdatedTextInfo();
        this.data.putParcelable(LensCompensationParameter.ID_PARAMETER, param);
        if (settingKind != -1) {
            this.data.putInt(OCConstants.SETTING_KIND, settingKind);
        }
        openNextMenu(OCConstants.MENU_SETTING_KIND_ADJUST, nextMenu, true, this.data);
    }

    protected View focusSearch(int direction) {
        View root = getActivity().getCurrentFocus();
        if (root == null) {
            return null;
        }
        return root.focusSearch(direction);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class OnButtonClickListener implements View.OnClickListener {
        private OnButtonClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            LensEditMenuLayout.this.onActionPerformed(view);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onActionPerformed(View v) {
        switch (v.getId()) {
            case R.id.light /* 2131361999 */:
                if (OCController.getInstance().isSupportPictureEffect()) {
                    editParamters(0);
                    return;
                } else {
                    CautionUtilityClass.getInstance().requestTrigger(OCInfo.CAUTION_ID_DLAPP_PE_NOT_SUPPORTED);
                    return;
                }
            case R.id.FocalLength /* 2131362000 */:
            case R.id.FValueOne /* 2131362001 */:
            case R.id.FocalLengthLabel /* 2131362002 */:
            case R.id.FValueSecond /* 2131362003 */:
            case R.id.fValueDot /* 2131362004 */:
            default:
                AppLog.enter(TAG, AppLog.getMethodName());
                return;
            case R.id.color /* 2131362005 */:
                editParamters(3);
                return;
            case R.id.distortion /* 2131362006 */:
                editParamters(5);
                return;
            case R.id.saveImageButton /* 2131362007 */:
                closeMenuLayout(null);
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class EditTextInputListener implements TextView.OnEditorActionListener {
        EditTextInputListener() {
        }

        @Override // android.widget.TextView.OnEditorActionListener
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            if (actionId != 5) {
                return false;
            }
            TextView nextFocusedView = null;
            switch (view.getId()) {
                case R.id.LensName /* 2131361871 */:
                    nextFocusedView = LensEditMenuLayout.this.mFocalLengthView;
                    break;
                case R.id.FocalLength /* 2131362000 */:
                    nextFocusedView = LensEditMenuLayout.this.mFValueViewFirst;
                    break;
                case R.id.FValueOne /* 2131362001 */:
                    nextFocusedView = LensEditMenuLayout.this.mFValueViewSecond;
                    break;
            }
            if (nextFocusedView == null) {
                return false;
            }
            nextFocusedView.requestFocus();
            return true;
        }
    }

    @Override // android.view.View.OnFocusChangeListener
    public void onFocusChange(View view, boolean arg1) {
        switch (view.getId()) {
            case R.id.LensName /* 2131361871 */:
                this.mFocusedItem = 11;
                return;
            case R.id.light /* 2131361999 */:
                this.mFocusedItem = 0;
                return;
            case R.id.FocalLength /* 2131362000 */:
                this.mFocusedItem = 12;
                return;
            case R.id.FValueOne /* 2131362001 */:
                this.mFocusedItem = 13;
                return;
            case R.id.FValueSecond /* 2131362003 */:
                this.mFocusedItem = 14;
                return;
            case R.id.color /* 2131362005 */:
                this.mFocusedItem = 3;
                return;
            case R.id.distortion /* 2131362006 */:
                this.mFocusedItem = 5;
                return;
            case R.id.saveImageButton /* 2131362007 */:
                this.mFocusedItem = 10;
                return;
            default:
                return;
        }
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void updateView() {
        super.updateView();
        if (this.mUpdater != null) {
            this.mUpdater.finishMenuUpdater();
        }
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent event) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.trace(TAG, "Touch operation disabled on view which ID is= " + view.getId());
        AppLog.exit(TAG, AppLog.getMethodName());
        return true;
    }
}
