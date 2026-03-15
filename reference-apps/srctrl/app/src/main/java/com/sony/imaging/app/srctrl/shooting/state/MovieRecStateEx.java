package com.sony.imaging.app.srctrl.shooting.state;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.sony.imaging.app.base.shooting.movie.MovieRecState;
import com.sony.imaging.app.srctrl.network.wifiWrapper.WifiP2pManagerFactory;
import com.sony.imaging.app.srctrl.util.ModelInfo;
import com.sony.imaging.app.srctrl.util.NfcCtrlManager;
import com.sony.imaging.app.srctrl.util.SafeBroadcastReceiver;
import com.sony.imaging.app.srctrl.util.StateController;

/* loaded from: classes.dex */
public class MovieRecStateEx extends MovieRecState {
    public static final String STATE_NAME = "MovieRec";
    public static final String tag = MovieRecStateEx.class.getName();
    private SafeBroadcastReceiver bReceiver = null;
    private IntentFilter iFilter = null;

    @Override // com.sony.imaging.app.base.shooting.movie.MovieRecState, com.sony.imaging.app.base.shooting.movie.MovieStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.bReceiver = null;
        if (ModelInfo.getInstance().isNfcSupported()) {
            this.iFilter = new IntentFilter();
            this.iFilter.addAction(WifiP2pManagerFactory.IWifiP2pManager.STA_DISCONNECTED_ACTION);
            this.iFilter.addAction(WifiP2pManagerFactory.IWifiP2pManager.STA_CONNECTED_ACTION);
            this.bReceiver = new SafeBroadcastReceiver() { // from class: com.sony.imaging.app.srctrl.shooting.state.MovieRecStateEx.1
                @Override // com.sony.imaging.app.srctrl.util.SafeBroadcastReceiver
                public void checkedOnReceive(Context context, Intent intent) {
                    MovieRecStateEx.this.handleEvent(intent);
                }
            };
            this.bReceiver.registerThis(getActivity(), this.iFilter);
        }
        StateController stateController = StateController.getInstance();
        stateController.setState(this);
        stateController.setAppCondition(StateController.AppCondition.SHOOTING_MOVIE_REC);
    }

    @Override // com.sony.imaging.app.base.shooting.movie.MovieRecState, com.sony.imaging.app.base.shooting.movie.MovieStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (this.bReceiver != null) {
            NfcCtrlManager.getInstance().stopNfcTouchStandby();
            this.bReceiver.unregisterThis(getActivity());
        }
        StateController stateController = StateController.getInstance();
        stateController.setState(this);
        stateController.setAppCondition(StateController.AppCondition.SHOOTING_MOVIE_WAIT_REC_STOP);
        super.onPause();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleEvent(Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManagerFactory.IWifiP2pManager.STA_CONNECTED_ACTION.equals(action)) {
            NfcCtrlManager.getInstance().stopNfcTouchStandby();
        } else if (WifiP2pManagerFactory.IWifiP2pManager.STA_DISCONNECTED_ACTION.equals(action)) {
            NfcCtrlManager.getInstance().startNfcTouchStandby();
        }
    }
}
