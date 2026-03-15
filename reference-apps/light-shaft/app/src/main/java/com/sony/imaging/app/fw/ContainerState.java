package com.sony.imaging.app.fw;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import com.sony.imaging.app.fw.AppRoot;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class ContainerState extends State {
    private static final String TAG = "FW";
    private static final String log_doesnothave = "This ContainerState does not have child states.";
    private static final StringBuilder log_string = new StringBuilder();
    ArrayList<StateHandle> currentState = new ArrayList<>();
    public StateFactory stateFactory = null;

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.constituentRecord != null) {
            this.stateFactory = this.constituentRecord.getStateFactory();
        } else {
            Log.i(TAG, log_doesnothave);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.sony.imaging.app.fw.State
    public void remove() {
        int c = this.currentState.size();
        for (int i = 0; i < c; i++) {
            StateHandle handle = this.currentState.get(i);
            handle.state.remove();
            handle.expire();
        }
        this.currentState.clear();
        super.remove();
    }

    public StateHandle addChildState(String name, Bundle data) {
        State f = getState(name);
        return addChildState(f, data);
    }

    StateHandle addChildState(State f, Bundle data) {
        StateHandle handle = new StateHandle(f);
        AppRoot.StateUpdater.add(this, f, handle, data);
        AppRoot.StateUpdater.commit();
        return handle;
    }

    public void removeChildState(StateHandle handle) {
        AppRoot.StateUpdater.remove(this, handle);
        AppRoot.StateUpdater.commit();
    }

    public void replaceChildState(StateHandle handle, String name, Bundle data) {
        State f = getState(name);
        replaceChildState(handle, f, data);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void replaceChildState(StateHandle handle, State f, Bundle data) {
        AppRoot.StateUpdater.replace(this, f, handle, data);
        AppRoot.StateUpdater.commit();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.sony.imaging.app.fw.KeyReceiver
    public int dispatchKeyDown(int keyCode, KeyEvent event) {
        int ret = 0;
        for (int i = this.currentState.size() - 1; i >= 0 && ret == 0; i--) {
            ret = this.currentState.get(i).state.dispatchKeyDown(keyCode, event);
        }
        if (ret == 0) {
            int ret2 = super.dispatchKeyDown(keyCode, event);
            return ret2;
        }
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.sony.imaging.app.fw.KeyReceiver
    public int dispatchKeyUp(int keyCode, KeyEvent event) {
        int ret = 0;
        for (int i = this.currentState.size() - 1; i >= 0 && ret == 0; i--) {
            ret = this.currentState.get(i).state.dispatchKeyUp(keyCode, event);
        }
        if (ret == 0) {
            int ret2 = super.dispatchKeyUp(keyCode, event);
            return ret2;
        }
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.KeyReceiver
    public boolean _handleMessage(Message msg) {
        boolean ret = false;
        for (int i = this.currentState.size() - 1; i >= 0 && !ret; i--) {
            ret = this.currentState.get(i).state._handleMessage(msg);
        }
        if (!ret) {
            boolean ret2 = super._handleMessage(msg);
            return ret2;
        }
        return ret;
    }

    public State getState(String name) {
        return this.stateFactory.get(name);
    }
}
