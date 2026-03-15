package com.sony.imaging.app.lightgraffiti.shooting.state;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.shooting.S1OffEEState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.lightgraffiti.shooting.LGAbstractLensStateChangeListener;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGMenuDataInitializer;
import com.sony.imaging.app.lightgraffiti.shooting.layout.LGDiscardLightTrailDialogueLayout;
import com.sony.imaging.app.lightgraffiti.util.LGConstants;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.imaging.app.util.PTag;

/* loaded from: classes.dex */
public class LGDiscardLightTrailDialogueState extends State implements LGDiscardLightTrailDialogueLayout.Callback {
    private static final String PTAG_TRANSITION_PLAYBACK = "transition from EE to playback";
    public static boolean showDiscardCaution = false;
    private LGDiscardLightTrailDialogueStateLensStateListener mLensListener = new LGDiscardLightTrailDialogueStateLensStateListener();

    /* loaded from: classes.dex */
    private class LGDiscardLightTrailDialogueStateLensStateListener extends LGAbstractLensStateChangeListener {
        private LGDiscardLightTrailDialogueStateLensStateListener() {
        }

        @Override // com.sony.imaging.app.lightgraffiti.shooting.LGAbstractLensStateChangeListener
        protected void onLensStateChanged() {
            Log.d("LGDiscardLightTrailDialogueState", "onLensStateChanged");
            LGStateHolder.getInstance().prepareShootingStage1st();
            String stage = LGStateHolder.getInstance().getShootingStage();
            LGMenuDataInitializer.initMenuData(stage);
            LGDiscardLightTrailDialogueState.this.setNextState("LensProblem", null);
        }
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        closeLayout("ID_LGLAYOUT_DISCARD_DIALOGUE");
        LGDiscardLightTrailDialogueLayout layout = (LGDiscardLightTrailDialogueLayout) getLayout("ID_LGLAYOUT_DISCARD_DIALOGUE");
        layout.setCallback(null);
        CameraNotificationManager.getInstance().removeNotificationListener(this.mLensListener);
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        if (showDiscardCaution) {
            this.data.putString(LGConstants.TRANSIT_FROM_WHERE, LGConstants.TRANSIT_FROM_3RD_BY_PB);
            showDiscardCaution = false;
        }
        if (this.data.getString(LGConstants.TRANSIT_FROM_WHERE).equals(LGConstants.TRANSIT_FROM_2ND_BY_LENSCHANGING)) {
            CameraNotificationManager.getInstance().requestNotify(LGConstants.SHOOTING_2ND_CLOSE);
            setNextState("LGFakeS1OffEEState", null);
            ExecutorCreator.getInstance().stableSequence();
            ExecutorCreator.getInstance().updateSequence();
        } else {
            openLayout("ID_LGLAYOUT_DISCARD_DIALOGUE", this.data);
            LGDiscardLightTrailDialogueLayout layout = (LGDiscardLightTrailDialogueLayout) getLayout("ID_LGLAYOUT_DISCARD_DIALOGUE");
            layout.setCallback(this);
        }
        CameraNotificationManager.getInstance().setNotificationListener(this.mLensListener);
    }

    @Override // com.sony.imaging.app.lightgraffiti.shooting.layout.LGDiscardLightTrailDialogueLayout.Callback
    public void onClose() {
        if (this.data.getString(LGConstants.TRANSIT_FROM_WHERE).equals(LGConstants.TRANSIT_FROM_3RD_BY_CENTER)) {
            this.data.putString(LGConstants.TRANSIT_FROM_WHERE, LGConstants.TRANSIT_FROM_3RD_BY_DISCARD_DIALOG);
            setNextState("EE", null);
            return;
        }
        if (this.data.getString(LGConstants.TRANSIT_FROM_WHERE).equals(LGConstants.TRANSIT_FROM_3RD_BY_PB)) {
            this.data.putString(LGConstants.TRANSIT_FROM_WHERE, LGConstants.TRANSIT_FROM_3RD_BY_DISCARD_DIALOG);
            setNextState("EE", null);
        } else if (this.data.getString(LGConstants.TRANSIT_FROM_WHERE).equals(LGConstants.TRANSIT_FROM_2ND)) {
            setNextState("LGFakeS1OffEEState", null);
            ExecutorCreator.getInstance().stableSequence();
            ExecutorCreator.getInstance().updateSequence();
        } else if (this.data.getString(LGConstants.TRANSIT_FROM_WHERE).equals(LGConstants.TRANSIT_FROM_3RD_BY_AUTO_REVIEW)) {
            this.data.putString(LGConstants.TRANSIT_FROM_WHERE, LGConstants.TRANSIT_FROM_3RD_BY_DISCARD_DIALOG);
            setNextState("EE", null);
        }
    }

    @Override // com.sony.imaging.app.lightgraffiti.shooting.layout.LGDiscardLightTrailDialogueLayout.Callback
    public void onFinish() {
        if (this.data.getString(LGConstants.TRANSIT_FROM_WHERE).equals(LGConstants.TRANSIT_FROM_3RD_BY_CENTER)) {
            setNextState(S1OffEEState.STATE_NAME, null);
            return;
        }
        if (this.data.getString(LGConstants.TRANSIT_FROM_WHERE).equals(LGConstants.TRANSIT_FROM_3RD_BY_PB) || this.data.getString(LGConstants.TRANSIT_FROM_WHERE).equals(LGConstants.TRANSIT_FROM_3RD_BY_AUTO_REVIEW)) {
            PTag.start(PTAG_TRANSITION_PLAYBACK);
            Bundle b = new Bundle();
            Bundle data = new Bundle();
            data.putParcelable(EventParcel.KEY_KEYCODE, new EventParcel(AppRoot.USER_KEYCODE.PLAYBACK));
            b.putParcelable(S1OffEEState.STATE_NAME, data);
            Activity appRoot = getActivity();
            ((BaseApp) appRoot).changeApp(BaseApp.APP_PLAY, b);
        }
    }

    @Override // com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        return ApoWrapper.APO_TYPE.NONE;
    }
}
