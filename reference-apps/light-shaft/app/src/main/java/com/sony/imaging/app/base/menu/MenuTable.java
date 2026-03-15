package com.sony.imaging.app.base.menu;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.util.Xml;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: classes.dex */
public class MenuTable {
    private static final String CAUTION_ID = "CautionID";
    private static final String CONFIG_CLASS = "ConfigClass";
    private static final String CUSTOM_START_LAYOUT_ID = "CustomStartLayoutID";
    private static final boolean DEBUG = false;
    private static final String EXEC_TYPE = "ExecType";
    private static final String FN_ACTIVE_ICON = "FnActiveIcon";
    private static final String FN_NEXT_MENU_ID = "FnNextMenuID";
    private static final String FN_OPTION_ICON_RES = "FnOptionIconRes";
    private static final String FN_VALUE = "FnValue";
    private static final String GUIDE_RES = "GuideRes";
    private static final String ICON_RES = "IconRes";
    private static final String ITEM_ID = "ItemId";
    private static final String LAYER1_ICON_RES = "Layer1IconRes";
    private static final String MENUDATAFILENAME = "MenuData.xml";
    public static final String NEXT_LAYOUT = "NEXT_LAYOUT";
    public static final String NEXT_LAYOUT_WITHOUT_SET = "NEXT_LAYOUT_WITHOUT_SET";
    private static final String NEXT_MENU_ID = "NextMenuID";
    public static final String NEXT_STATE = "NEXT_STATE";
    public static final String NO_ACTION = "NO_ACTION";
    private static final String OPTION_STR = "OptionStr";
    private static final String SELECTED_ICON_RES = "SelectedIconRes";
    public static final String SET_VALUE = "SET_VALUE";
    public static final String SET_VALUE_ONLY_CLICK = "SET_VALUE_ONLY_CLICK";
    private static final String TAG = "MenuTable";
    private static final String TEXT_RES = "TextRes";
    private static final String UPDATE_TAG = "UpdateTag";
    private static final String VALUE = "Value";
    private static MenuTable mMenuTable = new MenuTable();
    private CmnViewMenuItem mRoot = null;
    private String mOpeningFileName = "";

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class CmnViewMenuItem {
        ArrayList<String> cautionID;
        String configClass;
        String customStartLayoutID;
        Map<String, String> elements;
        String execType;
        String fnActiveIcon;
        String fnNextMenuID;
        String fnOptionIconRes;
        String fnValue;
        String guideRes;
        String iconRes;
        String itemId;
        String layer1IconRes;
        String nextMenuID;
        String optionStr;
        CmnViewMenuItem parent;
        String selectedIconRes;
        SparseArray<CmnViewMenuItem> subItemArray;
        String textRes;
        String updateTag;
        String value;

        CmnViewMenuItem() {
        }
    }

    private MenuTable() {
    }

    public static MenuTable getInstance() {
        return mMenuTable;
    }

    public void createDefaultMenuTree(Context context) {
        createMenuTree(context, null, null);
    }

    public synchronized CmnViewMenuItem createMenuTree(Context context, String topMenuName, String menuDataFile) {
        CmnViewMenuItem topMenu;
        CmnViewMenuItem cmnViewMenuItem;
        XmlPullParser parser = Xml.newPullParser();
        if (menuDataFile == null) {
            menuDataFile = MENUDATAFILENAME;
        }
        if (!this.mOpeningFileName.equals(menuDataFile)) {
            this.mOpeningFileName = menuDataFile;
            try {
                InputStream fis = context.getAssets().open(this.mOpeningFileName);
                try {
                    parser.setInput(fis, "UTF-8");
                } catch (XmlPullParserException e) {
                    Log.e(TAG, "setInput error.");
                    e.printStackTrace();
                }
                try {
                    this.mRoot = getMenu(parser);
                } catch (IOException e2) {
                    e2.printStackTrace();
                } catch (XmlPullParserException e3) {
                    e3.printStackTrace();
                }
            } catch (IOException e4) {
                Log.e(TAG, "File no found error. Please select valid MenuData.xml");
                e4.printStackTrace();
                cmnViewMenuItem = null;
            }
        }
        if (topMenuName != null) {
            topMenu = findMenuItem(this.mRoot, topMenuName);
        } else {
            topMenu = this.mRoot;
        }
        cmnViewMenuItem = topMenu;
        return cmnViewMenuItem;
    }

    public CmnViewMenuItem findMenuItem(CmnViewMenuItem item, String name) {
        logcat("findMenuItem start");
        if (name == null) {
            return null;
        }
        if (item.itemId.equals(name)) {
            return item;
        }
        if (item.subItemArray == null) {
            return null;
        }
        int array_size = item.subItemArray.size();
        for (int i = 0; i < array_size; i++) {
            CmnViewMenuItem found = item.subItemArray.get(i);
            if (found.itemId.equals(name)) {
                return found;
            }
        }
        for (int i2 = 0; i2 < array_size; i2++) {
            CmnViewMenuItem found2 = findMenuItem(item.subItemArray.get(i2), name);
            if (found2 != null) {
                return found2;
            }
        }
        return null;
    }

    private CmnViewMenuItem getMenu(XmlPullParser parse) throws XmlPullParserException, IOException {
        ArrayList<CmnViewMenuItem> layerList = new ArrayList<>();
        int item = parse.getEventType();
        while (item != 1) {
            if (parse.getName() != null) {
                int currentLayerDepth = parse.getDepth() - 1;
                if (parse.getName().equals("Root") && item == 2) {
                    CmnViewMenuItem root = setMenuItem(parse);
                    layerList.add(currentLayerDepth, root);
                } else if (item == 2) {
                    CmnViewMenuItem sub = setMenuItem(parse);
                    layerList.add(currentLayerDepth, sub);
                    CmnViewMenuItem menuItem = layerList.get(currentLayerDepth - 1);
                    sub.parent = menuItem;
                    if (menuItem.subItemArray == null) {
                        menuItem.subItemArray = new SparseArray<>();
                    }
                    menuItem.subItemArray.put(menuItem.subItemArray.size(), sub);
                }
            }
            item = parse.next();
        }
        return layerList.get(0);
    }

    private CmnViewMenuItem setMenuItem(XmlPullParser parse) {
        CmnViewMenuItem item = new CmnViewMenuItem();
        int attributeNum = parse.getAttributeCount();
        for (int attr = 0; attr < attributeNum; attr++) {
            String attrname = parse.getAttributeName(attr);
            String value = parse.getAttributeValue(attr);
            if (!value.equals("NULL") && !value.equals("")) {
                if (attrname.equals("ItemId")) {
                    item.itemId = value;
                } else if (attrname.equals(TEXT_RES)) {
                    item.textRes = value;
                } else if (attrname.equals(GUIDE_RES)) {
                    item.guideRes = value;
                } else if (attrname.equals(ICON_RES)) {
                    item.iconRes = value;
                } else if (attrname.equals(SELECTED_ICON_RES)) {
                    item.selectedIconRes = value;
                } else if (attrname.equals(LAYER1_ICON_RES)) {
                    item.layer1IconRes = value;
                } else if (attrname.equals(CONFIG_CLASS)) {
                    item.configClass = value;
                } else if (attrname.startsWith(CAUTION_ID)) {
                    if (!value.equals("") && !value.equals(ISOSensitivityController.ISO_AUTO)) {
                        if (item.cautionID == null) {
                            item.cautionID = new ArrayList<>();
                        }
                        item.cautionID.add(value);
                    }
                } else if (attrname.equals(OPTION_STR)) {
                    item.optionStr = value;
                } else if (attrname.equals(EXEC_TYPE)) {
                    item.execType = value;
                } else if (attrname.equals(NEXT_MENU_ID)) {
                    item.nextMenuID = value;
                } else if (attrname.equals(UPDATE_TAG)) {
                    item.updateTag = value;
                } else if (attrname.equals(FN_NEXT_MENU_ID)) {
                    item.fnNextMenuID = value;
                } else if (attrname.equals(FN_OPTION_ICON_RES)) {
                    item.fnOptionIconRes = value;
                } else if (attrname.equals(CUSTOM_START_LAYOUT_ID)) {
                    item.customStartLayoutID = value;
                } else if (attrname.equals(VALUE)) {
                    item.value = value;
                } else if (attrname.equals(FN_ACTIVE_ICON)) {
                    item.fnActiveIcon = value;
                } else if (attrname.equals(FN_VALUE)) {
                    item.fnValue = value;
                } else {
                    if (item.elements == null) {
                        item.elements = new HashMap();
                    }
                    item.elements.put(attrname, value);
                }
            }
        }
        return item;
    }

    private void logcat(String str) {
    }
}
