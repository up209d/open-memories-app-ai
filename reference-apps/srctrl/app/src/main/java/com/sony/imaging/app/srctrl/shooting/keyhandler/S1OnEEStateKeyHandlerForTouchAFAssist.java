package com.sony.imaging.app.srctrl.shooting.keyhandler;

import android.view.KeyEvent;
import com.sony.imaging.app.base.shooting.trigger.MfAssistKeyHandler;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.srctrl.util.StateController;

/* loaded from: classes.dex */
public class S1OnEEStateKeyHandlerForTouchAFAssist extends MfAssistKeyHandler {
    private static final String tag = S1OnEEStateKeyHandlerForTouchAFAssist.class.getName();
    private boolean mS1KeyDown = false;

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        switch (code) {
            case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                this.mS1KeyDown = true;
                return 1;
            case AppRoot.USER_KEYCODE.S2_ON /* 518 */:
                if (!this.mS1KeyDown) {
                    return -1;
                }
                this.mS1KeyDown = false;
                StateController stateController = StateController.getInstance();
                stateController.setLastAppConditionBeforeCapturing(StateController.AppCondition.SHOOTING_REMOTE_TOUCHAF);
                return super.onKeyDown(keyCode, event);
            case AppRoot.USER_KEYCODE.S1_OFF /* 772 */:
            case AppRoot.USER_KEYCODE.S2_OFF /* 774 */:
                return -1;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        switch (code) {
            case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                if (!this.mS1KeyDown) {
                    return -1;
                }
                this.mS1KeyDown = false;
                returnToNormalEE();
                return 1;
            case AppRoot.USER_KEYCODE.S2_ON /* 518 */:
            case AppRoot.USER_KEYCODE.S1_OFF /* 772 */:
            case AppRoot.USER_KEYCODE.S2_OFF /* 774 */:
                return -1;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int scanCode = event.getScanCode();
        switch (scanCode) {
            case 103:
            case AppRoot.USER_KEYCODE.LEFT /* 105 */:
            case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
            case AppRoot.USER_KEYCODE.MENU /* 514 */:
            case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
            case AppRoot.USER_KEYCODE.S2_ON /* 518 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
            case AppRoot.USER_KEYCODE.EXPAND_FOCUS /* 607 */:
            case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
            case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
            case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
            case AppRoot.USER_KEYCODE.S1_OFF /* 772 */:
            case AppRoot.USER_KEYCODE.S2_OFF /* 774 */:
                int result = super.onConvertedKeyDown(event, func);
                return result;
            default:
                return -1;
        }
    }

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyUp(KeyEvent event, IKeyFunction func) {
        int scanCode = event.getScanCode();
        switch (scanCode) {
            case 103:
            case AppRoot.USER_KEYCODE.LEFT /* 105 */:
            case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
            case AppRoot.USER_KEYCODE.MENU /* 514 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
            case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
            case AppRoot.USER_KEYCODE.EXPAND_FOCUS /* 607 */:
            case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
            case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
            case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                int result = super.onConvertedKeyUp(event, func);
                return result;
            default:
                return -1;
        }
    }

    @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        StateController stateController = StateController.getInstance();
        stateController.setLastAppConditionBeforeCapturing(StateController.AppCondition.SHOOTING_EE);
        returnToNormalEE();
        return 1;
    }
}
