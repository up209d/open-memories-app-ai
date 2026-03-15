package com.sony.imaging.app.lightgraffiti.playback;

import android.util.Log;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.lightgraffiti.util.LGUtility;

/* loaded from: classes.dex */
public class LGPlayRoot extends PlayRootContainer {
    public static final String ID_DELETE_MULTIPLE = "ID_DELETE_MULTIPLE";
    private static final String TAG = LGPlayRoot.class.getSimpleName();

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer, com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        int playDispMode = DisplayModeObserver.getInstance().getActiveDispMode(1);
        int playDispModeBackup = LGUtility.getPlayDispMode();
        if (playDispMode != playDispModeBackup && playDispModeBackup != LGUtility.PLAY_DISP_MODE_UNINTIALIZED) {
            DisplayModeObserver.getInstance().setDisplayMode(1, playDispModeBackup);
        }
        if (!LGStateHolder.SHOOTING_1ST.equals(LGStateHolder.getInstance().getShootingStage())) {
            Log.d(TAG, "->prepareShootingStage1st");
            LGStateHolder.getInstance().endShootingStage();
        }
        super.onResume();
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        int playDispMode = DisplayModeObserver.getInstance().getActiveDispMode(1);
        LGUtility.setPlayDispMode(playDispMode);
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        int playDispMode = DisplayModeObserver.getInstance().getActiveDispMode(1);
        LGUtility.setPlayDispMode(playDispMode);
        super.onDestroy();
    }
}
