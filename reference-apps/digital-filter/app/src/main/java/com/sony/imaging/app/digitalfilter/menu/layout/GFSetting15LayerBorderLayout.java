package com.sony.imaging.app.digitalfilter.menu.layout;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.DigitalZoomController;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.sa.GFHistgramTask;
import com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFPositionLinkController;
import com.sony.imaging.app.digitalfilter.shooting.widget.BorderView;
import com.sony.imaging.app.digitalfilter.shooting.widget.GFGraphView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GFSetting15LayerBorderLayout extends Fn15LayerAbsLayout {
    private static final int KEY_MOVE = 1;
    private static final int KEY_NONE = 0;
    private static final int KEY_ROTATE = 2;
    public static final String MENU_ID = "ID_GFSETTING15LAYERBORDERLAYOUT";
    private static final String TAG = AppLog.getClassName();
    private static boolean isCanceled = false;
    private static boolean isKeyRepeat = false;
    private static int mSetting = 0;
    private static int mOldDegree = 0;
    private static PointF mOldOSDPoint = null;
    private static int mOriginalDegree = 0;
    private static Point mOriginalPoint = null;
    private static Point mRefOSDPoint = null;
    private static BorderView mBorderView = null;
    private static int mBoundaryId = 0;
    private static int mPrevCode = -1;
    private static long mUpTimeMillis = -1;
    private static float mMoveMax = 1.0f;
    private static int KEYREPEAT_COUNT = 8;
    private static long KEYREPEAT_TIME = KEYREPEAT_COUNT * DigitalZoomController.ZOOM_INIT_VALUE_MODIFIED;
    private static List<Long> mTimeList = null;
    private static boolean isLayer3 = false;
    private static boolean isPosLink = false;
    private static int mLastKeyTrigger = 0;
    private static int mOtherBoundaryId = 0;
    private static int mOldOtherDegree = 0;
    private static PointF mOldOtherOSDPoint = null;
    private static int mOriginalOtherDegree = 0;
    private static Point mOriginalOtherPoint = null;
    private static int mRelativeDegree = 0;
    private static PointF mRelativeOSDPoint = null;
    private static boolean isOneDial = false;
    private static int mMoveCount = 0;
    private static boolean isLayerSetting = false;
    private static GFGraphView mGFGraphViewFilter = null;
    private static GFGraphView mGFGraphViewBase = null;
    private static boolean isHistgramView = false;
    private static int mRoundPostionState = 0;
    private static int ROUND_BY_MOVETOUPPER = 1;
    private static int ROUND_BY_MOVETOLOWER = 2;

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View currentView = obtainViewFromPool(R.layout.menu_setting15_border);
        mBorderView = GFSettingMenuLayout.getBorderView();
        mGFGraphViewFilter = (GFGraphView) currentView.findViewById(R.id.filter_histgram);
        mGFGraphViewBase = (GFGraphView) currentView.findViewById(R.id.base_histgram);
        isHistgramView = BackUpUtil.getInstance().getPreferenceBoolean(GFBackUpKey.KEY_HISTGRAM_VIEW, true);
        if (!isHistgramView) {
            mGFGraphViewFilter.setVisibility(4);
            mGFGraphViewBase.setVisibility(4);
        } else {
            mGFGraphViewFilter.setVisibility(0);
            mGFGraphViewBase.setVisibility(0);
        }
        isOneDial = true;
        mMoveCount = 0;
        GFHistgramTask.getInstance().startHistgramUpdating();
        GFHistgramTask.getInstance().setEnableUpdating(isHistgramView);
        return currentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        GFCommonUtil.getInstance().setVerticalLinkState(false);
        isCanceled = false;
        isLayer3 = GFCommonUtil.getInstance().isLayer3();
        mSetting = GFCommonUtil.getInstance().getSettingLayer();
        mBoundaryId = 0;
        mOtherBoundaryId = 1;
        if (mSetting == 2) {
            mBoundaryId = 1;
            mOtherBoundaryId = 0;
        }
        mOldDegree = GFCommonUtil.getInstance().getDegree(mBoundaryId);
        mOldOSDPoint = GFCommonUtil.getInstance().getOSDPoint(mBoundaryId);
        mOriginalDegree = mOldDegree;
        mOriginalPoint = GFCommonUtil.getInstance().getPoint(mBoundaryId);
        mRefOSDPoint = new Point(mOriginalPoint.x, mOriginalPoint.y);
        isLayerSetting = GFCommonUtil.getInstance().isLayerSetting();
        isPosLink = GFPositionLinkController.getInstance().isLink() && isLayerSetting;
        if (isPosLink) {
            mOldOtherDegree = GFCommonUtil.getInstance().getDegree(mOtherBoundaryId);
            mOldOtherOSDPoint = GFCommonUtil.getInstance().getOSDPoint(mOtherBoundaryId);
            mOriginalOtherPoint = GFCommonUtil.getInstance().getPoint(mOtherBoundaryId);
            mOriginalOtherDegree = mOldOtherDegree;
            mRelativeDegree = mOldOtherDegree - mOldDegree;
            mRelativeOSDPoint = new PointF(mOldOtherOSDPoint.x, mOldOtherOSDPoint.y);
            mRelativeOSDPoint.x -= mOldOSDPoint.x;
            mRelativeOSDPoint.y -= mOldOSDPoint.y;
        }
        mLastKeyTrigger = 0;
        mUpTimeMillis = 0L;
        mMoveMax = 1.0f;
        mTimeList = new ArrayList();
        super.onResume();
    }

    private void checkVirticalLinkState() {
        if (GFCommonUtil.getInstance().isVerticalLinkExecuted()) {
            GFCommonUtil.getInstance().setVerticalLinkState(false);
            mOriginalPoint = GFCommonUtil.getInstance().getPoint(mBoundaryId);
            if (isPosLink) {
                mOriginalOtherPoint = GFCommonUtil.getInstance().getPoint(mOtherBoundaryId);
            }
            refreshInfo();
            mOriginalDegree = mOldDegree;
            mOriginalOtherDegree = mOldOtherDegree;
            updatePointAndCount();
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

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        mOldOSDPoint = null;
        mOriginalPoint = null;
        mRefOSDPoint = null;
        mOldOtherOSDPoint = null;
        mOriginalOtherPoint = null;
        mTimeList = null;
        mRelativeOSDPoint = null;
        mGFGraphViewFilter = null;
        mGFGraphViewBase = null;
        BackUpUtil.getInstance().setPreference(GFBackUpKey.KEY_HISTGRAM_VIEW, Boolean.valueOf(isHistgramView));
        GFHistgramTask.getInstance().stopHistgramUpdating();
        CameraNotificationManager.getInstance().requestNotify(GFConstants.CLOSE_15LAYER_BOUNDARY_SETTING);
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        if (isCanceled) {
            GFCommonUtil.getInstance().setDegree(mOriginalDegree, mBoundaryId);
            GFCommonUtil.getInstance().setPoint(mOriginalPoint, mBoundaryId);
            if (isPosLink) {
                GFCommonUtil.getInstance().setDegree(mOriginalOtherDegree, mOtherBoundaryId);
                GFCommonUtil.getInstance().setPoint(mOriginalOtherPoint, mOtherBoundaryId);
            }
            CameraNotificationManager.getInstance().requestNotify(GFConstants.UPDATE_APPSETTING);
        } else if (isPosLink && isLayer3) {
            CameraNotificationManager.getInstance().requestNotify(GFConstants.RELOAD_ALL_IMAGE);
        }
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        mBorderView = null;
        super.onDestroyView();
        System.gc();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected boolean isAELCancel() {
        return false;
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

    private void toggleDispMode() {
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

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK1Key() {
        return pushedMenuKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        checkVirticalLinkState();
        isCanceled = true;
        return super.pushedMenuKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        checkRefresh();
        checkVirticalLinkState();
        if (isOneDial) {
            moveToLower();
            return 1;
        }
        moveToTop();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        checkRefresh();
        checkVirticalLinkState();
        if (isOneDial) {
            moveToUpper();
            return 1;
        }
        moveToBottom();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        checkVirticalLinkState();
        if (isOneDial) {
            rotateRight();
            return 1;
        }
        checkRefresh();
        moveToRight();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        checkVirticalLinkState();
        if (isOneDial) {
            rotateLeft();
            return 1;
        }
        checkRefresh();
        moveToLeft();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialNext() {
        checkRefresh();
        checkVirticalLinkState();
        if (isOneDial) {
            moveToLower();
            return 1;
        }
        moveToTop();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialPrev() {
        checkRefresh();
        checkVirticalLinkState();
        if (isOneDial) {
            moveToUpper();
            return 1;
        }
        moveToBottom();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        closeLayout();
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int movedAelFocusSlideKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAFMFKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int ret;
        int code = event.getScanCode();
        if (GFConstants.Setting15LayerCustomizableFunction.contains(func)) {
            ret = super.onConvertedKeyDown(event, func);
        } else {
            if (GFCommonUtil.getInstance().isTriggerMfAssist(code)) {
                return 0;
            }
            ret = -1;
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

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        mUpTimeMillis = System.currentTimeMillis();
        return super.onKeyUp(keyCode, event);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        isKeyRepeat = isKeyRepeat(code, System.currentTimeMillis(), mUpTimeMillis);
        switch (code) {
            case 103:
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                if (isFunctionGuideShown()) {
                    return 1;
                }
                closeMenuLayout(new Bundle());
                return 0;
            default:
                int ret = super.onKeyDown(keyCode, event);
                return ret;
        }
    }

    private boolean isKeyRepeat(int code, long downTime, long upTime) {
        if (mPrevCode == code) {
            mTimeList.add(Long.valueOf(downTime - upTime));
            if (mTimeList.size() > KEYREPEAT_COUNT) {
                mTimeList.remove(0);
            }
            if (mTimeList.size() == KEYREPEAT_COUNT) {
                int sum = 0;
                for (int i = 0; i < KEYREPEAT_COUNT; i++) {
                    sum = (int) (sum + mTimeList.get(i).longValue());
                }
                if (sum < KEYREPEAT_TIME) {
                    mMoveMax += 0.33f;
                    if (mMoveMax > 10.0f) {
                        mMoveMax = 10.0f;
                    }
                    return true;
                }
            }
        } else {
            mMoveMax = 1.0f;
            mPrevCode = code;
            mTimeList.clear();
            mTimeList.add(Long.valueOf(downTime));
        }
        return false;
    }

    private void rotateLeft() {
        int degree;
        int degree2 = GFCommonUtil.getInstance().getDegree();
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

    private void rotateRight() {
        int degree;
        int degree2 = GFCommonUtil.getInstance().getDegree();
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

    public void adjustOtherPoint() {
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
        PointF point = GFCommonUtil.getInstance().getOSDPoint();
        int centerX = (int) point.x;
        int centerY = (int) point.y;
        double distance = Math.sqrt((mRelativeOSDPoint.x * mRelativeOSDPoint.x) + (mRelativeOSDPoint.y * mRelativeOSDPoint.y));
        double diffX = distance * Math.cos(degree0 + rotateDegree);
        double diffY = distance * Math.sin(degree0 + rotateDegree);
        GFCommonUtil.getInstance().setOSDPoint(new PointF(centerX + ((float) diffX), centerY + ((float) diffY)), mOtherBoundaryId);
    }

    private void updateRotation(int degree) {
        mRefOSDPoint = GFCommonUtil.getInstance().getPoint(mBoundaryId);
        mMoveCount = 0;
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
            adjustOtherPoint();
        }
        CameraNotificationManager.getInstance().requestNotify(GFConstants.UPDATE_APPSETTING);
        mBorderView.invalidate();
    }

    private void moveToRight() {
        AppLog.enter(TAG, AppLog.getMethodName());
        PointF point = GFCommonUtil.getInstance().getOSDPoint();
        if (isKeyRepeat) {
            point.x += 10.0f;
        } else {
            point.x += 1.0f;
        }
        updatePointPosition(point);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void moveToLeft() {
        AppLog.enter(TAG, AppLog.getMethodName());
        PointF point = GFCommonUtil.getInstance().getOSDPoint();
        if (isKeyRepeat) {
            point.x -= 10.0f;
        } else {
            point.x -= 1.0f;
        }
        updatePointPosition(point);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void moveToBottom() {
        AppLog.enter(TAG, AppLog.getMethodName());
        PointF point = GFCommonUtil.getInstance().getOSDPoint();
        if (isKeyRepeat) {
            point.y += 10.0f;
        } else {
            point.y += 1.0f;
        }
        updatePointPosition(point);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void moveToTop() {
        AppLog.enter(TAG, AppLog.getMethodName());
        PointF point = GFCommonUtil.getInstance().getOSDPoint();
        if (isKeyRepeat) {
            point.y -= 10.0f;
        } else {
            point.y -= 1.0f;
        }
        updatePointPosition(point);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void updatePointAndCount() {
        Point point = GFCommonUtil.getInstance().getPoint();
        mRefOSDPoint.x = point.x;
        mRefOSDPoint.y = point.y;
        mMoveCount = 0;
    }

    private void moveToLower() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mRoundPostionState == ROUND_BY_MOVETOUPPER) {
            updatePointAndCount();
        }
        PointF point = GFCommonUtil.getInstance().getOSDPoint();
        int degree = GFCommonUtil.getInstance().getDegree();
        double sin = Math.sin((degree * 3.141592653589793d) / 180.0d);
        double cos = Math.cos((degree * 3.141592653589793d) / 180.0d);
        if (isKeyRepeat) {
            mMoveCount = (int) (mMoveCount - mMoveMax);
        } else {
            mMoveCount--;
        }
        PointF refOSDPoint = GFEffectParameters.getInstance().getParameters().getOSDPoint(mRefOSDPoint);
        if (GFCommonUtil.getInstance().isReversedDisplay()) {
            point.x = refOSDPoint.x + ((float) (mMoveCount * sin));
        } else {
            point.x = refOSDPoint.x - ((float) (mMoveCount * sin));
        }
        point.y = refOSDPoint.y + ((float) (mMoveCount * cos));
        if (updatePointPosition(point)) {
            mRoundPostionState = ROUND_BY_MOVETOLOWER;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void moveToUpper() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mRoundPostionState == ROUND_BY_MOVETOLOWER) {
            updatePointAndCount();
        }
        PointF point = GFCommonUtil.getInstance().getOSDPoint();
        int degree = GFCommonUtil.getInstance().getDegree();
        double sin = Math.sin((degree * 3.141592653589793d) / 180.0d);
        double cos = Math.cos((degree * 3.141592653589793d) / 180.0d);
        if (isKeyRepeat) {
            mMoveCount = (int) (mMoveCount + mMoveMax);
        } else {
            mMoveCount++;
        }
        PointF refOSDPoint = GFEffectParameters.getInstance().getParameters().getOSDPoint(mRefOSDPoint);
        if (GFCommonUtil.getInstance().isReversedDisplay()) {
            point.x = refOSDPoint.x + ((float) (mMoveCount * sin));
        } else {
            point.x = refOSDPoint.x - ((float) (mMoveCount * sin));
        }
        point.y = refOSDPoint.y + ((float) (mMoveCount * cos));
        if (updatePointPosition(point)) {
            mRoundPostionState = ROUND_BY_MOVETOUPPER;
        }
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

    private boolean updatePointPosition(PointF point) {
        RectF limit = GFEffectParameters.getInstance().getParameters().getLimitOSDPoint();
        boolean isLmited = !limit.contains(point.x, point.y);
        if (isLmited) {
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
        CameraNotificationManager.getInstance().requestNotify(GFConstants.UPDATE_APPSETTING);
        mBorderView.invalidate();
        return isLmited;
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
}
