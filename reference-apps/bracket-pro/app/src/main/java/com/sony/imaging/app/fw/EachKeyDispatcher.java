package com.sony.imaging.app.fw;

import android.view.KeyEvent;
import com.sony.imaging.app.fw.AppRoot;

/* loaded from: classes.dex */
public class EachKeyDispatcher {
    public static int dispatchDownAction(IEachKeyHandler handler, KeyEvent event) {
        int code = event.getScanCode();
        switch (code) {
            case 103:
                int ret = handler.onUpKeyPushed(event);
                return ret;
            case AppRoot.USER_KEYCODE.LEFT /* 105 */:
                int ret2 = handler.onLeftKeyPushed(event);
                return ret2;
            case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
                int ret3 = handler.onRightKeyPushed(event);
                return ret3;
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                int ret4 = handler.onDownKeyPushed(event);
                return ret4;
            case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                int ret5 = handler.onPlayBackKeyPushed(event);
                return ret5;
            case AppRoot.USER_KEYCODE.SK1 /* 229 */:
                int ret6 = handler.onSK1KeyPushed(event);
                return ret6;
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                int ret7 = handler.onCenterKeyPushed(event);
                return ret7;
            case AppRoot.USER_KEYCODE.SK2 /* 513 */:
                int ret8 = handler.onSK2KeyPushed(event);
                return ret8;
            case AppRoot.USER_KEYCODE.MENU /* 514 */:
                int ret9 = handler.onMenuKeyPushed(event);
                return ret9;
            case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
                int ret10 = handler.onMovieRecKeyPushed(event);
                return ret10;
            case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                int ret11 = handler.onS1KeyPushed(event);
                return ret11;
            case AppRoot.USER_KEYCODE.S2_ON /* 518 */:
                int ret12 = handler.onS2KeyPushed(event);
                return ret12;
            case AppRoot.USER_KEYCODE.EYE_SENSER /* 519 */:
                return -1;
            case AppRoot.USER_KEYCODE.FN /* 520 */:
                int ret13 = handler.onFnKeyPushed(event);
                return ret13;
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
                int ret14 = handler.onShuttleTurnedToRight(event);
                return ret14;
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
                int ret15 = handler.onShuttleTurnedToLeft(event);
                return ret15;
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
                int ret16 = handler.onDial1TurnedToRight(event);
                return ret16;
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
                int ret17 = handler.onDial1TurnedToLeft(event);
                return ret17;
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
                int ret18 = handler.onDial2TurnedToRight(event);
                return ret18;
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
                int ret19 = handler.onDial2TurnedToLeft(event);
                return ret19;
            case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                int ret20 = handler.onLensAttached(event);
                return ret20;
            case AppRoot.USER_KEYCODE.AEL /* 532 */:
                int ret21 = handler.onAELKeyPushed(event);
                return ret21;
            case AppRoot.USER_KEYCODE.AF_MF /* 533 */:
                int ret22 = handler.onAFMFKeyPushed(event);
                return ret22;
            case AppRoot.USER_KEYCODE.IR_SHUTTER /* 552 */:
                int ret23 = handler.onIRShutterKeyPushed(event);
                return ret23;
            case AppRoot.USER_KEYCODE.IR_SHUTTER_2SEC /* 553 */:
                int ret24 = handler.onIR2SecKeyPushed(event);
                return ret24;
            case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                int ret25 = handler.onModeDialTurned(event);
                return ret25;
            case AppRoot.USER_KEYCODE.CUSTOM /* 588 */:
                int ret26 = handler.onCustomKeyPushed(event);
                return ret26;
            case AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED /* 589 */:
                int ret27 = handler.onAelFocusSlideKeyMoved(event);
                return ret27;
            case AppRoot.USER_KEYCODE.RIGHT_UP /* 591 */:
                int ret28 = handler.onRightUpKeyPushed(event);
                return ret28;
            case AppRoot.USER_KEYCODE.RIGHT_DOWN /* 592 */:
                int ret29 = handler.onRightDownKeyPushed(event);
                return ret29;
            case AppRoot.USER_KEYCODE.LEFT_UP /* 593 */:
                int ret30 = handler.onLeftUpKeyPushed(event);
                return ret30;
            case AppRoot.USER_KEYCODE.LEFT_DOWN /* 594 */:
                int ret31 = handler.onLeftDownKeyPushed(event);
                return ret31;
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                int ret32 = handler.onDeleteKeyPushed(event);
                return ret32;
            case AppRoot.USER_KEYCODE.SLIDE_AF_MF /* 596 */:
                int ret33 = handler.onAfMfSlideKeyMoved(event);
                return ret33;
            case AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED /* 597 */:
                int ret34 = handler.onFocusModeDialTurned(event);
                return ret34;
            case AppRoot.USER_KEYCODE.EV_COMPENSATION /* 602 */:
                int ret35 = handler.onEVKeyPushed(event);
                return ret35;
            case AppRoot.USER_KEYCODE.ISO /* 603 */:
                int ret36 = handler.onIsoKeyPushed(event);
                return ret36;
            case AppRoot.USER_KEYCODE.WB /* 604 */:
                int ret37 = handler.onWBKeyPushed(event);
                return ret37;
            case AppRoot.USER_KEYCODE.DRIVE_MODE /* 605 */:
                int ret38 = handler.onDriveModeKeyPushed(event);
                return ret38;
            case AppRoot.USER_KEYCODE.SMART_TELECON /* 606 */:
                int ret39 = handler.onSmartTeleconKeyPushed(event);
                return ret39;
            case AppRoot.USER_KEYCODE.EXPAND_FOCUS /* 607 */:
                int ret40 = handler.onExpandFocusKeyPushed(event);
                return ret40;
            case AppRoot.USER_KEYCODE.DISP /* 608 */:
                int ret41 = handler.onDispKeyPushed(event);
                return ret41;
            case AppRoot.USER_KEYCODE.DIGITAL_ZOOM /* 609 */:
                int ret42 = handler.onDigitalZoomKeyPushed(event);
                return ret42;
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_TELE /* 610 */:
                int ret43 = handler.onZoomTeleLeverPushed(event);
                return ret43;
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_WIDE /* 611 */:
                int ret44 = handler.onZoomWideLeverPushed(event);
                return ret44;
            case AppRoot.USER_KEYCODE.FEL /* 612 */:
                int ret45 = handler.onFELKeyPushed(event);
                return ret45;
            case AppRoot.USER_KEYCODE.UM_S1 /* 613 */:
                int ret46 = handler.onUmRemoteS1KeyPushed(event);
                return ret46;
            case AppRoot.USER_KEYCODE.AF_RANGE /* 614 */:
                int ret47 = handler.onAfRangeKeyPushed(event);
                return ret47;
            case AppRoot.USER_KEYCODE.UM_S2 /* 615 */:
                int ret48 = handler.onUmRemoteS2KeyPushed(event);
                return ret48;
            case AppRoot.USER_KEYCODE.UM_MOVIE_REC /* 616 */:
                int ret49 = handler.onUmRemoteRecKeyPushed(event);
                return ret49;
            case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                int ret50 = handler.onEVDialTurned(event);
                return ret50;
            case AppRoot.USER_KEYCODE.IRIS_DIAL_CHANGED /* 621 */:
                int ret51 = handler.onIrisDialTurned(event);
                return ret51;
            case AppRoot.USER_KEYCODE.CUSTOM1 /* 622 */:
                int ret52 = handler.onCustom1KeyPushed(event);
                return ret52;
            case AppRoot.USER_KEYCODE.CUSTOM2 /* 623 */:
                int ret53 = handler.onCustom2KeyPushed(event);
                return ret53;
            case AppRoot.USER_KEYCODE.SHOOTING_MODE /* 624 */:
                int ret54 = handler.onShootingModeKeyPushed(event);
                return ret54;
            case AppRoot.USER_KEYCODE.PEAKING /* 625 */:
                int ret55 = handler.onPeakingKeyPushed(event);
                return ret55;
            case AppRoot.USER_KEYCODE.PROJECTOR /* 626 */:
                int ret56 = handler.onProjectorKeyPushed(event);
                return ret56;
            case AppRoot.USER_KEYCODE.ZEBRA /* 627 */:
                int ret57 = handler.onZebraKeyPushed(event);
                return ret57;
            case AppRoot.USER_KEYCODE.MODE_P /* 628 */:
                int ret58 = handler.onModePKeyPushed(event);
                return ret58;
            case AppRoot.USER_KEYCODE.MODE_A /* 629 */:
                int ret59 = handler.onModeAKeyPushed(event);
                return ret59;
            case AppRoot.USER_KEYCODE.MODE_S /* 630 */:
                int ret60 = handler.onModeSKeyPushed(event);
                return ret60;
            case AppRoot.USER_KEYCODE.MODE_M /* 631 */:
                int ret61 = handler.onModeMKeyPushed(event);
                return ret61;
            case AppRoot.USER_KEYCODE.PREVIEW /* 632 */:
                int ret62 = handler.onPreviewKeyPushed(event);
                return ret62;
            case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
                int ret63 = handler.onDial3TurnedToLeft(event);
                return ret63;
            case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                int ret64 = handler.onDial3TurnedToRight(event);
                return ret64;
            case AppRoot.USER_KEYCODE.FOCUS /* 636 */:
                int ret65 = handler.onFocusKeyPushed(event);
                return ret65;
            case AppRoot.USER_KEYCODE.MOVIE_REC_2ND /* 637 */:
                int ret66 = handler.onMovieRec2ndKeyPushed(event);
                return ret66;
            case AppRoot.USER_KEYCODE.AF_MF_AEL /* 638 */:
                int ret67 = handler.onAfMfAelPushed(event);
                return ret67;
            case AppRoot.USER_KEYCODE.WATER_HOUSING /* 640 */:
                int ret68 = handler.onWaterHousingAttached(event);
                return ret68;
            case AppRoot.USER_KEYCODE.IR_MOVIE_REC /* 643 */:
                int ret69 = handler.onIRRecKeyPushed(event);
                return ret69;
            case AppRoot.USER_KEYCODE.RING_CLOCKWISE /* 648 */:
                int ret70 = handler.onRingTurnedClockwise(event);
                return ret70;
            case AppRoot.USER_KEYCODE.RING_COUNTERCW /* 649 */:
                int ret71 = handler.onRingTurnedCounterClockwise(event);
                return ret71;
            case AppRoot.USER_KEYCODE.LENS_APERTURE_RING_RIGHT /* 651 */:
                int ret72 = handler.onLensApertureRingTurnedToRight(event);
                return ret72;
            case AppRoot.USER_KEYCODE.LENS_APERTURE_RING_LEFT /* 652 */:
                int ret73 = handler.onLensApertureRingTurnedToLeft(event);
                return ret73;
            case AppRoot.USER_KEYCODE.LENS_FOCUS_HOLD /* 653 */:
                int ret74 = handler.onLensFocusHoldPushed(event);
                return ret74;
            case AppRoot.USER_KEYCODE.LENS_PARTS_STATE /* 654 */:
                int ret75 = handler.onLensPartsStateChanged(event);
                return ret75;
            case AppRoot.USER_KEYCODE.LENS_PARTS_OPERATION /* 655 */:
                int ret76 = handler.onLensPartsOperated(event);
                return ret76;
            case AppRoot.USER_KEYCODE.IR_TC_RESET /* 656 */:
                int ret77 = handler.onIRTcResetKeyPushed(event);
                return ret77;
            case AppRoot.USER_KEYCODE.WIFI /* 657 */:
                int ret78 = handler.onWiFiKeyPushed(event);
                return ret78;
            case AppRoot.USER_KEYCODE.RESET /* 658 */:
                int ret79 = handler.onResetKeyPushed(event);
                return ret79;
            default:
                return 0;
        }
    }

    public static int dispatchUpAction(IEachKeyHandler handler, KeyEvent event) {
        int code = event.getScanCode();
        switch (code) {
            case 103:
                int ret = handler.onUpKeyReleased(event);
                return ret;
            case AppRoot.USER_KEYCODE.LEFT /* 105 */:
                int ret2 = handler.onLeftKeyReleased(event);
                return ret2;
            case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
                int ret3 = handler.onRightKeyReleased(event);
                return ret3;
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                int ret4 = handler.onDownKeyReleased(event);
                return ret4;
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                int ret5 = handler.onCenterKeyReleased(event);
                return ret5;
            case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
                int ret6 = handler.onMovieRecKeyReleased(event);
                return ret6;
            case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                int ret7 = handler.onS1KeyReleased(event);
                return ret7;
            case AppRoot.USER_KEYCODE.S2_ON /* 518 */:
                int ret8 = handler.onS2KeyReleased(event);
                return ret8;
            case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                int ret9 = handler.onLensDetached(event);
                return ret9;
            case AppRoot.USER_KEYCODE.AEL /* 532 */:
                int ret10 = handler.onAELKeyReleased(event);
                return ret10;
            case AppRoot.USER_KEYCODE.AF_MF /* 533 */:
                int ret11 = handler.onAFMFKeyReleased(event);
                return ret11;
            case AppRoot.USER_KEYCODE.CUSTOM /* 588 */:
                int ret12 = handler.onCustomKeyReleased(event);
                return ret12;
            case AppRoot.USER_KEYCODE.RIGHT_UP /* 591 */:
                int ret13 = handler.onRightUpKeyReleased(event);
                return ret13;
            case AppRoot.USER_KEYCODE.RIGHT_DOWN /* 592 */:
                int ret14 = handler.onRightDownKeyReleased(event);
                return ret14;
            case AppRoot.USER_KEYCODE.LEFT_UP /* 593 */:
                int ret15 = handler.onLeftUpKeyReleased(event);
                return ret15;
            case AppRoot.USER_KEYCODE.LEFT_DOWN /* 594 */:
                int ret16 = handler.onLeftDownKeyReleased(event);
                return ret16;
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                int ret17 = handler.onDeleteKeyReleased(event);
                return ret17;
            case AppRoot.USER_KEYCODE.EV_COMPENSATION /* 602 */:
                int ret18 = handler.onEVKeyReleased(event);
                return ret18;
            case AppRoot.USER_KEYCODE.ISO /* 603 */:
                int ret19 = handler.onIsoKeyReleased(event);
                return ret19;
            case AppRoot.USER_KEYCODE.SMART_TELECON /* 606 */:
                int ret20 = handler.onSmartTeleconKeyReleased(event);
                return ret20;
            case AppRoot.USER_KEYCODE.DIGITAL_ZOOM /* 609 */:
                int ret21 = handler.onDigitalZoomKeyReleased(event);
                return ret21;
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_TELE /* 610 */:
                int ret22 = handler.onZoomTeleLeverReleased(event);
                return ret22;
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_WIDE /* 611 */:
                int ret23 = handler.onZoomWideLeverReleased(event);
                return ret23;
            case AppRoot.USER_KEYCODE.FEL /* 612 */:
                int ret24 = handler.onFELKeyReleased(event);
                return ret24;
            case AppRoot.USER_KEYCODE.UM_S1 /* 613 */:
                int ret25 = handler.onUmRemoteS1KeyReleased(event);
                return ret25;
            case AppRoot.USER_KEYCODE.AF_RANGE /* 614 */:
                int ret26 = handler.onAfRangeKeyReleased(event);
                return ret26;
            case AppRoot.USER_KEYCODE.UM_S2 /* 615 */:
                int ret27 = handler.onUmRemoteS2KeyReleased(event);
                return ret27;
            case AppRoot.USER_KEYCODE.UM_MOVIE_REC /* 616 */:
                int ret28 = handler.onUmRemoteRecKeyReleased(event);
                return ret28;
            case AppRoot.USER_KEYCODE.CUSTOM1 /* 622 */:
                int ret29 = handler.onCustom1KeyReleased(event);
                return ret29;
            case AppRoot.USER_KEYCODE.CUSTOM2 /* 623 */:
                int ret30 = handler.onCustom2KeyReleased(event);
                return ret30;
            case AppRoot.USER_KEYCODE.PREVIEW /* 632 */:
                int ret31 = handler.onPreviewKeyReleased(event);
                return ret31;
            case AppRoot.USER_KEYCODE.AF_MF_AEL /* 638 */:
                int ret32 = handler.onAfMfAelReleased(event);
                return ret32;
            case AppRoot.USER_KEYCODE.WATER_HOUSING /* 640 */:
                int ret33 = handler.onWaterHousingDetached(event);
                return ret33;
            case AppRoot.USER_KEYCODE.LENS_FOCUS_HOLD /* 653 */:
                int ret34 = handler.onLensFocusHoldReleased(event);
                return ret34;
            default:
                return 0;
        }
    }
}
