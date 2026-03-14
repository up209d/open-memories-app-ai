package com.sony.imaging.app.digitalfilter.shooting.layout;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.layout.ShootingLayout;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.common.GFImageAdjustmentUtil;
import com.sony.imaging.app.digitalfilter.menu.layout.GFBorderMenuLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFShadingMenuLayout;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFFilterSetController;
import com.sony.imaging.app.digitalfilter.shooting.widget.BorderView;
import com.sony.imaging.app.digitalfilter.shooting.widget.UpdatingImage;
import com.sony.imaging.app.digitalfilter.shooting.widget.UpdatingText;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomKeyMgr;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.RelativeLayoutGroup;

/* loaded from: classes.dex */
public class GFAdjustmentLayout extends ShootingLayout implements NotificationListener {
    private static final int INVISIBLE_ALPHA = 128;
    private static final int POS = 0;
    private static final int SAVE = 2;
    private static final int SHADING = 1;
    private static final int VISIBLE_ALPHA = 255;
    private static View[] mFocus;
    private static ImageView mSetting15Zabuton0;
    private static ImageView mSetting15Zabuton1;
    private static View mCurrentView = null;
    private static int mCurrentFocus = 0;
    private static int mLastFocus = 0;
    private static ImageView mAreaIcon = null;
    private static TextView mTitle = null;
    private static TextView mItemName = null;
    private static UpdatingImage mUpdatingImage = null;
    private static UpdatingText mUpdatingText = null;
    private static View mFunctionGuideView = null;
    private static View mFocused = null;
    private static BorderView mBorderView = null;
    private static boolean mOpeningOtherLayout = false;
    private static TextView mFunctionNameTextView = null;
    private static TextView mGuideTextView = null;
    private static TextView mInvalidTextView = null;
    private static RelativeLayout mFilterGuide = null;
    private static Handler mHandler = null;
    private static Runnable mRunnable = null;
    private static Runnable mTitleRunnable = null;
    private static Runnable mItemNameRunnable = null;
    private static RelativeLayoutGroup mBatteryInfo = null;
    private final String TAG = AppLog.getClassName();
    private FooterGuide mFooterGuide = null;
    private String[] TAGS = {GFConstants.CHECKE_UPDATE_STATUS};
    NotificationListener mListener = new ChangeYUVNotifier();

    @Override // com.sony.imaging.app.fw.KeyReceiver
    protected String getKeyConvCategory() {
        return "Menu";
    }

    @Override // com.sony.imaging.app.base.shooting.layout.ShootingLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.adjustmentlayout);
        mFocus = new View[]{(ImageView) mCurrentView.findViewById(R.id.focus0), (ImageView) mCurrentView.findViewById(R.id.focus1), (ImageView) mCurrentView.findViewById(R.id.focus2)};
        mSetting15Zabuton0 = (ImageView) mCurrentView.findViewById(R.id.zabuton0);
        mSetting15Zabuton1 = (ImageView) mCurrentView.findViewById(R.id.zabuton1);
        mAreaIcon = (ImageView) mCurrentView.findViewById(R.id.area_icon);
        mTitle = (TextView) mCurrentView.findViewById(R.id.menu_screen_title);
        mItemName = (TextView) mCurrentView.findViewById(R.id.item_name);
        this.mFooterGuide = (FooterGuide) mCurrentView.findViewById(R.id.footer_guide);
        mFunctionGuideView = inflater.inflate(R.layout.function_guide, (ViewGroup) null);
        mFunctionNameTextView = (TextView) mFunctionGuideView.findViewById(R.id.function_name);
        mGuideTextView = (TextView) mFunctionGuideView.findViewById(R.id.guide);
        mInvalidTextView = (TextView) mFunctionGuideView.findViewById(R.id.invalid);
        mBorderView = (BorderView) mCurrentView.findViewById(R.id.border_view);
        mUpdatingImage = (UpdatingImage) mCurrentView.findViewById(R.id.updating);
        mUpdatingText = (UpdatingText) mCurrentView.findViewById(R.id.updating_text);
        if (GFImageAdjustmentUtil.getInstance().isConformedToUISetting()) {
            mUpdatingImage.setVisibility(4);
            mUpdatingText.setVisibility(4);
            this.mFooterGuide.setVisibility(0);
        } else {
            mUpdatingImage.setVisibility(0);
            mUpdatingText.setVisibility(0);
            this.mFooterGuide.setVisibility(4);
        }
        mFilterGuide = (RelativeLayout) mCurrentView.findViewById(R.id.adjust_guide);
        mBatteryInfo = (RelativeLayoutGroup) mCurrentView.findViewById(R.id.battery_icon_on);
        return mCurrentView;
    }

    @Override // com.sony.imaging.app.base.shooting.layout.ShootingLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        CameraNotificationManager.getInstance().setNotificationListener(this);
        CameraNotificationManager.getInstance().requestNotify(GFConstants.CHECKE_UPDATE_STATUS);
        DisplayModeObserver.getInstance().setNotificationListener(this.mListener);
        mCurrentFocus = mLastFocus;
        View[] arr$ = mFocus;
        for (View focus : arr$) {
            focus.setBackgroundResource(17306069);
        }
        updateHeader();
        setFooterGuide();
        updateFocus();
        boolean isShownAdjustGuide = BackUpUtil.getInstance().getPreferenceBoolean(GFBackUpKey.KEY_SHOW_ADJUSTGUIDE, false);
        if (!isShownAdjustGuide) {
            mFilterGuide.setVisibility(0);
            BackUpUtil.getInstance().setPreference(GFBackUpKey.KEY_SHOW_ADJUSTGUIDE, true);
            setFooterGuide();
            return;
        }
        mFilterGuide.setVisibility(4);
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void onReopened() {
        super.onReopened();
        getFocusable();
    }

    @Override // com.sony.imaging.app.base.shooting.layout.ShootingLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        DisplayModeObserver.getInstance().removeNotificationListener(this.mListener);
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        View view = getView();
        if (view != null && (view instanceof ViewGroup)) {
            ViewGroup layoutView = (ViewGroup) view;
            layoutView.removeView(mFunctionGuideView);
        }
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
            mHandler.removeCallbacks(mTitleRunnable);
            mHandler.removeCallbacks(mItemNameRunnable);
            mHandler = null;
        }
        mRunnable = null;
        mTitleRunnable = null;
        mItemNameRunnable = null;
        mSetting15Zabuton0 = null;
        mSetting15Zabuton1 = null;
        mAreaIcon = null;
        mTitle = null;
        this.mFooterGuide = null;
        mFocused = null;
        mFunctionNameTextView = null;
        mGuideTextView = null;
        mInvalidTextView = null;
        mFunctionGuideView = null;
        mBorderView = null;
        mUpdatingImage = null;
        mUpdatingText = null;
        mCurrentView = null;
        mFocus = null;
        mBatteryInfo = null;
        super.onPause();
    }

    private void updateHeader() {
        int iconId = R.drawable.p_16_dd_parts_skyhdr_2nd_icon_2area;
        int textId = R.string.STRID_FUNC_DF_FINISHINGADJUSTMENT_2ND;
        if (GFFilterSetController.getInstance().need3rdShooting()) {
            if (GFCommonUtil.getInstance().getBorderId() != 0) {
                iconId = R.drawable.p_16_dd_parts_skyhdr_3rd_icon_3area;
                textId = R.string.STRID_FUNC_SKYND_FINISHINGADJUSTMENT_LAYER3;
            } else {
                iconId = R.drawable.p_16_dd_parts_skyhdr_2nd_icon_3area;
            }
        }
        mBatteryInfo.setVisibility(4);
        mAreaIcon.setVisibility(0);
        mTitle.setVisibility(0);
        mAreaIcon.setImageResource(iconId);
        mTitle.setText(textId);
        disappearTitle();
    }

    private void disappearTitle() {
        if (mHandler == null) {
            mHandler = new Handler();
        } else {
            mHandler.removeCallbacks(mTitleRunnable);
        }
        if (mTitleRunnable == null) {
            mTitleRunnable = new Runnable() { // from class: com.sony.imaging.app.digitalfilter.shooting.layout.GFAdjustmentLayout.1
                @Override // java.lang.Runnable
                public void run() {
                    GFAdjustmentLayout.mAreaIcon.setVisibility(4);
                    GFAdjustmentLayout.mTitle.setVisibility(4);
                    GFAdjustmentLayout.mBatteryInfo.setVisibility(0);
                }
            };
        }
        mHandler.postDelayed(mTitleRunnable, 3000L);
    }

    private void disappearItemName() {
        if (mHandler == null) {
            mHandler = new Handler();
        } else {
            mHandler.removeCallbacks(mItemNameRunnable);
        }
        if (mItemNameRunnable == null) {
            mItemNameRunnable = new Runnable() { // from class: com.sony.imaging.app.digitalfilter.shooting.layout.GFAdjustmentLayout.2
                @Override // java.lang.Runnable
                public void run() {
                    GFAdjustmentLayout.mItemName.setVisibility(4);
                }
            };
        }
        mHandler.postDelayed(mItemNameRunnable, 3000L);
    }

    private void updateFocus() {
        View[] arr$ = mFocus;
        for (View focus : arr$) {
            focus.setVisibility(4);
        }
        mFocus[mCurrentFocus].setVisibility(0);
        mOpeningOtherLayout = false;
        setZabuton(0);
    }

    public static int getFocusPosition() {
        return mCurrentFocus;
    }

    private void getFocusable() {
        if (mOpeningOtherLayout) {
            mOpeningOtherLayout = false;
            mItemName.setVisibility(0);
            setZabuton(0);
            setFooterGuide();
        }
    }

    private void setFooterGuide() {
        int id = R.string.STRID_FUNC_DF_FIN_ADJ_FOOTER;
        if (GFCommonUtil.getInstance().getBorderId() == 0) {
            switch (mCurrentFocus) {
                case 0:
                case 1:
                    if (GFFilterSetController.getInstance().need3rdShooting()) {
                        id = R.string.STRID_FUNC_DF_AREA_BTM_ADJ_FOOTER;
                        break;
                    } else {
                        id = R.string.STRID_FUNC_DF_FIN_ADJ_FOOTER;
                        break;
                    }
                case 2:
                    if (GFFilterSetController.getInstance().need3rdShooting()) {
                        id = R.string.STRID_FUNC_DF_FIN_BTM_OK_FOOTER;
                        break;
                    } else {
                        id = R.string.STRID_FOOTERGUIDE_ENTER_RETURN;
                        break;
                    }
            }
        } else {
            switch (mCurrentFocus) {
                case 0:
                case 1:
                    if (GFFilterSetController.getInstance().need3rdShooting()) {
                        id = R.string.STRID_FUNC_DF_AREA_TOP_ADJ_FOOTER;
                        break;
                    } else {
                        id = R.string.STRID_FUNC_DF_FIN_ADJ_FOOTER;
                        break;
                    }
                case 2:
                    if (GFFilterSetController.getInstance().need3rdShooting()) {
                        id = R.string.STRID_FUNC_DF_FIN_TOP_OK_FOOTER;
                        break;
                    } else {
                        id = R.string.STRID_FOOTERGUIDE_ENTER_RETURN;
                        break;
                    }
            }
        }
        if (mFilterGuide != null && mFilterGuide.getVisibility() == 0) {
            id = 17042422;
        }
        this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), id, id));
        updateItemName();
    }

    private void setZabuton(int visibility) {
        int id = R.drawable.p_16_dd_parts_skyhdr_base;
        if (visibility != 0) {
            id = R.drawable.p_16_dd_parts_skyhdr_transparent_base;
        }
        mSetting15Zabuton0.setImageResource(id);
        mSetting15Zabuton1.setImageResource(id);
    }

    private void updateItemName() {
        switch (mCurrentFocus) {
            case 0:
                if (GFCommonUtil.getInstance().getBorderId() == 0) {
                    mItemName.setText(R.string.STRID_FUNC_DF_POSITION_SKY);
                } else {
                    mItemName.setText(R.string.STRID_FUNC_SKYND_POSITION_LAYER3);
                }
                mItemName.setVisibility(0);
                break;
            case 1:
                if (GFCommonUtil.getInstance().getBorderId() == 0) {
                    mItemName.setText(R.string.STRID_FUNC_DF_GRADATION_SKY);
                } else {
                    mItemName.setText(R.string.STRID_FUNC_SKYND_GRADATION_LAYER3);
                }
                mItemName.setVisibility(0);
                break;
            default:
                mItemName.setVisibility(4);
                break;
        }
        disappearItemName();
    }

    private void moveFocusToLeft() {
        mCurrentFocus--;
        if (mCurrentFocus < 0) {
            mCurrentFocus = mFocus.length - 1;
        }
        updateFocus();
        setFooterGuide();
        mBorderView.invalidate();
    }

    private void moveFocusToRight() {
        mCurrentFocus++;
        if (mCurrentFocus > mFocus.length - 1) {
            mCurrentFocus = 0;
        }
        updateFocus();
        setFooterGuide();
        mBorderView.invalidate();
    }

    private void setFunctionGuideResources() {
        switch (mCurrentFocus) {
            case 0:
                if (GFCommonUtil.getInstance().getBorderId() == 0) {
                    mFunctionNameTextView.setText(R.string.STRID_FUNC_DF_POSITION_SKY);
                    mGuideTextView.setText(R.string.STRID_FUNC_DF_SKY_SETTINGS_POSITION);
                    return;
                } else {
                    mFunctionNameTextView.setText(R.string.STRID_FUNC_SKYND_POSITION_LAYER3);
                    mGuideTextView.setText(R.string.STRID_FUNC_SKYND_LAYER3_SETTINGS_POSITION);
                    return;
                }
            case 1:
                if (GFCommonUtil.getInstance().getBorderId() == 0) {
                    mFunctionNameTextView.setText(R.string.STRID_FUNC_DF_GRADATION_SKY);
                    mGuideTextView.setText(R.string.STRID_FUNC_DF_SKY_SETTINGS_GRADATION);
                    return;
                } else {
                    mFunctionNameTextView.setText(R.string.STRID_FUNC_SKYND_GRADATION_LAYER3);
                    mGuideTextView.setText(R.string.STRID_FUNC_SKYND_LAYER3_SETTINGS_GRADATION);
                    return;
                }
            default:
                mFunctionNameTextView.setText(R.string.STRID_FUNC_SKYND_ADJUST_SAVE);
                mGuideTextView.setText(R.string.STRID_FUNC_SKYND_ADJUST_SAVE_GUIDE);
                return;
        }
    }

    private int openNextMenuLayout() {
        int ret = 1;
        String layoutID = null;
        switch (mCurrentFocus) {
            case 0:
                mLastFocus = mCurrentFocus;
                layoutID = GFBorderMenuLayout.MENU_ID;
                break;
            case 1:
                mLastFocus = mCurrentFocus;
                layoutID = GFShadingMenuLayout.MENU_ID;
                break;
            case 2:
                closeLayout();
                ret = 0;
                break;
        }
        if (layoutID != null) {
            closeLayout();
            openLayout(layoutID);
        }
        return ret;
    }

    private void openFn15LayerLayout(Bundle bundle) {
        String layoutId = null;
        boolean valid = false;
        if (mCurrentFocus == 0) {
            layoutId = GFAdjustment15LayerBorderLayout.MENU_ID;
            valid = true;
        } else if (mCurrentFocus == 1) {
            layoutId = GFAdjustment15LayerShadingLayout.MENU_ID;
            valid = true;
        }
        if (valid) {
            openLayout(layoutId, bundle);
            setZabuton(4);
            mItemName.setVisibility(8);
            this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), R.string.STRID_FUNC_TIMELAPSE_OPT_FOOTER_GUIDE, R.string.STRID_FUNC_TIMELAPSE_OPT_FOOTER_GUIDE));
            mOpeningOtherLayout = true;
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        Bundle b = new Bundle();
        b.putInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION, 1);
        openFn15LayerLayout(b);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        Bundle b = new Bundle();
        b.putInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION, 2);
        openFn15LayerLayout(b);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        Bundle b = new Bundle();
        b.putInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION, 3);
        openFn15LayerLayout(b);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        Bundle b = new Bundle();
        b.putInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION, 4);
        openFn15LayerLayout(b);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialNext() {
        Bundle b = new Bundle();
        b.putInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION, 1);
        openFn15LayerLayout(b);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialPrev() {
        Bundle b = new Bundle();
        b.putInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION, 2);
        openFn15LayerLayout(b);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK2Key() {
        return pushedDeleteKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteKey() {
        return pushedMenuKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK1Key() {
        return pushedMenuKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        if (!mOpeningOtherLayout) {
            closeLayout();
            openLayout("PreviewLayout");
            GFCommonUtil.getInstance().setBorderId(0);
            return 1;
        }
        updateFocus();
        setFooterGuide();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        return -1;
    }

    private int handleGuideFuncKey() {
        View view = getView();
        if (view != null && (view instanceof ViewGroup)) {
            ViewGroup layoutView = (ViewGroup) view;
            if (!mFunctionGuideView.isShown()) {
                setFunctionGuideResources();
                mInvalidTextView.setVisibility(8);
                layoutView.addView(mFunctionGuideView);
                mFocused = layoutView.getFocusedChild();
                layoutView.requestChildFocus(mFunctionGuideView, mFocused);
            } else {
                layoutView.requestChildFocus(mFocused, mFunctionGuideView);
                layoutView.removeView(mFunctionGuideView);
                mFocused = null;
            }
            return 1;
        }
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int retVal = 1;
        int code = event.getScanCode();
        if (mFilterGuide.getVisibility() == 0) {
            mFilterGuide.setVisibility(4);
            setFooterGuide();
            if (232 == code) {
                return 1;
            }
        }
        ICustomKey key = CustomKeyMgr.getInstance().get(code);
        if (key.getAssigned("Menu").equals(CustomizableFunction.Guide)) {
            retVal = handleGuideFuncKey();
        } else if (mFunctionGuideView.isShown()) {
            if (code == 232 || code == 514 || code == 229) {
                ViewGroup layoutView = (ViewGroup) getView();
                layoutView.removeView(mFunctionGuideView);
            } else if (code == 516) {
                retVal = super.onKeyDown(keyCode, event);
            }
        } else {
            switch (code) {
                case 103:
                    if (GFFilterSetController.getInstance().need3rdShooting() && GFCommonUtil.getInstance().getBorderId() == 0) {
                        GFCommonUtil.getInstance().setCameraSettingsLayerDuringShots(2);
                        GFCommonUtil.getInstance().toggleBorder();
                        updateHeader();
                        setFooterGuide();
                        CameraNotificationManager.getInstance().requestNotify(GFConstants.SHADING_LEVEL_CHANGE);
                        mBorderView.invalidate();
                    }
                    mOpeningOtherLayout = false;
                    break;
                case AppRoot.USER_KEYCODE.LEFT /* 105 */:
                    moveFocusToLeft();
                    break;
                case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
                    moveFocusToRight();
                    break;
                case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                    if (GFFilterSetController.getInstance().need3rdShooting() && GFCommonUtil.getInstance().getBorderId() == 1) {
                        GFCommonUtil.getInstance().setCameraSettingsLayerDuringShots(1);
                        GFCommonUtil.getInstance().toggleBorder();
                        updateHeader();
                        setFooterGuide();
                        CameraNotificationManager.getInstance().requestNotify(GFConstants.SHADING_LEVEL_CHANGE);
                        mBorderView.invalidate();
                    }
                    mOpeningOtherLayout = false;
                    break;
                case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                    if (!mOpeningOtherLayout) {
                        retVal = openNextMenuLayout();
                        if (retVal == 0) {
                            retVal = super.onKeyDown(keyCode, event);
                            break;
                        }
                    } else {
                        updateFocus();
                        setFooterGuide();
                        break;
                    }
                    break;
                case AppRoot.USER_KEYCODE.SK2 /* 513 */:
                case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                    break;
                case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
                case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
                case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
                case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
                case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
                case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
                case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
                case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                    if (!mOpeningOtherLayout) {
                        retVal = super.onKeyDown(keyCode, event);
                        break;
                    }
                    break;
                case AppRoot.USER_KEYCODE.LENS_FOCUS_HOLD /* 653 */:
                    retVal = -1;
                    break;
                default:
                    retVal = super.onKeyDown(keyCode, event);
                    break;
            }
        }
        return retVal;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        if (code == 653) {
            return -1;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return this.TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (GFImageAdjustmentUtil.getInstance().isConformedToUISetting()) {
            this.mFooterGuide.setVisibility(0);
        } else {
            this.mFooterGuide.setVisibility(4);
        }
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
            GFAdjustmentLayout.mBorderView.invalidate();
        }
    }
}
