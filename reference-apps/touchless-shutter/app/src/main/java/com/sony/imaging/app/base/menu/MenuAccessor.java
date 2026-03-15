package com.sony.imaging.app.base.menu;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import com.sony.imaging.app.base.menu.MenuTable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/* loaded from: classes.dex */
public class MenuAccessor {
    private static final boolean DEBUG = false;
    private static final String NOT_FOUND_ITEMID = "Not found: ";
    private static final String TAG = "MenuAccessor";
    private static HashMap<String, DisplayMenuItem> mDisplayMenuItemPool = new HashMap<>();
    private Context mContext;
    private ArrayList<String> mMenuList;
    private MenuTable mMenuTable;
    private Stack<HistoryItem> mMoveHistory;
    private MenuTable.CmnViewMenuItem mOpening;
    private MenuTable.CmnViewMenuItem mTopMenu;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MenuAccessor() {
        this.mTopMenu = null;
        this.mOpening = null;
        this.mMenuList = new ArrayList<>();
        this.mMoveHistory = new Stack<>();
        this.mMenuTable = MenuTable.getInstance();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MenuAccessor(MenuAccessor accessor) {
        this.mTopMenu = null;
        this.mOpening = null;
        this.mContext = accessor.mContext;
        this.mMenuTable = accessor.mMenuTable;
        this.mTopMenu = accessor.mTopMenu;
        this.mOpening = accessor.mOpening;
        this.mMenuList = new ArrayList<>(accessor.mMenuList);
        this.mMoveHistory = (Stack) accessor.mMoveHistory.clone();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void initialize(Context con) {
        this.mContext = con;
        this.mMenuList.clear();
        this.mMoveHistory.clear();
        this.mTopMenu = this.mMenuTable.createMenuTree(this.mContext, null, null);
        this.mOpening = this.mTopMenu;
        updateMainMenuList(this.mTopMenu.itemId);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void initialize(Context con, String topMenuItem, String fileName) {
        this.mContext = con;
        this.mMenuList.clear();
        this.mMoveHistory.clear();
        this.mTopMenu = this.mMenuTable.createMenuTree(this.mContext, topMenuItem, fileName);
        if (this.mTopMenu == null) {
            this.mTopMenu = this.mMenuTable.createMenuTree(this.mContext, null, fileName);
        }
        this.mOpening = this.mTopMenu;
        updateMainMenuList(topMenuItem);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class DisplayMenuItem {
        public ArrayList<String> cautionID;
        public String configClass;
        public String customStartLaoutID;
        public Map<String, String> elements;
        public String execType;
        public String fnActiveIcon;
        public String fnNextMenuID;
        public String fnOptionIconRes;
        public String fnValue;
        public String guideRes;
        public boolean hasSubArray;
        public String iconRes;
        public boolean isFnValid;
        public boolean isValid;
        public String itemId;
        public String layer1IconRes;
        public String nextMenuID;
        public String optionStr;
        public String parentItemId;
        public String selectedIconRes;
        public ArrayList<String> subItemArray;
        public String textRes;
        public String updateTag;
        public String value;

        public DisplayMenuItem() {
            this.isValid = true;
            this.isFnValid = true;
        }

        private DisplayMenuItem(MenuTable.CmnViewMenuItem sub) {
            this.isValid = true;
            this.isFnValid = true;
            this.itemId = sub.itemId;
            this.textRes = sub.textRes;
            this.guideRes = sub.guideRes;
            this.iconRes = sub.iconRes;
            this.selectedIconRes = sub.selectedIconRes;
            this.layer1IconRes = sub.layer1IconRes;
            this.configClass = sub.configClass;
            this.cautionID = sub.cautionID;
            this.optionStr = sub.optionStr;
            this.execType = sub.execType;
            this.nextMenuID = sub.nextMenuID;
            this.updateTag = sub.updateTag;
            if (sub.subItemArray != null) {
                this.hasSubArray = true;
                int size = sub.subItemArray.size();
                this.subItemArray = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    MenuTable.CmnViewMenuItem cvmi = sub.subItemArray.get(i);
                    this.subItemArray.add(cvmi.itemId);
                }
            }
            this.fnNextMenuID = sub.fnNextMenuID;
            this.fnOptionIconRes = sub.fnOptionIconRes;
            this.customStartLaoutID = (sub.customStartLayoutID == null || sub.customStartLayoutID.length() == 0) ? sub.nextMenuID : sub.customStartLayoutID;
            this.value = sub.value;
            this.fnActiveIcon = sub.fnActiveIcon;
            this.fnValue = (sub.fnValue == null || sub.fnValue.length() == 0) ? sub.value : sub.fnValue;
            this.parentItemId = sub.parent == null ? null : sub.parent.itemId;
            this.elements = sub.elements;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DisplayMenuItem getParentItem() {
        return getDisplayItem(this.mOpening.itemId);
    }

    public ArrayList<String> getMenuList(String topMenuName) {
        if (this.mTopMenu == null) {
            this.mTopMenu = this.mMenuTable.createMenuTree(this.mContext, topMenuName, null);
        }
        MenuTable.CmnViewMenuItem cmnViewMenuItem = this.mMenuTable.findMenuItem(this.mTopMenu, topMenuName);
        if (cmnViewMenuItem == null || cmnViewMenuItem.subItemArray == null) {
            return null;
        }
        int size = cmnViewMenuItem.subItemArray.size();
        ArrayList<String> ret = new ArrayList<>(size);
        for (int count = 0; size > count; count++) {
            MenuTable.CmnViewMenuItem subItem = cmnViewMenuItem.subItemArray.get(count);
            ret.add(subItem.itemId);
        }
        return ret;
    }

    public void clearDisplayMenuItemPool() {
        mDisplayMenuItemPool.clear();
    }

    public ArrayList<String> getSpecifiedMenuList(String itemId) {
        ArrayList<String> ret = null;
        if (itemId != null) {
            MenuTable.CmnViewMenuItem partTreeRoot = this.mMenuTable.createMenuTree(this.mContext, itemId, null);
            if (partTreeRoot != null) {
                if (partTreeRoot.subItemArray != null) {
                    int size = partTreeRoot.subItemArray.size();
                    ret = new ArrayList<>(size);
                    SparseArray<MenuTable.CmnViewMenuItem> subItemArray = partTreeRoot.subItemArray;
                    for (int i = 0; i < size; i++) {
                        MenuTable.CmnViewMenuItem cvmi = subItemArray.get(i);
                        ret.add(cvmi.itemId);
                    }
                }
            } else {
                Log.e(TAG, "Not found " + itemId + "'s CmnViewMenuItem");
            }
        }
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ArrayList<String> getOpeningMenuList() {
        return this.mMenuList;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ArrayList<String> updateMainMenuList(String topMenuName) {
        if (topMenuName == null) {
            this.mOpening = this.mTopMenu;
            return setMenuList(this.mOpening);
        }
        if (this.mTopMenu.itemId.equals(topMenuName)) {
            this.mOpening = this.mTopMenu;
            return setMenuList(this.mTopMenu);
        }
        if (this.mOpening != null && this.mMenuTable.findMenuItem(this.mOpening, topMenuName) != null) {
            this.mOpening = this.mMenuTable.findMenuItem(this.mOpening, topMenuName);
            return setMenuList(this.mOpening);
        }
        this.mOpening = this.mMenuTable.findMenuItem(this.mTopMenu, topMenuName);
        return setMenuList(this.mOpening);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean pushMenuHistory(HistoryItem item) {
        if (item == null) {
            return DEBUG;
        }
        this.mMoveHistory.push(item);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HistoryItem popMenuHistory() {
        if (this.mMoveHistory.isEmpty()) {
            return null;
        }
        return this.mMoveHistory.pop();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isExistMenuHistory() {
        return this.mMoveHistory.isEmpty();
    }

    private ArrayList<String> setMenuList(MenuTable.CmnViewMenuItem openItem) {
        if (openItem == null) {
            openItem = this.mTopMenu;
        }
        if (openItem.subItemArray == null) {
            this.mMenuList.clear();
            return null;
        }
        int size = openItem.subItemArray.size();
        this.mMenuList.clear();
        for (int count = 0; size > count; count++) {
            MenuTable.CmnViewMenuItem subItem = openItem.subItemArray.get(count);
            this.mMenuList.add(subItem.itemId);
        }
        return this.mMenuList;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DisplayMenuItem getDisplayItem(String itemId) {
        DisplayMenuItem item = mDisplayMenuItemPool.get(itemId);
        if (item == null) {
            MenuTable.CmnViewMenuItem cvmi = this.mMenuTable.findMenuItem(this.mOpening, itemId);
            if (cvmi != null) {
                item = new DisplayMenuItem(cvmi);
            } else {
                Log.w(TAG, NOT_FOUND_ITEMID + itemId);
            }
            mDisplayMenuItemPool.put(itemId, item);
        }
        return item;
    }

    DisplayMenuItem createDisplayMenuItem(String itemId) {
        MenuTable.CmnViewMenuItem cvmi = this.mMenuTable.findMenuItem(this.mOpening, itemId);
        if (cvmi == null) {
            return null;
        }
        DisplayMenuItem dmi = new DisplayMenuItem(cvmi);
        return dmi;
    }

    public String getSubItemText(String itemId, ArrayList<String> currentValue) {
        String ret = "";
        MenuTable.CmnViewMenuItem foundItem = this.mOpening;
        int size = currentValue.size();
        for (int i = 0; size > i; i++) {
            foundItem = this.mMenuTable.findMenuItem(foundItem, currentValue.get(i));
            if (foundItem != null && foundItem.textRes != null) {
                ret = foundItem.textRes;
            }
        }
        return ret;
    }

    public String getSubItemGuideText(String itemId, ArrayList<String> currentValue) {
        String ret = "";
        MenuTable.CmnViewMenuItem foundItem = this.mOpening;
        int size = currentValue.size();
        for (int i = 0; size > i; i++) {
            foundItem = this.mMenuTable.findMenuItem(foundItem, currentValue.get(i));
            if (foundItem != null && foundItem.guideRes != null) {
                ret = foundItem.guideRes;
            }
        }
        return ret;
    }

    public String getSubItemIcon(String itemId, ArrayList<String> currentValue) {
        String ret = "";
        MenuTable.CmnViewMenuItem foundItem = this.mOpening;
        int size = currentValue.size();
        for (int i = 0; size > i; i++) {
            foundItem = this.mMenuTable.findMenuItem(foundItem, currentValue.get(i));
            if (foundItem != null && foundItem.iconRes != null) {
                ret = foundItem.iconRes;
            }
        }
        return ret;
    }

    public String getSubItemSelectedIcon(String itemId, ArrayList<String> currentValue) {
        String ret = "";
        MenuTable.CmnViewMenuItem foundItem = this.mOpening;
        int size = currentValue.size();
        for (int i = 0; size > i; i++) {
            foundItem = this.mMenuTable.findMenuItem(foundItem, currentValue.get(i));
            if (foundItem != null && foundItem.selectedIconRes != null) {
                ret = foundItem.selectedIconRes;
            }
        }
        return ret;
    }

    public String getSubItemLayer1Icon(String itemId, ArrayList<String> currentValue) {
        String ret = "";
        MenuTable.CmnViewMenuItem foundItem = this.mOpening;
        int size = currentValue.size();
        for (int i = 0; size > i; i++) {
            foundItem = this.mMenuTable.findMenuItem(foundItem, currentValue.get(i));
            if (foundItem != null && foundItem.layer1IconRes != null) {
                ret = foundItem.layer1IconRes;
            }
        }
        return ret;
    }

    public String getNextMenuId(String itemId) {
        MenuTable.CmnViewMenuItem foundItem = this.mMenuTable.findMenuItem(this.mOpening, itemId);
        return foundItem != null ? foundItem.nextMenuID : "";
    }

    public String getFnNextMenuId(String itemId) {
        MenuTable.CmnViewMenuItem foundItem = this.mMenuTable.findMenuItem(this.mOpening, itemId);
        return foundItem != null ? foundItem.fnNextMenuID : "";
    }

    public String getClassName(String itemId) {
        MenuTable.CmnViewMenuItem foundItem = this.mMenuTable.findMenuItem(this.mOpening, itemId);
        return foundItem != null ? foundItem.configClass : "";
    }
}
