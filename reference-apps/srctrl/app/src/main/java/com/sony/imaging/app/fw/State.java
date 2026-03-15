package com.sony.imaging.app.fw;

import android.os.Bundle;
import android.os.Message;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.imaging.app.util.HDMIInfoWrapper;
import com.sony.imaging.app.util.OLEDWrapper;

/* loaded from: classes.dex */
public class State extends KeyReceiver {
    public ContainerState container;
    public Bundle data;
    int depthOffset;
    StateHandle handle;

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.sony.imaging.app.fw.KeyReceiver
    public int getDepth() {
        return this.depth + (this.handle.layer * this.depthOffset);
    }

    public boolean setData(String name, Object o) {
        AppRoot root = (AppRoot) getActivity();
        return root.setData(name, o);
    }

    public Object getData(String name) {
        AppRoot root = (AppRoot) getActivity();
        return root.getData(name);
    }

    public Object removeData(String name) {
        AppRoot root = (AppRoot) getActivity();
        return root.removeData(name);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void remove() {
        this.data = null;
        stop();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.sony.imaging.app.fw.KeyReceiver
    public boolean _handleMessage(Message msg) {
        HDMIInfoWrapper.INFO_TYPE mode;
        Integer mode2;
        if (msg.what == 305419897) {
            getHandler().removeMessages(Definition.STATE_TREE_CHANGED);
            int handled = msg.arg1;
            if ((handled & 1) == 0 && ApoWrapper.setApoType(getApoType())) {
                handled |= 1;
            }
            if ((handled & 2) == 0 && (mode2 = getCautionMode()) != null) {
                AppRoot root = (AppRoot) getActivity();
                root.onCautionModeChanged(mode2.intValue());
                handled |= 2;
            }
            if ((handled & 4) == 0 && OLEDWrapper.setOledType(getOledType())) {
                handled |= 4;
            }
            if ((handled & 8) == 0 && HDMIInfoWrapper.INFO_TYPE.NEUTRAL != (mode = getHDMIInfoType())) {
                HDMIInfoWrapper.setType(mode);
                handled |= 8;
            }
            msg.arg1 = handled;
            return handled == 15;
        }
        return handleMessage(msg);
    }

    public void setNextState(String name, Bundle bundle) {
        if (this.container == null) {
            throw new RuntimeException(name + " doesn't exist in current status.");
        }
        State f = this.container.getState(name);
        if (f == null) {
            this.container.setNextState(name, bundle);
        } else {
            this.container.replaceChildState(this.handle, f, bundle);
        }
    }

    public StateHandle addState(String name, Bundle bundle) {
        return this.container.addChildState(name, bundle);
    }

    public void removeState() {
        this.container.removeChildState(this.handle);
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        Message msg = Message.obtain(getHandler(), Definition.STATE_TREE_CHANGED, 0, 0);
        getHandler().sendMessageAtFrontOfQueue(msg);
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        Message msg = Message.obtain(getHandler(), Definition.STATE_TREE_CHANGED, 0, 0);
        getHandler().sendMessageAtFrontOfQueue(msg);
        super.onPause();
    }

    public void closeLayout(String name) {
        Layout layout = getLayout(name);
        closeLayout(layout);
    }

    public void updateLayout(String name) {
        Layout layout = getLayout(name);
        updateLayout(layout, 1);
    }

    public void updateLayout(String name, int type) {
        Layout layout = getLayout(name);
        updateLayout(layout, type);
    }

    public void updateView(String name) {
        Layout layout = getLayout(name);
        updateView(layout);
    }

    public ApoWrapper.APO_TYPE getApoType() {
        return ApoWrapper.APO_TYPE.UNKNOWN;
    }

    public OLEDWrapper.OLED_TYPE getOledType() {
        return OLEDWrapper.OLED_TYPE.LUMINANCE_ONLY;
    }

    public Integer getCautionMode() {
        return null;
    }

    public HDMIInfoWrapper.INFO_TYPE getHDMIInfoType() {
        return HDMIInfoWrapper.INFO_TYPE.NEUTRAL;
    }
}
