package com.sony.imaging.app.srctrl.util;

import android.media.MediaPlayer;
import android.util.Log;
import java.util.HashSet;
import java.util.Set;

/* loaded from: classes.dex */
public class SEPlayer {
    public static final String BACK_CANCEL = "BEEP_ID_BACK_CANCEL";
    public static final String CAUTION = "BEEP_ID_CAUTION";
    public static final String FOCUSLOCK = "BEEP_ID_FORCUSLOCK";
    public static final String MENU_CLOSE_OPTION_OFF = "BEEP_ID_OPTION_OFF";
    public static final String MENU_OPEN_OPTION_ON = "BEEP_ID_OPTION_ON";
    public static final String MOVE_DOWN_WHEEL_DOWN = "BEEP_ID_WHEEL_DOWN";
    public static final String MOVE_UP_WHEEL_UP = "BEEP_ID_WHEEL_UP";
    public static final String REC_START = "BEEP_ID_RECSTART";
    public static final String REC_STOP = "BEEP_ID_RECSTOP";
    private static final String SCALARA_PREFIX = "SCALAR_A://";
    public static final String SELECT_SWITCHMODE = "BEEP_ID_SWITCH_MODE";
    public static final String SELFTIMER_2SEC = "BEEP_ID_SELFTIMER_2SEC";
    public static final String SELFTIMER_REMOTERELEASE = "BEEP_ID_SELFTIMER";
    private static final String TAG = "SEPlayer";
    public static final String T_BACK_CANCEL = "BEEP_ID_BACK_CANCEL_TP";
    public static final String T_MENU_CLOSE = "BEEP_ID_MENU_OFF";
    public static final String T_MENU_OPEN = "BEEP_ID_MENU_ON";
    public static final String T_MOVE_DOWN = "BEEP_ID_MOVE_DOWN";
    public static final String T_MOVE_UP = "BEEP_ID_MOVE_UP";
    public static final String T_OFF = "BEEP_ID_OFF";
    public static final String T_ON = "BEEP_ID_ON";
    public static final String T_SELECT = "BEEP_ID_SELECT";
    public static final String T_TOUCH = "BEEP_ID_TAP";
    private static final Set<String> sSounds = new HashSet();
    private MediaPlayer mPlayer = new MediaPlayer();
    private String mPreSelectedBeepId = "BEEP_ID_UNKNOWN";

    static {
        sSounds.add("BEEP_ID_MENU_ON");
        sSounds.add("BEEP_ID_MENU_OFF");
        sSounds.add("BEEP_ID_SELECT");
        sSounds.add("BEEP_ID_BACK_CANCEL_TP");
        sSounds.add("BEEP_ID_MOVE_UP");
        sSounds.add("BEEP_ID_MOVE_DOWN");
        sSounds.add("BEEP_ID_TAP");
        sSounds.add("BEEP_ID_ON");
        sSounds.add("BEEP_ID_OFF");
        sSounds.add(FOCUSLOCK);
        sSounds.add("BEEP_ID_SELFTIMER");
        sSounds.add("BEEP_ID_SELFTIMER_2SEC");
        sSounds.add("BEEP_ID_RECSTART");
        sSounds.add("BEEP_ID_RECSTOP");
        sSounds.add("BEEP_ID_SWITCH_MODE");
        sSounds.add("BEEP_ID_OPTION_ON");
        sSounds.add("BEEP_ID_OPTION_OFF");
        sSounds.add("BEEP_ID_WHEEL_DOWN");
        sSounds.add("BEEP_ID_WHEEL_UP");
        sSounds.add("BEEP_ID_BACK_CANCEL");
        sSounds.add("BEEP_ID_CAUTION");
    }

    public boolean play(String id) {
        if (!sSounds.contains(id)) {
            return false;
        }
        try {
            if (this.mPreSelectedBeepId == id) {
                this.mPlayer.start();
            } else {
                this.mPlayer.reset();
                this.mPlayer.setDataSource(SCALARA_PREFIX + id);
                this.mPlayer.prepare();
                this.mPlayer.start();
                this.mPreSelectedBeepId = id;
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error! Failed to start()");
            return false;
        }
    }

    public void release() {
        this.mPlayer.stop();
        this.mPlayer.release();
    }
}
