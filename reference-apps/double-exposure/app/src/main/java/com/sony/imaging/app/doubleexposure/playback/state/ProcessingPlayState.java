package com.sony.imaging.app.doubleexposure.playback.state;

import android.os.Message;
import com.sony.imaging.app.base.playback.base.PlayStateBase;
import com.sony.imaging.app.doubleexposure.common.AppLog;

/* loaded from: classes.dex */
public class ProcessingPlayState extends PlayStateBase {
    public static String ID_PROCESSINGLAYOUT = "ID_PROCESSINGLAYOUT";
    private final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        openLayout(ID_PROCESSINGLAYOUT);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        super.onResume();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (getLayout(ID_PROCESSINGLAYOUT).getView() != null) {
            getLayout(ID_PROCESSINGLAYOUT).closeLayout();
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        super.onDestroy();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver
    public boolean handleMessage(Message msg) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        switch (msg.what) {
            case PreviewPlayState.TRANSIT_TO_PREVIEWSTATE /* 101 */:
                setNextState(PreviewPlayState.ID_PREVIEWLAYOUT, null);
                break;
        }
        return super.handleMessage(msg);
    }
}
