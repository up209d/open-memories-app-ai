package com.sony.imaging.app.each.playback.delete.multiple;

import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.base.playback.base.editor.Executor;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.HoldKeyServer;

/* loaded from: classes.dex */
public class DeleteMultipleExecutor extends Executor {
    @Override // com.sony.imaging.app.base.playback.contents.EditService.ExecutionListener
    public void onEnd(int status) {
        PlayRootContainer root = getRootContainer();
        root.changeApp(PlayRootContainer.ID_BROWSER);
    }

    @Override // com.sony.imaging.app.base.playback.base.editor.Executor, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase, com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.EV_DIAL_CHANGED, 1);
        super.onResume();
    }

    @Override // com.sony.imaging.app.base.playback.base.editor.Executor, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        HoldKeyServer.holdKey(AppRoot.USER_KEYCODE.EV_DIAL_CHANGED, 0);
    }
}
