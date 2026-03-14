package com.sony.imaging.app.digitalfilter.menu.layout;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.executor.IntervalRecExecutor;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.caution.GFInfo;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.common.GFImageAdjustmentUtil;
import com.sony.imaging.app.digitalfilter.sa.GFHistgramTask;
import com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFPositionLinkController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFThemeController;
import com.sony.imaging.app.digitalfilter.shooting.widget.BorderView;
import com.sony.imaging.app.digitalfilter.shooting.widget.GFGraphView;
import com.sony.imaging.app.digitalfilter.shooting.widget.UpdatingImage;
import com.sony.imaging.app.digitalfilter.shooting.widget.UpdatingText;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GFBorderMenuLayout extends DisplayMenuItemsMenuLayout implements NotificationListener {
    private static final int KEY_MOVE = 1;
    private static final int KEY_NONE = 0;
    private static final int KEY_ROTATE = 2;
    public static final String MENU_ID = "ID_GFBORDERMENULAYOUT";
    private static final int THRESH_KEY_REPEAT = 10;
    private static final String TAG = AppLog.getClassName();
    private static View mCurrentView = null;
    private static BorderView mBorderView = null;
    private static GFGraphView mGFGraphViewFilter = null;
    private static GFGraphView mGFGraphViewBase = null;
    private static UpdatingImage mUpdatingImage = null;
    private static UpdatingText mUpdatingText = null;
    private static FooterGuide mFooterGuide = null;
    private static TextView mTitle = null;
    private static int mOldDegree = 0;
    private static PointF mOldOSDPoint = null;
    private static int mOriginalDegree = 0;
    private static Point mOriginalPoint = null;
    private static boolean isHistgramView = false;
    private static boolean isLayerSetting = false;
    private static boolean isLayer3 = false;
    private static int mPrevCode = -1;
    private static int KEYREPEAT_COUNT = 8;
    private static long KEYREPEAT_TIME = KEYREPEAT_COUNT * IntervalRecExecutor.INTVL_REC_INITIALIZED;
    private static List<Long> mTimeList = null;
    private static boolean isPosLink = false;
    private static int mLastKeyTrigger = 0;
    private static int mOtherBoundaryId = 0;
    private static int mOldOtherDegree = 0;
    private static PointF mOldOtherOSDPoint = null;
    private static int mOriginalOtherDegree = 0;
    private static Point mOriginalOtherPoint = null;
    private static int mRelativeDegree = 0;
    private static PointF mRelativeOSDPoint = null;
    private static boolean isCanceled = false;
    private static boolean isReversed = false;
    NotificationListener mListener = new ChangeYUVNotifier();
    private String[] TAGS = {GFConstants.CHECKE_UPDATE_STATUS, GFConstants.TAG_GYROSCOPE, GFConstants.RESET_BOUNDARY_SETTING};

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
        mTitle = (TextView) mCurrentView.findViewById(R.id.menu_screen_title);
        isLayerSetting = GFCommonUtil.getInstance().isLayerSetting();
        if (isLayerSetting) {
            mFooterGuide.setData(new FooterGuideDataResId(getActivity(), R.string.STRID_FUNC_DF_POSITION_FOOTER, R.string.STRID_FUNC_DF_POSITION_FOOTER));
        } else {
            mFooterGuide.setData(new FooterGuideDataResId(getActivity(), R.string.STRID_FUNC_SKYND_POSITION_FOOTER, R.string.STRID_FUNC_SKYND_POSITION_FOOTER));
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
        mLastKeyTrigger = 0;
        isCanceled = false;
        return mCurrentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        GFCommonUtil.getInstance().setVerticalLinkState(false);
        isLayer3 = GFCommonUtil.getInstance().isLayer3();
        if (isLayerSetting) {
            if (!isLayer3) {
                mTitle.setText(R.string.STRID_FUNC_DF_POSITION_SKY);
            } else {
                mTitle.setText(R.string.STRID_FUNC_SKYND_POSITION_LAYER3);
            }
        } else if (GFCommonUtil.getInstance().getBorderId() == 0) {
            mTitle.setText(R.string.STRID_FUNC_DF_POSITION_SKY);
        } else {
            mTitle.setText(R.string.STRID_FUNC_SKYND_POSITION_LAYER3);
        }
        CameraNotificationManager.getInstance().setNotificationListener(this);
        CameraNotificationManager.getInstance().requestNotify(GFConstants.CHECKE_UPDATE_STATUS);
        DisplayModeObserver.getInstance().setNotificationListener(this.mListener);
        mOldDegree = GFCommonUtil.getInstance().getDegree();
        mOldOSDPoint = GFCommonUtil.getInstance().getOSDPoint();
        mOriginalDegree = mOldDegree;
        mOriginalPoint = GFCommonUtil.getInstance().getPoint();
        isPosLink = GFPositionLinkController.getInstance().isLink() && isLayerSetting;
        if (GFCommonUtil.getInstance().isLayer3()) {
            mOtherBoundaryId = 0;
        } else {
            mOtherBoundaryId = 1;
        }
        if (isPosLink) {
            mOldOtherDegree = GFCommonUtil.getInstance().getDegree(mOtherBoundaryId);
            mOldOtherOSDPoint = GFCommonUtil.getInstance().getOSDPoint(mOtherBoundaryId);
            mOriginalOtherDegree = mOldOtherDegree;
            mOriginalOtherPoint = GFCommonUtil.getInstance().getPoint(mOtherBoundaryId);
            mRelativeDegree = mOldOtherDegree - mOldDegree;
            mRelativeOSDPoint = new PointF(mOldOtherOSDPoint.x, mOldOtherOSDPoint.y);
            mRelativeOSDPoint.x -= mOldOSDPoint.x;
            mRelativeOSDPoint.y -= mOldOSDPoint.y;
        }
        isReversed = GFCommonUtil.getInstance().isReversedDisplay();
        mTimeList = new ArrayList();
    }

    private void checkVirticalLinkState() {
        if (GFCommonUtil.getInstance().isVerticalLinkExecuted()) {
            GFCommonUtil.getInstance().setVerticalLinkState(false);
            mOriginalPoint = GFCommonUtil.getInstance().getPoint();
            if (isPosLink) {
                mOriginalOtherPoint = GFCommonUtil.getInstance().getPoint(mOtherBoundaryId);
            }
            refreshInfo();
            mOriginalDegree = mOldDegree;
            mOriginalOtherDegree = mOldOtherDegree;
        }
    }

    private void refreshInfo() {
        mOldDegree = GFCommonUtil.getInstance().getDegree();
        mOldOSDPoint = GFCommonUtil.getInstance().getOSDPoint();
        if (isPosLink) {
            mOldOtherDegree = GFCommonUtil.getInstance().getDegree(mOtherBoundaryId);
            mOldOtherOSDPoint = GFCommonUtil.getInstance().getOSDPoint(mOtherBoundaryId);
            mRelativeOSDPoint.x = mOldOtherOSDPoint.x - mOldOSDPoint.x;
            mRelativeOSDPoint.y = mOldOtherOSDPoint.y - mOldOSDPoint.y;
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        CameraNotificationManager.getInstance().removeNotificationListener(this.mListener);
        DisplayModeObserver.getInstance().removeNotificationListener(this.mListener);
        mTitle = null;
        mCurrentView = null;
        mGFGraphViewFilter = null;
        mGFGraphViewBase = null;
        mBorderView = null;
        mUpdatingImage = null;
        mUpdatingText = null;
        mFooterGuide = null;
        mOldOSDPoint = null;
        mOriginalPoint = null;
        mTimeList = null;
        mOldOtherOSDPoint = null;
        mOriginalOtherPoint = null;
        mRelativeOSDPoint = null;
        GFHistgramTask.getInstance().setEnableUpdating(false);
        if (isLayerSetting) {
            GFHistgramTask.getInstance().stopHistgramUpdating();
        }
        BackUpUtil.getInstance().setPreference(GFBackUpKey.KEY_HISTGRAM_VIEW, Boolean.valueOf(isHistgramView));
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        System.gc();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        int guideTitleID = R.string.STRID_FUNC_DF_POSITION_SKY;
        int guideDefi = R.string.STRID_FUNC_DF_SKY_SETTINGS_POSITION;
        if (GFCommonUtil.getInstance().isLayer3()) {
            guideTitleID = R.string.STRID_FUNC_SKYND_POSITION_LAYER3;
            guideDefi = R.string.STRID_FUNC_SKYND_LAYER3_SETTINGS_POSITION;
        }
        guideResources.add(getText(guideTitleID));
        guideResources.add(getText(guideDefi));
        guideResources.add(true);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        if (!isCanceled && isPosLink && isLayer3) {
            CameraNotificationManager.getInstance().requestNotify(GFConstants.RELOAD_ALL_IMAGE);
        }
        super.closeLayout();
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
        int degree = GFCommonUtil.getInstance().getDegree();
        int degreeDiff = -1;
        if (isKeyRepeat) {
            degreeDiff = -5;
        }
        int degree2 = degree + degreeDiff;
        if (degree2 < 0) {
            degree2 += 360;
        }
        updateRotation(degree2, degreeDiff);
    }

    private void rotateRight(boolean isKeyRepeat) {
        int degree = GFCommonUtil.getInstance().getDegree();
        int degreeDiff = 1;
        if (isKeyRepeat) {
            degreeDiff = 5;
        }
        int degree2 = degree + degreeDiff;
        if (degree2 >= 360) {
            degree2 -= 360;
        }
        updateRotation(degree2, degreeDiff);
    }

    public void adjustLayer3Point() {
        double degree0 = 0.0d;
        if (mRelativeOSDPoint.x == 0.0d || mRelativeOSDPoint.y == 0.0d) {
            if (mRelativeOSDPoint.y > 0.0f) {
                degree0 = 1.5707963267948966d;
            } else if (mRelativeOSDPoint.y < 0.0f) {
                degree0 = -1.5707963267948966d;
            }
            if (mRelativeOSDPoint.x > 0.0f) {
                degree0 = 0.0d;
            } else if (mRelativeOSDPoint.x < 0.0f) {
                degree0 = 3.141592653589793d;
            }
        } else {
            degree0 = Math.atan(mRelativeOSDPoint.y / mRelativeOSDPoint.x);
            if (mRelativeOSDPoint.x < 0.0f) {
                degree0 += 3.141592653589793d;
            }
        }
        int origDegree = GFCommonUtil.getInstance().getDegree();
        double rotateDegree = ((origDegree - mOldDegree) / 180.0d) * 3.141592653589793d;
        if (GFCommonUtil.getInstance().isReversedDisplay()) {
            rotateDegree = -rotateDegree;
        }
        PointF point = GFCommonUtil.getInstance().getOSDPoint();
        int centerX = (int) point.x;
        int centerY = (int) point.y;
        double distance = Math.sqrt((mRelativeOSDPoint.x * mRelativeOSDPoint.x) + (mRelativeOSDPoint.y * mRelativeOSDPoint.y));
        double diffX = distance * Math.cos(degree0 + rotateDegree);
        double diffY = distance * Math.sin(degree0 + rotateDegree);
        GFCommonUtil.getInstance().setOSDPoint(new PointF(centerX + ((float) diffX), centerY + ((float) diffY)), mOtherBoundaryId);
    }

    private void updateRotation(int degree, int diff) {
        GFCommonUtil.getInstance().setDegree(degree);
        if (isPosLink) {
            if (mLastKeyTrigger == 1) {
                refreshInfo();
            }
            mLastKeyTrigger = 2;
            int degree3 = degree + mRelativeDegree;
            if (degree3 < 0) {
                degree3 += 360;
            }
            if (degree3 >= 360) {
                degree3 -= 360;
            }
            GFCommonUtil.getInstance().setDegree(degree3, mOtherBoundaryId);
            adjustLayer3Point();
        }
        mBorderView.invalidate();
        CameraNotificationManager.getInstance().requestNotify(GFConstants.UPDATE_APPSETTING);
    }

    private void moveToRight(KeyEvent event) {
        AppLog.enter(TAG, AppLog.getMethodName());
        checkRefresh();
        PointF point = GFCommonUtil.getInstance().getOSDPoint();
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
        checkRefresh();
        PointF point = GFCommonUtil.getInstance().getOSDPoint();
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
        checkRefresh();
        PointF point = GFCommonUtil.getInstance().getOSDPoint();
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
        checkRefresh();
        PointF point = GFCommonUtil.getInstance().getOSDPoint();
        if (event.getRepeatCount() >= 10) {
            point.y -= 10.0f;
        } else {
            point.y -= 1.0f;
        }
        updatePointPosition(point);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void checkRefresh() {
        if (isPosLink) {
            if (mLastKeyTrigger == 2) {
                refreshInfo();
            }
            mLastKeyTrigger = 1;
        }
    }

    private void checkReversePanel() {
        if (isReversed != GFCommonUtil.getInstance().isReversedDisplay()) {
            isReversed = GFCommonUtil.getInstance().isReversedDisplay();
            if (isPosLink) {
                mRelativeOSDPoint.x = -mRelativeOSDPoint.x;
            }
        }
    }

    private void updatePointPosition(PointF point) {
        AppLog.enter(TAG, AppLog.getMethodName());
        RectF limit = GFEffectParameters.getInstance().getParameters().getLimitOSDPoint();
        if (!limit.contains(point.x, point.y)) {
            point = handleLimit(point, limit);
        }
        if (isPosLink) {
            PointF point3 = new PointF(mRelativeOSDPoint.x + point.x, mRelativeOSDPoint.y + point.y);
            if (!limit.contains(point3.x, point3.y)) {
                point3 = handleLimit(point3, limit);
            }
            GFCommonUtil.getInstance().setOSDPoint(point3, mOtherBoundaryId);
        }
        GFCommonUtil.getInstance().setOSDPoint(point);
        mBorderView.invalidate();
        CameraNotificationManager.getInstance().requestNotify(GFConstants.UPDATE_APPSETTING);
        AppLog.exit(TAG, AppLog.getMethodName());
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

    private void initBoundary() {
        GFEffectParameters.Parameters param = GFEffectParameters.getInstance().getParameters();
        int themeId = GFEffectParameters.Parameters.getEffect();
        param.setPoint(GFEffectParameters.Parameters.DEFAULT_CENTER.get(themeId));
        param.setDegree(GFEffectParameters.Parameters.DEFAULT_DEGREE[themeId]);
        param.setStrength(GFEffectParameters.Parameters.DEFAULT_STRENGTH[themeId]);
        param.setPoint2(GFEffectParameters.Parameters.DEFAULT_CENTER2.get(themeId));
        param.setDegree2(GFEffectParameters.Parameters.DEFAULT_DEGREE2[themeId]);
        param.setStrength2(GFEffectParameters.Parameters.DEFAULT_STRENGTH2[themeId]);
        mOldDegree = GFCommonUtil.getInstance().getDegree();
        mOldOSDPoint = GFCommonUtil.getInstance().getOSDPoint();
        mOriginalDegree = mOldDegree;
        mOriginalPoint = GFCommonUtil.getInstance().getPoint();
        if (isPosLink) {
            mOldOtherDegree = GFCommonUtil.getInstance().getDegree(mOtherBoundaryId);
            mOldOtherOSDPoint = GFCommonUtil.getInstance().getOSDPoint(mOtherBoundaryId);
            mRelativeOSDPoint.x = mOldOtherOSDPoint.x - mOldOSDPoint.x;
            mRelativeOSDPoint.y = mOldOtherOSDPoint.y - mOldOSDPoint.y;
            mOriginalOtherDegree = mOldOtherDegree;
            mOriginalOtherPoint = GFCommonUtil.getInstance().getPoint(mOtherBoundaryId);
            mRelativeDegree = mOldOtherDegree - mOldDegree;
        }
        String theme = GFThemeController.getInstance().getValue();
        GFBackUpKey.getInstance().resetDeviceDirection(theme);
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
        checkVirticalLinkState();
        GFCommonUtil.getInstance().setDegree(mOriginalDegree);
        GFCommonUtil.getInstance().setPoint(mOriginalPoint);
        if (isPosLink) {
            GFCommonUtil.getInstance().setDegree(mOriginalOtherDegree, mOtherBoundaryId);
            GFCommonUtil.getInstance().setPoint(mOriginalOtherPoint, mOtherBoundaryId);
        }
        CameraNotificationManager.getInstance().requestNotify(GFConstants.UPDATE_APPSETTING);
        isCanceled = true;
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
        checkReversePanel();
        switch (code) {
            case 103:
                if (!isFunctionGuideShown()) {
                    checkVirticalLinkState();
                    moveToTop(event);
                }
                return 1;
            case AppRoot.USER_KEYCODE.LEFT /* 105 */:
                if (!isFunctionGuideShown()) {
                    checkVirticalLinkState();
                    if (GFCommonUtil.getInstance().isReversedDisplay()) {
                        moveToRight(event);
                    } else {
                        moveToLeft(event);
                    }
                }
                return 1;
            case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
                if (!isFunctionGuideShown()) {
                    checkVirticalLinkState();
                    if (GFCommonUtil.getInstance().isReversedDisplay()) {
                        moveToLeft(event);
                    } else {
                        moveToRight(event);
                    }
                }
                return 1;
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                if (!isFunctionGuideShown()) {
                    checkVirticalLinkState();
                    moveToBottom(event);
                }
                return 1;
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                if (!isFunctionGuideShown()) {
                    openPreviousMenu();
                    return 1;
                }
                int result = super.onKeyDown(keyCode, event);
                return result;
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
            case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                if (!isFunctionGuideShown()) {
                    checkVirticalLinkState();
                    rotateRight(isKeyRepeat(code, event.getDownTime()));
                }
                return 1;
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
            case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
                if (!isFunctionGuideShown()) {
                    checkVirticalLinkState();
                    rotateLeft(isKeyRepeat(code, event.getDownTime()));
                }
                return 1;
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
        return super.onKeyUp(keyCode, event);
    }

    private boolean isKeyRepeat(int code, long downTime) {
        if (mPrevCode == code) {
            mTimeList.add(Long.valueOf(downTime));
            if (mTimeList.size() > KEYREPEAT_COUNT) {
                mTimeList.remove(0);
            }
            if (mTimeList.size() == KEYREPEAT_COUNT && mTimeList.get(KEYREPEAT_COUNT - 1).longValue() - mTimeList.get(0).longValue() < KEYREPEAT_TIME) {
                return true;
            }
        } else {
            mPrevCode = code;
            mTimeList.clear();
            mTimeList.add(Long.valueOf(downTime));
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
