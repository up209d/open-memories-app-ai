package com.sony.imaging.app.fw;

/* loaded from: classes.dex */
public interface IEachFunctionEventHandler {
    int attachedLens();

    int attachedWaterHousing();

    int decrementedApertureCustomKey();

    int decrementedExposureCompensationCustomKey();

    int decrementedIsoSensitivityCustomKey();

    int decrementedProgramShiftCustomKey();

    int decrementedShutterSpeedCustomKey();

    int decrementedTvOrAvCustomKey();

    int detachedLens();

    int detachedWaterHousing();

    int incrementedApertureCustomKey();

    int incrementedExposureCompensationCustomKey();

    int incrementedIsoSensitivityCustomKey();

    int incrementedProgramShiftCustomKey();

    int incrementedShutterSpeedCustomKey();

    int incrementedTvOrAvCustomKey();

    int movedAelFocusSlideKey();

    int movedAfMfSlideKey();

    int pressedZoomLeverToTele();

    int pressedZoomLeverToWide();

    int pushedAELHoldCustomKey();

    int pushedAELKey();

    int pushedAELToggleCustomKey();

    int pushedAFMFKey();

    int pushedAfMfAelKey();

    int pushedAfMfHoldCustomKey();

    int pushedAfMfToggleCustomKey();

    int pushedAfRangeKey();

    int pushedCenterKey();

    int pushedCustom1Key();

    int pushedCustom2Key();

    int pushedCustomKey();

    int pushedDeleteFuncKey();

    int pushedDeleteKey();

    int pushedDigitalZoomKey();

    int pushedDispFuncKey();

    int pushedDispKey();

    int pushedDownKey();

    int pushedDriveModeKey();

    int pushedEVKey();

    int pushedEnter5WayFuncKey();

    int pushedEnterJoyStickFuncKey();

    int pushedExpandFocusKey();

    int pushedFELKey();

    int pushedFnKey();

    int pushedFocusKey();

    int pushedGuideFuncKey();

    int pushedIR2SecKey();

    int pushedIRRecInhDirectRecKey();

    int pushedIRRecKey();

    int pushedIRShutterKey();

    int pushedIRShutterNotCheckDrivemodeKey();

    int pushedInvalidCustomKey(IKeyFunction iKeyFunction);

    int pushedIsoKey();

    int pushedLeftDownKey();

    int pushedLeftKey();

    int pushedLeftUpKey();

    int pushedMenuKey();

    int pushedMfAssistCustomKey();

    int pushedModeAKey();

    int pushedModeMKey();

    int pushedModePKey();

    int pushedModeSKey();

    int pushedMovieEELockKey();

    int pushedMovieLockKey();

    int pushedMovieRec2ndKey();

    int pushedMovieRecCustomKey();

    int pushedMovieRecKey();

    int pushedNoAssignedCustomKey(IKeyFunction iKeyFunction);

    int pushedPbZoomFuncMinus();

    int pushedPbZoomFuncPlus();

    int pushedPeakingKey();

    int pushedPlayBackKey();

    int pushedPlayIndexKey();

    int pushedPreviewKey();

    int pushedProjectorKey();

    int pushedResetFuncKey();

    int pushedRightDownKey();

    int pushedRightKey();

    int pushedRightUpKey();

    int pushedS1Key();

    int pushedS2Key();

    int pushedSK1Key();

    int pushedSK2Key();

    int pushedSettingFuncCustomKey(IKeyFunction iKeyFunction);

    int pushedShootingModeKey();

    int pushedSmartTeleconKey();

    @Deprecated
    int pushedSpotAELHoldCustomKey();

    @Deprecated
    int pushedSpotAELToggleCustomKey();

    int pushedTvAvChangeCustomKey();

    int pushedUmRemoteRecKey();

    int pushedUmRemoteS1Key();

    int pushedUmRemoteS2Key();

    int pushedUpKey();

    int pushedWBKey();

    int pushedZebraKey();

    int releasedAELHoldCustomKey();

    int releasedAELKey();

    int releasedAFMFKey();

    int releasedAfMfAelKey();

    int releasedAfMfHoldCustomKey();

    int releasedAfRangeKey();

    int releasedCenterKey();

    int releasedCustom1Key();

    int releasedCustom2Key();

    int releasedCustomKey();

    int releasedDeleteKey();

    int releasedDigitalZoomKey();

    int releasedDownKey();

    int releasedEVKey();

    int releasedFELKey();

    int releasedIRShutterNotCheckDrivemodeKey();

    int releasedIsoKey();

    int releasedLeftDownKey();

    int releasedLeftKey();

    int releasedLeftUpKey();

    int releasedMovieEELockKey();

    int releasedMovieLockKey();

    int releasedMovieRecKey();

    int releasedPbZoomFuncMinus();

    int releasedPbZoomFuncPlus();

    int releasedPreviewKey();

    int releasedRightDownKey();

    int releasedRightKey();

    int releasedRightUpKey();

    int releasedS1Key();

    int releasedS2Key();

    int releasedSmartTeleconKey();

    @Deprecated
    int releasedSpotAELHoldCustomKey();

    int releasedUmRemoteS1Key();

    int releasedUmRemoteS2Key();

    int releasedUpKey();

    int releasedZoomLeverFromTele();

    int releasedZoomLeverFromWide();

    int turnedDial1ToLeft();

    int turnedDial1ToRight();

    int turnedDial2ToLeft();

    int turnedDial2ToRight();

    int turnedDial3ToLeft();

    int turnedDial3ToRight();

    int turnedEVDial();

    int turnedFocusModeDial();

    int turnedFuncRingNext();

    int turnedFuncRingPrev();

    int turnedIrisDial();

    int turnedMainDialNext();

    int turnedMainDialPrev();

    int turnedModeDial();

    int turnedRingClockwise();

    int turnedRingCounterClockwise();

    int turnedShuttleToLeft();

    int turnedShuttleToRight();

    int turnedSubDialNext();

    int turnedSubDialPrev();

    int turnedThirdDialNext();

    int turnedThirdDialPrev();
}
