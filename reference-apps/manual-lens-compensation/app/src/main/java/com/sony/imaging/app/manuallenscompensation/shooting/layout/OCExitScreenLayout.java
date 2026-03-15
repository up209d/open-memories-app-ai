package com.sony.imaging.app.manuallenscompensation.shooting.layout;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.common.layout.ExitScreenLayout;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.manuallenscompensation.R;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil;

/* loaded from: classes.dex */
public class OCExitScreenLayout extends ExitScreenLayout implements View.OnTouchListener {
    TextView mOCCancel;
    View v = null;

    @Override // com.sony.imaging.app.base.common.layout.ExitScreenLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        int ret = super.onKeyDown(keyCode, event);
        if ((code == 513 || code == 595) && OCUtil.getInstance().isLauncherBooted()) {
            return -1;
        }
        return ret;
    }

    @Override // com.sony.imaging.app.base.common.layout.ExitScreenLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        if (code == 232 && OCUtil.getInstance().isLauncherBooted() && isCancelFocused()) {
            event = new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 1, keyCode, 0, 0, 0, AppRoot.USER_KEYCODE.LENS_ATTACH);
        }
        return super.onKeyUp(keyCode, event);
    }

    private boolean isCancelFocused() {
        if (this.mOCCancel == null) {
            return false;
        }
        if (!this.mOCCancel.hasFocus() && !this.mOCCancel.isSelected()) {
            return false;
        }
        return true;
    }

    @Override // com.sony.imaging.app.base.common.layout.ExitScreenLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.v = super.onCreateView(inflater, container, savedInstanceState);
        this.mOCCancel = (TextView) this.v.findViewById(R.id.button_lower);
        if (this.mOCCancel != null) {
        }
        this.v.setOnTouchListener(this);
        return this.v;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (this.v != null) {
            this.v.setOnTouchListener(null);
        }
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.common.layout.ExitScreenLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mOCCancel = null;
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }
}
