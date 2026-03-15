package com.sony.imaging.app.base.menu;

import android.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import com.sony.imaging.app.base.caution.CautionID;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.menu.MenuAccessor;
import com.sony.imaging.app.base.shooting.camera.CompensationControllable;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.parameters.BooleanSupportedChecker;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.util.Environment;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public class BaseMenuService {
    protected static final String CAUTION = "isFeasibleCaution";
    private static final boolean DEBUG = false;
    private static HashMap<Class<?>, IController> EXEC_CLASS_INSTANCE_POOL = null;
    private static HashMap<String, Class<?>> EXEC_CLASS_POOL = null;
    protected static final String GET = "getValue";
    protected static final String GETAVAILABLE = "getAvailableValue";
    protected static final String GETDETAIL = "getDetailValue";
    private static final String GETINSTANCE = "getInstance";
    protected static final String GETSUPPORTED = "getSupportedValue";
    protected static final String ISAVAILABLE = "isAvailable";
    public static final int MODE_FN = 1;
    public static final int MODE_NORMAL = 0;
    protected static final String SET = "setValue";
    protected static final String SETDETAIL = "setDetailValue";
    private static final String TAG = "BaseMenuService";
    protected static String TRUE;
    private static Handler handler;
    private static SoftReference<Field[]> mCautionFieldPool;
    private static HashMap<String, int[]> mCautionIDPool;
    private static HashMap<String, Integer> mMenuItemDrawablePool;
    private static final HashMap<String, Integer> mMenuItemFnActiveIconId;
    private static HashMap<String, Integer> mMenuItemResourceIdPool;
    private static HashMap<String, CharSequence> mMenuItemTextPool;
    private static List<Runnable> mRunnableList;
    protected MenuAccessor mAccessor;
    protected Context mContext;
    protected Resources mRes;
    private static final Pattern COMPENSATION_PATTERN = Pattern.compile("-?\\d?\\d");
    public static final HashMap<Integer, Integer> CAUTION_ID_DICTIONARY = new HashMap<>();

    static {
        CAUTION_ID_DICTIONARY.put(-1, Integer.valueOf(Info.CAUTION_ID_DLAPP_INVALID_MOVIE_MODE));
        CAUTION_ID_DICTIONARY.put(-2, Integer.valueOf(Info.CAUTION_ID_DLAPP_INVALID_STILL_MODE));
        handler = new Handler();
        mRunnableList = new ArrayList();
        EXEC_CLASS_INSTANCE_POOL = new HashMap<>();
        EXEC_CLASS_POOL = new HashMap<>();
        mMenuItemTextPool = new HashMap<>();
        mMenuItemDrawablePool = new HashMap<>();
        mCautionFieldPool = new SoftReference<>(null);
        mCautionIDPool = new HashMap<>();
        mMenuItemFnActiveIconId = new HashMap<>();
        mMenuItemResourceIdPool = new HashMap<>();
        TRUE = BooleanSupportedChecker.TRUE;
    }

    public BaseMenuService(Context context) {
        this.mAccessor = null;
        this.mContext = null;
        this.mContext = context;
        this.mRes = context.getResources();
        this.mAccessor = new MenuAccessor();
        this.mAccessor.initialize(context);
    }

    public BaseMenuService(BaseMenuService service) {
        this.mAccessor = null;
        this.mContext = null;
        this.mContext = service.getContext();
        this.mRes = this.mContext.getResources();
        this.mAccessor = new MenuAccessor(service.getMenuAccessor());
    }

    MenuAccessor getMenuAccessor() {
        return this.mAccessor;
    }

    void setMenuAccessor(MenuAccessor accessor) {
        this.mAccessor = accessor;
    }

    Context getContext() {
        return this.mContext;
    }

    public ArrayList<String> getMenuItemList() {
        ArrayList<String> ret = this.mAccessor.getOpeningMenuList();
        return ret;
    }

    public void setMenuItemId(String itemId) {
        this.mAccessor.updateMainMenuList(itemId);
    }

    public void pushMenuHistory(HistoryItem item) {
        this.mAccessor.pushMenuHistory(item);
    }

    public HistoryItem popMenuHistory() {
        return this.mAccessor.popMenuHistory();
    }

    public boolean isEmptyMenuHistory() {
        return this.mAccessor.isExistMenuHistory();
    }

    private ArrayList<String> getSupportedSettingItemList(int mode) {
        return getSupportedSettingItemList(this.mAccessor.getOpeningMenuList(), 0);
    }

    private ArrayList<String> getSupportedSettingItemList(String itemId, int mode) {
        ArrayList<String> items = this.mAccessor.getSpecifiedMenuList(itemId);
        return getSupportedSettingItemList(items, mode);
    }

    protected ArrayList<String> getSupportedSettingItemList(ArrayList<String> items, int mode) {
        ArrayList<String> retList = new ArrayList<>();
        boolean hasModeDial = ModeDialDetector.hasModeDial();
        Iterator i$ = items.iterator();
        while (i$.hasNext()) {
            String itemId = i$.next();
            if (!hasModeDial || !ExposureModeController.EXPOSURE_MODE.equals(itemId)) {
                MenuAccessor.DisplayMenuItem setItem = getDisplayItem(itemId);
                if (setItem != null) {
                    String confName = setItem.configClass;
                    if (setItem.configClass == null) {
                        retList.add(itemId);
                    } else {
                        Class<?> confClass = getExecClass(confName);
                        List<String> supportedList = null;
                        try {
                            IController controller = getExecClassInstance(confClass);
                            if (mode == 1) {
                                supportedList = controller.getSupportedValue(setItem.fnValue);
                            } else {
                                supportedList = controller.getSupportedValue(setItem.value);
                            }
                        } catch (IllegalArgumentException e) {
                            Log.e(TAG, "error", e);
                        }
                        if (supportedList != null && supportedList.size() != 0) {
                            retList.add(itemId);
                        }
                    }
                }
            }
        }
        return retList;
    }

    private ArrayList<String> getSupportedValueItemList(int mode) {
        return getSupportedValueItemList(getMenuItemId(), mode);
    }

    protected ArrayList<String> getSupportedValueItemList(String itemId, int mode) {
        String value;
        ArrayList<String> itemIdList = this.mAccessor.getSpecifiedMenuList(itemId);
        if (itemIdList == null) {
            return null;
        }
        if (Environment.DEVICE_TYPE == 4) {
            return itemIdList;
        }
        MenuAccessor.DisplayMenuItem parentItem = getDisplayItem(itemId);
        List<String> supportedList = null;
        if (parentItem != null) {
            try {
                IController controller = getExecClassInstance(parentItem.itemId);
                if (controller != null) {
                    if (mode == 1) {
                        value = parentItem.fnValue;
                    } else {
                        value = parentItem.value;
                    }
                    supportedList = controller.getSupportedValue(value);
                }
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Fail to get controller instance.");
            }
        }
        if (supportedList == null) {
            return null;
        }
        ArrayList<String> retList = new ArrayList<>();
        Iterator i$ = itemIdList.iterator();
        while (i$.hasNext()) {
            String menuItemID = i$.next();
            String menuItemValue = getMenuItemValue(menuItemID);
            if (supportedList.contains(menuItemValue)) {
                retList.add(menuItemID);
            }
        }
        return retList;
    }

    public ArrayList<String> getSupportedItemList(String itemId, int mode) {
        MenuAccessor.DisplayMenuItem parentItem = this.mAccessor.getParentItem();
        if (parentItem.configClass == null) {
            ArrayList<String> supportedList = getSupportedSettingItemList(itemId, mode);
            return supportedList;
        }
        ArrayList<String> supportedList2 = getSupportedValueItemList(itemId, mode);
        return supportedList2;
    }

    public ArrayList<String> getSupportedItemList(int mode) {
        MenuAccessor.DisplayMenuItem parentItem = this.mAccessor.getParentItem();
        if (parentItem.configClass == null) {
            ArrayList<String> supportedList = getSupportedSettingItemList(mode);
            return supportedList;
        }
        ArrayList<String> supportedList2 = getSupportedValueItemList(mode);
        return supportedList2;
    }

    public ArrayList<String> getSupportedItemList(String itemId) {
        MenuAccessor.DisplayMenuItem parentItem = getDisplayItem(itemId);
        if (parentItem == null) {
            return null;
        }
        if (parentItem.configClass == null) {
            ArrayList<String> supportedList = getSupportedSettingItemList(itemId, 0);
            return supportedList;
        }
        ArrayList<String> supportedList2 = getSupportedValueItemList(parentItem.itemId, 0);
        return supportedList2;
    }

    public ArrayList<String> getSupportedItemList() {
        return getSupportedItemList(0);
    }

    public void updateSettingItemsAvailable(ArrayList<String> items) {
        updateSettingItemsAvailable(items, 0);
    }

    public void updateSettingItemsAvailable(ArrayList<String> items, int mode) {
        String value;
        Iterator i$ = items.iterator();
        while (i$.hasNext()) {
            String itemId = i$.next();
            MenuAccessor.DisplayMenuItem setItem = getDisplayItem(itemId);
            if (setItem != null) {
                boolean isValid = true;
                String confName = setItem.configClass;
                if (confName != null) {
                    Class<?> confClass = getExecClass(confName);
                    try {
                        IController controller = getExecClassInstance(confClass);
                        if (mode == 1) {
                            value = setItem.fnValue;
                        } else {
                            value = setItem.value;
                        }
                        isValid = controller.isAvailable(value);
                    } catch (IllegalArgumentException e) {
                        Log.e(TAG, "error", e);
                    }
                }
                setItem.isValid = isValid;
            }
        }
    }

    public void updateValueItemsAvailable(ArrayList<String> items) {
        updateValueItemsAvailable(items, 0);
    }

    public void updateValueItemsAvailable(ArrayList<String> items, int mode) {
        String value;
        MenuAccessor.DisplayMenuItem child = getDisplayItem(items.get(0));
        MenuAccessor.DisplayMenuItem parentItem = getDisplayItem(child.parentItemId);
        String confName = parentItem.configClass;
        if (parentItem.configClass != null) {
            Class<?> confClass = getExecClass(confName);
            List<String> validList = null;
            try {
                IController controller = getExecClassInstance(confClass);
                if (mode == 1) {
                    value = parentItem.fnValue;
                } else {
                    value = parentItem.value;
                }
                validList = controller.getAvailableValue(value);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "error", e);
            }
            if (Environment.DEVICE_TYPE == 4 && validList == null) {
                Iterator i$ = items.iterator();
                while (i$.hasNext()) {
                    String itemId = i$.next();
                    MenuAccessor.DisplayMenuItem checkItem = getDisplayItem(itemId);
                    if (checkItem != null) {
                        checkItem.isValid = true;
                    }
                }
                return;
            }
            if (validList == null) {
                Iterator i$2 = items.iterator();
                while (i$2.hasNext()) {
                    String itemId2 = i$2.next();
                    MenuAccessor.DisplayMenuItem checkItem2 = getDisplayItem(itemId2);
                    if (checkItem2 != null) {
                        checkItem2.isValid = false;
                    }
                }
                return;
            }
            Iterator i$3 = items.iterator();
            while (i$3.hasNext()) {
                String itemId3 = i$3.next();
                MenuAccessor.DisplayMenuItem checkItem3 = getDisplayItem(itemId3);
                if (checkItem3 != null) {
                    checkItem3.isValid = validList.contains(checkItem3.value);
                }
            }
        }
    }

    public boolean execCurrentMenuItem(String itemId) {
        return execCurrentMenuItem(itemId, true);
    }

    public boolean execCurrentMenuItem(String itemId, boolean async) {
        MenuAccessor.DisplayMenuItem dmi = getDisplayItem(itemId);
        boolean ret = false;
        if (dmi != null) {
            final String value = dmi.value;
            final String tag = getMenuItemValue(dmi.parentItemId);
            String confName = dmi.configClass;
            if (confName == null) {
                return false;
            }
            Class<?> confClass = getExecClass(confName);
            try {
                final IController controller = getExecClassInstance(confClass);
                if (async) {
                    Runnable r = new Runnable() { // from class: com.sony.imaging.app.base.menu.BaseMenuService.1
                        @Override // java.lang.Runnable
                        public void run() {
                            BaseMenuService.mRunnableList.remove(this);
                            controller.setValue(tag, value);
                        }
                    };
                    mRunnableList.add(r);
                    handler.post(r);
                } else {
                    controller.setValue(tag, value);
                }
                ret = true;
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "error", e);
            }
        }
        return ret;
    }

    public static void flushAllRunnable() {
        handler.removeCallbacksAndMessages(null);
        Runnable[] rArray = (Runnable[]) mRunnableList.toArray(new Runnable[0]);
        if (mRunnableList != null) {
            for (Runnable r : rArray) {
                r.run();
            }
        }
    }

    public String getCurrentValue(String itemId) {
        MenuAccessor.DisplayMenuItem dmi = getDisplayItem(itemId);
        if (dmi == null) {
            return null;
        }
        String value = dmi.value;
        String confName = dmi.configClass;
        Class<?> confClass = getExecClass(confName);
        if (confClass == null) {
            return null;
        }
        String currentValueItemId = null;
        try {
            IController controller = getExecClassInstance(confClass);
            String currentValue = controller.getValue(value);
            ArrayList<String> subItemIdList = dmi.subItemArray;
            if (subItemIdList != null) {
                if (currentValue != null) {
                    Iterator i$ = subItemIdList.iterator();
                    while (true) {
                        if (!i$.hasNext()) {
                            break;
                        }
                        String subItemId = i$.next();
                        String subItemValue = getMenuItemValue(subItemId);
                        if (currentValue.equals(subItemValue)) {
                            currentValueItemId = subItemId;
                            break;
                        }
                    }
                } else {
                    currentValueItemId = this.mContext.getResources().getString(17041730);
                }
            } else if (currentValue == null && (controller instanceof CompensationControllable)) {
                currentValueItemId = this.mContext.getResources().getString(17041730);
            } else {
                currentValueItemId = currentValue;
            }
            boolean isCompensationValue = false;
            if (currentValueItemId != null) {
                Matcher m = COMPENSATION_PATTERN.matcher(currentValueItemId);
                isCompensationValue = m.find();
            }
            if (isCompensationValue && (controller instanceof CompensationControllable)) {
                CompensationControllable cc = (CompensationControllable) controller;
                float f = cc.getExternalCompensation();
                Resources r = this.mContext.getResources();
                if (f == 0.0f) {
                    String currentValueItemId2 = r.getString(R.string.restr_pin_confirm_pin);
                    return currentValueItemId2;
                }
                if (f > 0.0f) {
                    String currentValueItemId3 = String.format(r.getString(17041743), Integer.valueOf(((int) f) / 1), Integer.valueOf(((int) (10.0f * f)) % 10));
                    return currentValueItemId3;
                }
                float f2 = Math.abs(f);
                String currentValueItemId4 = String.format(r.getString(17041717), Integer.valueOf(((int) f2) / 1), Integer.valueOf(((int) (10.0f * f2)) % 10));
                return currentValueItemId4;
            }
            return currentValueItemId;
        } catch (IController.NotSupportedException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalArgumentException e2) {
            Log.e(TAG, "error", e2);
            return null;
        }
    }

    public String getMenuItemId() {
        if (this.mAccessor == null) {
            return null;
        }
        return this.mAccessor.getParentItem().itemId;
    }

    public IController getExecClassInstance(Class<?> confClass) {
        IController instance = EXEC_CLASS_INSTANCE_POOL.get(confClass);
        if (instance == null) {
            try {
                Method getInstance = confClass.getMethod(GETINSTANCE, (Class[]) null);
                instance = (IController) getInstance.invoke(null, new Object[0]);
                EXEC_CLASS_INSTANCE_POOL.put(confClass, instance);
                return instance;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return instance;
            } catch (IllegalArgumentException e2) {
                e2.printStackTrace();
                return instance;
            } catch (NoSuchMethodException e1) {
                e1.printStackTrace();
                return instance;
            } catch (SecurityException e12) {
                e12.printStackTrace();
                return instance;
            } catch (InvocationTargetException e3) {
                e3.printStackTrace();
                return instance;
            }
        }
        return instance;
    }

    public Class<?> getExecClass(String className) {
        if (className == null || className.length() < 1) {
            return null;
        }
        Class<?> c = EXEC_CLASS_POOL.get(className);
        if (c == null) {
            try {
                c = Class.forName(className);
                EXEC_CLASS_POOL.put(className, c);
                return c;
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "error", e);
                return c;
            }
        }
        return c;
    }

    private void logcat(String str) {
    }

    public CharSequence getMenuItemText(String itemid) {
        MenuAccessor.DisplayMenuItem item;
        int id;
        CharSequence s = mMenuItemTextPool.get(itemid);
        if (s == null && (item = getDisplayItem(itemid)) != null && item.textRes != null && item.textRes.length() > 0 && (id = this.mRes.getIdentifier(item.textRes, null, this.mContext.getPackageName())) != 0) {
            CharSequence s2 = this.mRes.getText(id);
            mMenuItemTextPool.put(itemid, s2);
            return s2;
        }
        return s;
    }

    public CharSequence getMenuItemGuideText(String itemid) {
        int id;
        if (this.mAccessor == null) {
            return null;
        }
        MenuAccessor.DisplayMenuItem item = getDisplayItem(itemid);
        if (item.guideRes == null || item.guideRes.length() <= 0 || (id = this.mRes.getIdentifier(item.guideRes, null, this.mContext.getPackageName())) == 0) {
            return null;
        }
        return this.mRes.getText(id);
    }

    public Drawable getMenuItemDrawable(String itemid) {
        MenuAccessor.DisplayMenuItem item;
        Integer drawableId = mMenuItemDrawablePool.get(itemid);
        if (drawableId == null && this.mAccessor != null && (item = getDisplayItem(itemid)) != null && item.iconRes != null && item.iconRes.length() > 0) {
            drawableId = Integer.valueOf(this.mRes.getIdentifier(item.iconRes, null, this.mContext.getPackageName()));
            mMenuItemDrawablePool.put(itemid, drawableId);
        }
        if (drawableId == null || drawableId.intValue() == 0) {
            return null;
        }
        Drawable drawable = this.mRes.getDrawable(drawableId.intValue());
        return drawable;
    }

    public Drawable getMenuItemSelectedDrawable(String itemid) {
        int id;
        if (this.mAccessor == null) {
            return null;
        }
        MenuAccessor.DisplayMenuItem item = getDisplayItem(itemid);
        if (item.selectedIconRes == null || item.selectedIconRes.length() <= 0 || (id = this.mRes.getIdentifier(item.selectedIconRes, null, this.mContext.getPackageName())) == 0) {
            return null;
        }
        return this.mRes.getDrawable(id);
    }

    public int[] getMenuItemCautionId(String itemid) {
        int[] cautionid = mCautionIDPool.get(itemid);
        if (cautionid != null) {
            return cautionid;
        }
        if (this.mAccessor != null) {
            MenuAccessor.DisplayMenuItem item = getDisplayItem(itemid);
            if (item == null || item.cautionID == null) {
                return null;
            }
            Class cls = null;
            Field field = null;
            cautionid = new int[item.cautionID.size()];
            int i = 0;
            Iterator<String> it = item.cautionID.iterator();
            while (it.hasNext()) {
                String cautionName = it.next();
                if (cautionName.contains(".")) {
                    int splitIndex = cautionName.indexOf(".CAUTION");
                    String className = cautionName.substring(0, splitIndex);
                    try {
                        cls = Class.forName(className);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    cautionName = cautionName.substring(splitIndex + 1);
                } else {
                    cls = CautionID.class;
                }
                if (cls != null) {
                    try {
                        Field[] fields = mCautionFieldPool.get();
                        if (fields == null) {
                            fields = cls.getFields();
                            mCautionFieldPool = new SoftReference<>(fields);
                        }
                        Field[] arr$ = fields;
                        int len$ = arr$.length;
                        int i$ = 0;
                        while (true) {
                            if (i$ >= len$) {
                                break;
                            }
                            Field f = arr$[i$];
                            if (!f.getName().equals(cautionName)) {
                                i$++;
                            } else {
                                field = f;
                                break;
                            }
                        }
                    } catch (SecurityException e2) {
                        e2.printStackTrace();
                    }
                }
                if (field != null) {
                    try {
                        cautionid[i] = field.getInt(cls);
                    } catch (IllegalAccessException e3) {
                        e3.printStackTrace();
                    } catch (IllegalArgumentException e4) {
                        e4.printStackTrace();
                    }
                }
                i++;
            }
            mCautionIDPool.put(itemid, cautionid);
        }
        return cautionid;
    }

    public String getMenuItemOptionStr(String itemid) {
        if (this.mAccessor != null) {
            MenuAccessor.DisplayMenuItem item = getDisplayItem(itemid);
            if (item.optionStr != null && item.optionStr.length() > 0) {
                return item.optionStr;
            }
        }
        return null;
    }

    public String getMenuItemExecType(String itemid) {
        MenuAccessor.DisplayMenuItem item = getDisplayItem(itemid);
        if (item == null) {
            return null;
        }
        String ret = item.execType;
        return ret;
    }

    public String getMenuItemNextMenuID(String itemid) {
        MenuAccessor.DisplayMenuItem item = getDisplayItem(itemid);
        if (item == null) {
            return null;
        }
        String ret = item.nextMenuID;
        return ret;
    }

    public boolean isMenuItemValid(String itemid) {
        MenuAccessor.DisplayMenuItem item = getDisplayItem(itemid);
        if (item == null) {
            return false;
        }
        return item.isValid;
    }

    public String getMenuItemNotifyTag(String itemId) {
        MenuAccessor.DisplayMenuItem item = getDisplayItem(itemId);
        if (item == null) {
            return null;
        }
        String ret = item.updateTag;
        return ret;
    }

    public boolean hasSubArray(String itemId) {
        MenuAccessor.DisplayMenuItem item = getDisplayItem(itemId);
        if (item == null) {
            return false;
        }
        boolean ret = item.hasSubArray;
        return ret;
    }

    public boolean hasSupportedSubArray(String itemId) {
        boolean ret = hasSubArray(itemId);
        if (ret) {
            ArrayList<String> list = getSupportedItemList(itemId);
            return list != null && list.size() > 1;
        }
        return ret;
    }

    MenuAccessor.DisplayMenuItem getDisplayItem(String itemId) {
        if (this.mAccessor != null) {
            MenuAccessor.DisplayMenuItem ret = this.mAccessor.getDisplayItem(itemId);
            return ret;
        }
        Log.e(TAG, "Accessor is not set.");
        return null;
    }

    public String getMenuItemFnNextMenuID(String itemid) {
        MenuAccessor.DisplayMenuItem item = getDisplayItem(itemid);
        if (item == null) {
            return null;
        }
        String ret = item.fnNextMenuID;
        return ret;
    }

    public Drawable getMenuItemFnOptionDrawable(String itemid) {
        int id;
        MenuAccessor.DisplayMenuItem item = getDisplayItem(itemid);
        if (item == null || item.fnOptionIconRes == null || item.fnOptionIconRes.length() <= 0 || (id = this.mRes.getIdentifier(item.fnOptionIconRes, null, this.mContext.getPackageName())) == 0) {
            return null;
        }
        Drawable ret = this.mRes.getDrawable(id);
        return ret;
    }

    public String getMenuItemCustomStartLayoutID(String itemid) {
        MenuAccessor.DisplayMenuItem item = getDisplayItem(itemid);
        if (item == null) {
            return null;
        }
        String ret = item.customStartLaoutID;
        return ret;
    }

    public String getMenuItemValue(String itemid) {
        MenuAccessor.DisplayMenuItem item = getDisplayItem(itemid);
        if (item != null) {
            return item.value;
        }
        return null;
    }

    public int getMenuItemFnActiveIconId(String itemId) {
        Integer ret = mMenuItemFnActiveIconId.get(itemId);
        if (ret == null) {
            MenuAccessor.DisplayMenuItem item = getDisplayItem(itemId);
            int id = 0;
            if (item != null && item.fnActiveIcon != null && item.fnActiveIcon.length() > 0 && (id = this.mRes.getIdentifier(item.fnActiveIcon, null, this.mContext.getPackageName())) != 0) {
                mMenuItemFnActiveIconId.put(itemId, Integer.valueOf(id));
            }
            ret = Integer.valueOf(id);
        }
        return ret.intValue();
    }

    public Drawable getDrawable(String itemId, String element) {
        int id = getResourceId(itemId, element);
        if (id != -1) {
            return this.mRes.getDrawable(id);
        }
        return null;
    }

    public CharSequence getText(String itemId, String element) {
        int id;
        String hashKey = itemId + element;
        CharSequence s = mMenuItemTextPool.get(hashKey);
        if (s == null && (id = getResourceId(itemId, element)) != -1) {
            CharSequence s2 = this.mRes.getText(id);
            mMenuItemTextPool.put(hashKey, s2);
            return s2;
        }
        return s;
    }

    public int getResourceId(String itemId, String element) {
        MenuAccessor.DisplayMenuItem item;
        String strid;
        String key = itemId + element;
        Integer intId = mMenuItemResourceIdPool.get(key);
        if (intId == null && (item = getDisplayItem(itemId)) != null && item.elements != null && (strid = item.elements.get(element)) != null && strid.length() > 0) {
            intId = Integer.valueOf(this.mRes.getIdentifier(strid, null, this.mContext.getPackageName()));
            mMenuItemResourceIdPool.put(key, intId);
        }
        if (intId != null) {
            return intId.intValue();
        }
        return -1;
    }

    public String getString(String itemId, String element) {
        MenuAccessor.DisplayMenuItem item = getDisplayItem(itemId);
        if (item == null || item.elements == null) {
            return null;
        }
        return item.elements.get(element);
    }

    public boolean getBoolean(String itemId, String element) {
        return getBoolean(itemId, element, true);
    }

    public boolean getBoolean(String itemId, String element, boolean defaultBoolean) {
        String value;
        MenuAccessor.DisplayMenuItem item = getDisplayItem(itemId);
        if (item != null && item.elements != null && (value = item.elements.get(element)) != null) {
            return TRUE.equals(value);
        }
        return defaultBoolean;
    }

    public IController getExecClassInstance(String aItemID) {
        String itemId = aItemID;
        if (itemId == null) {
            itemId = getMenuItemId();
        }
        MenuAccessor.DisplayMenuItem dispItem = getDisplayItem(itemId);
        if (dispItem == null) {
            return null;
        }
        Class<?> clazz = getExecClass(dispItem.configClass);
        IController iController = getExecClassInstance(clazz);
        return iController;
    }

    public static final void clearTextCache() {
        mMenuItemTextPool.clear();
    }

    @Deprecated
    public CharSequence getParentMenuItemText() {
        Log.w(TAG, "WARNING: Deprecated API getParentMenuItemText is called.");
        return "getParentMenuItemText is Deprecated";
    }

    @Deprecated
    public void accessorInit(String fileName, MenuAccessor accessor) {
        this.mAccessor = new MenuAccessor();
        this.mAccessor.initialize(this.mContext, null);
    }

    public ArrayList<String> createMenuItemList(String topMenu) {
        String itemId = topMenu;
        if (itemId == "back") {
            HistoryItem item = this.mAccessor.popMenuHistory();
            if (item != null) {
                itemId = item.itemId;
            }
        } else {
            MenuAccessor.DisplayMenuItem displayMenuItem = this.mAccessor.getParentItem();
            this.mAccessor.pushMenuHistory(new HistoryItem(displayMenuItem.itemId, displayMenuItem.nextMenuID));
        }
        ArrayList<String> ret = this.mAccessor.getMenuList(topMenu);
        this.mAccessor.updateMainMenuList(itemId);
        return ret;
    }

    @Deprecated
    public ArrayList<String> setSupportedSettingsForFirstLayer(ArrayList<String> items) {
        logcat("setSupportedSettingsForFirstLayer start");
        ArrayList<String> newList = new ArrayList<>();
        Iterator i$ = items.iterator();
        while (i$.hasNext()) {
            String itemId = i$.next();
            MenuAccessor.DisplayMenuItem setItem = getDisplayItem(itemId);
            if (setItem == null) {
                return null;
            }
            String confName = setItem.configClass;
            if (setItem.configClass == null) {
                newList.add(itemId);
            } else {
                Class<?> confClass = getExecClass(confName);
                List<String> supportedList = null;
                try {
                    IController controller = getExecClassInstance(confClass);
                    supportedList = controller.getSupportedValue(setItem.value);
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "error", e);
                }
                if (supportedList != null && supportedList.size() != 0) {
                    newList.add(itemId);
                }
            }
        }
        logcat("setSupportedSettingsForFirstLayer end");
        return newList;
    }

    @Deprecated
    public ArrayList<String> setSupportedSettings(ArrayList<String> items) {
        logcat("setSupportedSettings start");
        MenuAccessor.DisplayMenuItem parentItem = this.mAccessor.getParentItem();
        String confName = parentItem.configClass;
        if (parentItem.configClass == null) {
            return null;
        }
        Class<?> confClass = getExecClass(confName);
        List<String> supportedList = null;
        try {
            IController controller = getExecClassInstance(confClass);
            supportedList = controller.getSupportedValue(parentItem.value);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "error", e);
        }
        if (Environment.DEVICE_TYPE != 4 || supportedList != null) {
            ArrayList<String> retList = new ArrayList<>();
            ArrayList<MenuAccessor.DisplayMenuItem> displayMenuList = new ArrayList<>();
            Iterator i$ = items.iterator();
            while (i$.hasNext()) {
                String itemId = i$.next();
                displayMenuList.add(getDisplayItem(itemId));
            }
            Iterator i$2 = displayMenuList.iterator();
            while (i$2.hasNext()) {
                MenuAccessor.DisplayMenuItem dmi = i$2.next();
                if (supportedList.contains(dmi.value)) {
                    retList.add(dmi.itemId);
                }
            }
            logcat("setSupportedSettings end");
            return retList;
        }
        return items;
    }

    @Deprecated
    public void setAvailableSettingsForFirstLayer(ArrayList<String> items) {
        updateSettingItemsAvailable(items, 0);
    }

    @Deprecated
    public void setAvailableSettings(ArrayList<String> items) {
        updateValueItemsAvailable(items);
    }

    @Deprecated
    public void setCurrentConfigValue(ArrayList<String> items) {
    }

    @Deprecated
    public String getParentItem() {
        return getMenuItemId();
    }

    public String getParentItemId(String itemId) {
        MenuAccessor.DisplayMenuItem dmi = getDisplayItem(itemId);
        if (dmi == null) {
            return null;
        }
        return dmi.parentItemId;
    }

    @Deprecated
    public Drawable getMenuItemLayer1Drawable(String itemid) {
        return this.mRes.getDrawable(17305583);
    }

    @Deprecated
    public Object getDetailValue() {
        String confName = this.mAccessor.getParentItem().configClass;
        Class<?> confClass = getExecClass(confName);
        try {
            IController controller = getExecClassInstance(confClass);
            Object ret = controller.getDetailValue();
            return ret;
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "error", e);
            return null;
        }
    }

    public void setDetailValue(Object obj) {
        String confName = this.mAccessor.getParentItem().configClass;
        Class<?> confClass = getExecClass(confName);
        try {
            IController controller = getExecClassInstance(confClass);
            controller.setDetailValue(obj);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "error", e);
        }
        logcat("setDetailValue end");
    }

    protected Method getMethod(Class<?> confClass, String MethodType) {
        Log.e(TAG, "BaseMenuService#getMethod is deprecated. Please refer to Javadoc.");
        return null;
    }
}
