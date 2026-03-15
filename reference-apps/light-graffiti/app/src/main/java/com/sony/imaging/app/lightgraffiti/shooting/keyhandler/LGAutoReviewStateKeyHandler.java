package com.sony.imaging.app.lightgraffiti.shooting.keyhandler;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.shooting.trigger.AutoReviewStateKeyHandler;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.lightgraffiti.util.AppLog;
import com.sony.imaging.app.lightgraffiti.util.LGConstants;
import com.sony.imaging.app.lightgraffiti.util.LGUtility;

/* loaded from: classes.dex */
public class LGAutoReviewStateKeyHandler extends AutoReviewStateKeyHandler {
    private static final String TAG = LGAutoReviewStateKeyHandler.class.getSimpleName();

    @Override // com.sony.imaging.app.base.shooting.trigger.AutoReviewStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        Log.d(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.AutoReviewStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        if (lgPushedDiscardDialogueKey()) {
            return 1;
        }
        return super.pushedPlayBackKey();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.AutoReviewStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        if (lgPushedDiscardDialogueKey()) {
            return 1;
        }
        return super.pushedPlayBackKey();
    }

    public boolean lgPushedDiscardDialogueKey() {
        Log.d(TAG, AppLog.getMethodName());
        if (LGStateHolder.getInstance().isShootingStage3rd()) {
            State state = (State) this.target;
            Bundle bundle = new Bundle();
            bundle.putString(LGConstants.TRANSIT_FROM_WHERE, LGConstants.TRANSIT_FROM_3RD_BY_AUTO_REVIEW);
            state.setNextState("EE", bundle);
            return true;
        }
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.AutoReviewStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        if (lgPushedDiscardDialogueKey()) {
            return 1;
        }
        return super.pushedPlayBackKey();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.AutoReviewStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        if (lgPushedDiscardDialogueKey()) {
            return 1;
        }
        return super.pushedUpKey();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.AutoReviewStateKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        if (lgPushedDiscardDialogueKey()) {
            return 1;
        }
        return super.turnedSubDialNext();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.AutoReviewStateKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        if (lgPushedDiscardDialogueKey()) {
            return 1;
        }
        return super.turnedSubDialPrev();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.AutoReviewStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        if (lgPushedDiscardDialogueKey()) {
            return 1;
        }
        return super.turnedMainDialNext();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.AutoReviewStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        if (lgPushedDiscardDialogueKey()) {
            return 1;
        }
        return super.turnedMainDialPrev();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.AutoReviewStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteFuncKey() {
        if (lgPushedDiscardDialogueKey()) {
            return 1;
        }
        return super.pushedDeleteFuncKey();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.AutoReviewStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        if (lgPushedDiscardDialogueKey()) {
            return 1;
        }
        return super.pushedDeleteFuncKey();
    }

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int result;
        if (true == CustomizableFunction.Unchanged.equals(func)) {
            Log.d(TAG, AppLog.getMethodName() + " : Unchanged is through");
            result = 0;
        } else if (true == CustomizableFunction.DispChange.equals(func)) {
            Log.e(TAG, AppLog.getMethodName() + " : DispChange is not avaliable");
            lgPushedDiscardDialogueKey();
            result = 1;
        } else {
            Log.d(TAG, AppLog.getMethodName() + LogHelper.MSG_COLON + func + " : received the unknown logical event. App will do nothing reaction.");
            result = 1;
        }
        if (result == 0) {
            int result2 = super.onConvertedKeyDown(event, func);
            return result2;
        }
        return result;
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.AutoReviewStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        Log.d(TAG, "attachedLens");
        LGStateHolder.getInstance().setLensProblemFlag(true);
        LGUtility.getInstance().isLensAttachEventReady = true;
        return pushedS1Key();
    }

    @Override // com.sony.imaging.app.base.shooting.trigger.AutoReviewStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        Log.d(TAG, "detachedLens");
        LGStateHolder.getInstance().setLensProblemFlag(true);
        return pushedS1Key();
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedModeDial() {
        Log.d(TAG, AppLog.getMethodName() + " : do nothing");
        return 1;
    }
}
