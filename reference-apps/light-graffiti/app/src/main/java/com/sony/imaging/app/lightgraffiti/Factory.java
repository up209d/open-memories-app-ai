package com.sony.imaging.app.lightgraffiti;

import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.caution.CautionFragment;
import com.sony.imaging.app.base.common.ExitScreenState;
import com.sony.imaging.app.base.common.ForceExitScreenState;
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
import com.sony.imaging.app.base.playback.browser.Browser;
import com.sony.imaging.app.base.playback.browser.BrowserIndex;
import com.sony.imaging.app.base.playback.browser.BrowserSingle;
import com.sony.imaging.app.base.playback.delete.single.DeleteSingle;
import com.sony.imaging.app.base.playback.delete.single.DeleteSingleConfirm;
import com.sony.imaging.app.base.playback.delete.single.DeleteSingleExecutor;
import com.sony.imaging.app.base.playback.delete.single.DeleteSingleFinalize;
import com.sony.imaging.app.base.playback.delete.single.DeleteSingleTestContents;
import com.sony.imaging.app.base.playback.layout.BrowserDialog4kStart;
import com.sony.imaging.app.base.playback.layout.BrowserIndexLayout;
import com.sony.imaging.app.base.playback.layout.BrowserIndexNoFileLayout;
import com.sony.imaging.app.base.playback.layout.BrowserSingle4kLayout;
import com.sony.imaging.app.base.playback.layout.BrowserSingleLayout;
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
import com.sony.imaging.app.base.shooting.CustomWhiteBalanceControllerState;
import com.sony.imaging.app.base.shooting.CustomWhiteBalanceEEState;
import com.sony.imaging.app.base.shooting.CustomWhiteBalanceExposureState;
import com.sony.imaging.app.base.shooting.DevelopmentState;
import com.sony.imaging.app.base.shooting.ExposureModeCheckState;
import com.sony.imaging.app.base.shooting.IntervalRecCaptureState;
import com.sony.imaging.app.base.shooting.IntervalRecRecordingState;
import com.sony.imaging.app.base.shooting.MovieMenuState;
import com.sony.imaging.app.base.shooting.S1OffEEState;
import com.sony.imaging.app.base.shooting.StateBase;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.layout.AutoReviewForLimitedContShootingLayout;
import com.sony.imaging.app.base.shooting.layout.AutoReviewLayout;
import com.sony.imaging.app.base.shooting.layout.CustomWhiteBalanceConfLayout;
import com.sony.imaging.app.base.shooting.layout.CustomWhiteBalanceEELayout;
import com.sony.imaging.app.base.shooting.layout.FocusAdjustmentLayout;
import com.sony.imaging.app.base.shooting.layout.FocusLayout;
import com.sony.imaging.app.base.shooting.layout.GuideLayout;
import com.sony.imaging.app.base.shooting.layout.IntervalRecCaptureLayout;
import com.sony.imaging.app.base.shooting.layout.IntervalRecRecordingLayout;
import com.sony.imaging.app.base.shooting.layout.MfAssistLayout;
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
import com.sony.imaging.app.base.shooting.trigger.AutoReviewForLimitedContShootingStateKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.CaptureStateKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.CustomWhiteBalanceCaptureKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.CustomWhiteBalanceEEKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.CustomWhiteBalanceExposureKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.DevelopmentStateKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.EEStateKeyHandler;
import com.sony.imaging.app.base.shooting.trigger.IntervalRecRecordingStateKeyHandler;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.fw.LayoutFactory;
import com.sony.imaging.app.fw.StateFactory;
import com.sony.imaging.app.lightgraffiti.menu.layout.LGExposureModeMenuLayout;
import com.sony.imaging.app.lightgraffiti.menu.layout.LGPaintingDurationLayout;
import com.sony.imaging.app.lightgraffiti.playback.LGPlayRoot;
import com.sony.imaging.app.lightgraffiti.playback.browser.LGBrowserIndex;
import com.sony.imaging.app.lightgraffiti.playback.browser.LGBrowserSingle;
import com.sony.imaging.app.lightgraffiti.playback.delete.multiple.DeleteMultiple;
import com.sony.imaging.app.lightgraffiti.playback.delete.multiple.DeleteMultipleConfirm;
import com.sony.imaging.app.lightgraffiti.playback.delete.multiple.DeleteMultipleExecutor;
import com.sony.imaging.app.lightgraffiti.playback.delete.multiple.DeleteMultipleIndex;
import com.sony.imaging.app.lightgraffiti.playback.delete.multiple.DeleteMultipleSingle;
import com.sony.imaging.app.lightgraffiti.playback.layout.DeleteMultipleConfirmLayout;
import com.sony.imaging.app.lightgraffiti.playback.layout.DeleteMultipleIndexLayout;
import com.sony.imaging.app.lightgraffiti.playback.layout.DeleteMultipleProcessingLayout;
import com.sony.imaging.app.lightgraffiti.playback.layout.DeleteMultipleSingleLayout;
import com.sony.imaging.app.lightgraffiti.playback.layout.LGBrowserIndexLayout;
import com.sony.imaging.app.lightgraffiti.playback.layout.LGBrowserSingleLayout;
import com.sony.imaging.app.lightgraffiti.playback.layout.LGCautionLayout;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGExposureModeController;
import com.sony.imaging.app.lightgraffiti.shooting.keyhandler.LGAutoReviewStateKeyHandler;
import com.sony.imaging.app.lightgraffiti.shooting.keyhandler.LGFakeS1OffEEStateKeyHandler;
import com.sony.imaging.app.lightgraffiti.shooting.keyhandler.LGFocusAdjustmentKeyHandler;
import com.sony.imaging.app.lightgraffiti.shooting.keyhandler.LGMfAssistStateKeyHandler;
import com.sony.imaging.app.lightgraffiti.shooting.keyhandler.LGNormalCaptureStateKeyHandler;
import com.sony.imaging.app.lightgraffiti.shooting.keyhandler.LGS1OffEEStateKeyHandler;
import com.sony.imaging.app.lightgraffiti.shooting.keyhandler.LGS1OnEEStateKeyHandler;
import com.sony.imaging.app.lightgraffiti.shooting.keyhandler.LGShootingStateKeyHandler;
import com.sony.imaging.app.lightgraffiti.shooting.layout.LGConfirmReviewLayout;
import com.sony.imaging.app.lightgraffiti.shooting.layout.LGDiscardLayout;
import com.sony.imaging.app.lightgraffiti.shooting.layout.LGDiscardLightTrailDialogueLayout;
import com.sony.imaging.app.lightgraffiti.shooting.layout.LGEEGuideDetailLayout;
import com.sony.imaging.app.lightgraffiti.shooting.layout.LGEEGuideEasyLayout;
import com.sony.imaging.app.lightgraffiti.shooting.layout.LGEasyReviewLayout;
import com.sony.imaging.app.lightgraffiti.shooting.layout.LGNormalCaptureLayout;
import com.sony.imaging.app.lightgraffiti.shooting.layout.LGPageMenuLayout;
import com.sony.imaging.app.lightgraffiti.shooting.layout.LGProgressLayout;
import com.sony.imaging.app.lightgraffiti.shooting.layout.LGS1OffLayout;
import com.sony.imaging.app.lightgraffiti.shooting.layout.LGStableLayout;
import com.sony.imaging.app.lightgraffiti.shooting.state.LGAutoReviewState;
import com.sony.imaging.app.lightgraffiti.shooting.state.LGCautionDisplayState;
import com.sony.imaging.app.lightgraffiti.shooting.state.LGConfirmReviewState;
import com.sony.imaging.app.lightgraffiti.shooting.state.LGDiscardLightTrailDialogueState;
import com.sony.imaging.app.lightgraffiti.shooting.state.LGEEState;
import com.sony.imaging.app.lightgraffiti.shooting.state.LGEasyReviewState;
import com.sony.imaging.app.lightgraffiti.shooting.state.LGFakeS1OffEEState;
import com.sony.imaging.app.lightgraffiti.shooting.state.LGFakeS1OnEEState;
import com.sony.imaging.app.lightgraffiti.shooting.state.LGFocusAdjustmentState;
import com.sony.imaging.app.lightgraffiti.shooting.state.LGForceSettingState;
import com.sony.imaging.app.lightgraffiti.shooting.state.LGLensProblemCautionRelayState;
import com.sony.imaging.app.lightgraffiti.shooting.state.LGMfAssistState;
import com.sony.imaging.app.lightgraffiti.shooting.state.LGNormalCaptureState;
import com.sony.imaging.app.lightgraffiti.shooting.state.LGS1OffEEState;
import com.sony.imaging.app.lightgraffiti.shooting.state.LGS1OnEEState;
import com.sony.imaging.app.lightgraffiti.shooting.state.LGShootingMenuState;
import com.sony.imaging.app.lightgraffiti.shooting.state.LGShootingState;
import com.sony.imaging.app.lightgraffiti.util.LGConstants;
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
            add(BaseApp.APP_SHOOTING, LGShootingState.class);
            Factory.add(LGShootingState.class, ShootingStateFactory.class, ShootingLayoutFactory.class, LGShootingStateKeyHandler.class);
            add(BaseApp.APP_PLAY, LGPlayRoot.class);
            Factory.add(LGPlayRoot.class, PlaybackStateFactory.class, null, null);
            add(BaseApp.CAUTION, CautionFragment.class);
            Factory.add(CautionFragment.class, CautionStateFactory.class, null, null);
            add(BaseApp.EXIT_SCREEN, ExitScreenState.class);
            Factory.add(ExitScreenState.class, null, ExitScreenLayoutFactory.class, null);
            add(BaseApp.FORCE_EXIT_SCREEN, ForceExitScreenState.class);
            Factory.add(ForceExitScreenState.class, null, ForceExitScreenLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class CautionStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("CautionDisplayState", LGCautionDisplayState.class);
            Factory.add(LGCautionDisplayState.class, null, CautionLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class ShootingStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("FORCESETTING", LGForceSettingState.class);
            Factory.add(LGForceSettingState.class, null, null, null);
            add("ExposureModeCheck", ExposureModeCheckState.class);
            Factory.add(ExposureModeCheckState.class, null, null, null);
            add("EE", LGEEState.class);
            Factory.add(LGEEState.class, EEStateFactory.class, null, EEStateKeyHandler.class);
            add("Capture", CaptureState.class);
            Factory.add(CaptureState.class, CaptureStateFactory.class, null, CaptureStateKeyHandler.class);
            add("Development", DevelopmentState.class);
            Factory.add(DevelopmentState.class, DevelopmentStateFactory.class, DevelopmentLayoutFactory.class, DevelopmentStateKeyHandler.class);
            add(AutoReviewState.STATE_NAME, LGAutoReviewState.class);
            Factory.add(LGAutoReviewState.class, null, AutoReviewLayoutFactory.class, LGAutoReviewStateKeyHandler.class);
            add("AutoReviewForLimitedContShooting", AutoReviewForLimitedContShootingState.class);
            Factory.add(AutoReviewForLimitedContShootingState.class, null, AutoReviewForLimitedContShootingLayoutFactory.class, AutoReviewForLimitedContShootingStateKeyHandler.class);
            add("CustomWhiteBalance", CustomWhiteBalanceControllerState.class);
            Factory.add(CustomWhiteBalanceControllerState.class, CustomWhiteBalanceControllerStateFactory.class, null, null);
            if (Environment.isMovieAPISupported()) {
                add("MovieCapture", MovieCaptureState.class);
                Factory.add(MovieCaptureState.class, null, MovieLayoutFactory.class, MovieCaptureStateKeyHandler.class);
                add("MovieRec", MovieRecState.class);
                Factory.add(MovieRecState.class, MovieRecStateFactory.class, MovieLayoutFactory.class, MovieRecStateKeyHandler.class);
                add("MovieSave", MovieSaveState.class);
                Factory.add(MovieSaveState.class, null, MovieSaveLayoutFactory.class, MovieSaveStateKeyHandler.class);
            }
            if (ExecutorCreator.isIntervalRecEnable()) {
                add(IntervalRecCaptureState.INTVLREC_RECORDING, IntervalRecRecordingState.class);
                Factory.add(IntervalRecRecordingState.class, null, IntervalRecRecordingLayoutFactory.class, IntervalRecRecordingStateKeyHandler.class);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class MovieRecStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(ICustomKey.CATEGORY_MENU, MovieMenuState.class);
            Factory.add(MovieMenuState.class, null, MenuLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class MovieLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(StateBase.DEFAULT_LAYOUT, LGStableLayout.class);
            add(DigitalZoomSettingLayout.MENU_ID, DigitalZoomSettingLayout.class);
            add(StateBase.FOCUS_LAYOUT, FocusLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class MovieSaveLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("StableLayout", LGStableLayout.class);
            add("MovieSaveLayout", MovieSaveLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class IntervalRecRecordingLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("IntervalRecRecordingLayout", IntervalRecRecordingLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class EEStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(S1OffEEState.STATE_NAME, LGS1OffEEState.class);
            Factory.add(LGS1OffEEState.class, null, S1OffEELayoutFactory.class, LGS1OffEEStateKeyHandler.class);
            add("S1OnEE", LGS1OnEEState.class);
            Factory.add(LGS1OnEEState.class, null, S1OnEELayoutFactory.class, LGS1OnEEStateKeyHandler.class);
            add("MfAssist", LGMfAssistState.class);
            Factory.add(LGMfAssistState.class, null, MfAssistLayoutFactory.class, LGMfAssistStateKeyHandler.class);
            add("FocusAdjustment", LGFocusAdjustmentState.class);
            Factory.add(LGFocusAdjustmentState.class, null, FocusAdjustmentFactory.class, LGFocusAdjustmentKeyHandler.class);
            add("MovieRecStandby", MovieRecStandbyState.class);
            Factory.add(MovieRecStandbyState.class, null, MovieLayoutFactory.class, MovieRecStandbyStateKeyHandler.class);
            add(ICustomKey.CATEGORY_MENU, LGShootingMenuState.class);
            Factory.add(LGShootingMenuState.class, null, MenuLayoutFactory.class, null);
            add("DiscardDialogue", LGDiscardLightTrailDialogueState.class);
            Factory.add(LGDiscardLightTrailDialogueState.class, null, LGDiscardLightTrailDialogueLayoutFactory.class, null);
            add("EasyReview", LGEasyReviewState.class);
            Factory.add(LGEasyReviewState.class, null, LGEasyReviewLayoutFactory.class, null);
            add("LensProblem", LGLensProblemCautionRelayState.class);
            Factory.add(LGLensProblemCautionRelayState.class, null, null, null);
        }
    }

    /* loaded from: classes.dex */
    public static class LGEasyReviewLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("LGEasyReviewLayout", LGEasyReviewLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class CaptureStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("NormalCapture", LGNormalCaptureState.class);
            Factory.add(LGNormalCaptureState.class, LGNormalCaptureStateFactory.class, NormalCaptureLayoutFactory.class, LGNormalCaptureStateKeyHandler.class);
        }
    }

    /* loaded from: classes.dex */
    public static class NormalCaptureLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(LGNormalCaptureState.ID_LG_LAYOUT_SHOOTING_GUIDE, LGNormalCaptureLayout.class);
            add("LGProcessingLayout", LGProgressLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class LGNormalCaptureStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("LGFakeS1OffEEState", LGFakeS1OffEEState.class);
            Factory.add(LGFakeS1OffEEState.class, null, LGFakeS1OffEELayoutFactory.class, LGFakeS1OffEEStateKeyHandler.class);
            add("LGFakeS1OnEEState", LGFakeS1OnEEState.class);
            Factory.add(LGFakeS1OnEEState.class, null, LGFakeS1OnEELayoutFactory.class, null);
            add("LGConfirmReviewState", LGConfirmReviewState.class);
            Factory.add(LGConfirmReviewState.class, null, LGConfirmReviewLayoutFactory.class, null);
            add("FakeDiscardDialogue", LGDiscardLightTrailDialogueState.class);
            Factory.add(LGDiscardLightTrailDialogueState.class, null, LGDiscardLightTrailDialogueLayoutFactory.class, null);
            add("FakeLensProblem", LGLensProblemCautionRelayState.class);
            Factory.add(LGLensProblemCautionRelayState.class, null, null, null);
        }
    }

    /* loaded from: classes.dex */
    public static class LGConfirmReviewLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("LGConfirmReviewLayout", LGConfirmReviewLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class LGFakeS1OnEELayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("LGFakeS1OnEELayout", LGStableLayout.class);
            add("LGFakeFocusLayout", FocusLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class LGFakeS1OffEELayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("LGFakeS1OffEELayout", LGStableLayout.class);
            add(LGS1OffEEState.LAYOUT_EE_GUIDE_EASY, LGEEGuideEasyLayout.class);
            add(LGConstants.LAYOUT_EE_GUIDE_DETAIL, LGEEGuideDetailLayout.class);
            add("FakeDiscardLayout", LGDiscardLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class IntervalReCaptureLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("IntervalRecCaptureLayout", IntervalRecCaptureLayout.class);
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
            add("cautionLayout", LGCautionLayout.class);
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
            add(LGS1OffEEState.LAYOUT_EE_GUIDE_EASY, LGEEGuideEasyLayout.class);
            add(StateBase.DEFAULT_LAYOUT, LGStableLayout.class);
            add(StateBase.S1OFF_LAYOUT, LGS1OffLayout.class);
            add(StateBase.FOCUS_LAYOUT, FocusLayout.class);
            add(StateBase.GUIDE_LAYOUT, GuideLayout.class);
            add(LGConstants.LAYOUT_EE_GUIDE_DETAIL, LGEEGuideDetailLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class S1OnEELayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(StateBase.DEFAULT_LAYOUT, LGStableLayout.class);
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
            add(PlaySubApp.ID_SINGLE_PB, BrowserSingle.class);
            Factory.add(BrowserSingle.class, PbFunctionCommonFactory.class, BrowserSingleLayoutFactory.class, null);
            add(PlaySubApp.ID_INDEX_PB, BrowserIndex.class);
            Factory.add(BrowserIndex.class, PbFunctionCommonFactory.class, BrowserIndexLayoutFactory.class, null);
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
            add("NORMAL_PB", BrowserSingleLayout.class);
            add("NO_FILE", BrowserSingleNoFileLayout.class);
            add(SingleStateBase.ID_BUFFER_PLAYBACK_LAYOUT, BrowserSingleLayout.class);
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
            if (LGExposureModeController.isUiMovieMainFeature()) {
                add(ExposureModeMenuLayout.MENU_ID, ExposureModeMenuMovieFeatureLayout.class);
                Factory.add(ExposureModeMenuMovieFeatureLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            } else {
                add(ExposureModeMenuLayout.MENU_ID, LGExposureModeMenuLayout.class);
                Factory.add(LGExposureModeMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            }
            add("ID_SCENESELECTIONMENULAYOUT", SceneSelectionMenuLayout.class);
            Factory.add(SceneSelectionMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_EXPOSURECOMPENSATIONMENULAYOUT", ExposureCompensationMenuLayout.class);
            Factory.add(ExposureCompensationMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_MOVIEMODEMENULAYOUT", MovieModeMenuLayout.class);
            Factory.add(MovieModeMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(UnknownItemMenuLayout.MENU_ID, UnknownItemMenuLayout.class);
            Factory.add(UnknownItemMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("LastBastionLayout", LastBastionMenuLayout.class);
            Factory.add(LastBastionMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
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
            add(PageMenuLayout.MENU_ID, LGPageMenuLayout.class);
            Factory.add(LGPageMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(SetMenuLayout.MENU_ID, SetMenuLayout.class);
            Factory.add(SetMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(SpecialScreenMenuLayoutHiddenEE.MENU_ID, SpecialScreenMenuLayoutHiddenEE.class);
            Factory.add(SpecialScreenMenuLayoutHiddenEE.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("ID_LIGHTGRAFITTIPAINTINGDURATIONLAYOUT", LGPaintingDurationLayout.class);
            Factory.add(LGPaintingDurationLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
            add("LGMenuDiscardLightTrailDialogueLayout", LGDiscardLightTrailDialogueLayout.class);
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
            if (LGExposureModeController.isUiMovieMainFeature()) {
                add(ExposureModeMenuLayout.MENU_ID, ExposureModeMenuMovieFeatureLayout.class);
            } else {
                add(ExposureModeMenuLayout.MENU_ID, LGExposureModeMenuLayout.class);
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
            add(PageMenuLayout.MENU_ID, LGPageMenuLayout.class);
            add(SetMenuLayout.MENU_ID, SetMenuLayout.class);
            add(SpecialScreenMenuLayoutHiddenEE.MENU_ID, SpecialScreenMenuLayoutHiddenEE.class);
            add("ID_LIGHTGRAFITTIPAINTINGDURATIONLAYOUT", LGPaintingDurationLayout.class);
            add("ID_LGLAYOUT_DISCARD_DIALOGUE", LGDiscardLightTrailDialogueLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class EachPlaybackStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlayRootContainer.ID_BROWSER, Browser.class);
            Factory.add(Browser.class, EachBrowserFactory.class, null, null);
            add(LGPlayRoot.ID_DELETE_MULTIPLE, DeleteMultiple.class);
            Factory.add(DeleteMultiple.class, DeleteMultipleFactory.class, null, null);
            add(PlayRootContainer.ID_PLAYZOOM, PlayZoom.class);
            Factory.add(PlayZoom.class, PlayZoomFactory.class, null, null);
            add(PlayRootContainer.ID_PSEUDOREC, PseudoRec.class);
            Factory.add(PseudoRec.class, PseudoRecFactory.class, null, null);
            add(PlayRootContainer.ID_PSEUDOREC_ZOOM, PseudoRecZoom.class);
            Factory.add(PseudoRecZoom.class, PseudoRecZoomFactory.class, null, null);
            add(PlayRootContainer.ID_TRANSITING_TO_SHOOTING, TransitionToShooting.class);
        }
    }

    /* loaded from: classes.dex */
    public static class EachBrowserFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlaySubApp.ID_SINGLE_PB, LGBrowserSingle.class);
            Factory.add(LGBrowserSingle.class, PbFunctionCommonFactory.class, EachBrowserSingleLayoutFactory.class, null);
            add(PlaySubApp.ID_INDEX_PB, LGBrowserIndex.class);
            Factory.add(LGBrowserIndex.class, PbFunctionCommonFactory.class, EachBrowserIndexLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class EachBrowserSingleLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("NORMAL_PB", LGBrowserSingleLayout.class);
            add("NO_FILE", BrowserSingleNoFileLayout.class);
            add(SingleStateBase.ID_BUFFER_PLAYBACK_LAYOUT, LGBrowserSingleLayout.class);
            add(SingleStateBase.ID_NO_BUFFER_LAYOUT, BrowserSingleNoBufferLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class EachBrowserIndexLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("NORMAL_PB", LGBrowserIndexLayout.class);
            add("NO_FILE", BrowserIndexNoFileLayout.class);
            add(IndexStateBase.ID_DATA_UNREADY_LAYOUT, BrowserSingleNoBufferLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class DeleteMultipleFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlaySubApp.ID_SINGLE_PB, DeleteMultipleSingle.class);
            Factory.add(DeleteMultipleSingle.class, null, DeleteMultipleSingleLayoutFactory.class, null);
            add(PlaySubApp.ID_INDEX_PB, DeleteMultipleIndex.class);
            Factory.add(DeleteMultipleIndex.class, null, DeleteMultipleIndexLayoutFactory.class, null);
            add(Editor.ID_CONFIRM, DeleteMultipleConfirm.class);
            Factory.add(DeleteMultipleConfirm.class, null, DeleteMultipleConfirmLayoutFactory.class, null);
            add(Editor.ID_EXECUTOR, DeleteMultipleExecutor.class);
            Factory.add(DeleteMultipleExecutor.class, null, DeleteMultipleExecutorLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class DeleteMultipleSingleLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("NORMAL_PB", DeleteMultipleSingleLayout.class);
            add(SingleStateBase.ID_NO_BUFFER_LAYOUT, BrowserSingleNoBufferLayout.class);
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
    public static class DeleteMultipleConfirmLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlayStateWithLayoutBase.ID_DEFAULT_LAYOUT, DeleteMultipleConfirmLayout.class);
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
    public static class LGDiscardLightTrailDialogueLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("ID_LGLAYOUT_DISCARD_DIALOGUE", LGDiscardLightTrailDialogueLayout.class);
        }
    }
}
