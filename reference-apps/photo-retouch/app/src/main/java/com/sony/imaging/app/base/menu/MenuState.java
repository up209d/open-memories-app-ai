package com.sony.imaging.app.base.menu;

import android.os.Bundle;
import android.util.Log;
import com.sony.imaging.app.base.menu.layout.BaseMenuLayout;
import com.sony.imaging.app.base.menu.layout.FnMenuLayout;
import com.sony.imaging.app.base.menu.layout.MenuLayoutListener;
import com.sony.imaging.app.base.menu.layout.UnknownItemMenuLayout;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.fw.State;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.imaging.app.util.PTag;
import java.util.ArrayList;

/* loaded from: classes.dex */
public abstract class MenuState extends State implements MenuLayoutListener {
    public static final String BOOLEAN_FINISH_MENU_STATE = "boolean_end_menu";
    public static final String FACTOR_CUSTOM_KEY = "factor_custom_key";
    public static final String FACTOR_FN_KEY = "factor_fn_key";
    public static final String FACTOR_MENU_KEY = "factor_menu_key";
    protected static final String ID_LASTBASTIONLAYOUT = "LastBastionLayout";
    public static final String ITEM_ID = "ItemId";
    public static final String LAYOUT_ID = "MenuLayoutId";
    private static final String MENUDATAFILE = "MenuData.xml";
    private static final String MSG_CAN_NOT_OPEN_LAST_BASTION_LAYOUT = "Can not open the last bastion layout. MenuState is closing";
    public static final String START_FACTOR = "StartFactor";
    private static final String TAG = "MenuState";
    private boolean doneFinishMenuState = false;
    private MenuDataParcelable mMenuData = null;
    protected boolean mbCloseLayoutsInAnyCase = true;
    private ArrayList<MenuStateClosedListener> mListenerList = new ArrayList<>();

    protected abstract void finishMenuState(Bundle bundle);

    @Override // com.sony.imaging.app.fw.KeyReceiver
    protected String getKeyConvCategory() {
        return ICustomKey.CATEGORY_MENU;
    }

    @Override // com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        return ApoWrapper.APO_TYPE.SPECIAL;
    }

    @Override // com.sony.imaging.app.fw.State
    public Integer getCautionMode() {
        return 14;
    }

    protected String getMenuDataFile() {
        return MENUDATAFILE;
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        PTag.start("Menu Opened");
        String itemId = null;
        String nextMenuID = null;
        BaseMenuService service = new BaseMenuService(getActivity());
        if (this.data != null) {
            itemId = this.data.getString(ITEM_ID);
            if (itemId == null) {
                itemId = service.getMenuItemId();
            }
            nextMenuID = this.data.getString(LAYOUT_ID);
            if (nextMenuID == null) {
                String factor = this.data.getString(START_FACTOR);
                if (FACTOR_MENU_KEY.equals(factor)) {
                    nextMenuID = service.getMenuItemNextMenuID(itemId);
                } else if (FACTOR_CUSTOM_KEY.equals(factor)) {
                    nextMenuID = service.getMenuItemCustomStartLayoutID(itemId);
                } else if (FACTOR_FN_KEY.equals(factor)) {
                    nextMenuID = FnMenuLayout.MENU_ID;
                } else {
                    nextMenuID = service.getMenuItemNextMenuID(itemId);
                }
            }
        }
        openMenuLayout(itemId, nextMenuID);
        this.doneFinishMenuState = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void openMenuLayout(String itemId, String layoutID) {
        if (this.data == null) {
            this.data = new Bundle();
        }
        String nextItemId = itemId;
        String nextLayoutId = layoutID;
        BaseMenuService baseMenuService = new BaseMenuService(getActivity());
        if (nextItemId == null) {
            nextItemId = baseMenuService.getMenuItemId();
        }
        if (nextLayoutId == null && ((nextLayoutId = baseMenuService.getMenuItemNextMenuID(nextItemId)) == null || nextLayoutId.equals(""))) {
            nextLayoutId = UnknownItemMenuLayout.MENU_ID;
        }
        if (this.mMenuData == null) {
            this.mMenuData = new MenuDataParcelable();
        }
        baseMenuService.setMenuItemId(nextItemId);
        this.mMenuData.setMenuData(getMenuDataFile(), nextItemId, (String) null, (String) null, (String) null, baseMenuService);
        this.data.putParcelable(MenuDataParcelable.KEY, this.mMenuData);
        BaseMenuLayout layout = (BaseMenuLayout) getLayout(nextLayoutId);
        if (layout != null) {
            layout.setMenuLayoutListener(this);
        }
        openLayout(nextLayoutId, this.data);
    }

    @Override // com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        closeMenuLayouts();
    }

    @Override // com.sony.imaging.app.base.menu.layout.MenuLayoutListener
    public void onClosed(Bundle bundle) {
        boolean endMenuFlag;
        if (bundle == null) {
            endMenuFlag = true;
        } else {
            boolean finishMenuState = bundle.getBoolean(BOOLEAN_FINISH_MENU_STATE);
            String nextState = bundle.getString(MenuTable.NEXT_STATE);
            endMenuFlag = finishMenuState || nextState != null;
        }
        if (endMenuFlag) {
            if (!canRemoveState()) {
                boolean b = openLastBastionLayout();
                if (!b) {
                    Log.w(TAG, MSG_CAN_NOT_OPEN_LAST_BASTION_LAYOUT);
                    return;
                }
                return;
            }
            if (!this.doneFinishMenuState) {
                this.doneFinishMenuState = true;
                finishMenuState(bundle);
            }
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.MenuLayoutListener
    public MenuState getState() {
        return this;
    }

    public void addMenuStateClosedListener(MenuStateClosedListener listener) {
        if (listener != null && !this.mListenerList.contains(listener)) {
            this.mListenerList.add(listener);
        }
    }

    public void removeMenuStateClosedListener(MenuStateClosedListener listener) {
        this.mListenerList.remove(listener);
    }

    public void closeMenuLayouts() {
        MenuStateClosedListener[] list = (MenuStateClosedListener[]) this.mListenerList.toArray(new MenuStateClosedListener[0]);
        for (MenuStateClosedListener listener : list) {
            listener.onStateClosed(null);
        }
    }

    protected boolean canRemoveState() {
        return true;
    }

    protected String getLastBastionLayoutName() {
        return ID_LASTBASTIONLAYOUT;
    }

    protected Bundle getLastBastionLayoutData() {
        this.data = new Bundle();
        MenuDataParcelable parcelable = new MenuDataParcelable();
        parcelable.setMenuDataFile(MENUDATAFILE);
        this.data.putParcelable(MenuDataParcelable.KEY, parcelable);
        return this.data;
    }

    protected boolean openLastBastionLayout() {
        closeMenuLayouts();
        String name = getLastBastionLayoutName();
        BaseMenuLayout layout = (BaseMenuLayout) getLayout(name);
        if (layout == null) {
            return false;
        }
        layout.setMenuLayoutListener(this);
        openLayout(name, getLastBastionLayoutData());
        return true;
    }
}
