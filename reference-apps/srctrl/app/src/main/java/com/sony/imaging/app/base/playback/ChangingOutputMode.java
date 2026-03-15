package com.sony.imaging.app.base.playback;

import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.playback.base.PlayStateBase;
import com.sony.imaging.app.fw.RunStatus;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class ChangingOutputMode extends PlayStateBase {
    private static final String[] TAGS = {DisplayModeObserver.TAG_4K_PLAYBACK_STATUS_CHANGE};
    public static final String TAG_NEXT_APP = "next_app";
    private DisplayModeObserver mDispModeObserver;
    protected NotificationListener mDisplayModeListener;

    /* loaded from: classes.dex */
    protected class DisplayModeListener implements NotificationListener {
        protected DisplayModeListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return ChangingOutputMode.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (DisplayModeObserver.TAG_4K_PLAYBACK_STATUS_CHANGE.equals(tag)) {
                int status = ChangingOutputMode.this.mDispModeObserver.get4kOutputStatus();
                if (4 == RunStatus.getStatus()) {
                    if (1 == status || 2 == status) {
                        String app = null;
                        if (ChangingOutputMode.this.data != null) {
                            app = ChangingOutputMode.this.data.getString(ChangingOutputMode.TAG_NEXT_APP);
                            ChangingOutputMode.this.data.remove(ChangingOutputMode.TAG_NEXT_APP);
                        }
                        PlayRootContainer rootContainer = ChangingOutputMode.this.getRootContainer();
                        if (app == null) {
                            app = ChangingOutputMode.this.getRootContainer().getStartFunction();
                        }
                        rootContainer.changeApp(app, ChangingOutputMode.this.data);
                    }
                }
            }
        }
    }

    protected NotificationListener getDisplayObserverListener() {
        return new DisplayModeListener();
    }
}
