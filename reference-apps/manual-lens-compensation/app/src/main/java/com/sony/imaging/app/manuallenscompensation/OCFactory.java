package com.sony.imaging.app.manuallenscompensation;

import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.caution.CautionDisplayState;
import com.sony.imaging.app.base.caution.CautionFragment;
import com.sony.imaging.app.base.common.ForceExitScreenState;
import com.sony.imaging.app.base.common.layout.ForceExitScreenLayout;
import com.sony.imaging.app.base.menu.layout.AFFlexiblePositionMenuLayout;
import com.sony.imaging.app.base.menu.layout.AFLocalPositionMenuLayout;
import com.sony.imaging.app.base.menu.layout.CreativeStyleMenuLayout;
import com.sony.imaging.app.base.menu.layout.DigitalZoomSettingLayout;
import com.sony.imaging.app.base.menu.layout.ExposureCompensationMenuLayout;
import com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout;
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
import com.sony.imaging.app.base.playback.base.editor.Editor;
import com.sony.imaging.app.base.playback.browser.Browser;
import com.sony.imaging.app.base.playback.browser.BrowserIndex;
import com.sony.imaging.app.base.playback.browser.BrowserSingle;
import com.sony.imaging.app.base.playback.delete.single.DeleteSingle;
import com.sony.imaging.app.base.playback.delete.single.DeleteSingleConfirm;
import com.sony.imaging.app.base.playback.delete.single.DeleteSingleExecutor;
import com.sony.imaging.app.base.playback.delete.single.DeleteSingleFinalize;
import com.sony.imaging.app.base.playback.delete.single.DeleteSingleTestContents;
import com.sony.imaging.app.base.playback.layout.BrowserIndexLayout;
import com.sony.imaging.app.base.playback.layout.BrowserIndexNoFileLayout;
import com.sony.imaging.app.base.playback.layout.BrowserSingleLayout;
import com.sony.imaging.app.base.playback.layout.BrowserSingleNoBufferLayout;
import com.sony.imaging.app.base.playback.layout.BrowserSingleNoFileLayout;
import com.sony.imaging.app.base.playback.layout.DeleteSingleConfirmLayout;
import com.sony.imaging.app.base.playback.layout.DeleteSingleProcessingLayout;
import com.sony.imaging.app.base.playback.layout.EventBlockerLayout;
import com.sony.imaging.app.base.playback.layout.PlayZoomLayout;
import com.sony.imaging.app.base.playback.layout.PseudoRecNoFileLayout;
import com.sony.imaging.app.base.playback.layout.PseudoRecSingleLayout;
import com.sony.imaging.app.base.playback.layout.PseudoRecZoomLayout;
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
import com.sony.imaging.app.base.shooting.CustomWhiteBalanceControllerState;
import com.sony.imaging.app.base.shooting.CustomWhiteBalanceEEState;
import com.sony.imaging.app.base.shooting.CustomWhiteBalanceExposureState;
import com.sony.imaging.app.base.shooting.DevelopmentState;
import com.sony.imaging.app.base.shooting.FocusAdjustmentState;
import com.sony.imaging.app.base.shooting.MfAssistState;
import com.sony.imaging.app.base.shooting.MovieMenuState;
import com.sony.imaging.app.base.shooting.S1OffEEState;
import com.sony.imaging.app.base.shooting.S1OnEEState;
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
import com.sony.imaging.app.base.shooting.layout.ShootingLayout;
import com.sony.imaging.app.base.shooting.movie.MovieRecStandbyState;
import com.sony.imaging.app.base.shooting.movie.MovieRecState;
import com.sony.imaging.app.base.shooting.movie.MovieSaveState;
import com.sony.imaging.app.base.shooting.movie.layout.MovieSaveLayout;
import com.sony.imaging.app.base.shooting.movie.trigger.MovieCaptureStateKeyHandler;
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
import com.sony.imaging.app.manuallenscompensation.caution.layout.OCCautionLayout;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCConstants;
import com.sony.imaging.app.manuallenscompensation.menu.layout.DeleteProfileProcessingMenuLayout;
import com.sony.imaging.app.manuallenscompensation.menu.layout.DeleteSingleConfirmMenuLayout;
import com.sony.imaging.app.manuallenscompensation.menu.layout.ExternalProfileDescriptionMenuLayout;
import com.sony.imaging.app.manuallenscompensation.menu.layout.ExternalProfileMenuLayout;
import com.sony.imaging.app.manuallenscompensation.menu.layout.LensAdjustMenuLayout;
import com.sony.imaging.app.manuallenscompensation.menu.layout.LensEditMenuLayout;
import com.sony.imaging.app.manuallenscompensation.menu.layout.LensListMenuLayout;
import com.sony.imaging.app.manuallenscompensation.menu.layout.OCExposureModeMenuMovieFeatureLayout;
import com.sony.imaging.app.manuallenscompensation.menu.layout.OCExposureModeSubMenuLayout;
import com.sony.imaging.app.manuallenscompensation.menu.layout.OCMovieModeMenuLayout;
import com.sony.imaging.app.manuallenscompensation.menu.layout.OCPageMenuLayout;
import com.sony.imaging.app.manuallenscompensation.menu.layout.OCSceneSelectionMenuLayout;
import com.sony.imaging.app.manuallenscompensation.menu.layout.OCSpecialScreenMenuLayout;
import com.sony.imaging.app.manuallenscompensation.playback.OCPlayRoot;
import com.sony.imaging.app.manuallenscompensation.playback.state.OCBrowserSingle;
import com.sony.imaging.app.manuallenscompensation.shooting.keyhandler.OCExposureModeCheckStateKeyHandler;
import com.sony.imaging.app.manuallenscompensation.shooting.keyhandler.OCMfAssistKeyHandler;
import com.sony.imaging.app.manuallenscompensation.shooting.keyhandler.OCMovieRecStandbyStateKeyHandler;
import com.sony.imaging.app.manuallenscompensation.shooting.keyhandler.OCS1OffEEStateKeyHandler;
import com.sony.imaging.app.manuallenscompensation.shooting.keyhandler.OCShootingStateKeyHandler;
import com.sony.imaging.app.manuallenscompensation.shooting.layout.OCExitScreenLayout;
import com.sony.imaging.app.manuallenscompensation.shooting.layout.OCS1OffLayout;
import com.sony.imaging.app.manuallenscompensation.shooting.layout.OCStableLayout;
import com.sony.imaging.app.manuallenscompensation.shooting.state.OCEEState;
import com.sony.imaging.app.manuallenscompensation.shooting.state.OCExitScreenState;
import com.sony.imaging.app.manuallenscompensation.shooting.state.OCExposureModeCheckState;
import com.sony.imaging.app.manuallenscompensation.shooting.state.OCMovieCaptureState;
import com.sony.imaging.app.manuallenscompensation.shooting.state.OCNormalCaptureState;
import com.sony.imaging.app.manuallenscompensation.shooting.state.OCSelfTimerCaptureState;
import com.sony.imaging.app.manuallenscompensation.shooting.state.OCShootingMenuState;
import com.sony.imaging.app.manuallenscompensation.shooting.state.OCShootingState;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class OCFactory extends Factory {
    @Override // com.sony.imaging.app.fw.Factory
    protected Class<? extends StateFactory> getTop() {
        return RootStateFactory.class;
    }

    /* loaded from: classes.dex */
    public static class RootStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(BaseApp.APP_SHOOTING, OCShootingState.class);
            OCFactory.add(OCShootingState.class, ShootingStateFactory.class, ShootingLayoutFactory.class, OCShootingStateKeyHandler.class);
            add(BaseApp.APP_PLAY, OCPlayRoot.class);
            OCFactory.add(OCPlayRoot.class, PlaybackStateFactory.class, null, null);
            add(BaseApp.CAUTION, CautionFragment.class);
            OCFactory.add(CautionFragment.class, CautionStateFactory.class, null, null);
            add(BaseApp.EXIT_SCREEN, OCExitScreenState.class);
            OCFactory.add(OCExitScreenState.class, null, ExitScreenLayoutFactory.class, null);
            add(BaseApp.FORCE_EXIT_SCREEN, ForceExitScreenState.class);
            OCFactory.add(ForceExitScreenState.class, null, ForceExitScreenLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class CautionStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("CautionDisplayState", CautionDisplayState.class);
            OCFactory.add(CautionDisplayState.class, null, CautionLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class ShootingStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(OCConstants.EXPOSURE_MODE_STATE, OCExposureModeCheckState.class);
            OCFactory.add(OCExposureModeCheckState.class, null, null, OCExposureModeCheckStateKeyHandler.class);
            add("EE", OCEEState.class);
            OCFactory.add(OCEEState.class, EEStateFactory.class, null, EEStateKeyHandler.class);
            add("Capture", CaptureState.class);
            OCFactory.add(CaptureState.class, CaptureStateFactory.class, null, CaptureStateKeyHandler.class);
            add("Development", DevelopmentState.class);
            OCFactory.add(DevelopmentState.class, DevelopmentStateFactory.class, DevelopmentLayoutFactory.class, DevelopmentStateKeyHandler.class);
            add(AutoReviewState.STATE_NAME, AutoReviewState.class);
            OCFactory.add(AutoReviewState.class, null, AutoReviewLayoutFactory.class, AutoReviewStateKeyHandler.class);
            add("AutoReviewForLimitedContShooting", AutoReviewForLimitedContShootingState.class);
            OCFactory.add(AutoReviewForLimitedContShootingState.class, null, AutoReviewForLimitedContShootingLayoutFactory.class, AutoReviewForLimitedContShootingStateKeyHandler.class);
            add("CustomWhiteBalance", CustomWhiteBalanceControllerState.class);
            OCFactory.add(CustomWhiteBalanceControllerState.class, CustomWhiteBalanceControllerStateFactory.class, null, null);
            if (Environment.isMovieAPISupported()) {
                add("MovieCapture", OCMovieCaptureState.class);
                OCFactory.add(OCMovieCaptureState.class, null, MovieLayoutFactory.class, MovieCaptureStateKeyHandler.class);
                add("MovieRec", MovieRecState.class);
                OCFactory.add(MovieRecState.class, MovieRecStateFactory.class, MovieLayoutFactory.class, MovieRecStateKeyHandler.class);
                add("MovieSave", MovieSaveState.class);
                OCFactory.add(MovieSaveState.class, null, MovieSaveLayoutFactory.class, MovieSaveStateKeyHandler.class);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class MovieRecStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("Menu", MovieMenuState.class);
            OCFactory.add(MovieMenuState.class, null, MenuLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class MovieLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(StateBase.DEFAULT_LAYOUT, OCStableLayout.class);
            add(DigitalZoomSettingLayout.MENU_ID, DigitalZoomSettingLayout.class);
            add(StateBase.FOCUS_LAYOUT, FocusLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class MovieSaveLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("StableLayout", OCStableLayout.class);
            add("MovieSaveLayout", MovieSaveLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class EEStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(S1OffEEState.STATE_NAME, S1OffEEState.class);
            OCFactory.add(S1OffEEState.class, null, S1OffEELayoutFactory.class, OCS1OffEEStateKeyHandler.class);
            add("S1OnEE", S1OnEEState.class);
            OCFactory.add(S1OnEEState.class, null, S1OnEELayoutFactory.class, S1OnEEStateKeyHandler.class);
            add("MfAssist", MfAssistState.class);
            OCFactory.add(MfAssistState.class, null, MfAssistLayoutFactory.class, OCMfAssistKeyHandler.class);
            add("FocusAdjustment", FocusAdjustmentState.class);
            OCFactory.add(FocusAdjustmentState.class, null, FocusAdjustmentFactory.class, FocusAdjustmentKeyHandler.class);
            add("MovieRecStandby", MovieRecStandbyState.class);
            OCFactory.add(MovieRecStandbyState.class, null, MovieLayoutFactory.class, OCMovieRecStandbyStateKeyHandler.class);
            add("Menu", OCShootingMenuState.class);
            OCFactory.add(OCShootingMenuState.class, null, MenuLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class CaptureStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("NormalCapture", OCNormalCaptureState.class);
            OCFactory.add(OCNormalCaptureState.class, null, null, null);
            add("SelfTimerCapture", OCSelfTimerCaptureState.class);
            OCFactory.add(OCSelfTimerCaptureState.class, null, SelfTimerCaptureLayoutFactory.class, SelfTimerCaptureStateKeyHandler.class);
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
            OCFactory.add(CustomWhiteBalanceEEState.class, null, CWBEELayoutFactory.class, CustomWhiteBalanceEEKeyHandler.class);
            add("CWBCapture", CustomWhiteBalanceCaptureState.class);
            OCFactory.add(CustomWhiteBalanceCaptureState.class, null, null, CustomWhiteBalanceCaptureKeyHandler.class);
            add("CWBExposure", CustomWhiteBalanceExposureState.class);
            OCFactory.add(CustomWhiteBalanceExposureState.class, null, CWBExposureLayoutFactory.class, CustomWhiteBalanceExposureKeyHandler.class);
        }
    }

    /* loaded from: classes.dex */
    public static class ExitScreenLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("ExitScreen", OCExitScreenLayout.class);
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
            add("cautionLayout", OCCautionLayout.class);
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
            add(StateBase.DEFAULT_LAYOUT, OCStableLayout.class);
            add(StateBase.S1OFF_LAYOUT, OCS1OffLayout.class);
            add(StateBase.FOCUS_LAYOUT, FocusLayout.class);
            add(StateBase.GUIDE_LAYOUT, GuideLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class S1OnEELayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(StateBase.DEFAULT_LAYOUT, OCStableLayout.class);
            add(StateBase.FOCUS_LAYOUT, FocusLayout.class);
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
            add(StateBase.DEFAULT_LAYOUT, OCStableLayout.class);
            add(StateBase.FOCUS_LAYOUT, FocusLayout.class);
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
            OCFactory.add(Browser.class, BrowserFactory.class, null, null);
            add(PlayRootContainer.ID_PLAYZOOM, PlayZoom.class);
            OCFactory.add(PlayZoom.class, PlayZoomFactory.class, null, null);
            add(PlayRootContainer.ID_PSEUDOREC, PseudoRec.class);
            OCFactory.add(PseudoRec.class, PseudoRecFactory.class, null, null);
            add(PlayRootContainer.ID_PSEUDOREC_ZOOM, PseudoRecZoom.class);
            OCFactory.add(PseudoRecZoom.class, PseudoRecZoomFactory.class, null, null);
            add(PlayRootContainer.ID_TRANSITING_TO_SHOOTING, TransitionToShooting.class);
        }
    }

    /* loaded from: classes.dex */
    public static class BrowserFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlaySubApp.ID_SINGLE_PB, BrowserSingle.class);
            OCFactory.add(BrowserSingle.class, PbFunctionCommonFactory.class, BrowserSingleLayoutFactory.class, null);
            add(PlaySubApp.ID_INDEX_PB, BrowserIndex.class);
            OCFactory.add(BrowserIndex.class, PbFunctionCommonFactory.class, BrowserIndexLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class PbFunctionCommonFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlayRootContainer.ID_DELETE_SINGLE, DeleteSingle.class);
            OCFactory.add(DeleteSingle.class, DeleteSingleFactory.class, null, null);
        }
    }

    /* loaded from: classes.dex */
    public static class DeleteSingleFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(Editor.ID_TEST_THIS, DeleteSingleTestContents.class);
            add(Editor.ID_CONFIRM, DeleteSingleConfirm.class);
            OCFactory.add(DeleteSingleConfirm.class, null, DeleteSingleConfirmLayoutFactory.class, null);
            add(Editor.ID_EXECUTOR, DeleteSingleExecutor.class);
            OCFactory.add(DeleteSingleExecutor.class, null, DeleteSingleExecutorLayoutFactory.class, null);
            add(DeleteSingle.ID_FINALIZER, DeleteSingleFinalize.class);
            OCFactory.add(DeleteSingleFinalize.class, null, DeleteSingleFinalizeLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class PlayZoomFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlaySubApp.ID_DEFAULT_STATE, PlayZoomState.class);
            OCFactory.add(PlayZoomState.class, PbFunctionCommonFactory.class, PlayZoomStateLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class PseudoRecFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlaySubApp.ID_SINGLE_PB, PseudoRecSingle.class);
            OCFactory.add(PseudoRecSingle.class, null, PseudoRecSingleLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class PseudoRecZoomFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlaySubApp.ID_DEFAULT_STATE, PseudoRecZoomState.class);
            OCFactory.add(PseudoRecZoomState.class, null, PseudoRecZoomStateLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class BrowserSingleLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("NORMAL_PB", BrowserSingleLayout.class);
            add("NO_FILE", BrowserSingleNoFileLayout.class);
            add(SingleStateBase.ID_BUFFER_PLAYBACK_LAYOUT, BrowserSingleLayout.class);
            add(SingleStateBase.ID_NO_BUFFER_LAYOUT, BrowserSingleNoBufferLayout.class);
            add(OCBrowserSingle.ID_REC_MODE_MOVIE_NOT_SUPPORTED, BrowserSingleNoFileLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class BrowserIndexLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("NORMAL_PB", BrowserIndexLayout.class);
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
            OCFactory.add(WhiteBalanceMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(WhiteBalanceAdjustmentMenuLayout.MENU_ID, WhiteBalanceAdjustmentMenuLayout.class);
            OCFactory.add(WhiteBalanceAdjustmentMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_FLASHCOMPENSATIONMENULAYOUT", FlashCompensationMenuLayout.class);
            OCFactory.add(FlashCompensationMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_AFFLEXIBLEPOSITIONMENULAYOUT", AFFlexiblePositionMenuLayout.class);
            OCFactory.add(AFFlexiblePositionMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_CREATIVESTYLEMENULAYOUT", CreativeStyleMenuLayout.class);
            OCFactory.add(CreativeStyleMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_AFLOCALPOSITIONMENULAYOUT", AFLocalPositionMenuLayout.class);
            OCFactory.add(AFLocalPositionMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            if (ExposureModeController.isUiMovieMainFeature()) {
                add(ExposureModeMenuLayout.MENU_ID, OCExposureModeMenuMovieFeatureLayout.class);
                OCFactory.add(OCExposureModeMenuMovieFeatureLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            } else {
                add(ExposureModeMenuLayout.MENU_ID, OCExposureModeSubMenuLayout.class);
                OCFactory.add(OCExposureModeSubMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            }
            add("ID_SCENESELECTIONMENULAYOUT", OCSceneSelectionMenuLayout.class);
            OCFactory.add(OCSceneSelectionMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_MOVIEMODEMENULAYOUT", OCMovieModeMenuLayout.class);
            OCFactory.add(OCMovieModeMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_EXPOSURECOMPENSATIONMENULAYOUT", ExposureCompensationMenuLayout.class);
            OCFactory.add(ExposureCompensationMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(UnknownItemMenuLayout.MENU_ID, UnknownItemMenuLayout.class);
            OCFactory.add(UnknownItemMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("LastBastionLayout", LastBastionMenuLayout.class);
            OCFactory.add(LastBastionMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(FnMenuLayout.MENU_ID, FnMenuLayout.class);
            OCFactory.add(FnMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(Fn15LayerMenuLayout.MENU_ID, Fn15LayerMenuLayout.class);
            OCFactory.add(Fn15LayerMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(SpecialScreenMenuLayout.MENU_ID, SpecialScreenMenuLayout.class);
            OCFactory.add(SpecialScreenMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(ISOSpecialScreenMenuLayout.MENU_ID, ISOSpecialScreenMenuLayout.class);
            OCFactory.add(ISOSpecialScreenMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(DigitalZoomSettingLayout.MENU_ID, DigitalZoomSettingLayout.class);
            OCFactory.add(DigitalZoomSettingLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(FocusModeSpecialScreenMenuLayout.MENU_ID, FocusModeSpecialScreenMenuLayout.class);
            OCFactory.add(FocusModeSpecialScreenMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(Fn15LayerExposureCompensationLayout.MENU_ID, Fn15LayerExposureCompensationLayout.class);
            OCFactory.add(Fn15LayerExposureCompensationLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(Fn15LayerFlashCompensationLayout.MENU_ID, Fn15LayerFlashCompensationLayout.class);
            OCFactory.add(Fn15LayerFlashCompensationLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(Fn15LayerCreativeStyleLayout.MENU_ID, Fn15LayerCreativeStyleLayout.class);
            OCFactory.add(Fn15LayerCreativeStyleLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(Fn15LayerWhiteBalanceLayout.MENU_ID, Fn15LayerWhiteBalanceLayout.class);
            OCFactory.add(Fn15LayerWhiteBalanceLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_PAGEMENULAYOUT", OCPageMenuLayout.class);
            OCFactory.add(OCPageMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(SetMenuLayout.MENU_ID, SetMenuLayout.class);
            OCFactory.add(SetMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(SpecialScreenMenuLayoutHiddenEE.MENU_ID, SpecialScreenMenuLayoutHiddenEE.class);
            OCFactory.add(SpecialScreenMenuLayoutHiddenEE.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(SpecialScreenMenuLayout.MENU_ID, OCSpecialScreenMenuLayout.class);
            OCFactory.add(OCSpecialScreenMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_EXPOSUREMODESUBMENULAYOUT", OCExposureModeSubMenuLayout.class);
            OCFactory.add(OCExposureModeSubMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("LastBastionLayout", LastBastionMenuLayout.class);
            OCFactory.add(LastBastionMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(ExternalProfileMenuLayout.TAG, ExternalProfileMenuLayout.class);
            OCFactory.add(ExternalProfileMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(LensListMenuLayout.TAG, LensListMenuLayout.class);
            OCFactory.add(LensListMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_LENSEDITMENULAYOUT", LensEditMenuLayout.class);
            OCFactory.add(LensEditMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_DELETESINGLECONFIRMMENULAYOUT", DeleteSingleConfirmMenuLayout.class);
            OCFactory.add(DeleteSingleConfirmMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_LENSADJUSTMENULAYOUT", LensAdjustMenuLayout.class);
            OCFactory.add(LensAdjustMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(OCConstants.ID_DELETEPROFILEPROCESSING, DeleteProfileProcessingMenuLayout.class);
            OCFactory.add(DeleteProfileProcessingMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(ExternalProfileDescriptionMenuLayout.TAG, ExternalProfileDescriptionMenuLayout.class);
            OCFactory.add(ExternalProfileDescriptionMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
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
            if (ExposureModeController.isUiMovieMainFeature()) {
                add(ExposureModeMenuLayout.MENU_ID, OCExposureModeMenuMovieFeatureLayout.class);
            } else {
                add(ExposureModeMenuLayout.MENU_ID, OCExposureModeSubMenuLayout.class);
            }
            add("ID_SCENESELECTIONMENULAYOUT", OCSceneSelectionMenuLayout.class);
            add("ID_EXPOSURECOMPENSATIONMENULAYOUT", ExposureCompensationMenuLayout.class);
            add("ID_MOVIEMODEMENULAYOUT", OCMovieModeMenuLayout.class);
            add(UnknownItemMenuLayout.MENU_ID, UnknownItemMenuLayout.class);
            add("LastBastionLayout", LastBastionMenuLayout.class);
            add(FnMenuLayout.MENU_ID, FnMenuLayout.class);
            add(Fn15LayerMenuLayout.MENU_ID, Fn15LayerMenuLayout.class);
            add(SpecialScreenMenuLayout.MENU_ID, OCSpecialScreenMenuLayout.class);
            add(ISOSpecialScreenMenuLayout.MENU_ID, ISOSpecialScreenMenuLayout.class);
            add(DigitalZoomSettingLayout.MENU_ID, DigitalZoomSettingLayout.class);
            add(FocusModeSpecialScreenMenuLayout.MENU_ID, FocusModeSpecialScreenMenuLayout.class);
            add(Fn15LayerExposureCompensationLayout.MENU_ID, Fn15LayerExposureCompensationLayout.class);
            add(Fn15LayerFlashCompensationLayout.MENU_ID, Fn15LayerFlashCompensationLayout.class);
            add(Fn15LayerCreativeStyleLayout.MENU_ID, Fn15LayerCreativeStyleLayout.class);
            add(Fn15LayerWhiteBalanceLayout.MENU_ID, Fn15LayerWhiteBalanceLayout.class);
            add(ISOFn15LayerMenuLayout.MENU_ID, ISOFn15LayerMenuLayout.class);
            add("ID_PAGEMENULAYOUT", OCPageMenuLayout.class);
            add(SetMenuLayout.MENU_ID, SetMenuLayout.class);
            add(SpecialScreenMenuLayoutHiddenEE.MENU_ID, SpecialScreenMenuLayoutHiddenEE.class);
            add("ID_EXPOSUREMODESUBMENULAYOUT", OCExposureModeSubMenuLayout.class);
            add(LensListMenuLayout.TAG, LensListMenuLayout.class);
            add(ExternalProfileMenuLayout.TAG, ExternalProfileMenuLayout.class);
            add("ID_LENSEDITMENULAYOUT", LensEditMenuLayout.class);
            add("ID_LENSADJUSTMENULAYOUT", LensAdjustMenuLayout.class);
            add("ID_DELETESINGLECONFIRMMENULAYOUT", DeleteSingleConfirmMenuLayout.class);
            add(OCConstants.ID_DELETEPROFILEPROCESSING, DeleteProfileProcessingMenuLayout.class);
            add(ExternalProfileDescriptionMenuLayout.TAG, ExternalProfileDescriptionMenuLayout.class);
        }
    }
}
