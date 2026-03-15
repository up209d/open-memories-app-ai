package com.sony.imaging.app.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import com.sony.imaging.app.base.shooting.camera.PictureQualityController;
import java.io.IOException;
import java.util.HashMap;

/* loaded from: classes.dex */
public final class BeepUtility {
    private static final String TAG = "BeepUtility";
    private static BeepUtility mBeepUtility = new BeepUtility();
    private BeepMode beepMode = null;
    private BeepUtilityIdTableBase mBeepUtilityIdTable;

    private BeepUtility() {
    }

    public void setIdTable(BeepUtilityIdTableBase beepUtilityIdTable) {
        this.mBeepUtilityIdTable = beepUtilityIdTable;
    }

    public BeepUtilityIdTableBase getIdTable() {
        return this.mBeepUtilityIdTable;
    }

    public static BeepUtility getInstance() {
        return mBeepUtility;
    }

    public String findBeep(KeyEvent event, Integer keyBeepPattern) {
        SparseArray<String> table = this.mBeepUtilityIdTable.get(keyBeepPattern.intValue());
        return table != null ? table.get(event.getScanCode()) : BeepUtilityRsrcTable.BEEP_ID_NONE;
    }

    public void init() {
        if (Environment.DEVICE_TYPE == 2 || Environment.DEVICE_TYPE == 4) {
            this.beepMode = new BeepQEMU();
        } else {
            this.beepMode = new BeepSYS();
        }
    }

    public void prepare(Context contextApp) {
        this.beepMode.init(contextApp);
    }

    public void playBeep(String beepId) {
        this.beepMode.playBeep(beepId);
    }

    public void term() {
        this.beepMode.release();
        this.beepMode = null;
    }

    /* loaded from: classes.dex */
    private abstract class BeepMode {
        abstract void init(Context context);

        abstract void playBeep(String str);

        abstract void release();

        private BeepMode() {
        }
    }

    /* loaded from: classes.dex */
    private class BeepQEMU extends BeepMode {
        private HashMap<String, MediaPlayer> mBeepMap;
        private MediaPlayer mMediaPlayer;

        private BeepQEMU() {
            super();
        }

        @Override // com.sony.imaging.app.util.BeepUtility.BeepMode
        public void init(Context contextApp) {
            this.mBeepMap = new HashMap<>();
            Context contextRsrc = null;
            try {
                contextRsrc = contextApp.createPackageContext("com.sony.imaging.app.resourcesqemu", 4);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(BeepUtility.TAG, "StackTrace", e);
            }
            for (String beepId : BeepUtilityRsrcTable.BEEP_ID_TO_RSRC.keySet()) {
                String beepName = BeepUtilityRsrcTable.BEEP_ID_TO_RSRC.get(beepId);
                Resources res = contextRsrc.getResources();
                Integer beepRsrc = Integer.valueOf(res.getIdentifier(beepName, PictureQualityController.PICTURE_QUALITY_RAW, "com.sony.imaging.app.resourcesqemu"));
                this.mMediaPlayer = MediaPlayer.create(contextRsrc, beepRsrc.intValue());
                this.mBeepMap.put(beepId, this.mMediaPlayer);
            }
        }

        @Override // com.sony.imaging.app.util.BeepUtility.BeepMode
        public void playBeep(String beepId) {
            if (!BeepUtilityRsrcTable.BEEP_ID_NONE.equals(beepId)) {
                this.mBeepMap.get(beepId).start();
            }
        }

        @Override // com.sony.imaging.app.util.BeepUtility.BeepMode
        public void release() {
            for (MediaPlayer mp : this.mBeepMap.values()) {
                if (mp != null) {
                    mp.release();
                }
            }
            this.mBeepMap.clear();
        }
    }

    /* loaded from: classes.dex */
    private class BeepSYS extends BeepMode {
        private static final String THREAD_NAME = "BeepThread";
        private final String LOG_MSG_RELEASE_END;
        private final String LOG_MSG_RELEASE_START;
        private HashMap<String, BeepSYSMediaPlayer> mBeepMap;
        private BeepHandler mHandler;
        private HandlerThread mThread;
        private final StringBuilder uri;
        private final String uri_prefix;

        /* loaded from: classes.dex */
        private class BeepSYSMediaPlayer extends MediaPlayer {
            private final String uri;
            private boolean mIsPrepared = false;
            private boolean mIsPreparing = false;
            private int beepCntToPlay = 0;

            static /* synthetic */ int access$310(BeepSYSMediaPlayer x0) {
                int i = x0.beepCntToPlay;
                x0.beepCntToPlay = i - 1;
                return i;
            }

            public BeepSYSMediaPlayer(String uri) {
                this.uri = uri;
            }

            void init() {
                try {
                    if (!this.mIsPreparing) {
                        this.mIsPreparing = true;
                        setDataSource(this.uri);
                        setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: com.sony.imaging.app.util.BeepUtility.BeepSYS.BeepSYSMediaPlayer.1
                            @Override // android.media.MediaPlayer.OnPreparedListener
                            public void onPrepared(MediaPlayer mediaplayer) {
                                BeepSYSMediaPlayer.this.mIsPrepared = true;
                                if (BeepSYSMediaPlayer.this.beepCntToPlay != 0) {
                                    mediaplayer.start();
                                    BeepSYSMediaPlayer.access$310(BeepSYSMediaPlayer.this);
                                }
                            }
                        });
                        prepareAsync();
                    }
                } catch (IOException e) {
                    Log.e(BeepUtility.TAG, "StackTrace", e);
                } catch (IllegalArgumentException e2) {
                    Log.e(BeepUtility.TAG, "StackTrace", e2);
                } catch (IllegalStateException e3) {
                    Log.e(BeepUtility.TAG, "StackTrace", e3);
                }
            }

            @Override // android.media.MediaPlayer
            public void start() {
                if (this.mIsPrepared) {
                    super.start();
                } else {
                    this.beepCntToPlay++;
                    init();
                }
            }

            @Override // android.media.MediaPlayer
            public void release() {
                this.mIsPrepared = false;
                this.mIsPreparing = false;
                super.release();
            }
        }

        /* loaded from: classes.dex */
        private class BeepHandler extends Handler {
            static final int MSG_ID_INIT = 1;
            static final int MSG_ID_PLAY = 3;
            static final int MSG_ID_TERMINATE = 2;

            public BeepHandler(Looper looper) {
                super(looper);
            }

            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        ((BeepSYSMediaPlayer) msg.obj).init();
                        return;
                    case 2:
                    default:
                        return;
                    case 3:
                        ((BeepSYSMediaPlayer) msg.obj).start();
                        return;
                }
            }
        }

        BeepSYS() {
            super();
            this.uri = new StringBuilder();
            this.uri_prefix = "SCALAR_A://";
            this.LOG_MSG_RELEASE_START = "start release";
            this.LOG_MSG_RELEASE_END = "end release";
            this.mThread = new HandlerThread(THREAD_NAME);
            this.mThread.start();
            this.mHandler = new BeepHandler(this.mThread.getLooper());
            this.mBeepMap = new HashMap<>();
            for (String beepId : BeepUtilityRsrcTable.BEEP_ID_TO_RSRC.keySet()) {
                if (!BeepUtilityRsrcTable.BEEP_ID_NONE.equals(beepId)) {
                    this.uri.replace(0, this.uri.length(), "SCALAR_A://").append(beepId);
                    BeepSYSMediaPlayer beepMediaPlayer = new BeepSYSMediaPlayer(this.uri.toString());
                    this.mBeepMap.put(beepId, beepMediaPlayer);
                }
            }
        }

        @Override // com.sony.imaging.app.util.BeepUtility.BeepMode
        public void init(Context contextApp) {
            for (BeepSYSMediaPlayer mp : this.mBeepMap.values()) {
                this.mHandler.sendMessage(Message.obtain(this.mHandler, 1, mp));
            }
        }

        @Override // com.sony.imaging.app.util.BeepUtility.BeepMode
        public void playBeep(String beepId) {
            BeepSYSMediaPlayer beepMediaPlayer;
            if (!BeepUtilityRsrcTable.BEEP_ID_NONE.equals(beepId) && (beepMediaPlayer = this.mBeepMap.get(beepId)) != null) {
                this.mHandler.sendMessage(Message.obtain(this.mHandler, 3, beepMediaPlayer));
            }
        }

        @Override // com.sony.imaging.app.util.BeepUtility.BeepMode
        public void release() {
            Log.d(BeepUtility.TAG, "start release");
            this.mHandler.removeMessages(3);
            this.mHandler.removeMessages(1);
            this.mThread.quit();
            try {
                this.mThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (BeepSYSMediaPlayer mp : this.mBeepMap.values()) {
                if (mp != null) {
                    mp.release();
                }
            }
            this.mBeepMap.clear();
            Log.d(BeepUtility.TAG, "end release");
        }
    }
}
