package com.sony.imaging.app.base.menu.layout;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.HistoryItem;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.menu.MenuDataParcelable;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.menu.MenuStateClosedListener;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.base.shooting.camera.AELController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.movie.MovieShootingExecutor;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import java.util.ArrayList;
import java.util.Locale;

/* loaded from: classes.dex */
public class BaseMenuLayout extends Layout implements MenuStateClosedListener {
    private static final String BUNDLE_KEY_CODE = "keyCode";
    private static final int GUIDE_RESOURCES = 3;
    protected static final int INVISIBLE_ALPHA = 128;
    private static final String TAG = "BaseMenuLayout";
    protected static final int VISIBLE_ALPHA = 255;
    private static ArrayList<Object> mGuideResources = new ArrayList<>(3);
    private View mFocused;
    private View mFunctionGuideView;
    private TextView mFunctionNameTextView;
    private TextView mGuideTextView;
    private TextView mInvalidTextView;
    private boolean mbMounted;
    private MenuLayoutListener mListener = null;
    private MediaNotificationManager mMediaNotifier = null;
    private MediaMountEventListener mMediaListener = null;

    protected String getMenuLayoutID() {
        return "ID_" + getClass().getName().substring(getClass().getName().lastIndexOf(".") + 1).toUpperCase(Locale.US);
    }

    public void setMenuLayoutListener(MenuLayoutListener listener) {
        this.mListener = listener;
        if (this.mListener != null && this.mListener.getState() != null) {
            this.mListener.getState().addMenuStateClosedListener(this);
        }
    }

    public MenuLayoutListener getMenuLayoutListener() {
        return this.mListener;
    }

    protected int getBeepPattern() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver
    protected String getKeyConvCategory() {
        return ICustomKey.CATEGORY_MENU;
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mFunctionGuideView = inflater.inflate(R.layout.function_guide, (ViewGroup) null);
        this.mFunctionNameTextView = (TextView) this.mFunctionGuideView.findViewById(R.id.function_name);
        this.mGuideTextView = (TextView) this.mFunctionGuideView.findViewById(R.id.guide);
        this.mInvalidTextView = (TextView) this.mFunctionGuideView.findViewById(R.id.invalid);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mFunctionNameTextView = null;
        this.mGuideTextView = null;
        this.mInvalidTextView = null;
        this.mFunctionGuideView = null;
        this.mFocused = null;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AELController cntl;
        super.onResume();
        if (isAELCancel() && (cntl = AELController.getInstance()) != null) {
            cntl.cancelAELock();
        }
        this.mMediaNotifier = MediaNotificationManager.getInstance();
        this.mbMounted = this.mMediaNotifier.isMounted();
        if (this.mMediaListener == null) {
            this.mMediaListener = new MediaMountEventListener();
        }
        this.mMediaNotifier.setNotificationListener(this.mMediaListener);
    }

    protected boolean isAELCancel() {
        return true;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        View view = getView();
        if (view != null && (view instanceof ViewGroup)) {
            ViewGroup layoutView = (ViewGroup) view;
            layoutView.removeView(this.mFunctionGuideView);
        }
        if (this.mMediaNotifier != null) {
            this.mMediaNotifier.removeNotificationListener(this.mMediaListener);
        }
        this.mMediaListener = null;
        this.mMediaNotifier = null;
        super.onPause();
    }

    /* loaded from: classes.dex */
    class MediaMountEventListener implements NotificationListener {
        private final String[] tags = {MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE, MediaNotificationManager.TAG_MEDIA_REMAINING_CHANGE};

        MediaMountEventListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            Log.d(BaseMenuLayout.TAG, tag);
            if (MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE.equals(tag)) {
                if (BaseMenuLayout.this.mMediaNotifier != null && BaseMenuLayout.this.mMediaNotifier.isMounted() != BaseMenuLayout.this.mbMounted) {
                    BaseMenuLayout.this.closeMenuLayout(null);
                    return;
                }
                return;
            }
            if (MediaNotificationManager.TAG_MEDIA_REMAINING_CHANGE.equals(tag)) {
                BaseMenuLayout.this.onMediaRemainingChanged();
            }
        }
    }

    protected void onMediaRemainingChanged() {
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedRightKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedDownKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedLeftKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedUpKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedCenterKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        BaseMenuService service = new BaseMenuService(getActivity());
        openNextMenu(service.getMenuItemId(), FnMenuLayout.MENU_ID, false);
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        closeMenuLayout(null);
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUmRemoteRecKey() {
        closeMenuLayout(null);
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRRecKey() {
        closeMenuLayout(null);
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAFMFKey() {
        closeMenuLayout(null);
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfHoldCustomKey() {
        closeMenuLayout(null);
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAfMfHoldCustomKey() {
        closeMenuLayout(null);
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfToggleCustomKey() {
        closeMenuLayout(null);
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        boolean isMovieRecording = false;
        if (Environment.isMovieAPISupported()) {
            isMovieRecording = 2 == ExecutorCreator.getInstance().getRecordingMode() && MovieShootingExecutor.isMovieRecording();
        }
        if (isMovieRecording) {
            return 1;
        }
        closeMenuLayout(null);
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayIndexKey() {
        closeMenuLayout(null);
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRShutterKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIR2SecKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        closeMenuLayout(null);
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedS1Key() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedS2Key() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedModeDial() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightUpKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightDownKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftUpKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftDownKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        closeMenuLayout(null);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedFocusModeDial() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedIrisDial() {
        closeMenuLayout(null);
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int movedAfMfSlideKey() {
        closeMenuLayout(null);
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pressedZoomLeverToTele() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedZoomLeverFromTele() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pressedZoomLeverToWide() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedZoomLeverFromWide() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAELHoldCustomKey() {
        closeMenuLayout(null);
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELHoldCustomKey() {
        closeMenuLayout(null);
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELToggleCustomKey() {
        closeMenuLayout(null);
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedShootingModeKey() {
        Bundle bundle = new Bundle();
        bundle.putInt("keyCode", AppRoot.USER_KEYCODE.SHOOTING_MODE);
        openNextState("", bundle);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedModePKey() {
        closeMenuLayout(null);
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedModeAKey() {
        closeMenuLayout(null);
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedModeSKey() {
        closeMenuLayout(null);
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedModeMKey() {
        closeMenuLayout(null);
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteFuncKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        return turnedMainDialPrev();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        return turnedMainDialNext();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedFuncRingPrev() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedFuncRingNext() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMfAssistCustomKey() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void openMenuLayout(String menulayoutid, Bundle bundle) {
        BaseMenuLayout layout = (BaseMenuLayout) getLayout(menulayoutid);
        if (layout != null) {
            layout.setMenuLayoutListener(this.mListener);
        }
        openLayout(menulayoutid, bundle);
        closeMenuLayout(bundle);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void openNextMenu(String itemId, String layoutId) {
        openNextMenu(itemId, layoutId, true, null);
    }

    protected void openNextMenu(String itemId, String layoutId, boolean history) {
        openNextMenu(itemId, layoutId, history, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void openNextMenu(String itemId, String layoutId, boolean history, Bundle bundle) {
        Bundle passedData;
        MenuDataParcelable passedParcelable;
        MenuDataParcelable parcelable = (MenuDataParcelable) this.data.getParcelable(MenuDataParcelable.KEY);
        BaseMenuService myService = parcelable.getMenuService();
        if (myService == null) {
            myService = new BaseMenuService(getActivity());
        }
        String myItemId = myService.getMenuItemId();
        String myLayoutId = getMenuLayoutID();
        if ((!myLayoutId.equals(layoutId) || !myItemId.equals(itemId)) && itemId != null && itemId.length() > 0) {
            BaseMenuService passedService = new BaseMenuService(myService);
            if (!history) {
                while (!passedService.isEmptyMenuHistory()) {
                    passedService.popMenuHistory();
                }
            }
            boolean updateMySelf = myLayoutId.equals(layoutId);
            if (history) {
                passedService.pushMenuHistory(new HistoryItem(myItemId, myLayoutId));
            }
            passedService.setMenuItemId(itemId);
            if (updateMySelf) {
                if (bundle != null) {
                    this.data.putAll(bundle);
                }
                passedData = this.data;
                passedParcelable = parcelable;
            } else {
                passedData = new Bundle();
                if (bundle != null) {
                    passedData.putAll(bundle);
                }
                passedParcelable = new MenuDataParcelable();
            }
            passedParcelable.setMenuData(parcelable.getMenuDataFile(), itemId, myLayoutId, passedService.getMenuItemExecType(itemId), layoutId, passedService);
            passedService.setMenuItemId(myItemId);
            passedData.putParcelable(MenuDataParcelable.KEY, passedParcelable);
            if (updateMySelf) {
                updateLayout();
                return;
            }
            BaseMenuLayout layout = (BaseMenuLayout) getLayout(layoutId);
            if (layout.isDisplayedAlone()) {
                MenuState state = this.mListener != null ? this.mListener.getState() : null;
                if (state != null) {
                    state.closeMenuLayouts();
                } else {
                    closeMenuLayout(passedData);
                }
            }
            if (layout != null) {
                layout.setMenuLayoutListener(getMenuLayoutListener());
            }
            openLayout(layoutId, passedData);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void openPreviousMenu() {
        MenuDataParcelable parcelable = (MenuDataParcelable) this.data.getParcelable(MenuDataParcelable.KEY);
        BaseMenuService service = new BaseMenuService(parcelable.getMenuService());
        HistoryItem item = service.popMenuHistory();
        if (item == null) {
            closeMenuLayout(null);
            return;
        }
        String nextLayoutId = item.layoutId;
        String myLayoutId = getMenuLayoutID();
        service.setMenuItemId(item.itemId);
        parcelable.setMenuData(parcelable.getMenuDataFile(), "back", myLayoutId, service.getMenuItemExecType(item.itemId), nextLayoutId, service);
        this.data.putParcelable(MenuDataParcelable.KEY, parcelable);
        if (myLayoutId.equals(nextLayoutId)) {
            updateLayout();
        } else {
            openMenuLayout(nextLayoutId, this.data);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void openRootMenu() {
        MenuDataParcelable parcelable = (MenuDataParcelable) this.data.getParcelable(MenuDataParcelable.KEY);
        BaseMenuService service = new BaseMenuService(getActivity());
        String itemId = service.getMenuItemId();
        String layoutId = service.getMenuItemNextMenuID(itemId);
        parcelable.setMenuData(parcelable.getMenuDataFile(), itemId, getMenuLayoutID(), service.getMenuItemExecType(itemId), layoutId, service);
        this.data.putParcelable(MenuDataParcelable.KEY, parcelable);
        openMenuLayout(layoutId, this.data);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void openNextState(String state, Bundle a_bundle) {
        Bundle bundle = a_bundle;
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putString(MenuTable.NEXT_STATE, state);
        closeMenuLayout(bundle);
    }

    public void closeMenuLayout(Bundle bundle) {
        closeLayout();
        this.mListener.onClosed(bundle);
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        if (this.mListener != null && this.mListener.getState() != null) {
            this.mListener.getState().removeMenuStateClosedListener(this);
        }
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.base.menu.MenuStateClosedListener
    public void onStateClosed(Bundle bundle) {
        closeLayout();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int scanCode = event.getScanCode();
        boolean handle = false;
        switch (scanCode) {
            case AppRoot.USER_KEYCODE.UP /* 103 */:
            case AppRoot.USER_KEYCODE.LEFT /* 105 */:
            case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
            case AppRoot.USER_KEYCODE.SK1 /* 229 */:
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
            case AppRoot.USER_KEYCODE.MENU /* 514 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
            case AppRoot.USER_KEYCODE.RIGHT_UP /* 591 */:
            case AppRoot.USER_KEYCODE.RIGHT_DOWN /* 592 */:
            case AppRoot.USER_KEYCODE.LEFT_UP /* 593 */:
            case AppRoot.USER_KEYCODE.LEFT_DOWN /* 594 */:
            case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
            case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                if (this.mFunctionGuideView.isShown()) {
                    if (scanCode == 232 || scanCode == 514 || scanCode == 229) {
                        ViewGroup layoutView = (ViewGroup) getView();
                        layoutView.removeView(this.mFunctionGuideView);
                    }
                    handle = true;
                    break;
                }
                break;
        }
        if (handle) {
            return 1;
        }
        return super.onKeyDown(keyCode, event);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isFunctionGuideShown() {
        return this.mFunctionGuideView.isShown();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedGuideFuncKey() {
        View view = getView();
        if (view != null && (view instanceof ViewGroup)) {
            ViewGroup layoutView = (ViewGroup) view;
            if (!this.mFunctionGuideView.isShown()) {
                mGuideResources.clear();
                setFunctionGuideResources(mGuideResources);
                String funcName = null;
                String guideText = null;
                Boolean valid = true;
                int size = mGuideResources.size();
                if (size >= 2) {
                    funcName = (String) mGuideResources.get(0);
                    guideText = (String) mGuideResources.get(1);
                    if (size >= 3 && (valid = (Boolean) mGuideResources.get(2)) == null) {
                        valid = true;
                    }
                }
                if (funcName != null && guideText != null) {
                    this.mFunctionNameTextView.setText(funcName);
                    this.mGuideTextView.setText(guideText);
                    this.mInvalidTextView.setVisibility(valid.booleanValue() ? 8 : 0);
                    layoutView.addView(this.mFunctionGuideView);
                    this.mFocused = layoutView.getFocusedChild();
                    layoutView.requestChildFocus(this.mFunctionGuideView, this.mFocused);
                }
            } else {
                layoutView.requestChildFocus(this.mFocused, this.mFunctionGuideView);
                layoutView.removeView(this.mFunctionGuideView);
                this.mFocused = null;
            }
            return 1;
        }
        return -1;
    }

    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
    }

    protected View getFunctionGuideView() {
        return this.mFunctionGuideView;
    }

    protected boolean isDisplayedAlone() {
        return true;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSettingFuncCustomKey(IKeyFunction keyFunction) {
        int[] cautionId;
        Integer intId;
        String nextItemId = keyFunction.getItemIdForMenu();
        if (nextItemId != null) {
            BaseMenuService service = new BaseMenuService(getActivity());
            String nextLayoutId = service.getMenuItemCustomStartLayoutID(nextItemId);
            String layoutId = getMenuLayoutID();
            boolean isMovieRecording = false;
            if (Environment.isMovieAPISupported()) {
                isMovieRecording = 2 == ExecutorCreator.getInstance().getRecordingMode() && MovieShootingExecutor.isMovieRecording();
            }
            MenuDataParcelable p = this.data != null ? (MenuDataParcelable) this.data.getParcelable(MenuDataParcelable.KEY) : null;
            String itemId = p != null ? p.getItemId() : null;
            if (nextItemId.equals(itemId) && nextLayoutId.equals(layoutId)) {
                openPreviousMenu();
                return 1;
            }
            if (keyFunction.isValid()) {
                if (!isMovieRecording || !CustomizableFunction.ExposureMode.equals(keyFunction)) {
                    openNextMenu(nextItemId, nextLayoutId, false);
                    return 1;
                }
                return 1;
            }
            if (!isMovieRecording && (cautionId = service.getMenuItemCautionId(nextItemId)) != null) {
                IController controller = service.getExecClassInstance(nextItemId);
                int index = controller.getCautionIndex(nextItemId);
                if (index < 0) {
                    if (BaseMenuService.CAUTION_ID_DICTIONARY.containsKey(Integer.valueOf(index)) && (intId = BaseMenuService.CAUTION_ID_DICTIONARY.get(Integer.valueOf(index))) != null) {
                        CautionUtilityClass.getInstance().requestTrigger(intId.intValue());
                        return 1;
                    }
                    return 1;
                }
                if (index >= 0 && index < cautionId.length) {
                    CautionUtilityClass.getInstance().requestTrigger(cautionId[index]);
                    return 1;
                }
                return 1;
            }
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        closeMenuLayout(null);
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        closeMenuLayout(null);
        return 0;
    }
}
