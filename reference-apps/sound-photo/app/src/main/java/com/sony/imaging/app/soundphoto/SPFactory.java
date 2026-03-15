package com.sony.imaging.app.soundphoto;

import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.caution.CautionDisplayState;
import com.sony.imaging.app.base.caution.CautionFragment;
import com.sony.imaging.app.base.common.layout.ExitScreenLayout;
import com.sony.imaging.app.base.common.layout.ForceExitScreenLayout;
import com.sony.imaging.app.base.menu.layout.AFFlexiblePositionMenuLayout;
import com.sony.imaging.app.base.menu.layout.AFLocalPositionMenuLayout;
import com.sony.imaging.app.base.menu.layout.CreativeStyleMenuLayout;
import com.sony.imaging.app.base.menu.layout.DigitalZoomSettingLayout;
import com.sony.imaging.app.base.menu.layout.ExposureCompensationMenuLayout;
import com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout;
import com.sony.imaging.app.base.menu.layout.ExposureModeMenuMovieFeatureLayout;
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
import com.sony.imaging.app.base.menu.layout.LastBastionMenuLayout;
import com.sony.imaging.app.base.menu.layout.MovieModeMenuLayout;
import com.sony.imaging.app.base.menu.layout.PageMenuLayout;
import com.sony.imaging.app.base.menu.layout.SceneSelectionMenuLayout;
import com.sony.imaging.app.base.menu.layout.SetMenuLayout;
import com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout;
import com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayoutHiddenEE;
import com.sony.imaging.app.base.menu.layout.UnknownItemMenuLayout;
import com.sony.imaging.app.base.menu.layout.WhiteBalanceAdjustmentMenuLayout;
import com.sony.imaging.app.base.menu.layout.WhiteBalanceMenuLayout;
import com.sony.imaging.app.base.playback.ChangingOutputMode;
import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.base.playback.TransitionToShooting;
import com.sony.imaging.app.base.playback.base.IndexStateBase;
import com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase;
import com.sony.imaging.app.base.playback.base.PlaySubApp;
import com.sony.imaging.app.base.playback.base.SingleStateBase;
import com.sony.imaging.app.base.playback.base.editor.Editor;
import com.sony.imaging.app.base.playback.browser.BrowserSingle;
import com.sony.imaging.app.base.playback.delete.single.DeleteSingle;
import com.sony.imaging.app.base.playback.delete.single.DeleteSingleFinalize;
import com.sony.imaging.app.base.playback.delete.single.DeleteSingleTestContents;
import com.sony.imaging.app.base.playback.layout.BrowserIndexNoFileLayout;
import com.sony.imaging.app.base.playback.layout.BrowserSingleNoBufferLayout;
import com.sony.imaging.app.base.playback.layout.BrowserSingleNoFileLayout;
import com.sony.imaging.app.base.playback.layout.EventBlockerLayout;
import com.sony.imaging.app.base.playback.layout.Play4kZoomLayout;
import com.sony.imaging.app.base.playback.layout.PlayZoomLayout;
import com.sony.imaging.app.base.playback.layout.PseudoRecNoFileLayout;
import com.sony.imaging.app.base.playback.layout.PseudoRecSingleLayout;
import com.sony.imaging.app.base.playback.layout.PseudoRecZoomLayout;
import com.sony.imaging.app.base.playback.pseudorec.PseudoRec;
import com.sony.imaging.app.base.playback.pseudorec.PseudoRecSingle;
import com.sony.imaging.app.base.playback.zoom.PlayZoom;
import com.sony.imaging.app.base.playback.zoom.PlayZoomState;
import com.sony.imaging.app.base.shooting.AutoReviewForLimitedContShootingState;
import com.sony.imaging.app.base.shooting.AutoReviewState;
import com.sony.imaging.app.base.shooting.CaptureState;
import com.sony.imaging.app.base.shooting.CustomWhiteBalanceCaptureState;
import com.sony.imaging.app.base.shooting.CustomWhiteBalanceControllerState;
import com.sony.imaging.app.base.shooting.CustomWhiteBalanceEEState;
import com.sony.imaging.app.base.shooting.CustomWhiteBalanceExposureState;
import com.sony.imaging.app.base.shooting.DevelopmentState;
import com.sony.imaging.app.base.shooting.ExposureModeCheckState;
import com.sony.imaging.app.base.shooting.FocusAdjustmentState;
import com.sony.imaging.app.base.shooting.MfAssistState;
import com.sony.imaging.app.base.shooting.MovieMenuState;
import com.sony.imaging.app.base.shooting.S1OffEEState;
import com.sony.imaging.app.base.shooting.StateBase;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.layout.AutoReviewForLimitedContShootingLayout;
import com.sony.imaging.app.base.shooting.layout.AutoReviewLayout;
import com.sony.imaging.app.base.shooting.layout.CustomWhiteBalanceConfLayout;
import com.sony.imaging.app.base.shooting.layout.CustomWhiteBalanceEELayout;
import com.sony.imaging.app.base.shooting.layout.FocusAdjustmentLayout;
import com.sony.imaging.app.base.shooting.layout.FocusLayout;
import com.sony.imaging.app.base.shooting.layout.GuideLayout;
import com.sony.imaging.app.base.shooting.layout.MfAssistLayout;
import com.sony.imaging.app.base.shooting.layout.ProgressLayout;
import com.sony.imaging.app.base.shooting.layout.S1OffLayout;
import com.sony.imaging.app.base.shooting.layout.ShootingLayout;
import com.sony.imaging.app.base.shooting.layout.StableLayout;
import com.sony.imaging.app.base.shooting.movie.MovieCaptureState;
import com.sony.imaging.app.base.shooting.movie.MovieRecStandbyState;
import com.sony.imaging.app.base.shooting.movie.MovieRecState;
import com.sony.imaging.app.base.shooting.movie.MovieSaveState;
import com.sony.imaging.app.base.shooting.movie.layout.MovieSaveLayout;
import com.sony.imaging.app.base.shooting.movie.trigger.MovieCaptureStateKeyHandler;
import com.sony.imaging.app.base.shooting.movie.trigger.MovieRecStandbyStateKeyHandler;
import com.sony.imaging.app.base.shooting.movie.trigger.MovieRecStateKeyHandler;
import com.sony.imaging.app.base.shooting.movie.trigger.MovieSaveStateKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.AutoReviewForLimitedContShootingStateKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.AutoReviewStateKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.CaptureStateKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.CustomWhiteBalanceCaptureKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.CustomWhiteBalanceEEKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.CustomWhiteBalanceExposureKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.DevelopmentStateKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.EEStateKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.FocusAdjustmentKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.S1OnEEStateKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.SelfTimerCaptureStateKeyHandler;
import com.sony.imaging.app.fw.Factory;
import com.sony.imaging.app.fw.LayoutFactory;
import com.sony.imaging.app.fw.StateFactory;
import com.sony.imaging.app.soundphoto.common.caution.SPCautionLayout;
import com.sony.imaging.app.soundphoto.menu.base.layout.SPPBPageMenuLayout;
import com.sony.imaging.app.soundphoto.menu.base.layout.SPPageMenuLayout;
import com.sony.imaging.app.soundphoto.menu.layout.ApplicationSettingsMenuLayout;
import com.sony.imaging.app.soundphoto.playback.SPPlayRootContainer;
import com.sony.imaging.app.soundphoto.playback.browser.SP4KDisplayBrowser;
import com.sony.imaging.app.soundphoto.playback.browser.SP4KDisplayBrowserSingle;
import com.sony.imaging.app.soundphoto.playback.browser.SPBrowser;
import com.sony.imaging.app.soundphoto.playback.browser.SPBrowserIndex;
import com.sony.imaging.app.soundphoto.playback.browser.SPBrowserSingle;
import com.sony.imaging.app.soundphoto.playback.browser.SPVolumeBrowserSingle;
import com.sony.imaging.app.soundphoto.playback.browser.SPVolumeSettingBrowser;
import com.sony.imaging.app.soundphoto.playback.delete.multiple.DeleteMultiple;
import com.sony.imaging.app.soundphoto.playback.delete.multiple.DeleteMultipleConfirm;
import com.sony.imaging.app.soundphoto.playback.delete.multiple.DeleteMultipleConfirmLayout;
import com.sony.imaging.app.soundphoto.playback.delete.multiple.DeleteMultipleExecutor;
import com.sony.imaging.app.soundphoto.playback.delete.multiple.DeleteMultipleIndex;
import com.sony.imaging.app.soundphoto.playback.delete.multiple.DeleteMultipleIndexLayout;
import com.sony.imaging.app.soundphoto.playback.delete.multiple.DeleteMultipleProcessingLayout;
import com.sony.imaging.app.soundphoto.playback.delete.multiple.DeleteMultipleSingle;
import com.sony.imaging.app.soundphoto.playback.delete.multiple.DeleteMultipleSingleLayout;
import com.sony.imaging.app.soundphoto.playback.delete.single.executor.SPDeleteSingleConfirm;
import com.sony.imaging.app.soundphoto.playback.delete.single.executor.SPDeleteSingleExecutor;
import com.sony.imaging.app.soundphoto.playback.delete.single.layout.SPDeleteSingleProcessingLayout;
import com.sony.imaging.app.soundphoto.playback.delete.single.layout.state.SPDeleteSingle;
import com.sony.imaging.app.soundphoto.playback.delete.sound.DeleteSoundConfirmLayout;
import com.sony.imaging.app.soundphoto.playback.layout.BrowserSingle4kLayout;
import com.sony.imaging.app.soundphoto.playback.layout.SPBrowserDialog4kStart;
import com.sony.imaging.app.soundphoto.playback.layout.SPBrowserIndexLayout;
import com.sony.imaging.app.soundphoto.playback.layout.SPBrowserSingleLayout;
import com.sony.imaging.app.soundphoto.playback.layout.SPBrowserSingleNoBufferLayout;
import com.sony.imaging.app.soundphoto.playback.menu.layout.PBSetMenuLayout;
import com.sony.imaging.app.soundphoto.playback.menu.layout.SPDeleteMenuConfirmLayout;
import com.sony.imaging.app.soundphoto.playback.player.layout.SPBrowserPlayingLayout;
import com.sony.imaging.app.soundphoto.playback.setting.volume.layout.SPBrowserVolumeSettingLayout;
import com.sony.imaging.app.soundphoto.playback.state.SPPlayBackMenuState;
import com.sony.imaging.app.soundphoto.playback.upload.multiple.UploadMultiple;
import com.sony.imaging.app.soundphoto.playback.upload.multiple.UploadMultipleConfirm;
import com.sony.imaging.app.soundphoto.playback.upload.multiple.UploadMultipleConfirmLayout;
import com.sony.imaging.app.soundphoto.playback.upload.multiple.UploadMultipleExecutor;
import com.sony.imaging.app.soundphoto.playback.upload.multiple.UploadMultipleIndex;
import com.sony.imaging.app.soundphoto.playback.upload.multiple.UploadMultipleIndexLayout;
import com.sony.imaging.app.soundphoto.playback.upload.multiple.UploadMultipleProcessingLayout;
import com.sony.imaging.app.soundphoto.playback.upload.multiple.UploadMultipleSingle;
import com.sony.imaging.app.soundphoto.playback.upload.multiple.UploadMultipleSingleLayout;
import com.sony.imaging.app.soundphoto.shooting.SPForceSettingState;
import com.sony.imaging.app.soundphoto.shooting.state.SPEEState;
import com.sony.imaging.app.soundphoto.shooting.state.SPNormalCaptureState;
import com.sony.imaging.app.soundphoto.shooting.state.SPS1OffEEState;
import com.sony.imaging.app.soundphoto.shooting.state.SPS1OnEEState;
import com.sony.imaging.app.soundphoto.shooting.state.SPSelfTimerCaptureState;
import com.sony.imaging.app.soundphoto.shooting.state.SPShootingMenuState;
import com.sony.imaging.app.soundphoto.shooting.state.SPShootingState;
import com.sony.imaging.app.soundphoto.shooting.state.keyHandler.SPS1OffEEStateKeyHandler;
import com.sony.imaging.app.soundphoto.shooting.state.layout.SPAudioBufferAnimationLayout;
import com.sony.imaging.app.soundphoto.shooting.state.layout.SPShootingProcessingLayout;
import com.sony.imaging.app.soundphoto.shooting.trigger.SPMFAssistKeyHandler;
import com.sony.imaging.app.soundphoto.shooting.trigger.SPShootingStateKeyHandler;
import com.sony.imaging.app.soundphoto.state.ExitScreenProcessingLayout;
import com.sony.imaging.app.soundphoto.state.SPExitScreenState;
import com.sony.imaging.app.soundphoto.state.SPForceExitScreenState;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class SPFactory extends Factory {
    @Override // com.sony.imaging.app.fw.Factory
    protected Class<? extends StateFactory> getTop() {
        return RootStateFactory.class;
    }

    /* loaded from: classes.dex */
    public static class RootStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(BaseApp.APP_SHOOTING, SPShootingState.class);
            SPFactory.add(SPShootingState.class, ShootingStateFactory.class, ShootingLayoutFactory.class, SPShootingStateKeyHandler.class);
            add(BaseApp.APP_PLAY, SPPlayRootContainer.class);
            SPFactory.add(SPPlayRootContainer.class, SPPlaybackStateFactory.class, null, null);
            add(BaseApp.CAUTION, CautionFragment.class);
            SPFactory.add(CautionFragment.class, CautionStateFactory.class, null, null);
            add(BaseApp.EXIT_SCREEN, SPExitScreenState.class);
            SPFactory.add(SPExitScreenState.class, null, ExitScreenLayoutFactory.class, null);
            add(BaseApp.FORCE_EXIT_SCREEN, SPForceExitScreenState.class);
            SPFactory.add(SPForceExitScreenState.class, null, ForceExitScreenLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class CautionStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("CautionDisplayState", CautionDisplayState.class);
            SPFactory.add(CautionDisplayState.class, null, CautionLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class ShootingStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("FORCESETTING", SPForceSettingState.class);
            SPFactory.add(SPForceSettingState.class, null, null, null);
            add("ExposureModeCheck", ExposureModeCheckState.class);
            SPFactory.add(ExposureModeCheckState.class, null, null, null);
            add("EE", SPEEState.class);
            SPFactory.add(SPEEState.class, EEStateFactory.class, null, EEStateKeyHandler.class);
            add("Capture", CaptureState.class);
            SPFactory.add(CaptureState.class, CaptureStateFactory.class, null, CaptureStateKeyHandler.class);
            add("Development", DevelopmentState.class);
            SPFactory.add(DevelopmentState.class, DevelopmentStateFactory.class, DevelopmentLayoutFactory.class, DevelopmentStateKeyHandler.class);
            add(AutoReviewState.STATE_NAME, AutoReviewState.class);
            SPFactory.add(AutoReviewState.class, null, AutoReviewLayoutFactory.class, AutoReviewStateKeyHandler.class);
            add("AutoReviewForLimitedContShooting", AutoReviewForLimitedContShootingState.class);
            SPFactory.add(AutoReviewForLimitedContShootingState.class, null, AutoReviewForLimitedContShootingLayoutFactory.class, AutoReviewForLimitedContShootingStateKeyHandler.class);
            add("CustomWhiteBalance", CustomWhiteBalanceControllerState.class);
            SPFactory.add(CustomWhiteBalanceControllerState.class, CustomWhiteBalanceControllerStateFactory.class, null, null);
            if (Environment.isMovieAPISupported()) {
                add("MovieCapture", MovieCaptureState.class);
                SPFactory.add(MovieCaptureState.class, null, MovieLayoutFactory.class, MovieCaptureStateKeyHandler.class);
                add("MovieRec", MovieRecState.class);
                SPFactory.add(MovieRecState.class, MovieRecStateFactory.class, MovieLayoutFactory.class, MovieRecStateKeyHandler.class);
                add("MovieSave", MovieSaveState.class);
                SPFactory.add(MovieSaveState.class, null, MovieSaveLayoutFactory.class, MovieSaveStateKeyHandler.class);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class MovieRecStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("Menu", MovieMenuState.class);
            SPFactory.add(MovieMenuState.class, null, MenuLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class MovieLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(StateBase.DEFAULT_LAYOUT, StableLayout.class);
            add(DigitalZoomSettingLayout.MENU_ID, DigitalZoomSettingLayout.class);
            add(StateBase.FOCUS_LAYOUT, FocusLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class MovieSaveLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("StableLayout", StableLayout.class);
            add("MovieSaveLayout", MovieSaveLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class EEStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(S1OffEEState.STATE_NAME, SPS1OffEEState.class);
            SPFactory.add(SPS1OffEEState.class, null, S1OffEELayoutFactory.class, SPS1OffEEStateKeyHandler.class);
            add("S1OnEE", SPS1OnEEState.class);
            SPFactory.add(SPS1OnEEState.class, null, S1OnEELayoutFactory.class, S1OnEEStateKeyHandler.class);
            add("MfAssist", MfAssistState.class);
            SPFactory.add(MfAssistState.class, null, MfAssistLayoutFactory.class, SPMFAssistKeyHandler.class);
            add("FocusAdjustment", FocusAdjustmentState.class);
            SPFactory.add(FocusAdjustmentState.class, null, FocusAdjustmentFactory.class, FocusAdjustmentKeyHandler.class);
            add("MovieRecStandby", MovieRecStandbyState.class);
            SPFactory.add(MovieRecStandbyState.class, null, MovieLayoutFactory.class, MovieRecStandbyStateKeyHandler.class);
            add("Menu", SPShootingMenuState.class);
            SPFactory.add(SPShootingMenuState.class, null, MenuLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class CaptureStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("NormalCapture", SPNormalCaptureState.class);
            SPFactory.add(SPNormalCaptureState.class, null, NormalCaptureLayoutFactory.class, null);
            add("SelfTimerCapture", SPSelfTimerCaptureState.class);
            SPFactory.add(SPSelfTimerCaptureState.class, null, SelfTimerCaptureLayoutFactory.class, SelfTimerCaptureStateKeyHandler.class);
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
            SPFactory.add(CustomWhiteBalanceEEState.class, null, CWBEELayoutFactory.class, CustomWhiteBalanceEEKeyHandler.class);
            add("CWBCapture", CustomWhiteBalanceCaptureState.class);
            SPFactory.add(CustomWhiteBalanceCaptureState.class, null, null, CustomWhiteBalanceCaptureKeyHandler.class);
            add("CWBExposure", CustomWhiteBalanceExposureState.class);
            SPFactory.add(CustomWhiteBalanceExposureState.class, null, CWBExposureLayoutFactory.class, CustomWhiteBalanceExposureKeyHandler.class);
        }
    }

    /* loaded from: classes.dex */
    public static class ExitScreenLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("ExitScreen", ExitScreenLayout.class);
            add(ExitScreenProcessingLayout.TAG, ExitScreenProcessingLayout.class);
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
            add("cautionLayout", SPCautionLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class ShootingLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(StateBase.DEFAULT_LAYOUT, ShootingLayout.class);
            add(SPShootingProcessingLayout.TAG, SPShootingProcessingLayout.class);
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
            add(StateBase.DEFAULT_LAYOUT, StableLayout.class);
            add(StateBase.S1OFF_LAYOUT, S1OffLayout.class);
            add(StateBase.FOCUS_LAYOUT, FocusLayout.class);
            add(StateBase.GUIDE_LAYOUT, GuideLayout.class);
            add(SPAudioBufferAnimationLayout.TAG, SPAudioBufferAnimationLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class S1OnEELayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(StateBase.DEFAULT_LAYOUT, StableLayout.class);
            add(StateBase.FOCUS_LAYOUT, FocusLayout.class);
            add(SPAudioBufferAnimationLayout.TAG, SPAudioBufferAnimationLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class MfAssistLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(StateBase.DEFAULT_LAYOUT, MfAssistLayout.class);
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
            add(StateBase.DEFAULT_LAYOUT, StableLayout.class);
            add(StateBase.FOCUS_LAYOUT, FocusLayout.class);
            add(SPAudioBufferAnimationLayout.TAG, SPAudioBufferAnimationLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class NormalCaptureLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(StateBase.DEFAULT_LAYOUT, StableLayout.class);
            add(StateBase.FOCUS_LAYOUT, FocusLayout.class);
            add(SPAudioBufferAnimationLayout.TAG, SPAudioBufferAnimationLayout.class);
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
    public static class PbFunctionCommonFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlayRootContainer.ID_DELETE_SINGLE, SPDeleteSingle.class);
            SPFactory.add(SPDeleteSingle.class, DeleteSingleFactory.class, null, null);
        }
    }

    /* loaded from: classes.dex */
    public static class DeleteSingleFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(Editor.ID_TEST_THIS, DeleteSingleTestContents.class);
            add(Editor.ID_CONFIRM, SPDeleteSingleConfirm.class);
            SPFactory.add(SPDeleteSingleConfirm.class, null, DeleteSingleConfirmLayoutFactory.class, null);
            add(Editor.ID_EXECUTOR, SPDeleteSingleExecutor.class);
            SPFactory.add(SPDeleteSingleExecutor.class, null, DeleteSingleExecutorLayoutFactory.class, null);
            add(DeleteSingle.ID_FINALIZER, DeleteSingleFinalize.class);
            SPFactory.add(DeleteSingleFinalize.class, null, DeleteSingleFinalizeLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class PseudoRecFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlaySubApp.ID_SINGLE_PB, PseudoRecSingle.class);
            SPFactory.add(PseudoRecSingle.class, null, PseudoRecSingleLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class DeleteSingleConfirmLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlayStateWithLayoutBase.ID_DEFAULT_LAYOUT, DeleteSoundConfirmLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class DeleteSingleExecutorLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlayStateWithLayoutBase.ID_DEFAULT_LAYOUT, SPDeleteSingleProcessingLayout.class);
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
    public static class PlayZoomFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlaySubApp.ID_DEFAULT_STATE, PlayZoomState.class);
            SPFactory.add(PlayZoomState.class, PbFunctionCommonFactory.class, PlayZoomStateLayoutFactory.class, null);
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
            add("ID_WHITEBALANCEMENULAYOUT", WhiteBalanceMenuLayout.class);
            SPFactory.add(WhiteBalanceMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(WhiteBalanceAdjustmentMenuLayout.MENU_ID, WhiteBalanceAdjustmentMenuLayout.class);
            SPFactory.add(WhiteBalanceAdjustmentMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_FLASHCOMPENSATIONMENULAYOUT", FlashCompensationMenuLayout.class);
            SPFactory.add(FlashCompensationMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_AFFLEXIBLEPOSITIONMENULAYOUT", AFFlexiblePositionMenuLayout.class);
            SPFactory.add(AFFlexiblePositionMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_CREATIVESTYLEMENULAYOUT", CreativeStyleMenuLayout.class);
            SPFactory.add(CreativeStyleMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_AFLOCALPOSITIONMENULAYOUT", AFLocalPositionMenuLayout.class);
            SPFactory.add(AFLocalPositionMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            if (ExposureModeController.isMovieMainFeature()) {
                add(ExposureModeMenuLayout.MENU_ID, ExposureModeMenuMovieFeatureLayout.class);
                SPFactory.add(ExposureModeMenuMovieFeatureLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            } else {
                add(ExposureModeMenuLayout.MENU_ID, ExposureModeMenuLayout.class);
                SPFactory.add(ExposureModeMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            }
            add("ID_SCENESELECTIONMENULAYOUT", SceneSelectionMenuLayout.class);
            SPFactory.add(SceneSelectionMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_EXPOSURECOMPENSATIONMENULAYOUT", ExposureCompensationMenuLayout.class);
            SPFactory.add(ExposureCompensationMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_MOVIEMODEMENULAYOUT", MovieModeMenuLayout.class);
            SPFactory.add(MovieModeMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(UnknownItemMenuLayout.MENU_ID, UnknownItemMenuLayout.class);
            SPFactory.add(UnknownItemMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("LastBastionLayout", LastBastionMenuLayout.class);
            SPFactory.add(LastBastionMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(FnMenuLayout.MENU_ID, FnMenuLayout.class);
            SPFactory.add(FnMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(Fn15LayerMenuLayout.MENU_ID, Fn15LayerMenuLayout.class);
            SPFactory.add(Fn15LayerMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(SpecialScreenMenuLayout.MENU_ID, SpecialScreenMenuLayout.class);
            SPFactory.add(SpecialScreenMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(ISOSpecialScreenMenuLayout.MENU_ID, ISOSpecialScreenMenuLayout.class);
            SPFactory.add(ISOSpecialScreenMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(DigitalZoomSettingLayout.MENU_ID, DigitalZoomSettingLayout.class);
            SPFactory.add(DigitalZoomSettingLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(FocusModeSpecialScreenMenuLayout.MENU_ID, FocusModeSpecialScreenMenuLayout.class);
            SPFactory.add(FocusModeSpecialScreenMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(Fn15LayerExposureCompensationLayout.MENU_ID, Fn15LayerExposureCompensationLayout.class);
            SPFactory.add(Fn15LayerExposureCompensationLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(Fn15LayerFlashCompensationLayout.MENU_ID, Fn15LayerFlashCompensationLayout.class);
            SPFactory.add(Fn15LayerFlashCompensationLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(Fn15LayerCreativeStyleLayout.MENU_ID, Fn15LayerCreativeStyleLayout.class);
            SPFactory.add(Fn15LayerCreativeStyleLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(Fn15LayerWhiteBalanceLayout.MENU_ID, Fn15LayerWhiteBalanceLayout.class);
            SPFactory.add(Fn15LayerWhiteBalanceLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(ISOFn15LayerMenuLayout.MENU_ID, ISOFn15LayerMenuLayout.class);
            SPFactory.add(ISOFn15LayerMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(PageMenuLayout.MENU_ID, SPPageMenuLayout.class);
            SPFactory.add(SPPageMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(SetMenuLayout.MENU_ID, SetMenuLayout.class);
            SPFactory.add(SetMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(SpecialScreenMenuLayoutHiddenEE.MENU_ID, SpecialScreenMenuLayoutHiddenEE.class);
            SPFactory.add(SpecialScreenMenuLayoutHiddenEE.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(ApplicationSettingsMenuLayout.TAG, ApplicationSettingsMenuLayout.class);
            SPFactory.add(ApplicationSettingsMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
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
            if (ExposureModeController.isMovieMainFeature()) {
                add(ExposureModeMenuLayout.MENU_ID, ExposureModeMenuMovieFeatureLayout.class);
            } else {
                add(ExposureModeMenuLayout.MENU_ID, ExposureModeMenuLayout.class);
            }
            add("ID_SCENESELECTIONMENULAYOUT", SceneSelectionMenuLayout.class);
            add("ID_EXPOSURECOMPENSATIONMENULAYOUT", ExposureCompensationMenuLayout.class);
            add("ID_MOVIEMODEMENULAYOUT", MovieModeMenuLayout.class);
            add(UnknownItemMenuLayout.MENU_ID, UnknownItemMenuLayout.class);
            add("LastBastionLayout", LastBastionMenuLayout.class);
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
            add(PageMenuLayout.MENU_ID, SPPageMenuLayout.class);
            add(SetMenuLayout.MENU_ID, SetMenuLayout.class);
            add(SpecialScreenMenuLayoutHiddenEE.MENU_ID, SpecialScreenMenuLayoutHiddenEE.class);
            add(ApplicationSettingsMenuLayout.TAG, ApplicationSettingsMenuLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class SPPlaybackStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlayRootContainer.ID_BROWSER, SPBrowser.class);
            SPFactory.add(SPBrowser.class, SPBrowserFactory.class, null, null);
            add(SPPlayRootContainer.ID_DELETE_MULTIPLE, DeleteMultiple.class);
            SPFactory.add(DeleteMultiple.class, DeleteMultipleFactory.class, null, null);
            add(SPPlayRootContainer.ID_UPLOAD_MULTIPLE, UploadMultiple.class);
            SPFactory.add(UploadMultiple.class, UploadMultipleFactory.class, null, null);
            add(SPPlayRootContainer.ID_TRANSIT_4K_DISPLAY, SP4KDisplayBrowser.class);
            SPFactory.add(SP4KDisplayBrowser.class, SP4KDisplayBrowserFactory.class, null, null);
            add(SPPlayRootContainer.ID_VOLUME_SETTING, SPVolumeSettingBrowser.class);
            SPFactory.add(SPVolumeSettingBrowser.class, SPVolumeBrowserFactory.class, null, null);
            add(PlayRootContainer.ID_PLAYZOOM, PlayZoom.class);
            SPFactory.add(PlayZoom.class, PlayZoomFactory.class, null, null);
            add(PlayRootContainer.ID_PSEUDOREC, PseudoRec.class);
            SPFactory.add(PseudoRec.class, PseudoRecFactory.class, null, null);
            add(PlayRootContainer.ID_TRANSITING_TO_SHOOTING, TransitionToShooting.class);
            add(PlayRootContainer.ID_CHANGING_OUTPUT_MODE, ChangingOutputMode.class);
        }
    }

    /* loaded from: classes.dex */
    public static class SPBrowserFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlaySubApp.ID_SINGLE_PB, SPBrowserSingle.class);
            SPFactory.add(SPBrowserSingle.class, PbFunctionCommonFactory.class, SPBrowserSingleLayoutFactory.class, null);
            add("Menu", SPPlayBackMenuState.class);
            SPFactory.add(SPPlayBackMenuState.class, null, PBMenuLayoutFactory.class, null);
            add(PlaySubApp.ID_INDEX_PB, SPBrowserIndex.class);
            SPFactory.add(SPBrowserIndex.class, PbFunctionCommonFactory.class, SPBrowserIndexLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class SP4KDisplayBrowserFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlaySubApp.ID_SINGLE_PB, SP4KDisplayBrowserSingle.class);
            SPFactory.add(SP4KDisplayBrowserSingle.class, PbFunctionCommonFactory.class, SPBrowserSingleLayoutFactory.class, null);
            add(PlaySubApp.ID_INDEX_PB, SP4KDisplayBrowserSingle.class);
            SPFactory.add(SP4KDisplayBrowserSingle.class, PbFunctionCommonFactory.class, SPBrowserSingleLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class SPVolumeBrowserFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlaySubApp.ID_SINGLE_PB, SPVolumeBrowserSingle.class);
            SPFactory.add(SPVolumeBrowserSingle.class, PbFunctionCommonFactory.class, SPBrowserVolumeSingleLayoutFactory.class, null);
            add(PlaySubApp.ID_INDEX_PB, SPVolumeBrowserSingle.class);
            SPFactory.add(SPVolumeBrowserSingle.class, PbFunctionCommonFactory.class, SPBrowserVolumeSingleLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class SPBrowserSingleLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("NORMAL_PB", SPBrowserPlayingLayout.class);
            add("NO_FILE", BrowserSingleNoFileLayout.class);
            add(SingleStateBase.ID_BUFFER_PLAYBACK_LAYOUT, SPBrowserPlayingLayout.class);
            add(SingleStateBase.ID_NO_BUFFER_LAYOUT, SPBrowserSingleNoBufferLayout.class);
            add(BrowserSingle.ID_DIALOG_STARTING_4K_PB_LAYOUT, SPBrowserDialog4kStart.class);
            add(SingleStateBase.ID_4K_PLAYBACK_LAYOUT, BrowserSingle4kLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class SPBrowserSingleSoundDeleteLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("NORMAL_PB", SPBrowserSingleLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class SPBrowserVolumeSingleLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("NORMAL_PB", SPBrowserVolumeSettingLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class SPBrowserIndexLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("NORMAL_PB", SPBrowserIndexLayout.class);
            add("NO_FILE", BrowserIndexNoFileLayout.class);
            add(IndexStateBase.ID_DATA_UNREADY_LAYOUT, BrowserSingleNoBufferLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class PBMenuLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PageMenuLayout.MENU_ID, SPPBPageMenuLayout.class);
            SPFactory.add(SPPBPageMenuLayout.class, null, PlayMenuGuideLayoutFactory.class, null);
            add(SetMenuLayout.MENU_ID, PBSetMenuLayout.class);
            SPFactory.add(PBSetMenuLayout.class, null, PlayMenuGuideLayoutFactory.class, null);
            add(SPPlayBackMenuState.ID_DELETE_SELECTOR, SPDeleteMenuConfirmLayout.class);
            SPFactory.add(SPDeleteMenuConfirmLayout.class, null, PlayMenuGuideLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class PlayMenuGuideLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PageMenuLayout.MENU_ID, SPPBPageMenuLayout.class);
            add(SetMenuLayout.MENU_ID, PBSetMenuLayout.class);
            add(SPPlayBackMenuState.ID_DELETE_SELECTOR, SPDeleteMenuConfirmLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class DeleteMultipleFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlaySubApp.ID_SINGLE_PB, DeleteMultipleSingle.class);
            SPFactory.add(DeleteMultipleSingle.class, null, DeleteMultipleSingleLayoutFactory.class, null);
            add(PlaySubApp.ID_INDEX_PB, DeleteMultipleIndex.class);
            SPFactory.add(DeleteMultipleIndex.class, null, DeleteMultipleIndexLayoutFactory.class, null);
            add(Editor.ID_CONFIRM, DeleteMultipleConfirm.class);
            SPFactory.add(DeleteMultipleConfirm.class, null, DeleteMultipleConfirmLayoutFactory.class, null);
            add(Editor.ID_EXECUTOR, DeleteMultipleExecutor.class);
            SPFactory.add(DeleteMultipleExecutor.class, null, DeleteMultipleExecutorLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class UploadMultipleFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlaySubApp.ID_SINGLE_PB, UploadMultipleSingle.class);
            SPFactory.add(UploadMultipleSingle.class, null, UploadMultipleSingleLayoutFactory.class, null);
            add(PlaySubApp.ID_INDEX_PB, UploadMultipleIndex.class);
            SPFactory.add(UploadMultipleIndex.class, null, UploadMultipleIndexLayoutFactory.class, null);
            add(Editor.ID_CONFIRM, UploadMultipleConfirm.class);
            SPFactory.add(UploadMultipleConfirm.class, null, UploadMultipleConfirmLayoutFactory.class, null);
            add(Editor.ID_EXECUTOR, UploadMultipleExecutor.class);
            SPFactory.add(UploadMultipleExecutor.class, null, UploadMultipleExecutorLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class DeleteMultipleSingleLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("NORMAL_PB", DeleteMultipleSingleLayout.class);
            add(SingleStateBase.ID_NO_BUFFER_LAYOUT, SPBrowserSingleNoBufferLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class UploadMultipleSingleLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("NORMAL_PB", UploadMultipleSingleLayout.class);
            add(SingleStateBase.ID_NO_BUFFER_LAYOUT, SPBrowserSingleNoBufferLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class DeleteMultipleIndexLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("NORMAL_PB", DeleteMultipleIndexLayout.class);
            add(IndexStateBase.ID_DATA_UNREADY_LAYOUT, BrowserSingleNoBufferLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class UploadMultipleIndexLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("NORMAL_PB", UploadMultipleIndexLayout.class);
            add(IndexStateBase.ID_DATA_UNREADY_LAYOUT, BrowserSingleNoBufferLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class DeleteMultipleConfirmLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlayStateWithLayoutBase.ID_DEFAULT_LAYOUT, DeleteMultipleConfirmLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class UploadMultipleConfirmLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlayStateWithLayoutBase.ID_DEFAULT_LAYOUT, UploadMultipleConfirmLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class DeleteMultipleExecutorLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlayStateWithLayoutBase.ID_DEFAULT_LAYOUT, DeleteMultipleProcessingLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class UploadMultipleExecutorLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlayStateWithLayoutBase.ID_DEFAULT_LAYOUT, UploadMultipleProcessingLayout.class);
        }
    }
}
