package com.sony.imaging.app.digitalfilter.menu.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.caution.GFInfo;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.common.GFImageAdjustmentUtil;
import com.sony.imaging.app.digitalfilter.common.GFTextHighlighter;
import com.sony.imaging.app.digitalfilter.sa.GFHistgramTask;
import com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFShadingLinkController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFThemeController;
import com.sony.imaging.app.digitalfilter.shooting.widget.BorderView;
import com.sony.imaging.app.digitalfilter.shooting.widget.GFGraphView;
import com.sony.imaging.app.digitalfilter.shooting.widget.UpdatingImage;
import com.sony.imaging.app.digitalfilter.shooting.widget.UpdatingText;
import com.sony.imaging.app.digitalfilter.shooting.widget.VerticalSeekBar;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class GFShadingMenuLayout extends DisplayMenuItemsMenuLayout implements NotificationListener {
    public static final String MENU_ID = "ID_GFSHADINGMENULAYOUT";
    private static final int SEEKBAR_VISIBILITY = 4;
    private static View mCurrentView = null;
    private static VerticalSeekBar mVerticalSeekBar = null;
    private static TextView mShadingLevel = null;
    private static TextView mTitle = null;
    private static int mLevel = 0;
    private static BorderView mBorderView = null;
    private static UpdatingImage mUpdatingImage = null;
    private static UpdatingText mUpdatingText = null;
    private static FooterGuide mFooterGuide = null;
    private static GFGraphView mGFGraphViewFilter = null;
    private static GFGraphView mGFGraphViewBase = null;
    private static boolean isHistgramView = false;
    private static boolean isLayerSetting = false;
    private static GFTextHighlighter mTextHighlighter = null;
    private static ImageView mUpArrow = null;
    private static ImageView mDownArrow = null;
    private static int mCompValue = 0;
    private static int mOtherBoundaryId = 0;
    private static int mOtherLevel = 0;
    public static boolean isShadingLink = false;
    private static boolean isCanceled = false;
    private static boolean isLayer3 = false;
    private final String TAG = AppLog.getClassName();
    NotificationListener mListener = new ChangeYUVNotifier();
    private String[] TAGS = {GFConstants.CHECKE_UPDATE_STATUS, GFConstants.TAG_GYROSCOPE, GFConstants.RESET_BOUNDARY_SETTING};

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (GFCommonUtil.getInstance().avoidLensDistortion()) {
            mCompValue = 9;
        }
        mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.menu_shading);
        mBorderView = (BorderView) mCurrentView.findViewById(R.id.shading_view);
        mGFGraphViewFilter = (GFGraphView) mCurrentView.findViewById(R.id.filter_histgram);
        mGFGraphViewBase = (GFGraphView) mCurrentView.findViewById(R.id.base_histgram);
        mUpArrow = (ImageView) mCurrentView.findViewById(R.id.up_arrow);
        mDownArrow = (ImageView) mCurrentView.findViewById(R.id.down_arrow);
        mVerticalSeekBar = (VerticalSeekBar) mCurrentView.findViewById(R.id.vertical_Seekbar);
        mVerticalSeekBar.setProgressDrawable(null);
        mLevel = GFCommonUtil.getInstance().getStrength();
        mTitle = (TextView) mCurrentView.findViewById(R.id.menu_screen_title);
        mVerticalSeekBar.setMax((GFEffectParameters.Parameters.mSAStrength.length - 1) - mCompValue);
        mVerticalSeekBar.setProgress(mLevel - mCompValue);
        mVerticalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.sony.imaging.app.digitalfilter.menu.layout.GFShadingMenuLayout.1
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar arg0) {
            }
        });
        mUpdatingImage = (UpdatingImage) mCurrentView.findViewById(R.id.updating);
        mUpdatingText = (UpdatingText) mCurrentView.findViewById(R.id.updating_text);
        mFooterGuide = (FooterGuide) mCurrentView.findViewById(R.id.footer_guide);
        isLayerSetting = GFCommonUtil.getInstance().isLayerSetting();
        if (isLayerSetting) {
            mFooterGuide.setData(new FooterGuideDataResId(getActivity(), R.string.STRID_FUNC_DF_GRADATION_FOOTER, R.string.STRID_FUNC_DF_GRADATION_FOOTER));
        } else {
            mFooterGuide.setData(new FooterGuideDataResId(getActivity(), R.string.STRID_FUNC_SKYND_GRADATION_DISABLEBAR_FOOTER, R.string.STRID_FUNC_SKYND_GRADATION_DISABLEBAR_FOOTER));
        }
        if (isLayerSetting || GFImageAdjustmentUtil.getInstance().isConformedToUISetting()) {
            mUpdatingImage.setVisibility(4);
            mUpdatingText.setVisibility(4);
            mFooterGuide.setVisibility(0);
        } else {
            mUpdatingImage.setVisibility(0);
            mUpdatingText.setVisibility(0);
            mFooterGuide.setVisibility(4);
        }
        isHistgramView = BackUpUtil.getInstance().getPreferenceBoolean(GFBackUpKey.KEY_HISTGRAM_VIEW, true);
        if (isLayerSetting) {
            GFHistgramTask.getInstance().startHistgramUpdating();
        }
        GFHistgramTask.getInstance().setEnableUpdating(isHistgramView);
        if (!isHistgramView || !isLayerSetting) {
            mGFGraphViewFilter.setVisibility(4);
            mGFGraphViewBase.setVisibility(4);
        } else {
            mGFGraphViewFilter.setVisibility(0);
            mGFGraphViewBase.setVisibility(0);
        }
        mVerticalSeekBar.setVisibility(4);
        mShadingLevel = (TextView) mCurrentView.findViewById(R.id.shading_level);
        isShadingLink = GFShadingLinkController.getInstance().isLink() && isLayerSetting;
        if (GFCommonUtil.getInstance().isLayer3()) {
            mOtherBoundaryId = 0;
        } else {
            mOtherBoundaryId = 1;
        }
        if (isShadingLink) {
            mOtherLevel = GFCommonUtil.getInstance().getStrength(mOtherBoundaryId);
        }
        return mCurrentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        isLayer3 = GFCommonUtil.getInstance().isLayer3();
        if (isLayerSetting) {
            if (!isLayer3) {
                mTitle.setText(R.string.STRID_FUNC_DF_GRADATION_SKY);
            } else {
                mTitle.setText(R.string.STRID_FUNC_SKYND_GRADATION_LAYER3);
            }
        } else if (GFCommonUtil.getInstance().getBorderId() == 0) {
            mTitle.setText(R.string.STRID_FUNC_DF_GRADATION_SKY);
        } else {
            mTitle.setText(R.string.STRID_FUNC_SKYND_GRADATION_LAYER3);
        }
        isCanceled = false;
        CameraNotificationManager.getInstance().setNotificationListener(this);
        CameraNotificationManager.getInstance().requestNotify(GFConstants.CHECKE_UPDATE_STATUS);
        DisplayModeObserver.getInstance().setNotificationListener(this.mListener);
        mTextHighlighter = new GFTextHighlighter(mShadingLevel, -2236963, -2267648);
        updateValue();
    }

    private void updateValue() {
        int level = mVerticalSeekBar.getProgress() + 1;
        if (level > 0) {
            mShadingLevel.setText(GFCommonUtil.getInstance().getSignedInteger(level));
        } else {
            mShadingLevel.setText("" + level);
        }
        if (level == 1) {
            mUpArrow.setVisibility(0);
            mDownArrow.setVisibility(4);
        } else if (level == mVerticalSeekBar.getMax() + 1) {
            mUpArrow.setVisibility(4);
            mDownArrow.setVisibility(0);
        } else {
            mUpArrow.setVisibility(0);
            mDownArrow.setVisibility(0);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected boolean isAELCancel() {
        return false;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        CameraNotificationManager.getInstance().removeNotificationListener(this.mListener);
        DisplayModeObserver.getInstance().removeNotificationListener(this.mListener);
        mTitle = null;
        mShadingLevel = null;
        mUpdatingImage = null;
        mUpdatingText = null;
        mFooterGuide = null;
        mBorderView = null;
        mUpArrow = null;
        mDownArrow = null;
        mVerticalSeekBar = null;
        mGFGraphViewFilter = null;
        mGFGraphViewBase = null;
        mTextHighlighter = null;
        mCurrentView = null;
        isShadingLink = false;
        BackUpUtil.getInstance().setPreference(GFBackUpKey.KEY_HISTGRAM_VIEW, Boolean.valueOf(isHistgramView));
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        System.gc();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        if (!isCanceled && isShadingLink && isLayer3) {
            CameraNotificationManager.getInstance().requestNotify(GFConstants.RELOAD_ALL_IMAGE);
        }
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        int guideTitleID = R.string.STRID_FUNC_DF_GRADATION_SKY;
        int guideDefi = R.string.STRID_FUNC_DF_SKY_SETTINGS_GRADATION;
        if (GFCommonUtil.getInstance().isLayer3()) {
            guideTitleID = R.string.STRID_FUNC_SKYND_GRADATION_LAYER3;
            guideDefi = R.string.STRID_FUNC_SKYND_LAYER3_SETTINGS_GRADATION;
        }
        guideResources.add(getText(guideTitleID));
        guideResources.add(getText(guideDefi));
        guideResources.add(true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    public void openPreviousMenu() {
        if (GFCommonUtil.getInstance().isAdjustmentSetting()) {
            closeLayout();
            openLayout("AdjustmentLayout");
        } else {
            super.openPreviousMenu();
        }
    }

    private void incrementShading() {
        int level = mVerticalSeekBar.moveUp() + mCompValue;
        GFCommonUtil.getInstance().setStrength(level);
        updateValue();
        if (isShadingLink) {
            int otherLevel = GFCommonUtil.getInstance().getStrength(mOtherBoundaryId);
            if (otherLevel < mVerticalSeekBar.getMax() + mCompValue) {
                otherLevel++;
            }
            GFCommonUtil.getInstance().setStrength(otherLevel, mOtherBoundaryId);
        }
        mTextHighlighter.startHighlight();
        CameraNotificationManager.getInstance().requestNotify(GFConstants.UPDATE_APPSETTING);
        mBorderView.invalidate();
    }

    private void decrementShading() {
        int level = mVerticalSeekBar.moveDown() + mCompValue;
        GFCommonUtil.getInstance().setStrength(level);
        updateValue();
        if (isShadingLink) {
            int otherLevel = GFCommonUtil.getInstance().getStrength(mOtherBoundaryId);
            if (otherLevel > mCompValue) {
                otherLevel--;
            }
            GFCommonUtil.getInstance().setStrength(otherLevel, mOtherBoundaryId);
        }
        mTextHighlighter.startHighlight();
        CameraNotificationManager.getInstance().requestNotify(GFConstants.UPDATE_APPSETTING);
        mBorderView.invalidate();
    }

    private void initBoundary() {
        GFEffectParameters.Parameters param = GFEffectParameters.getInstance().getParameters();
        int themeId = GFEffectParameters.Parameters.getEffect();
        param.setPoint(GFEffectParameters.Parameters.DEFAULT_CENTER.get(themeId));
        param.setDegree(GFEffectParameters.Parameters.DEFAULT_DEGREE[themeId]);
        param.setStrength(GFEffectParameters.Parameters.DEFAULT_STRENGTH[themeId]);
        param.setPoint2(GFEffectParameters.Parameters.DEFAULT_CENTER2.get(themeId));
        param.setDegree2(GFEffectParameters.Parameters.DEFAULT_DEGREE2[themeId]);
        param.setStrength2(GFEffectParameters.Parameters.DEFAULT_STRENGTH2[themeId]);
        mLevel = GFCommonUtil.getInstance().getStrength();
        String theme = GFThemeController.getInstance().getValue();
        GFBackUpKey.getInstance().resetDeviceDirection(theme);
        mVerticalSeekBar.setProgress(mLevel - mCompValue);
        updateValue();
        mBorderView.invalidate();
        CameraNotificationManager.getInstance().requestNotify(GFConstants.UPDATE_APPSETTING);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        if (isLayerSetting) {
            return super.attachedLens();
        }
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        if (isLayerSetting) {
            return super.detachedLens();
        }
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        if (isLayerSetting) {
            CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_INVALID_FUNCTION_IN_APPSETTING);
            return -1;
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUmRemoteRecKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        if (isLayerSetting) {
            return super.pushedS1Key();
        }
        closeLayout();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK1Key() {
        return pushedMenuKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        boolean isChanged = false;
        isCanceled = true;
        if (isShadingLink && mOtherLevel != GFCommonUtil.getInstance().getStrength(mOtherBoundaryId)) {
            GFCommonUtil.getInstance().setStrength(mOtherLevel, mOtherBoundaryId);
            isChanged = true;
        }
        if (GFCommonUtil.getInstance().getStrength() != mLevel) {
            GFCommonUtil.getInstance().setStrength(mLevel);
            isChanged = true;
        }
        if (isChanged) {
            CameraNotificationManager.getInstance().requestNotify(GFConstants.UPDATE_APPSETTING);
        }
        openPreviousMenu();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        if (isLayerSetting) {
            return super.pushedPlayBackKey();
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAFMFKey() {
        if (isLayerSetting) {
            return super.pushedAFMFKey();
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfHoldCustomKey() {
        if (isLayerSetting) {
            return super.pushedAfMfHoldCustomKey();
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfToggleCustomKey() {
        if (isLayerSetting) {
            return super.pushedAfMfToggleCustomKey();
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELHoldCustomKey() {
        if (isLayerSetting) {
            return super.pushedAELHoldCustomKey();
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELToggleCustomKey() {
        if (isLayerSetting) {
            return super.pushedAELToggleCustomKey();
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAfMfHoldCustomKey() {
        if (isLayerSetting) {
            return super.releasedAfMfHoldCustomKey();
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAELHoldCustomKey() {
        if (isLayerSetting) {
            return super.releasedAELHoldCustomKey();
        }
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int movedAelFocusSlideKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int ret;
        if (!isLayerSetting) {
            if (!CustomizableFunction.Unchanged.equals(func)) {
                ret = -1;
            } else {
                ret = super.onConvertedKeyDown(event, func);
            }
        } else {
            int code = event.getScanCode();
            if (GFConstants.SettingLayerCustomizableFunction.contains(func)) {
                ret = super.onConvertedKeyDown(event, func);
            } else {
                if (GFCommonUtil.getInstance().isTriggerMfAssist(code)) {
                    return 0;
                }
                ret = -1;
            }
        }
        return ret;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyUp(KeyEvent event, IKeyFunction func) {
        if (CustomizableFunction.Unchanged.equals(func)) {
            return super.onConvertedKeyUp(event, func);
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        switch (code) {
            case 103:
            case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
            case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                if (!isFunctionGuideShown()) {
                    incrementShading();
                }
                return 1;
            case AppRoot.USER_KEYCODE.LEFT /* 105 */:
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
            case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
                if (!isFunctionGuideShown()) {
                    decrementShading();
                }
                return 1;
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                if (!isFunctionGuideShown()) {
                    openPreviousMenu();
                    return 1;
                }
                int result = super.onKeyDown(keyCode, event);
                return result;
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                if (!isFunctionGuideShown() && isLayerSetting) {
                    CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_BOUNDARY_INIT_MSG);
                    return 1;
                }
                int result2 = super.onKeyDown(keyCode, event);
                return result2;
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_TELE /* 610 */:
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_WIDE /* 611 */:
            case 645:
                return -1;
            case AppRoot.USER_KEYCODE.LENS_FOCUS_HOLD /* 653 */:
                return -1;
            default:
                int result3 = super.onKeyDown(keyCode, event);
                return result3;
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        if (code == 653) {
            return -1;
        }
        if (code == 232 || code == 595) {
            return 1;
        }
        return super.onKeyUp(keyCode, event);
    }

    /* loaded from: classes.dex */
    static class ChangeYUVNotifier implements NotificationListener {
        private static final String[] tags = {DisplayModeObserver.TAG_DEVICE_CHANGE, DisplayModeObserver.TAG_YUVLAYOUT_CHANGE, DisplayModeObserver.TAG_VIEW_PATTERN_CHANGE, DisplayModeObserver.TAG_DISPMODE_CHANGE, "com.sony.imaging.app.base.common.DisplayModeObserver.OledScreenChange"};

        ChangeYUVNotifier() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            GFShadingMenuLayout.mBorderView.invalidate();
        }
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return this.TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (tag.equals(GFConstants.CHECKE_UPDATE_STATUS)) {
            if (GFImageAdjustmentUtil.getInstance().isConformedToUISetting()) {
                mFooterGuide.setVisibility(0);
                return;
            } else {
                mFooterGuide.setVisibility(4);
                return;
            }
        }
        if (tag.equals(GFConstants.TAG_GYROSCOPE)) {
            mBorderView.invalidate();
        } else if (tag.equals(GFConstants.RESET_BOUNDARY_SETTING)) {
            initBoundary();
        }
    }
}
