package com.sony.imaging.app.fw;

import android.util.SparseArray;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;
import java.util.HashMap;

/* loaded from: classes.dex */
public class EKey implements ICustomKey {
    protected static SparseArray<IKeyFunction> cmn_table;
    protected static HashMap<String, SparseArray<IKeyFunction>> table;
    protected IKeyFunction assignedFunc;
    protected String mCategory;
    private boolean mCategoryDependent = true;
    protected final int mKeycode;

    public EKey(int keycode) {
        this.mKeycode = keycode;
        initAssigned();
    }

    static {
        initTable();
    }

    protected static void initTable() {
        if (cmn_table == null) {
            cmn_table = new SparseArray<>();
        }
        if (table == null) {
            table = new HashMap<>();
            SparseArray<IKeyFunction> p_keytable = new SparseArray<>();
            SparseArray<IKeyFunction> a_keytable = new SparseArray<>();
            SparseArray<IKeyFunction> s_keytable = new SparseArray<>();
            SparseArray<IKeyFunction> m_keytable = new SparseArray<>();
            SparseArray<IKeyFunction> other_keytable = new SparseArray<>();
            SparseArray<IKeyFunction> single_keytable = new SparseArray<>();
            SparseArray<IKeyFunction> index_keytable = new SparseArray<>();
            SparseArray<IKeyFunction> menu_keytable = new SparseArray<>();
            SparseArray<IKeyFunction> focussetting_keytable = new SparseArray<>();
            p_keytable.append(AppRoot.USER_KEYCODE.DELETE, CustomizableFunction.Guide);
            p_keytable.append(AppRoot.USER_KEYCODE.SK2, CustomizableFunction.Guide);
            p_keytable.append(AppRoot.USER_KEYCODE.CENTER, CustomizableFunction.Custom);
            p_keytable.append(AppRoot.USER_KEYCODE.RIGHT, CustomizableFunction.Custom);
            p_keytable.append(AppRoot.USER_KEYCODE.LEFT, CustomizableFunction.Custom);
            p_keytable.append(103, CustomizableFunction.Custom);
            p_keytable.append(AppRoot.USER_KEYCODE.DOWN, CustomizableFunction.Custom);
            p_keytable.append(AppRoot.USER_KEYCODE.AEL, CustomizableFunction.Custom);
            a_keytable.append(AppRoot.USER_KEYCODE.DELETE, CustomizableFunction.Guide);
            a_keytable.append(AppRoot.USER_KEYCODE.SK2, CustomizableFunction.Guide);
            a_keytable.append(AppRoot.USER_KEYCODE.CENTER, CustomizableFunction.Custom);
            a_keytable.append(AppRoot.USER_KEYCODE.RIGHT, CustomizableFunction.Custom);
            a_keytable.append(AppRoot.USER_KEYCODE.LEFT, CustomizableFunction.Custom);
            a_keytable.append(103, CustomizableFunction.Custom);
            a_keytable.append(AppRoot.USER_KEYCODE.DOWN, CustomizableFunction.Custom);
            a_keytable.append(AppRoot.USER_KEYCODE.AEL, CustomizableFunction.Custom);
            s_keytable.append(AppRoot.USER_KEYCODE.DELETE, CustomizableFunction.Guide);
            s_keytable.append(AppRoot.USER_KEYCODE.SK2, CustomizableFunction.Guide);
            s_keytable.append(AppRoot.USER_KEYCODE.CENTER, CustomizableFunction.Custom);
            s_keytable.append(AppRoot.USER_KEYCODE.RIGHT, CustomizableFunction.Custom);
            s_keytable.append(AppRoot.USER_KEYCODE.LEFT, CustomizableFunction.Custom);
            s_keytable.append(103, CustomizableFunction.Custom);
            s_keytable.append(AppRoot.USER_KEYCODE.DOWN, CustomizableFunction.Custom);
            s_keytable.append(AppRoot.USER_KEYCODE.AEL, CustomizableFunction.Custom);
            m_keytable.append(AppRoot.USER_KEYCODE.DELETE, CustomizableFunction.Guide);
            m_keytable.append(AppRoot.USER_KEYCODE.SK2, CustomizableFunction.Guide);
            m_keytable.append(AppRoot.USER_KEYCODE.CENTER, CustomizableFunction.Custom);
            m_keytable.append(AppRoot.USER_KEYCODE.RIGHT, CustomizableFunction.Custom);
            m_keytable.append(AppRoot.USER_KEYCODE.LEFT, CustomizableFunction.Custom);
            m_keytable.append(103, CustomizableFunction.Custom);
            m_keytable.append(AppRoot.USER_KEYCODE.DOWN, CustomizableFunction.Custom);
            m_keytable.append(AppRoot.USER_KEYCODE.AEL, CustomizableFunction.Custom);
            other_keytable.append(AppRoot.USER_KEYCODE.DELETE, CustomizableFunction.Guide);
            other_keytable.append(AppRoot.USER_KEYCODE.SK2, CustomizableFunction.Guide);
            other_keytable.append(AppRoot.USER_KEYCODE.CENTER, CustomizableFunction.Custom);
            other_keytable.append(AppRoot.USER_KEYCODE.RIGHT, CustomizableFunction.Custom);
            other_keytable.append(AppRoot.USER_KEYCODE.LEFT, CustomizableFunction.Custom);
            other_keytable.append(103, CustomizableFunction.Custom);
            other_keytable.append(AppRoot.USER_KEYCODE.DOWN, CustomizableFunction.Custom);
            other_keytable.append(AppRoot.USER_KEYCODE.AEL, CustomizableFunction.Custom);
            menu_keytable.append(AppRoot.USER_KEYCODE.DELETE, CustomizableFunction.Guide);
            menu_keytable.append(AppRoot.USER_KEYCODE.SK2, CustomizableFunction.Guide);
            menu_keytable.append(AppRoot.USER_KEYCODE.DIAL1_LEFT, CustomizableFunction.SubPrev);
            menu_keytable.append(AppRoot.USER_KEYCODE.DIAL1_RIGHT, CustomizableFunction.SubNext);
            menu_keytable.append(AppRoot.USER_KEYCODE.SHUTTLE_LEFT, CustomizableFunction.MainPrev);
            menu_keytable.append(AppRoot.USER_KEYCODE.SHUTTLE_RIGHT, CustomizableFunction.MainNext);
            menu_keytable.append(AppRoot.USER_KEYCODE.DIAL2_LEFT, CustomizableFunction.SubPrev);
            menu_keytable.append(AppRoot.USER_KEYCODE.DIAL2_RIGHT, CustomizableFunction.SubNext);
            menu_keytable.append(AppRoot.USER_KEYCODE.AEL, CustomizableFunction.Custom);
            focussetting_keytable.append(AppRoot.USER_KEYCODE.DELETE, CustomizableFunction.Reset);
            focussetting_keytable.append(AppRoot.USER_KEYCODE.SK2, CustomizableFunction.Reset);
            focussetting_keytable.append(AppRoot.USER_KEYCODE.DIAL1_LEFT, CustomizableFunction.SubPrev);
            focussetting_keytable.append(AppRoot.USER_KEYCODE.DIAL1_RIGHT, CustomizableFunction.SubNext);
            focussetting_keytable.append(AppRoot.USER_KEYCODE.SHUTTLE_LEFT, CustomizableFunction.MainPrev);
            focussetting_keytable.append(AppRoot.USER_KEYCODE.SHUTTLE_RIGHT, CustomizableFunction.MainNext);
            focussetting_keytable.append(AppRoot.USER_KEYCODE.DIAL2_LEFT, CustomizableFunction.SubPrev);
            focussetting_keytable.append(AppRoot.USER_KEYCODE.DIAL2_RIGHT, CustomizableFunction.SubNext);
            focussetting_keytable.append(AppRoot.USER_KEYCODE.AEL, CustomizableFunction.Custom);
            single_keytable.append(AppRoot.USER_KEYCODE.SK2, CustomizableFunction.Delete);
            single_keytable.append(AppRoot.USER_KEYCODE.DELETE, CustomizableFunction.Delete);
            single_keytable.append(AppRoot.USER_KEYCODE.DIAL1_LEFT, CustomizableFunction.SubPrev);
            single_keytable.append(AppRoot.USER_KEYCODE.DIAL1_RIGHT, CustomizableFunction.SubNext);
            single_keytable.append(AppRoot.USER_KEYCODE.SHUTTLE_LEFT, CustomizableFunction.MainPrev);
            single_keytable.append(AppRoot.USER_KEYCODE.SHUTTLE_RIGHT, CustomizableFunction.MainNext);
            single_keytable.append(AppRoot.USER_KEYCODE.DIAL2_LEFT, CustomizableFunction.SubPrev);
            single_keytable.append(AppRoot.USER_KEYCODE.DIAL2_RIGHT, CustomizableFunction.SubNext);
            index_keytable.append(AppRoot.USER_KEYCODE.SK2, CustomizableFunction.Delete);
            index_keytable.append(AppRoot.USER_KEYCODE.DELETE, CustomizableFunction.Delete);
            index_keytable.append(AppRoot.USER_KEYCODE.DIAL1_LEFT, CustomizableFunction.SubPrev);
            index_keytable.append(AppRoot.USER_KEYCODE.DIAL1_RIGHT, CustomizableFunction.SubNext);
            index_keytable.append(AppRoot.USER_KEYCODE.SHUTTLE_LEFT, CustomizableFunction.MainPrev);
            index_keytable.append(AppRoot.USER_KEYCODE.SHUTTLE_RIGHT, CustomizableFunction.MainNext);
            index_keytable.append(AppRoot.USER_KEYCODE.DIAL2_LEFT, CustomizableFunction.SubPrev);
            index_keytable.append(AppRoot.USER_KEYCODE.DIAL2_RIGHT, CustomizableFunction.SubNext);
            DialType type = getDialType();
            if (type == DialType.THREE_DIAL) {
                m_keytable.append(AppRoot.USER_KEYCODE.DIAL1_LEFT, CustomizableFunction.ShutterSpeedDecrement);
                m_keytable.append(AppRoot.USER_KEYCODE.DIAL1_RIGHT, CustomizableFunction.ShutterSpeedIncrement);
                m_keytable.append(AppRoot.USER_KEYCODE.DIAL2_LEFT, CustomizableFunction.ApertureDecrement);
                m_keytable.append(AppRoot.USER_KEYCODE.DIAL2_RIGHT, CustomizableFunction.ApertureIncrement);
                m_keytable.append(AppRoot.USER_KEYCODE.SHUTTLE_LEFT, CustomizableFunction.IsoSensitivityDecrement);
                m_keytable.append(AppRoot.USER_KEYCODE.SHUTTLE_RIGHT, CustomizableFunction.IsoSensitivityIncrement);
                p_keytable.append(AppRoot.USER_KEYCODE.DIAL1_LEFT, CustomizableFunction.ProgramShiftDecrement);
                p_keytable.append(AppRoot.USER_KEYCODE.DIAL1_RIGHT, CustomizableFunction.ProgramShiftIncrement);
                p_keytable.append(AppRoot.USER_KEYCODE.DIAL2_LEFT, CustomizableFunction.ExposureCompensationDecrement);
                p_keytable.append(AppRoot.USER_KEYCODE.DIAL2_RIGHT, CustomizableFunction.ExposureCompensationIncrement);
                p_keytable.append(AppRoot.USER_KEYCODE.SHUTTLE_LEFT, CustomizableFunction.IsoSensitivityDecrement);
                p_keytable.append(AppRoot.USER_KEYCODE.SHUTTLE_RIGHT, CustomizableFunction.IsoSensitivityIncrement);
                a_keytable.append(AppRoot.USER_KEYCODE.DIAL1_LEFT, CustomizableFunction.ApertureDecrement);
                a_keytable.append(AppRoot.USER_KEYCODE.DIAL1_RIGHT, CustomizableFunction.ApertureIncrement);
                a_keytable.append(AppRoot.USER_KEYCODE.DIAL2_LEFT, CustomizableFunction.ExposureCompensationDecrement);
                a_keytable.append(AppRoot.USER_KEYCODE.DIAL2_RIGHT, CustomizableFunction.ExposureCompensationIncrement);
                a_keytable.append(AppRoot.USER_KEYCODE.SHUTTLE_LEFT, CustomizableFunction.IsoSensitivityDecrement);
                a_keytable.append(AppRoot.USER_KEYCODE.SHUTTLE_RIGHT, CustomizableFunction.IsoSensitivityIncrement);
                s_keytable.append(AppRoot.USER_KEYCODE.DIAL1_LEFT, CustomizableFunction.ShutterSpeedDecrement);
                s_keytable.append(AppRoot.USER_KEYCODE.DIAL1_RIGHT, CustomizableFunction.ShutterSpeedIncrement);
                s_keytable.append(AppRoot.USER_KEYCODE.DIAL2_LEFT, CustomizableFunction.ExposureCompensationDecrement);
                s_keytable.append(AppRoot.USER_KEYCODE.DIAL2_RIGHT, CustomizableFunction.ExposureCompensationIncrement);
                s_keytable.append(AppRoot.USER_KEYCODE.SHUTTLE_LEFT, CustomizableFunction.IsoSensitivityDecrement);
                s_keytable.append(AppRoot.USER_KEYCODE.SHUTTLE_RIGHT, CustomizableFunction.IsoSensitivityIncrement);
            } else if (type == DialType.TWO_DIAL) {
                m_keytable.append(AppRoot.USER_KEYCODE.DIAL1_LEFT, CustomizableFunction.ApertureDecrement);
                m_keytable.append(AppRoot.USER_KEYCODE.DIAL1_RIGHT, CustomizableFunction.ApertureIncrement);
                m_keytable.append(AppRoot.USER_KEYCODE.SHUTTLE_LEFT, CustomizableFunction.ShutterSpeedDecrement);
                m_keytable.append(AppRoot.USER_KEYCODE.SHUTTLE_RIGHT, CustomizableFunction.ShutterSpeedIncrement);
                p_keytable.append(AppRoot.USER_KEYCODE.DIAL1_LEFT, CustomizableFunction.ProgramShiftDecrement);
                p_keytable.append(AppRoot.USER_KEYCODE.DIAL1_RIGHT, CustomizableFunction.ProgramShiftIncrement);
                a_keytable.append(AppRoot.USER_KEYCODE.DIAL1_LEFT, CustomizableFunction.ApertureDecrement);
                a_keytable.append(AppRoot.USER_KEYCODE.DIAL1_RIGHT, CustomizableFunction.ApertureIncrement);
                s_keytable.append(AppRoot.USER_KEYCODE.DIAL1_LEFT, CustomizableFunction.ShutterSpeedDecrement);
                s_keytable.append(AppRoot.USER_KEYCODE.DIAL1_RIGHT, CustomizableFunction.ShutterSpeedIncrement);
                other_keytable.append(AppRoot.USER_KEYCODE.DIAL1_LEFT, CustomizableFunction.EeMainPrev);
                other_keytable.append(AppRoot.USER_KEYCODE.DIAL1_RIGHT, CustomizableFunction.EeMainNext);
            } else if (type == DialType.ONE_DIAL) {
                m_keytable.append(AppRoot.USER_KEYCODE.SHUTTLE_LEFT, CustomizableFunction.ShutterSpeedDecrement);
                m_keytable.append(AppRoot.USER_KEYCODE.SHUTTLE_RIGHT, CustomizableFunction.ShutterSpeedIncrement);
                p_keytable.append(AppRoot.USER_KEYCODE.SHUTTLE_LEFT, CustomizableFunction.ProgramShiftDecrement);
                p_keytable.append(AppRoot.USER_KEYCODE.SHUTTLE_RIGHT, CustomizableFunction.ProgramShiftIncrement);
                a_keytable.append(AppRoot.USER_KEYCODE.SHUTTLE_LEFT, CustomizableFunction.ApertureDecrement);
                a_keytable.append(AppRoot.USER_KEYCODE.SHUTTLE_RIGHT, CustomizableFunction.ApertureIncrement);
                s_keytable.append(AppRoot.USER_KEYCODE.SHUTTLE_LEFT, CustomizableFunction.ShutterSpeedIncrement);
                s_keytable.append(AppRoot.USER_KEYCODE.SHUTTLE_RIGHT, CustomizableFunction.ShutterSpeedDecrement);
            }
            table.put(ICustomKey.CATEGORY_SHOOTING_P, p_keytable);
            table.put(ICustomKey.CATEGORY_SHOOTING_A, a_keytable);
            table.put(ICustomKey.CATEGORY_SHOOTING_S, s_keytable);
            table.put(ICustomKey.CATEGORY_SHOOTING_M, m_keytable);
            table.put(ICustomKey.CATEGORY_SHOOTING_OTHER, other_keytable);
            table.put(ICustomKey.CATEGORY_MOVIE_P, p_keytable);
            table.put(ICustomKey.CATEGORY_MOVIE_A, a_keytable);
            table.put(ICustomKey.CATEGORY_MOVIE_S, s_keytable);
            table.put(ICustomKey.CATEGORY_MOVIE_M, m_keytable);
            table.put(ICustomKey.CATEGORY_MOVIE_OTHER, other_keytable);
            table.put(ICustomKey.CATEGORY_SINGLE, single_keytable);
            table.put(ICustomKey.CATEGORY_INDEX, index_keytable);
            table.put(ICustomKey.CATEGORY_MENU, menu_keytable);
            table.put(ICustomKey.CATEGORY_FOCUS_SETTING, focussetting_keytable);
        }
    }

    @Override // com.sony.imaging.app.fw.ICustomKey
    public void setAssigned(IKeyFunction func) {
        this.assignedFunc = func;
    }

    @Override // com.sony.imaging.app.fw.ICustomKey
    public IKeyFunction getAssigned() {
        updateAssigned();
        IKeyFunction func = this.assignedFunc;
        if (CustomizableFunction.Custom.equals(func)) {
            return CustomKeyMgr.getInstance().getCustomKeyFunction(this.mKeycode);
        }
        return func;
    }

    @Override // com.sony.imaging.app.fw.ICustomKey
    public IKeyFunction getAssigned(String category) {
        this.mCategory = category;
        return getAssigned();
    }

    protected void initAssigned() {
        IKeyFunction func;
        if (cmn_table != null && -1 != this.mKeycode && (func = cmn_table.get(this.mKeycode)) != null) {
            this.mCategoryDependent = false;
            setAssigned(func);
        }
    }

    public void updateAssigned() {
        if (this.mCategoryDependent && -1 != this.mKeycode) {
            IKeyFunction func = null;
            SparseArray<IKeyFunction> t = table.get(this.mCategory);
            if (t != null) {
                IKeyFunction func2 = t.get(this.mKeycode);
                func = func2;
            }
            if (func == null) {
                func = CustomizableFunction.Unchanged;
            }
            setAssigned(func);
        }
    }

    @Override // com.sony.imaging.app.fw.ICustomKey
    public IKeyFunction getFunctionCode(String category) {
        this.mCategory = category;
        updateAssigned();
        return this.assignedFunc;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public enum DialType {
        ONE_DIAL,
        TWO_DIAL,
        THREE_DIAL,
        NO_SUPPORT;

        public static DialType getDialType(int dialNum) {
            DialType dialType = NO_SUPPORT;
            if (dialNum == 1) {
                DialType type = ONE_DIAL;
                return type;
            }
            if (dialNum == 2) {
                DialType type2 = TWO_DIAL;
                return type2;
            }
            if (dialNum == 3) {
                DialType type3 = THREE_DIAL;
                return type3;
            }
            DialType type4 = TWO_DIAL;
            return type4;
        }
    }

    protected static DialType getDialType() {
        KeyStatus s1 = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.DIAL1_LEFT);
        KeyStatus s2 = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.DIAL2_LEFT);
        KeyStatus s3 = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.SHUTTLE_LEFT);
        int[] valids = {s1.valid, s2.valid, s3.valid};
        int num = getSupportedDialNum(valids);
        return DialType.getDialType(num);
    }

    private static int getSupportedDialNum(int[] valids) {
        int num = 0;
        for (int i : valids) {
            if (i == 1) {
                num++;
            }
        }
        return num;
    }
}
