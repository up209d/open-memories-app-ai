package com.sony.imaging.app.photoretouch;

import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.caution.CautionDisplayState;
import com.sony.imaging.app.base.caution.CautionFragment;
import com.sony.imaging.app.base.caution.CautionLayout;
import com.sony.imaging.app.base.common.ExitScreenState;
import com.sony.imaging.app.base.common.ForceExitScreenState;
import com.sony.imaging.app.base.common.layout.ExitScreenLayout;
import com.sony.imaging.app.base.common.layout.ForceExitScreenLayout;
import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.base.playback.base.IndexStateBase;
import com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase;
import com.sony.imaging.app.base.playback.base.PlaySubApp;
import com.sony.imaging.app.base.playback.base.SingleStateBase;
import com.sony.imaging.app.base.playback.base.editor.Editor;
import com.sony.imaging.app.base.playback.browser.BrowserIndex;
import com.sony.imaging.app.base.playback.delete.single.DeleteSingle;
import com.sony.imaging.app.base.playback.delete.single.DeleteSingleConfirm;
import com.sony.imaging.app.base.playback.delete.single.DeleteSingleExecutor;
import com.sony.imaging.app.base.playback.delete.single.DeleteSingleFinalize;
import com.sony.imaging.app.base.playback.delete.single.DeleteSingleTestContents;
import com.sony.imaging.app.base.playback.layout.BrowserIndexNoFileLayout;
import com.sony.imaging.app.base.playback.layout.BrowserSingleNoBufferLayout;
import com.sony.imaging.app.base.playback.layout.BrowserSingleNoFileLayout;
import com.sony.imaging.app.base.playback.layout.DeleteSingleConfirmLayout;
import com.sony.imaging.app.base.playback.layout.DeleteSingleProcessingLayout;
import com.sony.imaging.app.base.playback.layout.EventBlockerLayout;
import com.sony.imaging.app.base.playback.pseudorec.PseudoRec;
import com.sony.imaging.app.base.playback.pseudorec.PseudoRecSingle;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.fw.LayoutFactory;
import com.sony.imaging.app.fw.StateFactory;
import com.sony.imaging.app.photoretouch.menu.PhotoRetouchMenuState;
import com.sony.imaging.app.photoretouch.menu.layout.ConfirmSavingLayout;
import com.sony.imaging.app.photoretouch.menu.layout.PhotoRetouchSubMenuLayout;
import com.sony.imaging.app.photoretouch.playback.PhotoRetouchPlay;
import com.sony.imaging.app.photoretouch.playback.browser.PhotoRetouchBrowser;
import com.sony.imaging.app.photoretouch.playback.browser.PhotoRetouchBrowserSingle;
import com.sony.imaging.app.photoretouch.playback.layout.AlertMessageLayout;
import com.sony.imaging.app.photoretouch.playback.layout.NoFilePresentLayout;
import com.sony.imaging.app.photoretouch.playback.layout.NoMediaLayout;
import com.sony.imaging.app.photoretouch.playback.layout.PhotoRetouchAppStartMessageLayout;
import com.sony.imaging.app.photoretouch.playback.layout.PhotoRetouchBrowserSingleLayout;
import com.sony.imaging.app.photoretouch.playback.layout.PhotoRetouchIndexLayout;
import com.sony.imaging.app.photoretouch.playback.layout.SavingLayout;
import com.sony.imaging.app.photoretouch.playback.layout.UnsupportedImageMessageLayout;
import com.sony.imaging.app.photoretouch.playback.layout.brightness.BrightnessControlLayout;
import com.sony.imaging.app.photoretouch.playback.layout.contrast.ContrastControlLayout;
import com.sony.imaging.app.photoretouch.playback.layout.framing.FramingLayout;
import com.sony.imaging.app.photoretouch.playback.layout.horizontaladjustment.HorizontalAdjustmentLayout;
import com.sony.imaging.app.photoretouch.playback.layout.manualframing.ManualFramingLayout;
import com.sony.imaging.app.photoretouch.playback.layout.manualframing.ManualStartUpMessageLayout;
import com.sony.imaging.app.photoretouch.playback.layout.resize.ResizeLayout;
import com.sony.imaging.app.photoretouch.playback.layout.saturation.SaturationControlLayout;
import com.sony.imaging.app.photoretouch.playback.layout.softskin.NoFaceMessageLayout;
import com.sony.imaging.app.photoretouch.playback.layout.softskin.SoftSkinLayout;

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
            add(BaseApp.APP_PLAY, PhotoRetouchPlay.class);
            Factory.add(PhotoRetouchPlay.class, PlaybackStateFactory.class, null, null);
            add(BaseApp.CAUTION, CautionFragment.class);
            Factory.add(CautionFragment.class, CautionStateFactory.class, null, null);
            add(BaseApp.EXIT_SCREEN, ExitScreenState.class);
            Factory.add(ExitScreenState.class, null, ExitScreenLayoutFactory.class, null);
            add(BaseApp.FORCE_EXIT_SCREEN, ForceExitScreenState.class);
            Factory.add(ForceExitScreenState.class, null, ForceExitScreenLayoutFactory.class, null);
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
    public static class CautionStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("CautionDisplayState", CautionDisplayState.class);
            Factory.add(CautionDisplayState.class, null, CautionLayoutFactory.class, null);
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
    public static class PlaybackStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlayRootContainer.ID_BROWSER, PhotoRetouchBrowser.class);
            Factory.add(PhotoRetouchBrowser.class, BrowserFactory.class, null, null);
            add(PlayRootContainer.ID_PSEUDOREC, PseudoRec.class);
            Factory.add(PseudoRec.class, PseudoRecFactory.class, null, null);
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
    public static class PseudoRecSingleLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(SingleStateBase.ID_BUFFER_PLAYBACK_LAYOUT, NoMediaLayout.class);
            add(SingleStateBase.ID_NO_BUFFER_LAYOUT, NoMediaLayout.class);
            Factory.add(NoMediaLayout.class, null, NoMediaLayoutLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class NoMediaLayoutLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(Constant.ID_MESSAGEALERT, AlertMessageLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class BrowserFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlaySubApp.ID_SINGLE_PB, PhotoRetouchBrowserSingle.class);
            Factory.add(PhotoRetouchBrowserSingle.class, PbFunctionCommonFactory.class, BrowserSingleLayoutFactory.class, null);
            add(PlaySubApp.ID_INDEX_PB, BrowserIndex.class);
            Factory.add(BrowserIndex.class, PbFunctionCommonFactory.class, BrowserIndexLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class BrowserSingleLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("NORMAL_PB", PhotoRetouchBrowserSingleLayout.class);
            add("NO_FILE", BrowserSingleNoFileLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class PbFunctionCommonFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlayRootContainer.ID_DELETE_SINGLE, DeleteSingle.class);
            Factory.add(DeleteSingle.class, DeleteSingleFactory.class, null, null);
            add(ICustomKey.CATEGORY_MENU, PhotoRetouchMenuState.class);
            Factory.add(PhotoRetouchMenuState.class, null, MenuLayoutFactory.class, null);
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
    public static class DeleteSingleFinalizeLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlayStateWithLayoutBase.ID_DEFAULT_LAYOUT, EventBlockerLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class BrowserIndexLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add("NORMAL_PB", PhotoRetouchIndexLayout.class);
            Factory.add(PhotoRetouchIndexLayout.class, null, PhotoRetouchIndexLayoutFactory.class, null);
            add("NO_FILE", BrowserIndexNoFileLayout.class);
            add("NO_FILE", NoFilePresentLayout.class);
            Factory.add(NoFilePresentLayout.class, null, PhotoRetouchNoFilePresentLayoutFactory.class, null);
            add(IndexStateBase.ID_DATA_UNREADY_LAYOUT, BrowserSingleNoBufferLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class PhotoRetouchNoFilePresentLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(Constant.ID_MESSAGEAPPSTART, PhotoRetouchAppStartMessageLayout.class);
            add(Constant.ID_MESSAGEALERT, AlertMessageLayout.class);
            Factory.add(AlertMessageLayout.class, null, AlertMessageLayoutFactory.class, null);
            add(Constant.ID_MESSAGEUNSUPPORTEDFILE, UnsupportedImageMessageLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class PhotoRetouchIndexLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(Constant.ID_MESSAGEAPPSTART, PhotoRetouchAppStartMessageLayout.class);
            add(Constant.ID_MESSAGEALERT, AlertMessageLayout.class);
            Factory.add(AlertMessageLayout.class, null, AlertMessageLayoutFactory.class, null);
            add(Constant.ID_MESSAGEUNSUPPORTEDFILE, UnsupportedImageMessageLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class AlertMessageLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(Constant.ID_RESIZELAYOUT, ResizeLayout.class);
            add(Constant.ID_HORIZONTALADJUSTMENTLAYOUT, HorizontalAdjustmentLayout.class);
            add(Constant.ID_SOFTSKINLAYOUT, SoftSkinLayout.class);
            add(Constant.ID_FRAMINGLAYOUT, FramingLayout.class);
            add(Constant.ID_SATURATIONCONTROL, SaturationControlLayout.class);
            add(Constant.ID_BRIGHTNESSCONTROL, BrightnessControlLayout.class);
            add(Constant.ID_CONTRASTCONTROL, ContrastControlLayout.class);
            add(Constant.ID_MANUALFRAMING, ManualFramingLayout.class);
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
    public static class ResizeConfirmLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlayStateWithLayoutBase.ID_DEFAULT_LAYOUT, DeleteSingleConfirmLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class ResizeExecutorLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(PlayStateWithLayoutBase.ID_DEFAULT_LAYOUT, DeleteSingleProcessingLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class MenuLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(Constant.ID_PHOTORETOUCHSUBMENULAYOUT, PhotoRetouchSubMenuLayout.class);
            Factory.add(PhotoRetouchSubMenuLayout.class, null, OneAnotherMenuLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class OneAnotherMenuLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(Constant.ID_RESIZELAYOUT, ResizeLayout.class);
            Factory.add(ResizeLayout.class, null, ToolKitCommonLayoutFactory.class, null);
            add(Constant.ID_HORIZONTALADJUSTMENTLAYOUT, HorizontalAdjustmentLayout.class);
            Factory.add(HorizontalAdjustmentLayout.class, null, ToolKitCommonLayoutFactory.class, null);
            add(Constant.ID_SOFTSKINLAYOUT, SoftSkinLayout.class);
            Factory.add(SoftSkinLayout.class, null, ToolKitCommonLayoutFactory.class, null);
            add(Constant.ID_FRAMINGLAYOUT, FramingLayout.class);
            Factory.add(FramingLayout.class, null, FramingLayoutFactory.class, null);
            add(Constant.ID_SATURATIONCONTROL, SaturationControlLayout.class);
            Factory.add(SaturationControlLayout.class, null, ToolKitCommonLayoutFactory.class, null);
            add(Constant.ID_BRIGHTNESSCONTROL, BrightnessControlLayout.class);
            Factory.add(BrightnessControlLayout.class, null, ToolKitCommonLayoutFactory.class, null);
            add(Constant.ID_CONTRASTCONTROL, ContrastControlLayout.class);
            Factory.add(ContrastControlLayout.class, null, ToolKitCommonLayoutFactory.class, null);
            add(Constant.ID_MANUALFRAMING, ManualFramingLayout.class);
            add(Constant.ID_MESSAGEALERT, AlertMessageLayout.class);
            add(Constant.ID_MESSAGENOFACE, NoFaceMessageLayout.class);
            add(Constant.ID_SAVINGLAYOUT, SavingLayout.class);
            add(Constant.ID_MANUALSTARTUPMESSAGE, ManualStartUpMessageLayout.class);
            add(Constant.ID_CONFIRMSAVINGLAYOUT, ConfirmSavingLayout.class);
            Factory.add(ConfirmSavingLayout.class, null, ConfirmSavingLayoutFactory.class, null);
        }

        /* loaded from: classes.dex */
        public static class ConfirmSavingLayoutFactory extends LayoutFactory {
            @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
            public void init() {
                add(Constant.ID_MESSAGEALERT, AlertMessageLayout.class);
                add(Constant.ID_SAVINGLAYOUT, SavingLayout.class);
            }
        }

        /* loaded from: classes.dex */
        public static class GuideLayoutFactory extends LayoutFactory {
            @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
            public void init() {
                add(Constant.ID_PHOTORETOUCHSUBMENULAYOUT, PhotoRetouchSubMenuLayout.class);
                add(Constant.ID_RESIZELAYOUT, ResizeLayout.class);
                add(Constant.ID_HORIZONTALADJUSTMENTLAYOUT, HorizontalAdjustmentLayout.class);
                add(Constant.ID_SOFTSKINLAYOUT, SoftSkinLayout.class);
                add(Constant.ID_FRAMINGLAYOUT, FramingLayout.class);
                add(Constant.ID_SATURATIONCONTROL, SaturationControlLayout.class);
                add(Constant.ID_BRIGHTNESSCONTROL, BrightnessControlLayout.class);
                add(Constant.ID_CONTRASTCONTROL, ContrastControlLayout.class);
                add(Constant.ID_MANUALFRAMING, ManualFramingLayout.class);
            }
        }

        /* loaded from: classes.dex */
        public static class FramingLayoutFactory extends LayoutFactory {
            @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
            public void init() {
                add(Constant.ID_MANUALFRAMING, ManualFramingLayout.class);
                Factory.add(ManualFramingLayout.class, null, ManualFramingLayoutFactory.class, null);
                add(Constant.ID_PHOTORETOUCHSUBMENULAYOUT, PhotoRetouchSubMenuLayout.class);
                add(Constant.ID_SAVINGLAYOUT, SavingLayout.class);
                Factory.add(SavingLayout.class, null, SavingLayoutFactory.class, null);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class ManualFramingLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(Constant.ID_FRAMINGLAYOUT, FramingLayout.class);
            add(Constant.ID_SAVINGLAYOUT, SavingLayout.class);
            add(Constant.ID_PHOTORETOUCHSUBMENULAYOUT, PhotoRetouchSubMenuLayout.class);
            add(Constant.ID_MANUALSTARTUPMESSAGE, ManualStartUpMessageLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class ToolKitCommonLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(Constant.ID_SAVINGLAYOUT, SavingLayout.class);
            Factory.add(SavingLayout.class, null, SavingLayoutFactory.class, null);
            add(Constant.ID_PHOTORETOUCHSUBMENULAYOUT, PhotoRetouchSubMenuLayout.class);
            add(Constant.ID_MESSAGENOFACE, NoFaceMessageLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class SavingLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(Constant.ID_PHOTORETOUCHSUBMENULAYOUT, PhotoRetouchSubMenuLayout.class);
            add(Constant.ID_MESSAGEALERT, AlertMessageLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class ConfirmationMessageLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(Constant.ID_PHOTORETOUCHSUBMENULAYOUT, PhotoRetouchSubMenuLayout.class);
            add(Constant.ID_MESSAGEALERT, AlertMessageLayout.class);
        }
    }
}
