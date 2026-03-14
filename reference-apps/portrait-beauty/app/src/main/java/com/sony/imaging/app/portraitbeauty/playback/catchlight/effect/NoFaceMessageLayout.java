package com.sony.imaging.app.portraitbeauty.playback.catchlight.effect;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.portraitbeauty.R;
import com.sony.imaging.app.portraitbeauty.common.EVFOffLayout;

/* loaded from: classes.dex */
public class NoFaceMessageLayout extends EVFOffLayout implements View.OnTouchListener {
    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View currentView = obtainViewFromPool(R.layout.message_alert);
        TextView textView = (TextView) currentView.findViewById(R.id.msg_alert);
        textView.setText("temp text");
        ImageView outerArea = (ImageView) currentView.findViewById(R.id.block_touch_below_guide_layout);
        outerArea.setOnTouchListener(this);
        setKeyBeepPattern(0);
        return currentView;
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
                getHandler().sendEmptyMessage(1000);
                return 1;
            default:
                return 0;
        }
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View arg0, MotionEvent arg1) {
        return true;
    }
}
