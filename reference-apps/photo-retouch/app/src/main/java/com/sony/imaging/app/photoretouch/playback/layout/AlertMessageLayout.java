package com.sony.imaging.app.photoretouch.playback.layout;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.photoretouch.Constant;
import com.sony.imaging.app.photoretouch.R;

/* loaded from: classes.dex */
public class AlertMessageLayout extends Layout implements View.OnTouchListener {
    private boolean isSwitchingToIndexPB = false;
    private String mCurrentKitId = null;

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View currentView = obtainViewFromPool(R.layout.message_alert);
        ImageView outerArea = (ImageView) currentView.findViewById(R.id.block_touch_below_guide_layout);
        outerArea.setOnTouchListener(this);
        setKeyBeepPattern(0);
        return currentView;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        updateGuideLayout();
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void onReopened() {
        super.onReopened();
        updateGuideLayout();
    }

    private void updateGuideLayout() {
        Bundle bundle = this.data;
        if (bundle != null) {
            this.isSwitchingToIndexPB = bundle.getBoolean(Constant.ALERT_SWITCHTOINDEXVIEW);
            String message = bundle.getString(Constant.ALERT_MESSAGE);
            this.mCurrentKitId = bundle.getString(Constant.ALERT_CURRENTKIT);
            TextView textView = (TextView) getView().findViewById(R.id.msg_alert);
            textView.setText(message);
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int keyCode2 = event.getScanCode();
        switch (keyCode2) {
            case AppRoot.USER_KEYCODE.UP /* 103 */:
            case AppRoot.USER_KEYCODE.LEFT /* 105 */:
            case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
            case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
            case AppRoot.USER_KEYCODE.SK1 /* 229 */:
            case AppRoot.USER_KEYCODE.SK2 /* 513 */:
            case AppRoot.USER_KEYCODE.MENU /* 514 */:
            case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
            case AppRoot.USER_KEYCODE.FN /* 520 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
            case AppRoot.USER_KEYCODE.AEL /* 532 */:
            case AppRoot.USER_KEYCODE.AF_MF /* 533 */:
            case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
            case AppRoot.USER_KEYCODE.CUSTOM /* 588 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
            case AppRoot.USER_KEYCODE.AF_RANGE /* 614 */:
            case AppRoot.USER_KEYCODE.UM_MOVIE_REC /* 616 */:
            case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
            case AppRoot.USER_KEYCODE.CUSTOM1 /* 622 */:
            case AppRoot.USER_KEYCODE.CUSTOM2 /* 623 */:
            case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
            case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
            case AppRoot.USER_KEYCODE.MOVIE_REC_2ND /* 637 */:
            case AppRoot.USER_KEYCODE.AF_MF_AEL /* 638 */:
            case AppRoot.USER_KEYCODE.IR_MOVIE_REC /* 643 */:
                return -1;
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                if (this.isSwitchingToIndexPB) {
                    getHandler().sendEmptyMessage(Constant.TRANSITION_INDEXPB);
                } else if (this.mCurrentKitId != null) {
                    openLayout(this.mCurrentKitId);
                    this.mCurrentKitId = null;
                }
                closeLayout();
                return 1;
            default:
                return 0;
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        int keyCode2 = event.getScanCode();
        switch (keyCode2) {
            case AppRoot.USER_KEYCODE.AEL /* 532 */:
            case AppRoot.USER_KEYCODE.AF_MF /* 533 */:
            case AppRoot.USER_KEYCODE.CUSTOM /* 588 */:
            case AppRoot.USER_KEYCODE.AF_RANGE /* 614 */:
            case AppRoot.USER_KEYCODE.CUSTOM1 /* 622 */:
            case AppRoot.USER_KEYCODE.CUSTOM2 /* 623 */:
            case AppRoot.USER_KEYCODE.AF_MF_AEL /* 638 */:
                Log.d("", "Alert Message UP");
                return -1;
            default:
                return 0;
        }
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View arg0, MotionEvent arg1) {
        return true;
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        this.isSwitchingToIndexPB = false;
        this.mCurrentKitId = null;
        super.onDestroyView();
    }
}
