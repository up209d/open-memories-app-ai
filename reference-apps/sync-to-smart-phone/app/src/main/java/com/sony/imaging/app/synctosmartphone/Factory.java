package com.sony.imaging.app.synctosmartphone;

import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.caution.CautionDisplayState;
import com.sony.imaging.app.base.caution.CautionFragment;
import com.sony.imaging.app.base.caution.CautionLayout;
import com.sony.imaging.app.base.common.ExitScreenState;
import com.sony.imaging.app.base.common.ForceExitScreenState;
import com.sony.imaging.app.base.common.layout.ExitScreenLayout;
import com.sony.imaging.app.base.common.layout.ForceExitScreenLayout;
import com.sony.imaging.app.base.menu.layout.SetMenuLayout;
import com.sony.imaging.app.fw.LayoutFactory;
import com.sony.imaging.app.fw.StateFactory;
import com.sony.imaging.app.synctosmartphone.commonUtil.ConstantsSync;
import com.sony.imaging.app.synctosmartphone.layout.BootLayout;
import com.sony.imaging.app.synctosmartphone.layout.ConnectingLayoutSync;
import com.sony.imaging.app.synctosmartphone.layout.DialogLayout;
import com.sony.imaging.app.synctosmartphone.layout.MainLayoutSync;
import com.sony.imaging.app.synctosmartphone.layout.MenuLayoutSync;
import com.sony.imaging.app.synctosmartphone.layout.PowerOffLayout;
import com.sony.imaging.app.synctosmartphone.layout.RegisteredLayout;
import com.sony.imaging.app.synctosmartphone.layout.RegistratingLayoutSync;
import com.sony.imaging.app.synctosmartphone.layout.RegistrationSettingLayoutSync;
import com.sony.imaging.app.synctosmartphone.layout.RegistrationWaitingLayoutSync;
import com.sony.imaging.app.synctosmartphone.layout.SetMenuLayoutSync;
import com.sony.imaging.app.synctosmartphone.layout.TransferringGuideLayoutSync;
import com.sony.imaging.app.synctosmartphone.layout.TransferringLayoutSync;
import com.sony.imaging.app.synctosmartphone.layout.TransferringStatusLayoutSync;
import com.sony.imaging.app.synctosmartphone.state.BootState;
import com.sony.imaging.app.synctosmartphone.state.ConnectingStateSync;
import com.sony.imaging.app.synctosmartphone.state.DialogState;
import com.sony.imaging.app.synctosmartphone.state.MainStateSync;
import com.sony.imaging.app.synctosmartphone.state.MenuStateSync;
import com.sony.imaging.app.synctosmartphone.state.PowerOffState;
import com.sony.imaging.app.synctosmartphone.state.RegisteredState;
import com.sony.imaging.app.synctosmartphone.state.RegistratingStateSync;
import com.sony.imaging.app.synctosmartphone.state.RegistrationSettingStateSync;
import com.sony.imaging.app.synctosmartphone.state.RegistrationWaitingStateSync;
import com.sony.imaging.app.synctosmartphone.state.TransferringGuideStateSync;
import com.sony.imaging.app.synctosmartphone.state.TransferringStateSync;
import com.sony.imaging.app.synctosmartphone.state.TransferringStatusStateSync;

/* loaded from: classes.dex */
public class Factory extends com.sony.imaging.app.fw.Factory {
    private static final String TAG = "Factory";

    @Override // com.sony.imaging.app.fw.Factory
    protected Class<? extends StateFactory> getTop() {
        return RootStateFactory.class;
    }

    /* loaded from: classes.dex */
    public static class RootStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(BaseApp.CAUTION, CautionFragment.class);
            Factory.add(CautionFragment.class, CautionStateFactory.class, null, null);
            add(BaseApp.EXIT_SCREEN, ExitScreenState.class);
            Factory.add(ExitScreenState.class, null, ExitScreenLayoutFactory.class, null);
            add(BaseApp.FORCE_EXIT_SCREEN, ForceExitScreenState.class);
            Factory.add(ForceExitScreenState.class, null, ForceExitScreenLayoutFactory.class, null);
            add(ConstantsSync.BOOT_STATE, BootState.class);
            Factory.add(BootState.class, BootStateFactory.class, BootLayoutFactory.class, null);
            add(ConstantsSync.POWEROFF_STATE, PowerOffState.class);
            Factory.add(PowerOffState.class, PowerOffStateFactory.class, PowerOffLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class BootStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(ConstantsSync.MAIN_STATE, MainStateSync.class);
            Factory.add(MainStateSync.class, null, MainLayoutFactory.class, null);
            add(ConstantsSync.MENU_STATE, MenuStateSync.class);
            Factory.add(MenuStateSync.class, null, MenuLayoutFactory.class, null);
            add(ConstantsSync.REGISTRATION_SETTING_STATE, RegistrationSettingStateSync.class);
            Factory.add(RegistrationSettingStateSync.class, null, RegistrationSettingLayoutFactory.class, null);
            add(ConstantsSync.REGISTRATING_STATE, RegistratingStateSync.class);
            Factory.add(RegistratingStateSync.class, null, RegistratingLayoutFactory.class, null);
            add(ConstantsSync.REGISTRATION_WAITING_STATE, RegistrationWaitingStateSync.class);
            Factory.add(RegistrationWaitingStateSync.class, null, RegistrationWaitingLayoutFactory.class, null);
            add(ConstantsSync.TRANSFERRING_GUIDE_STATE, TransferringGuideStateSync.class);
            Factory.add(TransferringGuideStateSync.class, null, TransferringGuideLayoutFactory.class, null);
            add(ConstantsSync.TRANSFERRING_STATUS_STATE, TransferringStatusStateSync.class);
            Factory.add(TransferringStatusStateSync.class, null, TransferringStatusLayoutFactory.class, null);
            add(ConstantsSync.CONNECTING_STATE, ConnectingStateSync.class);
            Factory.add(ConnectingStateSync.class, null, ConnectingLayoutFactory.class, null);
            add(ConstantsSync.TRANSFERRING_STATE, TransferringStateSync.class);
            Factory.add(TransferringStateSync.class, null, TransferringLayoutFactory.class, null);
            add(ConstantsSync.DIALOG_STATE, DialogState.class);
            Factory.add(DialogState.class, null, DialogLayoutFactory.class, null);
            add(ConstantsSync.REGISTERED_STATE, RegisteredState.class);
            Factory.add(RegisteredState.class, null, RegisteredLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class PowerOffStateFactory extends StateFactory {
        @Override // com.sony.imaging.app.fw.StateFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(ConstantsSync.REGISTRATING_STATE, RegistratingStateSync.class);
            Factory.add(RegistratingStateSync.class, null, RegistratingLayoutFactory.class, null);
            add(ConstantsSync.REGISTRATION_WAITING_STATE, RegistrationWaitingStateSync.class);
            Factory.add(RegistrationWaitingStateSync.class, null, RegistrationWaitingLayoutFactory.class, null);
            add(ConstantsSync.TRANSFERRING_GUIDE_STATE, TransferringGuideStateSync.class);
            Factory.add(TransferringGuideStateSync.class, null, TransferringGuideLayoutFactory.class, null);
            add(ConstantsSync.TRANSFERRING_STATUS_STATE, TransferringStatusStateSync.class);
            Factory.add(TransferringStatusStateSync.class, null, TransferringStatusLayoutFactory.class, null);
            add(ConstantsSync.CONNECTING_STATE, ConnectingStateSync.class);
            Factory.add(ConnectingStateSync.class, null, ConnectingLayoutFactory.class, null);
            add(ConstantsSync.TRANSFERRING_STATE, TransferringStateSync.class);
            Factory.add(TransferringStateSync.class, null, TransferringLayoutFactory.class, null);
            add(ConstantsSync.DIALOG_STATE, DialogState.class);
            Factory.add(DialogState.class, null, DialogLayoutFactory.class, null);
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
    public static class BootLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(ConstantsSync.BOOT_LAYOUT, BootLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class PowerOffLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(ConstantsSync.POWEROFF_LAYOUT, PowerOffLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class MainLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(ConstantsSync.MAIN_LAYOUT, MainLayoutSync.class);
        }
    }

    /* loaded from: classes.dex */
    public static class RegistrationSettingLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(ConstantsSync.REGISTRATION_SETTING_LAYOUT, RegistrationSettingLayoutSync.class);
        }
    }

    /* loaded from: classes.dex */
    public static class RegistratingLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(ConstantsSync.REGISTRATING_LAYOUT, RegistratingLayoutSync.class);
        }
    }

    /* loaded from: classes.dex */
    public static class RegistrationWaitingLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(ConstantsSync.REGISTRATION_WAITING_LAYOUT, RegistrationWaitingLayoutSync.class);
        }
    }

    /* loaded from: classes.dex */
    public static class TransferringGuideLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(ConstantsSync.TRANSFERRING_GUIDE_LAYOUT, TransferringGuideLayoutSync.class);
        }
    }

    /* loaded from: classes.dex */
    public static class TransferringStatusLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(ConstantsSync.TRANSFERRING_STATUS_LAYOUT, TransferringStatusLayoutSync.class);
        }
    }

    /* loaded from: classes.dex */
    public static class ConnectingLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(ConstantsSync.CONNECTING_LAYOUT, ConnectingLayoutSync.class);
        }
    }

    /* loaded from: classes.dex */
    public static class TransferringLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(ConstantsSync.TRANSFERRING_LAYOUT, TransferringLayoutSync.class);
        }
    }

    /* loaded from: classes.dex */
    public static class DialogLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(ConstantsSync.DIALOG_LAYOUT, DialogLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class RegisteredLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(ConstantsSync.REGISTERED_LAYOUT, RegisteredLayout.class);
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
            add("cautionLayout", CautionLayout.class);
        }
    }

    /* loaded from: classes.dex */
    public static class MenuLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(ConstantsSync.MAIN_LAYOUT, MainLayoutSync.class);
            Factory.add(MainLayoutSync.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(ConstantsSync.MENU_LAYOUT, MenuLayoutSync.class);
            Factory.add(MenuLayoutSync.class, null, OneAnotherMenuLayoutFactory.class, null);
            add(SetMenuLayout.MENU_ID, SetMenuLayoutSync.class);
            Factory.add(SetMenuLayoutSync.class, null, OneAnotherMenuLayoutFactory.class, null);
        }
    }

    /* loaded from: classes.dex */
    public static class OneAnotherMenuLayoutFactory extends LayoutFactory {
        @Override // com.sony.imaging.app.fw.LayoutFactory, com.sony.imaging.app.fw.BaseFactory
        public void init() {
            add(ConstantsSync.MAIN_LAYOUT, MainLayoutSync.class);
            add(ConstantsSync.MENU_LAYOUT, MenuLayoutSync.class);
            add(SetMenuLayout.MENU_ID, SetMenuLayoutSync.class);
        }
    }
}
