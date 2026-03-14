package com.sony.imaging.app.fw;

import android.util.Log;
import android.view.KeyEvent;
import com.sony.imaging.app.fw.AppRoot;

/* loaded from: classes.dex */
public class EachConvertedKeyDispatcher {
    private static final String MSG_COMMA = ", ";
    private static final String MSG_UNKNOWN_FUNC = "unknown function ";
    private static final String TAG = "ConvertedKeyDispatcher";
    private static StringBuilder mBuilder = new StringBuilder();

    public static int dispatchDownAction(IEachFunctionEventHandler handler, KeyEvent event, IKeyFunction func) {
        int ret;
        String type = func.getType();
        if (CustomizableFunction.Unchanged.equals(func)) {
            ret = onEventKeyDown(handler, event);
        } else if (type.equals(IKeyFunction.TYPE_SETTING)) {
            ret = handler.pushedSettingFuncCustomKey(func);
        } else if (type.equals(IKeyFunction.TYPE_EXEC)) {
            try {
                CustomizableFunction customizableFunc = (CustomizableFunction) func;
                switch (customizableFunc) {
                    case DispChange:
                        ret = handler.pushedDispFuncKey();
                        break;
                    case Delete:
                        ret = handler.pushedDeleteFuncKey();
                        break;
                    case MainNext:
                        ret = handler.turnedMainDialNext();
                        break;
                    case MainPrev:
                        ret = handler.turnedMainDialPrev();
                        break;
                    case SubNext:
                        ret = handler.turnedSubDialNext();
                        break;
                    case SubPrev:
                        ret = handler.turnedSubDialPrev();
                        break;
                    case ThirdNext:
                        ret = handler.turnedThirdDialNext();
                        break;
                    case ThirdPrev:
                        ret = handler.turnedThirdDialPrev();
                        break;
                    case PbZoomMinus:
                        ret = handler.pushedPbZoomFuncMinus();
                        break;
                    case PbZoomPlus:
                        ret = handler.pushedPbZoomFuncPlus();
                        break;
                    case RingNext:
                        ret = handler.turnedFuncRingNext();
                        break;
                    case RingPrev:
                        ret = handler.turnedFuncRingPrev();
                        break;
                    case Guide:
                        ret = handler.pushedGuideFuncKey();
                        break;
                    case Reset:
                        ret = handler.pushedResetFuncKey();
                        break;
                    case EeMainPrev:
                        ret = handler.turnedMainDialPrev();
                        break;
                    case EeMainNext:
                        ret = handler.turnedMainDialNext();
                        break;
                    case EeSubPrev:
                        ret = handler.turnedSubDialPrev();
                        break;
                    case EeSubNext:
                        ret = handler.turnedSubDialNext();
                        break;
                    case MovieLock:
                        ret = handler.pushedMovieLockKey();
                        break;
                    case MovieEELock:
                        ret = handler.pushedMovieEELockKey();
                        break;
                    case MovieStartStop:
                        ret = handler.pushedMovieRecCustomKey();
                        break;
                    case Enter5Way:
                        ret = handler.pushedEnter5WayFuncKey();
                        break;
                    case EnterJoyStick:
                        ret = handler.pushedEnterJoyStickFuncKey();
                        break;
                    case ShutterSpeedDecrement:
                        ret = handler.decrementedShutterSpeedCustomKey();
                        break;
                    case ShutterSpeedIncrement:
                        ret = handler.incrementedShutterSpeedCustomKey();
                        break;
                    case ApertureIncrement:
                        ret = handler.incrementedApertureCustomKey();
                        break;
                    case ApertureDecrement:
                        ret = handler.decrementedApertureCustomKey();
                        break;
                    case ExposureCompensationIncrement:
                        ret = handler.incrementedExposureCompensationCustomKey();
                        break;
                    case ExposureCompensationDecrement:
                        ret = handler.decrementedExposureCompensationCustomKey();
                        break;
                    case ProgramShiftIncrement:
                        ret = handler.incrementedProgramShiftCustomKey();
                        break;
                    case ProgramShiftDecrement:
                        ret = handler.decrementedProgramShiftCustomKey();
                        break;
                    case IsoSensitivityDecrement:
                        ret = handler.decrementedIsoSensitivityCustomKey();
                        break;
                    case IsoSensitivityIncrement:
                        ret = handler.incrementedIsoSensitivityCustomKey();
                        break;
                    case TvOrAvDec:
                        ret = handler.decrementedTvOrAvCustomKey();
                        break;
                    case TvOrAvInc:
                        ret = handler.incrementedTvOrAvCustomKey();
                        break;
                    case ScnSelection:
                        ret = handler.pushedSettingFuncCustomKey(func);
                        break;
                    case AelHold:
                        ret = handler.pushedAELHoldCustomKey();
                        break;
                    case AelToggle:
                        ret = handler.pushedAELToggleCustomKey();
                        break;
                    case SpotAelHold:
                        ret = handler.pushedSpotAELHoldCustomKey();
                        break;
                    case SpotAelToggle:
                        ret = handler.pushedSpotAELToggleCustomKey();
                        break;
                    case AfMfHold:
                        ret = handler.pushedAfMfHoldCustomKey();
                        break;
                    case AfMfToggle:
                        ret = handler.pushedAfMfToggleCustomKey();
                        break;
                    case MfAssist:
                        ret = handler.pushedMfAssistCustomKey();
                        break;
                    case TvAvChange:
                        ret = handler.pushedTvAvChangeCustomKey();
                        break;
                    case PlayIndex:
                        ret = handler.pushedPlayIndexKey();
                        break;
                    case IRShutterNotCheckDrivemode:
                        ret = handler.pushedIRShutterNotCheckDrivemodeKey();
                        break;
                    case IRRecInhDirectRec:
                        ret = handler.pushedIRRecInhDirectRecKey();
                        break;
                    default:
                        Log.w(TAG, mBuilder.replace(0, mBuilder.length(), MSG_UNKNOWN_FUNC).append(customizableFunc.getType()).append(", ").append(customizableFunc.name()).toString());
                        ret = handler.pushedInvalidCustomKey(func);
                        break;
                }
            } catch (ClassCastException e) {
                return -1;
            }
        } else if (func.equals(CustomizableFunction.NoAssign)) {
            ret = handler.pushedNoAssignedCustomKey(func);
        } else if (func.equals(CustomizableFunction.Invalid)) {
            ret = handler.pushedInvalidCustomKey(func);
        } else if (func.equals(CustomizableFunction.Unknown)) {
            try {
                CustomizableFunction customizableFunc2 = (CustomizableFunction) func;
                Log.w(TAG, mBuilder.replace(0, mBuilder.length(), MSG_UNKNOWN_FUNC).append(customizableFunc2.getType()).append(", ").append(customizableFunc2.name()).toString());
                ret = handler.pushedInvalidCustomKey(func);
            } catch (ClassCastException e2) {
                return -1;
            }
        } else {
            ret = onEventKeyDown(handler, event);
        }
        return ret;
    }

    public static int dispatchUpAction(IEachFunctionEventHandler handler, KeyEvent event, IKeyFunction func) {
        if (CustomizableFunction.Unchanged.equals(func)) {
            return onEventKeyUp(handler, event);
        }
        try {
            CustomizableFunction customizableFunc = (CustomizableFunction) func;
            switch (customizableFunc) {
                case PbZoomMinus:
                    int ret = handler.releasedPbZoomFuncMinus();
                    return ret;
                case PbZoomPlus:
                    int ret2 = handler.releasedPbZoomFuncPlus();
                    return ret2;
                case MovieLock:
                    int ret3 = handler.releasedMovieLockKey();
                    return ret3;
                case MovieEELock:
                    int ret4 = handler.releasedMovieEELockKey();
                    return ret4;
                case AelHold:
                    int ret5 = handler.releasedAELHoldCustomKey();
                    return ret5;
                case SpotAelHold:
                    int ret6 = handler.releasedSpotAELHoldCustomKey();
                    return ret6;
                case AfMfHold:
                    int ret7 = handler.releasedAfMfHoldCustomKey();
                    return ret7;
                case IRShutterNotCheckDrivemode:
                    int ret8 = handler.releasedIRShutterNotCheckDrivemodeKey();
                    return ret8;
                case Unchanged:
                    int ret9 = onEventKeyUp(handler, event);
                    return ret9;
                default:
                    int ret10 = onEventKeyUp(handler, event);
                    return ret10;
            }
        } catch (ClassCastException e) {
            return -1;
        }
    }

    protected static int onEventKeyDown(IEachFunctionEventHandler handler, KeyEvent event) {
        int scanCode = event.getScanCode();
        switch (scanCode) {
            case AppRoot.USER_KEYCODE.UP /* 103 */:
                int ret = handler.pushedUpKey();
                return ret;
            case AppRoot.USER_KEYCODE.LEFT /* 105 */:
                int ret2 = handler.pushedLeftKey();
                return ret2;
            case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
                int ret3 = handler.pushedRightKey();
                return ret3;
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                int ret4 = handler.pushedDownKey();
                return ret4;
            case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                int ret5 = handler.pushedPlayBackKey();
                return ret5;
            case AppRoot.USER_KEYCODE.SK1 /* 229 */:
                int ret6 = handler.pushedSK1Key();
                return ret6;
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                int ret7 = handler.pushedCenterKey();
                return ret7;
            case AppRoot.USER_KEYCODE.SK2 /* 513 */:
                int ret8 = handler.pushedSK2Key();
                return ret8;
            case AppRoot.USER_KEYCODE.MENU /* 514 */:
                int ret9 = handler.pushedMenuKey();
                return ret9;
            case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
                int ret10 = handler.pushedMovieRecKey();
                return ret10;
            case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                int ret11 = handler.pushedS1Key();
                return ret11;
            case AppRoot.USER_KEYCODE.S2_ON /* 518 */:
                int ret12 = handler.pushedS2Key();
                return ret12;
            case AppRoot.USER_KEYCODE.FN /* 520 */:
                int ret13 = handler.pushedFnKey();
                return ret13;
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
                int ret14 = handler.turnedShuttleToRight();
                return ret14;
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
                int ret15 = handler.turnedShuttleToLeft();
                return ret15;
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
                int ret16 = handler.turnedDial1ToRight();
                return ret16;
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
                int ret17 = handler.turnedDial1ToLeft();
                return ret17;
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
                int ret18 = handler.turnedDial2ToRight();
                return ret18;
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
                int ret19 = handler.turnedDial2ToLeft();
                return ret19;
            case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                int ret20 = handler.attachedLens();
                return ret20;
            case AppRoot.USER_KEYCODE.AEL /* 532 */:
                int ret21 = handler.pushedAELKey();
                return ret21;
            case AppRoot.USER_KEYCODE.AF_MF /* 533 */:
                int ret22 = handler.pushedAFMFKey();
                return ret22;
            case AppRoot.USER_KEYCODE.IR_SHUTTER /* 552 */:
                int ret23 = handler.pushedIRShutterKey();
                return ret23;
            case AppRoot.USER_KEYCODE.IR_SHUTTER_2SEC /* 553 */:
                int ret24 = handler.pushedIR2SecKey();
                return ret24;
            case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                int ret25 = handler.turnedModeDial();
                return ret25;
            case AppRoot.USER_KEYCODE.CUSTOM /* 588 */:
                int ret26 = handler.pushedCustomKey();
                return ret26;
            case AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED /* 589 */:
                int ret27 = handler.movedAelFocusSlideKey();
                return ret27;
            case AppRoot.USER_KEYCODE.RIGHT_UP /* 591 */:
                int ret28 = handler.pushedRightUpKey();
                return ret28;
            case AppRoot.USER_KEYCODE.RIGHT_DOWN /* 592 */:
                int ret29 = handler.pushedRightDownKey();
                return ret29;
            case AppRoot.USER_KEYCODE.LEFT_UP /* 593 */:
                int ret30 = handler.pushedLeftUpKey();
                return ret30;
            case AppRoot.USER_KEYCODE.LEFT_DOWN /* 594 */:
                int ret31 = handler.pushedLeftDownKey();
                return ret31;
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                int ret32 = handler.pushedDeleteKey();
                return ret32;
            case AppRoot.USER_KEYCODE.SLIDE_AF_MF /* 596 */:
                int ret33 = handler.movedAfMfSlideKey();
                return ret33;
            case AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED /* 597 */:
                int ret34 = handler.turnedFocusModeDial();
                return ret34;
            case AppRoot.USER_KEYCODE.EV_COMPENSATION /* 602 */:
                int ret35 = handler.pushedEVKey();
                return ret35;
            case AppRoot.USER_KEYCODE.ISO /* 603 */:
                int ret36 = handler.pushedIsoKey();
                return ret36;
            case AppRoot.USER_KEYCODE.WB /* 604 */:
                int ret37 = handler.pushedWBKey();
                return ret37;
            case AppRoot.USER_KEYCODE.DRIVE_MODE /* 605 */:
                int ret38 = handler.pushedDriveModeKey();
                return ret38;
            case AppRoot.USER_KEYCODE.SMART_TELECON /* 606 */:
                int ret39 = handler.pushedSmartTeleconKey();
                return ret39;
            case AppRoot.USER_KEYCODE.EXPAND_FOCUS /* 607 */:
                int ret40 = handler.pushedExpandFocusKey();
                return ret40;
            case AppRoot.USER_KEYCODE.DISP /* 608 */:
                int ret41 = handler.pushedDispKey();
                return ret41;
            case AppRoot.USER_KEYCODE.DIGITAL_ZOOM /* 609 */:
                int ret42 = handler.pushedDigitalZoomKey();
                return ret42;
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_TELE /* 610 */:
                int ret43 = handler.pressedZoomLeverToTele();
                return ret43;
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_WIDE /* 611 */:
                int ret44 = handler.pressedZoomLeverToWide();
                return ret44;
            case AppRoot.USER_KEYCODE.FEL /* 612 */:
                int ret45 = handler.pushedFELKey();
                return ret45;
            case AppRoot.USER_KEYCODE.UM_S1 /* 613 */:
                int ret46 = handler.pushedUmRemoteS1Key();
                return ret46;
            case AppRoot.USER_KEYCODE.AF_RANGE /* 614 */:
                int ret47 = handler.pushedAfRangeKey();
                return ret47;
            case AppRoot.USER_KEYCODE.UM_S2 /* 615 */:
                int ret48 = handler.pushedUmRemoteS2Key();
                return ret48;
            case AppRoot.USER_KEYCODE.UM_MOVIE_REC /* 616 */:
                int ret49 = handler.pushedUmRemoteRecKey();
                return ret49;
            case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                int ret50 = handler.turnedEVDial();
                return ret50;
            case AppRoot.USER_KEYCODE.IRIS_DIAL_CHANGED /* 621 */:
                int ret51 = handler.turnedIrisDial();
                return ret51;
            case AppRoot.USER_KEYCODE.CUSTOM1 /* 622 */:
                int ret52 = handler.pushedCustom1Key();
                return ret52;
            case AppRoot.USER_KEYCODE.CUSTOM2 /* 623 */:
                int ret53 = handler.pushedCustom2Key();
                return ret53;
            case AppRoot.USER_KEYCODE.SHOOTING_MODE /* 624 */:
                int ret54 = handler.pushedShootingModeKey();
                return ret54;
            case AppRoot.USER_KEYCODE.PEAKING /* 625 */:
                int ret55 = handler.pushedPeakingKey();
                return ret55;
            case AppRoot.USER_KEYCODE.PROJECTOR /* 626 */:
                int ret56 = handler.pushedProjectorKey();
                return ret56;
            case AppRoot.USER_KEYCODE.ZEBRA /* 627 */:
                int ret57 = handler.pushedZebraKey();
                return ret57;
            case AppRoot.USER_KEYCODE.MODE_P /* 628 */:
                int ret58 = handler.pushedModePKey();
                return ret58;
            case AppRoot.USER_KEYCODE.MODE_A /* 629 */:
                int ret59 = handler.pushedModeAKey();
                return ret59;
            case AppRoot.USER_KEYCODE.MODE_S /* 630 */:
                int ret60 = handler.pushedModeSKey();
                return ret60;
            case AppRoot.USER_KEYCODE.MODE_M /* 631 */:
                int ret61 = handler.pushedModeMKey();
                return ret61;
            case AppRoot.USER_KEYCODE.PREVIEW /* 632 */:
                int ret62 = handler.pushedPreviewKey();
                return ret62;
            case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
                int ret63 = handler.turnedDial3ToLeft();
                return ret63;
            case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                int ret64 = handler.turnedDial3ToRight();
                return ret64;
            case AppRoot.USER_KEYCODE.FOCUS /* 636 */:
                int ret65 = handler.pushedFocusKey();
                return ret65;
            case AppRoot.USER_KEYCODE.MOVIE_REC_2ND /* 637 */:
                int ret66 = handler.pushedMovieRec2ndKey();
                return ret66;
            case AppRoot.USER_KEYCODE.AF_MF_AEL /* 638 */:
                int ret67 = handler.pushedAfMfAelKey();
                return ret67;
            case AppRoot.USER_KEYCODE.WATER_HOUSING /* 640 */:
                int ret68 = handler.attachedWaterHousing();
                return ret68;
            case AppRoot.USER_KEYCODE.IR_MOVIE_REC /* 643 */:
                int ret69 = handler.pushedIRRecKey();
                return ret69;
            case AppRoot.USER_KEYCODE.RING_CLOCKWISE /* 648 */:
                int ret70 = handler.turnedRingClockwise();
                return ret70;
            case AppRoot.USER_KEYCODE.RING_COUNTERCW /* 649 */:
                int ret71 = handler.turnedRingCounterClockwise();
                return ret71;
            default:
                return -1;
        }
    }

    protected static int onEventKeyUp(IEachFunctionEventHandler handler, KeyEvent event) {
        int scanCode = event.getScanCode();
        switch (scanCode) {
            case AppRoot.USER_KEYCODE.UP /* 103 */:
                int ret = handler.releasedUpKey();
                return ret;
            case AppRoot.USER_KEYCODE.LEFT /* 105 */:
                int ret2 = handler.releasedLeftKey();
                return ret2;
            case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
                int ret3 = handler.releasedRightKey();
                return ret3;
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                int ret4 = handler.releasedDownKey();
                return ret4;
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                int ret5 = handler.releasedCenterKey();
                return ret5;
            case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
                int ret6 = handler.releasedMovieRecKey();
                return ret6;
            case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                int ret7 = handler.releasedS1Key();
                return ret7;
            case AppRoot.USER_KEYCODE.S2_ON /* 518 */:
                int ret8 = handler.releasedS2Key();
                return ret8;
            case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                int ret9 = handler.detachedLens();
                return ret9;
            case AppRoot.USER_KEYCODE.AEL /* 532 */:
                int ret10 = handler.releasedAELKey();
                return ret10;
            case AppRoot.USER_KEYCODE.AF_MF /* 533 */:
                int ret11 = handler.releasedAFMFKey();
                return ret11;
            case AppRoot.USER_KEYCODE.CUSTOM /* 588 */:
                int ret12 = handler.releasedCustomKey();
                return ret12;
            case AppRoot.USER_KEYCODE.RIGHT_UP /* 591 */:
                int ret13 = handler.releasedRightUpKey();
                return ret13;
            case AppRoot.USER_KEYCODE.RIGHT_DOWN /* 592 */:
                int ret14 = handler.releasedRightDownKey();
                return ret14;
            case AppRoot.USER_KEYCODE.LEFT_UP /* 593 */:
                int ret15 = handler.releasedLeftUpKey();
                return ret15;
            case AppRoot.USER_KEYCODE.LEFT_DOWN /* 594 */:
                int ret16 = handler.releasedLeftDownKey();
                return ret16;
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                int ret17 = handler.releasedDeleteKey();
                return ret17;
            case AppRoot.USER_KEYCODE.EV_COMPENSATION /* 602 */:
                int ret18 = handler.releasedEVKey();
                return ret18;
            case AppRoot.USER_KEYCODE.ISO /* 603 */:
                int ret19 = handler.releasedIsoKey();
                return ret19;
            case AppRoot.USER_KEYCODE.SMART_TELECON /* 606 */:
                int ret20 = handler.releasedSmartTeleconKey();
                return ret20;
            case AppRoot.USER_KEYCODE.DIGITAL_ZOOM /* 609 */:
                int ret21 = handler.releasedDigitalZoomKey();
                return ret21;
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_TELE /* 610 */:
                int ret22 = handler.releasedZoomLeverFromTele();
                return ret22;
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_WIDE /* 611 */:
                int ret23 = handler.releasedZoomLeverFromWide();
                return ret23;
            case AppRoot.USER_KEYCODE.FEL /* 612 */:
                int ret24 = handler.releasedFELKey();
                return ret24;
            case AppRoot.USER_KEYCODE.UM_S1 /* 613 */:
                int ret25 = handler.releasedUmRemoteS1Key();
                return ret25;
            case AppRoot.USER_KEYCODE.AF_RANGE /* 614 */:
                int ret26 = handler.releasedAfRangeKey();
                return ret26;
            case AppRoot.USER_KEYCODE.UM_S2 /* 615 */:
                int ret27 = handler.releasedUmRemoteS2Key();
                return ret27;
            case AppRoot.USER_KEYCODE.CUSTOM1 /* 622 */:
                int ret28 = handler.releasedCustom1Key();
                return ret28;
            case AppRoot.USER_KEYCODE.CUSTOM2 /* 623 */:
                int ret29 = handler.releasedCustom2Key();
                return ret29;
            case AppRoot.USER_KEYCODE.PREVIEW /* 632 */:
                int ret30 = handler.releasedPreviewKey();
                return ret30;
            case AppRoot.USER_KEYCODE.AF_MF_AEL /* 638 */:
                int ret31 = handler.releasedAfMfAelKey();
                return ret31;
            case AppRoot.USER_KEYCODE.WATER_HOUSING /* 640 */:
                int ret32 = handler.detachedWaterHousing();
                return ret32;
            default:
                return -1;
        }
    }
}
