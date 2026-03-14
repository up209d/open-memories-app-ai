package com.sony.imaging.app.digitalfilter.menu.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.sony.imaging.app.base.shooting.camera.AELController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.common.GFKikiLogUtil;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFWhiteBalanceController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomKeyMgr;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class GFIntroductionLayout extends Layout {
    private static final int PAGE1 = 0;
    private static final int PAGE2 = 1;
    private static final int PAGE3 = 2;
    private static final int PAGE4 = 3;
    private static final int PAGE5 = 4;
    private static final int PAGE6 = 5;
    private static final int PAGE7 = 6;
    private static final String TAG = AppLog.getClassName();
    private static View mCurrentView = null;
    private static ScrollView mScollView = null;
    private static int mCurrentPosition = 0;
    private static TextView mGuideText1 = null;
    private static TextView mGuideText2 = null;
    private static TextView mGuideText3 = null;
    private static TextView mGuideText4 = null;
    private static ImageView mGuideImage1 = null;
    private static ImageView mGuideImage2 = null;
    private static ImageView mGuideImage3 = null;
    private static ImageView mPointer1 = null;
    private static ImageView mPointer2 = null;
    private static ImageView mPointer3 = null;
    private static ImageView mPointer4 = null;
    private static ImageView mPointer5 = null;
    private static ImageView mPointer6 = null;
    private static ImageView mPointer7 = null;
    private static ImageView[] mPointer = null;
    private static View[] mViewArray = null;
    private static boolean isShownIntroGuide = false;

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mCurrentView = obtainViewFromPool(R.layout.menu_introduction);
        mScollView = (ScrollView) mCurrentView.findViewById(R.id.scrollView);
        mGuideText1 = (TextView) mCurrentView.findViewById(R.id.guide_text1);
        mGuideText2 = (TextView) mCurrentView.findViewById(R.id.guide_text2);
        mGuideText3 = (TextView) mCurrentView.findViewById(R.id.guide_text3);
        mGuideText4 = (TextView) mCurrentView.findViewById(R.id.guide_text4);
        mGuideImage1 = (ImageView) mCurrentView.findViewById(R.id.guide_image1);
        mGuideImage2 = (ImageView) mCurrentView.findViewById(R.id.guide_image2);
        mGuideImage3 = (ImageView) mCurrentView.findViewById(R.id.guide_image3);
        mPointer1 = (ImageView) mCurrentView.findViewById(R.id.guide_page1);
        mPointer2 = (ImageView) mCurrentView.findViewById(R.id.guide_page2);
        mPointer3 = (ImageView) mCurrentView.findViewById(R.id.guide_page3);
        mPointer4 = (ImageView) mCurrentView.findViewById(R.id.guide_page4);
        mPointer5 = (ImageView) mCurrentView.findViewById(R.id.guide_page5);
        mPointer6 = (ImageView) mCurrentView.findViewById(R.id.guide_page6);
        mPointer7 = (ImageView) mCurrentView.findViewById(R.id.guide_page7);
        mPointer = new ImageView[]{mPointer1, mPointer2, mPointer3, mPointer4, mPointer5, mPointer6, mPointer7};
        mViewArray = new View[]{mScollView, mGuideText1, mGuideText2, mGuideText3, mGuideText4, mGuideImage1, mGuideImage2, mGuideImage3, mPointer1, mPointer2, mPointer3, mPointer4, mPointer5, mPointer6, mPointer7};
        return mCurrentView;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        makeViewsUnfocusable();
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
        mPointer6 = null;
        mPointer7 = null;
        mPointer = null;
        mGuideText1 = null;
        mGuideText2 = null;
        mGuideText3 = null;
        mGuideText4 = null;
        mGuideImage1 = null;
        mGuideImage2 = null;
        mGuideImage3 = null;
        mScollView = null;
        mCurrentView = null;
        mViewArray = null;
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

    private void makeViewsUnfocusable() {
        if (getView() != null) {
            getView().setFocusable(false);
            getView().setFocusableInTouchMode(false);
        }
        for (int i = 0; i < mViewArray.length - 1; i++) {
            mViewArray[i].setFocusable(false);
            mViewArray[i].setFocusableInTouchMode(false);
        }
    }

    private void updateGuideView(int position) {
        String text;
        int imageId1 = 0;
        int imageId2 = 0;
        int imageId3 = 0;
        mScollView.scrollTo(0, 0);
        ImageView[] arr$ = mPointer;
        for (ImageView img : arr$) {
            img.setImageResource(R.drawable.p_16_dd_parts_lvg_param_box_other_position_circle);
        }
        mPointer[position].setImageResource(R.drawable.p_16_dd_parts_lvg_param_box_current_position_circle);
        switch (position) {
            case 1:
                mGuideText1.setText("");
                mGuideText2.setText(((getResources().getString(R.string.STRID_FUNC_DF_INFO2_OVERALL) + "\n\n" + getResources().getString(R.string.STRID_FUNC_DF_INFO2_FILTERSET)) + "\n" + getResources().getString(R.string.STRID_FUNC_DF_INFO2_3RD_AREA)) + "\n\n" + getResources().getString(R.string.STRID_FUNC_DF_INFO2_NEXT));
                mGuideText3.setText("");
                mGuideText4.setText("");
                imageId1 = R.drawable.p_16_dd_parts_hgf_shootingtips_summary;
                break;
            case 2:
                mGuideText1.setText(R.string.STRID_FUNC_SKYND_OPERATION_STEP1);
                mGuideText2.setText(R.string.STRID_FUNC_DF_INFO_STEP1_FLOW);
                mGuideText3.setText("");
                mGuideText4.setText("");
                imageId1 = R.drawable.p_16_dd_parts_hgf_shootingtips_step1;
                break;
            case 3:
                mGuideText1.setText(R.string.STRID_FUNC_SKYND_OPERATION_STEP2);
                mGuideText2.setText(R.string.STRID_FUNC_DF_INFO_STEP2_FLOW);
                mGuideText3.setText("");
                mGuideText4.setText("");
                imageId1 = R.drawable.p_16_dd_parts_hgf_shootingtips_step2;
                break;
            case 4:
                mGuideText1.setText(R.string.STRID_FUNC_SKYND_OPERATION_STEP3);
                mGuideText2.setText(R.string.STRID_FUNC_DF_INFO_STEP3_FLOW);
                mGuideText3.setText("");
                mGuideText4.setText("");
                imageId1 = R.drawable.p_16_dd_parts_hgf_shootingtips_summary;
                break;
            case 5:
                mGuideText1.setText(R.string.STRID_FUNC_DF_INFO_SHOOTING_SCREEN_TITLE);
                String text2 = (((((getResources().getString(R.string.STRID_FUNC_DF_INFO_SHOOTING_SCREEN_OPERATION) + "\n\n" + getResources().getString(R.string.STRID_FUNC_DF_INFO_AREA_SET_TITLE)) + "\n" + getResources().getString(R.string.STRID_FUNC_DF_INFO_AREA_SET_OPERATION1)) + "\n" + getResources().getString(R.string.STRID_FUNC_DF_INFO_AREA_SET_OPERATION2)) + "\n" + getResources().getString(R.string.STRID_FUNC_DF_INFO_AREA_SET_OPERATION3)) + "\n" + getResources().getString(R.string.STRID_FUNC_DF_INFO_AREA_SET_OPERATION4)) + "\n" + getResources().getString(R.string.STRID_FUNC_DF_INFO_AREA_SET_OPERATION5);
                if (GFWhiteBalanceController.getInstance().isSupportedABGM()) {
                    text = text2 + "\n" + getResources().getString(R.string.STRID_FUNC_DF_INFO_AREA_SET_OPERATION6);
                } else {
                    text = text2 + "\n" + getResources().getString(R.string.STRID_FUNC_DF_INFO_AREA_SET_OPERATION6_S);
                }
                mGuideText2.setText(text);
                mGuideText3.setText("");
                mGuideText4.setText("");
                break;
            case 6:
                mGuideText1.setText(getResources().getString(R.string.STRID_FUNC_SKYND_INTRODUCTION_GUIDE_TITLE_INFO));
                if (GFCommonUtil.getInstance().isDSC()) {
                    mGuideText2.setText(getResources().getString(R.string.STRID_FUNC_SKYND_INTRODUCTION_GUIDE_INFO_DSC));
                } else {
                    mGuideText2.setText(getResources().getString(R.string.STRID_FUNC_SKYND_INTRODUCTION_GUIDE_INFO));
                }
                mGuideText3.setText("");
                mGuideText4.setText("");
                break;
            default:
                mGuideText1.setText((((getResources().getString(R.string.STRID_FUNC_DF_INFO1_OVERALL) + "\n" + getResources().getString(R.string.STRID_FUNC_DF_INFO1_EX1)) + "\n" + getResources().getString(R.string.STRID_FUNC_DF_INFO1_EX2)) + "\n" + getResources().getString(R.string.STRID_FUNC_DF_INFO1_EX3)) + "\n" + getResources().getString(R.string.STRID_FUNC_DF_INFO1_EX4));
                mGuideText2.setText(getResources().getString(R.string.STRID_FUNC_DF_INFO1_SCEAN));
                mGuideText3.setText(getResources().getString(R.string.STRID_FUNC_DF_INFO1_AREA));
                mGuideText4.setText(getResources().getString(R.string.STRID_FUNC_DF_INFO1_SHOT));
                imageId2 = R.drawable.p_16_dd_parts_hgf_shootingtips_summary;
                imageId3 = R.drawable.p_16_dd_parts_hgf_shootingtips_areaimg;
                break;
        }
        if (imageId1 != 0) {
            mGuideImage1.setImageResource(imageId1);
            mGuideImage1.setVisibility(0);
        } else {
            mGuideImage1.setVisibility(8);
        }
        if (imageId2 != 0) {
            mGuideImage2.setImageResource(imageId2);
            mGuideImage2.setVisibility(0);
        } else {
            mGuideImage2.setVisibility(8);
        }
        if (imageId3 != 0) {
            mGuideImage3.setImageResource(imageId3);
            mGuideImage3.setVisibility(0);
        } else {
            mGuideImage3.setVisibility(8);
        }
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
        if (530 == scanCode || 786 == scanCode) {
            closeLayout();
            return 0;
        }
        int code = event.getScanCode();
        ICustomKey key = CustomKeyMgr.getInstance().get(code);
        IKeyFunction convKey = key.getAssigned("Menu");
        if (CustomizableFunction.AelHold.equals(convKey)) {
            if (AELController.getInstance().isAELockByAELHold()) {
                AELController.getInstance().holdAELock(false);
            }
        } else if (CustomizableFunction.AfMfHold.equals(convKey)) {
            if (FocusModeController.getInstance().isAfMfControlHold()) {
                FocusModeController.getInstance().holdFocusControl(false);
            }
        } else if (CustomizableFunction.FocusHold.equals(convKey) && CameraSetting.getInstance().isFocusHold()) {
            CameraSetting.getInstance().stopFocusHold();
        }
        return -1;
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
