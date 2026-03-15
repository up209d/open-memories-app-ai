package com.sony.imaging.app.fw;

import android.view.KeyEvent;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public abstract class BaseKeyHandler implements IKeyHandler, IConvertibleKeyHandler, IEachKeyHandler, IEachFunctionEventHandler {
    protected WeakReference<IEachFunctionEventHandler> mEachFuncHandler;
    protected WeakReference<IEachKeyHandler> mEachKeyHandler;
    protected KeyConverter mKeyConverter;
    protected KeyReceiver target;

    protected abstract String getKeyConvCategory();

    protected KeyConverter getKeyConverter(IConvertibleKeyHandler handler) {
        return new KeyConverter(handler);
    }

    public BaseKeyHandler() {
        this.mKeyConverter = null;
        this.mKeyConverter = getKeyConverter(this);
        this.mEachKeyHandler = new WeakReference<>(this);
        this.mEachFuncHandler = new WeakReference<>(this);
    }

    public BaseKeyHandler(IConvertibleKeyHandler key, IEachKeyHandler eachkey, IEachFunctionEventHandler eachfunc) {
        this.mKeyConverter = null;
        this.mKeyConverter = getKeyConverter(key);
        this.mEachKeyHandler = new WeakReference<>(eachkey);
        this.mEachFuncHandler = new WeakReference<>(eachfunc);
    }

    protected boolean canRepeat(int KeyCode) {
        return true;
    }

    @Override // com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        int repCount = event.getRepeatCount();
        if (repCount != 0 && !canRepeat(code)) {
            return 0;
        }
        int ret = EachKeyDispatcher.dispatchDownAction(this.mEachKeyHandler.get(), event);
        return ret;
    }

    @Override // com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        return EachKeyDispatcher.dispatchUpAction(this.mEachKeyHandler.get(), event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onModeDialTurned(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onFocusModeDialTurned(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onEVDialTurned(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onIrisDialTurned(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onAfMfSlideKeyMoved(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onIRShutterKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onIR2SecKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onIRRecKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onUmRemoteS1KeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onUmRemoteS2KeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onUmRemoteRecKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onS2KeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onS1KeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onPlayBackKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onMovieRecKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onMovieRec2ndKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onLensAttached(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onLensDetached(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onWaterHousingAttached(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onWaterHousingDetached(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onFnKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onDial1TurnedToRight(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onDial1TurnedToLeft(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onDial2TurnedToRight(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onDial2TurnedToLeft(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onDial3TurnedToRight(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onDial3TurnedToLeft(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onShuttleTurnedToRight(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onShuttleTurnedToLeft(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onRingTurnedClockwise(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onRingTurnedCounterClockwise(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onLensApertureRingTurnedToLeft(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onLensApertureRingTurnedToRight(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onUpKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onDownKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onLeftKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onRightKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onLeftUpKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onLeftDownKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onRightUpKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onRightDownKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onCenterKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onMenuKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onDeleteKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onSK1KeyPushed(KeyEvent event) {
        return onMenuKeyPushed(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onSK2KeyPushed(KeyEvent event) {
        return onDeleteKeyPushed(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onAFMFKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    public int onAELKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onEVKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onIsoKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onWBKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onDriveModeKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onSmartTeleconKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onExpandFocusKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onDispKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onPreviewKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onDigitalZoomKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onFELKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onAfRangeKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onCustomKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onCustom1KeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onCustom2KeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onWiFiKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onResetKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onShootingModeKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onPeakingKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onProjectorKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onZebraKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onModePKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onModeAKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onModeSKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onModeMKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onFocusKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onZoomTeleLeverPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onZoomWideLeverPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onIRTcResetKeyPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onLensFocusHoldPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onLensPartsOperated(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onLensPartsStateChanged(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onS1KeyReleased(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onS2KeyReleased(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onUmRemoteS1KeyReleased(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onUmRemoteS2KeyReleased(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onCenterKeyReleased(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onUpKeyReleased(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onDownKeyReleased(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onLeftKeyReleased(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onRightKeyReleased(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onRightUpKeyReleased(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onRightDownKeyReleased(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onLeftUpKeyReleased(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onLeftDownKeyReleased(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onDeleteKeyReleased(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onAFMFKeyReleased(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    public int onAELKeyReleased(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onEVKeyReleased(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onIsoKeyReleased(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onSmartTeleconKeyReleased(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onPreviewKeyReleased(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onDigitalZoomKeyReleased(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onFELKeyReleased(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onAfRangeKeyReleased(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onCustomKeyReleased(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onCustom1KeyReleased(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onCustom2KeyReleased(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onMovieRecKeyReleased(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onZoomTeleLeverReleased(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onZoomWideLeverReleased(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onAfMfAelPushed(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    public int onAelFocusSlideKeyMoved(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onAfMfAelReleased(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onLensFocusHoldReleased(KeyEvent event) {
        return dispatchKeyConversion(event);
    }

    public int pushedS1Key() {
        return 0;
    }

    public int pushedS2Key() {
        return 0;
    }

    public int pushedIRShutterKey() {
        return 0;
    }

    public int pushedIR2SecKey() {
        return 0;
    }

    public int pushedIRRecKey() {
        return 0;
    }

    public int pushedIRTcResetKey() {
        return 0;
    }

    public int pushedUmRemoteS1Key() {
        return 0;
    }

    public int pushedUmRemoteS2Key() {
        return 0;
    }

    public int pushedUmRemoteRecKey() {
        return 0;
    }

    public int pushedPlayBackKey() {
        return 0;
    }

    public int pushedMovieRecKey() {
        return 0;
    }

    public int pushedMovieRec2ndKey() {
        return pushedMovieRecKey();
    }

    public int pushedShootingModeKey() {
        return 0;
    }

    public int pushedPeakingKey() {
        return 0;
    }

    public int pushedProjectorKey() {
        return 0;
    }

    public int pushedZebraKey() {
        return 0;
    }

    public int pushedModePKey() {
        return 0;
    }

    public int pushedModeAKey() {
        return 0;
    }

    public int pushedModeSKey() {
        return 0;
    }

    public int pushedModeMKey() {
        return 0;
    }

    public int pushedFocusKey() {
        return 0;
    }

    public int pushedFnKey() {
        return 0;
    }

    public int turnedModeDial() {
        return 0;
    }

    public int turnedFocusModeDial() {
        return 0;
    }

    public int turnedEVDial() {
        return 0;
    }

    public int turnedIrisDial() {
        return 0;
    }

    public int movedAfMfSlideKey() {
        return 0;
    }

    public int pushedAELKey() {
        return 0;
    }

    public int pushedAFMFKey() {
        return 0;
    }

    public int attachedLens() {
        return 0;
    }

    public int detachedLens() {
        return 0;
    }

    public int attachedWaterHousing() {
        return 0;
    }

    public int detachedWaterHousing() {
        return 0;
    }

    @Deprecated
    public int pushedLensFocusHold() {
        return pushedFocusHoldCustomKey();
    }

    public int changedLensPartsState() {
        return 0;
    }

    public int operatedLensParts() {
        return 0;
    }

    public int pushedCenterKey() {
        return 0;
    }

    public int pushedLeftKey() {
        return 0;
    }

    public int pushedRightKey() {
        return 0;
    }

    public int pushedUpKey() {
        return 0;
    }

    public int pushedDownKey() {
        return 0;
    }

    public int pushedRightUpKey() {
        return 0;
    }

    public int pushedRightDownKey() {
        return 0;
    }

    public int pushedLeftUpKey() {
        return 0;
    }

    public int pushedLeftDownKey() {
        return 0;
    }

    public int turnedDial1ToLeft() {
        return 0;
    }

    public int turnedDial1ToRight() {
        return 0;
    }

    public int turnedDial2ToLeft() {
        return 0;
    }

    public int turnedDial2ToRight() {
        return 0;
    }

    public int turnedDial3ToLeft() {
        return 0;
    }

    public int turnedDial3ToRight() {
        return 0;
    }

    public int turnedRingClockwise() {
        return 0;
    }

    public int turnedRingCounterClockwise() {
        return 0;
    }

    public int turnedLensApertureRingToLeft() {
        return 0;
    }

    public int turnedLensApertureRingToRight() {
        return 0;
    }

    public int turnedShuttleToLeft() {
        return 0;
    }

    public int turnedShuttleToRight() {
        return 0;
    }

    public int pushedMenuKey() {
        return 0;
    }

    public int pushedDeleteKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK1Key() {
        return pushedMenuKey();
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK2Key() {
        return pushedDeleteKey();
    }

    public int pushedEVKey() {
        return 0;
    }

    public int pushedIsoKey() {
        return 0;
    }

    public int pushedWBKey() {
        return 0;
    }

    public int pushedDriveModeKey() {
        return 0;
    }

    public int pushedSmartTeleconKey() {
        return 0;
    }

    public int pushedExpandFocusKey() {
        return 0;
    }

    public int pushedDispKey() {
        return 0;
    }

    public int pushedPreviewKey() {
        return 0;
    }

    public int pushedDigitalZoomKey() {
        return 0;
    }

    public int pushedFELKey() {
        return 0;
    }

    public int pushedAfRangeKey() {
        return 0;
    }

    public int pushedCustomKey() {
        return 0;
    }

    public int pushedCustom1Key() {
        return 0;
    }

    public int pushedCustom2Key() {
        return 0;
    }

    public int pushedWiFiKey() {
        return 0;
    }

    public int pushedResetKey() {
        return 0;
    }

    public int pressedZoomLeverToTele() {
        return 0;
    }

    public int pressedZoomLeverToWide() {
        return 0;
    }

    public int releasedS1Key() {
        return 0;
    }

    public int releasedS2Key() {
        return 0;
    }

    public int releasedUmRemoteS1Key() {
        return 0;
    }

    public int releasedUmRemoteS2Key() {
        return 0;
    }

    public int releasedCenterKey() {
        return 0;
    }

    public int releasedUpKey() {
        return 0;
    }

    public int releasedDownKey() {
        return 0;
    }

    public int releasedLeftKey() {
        return 0;
    }

    public int releasedRightKey() {
        return 0;
    }

    public int releasedRightUpKey() {
        return 0;
    }

    public int releasedRightDownKey() {
        return 0;
    }

    public int releasedLeftUpKey() {
        return 0;
    }

    public int releasedLeftDownKey() {
        return 0;
    }

    public int releasedAFMFKey() {
        return 0;
    }

    public int releasedAELKey() {
        return 0;
    }

    public int releasedDeleteKey() {
        return 0;
    }

    public int releasedEVKey() {
        return 0;
    }

    public int releasedIsoKey() {
        return 0;
    }

    public int releasedSmartTeleconKey() {
        return 0;
    }

    public int releasedPreviewKey() {
        return 0;
    }

    public int releasedFELKey() {
        return 0;
    }

    public int releasedDigitalZoomKey() {
        return 0;
    }

    public int releasedAfRangeKey() {
        return 0;
    }

    public int releasedCustomKey() {
        return 0;
    }

    public int releasedCustom1Key() {
        return 0;
    }

    public int releasedCustom2Key() {
        return 0;
    }

    public int releasedMovieRecKey() {
        return 0;
    }

    public int releasedZoomLeverFromTele() {
        return 0;
    }

    public int releasedZoomLeverFromWide() {
        return 0;
    }

    @Deprecated
    public int releasedLensFocusHold() {
        return releasedFocusHoldCustomKey();
    }

    public final int dispatchKeyConversion(KeyEvent event) {
        return this.mKeyConverter.apply(event, getKeyConvCategory());
    }

    @Override // com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        return EachConvertedKeyDispatcher.dispatchDownAction(this.mEachFuncHandler.get(), event, func);
    }

    @Override // com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyUp(KeyEvent event, IKeyFunction func) {
        return EachConvertedKeyDispatcher.dispatchUpAction(this.mEachFuncHandler.get(), event, func);
    }

    public int pushedInvalidCustomKey(IKeyFunction func) {
        return 0;
    }

    public int pushedNoAssignedCustomKey(IKeyFunction func) {
        return 0;
    }

    public int pushedSettingFuncCustomKey(IKeyFunction func) {
        return 0;
    }

    public int pushedDispFuncKey() {
        return 0;
    }

    public int pushedGuideFuncKey() {
        return 0;
    }

    public int pushedResetFuncKey() {
        return 0;
    }

    public int pushedDeleteFuncKey() {
        return 0;
    }

    public int pushedPbZoomFuncMinus() {
        return 0;
    }

    public int pushedPbZoomFuncPlus() {
        return 0;
    }

    public int pushedMovieLockKey() {
        return 0;
    }

    public int pushedMovieEELockKey() {
        return 0;
    }

    public int pushedEnter5WayFuncKey() {
        return 0;
    }

    public int pushedEnterJoyStickFuncKey() {
        return 0;
    }

    public int pushedPlayIndexKey() {
        return 0;
    }

    public int pushedIRShutterNotCheckDrivemodeKey() {
        return 0;
    }

    public int pushedIRRecInhDirectRecKey() {
        return 0;
    }

    public int incrementedProgramShiftCustomKey() {
        return 0;
    }

    public int decrementedProgramShiftCustomKey() {
        return 0;
    }

    public int incrementedExposureCompensationCustomKey() {
        return 0;
    }

    public int decrementedExposureCompensationCustomKey() {
        return 0;
    }

    public int incrementedApertureCustomKey() {
        return 0;
    }

    public int decrementedApertureCustomKey() {
        return 0;
    }

    public int incrementedShutterSpeedCustomKey() {
        return 0;
    }

    public int decrementedShutterSpeedCustomKey() {
        return 0;
    }

    public int incrementedIsoSensitivityCustomKey() {
        return 0;
    }

    public int decrementedIsoSensitivityCustomKey() {
        return 0;
    }

    public int incrementedTvOrAvCustomKey() {
        return 0;
    }

    public int decrementedTvOrAvCustomKey() {
        return 0;
    }

    public int pushedAfMfHoldCustomKey() {
        return 0;
    }

    public int pushedAELHoldCustomKey() {
        return 0;
    }

    public int pushedFocusHoldCustomKey() {
        return 0;
    }

    public int pushedAfMfToggleCustomKey() {
        return 0;
    }

    public int pushedAELToggleCustomKey() {
        return 0;
    }

    public int pushedMfAssistCustomKey() {
        return 0;
    }

    public int pushedMovieRecCustomKey() {
        return pushedMovieRecKey();
    }

    public int releasedPbZoomFuncMinus() {
        return 0;
    }

    public int releasedPbZoomFuncPlus() {
        return 0;
    }

    public int releasedMovieLockKey() {
        return 0;
    }

    public int releasedMovieEELockKey() {
        return 0;
    }

    public int releasedAELHoldCustomKey() {
        return 0;
    }

    public int releasedAfMfHoldCustomKey() {
        return 0;
    }

    public int releasedFocusHoldCustomKey() {
        return 0;
    }

    public int turnedMainDialPrev() {
        return 0;
    }

    public int turnedMainDialNext() {
        return 0;
    }

    public int turnedSubDialPrev() {
        return turnedMainDialPrev();
    }

    public int turnedSubDialNext() {
        return turnedMainDialNext();
    }

    public int turnedThirdDialPrev() {
        return 0;
    }

    public int turnedThirdDialNext() {
        return 0;
    }

    public int turnedFuncRingPrev() {
        return 0;
    }

    public int turnedFuncRingNext() {
        return 0;
    }

    public int pushedSpotAELHoldCustomKey() {
        return 0;
    }

    public int pushedSpotAELToggleCustomKey() {
        return 0;
    }

    public int pushedTvAvChangeCustomKey() {
        return 0;
    }

    public int releasedSpotAELHoldCustomKey() {
        return 0;
    }

    public int releasedIRShutterNotCheckDrivemodeKey() {
        return 0;
    }

    public int pushedAfMfAelKey() {
        return 0;
    }

    public int movedAelFocusSlideKey() {
        return 0;
    }

    public int releasedAfMfAelKey() {
        return 0;
    }
}
