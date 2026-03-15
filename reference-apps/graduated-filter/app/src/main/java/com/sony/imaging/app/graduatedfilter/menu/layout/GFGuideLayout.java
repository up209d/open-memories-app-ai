package com.sony.imaging.app.graduatedfilter.menu.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.graduatedfilter.GFBackUpKey;
import com.sony.imaging.app.graduatedfilter.R;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.graduatedfilter.common.GFCommonUtil;
import com.sony.imaging.app.graduatedfilter.common.SaUtil;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class GFGuideLayout extends Layout {
    public static final String ID_GFGUIDELAYOUT = "ID_GFGUIDELAYOUT";
    private static final String STEP1 = "STEP1";
    private static final String STEP2 = "STEP2";
    private static final String STEP3 = "STEP3";
    private final String TAG = AppLog.getClassName();
    private static View mCurrentView = null;
    private static ScrollView mScollView = null;
    private static String mStep = null;
    private static TextView mGuideText = null;
    private static ImageView mGuideImage = null;

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.cmn_guide);
        mScollView = (ScrollView) mCurrentView.findViewById(R.id.scrollView);
        mStep = this.data.getString(ID_GFGUIDELAYOUT);
        mGuideText = (TextView) mCurrentView.findViewById(R.id.guide_text);
        mGuideImage = (ImageView) mCurrentView.findViewById(R.id.guide_image);
        return mCurrentView;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        mScollView.scrollTo(0, 0);
        updateGuideView();
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (STEP1.equalsIgnoreCase(mStep)) {
            boolean isShownIntroGuide = BackUpUtil.getInstance().getPreferenceBoolean(GFBackUpKey.KEY_SHOW_INTRODUCTION, false);
            if (isShownIntroGuide) {
                BackUpUtil.getInstance().setPreference(GFBackUpKey.KEY_SHOW_STEP1GUIDE, true);
            }
        } else if (STEP2.equalsIgnoreCase(mStep)) {
            BackUpUtil.getInstance().setPreference(GFBackUpKey.KEY_SHOW_STEP2GUIDE, true);
        } else if (STEP3.equalsIgnoreCase(mStep)) {
        }
        mGuideText = null;
        mGuideImage = null;
        mStep = null;
        mScollView = null;
        mCurrentView = null;
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        System.gc();
    }

    private void updateGuideView() {
        getResources().getString(R.string.STRID_FUNC_SKYND_OPERATION_GUIDE_STEP);
        if (STEP1.equalsIgnoreCase(mStep)) {
            if (SaUtil.isAVIP()) {
                mGuideText.setText(R.string.STRID_FUNC_SKYND_OPERATION_GUIDE_STEP1_SK);
            } else {
                mGuideText.setText(R.string.STRID_FUNC_SKYND_OPERATION_GUIDE_STEP1);
            }
            mGuideImage.setImageResource(R.drawable.p_16_dd_parts_hgf_shootingtips_step1);
            return;
        }
        if (STEP2.equalsIgnoreCase(mStep)) {
            mGuideText.setText(R.string.STRID_FUNC_SKYND_OPERATION_GUIDE_STEP2_L);
            mGuideImage.setImageResource(R.drawable.p_16_dd_parts_hgf_shootingtips_step2);
        } else if (STEP3.equalsIgnoreCase(mStep)) {
            mGuideImage.setImageResource(R.drawable.p_16_dd_parts_hgf_shootingtips_summary);
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyUp(KeyEvent event, IKeyFunction func) {
        int scanCode = event.getScanCode();
        if (530 != scanCode && 786 != scanCode) {
            return -1;
        }
        closeLayout();
        return 1;
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
                mScollView.smoothScrollBy(0, -20);
                break;
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
            case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                mScollView.smoothScrollBy(0, 20);
                break;
            case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
            case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
            case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
            case AppRoot.USER_KEYCODE.S2_ON /* 518 */:
            case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
            case AppRoot.USER_KEYCODE.IR_SHUTTER /* 552 */:
            case AppRoot.USER_KEYCODE.IR_SHUTTER_2SEC /* 553 */:
            case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
            case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                closeLayout();
                returnStatus = 0;
                break;
            case AppRoot.USER_KEYCODE.SK1 /* 229 */:
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
            case AppRoot.USER_KEYCODE.MENU /* 514 */:
                closeLayout();
                break;
            case AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED /* 597 */:
                GFCommonUtil.getInstance().setFocusModeByDial();
                break;
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return returnStatus;
    }
}
