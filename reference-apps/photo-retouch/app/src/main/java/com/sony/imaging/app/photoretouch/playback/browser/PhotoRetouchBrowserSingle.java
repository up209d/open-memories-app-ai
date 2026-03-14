package com.sony.imaging.app.photoretouch.playback.browser;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.playback.base.SingleStateBase;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.fw.StateHandle;
import com.sony.imaging.app.photoretouch.Constant;
import com.sony.imaging.app.util.ApoWrapper;

/* loaded from: classes.dex */
public class PhotoRetouchBrowserSingle extends SingleStateBase {
    private final String TAG = PhotoRetouchBrowserSingle.class.getSimpleName();
    private StateHandle mMenuStateHandle = null;

    @Override // com.sony.imaging.app.base.playback.base.SingleStateBase, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase, com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        Bundle bundle = new Bundle();
        bundle.putString(MenuState.ITEM_ID, Constant.ID_PHOTORETOUCHMENU);
        this.mMenuStateHandle = addChildState(ICustomKey.CATEGORY_MENU, bundle);
    }

    @Override // com.sony.imaging.app.fw.State
    public Integer getCautionMode() {
        return 13;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver
    public boolean handleMessage(Message msg) {
        Log.d(this.TAG, "============msg= " + msg.what);
        if (1000 != msg.what) {
            return super.handleMessage(msg);
        }
        removeChildState(this.mMenuStateHandle);
        transitionIndexPb();
        return true;
    }

    @Override // com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        return ApoWrapper.APO_TYPE.NONE;
    }
}
