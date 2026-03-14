package com.sony.imaging.app.digitalfilter;

import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.caution.CautionDisplayState;
import com.sony.imaging.app.base.caution.CautionFragment;
import com.sony.imaging.app.base.common.ExitScreenState;
import com.sony.imaging.app.base.common.ExitToConfigState;
import com.sony.imaging.app.base.common.ExitToPlayState;
import com.sony.imaging.app.base.common.ExitToPowerOffState;
import com.sony.imaging.app.base.common.ExitToSetupState;
import com.sony.imaging.app.base.common.ExitToWifiState;
import com.sony.imaging.app.base.common.ForceExitScreenState;
import com.sony.imaging.app.base.common.layout.ExitScreenLayout;
import com.sony.imaging.app.base.common.layout.ForceExitScreenLayout;
import com.sony.imaging.app.base.menu.layout.AFFlexiblePositionMenuLayout;
import com.sony.imaging.app.base.menu.layout.AFLocalPositionMenuLayout;
import com.sony.imaging.app.base.menu.layout.DigitalZoomSettingLayout;
import com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout;
import com.sony.imaging.app.base.menu.layout.FnMenuLayout;
import com.sony.imaging.app.base.menu.layout.FocusModeSpecialScreenMenuLayout;
import com.sony.imaging.app.base.menu.layout.LastBastionMenuLayout;
import com.sony.imaging.app.base.menu.layout.MovieModeMenuLayout;
import com.sony.imaging.app.base.menu.layout.SceneSelectionMenuLayout;
import com.sony.imaging.app.base.menu.layout.SegmentMenuLayout;
import com.sony.imaging.app.base.menu.layout.SetMenuLayout;
import com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayoutHiddenEE;
import com.sony.imaging.app.base.menu.layout.UnknownItemMenuLayout;
import com.sony.imaging.app.base.playback.ChangingOutputMode;
import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.base.playback.TransitionToShooting;
import com.sony.imaging.app.base.playback.base.IndexStateBase;
import com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase;
import com.sony.imaging.app.base.playback.base.PlaySubApp;
import com.sony.imaging.app.base.playback.base.SingleStateBase;
import com.sony.imaging.app.base.playback.base.editor.Editor;
import com.sony.imaging.app.base.playback.browser.Browser;
import com.sony.imaging.app.base.playback.browser.BrowserSingle;
import com.sony.imaging.app.base.playback.delete.single.DeleteSingle;
import com.sony.imaging.app.base.playback.delete.single.DeleteSingleConfirm;
import com.sony.imaging.app.base.playback.delete.single.DeleteSingleExecutor;
import com.sony.imaging.app.base.playback.delete.single.DeleteSingleFinalize;
import com.sony.imaging.app.base.playback.delete.single.DeleteSingleTestContents;
import com.sony.imaging.app.base.playback.layout.BrowserDialog4kStart;
import com.sony.imaging.app.base.playback.layout.BrowserIndexNoFileLayout;
import com.sony.imaging.app.base.playback.layout.BrowserSingle4kLayout;
import com.sony.imaging.app.base.playback.layout.BrowserSingleNoBufferLayout;
import com.sony.imaging.app.base.playback.layout.BrowserSingleNoFileLayout;
import com.sony.imaging.app.base.playback.layout.DeleteSingleConfirmLayout;
import com.sony.imaging.app.base.playback.layout.DeleteSingleProcessingLayout;
import com.sony.imaging.app.base.playback.layout.EventBlockerLayout;
import com.sony.imaging.app.base.playback.layout.Play4kZoomLayout;
import com.sony.imaging.app.base.playback.layout.PlayZoomLayout;
import com.sony.imaging.app.base.playback.layout.PseudoRecNoFileLayout;
import com.sony.imaging.app.base.playback.layout.PseudoRecSingleLayout;
import com.sony.imaging.app.base.playback.layout.PseudoRecZoomLayout;
import com.sony.imaging.app.base.playback.layout.VolumeAdjustmentLayout;
import com.sony.imaging.app.base.playback.pseudorec.PseudoRec;
import com.sony.imaging.app.base.playback.pseudorec.PseudoRecSingle;
import com.sony.imaging.app.base.playback.pseudorec.zoom.PseudoRecZoom;
import com.sony.imaging.app.base.playback.pseudorec.zoom.PseudoRecZoomState;
import com.sony.imaging.app.base.playback.zoom.PlayZoom;
import com.sony.imaging.app.base.playback.zoom.PlayZoomState;
import com.sony.imaging.app.base.shooting.AutoReviewForLimitedContShootingState;
import com.sony.imaging.app.base.shooting.AutoReviewState;
import com.sony.imaging.app.base.shooting.CaptureState;
import com.sony.imaging.app.base.shooting.CustomWhiteBalanceCaptureState;
import com.sony.imaging.app.base.shooting.CustomWhiteBalanceEEState;
import com.sony.imaging.app.base.shooting.CustomWhiteBalanceExposureState;
import com.sony.imaging.app.base.shooting.DevelopmentState;
import com.sony.imaging.app.base.shooting.ExposureModeCheckState;
import com.sony.imaging.app.base.shooting.FocusAdjustmentState;
import com.sony.imaging.app.base.shooting.MfAssistState;
import com.sony.imaging.app.base.shooting.MovieMenuState;
import com.sony.imaging.app.base.shooting.NormalCaptureState;
import com.sony.imaging.app.base.shooting.S1OffEEState;
import com.sony.imaging.app.base.shooting.SelfTimerCaptureState;
import com.sony.imaging.app.base.shooting.StateBase;
import com.sony.imaging.app.base.shooting.layout.AutoReviewForLimitedContShootingLayout;
import com.sony.imaging.app.base.shooting.layout.AutoReviewLayout;
import com.sony.imaging.app.base.shooting.layout.CustomWhiteBalanceConfLayout;
import com.sony.imaging.app.base.shooting.layout.CustomWhiteBalanceEELayout;
import com.sony.imaging.app.base.shooting.layout.FocusAdjustmentLayout;
import com.sony.imaging.app.base.shooting.layout.FocusLayout;
import com.sony.imaging.app.base.shooting.layout.GuideLayout;
import com.sony.imaging.app.base.shooting.layout.ProgressLayout;
import com.sony.imaging.app.base.shooting.layout.ShootingLayout;
import com.sony.imaging.app.base.shooting.layout.SubLcdStableLayout;
import com.sony.imaging.app.base.shooting.layout.SublcdCaptureLayout;
import com.sony.imaging.app.base.shooting.movie.MovieCaptureState;
import com.sony.imaging.app.base.shooting.movie.MovieRecStandbyState;
import com.sony.imaging.app.base.shooting.movie.MovieRecState;
import com.sony.imaging.app.base.shooting.movie.MovieSaveState;
import com.sony.imaging.app.base.shooting.movie.layout.MovieSaveLayout;
import com.sony.imaging.app.base.shooting.movie.layout.SubLcdMovieRecLayout;
import com.sony.imaging.app.base.shooting.movie.layout.SubLcdMovieSaveLayout;
import com.sony.imaging.app.base.shooting.movie.trigger.MovieCaptureStateKeyHandler;
import com.sony.imaging.app.base.shooting.movie.trigger.MovieRecStandbyStateKeyHandler;
import com.sony.imaging.app.base.shooting.movie.trigger.MovieRecStateKeyHandler;
import com.sony.imaging.app.base.shooting.movie.trigger.MovieSaveStateKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.AutoReviewForLimitedContShootingStateKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.AutoReviewStateKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.CaptureStateKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.CustomWhiteBalanceCaptureKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.CustomWhiteBalanceEEKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.FocusAdjustmentKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.SelfTimerCaptureStateKeyHandler;
import com.sony.imaging.app.digitalfilter.caution.GFCautionLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFAvMenuLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFBorderMenuLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFCopyThemeMenuLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFCreativeStyleMenuLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFExposureCompensationMenuLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFExposureModeMenuLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFFilterSetMenuLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFFilterShootingMenuLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFFlashCompensationMenuLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFFn15LayerCreativeStyleLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFFn15LayerExposureCompensationLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFFn15LayerFlashCompensationLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFFn15LayerMenuLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFFn15LayerWhiteBalanceLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFFnMenuLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFFocusModeSpecialScreenMenuLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFGuideLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFISOAutoMaxMinMenuLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFISOFn15LayerMenuLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFISOMenuLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFISOSpecialScreenMenuLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFIntroductionLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFLinkAreaMenuLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFPageMenuLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFSetMenuLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFSetting15LayerBorderLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFSetting15LayerExposureCompensationLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFSetting15LayerFilterSetLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFSetting15LayerISOLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFSetting15LayerShadingLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFSetting15LayerTvAvLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFSetting15LayerWhiteBalanceLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFSettingMenuLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFShadingMenuLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFSpecialScreenMenuLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFThemeSelectionLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFTvMenuLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFWhiteBalanceAdjustmentMenuLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFWhiteBalanceLimitSetMenuLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFWhiteBalanceMenuLayout;
import com.sony.imaging.app.digitalfilter.playback.GFPlayRoot;
import com.sony.imaging.app.digitalfilter.playback.browser.GFBrowserIndex;
import com.sony.imaging.app.digitalfilter.playback.browser.GFBrowserSingle;
import com.sony.imaging.app.digitalfilter.playback.layout.GFBrowserIndexLayout;
import com.sony.imaging.app.digitalfilter.playback.layout.GFBrowserSingleLayout;
import com.sony.imaging.app.digitalfilter.shooting.GFForceSettingState;
import com.sony.imaging.app.digitalfilter.shooting.layout.BlackMuteLayout;
import com.sony.imaging.app.digitalfilter.shooting.layout.GFAdjustment15LayerBorderLayout;
import com.sony.imaging.app.digitalfilter.shooting.layout.GFAdjustment15LayerShadingLayout;
import com.sony.imaging.app.digitalfilter.shooting.layout.GFAdjustmentLayout;
import com.sony.imaging.app.digitalfilter.shooting.layout.GFMfAssistLayout;
import com.sony.imaging.app.digitalfilter.shooting.layout.GFPreviewLayout;
import com.sony.imaging.app.digitalfilter.shooting.layout.GFProcessingLayout;
import com.sony.imaging.app.digitalfilter.shooting.layout.GFS1OffLayout;
import com.sony.imaging.app.digitalfilter.shooting.layout.GFStableLayout;
import com.sony.imaging.app.digitalfilter.shooting.state.GFAdjustmentState;
import com.sony.imaging.app.digitalfilter.shooting.state.GFCustomWhiteBalanceControllerState;
import com.sony.imaging.app.digitalfilter.shooting.state.GFEEState;
import com.sony.imaging.app.digitalfilter.shooting.state.GFNormalCaptureState;
import com.sony.imaging.app.digitalfilter.shooting.state.GFProcessingState;
import com.sony.imaging.app.digitalfilter.shooting.state.GFS1OffEEState;
import com.sony.imaging.app.digitalfilter.shooting.state.GFS1OnEEState;
import com.sony.imaging.app.digitalfilter.shooting.state.GFShootingMenuState;
import com.sony.imaging.app.digitalfilter.shooting.state.GFShootingState;
import com.sony.imaging.app.digitalfilter.shooting.trigger.GFAdjustmentStateKeyHandler;
import com.sony.imaging.app.digitalfilter.shooting.trigger.GFCaptureStateKeyHandler;
import com.sony.imaging.app.digitalfilter.shooting.trigger.GFCustomWhiteBalanceExposureKeyHandler;
import com.sony.imaging.app.digitalfilter.shooting.trigger.GFDevelopmentStateKeyHandler;
import com.sony.imaging.app.digitalfilter.shooting.trigger.GFEEStateKeyHandler;
import com.sony.imaging.app.digitalfilter.shooting.trigger.GFMfAssistKeyHandler;
import com.sony.imaging.app.digitalfilter.shooting.trigger.GFProcessingStateKeyHandler;
import com.sony.imaging.app.digitalfilter.shooting.trigger.GFS1OffEEStateKeyHandler;
import com.sony.imaging.app.digitalfilter.shooting.trigger.GFS1OnEEStateKeyHandler;
import com.sony.imaging.app.digitalfilter.shooting.trigger.GFShootingStateKeyHandler;
import com.sony.imaging.app.fw.LayoutFactory;
import com.sony.imaging.app.fw.StateFactory;
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
            add(BaseApp.APP_SHOOTING, GFShootingState.class);
            Factory.add(GFShootingState.class, ShootingStateFactory.class, ShootingLayoutFactory.class, GFShootingStateKeyHandler.class);
            add(BaseApp.APP_PLAY, GFPlayRoot.class);
            Factory.add(GFPlayRoot.class, PlaybackStateFactory.class, null, null);
            add(BaseApp.CAUTION, CautionFragment.class);
            Factory.add(CautionFragment.class, CautionStateFactory.class, null, null);
            add(BaseApp.EXIT_SCREEN, ExitScreenState.class);
            Factory.add(ExitScreenState.class, null, ExitScreenLayoutFactory.class, null);
            add(BaseApp.FORCE_EXIT_SCREEN, ForceExitScreenState.class);
            Factory.add(ForceExitScreenState.class, null, ForceExitScreenLayoutFactory.class, null);
            if (Environment.isNewBizDeviceActionCam() && Environment.hasSubLcd()) {
                add("ExitToPlay", ExitToPlayState.class);
                Factory.add(ExitToPlayState.class, null, null, null);
                add("ExitToConfig", ExitToConfigState.class);
                Factory.add(ExitToConfigState.class, null, null, null);
                add("ExitToPowerOff", ExitToPowerOffState.class);
                Factory.add(ExitToPowerOffState.class, null, null, null);
                add("ExitToWifi", ExitToWifiState.class);
                Factory.add(ExitToWifiState.class, null, null, null);
                add("ExitToSetup", ExitToSetupState.class);
                Factory.add(ExitToSetupState.class, null, null, null);
            }
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
    public static class ShootingStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("FORCESETTING", GFForceSettingState.class);
            Factory.add(GFForceSettingState.class, null, null, null);
            add("ExposureModeCheck", ExposureModeCheckState.class);
            Factory.add(ExposureModeCheckState.class, null, null, null);
            add("EE", GFEEState.class);
            Factory.add(GFEEState.class, EEStateFactory.class, null, GFEEStateKeyHandler.class);
            add("Capture", CaptureState.class);
            Factory.add(CaptureState.class, CaptureStateFactory.class, null, CaptureStateKeyHandler.class);
            add("Adjustment", GFAdjustmentState.class);
            Factory.add(GFAdjustmentState.class, null, MenuLayoutFactory.class, GFAdjustmentStateKeyHandler.class);
            add("Processing", GFProcessingState.class);
            Factory.add(GFProcessingState.class, null, GFProcessingStateLayoutFactory.class, GFProcessingStateKeyHandler.class);
            add("Development", DevelopmentState.class);
            Factory.add(DevelopmentState.class, DevelopmentStateFactory.class, DevelopmentLayoutFactory.class, GFDevelopmentStateKeyHandler.class);
            add(AutoReviewState.STATE_NAME, AutoReviewState.class);
            Factory.add(AutoReviewState.class, null, AutoReviewLayoutFactory.class, AutoReviewStateKeyHandler.class);
            add("AutoReviewForLimitedContShooting", AutoReviewForLimitedContShootingState.class);
            Factory.add(AutoReviewForLimitedContShootingState.class, null, AutoReviewForLimitedContShootingLayoutFactory.class, AutoReviewForLimitedContShootingStateKeyHandler.class);
            add("CustomWhiteBalance", GFCustomWhiteBalanceControllerState.class);
            Factory.add(GFCustomWhiteBalanceControllerState.class, CustomWhiteBalanceControllerStateFactory.class, null, null);
            if (Environment.isMovieAPISupported()) {
                add("MovieCapture", MovieCaptureState.class);
                if (!Environment.isNewBizDeviceActionCam() || !Environment.hasSubLcd()) {
                    Factory.add(MovieCaptureState.class, null, MovieLayoutFactory.class, MovieCaptureStateKeyHandler.class);
                } else {
                    Factory.add(MovieCaptureState.class, null, MovieRecLayoutFactory.class, MovieCaptureStateKeyHandler.class);
                }
                add("MovieRec", MovieRecState.class);
                if (!Environment.isNewBizDeviceActionCam() || !Environment.hasSubLcd()) {
                    Factory.add(MovieRecState.class, MovieRecStateFactory.class, MovieLayoutFactory.class, MovieRecStateKeyHandler.class);
                } else {
                    Factory.add(MovieRecState.class, MovieRecStateFactory.class, MovieRecLayoutFactory.class, MovieRecStateKeyHandler.class);
                }
                add("MovieSave", MovieSaveState.class);
                Factory.add(MovieSaveState.class, null, MovieSaveLayoutFactory.class, MovieSaveStateKeyHandler.class);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class MovieRecStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("Menu", MovieMenuState.class);
            Factory.add(MovieMenuState.class, null, MenuLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class MovieLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            if (Environment.isNewBizDeviceActionCam() && Environment.hasSubLcd()) {
                add(StateBase.DEFAULT_LAYOUT, SubLcdStableLayout.class);
                return;
            }
            add(StateBase.DEFAULT_LAYOUT, GFStableLayout.class);
            add(DigitalZoomSettingLayout.MENU_ID, DigitalZoomSettingLayout.class);
            add(StateBase.FOCUS_LAYOUT, FocusLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class MovieRecLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(StateBase.DEFAULT_LAYOUT, SubLcdMovieRecLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class MovieSaveLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            if (Environment.isNewBizDeviceActionCam() && Environment.hasSubLcd()) {
                add("StableLayout", SubLcdStableLayout.class);
                add("MovieSaveLayout", SubLcdMovieSaveLayout.class);
            } else {
                add("StableLayout", GFStableLayout.class);
                add("MovieSaveLayout", MovieSaveLayout.class);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class EEStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(S1OffEEState.STATE_NAME, GFS1OffEEState.class);
            Factory.add(GFS1OffEEState.class, null, S1OffEELayoutFactory.class, GFS1OffEEStateKeyHandler.class);
            add("S1OnEE", GFS1OnEEState.class);
            Factory.add(GFS1OnEEState.class, null, S1OnEELayoutFactory.class, GFS1OnEEStateKeyHandler.class);
            add("MfAssist", MfAssistState.class);
            Factory.add(MfAssistState.class, null, MfAssistLayoutFactory.class, GFMfAssistKeyHandler.class);
            add("FocusAdjustment", FocusAdjustmentState.class);
            Factory.add(FocusAdjustmentState.class, null, FocusAdjustmentFactory.class, FocusAdjustmentKeyHandler.class);
            add("MovieRecStandby", MovieRecStandbyState.class);
            Factory.add(MovieRecStandbyState.class, null, MovieLayoutFactory.class, MovieRecStandbyStateKeyHandler.class);
            add("Menu", GFShootingMenuState.class);
            Factory.add(GFShootingMenuState.class, null, MenuLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class CaptureStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("NormalCapture", GFNormalCaptureState.class);
            if (!Environment.isNewBizDeviceActionCam() || !Environment.hasSubLcd()) {
                Factory.add(GFNormalCaptureState.class, null, NormalCaptureLayoutFactory.class, GFCaptureStateKeyHandler.class);
            } else {
                Factory.add(NormalCaptureState.class, null, SublcdNormalCaptureLayoutFactory.class, null);
            }
            add("SelfTimerCapture", SelfTimerCaptureState.class);
            if (!Environment.isNewBizDeviceActionCam() || !Environment.hasSubLcd()) {
                Factory.add(SelfTimerCaptureState.class, null, SelfTimerCaptureLayoutFactory.class, SelfTimerCaptureStateKeyHandler.class);
            } else {
                Factory.add(SelfTimerCaptureState.class, null, SublcdSelfTimerCaptureLayoutFactory.class, SelfTimerCaptureStateKeyHandler.class);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class NormalCaptureLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(StateBase.DEFAULT_LAYOUT, GFStableLayout.class);
            add(StateBase.FOCUS_LAYOUT, FocusLayout.class);
            add("ProcessingLayout", GFProcessingLayout.class);
            add(BlackMuteLayout.TAG, BlackMuteLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class SublcdNormalCaptureLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(StateBase.DEFAULT_LAYOUT, SublcdCaptureLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class SublcdSelfTimerCaptureLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(StateBase.DEFAULT_LAYOUT, SublcdCaptureLayout.class);
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
            Factory.add(CustomWhiteBalanceExposureState.class, null, CWBExposureLayoutFactory.class, GFCustomWhiteBalanceExposureKeyHandler.class);
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
            add("cautionLayout", GFCautionLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class ShootingLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(StateBase.DEFAULT_LAYOUT, ShootingLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class GFProcessingStateLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(StateBase.DEFAULT_LAYOUT, GFProcessingLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class DevelopmentLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(StateBase.DEFAULT_LAYOUT, ProgressLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class AutoReviewLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(StateBase.DEFAULT_LAYOUT, AutoReviewLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class AutoReviewForLimitedContShootingLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(StateBase.DEFAULT_LAYOUT, AutoReviewForLimitedContShootingLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class S1OffEELayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            if (Environment.isNewBizDeviceActionCam() && Environment.hasSubLcd()) {
                add(StateBase.DEFAULT_LAYOUT, SubLcdStableLayout.class);
                return;
            }
            add(StateBase.DEFAULT_LAYOUT, GFStableLayout.class);
            add(StateBase.S1OFF_LAYOUT, GFS1OffLayout.class);
            add(StateBase.FOCUS_LAYOUT, FocusLayout.class);
            add(StateBase.GUIDE_LAYOUT, GuideLayout.class);
            add("ID_GFINTRODUCTIONLAYOUT", GFIntroductionLayout.class);
            add(GFGuideLayout.ID_GFGUIDELAYOUT, GFGuideLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class S1OnEELayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            if (Environment.isNewBizDeviceActionCam() && Environment.hasSubLcd()) {
                add(StateBase.DEFAULT_LAYOUT, SubLcdStableLayout.class);
            } else {
                add(StateBase.DEFAULT_LAYOUT, GFStableLayout.class);
                add(StateBase.FOCUS_LAYOUT, FocusLayout.class);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class MfAssistLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(StateBase.DEFAULT_LAYOUT, GFMfAssistLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class FocusAdjustmentFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(StateBase.DEFAULT_LAYOUT, FocusAdjustmentLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class CWBEELayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(StateBase.DEFAULT_LAYOUT, CustomWhiteBalanceEELayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class SelfTimerCaptureLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            if (Environment.isNewBizDeviceActionCam() && Environment.hasSubLcd()) {
                add(StateBase.DEFAULT_LAYOUT, SubLcdStableLayout.class);
            } else {
                add(StateBase.DEFAULT_LAYOUT, GFStableLayout.class);
                add(StateBase.FOCUS_LAYOUT, FocusLayout.class);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class CWBExposureLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(StateBase.DEFAULT_LAYOUT, CustomWhiteBalanceConfLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class PlaybackStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlayRootContainer.ID_BROWSER, Browser.class);
            Factory.add(Browser.class, BrowserFactory.class, null, null);
            add(PlayRootContainer.ID_PLAYZOOM, PlayZoom.class);
            Factory.add(PlayZoom.class, PlayZoomFactory.class, null, null);
            add(PlayRootContainer.ID_PSEUDOREC, PseudoRec.class);
            Factory.add(PseudoRec.class, PseudoRecFactory.class, null, null);
            add(PlayRootContainer.ID_PSEUDOREC_ZOOM, PseudoRecZoom.class);
            Factory.add(PseudoRecZoom.class, PseudoRecZoomFactory.class, null, null);
            add(PlayRootContainer.ID_TRANSITING_TO_SHOOTING, TransitionToShooting.class);
            add(PlayRootContainer.ID_CHANGING_OUTPUT_MODE, ChangingOutputMode.class);
        }
    }

    /* loaded from: classes.dex */
    public static class BrowserFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlaySubApp.ID_SINGLE_PB, GFBrowserSingle.class);
            Factory.add(GFBrowserSingle.class, PbFunctionCommonFactory.class, BrowserSingleLayoutFactory.class, null);
            add(PlaySubApp.ID_INDEX_PB, GFBrowserIndex.class);
            Factory.add(GFBrowserIndex.class, PbFunctionCommonFactory.class, BrowserIndexLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class PbFunctionCommonFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlayRootContainer.ID_DELETE_SINGLE, DeleteSingle.class);
            Factory.add(DeleteSingle.class, DeleteSingleFactory.class, null, null);
        }
    }

    /* loaded from: classes.dex */
    public static class DeleteSingleFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(Editor.ID_TEST_THIS, DeleteSingleTestContents.class);
            add(Editor.ID_CONFIRM, DeleteSingleConfirm.class);
            Factory.add(DeleteSingleConfirm.class, null, DeleteSingleConfirmLayoutFactory.class, null);
            add(Editor.ID_EXECUTOR, DeleteSingleExecutor.class);
            Factory.add(DeleteSingleExecutor.class, null, DeleteSingleExecutorLayoutFactory.class, null);
            add(DeleteSingle.ID_FINALIZER, DeleteSingleFinalize.class);
            Factory.add(DeleteSingleFinalize.class, null, DeleteSingleFinalizeLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class PlayZoomFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlaySubApp.ID_DEFAULT_STATE, PlayZoomState.class);
            Factory.add(PlayZoomState.class, PbFunctionCommonFactory.class, PlayZoomStateLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class PseudoRecFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlaySubApp.ID_SINGLE_PB, PseudoRecSingle.class);
            Factory.add(PseudoRecSingle.class, null, PseudoRecSingleLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class PseudoRecZoomFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlaySubApp.ID_DEFAULT_STATE, PseudoRecZoomState.class);
            Factory.add(PseudoRecZoomState.class, null, PseudoRecZoomStateLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class BrowserSingleLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("NORMAL_PB", GFBrowserSingleLayout.class);
            add("NO_FILE", BrowserSingleNoFileLayout.class);
            add(SingleStateBase.ID_BUFFER_PLAYBACK_LAYOUT, GFBrowserSingleLayout.class);
            add(SingleStateBase.ID_NO_BUFFER_LAYOUT, BrowserSingleNoBufferLayout.class);
            add(BrowserSingle.ID_DIALOG_STARTING_4K_PB_LAYOUT, BrowserDialog4kStart.class);
            add(SingleStateBase.ID_4K_PLAYBACK_LAYOUT, BrowserSingle4kLayout.class);
            add(SingleStateBase.ID_VOLUME_ADJUST_LAYOUT, VolumeAdjustmentLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class BrowserIndexLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("NORMAL_PB", GFBrowserIndexLayout.class);
            add("NO_FILE", BrowserIndexNoFileLayout.class);
            add(IndexStateBase.ID_DATA_UNREADY_LAYOUT, BrowserSingleNoBufferLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class DeleteSingleConfirmLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlayStateWithLayoutBase.ID_DEFAULT_LAYOUT, DeleteSingleConfirmLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class DeleteSingleExecutorLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlayStateWithLayoutBase.ID_DEFAULT_LAYOUT, DeleteSingleProcessingLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class DeleteSingleFinalizeLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlayStateWithLayoutBase.ID_DEFAULT_LAYOUT, EventBlockerLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class PlayZoomStateLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("NORMAL_PB", PlayZoomLayout.class);
            add(SingleStateBase.ID_BUFFER_PLAYBACK_LAYOUT, PlayZoomLayout.class);
            add(SingleStateBase.ID_4K_PLAYBACK_LAYOUT, Play4kZoomLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class PseudoRecSingleLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(SingleStateBase.ID_BUFFER_PLAYBACK_LAYOUT, PseudoRecSingleLayout.class);
            add(SingleStateBase.ID_NO_BUFFER_LAYOUT, PseudoRecNoFileLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class PseudoRecZoomStateLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(SingleStateBase.ID_BUFFER_PLAYBACK_LAYOUT, PseudoRecZoomLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class MenuLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("ID_GFTHEMESELECTIONLAYOUT", GFThemeSelectionLayout.class);
            Factory.add(GFThemeSelectionLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_GFISOAUTOMAXMINMENULAYOUT", GFISOAutoMaxMinMenuLayout.class);
            Factory.add(GFISOAutoMaxMinMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_GFIMPORTMENULAYOUT", GFCopyThemeMenuLayout.class);
            Factory.add(GFCopyThemeMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(GFWhiteBalanceMenuLayout.MENU_ID, GFWhiteBalanceMenuLayout.class);
            Factory.add(GFWhiteBalanceMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(GFWhiteBalanceAdjustmentMenuLayout.MENU_ID, GFWhiteBalanceAdjustmentMenuLayout.class);
            Factory.add(GFWhiteBalanceAdjustmentMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_GFFLASHCOMPENSATIONMENULAYOUT", GFFlashCompensationMenuLayout.class);
            Factory.add(GFFlashCompensationMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_AFFLEXIBLEPOSITIONMENULAYOUT", AFFlexiblePositionMenuLayout.class);
            Factory.add(AFFlexiblePositionMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_GFCREATIVESTYLEMENULAYOUT", GFCreativeStyleMenuLayout.class);
            Factory.add(GFCreativeStyleMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_AFLOCALPOSITIONMENULAYOUT", AFLocalPositionMenuLayout.class);
            Factory.add(AFLocalPositionMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(ExposureModeMenuLayout.MENU_ID, GFExposureModeMenuLayout.class);
            Factory.add(GFExposureModeMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_SCENESELECTIONMENULAYOUT", SceneSelectionMenuLayout.class);
            Factory.add(SceneSelectionMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(GFExposureCompensationMenuLayout.MENU_ID, GFExposureCompensationMenuLayout.class);
            Factory.add(GFExposureCompensationMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_MOVIEMODEMENULAYOUT", MovieModeMenuLayout.class);
            Factory.add(MovieModeMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(UnknownItemMenuLayout.MENU_ID, UnknownItemMenuLayout.class);
            Factory.add(UnknownItemMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("LastBastionLayout", LastBastionMenuLayout.class);
            Factory.add(LastBastionMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(FnMenuLayout.MENU_ID, GFFnMenuLayout.class);
            add("ID_GFFNMENULAYOUT", GFFnMenuLayout.class);
            Factory.add(GFFnMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(GFFn15LayerMenuLayout.MENU_ID, GFFn15LayerMenuLayout.class);
            Factory.add(GFFn15LayerMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(GFSpecialScreenMenuLayout.MENU_ID, GFSpecialScreenMenuLayout.class);
            Factory.add(GFSpecialScreenMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(GFFilterSetMenuLayout.MENU_ID, GFFilterSetMenuLayout.class);
            Factory.add(GFFilterSetMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(GFFilterShootingMenuLayout.MENU_ID, GFFilterShootingMenuLayout.class);
            Factory.add(GFFilterShootingMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(GFISOSpecialScreenMenuLayout.MENU_ID, GFISOSpecialScreenMenuLayout.class);
            Factory.add(GFISOSpecialScreenMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(DigitalZoomSettingLayout.MENU_ID, DigitalZoomSettingLayout.class);
            Factory.add(DigitalZoomSettingLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(FocusModeSpecialScreenMenuLayout.MENU_ID, GFFocusModeSpecialScreenMenuLayout.class);
            Factory.add(GFFocusModeSpecialScreenMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(GFFn15LayerExposureCompensationLayout.MENU_ID, GFFn15LayerExposureCompensationLayout.class);
            Factory.add(GFFn15LayerExposureCompensationLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(GFFn15LayerFlashCompensationLayout.MENU_ID, GFFn15LayerFlashCompensationLayout.class);
            Factory.add(GFFn15LayerFlashCompensationLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(GFFn15LayerCreativeStyleLayout.MENU_ID, GFFn15LayerCreativeStyleLayout.class);
            Factory.add(GFFn15LayerCreativeStyleLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(GFFn15LayerWhiteBalanceLayout.MENU_ID, GFFn15LayerWhiteBalanceLayout.class);
            Factory.add(GFFn15LayerWhiteBalanceLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(GFISOFn15LayerMenuLayout.MENU_ID, GFISOFn15LayerMenuLayout.class);
            Factory.add(GFISOFn15LayerMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(GFPageMenuLayout.MENU_ID, GFPageMenuLayout.class);
            Factory.add(GFPageMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(SetMenuLayout.MENU_ID, GFSetMenuLayout.class);
            Factory.add(GFSetMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(SpecialScreenMenuLayoutHiddenEE.MENU_ID, SpecialScreenMenuLayoutHiddenEE.class);
            Factory.add(SpecialScreenMenuLayoutHiddenEE.class, null, OneAnotherMenuLayoutFactory.class, null);
            if (Environment.isNewBizDeviceActionCam() && Environment.hasSubLcd()) {
                add(SegmentMenuLayout.MENU_ID, SegmentMenuLayout.class);
                Factory.add(SegmentMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            }
            add(GFWhiteBalanceLimitSetMenuLayout.MENU_ID, GFWhiteBalanceLimitSetMenuLayout.class);
            Factory.add(GFWhiteBalanceLimitSetMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(GFSettingMenuLayout.MENU_ID, GFSettingMenuLayout.class);
            Factory.add(GFSettingMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(GFLinkAreaMenuLayout.MENU_ID, GFLinkAreaMenuLayout.class);
            Factory.add(GFLinkAreaMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(GFSetting15LayerFilterSetLayout.MENU_ID, GFSetting15LayerFilterSetLayout.class);
            Factory.add(GFSetting15LayerFilterSetLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(GFSetting15LayerExposureCompensationLayout.MENU_ID, GFSetting15LayerExposureCompensationLayout.class);
            Factory.add(GFSetting15LayerExposureCompensationLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(GFSetting15LayerBorderLayout.MENU_ID, GFSetting15LayerBorderLayout.class);
            Factory.add(GFSetting15LayerBorderLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(GFSetting15LayerShadingLayout.MENU_ID, GFSetting15LayerShadingLayout.class);
            Factory.add(GFSetting15LayerShadingLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(GFSetting15LayerTvAvLayout.MENU_ID, GFSetting15LayerTvAvLayout.class);
            Factory.add(GFSetting15LayerTvAvLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(GFSetting15LayerISOLayout.MENU_ID, GFSetting15LayerISOLayout.class);
            Factory.add(GFSetting15LayerISOLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(GFSetting15LayerWhiteBalanceLayout.MENU_ID, GFSetting15LayerWhiteBalanceLayout.class);
            Factory.add(GFSetting15LayerWhiteBalanceLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(GFBorderMenuLayout.MENU_ID, GFBorderMenuLayout.class);
            Factory.add(GFBorderMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(GFShadingMenuLayout.MENU_ID, GFShadingMenuLayout.class);
            Factory.add(GFShadingMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(GFAvMenuLayout.MENU_ID, GFAvMenuLayout.class);
            Factory.add(GFAvMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(GFTvMenuLayout.MENU_ID, GFTvMenuLayout.class);
            Factory.add(GFTvMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(GFISOMenuLayout.MENU_ID, GFISOMenuLayout.class);
            Factory.add(GFISOMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(GFGuideLayout.ID_GFGUIDELAYOUT, GFGuideLayout.class);
            Factory.add(GFGuideLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_GFINTRODUCTIONLAYOUT", GFIntroductionLayout.class);
            add("PreviewLayout", GFPreviewLayout.class);
            Factory.add(GFPreviewLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("AdjustmentLayout", GFAdjustmentLayout.class);
            Factory.add(GFAdjustmentLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(GFAdjustment15LayerBorderLayout.MENU_ID, GFAdjustment15LayerBorderLayout.class);
            Factory.add(GFAdjustment15LayerBorderLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(GFAdjustment15LayerShadingLayout.MENU_ID, GFAdjustment15LayerShadingLayout.class);
            Factory.add(GFAdjustment15LayerShadingLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class OneAnotherMenuLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("ID_GFTHEMESELECTIONLAYOUT", GFThemeSelectionLayout.class);
            add("ID_GFISOAUTOMAXMINMENULAYOUT", GFISOAutoMaxMinMenuLayout.class);
            add("ID_GFIMPORTMENULAYOUT", GFCopyThemeMenuLayout.class);
            add(GFWhiteBalanceMenuLayout.MENU_ID, GFWhiteBalanceMenuLayout.class);
            add(GFWhiteBalanceAdjustmentMenuLayout.MENU_ID, GFWhiteBalanceAdjustmentMenuLayout.class);
            add("ID_GFFLASHCOMPENSATIONMENULAYOUT", GFFlashCompensationMenuLayout.class);
            add("ID_AFFLEXIBLEPOSITIONMENULAYOUT", AFFlexiblePositionMenuLayout.class);
            add("ID_GFCREATIVESTYLEMENULAYOUT", GFCreativeStyleMenuLayout.class);
            add("ID_AFLOCALPOSITIONMENULAYOUT", AFLocalPositionMenuLayout.class);
            add(ExposureModeMenuLayout.MENU_ID, GFExposureModeMenuLayout.class);
            add("ID_SCENESELECTIONMENULAYOUT", SceneSelectionMenuLayout.class);
            add(GFExposureCompensationMenuLayout.MENU_ID, GFExposureCompensationMenuLayout.class);
            add("ID_MOVIEMODEMENULAYOUT", MovieModeMenuLayout.class);
            add(UnknownItemMenuLayout.MENU_ID, UnknownItemMenuLayout.class);
            add("LastBastionLayout", LastBastionMenuLayout.class);
            add(FnMenuLayout.MENU_ID, GFFnMenuLayout.class);
            add("ID_GFFNMENULAYOUT", GFFnMenuLayout.class);
            add(GFFn15LayerMenuLayout.MENU_ID, GFFn15LayerMenuLayout.class);
            add(GFSpecialScreenMenuLayout.MENU_ID, GFSpecialScreenMenuLayout.class);
            add(GFFilterSetMenuLayout.MENU_ID, GFFilterSetMenuLayout.class);
            add(GFFilterShootingMenuLayout.MENU_ID, GFFilterShootingMenuLayout.class);
            add(GFISOSpecialScreenMenuLayout.MENU_ID, GFISOSpecialScreenMenuLayout.class);
            add(DigitalZoomSettingLayout.MENU_ID, DigitalZoomSettingLayout.class);
            add(FocusModeSpecialScreenMenuLayout.MENU_ID, GFFocusModeSpecialScreenMenuLayout.class);
            add(GFFn15LayerExposureCompensationLayout.MENU_ID, GFFn15LayerExposureCompensationLayout.class);
            add(GFFn15LayerFlashCompensationLayout.MENU_ID, GFFn15LayerFlashCompensationLayout.class);
            add(GFFn15LayerCreativeStyleLayout.MENU_ID, GFFn15LayerCreativeStyleLayout.class);
            add(GFFn15LayerWhiteBalanceLayout.MENU_ID, GFFn15LayerWhiteBalanceLayout.class);
            add(GFISOFn15LayerMenuLayout.MENU_ID, GFISOFn15LayerMenuLayout.class);
            add(GFPageMenuLayout.MENU_ID, GFPageMenuLayout.class);
            add(SetMenuLayout.MENU_ID, GFSetMenuLayout.class);
            add(SpecialScreenMenuLayoutHiddenEE.MENU_ID, SpecialScreenMenuLayoutHiddenEE.class);
            if (Environment.isNewBizDeviceActionCam() && Environment.hasSubLcd()) {
                add(SegmentMenuLayout.MENU_ID, SegmentMenuLayout.class);
            }
            add(GFWhiteBalanceLimitSetMenuLayout.MENU_ID, GFWhiteBalanceLimitSetMenuLayout.class);
            add(GFSettingMenuLayout.MENU_ID, GFSettingMenuLayout.class);
            add(GFLinkAreaMenuLayout.MENU_ID, GFLinkAreaMenuLayout.class);
            add(GFSetting15LayerFilterSetLayout.MENU_ID, GFSetting15LayerFilterSetLayout.class);
            add(GFSetting15LayerExposureCompensationLayout.MENU_ID, GFSetting15LayerExposureCompensationLayout.class);
            add(GFSetting15LayerBorderLayout.MENU_ID, GFSetting15LayerBorderLayout.class);
            add(GFSetting15LayerShadingLayout.MENU_ID, GFSetting15LayerShadingLayout.class);
            add(GFSetting15LayerTvAvLayout.MENU_ID, GFSetting15LayerTvAvLayout.class);
            add(GFSetting15LayerISOLayout.MENU_ID, GFSetting15LayerISOLayout.class);
            add(GFSetting15LayerWhiteBalanceLayout.MENU_ID, GFSetting15LayerWhiteBalanceLayout.class);
            add(GFBorderMenuLayout.MENU_ID, GFBorderMenuLayout.class);
            add(GFShadingMenuLayout.MENU_ID, GFShadingMenuLayout.class);
            add(GFAvMenuLayout.MENU_ID, GFAvMenuLayout.class);
            add(GFTvMenuLayout.MENU_ID, GFTvMenuLayout.class);
            add(GFISOMenuLayout.MENU_ID, GFISOMenuLayout.class);
            add("PreviewLayout", GFPreviewLayout.class);
            add("AdjustmentLayout", GFAdjustmentLayout.class);
            add(GFAdjustment15LayerBorderLayout.MENU_ID, GFAdjustment15LayerBorderLayout.class);
            add(GFAdjustment15LayerShadingLayout.MENU_ID, GFAdjustment15LayerShadingLayout.class);
            add("ID_GFINTRODUCTIONLAYOUT", GFIntroductionLayout.class);
            add(GFGuideLayout.ID_GFGUIDELAYOUT, GFGuideLayout.class);
        }
    }
}
