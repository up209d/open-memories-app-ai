package com.sony.imaging.app.synctosmartphone.layout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.menu.layout.PageMenuLayout;
import com.sony.imaging.app.base.shooting.widget.AppNameConstantView;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.synctosmartphone.R;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class MenuLayoutSync extends PageMenuLayout {
    public static final String BOOLEAN_FINISH_MENU_STATE = "boolean_end_menu";
    private static final String TAG = MenuLayoutSync.class.getSimpleName();
    protected View mCurrentView;
    protected AppNameConstantView mScreenTitleView;
    private boolean mbMounted;
    private MediaNotificationManager mMediaNotifier = null;
    private NotificationListener mMediaListener = null;

    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (this.mCurrentView == null) {
            this.mCurrentView = (ViewGroup) obtainViewFromPool(getLayoutID());
        }
        createView();
        this.mPageListView.setVisibility(4);
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout
    protected int getLayoutID() {
        return R.layout.menu_layout_panel;
    }

    private void createView() {
        this.mScreenTitleView = (AppNameConstantView) this.mCurrentView.findViewById(R.id.menu_screen_title);
    }

    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.mScreenTitleView.setVisibility(0);
        this.mMediaNotifier = MediaNotificationManager.getInstance();
        this.mbMounted = this.mMediaNotifier.isMounted();
        if (this.mMediaListener == null) {
            this.mMediaListener = getMediaNotificationListener();
        }
        if (this.mMediaListener != null) {
            this.mMediaNotifier.setNotificationListener(this.mMediaListener);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        if (this.mMediaNotifier != null && this.mMediaListener != null) {
            this.mMediaNotifier.removeNotificationListener(this.mMediaListener);
        }
        this.mMediaListener = null;
        this.mMediaNotifier = null;
    }

    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        deInitializeView();
        super.onDestroyView();
    }

    private void deInitializeView() {
        this.mCurrentView = null;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        Log.d(TAG, "pushedMovieRecKey");
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAFMFKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCustom1Key() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRRecKey() {
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUmRemoteRecKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfHoldCustomKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAfMfHoldCustomKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfToggleCustomKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayIndexKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedIrisDial() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int movedAfMfSlideKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAELHoldCustomKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELHoldCustomKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELToggleCustomKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedModePKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedModeAKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedModeSKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedModeMKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSettingFuncCustomKey(IKeyFunction keyFunction) {
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected NotificationListener getMediaNotificationListener() {
        return new MediaMountEventListener();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class MediaMountEventListener implements NotificationListener {
        private final String[] tags = {MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE, MediaNotificationManager.TAG_MEDIA_REMAINING_CHANGE};

        MediaMountEventListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            Log.d(MenuLayoutSync.TAG, tag);
            if (MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE.equals(tag)) {
                if (MenuLayoutSync.this.mMediaNotifier == null || MenuLayoutSync.this.mMediaNotifier.isMounted() == MenuLayoutSync.this.mbMounted) {
                }
            } else if (MediaNotificationManager.TAG_MEDIA_REMAINING_CHANGE.equals(tag)) {
                MenuLayoutSync.this.onMediaRemainingChanged();
            }
        }
    }
}
