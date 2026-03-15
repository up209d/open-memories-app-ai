package com.sony.imaging.app.graduatedfilter.menu.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.graduatedfilter.GFBackUpKey;
import com.sony.imaging.app.graduatedfilter.R;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.graduatedfilter.common.GFCommonUtil;
import com.sony.imaging.app.graduatedfilter.common.GFConstants;
import com.sony.imaging.app.graduatedfilter.common.GFKikiLogUtil;
import com.sony.imaging.app.graduatedfilter.common.SaUtil;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class GFIntroductionLayout extends Layout {
    private static final int PAGE1 = 0;
    private static final int PAGE2 = 1;
    private static final int PAGE3 = 2;
    private static final int PAGE4 = 3;
    private static final int PAGE5 = 4;
    private static final String TAG = AppLog.getClassName();
    private static View mCurrentView = null;
    private static ScrollView mScollView = null;
    private static int mCurrentPosition = 0;
    private static TextView mGuideText1 = null;
    private static TextView mGuideText2 = null;
    private static ImageView mGuideImage = null;
    private static ImageView mPointer1 = null;
    private static ImageView mPointer2 = null;
    private static ImageView mPointer3 = null;
    private static ImageView mPointer4 = null;
    private static ImageView mPointer5 = null;
    private static ImageView[] mPointer = null;
    private static int[] mImageVisibility = {0, 0, 0, 0, 8};
    private static boolean isShownIntroGuide = false;

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mCurrentView = obtainViewFromPool(R.layout.menu_introduction);
        mScollView = (ScrollView) mCurrentView.findViewById(R.id.scrollView);
        mGuideText1 = (TextView) mCurrentView.findViewById(R.id.guide_text1);
        mGuideText2 = (TextView) mCurrentView.findViewById(R.id.guide_text2);
        mGuideImage = (ImageView) mCurrentView.findViewById(R.id.guide_image);
        mPointer1 = (ImageView) mCurrentView.findViewById(R.id.guide_page1);
        mPointer2 = (ImageView) mCurrentView.findViewById(R.id.guide_page2);
        mPointer3 = (ImageView) mCurrentView.findViewById(R.id.guide_page3);
        mPointer4 = (ImageView) mCurrentView.findViewById(R.id.guide_page4);
        mPointer5 = (ImageView) mCurrentView.findViewById(R.id.guide_page5);
        mPointer = new ImageView[]{mPointer1, mPointer2, mPointer3, mPointer4, mPointer5};
        return mCurrentView;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        mCurrentPosition = 0;
        mScollView.scrollTo(0, 0);
        updateGuideView(mCurrentPosition);
        isShownIntroGuide = BackUpUtil.getInstance().getPreferenceBoolean(GFBackUpKey.KEY_SHOW_INTRODUCTION, false);
        GFKikiLogUtil.getInstance().countTipsShowing();
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        mPointer1 = null;
        mPointer2 = null;
        mPointer3 = null;
        mPointer4 = null;
        mPointer5 = null;
        mPointer = null;
        mGuideText1 = null;
        mGuideText2 = null;
        mGuideImage = null;
        mScollView = null;
        mCurrentView = null;
        BackUpUtil.getInstance().setPreference(GFBackUpKey.KEY_SHOW_INTRODUCTION, true);
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        System.gc();
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        CameraNotificationManager.getInstance().requestNotify(GFConstants.SHOW_STEP1_GUIDE);
        super.closeLayout();
    }

    private void updateGuideView(int position) {
        int imageId;
        mScollView.scrollTo(0, 0);
        ImageView[] arr$ = mPointer;
        for (ImageView img : arr$) {
            img.setImageResource(R.drawable.p_16_dd_parts_lvg_param_box_other_position_circle);
        }
        mPointer[position].setImageResource(R.drawable.p_16_dd_parts_lvg_param_box_current_position_circle);
        String step = getResources().getString(R.string.STRID_FUNC_SKYND_OPERATION_GUIDE_STEP);
        switch (position) {
            case 1:
                mGuideText1.setText(String.format(step, "1"));
                mGuideText2.setText(getResources().getString(R.string.STRID_FUNC_SKYND_INTRODUCTION_GUIDE_STEP1));
                imageId = R.drawable.p_16_dd_parts_hgf_shootingtips_step1;
                break;
            case 2:
                mGuideText1.setText(String.format(step, "2"));
                if (SaUtil.isAVIP()) {
                    mGuideText2.setText(getResources().getString(R.string.STRID_FUNC_SKYND_INTRODUCTION_GUIDE_STEP2_SK));
                } else {
                    mGuideText2.setText(getResources().getString(R.string.STRID_FUNC_SKYND_INTRODUCTION_GUIDE_STEP2));
                }
                mGuideText2.setVisibility(0);
                imageId = R.drawable.p_16_dd_parts_hgf_shootingtips_step2;
                break;
            case 3:
                mGuideText1.setText(String.format(step, "3"));
                mGuideText2.setText(getResources().getString(R.string.STRID_FUNC_SKYND_INTRODUCTION_GUIDE_STEP3));
                imageId = R.drawable.p_16_dd_parts_hgf_shootingtips_summary;
                break;
            case 4:
                mGuideText1.setText(getResources().getString(R.string.STRID_FUNC_SKYND_INTRODUCTION_GUIDE_TITLE_INFO));
                if (GFCommonUtil.getInstance().isDSC()) {
                    mGuideText2.setText(getResources().getString(R.string.STRID_FUNC_SKYND_INTRODUCTION_GUIDE_INFO_DSC));
                } else {
                    mGuideText2.setText(getResources().getString(R.string.STRID_FUNC_SKYND_INTRODUCTION_GUIDE_INFO));
                }
                imageId = R.drawable.p_16_dd_parts_hgf_shootingtips_summary;
                break;
            default:
                mGuideText1.setText("");
                mGuideText2.setText(getResources().getString(R.string.STRID_FUNC_SKYND_INTRODUCTION_GUIDE_OVERALL));
                imageId = R.drawable.p_16_dd_parts_hgf_shootingtips_summary;
                break;
        }
        mGuideImage.setImageResource(imageId);
        mGuideImage.setVisibility(mImageVisibility[position]);
    }

    private void prevPage() {
        mCurrentPosition--;
        if (mCurrentPosition < 0) {
            mCurrentPosition = mPointer.length - 1;
        }
        updateGuideView(mCurrentPosition);
    }

    private void nextPage() {
        mCurrentPosition++;
        if (mCurrentPosition > mPointer.length - 1) {
            mCurrentPosition = 0;
        }
        updateGuideView(mCurrentPosition);
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
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        AppLog.enter(TAG, AppLog.getMethodName());
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
            case AppRoot.USER_KEYCODE.LEFT /* 105 */:
                prevPage();
                break;
            case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
                nextPage();
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
            case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                closeLayout();
                if (isShownIntroGuide) {
                    returnStatus = 0;
                    break;
                } else {
                    returnStatus = 1;
                    break;
                }
            case AppRoot.USER_KEYCODE.SK1 /* 229 */:
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
            case AppRoot.USER_KEYCODE.MENU /* 514 */:
                closeLayout();
                break;
            case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                returnStatus = 0;
                break;
            case AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED /* 597 */:
                GFCommonUtil.getInstance().setFocusModeByDial();
                break;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return returnStatus;
    }
}
