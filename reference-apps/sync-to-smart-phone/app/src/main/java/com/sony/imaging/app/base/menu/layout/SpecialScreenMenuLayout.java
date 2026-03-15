package com.sony.imaging.app.base.menu.layout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.MenuDataParcelable;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.base.menu.layout.BaseMenuAdapter;
import com.sony.imaging.app.base.menu.layout.SpecialScreenView;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class SpecialScreenMenuLayout extends DisplayMenuItemsMenuLayout implements SpecialScreenView.OnItemSelectedListener {
    private static final String BACK = "back";
    private static final int ITEM_COUNT_5 = 5;
    public static final String MENU_ID = "ID_SPECIALSCREENMENULAYOUT";
    private static final String TAG = "SpecialScreenMenuLayout";
    private String mCancelMainItemId;
    private String[] mCancelOptionItemIdList;
    private boolean[] mIsChangedOptionValue;
    private boolean mIsChangedValue;
    private boolean mOpSubDial;
    protected SpecialScreenView mSpecialScreenView;
    protected SpecialScreenArea mViewArea = null;
    protected BaseMenuService mOptionService = null;
    protected SpecialBaseMenuAdapter mAdapter = null;
    protected BaseMenuAdapter mOptionAdapter = null;
    protected TextView mScreenTitleView = null;
    protected TextView mItemNameView = null;
    protected TextView mMovieRecView = null;
    private String mLastOptionItemId = null;
    private String mCancelOptionItemId = null;
    private ArrayList<String> mParentItems = new ArrayList<>();

    /* loaded from: classes.dex */
    public class SpecialBaseMenuAdapter extends BaseMenuAdapter {
        public SpecialBaseMenuAdapter(Context context, int ResId, BaseMenuService service) {
            super(context, ResId, service);
        }

        @Override // com.sony.imaging.app.base.menu.layout.BaseMenuAdapter, android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            BaseMenuAdapter.ViewHolder holder = (BaseMenuAdapter.ViewHolder) view.getTag();
            holder.iv.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_special_button));
            return view;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.sony.imaging.app.base.menu.layout.BaseMenuAdapter
        public Drawable getMenuItemDrawable(int position) {
            BaseMenuService baseMenuService = this.mService;
            String itemId = this.mItems.get(position);
            boolean hasSubArray = baseMenuService.hasSubArray(itemId);
            if (hasSubArray) {
                List<String> oplist = baseMenuService.getSupportedItemList(itemId);
                if (oplist != null && oplist.size() != 0) {
                    String optionItemId = this.mService.getCurrentValue(itemId);
                    Drawable d = super.getMenuItemDrawable(optionItemId);
                    return d;
                }
                Drawable d2 = super.getMenuItemDrawable(position);
                return d2;
            }
            Drawable d3 = super.getMenuItemDrawable(position);
            return d3;
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup currentView = (ViewGroup) obtainViewFromPool(getLayoutID());
        Context context = getActivity().getApplicationContext();
        this.mSpecialScreenView = (SpecialScreenView) currentView.findViewById(R.id.listview);
        this.mViewArea = (SpecialScreenArea) currentView.findViewById(R.id.listviewarea);
        this.mService = new BaseMenuService(context);
        this.mOptionService = new BaseMenuService(context);
        this.mAdapter = new SpecialBaseMenuAdapter(context, R.layout.menu_sub_adapter, this.mService);
        this.mOptionAdapter = new SpecialBaseMenuAdapter(context, R.layout.menu_sub_adapter, this.mOptionService);
        this.mScreenTitleView = (TextView) currentView.findViewById(R.id.menu_screen_title);
        this.mItemNameView = (TextView) currentView.findViewById(R.id.menu_item_name);
        return currentView;
    }

    protected int getLayoutID() {
        return R.layout.menu_special;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        String currentItemId = this.mService.getMenuItemId();
        this.mScreenTitleView.setText(this.mService.getMenuItemText(currentItemId));
        this.mLastOptionItemId = null;
        this.mSpecialScreenView.setOnSpecialScreenItemSelectedListener(this);
        MenuDataParcelable parcelable = (MenuDataParcelable) this.data.getParcelable(MenuDataParcelable.KEY);
        updateSpecialScreenView(!BACK.equals(parcelable.getItemId()));
    }

    private void updateSpecialScreenView(boolean storeCancelItemId) {
        ArrayList<String> list = getMenuItemList();
        this.mOpSubDial = allhasSubArray();
        this.mAdapter.setMenuItemList(list);
        this.mSpecialScreenView.setAdapter(this.mAdapter);
        String currentItemId = this.mService.getMenuItemId();
        String currentValueItemId = this.mService.getCurrentValue(currentItemId);
        if (storeCancelItemId) {
            this.mIsChangedOptionValue = new boolean[list.size()];
            this.mCancelOptionItemIdList = new String[list.size()];
            this.mIsChangedValue = false;
            this.mCancelMainItemId = currentValueItemId;
            if (this.mOpSubDial) {
                int i = 0;
                Iterator i$ = list.iterator();
                while (i$.hasNext()) {
                    String itemId = i$.next();
                    this.mIsChangedOptionValue[i] = false;
                    if (this.mService.hasSubArray(itemId)) {
                        this.mCancelOptionItemIdList[i] = this.mOptionService.getCurrentValue(itemId);
                    } else {
                        this.mCancelOptionItemIdList[i] = "";
                    }
                    i++;
                }
            }
        }
        int pos = 0;
        if (currentValueItemId != null) {
            Iterator i$2 = list.iterator();
            while (i$2.hasNext()) {
                String itemId2 = i$2.next();
                if (itemId2.equals(currentValueItemId)) {
                    break;
                } else {
                    pos++;
                }
            }
        }
        int block = list.size();
        if (block > 5) {
            block = 5;
        }
        int firstPosition = (pos - block) + 1;
        if (firstPosition < 0) {
            firstPosition = 0;
        }
        this.mSpecialScreenView.setSelection(firstPosition, pos);
        if (this.mService.isMenuItemValid(currentValueItemId)) {
            ArrayList<String> oplist = getOptionMenuItemList(currentValueItemId);
            if (oplist != null && oplist.size() != 0) {
                String value = this.mService.getMenuItemValue(currentValueItemId);
                if (PictureEffectController.MODE_MINIATURE.equals(value)) {
                    this.mOptionService.updateValueItemsAvailable(oplist);
                }
                this.mOptionAdapter.setMenuItemList(oplist);
                String optionItemId = this.mOptionService.getCurrentValue(currentValueItemId);
                currentValueItemId = optionItemId;
                if (storeCancelItemId) {
                    setCancelSetOptionValueItemId(optionItemId);
                }
                int pos2 = 0;
                Iterator i$3 = oplist.iterator();
                while (i$3.hasNext()) {
                    String itemId3 = i$3.next();
                    if (itemId3.equals(optionItemId)) {
                        break;
                    } else {
                        pos2++;
                    }
                }
                this.mSpecialScreenView.setOptionAdapter(this.mOptionAdapter, pos2);
            } else {
                this.mSpecialScreenView.setOptionAdapter(null, -1);
            }
        } else {
            this.mSpecialScreenView.setOptionAdapter(null, -1);
        }
        this.mItemNameView.setText(this.mService.getMenuItemText(currentValueItemId));
        this.mViewArea.intialize(getActivity().getApplicationContext(), this.mSpecialScreenView);
        this.mViewArea.update();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected boolean isAELCancel() {
        return false;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        this.mViewArea.terminate();
        this.mSpecialScreenView.setOnSpecialScreenItemSelectedListener(null);
        this.mCancelOptionItemIdList = null;
        this.mCancelOptionItemId = null;
        this.mAdapter.imageCacheClear();
        this.mOptionAdapter.imageCacheClear();
        this.mSpecialScreenView.setAdapter(null);
        this.mSpecialScreenView.setOptionAdapter(null, -1);
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mSpecialScreenView = null;
        this.mViewArea = null;
        this.mService = null;
        this.mOptionService = null;
        this.mAdapter = null;
        this.mOptionAdapter = null;
        this.mMovieRecView = null;
        this.mItemNameView = null;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        if (this.mUpdater != null) {
            this.mUpdater.finishMenuUpdater();
        }
        super.closeLayout();
    }

    protected int getSelection() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected ArrayList<String> getMenuItemList() {
        ArrayList<String> items = this.mService.getSupportedItemList();
        this.mService.updateValueItemsAvailable(items);
        return items;
    }

    protected ArrayList<String> getOptionMenuItemList(String itemId) {
        if (!this.mService.hasSubArray(itemId)) {
            return null;
        }
        this.mOptionService.setMenuItemId(itemId);
        return this.mOptionService.getSupportedItemList();
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

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        this.mSpecialScreenView.moveNext();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        this.mSpecialScreenView.movePrevious();
        return 1;
    }

    private boolean allhasSubArray() {
        ArrayList<String> list = this.mService.getSupportedItemList();
        for (int i = 0; i < list.size(); i++) {
            String itemId = list.get(i);
            if (this.mService.hasSubArray(itemId) && this.mService.isMenuItemValid(itemId)) {
                return true;
            }
        }
        return false;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        int opCt = this.mSpecialScreenView.getOptionItemCount();
        if (opCt <= 1) {
            return -1;
        }
        this.mSpecialScreenView.moveRight();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        int opCt = this.mSpecialScreenView.getOptionItemCount();
        if (opCt <= 1) {
            return -1;
        }
        this.mSpecialScreenView.moveLeft();
        return 1;
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
        return this.mOpSubDial ? pushedLeftKey() : pushedUpKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        return this.mOpSubDial ? pushedRightKey() : pushedDownKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        String opItemId;
        String itemId = (String) this.mSpecialScreenView.getSelectedItem();
        if (this.mService.hasSubArray(itemId) && (opItemId = this.mSpecialScreenView.getSelectedOptionItem()) != null) {
            itemId = opItemId;
        }
        doItemClickProcessing(itemId);
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        cancelValue();
        openPreviousMenu();
        return 1;
    }

    protected void setCancelSetOptionValueItemId(String itemId) {
        this.mCancelOptionItemId = itemId;
    }

    protected void cancelValue() {
        if (this.mUpdater != null) {
            this.mUpdater.cancelMenuUpdater();
        }
        if (this.mService != null) {
            if (this.mOpSubDial && this.mCancelOptionItemIdList != null && this.mIsChangedOptionValue != null) {
                int size = this.mIsChangedOptionValue.length;
                for (int i = 0; i < size; i++) {
                    if (this.mIsChangedOptionValue[i]) {
                        String opitemId = this.mCancelOptionItemIdList[i];
                        if (!opitemId.equals(this.mCancelOptionItemId)) {
                            this.mService.execCurrentMenuItem(opitemId, false);
                        } else {
                            this.mService.execCurrentMenuItem(opitemId);
                        }
                    }
                }
            }
            if (this.mCancelMainItemId != null && this.mIsChangedValue) {
                this.mService.execCurrentMenuItem(this.mCancelMainItemId, false);
            }
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        String itemId = (String) this.mSpecialScreenView.getSelectedItem();
        if (this.mService.hasSubArray(itemId)) {
            ArrayList<String> oplist = getOptionMenuItemList(itemId);
            String optionItemId = null;
            if (oplist != null && oplist.size() != 0) {
                optionItemId = (this.mSpecialScreenView.getOptionAdapter() == null || !this.mService.isMenuItemValid(itemId)) ? this.mService.getCurrentValue(itemId) : this.mSpecialScreenView.getSelectedOptionItem();
            }
            if (optionItemId != null) {
                itemId = optionItemId;
            }
        }
        guideResources.add(this.mService.getMenuItemText(itemId));
        guideResources.add(this.mService.getMenuItemGuideText(itemId));
    }

    public void onItemSelected(SpecialScreenView adapterView, View view, int position, long id) {
        String itemId = (String) adapterView.getItemAtPosition(position);
        String execType = this.mService.getMenuItemExecType(itemId);
        if ((MenuTable.SET_VALUE.equals(execType) || MenuTable.NEXT_LAYOUT.equals(execType)) && this.mService.isMenuItemValid(itemId)) {
            this.mService.execCurrentMenuItem(itemId);
            this.mIsChangedValue = true;
        }
        this.mItemNameView.setText(this.mService.getMenuItemText(itemId));
        if (this.mService.hasSubArray(itemId)) {
            ArrayList<String> oplist = getOptionMenuItemList(itemId);
            int pos = 0;
            if (oplist != null && oplist.size() != 0) {
                String value = this.mService.getMenuItemValue(itemId);
                if (PictureEffectController.MODE_MINIATURE.equals(value)) {
                    this.mOptionService.updateValueItemsAvailable(oplist);
                }
                String curOptionItemId = this.mService.getCurrentValue(itemId);
                this.mItemNameView.setText(this.mOptionService.getMenuItemText(curOptionItemId));
                if (this.mService.isMenuItemValid(itemId)) {
                    if (curOptionItemId != null) {
                        Iterator i$ = oplist.iterator();
                        while (i$.hasNext()) {
                            String optionItemId = i$.next();
                            if (optionItemId.equals(curOptionItemId)) {
                                break;
                            } else {
                                pos++;
                            }
                        }
                    }
                    this.mOptionAdapter.setMenuItemList(oplist);
                    this.mSpecialScreenView.setOptionAdapter(this.mOptionAdapter, pos);
                } else {
                    this.mSpecialScreenView.setOptionAdapter(null, -1);
                }
            } else {
                this.mSpecialScreenView.setOptionAdapter(null, -1);
            }
        } else {
            this.mSpecialScreenView.setOptionAdapter(null, -1);
        }
        if (this.mLastOptionItemId != null && !this.mOptionService.isMenuItemValid(this.mLastOptionItemId)) {
            this.mSpecialScreenView.requestLayout();
            this.mLastOptionItemId = null;
        }
        this.mViewArea.update();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenView.OnItemSelectedListener
    public void onNothingSelected(SpecialScreenView parent) {
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenView.OnItemSelectedListener
    public void onOptionItemSelected(SpecialScreenView parent, View view, int position, long id) {
        String optionItemId = parent.getOptionItemAtPosition(position);
        if (this.mOptionService.isMenuItemValid(optionItemId)) {
            this.mOptionService.execCurrentMenuItem(optionItemId);
            int mainPos = parent.getSelectedItemPosition();
            this.mIsChangedOptionValue[mainPos] = true;
        }
        this.mItemNameView.setText(this.mOptionService.getMenuItemText(optionItemId));
        this.mLastOptionItemId = optionItemId;
        this.mSpecialScreenView.requestLayout();
        this.mViewArea.update();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected void onDigitalZoomOnOff(boolean onoff) {
        if (!isParentItemAvailable()) {
            cancelValue();
            checkCaution();
        } else {
            updateSpecialScreenView(false);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected void onDeviceStatusChanged() {
        if (!isParentItemAvailable()) {
            cancelValue();
            checkCaution();
        } else {
            updateSpecialScreenView(false);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected void onStreamWriterStatusChanged() {
        if (!isParentItemAvailable()) {
            cancelValue();
            checkCaution();
        } else {
            updateSpecialScreenView(false);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected void onFocusModeChanged() {
        ArrayList<String> oplist;
        String currentItemId = this.mService.getMenuItemId();
        if (PictureEffectController.PICTUREEFFECT.equals(currentItemId)) {
            String currentValueItemId = this.mService.getCurrentValue(currentItemId);
            int pos = 0;
            String value = this.mService.getMenuItemValue(currentValueItemId);
            if (PictureEffectController.MODE_MINIATURE.equals(value) && (oplist = getOptionMenuItemList(currentValueItemId)) != null && oplist.size() != 0) {
                this.mOptionService.updateValueItemsAvailable(oplist);
                this.mOptionAdapter.setMenuItemList(oplist);
                String optionItemId = this.mOptionService.getCurrentValue(currentValueItemId);
                this.mItemNameView.setText(this.mOptionService.getMenuItemText(optionItemId));
                Iterator i$ = oplist.iterator();
                while (i$.hasNext()) {
                    String itemId = i$.next();
                    if (itemId.equals(optionItemId)) {
                        break;
                    } else {
                        pos++;
                    }
                }
                this.mSpecialScreenView.setOptionAdapter(this.mOptionAdapter, pos);
            }
            this.mSpecialScreenView.setSelection(this.mSpecialScreenView.getSelectedItemPosition());
        } else if ("ExposureMode_movie".equals(currentItemId) || ExposureModeController.MOVIE_MODE_SUB.equals(currentItemId)) {
            updateSpecialScreenView(false);
        }
        this.mViewArea.update();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected void onAFMFSwitchChanged() {
        closeMenuLayout(null);
    }
}
