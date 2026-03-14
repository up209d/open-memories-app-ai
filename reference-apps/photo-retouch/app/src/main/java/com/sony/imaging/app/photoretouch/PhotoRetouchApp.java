package com.sony.imaging.app.photoretouch;

import android.content.Intent;
import android.os.Looper;
import android.os.MessageQueue;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.fw.BootFactor;
import com.sony.imaging.app.photoretouch.common.ImageEditor;
import com.sony.imaging.app.photoretouch.menu.layout.PhotoRetouchSubMenuLayout;
import com.sony.imaging.app.util.AppInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.scalar.hardware.MemoryMapConfig;
import com.sony.scalar.sysutil.ScalarProperties;
import com.sony.scalar.sysutil.didep.Gpelibrary;

/* loaded from: classes.dex */
public class PhotoRetouchApp extends BaseApp {
    public static final String[] PULLING_BACK_KEYS_FOR_EASY_EDITING = {AppInfo.KEY_S2, AppInfo.KEY_S1_1, AppInfo.KEY_S1_2, AppInfo.KEY_MOVREC, AppInfo.KEY_USB_CONNECT};
    private String mBootApp = BaseApp.APP_PLAY;

    public PhotoRetouchApp() {
        doAbortInCaseUncautghtException = true;
        new Factory();
        setBootLogo(R.drawable.p_16_dd_parts_pr_launchericon, R.string.STRID_FUNC_EZEDIT);
    }

    @Override // com.sony.imaging.app.base.BaseApp
    public String getStartApp() {
        return this.mBootApp;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.fw.AppRoot, android.app.Activity
    public void onResume() {
        super.onResume();
        setAPOState();
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() { // from class: com.sony.imaging.app.photoretouch.PhotoRetouchApp.1
            @Override // android.os.MessageQueue.IdleHandler
            public boolean queueIdle() {
                MenuTable.getInstance().createDefaultMenuTree(PhotoRetouchApp.this.getApplicationContext());
                return false;
            }
        });
        AppInfo.notifyAppInfo(this, getPackageName(), getClass().getName(), AppInfo.LARGE_CATEGORY_PB, AppInfo.SMALL_CATEGORY_SINGLE, PULLING_BACK_KEYS_FOR_EASY_EDITING, RESUME_KEYS_FOR_EDITING);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot
    public void onBoot(BootFactor factor) {
        super.onBoot(factor);
        String version = ScalarProperties.getString("version.platform");
        int pfMajorVersion = Integer.parseInt(version.substring(0, version.indexOf(".")));
        if (pfMajorVersion >= 2) {
            MemoryMapConfig.setAllocationPolicy(1);
        }
        BackUpUtil.getInstance().setDefaultValues(R.xml.base_default_value);
        ImageEditor.init(this);
        PhotoRetouchSubMenuLayout.resetItemId();
        Gpelibrary.changeFrameBufferPixel(Gpelibrary.GS_FRAMEBUFFER_TYPE.ABGR8888);
        switch (factor.bootFactor) {
            case 0:
                PhotoRetouchKikiLog.photoRetouchStartLog();
                break;
        }
        if (AppInfo.KEY_PLAY_APO.equals(factor.bootKey)) {
            this.mBootApp = BaseApp.APP_PLAY;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.BaseApp, com.sony.imaging.app.fw.AppRoot
    public void onShutdown() {
        Gpelibrary.changeFrameBufferPixel(Gpelibrary.GS_FRAMEBUFFER_TYPE.RGBA4444);
        ImageEditor.term();
        super.onShutdown();
    }

    private void setAPOState() {
        Intent intent = new Intent();
        intent.setAction("com.android.server.DAConnectionManagerService.apo");
        intent.putExtra("apo_info", "APO/NO");
        sendBroadcast(intent);
    }

    @Override // com.sony.imaging.app.base.BaseApp
    public int getSupportingRecMode() {
        return 0;
    }
}
