package com.sony.imaging.app.base.menu.layout;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.BaseBackUpKey;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.widget.BatteryIcon;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.imaging.app.base.shooting.widget.ActiveLayout;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.sysutil.didep.Settings;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class FnMenuLayout extends DisplayMenuItemsMenuLayout {
    protected static final int FUNCTION_ASSIGNED = 2;
    protected static final int FUNCTION_INVALID = 0;
    protected static final int FUNCTION_NO_ASSIGNED = 1;
    private static final int ITEM_HEIGHT = 60;
    private static final int ITEM_WIDTH = 105;
    public static final String MENU_ID = "ID_FNMENULAYOUT";
    protected static final String STRING_NO_ASSIGNED = "_NA";
    private static final String TAG = FnMenuLayout.class.getSimpleName();
    private FnItemAdapter mAdapter;
    private ImageView[] mBackGroundImageViews;
    private Context mContext;
    private RelativeLayout mFnMenuLayoutView;
    private ArrayList<IKeyFunction> mFunctions = null;
    private Runnable mGrayoutRunnable;
    private CursorableGridView mGridView;
    private Handler mHandler;
    private ItemClickListenerForGridView mItemClickListenerForGridView;
    protected ArrayList<String> mItemIds;
    protected TextView mItemTextView;
    protected ArrayList<Item> mItems;
    private AdapterView.OnItemSelectedListener mOnItemSelectedListenerForGridView;
    protected boolean mOpeningOtherLayout;

    /* loaded from: classes.dex */
    private class ItemClickListenerForGridView implements AdapterView.OnItemClickListener {
        private ItemClickListenerForGridView() {
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            Item item = (Item) arg0.getItemAtPosition(arg2);
            String clickedItemId = item.itemId;
            String nextMenuId = FnMenuLayout.this.mService.getMenuItemNextMenuID(clickedItemId);
            if (nextMenuId != null && nextMenuId.length() > 0) {
                FnMenuLayout.this.openNextMenu(clickedItemId, nextMenuId);
            }
        }
    }

    /* loaded from: classes.dex */
    private class OnItemSelectedListenerForGridView implements AdapterView.OnItemSelectedListener {
        private OnItemSelectedListenerForGridView() {
        }

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Item item = (Item) parent.getItemAtPosition(position);
            if (item != null) {
                FnMenuLayout.this.mItemTextView.setText(FnMenuLayout.this.mService.getMenuItemText(item.itemId));
            }
        }

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mFunctions = createCustomFunctions();
        this.mHandler = new Handler();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int iconId;
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup currentView = (ViewGroup) obtainViewFromPool(R.layout.menu_fn_pjone);
        this.mFnMenuLayoutView = (RelativeLayout) currentView;
        this.mItemTextView = (TextView) currentView.findViewById(R.id.item_text);
        this.mGridView = (CursorableGridView) currentView.findViewById(R.id.grid_view);
        this.mContext = getActivity().getApplicationContext();
        this.mCameraListener = new DisplayMenuItemsMenuLayout.CameraEventListener();
        this.mAdapter = new FnItemAdapter(getActivity(), 0, 0);
        this.mItemClickListenerForGridView = new ItemClickListenerForGridView();
        this.mOnItemSelectedListenerForGridView = new OnItemSelectedListenerForGridView();
        BaseMenuService service = new BaseMenuService(getActivity());
        int columnNum = this.mGridView.getColumnNum();
        this.mItemIds = new ArrayList<>();
        this.mItems = new ArrayList<>();
        boolean lineExist = false;
        Iterator i$ = this.mFunctions.iterator();
        while (i$.hasNext()) {
            IKeyFunction key = i$.next();
            String itemId = key.getItemIdForMenu();
            Item item = new Item();
            if (itemId != null && getKeyAssigned(key) == 2 && (iconId = service.getMenuItemFnActiveIconId(itemId)) != 0) {
                item.itemId = itemId;
                item.fnActiveIconId = iconId;
                lineExist = true;
            } else {
                itemId = STRING_NO_ASSIGNED;
                item.itemId = STRING_NO_ASSIGNED;
            }
            this.mItemIds.add(itemId);
            this.mItems.add(item);
            int size = this.mItemIds.size();
            if (size % columnNum == 0) {
                int lastIndex = size - 1;
                if (!lineExist) {
                    for (int i = lastIndex; i > lastIndex - columnNum; i--) {
                        this.mItemIds.remove(i);
                        this.mItems.remove(i);
                    }
                }
                lineExist = false;
            }
        }
        this.mAdapter.clear();
        service.updateSettingItemsAvailable(this.mItemIds, 1);
        Iterator i$2 = this.mItems.iterator();
        while (i$2.hasNext()) {
            Item item2 = i$2.next();
            item2.valid = service.isMenuItemValid(item2.itemId);
            this.mAdapter.add(item2);
        }
        int rowNum = ((this.mItemIds.size() + columnNum) - 1) / columnNum;
        if (columnNum == 6) {
            int gridViewId = this.mGridView.getId();
            this.mBackGroundImageViews = new ImageView[rowNum];
            int topMargin = -2;
            for (int i2 = 0; i2 < rowNum; i2++) {
                RelativeLayout.LayoutParams backgroundlp = new RelativeLayout.LayoutParams(-2, -2);
                backgroundlp.addRule(5, gridViewId);
                backgroundlp.addRule(6, gridViewId);
                int topMargin2 = topMargin + (i2 * ITEM_HEIGHT);
                backgroundlp.topMargin = topMargin2;
                this.mBackGroundImageViews[i2] = new ImageView(this.mContext);
                this.mBackGroundImageViews[i2].setLayoutParams(backgroundlp);
                this.mBackGroundImageViews[i2].setImageResource(android.R.drawable.pointer_hand_large);
                this.mFnMenuLayoutView.addView(this.mBackGroundImageViews[i2], backgroundlp);
                topMargin = topMargin2 + 2;
            }
        }
        int gridViewHeight = rowNum * 62;
        ImageView backGround = (ImageView) currentView.findViewById(R.id.gridview_background);
        if (backGround != null) {
            ViewGroup.LayoutParams lp = backGround.getLayoutParams();
            lp.height = gridViewHeight;
            backGround.setLayoutParams(lp);
        }
        return currentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.fw.Layout
    public void onReopened() {
        getFocusable();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        if (existAssigned()) {
            this.mGridView.setAdapter((ListAdapter) this.mAdapter);
            this.mGridView.setFocusable(false);
            this.mGridView.setOnItemClickListener(this.mItemClickListenerForGridView);
            this.mGridView.setOnItemSelectedListener(this.mOnItemSelectedListenerForGridView);
            int pos = BackUpUtil.getInstance().getPreferenceInt(BaseBackUpKey.ID_MENU_POSITION_FN_MENU, 0);
            int lastIndex = this.mItemIds.size() - 1;
            if (pos > lastIndex || this.mItemIds.get(pos).equals(STRING_NO_ASSIGNED)) {
                pos = 0;
            }
            while (this.mItemIds.get(pos).equals(STRING_NO_ASSIGNED)) {
                pos = lastIndex == pos ? 0 : pos + 1;
            }
            this.mGridView.setSelection(pos);
            Item item = (Item) this.mGridView.getSelectedItem();
            if (item != null) {
                this.mItemTextView.setText(this.mService.getMenuItemText(item.itemId));
                this.mItemTextView.setVisibility(0);
            }
            this.mOpeningOtherLayout = false;
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        int pos = this.mGridView.getSelectedItemPosition();
        BackUpUtil backUpUtil = BackUpUtil.getInstance();
        if (pos < 0) {
            pos = 0;
        }
        backUpUtil.setPreference(BaseBackUpKey.ID_MENU_POSITION_FN_MENU, Integer.valueOf(pos));
        this.mGridView.setAdapter((ListAdapter) null);
        this.mAdapter.clear();
        this.mGridView.setOnItemClickListener(null);
        this.mGridView.setOnItemSelectedListener(null);
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        if (this.mBackGroundImageViews != null) {
            ImageView[] arr$ = this.mBackGroundImageViews;
            for (ImageView iv : arr$) {
                this.mFnMenuLayoutView.removeView(iv);
            }
        }
        this.mBackGroundImageViews = null;
        this.mItemTextView = null;
        this.mGridView = null;
        this.mCameraListener = null;
        this.mAdapter = null;
        this.mItemClickListenerForGridView = null;
        this.mOnItemSelectedListenerForGridView = null;
        this.mGrayoutRunnable = null;
        super.onDestroyView();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        this.mHandler = null;
        this.mFunctions = null;
        this.mItemIds = null;
        this.mItems = null;
        super.onDestroy();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected boolean isAELCancel() {
        return false;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        boolean handled = this.mGridView.dispatchKeyEvent(event);
        if (handled) {
            getFocusable();
        }
        if (handled) {
            return 1;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void getFocusable() {
        if (this.mOpeningOtherLayout) {
            this.mOpeningOtherLayout = false;
            this.mHandler.removeCallbacks(this.mGrayoutRunnable);
            this.mHandler.post(new Runnable() { // from class: com.sony.imaging.app.base.menu.layout.FnMenuLayout.1
                @Override // java.lang.Runnable
                public void run() {
                    FnMenuLayout.this.mService.updateSettingItemsAvailable(FnMenuLayout.this.mItemIds, 1);
                    Iterator i$ = FnMenuLayout.this.mItems.iterator();
                    while (i$.hasNext()) {
                        Item item = i$.next();
                        if (item != null) {
                            item.valid = FnMenuLayout.this.mService.isMenuItemValid(item.itemId);
                        }
                    }
                    FnMenuLayout.this.mGridView.invalidateViews();
                }
            });
            this.mItemTextView.setVisibility(0);
        }
    }

    protected ArrayList<IKeyFunction> createCustomFunctions() {
        ArrayList<IKeyFunction> keyFunctions = new ArrayList<>();
        int[] funcs = Settings.getCustomLauncherAssign();
        for (int f : funcs) {
            IKeyFunction func = CustomizableFunction.customLauncherFunction2CustomizableFunction(f);
            keyFunctions.add(func);
        }
        return keyFunctions;
    }

    protected int getKeyAssigned(IKeyFunction func) {
        if (func == null || CustomizableFunction.Invalid.equals(func) || CustomizableFunction.Unknown.equals(func) || CustomizableFunction.NoAssign.equals(func)) {
            return 1;
        }
        return 2;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected ArrayList<String> getCameraNotifyTags() {
        ArrayList<String> tags = new ArrayList<>();
        for (int i = 0; i < this.mAdapter.getCount(); i++) {
            String itemid = this.mFunctions.get(i).getItemIdForMenu();
            if (itemid != null) {
                tags.add(this.mService.getMenuItemNotifyTag(itemid));
            }
        }
        return tags;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected boolean isParentItemAvailable() {
        return false;
    }

    public boolean existAssigned() {
        Iterator i$ = this.mItemIds.iterator();
        while (i$.hasNext()) {
            String itemId = i$.next();
            if (!itemId.equals(STRING_NO_ASSIGNED)) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void checkCaution() {
        if (!existAssigned()) {
            int cautionId = Environment.getVersionPfAPI() >= 2 ? 1742 : 1474;
            CautionUtilityClass.getInstance().requestTrigger(cautionId);
            closeMenuLayout(null);
        }
    }

    private boolean isHit(View v, float touchedAbsoluteX, float touchedAbsoluteY) {
        int[] absolutePos = new int[2];
        v.getLocationInWindow(absolutePos);
        int absoluteX = absolutePos[0];
        int absoluteY = absolutePos[1];
        int width = v.getWidth();
        int height = v.getHeight();
        if (touchedAbsoluteX < absoluteX || absoluteX + width < touchedAbsoluteX || touchedAbsoluteY < absoluteY || absoluteY + height < touchedAbsoluteY) {
            return false;
        }
        return true;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        if (this.mOpeningOtherLayout) {
            getFocusable();
            return -1;
        }
        Item item = (Item) this.mGridView.getSelectedItem();
        doItemClickProcessing(item.itemId);
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void doItemClickProcessing(String itemid) {
        if (this.mUpdater != null) {
            this.mUpdater.cancelMenuUpdater();
        }
        if (itemid.equals("ExitApplication")) {
            BaseApp baseApp = (BaseApp) getActivity();
            baseApp.finish();
            return;
        }
        if (!this.mService.isMenuItemValid(itemid)) {
            requestCautionTrigger(itemid);
            return;
        }
        String execType = this.mService.getMenuItemExecType(itemid);
        String nextFragmentID = this.mService.getMenuItemCustomStartLayoutID(itemid);
        if (MenuTable.NEXT_LAYOUT.equals(execType) || MenuTable.NEXT_LAYOUT_WITHOUT_SET.equals(execType)) {
            if (MenuTable.NEXT_LAYOUT.equals(execType)) {
                this.mService.execCurrentMenuItem(itemid, false);
            }
            if (nextFragmentID != null) {
                openNextMenu(itemid, nextFragmentID);
                return;
            }
            return;
        }
        if (MenuTable.NEXT_STATE.equals(execType)) {
            openNextState(nextFragmentID, null);
        } else if (MenuTable.SET_VALUE.equals(execType) || MenuTable.SET_VALUE_ONLY_CLICK.equals(execType)) {
            this.mService.execCurrentMenuItem(itemid, false);
            postSetValue();
        }
    }

    public String getItemId() {
        Item curItem = (Item) this.mGridView.getSelectedItem();
        if (curItem == null) {
            return null;
        }
        String itemId = curItem.itemId;
        return itemId;
    }

    protected void openFn15LayerLayout(Bundle bundle) {
        Item curItem = (Item) this.mGridView.getSelectedItem();
        boolean valid = this.mService.isMenuItemValid(curItem.itemId);
        if (curItem != null && valid) {
            String layoutId = this.mService.getMenuItemFnNextMenuID(curItem.itemId);
            String itemId = curItem.itemId;
            if (itemId.equals(ExposureModeController.EXPOSURE_MODE)) {
                itemId = this.mService.getCurrentValue(itemId);
                if (!this.mService.hasSubArray(itemId)) {
                    return;
                }
            }
            this.mGrayoutRunnable = new Runnable() { // from class: com.sony.imaging.app.base.menu.layout.FnMenuLayout.2
                @Override // java.lang.Runnable
                public void run() {
                    if (FnMenuLayout.this.mGridView != null) {
                        Item curItem2 = (Item) FnMenuLayout.this.mGridView.getSelectedItem();
                        if (FnMenuLayout.this.mGridView != null) {
                            int ct = FnMenuLayout.this.mGridView.getCount();
                            for (int i = 0; i < ct; i++) {
                                Item item = (Item) FnMenuLayout.this.mGridView.getItemAtPosition(i);
                                if (item != null) {
                                    item.valid = false;
                                }
                            }
                            curItem2.valid = true;
                            FnMenuLayout.this.mGridView.invalidateViews();
                        }
                    }
                }
            };
            this.mHandler.post(this.mGrayoutRunnable);
            openNextMenu(itemId, layoutId, true, bundle);
            this.mItemTextView.setVisibility(8);
            this.mOpeningOtherLayout = true;
            return;
        }
        requestCautionTrigger(curItem.itemId);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        Item item = (Item) this.mGridView.getSelectedItem();
        guideResources.add(this.mService.getMenuItemText(item.itemId));
        guideResources.add(this.mService.getMenuItemGuideText(item.itemId));
        guideResources.add(Boolean.valueOf(this.mService.isMenuItemValid(item.itemId)));
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected void onDeviceStatusChanged() {
        closeMenuLayout(null);
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected void onAFMFSwitchChanged() {
        closeMenuLayout(null);
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected void onDigitalZoomOnOff(boolean onoff) {
        if (!this.mOpeningOtherLayout) {
            this.mService.updateSettingItemsAvailable(this.mItemIds, 1);
            Iterator i$ = this.mItems.iterator();
            while (i$.hasNext()) {
                Item item = i$.next();
                if (item != null) {
                    item.valid = this.mService.isMenuItemValid(item.itemId);
                }
            }
            this.mGridView.invalidateViews();
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected void onFocusModeChanged() {
        if (!this.mOpeningOtherLayout) {
            this.mService.updateSettingItemsAvailable(this.mItemIds, 1);
            Iterator i$ = this.mItems.iterator();
            while (i$.hasNext()) {
                Item item = i$.next();
                if (item != null) {
                    item.valid = this.mService.isMenuItemValid(item.itemId);
                }
            }
            this.mGridView.invalidateViews();
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected void onStreamWriterStatusChanged() {
        if (!this.mOpeningOtherLayout) {
            this.mService.updateSettingItemsAvailable(this.mItemIds, 1);
            Iterator i$ = this.mItems.iterator();
            while (i$.hasNext()) {
                Item item = i$.next();
                if (item != null) {
                    item.valid = this.mService.isMenuItemValid(item.itemId);
                }
            }
            this.mGridView.invalidateViews();
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        Bundle b = new Bundle();
        b.putInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION, 1);
        openFn15LayerLayout(b);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        Bundle b = new Bundle();
        b.putInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION, 2);
        openFn15LayerLayout(b);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        Bundle b = new Bundle();
        b.putInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION, 3);
        openFn15LayerLayout(b);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        Bundle b = new Bundle();
        b.putInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION, 4);
        openFn15LayerLayout(b);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialNext() {
        Bundle b = new Bundle();
        b.putInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION, 1);
        openFn15LayerLayout(b);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialPrev() {
        Bundle b = new Bundle();
        b.putInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION, 2);
        openFn15LayerLayout(b);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        if (this.mOpeningOtherLayout) {
            getFocusable();
            return 1;
        }
        openPreviousMenu();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        closeMenuLayout(null);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMfAssistCustomKey() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class Item {
        int fnActiveIconId;
        String itemId;
        boolean valid;

        private Item() {
        }
    }

    /* loaded from: classes.dex */
    private class FnItemAdapter extends ArrayAdapter<Item> {
        private SparseArray<View> mIcons;
        private LayoutInflater mInflater;

        public FnItemAdapter(Context context, int resource, int textViewResourceId) {
            super(context, resource, textViewResourceId);
            this.mInflater = (LayoutInflater) FnMenuLayout.this.getActivity().getSystemService("layout_inflater");
            this.mIcons = new SparseArray<>();
        }

        private View obtainInflatedIcon(int position) {
            Item item = getItem(position);
            View ret = this.mInflater.inflate(item.fnActiveIconId, (ViewGroup) null);
            if (ret instanceof ActiveImage) {
                ActiveImage aImage = (ActiveImage) ret;
                aImage.setFnMode(true);
            } else if (ret instanceof ActiveLayout) {
                ((ActiveLayout) ret).setFnMode(true);
            }
            ret.setLayoutParams(new LinearLayout.LayoutParams(70, FnMenuLayout.ITEM_HEIGHT));
            return ret;
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout ll;
            Item item = getItem(position);
            if (convertView == null || !(convertView instanceof LinearLayout)) {
                ll = new LinearLayout(getContext());
                Log.d(FnMenuLayout.TAG, "new LinearLayout: " + ll);
                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(105, FnMenuLayout.ITEM_HEIGHT);
                ll.setLayoutParams(lp);
                ll.setGravity(17);
                ll.setBackgroundDrawable(FnMenuLayout.this.getResources().getDrawable(R.drawable.menu_fn_selector));
            } else {
                ll = (LinearLayout) convertView;
            }
            View view = ll;
            ll.removeAllViews();
            View inflatedView = null;
            if (!item.itemId.equals(FnMenuLayout.STRING_NO_ASSIGNED)) {
                if (item.fnActiveIconId > 0) {
                    inflatedView = obtainInflatedIcon(position);
                } else {
                    ImageView iv = new ImageView(FnMenuLayout.this.mContext);
                    inflatedView = iv;
                }
                ViewParent parentOfIcon = inflatedView.getParent();
                if (parentOfIcon != null) {
                    ((ViewGroup) parentOfIcon).removeView(inflatedView);
                }
                ll.addView(inflatedView);
            }
            if (inflatedView instanceof ImageView) {
                ImageView iv2 = (ImageView) inflatedView;
                iv2.setAlpha(item.valid ? BatteryIcon.BATTERY_STATUS_CHARGING : 128);
            } else if (inflatedView instanceof ActiveLayout) {
                ActiveLayout al = (ActiveLayout) inflatedView;
                al.setAlpha(item.valid ? BatteryIcon.BATTERY_STATUS_CHARGING : 128);
            }
            view.setTag(Integer.valueOf(position));
            return view;
        }

        @Override // android.widget.BaseAdapter, android.widget.ListAdapter
        public boolean isEnabled(int position) {
            return !getItem(position).itemId.equals(FnMenuLayout.STRING_NO_ASSIGNED);
        }
    }
}
