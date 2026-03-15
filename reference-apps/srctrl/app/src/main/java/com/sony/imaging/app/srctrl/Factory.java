package com.sony.imaging.app.srctrl;

import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.caution.CautionDisplayState;
import com.sony.imaging.app.base.caution.CautionFragment;
import com.sony.imaging.app.base.caution.CautionLayout;
import com.sony.imaging.app.base.common.ExitScreenState;
import com.sony.imaging.app.base.common.ForceExitScreenState;
import com.sony.imaging.app.base.common.layout.ExitScreenLayout;
import com.sony.imaging.app.base.common.layout.ForceExitScreenLayout;
import com.sony.imaging.app.base.menu.layout.AFFlexiblePositionMenuLayout;
import com.sony.imaging.app.base.menu.layout.AFLocalPositionMenuLayout;
import com.sony.imaging.app.base.menu.layout.CreativeStyleMenuLayout;
import com.sony.imaging.app.base.menu.layout.DigitalZoomSettingLayout;
import com.sony.imaging.app.base.menu.layout.ExposureCompensationMenuLayout;
import com.sony.imaging.app.base.menu.layout.FlashCompensationMenuLayout;
import com.sony.imaging.app.base.menu.layout.Fn15LayerCreativeStyleLayout;
import com.sony.imaging.app.base.menu.layout.Fn15LayerExposureCompensationLayout;
import com.sony.imaging.app.base.menu.layout.Fn15LayerFlashCompensationLayout;
import com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout;
import com.sony.imaging.app.base.menu.layout.Fn15LayerWhiteBalanceLayout;
import com.sony.imaging.app.base.menu.layout.FnMenuLayout;
import com.sony.imaging.app.base.menu.layout.FocusModeSpecialScreenMenuLayout;
import com.sony.imaging.app.base.menu.layout.ISOFn15LayerMenuLayout;
import com.sony.imaging.app.base.menu.layout.ISOSpecialScreenMenuLayout;
import com.sony.imaging.app.base.menu.layout.PageMenuLayout;
import com.sony.imaging.app.base.menu.layout.SceneSelectionMenuLayout;
import com.sony.imaging.app.base.menu.layout.SetMenuLayout;
import com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout;
import com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayoutHiddenEE;
import com.sony.imaging.app.base.menu.layout.UnknownItemMenuLayout;
import com.sony.imaging.app.base.menu.layout.WhiteBalanceAdjustmentMenuLayout;
import com.sony.imaging.app.base.menu.layout.WhiteBalanceMenuLayout;
import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.base.playback.TransitionToShooting;
import com.sony.imaging.app.base.playback.base.IndexStateBase;
import com.sony.imaging.app.base.playback.base.PlaySubApp;
import com.sony.imaging.app.base.playback.layout.BrowserSingleNoBufferLayout;
import com.sony.imaging.app.base.shooting.AutoReviewForLimitedContShootingState;
import com.sony.imaging.app.base.shooting.AutoReviewState;
import com.sony.imaging.app.base.shooting.CustomWhiteBalanceCaptureState;
import com.sony.imaging.app.base.shooting.CustomWhiteBalanceControllerState;
import com.sony.imaging.app.base.shooting.CustomWhiteBalanceEEState;
import com.sony.imaging.app.base.shooting.CustomWhiteBalanceExposureState;
import com.sony.imaging.app.base.shooting.StateBase;
import com.sony.imaging.app.base.shooting.layout.AutoReviewForLimitedContShootingLayout;
import com.sony.imaging.app.base.shooting.layout.CustomWhiteBalanceConfLayout;
import com.sony.imaging.app.base.shooting.layout.CustomWhiteBalanceEELayout;
import com.sony.imaging.app.base.shooting.layout.FocusAdjustmentLayout;
import com.sony.imaging.app.base.shooting.layout.FocusLayout;
import com.sony.imaging.app.base.shooting.layout.GuideLayout;
import com.sony.imaging.app.base.shooting.layout.MfAssistLayout;
import com.sony.imaging.app.base.shooting.layout.S1OffLayout;
import com.sony.imaging.app.base.shooting.layout.ShootingLayout;
import com.sony.imaging.app.base.shooting.movie.MovieSaveState;
import com.sony.imaging.app.base.shooting.movie.layout.MovieSaveLayout;
import com.sony.imaging.app.base.shooting.movie.trigger.MovieCaptureStateKeyHandler;
import com.sony.imaging.app.base.shooting.movie.trigger.MovieRecStateKeyHandler;
import com.sony.imaging.app.base.shooting.movie.trigger.MovieSaveStateKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.AutoReviewForLimitedContShootingStateKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.CustomWhiteBalanceCaptureKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.CustomWhiteBalanceEEKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.CustomWhiteBalanceExposureKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.EEStateKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.FocusAdjustmentKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.S1OnEEStateKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.SelfTimerCaptureStateKeyHandler;
import com.sony.imaging.app.fw.LayoutFactory;
import com.sony.imaging.app.fw.StateFactory;
import com.sony.imaging.app.srctrl.menu.layout.ExposureModeMenuLayoutEx;
import com.sony.imaging.app.srctrl.menu.layout.LastBastionMenuLayoutEx;
import com.sony.imaging.app.srctrl.network.NetworkRootState;
import com.sony.imaging.app.srctrl.network.message.confirmation.NwConfirmationKeyHandler;
import com.sony.imaging.app.srctrl.network.message.confirmation.NwConfirmationLayout;
import com.sony.imaging.app.srctrl.network.message.confirmation.NwConfirmationState;
import com.sony.imaging.app.srctrl.network.message.fatal.NwFatalErrorKeyHandler;
import com.sony.imaging.app.srctrl.network.message.fatal.NwFatalErrorLayout;
import com.sony.imaging.app.srctrl.network.message.fatal.NwFatalErrorState;
import com.sony.imaging.app.srctrl.network.message.wpserror.NwWpsErrorKeyHandler;
import com.sony.imaging.app.srctrl.network.message.wpserror.NwWpsErrorState;
import com.sony.imaging.app.srctrl.network.message.wpserror.NwWpsInvalidLayout;
import com.sony.imaging.app.srctrl.network.message.wpserror.NwWpsTimeoutLayout;
import com.sony.imaging.app.srctrl.network.progress.connected.NwConnectedKeyHandler;
import com.sony.imaging.app.srctrl.network.progress.connected.NwConnectedLayout;
import com.sony.imaging.app.srctrl.network.progress.connected.NwConnectedState;
import com.sony.imaging.app.srctrl.network.progress.registering.NwWpsRegisteringKeyHandler;
import com.sony.imaging.app.srctrl.network.progress.registering.NwWpsRegisteringLayout;
import com.sony.imaging.app.srctrl.network.progress.registering.NwWpsRegisteringState;
import com.sony.imaging.app.srctrl.network.progress.restarting.RestartingKeyHandler;
import com.sony.imaging.app.srctrl.network.progress.restarting.RestartingLayout;
import com.sony.imaging.app.srctrl.network.progress.restarting.RestartingState;
import com.sony.imaging.app.srctrl.network.standby.standby.NwStandbyKeyHandler;
import com.sony.imaging.app.srctrl.network.standby.standby.NwStandbyLayout;
import com.sony.imaging.app.srctrl.network.standby.standby.NwStandbyState;
import com.sony.imaging.app.srctrl.network.waiting.waiting.NwWaitingKeyHandler;
import com.sony.imaging.app.srctrl.network.waiting.waiting.NwWaitingLayout;
import com.sony.imaging.app.srctrl.network.waiting.waiting.NwWaitingQRCodeLayout;
import com.sony.imaging.app.srctrl.network.waiting.waiting.NwWaitingState;
import com.sony.imaging.app.srctrl.network.wpspbc.pbc.NwWpsPbcKeyHandler;
import com.sony.imaging.app.srctrl.network.wpspbc.pbc.NwWpsPbcLayout;
import com.sony.imaging.app.srctrl.network.wpspbc.pbc.NwWpsPbcState;
import com.sony.imaging.app.srctrl.network.wpspin.input.NwWpsPinInputKeyHandler;
import com.sony.imaging.app.srctrl.network.wpspin.input.NwWpsPinInputLayout;
import com.sony.imaging.app.srctrl.network.wpspin.input.NwWpsPinInputState;
import com.sony.imaging.app.srctrl.network.wpspin.print.NwWpsPinPrintKeyHandler;
import com.sony.imaging.app.srctrl.network.wpspin.print.NwWpsPinPrintLayout;
import com.sony.imaging.app.srctrl.network.wpspin.print.NwWpsPinPrintState;
import com.sony.imaging.app.srctrl.playback.PlayRootContainerEx;
import com.sony.imaging.app.srctrl.playback.browser.BrowserEx;
import com.sony.imaging.app.srctrl.playback.browser.ContentsTransferState;
import com.sony.imaging.app.srctrl.playback.delete.multiple.DeleteMultiple;
import com.sony.imaging.app.srctrl.playback.layout.BrowserSingleNoBufferLayoutEx;
import com.sony.imaging.app.srctrl.playback.layout.PlaybackLayout;
import com.sony.imaging.app.srctrl.shooting.SRCtrlForceSettingState;
import com.sony.imaging.app.srctrl.shooting.keyhandler.AutoReviewStateKeyHandlerEx;
import com.sony.imaging.app.srctrl.shooting.keyhandler.CaptureStateKeyHandlerEx;
import com.sony.imaging.app.srctrl.shooting.keyhandler.DevelopmentStateKeyHandlerEx;
import com.sony.imaging.app.srctrl.shooting.keyhandler.MfAssistKeyHandlerEx;
import com.sony.imaging.app.srctrl.shooting.keyhandler.MovieRecStandbyStateKeyHandlerEx;
import com.sony.imaging.app.srctrl.shooting.keyhandler.NormalCaptureStateKeyHandlerEx;
import com.sony.imaging.app.srctrl.shooting.keyhandler.S1OffEEStateKeyHandlerEx;
import com.sony.imaging.app.srctrl.shooting.keyhandler.S1OnEEStateKeyHandlerForTouchAF;
import com.sony.imaging.app.srctrl.shooting.keyhandler.S1OnEEStateKeyHandlerForTouchAFAssist;
import com.sony.imaging.app.srctrl.shooting.keyhandler.ShootingStateKeyHandlerEx;
import com.sony.imaging.app.srctrl.shooting.layout.AutoReviewLayoutEx;
import com.sony.imaging.app.srctrl.shooting.layout.MovieModeMenuLayoutEx;
import com.sony.imaging.app.srctrl.shooting.layout.ProgressLayoutEx;
import com.sony.imaging.app.srctrl.shooting.layout.SRCtrlStableLayout;
import com.sony.imaging.app.srctrl.shooting.state.CaptureStateEx;
import com.sony.imaging.app.srctrl.shooting.state.DevelopmentStateExContShootingSupported;
import com.sony.imaging.app.srctrl.shooting.state.DevelopmentStateExSingleShootingOnly;
import com.sony.imaging.app.srctrl.shooting.state.EEStateEx;
import com.sony.imaging.app.srctrl.shooting.state.ExposureModeCheckStateEx;
import com.sony.imaging.app.srctrl.shooting.state.FocusAdjustmentStateEx;
import com.sony.imaging.app.srctrl.shooting.state.MfAssistStateEx;
import com.sony.imaging.app.srctrl.shooting.state.MovieCaptureStateEx;
import com.sony.imaging.app.srctrl.shooting.state.MovieMenuStateEx;
import com.sony.imaging.app.srctrl.shooting.state.MovieRecStandbyStateEx;
import com.sony.imaging.app.srctrl.shooting.state.MovieRecStateEx;
import com.sony.imaging.app.srctrl.shooting.state.NormalCaptureStateEx;
import com.sony.imaging.app.srctrl.shooting.state.S1OffEEStateEx;
import com.sony.imaging.app.srctrl.shooting.state.S1OnEEStateEx;
import com.sony.imaging.app.srctrl.shooting.state.S1OnEEStateForTouchAF;
import com.sony.imaging.app.srctrl.shooting.state.S1OnEEStateForTouchAFAssist;
import com.sony.imaging.app.srctrl.shooting.state.SelfTimerCaptureStateEx;
import com.sony.imaging.app.srctrl.shooting.state.ShootingMenuStateEx;
import com.sony.imaging.app.srctrl.shooting.state.ShootingStateEx;
import com.sony.imaging.app.srctrl.util.SRCtrlEnvironment;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class Factory extends com.sony.imaging.app.fw.Factory {
    @Override // com.sony.imaging.app.fw.Factory
    protected Class<? extends StateFactory> getTop() {
        return RootStateFactory.class;
    }

    /* loaded from: classes.dex */
    public static class RootStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(SRCtrl.SRCTRL_ROOT, SRCtrlRootState.class);
            Factory.add(SRCtrlRootState.class, ManagerStateFactory.class, null, null);
            add(BaseApp.CAUTION, CautionFragment.class);
            Factory.add(CautionFragment.class, CautionStateFactory.class, null, null);
            add(BaseApp.EXIT_SCREEN, ExitScreenState.class);
            Factory.add(ExitScreenState.class, null, ExitScreenLayoutFactory.class, null);
            add(BaseApp.FORCE_EXIT_SCREEN, ForceExitScreenState.class);
            Factory.add(ForceExitScreenState.class, null, ForceExitScreenLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class ManagerStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("APP_SHOOTING", ShootingStateEx.class);
            Factory.add(ShootingStateEx.class, ShootingStateFactory.class, ShootingLayoutFactory.class, ShootingStateKeyHandlerEx.class);
            add(SRCtrlRootState.APP_NETWORK, NetworkRootState.class);
            Factory.add(NetworkRootState.class, NwStateFactory.class, null, null);
            add(SRCtrlRootState.APP_PLAYBACK, PlayRootContainerEx.class);
            Factory.add(PlayRootContainerEx.class, PlaybackStateFactory.class, PlayRootLayoutFactory.class, null);
            add(SRCtrlRootState.FATAL_ERROR, NwFatalErrorState.class);
            Factory.add(NwFatalErrorState.class, null, NwFatalErrorLayoutFactory.class, NwFatalErrorKeyHandler.class);
        }
    }

    /* loaded from: classes.dex */
    public static class CautionStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("CautionDisplayState", CautionDisplayState.class);
            Factory.add(CautionDisplayState.class, null, CautionLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class ExitScreenLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("ExitScreen", ExitScreenLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class NwStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(NetworkRootState.ID_STANDBY, NwStandbyState.class);
            Factory.add(NwStandbyState.class, null, NwStandbyLayoutFactory.class, NwStandbyKeyHandler.class);
            add(NetworkRootState.ID_WAITING, NwWaitingState.class);
            Factory.add(NwWaitingState.class, null, NwWaitingLayoutFactory.class, NwWaitingKeyHandler.class);
            add(NetworkRootState.ID_CONNECTED, NwConnectedState.class);
            Factory.add(NwConnectedState.class, null, NwConnectedLayoutFactory.class, NwConnectedKeyHandler.class);
            add(NetworkRootState.ID_WPS_PBC, NwWpsPbcState.class);
            Factory.add(NwWpsPbcState.class, null, NwWpsPbcLayoutFactory.class, NwWpsPbcKeyHandler.class);
            add(NetworkRootState.ID_WPS_PIN_PRINT, NwWpsPinPrintState.class);
            Factory.add(NwWpsPinPrintState.class, null, NwWpsPinPrintLayoutFactory.class, NwWpsPinPrintKeyHandler.class);
            add(NetworkRootState.ID_WPS_PIN_INPUT, NwWpsPinInputState.class);
            Factory.add(NwWpsPinInputState.class, null, NwWpsPinInputLayoutFactory.class, NwWpsPinInputKeyHandler.class);
            add(NetworkRootState.ID_REGISTERING, NwWpsRegisteringState.class);
            Factory.add(NwWpsRegisteringState.class, null, NwWpsRegisteringLayoutFactory.class, NwWpsRegisteringKeyHandler.class);
            add(NetworkRootState.ID_WPS_ERROR, NwWpsErrorState.class);
            Factory.add(NwWpsErrorState.class, null, NwWpsErrorLayoutFactory.class, NwWpsErrorKeyHandler.class);
            add("ID_CONFIRM", NwConfirmationState.class);
            Factory.add(NwConfirmationState.class, null, NwConfirmChangeConfigLayoutFactory.class, NwConfirmationKeyHandler.class);
            add(NetworkRootState.ID_RESTARTING, RestartingState.class);
            Factory.add(RestartingState.class, null, NwRestartingLayoutFactory.class, RestartingKeyHandler.class);
        }
    }

    /* loaded from: classes.dex */
    public static class NwStandbyLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(NwStandbyState.ID_STANDBY_LAYOUT, NwStandbyLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class NwWaitingLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(NwWaitingState.ID_WAITING_LAYOUT, NwWaitingLayout.class);
            add(NwWaitingState.ID_WAITING_LAYOUT_QR, NwWaitingQRCodeLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class NwConnectedLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(NwConnectedState.ID_CONNECTED_LAYOUT, NwConnectedLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class NwWpsPbcLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(NwWpsPbcState.ID_PBC_LAYOUT, NwWpsPbcLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class NwWpsPinPrintLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(NwWpsPinPrintState.ID_PIN_PRINT_LAYOUT, NwWpsPinPrintLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class NwWpsPinInputLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(NwWpsPinInputState.ID_PIN_INPUT_LAYOUT, NwWpsPinInputLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class NwWpsRegisteringLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(NwWpsRegisteringState.ID_REGISTERING_LAYOUT, NwWpsRegisteringLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class NwWpsErrorLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(NwWpsErrorState.ID_INVALID_LAYOUT, NwWpsInvalidLayout.class);
            add(NwWpsErrorState.ID_TIMEOUT_LAYOUT, NwWpsTimeoutLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class NwFatalErrorLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(NwFatalErrorState.ID_FATAL_LAYOUT, NwFatalErrorLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class NwConfirmChangeConfigLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(NwConfirmationState.ID_CONFIRM_CHANGE_CONFIG_LAYOUT, NwConfirmationLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class NwRestartingLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(RestartingState.ID_CHANGING_CONFIG_LAYOUT, RestartingLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class ShootingStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("FORCESETTING", SRCtrlForceSettingState.class);
            Factory.add(SRCtrlForceSettingState.class, null, null, null);
            add("ExposureModeCheck", ExposureModeCheckStateEx.class);
            Factory.add(ExposureModeCheckStateEx.class, null, null, null);
            add("EE", EEStateEx.class);
            Factory.add(EEStateEx.class, EEStateFactory.class, null, EEStateKeyHandler.class);
            add(CaptureStateEx.STATE_NAME, CaptureStateEx.class);
            Factory.add(CaptureStateEx.class, CaptureStateFactory.class, null, CaptureStateKeyHandlerEx.class);
            if (SRCtrlEnvironment.getInstance().isEnableContinuousShooting()) {
                add("Development", DevelopmentStateExContShootingSupported.class);
                Factory.add(DevelopmentStateExContShootingSupported.class, DevelopmentStateFactory.class, DevelopmentLayoutFactory.class, DevelopmentStateKeyHandlerEx.class);
            } else {
                add("Development", DevelopmentStateExSingleShootingOnly.class);
                Factory.add(DevelopmentStateExSingleShootingOnly.class, DevelopmentStateFactory.class, DevelopmentLayoutFactory.class, DevelopmentStateKeyHandlerEx.class);
            }
            add(AutoReviewState.STATE_NAME, AutoReviewState.class);
            Factory.add(AutoReviewState.class, null, AutoReviewLayoutFactory.class, AutoReviewStateKeyHandlerEx.class);
            add("AutoReviewForLimitedContShooting", AutoReviewForLimitedContShootingState.class);
            Factory.add(AutoReviewForLimitedContShootingState.class, null, AutoReviewForLimitedContShootingLayoutFactory.class, AutoReviewForLimitedContShootingStateKeyHandler.class);
            add("CustomWhiteBalance", CustomWhiteBalanceControllerState.class);
            Factory.add(CustomWhiteBalanceControllerState.class, CustomWhiteBalanceControllerStateFactory.class, null, null);
            if (Environment.isMovieAPISupported()) {
                add(MovieCaptureStateEx.STATE_NAME, MovieCaptureStateEx.class);
                Factory.add(MovieCaptureStateEx.class, null, MovieLayoutFactory.class, MovieCaptureStateKeyHandler.class);
                add(MovieRecStateEx.STATE_NAME, MovieRecStateEx.class);
                Factory.add(MovieRecStateEx.class, MovieRecStateFactory.class, MovieLayoutFactory.class, MovieRecStateKeyHandler.class);
                add("MovieSave", MovieSaveState.class);
                Factory.add(MovieSaveState.class, null, MovieSaveLayoutFactory.class, MovieSaveStateKeyHandler.class);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class MovieRecStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("Menu", MovieMenuStateEx.class);
            Factory.add(MovieMenuStateEx.class, null, MenuLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class MovieLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("DefaultLayout", SRCtrlStableLayout.class);
            add(DigitalZoomSettingLayout.MENU_ID, DigitalZoomSettingLayout.class);
            add(StateBase.FOCUS_LAYOUT, FocusLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class MovieSaveLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("StableLayout", SRCtrlStableLayout.class);
            add("MovieSaveLayout", MovieSaveLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class EEStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("S1OffEE", S1OffEEStateEx.class);
            Factory.add(S1OffEEStateEx.class, null, S1OffEELayoutFactory.class, S1OffEEStateKeyHandlerEx.class);
            add(S1OnEEStateEx.STATE_NAME, S1OnEEStateEx.class);
            Factory.add(S1OnEEStateEx.class, null, S1OnEELayoutFactory.class, S1OnEEStateKeyHandler.class);
            add(S1OnEEStateForTouchAF.STATE_NAME, S1OnEEStateForTouchAF.class);
            Factory.add(S1OnEEStateForTouchAF.class, null, S1OnEELayoutFactory.class, S1OnEEStateKeyHandlerForTouchAF.class);
            add(S1OnEEStateForTouchAFAssist.STATE_NAME, S1OnEEStateForTouchAFAssist.class);
            Factory.add(S1OnEEStateForTouchAFAssist.class, null, MfAssistLayoutFactory.class, S1OnEEStateKeyHandlerForTouchAFAssist.class);
            add("MfAssist", MfAssistStateEx.class);
            Factory.add(MfAssistStateEx.class, null, MfAssistLayoutFactory.class, MfAssistKeyHandlerEx.class);
            add("FocusAdjustment", FocusAdjustmentStateEx.class);
            Factory.add(FocusAdjustmentStateEx.class, null, FocusAdjustmentFactory.class, FocusAdjustmentKeyHandler.class);
            add("Menu", ShootingMenuStateEx.class);
            Factory.add(ShootingMenuStateEx.class, null, MenuLayoutFactory.class, null);
            add(MovieRecStandbyStateEx.STATE_NAME, MovieRecStandbyStateEx.class);
            Factory.add(MovieRecStandbyStateEx.class, null, MovieLayoutFactory.class, MovieRecStandbyStateKeyHandlerEx.class);
        }
    }

    /* loaded from: classes.dex */
    public static class CaptureStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("NormalCapture", NormalCaptureStateEx.class);
            Factory.add(NormalCaptureStateEx.class, null, null, NormalCaptureStateKeyHandlerEx.class);
            add("SelfTimerCapture", SelfTimerCaptureStateEx.class);
            Factory.add(SelfTimerCaptureStateEx.class, null, SelfTimerCaptureLayoutFactory.class, SelfTimerCaptureStateKeyHandler.class);
        }
    }

    /* loaded from: classes.dex */
    public static class DevelopmentStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
        }
    }

    /* loaded from: classes.dex */
    public static class CustomWhiteBalanceControllerStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("CWBEE", CustomWhiteBalanceEEState.class);
            Factory.add(CustomWhiteBalanceEEState.class, null, CWBEELayoutFactory.class, CustomWhiteBalanceEEKeyHandler.class);
            add("CWBCapture", CustomWhiteBalanceCaptureState.class);
            Factory.add(CustomWhiteBalanceCaptureState.class, null, null, CustomWhiteBalanceCaptureKeyHandler.class);
            add("CWBExposure", CustomWhiteBalanceExposureState.class);
            Factory.add(CustomWhiteBalanceExposureState.class, null, CWBExposureLayoutFactory.class, CustomWhiteBalanceExposureKeyHandler.class);
        }
    }

    /* loaded from: classes.dex */
    public static class ForceExitScreenLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("ForceExitScreen", ForceExitScreenLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class CautionLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("cautionLayout", CautionLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class ShootingLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("DefaultLayout", ShootingLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class PlayRootLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("DefaultLayout", PlaybackLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class DevelopmentLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            if (SRCtrlEnvironment.getInstance().isEnableContinuousShooting()) {
                add("DefaultLayout", ProgressLayoutEx.class);
            } else {
                add("DefaultLayout", ProgressLayoutEx.class);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class AutoReviewLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("DefaultLayout", AutoReviewLayoutEx.class);
        }
    }

    /* loaded from: classes.dex */
    public static class AutoReviewForLimitedContShootingLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("DefaultLayout", AutoReviewForLimitedContShootingLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class S1OffEELayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("DefaultLayout", SRCtrlStableLayout.class);
            add(StateBase.FOCUS_LAYOUT, FocusLayout.class);
            add(StateBase.GUIDE_LAYOUT, GuideLayout.class);
            add(StateBase.S1OFF_LAYOUT, S1OffLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class S1OnEELayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("DefaultLayout", SRCtrlStableLayout.class);
            add(StateBase.FOCUS_LAYOUT, FocusLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class MfAssistLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("DefaultLayout", MfAssistLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class FocusAdjustmentFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("DefaultLayout", FocusAdjustmentLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class CWBEELayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("DefaultLayout", CustomWhiteBalanceEELayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class NormalCaptureLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("DefaultLayout", SRCtrlStableLayout.class);
            add(StateBase.FOCUS_LAYOUT, FocusLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class SelfTimerCaptureLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("DefaultLayout", SRCtrlStableLayout.class);
            add(StateBase.FOCUS_LAYOUT, FocusLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class CWBExposureLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("DefaultLayout", CustomWhiteBalanceConfLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class MenuLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("ID_WHITEBALANCEMENULAYOUT", WhiteBalanceMenuLayout.class);
            Factory.add(WhiteBalanceMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(WhiteBalanceAdjustmentMenuLayout.MENU_ID, WhiteBalanceAdjustmentMenuLayout.class);
            Factory.add(WhiteBalanceAdjustmentMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_FLASHCOMPENSATIONMENULAYOUT", FlashCompensationMenuLayout.class);
            Factory.add(FlashCompensationMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_AFFLEXIBLEPOSITIONMENULAYOUT", AFFlexiblePositionMenuLayout.class);
            Factory.add(AFFlexiblePositionMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_CREATIVESTYLEMENULAYOUT", CreativeStyleMenuLayout.class);
            Factory.add(CreativeStyleMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_AFLOCALPOSITIONMENULAYOUT", AFLocalPositionMenuLayout.class);
            Factory.add(AFLocalPositionMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_EXPOSUREMODEMENULAYOUT", ExposureModeMenuLayoutEx.class);
            Factory.add(ExposureModeMenuLayoutEx.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_SCENESELECTIONMENULAYOUT", SceneSelectionMenuLayout.class);
            Factory.add(SceneSelectionMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_EXPOSURECOMPENSATIONMENULAYOUT", ExposureCompensationMenuLayout.class);
            Factory.add(ExposureCompensationMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_MOVIEMODEMENULAYOUT", MovieModeMenuLayoutEx.class);
            Factory.add(MovieModeMenuLayoutEx.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(UnknownItemMenuLayout.MENU_ID, UnknownItemMenuLayout.class);
            Factory.add(UnknownItemMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("LastBastionLayout", LastBastionMenuLayoutEx.class);
            Factory.add(LastBastionMenuLayoutEx.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(FnMenuLayout.MENU_ID, FnMenuLayout.class);
            Factory.add(FnMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(Fn15LayerMenuLayout.MENU_ID, Fn15LayerMenuLayout.class);
            Factory.add(Fn15LayerMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(SpecialScreenMenuLayout.MENU_ID, SpecialScreenMenuLayout.class);
            Factory.add(SpecialScreenMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(ISOSpecialScreenMenuLayout.MENU_ID, ISOSpecialScreenMenuLayout.class);
            Factory.add(ISOSpecialScreenMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(DigitalZoomSettingLayout.MENU_ID, DigitalZoomSettingLayout.class);
            Factory.add(DigitalZoomSettingLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(FocusModeSpecialScreenMenuLayout.MENU_ID, FocusModeSpecialScreenMenuLayout.class);
            Factory.add(FocusModeSpecialScreenMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(Fn15LayerExposureCompensationLayout.MENU_ID, Fn15LayerExposureCompensationLayout.class);
            Factory.add(Fn15LayerExposureCompensationLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(Fn15LayerFlashCompensationLayout.MENU_ID, Fn15LayerFlashCompensationLayout.class);
            Factory.add(Fn15LayerFlashCompensationLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(Fn15LayerCreativeStyleLayout.MENU_ID, Fn15LayerCreativeStyleLayout.class);
            Factory.add(Fn15LayerCreativeStyleLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(Fn15LayerWhiteBalanceLayout.MENU_ID, Fn15LayerWhiteBalanceLayout.class);
            Factory.add(Fn15LayerWhiteBalanceLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(ISOFn15LayerMenuLayout.MENU_ID, ISOFn15LayerMenuLayout.class);
            Factory.add(ISOFn15LayerMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(PageMenuLayout.MENU_ID, PageMenuLayout.class);
            Factory.add(PageMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(SetMenuLayout.MENU_ID, SetMenuLayout.class);
            Factory.add(SetMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(SpecialScreenMenuLayoutHiddenEE.MENU_ID, SpecialScreenMenuLayoutHiddenEE.class);
            Factory.add(SpecialScreenMenuLayoutHiddenEE.class, null, OneAnotherMenuLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class OneAnotherMenuLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("ID_WHITEBALANCEMENULAYOUT", WhiteBalanceMenuLayout.class);
            add(WhiteBalanceAdjustmentMenuLayout.MENU_ID, WhiteBalanceAdjustmentMenuLayout.class);
            add("ID_FLASHCOMPENSATIONMENULAYOUT", FlashCompensationMenuLayout.class);
            add("ID_AFFLEXIBLEPOSITIONMENULAYOUT", AFFlexiblePositionMenuLayout.class);
            add("ID_CREATIVESTYLEMENULAYOUT", CreativeStyleMenuLayout.class);
            add("ID_AFLOCALPOSITIONMENULAYOUT", AFLocalPositionMenuLayout.class);
            add("ID_EXPOSUREMODEMENULAYOUT", ExposureModeMenuLayoutEx.class);
            add("ID_SCENESELECTIONMENULAYOUT", SceneSelectionMenuLayout.class);
            add("ID_EXPOSURECOMPENSATIONMENULAYOUT", ExposureCompensationMenuLayout.class);
            add("ID_MOVIEMODEMENULAYOUT", MovieModeMenuLayoutEx.class);
            add(UnknownItemMenuLayout.MENU_ID, UnknownItemMenuLayout.class);
            add("LastBastionLayoutEx", LastBastionMenuLayoutEx.class);
            add(FnMenuLayout.MENU_ID, FnMenuLayout.class);
            add(Fn15LayerMenuLayout.MENU_ID, Fn15LayerMenuLayout.class);
            add(SpecialScreenMenuLayout.MENU_ID, SpecialScreenMenuLayout.class);
            add(ISOSpecialScreenMenuLayout.MENU_ID, ISOSpecialScreenMenuLayout.class);
            add(DigitalZoomSettingLayout.MENU_ID, DigitalZoomSettingLayout.class);
            add(FocusModeSpecialScreenMenuLayout.MENU_ID, FocusModeSpecialScreenMenuLayout.class);
            add(Fn15LayerExposureCompensationLayout.MENU_ID, Fn15LayerExposureCompensationLayout.class);
            add(Fn15LayerFlashCompensationLayout.MENU_ID, Fn15LayerFlashCompensationLayout.class);
            add(Fn15LayerCreativeStyleLayout.MENU_ID, Fn15LayerCreativeStyleLayout.class);
            add(Fn15LayerWhiteBalanceLayout.MENU_ID, Fn15LayerWhiteBalanceLayout.class);
            add(ISOFn15LayerMenuLayout.MENU_ID, ISOFn15LayerMenuLayout.class);
            add(PageMenuLayout.MENU_ID, PageMenuLayout.class);
            add(SetMenuLayout.MENU_ID, SetMenuLayout.class);
            add(SpecialScreenMenuLayoutHiddenEE.MENU_ID, SpecialScreenMenuLayoutHiddenEE.class);
        }
    }

    /* loaded from: classes.dex */
    public static class PlaybackStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlayRootContainer.ID_BROWSER, BrowserEx.class);
            Factory.add(BrowserEx.class, BrowserFactory.class, null, null);
            add(PlayRootContainer.ID_TRANSITING_TO_SHOOTING, TransitionToShooting.class);
        }
    }

    /* loaded from: classes.dex */
    public static class BrowserFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlaySubApp.ID_INDEX_PB, ContentsTransferState.class);
            Factory.add(ContentsTransferState.class, null, BrowserIndexLayoutFactory.class, null);
            add(PlayRootContainerEx.ID_DELETE_MULTIPLE, DeleteMultiple.class);
            Factory.add(DeleteMultiple.class, null, DeleteMultipleLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class BrowserIndexLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(IndexStateBase.ID_DATA_UNREADY_LAYOUT, BrowserSingleNoBufferLayoutEx.class);
        }
    }

    /* loaded from: classes.dex */
    public static class DeleteMultipleLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(IndexStateBase.ID_DATA_UNREADY_LAYOUT, BrowserSingleNoBufferLayout.class);
        }
    }
}
