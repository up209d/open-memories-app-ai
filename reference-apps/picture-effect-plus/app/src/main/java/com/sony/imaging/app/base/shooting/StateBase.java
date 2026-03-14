package com.sony.imaging.app.base.shooting;

import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.fw.ContainerState;
import com.sony.imaging.app.util.ApoWrapper;

/* loaded from: classes.dex */
public class StateBase extends ContainerState {
    public static final String DEFAULT_LAYOUT = "DefaultLayout";
    public static final String FOCUS_LAYOUT = "FocusLayout";
    public static final String GUIDE_LAYOUT = "GuideLayout";
    public static final String PROGRESS_LAYOUT = "ProgressLayout";
    public static final String RETURN_STATE_KEY = "ReturnStateKey";
    public static final String S1OFF_LAYOUT = "S1OffLayout";
    private static StringBuilder mStringBuilder = new StringBuilder();

    protected final StringBuilder getStringBuilder() {
        return mStringBuilder;
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        openLayout(DEFAULT_LAYOUT);
        setKeyBeepPattern(getMyBeepPattern());
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        closeLayout(DEFAULT_LAYOUT);
        super.onPause();
    }

    protected int getMyBeepPattern() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        ApoWrapper.APO_TYPE type = ApoWrapper.APO_TYPE.NORMAL;
        if (DriveModeController.getInstance().isRemoteControl()) {
            ApoWrapper.APO_TYPE type2 = ApoWrapper.APO_TYPE.NONE;
            return type2;
        }
        return type;
    }

    @Override // com.sony.imaging.app.fw.State
    public Integer getCautionMode() {
        return 3;
    }
}
