package com.sony.imaging.app.portraitbeauty.playback.catchlight.effect;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.portraitbeauty.R;
import com.sony.imaging.app.portraitbeauty.common.EVFOffLayout;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyConstants;

/* loaded from: classes.dex */
public class ManualStartUpMessageLayout extends EVFOffLayout implements View.OnTouchListener {
    private View mCurrentView = null;
    private TextView mTextview = null;
    private Handler mHandler = null;
    private Runnable mRunnable = null;
    private final int TIME_OUT = PortraitBeautyConstants.THREE_SECOND_MILLIS;
    private RelativeLayout mMessageLayout = null;

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (this.mCurrentView == null) {
            this.mCurrentView = obtainViewFromPool(R.layout.message_single);
        }
        this.mTextview = (TextView) this.mCurrentView.findViewById(R.id.message_single);
        this.mTextview.setText("Temporary Text");
        this.mMessageLayout = (RelativeLayout) this.mCurrentView.findViewById(R.id.message_single_container);
        this.mMessageLayout.setOnTouchListener(this);
        this.mHandler = new Handler();
        this.mRunnable = new Runnable() { // from class: com.sony.imaging.app.portraitbeauty.playback.catchlight.effect.ManualStartUpMessageLayout.1
            @Override // java.lang.Runnable
            public void run() {
                ManualStartUpMessageLayout.this.closeLayout();
            }
        };
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.portraitbeauty.common.EVFOffLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.mHandler.postDelayed(this.mRunnable, 3000L);
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        this.mHandler.removeCallbacks(this.mRunnable);
        this.mCurrentView = null;
        super.onDestroyView();
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View arg0, MotionEvent event) {
        switch (event.getAction()) {
            case 1:
                closeLayout();
            default:
                return true;
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int keyCode2 = event.getScanCode();
        switch (keyCode2) {
            case 103:
            case AppRoot.USER_KEYCODE.LEFT /* 105 */:
            case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
            case AppRoot.USER_KEYCODE.SK1 /* 229 */:
            case AppRoot.USER_KEYCODE.SK2 /* 513 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
                return -1;
            case 232:
                closeLayout();
                return 1;
            default:
                return 0;
        }
    }
}
