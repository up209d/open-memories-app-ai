package com.sony.imaging.app.pictureeffectplus.menu.layout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.HistoryItem;
import com.sony.imaging.app.base.menu.MenuDataParcelable;
import com.sony.imaging.app.base.menu.layout.BaseMenuAdapter;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.pictureeffectplus.AppLog;
import com.sony.imaging.app.pictureeffectplus.R;
import com.sony.imaging.app.pictureeffectplus.shooting.camera.PictureEffectPlusController;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class VerticalScrollMenuLayout extends DisplayMenuItemsMenuLayout implements AdapterView.OnItemSelectedListener {
    public static final String MENU_ID = "ID_VERTICALSCROLLMENULAYOUT";
    private static final String TAG = "VerticalScrollMenuLayout";
    protected String mCancelItemId;
    protected DisplayMenuItemsMenuLayout.MenuUpdater mLayoutUpdater;
    private int mOldSelectedPos;
    private ImageView mOldSelectedView;
    protected DisplayMenuItemsMenuLayout.MenuUpdater mValueDispatcher;
    protected VerticalScrollView mVerticalScrollView;
    protected TextView mScreenTitleView = null;
    protected TextView mItemTextView = null;
    protected BaseMenuAdapter mAdapter = null;
    private String lastItemID = null;
    private PictureEffectPlusController mController = null;
    private String mCurSelectedEffect = null;
    public int mInitPos = 0;
    private FooterGuide mFooterGuide = null;
    private ArrayList<String> mParentItems = new ArrayList<>();

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(getLayoutId(), (ViewGroup) null);
        this.mVerticalScrollView = (VerticalScrollView) v.findViewById(R.id.verticalScrollView);
        this.mScreenTitleView = (TextView) v.findViewById(R.id.menu_screen_title);
        this.mItemTextView = (TextView) v.findViewById(R.id.menu_item_name);
        this.mService = new BaseMenuService(getActivity());
        this.mAdapter = new VerticalScrollAdapter(getActivity(), R.layout.menu_vertical_adapter, this.mService);
        this.lastItemID = this.mService.getMenuItemId();
        this.mController = PictureEffectPlusController.getInstance();
        this.mCurSelectedEffect = this.mController.getBackupEffectValue();
        if (this.mCurSelectedEffect.equals(PictureEffectController.MODE_SOFT_FOCUS) || this.mCurSelectedEffect.equals(PictureEffectController.MODE_HDR_ART) || this.mCurSelectedEffect.equals("illust") || this.mCurSelectedEffect.equals(PictureEffectController.MODE_SOFT_HIGH_KEY) || this.mCurSelectedEffect.equals(PictureEffectController.MODE_POSTERIZATION)) {
            this.mService.setMenuItemId(this.mCurSelectedEffect);
            this.mAdapter.setMenuItemList(this.mService.getMenuItemList());
            this.mVerticalScrollView.setAdapter(this.mAdapter);
        }
        this.mFooterGuide = (FooterGuide) v.findViewById(R.id.footer_guide);
        this.mFooterGuide.setData(new FooterGuideDataResId(getActivity().getApplicationContext(), android.R.string.zen_mode_feature_name, android.R.string.httpErrorRedirectLoop));
        return v;
    }

    protected int getLayoutId() {
        return R.layout.menu_vertical;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        this.mVerticalScrollView = null;
        this.mScreenTitleView = null;
        this.mItemTextView = null;
        this.mCancelItemId = null;
        this.mLayoutUpdater = null;
        this.mValueDispatcher = null;
        this.mService = null;
        this.mAdapter = null;
        this.lastItemID = null;
        this.mController = null;
        this.mCurSelectedEffect = null;
        this.mInitPos = 0;
        this.mFooterGuide = null;
        super.onDestroyView();
    }

    protected void updateAdapterView(ArrayList<String> itemIds) {
        String currentItemId = this.mService.getMenuItemId();
        String currentValueItemId = this.mService.getCurrentValue(currentItemId);
        int pos = 0;
        if (currentValueItemId != null) {
            int count = itemIds.size();
            for (int i = 0; i < count; i++) {
                if (currentValueItemId.equals(itemIds.get(i))) {
                    pos = i;
                }
            }
        }
        this.mInitPos = pos;
        this.mAdapter.setMenuItemList(itemIds);
        this.mVerticalScrollView.setAdapter(this.mAdapter);
        this.mVerticalScrollView.setSelection(pos);
        this.mVerticalScrollView.setOnItemSelectedListener(this);
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        String currentItemId = this.mService.getMenuItemId();
        String currentValueItemId = this.mService.getCurrentValue(currentItemId);
        updateAdapterView(getMenuItemList());
        if (this.mScreenTitleView != null) {
            this.mScreenTitleView.setText(this.mService.getMenuItemText(currentItemId));
        }
        if (this.mItemTextView != null) {
            this.mItemTextView.setText(this.mService.getMenuItemText(currentValueItemId));
        }
        this.mValueDispatcher = new DisplayMenuItemsMenuLayout.MenuUpdater(new Handler()) { // from class: com.sony.imaging.app.pictureeffectplus.menu.layout.VerticalScrollMenuLayout.1
            @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout.MenuUpdater, java.lang.Runnable
            public void run() {
                try {
                    String itemId = (String) VerticalScrollMenuLayout.this.mVerticalScrollView.getSelectedItem();
                    VerticalScrollMenuLayout.this.mService.execCurrentMenuItem(itemId, false);
                } catch (Exception e) {
                    AppLog.info(VerticalScrollMenuLayout.TAG, e.toString());
                }
            }
        };
        this.mValueDispatcher.setDelayTime(150L);
        this.mLayoutUpdater = new DisplayMenuItemsMenuLayout.MenuUpdater(new Handler()) { // from class: com.sony.imaging.app.pictureeffectplus.menu.layout.VerticalScrollMenuLayout.2
            @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout.MenuUpdater, java.lang.Runnable
            public void run() {
                VerticalScrollMenuLayout.this.updateAdapterView(VerticalScrollMenuLayout.this.getMenuItemList());
            }
        };
        MenuDataParcelable parcelable = (MenuDataParcelable) this.data.getParcelable(MenuDataParcelable.KEY);
        if (!"back".equals(parcelable.getItemId())) {
            this.mCancelItemId = currentValueItemId;
        }
        this.mVerticalScrollView.setOnTouchListener(new View.OnTouchListener() { // from class: com.sony.imaging.app.pictureeffectplus.menu.layout.VerticalScrollMenuLayout.3
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    private void setSelectedView(ImageView iView, int position) {
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
        this.mVerticalScrollView.setOnItemClickListener(null);
        this.mVerticalScrollView.setOnItemSelectedListener(null);
        this.mVerticalScrollView.setOnItemScrollListener(null);
        this.mOldSelectedPos = 0;
        this.mAdapter.imageCacheClear();
        this.mVerticalScrollView.setAdapter(null);
        this.mValueDispatcher.cancelMenuUpdater();
        this.mLayoutUpdater.cancelMenuUpdater();
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
        if ((this.mCurSelectedEffect.equals(PictureEffectController.MODE_SOFT_FOCUS) || this.mCurSelectedEffect.equals(PictureEffectController.MODE_HDR_ART) || this.mCurSelectedEffect.equals("illust") || this.mCurSelectedEffect.equals(PictureEffectController.MODE_SOFT_HIGH_KEY) || this.mCurSelectedEffect.equals(PictureEffectController.MODE_POSTERIZATION)) && this.mController.isComingFromApplicationSettings()) {
            this.mController.setComingFromApplicationSettings(false);
        }
        String itemId = (String) this.mVerticalScrollView.getSelectedItem();
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
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        Log.i(TAG, "mVerticalScrollView.getCount()*********************" + this.mVerticalScrollView.getCount());
        if (this.mVerticalScrollView.getCount() != 1) {
            this.mVerticalScrollView.movePrevious();
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        if (this.mVerticalScrollView.getCount() != 1) {
            this.mVerticalScrollView.moveNext();
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        int ret;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mCurSelectedEffect.equals(PictureEffectController.MODE_SOFT_FOCUS) || this.mCurSelectedEffect.equals(PictureEffectController.MODE_HDR_ART) || this.mCurSelectedEffect.equals("illust") || this.mCurSelectedEffect.equals(PictureEffectController.MODE_SOFT_HIGH_KEY) || this.mCurSelectedEffect.equals(PictureEffectController.MODE_POSTERIZATION)) {
            if (!this.mController.isComingFromApplicationSettings()) {
                openPreviousMenu();
                return 1;
            }
            cancelSetValue();
            String prevLayoutID = getMenuData().getPreviousMenuLayoutId();
            HistoryItem historyItem = new HistoryItem(this.lastItemID, prevLayoutID);
            this.mService.pushMenuHistory(historyItem);
            openPreviousMenu();
            this.mController.setComingFromApplicationSettings(false);
            AppLog.exit(TAG, AppLog.getMethodName());
            ret = 1;
        } else {
            this.mService.execCurrentMenuItem(this.mCancelItemId, false);
            openPreviousMenu();
            ret = 1;
        }
        return ret;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        String itemId = (String) this.mVerticalScrollView.getSelectedItem();
        guideResources.add(this.mService.getMenuItemText(itemId));
        guideResources.add(this.mService.getMenuItemGuideText(itemId));
        guideResources.add(Boolean.valueOf(this.mService.isMenuItemValid(itemId)));
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ImageView iconView = (ImageView) view.findViewWithTag("icon");
        String itemId = (String) parent.getItemAtPosition(position);
        iconView.setImageDrawable(this.mService.getMenuItemSelectedDrawable(itemId));
        String oldItemId = null;
        AppLog.info(TAG, "Position== " + position + ExposureModeController.SOFT_SNAP + "mOldSelectedPos== " + this.mOldSelectedPos + "  itemId " + itemId);
        try {
            oldItemId = (String) parent.getItemAtPosition(this.mOldSelectedPos);
            if (this.mOldSelectedView != null) {
                this.mOldSelectedView.setImageDrawable(this.mService.getMenuItemDrawable(oldItemId));
            }
            this.mOldSelectedView = iconView;
            this.mOldSelectedPos = position;
        } catch (Exception e) {
            AppLog.info(TAG, "old itemid " + oldItemId);
            AppLog.info(TAG, e.toString());
        }
        if (this.mItemTextView != null) {
            this.mItemTextView.setText(this.mService.getMenuItemText(itemId));
        }
        this.mValueDispatcher.restartMenuUpdater();
        this.mInitPos = -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected void onDeviceStatusChanged() {
        this.mLayoutUpdater.restartMenuUpdater();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected void onStreamWriterStatusChanged() {
        this.mLayoutUpdater.restartMenuUpdater();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected void onDigitalZoomOnOff(boolean onoff) {
        this.mLayoutUpdater.restartMenuUpdater();
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    /* loaded from: classes.dex */
    private class VerticalScrollAdapter extends BaseMenuAdapter {
        public VerticalScrollAdapter(Context context, int ResId, BaseMenuService service) {
            super(context, ResId, service);
        }

        @Override // com.sony.imaging.app.base.menu.layout.BaseMenuAdapter, android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            if (position == VerticalScrollMenuLayout.this.mInitPos) {
                Drawable d = getItemDrawable(position, true);
                ImageView iv = (ImageView) v.findViewWithTag("icon");
                iv.setImageDrawable(d);
                VerticalScrollMenuLayout.this.mOldSelectedView = iv;
                VerticalScrollMenuLayout.this.mOldSelectedPos = position;
            }
            return v;
        }
    }
}
