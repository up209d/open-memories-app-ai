package com.sony.imaging.app.graduatedfilter.menu.layout;

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
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.graduatedfilter.GFBackUpKey;
import com.sony.imaging.app.graduatedfilter.R;
import com.sony.imaging.app.graduatedfilter.caution.GFInfo;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.graduatedfilter.common.GFCommonUtil;
import com.sony.imaging.app.graduatedfilter.common.GFConstants;
import com.sony.imaging.app.graduatedfilter.common.GFImageAdjustmentUtil;
import com.sony.imaging.app.graduatedfilter.common.GFTextHighlighter;
import com.sony.imaging.app.graduatedfilter.sa.GFHistgramTask;
import com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.graduatedfilter.shooting.widget.BorderView;
import com.sony.imaging.app.graduatedfilter.shooting.widget.GFGraphView;
import com.sony.imaging.app.graduatedfilter.shooting.widget.UpdatingImage;
import com.sony.imaging.app.graduatedfilter.shooting.widget.UpdatingText;
import com.sony.imaging.app.graduatedfilter.shooting.widget.VerticalSeekBar;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class GFShadingMenuLayout extends DisplayMenuItemsMenuLayout implements NotificationListener {
    public static final String MENU_ID = "ID_GFSHADINGMENULAYOUT";
    private static final int SEEKBAR_VISIBILITY = 4;
    private static View mCurrentView = null;
    private static VerticalSeekBar mVerticalSeekBar = null;
    private static GFEffectParameters.Parameters mParams = null;
    private static int mLevel = 0;
    private static BorderView mBorderView = null;
    private static UpdatingImage mUpdatingImage = null;
    private static UpdatingText mUpdatingText = null;
    private static FooterGuide mFooterGuide = null;
    private static GFGraphView mGFGraphViewFilter = null;
    private static GFGraphView mGFGraphViewBase = null;
    private static boolean isHistgramView = false;
    private static boolean isFilterSetting = false;
    private static GFTextHighlighter mTextHighlighter = null;
    private static ImageView mUpArrow = null;
    private static ImageView mDownArrow = null;
    private static ImageView mVsbPlus = null;
    private static ImageView mVsbMinus = null;
    private static ImageView mVsbSrc = null;
    private static int mCompValue = 0;
    private final String TAG = AppLog.getClassName();
    private TextView mGaugeText = null;
    private TextView mShadingLevel = null;
    NotificationListener mListener = new ChangeYUVNotifier();
    private String[] TAGS = {GFConstants.CHECKE_UPDATE_STATUS};

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
        mVsbPlus = (ImageView) mCurrentView.findViewById(R.id.vsb_plus);
        mVsbMinus = (ImageView) mCurrentView.findViewById(R.id.vsb_minus);
        mVsbSrc = (ImageView) mCurrentView.findViewById(R.id.vsb_src);
        mVerticalSeekBar = (VerticalSeekBar) mCurrentView.findViewById(R.id.vertical_Seekbar);
        mVerticalSeekBar.setProgressDrawable(null);
        mParams = GFEffectParameters.getInstance().getParameters();
        mLevel = mParams.getStrength();
        mVerticalSeekBar.setMax((GFEffectParameters.Parameters.mSAStrength.length - 1) - mCompValue);
        mVerticalSeekBar.setProgress(mLevel - mCompValue);
        mVerticalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.sony.imaging.app.graduatedfilter.menu.layout.GFShadingMenuLayout.1
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
        this.mGaugeText = (TextView) mCurrentView.findViewById(R.id.seek_val);
        this.mGaugeText.setText("" + GFEffectParameters.Parameters.mSAStrength[mVerticalSeekBar.getProgress()]);
        mUpdatingImage = (UpdatingImage) mCurrentView.findViewById(R.id.updating);
        mUpdatingText = (UpdatingText) mCurrentView.findViewById(R.id.updating_text);
        mFooterGuide = (FooterGuide) mCurrentView.findViewById(R.id.footer_guide);
        isFilterSetting = GFCommonUtil.getInstance().isFilterSetting();
        if (isFilterSetting || GFImageAdjustmentUtil.getInstance().isConformedToUISetting()) {
            mUpdatingImage.setVisibility(4);
            mUpdatingText.setVisibility(4);
            mFooterGuide.setVisibility(0);
        } else {
            mUpdatingImage.setVisibility(0);
            mUpdatingText.setVisibility(0);
            mFooterGuide.setVisibility(4);
        }
        isHistgramView = BackUpUtil.getInstance().getPreferenceBoolean(GFBackUpKey.KEY_HISTGRAM_VIEW, true);
        if (isFilterSetting) {
            GFHistgramTask.getInstance().startHistgramUpdating();
        }
        GFHistgramTask.getInstance().setEnableUpdating(isHistgramView);
        if (!isHistgramView || !isFilterSetting) {
            mGFGraphViewFilter.setVisibility(4);
            mGFGraphViewBase.setVisibility(4);
        } else {
            mGFGraphViewFilter.setVisibility(0);
            mGFGraphViewBase.setVisibility(0);
        }
        mVsbPlus.setVisibility(4);
        mVsbMinus.setVisibility(4);
        mVsbSrc.setVisibility(4);
        mVerticalSeekBar.setVisibility(4);
        this.mGaugeText.setVisibility(4);
        this.mShadingLevel = (TextView) mCurrentView.findViewById(R.id.shading_level);
        return mCurrentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        CameraNotificationManager.getInstance().setNotificationListener(this);
        CameraNotificationManager.getInstance().requestNotify(GFConstants.CHECKE_UPDATE_STATUS);
        DisplayModeObserver.getInstance().setNotificationListener(this.mListener);
        mTextHighlighter = new GFTextHighlighter(this.mShadingLevel, -2236963, -2267648);
        updateValue();
    }

    private void updateValue() {
        int level = mVerticalSeekBar.getProgress() + 1;
        if (level > 0) {
            this.mShadingLevel.setText(GFCommonUtil.getInstance().getSignedInteger(level));
        } else {
            this.mShadingLevel.setText("" + level);
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
        DisplayModeObserver.getInstance().removeNotificationListener(this.mListener);
        this.mShadingLevel = null;
        mUpdatingImage = null;
        mUpdatingText = null;
        mFooterGuide = null;
        mParams = null;
        mBorderView = null;
        mUpArrow = null;
        mDownArrow = null;
        mVsbPlus = null;
        mVsbMinus = null;
        mVsbSrc = null;
        mVerticalSeekBar = null;
        this.mGaugeText = null;
        mGFGraphViewFilter = null;
        mGFGraphViewBase = null;
        mCurrentView = null;
        BackUpUtil.getInstance().setPreference(GFBackUpKey.KEY_HISTGRAM_VIEW, Boolean.valueOf(isHistgramView));
        super.onPause();
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
        mParams.setStrength(level);
        updateValue();
        mTextHighlighter.startHighlight();
        CameraNotificationManager.getInstance().requestNotify(GFConstants.UPDATE_APPSETTING);
        mBorderView.invalidate();
        this.mGaugeText.setText("" + GFEffectParameters.Parameters.mSAStrength[level]);
    }

    private void decrementShading() {
        int level = mVerticalSeekBar.moveDown() + mCompValue;
        mParams.setStrength(level);
        updateValue();
        mTextHighlighter.startHighlight();
        CameraNotificationManager.getInstance().requestNotify(GFConstants.UPDATE_APPSETTING);
        mBorderView.invalidate();
        this.mGaugeText.setText("" + GFEffectParameters.Parameters.mSAStrength[level]);
    }

    private void toggleDispMode() {
        if (isFilterSetting) {
            if (!isHistgramView) {
                isHistgramView = true;
                mGFGraphViewFilter.setVisibility(0);
                mGFGraphViewBase.setVisibility(0);
            } else {
                isHistgramView = false;
                mGFGraphViewFilter.setVisibility(4);
                mGFGraphViewBase.setVisibility(4);
            }
            GFHistgramTask.getInstance().setEnableUpdating(isHistgramView);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        if (isFilterSetting) {
            return super.attachedLens();
        }
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        if (isFilterSetting) {
            return super.detachedLens();
        }
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        if (isFilterSetting) {
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
    public int pushedS1Key() {
        if (isFilterSetting) {
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
        if (mParams.getStrength() != mLevel) {
            mParams.setStrength(mLevel);
            CameraNotificationManager.getInstance().requestNotify(GFConstants.UPDATE_APPSETTING);
        }
        openPreviousMenu();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        if (isFilterSetting) {
            return super.pushedPlayBackKey();
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAFMFKey() {
        if (isFilterSetting) {
            return super.pushedAFMFKey();
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfHoldCustomKey() {
        if (isFilterSetting) {
            return super.pushedAfMfHoldCustomKey();
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfToggleCustomKey() {
        if (isFilterSetting) {
            return super.pushedAfMfToggleCustomKey();
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELHoldCustomKey() {
        if (isFilterSetting) {
            return super.pushedAELHoldCustomKey();
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELToggleCustomKey() {
        if (isFilterSetting) {
            return super.pushedAELToggleCustomKey();
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAfMfHoldCustomKey() {
        if (isFilterSetting) {
            return super.releasedAfMfHoldCustomKey();
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAELHoldCustomKey() {
        if (isFilterSetting) {
            return super.releasedAELHoldCustomKey();
        }
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        if (!isFilterSetting) {
            if (!CustomizableFunction.Unchanged.equals(func)) {
                return -1;
            }
            int ret = super.onConvertedKeyDown(event, func);
            return ret;
        }
        if (CustomizableFunction.DispChange.equals(func)) {
            toggleDispMode();
            return 1;
        }
        if (CustomizableFunction.Unchanged.equals(func) || CustomizableFunction.MfAssist.equals(func)) {
            int ret2 = super.onConvertedKeyDown(event, func);
            return ret2;
        }
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyUp(KeyEvent event, IKeyFunction func) {
        if (GFCommonUtil.getInstance().isFilterSetting() && (CustomizableFunction.AelHold.equals(func) || CustomizableFunction.AfMfHold.equals(func))) {
            return -1;
        }
        return super.onConvertedKeyUp(event, func);
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
                incrementShading();
                return 1;
            case AppRoot.USER_KEYCODE.LEFT /* 105 */:
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
            case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
                decrementShading();
                return 1;
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                openPreviousMenu();
                return 1;
            case AppRoot.USER_KEYCODE.SK2 /* 513 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                toggleDispMode();
                return 1;
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_TELE /* 610 */:
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_WIDE /* 611 */:
            case 645:
                return -1;
            case AppRoot.USER_KEYCODE.LENS_FOCUS_HOLD /* 653 */:
                return -1;
            default:
                int result = super.onKeyDown(keyCode, event);
                return result;
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
        if (GFImageAdjustmentUtil.getInstance().isConformedToUISetting()) {
            mFooterGuide.setVisibility(0);
        } else {
            mFooterGuide.setVisibility(4);
        }
    }
}
