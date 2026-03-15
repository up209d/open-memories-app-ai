package com.sony.imaging.app.startrails;

import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.caution.CautionDisplayState;
import com.sony.imaging.app.base.caution.CautionFragment;
import com.sony.imaging.app.base.common.ExitScreenState;
import com.sony.imaging.app.base.common.ForceExitScreenState;
import com.sony.imaging.app.base.common.layout.ForceExitScreenLayout;
import com.sony.imaging.app.base.menu.layout.AFFlexiblePositionMenuLayout;
import com.sony.imaging.app.base.menu.layout.AFLocalPositionMenuLayout;
import com.sony.imaging.app.base.menu.layout.DigitalZoomSettingLayout;
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
import com.sony.imaging.app.base.playback.TransitionToShooting;
import com.sony.imaging.app.base.playback.base.IndexStateBase;
import com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase;
import com.sony.imaging.app.base.playback.base.PlaySubApp;
import com.sony.imaging.app.base.playback.base.SingleStateBase;
import com.sony.imaging.app.base.playback.browser.Browser;
import com.sony.imaging.app.base.playback.browser.BrowserIndex;
import com.sony.imaging.app.base.playback.browser.BrowserSingle;
import com.sony.imaging.app.base.playback.layout.DeleteSingleProcessingLayout;
import com.sony.imaging.app.base.playback.layout.PseudoRecNoFileLayout;
import com.sony.imaging.app.base.playback.pseudorec.PseudoRec;
import com.sony.imaging.app.base.playback.pseudorec.zoom.PseudoRecZoom;
import com.sony.imaging.app.base.playback.pseudorec.zoom.PseudoRecZoomState;
import com.sony.imaging.app.base.shooting.AutoReviewForLimitedContShootingState;
import com.sony.imaging.app.base.shooting.AutoReviewState;
import com.sony.imaging.app.base.shooting.CaptureState;
import com.sony.imaging.app.base.shooting.CustomWhiteBalanceCaptureState;
import com.sony.imaging.app.base.shooting.CustomWhiteBalanceEEState;
import com.sony.imaging.app.base.shooting.CustomWhiteBalanceExposureState;
import com.sony.imaging.app.base.shooting.FocusAdjustmentState;
import com.sony.imaging.app.base.shooting.MovieMenuState;
import com.sony.imaging.app.base.shooting.S1OffEEState;
import com.sony.imaging.app.base.shooting.SelfTimerCaptureState;
import com.sony.imaging.app.base.shooting.StateBase;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.layout.AutoReviewForLimitedContShootingLayout;
import com.sony.imaging.app.base.shooting.layout.AutoReviewLayout;
import com.sony.imaging.app.base.shooting.layout.CustomWhiteBalanceConfLayout;
import com.sony.imaging.app.base.shooting.layout.CustomWhiteBalanceEELayout;
import com.sony.imaging.app.base.shooting.layout.FocusAdjustmentLayout;
import com.sony.imaging.app.base.shooting.layout.FocusLayout;
import com.sony.imaging.app.base.shooting.layout.GuideLayout;
import com.sony.imaging.app.base.shooting.layout.ProgressLayout;
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
import com.sony.imaging.app.base.shooting.trigger.FocusAdjustmentKeyHandler;
import com.sony.imaging.app.fw.Factory;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.fw.LayoutFactory;
import com.sony.imaging.app.fw.StateFactory;
import com.sony.imaging.app.startrails.common.caution.STCautionLayout;
import com.sony.imaging.app.startrails.exit.layout.STExitScreenLayout;
import com.sony.imaging.app.startrails.menu.base.layout.LastBastionMenuLayout;
import com.sony.imaging.app.startrails.menu.base.layout.STCreativeStyleMenuLayout;
import com.sony.imaging.app.startrails.menu.base.layout.STExposureCompensationMenuLayout;
import com.sony.imaging.app.startrails.menu.base.layout.STExposureModeMenuLayout;
import com.sony.imaging.app.startrails.menu.base.layout.STPageMenuLayout;
import com.sony.imaging.app.startrails.menu.base.layout.STWhiteBalanceAdjustmentMenuLayout;
import com.sony.imaging.app.startrails.menu.layout.ParameterSettingMenuLayout;
import com.sony.imaging.app.startrails.menu.layout.ShootingTipMenuLayout;
import com.sony.imaging.app.startrails.menu.layout.ThemeSelectMenuLayout;
import com.sony.imaging.app.startrails.playback.STPlayRootContainer;
import com.sony.imaging.app.startrails.playback.browser.STPseudoRecSingle;
import com.sony.imaging.app.startrails.playback.layout.DeleteMultipleConfirmLayout;
import com.sony.imaging.app.startrails.playback.layout.DeleteMultipleProcessingLayout;
import com.sony.imaging.app.startrails.playback.layout.ListViewLayout;
import com.sony.imaging.app.startrails.playback.layout.PlaybackLayout;
import com.sony.imaging.app.startrails.playback.layout.STPseudoRecSingleLayout;
import com.sony.imaging.app.startrails.playback.layout.STPseudoRecZoomLayout;
import com.sony.imaging.app.startrails.shooting.keyhandler.STCaptureStateKeyHandler;
import com.sony.imaging.app.startrails.shooting.keyhandler.STEEStateKeyHandler;
import com.sony.imaging.app.startrails.shooting.keyhandler.STMFAssistStateKeyHandler;
import com.sony.imaging.app.startrails.shooting.keyhandler.STS1OffKeyHandler;
import com.sony.imaging.app.startrails.shooting.keyhandler.STS1OnEEStateKeyHandler;
import com.sony.imaging.app.startrails.shooting.keyhandler.STSelfTimerCaptureStateKeyHandler;
import com.sony.imaging.app.startrails.shooting.keyhandler.STShootingStateKeyHandler;
import com.sony.imaging.app.startrails.shooting.layout.AutoReviewBlackLayout;
import com.sony.imaging.app.startrails.shooting.layout.IntroductoryLayout;
import com.sony.imaging.app.startrails.shooting.layout.STCaptureStableLayout;
import com.sony.imaging.app.startrails.shooting.layout.STMFassistLayout;
import com.sony.imaging.app.startrails.shooting.layout.STS1OffLayout;
import com.sony.imaging.app.startrails.shooting.layout.STStableLayout;
import com.sony.imaging.app.startrails.shooting.layout.SelfTimerDisplayLayout;
import com.sony.imaging.app.startrails.shooting.state.LongExposureNRCheckState;
import com.sony.imaging.app.startrails.shooting.state.MFModeCheckState;
import com.sony.imaging.app.startrails.shooting.state.STCustomWhiteBalanceControllerState;
import com.sony.imaging.app.startrails.shooting.state.STDevelopmentState;
import com.sony.imaging.app.startrails.shooting.state.STEEState;
import com.sony.imaging.app.startrails.shooting.state.STExposureModeCheckState;
import com.sony.imaging.app.startrails.shooting.state.STForceSettingState;
import com.sony.imaging.app.startrails.shooting.state.STMFModeCheckState;
import com.sony.imaging.app.startrails.shooting.state.STMfAssistState;
import com.sony.imaging.app.startrails.shooting.state.STNormalCaptureState;
import com.sony.imaging.app.startrails.shooting.state.STS1OffEEState;
import com.sony.imaging.app.startrails.shooting.state.STS1OnEEState;
import com.sony.imaging.app.startrails.shooting.state.STShootingMenuState;
import com.sony.imaging.app.startrails.shooting.state.STShootingState;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class STFactory extends Factory {
    @Override // com.sony.imaging.app.fw.Factory
    protected Class<? extends StateFactory> getTop() {
        return RootStateFactory.class;
    }

    /* loaded from: classes.dex */
    public static class RootStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(BaseApp.APP_SHOOTING, STShootingState.class);
            STFactory.add(STShootingState.class, ShootingStateFactory.class, ShootingLayoutFactory.class, STShootingStateKeyHandler.class);
            add(BaseApp.APP_PLAY, STPlayRootContainer.class);
            STFactory.add(STPlayRootContainer.class, PlaybackStateFactory.class, null, null);
            add(BaseApp.CAUTION, CautionFragment.class);
            STFactory.add(CautionFragment.class, CautionStateFactory.class, null, null);
            add(BaseApp.EXIT_SCREEN, ExitScreenState.class);
            STFactory.add(ExitScreenState.class, null, ExitScreenLayoutFactory.class, null);
            add(BaseApp.FORCE_EXIT_SCREEN, ForceExitScreenState.class);
            STFactory.add(ForceExitScreenState.class, null, ForceExitScreenLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class CautionStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("CautionDisplayState", CautionDisplayState.class);
            STFactory.add(CautionDisplayState.class, null, CautionLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class ShootingStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("FORCESETTING", STForceSettingState.class);
            STFactory.add(STForceSettingState.class, null, null, null);
            add(MFModeCheckState.TAG, MFModeCheckState.class);
            STFactory.add(MFModeCheckState.class, null, null, null);
            add(LongExposureNRCheckState.TAG, LongExposureNRCheckState.class);
            STFactory.add(LongExposureNRCheckState.class, null, null, null);
            add(STExposureModeCheckState.TAG, STExposureModeCheckState.class);
            STFactory.add(STExposureModeCheckState.class, null, null, null);
            add("EE", STEEState.class);
            STFactory.add(STEEState.class, EEStateFactory.class, null, STEEStateKeyHandler.class);
            add("Capture", CaptureState.class);
            STFactory.add(CaptureState.class, CaptureStateFactory.class, null, CaptureStateKeyHandler.class);
            add("Development", STDevelopmentState.class);
            STFactory.add(STDevelopmentState.class, DevelopmentStateFactory.class, DevelopmentLayoutFactory.class, DevelopmentStateKeyHandler.class);
            add(AutoReviewState.STATE_NAME, AutoReviewState.class);
            STFactory.add(AutoReviewState.class, null, AutoReviewLayoutFactory.class, AutoReviewStateKeyHandler.class);
            add("AutoReviewForLimitedContShooting", AutoReviewForLimitedContShootingState.class);
            STFactory.add(AutoReviewForLimitedContShootingState.class, null, AutoReviewForLimitedContShootingLayoutFactory.class, AutoReviewForLimitedContShootingStateKeyHandler.class);
            add("CustomWhiteBalance", STCustomWhiteBalanceControllerState.class);
            STFactory.add(STCustomWhiteBalanceControllerState.class, CustomWhiteBalanceControllerStateFactory.class, null, null);
            if (Environment.isMovieAPISupported()) {
                add("MovieCapture", MovieCaptureState.class);
                STFactory.add(MovieCaptureState.class, null, MovieLayoutFactory.class, MovieCaptureStateKeyHandler.class);
                add("MovieRec", MovieRecState.class);
                STFactory.add(MovieRecState.class, MovieRecStateFactory.class, MovieLayoutFactory.class, MovieRecStateKeyHandler.class);
                add("MovieSave", MovieSaveState.class);
                STFactory.add(MovieSaveState.class, null, MovieSaveLayoutFactory.class, MovieSaveStateKeyHandler.class);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class MovieRecStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(ICustomKey.CATEGORY_MENU, MovieMenuState.class);
            STFactory.add(MovieMenuState.class, null, MenuLayoutFactory.class, null);
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
            add("MovieSaveLayout", MovieSaveLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class EEStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(S1OffEEState.STATE_NAME, STS1OffEEState.class);
            STFactory.add(STS1OffEEState.class, null, S1OffEELayoutFactory.class, STS1OffKeyHandler.class);
            add("S1OnEE", STS1OnEEState.class);
            STFactory.add(STS1OnEEState.class, null, S1OnEELayoutFactory.class, STS1OnEEStateKeyHandler.class);
            add(STMFModeCheckState.TAG, STMFModeCheckState.class);
            STFactory.add(STMFModeCheckState.class, null, null, null);
            add("MfAssist", STMfAssistState.class);
            STFactory.add(STMfAssistState.class, null, MfAssistLayoutFactory.class, STMFAssistStateKeyHandler.class);
            add("FocusAdjustment", FocusAdjustmentState.class);
            STFactory.add(FocusAdjustmentState.class, null, FocusAdjustmentFactory.class, FocusAdjustmentKeyHandler.class);
            add("MovieRecStandby", MovieRecStandbyState.class);
            STFactory.add(MovieRecStandbyState.class, null, MovieLayoutFactory.class, MovieRecStandbyStateKeyHandler.class);
            add(ICustomKey.CATEGORY_MENU, STShootingMenuState.class);
            STFactory.add(STShootingMenuState.class, null, MenuLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class CaptureStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("NormalCapture", STNormalCaptureState.class);
            STFactory.add(STNormalCaptureState.class, null, CaptureLayoutFactory.class, STCaptureStateKeyHandler.class);
            add("SelfTimerCapture", SelfTimerCaptureState.class);
            STFactory.add(SelfTimerCaptureState.class, null, SelfTimerCaptureLayoutFactory.class, STSelfTimerCaptureStateKeyHandler.class);
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
            STFactory.add(CustomWhiteBalanceEEState.class, null, CWBEELayoutFactory.class, CustomWhiteBalanceEEKeyHandler.class);
            add("CWBCapture", CustomWhiteBalanceCaptureState.class);
            STFactory.add(CustomWhiteBalanceCaptureState.class, null, null, CustomWhiteBalanceCaptureKeyHandler.class);
            add("CWBExposure", CustomWhiteBalanceExposureState.class);
            STFactory.add(CustomWhiteBalanceExposureState.class, null, CWBExposureLayoutFactory.class, CustomWhiteBalanceExposureKeyHandler.class);
        }
    }

    /* loaded from: classes.dex */
    public static class ExitScreenLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("ExitScreen", STExitScreenLayout.class);
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
            add("cautionLayout", STCautionLayout.class);
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
            add(StateBase.DEFAULT_LAYOUT, STStableLayout.class);
            add(StateBase.S1OFF_LAYOUT, STS1OffLayout.class);
            add(StateBase.FOCUS_LAYOUT, FocusLayout.class);
            add(StateBase.GUIDE_LAYOUT, GuideLayout.class);
            add(STS1OffEEState.INTRODUCTION_LAYOUT, IntroductoryLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class S1OnEELayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(StateBase.DEFAULT_LAYOUT, STStableLayout.class);
            add(StateBase.FOCUS_LAYOUT, FocusLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class MfAssistLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(StateBase.DEFAULT_LAYOUT, STMFassistLayout.class);
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
            add(StateBase.DEFAULT_LAYOUT, STCaptureStableLayout.class);
            add(StateBase.FOCUS_LAYOUT, FocusLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class CaptureLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(StateBase.DEFAULT_LAYOUT, STCaptureStableLayout.class);
            add(StateBase.FOCUS_LAYOUT, FocusLayout.class);
            add(SelfTimerDisplayLayout.TAG, SelfTimerDisplayLayout.class);
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
            STFactory.add(Browser.class, BrowserFactory.class, null, null);
            add(PlayRootContainer.ID_PSEUDOREC, PseudoRec.class);
            STFactory.add(PseudoRec.class, PseudoRecFactory.class, null, null);
            add(PlayRootContainer.ID_PSEUDOREC_ZOOM, PseudoRecZoom.class);
            STFactory.add(PseudoRecZoom.class, PseudoRecZoomFactory.class, null, null);
            add(PlayRootContainer.ID_TRANSITING_TO_SHOOTING, TransitionToShooting.class);
        }
    }

    /* loaded from: classes.dex */
    public static class BrowserSingleLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("NORMAL_PB", PlaybackLayout.class);
            STFactory.add(PlaybackLayout.class, null, SinglePBLayoutFactory.class, null);
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
    public static class PseudoRecFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlaySubApp.ID_SINGLE_PB, STPseudoRecSingle.class);
            STFactory.add(STPseudoRecSingle.class, null, PseudoRecSingleLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class PseudoRecZoomFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlaySubApp.ID_DEFAULT_STATE, PseudoRecZoomState.class);
            STFactory.add(PseudoRecZoomState.class, null, PseudoRecZoomStateLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class PseudoRecZoomStateLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(SingleStateBase.ID_BUFFER_PLAYBACK_LAYOUT, STPseudoRecZoomLayout.class);
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
            STFactory.add(WhiteBalanceMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(WhiteBalanceAdjustmentMenuLayout.MENU_ID, STWhiteBalanceAdjustmentMenuLayout.class);
            STFactory.add(STWhiteBalanceAdjustmentMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_FLASHCOMPENSATIONMENULAYOUT", FlashCompensationMenuLayout.class);
            STFactory.add(FlashCompensationMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_AFFLEXIBLEPOSITIONMENULAYOUT", AFFlexiblePositionMenuLayout.class);
            STFactory.add(AFFlexiblePositionMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_CREATIVESTYLEMENULAYOUT", STCreativeStyleMenuLayout.class);
            STFactory.add(STCreativeStyleMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_AFLOCALPOSITIONMENULAYOUT", AFLocalPositionMenuLayout.class);
            STFactory.add(AFLocalPositionMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            if (ExposureModeController.isUiMovieMainFeature()) {
                add(ExposureModeMenuLayout.MENU_ID, ExposureModeMenuMovieFeatureLayout.class);
                STFactory.add(ExposureModeMenuMovieFeatureLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            } else {
                add(ExposureModeMenuLayout.MENU_ID, STExposureModeMenuLayout.class);
                STFactory.add(STExposureModeMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            }
            add("ID_SCENESELECTIONMENULAYOUT", SceneSelectionMenuLayout.class);
            STFactory.add(SceneSelectionMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_EXPOSURECOMPENSATIONMENULAYOUT", STExposureCompensationMenuLayout.class);
            STFactory.add(STExposureCompensationMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_MOVIEMODEMENULAYOUT", MovieModeMenuLayout.class);
            STFactory.add(MovieModeMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(UnknownItemMenuLayout.MENU_ID, UnknownItemMenuLayout.class);
            STFactory.add(UnknownItemMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("LastBastionLayout", LastBastionMenuLayout.class);
            STFactory.add(LastBastionMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(FnMenuLayout.MENU_ID, FnMenuLayout.class);
            STFactory.add(FnMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(Fn15LayerMenuLayout.MENU_ID, Fn15LayerMenuLayout.class);
            STFactory.add(Fn15LayerMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(SpecialScreenMenuLayout.MENU_ID, SpecialScreenMenuLayout.class);
            STFactory.add(SpecialScreenMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(ISOSpecialScreenMenuLayout.MENU_ID, ISOSpecialScreenMenuLayout.class);
            STFactory.add(ISOSpecialScreenMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(DigitalZoomSettingLayout.MENU_ID, DigitalZoomSettingLayout.class);
            STFactory.add(DigitalZoomSettingLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(FocusModeSpecialScreenMenuLayout.MENU_ID, FocusModeSpecialScreenMenuLayout.class);
            STFactory.add(FocusModeSpecialScreenMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(Fn15LayerExposureCompensationLayout.MENU_ID, Fn15LayerExposureCompensationLayout.class);
            STFactory.add(Fn15LayerExposureCompensationLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(Fn15LayerFlashCompensationLayout.MENU_ID, Fn15LayerFlashCompensationLayout.class);
            STFactory.add(Fn15LayerFlashCompensationLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(Fn15LayerCreativeStyleLayout.MENU_ID, Fn15LayerCreativeStyleLayout.class);
            STFactory.add(Fn15LayerCreativeStyleLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(Fn15LayerWhiteBalanceLayout.MENU_ID, Fn15LayerWhiteBalanceLayout.class);
            STFactory.add(Fn15LayerWhiteBalanceLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(ISOFn15LayerMenuLayout.MENU_ID, ISOFn15LayerMenuLayout.class);
            STFactory.add(ISOFn15LayerMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(PageMenuLayout.MENU_ID, STPageMenuLayout.class);
            STFactory.add(STPageMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(SetMenuLayout.MENU_ID, SetMenuLayout.class);
            STFactory.add(SetMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(SpecialScreenMenuLayoutHiddenEE.MENU_ID, SpecialScreenMenuLayoutHiddenEE.class);
            STFactory.add(SpecialScreenMenuLayoutHiddenEE.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(ThemeSelectMenuLayout.APPTOP_MENU_ID, ThemeSelectMenuLayout.class);
            STFactory.add(ThemeSelectMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_PARAMETERSETTINGMENULAYOUT", ParameterSettingMenuLayout.class);
            STFactory.add(ParameterSettingMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_SHOOTINGTIPMENULAYOUT", ShootingTipMenuLayout.class);
            STFactory.add(ShootingTipMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class OneAnotherMenuLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("ID_WHITEBALANCEMENULAYOUT", WhiteBalanceMenuLayout.class);
            add(WhiteBalanceAdjustmentMenuLayout.MENU_ID, STWhiteBalanceAdjustmentMenuLayout.class);
            add("ID_FLASHCOMPENSATIONMENULAYOUT", FlashCompensationMenuLayout.class);
            add("ID_AFFLEXIBLEPOSITIONMENULAYOUT", AFFlexiblePositionMenuLayout.class);
            add("ID_CREATIVESTYLEMENULAYOUT", STCreativeStyleMenuLayout.class);
            add("ID_AFLOCALPOSITIONMENULAYOUT", AFLocalPositionMenuLayout.class);
            if (ExposureModeController.isUiMovieMainFeature()) {
                add(ExposureModeMenuLayout.MENU_ID, ExposureModeMenuMovieFeatureLayout.class);
            } else {
                add(ExposureModeMenuLayout.MENU_ID, STExposureModeMenuLayout.class);
            }
            add("ID_SCENESELECTIONMENULAYOUT", SceneSelectionMenuLayout.class);
            add("ID_EXPOSURECOMPENSATIONMENULAYOUT", STExposureCompensationMenuLayout.class);
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
            add(PageMenuLayout.MENU_ID, STPageMenuLayout.class);
            add(SetMenuLayout.MENU_ID, SetMenuLayout.class);
            add(SpecialScreenMenuLayoutHiddenEE.MENU_ID, SpecialScreenMenuLayoutHiddenEE.class);
            add(ThemeSelectMenuLayout.APPTOP_MENU_ID, ThemeSelectMenuLayout.class);
            add("ID_PARAMETERSETTINGMENULAYOUT", ParameterSettingMenuLayout.class);
            add("ID_SHOOTINGTIPMENULAYOUT", ShootingTipMenuLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class PseudoRecSingleLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(SingleStateBase.ID_BUFFER_PLAYBACK_LAYOUT, STPseudoRecSingleLayout.class);
            add(SingleStateBase.ID_NO_BUFFER_LAYOUT, PseudoRecNoFileLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class BrowserFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlaySubApp.ID_SINGLE_PB, BrowserSingle.class);
            STFactory.add(BrowserSingle.class, null, BrowserSingleLayoutFactory.class, null);
            add(PlaySubApp.ID_INDEX_PB, BrowserIndex.class);
            STFactory.add(BrowserIndex.class, null, BrowserIndexLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class SinglePBLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(STPlayRootContainer.ID_DELETE_MULTIPLE_LAYOUT, DeleteMultipleConfirmLayout.class);
            STFactory.add(DeleteMultipleConfirmLayout.class, null, DeleteMultipleConfirmLayoutFactory.class, null);
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
