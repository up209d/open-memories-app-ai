package com.sony.imaging.app.fw;

import android.util.Log;
import android.util.SparseArray;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;
import com.sony.scalar.sysutil.ScalarProperties;
import com.sony.scalar.sysutil.didep.Settings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

/* loaded from: classes.dex */
public class CustomKeyMgr implements ICustomKeyMgr {
    private static final int DEFAULT_ARRAY_SIZE = 16;
    private static final String MSG_PAUSE = "pause";
    public static final int STATUS_NOT_EXIST = -4096;
    private static final String TAG = "CustomKeyMgr";
    private SparseArray<ICustomKey> keyPool = new SparseArray<>();
    private static CustomKeyMgr mInstance = new CustomKeyMgr();
    protected static SparseArray<IKeyFunction> sCustomKeyTable = new SparseArray<>();
    protected static HashMap<String, HashMap<IKeyFunction, ArrayList<Integer>>> funcReverseResolutionTable = new HashMap<>();

    public static CustomKeyMgr getInstance() {
        return mInstance;
    }

    private CustomKeyMgr() {
        this.keyPool.clear();
    }

    @Override // com.sony.imaging.app.fw.ICustomKeyMgr
    public void pause() {
        Log.i(TAG, MSG_PAUSE);
        this.keyPool.clear();
        int version = Environment.getVersionOfHW();
        if (2 <= version) {
            P1Key.pause();
        }
        sCustomKeyTable.clear();
    }

    @Override // com.sony.imaging.app.fw.ICustomKeyMgr
    public ICustomKey get(int keycode) {
        ICustomKey key = this.keyPool.get(keycode);
        if (key == null && (key = create(keycode)) != null) {
            this.keyPool.append(keycode, key);
        }
        return key;
    }

    protected ICustomKey create(int keycode) {
        int version = Environment.getVersionOfHW();
        if (1 == version) {
            ICustomKey key = new EKey(keycode);
            return key;
        }
        if (2 > version) {
            return null;
        }
        ICustomKey key2 = new P1Key(keycode);
        return key2;
    }

    public IKeyFunction getCustomKeyFunction(int keyCode) {
        int[] list;
        IKeyFunction func = sCustomKeyTable.get(keyCode);
        if (func == null) {
            if (232 == keyCode && 1 == Environment.getVersionOfHW() && (list = ScalarProperties.getIntArray("input.mode.sw.mode.list")) != null && list.length > 0) {
                func = CustomizableFunction.NoAssign;
            }
            if (func == null) {
                int[] keys = {keyCode};
                int[] functions = Settings.getKeyFunction(keys);
                if (functions != null) {
                    func = CustomizableFunction.keyFunction2CustomizableFunction(functions[0]);
                } else {
                    func = CustomizableFunction.Invalid;
                }
            }
            sCustomKeyTable.put(keyCode, func);
        }
        return func;
    }

    public int getKeyStatusByFuncCode(String mode, IKeyFunction func) {
        if (IKeyFunction.TYPE_NONE.equals(func.getType())) {
            return Integer.MAX_VALUE;
        }
        int result = STATUS_NOT_EXIST;
        HashMap<IKeyFunction, ArrayList<Integer>> modeTable = funcReverseResolutionTable.get(mode);
        if (modeTable == null) {
            modeTable = new HashMap<>();
            funcReverseResolutionTable.put(mode, modeTable);
        }
        if (modeTable.containsKey(func)) {
            Iterator<Integer> it = modeTable.get(func).iterator();
            while (it.hasNext()) {
                int code = it.next().intValue();
                KeyStatus keyStatus = AppRoot.USER_KEYCODE.getKeyStatus(code);
                if (532 == code || 533 == code) {
                    KeyStatus slideStatus = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED);
                    if (1 == slideStatus.valid) {
                        if (slideStatus.status == code) {
                            keyStatus = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.AF_MF_AEL);
                        } else {
                            continue;
                        }
                    }
                }
                if (1 != keyStatus.valid) {
                    continue;
                } else {
                    if (keyStatus.status == 1) {
                        return keyStatus.status;
                    }
                    if (keyStatus.status == 0) {
                        if (result == Integer.MAX_VALUE || result == -4096) {
                            result = 0;
                        }
                    } else if (result == -4096) {
                        result = keyStatus.status;
                    }
                }
            }
            return result;
        }
        Iterator<Integer> it2 = AppRoot.USER_KEYCODE.getIterator();
        ArrayList<Integer> list = new ArrayList<>();
        while (it2.hasNext()) {
            int code2 = it2.next().intValue();
            ICustomKey key = get(code2);
            if (func.equals(key.getFunctionCode(mode))) {
                KeyStatus keyStatus2 = AppRoot.USER_KEYCODE.getKeyStatus(code2);
                if (532 == code2 || 533 == code2) {
                    KeyStatus slideStatus2 = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED);
                    if (1 == slideStatus2.valid) {
                        if (slideStatus2.status == code2) {
                            keyStatus2 = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.AF_MF_AEL);
                        } else {
                            list.add(Integer.valueOf(code2));
                        }
                    }
                }
                if (1 == keyStatus2.valid) {
                    if (keyStatus2.status == 1) {
                        result = keyStatus2.status;
                    } else if (keyStatus2.status == 0) {
                        if (result == Integer.MAX_VALUE || result == -4096) {
                            result = 0;
                        }
                    } else if (result == -4096) {
                        result = keyStatus2.status;
                    }
                    list.add(Integer.valueOf(code2));
                }
            }
        }
        modeTable.put(func, list);
        return result;
    }

    public int getKeyStatusByCustomFuncCode(String mode, IKeyFunction func) {
        int[] array;
        int[] functions;
        if (IKeyFunction.TYPE_NONE.equals(func.getType())) {
            return Integer.MAX_VALUE;
        }
        int result = getKeyStatusByFuncCode(mode, func);
        if (1 != result) {
            HashMap<IKeyFunction, ArrayList<Integer>> modeTable = funcReverseResolutionTable.get(mode);
            if (modeTable == null) {
                modeTable = new HashMap<>();
                funcReverseResolutionTable.put(mode, modeTable);
            }
            ArrayList<Integer> list = modeTable.get(CustomizableFunction.Custom);
            if (list == null) {
                list = new ArrayList<>();
                int[] array2 = new int[16];
                int c = 0;
                Iterator<Integer> it = AppRoot.USER_KEYCODE.getIterator();
                while (it.hasNext()) {
                    int code = it.next().intValue();
                    ICustomKey key = get(code);
                    if (CustomizableFunction.Custom.equals(key.getFunctionCode(mode)) && 1 == AppRoot.USER_KEYCODE.getKeyStatus(code).valid) {
                        list.add(Integer.valueOf(code));
                        if (sCustomKeyTable.indexOfKey(code) < 0) {
                            int c2 = c + 1;
                            array2[c] = code;
                            if (c2 == array2.length) {
                                Arrays.copyOf(array2, c2 * 2);
                                c = c2;
                            } else {
                                c = c2;
                            }
                        }
                    }
                }
                modeTable.put(CustomizableFunction.Custom, list);
                if (c > 0 && (functions = Settings.getKeyFunction((array = Arrays.copyOf(array2, c)))) != null) {
                    for (int i = 0; i < c; i++) {
                        sCustomKeyTable.put(array[i], CustomizableFunction.keyFunction2CustomizableFunction(functions[i]));
                    }
                }
            }
            Iterator<Integer> it2 = list.iterator();
            while (it2.hasNext()) {
                int code2 = it2.next().intValue();
                if (func.equals(getCustomKeyFunction(code2))) {
                    KeyStatus keyStatus = AppRoot.USER_KEYCODE.getKeyStatus(code2);
                    if (532 == code2 || 533 == code2) {
                        KeyStatus slideStatus = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED);
                        if (1 == slideStatus.valid) {
                            if (slideStatus.status == code2) {
                                keyStatus = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.AF_MF_AEL);
                            } else {
                                continue;
                            }
                        }
                    }
                    if (1 != keyStatus.valid) {
                        continue;
                    } else {
                        if (keyStatus.status == 1) {
                            return keyStatus.status;
                        }
                        if (keyStatus.status == 0) {
                            if (result == Integer.MAX_VALUE || result == -4096) {
                                result = 0;
                            }
                        } else if (result == -4096) {
                            result = keyStatus.status;
                        }
                    }
                }
            }
            return result;
        }
        return result;
    }

    public boolean isFuncKeyExist(String mode, IKeyFunction func) {
        return -4096 != getKeyStatusByFuncCode(mode, func);
    }

    public boolean isCustomFuncKeyExist(String mode, IKeyFunction func) {
        return -4096 != getKeyStatusByCustomFuncCode(mode, func);
    }
}
