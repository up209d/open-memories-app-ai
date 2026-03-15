package com.sony.imaging.app.graduatedfilter.menu.layout;

import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.executor.IntervalRecExecutor;
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
import com.sony.imaging.app.graduatedfilter.common.SaUtil;
import com.sony.imaging.app.graduatedfilter.sa.GFHistgramTask;
import com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.graduatedfilter.shooting.widget.BorderView;
import com.sony.imaging.app.graduatedfilter.shooting.widget.GFGraphView;
import com.sony.imaging.app.graduatedfilter.shooting.widget.UpdatingImage;
import com.sony.imaging.app.graduatedfilter.shooting.widget.UpdatingText;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GFBorderMenuLayout extends DisplayMenuItemsMenuLayout implements NotificationListener {
    public static final String MENU_ID = "ID_GFBORDERMENULAYOUT";
    private static final int THRESH_KEY_REPEAT = 10;
    private static GFEffectParameters.Parameters mParams;
    private static final String TAG = AppLog.getClassName();
    private static View mCurrentView = null;
    private static BorderView mBorderView = null;
    private static GFGraphView mGFGraphViewFilter = null;
    private static GFGraphView mGFGraphViewBase = null;
    private static ImageView mDeleteButton = null;
    private static ImageView mDeleteButtonDisp = null;
    private static UpdatingImage mUpdatingImage = null;
    private static UpdatingText mUpdatingText = null;
    private static FooterGuide mFooterGuide = null;
    private static boolean userSettingBorder = false;
    private static int mOldDegree = 0;
    private static PointF mOldOSDPoint = null;
    private static boolean isHistgramView = false;
    private static boolean isFilterSetting = false;
    private static Handler myHandler = null;
    private static Runnable myRunnable = null;
    private static int mPrevCode = -1;
    private static int KEYREPEAT_COUNT = 8;
    private static long KEYREPEAT_TIME = KEYREPEAT_COUNT * IntervalRecExecutor.INTVL_REC_INITIALIZED;
    private static List<Long> timeList = new ArrayList();
    NotificationListener mListener = new ChangeYUVNotifier();
    private String[] TAGS = {GFConstants.CHECKE_UPDATE_STATUS};

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mCurrentView = obtainViewFromPool(R.layout.menu_border);
        mBorderView = (BorderView) mCurrentView.findViewById(R.id.border_view);
        mGFGraphViewFilter = (GFGraphView) mCurrentView.findViewById(R.id.filter_histgram);
        mGFGraphViewBase = (GFGraphView) mCurrentView.findViewById(R.id.base_histgram);
        mUpdatingImage = (UpdatingImage) mCurrentView.findViewById(R.id.updating);
        mUpdatingText = (UpdatingText) mCurrentView.findViewById(R.id.updating_text);
        mFooterGuide = (FooterGuide) mCurrentView.findViewById(R.id.footer_guide);
        mDeleteButton = (ImageView) mCurrentView.findViewById(R.id.delete_button);
        if (SaUtil.isAVIP()) {
            mDeleteButton.setBackgroundResource(R.drawable.p_16_dd_parts_key_sk2);
        } else {
            mDeleteButton.setBackgroundResource(R.drawable.p_16_dd_parts_key_delete);
        }
        mDeleteButtonDisp = (ImageView) mCurrentView.findViewById(R.id.delete_button_disp);
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
        setButtonVisibility(0);
        if (!isHistgramView || !isFilterSetting) {
            disappearButtonView();
            mGFGraphViewFilter.setVisibility(4);
            mGFGraphViewBase.setVisibility(4);
        } else {
            mGFGraphViewFilter.setVisibility(0);
            mGFGraphViewBase.setVisibility(0);
        }
        return mCurrentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        CameraNotificationManager.getInstance().setNotificationListener(this);
        CameraNotificationManager.getInstance().requestNotify(GFConstants.CHECKE_UPDATE_STATUS);
        DisplayModeObserver.getInstance().setNotificationListener(this.mListener);
        mParams = GFEffectParameters.getInstance().getParameters();
        mOldDegree = mParams.getDegree();
        mOldOSDPoint = mParams.getOSDPoint();
        userSettingBorder = false;
        if (SaUtil.isAVIP()) {
            KEYREPEAT_COUNT = 5;
            KEYREPEAT_TIME = KEYREPEAT_COUNT * AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_AF_S;
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        DisplayModeObserver.getInstance().removeNotificationListener(this.mListener);
        if (myHandler != null) {
            myHandler.removeCallbacks(myRunnable);
            myHandler = null;
        }
        myRunnable = null;
        userSettingBorder = false;
        mCurrentView = null;
        mGFGraphViewFilter = null;
        mGFGraphViewBase = null;
        mBorderView = null;
        mUpdatingImage = null;
        mUpdatingText = null;
        mFooterGuide = null;
        mDeleteButton = null;
        mDeleteButtonDisp = null;
        GFHistgramTask.getInstance().setEnableUpdating(false);
        if (isFilterSetting) {
            GFHistgramTask.getInstance().stopHistgramUpdating();
        }
        BackUpUtil.getInstance().setPreference(GFBackUpKey.KEY_HISTGRAM_VIEW, Boolean.valueOf(isHistgramView));
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected boolean isAELCancel() {
        return false;
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

    private void rotateLeft(boolean isKeyRepeat) {
        int degree;
        int degree2 = mParams.getDegree();
        if (isKeyRepeat) {
            degree = degree2 - 5;
        } else {
            degree = degree2 - 1;
        }
        if (degree < 0) {
            degree += 360;
        }
        updateRotation(degree);
    }

    private void rotateRight(boolean isKeyRepeat) {
        int degree;
        int degree2 = mParams.getDegree();
        if (isKeyRepeat) {
            degree = degree2 + 5;
        } else {
            degree = degree2 + 1;
        }
        if (degree >= 360) {
            degree -= 360;
        }
        updateRotation(degree);
    }

    private void updateRotation(int degree) {
        userSettingBorder = true;
        mParams.setDegree(degree);
        mBorderView.invalidate();
        CameraNotificationManager.getInstance().requestNotify(GFConstants.UPDATE_APPSETTING);
    }

    private void moveToRight(KeyEvent event) {
        AppLog.enter(TAG, AppLog.getMethodName());
        PointF point = mParams.getOSDPoint();
        if (event.getRepeatCount() >= 10) {
            point.x += 10.0f;
        } else {
            point.x += 1.0f;
        }
        updatePointPosition(point);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void moveToLeft(KeyEvent event) {
        AppLog.enter(TAG, AppLog.getMethodName());
        PointF point = mParams.getOSDPoint();
        if (event.getRepeatCount() >= 10) {
            point.x -= 10.0f;
        } else {
            point.x -= 1.0f;
        }
        updatePointPosition(point);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void moveToBottom(KeyEvent event) {
        AppLog.enter(TAG, AppLog.getMethodName());
        PointF point = mParams.getOSDPoint();
        if (event.getRepeatCount() >= 10) {
            point.y += 10.0f;
        } else {
            point.y += 1.0f;
        }
        updatePointPosition(point);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void moveToTop(KeyEvent event) {
        AppLog.enter(TAG, AppLog.getMethodName());
        PointF point = mParams.getOSDPoint();
        if (event.getRepeatCount() >= 10) {
            point.y -= 10.0f;
        } else {
            point.y -= 1.0f;
        }
        updatePointPosition(point);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void updatePointPosition(PointF point) {
        AppLog.enter(TAG, AppLog.getMethodName());
        RectF limit = mParams.getLimitOSDPoint();
        if (!limit.contains(point.x, point.y)) {
            point = handleLimit(point, limit);
        }
        mParams.setOSDPoint(point);
        mBorderView.invalidate();
        userSettingBorder = true;
        CameraNotificationManager.getInstance().requestNotify(GFConstants.UPDATE_APPSETTING);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void toggleDispMode() {
        if (isFilterSetting) {
            if (!isHistgramView) {
                isHistgramView = true;
                setButtonVisibility(0);
                mGFGraphViewFilter.setVisibility(0);
                mGFGraphViewBase.setVisibility(0);
            } else {
                isHistgramView = false;
                disappearButtonView();
                mGFGraphViewFilter.setVisibility(4);
                mGFGraphViewBase.setVisibility(4);
            }
            GFHistgramTask.getInstance().setEnableUpdating(isHistgramView);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setButtonVisibility(int visibility) {
        mDeleteButton.setVisibility(visibility);
        mDeleteButtonDisp.setVisibility(visibility);
    }

    private void disappearButtonView() {
        if (!isFilterSetting) {
            setButtonVisibility(4);
            return;
        }
        if (myHandler == null) {
            myHandler = new Handler();
        }
        if (myRunnable == null) {
            myRunnable = new Runnable() { // from class: com.sony.imaging.app.graduatedfilter.menu.layout.GFBorderMenuLayout.1
                @Override // java.lang.Runnable
                public void run() {
                    if (!GFBorderMenuLayout.isHistgramView) {
                        GFBorderMenuLayout.this.setButtonVisibility(4);
                    }
                }
            };
        }
        myHandler.postDelayed(myRunnable, 3000L);
    }

    private PointF handleLimit(PointF point, RectF limit) {
        if (GFEffectParameters.mRotation == 1) {
            point.x = Math.max(point.x, limit.left - 1.0f);
            point.x = Math.min(point.x, limit.right);
            point.y = Math.max(point.y, limit.top - 1.0f);
            point.y = Math.min(point.y, limit.bottom);
        } else {
            point.x = Math.max(point.x, limit.left);
            point.x = Math.min(point.x, limit.right - 1.0f);
            point.y = Math.max(point.y, limit.top);
            point.y = Math.min(point.y, limit.bottom - 1.0f);
        }
        return point;
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
        if (userSettingBorder) {
            mParams.setDegree(mOldDegree);
            mParams.setOSDPoint(mOldOSDPoint);
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
        if (!GFCommonUtil.getInstance().isFilterSetting() || CustomizableFunction.Unchanged.equals(func)) {
            return super.onConvertedKeyUp(event, func);
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        switch (code) {
            case 103:
                moveToTop(event);
                return 1;
            case AppRoot.USER_KEYCODE.LEFT /* 105 */:
                if (GFCommonUtil.getInstance().isReversedDisplay()) {
                    moveToRight(event);
                } else {
                    moveToLeft(event);
                }
                return 1;
            case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
                if (GFCommonUtil.getInstance().isReversedDisplay()) {
                    moveToLeft(event);
                } else {
                    moveToRight(event);
                }
                return 1;
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                moveToBottom(event);
                return 1;
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                openPreviousMenu();
                return 1;
            case AppRoot.USER_KEYCODE.SK2 /* 513 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                toggleDispMode();
                return 1;
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
            case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                rotateRight(isKeyRepeat(code, event.getDownTime()));
                return 1;
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
            case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
                rotateLeft(isKeyRepeat(code, event.getDownTime()));
                return 1;
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_TELE /* 610 */:
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_WIDE /* 611 */:
            case 645:
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
        return super.onKeyUp(keyCode, event);
    }

    private boolean isKeyRepeat(int code, long downTime) {
        if (mPrevCode == code) {
            timeList.add(Long.valueOf(downTime));
            if (timeList.size() > KEYREPEAT_COUNT) {
                timeList.remove(0);
            }
            if (timeList.size() == KEYREPEAT_COUNT && timeList.get(KEYREPEAT_COUNT - 1).longValue() - timeList.get(0).longValue() < KEYREPEAT_TIME) {
                return true;
            }
        } else {
            mPrevCode = code;
            timeList.clear();
            timeList.add(Long.valueOf(downTime));
        }
        return false;
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
            GFBorderMenuLayout.mBorderView.invalidate();
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
