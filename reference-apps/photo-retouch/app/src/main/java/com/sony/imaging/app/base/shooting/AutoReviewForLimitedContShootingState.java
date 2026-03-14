package com.sony.imaging.app.base.shooting;

import android.os.Bundle;

/* loaded from: classes.dex */
public class AutoReviewForLimitedContShootingState extends AutoReviewState {
    protected static final String CURRENT_STATE_NAME = "AutoReviewForLimitedContShooting";

    @Override // com.sony.imaging.app.base.shooting.AutoReviewState
    protected Bundle getBundle() {
        Bundle returnBundle = new Bundle();
        returnBundle.putString(StateBase.RETURN_STATE_KEY, CURRENT_STATE_NAME);
        return returnBundle;
    }

    @Override // com.sony.imaging.app.base.shooting.AutoReviewState
    protected boolean isHoldEVDial() {
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.AutoReviewState
    protected boolean isHoldFocusModeDial() {
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.AutoReviewState
    protected boolean enableCancelAutoPictureReview() {
        return false;
    }
}
