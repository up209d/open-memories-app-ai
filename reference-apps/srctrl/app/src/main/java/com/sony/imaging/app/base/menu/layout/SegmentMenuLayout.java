package com.sony.imaging.app.base.menu.layout;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.common.widget.AirPlaneSubLCDIcon;
import com.sony.imaging.app.base.common.widget.GpsSubLCDIcon;
import com.sony.imaging.app.base.common.widget.MediaSubLCDIcon;
import com.sony.imaging.app.base.common.widget.SubLcdTextView;
import com.sony.imaging.app.base.common.widget.WifiSubLCDIcon;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.widget.SubLcdMode;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.PTag;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class SegmentMenuLayout extends DisplayMenuItemsMenuLayout {
    private static final String EXIT_TO_WIFI = "ExitToWifi";
    public static final String LOG_AUTO_TRIGGER = "AutoTrigger timeout";
    public static final String LOG_DO_ITEM_CLICK = "doItemClick ";
    public static final String LOG_ITEM_SELECTED = "Menu item selected : ";
    public static final String LOG_ON_REOPENED = "onReopened ";
    public static final String LOG_UNKNOWN_PARETN_ITEMID = "unknown parentItemID";
    public static final String LOG_WAIT_RECMODE_CHANGE = "wait until recmode : ";
    protected AirPlaneSubLCDIcon mAirplane;
    protected GpsSubLCDIcon mGps;
    protected List<SegmentItem> mItems;
    protected MediaSubLCDIcon mMedia;
    protected SubLcdMode mMode;
    protected SubLcdTextView mTextInfo;
    protected WifiSubLCDIcon mWiFi;
    private static final String TAG = SegmentMenuLayout.class.getSimpleName();
    public static final String MENU_ID = "ID_" + SegmentMenuLayout.class.getSimpleName().toUpperCase();
    protected String mParentItemId = "";
    protected String mCurrentItemId = "";
    protected int mSelection = -1;
    protected boolean mRecModeChangeRequested = false;
    protected Runnable mAutoTriggerRunnable = new Runnable() { // from class: com.sony.imaging.app.base.menu.layout.SegmentMenuLayout.1
        @Override // java.lang.Runnable
        public void run() {
            Log.i(SegmentMenuLayout.TAG, SegmentMenuLayout.LOG_AUTO_TRIGGER);
            SegmentMenuLayout.this.doItemClickProcessing(SegmentMenuLayout.this.mCurrentItemId);
        }
    };

    /* loaded from: classes.dex */
    public interface OnItemChangedListener {
        void onItemChanged(String str);
    }

    /* loaded from: classes.dex */
    public class SegmentItem {
        public String itemId;
        public boolean valid;

        public SegmentItem() {
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View createdView = obtainViewFromPool(getLayoutID());
        this.mTextInfo = (SubLcdTextView) createdView.findViewById(R.id.text_info);
        this.mMode = (SubLcdMode) createdView.findViewById(R.id.current_mode);
        this.mWiFi = (WifiSubLCDIcon) createdView.findViewById(R.id.icon_wifi);
        this.mGps = (GpsSubLCDIcon) createdView.findViewById(R.id.icon_gps);
        this.mAirplane = (AirPlaneSubLCDIcon) createdView.findViewById(R.id.icon_airplane);
        this.mMedia = (MediaSubLCDIcon) createdView.findViewById(R.id.icon_media);
        this.mItems = new ArrayList();
        return createdView;
    }

    protected int getLayoutID() {
        return R.layout.menu_segment;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        this.mTextInfo = null;
        this.mMode = null;
        this.mItems = null;
        this.mWiFi = null;
        this.mGps = null;
        this.mAirplane = null;
        this.mMedia = null;
        this.mItems = null;
        this.mSelection = -1;
        super.onDestroyView();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.mParentItemId = this.mService.getMenuItemId();
        String selection = this.data.getString(BaseMenuLayout.BUNDLE_KEY_SELECTED_ITEM);
        if (selection != null) {
            this.mCurrentItemId = selection;
        } else {
            this.mCurrentItemId = this.mService.getCurrentValue(this.mParentItemId);
        }
        Log.i(TAG, "onResume " + this.mParentItemId + ", " + this.mCurrentItemId);
        updateAdapter(this.mCurrentItemId);
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.fw.Layout
    public void onReopened() {
        super.onReopened();
        this.mParentItemId = this.mService.getMenuItemId();
        this.mCurrentItemId = this.mService.getCurrentValue(this.mParentItemId);
        Log.i(TAG, LOG_ON_REOPENED + this.mParentItemId + ", " + this.mCurrentItemId);
        updateAdapter(this.mCurrentItemId);
        updateSegmentView();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        this.mRecModeChangeRequested = false;
        getHandler().removeCallbacks(this.mAutoTriggerRunnable);
        CameraNotificationManager.getInstance().hold(0, null);
        super.onPause();
    }

    protected void updateAdapter(String selectedItemId) {
        this.mItems.clear();
        ArrayList<String> menuItemIds = this.mService.getSupportedItemList();
        int selection = 0;
        int c = menuItemIds.size();
        for (int i = 0; i < c; i++) {
            String itemId = menuItemIds.get(i);
            boolean isDisplayed = this.mService.getBoolean(itemId, "DisplayedInMenu", true);
            if (isDisplayed) {
                SegmentItem item = new SegmentItem();
                item.itemId = itemId;
                if (itemId.equals(selectedItemId)) {
                    selection = i;
                }
                item.valid = this.mService.isMenuItemValid(itemId);
                this.mItems.add(item);
            }
        }
        onItemSelected(selection);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class SetRecordingModeCallback implements CameraEx.OpenCallback {
        protected String mItemId;

        SetRecordingModeCallback(String itemid) {
            this.mItemId = itemid;
        }

        public void onReopened(CameraEx arg0) {
            StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
            builder.replace(0, builder.length(), SegmentMenuLayout.LOG_ON_REOPENED).append(this.mItemId).append(" : ").append(SegmentMenuLayout.this.mCurrentItemId);
            Log.d(SegmentMenuLayout.TAG, builder.toString());
            StringBuilderThreadLocal.releaseScratchBuilder(builder);
            int state = MediaNotificationManager.getInstance().getMediaState();
            int type = MediaNotificationManager.getInstance().getInsertedMediaType();
            String[] m = ExecutorCreator.getInstance().getCautionMedia(state, type);
            CautionUtilityClass.getInstance().setMedia(m);
            if (this.mItemId.equals(SegmentMenuLayout.this.mCurrentItemId)) {
                SegmentMenuLayout.this.doItemClickProcessing(this.mItemId);
                CameraNotificationManager.getInstance().hold(0, null);
            } else {
                if (SegmentMenuLayout.this.mRecModeChangeRequested && SegmentMenuLayout.this.mService != null && SegmentMenuLayout.this.mCurrentItemId != null) {
                    List<String> recMode = SegmentMenuLayout.this.mService.getMenuItemRecMode(SegmentMenuLayout.this.mCurrentItemId);
                    if (recMode != null) {
                        SegmentMenuLayout.this.doItemClickProcessing(SegmentMenuLayout.this.mCurrentItemId);
                        return;
                    } else {
                        CameraNotificationManager.getInstance().hold(0, null);
                        return;
                    }
                }
                CameraNotificationManager.getInstance().hold(0, null);
            }
        }
    }

    protected CameraEx.OpenCallback getRecoModeChangedCallback(String itemid) {
        return new SetRecordingModeCallback(itemid);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void doItemClickProcessing(String itemid) {
        boolean traceTimetag = 1 <= PTag.getSystemLogLevel();
        long start = SystemClock.uptimeMillis();
        if (traceTimetag) {
            StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
            builder.replace(0, builder.length(), LOG_DO_ITEM_CLICK).append(itemid);
            PTag.startTimeTag(builder.toString(), 1);
            StringBuilderThreadLocal.releaseScratchBuilder(builder);
        }
        if (!itemid.equals("ExitApplication") && this.mService != null && this.mService.isMenuItemValid(itemid)) {
            this.mRecModeChangeRequested = false;
            List<String> recMode = this.mService.getMenuItemRecMode(itemid);
            if (recMode != null) {
                ExecutorCreator ec = ExecutorCreator.getInstance();
                DisplayMenuItemsMenuLayout.IMenuExecutorConverter converter = sConverter != null ? sConverter : sDefaultConverter;
                String current = converter.convExecutorToMenu(ec.getRecordingMode());
                if (!recMode.contains(current)) {
                    if (101 != ec.getPrepareStatusWithoutSync()) {
                        StringBuilder builder2 = StringBuilderThreadLocal.getScratchBuilder();
                        builder2.replace(0, builder2.length(), LOG_WAIT_RECMODE_CHANGE).append(itemid);
                        Log.d(TAG, builder2.toString());
                        StringBuilderThreadLocal.releaseScratchBuilder(builder2);
                        this.mRecModeChangeRequested = true;
                        if (traceTimetag) {
                            StringBuilder builder3 = StringBuilderThreadLocal.getScratchBuilder();
                            builder3.replace(0, builder3.length(), LOG_DO_ITEM_CLICK).append(itemid).append(" : ").append(SystemClock.uptimeMillis() - start);
                            PTag.endTimeTag(builder3.toString(), 1);
                            StringBuilderThreadLocal.releaseScratchBuilder(builder3);
                            return;
                        }
                        return;
                    }
                    ArrayList<String> list = new ArrayList<>();
                    list.add(CameraNotificationManager.REC_MODE_CHANGING);
                    CameraNotificationManager.getInstance().hold(2, list);
                    ec.setRecordingMode(converter.convMenuToExecutor(recMode.get(0)), getRecoModeChangedCallback(itemid));
                    if (traceTimetag) {
                        StringBuilder builder4 = StringBuilderThreadLocal.getScratchBuilder();
                        builder4.replace(0, builder4.length(), LOG_DO_ITEM_CLICK).append(itemid).append(" : ").append(SystemClock.uptimeMillis() - start);
                        PTag.endTimeTag(builder4.toString(), 1);
                        StringBuilderThreadLocal.releaseScratchBuilder(builder4);
                        return;
                    }
                    return;
                }
                if (101 != ec.getPrepareStatusWithoutSync()) {
                    Log.i(TAG, "doItemClick return because recmode is changing");
                    return;
                }
            }
        }
        super.doItemClickProcessing(itemid);
        if (traceTimetag) {
            StringBuilder builder5 = StringBuilderThreadLocal.getScratchBuilder();
            builder5.replace(0, builder5.length(), LOG_DO_ITEM_CLICK).append(itemid).append(" : ").append(SystemClock.uptimeMillis() - start);
            PTag.endTimeTag(builder5.toString(), 1);
            StringBuilderThreadLocal.releaseScratchBuilder(builder5);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        boolean enable = this.mService.getBoolean(this.mCurrentItemId, MenuTable.IS_EXIT_TO_WIFI, false);
        if (enable && isPushedKey(AppRoot.USER_KEYCODE.RIGHT)) {
            openNextState(EXIT_TO_WIFI, null);
            return 1;
        }
        int position = this.mSelection - 1;
        if (position < 0) {
            position = this.mItems.size() - 1;
        }
        onItemSelected(position);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        boolean enable = this.mService.getBoolean(this.mCurrentItemId, MenuTable.IS_EXIT_TO_WIFI, false);
        if (enable && isPushedKey(AppRoot.USER_KEYCODE.LEFT)) {
            openNextState(EXIT_TO_WIFI, null);
            return 1;
        }
        int position = this.mSelection + 1;
        if (this.mItems.size() <= position) {
            position = 0;
        }
        onItemSelected(position);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        return pushedCenterKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRRecKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        Log.i(TAG, "pushedCenterKey");
        Integer timeout = this.mService.getInteger(this.mCurrentItemId, MenuTable.AUTO_TRIGGER_TIMEOUT);
        doItemClickProcessing(this.mCurrentItemId);
        return timeout != null ? -1 : 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRTcResetKey() {
        return -1;
    }

    public void onItemSelected(int position) {
        this.mSelection = position;
        this.mCurrentItemId = this.mItems.get(position).itemId;
        StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
        builder.replace(0, builder.length(), LOG_ITEM_SELECTED).append(this.mCurrentItemId);
        PTag.setTimeTag(builder.toString(), 1);
        StringBuilderThreadLocal.releaseScratchBuilder(builder);
        Integer timeout = this.mService.getInteger(this.mCurrentItemId, MenuTable.AUTO_TRIGGER_TIMEOUT);
        getHandler().removeCallbacks(this.mAutoTriggerRunnable);
        if (timeout != null) {
            if (timeout.intValue() <= 0) {
                getHandler().postAtTime(this.mAutoTriggerRunnable, 5L);
            } else {
                getHandler().postDelayed(this.mAutoTriggerRunnable, timeout.intValue());
            }
        }
        updateSegmentView();
    }

    /* loaded from: classes.dex */
    protected class MediaMountEventListener implements NotificationListener {
        private final String[] tags = {MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE, MediaNotificationManager.TAG_MEDIA_REMAINING_CHANGE};

        protected MediaMountEventListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            Log.d(SegmentMenuLayout.TAG, tag);
            if (MediaNotificationManager.TAG_MEDIA_REMAINING_CHANGE.equals(tag)) {
                SegmentMenuLayout.this.onMediaRemainingChanged();
            }
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected NotificationListener getMediaNotificationListener() {
        return new MediaMountEventListener();
    }

    protected void updateSegmentView() {
        Log.i(TAG, "updateSegmentView");
        if (this.mTextInfo != null) {
            String title = (String) this.mService.getMenuItemText(this.mCurrentItemId);
            Log.i(TAG, "updateSegmentView " + this.mCurrentItemId + " : " + title);
            if (title != null) {
                this.mTextInfo.setText(title);
                this.mTextInfo.setVisibility(0);
            } else {
                this.mTextInfo.setVisibility(4);
            }
        }
        String wifi = this.mService.getString(this.mCurrentItemId, MenuTable.WIFI);
        if (this.mWiFi != null && wifi != null) {
            if (wifi.equals("normal")) {
                this.mAirplane.setVisibility(0);
                if (this.mWiFi != null) {
                    this.mWiFi.setVisibility(0);
                }
            } else if (wifi.equals("live")) {
                this.mAirplane.setVisibility(0);
                if (this.mWiFi != null) {
                    this.mWiFi.setVisibility(4);
                }
            } else if (wifi.equals("off")) {
                this.mAirplane.setVisibility(4);
                if (this.mWiFi != null) {
                    this.mWiFi.setVisibility(4);
                }
            }
        }
        boolean gps = this.mService.getBoolean(this.mCurrentItemId, MenuTable.GPS);
        if (this.mGps != null) {
            if (gps) {
                this.mGps.setVisibility(0);
            } else {
                this.mGps.setVisibility(4);
            }
        }
        boolean media = this.mService.getBoolean(this.mCurrentItemId, MenuTable.MEDIA);
        if (this.mMedia != null) {
            if (media) {
                this.mMedia.setVisibility(0);
            } else {
                this.mMedia.setVisibility(4);
            }
        }
        updateRecmode();
    }

    public void updateRecmode() {
        if (this.mMode != null) {
            List<String> recModes = this.mService.getMenuItemRecMode(this.mCurrentItemId);
            if (recModes != null) {
                List<SubLcdMode.MODE> modes = new ArrayList<>();
                DisplayMenuItemsMenuLayout.IMenuExecutorConverter converter = sConverter != null ? sConverter : sDefaultConverter;
                for (String recMode : recModes) {
                    int executorMode = converter.convMenuToExecutor(recMode);
                    SubLcdMode.MODE mode = SubLcdMode.convFromRecMode(executorMode);
                    if (!SubLcdMode.MODE.UNKNOWN.equals(mode)) {
                        modes.add(mode);
                    }
                }
                Log.i(TAG, "set Rec Mode");
                this.mMode.setMode(modes);
                return;
            }
            Log.i(TAG, "set Rec Mode");
            this.mMode.setMode(null);
        }
    }

    protected String getCurrentItemId() {
        return this.mCurrentItemId;
    }

    public boolean isPushedKey(int scanCode) {
        KeyStatus key = ScalarInput.getKeyStatus(scanCode);
        if (key.status != 1) {
            return false;
        }
        return true;
    }
}
