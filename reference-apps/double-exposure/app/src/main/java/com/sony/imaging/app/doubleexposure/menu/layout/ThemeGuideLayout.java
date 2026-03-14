package com.sony.imaging.app.doubleexposure.menu.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.layout.BaseMenuLayout;
import com.sony.imaging.app.doubleexposure.R;
import com.sony.imaging.app.doubleexposure.common.AppContext;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureUtil;
import com.sony.imaging.app.doubleexposure.menu.controller.ThemeSelectionController;
import com.sony.imaging.app.doubleexposure.shooting.state.DoubleExposureShootingMenuState;

/* loaded from: classes.dex */
public class ThemeGuideLayout extends BaseMenuLayout {
    private final String TAG = AppLog.getClassName();
    private View mCurrentView = null;
    private FooterGuide mFooterGuide = null;
    private TextView mTxtVwScreenTitle = null;
    private ScrollView mScollView = null;
    private boolean mIsTurnedEVDial = false;

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mCurrentView = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        this.mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.theme_selection_guide);
        this.mTxtVwScreenTitle = (TextView) this.mCurrentView.findViewById(R.id.menu_screen_title);
        this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide);
        this.mScollView = (ScrollView) this.mCurrentView.findViewById(R.id.scrollView);
        this.mScollView.setOnTouchListener(new View.OnTouchListener() { // from class: com.sony.imaging.app.doubleexposure.menu.layout.ThemeGuideLayout.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onResume();
        if (this.mScollView != null) {
            this.mScollView.scrollTo(0, 0);
        }
        this.mFooterGuide.setData(new FooterGuideDataResId(AppContext.getAppContext(), android.R.string.years));
        String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
        updateTitle(selectedTheme);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    public void updateTitle(String selectedTheme) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        int stringID = -1;
        if (selectedTheme.equalsIgnoreCase(ThemeSelectionController.SKY)) {
            stringID = R.string.STRID_FUNC_MLE_SKY;
        } else if (selectedTheme.equalsIgnoreCase(ThemeSelectionController.SOFTFILTER)) {
            stringID = R.string.STRID_FUNC_MLE_SOFTFILTER;
        } else if (selectedTheme.equalsIgnoreCase(ThemeSelectionController.SIL)) {
            stringID = R.string.STRID_FUNC_MLE_SIL;
        } else if (selectedTheme.equalsIgnoreCase(ThemeSelectionController.TEXTURE)) {
            stringID = R.string.STRID_FUNC_MLE_TEXTURE;
        } else if (selectedTheme.equalsIgnoreCase("Rotation")) {
            stringID = R.string.STRID_FUNC_MLE_ROTATE;
        } else if (selectedTheme.equalsIgnoreCase(ThemeSelectionController.MIRROR)) {
            stringID = R.string.STRID_FUNC_MLE_MIRROR;
        }
        this.mTxtVwScreenTitle.setText(stringID);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mFooterGuide = null;
        this.mScollView = null;
        this.mTxtVwScreenTitle = null;
        this.mCurrentView = null;
        super.onDestroy();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mScollView.smoothScrollBy(0, 20);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mScollView.smoothScrollBy(0, -20);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        openMenuLayout(DoubleExposureShootingMenuState.ID_THEMESELECTIONMENULAYOUT, this.data);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return super.pushedCenterKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        openMenuLayout(DoubleExposureShootingMenuState.ID_THEMESELECTIONMENULAYOUT, this.data);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return super.pushedCenterKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfToggleCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mIsTurnedEVDial = true;
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return super.turnedEVDial();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (this.mIsTurnedEVDial) {
            DoubleExposureUtil.getInstance().updateExposureCompensation();
            this.mIsTurnedEVDial = false;
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        if (event.getScanCode() == 595 || event.getScanCode() == 513) {
            return 1;
        }
        int returnState = super.onKeyUp(keyCode, event);
        return returnState;
    }
}
