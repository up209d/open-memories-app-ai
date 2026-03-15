package com.sony.imaging.app.lightshaft.menu.layout;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.MenuDataParcelable;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.lightshaft.AppLog;
import com.sony.imaging.app.lightshaft.LightShaftBackUpKey;
import com.sony.imaging.app.lightshaft.R;
import com.sony.imaging.app.lightshaft.shooting.ShaftsEffect;
import com.sony.imaging.app.lightshaft.shooting.camera.EffectSelectController;
import com.sony.imaging.app.lightshaft.shooting.widget.OverHeadShaftView;
import com.sony.imaging.app.lightshaft.shooting.widget.ShaftView;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.scalar.hardware.avio.DisplayManager;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;

/* loaded from: classes.dex */
public class LightSourceSettingLayout extends DisplayMenuItemsMenuLayout implements View.OnTouchListener {
    private static final String BACK = "back";
    private static final String TAG = "LightSourceSettingLayout";
    private static String mLastSavedOptionParams = "";
    private View mCurrentView;
    private FooterGuide mFooterGuide;
    private OverHeadShaftView mOverHeadView;
    private ShaftsEffect.Parameters mParams;
    private ShaftView mShaftView;
    private int mselectedEffect;
    private float xPosition = 0.0f;
    private float yPosition = 0.0f;
    private GestureDetector mGestures = null;
    private boolean mIsLongPressed = false;
    private TextView mScreenTitleView = null;

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.mCurrentView = obtainViewFromPool(R.layout.menu_ls_lightsource_settings);
        Context context = getActivity().getApplicationContext();
        this.mService = new BaseMenuService(context);
        this.mCurrentView.setOnTouchListener(this);
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        EffectSelectController.getInstance().setAngleChanging(true);
        String lastSavedOptionParams = LightShaftBackUpKey.getInstance().getLastSavedOptionSettings();
        AppLog.info(TAG, "TEST Option backup" + lastSavedOptionParams);
        this.mParams = ShaftsEffect.getInstance().getParameters();
        AppLog.info(TAG, "TEST Option mParamValue1>>>>1(before unflatten):" + this.mParams.flatten());
        this.mParams.unflatten(lastSavedOptionParams);
        AppLog.info(TAG, "TEST Option mParamValue1>>>>1(after unflatten):" + this.mParams.flatten());
        initializeViews();
        this.mShaftView.setParameters(this.mParams);
        this.mOverHeadView.setParameters(this.mParams);
        EffectSelectController.getInstance().setShootingEnable(false);
        setFooterguide();
        getLastStoredValues();
    }

    private void getLastStoredValues() {
        MenuDataParcelable parcelable = (MenuDataParcelable) this.data.getParcelable(MenuDataParcelable.KEY);
        if (!BACK.equals(parcelable.getItemId())) {
            mLastSavedOptionParams = LightShaftBackUpKey.getInstance().getLastSavedOptionSettings();
        }
    }

    private void setFooterguide() {
        this.mselectedEffect = ShaftsEffect.getInstance().getParameters().getEffect();
        switch (this.mselectedEffect) {
            case 3:
                this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), R.string.STRID_FOOTERGUIDE_ENTER_OPTION_RETURN, R.string.STRID_FOOTERGUIDE_ENTER_OPTION_RETURN_SK));
                return;
            default:
                this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), R.string.STRID_FOOTERGUIDE_ENTER_DIRECTION_OPTION_RETURN, R.string.STRID_FOOTERGUIDE_ENTER_DIRECTION_OPTION_RETURN_SK));
                return;
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        EffectSelectController.getInstance().setAngleChanging(false);
        deInitializeView();
        super.onPause();
    }

    private void initializeViews() {
        this.mShaftView = (ShaftView) this.mCurrentView.findViewById(R.id.light_shaft);
        this.mOverHeadView = (OverHeadShaftView) this.mCurrentView.findViewById(R.id.over_head_view);
        this.mScreenTitleView = (TextView) this.mCurrentView.findViewById(R.id.menu_screen_title);
        displaySelectedEffect();
        this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide);
        this.mGestures = new GestureDetector(getActivity().getApplicationContext(), new GestureListener());
        ApoWrapper.setApoType(ApoWrapper.APO_TYPE.SPECIAL);
        setKeyBeepPattern(0);
    }

    private void displaySelectedEffect() {
        this.mselectedEffect = ShaftsEffect.getInstance().getParameters().getEffect();
        switch (this.mselectedEffect) {
            case 3:
                this.mScreenTitleView.setText(getResources().getString(R.string.STRID_FUNC_ITEM_POSITION));
                return;
            default:
                this.mScreenTitleView.setText(getResources().getString(R.string.STRID_FUNC_ITEM_POSITION_DIRECTION));
                return;
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        if (event.getScanCode() == 595 || event.getScanCode() == 513) {
            return -1;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        if (event.getScanCode() == 595 || event.getScanCode() == 513) {
            return openOptionSettingLayout();
        }
        int ret = super.onKeyDown(keyCode, event);
        int scanCode = event.getScanCode();
        if (scanCode == 530 || scanCode != 532) {
        }
        switch (scanCode) {
            case AppRoot.USER_KEYCODE.UP /* 103 */:
            case AppRoot.USER_KEYCODE.LEFT /* 105 */:
            case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                KeyStatus status = ScalarInput.getKeyStatus(scanCode);
                if (1 == status.status) {
                    switch (scanCode) {
                        case AppRoot.USER_KEYCODE.UP /* 103 */:
                        case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
                            updateOSDonUpKeyPoint(null, event);
                            return ret;
                        case AppRoot.USER_KEYCODE.LEFT /* 105 */:
                            updateOSDonLeftKeyOperation(null, event);
                            return 1;
                        case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
                            updateOSDonRightKeyOperation(null, event);
                            return 1;
                        case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                            updateOSDonDownKeyPoint(null, event);
                            return ret;
                        default:
                            return ret;
                    }
                }
                return ret;
            case AppRoot.USER_KEYCODE.S2_ON /* 518 */:
                AppLog.info(TAG, "pushedS2On INVALID");
                return -1;
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
                int direction = this.mParams.getDirection() + 1;
                updateDirection(direction);
                if (ShaftsEffect.getInstance().getParameters().getEffect() == 3) {
                    return -1;
                }
                return ret;
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
                int direction2 = this.mParams.getDirection() - 1;
                updateDirection(direction2);
                if (ShaftsEffect.getInstance().getParameters().getEffect() == 3) {
                    return -1;
                }
                return ret;
            default:
                return ret;
        }
    }

    private void updateOSDonRightKeyOperation(PointF point, KeyEvent event) {
        PointF point2 = this.mParams.getOSDPoint();
        if (event.getRepeatCount() > 5) {
            point2.x += 30.0f;
        } else {
            point2.x += 10.0f;
        }
        updatePointPosition(point2);
    }

    private void updateOSDonLeftKeyOperation(PointF point, KeyEvent event) {
        PointF point2 = this.mParams.getOSDPoint();
        if (event.getRepeatCount() > 5) {
            point2.x -= 30.0f;
        } else {
            point2.x -= 10.0f;
        }
        updatePointPosition(point2);
    }

    private void updateOSDonDownKeyPoint(PointF point, KeyEvent event) {
        PointF point2 = this.mParams.getOSDPoint();
        if (event.getRepeatCount() > 5) {
            point2.y += 30.0f;
        } else {
            point2.y += 10.0f;
        }
        updatePointPosition(point2);
    }

    private void updateOSDonUpKeyPoint(PointF point, KeyEvent event) {
        PointF point2 = this.mParams.getOSDPoint();
        if (event.getRepeatCount() > 5) {
            point2.y -= 30.0f;
        } else {
            point2.y -= 10.0f;
        }
        updatePointPosition(point2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePointPosition(PointF point) {
        RectF limit = this.mParams.getLimitOSDPoint();
        if (!limit.contains(point.x, point.y)) {
            handleLimit(point, limit);
        }
        try {
            this.mParams.setOSDPoint(point);
        } catch (Exception e) {
            AppLog.info(TAG, "Range Over");
        }
        this.mShaftView.setParameters(this.mParams);
        this.mOverHeadView.setParameters(this.mParams);
    }

    private void handleLimit(PointF point, RectF limit) {
        if (ShaftsEffect.mRotation == 1) {
            if (point.x < limit.left) {
                point.x = limit.left - 1.0f;
                return;
            }
            if (point.x > limit.right) {
                point.x = limit.right;
                return;
            }
            if (point.y < limit.top) {
                point.y = limit.top;
                return;
            } else if (point.y > limit.bottom) {
                point.y = limit.bottom - 1.0f;
                return;
            } else {
                AppLog.error(TAG, "handleLimit point having out of limit values for VIEW_PATTERN_REVERSE_OSD180");
                return;
            }
        }
        if (point.x < limit.left) {
            point.x = limit.left;
            return;
        }
        if (point.x > limit.right) {
            point.x = limit.right - 1.0f;
            return;
        }
        if (point.y < limit.top) {
            point.y = limit.top;
        } else if (point.y > limit.bottom) {
            point.y = limit.bottom - 1.0f;
        } else {
            AppLog.error(TAG, "handleLimit point having out of limit valuesfor VIEW_PATTERN_OSDNORMAL");
        }
    }

    private void updateDirection(int direction) {
        try {
            this.mParams.setDirection(direction);
        } catch (Exception e) {
            AppLog.info(TAG, "Range Over");
        }
        this.mShaftView.setParameters(this.mParams);
        this.mOverHeadView.setParameters(this.mParams);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class GestureListener implements GestureDetector.OnGestureListener {
        private GestureListener() {
        }

        @Override // android.view.GestureDetector.OnGestureListener
        public boolean onDown(MotionEvent e) {
            AppLog.info(LightSourceSettingLayout.TAG, "INSIDE onDown");
            LightSourceSettingLayout.this.mIsLongPressed = false;
            LightSourceSettingLayout.this.xPosition = e.getX();
            LightSourceSettingLayout.this.yPosition = e.getY();
            return true;
        }

        @Override // android.view.GestureDetector.OnGestureListener
        public void onLongPress(MotionEvent e) {
            AppLog.info(LightSourceSettingLayout.TAG, "INSIDE onLongPress");
            LightSourceSettingLayout.this.mIsLongPressed = true;
            AppLog.info(LightSourceSettingLayout.TAG, "OUTSIDE onLongPress");
        }

        @Override // android.view.GestureDetector.OnGestureListener
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            DisplayManager.VideoRect eeRect = LightSourceSettingLayout.this.getEERect();
            boolean isXlessThanRight = LightSourceSettingLayout.this.xPosition <= ((float) eeRect.pxRight);
            boolean isYmoreThanTop = LightSourceSettingLayout.this.yPosition >= ((float) eeRect.pxTop);
            boolean isYlessThanBottom = LightSourceSettingLayout.this.yPosition <= ((float) eeRect.pxBottom);
            if (isXlessThanRight && isYmoreThanTop && isYlessThanBottom) {
                AppLog.info(LightSourceSettingLayout.TAG, "INSIDE onScroll");
                PointF point = LightSourceSettingLayout.this.mParams.getOSDPoint();
                point.x -= distanceX;
                point.y -= distanceY;
                LightSourceSettingLayout.this.updatePointPosition(point);
                AppLog.info(LightSourceSettingLayout.TAG, "OUTSIDE(): onScroll");
            }
            return true;
        }

        @Override // android.view.GestureDetector.OnGestureListener
        public void onShowPress(MotionEvent e) {
        }

        @Override // android.view.GestureDetector.OnGestureListener
        public boolean onSingleTapUp(MotionEvent e) {
            AppLog.info(LightSourceSettingLayout.TAG, "INSIDE onSingleTapUp");
            LightSourceSettingLayout.this.moveLightSourceOnTap();
            AppLog.info(LightSourceSettingLayout.TAG, "OUTSIDE: onSingleTapUp");
            return false;
        }

        @Override // android.view.GestureDetector.OnGestureListener
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }

    public void onAnimateMove(float totalDx, float totalDy, long l) {
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View v, MotionEvent event) {
        AppLog.info(TAG, "INSIDE onTouch " + event.getAction());
        boolean isTouched = false;
        if (this.mGestures != null) {
            isTouched = this.mGestures.onTouchEvent(event);
            if (event.getAction() == 1) {
                AppLog.info(TAG, "INSIDE ACTION UP");
                if (true == this.mIsLongPressed) {
                    moveLightSourceOnTap();
                }
            }
        }
        return isTouched;
    }

    public void moveLightSourceOnTap() {
        AppLog.info(TAG, "INSIDE: moveLightSourceOnTap");
        DisplayManager.VideoRect eeRect = getEERect();
        boolean isXlessThanRight = this.xPosition <= ((float) eeRect.pxRight);
        boolean isYmoreThanTop = this.yPosition >= ((float) eeRect.pxTop);
        boolean isYlessThanBottom = this.yPosition <= ((float) eeRect.pxBottom);
        if (isXlessThanRight && isYmoreThanTop && isYlessThanBottom) {
            AppLog.info(TAG, "Move");
            PointF point = this.mParams.getOSDPoint();
            point.x = this.xPosition;
            point.y = this.yPosition;
            updatePointPosition(point);
        } else {
            AppLog.info(TAG, "Do not Move");
        }
        AppLog.info(TAG, "OUTSIDE: moveLightSourceOnTap");
    }

    public DisplayManager.VideoRect getEERect() {
        DisplayModeObserver disp = DisplayModeObserver.getInstance();
        DisplayManager.VideoRect videoRect = (DisplayManager.VideoRect) disp.getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
        return videoRect;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    public void closeMenuLayout(Bundle bundle) {
        if (bundle == null) {
            updateParameter();
        }
        super.closeMenuLayout(bundle);
    }

    private void updateParameter() {
        ShaftsEffect.getInstance().setParameters(this.mParams);
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        closeMenuLayout(null);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedModeDial() {
        updateParameter();
        return super.turnedModeDial();
    }

    public int openOptionSettingLayout() {
        doItemClickProcessing("lightsource");
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDeleteKeyReleased(KeyEvent event) {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDeleteKeyPushed(KeyEvent event) {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedGuideFuncKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        this.mParams.unflatten(mLastSavedOptionParams);
        openPreviousMenu();
        return 1;
    }

    private void deInitializeView() {
        EffectSelectController.getInstance().setAngleChanging(false);
        this.mCurrentView = null;
        this.mShaftView = null;
        this.mOverHeadView = null;
        this.mGestures = null;
        this.mParams = null;
        this.mFooterGuide = null;
        this.mIsLongPressed = false;
        this.mScreenTitleView = null;
        this.mParams = null;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        deInitializeView();
        super.onDestroy();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        LightShaftBackUpKey.getInstance().saveOptionSettings(this.mParams.flatten());
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        KeyEvent event = new KeyEvent(0L, 0L, 0, 0, 0, 0, 0, AppRoot.USER_KEYCODE.UP);
        updateOSDonRightKeyOperation(null, event);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        KeyEvent event = new KeyEvent(0L, 0L, 0, 0, 0, 0, 0, AppRoot.USER_KEYCODE.DOWN);
        updateOSDonLeftKeyOperation(null, event);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        int direction = this.mParams.getDirection() + 1;
        updateDirection(direction);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        int direction = this.mParams.getDirection() - 1;
        updateDirection(direction);
        return 1;
    }
}
