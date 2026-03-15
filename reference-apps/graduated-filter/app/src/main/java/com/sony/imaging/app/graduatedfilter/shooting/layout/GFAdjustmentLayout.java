package com.sony.imaging.app.graduatedfilter.shooting.layout;

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
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.layout.ShootingLayout;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomKeyMgr;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.graduatedfilter.GFBackUpKey;
import com.sony.imaging.app.graduatedfilter.R;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.graduatedfilter.common.GFCommonUtil;
import com.sony.imaging.app.graduatedfilter.common.GFConstants;
import com.sony.imaging.app.graduatedfilter.common.GFImageAdjustmentUtil;
import com.sony.imaging.app.graduatedfilter.menu.layout.GFBorderMenuLayout;
import com.sony.imaging.app.graduatedfilter.menu.layout.GFShadingMenuLayout;
import com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.graduatedfilter.shooting.widget.BorderView;
import com.sony.imaging.app.graduatedfilter.shooting.widget.UpdatingImage;
import com.sony.imaging.app.graduatedfilter.shooting.widget.UpdatingText;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class GFAdjustmentLayout extends ShootingLayout implements NotificationListener {
    private static final int BORDER = 0;
    private static final int SAVE = 2;
    private static final int SHADING = 1;
    private static View mCurrentView = null;
    private static int mCurrentFocus = 0;
    private static int mLastFocus = 0;
    private static ImageView mFocus00 = null;
    private static ImageView mFocus01 = null;
    private static ImageView mFocus03 = null;
    private static TextView mItemName = null;
    private static ImageView[] mFocus = null;
    private static UpdatingImage mUpdatingImage = null;
    private static UpdatingText mUpdatingText = null;
    private static View mFunctionGuideView = null;
    private static View mFocused = null;
    private static BorderView mBorderView = null;
    private static TextView mShadingLevel = null;
    private static TextView mFunctionNameTextView = null;
    private static TextView mGuideTextView = null;
    private static TextView mInvalidTextView = null;
    private static RelativeLayout mFilterGuide = null;
    private final String TAG = AppLog.getClassName();
    private FooterGuide mFooterGuide = null;
    Handler myHandler = null;
    Runnable myRunnableGuide = null;
    private String[] TAGS = {GFConstants.CHECKE_UPDATE_STATUS};
    NotificationListener mListener = new ChangeYUVNotifier();

    @Override // com.sony.imaging.app.base.shooting.layout.ShootingLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.adjustmentlayout);
        mFocus00 = (ImageView) mCurrentView.findViewById(R.id.focus00);
        mFocus01 = (ImageView) mCurrentView.findViewById(R.id.focus01);
        mFocus03 = (ImageView) mCurrentView.findViewById(R.id.focus03);
        mFocus = new ImageView[]{mFocus00, mFocus01, mFocus03};
        mItemName = (TextView) mCurrentView.findViewById(R.id.item_name);
        this.mFooterGuide = (FooterGuide) mCurrentView.findViewById(R.id.footer_guide);
        mFunctionGuideView = inflater.inflate(R.layout.function_guide, (ViewGroup) null);
        mFunctionNameTextView = (TextView) mFunctionGuideView.findViewById(R.id.function_name);
        mGuideTextView = (TextView) mFunctionGuideView.findViewById(R.id.guide);
        mInvalidTextView = (TextView) mFunctionGuideView.findViewById(R.id.invalid);
        mShadingLevel = (TextView) mCurrentView.findViewById(R.id.option01Value);
        mBorderView = (BorderView) mCurrentView.findViewById(R.id.border_view);
        mUpdatingImage = (UpdatingImage) mCurrentView.findViewById(R.id.updating);
        mUpdatingText = (UpdatingText) mCurrentView.findViewById(R.id.updating_text);
        if (GFCommonUtil.getInstance().isFilterSetting() || GFImageAdjustmentUtil.getInstance().isConformedToUISetting()) {
            mUpdatingImage.setVisibility(4);
            mUpdatingText.setVisibility(4);
            this.mFooterGuide.setVisibility(0);
        } else {
            mUpdatingImage.setVisibility(0);
            mUpdatingText.setVisibility(0);
            this.mFooterGuide.setVisibility(4);
        }
        mFilterGuide = (RelativeLayout) mCurrentView.findViewById(R.id.adjust_guide);
        return mCurrentView;
    }

    @Override // com.sony.imaging.app.base.shooting.layout.ShootingLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        CameraNotificationManager.getInstance().setNotificationListener(this);
        CameraNotificationManager.getInstance().requestNotify(GFConstants.CHECKE_UPDATE_STATUS);
        DisplayModeObserver.getInstance().setNotificationListener(this.mListener);
        mCurrentFocus = mLastFocus;
        ImageView[] arr$ = mFocus;
        for (ImageView focus : arr$) {
            focus.setBackgroundResource(17306069);
        }
        setFooterGuide();
        updateFocus();
        int shadingLevel = GFEffectParameters.getInstance().getParameters().getStrength();
        if (GFCommonUtil.getInstance().avoidLensDistortion()) {
            mShadingLevel.setText(GFCommonUtil.getInstance().getSignedInteger((shadingLevel + 1) - 9));
        } else {
            mShadingLevel.setText(GFCommonUtil.getInstance().getSignedInteger(shadingLevel + 1));
        }
        boolean isShownAdjustGuide = BackUpUtil.getInstance().getPreferenceBoolean(GFBackUpKey.KEY_SHOW_ADJUSTGUIDE, false);
        if (!isShownAdjustGuide) {
            mFilterGuide.setVisibility(0);
            BackUpUtil.getInstance().setPreference(GFBackUpKey.KEY_SHOW_ADJUSTGUIDE, true);
        } else {
            mFilterGuide.setVisibility(4);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.layout.ShootingLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        DisplayModeObserver.getInstance().removeNotificationListener(this.mListener);
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        View view = getView();
        if (this.myHandler != null) {
            this.myHandler.removeCallbacks(this.myRunnableGuide);
            this.myHandler = null;
        }
        this.myRunnableGuide = null;
        if (view != null && (view instanceof ViewGroup)) {
            ViewGroup layoutView = (ViewGroup) view;
            layoutView.removeView(mFunctionGuideView);
        }
        this.mFooterGuide = null;
        mShadingLevel = null;
        mFocused = null;
        mFunctionNameTextView = null;
        mGuideTextView = null;
        mInvalidTextView = null;
        mFunctionGuideView = null;
        mBorderView = null;
        mUpdatingImage = null;
        mUpdatingText = null;
        mCurrentView = null;
        mFocus00 = null;
        mFocus01 = null;
        mFocus03 = null;
        mFocus = null;
        super.onPause();
    }

    private void updateFocus() {
        ImageView[] arr$ = mFocus;
        for (ImageView focus : arr$) {
            focus.setVisibility(4);
        }
        mFocus[mCurrentFocus].setVisibility(0);
    }

    private void setFooterGuide() {
        int id = R.string.STRID_FUNC_SKYND_APPSETTING_FOOTER_01;
        int idSK = R.string.STRID_FUNC_SKYND_APPSETTING_FOOTER_01_SK;
        switch (mCurrentFocus) {
            case 0:
            case 1:
                id = R.string.STRID_FUNC_SKYND_APPSETTING_FOOTER_01;
                idSK = R.string.STRID_FUNC_SKYND_APPSETTING_FOOTER_01_SK;
                break;
            case 2:
                id = R.string.STRID_FUNC_SKYND_APPSETTING_FOOTER_03;
                idSK = R.string.STRID_FUNC_SKYND_APPSETTING_FOOTER_03_SK;
                break;
        }
        this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), id, idSK));
        updateItemName();
    }

    private void updateItemName() {
        switch (mCurrentFocus) {
            case 0:
                mItemName.setText(R.string.STRID_FUNC_SKYND_SKYSETTING_POSITION);
                mItemName.setVisibility(0);
                return;
            case 1:
                mItemName.setText(R.string.STRID_FUNC_SKYND_SKYSETTING_GRADATION);
                mItemName.setVisibility(0);
                return;
            default:
                mItemName.setVisibility(4);
                return;
        }
    }

    private void moveFocusToLeft() {
        mCurrentFocus--;
        if (mCurrentFocus < 0) {
            mCurrentFocus = mFocus.length - 1;
        }
        updateFocus();
        setFooterGuide();
    }

    private void moveFocusToRight() {
        mCurrentFocus++;
        if (mCurrentFocus > mFocus.length - 1) {
            mCurrentFocus = 0;
        }
        updateFocus();
        setFooterGuide();
    }

    private void setFunctionGuideResources() {
        switch (mCurrentFocus) {
            case 0:
                mFunctionNameTextView.setText(R.string.STRID_FUNC_SKYND_SKYSETTING_POSITION);
                mGuideTextView.setText(R.string.STRID_FUNC_SKYND_SKYSETTINGS_POSITION);
                return;
            case 1:
                mFunctionNameTextView.setText(R.string.STRID_FUNC_SKYND_SKYSETTING_GRADATION);
                mGuideTextView.setText(R.string.STRID_FUNC_SKYND_SKYSETTINGS_GRADATION);
                return;
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
        closeLayout();
        openLayout("PreviewLayout");
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
                case AppRoot.USER_KEYCODE.LEFT /* 105 */:
                case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
                case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
                case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
                case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
                    moveFocusToLeft();
                    break;
                case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
                case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
                case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
                case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
                case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                    moveFocusToRight();
                    break;
                case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                    retVal = openNextMenuLayout();
                    if (retVal == 0) {
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
