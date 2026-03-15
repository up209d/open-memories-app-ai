package com.sony.imaging.app.srctrl.network.wpspin.input;

import android.util.Log;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.srctrl.caution.InfoEx;
import com.sony.imaging.app.srctrl.network.NetworkRootState;
import com.sony.imaging.app.srctrl.network.base.NwKeyHandlerBase;
import com.sony.imaging.app.srctrl.network.wifiWrapper.WifiP2pManagerFactory;
import com.sony.imaging.app.srctrl.util.StateController;
import com.sony.imaging.app.util.BeepUtility;

/* loaded from: classes.dex */
public class NwWpsPinInputKeyHandler extends NwKeyHandlerBase {
    public static final String TAG = NwWpsPinInputKeyHandler.class.getSimpleName();

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        BeepUtility.getInstance().playBeep("BEEP_ID_SELECT");
        cancelAndMoveToWaitingState();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        if (NwWpsPinInputLayout.isEditTextFocused()) {
            int ret = hadleEditBoxPushed();
            return ret;
        }
        int ret2 = handleOkButtonPushed();
        return ret2;
    }

    private int handleOkButtonPushed() {
        BeepUtility.getInstance().playBeep("BEEP_ID_SELECT");
        tryPinAndMoveToRegisteringState();
        return 1;
    }

    private int hadleEditBoxPushed() {
        BeepUtility.getInstance().playBeep("BEEP_ID_SELECT");
        openKeypad();
        return 1;
    }

    private void cancelAndMoveToWaitingState() {
        NetworkRootState state = (NetworkRootState) this.target;
        WifiP2pManagerFactory.IWifiP2pManager wifiP2pManager = WifiP2pManagerFactory.getInstance(state.getActivity().getApplicationContext());
        wifiP2pManager.cancel();
        state.setNextState(NetworkRootState.ID_WAITING, null);
    }

    private void tryPinAndMoveToRegisteringState() {
        NetworkRootState state = (NetworkRootState) this.target;
        WifiP2pManagerFactory.IWifiP2pManager wifiP2pManager = WifiP2pManagerFactory.getInstance(state.getActivity().getApplicationContext());
        String pin = NwWpsPinInputLayout.getPin();
        if (pin.length() == 8 || pin.length() == 4) {
            NwWpsPinInputLayout.clearPin();
            if (wifiP2pManager.isValidWpsPin(pin)) {
                Log.v(TAG, "PIN: " + pin);
                wifiP2pManager.startWpsPin(pin);
                state.setNextState(NetworkRootState.ID_REGISTERING, null);
                return;
            } else {
                Log.v(TAG, "PIN: " + pin + " is Invalid");
                wifiP2pManager.cancel();
                StateController.getInstance().setIsErrorByTimeout(false);
                state.setNextState(NetworkRootState.ID_WPS_ERROR, null);
                return;
            }
        }
        CautionUtilityClass.getInstance().requestTrigger(InfoEx.CAUTION_ID_SMART_REMOTE_INVALID_PIN);
    }

    private void openKeypad() {
        NwWpsPinInputLayout.openKeypad();
    }
}
