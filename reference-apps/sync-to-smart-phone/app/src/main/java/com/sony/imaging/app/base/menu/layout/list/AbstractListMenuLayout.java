package com.sony.imaging.app.base.menu.layout.list;

import android.app.Instrumentation;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.menu.layout.list.AbstractListMenuAdapter;
import com.sony.imaging.app.base.menu.layout.list.MenuListView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;
import java.util.ArrayList;

/* loaded from: classes.dex */
public abstract class AbstractListMenuLayout extends Layout implements AbstractListMenuAdapter.ListMenuSelectedListener, MenuListView.OverScrollListener {
    private static final int DELAY_CLICK_RE_ENABLED = 1000;
    private static final int DELAY_OVER_SCROLL_RE_ENABLED = 500;
    private static final String TAG = AbstractListMenuLayout.class.getSimpleName();
    private MenuListView menuListView;
    private Handler handler = new Handler();
    private GuideOpenRunnable guideOpenRunnable = new GuideOpenRunnable();
    private int fakeSelected = 0;
    private int lastFakeSelected = 0;
    private boolean isinitialSelection = false;
    private boolean isKeyPushed = false;
    private boolean isSelectionFromTouch = false;
    private boolean overScrolling = false;
    private boolean ignoreClick = false;
    private boolean isSetGlobalListener = false;
    private MenuActionListener menuActionListener = null;
    private ViewTreeObserver.OnGlobalLayoutListener grobalLayoutlistener = new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout.1
        @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
        public void onGlobalLayout() {
            Log.d(AbstractListMenuLayout.TAG, "Default selector setting in onGlobalLayout.");
            AbstractListMenuLayout.this.isinitialSelection = true;
            AbstractListMenuLayout.this.setFakeSelected(AbstractListMenuLayout.this.getInitialSelectedPosition());
            AbstractListMenuLayout.this.refreshSelection(AbstractListMenuLayout.this.getInitialSelectedPosition(), 0);
            AbstractListMenuLayout.this.getView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
            AbstractListMenuLayout.this.isSetGlobalListener = false;
            Log.d(AbstractListMenuLayout.TAG, "thread " + Thread.currentThread());
        }
    };

    /* loaded from: classes.dex */
    public interface MenuActionListener {
        void onGuideClosed();

        void onRequestGuideOpen(int i, int i2);

        void onRequestSelectorOpen(DefaultListMenuItem defaultListMenuItem, int i, int i2);

        void onSelectorClosed(int i);

        void onValueSelected(int i, int i2);
    }

    protected abstract int getDividerHeight();

    protected abstract int getGuideOpenDelay();

    protected abstract int getInitialSelectedPosition();

    protected abstract int getListviewId();

    protected abstract int getMainLayoutId();

    protected abstract int getMaxNumOfDisplayedItems();

    protected abstract ArrayList<DefaultListMenuItem> getMenuItems();

    protected abstract int getRowHeight();

    protected abstract int getTitleBarStringId();

    protected abstract int getTitleBarTextViewId();

    protected abstract boolean isInfiniteScrollByTouchEnabled();

    protected abstract boolean isInfiniteScrollEnabled();

    protected abstract int onKeyDownEx(int i, int i2, KeyEvent keyEvent);

    protected abstract int onKeyUpEx(int i, int i2, KeyEvent keyEvent);

    protected abstract void setInitialSoftKeyArea();

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = obtainViewFromPool(getMainLayoutId());
        this.menuListView = (MenuListView) view.findViewById(getListviewId());
        return view;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        setKeyBeepPattern(getResumeKeyBeepPattern());
        setAdapterOnListview();
        setInitialSoftKeyArea();
        setTitleBar();
        setGlobalLayoutListener();
        super.onResume();
        setEssentialListviewParameters();
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        Log.d(TAG, "isSetGlobalListener: " + Boolean.toString(this.isSetGlobalListener));
        if (this.isSetGlobalListener) {
            getView().getViewTreeObserver().removeGlobalOnLayoutListener(this.grobalLayoutlistener);
            this.isSetGlobalListener = false;
        }
        this.menuActionListener = null;
        stopGuideOpener();
        super.onPause();
    }

    protected void setGlobalLayoutListener() {
        getView().getViewTreeObserver().addOnGlobalLayoutListener(this.grobalLayoutlistener);
        this.isSetGlobalListener = true;
    }

    public void registerMenuActionListener(MenuActionListener listener) {
        this.menuActionListener = listener;
    }

    protected final int getResumeKeyBeepPattern() {
        return -1;
    }

    protected int getDelayOverScrollReEnabled() {
        return 500;
    }

    protected final ArrayList<DefaultListMenuItem> getItemsToBeListed(DefaultListMenuItem... items) {
        ArrayList<DefaultListMenuItem> itemsList = new ArrayList<>();
        if (items.length == 0) {
            return null;
        }
        for (DefaultListMenuItem item : items) {
            itemsList.add(item);
        }
        return itemsList;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void setAdapter(AbstractListMenuAdapter adapter) {
        this.menuListView.setAdapter((ListAdapter) adapter);
    }

    private void setEssentialListviewParameters() {
        this.menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout.2
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (!AbstractListMenuLayout.this.ignoreClick) {
                    AbstractListMenuLayout.this.ignoreClick = true;
                    AbstractListMenuLayout.this.refreshSelectorView(arg2);
                    arg1.performClick();
                    AbstractListMenuLayout.this.handler.postDelayed(new Runnable() { // from class: com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout.2.1
                        @Override // java.lang.Runnable
                        public void run() {
                            AbstractListMenuLayout.this.ignoreClick = false;
                        }
                    }, AbstractListMenuLayout.this.getDelayClickReEnabled());
                    return;
                }
                Log.w(AbstractListMenuLayout.TAG, "Double Click is Ignored.");
            }
        });
        this.menuListView.setOnScrollListener(new AbsListView.OnScrollListener() { // from class: com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout.3
            @Override // android.widget.AbsListView.OnScrollListener
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }

            @Override // android.widget.AbsListView.OnScrollListener
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case 0:
                        AbstractListMenuLayout.this.onScrollFinished(view);
                        return;
                    case 1:
                    case 2:
                        AbstractListMenuLayout.this.stopGuideOpener();
                        if (!AbstractListMenuLayout.this.overScrolling) {
                            AbstractListMenuLayout.this.refreshSelectorView(-1);
                            return;
                        }
                        return;
                    default:
                        return;
                }
            }
        });
        this.menuListView.setOnTouchListener(new View.OnTouchListener() { // from class: com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout.4
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                AbstractListMenuLayout.this.stopGuideOpener();
                return false;
            }
        });
        this.menuListView.registerOverScrollListener(this);
        this.menuListView.setChoiceMode(1);
        this.menuListView.setDescendantFocusability(Info.kind.BOOT_FAILED);
        setAdditionalListviewParameters(this.menuListView);
    }

    protected void setAdditionalListviewParameters(MenuListView listview) {
    }

    protected int getDelayClickReEnabled() {
        return 1000;
    }

    protected void setAdapterOnListview() {
        DefaultListMenuAdapter adapter = new DefaultListMenuAdapter(getActivity().getApplicationContext(), getMenuItems(), this);
        setAdapter(adapter);
    }

    private void setTitleBar() {
        int titleBarTextViewId = getTitleBarTextViewId();
        int titleBarStringId = getTitleBarStringId();
        if (titleBarTextViewId > 0 && titleBarStringId > 0) {
            ((TextView) getView().findViewById(titleBarTextViewId)).setText(titleBarStringId);
        }
    }

    protected final void setFakeSelected(int index) {
        if (getFakeSelected() != index) {
            this.lastFakeSelected = this.fakeSelected;
            this.fakeSelected = index;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getFakeSelected() {
        return this.fakeSelected;
    }

    private int getLastFakeSelected() {
        return this.lastFakeSelected;
    }

    private void setSelectionFromTouch(boolean bool) {
        this.isSelectionFromTouch = bool;
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.MenuListView.OverScrollListener
    public final void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        boolean isTopEndAction;
        int target;
        Log.d(TAG, "onOverScrolled: " + scrollX + ", " + scrollY + ", " + clampedX + ", " + clampedY);
        int lastItem = getListMenuAdapter().getCount() - 1;
        int firstVisible = this.menuListView.getFirstVisiblePosition();
        int lastVisible = this.menuListView.getLastVisiblePosition();
        if (scrollY != 0) {
            if (scrollY < 0) {
                isTopEndAction = true;
            } else {
                isTopEndAction = false;
            }
            if (clampedY && !this.overScrolling) {
                this.overScrolling = true;
                if (isInfiniteScrollByTouchEnabled()) {
                    if (firstVisible == 0) {
                        if (this.fakeSelected < getMaxNumOfDisplayedItems() && isTopEndAction) {
                            target = lastItem;
                        } else {
                            target = 0;
                        }
                    } else if (lastVisible == lastItem) {
                        if (lastItem - getMaxNumOfDisplayedItems() < this.fakeSelected && !isTopEndAction) {
                            target = 0;
                        } else {
                            target = lastItem;
                        }
                    } else {
                        Log.d(TAG, "Unknown Cases...");
                        this.overScrolling = false;
                        return;
                    }
                } else if (firstVisible == 0) {
                    target = 0;
                } else if (lastVisible == lastItem) {
                    target = lastItem;
                } else {
                    this.overScrolling = false;
                    return;
                }
                this.handler.postDelayed(new Runnable() { // from class: com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout.5
                    @Override // java.lang.Runnable
                    public void run() {
                        Log.d(AbstractListMenuLayout.TAG, "Overscroll lock is released");
                        AbstractListMenuLayout.this.overScrolling = false;
                    }
                }, getDelayOverScrollReEnabled());
                setFakeSelected(target);
                refreshSelection(target, 0);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void onScrollFinished(AbsListView view) {
        int target;
        int adjustEnd;
        int firstVisible = this.menuListView.getFirstVisiblePosition();
        int lastVisible = this.menuListView.getLastVisiblePosition();
        if (this.menuListView.getChildAt(0).getTop() < (-getRowHeight()) / 2) {
            firstVisible++;
        }
        if (getRowHeight() * ((float) (getMaxNumOfDisplayedItems() - 0.5d)) < this.menuListView.getChildAt(this.menuListView.getChildCount() - 1).getTop()) {
            lastVisible--;
        }
        if (this.fakeSelected < firstVisible) {
            target = firstVisible;
            adjustEnd = 1;
        } else if (lastVisible < this.fakeSelected) {
            target = lastVisible;
            adjustEnd = -1;
        } else {
            target = this.fakeSelected;
            adjustEnd = 0;
        }
        refreshSelection(target, adjustEnd);
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuAdapter.ListMenuSelectedListener
    public final void onItemFocused(int index) {
        if (this.isinitialSelection) {
            this.isinitialSelection = false;
        } else {
            startGuideOpener(index);
        }
        setFakeSelected(index);
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuAdapter.ListMenuSelectedListener
    public final void onItemTouched(int index) {
        Log.d(TAG, "On Item " + index + " touched. set selected this view.");
        stopGuideOpener();
        setSelectionFromTouch(true);
        setFakeSelected(index);
        refreshSelection(index, 0);
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuAdapter.ListMenuSelectedListener
    public void onItemReleased(int index) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshSelection(int index, int adjustEnd) {
        int scrollAdjustment;
        if (!this.isSelectionFromTouch) {
            int firstVisible = this.menuListView.getFirstVisiblePosition();
            if (this.menuListView.getChildAt(0).getTop() < (-getRowHeight()) / 2) {
                firstVisible++;
            }
            int numItems = getListMenuAdapter().getCount();
            int topDifference = firstVisible - index;
            if (index == 0 || numItems == index + 1 || numItems - 1 < getMaxNumOfDisplayedItems()) {
                this.menuListView.setSelection(index);
            } else {
                switch (adjustEnd) {
                    case -1:
                        scrollAdjustment = getMaxNumOfDisplayedItems() - 1;
                        break;
                    case 0:
                        switch (getFakeSelected() - getLastFakeSelected()) {
                            case -1:
                                if (topDifference < -1) {
                                    scrollAdjustment = -topDifference;
                                    break;
                                } else {
                                    scrollAdjustment = 1;
                                    break;
                                }
                            case 0:
                            default:
                                scrollAdjustment = 0;
                                break;
                            case 1:
                                if (-3 < topDifference) {
                                    scrollAdjustment = -topDifference;
                                    break;
                                } else {
                                    scrollAdjustment = getMaxNumOfDisplayedItems() - 2;
                                    break;
                                }
                        }
                    case 1:
                        scrollAdjustment = 0;
                        break;
                    default:
                        scrollAdjustment = 0;
                        break;
                }
                this.menuListView.setSelectionFromTop(index, scrollAdjustment * (getRowHeight() - 1));
            }
            if (!this.menuListView.isInTouchMode()) {
                onItemFocused(index);
            }
        } else {
            refreshSelectorView(index);
        }
        setSelectionFromTouch(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshSelectorView(int index) {
        clearSelectorView();
        if (-1 < index) {
            setSelectorView(index);
        }
        refreshAdapterData();
    }

    private void setSelectorView(int index) {
        setCheckedOnAdapterItem(index, true);
    }

    private void clearSelectorView() {
        for (int i = 0; i < getListMenuAdapter().getCount(); i++) {
            setCheckedOnAdapterItem(i, false);
        }
    }

    private void setCheckedOnAdapterItem(int index, boolean checked) {
        getListMenuAdapter().getItem(index).setFakeChecked(checked);
    }

    private void setDPadKeyPushed(boolean bool) {
        this.isKeyPushed = bool;
    }

    private boolean isDPadKeyPushed() {
        return this.isKeyPushed;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public final int onKeyUp(int keyCode, KeyEvent event) {
        int ret;
        int scanCode = event.getScanCode();
        int ret2 = -1;
        if (this.menuListView.getChildCount() == 0) {
            Log.i(TAG, "The child count of ListView is 0!!! AbstractListMenuLayout doesn't handle this onKeyUp!!!!");
            return noOperationKeyUp(keyCode, scanCode, event);
        }
        switch (keyCode) {
            case 19:
                if (!isDPadKeyPushed()) {
                    moveSelectedUp();
                }
                setDPadKeyPushed(false);
                ret2 = 1;
                break;
            case 20:
                if (!isDPadKeyPushed()) {
                    moveSelectedDown();
                }
                setDPadKeyPushed(false);
                ret2 = 1;
                break;
            case 23:
                ret2 = -1;
                break;
        }
        if (ret2 != -1) {
            return ret2;
        }
        switch (scanCode) {
            case AppRoot.USER_KEYCODE.UP /* 103 */:
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
                ret = 1;
                break;
            default:
                ret = onKeyUpEx(keyCode, scanCode, event);
                break;
        }
        return ret;
    }

    protected int noOperationKeyUp(int keyCode, int scanCode, KeyEvent event) {
        switch (keyCode) {
            case 19:
            case 20:
            case 23:
                return -1;
            case 21:
            case 22:
            default:
                switch (scanCode) {
                    case AppRoot.USER_KEYCODE.UP /* 103 */:
                    case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                    case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                    case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
                    case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
                    case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
                    case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
                    case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
                    case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
                        return -1;
                    default:
                        return onKeyUpEx(keyCode, scanCode, event);
                }
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public final int onKeyDown(int keyCode, KeyEvent event) {
        int ret;
        this.handler.removeCallbacks(this.guideOpenRunnable);
        int scanCode = event.getScanCode();
        int ret2 = -1;
        if (this.menuListView.getChildCount() == 0) {
            Log.i(TAG, "The child count of ListVIew is 0!!! AbstractListMenuLayout doesn't handle this onKeyDown!!!!");
            return noOperationKeyDown(keyCode, scanCode, event);
        }
        switch (keyCode) {
            case 19:
                setDPadKeyPushed(true);
                moveSelectedUp();
                ret2 = 1;
                break;
            case 20:
                setDPadKeyPushed(true);
                moveSelectedDown();
                ret2 = 1;
                break;
            case 23:
                this.menuListView.getChildAt(getFakeSelected() - this.menuListView.getFirstVisiblePosition()).performClick();
                ret2 = 1;
                break;
        }
        if (ret2 != -1) {
            return ret2;
        }
        switch (scanCode) {
            case AppRoot.USER_KEYCODE.UP /* 103 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
                if (this.menuListView.isInTouchMode()) {
                    sendImitationHardwareDirectionUpClickEvent();
                } else {
                    moveSelectedUp();
                }
                ret = 1;
                break;
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
                if (this.menuListView.isInTouchMode()) {
                    sendImitationHardwareDirectionDownClickEvent();
                } else {
                    moveSelectedDown();
                }
                ret = 1;
                break;
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                clickVirtually(this.menuListView, 23);
                ret = 1;
                break;
            default:
                ret = onKeyDownEx(keyCode, scanCode, event);
                break;
        }
        return ret;
    }

    protected int noOperationKeyDown(int keyCode, int scanCode, KeyEvent event) {
        switch (keyCode) {
            case 19:
            case 20:
            case 23:
                return -1;
            case 21:
            case 22:
            default:
                switch (scanCode) {
                    case AppRoot.USER_KEYCODE.UP /* 103 */:
                    case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                    case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                    case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
                    case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
                    case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
                    case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
                    case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
                    case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
                        return -1;
                    default:
                        return onKeyDownEx(keyCode, scanCode, event);
                }
        }
    }

    private void moveSelectedUp() {
        BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_MOVE_UP);
        if (getFakeSelected() == 0) {
            if (isInfiniteScrollEnabled()) {
                setFakeSelected(getListMenuAdapter().getCount() - 1);
            }
        } else {
            setFakeSelected(getFakeSelected() - 1);
        }
        refreshSelection(getFakeSelected(), 0);
    }

    private void moveSelectedDown() {
        BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_MOVE_DOWN);
        if (getFakeSelected() == this.menuListView.getAdapter().getCount() - 1) {
            if (isInfiniteScrollEnabled()) {
                setFakeSelected(0);
            }
        } else {
            setFakeSelected(getFakeSelected() + 1);
        }
        refreshSelection(getFakeSelected(), 0);
    }

    protected final void sendImitationHardwareDirectionUpClickEvent() {
        sendKeyClickEventFromOtherThread(19, AppRoot.USER_KEYCODE.UP);
    }

    protected final void sendImitationHardwareDirectionDownClickEvent() {
        sendKeyClickEventFromOtherThread(20, AppRoot.USER_KEYCODE.DOWN);
    }

    protected final void sendKeyClickEventFromOtherThread(final int keyCode, final int userKeyCode) {
        Thread thread = new Thread() { // from class: com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout.6
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                Instrumentation mInstrumentation = new Instrumentation();
                mInstrumentation.sendKeySync(new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 0, keyCode, 0, 0, 0, userKeyCode));
                mInstrumentation.sendKeySync(new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 1, keyCode, 0, 0, 0, userKeyCode));
            }
        };
        thread.start();
    }

    private void clickVirtually(View target, int keyCode) {
        target.dispatchKeyEvent(new KeyEvent(0, keyCode));
        target.dispatchKeyEvent(new KeyEvent(1, keyCode));
    }

    private AbstractListMenuAdapter getListMenuAdapter() {
        return (AbstractListMenuAdapter) this.menuListView.getAdapter();
    }

    public final void onValueSelected(int itemIndex, int valueIndex) {
        getMenuItem(itemIndex);
        switch (item.getItemType()) {
            case SELECTOR:
            case NO_PREVIEW_SELECTOR:
                getMenuItem(itemIndex).setSelectedItemIndex(valueIndex);
                refreshAdapterData();
                break;
        }
        yourOnValueSelected(itemIndex, valueIndex);
    }

    public void yourOnValueSelected(int itemIndex, int valueIndex) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class GuideOpenRunnable implements Runnable {
        private int titleStringId = 0;
        private int contentStringId = 0;

        public GuideOpenRunnable() {
        }

        public void setStrings(int titleId, int contentId) {
            this.titleStringId = titleId;
            this.contentStringId = contentId;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.titleStringId != 0 && this.contentStringId != 0 && !AbstractListMenuLayout.this.menuListView.getChildAt(AbstractListMenuLayout.this.getFakeSelected() - AbstractListMenuLayout.this.menuListView.getFirstVisiblePosition()).isPressed()) {
                AbstractListMenuLayout.this.menuActionListener.onRequestGuideOpen(this.titleStringId, this.contentStringId);
            }
        }
    }

    protected void stopGuideOpener() {
        this.handler.removeCallbacks(this.guideOpenRunnable);
    }

    private void startGuideOpener(int index) {
        stopGuideOpener();
        DefaultListMenuItem item = getListMenuAdapter().getItem(index);
        if (item.isGuideEnabled() && getGuideOpenDelay() > 0) {
            this.guideOpenRunnable.setStrings(item.getGuideTitleStrId(), item.getGuideStrId());
            this.handler.postDelayed(this.guideOpenRunnable, getGuideOpenDelay());
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuAdapter.ListMenuSelectedListener
    public void onItemSelected(int selectedItemindex, boolean isItemEnabled) {
        stopGuideOpener();
        setFakeSelected(selectedItemindex);
        refreshSelectorView(selectedItemindex);
        if (isItemEnabled) {
            BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELECT);
        }
        Log.d(TAG, "ON ITEM " + selectedItemindex + " SELECTED FOR ACTION");
    }

    protected final void openSelector() {
        DefaultListMenuItem item = getListMenuAdapter().getItem(getFakeSelected());
        if (this.menuActionListener != null) {
            int displayingPosition = getFakeSelected() - this.menuListView.getFirstVisiblePosition();
            if (this.menuListView.getChildAt(0).getTop() < (-getRowHeight()) / 2) {
                displayingPosition--;
            }
            this.menuActionListener.onRequestSelectorOpen(item, getFakeSelected(), displayingPosition);
            return;
        }
        Log.w(TAG, "MenuActionListener is not set.");
    }

    protected final DefaultListMenuItem getMenuItem(int index) {
        return getListMenuAdapter().getItem(index);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void refreshAdapterData() {
        getListMenuAdapter().notifyDataSetChanged();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final MenuActionListener getMenuActionListener() {
        return this.menuActionListener;
    }
}
