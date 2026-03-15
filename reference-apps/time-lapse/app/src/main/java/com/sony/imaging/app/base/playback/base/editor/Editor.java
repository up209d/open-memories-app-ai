package com.sony.imaging.app.base.playback.base.editor;

import com.sony.imaging.app.base.playback.base.PlaySubApp;
import com.sony.imaging.app.base.playback.contents.EditService;

/* loaded from: classes.dex */
public abstract class Editor extends PlaySubApp {
    public static final String ID_CONFIRM = "ID_CONFIRM";
    public static final String ID_EXECUTOR = "ID_EXECUTOR";
    public static final String ID_TEST_THIS = "ID_TEST_THIS";

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract EditService getEditService();

    @Override // com.sony.imaging.app.base.playback.base.PlaySubApp, com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        getEditService().initialize();
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        getEditService().terminate();
        super.onPause();
    }
}
