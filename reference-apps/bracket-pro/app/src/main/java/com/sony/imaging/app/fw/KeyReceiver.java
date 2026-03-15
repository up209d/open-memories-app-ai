package com.sony.imaging.app.fw;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.BeepUtilityIdTableBase;
import com.sony.imaging.app.util.StringBuilderThreadLocal;

/* loaded from: classes.dex */
public class KeyReceiver extends FakeFragment implements IKeyHandler, IConvertibleKeyHandler, IEachKeyHandler, IEachFunctionEventHandler {
    public static final int FULL = 2;
    public static final int PARTIAL = 1;
    private static final String log_doesnothave = "This class does not have LayoutFactory class. (Target layout name is ).";
    int depth;
    protected BaseKeyHandler mInnerHandler;
    private int mKeyBeepPattern;
    IKeyHandler keyHandler = null;
    LayoutFactory layoutFactory = null;
    ConstituentRecord constituentRecord = null;
    protected ICustomKeyMgr mCustomKeyMgr = getCustomKeyMgr();

    protected ICustomKeyMgr getCustomKeyMgr() {
        return CustomKeyMgr.getInstance();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getDepth() {
        return this.depth;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int dispatchKeyDown(int keyCode, KeyEvent event) {
        if (this.keyHandler instanceof BaseKeyHandler) {
            ((BaseKeyHandler) this.keyHandler).target = this;
        }
        int response = this.keyHandler.onKeyDown(keyCode, event);
        if (1 == response) {
            ((AppRoot) getActivity()).setCurrentState(this);
        }
        return response;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int dispatchKeyUp(int keyCode, KeyEvent event) {
        if (this.keyHandler instanceof BaseKeyHandler) {
            ((BaseKeyHandler) this.keyHandler).target = this;
        }
        return this.keyHandler.onKeyUp(keyCode, event);
    }

    public Layout getLayout(String name) {
        if (this.layoutFactory != null) {
            Layout ret = this.layoutFactory.get(name);
            return ret;
        }
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), log_doesnothave);
        builder.insert(69, name);
        Log.w(getClass().getSimpleName(), builder.toString());
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        return null;
    }

    int getBaseId() {
        return ((AppRoot) getActivity()).getRootContainerId();
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.constituentRecord = ((AppRoot) getActivity()).getConstituentRecord(getClass());
        if (this.constituentRecord != null) {
            this.keyHandler = this.constituentRecord.getKeyHandler();
            this.layoutFactory = this.constituentRecord.getLayoutFactory();
        }
        if (this.keyHandler == null) {
            this.keyHandler = this;
            this.mInnerHandler = new BaseKeyHandler(this, this, this) { // from class: com.sony.imaging.app.fw.KeyReceiver.1
                @Override // com.sony.imaging.app.fw.BaseKeyHandler
                protected String getKeyConvCategory() {
                    return KeyReceiver.this.getKeyConvCategory();
                }

                @Override // com.sony.imaging.app.fw.BaseKeyHandler
                protected KeyConverter getKeyConverter(IConvertibleKeyHandler handler) {
                    return new KeyConverter(KeyReceiver.this.getCustomKeyMgr(), handler);
                }

                @Override // com.sony.imaging.app.fw.BaseKeyHandler
                protected boolean canRepeat(int KeyCode) {
                    return KeyReceiver.this.canRepeat(KeyCode);
                }
            };
        }
        this.mKeyBeepPattern = -1;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        super.onDestroy();
        if (this.keyHandler != null) {
            if (this.keyHandler instanceof BaseKeyHandler) {
                ((BaseKeyHandler) this.keyHandler).target = null;
            }
            this.keyHandler = null;
        }
        if (this.mInnerHandler != null) {
            this.mInnerHandler = null;
        }
    }

    public void openLayout(String name) {
        openLayout(name, (Bundle) null);
    }

    public void openLayout(String name, Bundle data) {
        Layout layout = getLayout(name);
        openLayout(layout, data);
    }

    void openLayout(Layout layout, Bundle data) {
        if (layout != null) {
            layout.depth = getDepth();
            AppRoot.LayoutUpdater.add(getBaseId(), layout, layout.getClass().getSimpleName(), data);
            AppRoot.LayoutUpdater.commit();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void closeLayout(Layout layout) {
        if (layout != null) {
            AppRoot.LayoutUpdater.remove(layout);
            AppRoot.LayoutUpdater.commit();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateLayout(Layout layout, int type) {
        if (layout != null) {
            AppRoot.LayoutUpdater.update(layout, type);
            AppRoot.LayoutUpdater.commit();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateView(Layout layout) {
        if (layout != null) {
            AppRoot.LayoutUpdater.updateView(layout);
            AppRoot.LayoutUpdater.commit();
        }
    }

    public int getKeyBeepPattern() {
        return this.mKeyBeepPattern;
    }

    public boolean setKeyBeepPattern(int keyBeepPattern) {
        if (keyBeepPattern == -1) {
            return false;
        }
        this.mKeyBeepPattern = keyBeepPattern;
        return true;
    }

    public String changeKeyBeepId(KeyEvent event) {
        return BeepUtilityIdTableBase.BEEP_ID_DEFAULT;
    }

    boolean _handleMessage(Message msg) {
        if (msg.what != 305419895) {
            return handleMessage(msg);
        }
        getHandler().removeMessages(Definition.BEEP_PATTERN_CHANGED);
        return setKeyBeepPattern(-1);
    }

    public Handler getHandler() {
        return ((AppRoot) getActivity()).getHandler();
    }

    public boolean handleMessage(Message msg) {
        return false;
    }

    protected String getKeyConvCategory() {
        return null;
    }

    protected boolean canRepeat(int KeyCode) {
        return true;
    }

    public int onKeyDown(int keyCode, KeyEvent event) {
        return this.mInnerHandler.onKeyDown(keyCode, event);
    }

    public int onKeyUp(int keyCode, KeyEvent event) {
        return this.mInnerHandler.onKeyUp(keyCode, event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onModeDialTurned(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onFocusModeDialTurned(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onEVDialTurned(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onIrisDialTurned(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    public int onAfMfSlideKeyMoved(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onIRShutterKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onIR2SecKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onIRRecKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onUmRemoteS1KeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onUmRemoteS2KeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onUmRemoteRecKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onS2KeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onS1KeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onPlayBackKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onMovieRecKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onMovieRec2ndKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    public int onLensAttached(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    public int onLensDetached(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onWaterHousingAttached(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onWaterHousingDetached(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onFnKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onDial1TurnedToRight(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onDial1TurnedToLeft(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onDial2TurnedToRight(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onDial2TurnedToLeft(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onDial3TurnedToRight(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onDial3TurnedToLeft(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onRingTurnedClockwise(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onRingTurnedCounterClockwise(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onLensApertureRingTurnedToLeft(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onLensApertureRingTurnedToRight(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onShuttleTurnedToRight(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onShuttleTurnedToLeft(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onUpKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onDownKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onLeftKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onRightKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onLeftUpKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onLeftDownKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onRightUpKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onRightDownKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onCenterKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onMenuKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onDeleteKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
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
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onAELKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onEVKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onIsoKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onWBKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onDriveModeKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onSmartTeleconKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onExpandFocusKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onDispKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onPreviewKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onDigitalZoomKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onFELKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onAfRangeKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onCustomKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onCustom1KeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onCustom2KeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onWiFiKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onResetKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onShootingModeKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onPeakingKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onProjectorKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onZebraKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onModePKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onModeAKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onModeSKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onModeMKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onFocusKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onZoomTeleLeverPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onZoomWideLeverPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onIRTcResetKeyPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onLensFocusHoldPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onLensPartsOperated(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onLensPartsStateChanged(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onS1KeyReleased(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onS2KeyReleased(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onUmRemoteS1KeyReleased(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onUmRemoteS2KeyReleased(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onUmRemoteRecKeyReleased(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onCenterKeyReleased(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onUpKeyReleased(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onDownKeyReleased(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onLeftKeyReleased(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onRightKeyReleased(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onRightUpKeyReleased(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onRightDownKeyReleased(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onLeftUpKeyReleased(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onLeftDownKeyReleased(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onDeleteKeyReleased(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onAFMFKeyReleased(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onAELKeyReleased(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onEVKeyReleased(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onIsoKeyReleased(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onSmartTeleconKeyReleased(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onPreviewKeyReleased(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onDigitalZoomKeyReleased(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onFELKeyReleased(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onAfRangeKeyReleased(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onCustomKeyReleased(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onCustom1KeyReleased(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onCustom2KeyReleased(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onMovieRecKeyReleased(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onZoomTeleLeverReleased(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onZoomWideLeverReleased(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onAfMfAelPushed(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onAelFocusSlideKeyMoved(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onAfMfAelReleased(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
    }

    @Override // com.sony.imaging.app.fw.IEachKeyHandler
    public int onLensFocusHoldReleased(KeyEvent event) {
        return this.mInnerHandler.dispatchKeyConversion(event);
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

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
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

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedWaterHousing() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedWaterHousing() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    @Deprecated
    public int pushedLensFocusHold() {
        return pushedFocusHoldCustomKey();
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int changedLensPartsState() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
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

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial1ToLeft() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial1ToRight() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial2ToLeft() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial2ToRight() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial3ToLeft() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial3ToRight() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedRingClockwise() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedRingCounterClockwise() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedLensApertureRingToLeft() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedLensApertureRingToRight() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedShuttleToLeft() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedShuttleToRight() {
        return 0;
    }

    public int pushedMenuKey() {
        return 0;
    }

    public int pushedDeleteKey() {
        return 0;
    }

    public int pushedSK1Key() {
        return pushedMenuKey();
    }

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

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFELKey() {
        return 0;
    }

    public int pushedAfRangeKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCustomKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCustom1Key() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCustom2Key() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedWiFiKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
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

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedUmRemoteRecKey() {
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

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedDeleteKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedEVKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedIsoKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedSmartTeleconKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedPreviewKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedFELKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedDigitalZoomKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAfRangeKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedCustomKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedCustom1Key() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedCustom2Key() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedMovieRecKey() {
        return 0;
    }

    public int releasedZoomLeverFromTele() {
        return 0;
    }

    public int releasedZoomLeverFromWide() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    @Deprecated
    public int releasedLensFocusHold() {
        return releasedFocusHoldCustomKey();
    }

    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        return this.mInnerHandler.onConvertedKeyDown(event, func);
    }

    public int onConvertedKeyUp(KeyEvent event, IKeyFunction func) {
        return this.mInnerHandler.onConvertedKeyUp(event, func);
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedInvalidCustomKey(IKeyFunction func) {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedNoAssignedCustomKey(IKeyFunction func) {
        return -1;
    }

    public int pushedSettingFuncCustomKey(IKeyFunction func) {
        return -1;
    }

    public int pushedDispFuncKey() {
        return 0;
    }

    public int pushedGuideFuncKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
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

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieLockKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
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

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int incrementedProgramShiftCustomKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int decrementedProgramShiftCustomKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int incrementedExposureCompensationCustomKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int decrementedExposureCompensationCustomKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int incrementedApertureCustomKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int decrementedApertureCustomKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int incrementedShutterSpeedCustomKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int decrementedShutterSpeedCustomKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int incrementedIsoSensitivityCustomKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int decrementedIsoSensitivityCustomKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int incrementedTvOrAvCustomKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
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

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRShutterNotCheckDrivemodeKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRRecInhDirectRecKey() {
        return 0;
    }

    public int releasedPbZoomFuncMinus() {
        return 0;
    }

    public int releasedPbZoomFuncPlus() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedMovieLockKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
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

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedIRShutterNotCheckDrivemodeKey() {
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

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSpotAELHoldCustomKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSpotAELToggleCustomKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedTvAvChangeCustomKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedSpotAELHoldCustomKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecCustomKey() {
        return pushedMovieRecKey();
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfAelKey() {
        return 0;
    }

    public int movedAelFocusSlideKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAfMfAelKey() {
        return 0;
    }
}
