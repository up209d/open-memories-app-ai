package com.sony.imaging.app.startrails.shooting.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.startrails.R;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.scalar.sysutil.didep.Kikilog;

/* loaded from: classes.dex */
public class IntroductoryLayout extends Layout {
    private final String TAG = AppLog.getClassName();
    protected ViewGroup mCurrentLayout = null;
    protected View mMainView = null;
    private ScrollView mScollView = null;

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onCreateView(inflater, container, savedInstanceState);
        createView();
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return this.mCurrentLayout;
    }

    private void printTestShotKikiLog() {
        Kikilog.Options options = new Kikilog.Options();
        options.getClass();
        options.recType = 32;
        Integer kikilogId = 4243;
        Kikilog.setUserLog(kikilogId.intValue(), options);
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onResume();
        if (this.mScollView != null) {
            this.mScollView.scrollTo(0, 0);
        }
        this.mScollView.setOnTouchListener(new View.OnTouchListener() { // from class: com.sony.imaging.app.startrails.shooting.layout.IntroductoryLayout.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        STUtility.getInstance().setIsEEStateBoot(false);
        printTestShotKikiLog();
        ApoWrapper.setApoType(ApoWrapper.APO_TYPE.SPECIAL);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void onReopened() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        updateLayout(2);
        super.onReopened();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onDestroyView();
        detachView();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private void createView() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        detachView();
        this.mCurrentLayout = new RelativeLayout(getActivity());
        if (-1 != R.layout.st_layout_menu_introductory_guide) {
            this.mMainView = obtainViewFromPool(R.layout.st_layout_menu_introductory_guide);
            this.mCurrentLayout.addView(this.mMainView);
        }
        this.mScollView = (ScrollView) this.mMainView.findViewById(R.id.scrollView);
        this.mScollView.setScrollbarFadingEnabled(false);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private void detachView() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (this.mMainView != null) {
            if (this.mCurrentLayout != null) {
                this.mCurrentLayout.removeView(this.mMainView);
            }
            this.mMainView = null;
        }
        this.mCurrentLayout = null;
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        int returnStatus = 1;
        int scanCode = event.getScanCode();
        switch (scanCode) {
            case 103:
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
            case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
                pushedUpKey();
                break;
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
            case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                pushedDownKey();
                break;
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                closeLayout();
                break;
            case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
            case AppRoot.USER_KEYCODE.S2_ON /* 518 */:
            case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
            case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                closeLayout();
                returnStatus = 0;
                break;
            default:
                AppLog.info(this.TAG, "Key is Invalid");
                break;
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return returnStatus;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        event.getScanCode();
        super.onKeyUp(keyCode, event);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        return pushedUpKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        return pushedDownKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        return pushedUpKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        return pushedDownKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedShuttleToLeft() {
        return pushedDownKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedShuttleToRight() {
        return pushedUpKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mScollView.smoothScrollBy(0, -20);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mScollView.smoothScrollBy(0, 20);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        ApoWrapper.setApoType(ApoWrapper.APO_TYPE.NORMAL);
        super.closeLayout();
        releaseResources();
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        releaseResources();
    }

    private void releaseResources() {
        if (this.mScollView != null) {
            this.mScollView.destroyDrawingCache();
            this.mScollView = null;
        }
        if (this.mMainView != null) {
            this.mMainView.destroyDrawingCache();
            this.mMainView = null;
        }
        if (this.mCurrentLayout != null) {
            this.mCurrentLayout.destroyDrawingCache();
            this.mCurrentLayout = null;
        }
    }
}
