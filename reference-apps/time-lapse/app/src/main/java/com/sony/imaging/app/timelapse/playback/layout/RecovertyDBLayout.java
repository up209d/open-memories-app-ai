package com.sony.imaging.app.timelapse.playback.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.playback.layout.PlayLayoutBase;
import com.sony.imaging.app.timelapse.R;

/* loaded from: classes.dex */
public class RecovertyDBLayout extends PlayLayoutBase {
    private View mCurrentView = null;
    private TextView mRecoveryMessage = null;

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mCurrentView = super.onCreateView(inflater, container, savedInstanceState);
        this.mRecoveryMessage = (TextView) this.mCurrentView.findViewById(R.id.caution_text);
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        this.mRecoveryMessage.setText(android.R.string.display_manager_overlay_display_title);
        super.onResume();
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        this.mRecoveryMessage = null;
        this.mCurrentView = null;
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        return -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getLayoutResource() {
        return R.layout.layout_type_01_w_background;
    }
}
