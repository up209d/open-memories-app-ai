package com.sony.imaging.app.digitalfilter.shooting.layout;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.layout.ShootingLayout;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.caution.GFInfo;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.digitalfilter.shooting.widget.BorderView;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.util.NotificationListener;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GFAdjustment15LayerBorderLayout extends ShootingLayout {
    public static final String MENU_ID = "Adjustment15BorderLayout";
    NotificationListener mListener = new ChangeYUVNotifier();
    private static final String TAG = AppLog.getClassName();
    private static boolean isCanceled = false;
    private static boolean isKeyRepeat = false;
    private static int mOriginalDegree = 0;
    private static Point mOriginalPoint = null;
    private static Point mRefOSDPoint = null;
    private static BorderView mBorderView = null;
    private static int mBorder = 0;
    private static int mPrevCode = -1;
    private static long mUpTimeMillis = -1;
    private static float mMoveMax = 1.0f;
    private static int KEYREPEAT_COUNT = 8;
    private static long KEYREPEAT_TIME = KEYREPEAT_COUNT * 160;
    private static List<Long> timeList = new ArrayList();
    private static Handler mHandler = null;
    private static Runnable mDialRunnable = null;
    private static int mMoveCount = 0;
    private static int mRoundPostionState = 0;
    private static int ROUND_BY_MOVETOUPPER = 1;
    private static int ROUND_BY_MOVETOLOWER = 2;

    @Override // com.sony.imaging.app.fw.KeyReceiver
    protected String getKeyConvCategory() {
        return "Menu";
    }

    @Override // com.sony.imaging.app.base.shooting.layout.ShootingLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View currentView = obtainViewFromPool(R.layout.menu_adjustment15_border);
        mBorderView = (BorderView) currentView.findViewById(R.id.border_view);
        return currentView;
    }

    @Override // com.sony.imaging.app.base.shooting.layout.ShootingLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        DisplayModeObserver.getInstance().setNotificationListener(this.mListener);
        isCanceled = false;
        mBorder = GFCommonUtil.getInstance().getBorderId();
        mOriginalDegree = GFCommonUtil.getInstance().getDegree(mBorder);
        mOriginalPoint = GFCommonUtil.getInstance().getPoint(mBorder);
        mRefOSDPoint = new Point(mOriginalPoint.x, mOriginalPoint.y);
        mMoveCount = 0;
        mUpTimeMillis = 0L;
        mMoveMax = 1.0f;
        final int action = this.data.getInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION);
        mHandler = new Handler();
        mDialRunnable = new Runnable() { // from class: com.sony.imaging.app.digitalfilter.shooting.layout.GFAdjustment15LayerBorderLayout.1
            @Override // java.lang.Runnable
            public void run() {
                switch (action) {
                    case 1:
                        GFAdjustment15LayerBorderLayout.this.turnedMainDialNext();
                        return;
                    case 2:
                        GFAdjustment15LayerBorderLayout.this.turnedMainDialPrev();
                        return;
                    case 3:
                        GFAdjustment15LayerBorderLayout.this.turnedSubDialNext();
                        return;
                    case 4:
                        GFAdjustment15LayerBorderLayout.this.turnedSubDialPrev();
                        return;
                    default:
                        return;
                }
            }
        };
        mHandler.post(mDialRunnable);
    }

    @Override // com.sony.imaging.app.base.shooting.layout.ShootingLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        DisplayModeObserver.getInstance().removeNotificationListener(this.mListener);
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        if (isCanceled) {
            GFCommonUtil.getInstance().setDegree(mOriginalDegree, mBorder);
            GFCommonUtil.getInstance().setPoint(mOriginalPoint, mBorder);
            CameraNotificationManager.getInstance().requestNotify(GFConstants.UPDATE_APPSETTING);
        }
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        mBorderView = null;
        mRefOSDPoint = null;
        mOriginalPoint = null;
        super.onDestroyView();
        System.gc();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK1Key() {
        return pushedMenuKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        isCanceled = true;
        closeLayout();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        moveToLower();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        moveToUpper();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        rotateRight();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        rotateLeft();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialNext() {
        moveToLower();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialPrev() {
        moveToUpper();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        closeLayout();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        closeLayout();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        closeLayout();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        closeLayout();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        closeLayout();
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_INVALID_FUNCTION_IN_APPSETTING);
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedGuideFuncKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int movedAelFocusSlideKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAFMFKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        if (GFConstants.Setting15LayerCustomizableFunction.contains(func)) {
            int ret = super.onConvertedKeyDown(event, func);
            return ret;
        }
        return -1;
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

    private boolean isKeyRepeat(int code, long downTime, long upTime) {
        if (mPrevCode == code) {
            timeList.add(Long.valueOf(downTime - upTime));
            if (timeList.size() > KEYREPEAT_COUNT) {
                timeList.remove(0);
            }
            if (timeList.size() == KEYREPEAT_COUNT) {
                int sum = 0;
                for (int i = 0; i < KEYREPEAT_COUNT; i++) {
                    sum = (int) (sum + timeList.get(i).longValue());
                }
                if (sum < KEYREPEAT_TIME) {
                    mMoveMax += 0.3f;
                    if (mMoveMax > 10.0f) {
                        mMoveMax = 10.0f;
                    }
                    return true;
                }
            }
        } else {
            mMoveMax = 1.0f;
            mPrevCode = code;
            timeList.clear();
            timeList.add(Long.valueOf(downTime));
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

    private void updateRotation(int degree) {
        mRefOSDPoint = GFCommonUtil.getInstance().getPoint();
        mMoveCount = 0;
        GFCommonUtil.getInstance().setDegree(degree);
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

    private boolean updatePointPosition(PointF point) {
        RectF limit = GFEffectParameters.getInstance().getParameters().getLimitOSDPoint();
        boolean isLmited = !limit.contains(point.x, point.y);
        if (isLmited) {
            point = handleLimit(point, limit);
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
            GFAdjustment15LayerBorderLayout.mBorderView.invalidate();
        }
    }
}
