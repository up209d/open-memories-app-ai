package com.sony.imaging.app.srctrl.util;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.playback.player.MediaPlayerManager;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.RunStatus;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.srctrl.SRCtrlRootState;
import com.sony.imaging.app.srctrl.network.NetworkRootState;
import com.sony.imaging.app.srctrl.playback.PlayRootContainerEx;
import com.sony.imaging.app.srctrl.playback.browser.ContentsTransferState;
import com.sony.imaging.app.srctrl.shooting.state.CaptureStateEx;
import com.sony.imaging.app.srctrl.shooting.state.MfAssistStateEx;
import com.sony.imaging.app.srctrl.shooting.state.MovieCaptureStateEx;
import com.sony.imaging.app.srctrl.shooting.state.MovieRecStandbyStateEx;
import com.sony.imaging.app.srctrl.shooting.state.NormalCaptureStateEx;
import com.sony.imaging.app.srctrl.shooting.state.S1OffEEStateEx;
import com.sony.imaging.app.srctrl.shooting.state.S1OnEEStateEx;
import com.sony.imaging.app.srctrl.shooting.state.S1OnEEStateForTouchAF;
import com.sony.imaging.app.srctrl.shooting.state.S1OnEEStateForTouchAFAssist;
import com.sony.imaging.app.srctrl.shooting.state.SelfTimerCaptureStateEx;
import com.sony.imaging.app.srctrl.shooting.state.ShootingMenuStateEx;
import com.sony.imaging.app.srctrl.streaming.StreamingLoader;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationTouchAFPosition;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.imaging.app.srctrl.webapi.specific.RecModeTransitionHandler;
import com.sony.imaging.app.srctrl.webapi.specific.ServerEventHandler;
import com.sony.imaging.app.srctrl.webapi.specific.SetCameraFunctionHandler;
import com.sony.imaging.app.srctrl.webapi.specific.ShootingHandler;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public class StateController {
    private static final String TAG = StateController.class.getSimpleName();
    private static StateController sStateController = new StateController();
    private AppCondition appCondition;
    private boolean backTransitionFlag;
    private boolean hasModeDial;
    private boolean isClosingShootingState;
    private boolean isDdServerReady;
    private boolean isDuringCameraSetupRoutine = false;
    private boolean isErrorByTimeout;
    private boolean isFatalError;
    private boolean isInitialWifiSetup;
    private boolean isRestartForNewConfig;
    private boolean isShooting;
    private boolean isWaitingCameraFunctionChange;
    private boolean isWaitingDeletingStateChange;
    private boolean isWaitingRecModeChange;
    private boolean isWaitingStatusChange;
    private ShootingHandler.ShutterListenerEx listener;
    private WeakReference<Context> mApplicationContextRef;
    private AppCondition mLastAppConditionBeforeCapturing;
    private ShootingHandler.MovieShutterListenerEx movieListener;
    private OperationReceiver receiver;
    private volatile RecordingMode recordingMode;
    private ServerStatus servStat;
    private State state;

    /* loaded from: classes.dex */
    public enum AppCondition {
        PREPARATION,
        SHOOTING_EE,
        SHOOTING_MENU,
        SHOOTING_INHIBIT,
        SHOOTING_LOCAL,
        SHOOTING_REMOTE,
        DIAL_INHIBIT,
        SHOOTING_REMOTE_TOUCHAF,
        SHOOTING_REMOTE_TOUCHAFASSIST,
        SHOOTING_MOVIE_EE,
        SHOOTING_MOVIE_REC,
        SHOOTING_MOVIE_REC_MENU,
        SHOOTING_MOVIE_WAIT_REC_START,
        SHOOTING_MOVIE_WAIT_REC_STOP,
        SHOOTING_MOVIE_MENU,
        SHOOTING_MOVIE_INHIBIT,
        PLAYBACK_CONTENTS_TRANSFER,
        PLAYBACK_DELETING,
        PLAYBACK_STREAMING,
        SHOOTING_START_FOCUSING,
        SHOOTING_START_FOCUSING_REMOTE,
        SHOOTING_FOCUSING,
        SHOOTING_FOCUSING_REMOTE,
        SHOOTING_STILL_SAVING,
        SHOOTING_STILL_POST_PROCESSING
    }

    /* loaded from: classes.dex */
    public enum RecordingMode {
        REC_MODE_CHANGING,
        STILL,
        MOVIE
    }

    /* loaded from: classes.dex */
    public enum ServerStatus {
        IDLE,
        NOT_READY,
        STILL_CAPTURING,
        MOVIE_RECORDING,
        MOVIE_WAIT_REC_START,
        MOVIE_WAIT_REC_STOP,
        CONTENTS_TRANSFER,
        DELETING,
        STREAMING,
        STILL_SAVING,
        STILL_POST_PROCESSING
    }

    public static StateController getInstance() {
        return sStateController;
    }

    public StateController() {
        init();
    }

    public synchronized void init() {
        this.appCondition = AppCondition.PREPARATION;
        this.servStat = ServerStatus.NOT_READY;
        this.isDdServerReady = false;
        this.isInitialWifiSetup = true;
        this.isErrorByTimeout = false;
        this.isRestartForNewConfig = false;
        this.isFatalError = false;
        this.isShooting = false;
        this.isWaitingStatusChange = false;
        this.isWaitingRecModeChange = false;
        this.isWaitingCameraFunctionChange = false;
        this.isWaitingDeletingStateChange = false;
        this.isClosingShootingState = false;
        this.hasModeDial = false;
        initOperationReceiver();
        this.mLastAppConditionBeforeCapturing = AppCondition.SHOOTING_REMOTE;
        this.recordingMode = RecordingMode.REC_MODE_CHANGING;
    }

    public synchronized void initOperationReceiver() {
        this.receiver = new OperationReceiver();
    }

    public synchronized void setState(State state) {
        this.state = state;
        Activity act = state.getActivity();
        if (act != null) {
            this.mApplicationContextRef = new WeakReference<>(act.getApplicationContext());
        }
    }

    public synchronized State getState() {
        return this.state;
    }

    public synchronized Context getApplicationContext() {
        return this.mApplicationContextRef == null ? null : this.mApplicationContextRef.get();
    }

    public synchronized boolean changeToNetworkState() {
        boolean z = false;
        synchronized (this) {
            if (checkState()) {
                if (this.appCondition.equals(AppCondition.PREPARATION)) {
                    Log.v(TAG, "Already in NwState");
                } else {
                    Log.v(TAG, "Changing to Network State");
                    setIsClosingShootingState(true);
                    if (AppCondition.SHOOTING_EE.equals(this.appCondition)) {
                        if (this.state instanceof S1OffEEStateEx) {
                            ((S1OffEEStateEx) this.state).setNextState(SRCtrlRootState.APP_NETWORK, null);
                        } else {
                            Log.e(TAG, "Inconsistent state, S1OffEEStateEx state was expected in SHOOTING_EE state.");
                        }
                    } else if (AppCondition.SHOOTING_MOVIE_EE.equals(this.appCondition)) {
                        if (this.state instanceof MovieRecStandbyStateEx) {
                            ((MovieRecStandbyStateEx) this.state).setNextState(SRCtrlRootState.APP_NETWORK, null);
                        } else {
                            Log.e(TAG, "Inconsistent state, MovieRecStandbyStateEx state was expected in SHOOTING_EE_MOVIE state.");
                        }
                    } else if (AppCondition.SHOOTING_REMOTE_TOUCHAF.equals(this.appCondition)) {
                        CameraOperationTouchAFPosition.leaveTouchAFMode(false);
                        ((S1OnEEStateForTouchAF) this.state).setNextState(SRCtrlRootState.APP_NETWORK, null);
                    } else if (AppCondition.SHOOTING_REMOTE_TOUCHAFASSIST.equals(this.appCondition)) {
                        CameraOperationTouchAFPosition.leaveTouchAFMode(false);
                        ((S1OnEEStateForTouchAFAssist) this.state).setNextState(SRCtrlRootState.APP_NETWORK, null);
                    } else if (AppCondition.SHOOTING_FOCUSING.equals(this.appCondition) || AppCondition.SHOOTING_FOCUSING_REMOTE.equals(this.appCondition)) {
                        this.state.setNextState(SRCtrlRootState.APP_NETWORK, null);
                    } else if (AppCondition.SHOOTING_INHIBIT.equals(this.appCondition) || AppCondition.SHOOTING_MOVIE_INHIBIT.equals(this.appCondition)) {
                        if (this.state instanceof MfAssistStateEx) {
                            ((MfAssistStateEx) this.state).setNextState(SRCtrlRootState.APP_NETWORK, null);
                        }
                    } else if (AppCondition.DIAL_INHIBIT.equals(this.appCondition)) {
                        ((S1OffEEStateEx) this.state).setNextState(SRCtrlRootState.APP_NETWORK, null);
                    } else if (AppCondition.SHOOTING_MENU.equals(this.appCondition) || AppCondition.SHOOTING_MOVIE_MENU.equals(this.appCondition)) {
                        ((ShootingMenuStateEx) this.state).setNextState(SRCtrlRootState.APP_NETWORK, null);
                    } else if (AppCondition.PLAYBACK_CONTENTS_TRANSFER.equals(this.appCondition)) {
                        this.state.setNextState(SRCtrlRootState.APP_NETWORK, null);
                    } else if (AppCondition.PLAYBACK_STREAMING.equals(this.appCondition)) {
                        this.state.setNextState(SRCtrlRootState.APP_NETWORK, null);
                    } else if (AppCondition.PLAYBACK_DELETING.equals(this.appCondition)) {
                        this.state.setNextState(SRCtrlRootState.APP_NETWORK, null);
                    }
                    BaseApp act = (BaseApp) this.state.getActivity();
                    if (act != null) {
                        act.dismissExitScreen();
                    }
                    z = true;
                }
            } else {
                Log.v(TAG, "State is null");
            }
        }
        return z;
    }

    public synchronized boolean changeToShootingState() {
        boolean z = false;
        synchronized (this) {
            if (checkState()) {
                if (AppCondition.PREPARATION.equals(this.appCondition)) {
                    Log.v(TAG, "Changing to Shooting State from " + AppCondition.PREPARATION.name());
                    if (4 != RunStatus.getStatus()) {
                        Log.e(TAG, "  RunStatus is not RUNNING.  State transition will be canceled.");
                    } else {
                        ((NetworkRootState) this.state).setNextState("APP_SHOOTING", null);
                        BaseApp act = (BaseApp) this.state.getActivity();
                        if (act != null) {
                            act.dismissExitScreen();
                        }
                        z = true;
                    }
                } else if (AppCondition.PLAYBACK_CONTENTS_TRANSFER.equals(this.appCondition)) {
                    Log.v(TAG, "Changing to Shooting State from " + AppCondition.PLAYBACK_CONTENTS_TRANSFER);
                    this.state.setNextState("APP_SHOOTING", null);
                    BaseApp act2 = (BaseApp) this.state.getActivity();
                    if (act2 != null) {
                        act2.dismissExitScreen();
                    }
                    z = true;
                } else if (AppCondition.PLAYBACK_STREAMING.equals(this.appCondition)) {
                    Log.v(TAG, "Changing to Shooting State from " + AppCondition.PLAYBACK_STREAMING);
                    if (StreamingLoader.isLoadingPreview()) {
                        StreamingLoader.stopObtainingImages();
                        MediaPlayerManager.getInstance().terminate();
                    }
                    this.state.setNextState("APP_SHOOTING", null);
                    BaseApp act3 = (BaseApp) this.state.getActivity();
                    if (act3 != null) {
                        act3.dismissExitScreen();
                    }
                    z = true;
                } else if (AppCondition.PLAYBACK_DELETING.equals(this.appCondition)) {
                    Log.v(TAG, "Changing to Shooting State from " + AppCondition.PLAYBACK_DELETING);
                } else if (AppCondition.SHOOTING_REMOTE_TOUCHAF.equals(this.appCondition)) {
                    if (this.state instanceof S1OnEEStateForTouchAF) {
                        Log.v(TAG, "Changing to Shoogint State from " + AppCondition.SHOOTING_REMOTE_TOUCHAF.name());
                        ((S1OnEEStateForTouchAF) this.state).setNextState("APP_SHOOTING", null);
                        z = true;
                    } else if (this.state instanceof S1OnEEStateForTouchAFAssist) {
                        Log.v(TAG, "Changing to Shoogint State from " + AppCondition.SHOOTING_REMOTE_TOUCHAFASSIST.name());
                        ((S1OnEEStateForTouchAF) this.state).setNextState("APP_SHOOTING", null);
                        z = true;
                    } else {
                        Log.e(TAG, "State is incorrect: " + this.state.toString());
                    }
                } else {
                    Log.e(TAG, "Already in ShootingState");
                }
            } else {
                Log.e(TAG, "State is null");
            }
        }
        return z;
    }

    public synchronized boolean changeToCaptureState(ShootingHandler.ShutterListenerEx listener) {
        boolean z = false;
        synchronized (this) {
            if (checkState()) {
                Log.v(TAG, "Changing to Capture State");
                KeyStatus s2OnStatus = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.S2_ON);
                if (1 == s2OnStatus.status && !(this.state instanceof S1OnEEStateForTouchAF)) {
                    Log.e(TAG, "S2 has been pushed on the camera.");
                } else if (!(this.state instanceof S1OffEEStateEx) && !(this.state instanceof S1OnEEStateEx) && !(this.state instanceof S1OnEEStateForTouchAF) && !(this.state instanceof S1OnEEStateForTouchAFAssist) && !(this.state instanceof MfAssistStateEx)) {
                    Log.e(TAG, "The current state is not S1OffEEStateEx|S1OnEEStateForTouchAF|S1OnEEStateForTouchAFAssist|MfAssistStateEx.  State=" + this.state.toString());
                } else if (1 == ExecutorCreator.getInstance().getRecordingMode()) {
                    this.listener = listener;
                    getInstance().setAppCondition(AppCondition.SHOOTING_REMOTE);
                    this.state.setNextState(CaptureStateEx.STATE_NAME, null);
                    z = true;
                } else {
                    Log.e(TAG, "Not StillRecMode");
                }
            } else {
                Log.e(TAG, "State is null");
            }
        }
        return z;
    }

    public synchronized boolean changeToS1OnEEState() {
        boolean z = true;
        synchronized (this) {
            if (checkState()) {
                if (AppCondition.SHOOTING_EE.equals(this.appCondition)) {
                    Log.v(TAG, "Changing to S1OnEEState");
                    KeyStatus s1OnStatus = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.S1_ON);
                    if (1 == s1OnStatus.status) {
                        Log.e(TAG, "S1 has been pushed on the camera.");
                    } else {
                        getInstance().setAppCondition(AppCondition.SHOOTING_START_FOCUSING_REMOTE);
                        this.state.setNextState(S1OnEEStateEx.STATE_NAME, null);
                    }
                } else {
                    z = false;
                }
            } else {
                Log.e(TAG, "State is null");
                z = false;
            }
        }
        return z;
    }

    public synchronized boolean changeToS1OffEEState() {
        boolean z;
        if (checkState()) {
            if (AppCondition.SHOOTING_REMOTE_TOUCHAF.equals(this.appCondition) || AppCondition.SHOOTING_REMOTE_TOUCHAFASSIST.equals(this.appCondition) || AppCondition.SHOOTING_MOVIE_EE.equals(this.appCondition)) {
                if (this.state instanceof S1OnEEStateForTouchAF) {
                    Log.v(TAG, "Changing to Shooting State from " + AppCondition.SHOOTING_REMOTE_TOUCHAF.name());
                    ((S1OnEEStateForTouchAF) this.state).setNextState("S1OffEE", null);
                    z = true;
                } else if (this.state instanceof S1OnEEStateForTouchAFAssist) {
                    Log.v(TAG, "Changing to Shooting State from " + AppCondition.SHOOTING_REMOTE_TOUCHAFASSIST.name());
                    ((S1OnEEStateForTouchAFAssist) this.state).setNextState("S1OffEE", null);
                    z = true;
                } else if (this.state instanceof MovieRecStandbyStateEx) {
                    Log.v(TAG, "Changing to Shooting State from " + AppCondition.SHOOTING_MOVIE_EE.name());
                    ((MovieRecStandbyStateEx) this.state).setNextState("S1OffEE", null);
                    z = true;
                } else {
                    Log.e(TAG, "State is incorrect: " + this.state.toString());
                }
            } else if (AppCondition.SHOOTING_FOCUSING.equals(this.appCondition) || AppCondition.SHOOTING_FOCUSING_REMOTE.equals(this.appCondition)) {
                Log.v(TAG, "Changing to Shooting State from " + this.appCondition);
                this.state.setNextState("S1OffEE", null);
                z = true;
            } else {
                Log.e(TAG, "State is not touch AF: " + this.state.toString());
            }
        } else {
            Log.e(TAG, "State is null");
        }
        z = false;
        return z;
    }

    public synchronized boolean changeToS1OnEEStateForTouchAF() {
        boolean z = false;
        synchronized (this) {
            if (checkState()) {
                if (AppCondition.SHOOTING_REMOTE_TOUCHAF.equals(this.appCondition) || AppCondition.SHOOTING_REMOTE_TOUCHAFASSIST.equals(this.appCondition)) {
                    if (this.state instanceof S1OnEEStateForTouchAF) {
                        Log.v(TAG, "Already in S1OnEEStateForTouchAF...  Request ignored.");
                    } else {
                        Log.v(TAG, "Changing to S1OnEEStateForTouchAF");
                        this.state.setNextState(S1OnEEStateForTouchAF.STATE_NAME, null);
                    }
                    z = true;
                } else {
                    Log.e(TAG, "Touch AF is unnablable.");
                }
            } else {
                Log.e(TAG, "State is null");
            }
        }
        return z;
    }

    public synchronized boolean changeToMovieRecStandbyState() {
        boolean z;
        if (checkState()) {
            if (AppCondition.SHOOTING_EE.equals(this.appCondition)) {
                if (this.state instanceof S1OffEEStateEx) {
                    Log.v(TAG, "Changing to Movie Rec Standby State from " + AppCondition.SHOOTING_EE.name());
                    this.state.setNextState(MovieRecStandbyStateEx.STATE_NAME, null);
                    z = true;
                } else {
                    Log.e(TAG, "State is incorrect: " + this.state.toString());
                }
            } else {
                Log.e(TAG, "State is not Still EE: " + this.state.toString());
            }
        } else {
            Log.e(TAG, "State is null");
        }
        z = false;
        return z;
    }

    public synchronized boolean changeToMovieRecStartState(ShootingHandler.MovieShutterListenerEx listener) {
        boolean z = false;
        synchronized (this) {
            if (checkState()) {
                this.movieListener = listener;
                if (AppCondition.SHOOTING_MOVIE_EE.equals(this.appCondition)) {
                    Log.v(TAG, "Changing to Movie Rec State");
                    if (this.state instanceof MovieRecStandbyStateEx) {
                        this.state.setNextState(MovieCaptureStateEx.STATE_NAME, null);
                        z = true;
                    } else {
                        Log.e(TAG, "The current state is not MovieRecStandbyStateEx.  State=" + this.state.toString());
                    }
                } else {
                    Log.e(TAG, "Shooting is Unavailable");
                }
            } else {
                Log.e(TAG, "State is null");
            }
        }
        return z;
    }

    private synchronized boolean checkState() {
        return this.state != null;
    }

    public synchronized void setAppCondition(AppCondition aAppCondition) {
        Log.v(TAG, "setAppCondition aAppCondition :" + aAppCondition);
        AppCondition currentAppCondition = this.appCondition;
        if ((AppCondition.PREPARATION.equals(this.appCondition) && AppCondition.SHOOTING_INHIBIT.equals(aAppCondition)) || ((AppCondition.PREPARATION.equals(this.appCondition) && AppCondition.SHOOTING_MOVIE_INHIBIT.equals(aAppCondition)) || AppCondition.PLAYBACK_CONTENTS_TRANSFER.equals(this.appCondition) || AppCondition.PREPARATION.equals(aAppCondition))) {
            RecModeTransitionHandler.getInstance().onChangedOperatingMode();
            SetCameraFunctionHandler.getInstance().onChangedShootingMode();
        } else if (AppCondition.PLAYBACK_CONTENTS_TRANSFER.equals(aAppCondition)) {
            SetCameraFunctionHandler.getInstance().onChangedPlaybackMode();
        }
        if (!this.appCondition.equals(aAppCondition)) {
            this.appCondition = aAppCondition;
            Log.v(TAG, "Switched AppCondition from " + currentAppCondition.name() + " to " + aAppCondition.name());
            boolean toBeNotified = ParamsGenerator.updateAvailableApiList();
            if (toBeNotified) {
                ServerEventHandler.getInstance().onServerStatusChanged();
            }
        }
        setServerStatus(aAppCondition);
    }

    private synchronized void setServerStatus(AppCondition appCondition) {
        ServerStatus currentServStat = this.servStat;
        switch (appCondition) {
            case SHOOTING_EE:
            case SHOOTING_REMOTE_TOUCHAF:
            case SHOOTING_REMOTE_TOUCHAFASSIST:
            case SHOOTING_INHIBIT:
            case SHOOTING_MENU:
            case SHOOTING_START_FOCUSING:
            case SHOOTING_START_FOCUSING_REMOTE:
            case SHOOTING_FOCUSING:
            case SHOOTING_FOCUSING_REMOTE:
                this.servStat = ServerStatus.IDLE;
                break;
            case SHOOTING_REMOTE:
            case SHOOTING_LOCAL:
                this.servStat = ServerStatus.STILL_CAPTURING;
                break;
            case SHOOTING_STILL_SAVING:
                this.servStat = ServerStatus.STILL_SAVING;
                break;
            case SHOOTING_MOVIE_EE:
            case SHOOTING_MOVIE_INHIBIT:
            case SHOOTING_MOVIE_MENU:
                this.servStat = ServerStatus.IDLE;
                break;
            case SHOOTING_MOVIE_REC:
            case SHOOTING_MOVIE_REC_MENU:
                this.servStat = ServerStatus.MOVIE_RECORDING;
                break;
            case SHOOTING_MOVIE_WAIT_REC_START:
                this.servStat = ServerStatus.MOVIE_WAIT_REC_START;
                break;
            case SHOOTING_MOVIE_WAIT_REC_STOP:
                this.servStat = ServerStatus.MOVIE_WAIT_REC_STOP;
                break;
            case PLAYBACK_CONTENTS_TRANSFER:
                this.servStat = ServerStatus.CONTENTS_TRANSFER;
                break;
            case PLAYBACK_DELETING:
                this.servStat = ServerStatus.DELETING;
                break;
            case PLAYBACK_STREAMING:
                this.servStat = ServerStatus.STREAMING;
                break;
            case SHOOTING_STILL_POST_PROCESSING:
                this.servStat = ServerStatus.STILL_POST_PROCESSING;
                break;
            default:
                this.servStat = ServerStatus.NOT_READY;
                break;
        }
        if (!this.servStat.equals(currentServStat)) {
            Log.v(TAG, "Switched ServerStatus from " + currentServStat.name() + " to " + this.servStat.name());
            ParamsGenerator.updateAvailableApiList();
            ServerEventHandler.getInstance().onServerStatusChanged();
        }
    }

    public synchronized AppCondition getAppCondition() {
        return this.appCondition;
    }

    public synchronized ServerStatus getServerStatus() {
        return this.servStat;
    }

    /* JADX WARN: Code restructure failed: missing block: B:9:0x001e, code lost:            if (com.sony.imaging.app.srctrl.util.StateController.AppCondition.PLAYBACK_STREAMING.equals(r3.appCondition) != false) goto L10;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public synchronized boolean isContentsTransfer() {
        /*
            r3 = this;
            monitor-enter(r3)
            r0 = 0
            com.sony.imaging.app.srctrl.util.StateController$AppCondition r1 = com.sony.imaging.app.srctrl.util.StateController.AppCondition.PLAYBACK_CONTENTS_TRANSFER     // Catch: java.lang.Throwable -> L23
            com.sony.imaging.app.srctrl.util.StateController$AppCondition r2 = r3.appCondition     // Catch: java.lang.Throwable -> L23
            boolean r1 = r1.equals(r2)     // Catch: java.lang.Throwable -> L23
            if (r1 != 0) goto L20
            com.sony.imaging.app.srctrl.util.StateController$AppCondition r1 = com.sony.imaging.app.srctrl.util.StateController.AppCondition.PLAYBACK_DELETING     // Catch: java.lang.Throwable -> L23
            com.sony.imaging.app.srctrl.util.StateController$AppCondition r2 = r3.appCondition     // Catch: java.lang.Throwable -> L23
            boolean r1 = r1.equals(r2)     // Catch: java.lang.Throwable -> L23
            if (r1 != 0) goto L20
            com.sony.imaging.app.srctrl.util.StateController$AppCondition r1 = com.sony.imaging.app.srctrl.util.StateController.AppCondition.PLAYBACK_STREAMING     // Catch: java.lang.Throwable -> L23
            com.sony.imaging.app.srctrl.util.StateController$AppCondition r2 = r3.appCondition     // Catch: java.lang.Throwable -> L23
            boolean r1 = r1.equals(r2)     // Catch: java.lang.Throwable -> L23
            if (r1 == 0) goto L21
        L20:
            r0 = 1
        L21:
            monitor-exit(r3)
            return r0
        L23:
            r1 = move-exception
            monitor-exit(r3)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sony.imaging.app.srctrl.util.StateController.isContentsTransfer():boolean");
    }

    public synchronized void setNotifierOnCaptureState() {
        if (Environment.isMovieAPISupported() && (this.state instanceof MovieCaptureStateEx)) {
            if (this.movieListener != null) {
                ((MovieCaptureStateEx) this.state).setNotifier(this.movieListener);
                this.movieListener = null;
            }
        } else if (this.listener != null) {
            Log.v(TAG, "setNotifierOnCaptureState");
            if (DriveModeController.getInstance().isSelfTimer()) {
                ((SelfTimerCaptureStateEx) this.state).setNotifier(this.listener);
            } else {
                ((NormalCaptureStateEx) this.state).setNotifier(this.listener);
            }
            this.listener = null;
        }
    }

    public synchronized void setGoBackFlag(boolean bool) {
        Log.v(TAG, "StateController#setGoBackFlag(bool=" + bool + ") was called.");
        this.backTransitionFlag = bool;
    }

    public synchronized boolean isWaitingBackTransition() {
        return this.backTransitionFlag;
    }

    public synchronized void setInitialWifiSetup(boolean bool) {
        this.isInitialWifiSetup = bool;
    }

    public synchronized boolean isInitialWifiSetup() {
        return this.isInitialWifiSetup;
    }

    public synchronized void setFatalError(boolean bool) {
        this.isFatalError = bool;
    }

    public synchronized boolean isFatalError() {
        return this.isFatalError;
    }

    public synchronized void setDdServerReady(boolean bool) {
        this.isDdServerReady = bool;
    }

    public synchronized boolean isDdServerReady() {
        return this.isDdServerReady;
    }

    public synchronized void setShooting(boolean bool) {
        this.isShooting = bool;
    }

    public synchronized boolean isShooting() {
        return this.isShooting;
    }

    public synchronized void setWaitingStatusChange(boolean bool) {
        this.isWaitingStatusChange = bool;
    }

    public synchronized boolean isWaitingStatusChange() {
        return this.isWaitingStatusChange;
    }

    public synchronized void setWaitingRecModeChange(boolean bool) {
        this.isWaitingRecModeChange = bool;
    }

    public synchronized boolean isWaitingRecModeChange() {
        return this.isWaitingRecModeChange;
    }

    public synchronized void setWaitingCameraFunctionChange(boolean bool) {
        this.isWaitingCameraFunctionChange = bool;
    }

    public synchronized boolean isWaitingCameraFunctionChange() {
        return this.isWaitingCameraFunctionChange;
    }

    public synchronized void setIsClosingShootingState(boolean bool) {
        this.isClosingShootingState = bool;
    }

    public synchronized boolean isClosingShootingState() {
        return this.isClosingShootingState;
    }

    public synchronized void setIsErrorByTimeout(boolean bool) {
        this.isErrorByTimeout = bool;
    }

    public synchronized boolean isErrorByTimeout() {
        return this.isErrorByTimeout;
    }

    public synchronized void setIsRestartForNewConfig(boolean bool) {
        this.isRestartForNewConfig = bool;
    }

    public synchronized boolean isRestartForNewConfig() {
        return this.isRestartForNewConfig;
    }

    public synchronized void onPauseCalled() {
        ServerEventHandler.getInstance().onPauseCalled();
        ShootingHandler.getInstance().onPauseCalled();
        RecModeTransitionHandler.getInstance().onPauseCalled();
    }

    public synchronized OperationReceiver getReceiver() {
        return this.receiver;
    }

    public synchronized void setHasModeDial(boolean bool) {
        this.hasModeDial = bool;
    }

    public synchronized boolean hasModeDial() {
        return this.hasModeDial;
    }

    public synchronized boolean isDuringCameraSetupRoutine() {
        return this.isDuringCameraSetupRoutine;
    }

    public synchronized void setDuringCameraSetupRoutine(boolean duringCameraSetupRoutine) {
        this.isDuringCameraSetupRoutine = duringCameraSetupRoutine;
    }

    public synchronized AppCondition getLastAppConditionBeforeCapturing() {
        return this.mLastAppConditionBeforeCapturing;
    }

    public synchronized void setLastAppConditionBeforeCapturing(AppCondition appCondition) {
        if (AppCondition.SHOOTING_REMOTE == appCondition || AppCondition.SHOOTING_REMOTE_TOUCHAF == appCondition || AppCondition.SHOOTING_REMOTE_TOUCHAFASSIST == appCondition) {
            this.mLastAppConditionBeforeCapturing = appCondition;
        } else {
            Log.e(TAG, "Invalid parameter: " + appCondition.name());
        }
    }

    public synchronized boolean changeToDeletingState(String[] uri) {
        boolean z;
        if (checkState()) {
            if (AppCondition.PLAYBACK_CONTENTS_TRANSFER.equals(this.appCondition)) {
                if (this.state instanceof ContentsTransferState) {
                    Log.v(TAG, "Changing to Deleting State from " + AppCondition.PLAYBACK_CONTENTS_TRANSFER);
                    Bundle data = new Bundle();
                    data.putStringArray(SRCtrlConstants.DELETE_CONTENT_LIST, uri);
                    ((ContentsTransferState) this.state).setNextState(PlayRootContainerEx.ID_DELETE_MULTIPLE, data);
                    z = true;
                } else {
                    Log.e(TAG, "State is incorrect: " + this.state.toString());
                }
            } else {
                Log.e(TAG, "State is not Still EE: " + this.state.toString());
            }
        } else {
            Log.e(TAG, "State is null");
        }
        z = false;
        return z;
    }

    public synchronized boolean changeToPlaybackState() {
        boolean z = false;
        synchronized (this) {
            if (checkState()) {
                if (AppCondition.PLAYBACK_CONTENTS_TRANSFER.equals(this.appCondition) || AppCondition.PLAYBACK_STREAMING.equals(this.appCondition) || AppCondition.PLAYBACK_DELETING.equals(this.appCondition)) {
                    Log.v(TAG, "Already in PLAYBACK_STATE");
                } else {
                    Log.v(TAG, "Changing to PlaybackState");
                    setIsClosingShootingState(true);
                    if (AppCondition.SHOOTING_EE.equals(this.appCondition)) {
                        if (this.state instanceof S1OffEEStateEx) {
                            ((S1OffEEStateEx) this.state).setNextState(SRCtrlRootState.APP_PLAYBACK, null);
                        } else {
                            Log.e(TAG, "Inconsistent state, S1OffEEStateEx state was expected in SHOOTING_EE state.");
                        }
                    } else if (AppCondition.SHOOTING_MOVIE_EE.equals(this.appCondition)) {
                        if (this.state instanceof MovieRecStandbyStateEx) {
                            ((MovieRecStandbyStateEx) this.state).setNextState(SRCtrlRootState.APP_PLAYBACK, null);
                        } else {
                            Log.e(TAG, "Inconsistent state, MovieRecStandbyStateEx state was expected in SHOOTING_EE_MOVIE state.");
                        }
                    } else if (AppCondition.SHOOTING_REMOTE_TOUCHAF.equals(this.appCondition)) {
                        CameraOperationTouchAFPosition.leaveTouchAFMode(false);
                        ((S1OnEEStateForTouchAF) this.state).setNextState(SRCtrlRootState.APP_PLAYBACK, null);
                    } else if (AppCondition.SHOOTING_REMOTE_TOUCHAFASSIST.equals(this.appCondition)) {
                        CameraOperationTouchAFPosition.leaveTouchAFMode(false);
                        ((S1OnEEStateForTouchAFAssist) this.state).setNextState(SRCtrlRootState.APP_PLAYBACK, null);
                    } else if (AppCondition.SHOOTING_INHIBIT.equals(this.appCondition)) {
                        if (this.state instanceof MfAssistStateEx) {
                            ((MfAssistStateEx) this.state).setNextState(SRCtrlRootState.APP_PLAYBACK, null);
                        }
                    } else if (AppCondition.DIAL_INHIBIT.equals(this.appCondition)) {
                        ((S1OffEEStateEx) this.state).setNextState(SRCtrlRootState.APP_PLAYBACK, null);
                    } else if (AppCondition.SHOOTING_MENU.equals(this.appCondition)) {
                        ((ShootingMenuStateEx) this.state).setNextState(SRCtrlRootState.APP_PLAYBACK, null);
                    } else if (AppCondition.PREPARATION.equals(this.appCondition)) {
                        ((NetworkRootState) this.state).setNextState(SRCtrlRootState.APP_PLAYBACK, null);
                    } else {
                        setIsClosingShootingState(false);
                    }
                    BaseApp act = (BaseApp) this.state.getActivity();
                    if (act != null) {
                        act.dismissExitScreen();
                    }
                    z = true;
                }
            } else {
                Log.v(TAG, "State is null");
            }
        }
        return z;
    }

    public synchronized void setWaitingDeletingStateChange(boolean bool) {
        this.isWaitingDeletingStateChange = bool;
    }

    public synchronized boolean isWaitingDeletingStateChange() {
        return this.isWaitingDeletingStateChange;
    }

    public synchronized void setRecordingMode(RecordingMode mode) {
        this.recordingMode = mode;
    }

    public synchronized RecordingMode getRecordingMode() {
        return this.recordingMode;
    }
}
