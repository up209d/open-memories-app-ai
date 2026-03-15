package com.sony.imaging.app.lightgraffiti.shooting.layout;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.lightgraffiti.R;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.lightgraffiti.util.AppLog;
import com.sony.imaging.app.lightgraffiti.util.LGConstants;
import com.sony.imaging.app.lightgraffiti.util.LGUtility;
import com.sony.imaging.app.util.ApoWrapper;

/* loaded from: classes.dex */
public class LGEEGuideDetailLayout extends Layout {
    private final String TAG = LGEEGuideDetailLayout.class.getSimpleName();
    protected ViewGroup mCurrentLayout = null;
    protected View mMainView = null;
    private ScrollView mScollView = null;
    private ImageView mGuideHeaderImageView = null;
    private TextView mGuideTextView = null;
    private ImageView mGuideFooterImageView = null;

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        createView();
        return this.mCurrentLayout;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        if (this.mScollView != null) {
            this.mScollView.scrollTo(0, 0);
        }
        this.mScollView.setOnTouchListener(new View.OnTouchListener() { // from class: com.sony.imaging.app.lightgraffiti.shooting.layout.LGEEGuideDetailLayout.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        String stage = LGStateHolder.getInstance().getShootingStage();
        if (LGStateHolder.SHOOTING_1ST.equals(stage)) {
            this.mGuideHeaderImageView.setImageResource(R.drawable.p_16_dd_parts_lightgraffiti_shooting_guide_1st_header_illust);
            this.mGuideTextView.setText(R.string.STRID_FUNC_LIGHTGRAFFITI_GUIDE_CREATIVE_SHOOTING_1ST_W_APO_CAUTION);
            this.mGuideFooterImageView.setImageResource(R.drawable.p_16_dd_parts_lightgraffiti_shooting_guide_1st_footer_image);
            ApoWrapper.setApoType(ApoWrapper.APO_TYPE.SPECIAL);
            LGUtility.getInstance().outputKikilog(LGConstants.KIKILOG_ID_GUIDE_SHOWN_1ST);
            return;
        }
        if (LGStateHolder.SHOOTING_2ND.equals(stage)) {
            this.mGuideHeaderImageView.setImageResource(R.drawable.p_16_dd_parts_lightgraffiti_shooting_guide_2nd_header_illust);
            this.mGuideTextView.setText(R.string.STRID_FUNC_LIGHTGRAFFITI_GUIDE_CREATIVE_SHOOTING_2ND_W_APO_CAUTION_AND_ERROR_CASE);
            this.mGuideFooterImageView.setImageResource(R.drawable.p_16_dd_parts_lightgraffiti_shooting_guide_2nd_footer_image);
            ApoWrapper.setApoType(ApoWrapper.APO_TYPE.NONE);
            LGUtility.getInstance().outputKikilog(LGConstants.KIKILOG_ID_GUIDE_SHOWN_2ND);
            return;
        }
        if (LGStateHolder.getInstance().isShootingStage3rd()) {
            this.mGuideHeaderImageView.setImageResource(R.drawable.p_16_dd_parts_lightgraffiti_shooting_guide_3rd_header_illust);
            this.mGuideTextView.setText(R.string.STRID_FUNC_LIGHTGRAFFITI_GUIDE_CREATIVE_SHOOTING_3RD_W_APO_CAUTION);
            this.mGuideFooterImageView.setImageResource(R.drawable.p_16_dd_parts_lightgraffiti_shooting_guide_3rd_footer_image);
            ApoWrapper.setApoType(ApoWrapper.APO_TYPE.NONE);
            LGUtility.getInstance().outputKikilog(LGConstants.KIKILOG_ID_GUIDE_SHOWN_3RD);
            return;
        }
        Log.e(this.TAG, AppLog.getMethodName() + " Unexpected!");
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void onReopened() {
        updateLayout(2);
        super.onReopened();
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        detachView();
    }

    private void createView() {
        detachView();
        this.mCurrentLayout = new RelativeLayout(getActivity());
        if (-1 != R.layout.lightgraffiti_layout_ee_guide_detail) {
            this.mMainView = obtainViewFromPool(R.layout.lightgraffiti_layout_ee_guide_detail);
            this.mCurrentLayout.addView(this.mMainView);
        }
        this.mScollView = (ScrollView) this.mMainView.findViewById(R.id.scrollView);
        this.mScollView.setScrollbarFadingEnabled(false);
        this.mGuideHeaderImageView = (ImageView) this.mMainView.findViewById(R.id.guide_header_image);
        this.mGuideTextView = (TextView) this.mMainView.findViewById(R.id.guide_text);
        this.mGuideFooterImageView = (ImageView) this.mMainView.findViewById(R.id.guide_footer_image);
    }

    private void detachView() {
        if (this.mMainView != null) {
            if (this.mCurrentLayout != null) {
                this.mCurrentLayout.removeView(this.mMainView);
            }
            this.mMainView = null;
        }
        this.mCurrentLayout = null;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int scanCode = event.getScanCode();
        switch (scanCode) {
            case AppRoot.USER_KEYCODE.UP /* 103 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
            case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
                pushedUpKey();
                return 1;
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
            case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                pushedDownKey();
                return 1;
            case AppRoot.USER_KEYCODE.SK1 /* 229 */:
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
            case AppRoot.USER_KEYCODE.SK2 /* 513 */:
            case AppRoot.USER_KEYCODE.MENU /* 514 */:
            case AppRoot.USER_KEYCODE.IR_SHUTTER /* 552 */:
            case AppRoot.USER_KEYCODE.IR_SHUTTER_2SEC /* 553 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                closeLayout();
                return 1;
            case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
            case AppRoot.USER_KEYCODE.UM_MOVIE_REC /* 616 */:
            case AppRoot.USER_KEYCODE.IR_MOVIE_REC /* 643 */:
                closeLayout();
                CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_CANNOT_REC_MOVIE);
                return 1;
            case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
            case AppRoot.USER_KEYCODE.S2_ON /* 518 */:
            case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
            case AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED /* 597 */:
                closeLayout();
                return 0;
            case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
            case AppRoot.USER_KEYCODE.AF_MF_AEL /* 638 */:
            case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                return 0;
            case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                return 0;
            default:
                return 1;
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        event.getScanCode();
        super.onKeyUp(keyCode, event);
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
        this.mScollView.smoothScrollBy(0, -50);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        this.mScollView.smoothScrollBy(0, 50);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        super.closeLayout();
        releaseResources();
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        releaseResources();
    }

    private void releaseResources() {
        LGUtility.getInstance().releaseImageViewDrawable(this.mGuideHeaderImageView);
        LGUtility.getInstance().releaseImageViewDrawable(this.mGuideFooterImageView);
        this.mGuideHeaderImageView = null;
        this.mGuideFooterImageView = null;
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
