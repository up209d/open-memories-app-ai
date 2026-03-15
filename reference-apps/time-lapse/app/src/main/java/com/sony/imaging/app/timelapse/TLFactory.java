package com.sony.imaging.app.timelapse;

import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.caution.CautionDisplayState;
import com.sony.imaging.app.base.caution.CautionFragment;
import com.sony.imaging.app.base.common.ExitScreenState;
import com.sony.imaging.app.base.common.ForceExitScreenState;
import com.sony.imaging.app.base.common.layout.ExitScreenLayout;
import com.sony.imaging.app.base.common.layout.ForceExitScreenLayout;
import com.sony.imaging.app.base.menu.layout.AFFlexiblePositionMenuLayout;
import com.sony.imaging.app.base.menu.layout.AFLocalPositionMenuLayout;
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
import com.sony.imaging.app.base.menu.layout.MovieModeMenuLayout;
import com.sony.imaging.app.base.menu.layout.PageMenuLayout;
import com.sony.imaging.app.base.menu.layout.SceneSelectionMenuLayout;
import com.sony.imaging.app.base.menu.layout.SetMenuLayout;
import com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout;
import com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayoutHiddenEE;
import com.sony.imaging.app.base.menu.layout.UnknownItemMenuLayout;
import com.sony.imaging.app.base.menu.layout.WhiteBalanceAdjustmentMenuLayout;
import com.sony.imaging.app.base.menu.layout.WhiteBalanceMenuLayout;
import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.base.playback.base.IndexStateBase;
import com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase;
import com.sony.imaging.app.base.playback.base.PlaySubApp;
import com.sony.imaging.app.base.playback.base.SingleStateBase;
import com.sony.imaging.app.base.playback.browser.Browser;
import com.sony.imaging.app.base.playback.layout.DeleteSingleProcessingLayout;
import com.sony.imaging.app.base.playback.layout.PseudoRecNoFileLayout;
import com.sony.imaging.app.base.playback.pseudorec.PseudoRec;
import com.sony.imaging.app.base.playback.pseudorec.zoom.PseudoRecZoom;
import com.sony.imaging.app.base.playback.pseudorec.zoom.PseudoRecZoomState;
import com.sony.imaging.app.base.shooting.AutoReviewState;
import com.sony.imaging.app.base.shooting.CaptureState;
import com.sony.imaging.app.base.shooting.CustomWhiteBalanceCaptureState;
import com.sony.imaging.app.base.shooting.CustomWhiteBalanceEEState;
import com.sony.imaging.app.base.shooting.CustomWhiteBalanceExposureState;
import com.sony.imaging.app.base.shooting.FocusAdjustmentState;
import com.sony.imaging.app.base.shooting.MfAssistState;
import com.sony.imaging.app.base.shooting.S1OffEEState;
import com.sony.imaging.app.base.shooting.S1OnEEState;
import com.sony.imaging.app.base.shooting.StateBase;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.layout.AutoReviewLayout;
import com.sony.imaging.app.base.shooting.layout.CustomWhiteBalanceConfLayout;
import com.sony.imaging.app.base.shooting.layout.CustomWhiteBalanceEELayout;
import com.sony.imaging.app.base.shooting.layout.FocusAdjustmentLayout;
import com.sony.imaging.app.base.shooting.layout.FocusLayout;
import com.sony.imaging.app.base.shooting.layout.GuideLayout;
import com.sony.imaging.app.base.shooting.layout.ProgressLayout;
import com.sony.imaging.app.base.shooting.layout.ShootingLayout;
import com.sony.imaging.app.base.shooting.movie.MovieCaptureState;
import com.sony.imaging.app.base.shooting.movie.MovieRecStandbyState;
import com.sony.imaging.app.base.shooting.movie.MovieRecState;
import com.sony.imaging.app.base.shooting.movie.MovieSaveState;
import com.sony.imaging.app.base.shooting.movie.layout.MovieSaveLayout;
import com.sony.imaging.app.base.shooting.movie.trigger.MovieCaptureStateKeyHandler;
import com.sony.imaging.app.base.shooting.movie.trigger.MovieRecStandbyStateKeyHandler;
import com.sony.imaging.app.base.shooting.movie.trigger.MovieRecStateKeyHandler;
import com.sony.imaging.app.base.shooting.movie.trigger.MovieSaveStateKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.AutoReviewStateKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.CaptureStateKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.CustomWhiteBalanceCaptureKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.CustomWhiteBalanceEEKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.CustomWhiteBalanceExposureKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.DevelopmentStateKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.EEStateKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.FocusAdjustmentKeyHandler;
import com.sony.imaging.app.fw.Factory;
import com.sony.imaging.app.fw.LayoutFactory;
import com.sony.imaging.app.fw.StateFactory;
import com.sony.imaging.app.timelapse.angleshift.browser.AngleShiftEditSingle;
import com.sony.imaging.app.timelapse.angleshift.layout.AngleShiftBootLogoLayout;
import com.sony.imaging.app.timelapse.angleshift.layout.AngleShiftConfirmExitLayout;
import com.sony.imaging.app.timelapse.angleshift.layout.AngleShiftConfirmSavingLayout;
import com.sony.imaging.app.timelapse.angleshift.layout.AngleShiftEditLayout;
import com.sony.imaging.app.timelapse.angleshift.layout.AngleShiftFrameCroppingLayout;
import com.sony.imaging.app.timelapse.angleshift.layout.AngleShiftFrameSelectionLayout;
import com.sony.imaging.app.timelapse.angleshift.layout.AngleShiftFrameSettingLayout;
import com.sony.imaging.app.timelapse.angleshift.layout.AngleShiftPreviewLayout;
import com.sony.imaging.app.timelapse.angleshift.layout.AngleShiftProgressLayout;
import com.sony.imaging.app.timelapse.angleshift.layout.AngleShiftSavingDoneLayout;
import com.sony.imaging.app.timelapse.angleshift.layout.AngleShiftThemeOptionLayout;
import com.sony.imaging.app.timelapse.angleshift.layout.AngleShiftThemeSelectionLayout;
import com.sony.imaging.app.timelapse.angleshift.state.AngleShiftMenuState;
import com.sony.imaging.app.timelapse.caution.TimelapseCautionLayout;
import com.sony.imaging.app.timelapse.menu.base.layout.TLExposureModeMenuLayout;
import com.sony.imaging.app.timelapse.menu.base.layout.TLFn15LayerMenuLayout;
import com.sony.imaging.app.timelapse.menu.base.layout.TLFnMenuLayout;
import com.sony.imaging.app.timelapse.menu.base.layout.TLSetMenuLayout;
import com.sony.imaging.app.timelapse.menu.base.layout.TLSpecialScreenMenuLayout;
import com.sony.imaging.app.timelapse.menu.base.layout.TimelapseCreativeStyleMenuLayout;
import com.sony.imaging.app.timelapse.menu.base.layout.TimelapsePageMenuLayout;
import com.sony.imaging.app.timelapse.menu.layout.LastBastionMenuLayout;
import com.sony.imaging.app.timelapse.menu.layout.TLCustomThemeOptionLayout;
import com.sony.imaging.app.timelapse.menu.layout.TLThemeOptionLayout;
import com.sony.imaging.app.timelapse.menu.layout.TLThemeSelectionLayout;
import com.sony.imaging.app.timelapse.playback.TimeLapsePlayRootContainer;
import com.sony.imaging.app.timelapse.playback.TimeLapseTransitionToShooting;
import com.sony.imaging.app.timelapse.playback.browser.TLBrowserIndex;
import com.sony.imaging.app.timelapse.playback.browser.TLBrowserSingle;
import com.sony.imaging.app.timelapse.playback.browser.TLPseudoRecSingle;
import com.sony.imaging.app.timelapse.playback.layout.DeleteMultipleConfirmLayout;
import com.sony.imaging.app.timelapse.playback.layout.DeleteMultipleProcessingLayout;
import com.sony.imaging.app.timelapse.playback.layout.ListViewLayout;
import com.sony.imaging.app.timelapse.playback.layout.PlaybackLayout;
import com.sony.imaging.app.timelapse.playback.layout.RecovertyDBLayout;
import com.sony.imaging.app.timelapse.playback.layout.TLPseudoRecSingleLayout;
import com.sony.imaging.app.timelapse.playback.layout.TLPseudoRecZoomLayout;
import com.sony.imaging.app.timelapse.shooting.layout.AutoReviewBlackLayout;
import com.sony.imaging.app.timelapse.shooting.layout.TimeLapseMfAssistLayout;
import com.sony.imaging.app.timelapse.shooting.layout.TimeLapseS1OffLayout;
import com.sony.imaging.app.timelapse.shooting.layout.TimeLapseStableLayout;
import com.sony.imaging.app.timelapse.shooting.layout.TwoSecondTimerLayout;
import com.sony.imaging.app.timelapse.shooting.state.EachForceSettingState;
import com.sony.imaging.app.timelapse.shooting.state.TLCustomWhiteBalanceControllerState;
import com.sony.imaging.app.timelapse.shooting.state.TLExposureModeCheckState;
import com.sony.imaging.app.timelapse.shooting.state.TimeLapseEEState;
import com.sony.imaging.app.timelapse.shooting.state.TimeLapseNormalCaptureState;
import com.sony.imaging.app.timelapse.shooting.state.TimelapseDevelopmentState;
import com.sony.imaging.app.timelapse.shooting.state.TimelapseShootingMenuState;
import com.sony.imaging.app.timelapse.shooting.state.TimelapseShootingState;
import com.sony.imaging.app.timelapse.shooting.state.keyhandler.TLMfAssistKeyHandler;
import com.sony.imaging.app.timelapse.shooting.state.keyhandler.TLShootingStateKeyHandler;
import com.sony.imaging.app.timelapse.shooting.state.keyhandler.TimeLapseCaptureStateKeyHandler;
import com.sony.imaging.app.timelapse.shooting.state.keyhandler.TimeLapseS1OffKeyHandler;
import com.sony.imaging.app.timelapse.shooting.state.keyhandler.TimelapseS1OnEEStateKeyHandler;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class TLFactory extends Factory {
    @Override // com.sony.imaging.app.fw.Factory
    protected Class<? extends StateFactory> getTop() {
        return RootStateFactory.class;
    }

    /* loaded from: classes.dex */
    public static class RootStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(BaseApp.APP_SHOOTING, TimelapseShootingState.class);
            TLFactory.add(TimelapseShootingState.class, ShootingStateFactory.class, ShootingLayoutFactory.class, TLShootingStateKeyHandler.class);
            add(BaseApp.APP_PLAY, TimeLapsePlayRootContainer.class);
            TLFactory.add(TimeLapsePlayRootContainer.class, PlaybackStateFactory.class, null, null);
            add(BaseApp.CAUTION, CautionFragment.class);
            TLFactory.add(CautionFragment.class, CautionStateFactory.class, null, null);
            add(BaseApp.EXIT_SCREEN, ExitScreenState.class);
            TLFactory.add(ExitScreenState.class, null, ExitScreenLayoutFactory.class, null);
            add(BaseApp.FORCE_EXIT_SCREEN, ForceExitScreenState.class);
            TLFactory.add(ForceExitScreenState.class, null, ForceExitScreenLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class CautionStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("CautionDisplayState", CautionDisplayState.class);
            TLFactory.add(CautionDisplayState.class, null, CautionLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class ShootingStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("FORCESETTING", EachForceSettingState.class);
            TLFactory.add(EachForceSettingState.class, null, null, null);
            add("ExposureModeCheck", TLExposureModeCheckState.class);
            TLFactory.add(TLExposureModeCheckState.class, null, null, null);
            add("EE", TimeLapseEEState.class);
            TLFactory.add(TimeLapseEEState.class, EEStateFactory.class, null, EEStateKeyHandler.class);
            add("Capture", CaptureState.class);
            TLFactory.add(CaptureState.class, CaptureStateFactory.class, null, CaptureStateKeyHandler.class);
            add("Development", TimelapseDevelopmentState.class);
            TLFactory.add(TimelapseDevelopmentState.class, DevelopmentStateFactory.class, DevelopmentLayoutFactory.class, DevelopmentStateKeyHandler.class);
            add(AutoReviewState.STATE_NAME, AutoReviewState.class);
            TLFactory.add(AutoReviewState.class, null, AutoReviewLayoutFactory.class, AutoReviewStateKeyHandler.class);
            add("CustomWhiteBalance", TLCustomWhiteBalanceControllerState.class);
            TLFactory.add(TLCustomWhiteBalanceControllerState.class, CustomWhiteBalanceControllerStateFactory.class, null, null);
            if (Environment.isMovieAPISupported()) {
                add("MovieCapture", MovieCaptureState.class);
                TLFactory.add(MovieCaptureState.class, null, MovieLayoutFactory.class, MovieCaptureStateKeyHandler.class);
                add("MovieRec", MovieRecState.class);
                TLFactory.add(MovieRecState.class, MovieRecStateFactory.class, MovieLayoutFactory.class, MovieRecStateKeyHandler.class);
                add("MovieSave", MovieSaveState.class);
                TLFactory.add(MovieSaveState.class, null, MovieSaveLayoutFactory.class, MovieSaveStateKeyHandler.class);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class MovieRecStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("Menu", TimelapseShootingMenuState.class);
            TLFactory.add(TimelapseShootingMenuState.class, null, MenuLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class MovieLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(StateBase.DEFAULT_LAYOUT, TimeLapseStableLayout.class);
            add(DigitalZoomSettingLayout.MENU_ID, DigitalZoomSettingLayout.class);
            add(StateBase.FOCUS_LAYOUT, FocusLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class MovieSaveLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("MovieSaveLayout", MovieSaveLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class EEStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(S1OffEEState.STATE_NAME, S1OffEEState.class);
            TLFactory.add(S1OffEEState.class, null, S1OffEELayoutFactory.class, TimeLapseS1OffKeyHandler.class);
            add("S1OnEE", S1OnEEState.class);
            TLFactory.add(S1OnEEState.class, null, S1OnEELayoutFactory.class, TimelapseS1OnEEStateKeyHandler.class);
            add("Menu", TimelapseShootingMenuState.class);
            add("FocusAdjustment", FocusAdjustmentState.class);
            TLFactory.add(FocusAdjustmentState.class, null, FocusAdjustmentFactory.class, FocusAdjustmentKeyHandler.class);
            TLFactory.add(TimelapseShootingMenuState.class, null, MenuLayoutFactory.class, null);
            add("MovieRecStandby", MovieRecStandbyState.class);
            TLFactory.add(MovieRecStandbyState.class, null, MovieLayoutFactory.class, MovieRecStandbyStateKeyHandler.class);
            add("MfAssist", MfAssistState.class);
            TLFactory.add(MfAssistState.class, null, MfAssistLayoutFactory.class, TLMfAssistKeyHandler.class);
        }
    }

    /* loaded from: classes.dex */
    public static class MfAssistLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(StateBase.DEFAULT_LAYOUT, TimeLapseMfAssistLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class CaptureStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("NormalCapture", TimeLapseNormalCaptureState.class);
            TLFactory.add(TimeLapseNormalCaptureState.class, null, NormalCaptureLayoutFactory.class, TimeLapseCaptureStateKeyHandler.class);
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
            TLFactory.add(CustomWhiteBalanceEEState.class, null, CWBEELayoutFactory.class, CustomWhiteBalanceEEKeyHandler.class);
            add("CWBCapture", CustomWhiteBalanceCaptureState.class);
            TLFactory.add(CustomWhiteBalanceCaptureState.class, null, null, CustomWhiteBalanceCaptureKeyHandler.class);
            add("CWBExposure", CustomWhiteBalanceExposureState.class);
            TLFactory.add(CustomWhiteBalanceExposureState.class, null, CWBExposureLayoutFactory.class, CustomWhiteBalanceExposureKeyHandler.class);
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
            add("cautionLayout", TimelapseCautionLayout.class);
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
    public static class S1OffEELayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(StateBase.DEFAULT_LAYOUT, TimeLapseStableLayout.class);
            add(StateBase.S1OFF_LAYOUT, TimeLapseS1OffLayout.class);
            add(StateBase.FOCUS_LAYOUT, FocusLayout.class);
            add(StateBase.GUIDE_LAYOUT, GuideLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class S1OnEELayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(StateBase.DEFAULT_LAYOUT, TimeLapseStableLayout.class);
            add(StateBase.FOCUS_LAYOUT, FocusLayout.class);
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
    public static class NormalCaptureLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(StateBase.DEFAULT_LAYOUT, TimeLapseStableLayout.class);
            add(StateBase.FOCUS_LAYOUT, FocusLayout.class);
            add(TimeLapseNormalCaptureState.TWOSECONDTIMERLAYOUT, TwoSecondTimerLayout.class);
            add(AutoReviewBlackLayout.TAG, AutoReviewBlackLayout.class);
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
            TLFactory.add(Browser.class, BrowserFactory.class, null, null);
            add(PlayRootContainer.ID_PSEUDOREC, PseudoRec.class);
            TLFactory.add(PseudoRec.class, PseudoRecFactory.class, null, null);
            add(PlayRootContainer.ID_PSEUDOREC_ZOOM, PseudoRecZoom.class);
            TLFactory.add(PseudoRecZoom.class, PseudoRecZoomFactory.class, null, null);
            add(PlayRootContainer.ID_TRANSITING_TO_SHOOTING, TimeLapseTransitionToShooting.class);
        }
    }

    /* loaded from: classes.dex */
    public static class BrowserSingleLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("NORMAL_PB", PlaybackLayout.class);
            TLFactory.add(PlaybackLayout.class, null, SinglePBLayoutFactory.class, null);
            add("NO_FILE", PlaybackLayout.class);
            add(SingleStateBase.ID_BUFFER_PLAYBACK_LAYOUT, PlaybackLayout.class);
            add(SingleStateBase.ID_NO_BUFFER_LAYOUT, PlaybackLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class BrowserIndexLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("NORMAL_PB", ListViewLayout.class);
            add("NO_FILE", ListViewLayout.class);
            add(IndexStateBase.ID_DATA_UNREADY_LAYOUT, ListViewLayout.class);
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
    public static class MenuLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("ID_WHITEBALANCEMENULAYOUT", WhiteBalanceMenuLayout.class);
            TLFactory.add(WhiteBalanceMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(WhiteBalanceAdjustmentMenuLayout.MENU_ID, WhiteBalanceAdjustmentMenuLayout.class);
            TLFactory.add(WhiteBalanceAdjustmentMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_FLASHCOMPENSATIONMENULAYOUT", FlashCompensationMenuLayout.class);
            TLFactory.add(FlashCompensationMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_AFFLEXIBLEPOSITIONMENULAYOUT", AFFlexiblePositionMenuLayout.class);
            TLFactory.add(AFFlexiblePositionMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_CREATIVESTYLEMENULAYOUT", TimelapseCreativeStyleMenuLayout.class);
            TLFactory.add(TimelapseCreativeStyleMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_AFLOCALPOSITIONMENULAYOUT", AFLocalPositionMenuLayout.class);
            TLFactory.add(AFLocalPositionMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            if (ExposureModeController.isUiMovieMainFeature()) {
                add(ExposureModeMenuLayout.MENU_ID, ExposureModeMenuMovieFeatureLayout.class);
                TLFactory.add(ExposureModeMenuMovieFeatureLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            } else {
                add(ExposureModeMenuLayout.MENU_ID, TLExposureModeMenuLayout.class);
                TLFactory.add(TLExposureModeMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            }
            add("ID_SCENESELECTIONMENULAYOUT", SceneSelectionMenuLayout.class);
            TLFactory.add(SceneSelectionMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_EXPOSURECOMPENSATIONMENULAYOUT", ExposureCompensationMenuLayout.class);
            TLFactory.add(ExposureCompensationMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_MOVIEMODEMENULAYOUT", MovieModeMenuLayout.class);
            TLFactory.add(MovieModeMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(UnknownItemMenuLayout.MENU_ID, UnknownItemMenuLayout.class);
            TLFactory.add(UnknownItemMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("LastBastionLayout", LastBastionMenuLayout.class);
            TLFactory.add(LastBastionMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(FnMenuLayout.MENU_ID, TLFnMenuLayout.class);
            TLFactory.add(TLFnMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(Fn15LayerMenuLayout.MENU_ID, TLFn15LayerMenuLayout.class);
            TLFactory.add(TLFn15LayerMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(SpecialScreenMenuLayout.MENU_ID, TLSpecialScreenMenuLayout.class);
            TLFactory.add(TLSpecialScreenMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(ISOSpecialScreenMenuLayout.MENU_ID, ISOSpecialScreenMenuLayout.class);
            TLFactory.add(ISOSpecialScreenMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(DigitalZoomSettingLayout.MENU_ID, DigitalZoomSettingLayout.class);
            TLFactory.add(DigitalZoomSettingLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(FocusModeSpecialScreenMenuLayout.MENU_ID, FocusModeSpecialScreenMenuLayout.class);
            TLFactory.add(FocusModeSpecialScreenMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(Fn15LayerExposureCompensationLayout.MENU_ID, Fn15LayerExposureCompensationLayout.class);
            TLFactory.add(Fn15LayerExposureCompensationLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(Fn15LayerFlashCompensationLayout.MENU_ID, Fn15LayerFlashCompensationLayout.class);
            TLFactory.add(Fn15LayerFlashCompensationLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(Fn15LayerCreativeStyleLayout.MENU_ID, Fn15LayerCreativeStyleLayout.class);
            TLFactory.add(Fn15LayerCreativeStyleLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(Fn15LayerWhiteBalanceLayout.MENU_ID, Fn15LayerWhiteBalanceLayout.class);
            TLFactory.add(Fn15LayerWhiteBalanceLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(ISOFn15LayerMenuLayout.MENU_ID, ISOFn15LayerMenuLayout.class);
            TLFactory.add(ISOFn15LayerMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(PageMenuLayout.MENU_ID, TimelapsePageMenuLayout.class);
            TLFactory.add(TimelapsePageMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(SetMenuLayout.MENU_ID, TLSetMenuLayout.class);
            TLFactory.add(TLSetMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(SpecialScreenMenuLayoutHiddenEE.MENU_ID, SpecialScreenMenuLayoutHiddenEE.class);
            TLFactory.add(SpecialScreenMenuLayoutHiddenEE.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_TLTHEMESELECTIONLAYOUT", TLThemeSelectionLayout.class);
            TLFactory.add(TLThemeSelectionLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_TLTHEMEOPTIONLAYOUT", TLThemeOptionLayout.class);
            TLFactory.add(TLThemeOptionLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_TLCUSTOMTHEMEOPTIONLAYOUT", TLCustomThemeOptionLayout.class);
            TLFactory.add(TLCustomThemeOptionLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_ANGLESHIFTBOOTLOGOLAYOUT", AngleShiftBootLogoLayout.class);
            TLFactory.add(AngleShiftBootLogoLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_ANGLESHIFTCONFIRMEXITLAYOUT", AngleShiftConfirmExitLayout.class);
            TLFactory.add(AngleShiftConfirmExitLayout.class, null, null, null);
            add("ID_ANGLESHIFTTHEMESELECTIONLAYOUT", AngleShiftThemeSelectionLayout.class);
            TLFactory.add(AngleShiftThemeSelectionLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_ANGLESHIFTTHEMEOPTIONLAYOUT", AngleShiftThemeOptionLayout.class);
            TLFactory.add(AngleShiftThemeOptionLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_ANGLESHIFTFRAMESELECTIONLAYOUT", AngleShiftFrameSelectionLayout.class);
            TLFactory.add(AngleShiftFrameSelectionLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_ANGLESHIFTFRAMESETTINGLAYOUT", AngleShiftFrameSettingLayout.class);
            TLFactory.add(AngleShiftFrameSettingLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_ANGLESHIFTFRAMECROPPINGLAYOUT", AngleShiftFrameCroppingLayout.class);
            TLFactory.add(AngleShiftFrameCroppingLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
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
            add("ID_CREATIVESTYLEMENULAYOUT", TimelapseCreativeStyleMenuLayout.class);
            add("ID_AFLOCALPOSITIONMENULAYOUT", AFLocalPositionMenuLayout.class);
            if (ExposureModeController.isUiMovieMainFeature()) {
                add(ExposureModeMenuLayout.MENU_ID, ExposureModeMenuMovieFeatureLayout.class);
            } else {
                add(ExposureModeMenuLayout.MENU_ID, TLExposureModeMenuLayout.class);
            }
            add("ID_SCENESELECTIONMENULAYOUT", SceneSelectionMenuLayout.class);
            add("ID_EXPOSURECOMPENSATIONMENULAYOUT", ExposureCompensationMenuLayout.class);
            add("ID_MOVIEMODEMENULAYOUT", MovieModeMenuLayout.class);
            add(UnknownItemMenuLayout.MENU_ID, UnknownItemMenuLayout.class);
            add("LastBastionLayout", LastBastionMenuLayout.class);
            add(FnMenuLayout.MENU_ID, TLFnMenuLayout.class);
            add(Fn15LayerMenuLayout.MENU_ID, TLFn15LayerMenuLayout.class);
            add(SpecialScreenMenuLayout.MENU_ID, TLSpecialScreenMenuLayout.class);
            add(ISOSpecialScreenMenuLayout.MENU_ID, ISOSpecialScreenMenuLayout.class);
            add(DigitalZoomSettingLayout.MENU_ID, DigitalZoomSettingLayout.class);
            add(FocusModeSpecialScreenMenuLayout.MENU_ID, FocusModeSpecialScreenMenuLayout.class);
            add(Fn15LayerExposureCompensationLayout.MENU_ID, Fn15LayerExposureCompensationLayout.class);
            add(Fn15LayerFlashCompensationLayout.MENU_ID, Fn15LayerFlashCompensationLayout.class);
            add(Fn15LayerCreativeStyleLayout.MENU_ID, Fn15LayerCreativeStyleLayout.class);
            add(Fn15LayerWhiteBalanceLayout.MENU_ID, Fn15LayerWhiteBalanceLayout.class);
            add(ISOFn15LayerMenuLayout.MENU_ID, ISOFn15LayerMenuLayout.class);
            add(PageMenuLayout.MENU_ID, TimelapsePageMenuLayout.class);
            add(SetMenuLayout.MENU_ID, TLSetMenuLayout.class);
            add(SpecialScreenMenuLayoutHiddenEE.MENU_ID, SpecialScreenMenuLayoutHiddenEE.class);
            add("ID_TLTHEMESELECTIONLAYOUT", TLThemeSelectionLayout.class);
            add("ID_TLTHEMEOPTIONLAYOUT", TLThemeOptionLayout.class);
            add("ID_TLCUSTOMTHEMEOPTIONLAYOUT", TLCustomThemeOptionLayout.class);
            add("ID_ANGLESHIFTBOOTLOGOLAYOUT", AngleShiftBootLogoLayout.class);
            add("ID_ANGLESHIFTCONFIRMEXITLAYOUT", AngleShiftConfirmExitLayout.class);
            add("ID_ANGLESHIFTTHEMESELECTIONLAYOUT", AngleShiftThemeSelectionLayout.class);
            add("ID_ANGLESHIFTTHEMEOPTIONLAYOUT", AngleShiftThemeOptionLayout.class);
            add("ID_ANGLESHIFTFRAMESELECTIONLAYOUT", AngleShiftFrameSelectionLayout.class);
            add("ID_ANGLESHIFTFRAMESETTINGLAYOUT", AngleShiftFrameSettingLayout.class);
            add("ID_ANGLESHIFTFRAMECROPPINGLAYOUT", AngleShiftFrameCroppingLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class PseudoRecFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlaySubApp.ID_SINGLE_PB, TLPseudoRecSingle.class);
            TLFactory.add(TLPseudoRecSingle.class, null, PseudoRecSingleLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class PseudoRecZoomFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlaySubApp.ID_DEFAULT_STATE, PseudoRecZoomState.class);
            TLFactory.add(PseudoRecZoomState.class, null, PseudoRecZoomStateLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class PseudoRecZoomStateLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(SingleStateBase.ID_BUFFER_PLAYBACK_LAYOUT, TLPseudoRecZoomLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class PseudoRecSingleLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(SingleStateBase.ID_BUFFER_PLAYBACK_LAYOUT, TLPseudoRecSingleLayout.class);
            add(SingleStateBase.ID_NO_BUFFER_LAYOUT, PseudoRecNoFileLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class BrowserFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlaySubApp.ID_SINGLE_PB, TLBrowserSingle.class);
            TLFactory.add(TLBrowserSingle.class, null, BrowserSingleLayoutFactory.class, null);
            add(PlaySubApp.ID_INDEX_PB, TLBrowserIndex.class);
            TLFactory.add(TLBrowserIndex.class, null, BrowserIndexLayoutFactory.class, null);
            add("Edit", AngleShiftEditSingle.class);
            TLFactory.add(AngleShiftEditSingle.class, BrowserCommonFactory.class, EditSingleLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class EditSingleLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("NORMAL_PB", AngleShiftEditLayout.class);
            TLFactory.add(AngleShiftEditLayout.class, null, EditSinglePBLayoutFactory.class, null);
            add("ID_ANGLESHIFTPREVIEWLAYOUT", AngleShiftPreviewLayout.class);
            add("ID_ANGLESHIFTPROGRESSLAYOUT", AngleShiftProgressLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class BrowserCommonFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("PlayBackMenu", AngleShiftMenuState.class);
            TLFactory.add(AngleShiftMenuState.class, null, AngleShiftMenuLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class AngleShiftMenuLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("ID_ANGLESHIFTBOOTLOGOLAYOUT", AngleShiftBootLogoLayout.class);
            TLFactory.add(AngleShiftBootLogoLayout.class, null, AngleShiftOneAnotherMenuLayoutFactory.class, null);
            add("ID_ANGLESHIFTCONFIRMEXITLAYOUT", AngleShiftConfirmExitLayout.class);
            TLFactory.add(AngleShiftConfirmExitLayout.class, null, null, null);
            add("ID_ANGLESHIFTTHEMESELECTIONLAYOUT", AngleShiftThemeSelectionLayout.class);
            TLFactory.add(AngleShiftThemeSelectionLayout.class, null, AngleShiftOneAnotherMenuLayoutFactory.class, null);
            add("ID_ANGLESHIFTTHEMEOPTIONLAYOUT", AngleShiftThemeOptionLayout.class);
            TLFactory.add(AngleShiftThemeOptionLayout.class, null, AngleShiftOneAnotherMenuLayoutFactory.class, null);
            add("ID_ANGLESHIFTFRAMESELECTIONLAYOUT", AngleShiftFrameSelectionLayout.class);
            TLFactory.add(AngleShiftFrameSelectionLayout.class, null, AngleShiftOneAnotherMenuLayoutFactory.class, null);
            add("ID_ANGLESHIFTFRAMESETTINGLAYOUT", AngleShiftFrameSettingLayout.class);
            TLFactory.add(AngleShiftFrameSettingLayout.class, null, AngleShiftOneAnotherMenuLayoutFactory.class, null);
            add("ID_ANGLESHIFTFRAMECROPPINGLAYOUT", AngleShiftFrameCroppingLayout.class);
            TLFactory.add(AngleShiftFrameCroppingLayout.class, null, AngleShiftOneAnotherMenuLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class AngleShiftOneAnotherMenuLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("ID_ANGLESHIFTBOOTLOGOLAYOUT", AngleShiftBootLogoLayout.class);
            add("ID_ANGLESHIFTCONFIRMEXITLAYOUT", AngleShiftConfirmExitLayout.class);
            add("ID_ANGLESHIFTTHEMESELECTIONLAYOUT", AngleShiftThemeSelectionLayout.class);
            add("ID_ANGLESHIFTTHEMEOPTIONLAYOUT", AngleShiftThemeOptionLayout.class);
            add("ID_ANGLESHIFTFRAMESELECTIONLAYOUT", AngleShiftFrameSelectionLayout.class);
            add("ID_ANGLESHIFTFRAMESETTINGLAYOUT", AngleShiftFrameSettingLayout.class);
            add("ID_ANGLESHIFTFRAMECROPPINGLAYOUT", AngleShiftFrameCroppingLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class EditSinglePBLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("ID_ANGLESHIFTCONFIRMSAVINGLAYOUT", AngleShiftConfirmSavingLayout.class);
            TLFactory.add(AngleShiftConfirmSavingLayout.class, null, null, null);
            add("ID_ANGLESHIFTPREVIEWLAYOUT", AngleShiftPreviewLayout.class);
            TLFactory.add(AngleShiftPreviewLayout.class, null, PreviewLayoutFactory.class, null);
            add("ID_ANGLESHIFTPROGRESSLAYOUT", AngleShiftProgressLayout.class);
            TLFactory.add(AngleShiftProgressLayout.class, null, ProgressLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class PreviewLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("NORMAL_PB", AngleShiftEditLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class ProgressLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("NORMAL_PB", AngleShiftEditLayout.class);
            add("ID_ANGLESHIFTSAVINGDONWLAYOUT", AngleShiftSavingDoneLayout.class);
            TLFactory.add(AngleShiftSavingDoneLayout.class, null, null, null);
        }
    }

    /* loaded from: classes.dex */
    public static class SinglePBLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(TimeLapsePlayRootContainer.ID_DELETE_MULTIPLE_LAYOUT, DeleteMultipleConfirmLayout.class);
            TLFactory.add(DeleteMultipleConfirmLayout.class, null, DeleteMultipleConfirmLayoutFactory.class, null);
            add("ID_RECOVERYDB_LAYOUT", RecovertyDBLayout.class);
            TLFactory.add(RecovertyDBLayout.class, null, null, null);
        }
    }

    /* loaded from: classes.dex */
    public static class DeleteMultipleConfirmLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlayStateWithLayoutBase.ID_DEFAULT_LAYOUT, DeleteMultipleProcessingLayout.class);
        }
    }
}
