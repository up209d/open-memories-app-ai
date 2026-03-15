package com.sony.imaging.app.base.menu.layout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.menu.MenuDataParcelable;
import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.widget.CreativeStyleOptionView;
import com.sony.imaging.app.fw.ICustomKey;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class CreativeStyleMenuLayout extends SpecialScreenMenuLayout {
    private static final String TAG = "CreativeStyleMenuLayout";
    CreativeStyleController.CreativeStyleOptions mOldOptionParam;
    int mOptionFocusPos;
    CreativeStyleController.CreativeStyleOptions mOptionValue;
    CreativeStyleOptionView mContrastOption = null;
    CreativeStyleOptionView mSaturationOption = null;
    CreativeStyleOptionView mSharpnessOption = null;
    String mCancelItemId = "";
    boolean mSaturationAvailable = true;
    final int PUSH_RIGHT_KEY = 0;
    final int PUSH_LEFT_KEY = 1;
    Layout mLayoutStatus = Layout.SUB;
    final int CONTRAST = 0;
    final int SATURATION = 1;
    final int SHARPNESS = 2;
    protected TextView mCsItemName = null;
    protected ImageView mShadeArea = null;
    ArrayList<String> list = null;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum ChangeValue {
        UP,
        DOWN,
        NONE
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum Layout {
        SUB,
        OPTION
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public ViewGroup onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup currentView = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        this.mShadeArea = (ImageView) currentView.findViewById(R.id.specialscreen_shade_area);
        this.mCsItemName = (TextView) currentView.findViewById(R.id.cs_item_name);
        this.mContrastOption = (CreativeStyleOptionView) currentView.findViewById(R.id.optionContrast);
        this.mSaturationOption = (CreativeStyleOptionView) currentView.findViewById(R.id.optionSaturation);
        this.mSharpnessOption = (CreativeStyleOptionView) currentView.findViewById(R.id.optionSharpness);
        return currentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout
    protected int getLayoutID() {
        return R.layout.menu_creativestyle;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        this.mShadeArea = null;
        this.mCsItemName = null;
        this.mContrastOption = null;
        this.mSaturationOption = null;
        this.mSharpnessOption = null;
        super.onDestroyView();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.mLayoutStatus = Layout.SUB;
        setSubManuLayout();
        this.mShadeArea.setVisibility(4);
        this.list = this.mService.getSupportedItemList();
        MenuDataParcelable parcelable = (MenuDataParcelable) this.data.getParcelable(MenuDataParcelable.KEY);
        String itemId = parcelable.getItemId();
        if (itemId.equals("back")) {
            String parentItemId = this.mService.getMenuItemId();
            setSelection(this.mService.getCurrentValue(parentItemId));
        } else {
            this.mCancelItemId = this.mService.getCurrentValue(itemId);
            setSelection(this.mCancelItemId);
        }
        CreativeStyleController ctrl = CreativeStyleController.getInstance();
        this.mContrastOption.init(CreativeStyleController.CONTRAST, ctrl.getSupportedValue(CreativeStyleController.CONTRAST));
        this.mSaturationOption.init(CreativeStyleController.SATURATION, ctrl.getSupportedValue(CreativeStyleController.SATURATION));
        this.mSharpnessOption.init(CreativeStyleController.SHARPNESS, ctrl.getSupportedValue(CreativeStyleController.SHARPNESS));
        update(this.mSpecialScreenView.getSelectedItemPosition());
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenView.OnItemSelectedListener
    public void onItemSelected(SpecialScreenView parent, View view, int position, long id) {
        if (this.mLayoutStatus == Layout.SUB) {
            super.onItemSelected(parent, view, position, id);
            update(position);
            setSubManuLayout();
        }
        Log.d(TAG, "onItemSelected pos:" + position);
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void update(int position) {
        if (this.mItemNameView != null && this.mLayoutStatus == Layout.SUB) {
            this.mItemNameView.setText(this.mService.getMenuItemText(this.mAdapter.getItem(position)));
            this.mItemNameView.setVisibility(0);
            changeOptionAvailable(position);
            updateOptionValue();
        }
        Log.d(TAG, "update pos:" + position);
    }

    private void setOption(int option, String value) {
        switch (option) {
            case 0:
                this.mOptionValue.contrast = Integer.valueOf(value).intValue();
                break;
            case 1:
                this.mOptionValue.saturation = Integer.valueOf(value).intValue();
                break;
            case 2:
                this.mOptionValue.sharpness = Integer.valueOf(value).intValue();
                break;
        }
        CreativeStyleController.getInstance().setDetailValue(this.mOptionValue);
    }

    protected void moveOption(int pushKey) {
        boolean isMove = false;
        if (this.mLayoutStatus == Layout.SUB) {
            if (pushKey == 0) {
                this.mOptionFocusPos = 0;
                this.mContrastOption.setCursol();
                isMove = true;
            } else if (pushKey == 1) {
                this.mOptionFocusPos = 2;
                this.mSharpnessOption.setCursol();
                isMove = true;
            }
            if (isMove) {
                this.mLayoutStatus = Layout.OPTION;
                ArrayList<String> list = this.mService.getSupportedItemList();
                int position = this.mSpecialScreenView.getSelectedItemPosition();
                String itemId = list.get(position);
                String value = this.mService.getMenuItemValue(itemId);
                this.mOldOptionParam = (CreativeStyleController.CreativeStyleOptions) CreativeStyleController.getInstance().getDetailValue(value);
                modifyOption(ChangeValue.NONE);
            }
        }
    }

    protected void moveSubLayout(boolean cancelSetting) {
        if (this.mLayoutStatus == Layout.OPTION) {
            this.mLayoutStatus = Layout.SUB;
            if (cancelSetting) {
                CreativeStyleController.getInstance().setDetailValue(this.mOldOptionParam);
                updateOptionValue();
            }
            setSubManuLayout();
        }
    }

    private void modifyOption(ChangeValue change) {
        CreativeStyleOptionView option = null;
        int resID = 0;
        switch (this.mOptionFocusPos) {
            case 0:
                this.mContrastOption.setCursol();
                this.mSaturationOption.releaseCursol();
                this.mSharpnessOption.releaseCursol();
                option = this.mContrastOption;
                resID = android.R.string.config_screenRecorderComponent;
                break;
            case 1:
                this.mContrastOption.releaseCursol();
                this.mSaturationOption.setCursol();
                this.mSharpnessOption.releaseCursol();
                option = this.mSaturationOption;
                resID = android.R.string.config_screenshotErrorReceiverComponent;
                break;
            case 2:
                this.mContrastOption.releaseCursol();
                this.mSaturationOption.releaseCursol();
                this.mSharpnessOption.setCursol();
                option = this.mSharpnessOption;
                resID = android.R.string.config_screenshotServiceComponent;
                break;
        }
        if (option != null) {
            if (change == ChangeValue.UP) {
                option.moveNext();
            } else if (change == ChangeValue.DOWN) {
                option.movePrevious();
            }
            setOption(this.mOptionFocusPos, option.getCurrentValue());
            this.mCsItemName.setText(resID);
            this.mCsItemName.setVisibility(0);
            this.mShadeArea.setVisibility(0);
            updateOptionValue();
        }
    }

    private void changeOptionAvailable(int position) {
        ArrayList<String> list = this.mService.getSupportedItemList();
        String itemId = list.get(position);
        String value = this.mService.getMenuItemValue(itemId);
        CreativeStyleController.CreativeStyleOptionsAvailable optionAvailable = (CreativeStyleController.CreativeStyleOptionsAvailable) CreativeStyleController.getInstance().getOptionAvailable(value);
        this.mContrastOption.setAvailable(optionAvailable.mContrast);
        this.mSaturationOption.setAvailable(optionAvailable.mSaturation);
        this.mSharpnessOption.setAvailable(optionAvailable.mSharpness);
    }

    private void updateOptionValue() {
        ArrayList<String> list = this.mService.getSupportedItemList();
        int position = this.mSpecialScreenView.getSelectedItemPosition();
        String itemId = list.get(position);
        String value = this.mService.getMenuItemValue(itemId);
        this.mOptionValue = (CreativeStyleController.CreativeStyleOptions) CreativeStyleController.getInstance().getDetailValue(value);
        this.mContrastOption.setCurrentValue(this.mOptionValue.contrast);
        this.mSaturationOption.setCurrentValue(this.mOptionValue.saturation);
        this.mSharpnessOption.setCurrentValue(this.mOptionValue.sharpness);
    }

    protected void setSubManuLayout() {
        this.mCsItemName.setVisibility(4);
        this.mContrastOption.releaseCursol();
        this.mSaturationOption.releaseCursol();
        this.mSharpnessOption.releaseCursol();
        this.mShadeArea.setVisibility(4);
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        if (this.mLayoutStatus == Layout.SUB) {
            String itemId = (String) this.mSpecialScreenView.getSelectedItem();
            if (itemId != null) {
                guideResources.add(this.mService.getMenuItemText(itemId));
                guideResources.add(this.mService.getMenuItemGuideText(itemId));
                return;
            }
            return;
        }
        String title = null;
        String guide = null;
        switch (this.mOptionFocusPos) {
            case 0:
                title = getString(android.R.string.config_screenRecorderComponent);
                guide = getString(android.R.string.permlab_disableKeyguard);
                break;
            case 1:
                title = getString(android.R.string.config_screenshotErrorReceiverComponent);
                guide = getString(android.R.string.permlab_enableCarMode);
                break;
            case 2:
                title = getString(android.R.string.config_screenshotServiceComponent);
                guide = getString(android.R.string.permlab_exemptFromAudioRecordRestrictions);
                break;
        }
        if (title != null && guide != null) {
            guideResources.add(title);
            guideResources.add(guide);
        }
    }

    protected boolean isOptionAvailable() {
        switch (this.mOptionFocusPos) {
            case 0:
                boolean available = this.mContrastOption.getAvailable();
                return available;
            case 1:
                boolean available2 = this.mSaturationOption.getAvailable();
                return available2;
            case 2:
                boolean available3 = this.mSharpnessOption.getAvailable();
                return available3;
            default:
                return false;
        }
    }

    private void setSelection(String focusItemId) {
        ArrayList<String> list = this.mService.getSupportedItemList();
        int pos = 0;
        Iterator i$ = list.iterator();
        while (i$.hasNext()) {
            String itemId = i$.next();
            if (itemId.equals(focusItemId)) {
                break;
            } else {
                pos++;
            }
        }
        this.mSpecialScreenView.setSelection(pos);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver
    protected String getKeyConvCategory() {
        return ICustomKey.CATEGORY_MENU;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        if (this.mLayoutStatus == Layout.SUB) {
            int ret = super.pushedMenuKey();
            return ret;
        }
        moveSubLayout(true);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK1Key() {
        return pushedMenuKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        if (this.mLayoutStatus == Layout.SUB) {
            int ret = super.pushedCenterKey();
            return ret;
        }
        if (!isOptionAvailable()) {
            CautionUtilityClass.getInstance().requestTrigger(1445);
            return 1;
        }
        int ret2 = super.pushedCenterKey();
        return ret2;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        if (this.mLayoutStatus == Layout.OPTION) {
            this.mOptionFocusPos--;
            if (this.mOptionFocusPos < 0) {
                moveSubLayout(false);
            } else {
                modifyOption(ChangeValue.NONE);
            }
        } else {
            moveOption(1);
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        if (this.mLayoutStatus == Layout.OPTION) {
            this.mOptionFocusPos++;
            if (this.mOptionFocusPos > 2) {
                moveSubLayout(false);
                return 1;
            }
            modifyOption(ChangeValue.NONE);
            return 1;
        }
        moveOption(0);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        if (this.mLayoutStatus == Layout.OPTION) {
            modifyOption(ChangeValue.UP);
            return 1;
        }
        super.pushedUpKey();
        if (this.mItemNameView != null) {
            String itemId = this.list.get(this.mSpecialScreenView.getSelectedItemPosition());
            this.mItemNameView.setText(this.mService.getMenuItemText(itemId));
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        if (this.mLayoutStatus == Layout.OPTION) {
            modifyOption(ChangeValue.DOWN);
            return 1;
        }
        super.pushedDownKey();
        if (this.mItemNameView != null) {
            String itemId = this.list.get(this.mSpecialScreenView.getSelectedItemPosition());
            this.mItemNameView.setText(this.mService.getMenuItemText(itemId));
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        return pushedDownKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        return pushedUpKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        return pushedRightKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        return pushedLeftKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedFuncRingNext() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedFuncRingPrev() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftDownKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftUpKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightDownKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightUpKey() {
        return -1;
    }
}
