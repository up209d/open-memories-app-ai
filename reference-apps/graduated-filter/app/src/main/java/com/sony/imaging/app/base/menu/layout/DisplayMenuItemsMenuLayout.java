package com.sony.imaging.app.base.menu.layout;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.menu.MenuAccessor;
import com.sony.imaging.app.base.menu.MenuDataParcelable;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.layout.SubLcdStableLayout;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.PTag;
import java.util.ArrayList;
import java.util.HashMap;

/* loaded from: classes.dex */
public abstract class DisplayMenuItemsMenuLayout extends BaseMenuLayout {
    protected static final String ITEM_ID_APPLICATION_SETTINGS = "ApplicationSettings";
    protected static final String ITEM_ID_APPLICATION_TOP = "ApplicationTop";
    protected static final String ITEM_ID_EXIT_THE_APPLICATION = "ExitApplication";
    private static final String TAG = "DisplayMenuItemsMenuLayout";
    public static IMenuExecutorConverter sConverter;
    protected static IMenuExecutorConverter sDefaultConverter;
    protected MenuUpdater mUpdater;
    static HashMap<String, Integer> convMenuToExecutor = new HashMap<>();
    static SparseArray<String> convExecutorToMenu = new SparseArray<>();
    private Bundle mBundle = null;
    private MenuDataParcelable mMenuDataSent = null;
    private MenuUpdater mDeviceChangedHandler = null;
    private MenuUpdater mFocusModeHandler = null;
    private MenuUpdater mDigitalZoomHandler = null;
    private MenuUpdater mStreamStatusHandler = null;
    private MenuUpdater mAFMFSWChangedHandler = null;
    private Handler mHandler = new Handler();
    protected BaseMenuService mService = null;
    protected CameraEventListener mCameraListener = null;
    protected CameraNotificationManager mCameraNotifier = CameraNotificationManager.getInstance();
    protected String mCancelItemId = null;

    /* loaded from: classes.dex */
    public interface IMenuExecutorConverter {
        String convExecutorToMenu(int i);

        int convMenuToExecutor(String str);
    }

    /* loaded from: classes.dex */
    class CameraEventListener implements NotificationListener {
        private boolean digitalZoom = false;
        private final String[] TAGS = {CameraNotificationManager.DEVICE_EXTERNAL_FLASH_ONOFF, CameraNotificationManager.DEVICE_INTERNAL_FLASH_ONOFF, CameraNotificationManager.DIGITAL_ZOOM_ONOFF_CHANGED, CameraNotificationManager.DEVICE_LENS_AFMF_SW_CHANGED, CameraNotificationManager.FOCUS_CHANGE, CameraNotificationManager.STREAM_WRITE_START, CameraNotificationManager.STREAM_WRITE_STOP};

        public CameraEventListener() {
            DisplayMenuItemsMenuLayout.this.mDigitalZoomHandler = new MenuUpdater(DisplayMenuItemsMenuLayout.this.getHandler()) { // from class: com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout.CameraEventListener.1
                {
                    DisplayMenuItemsMenuLayout displayMenuItemsMenuLayout = DisplayMenuItemsMenuLayout.this;
                }

                @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout.MenuUpdater, java.lang.Runnable
                public void run() {
                    DisplayMenuItemsMenuLayout.this.onDigitalZoomOnOff(CameraEventListener.this.digitalZoom);
                }
            };
            DisplayMenuItemsMenuLayout.this.mDigitalZoomHandler.setDelayTime(100L);
            DisplayMenuItemsMenuLayout.this.mFocusModeHandler = new MenuUpdater(DisplayMenuItemsMenuLayout.this.getHandler()) { // from class: com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout.CameraEventListener.2
                {
                    DisplayMenuItemsMenuLayout displayMenuItemsMenuLayout = DisplayMenuItemsMenuLayout.this;
                }

                @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout.MenuUpdater, java.lang.Runnable
                public void run() {
                    DisplayMenuItemsMenuLayout.this.onFocusModeChanged();
                }
            };
            DisplayMenuItemsMenuLayout.this.mFocusModeHandler.setDelayTime(100L);
            DisplayMenuItemsMenuLayout.this.mDeviceChangedHandler = new MenuUpdater(DisplayMenuItemsMenuLayout.this.getHandler()) { // from class: com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout.CameraEventListener.3
                {
                    DisplayMenuItemsMenuLayout displayMenuItemsMenuLayout = DisplayMenuItemsMenuLayout.this;
                }

                @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout.MenuUpdater, java.lang.Runnable
                public void run() {
                    DisplayMenuItemsMenuLayout.this.onDeviceStatusChanged();
                }
            };
            DisplayMenuItemsMenuLayout.this.mDeviceChangedHandler.setDelayTime(100L);
            DisplayMenuItemsMenuLayout.this.mStreamStatusHandler = new MenuUpdater(DisplayMenuItemsMenuLayout.this.getHandler()) { // from class: com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout.CameraEventListener.4
                {
                    DisplayMenuItemsMenuLayout displayMenuItemsMenuLayout = DisplayMenuItemsMenuLayout.this;
                }

                @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout.MenuUpdater, java.lang.Runnable
                public void run() {
                    AvailableInfo.update();
                    DisplayMenuItemsMenuLayout.this.onStreamWriterStatusChanged();
                }
            };
            DisplayMenuItemsMenuLayout.this.mStreamStatusHandler.setDelayTime(100L);
            DisplayMenuItemsMenuLayout.this.mAFMFSWChangedHandler = new MenuUpdater(DisplayMenuItemsMenuLayout.this.getHandler()) { // from class: com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout.CameraEventListener.5
                {
                    DisplayMenuItemsMenuLayout displayMenuItemsMenuLayout = DisplayMenuItemsMenuLayout.this;
                }

                @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout.MenuUpdater, java.lang.Runnable
                public void run() {
                    DisplayMenuItemsMenuLayout.this.onAFMFSwitchChanged();
                }
            };
            DisplayMenuItemsMenuLayout.this.mAFMFSWChangedHandler.setDelayTime(100L);
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (CameraNotificationManager.DIGITAL_ZOOM_ONOFF_CHANGED.equals(tag)) {
                this.digitalZoom = ((Boolean) DisplayMenuItemsMenuLayout.this.mCameraNotifier.getValue(tag)).booleanValue();
                DisplayMenuItemsMenuLayout.this.mDigitalZoomHandler.restartMenuUpdater();
                return;
            }
            if (CameraNotificationManager.FOCUS_CHANGE.equals(tag)) {
                DisplayMenuItemsMenuLayout.this.mFocusModeHandler.restartMenuUpdater();
                return;
            }
            if (CameraNotificationManager.STREAM_WRITE_STOP.equals(tag) || CameraNotificationManager.STREAM_WRITE_START.equals(tag)) {
                DisplayMenuItemsMenuLayout.this.mStreamStatusHandler.restartMenuUpdater();
            } else if (CameraNotificationManager.DEVICE_LENS_AFMF_SW_CHANGED.equals(tag)) {
                DisplayMenuItemsMenuLayout.this.mAFMFSWChangedHandler.restartMenuUpdater();
            } else {
                DisplayMenuItemsMenuLayout.this.mDeviceChangedHandler.restartMenuUpdater();
            }
        }
    }

    protected void onStreamWriterStatusChanged() {
    }

    protected void onDigitalZoomOnOff(boolean onoff) {
    }

    protected void onFocusModeChanged() {
    }

    protected void onAFMFSwitchChanged() {
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        if (this.mCameraNotifier != null && this.mCameraListener != null) {
            this.mCameraNotifier.removeNotificationListener(this.mCameraListener);
            this.mCameraListener = null;
        }
        if (this.mDigitalZoomHandler != null) {
            this.mDigitalZoomHandler.cancelMenuUpdater();
            this.mDigitalZoomHandler = null;
        }
        if (this.mFocusModeHandler != null) {
            this.mFocusModeHandler.cancelMenuUpdater();
            this.mFocusModeHandler = null;
        }
        if (this.mDeviceChangedHandler != null) {
            this.mDeviceChangedHandler.cancelMenuUpdater();
            this.mDeviceChangedHandler = null;
        }
        if (this.mStreamStatusHandler != null) {
            this.mStreamStatusHandler.cancelMenuUpdater();
            this.mStreamStatusHandler = null;
        }
        if (this.mAFMFSWChangedHandler != null) {
            this.mAFMFSWChangedHandler.cancelMenuUpdater();
            this.mAFMFSWChangedHandler = null;
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        this.mService = null;
        super.onDestroyView();
    }

    @Deprecated
    protected ArrayList<String> getCameraNotifyTags() {
        return new ArrayList<>();
    }

    protected void updateNotificationListener(ArrayList<String> tags) {
    }

    @Deprecated
    protected void onNotify() {
    }

    protected void onDeviceStatusChanged() {
    }

    @Deprecated
    protected void updateMenuData() {
    }

    @Deprecated
    protected MenuDataParcelable getMenuData() {
        if (this.data == null || this.data.getParcelable(MenuDataParcelable.KEY) == null) {
            return null;
        }
        MenuDataParcelable parcelable = (MenuDataParcelable) this.data.getParcelable(MenuDataParcelable.KEY);
        return parcelable;
    }

    @Deprecated
    protected final Bundle getBundleWithMenuData() {
        return this.mBundle;
    }

    @Deprecated
    protected void setBundleWithMenuData(String menuDataFile, String itemId, String menuLayoutId, String execType, String nextMenuID, MenuAccessor accessor) {
        if (this.mBundle == null) {
            this.mBundle = new Bundle();
        }
        if (this.mMenuDataSent == null) {
            this.mMenuDataSent = new MenuDataParcelable();
        }
        this.mMenuDataSent.setMenuData(menuDataFile, itemId, menuLayoutId, execType, nextMenuID, accessor);
        this.mBundle.putParcelable(MenuDataParcelable.KEY, this.mMenuDataSent);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        MenuDataParcelable parcelable;
        super.onResume();
        if (this.mCameraNotifier != null) {
            if (this.mCameraListener == null) {
                this.mCameraListener = new CameraEventListener();
            }
            this.mCameraNotifier.setNotificationListener(this.mCameraListener);
        }
        if (this.mUpdater == null) {
            this.mUpdater = getMenuUpdater();
        }
        if (this.data != null && (parcelable = (MenuDataParcelable) this.data.getParcelable(MenuDataParcelable.KEY)) != null) {
            this.mService = parcelable.getMenuService();
        }
        if (this.mService == null) {
            this.mService = new BaseMenuService(getActivity());
        }
        if (!isParentItemAvailable()) {
            checkCaution();
        }
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void onReopened() {
        MenuDataParcelable parcelable;
        super.onReopened();
        if (this.data != null && (parcelable = (MenuDataParcelable) this.data.getParcelable(MenuDataParcelable.KEY)) != null) {
            this.mService = parcelable.getMenuService();
        }
        if (!isParentItemAvailable()) {
            checkCaution();
        }
    }

    protected boolean isParentItemAvailable() {
        return true;
    }

    protected MenuAccessor getMenuAccessor() {
        MenuDataParcelable p = (MenuDataParcelable) this.data.getParcelable(MenuDataParcelable.KEY);
        if (p == null) {
            return null;
        }
        MenuAccessor a = p.getMenuAccessor();
        return a;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void checkCaution() {
        String itemid;
        if (this.mService != null && (itemid = this.mService.getMenuItemId()) != null && !this.mService.isMenuItemValid(itemid)) {
            requestCautionTrigger(itemid);
            closeMenuLayout(null);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void requestCautionTrigger(String itemId) {
        Integer intId;
        int[] cautionId = this.mService.getMenuItemCautionId(itemId);
        if (cautionId != null) {
            IController controller = this.mService.getExecClassInstance(itemId);
            int index = controller.getCautionIndex(itemId);
            if (index < 0) {
                if (BaseMenuService.CAUTION_ID_DICTIONARY.containsKey(Integer.valueOf(index)) && (intId = BaseMenuService.CAUTION_ID_DICTIONARY.get(Integer.valueOf(index))) != null) {
                    CautionUtilityClass.getInstance().requestTrigger(intId.intValue());
                    return;
                }
                return;
            }
            if (index >= 0 && index < cautionId.length) {
                CautionUtilityClass.getInstance().requestTrigger(cautionId[index]);
            }
        }
    }

    @Deprecated
    protected String getCurrentValueItemId() {
        String itemId;
        if (this.mService == null) {
            return null;
        }
        MenuDataParcelable parcelable = (MenuDataParcelable) this.data.getParcelable(MenuDataParcelable.KEY);
        if ("back".equals(parcelable.getItemId())) {
            String parentItemId = this.mService.getMenuItemId();
            itemId = this.mService.getCurrentValue(parentItemId);
        } else {
            itemId = this.mService.getCurrentValue(parcelable.getItemId());
        }
        return itemId;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Deprecated
    public void setCancelSetValueItemId(String itemId) {
        this.mCancelItemId = itemId;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Deprecated
    public void cancelSetValue() {
        if (this.mUpdater != null) {
            this.mUpdater.cancelMenuUpdater();
        }
        if (this.mCancelItemId != null && this.mService != null) {
            this.mService.execCurrentMenuItem(this.mCancelItemId, false);
        }
    }

    @Deprecated
    protected ArrayList<String> getMenuItemList() {
        if (this.mService == null) {
            return null;
        }
        return this.mService.getMenuItemList();
    }

    public static void setConverter(IMenuExecutorConverter converter) {
        sConverter = converter;
    }

    static {
        convMenuToExecutor.put("movie", 2);
        convMenuToExecutor.put("photo", 1);
        convMenuToExecutor.put("interval", 4);
        convMenuToExecutor.put("loop", 8);
        convExecutorToMenu.put(2, "movie");
        convExecutorToMenu.put(1, "photo");
        convExecutorToMenu.put(4, "interval");
        convExecutorToMenu.put(8, "loop");
        sDefaultConverter = new IMenuExecutorConverter() { // from class: com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout.1
            @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout.IMenuExecutorConverter
            public int convMenuToExecutor(String menu) {
                return DisplayMenuItemsMenuLayout.convMenuToExecutor.get(menu).intValue();
            }

            @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout.IMenuExecutorConverter
            public String convExecutorToMenu(int executor) {
                return DisplayMenuItemsMenuLayout.convExecutorToMenu.get(executor);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void doItemClickProcessing(String itemid) {
        if (this.mUpdater != null) {
            this.mUpdater.cancelMenuUpdater();
        }
        if (itemid.equals(ITEM_ID_EXIT_THE_APPLICATION)) {
            BaseApp baseApp = (BaseApp) getActivity();
            baseApp.finish();
            return;
        }
        if (this.mService != null) {
            if (!this.mService.isMenuItemValid(itemid)) {
                requestCautionTrigger(itemid);
                return;
            }
            String execType = this.mService.getMenuItemExecType(itemid);
            String nextFragmentID = this.mService.getMenuItemNextMenuID(itemid);
            if (MenuTable.NEXT_LAYOUT.equals(execType) || MenuTable.NEXT_LAYOUT_WITHOUT_SET.equals(execType)) {
                if (MenuTable.NEXT_LAYOUT.equals(execType)) {
                    this.mService.execCurrentMenuItem(itemid, false);
                }
                if (nextFragmentID != null) {
                    PTag.start("Menu open next MenuLayout");
                    openNextMenu(itemid, nextFragmentID);
                    return;
                } else {
                    Log.e(TAG, "Can't open the next MenuLayout");
                    return;
                }
            }
            if (MenuTable.NEXT_STATE.equals(execType)) {
                PTag.start("MenuItem Clicked(NEXT_STATE)");
                openNextState(nextFragmentID, null);
                return;
            }
            if (MenuTable.SET_VALUE.equals(execType) || MenuTable.SET_VALUE_ONLY_CLICK.equals(execType)) {
                PTag.start("MenuItem Clicked(SET_VALUE or SET_VALUE_ONLY_CLICK)");
                this.mService.execCurrentMenuItem(itemid, false);
                postSetValue();
            } else if (MenuTable.SET_REC_MODE.equals(execType)) {
                PTag.start("MenuItem Clicked(SET_REC_MODE)");
                ((AppRoot) getActivity()).setData(SubLcdStableLayout.KEY_START_FROM_TOP, true);
                closeMenuLayout(null);
            } else if (MenuTable.BACK_TRANSITION.equals(execType)) {
                PTag.start("MenuItem Clicked(BACK_TRANSITION)");
                openPreviousMenu();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void postSetValue() {
        closeMenuLayout(null);
    }

    @Deprecated
    protected void onMenuItemClick(String itemid) {
        doItemClickProcessing(itemid);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        return -1;
    }

    protected void onMenuItemSelected(String itemid) {
        if (this.mService != null) {
            String execType = this.mService.getMenuItemExecType(itemid);
            if ((MenuTable.SET_VALUE.equals(execType) || MenuTable.NEXT_LAYOUT.equals(execType)) && this.mService.isMenuItemValid(itemid)) {
                this.mService.execCurrentMenuItem(itemid);
            }
        }
    }

    protected MenuUpdater getMenuUpdater() {
        return new MenuUpdater(this.mHandler);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class MenuUpdater implements Runnable {
        private Handler handler;
        public boolean isDone = true;
        private long delayTime = 400;

        public MenuUpdater(Handler handler) {
            this.handler = handler;
        }

        @Override // java.lang.Runnable
        public void run() {
        }

        public void setDelayTime(long time) {
            this.delayTime = time;
        }

        public long getDelayTime() {
            return this.delayTime;
        }

        public void restartMenuUpdater() {
            if (!this.isDone) {
                this.handler.removeCallbacks(this);
            }
            this.isDone = false;
            this.handler.postDelayed(this, this.delayTime);
        }

        public void finishMenuUpdater() {
            if (!this.isDone) {
                this.handler.removeCallbacks(this);
                run();
            }
        }

        public void cancelMenuUpdater() {
            if (!this.isDone) {
                this.handler.removeCallbacks(this);
                this.isDone = true;
            }
        }
    }
}
