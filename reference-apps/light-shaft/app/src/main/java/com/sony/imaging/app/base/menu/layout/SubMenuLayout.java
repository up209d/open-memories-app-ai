package com.sony.imaging.app.base.menu.layout;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.menu.MenuDataParcelable;
import com.sony.imaging.app.base.menu.layout.SubMenuView;
import com.sony.imaging.app.util.PTag;
import java.util.ArrayList;

/* loaded from: classes.dex */
public abstract class SubMenuLayout extends DisplayMenuItemsMenuLayout implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener, SubMenuView.OnItemUpdatedListener {
    private static final String MSG_ON_IDLE_HANDLER = "queueIdle to reflect the selection : ";
    private static final String TAG = "SubMenuLayout";
    private static final int TIME_GUIDE = 1100;
    private static StringBuilder mStrBuilder = new StringBuilder(64);
    private int mOldSelectedPos;
    private ImageView mOldSelectedView;
    protected SubMenuView mSubMenuView;
    protected int mPosition = 0;
    private Handler mHandler = new Handler();
    protected BaseMenuAdapter mAdapter = null;
    protected TextView mScreenTitleView = null;
    private ArrayList<String> mParentItems = new ArrayList<>();

    public abstract ViewGroup createView();

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup currentView = createView();
        return currentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        this.mSubMenuView = null;
        this.mService = null;
        this.mAdapter = null;
        this.mScreenTitleView = null;
        super.onDestroyView();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        if (this.mUpdater != null) {
            this.mUpdater.finishMenuUpdater();
        }
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.mSubMenuView.setOnItemClickListener(this);
        this.mSubMenuView.setOnItemSelectedListener(this);
        this.mSubMenuView.setOnItemUpdatedListener(this);
        this.mAdapter.setMenuItemList(getMenuItemList());
        this.mSubMenuView.setAdapter(this.mAdapter);
        String currentItemId = this.mService.getMenuItemId();
        String currentValueItemId = this.mService.getCurrentValue(currentItemId);
        this.mSubMenuView.setSelection(getFocusPosition(currentValueItemId));
        MenuDataParcelable parcelable = (MenuDataParcelable) this.data.getParcelable(MenuDataParcelable.KEY);
        if (!"back".equals(parcelable.getItemId())) {
            setCancelSetValueItemId(currentValueItemId);
        }
        PTag.end("Menu Opened (SubMenuLayout)");
        PTag.end("Menu open next MenuLayout (SubMenuLayout)");
    }

    protected int getFocusPosition(String itemId) {
        Adapter adapter = this.mSubMenuView.getAdapter();
        if (itemId != null) {
            int count = adapter.getCount();
            for (int i = 0; i < count; i++) {
                if (itemId.equals(adapter.getItem(i))) {
                    return i;
                }
            }
        }
        return 0;
    }

    private void setSelectedView(ImageView iView, int position) {
        Log.v(TAG, "setSelectedView changeImageDrawable");
        BaseMenuAdapter adapter = (BaseMenuAdapter) this.mSubMenuView.getAdapter();
        if (this.mOldSelectedView != null) {
            Drawable d = adapter.getItemDrawable(this.mOldSelectedPos, false);
            adapter.setAvailableAlpha(d, adapter.getItem(this.mOldSelectedPos));
            this.mOldSelectedView.setImageDrawable(d);
        }
        Drawable d2 = adapter.getItemDrawable(position, true);
        adapter.setAvailableAlpha(d2, adapter.getItem(position));
        iView.setImageDrawable(d2);
        this.mOldSelectedView = iView;
        this.mOldSelectedPos = position;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected boolean isParentItemAvailable() {
        if (this.mParentItems == null || this.mService == null) {
            return true;
        }
        this.mParentItems.clear();
        this.mParentItems.add(this.mService.getMenuItemId());
        this.mService.updateSettingItemsAvailable(this.mParentItems);
        return this.mService.isMenuItemValid(this.mService.getMenuItemId());
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.fw.Layout
    public void onReopened() {
        super.onReopened();
        this.mAdapter.imageCacheClear();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        this.mSubMenuView.setOnItemClickListener(null);
        this.mSubMenuView.setOnItemSelectedListener(null);
        this.mSubMenuView.setOnItemUpdatedListener(null);
        this.mSubMenuView.setOnItemScrollListener(null);
        this.mOldSelectedPos = 0;
        this.mAdapter.imageCacheClear();
        this.mSubMenuView.setAdapter(null);
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected ArrayList<String> getMenuItemList() {
        ArrayList<String> items = this.mService.getSupportedItemList();
        this.mService.updateValueItemsAvailable(items);
        return items;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        String itemId = this.mAdapter.getItem(this.mPosition);
        doItemClickProcessing(itemId);
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        return pushedUpKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        return pushedDownKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        return pushedLeftKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        return pushedRightKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        return super.pushedRightKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        return super.pushedLeftKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        if (this.mAdapter.getCount() != 1) {
            this.mSubMenuView.movePrevious();
            PTag.start("Menu move Cursor up (SubMenuLayout)");
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        if (this.mAdapter.getCount() != 1) {
            this.mSubMenuView.moveNext();
            PTag.start("Menu move Cursor down (SubMenuLayout)");
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        cancelSetValue();
        closeMenuLayout(null);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK2Key() {
        String itemId = this.mAdapter.getItem(this.mPosition);
        String nextMenu = this.mService.getMenuItemNextMenuID(itemId);
        boolean hasSubArray = this.mService.hasSubArray(itemId);
        if (!hasSubArray || nextMenu == null || !this.mService.isMenuItemValid(itemId)) {
            return -1;
        }
        openNextMenu(itemId, nextMenu);
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void update(int position) {
        this.mAdapter.getItem(this.mPosition);
    }

    @Override // com.sony.imaging.app.base.menu.layout.SubMenuView.OnItemUpdatedListener
    public void onItemUpdated(SubMenuView parent, View view, int position, long id) {
        Log.v(TAG, "position:" + position);
        this.mPosition = position;
        Log.v(TAG, "onItemUpdated changeImageDrawable");
        ImageView iconView = (ImageView) view.findViewWithTag("icon");
        setSelectedView(iconView, position);
        if (this.mScreenTitleView != null) {
            String itemId = this.mService.getMenuItemId();
            this.mScreenTitleView.setText(this.mService.getMenuItemText(itemId));
        }
        update(position);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.mPosition = position;
        ImageView iconView = (ImageView) view.findViewWithTag("icon");
        setSelectedView(iconView, position);
        PTag.end("Menu move Cursor up/down (SubMenuLayout)");
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
        String itemId = this.mAdapter.getItem(this.mPosition);
        doItemClickProcessing(itemId);
    }

    protected int getTimeOfGuide() {
        return TIME_GUIDE;
    }
}
