package com.sony.imaging.app.manuallenscompensation.menu.layout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.MenuDataParcelable;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
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
import com.sony.imaging.app.manuallenscompensation.widget.OCGridLine;
import com.sony.imaging.app.manuallenscompensation.widget.VerticalSeekBar;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

@SuppressLint({"DefaultLocale"})
/* loaded from: classes.dex */
public class LensAdjustMenuLayout extends DisplayMenuItemsMenuLayout {
    private static final String BACK = "back";
    private static final String STR_FORMAT_INTMINUS = "－%1d";
    private static final String STR_FORMAT_INTPLUS = "＋%1d";
    private static final String TAG = "LensAdjustMenuLayout";
    protected View mCurrentView;
    DisplayTitleTextRunnable mDispTitleRun;
    private ImageView mGaugeBackground;
    private TextView mGaugetext;
    protected OCGridLine mGridLine;
    private int mLevelOfZero;
    private ImageView mMinus;
    private LensCompensationParameter mOldParam;
    private LensCompensationParameter mParam;
    private ImageView mPlus;
    private TextView mScreenTitle = null;
    private ImageView mOpLeftFocus = null;
    private ImageView mOpCenterFocus = null;
    private ImageView mOpRightFocus = null;
    private ImageView mOpLeftIcon = null;
    private ImageView mOpCenterIcon = null;
    private Handler mTitleHandler = null;
    private ImageView mOpRightIcon = null;
    private RelativeLayout mRLayout = null;
    private RelativeLayout mBaseLayout = null;
    private int mTargetSetting = 0;
    private int mPreTargetSetting = 0;
    private int mCancelGridBackup = 0;
    private final int FIRST = 0;
    private final int SECOND = 1;
    private final int THIRD = 2;
    private final int CODINATE66 = 66;
    private final int CODINATE196 = 192;
    private final int CODINATE96 = 96;
    private final int CODINATE176 = 176;
    private final int CODINATE288 = 288;
    private final int CODINATE272 = 272;
    private final int CODINATE360 = 360;
    private final int CODINATE224 = 224;
    int[] mCompensationLayout = null;
    private VerticalSeekBar verticalSeekBar = null;
    int mSelectionPosition = -1;
    private FooterGuide mFooterGuide = null;
    private int mOptionFocusPos = 0;
    private List<String> mTitles = new ArrayList();
    OCController controller = OCController.getInstance();
    private int mLevel = 0;
    private Handler mHandler = new Handler();

    private static String titleText(int stringID) {
        String titleText = AppContext.getAppContext().getResources().getString(stringID);
        return titleText;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (this.data != null) {
            this.mTargetSetting = this.data.getInt(OCConstants.SETTING_KIND, 0);
            this.mPreTargetSetting = this.data.getInt(OCConstants.SETTING_KIND, 0);
        }
        fillTitleList();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        boolean ovfMode = OCUtil.getInstance().isDiademOVfMode();
        OCUtil.getInstance().setCameraPreviewMode("iris_ss_iso_aeunlock", ovfMode);
        this.mCurrentView = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        this.mCurrentView = obtainViewFromPool(getLayoutID(this.mTargetSetting));
        Context context = getActivity().getApplicationContext();
        this.mService = new BaseMenuService(context);
        return this.mCurrentView;
    }

    private int getLayoutID(int effectType) {
        if (this.mCompensationLayout == null) {
            this.mCompensationLayout = new int[]{R.layout.menu_lens_adjustment_shading_brighness, R.layout.menu_lens_adjustment_shading_red, R.layout.menu_lens_adjustment_shading_blue, R.layout.menu_lens_adjustment_chromatic_red, R.layout.menu_lens_adjustment_chromatic_blue, R.layout.menu_lens_adjustment_distortion};
        }
        return this.mCompensationLayout[effectType];
    }

    private void createView() {
        this.mGaugeBackground = (ImageView) this.mCurrentView.findViewById(R.id.vsb_src);
        this.mPlus = (ImageView) this.mCurrentView.findViewById(R.id.vsb_minus);
        this.mMinus = (ImageView) this.mCurrentView.findViewById(R.id.vsb_plus);
        this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide);
        this.mFooterGuide.setBackgroundResource(17304085);
        this.mGridLine = (OCGridLine) this.mCurrentView.findViewById(R.id.mlcgridview);
        Context context = getActivity().getApplicationContext();
        this.mService = new BaseMenuService(context);
        this.mRLayout = (RelativeLayout) this.mCurrentView.findViewById(R.id.mRLayout);
        this.mBaseLayout = (RelativeLayout) this.mCurrentView.findViewById(R.id.iconBase);
        this.mScreenTitle = (TextView) this.mCurrentView.findViewById(R.id.menu_screen_title);
        setTitleInVisibility(0);
        this.mOpLeftIcon = (ImageView) this.mCurrentView.findViewById(R.id.optionLeftImage);
        this.mOpLeftFocus = (ImageView) this.mCurrentView.findViewById(R.id.optionLeft);
        this.mOpCenterIcon = (ImageView) this.mCurrentView.findViewById(R.id.optionCenterImage);
        this.mOpCenterFocus = (ImageView) this.mCurrentView.findViewById(R.id.optionCenter);
        this.mOpRightIcon = (ImageView) this.mCurrentView.findViewById(R.id.optionRightImage);
        this.mOpRightFocus = (ImageView) this.mCurrentView.findViewById(R.id.optionRight);
        initializeGaugeRange();
        initializeTitleDisplayTimer();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        MenuDataParcelable parcelable;
        createView();
        super.onResume();
        setVerticalSeekBar();
        OCUtil.getInstance().setCloseMenuLayoutExecuted(false);
        if (this.data != null && (parcelable = (MenuDataParcelable) this.data.getParcelable(MenuDataParcelable.KEY)) != null && !BACK.equals(parcelable.getItemId())) {
            this.mParam = (LensCompensationParameter) this.data.getParcelable(LensCompensationParameter.ID_PARAMETER);
            this.mCancelGridBackup = BackUpUtil.getInstance().getPreferenceInt(OCBackUpKey.KEY_BKUP_GRIDLINE_STATE, this.mGridLine.getVisibility());
            this.mSelectionPosition = BackUpUtil.getInstance().getPreferenceInt(OCBackUpKey.KEY_BKUP_SELECTED_POSITION, -1);
            this.mOldParam = LensCompensationParameter.createCopiedObject(this.mParam);
        }
        selectedEffect();
        setFocus();
        isGridLineArea();
        disappeareTitilePostDelayed();
    }

    private void setVerticalSeekBar() {
        int compensation = getCompensationLevel();
        this.verticalSeekBar = (VerticalSeekBar) this.mCurrentView.findViewById(R.id.vertical_Seekbar);
        this.verticalSeekBar.setProgressDrawable(null);
        this.mGaugetext = (TextView) this.mCurrentView.findViewById(R.id.vertical_sb_progresstext);
        this.verticalSeekBar.setMax(32);
        this.verticalSeekBar.setProgress(compensation);
        getInformationText(compensation);
        this.verticalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.sony.imaging.app.manuallenscompensation.menu.layout.LensAdjustMenuLayout.1
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                LensAdjustMenuLayout.this.getInformationText(progress);
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar arg0) {
            }
        });
    }

    private void selectedEffect() {
        switch (this.mTargetSetting) {
            case 3:
            case 4:
                visibilityOFView(8, 0, 0);
                iconPositionHandling(3);
                if (this.mFooterGuide != null) {
                    this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), android.R.string.zen_mode_feature_name, android.R.string.httpErrorRedirectLoop));
                    return;
                }
                return;
            case 5:
                visibilityOFView(8, 0, 8);
                iconPositionHandling(1);
                if (this.mFooterGuide != null) {
                    this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), R.string.STRID_FOOTERGUIDE_GRID_RETURN_BY_MENU, R.string.STRID_FOOTERGUIDE_GRID_RETURN_FOR_SK));
                    return;
                }
                return;
            default:
                visibilityOFView(0, 0, 0);
                iconPositionHandling(2);
                if (this.mFooterGuide != null) {
                    this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), android.R.string.zen_mode_feature_name, android.R.string.httpErrorRedirectLoop));
                    return;
                }
                return;
        }
    }

    private void iconPositionHandling(int items) {
        RelativeLayout.LayoutParams centericon;
        RelativeLayout.LayoutParams righticon;
        RelativeLayout.LayoutParams baseicon;
        RelativeLayout.LayoutParams adaptLayout = new RelativeLayout.LayoutParams(288, 66);
        this.mRLayout.setVisibility(0);
        this.mBaseLayout.setVisibility(0);
        switch (items) {
            case 1:
                centericon = new RelativeLayout.LayoutParams(96, 66);
                centericon.setMargins(0, 0, 0, 0);
                righticon = new RelativeLayout.LayoutParams(96, 66);
                righticon.setMargins(96, 0, 0, 0);
                baseicon = new RelativeLayout.LayoutParams(96, 66);
                baseicon.setMargins(272, 360, 0, 0);
                adaptLayout.setMargins(272, 360, 0, 0);
                break;
            case 2:
                RelativeLayout.LayoutParams lefticon = new RelativeLayout.LayoutParams(96, 66);
                lefticon.setMargins(176, 360, 0, 0);
                centericon = new RelativeLayout.LayoutParams(96, 66);
                centericon.setMargins(0, 0, 0, 0);
                righticon = new RelativeLayout.LayoutParams(96, 66);
                righticon.setMargins(96, 0, 0, 0);
                baseicon = new RelativeLayout.LayoutParams(288, 66);
                baseicon.setMargins(176, 360, 0, 0);
                adaptLayout.setMargins(272, 360, 0, 0);
                this.mOpLeftIcon.setLayoutParams(lefticon);
                this.mOpLeftFocus.setLayoutParams(lefticon);
                break;
            default:
                centericon = new RelativeLayout.LayoutParams(96, 66);
                centericon.setMargins(0, 0, 0, 0);
                righticon = new RelativeLayout.LayoutParams(96, 66);
                righticon.setMargins(96, 0, 0, 0);
                baseicon = new RelativeLayout.LayoutParams(192, 66);
                baseicon.setMargins(224, 360, 0, 0);
                adaptLayout.setMargins(224, 360, 0, 0);
                break;
        }
        this.mOpCenterIcon.setLayoutParams(centericon);
        this.mOpCenterFocus.setLayoutParams(centericon);
        this.mOpRightIcon.setLayoutParams(righticon);
        this.mOpRightFocus.setLayoutParams(righticon);
        this.mRLayout.setLayoutParams(adaptLayout);
        this.mBaseLayout.setLayoutParams(baseicon);
    }

    private Boolean isGridLineArea() {
        if (this.mTargetSetting == 5) {
            int preViewState = BackUpUtil.getInstance().getPreferenceInt(OCBackUpKey.KEY_BKUP_GRIDLINE_STATE, 0);
            this.mGridLine.setVisibility(preViewState);
            return true;
        }
        if (this.mTargetSetting < 3 && !OCController.getInstance().isSupportPictureEffect()) {
            this.mGridLine.setVisibility(8);
            return false;
        }
        this.mGridLine.setVisibility(8);
        return false;
    }

    private void getCompensastionView(boolean status) {
        if (true == status) {
            this.mGaugeBackground.setVisibility(0);
            this.mMinus.setVisibility(0);
            this.mPlus.setVisibility(0);
            this.verticalSeekBar.setVisibility(0);
            this.mGaugetext.setVisibility(0);
            return;
        }
        this.mGaugeBackground.setVisibility(4);
        this.mMinus.setVisibility(4);
        this.mPlus.setVisibility(4);
        this.verticalSeekBar.setVisibility(4);
        this.mGaugetext.setVisibility(4);
    }

    private void setShaddingProperty() {
        boolean pictureEffectSupport = OCController.getInstance().isSupportPictureEffect();
        if (pictureEffectSupport) {
            this.mOpLeftIcon.setBackgroundResource(R.drawable.p_16_dd_parts_oc_adjust_shading_brightness_button);
            this.mOpCenterIcon.setBackgroundResource(R.drawable.p_16_dd_parts_oc_adjust_shading_red_button);
            this.mOpRightIcon.setBackgroundResource(R.drawable.p_16_dd_parts_oc_adjust_shading_blue_button);
        } else {
            this.mOpLeftIcon.setBackgroundResource(R.drawable.p_16_dd_parts_oc_adjust_shading_brightness_dark_button);
            this.mOpCenterIcon.setBackgroundResource(R.drawable.p_16_dd_parts_oc_adjust_shading_red_dark_button);
            this.mOpRightIcon.setBackgroundResource(R.drawable.p_16_dd_parts_oc_adjust_shading_blue_dark_button);
        }
        getCompensastionView(pictureEffectSupport);
    }

    private void visibilityOFView(int settingColor, int settingLight, int settingDistortion) {
        this.mOpLeftIcon.setVisibility(settingColor);
        this.mOpLeftFocus.setVisibility(settingColor);
        this.mOpCenterIcon.setVisibility(settingLight);
        this.mOpCenterFocus.setVisibility(settingLight);
        this.mOpRightIcon.setVisibility(settingDistortion);
        this.mOpRightFocus.setVisibility(settingDistortion);
        if (settingColor == 0 && settingLight == 0 && settingDistortion == 0) {
            setShaddingProperty();
            return;
        }
        if (settingLight == 0 && settingDistortion == 0) {
            this.mOpCenterIcon.setBackgroundResource(R.drawable.p_16_dd_parts_oc_adjust_chromatic_red_button);
            this.mOpRightIcon.setBackgroundResource(R.drawable.p_16_dd_parts_oc_adjust_chromatic_blue_button);
            getCompensastionView(true);
        } else {
            this.mOpCenterIcon.setBackgroundResource(R.drawable.p_16_dd_parts_oc_adjust_distortion_button);
            getCompensastionView(true);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteKey() {
        if (this.mTargetSetting < 3 && !OCController.getInstance().isSupportPictureEffect()) {
            return -1;
        }
        if (this.mTargetSetting != 5) {
            return 0;
        }
        if (this.mGridLine.getVisibility() == 0) {
            this.mGridLine.setVisibility(8);
        } else {
            this.mGridLine.setVisibility(0);
        }
        BackUpUtil.getInstance().setPreference(OCBackUpKey.KEY_BKUP_GRIDLINE_STATE, Integer.valueOf(this.mGridLine.getVisibility()));
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        deinitializeView();
        this.mOldParam = null;
        super.onDestroyView();
    }

    private void deinitializeView() {
        this.mScreenTitle = null;
        this.mGridLine = null;
        this.mOpLeftFocus = null;
        this.mOpCenterFocus = null;
        this.mOpRightFocus = null;
        this.mOpLeftIcon = null;
        this.mOpCenterIcon = null;
        this.mOpRightIcon = null;
        this.mRLayout = null;
        this.mBaseLayout = null;
        this.mCurrentView = null;
        if (this.mTitleHandler != null && this.mDispTitleRun != null) {
            this.mTitleHandler.removeCallbacks(this.mDispTitleRun);
        }
        this.mTitleHandler = null;
    }

    protected int initializeGaugeRange() {
        OCController controller = OCController.getInstance();
        String tag = getTag();
        List<String> list = controller.getSupportedValue(tag);
        if (list == null) {
            return 0;
        }
        this.mLevelOfZero = list.size() / 2;
        return list.size();
    }

    protected int getCompensationLevel() {
        OCController controller = OCController.getInstance();
        String tag = getTag();
        int value = controller.getLensCorrectionLevel(tag);
        int min = controller.getMinLensCorrectionLevel(tag);
        int value2 = value - min;
        AppLog.info(TAG, "getCompensationLevel : tag : " + tag + " level : " + value2);
        return value2;
    }

    protected void setCompensationLevel(int level) {
        this.controller.setLensCorrectionLevel(getTag(), level);
    }

    @SuppressLint({"DefaultLocale"})
    protected String getInformationText(int level) {
        String ret;
        String tag = getTag();
        this.mLevel = level;
        int value = level + this.controller.getMinLensCorrectionLevel(tag);
        Log.i(TAG, "::value::" + value + " =  level.." + level + "..+..controller.getMinLensCorrectionLevel(tag).." + this.controller.getMinLensCorrectionLevel(tag));
        this.controller.setLensCorrectionLevel(tag, value);
        if (level == this.mLevelOfZero) {
            ret = getResources().getString(17041844);
        } else if (value > 0) {
            ret = String.format(STR_FORMAT_INTPLUS, Integer.valueOf(value));
        } else {
            ret = String.format(STR_FORMAT_INTMINUS, Integer.valueOf(value * (-1)));
        }
        this.mGaugetext.setText(ret);
        return ret;
    }

    private String getTag() {
        return OCConstants.SETTINGSSUPPORTED.get(this.mTargetSetting);
    }

    private void fillTitleList() {
        if (this.mTitles == null) {
            this.mTitles = new ArrayList();
        } else {
            this.mTitles.clear();
        }
        this.mTitles.add(titleText(R.string.STRID_FUNC_OPTICAL_COMPENSATION_PERIPHERAL_SHADING));
        this.mTitles.add(titleText(R.string.STRID_FUNC_OPTICAL_COMPENSATION_PERIPHERAL_SHADING));
        this.mTitles.add(titleText(R.string.STRID_FUNC_OPTICAL_COMPENSATION_PERIPHERAL_SHADING));
        this.mTitles.add(titleText(R.string.STRID_FUNC_OPTICAL_COMPENSATION_CHROMATIC_ABERRATION));
        this.mTitles.add(titleText(R.string.STRID_FUNC_OPTICAL_COMPENSATION_CHROMATIC_ABERRATION));
        this.mTitles.add(titleText(R.string.STRID_FUNC_OPTICAL_COMPENSATION_DISTORTION_ABERRATION));
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        BackUpUtil.getInstance().setPreference(OCBackUpKey.KEY_BKUP_GRIDLINE_STATE, Integer.valueOf(this.mCancelGridBackup));
        AppLog.info("", "pushedSK1Key : ........mTargetSetting : " + this.mTargetSetting);
        this.mParam = this.mOldParam;
        backToMenu(this.mPreTargetSetting);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        if (this.mTargetSetting < 3 && !OCController.getInstance().isSupportPictureEffect()) {
            CautionUtilityClass.getInstance().requestTrigger(OCInfo.CAUTION_ID_DLAPP_PE_NOT_SUPPORTED);
        } else {
            OCUtil.getInstance().updateParamObjectWithChangedParameters(this.mParam);
            backToMenu(this.mTargetSetting);
        }
        return super.pushedCenterKey();
    }

    private void backToMenu(int focus) {
        if (this.mParam != null) {
            AppLog.info(TAG, AppLog.getMethodName());
            OCUtil.getInstance().printLensParametersLogs(this.mParam);
        }
        this.data.putInt(OCConstants.SETTING_KIND, focus);
        this.data.putParcelable(LensCompensationParameter.ID_PARAMETER, this.mParam);
        openPreviousMenu();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    public void closeMenuLayout(Bundle bundle) {
        if (OCUtil.getInstance().checkIfMenuStateClosed(bundle)) {
            OCUtil.getInstance().setLensCorrectionParameter(this.mParam);
        }
        OCUtil.getInstance().setCloseMenuLayoutExecuted(true);
        super.closeMenuLayout(bundle);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        if (!OCUtil.getInstance().isCloseMenuLayoutExecuted()) {
            OCUtil.getInstance().setLensCorrectionParameter(this.mParam);
        }
        super.closeLayout();
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

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        if (this.mTargetSetting >= 3 || OCController.getInstance().isSupportPictureEffect()) {
            return super.pushedUpKey();
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        Log.i(TAG, "pushedDownKey");
        if (this.mTargetSetting >= 3 || OCController.getInstance().isSupportPictureEffect()) {
            return super.pushedDownKey();
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        Log.i(TAG, "pushedRightKey");
        updateView();
        rollTarget(1);
        return 1;
    }

    private void setFocus() {
        switch (this.mTargetSetting) {
            case 0:
                this.mOptionFocusPos = 0;
                break;
            case 1:
            case 3:
            default:
                this.mOptionFocusPos = 1;
                break;
            case 2:
            case 4:
                this.mOptionFocusPos = 2;
                break;
        }
        updateOptionValue();
    }

    private void updateOptionValue() {
        this.mOpLeftFocus.setBackgroundResource(0);
        this.mOpCenterFocus.setBackgroundResource(0);
        this.mOpRightFocus.setBackgroundResource(0);
        switch (this.mOptionFocusPos) {
            case 0:
                this.mOpLeftFocus.setBackgroundResource(17306030);
                return;
            case 1:
                this.mOpCenterFocus.setBackgroundResource(17306030);
                return;
            case 2:
                this.mOpRightFocus.setBackgroundResource(17306030);
                return;
            default:
                return;
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        Log.i(TAG, "pushedLeftKey");
        updateView();
        rollTarget(-1);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void updateView() {
        super.updateView();
        if (this.mUpdater != null) {
            this.mUpdater.finishMenuUpdater();
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        this.mCurrentView = null;
        this.mGridLine = null;
        this.mCompensationLayout = null;
        this.mTitles.clear();
        this.mTitles = null;
        super.onDestroy();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class DisplayTitleTextRunnable implements Runnable {
        private DisplayTitleTextRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            LensAdjustMenuLayout.this.setTitleInVisibility(4);
        }
    }

    private void initializeTitleDisplayTimer() {
        if (this.mTitleHandler == null) {
            this.mTitleHandler = new Handler();
        }
        if (this.mDispTitleRun == null) {
            this.mDispTitleRun = new DisplayTitleTextRunnable();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTitleInVisibility(int visibility) {
        this.mScreenTitle.setVisibility(visibility);
        if (visibility == 4) {
            this.mTitleHandler.removeCallbacks(this.mDispTitleRun);
        }
    }

    private void disappeareTitilePostDelayed() {
        this.mTitleHandler.postDelayed(this.mDispTitleRun, 2000L);
    }

    private void rollTarget(int diff) {
        int size = OCConstants.SETTINGSSUPPORTED.size();
        this.mTargetSetting += diff;
        this.mTargetSetting += size;
        this.mTargetSetting %= size;
        AppLog.info("", "roll target size : " + size + "........mTargetSetting : " + this.mTargetSetting);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int result = super.onConvertedKeyDown(event, func);
        CustomizableFunction customFunc = (CustomizableFunction) func;
        AppLog.info(TAG, "Checking KeyHandling:::::   onConvertedKeyDown(), func type:" + func.getType());
        switch (customFunc) {
            case MainNext:
                this.verticalSeekBar.moveDown();
                return 1;
            case MainPrev:
                this.verticalSeekBar.moveUp();
                return 1;
            case SubNext:
                pushedRightKey();
                return result;
            case SubPrev:
                pushedLeftKey();
                return result;
            case Guide:
            case DoNothing:
                return -1;
            default:
                AppLog.info(TAG, "default section of onKeyDown(), event key code:" + event.getKeyCode());
                return result;
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int result;
        int scanCode = event.getScanCode();
        if (scanCode == 0) {
            scanCode = event.getKeyCode();
        }
        switch (scanCode) {
            case 103:
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
                this.verticalSeekBar.moveUp();
                result = 1;
                break;
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
                this.verticalSeekBar.moveDown();
                result = 1;
                break;
            case AppRoot.USER_KEYCODE.SK1 /* 229 */:
            case AppRoot.USER_KEYCODE.MENU /* 514 */:
                pushedMenuKey();
                result = 1;
                break;
            case AppRoot.USER_KEYCODE.SK2 /* 513 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                pushedDeleteKey();
                result = 1;
                break;
            case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
            case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                result = -1;
                break;
            default:
                result = super.onKeyDown(keyCode, event);
                break;
        }
        Log.i("onKeyDown", "LensAdjustMenuLayout....." + keyCode + ".." + result + "....");
        return result;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected DisplayMenuItemsMenuLayout.MenuUpdater getMenuUpdater() {
        return new DisplayMenuItemsMenuLayout.MenuUpdater(this.mHandler) { // from class: com.sony.imaging.app.manuallenscompensation.menu.layout.LensAdjustMenuLayout.2
            @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout.MenuUpdater, java.lang.Runnable
            public void run() {
                if (!this.isDone) {
                    this.isDone = true;
                    LensAdjustMenuLayout.this.setCompensationLevel(LensAdjustMenuLayout.this.mLevel);
                }
            }
        };
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        int ret;
        AppLog.enter(TAG, AppLog.getMethodName());
        int scanCode = event.getScanCode();
        if (scanCode == 530 || scanCode == 786) {
            AppLog.trace(TAG, AppLog.getMethodName() + scanCode);
            ret = -1;
        } else if (scanCode == 595 || scanCode == 513) {
            AppLog.trace(TAG, AppLog.getMethodName() + scanCode);
            ret = -1;
        } else {
            AppLog.trace(TAG, AppLog.getMethodName() + scanCode);
            ret = super.onKeyUp(keyCode, event);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return ret;
    }
}
