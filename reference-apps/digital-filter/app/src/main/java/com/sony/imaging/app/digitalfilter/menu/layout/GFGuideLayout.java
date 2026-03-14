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
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomKeyMgr;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class GFGuideLayout extends Layout {
    public static final String ID_GFGUIDELAYOUT = "ID_GFGUIDELAYOUT";
    private static final String LINK_MSG = "LINK_MSG";
    private static final String LINK_MSG_EXPCOMP = "LINK_MSG_EXPCOMP";
    private static final String LINK_MSG_F5_6 = "LINK_MSG_F5_6";
    private static final String LINK_MSG_F8 = "LINK_MSG_F8";
    private static final String LINK_MSG_ISO100 = "LINK_MSG_ISO100";
    private static final String LINK_MSG_ISO125 = "LINK_MSG_ISO125";
    private static final String LINK_MSG_SS = "LINK_MSG_SS";
    private static final String LINK_MSG_WB = "LINK_MSG_WB";
    private static final String STEP1 = "STEP1";
    private static final String STEP2 = "STEP2";
    private static final String STEP3 = "STEP3";
    private static final String TAG = AppLog.getClassName();
    private static View mCurrentView = null;
    private static ScrollView mScollView = null;
    private static String mStep = null;
    private static TextView mGuideText = null;
    private static ImageView mGuideImage = null;
    private static ImageView mGuideImage2 = null;
    private static TextView mGuideText2 = null;
    private static TextView mGuideText3 = null;

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.cmn_guide);
        mScollView = (ScrollView) mCurrentView.findViewById(R.id.scrollView);
        mStep = this.data.getString(ID_GFGUIDELAYOUT);
        mGuideText = (TextView) mCurrentView.findViewById(R.id.guide_text);
        mGuideText2 = (TextView) mCurrentView.findViewById(R.id.guide_text2);
        mGuideText3 = (TextView) mCurrentView.findViewById(R.id.guide_text3);
        mGuideImage = (ImageView) mCurrentView.findViewById(R.id.guide_image);
        mGuideImage2 = (ImageView) mCurrentView.findViewById(R.id.guide_image2);
        return mCurrentView;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AELController.getInstance().cancelAELock();
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
            CameraNotificationManager.getInstance().requestNotify(GFConstants.CLOSE_GUIDE);
        } else if (!STEP3.equalsIgnoreCase(mStep)) {
            if (LINK_MSG_EXPCOMP.equalsIgnoreCase(mStep)) {
                BackUpUtil.getInstance().setPreference(GFBackUpKey.KEY_SHOW_EXPCOMP_LINK_MSG, true);
            } else if (LINK_MSG_F8.equalsIgnoreCase(mStep) || LINK_MSG_F5_6.equalsIgnoreCase(mStep)) {
                BackUpUtil.getInstance().setPreference(GFBackUpKey.KEY_SHOW_FNO_LINK_MSG, true);
            } else if (LINK_MSG_SS.equalsIgnoreCase(mStep)) {
                BackUpUtil.getInstance().setPreference(GFBackUpKey.KEY_SHOW_SS_LINK_MSG, true);
            } else if (LINK_MSG_ISO100.equalsIgnoreCase(mStep) || LINK_MSG_ISO125.equalsIgnoreCase(mStep)) {
                BackUpUtil.getInstance().setPreference(GFBackUpKey.KEY_SHOW_ISO_LINK_MSG, true);
            } else if (LINK_MSG_WB.equalsIgnoreCase(mStep)) {
                BackUpUtil.getInstance().setPreference(GFBackUpKey.KEY_SHOW_WB_LINK_MSG, true);
            }
        }
        mGuideText = null;
        mGuideText2 = null;
        mGuideText3 = null;
        mGuideImage = null;
        mGuideImage2 = null;
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

    @Override // com.sony.imaging.app.fw.KeyReceiver
    protected String getKeyConvCategory() {
        return "Menu";
    }

    private void updateGuideView() {
        if (STEP1.equalsIgnoreCase(mStep)) {
            String text = getResources().getString(R.string.STRID_FUNC_DF_STEP1_FIRST) + ExposureModeController.SOFT_SNAP;
            mGuideText.setText(text + getResources().getString(R.string.STRID_FUNC_DF_STEP1_NEXT));
            mGuideText2.setVisibility(8);
            mGuideText3.setVisibility(8);
            mGuideImage.setImageResource(R.drawable.p_16_dd_parts_hgf_shootingtips_step1);
            mGuideImage.setVisibility(0);
            mGuideImage2.setVisibility(8);
            return;
        }
        if (STEP2.equalsIgnoreCase(mStep)) {
            String text2 = getResources().getString(R.string.STRID_FUNC_DF_STEP2);
            mGuideText.setText(text2);
            mGuideText2.setVisibility(8);
            mGuideText3.setVisibility(8);
            mGuideImage.setImageResource(R.drawable.p_16_dd_parts_hgf_shootingtips_step2);
            mGuideImage.setVisibility(0);
            mGuideImage2.setVisibility(8);
            return;
        }
        if (STEP3.equalsIgnoreCase(mStep)) {
            mGuideText2.setVisibility(8);
            mGuideText3.setVisibility(8);
            mGuideImage.setImageResource(R.drawable.p_16_dd_parts_hgf_shootingtips_summary);
            mGuideImage.setVisibility(0);
            mGuideImage2.setVisibility(8);
            return;
        }
        if (mStep.contains(LINK_MSG)) {
            String text3 = getResources().getString(R.string.STRID_FUNC_DF_LINK_MSG);
            mGuideText.setText(text3);
            mGuideText2.setVisibility(0);
            mGuideText3.setVisibility(0);
            if (LINK_MSG_EXPCOMP.equalsIgnoreCase(mStep)) {
                mGuideImage2.setImageResource(R.drawable.p_16_dd_parts_df_link_info_expcomp);
            } else if (LINK_MSG_F8.equalsIgnoreCase(mStep)) {
                mGuideImage2.setImageResource(R.drawable.p_16_dd_parts_df_link_info_f8);
            } else if (LINK_MSG_F5_6.equalsIgnoreCase(mStep)) {
                mGuideImage2.setImageResource(R.drawable.p_16_dd_parts_df_link_info_f5_6);
            } else if (LINK_MSG_SS.equalsIgnoreCase(mStep)) {
                mGuideImage2.setImageResource(R.drawable.p_16_dd_parts_df_link_info_ss);
            } else if (LINK_MSG_ISO100.equalsIgnoreCase(mStep)) {
                mGuideImage2.setImageResource(R.drawable.p_16_dd_parts_df_link_info_iso100);
            } else if (LINK_MSG_ISO125.equalsIgnoreCase(mStep)) {
                mGuideImage2.setImageResource(R.drawable.p_16_dd_parts_df_link_info_iso125);
            } else if (LINK_MSG_WB.equalsIgnoreCase(mStep)) {
                mGuideImage2.setImageResource(R.drawable.p_16_dd_parts_df_link_info_wb);
            }
            mGuideImage2.setVisibility(0);
            mGuideImage.setVisibility(8);
        }
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
            return 1;
        }
        int code = event.getScanCode();
        ICustomKey key = CustomKeyMgr.getInstance().get(code);
        IKeyFunction convKey = key.getAssigned("Menu");
        if (CustomizableFunction.AelHold.equals(convKey)) {
            if (!AELController.getInstance().isAELockByAELHold()) {
                return 0;
            }
            AELController.getInstance().holdAELock(false);
            return 0;
        }
        if (CustomizableFunction.AfMfHold.equals(convKey)) {
            if (!FocusModeController.getInstance().isAfMfControlHold()) {
                return 0;
            }
            FocusModeController.getInstance().holdFocusControl(false);
            return 0;
        }
        if (!CustomizableFunction.FocusHold.equals(convKey) || !CameraSetting.getInstance().isFocusHold()) {
            return 0;
        }
        CameraSetting.getInstance().stopFocusHold();
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
        AppLog.exit(TAG, AppLog.getMethodName());
        return returnStatus;
    }
}
