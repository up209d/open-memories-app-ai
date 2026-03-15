package com.sony.imaging.app.fw;

import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;
import com.sony.scalar.sysutil.didep.Settings;
import java.util.HashMap;

/* loaded from: classes.dex */
public class P1Key implements ICustomKey {
    public static final int EV_POSITION_0 = 0;
    private static final String MSG_COMMA = ", ";
    private static final String MSG_INITTABLE_EVDIAL = "initTable EVDial Status : ";
    private static final String TAG = "P1Key";
    protected static HashMap<String, SparseArray<IKeyFunction>> current_table;
    private static final StringBuilder log_string = new StringBuilder();
    private static HashMap<String, Integer> sModeTable = new HashMap<>();
    protected static HashMap<String, SparseArray<IKeyFunction>> table;
    protected static HashMap<String, SparseArray<IKeyFunction>> table_ev_dial_effective;
    private int mKeycode;

    static {
        sModeTable.put(ICustomKey.CATEGORY_INDEX, 6);
        sModeTable.put(ICustomKey.CATEGORY_MENU, 7);
        sModeTable.put(ICustomKey.CATEGORY_FOCUS_SETTING, 13);
        sModeTable.put(ICustomKey.CATEGORY_SHOOTING_A, 1);
        sModeTable.put(ICustomKey.CATEGORY_SHOOTING_M, 3);
        sModeTable.put(ICustomKey.CATEGORY_SHOOTING_OTHER, 4);
        sModeTable.put(ICustomKey.CATEGORY_SHOOTING_P, 0);
        sModeTable.put(ICustomKey.CATEGORY_SHOOTING_S, 2);
        sModeTable.put(ICustomKey.CATEGORY_MOVIE_A, 9);
        sModeTable.put(ICustomKey.CATEGORY_MOVIE_M, 11);
        sModeTable.put(ICustomKey.CATEGORY_MOVIE_OTHER, 12);
        sModeTable.put(ICustomKey.CATEGORY_MOVIE_P, 8);
        sModeTable.put(ICustomKey.CATEGORY_MOVIE_S, 10);
        sModeTable.put(ICustomKey.CATEGORY_SINGLE, 5);
    }

    public static void pause() {
        if (table != null) {
            table.clear();
            table = null;
        }
        if (table_ev_dial_effective != null) {
            table_ev_dial_effective.clear();
            table_ev_dial_effective = null;
        }
        current_table = null;
    }

    static void initTable() {
        if (table == null) {
            table = new HashMap<>();
            table_ev_dial_effective = new HashMap<>();
            SparseArray<IKeyFunction> p_keytable = new SparseArray<>();
            SparseArray<IKeyFunction> a_keytable = new SparseArray<>();
            SparseArray<IKeyFunction> s_keytable = new SparseArray<>();
            SparseArray<IKeyFunction> m_keytable = new SparseArray<>();
            SparseArray<IKeyFunction> other_keytable = new SparseArray<>();
            SparseArray<IKeyFunction> p_keytable_ev_effective = new SparseArray<>();
            SparseArray<IKeyFunction> a_keytable_ev_effective = new SparseArray<>();
            SparseArray<IKeyFunction> s_keytable_ev_effective = new SparseArray<>();
            SparseArray<IKeyFunction> movie_p_keytable = new SparseArray<>();
            SparseArray<IKeyFunction> movie_a_keytable = new SparseArray<>();
            SparseArray<IKeyFunction> movie_s_keytable = new SparseArray<>();
            SparseArray<IKeyFunction> movie_m_keytable = new SparseArray<>();
            SparseArray<IKeyFunction> movie_other_keytable = new SparseArray<>();
            SparseArray<IKeyFunction> movie_p_keytable_ev_effective = new SparseArray<>();
            SparseArray<IKeyFunction> movie_a_keytable_ev_effective = new SparseArray<>();
            SparseArray<IKeyFunction> movie_s_keytable_ev_effective = new SparseArray<>();
            int dialExposureSetting = Settings.getDialExposureCompensation();
            if (1 == dialExposureSetting) {
                p_keytable.append(AppRoot.USER_KEYCODE.DIAL1_LEFT, CustomizableFunction.ExposureCompensationDecrement);
                p_keytable.append(AppRoot.USER_KEYCODE.DIAL1_RIGHT, CustomizableFunction.ExposureCompensationIncrement);
                a_keytable.append(AppRoot.USER_KEYCODE.DIAL1_LEFT, CustomizableFunction.ExposureCompensationDecrement);
                a_keytable.append(AppRoot.USER_KEYCODE.DIAL1_RIGHT, CustomizableFunction.ExposureCompensationIncrement);
                s_keytable.append(AppRoot.USER_KEYCODE.DIAL1_LEFT, CustomizableFunction.ExposureCompensationDecrement);
                s_keytable.append(AppRoot.USER_KEYCODE.DIAL1_RIGHT, CustomizableFunction.ExposureCompensationIncrement);
                movie_p_keytable.append(AppRoot.USER_KEYCODE.DIAL1_LEFT, CustomizableFunction.ExposureCompensationDecrement);
                movie_p_keytable.append(AppRoot.USER_KEYCODE.DIAL1_RIGHT, CustomizableFunction.ExposureCompensationIncrement);
                movie_a_keytable.append(AppRoot.USER_KEYCODE.DIAL1_LEFT, CustomizableFunction.ExposureCompensationDecrement);
                movie_a_keytable.append(AppRoot.USER_KEYCODE.DIAL1_RIGHT, CustomizableFunction.ExposureCompensationIncrement);
                movie_s_keytable.append(AppRoot.USER_KEYCODE.DIAL1_LEFT, CustomizableFunction.ExposureCompensationDecrement);
                movie_s_keytable.append(AppRoot.USER_KEYCODE.DIAL1_RIGHT, CustomizableFunction.ExposureCompensationIncrement);
            } else {
                p_keytable.append(AppRoot.USER_KEYCODE.DIAL1_LEFT, CustomizableFunction.ProgramShiftDecrement);
                p_keytable.append(AppRoot.USER_KEYCODE.DIAL1_RIGHT, CustomizableFunction.ProgramShiftIncrement);
                a_keytable.append(AppRoot.USER_KEYCODE.DIAL1_LEFT, CustomizableFunction.ApertureDecrement);
                a_keytable.append(AppRoot.USER_KEYCODE.DIAL1_RIGHT, CustomizableFunction.ApertureIncrement);
                s_keytable.append(AppRoot.USER_KEYCODE.DIAL1_LEFT, CustomizableFunction.ShutterSpeedDecrement);
                s_keytable.append(AppRoot.USER_KEYCODE.DIAL1_RIGHT, CustomizableFunction.ShutterSpeedIncrement);
                movie_a_keytable.append(AppRoot.USER_KEYCODE.DIAL1_LEFT, CustomizableFunction.ApertureDecrement);
                movie_a_keytable.append(AppRoot.USER_KEYCODE.DIAL1_RIGHT, CustomizableFunction.ApertureIncrement);
                movie_s_keytable.append(AppRoot.USER_KEYCODE.DIAL1_LEFT, CustomizableFunction.ShutterSpeedDecrement);
                movie_s_keytable.append(AppRoot.USER_KEYCODE.DIAL1_RIGHT, CustomizableFunction.ShutterSpeedIncrement);
            }
            if (2 == dialExposureSetting) {
                p_keytable.append(AppRoot.USER_KEYCODE.DIAL2_LEFT, CustomizableFunction.ExposureCompensationDecrement);
                p_keytable.append(AppRoot.USER_KEYCODE.DIAL2_RIGHT, CustomizableFunction.ExposureCompensationIncrement);
                a_keytable.append(AppRoot.USER_KEYCODE.DIAL2_LEFT, CustomizableFunction.ExposureCompensationDecrement);
                a_keytable.append(AppRoot.USER_KEYCODE.DIAL2_RIGHT, CustomizableFunction.ExposureCompensationIncrement);
                s_keytable.append(AppRoot.USER_KEYCODE.DIAL2_LEFT, CustomizableFunction.ExposureCompensationDecrement);
                s_keytable.append(AppRoot.USER_KEYCODE.DIAL2_RIGHT, CustomizableFunction.ExposureCompensationIncrement);
                movie_p_keytable.append(AppRoot.USER_KEYCODE.DIAL2_LEFT, CustomizableFunction.ExposureCompensationDecrement);
                movie_p_keytable.append(AppRoot.USER_KEYCODE.DIAL2_RIGHT, CustomizableFunction.ExposureCompensationIncrement);
                movie_a_keytable.append(AppRoot.USER_KEYCODE.DIAL2_LEFT, CustomizableFunction.ExposureCompensationDecrement);
                movie_a_keytable.append(AppRoot.USER_KEYCODE.DIAL2_RIGHT, CustomizableFunction.ExposureCompensationIncrement);
                movie_s_keytable.append(AppRoot.USER_KEYCODE.DIAL2_LEFT, CustomizableFunction.ExposureCompensationDecrement);
                movie_s_keytable.append(AppRoot.USER_KEYCODE.DIAL2_RIGHT, CustomizableFunction.ExposureCompensationIncrement);
            } else {
                p_keytable.append(AppRoot.USER_KEYCODE.DIAL2_LEFT, CustomizableFunction.ProgramShiftDecrement);
                p_keytable.append(AppRoot.USER_KEYCODE.DIAL2_RIGHT, CustomizableFunction.ProgramShiftIncrement);
                a_keytable.append(AppRoot.USER_KEYCODE.DIAL2_LEFT, CustomizableFunction.ApertureDecrement);
                a_keytable.append(AppRoot.USER_KEYCODE.DIAL2_RIGHT, CustomizableFunction.ApertureIncrement);
                s_keytable.append(AppRoot.USER_KEYCODE.DIAL2_LEFT, CustomizableFunction.ShutterSpeedDecrement);
                s_keytable.append(AppRoot.USER_KEYCODE.DIAL2_RIGHT, CustomizableFunction.ShutterSpeedIncrement);
                movie_a_keytable.append(AppRoot.USER_KEYCODE.DIAL2_LEFT, CustomizableFunction.ApertureDecrement);
                movie_a_keytable.append(AppRoot.USER_KEYCODE.DIAL2_RIGHT, CustomizableFunction.ApertureIncrement);
                movie_s_keytable.append(AppRoot.USER_KEYCODE.DIAL2_LEFT, CustomizableFunction.ShutterSpeedDecrement);
                movie_s_keytable.append(AppRoot.USER_KEYCODE.DIAL2_RIGHT, CustomizableFunction.ShutterSpeedIncrement);
            }
            p_keytable_ev_effective.append(AppRoot.USER_KEYCODE.DIAL1_LEFT, CustomizableFunction.ProgramShiftDecrement);
            p_keytable_ev_effective.append(AppRoot.USER_KEYCODE.DIAL1_RIGHT, CustomizableFunction.ProgramShiftIncrement);
            a_keytable_ev_effective.append(AppRoot.USER_KEYCODE.DIAL1_LEFT, CustomizableFunction.ApertureDecrement);
            a_keytable_ev_effective.append(AppRoot.USER_KEYCODE.DIAL1_RIGHT, CustomizableFunction.ApertureIncrement);
            s_keytable_ev_effective.append(AppRoot.USER_KEYCODE.DIAL1_LEFT, CustomizableFunction.ShutterSpeedDecrement);
            s_keytable_ev_effective.append(AppRoot.USER_KEYCODE.DIAL1_RIGHT, CustomizableFunction.ShutterSpeedIncrement);
            movie_a_keytable_ev_effective.append(AppRoot.USER_KEYCODE.DIAL1_LEFT, CustomizableFunction.ApertureDecrement);
            movie_a_keytable_ev_effective.append(AppRoot.USER_KEYCODE.DIAL1_RIGHT, CustomizableFunction.ApertureIncrement);
            movie_s_keytable_ev_effective.append(AppRoot.USER_KEYCODE.DIAL1_LEFT, CustomizableFunction.ShutterSpeedDecrement);
            movie_s_keytable_ev_effective.append(AppRoot.USER_KEYCODE.DIAL1_RIGHT, CustomizableFunction.ShutterSpeedIncrement);
            p_keytable_ev_effective.append(AppRoot.USER_KEYCODE.DIAL2_LEFT, CustomizableFunction.ProgramShiftDecrement);
            p_keytable_ev_effective.append(AppRoot.USER_KEYCODE.DIAL2_RIGHT, CustomizableFunction.ProgramShiftIncrement);
            a_keytable_ev_effective.append(AppRoot.USER_KEYCODE.DIAL2_LEFT, CustomizableFunction.ApertureDecrement);
            a_keytable_ev_effective.append(AppRoot.USER_KEYCODE.DIAL2_RIGHT, CustomizableFunction.ApertureIncrement);
            s_keytable_ev_effective.append(AppRoot.USER_KEYCODE.DIAL2_LEFT, CustomizableFunction.ShutterSpeedDecrement);
            s_keytable_ev_effective.append(AppRoot.USER_KEYCODE.DIAL2_RIGHT, CustomizableFunction.ShutterSpeedIncrement);
            movie_a_keytable_ev_effective.append(AppRoot.USER_KEYCODE.DIAL2_LEFT, CustomizableFunction.ApertureDecrement);
            movie_a_keytable_ev_effective.append(AppRoot.USER_KEYCODE.DIAL2_RIGHT, CustomizableFunction.ApertureIncrement);
            movie_s_keytable_ev_effective.append(AppRoot.USER_KEYCODE.DIAL2_LEFT, CustomizableFunction.ShutterSpeedDecrement);
            movie_s_keytable_ev_effective.append(AppRoot.USER_KEYCODE.DIAL2_RIGHT, CustomizableFunction.ShutterSpeedIncrement);
            int dialSetting = Settings.getDialSetting();
            m_keytable.append(AppRoot.USER_KEYCODE.DIAL1_RIGHT, 1 == dialSetting ? CustomizableFunction.ApertureIncrement : CustomizableFunction.ShutterSpeedIncrement);
            m_keytable.append(AppRoot.USER_KEYCODE.DIAL1_LEFT, 1 == dialSetting ? CustomizableFunction.ApertureDecrement : CustomizableFunction.ShutterSpeedDecrement);
            m_keytable.append(AppRoot.USER_KEYCODE.DIAL2_RIGHT, 1 == dialSetting ? CustomizableFunction.ShutterSpeedIncrement : CustomizableFunction.ApertureIncrement);
            m_keytable.append(AppRoot.USER_KEYCODE.DIAL2_LEFT, 1 == dialSetting ? CustomizableFunction.ShutterSpeedDecrement : CustomizableFunction.ApertureDecrement);
            movie_m_keytable.append(AppRoot.USER_KEYCODE.DIAL1_RIGHT, 1 == dialSetting ? CustomizableFunction.ApertureIncrement : CustomizableFunction.ShutterSpeedIncrement);
            movie_m_keytable.append(AppRoot.USER_KEYCODE.DIAL1_LEFT, 1 == dialSetting ? CustomizableFunction.ApertureDecrement : CustomizableFunction.ShutterSpeedDecrement);
            movie_m_keytable.append(AppRoot.USER_KEYCODE.DIAL2_RIGHT, 1 == dialSetting ? CustomizableFunction.ShutterSpeedIncrement : CustomizableFunction.ApertureIncrement);
            movie_m_keytable.append(AppRoot.USER_KEYCODE.DIAL2_LEFT, 1 == dialSetting ? CustomizableFunction.ShutterSpeedDecrement : CustomizableFunction.ApertureDecrement);
            other_keytable.append(AppRoot.USER_KEYCODE.DIAL1_LEFT, CustomizableFunction.EeMainPrev);
            other_keytable.append(AppRoot.USER_KEYCODE.DIAL1_RIGHT, CustomizableFunction.EeMainNext);
            other_keytable.append(AppRoot.USER_KEYCODE.DIAL2_LEFT, CustomizableFunction.EeSubPrev);
            other_keytable.append(AppRoot.USER_KEYCODE.DIAL2_RIGHT, CustomizableFunction.EeSubNext);
            movie_other_keytable.append(AppRoot.USER_KEYCODE.DIAL1_LEFT, CustomizableFunction.EeMainPrev);
            movie_other_keytable.append(AppRoot.USER_KEYCODE.DIAL1_RIGHT, CustomizableFunction.EeMainNext);
            movie_other_keytable.append(AppRoot.USER_KEYCODE.DIAL2_LEFT, CustomizableFunction.EeSubPrev);
            movie_other_keytable.append(AppRoot.USER_KEYCODE.DIAL2_RIGHT, CustomizableFunction.EeSubNext);
            table.put(ICustomKey.CATEGORY_SHOOTING_P, p_keytable);
            table.put(ICustomKey.CATEGORY_SHOOTING_A, a_keytable);
            table.put(ICustomKey.CATEGORY_SHOOTING_S, s_keytable);
            table.put(ICustomKey.CATEGORY_SHOOTING_M, m_keytable);
            table.put(ICustomKey.CATEGORY_SHOOTING_OTHER, other_keytable);
            table.put(ICustomKey.CATEGORY_MOVIE_P, movie_p_keytable);
            table.put(ICustomKey.CATEGORY_MOVIE_A, movie_a_keytable);
            table.put(ICustomKey.CATEGORY_MOVIE_S, movie_s_keytable);
            table.put(ICustomKey.CATEGORY_MOVIE_M, movie_m_keytable);
            table.put(ICustomKey.CATEGORY_MOVIE_OTHER, movie_other_keytable);
            table_ev_dial_effective.put(ICustomKey.CATEGORY_SHOOTING_P, p_keytable_ev_effective);
            table_ev_dial_effective.put(ICustomKey.CATEGORY_SHOOTING_A, a_keytable_ev_effective);
            table_ev_dial_effective.put(ICustomKey.CATEGORY_SHOOTING_S, s_keytable_ev_effective);
            table_ev_dial_effective.put(ICustomKey.CATEGORY_SHOOTING_M, m_keytable);
            table_ev_dial_effective.put(ICustomKey.CATEGORY_SHOOTING_OTHER, other_keytable);
            table_ev_dial_effective.put(ICustomKey.CATEGORY_MOVIE_P, movie_p_keytable_ev_effective);
            table_ev_dial_effective.put(ICustomKey.CATEGORY_MOVIE_A, movie_a_keytable_ev_effective);
            table_ev_dial_effective.put(ICustomKey.CATEGORY_MOVIE_S, movie_s_keytable_ev_effective);
            table_ev_dial_effective.put(ICustomKey.CATEGORY_MOVIE_M, movie_m_keytable);
            table_ev_dial_effective.put(ICustomKey.CATEGORY_MOVIE_OTHER, movie_other_keytable);
        }
        if (current_table == null) {
            KeyStatus key = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.EV_DIAL_CHANGED);
            if (key == null) {
                current_table = table;
                return;
            }
            log_string.replace(0, log_string.length(), MSG_INITTABLE_EVDIAL).append(key.valid).append(", ").append(key.status);
            Log.i(TAG, log_string.toString());
            if (1 == key.valid && key.status != 0) {
                current_table = table_ev_dial_effective;
            } else {
                current_table = table;
            }
        }
    }

    public static void onKeyDown(int keyCode, KeyEvent event) {
        if (620 == event.getScanCode()) {
            current_table = null;
        }
    }

    public P1Key(int keycode) {
        this.mKeycode = keycode;
    }

    @Override // com.sony.imaging.app.fw.ICustomKey
    public IKeyFunction getFunctionCode(String category) {
        int funcId = ScalarInput.getKeyLogicCode(this.mKeycode, 1, sModeTable.get(category).intValue());
        IKeyFunction func = CustomizableFunction.logicKeyCode2CustomizableFunction(funcId);
        if (func == null) {
            return CustomizableFunction.Unchanged;
        }
        return func;
    }

    @Override // com.sony.imaging.app.fw.ICustomKey
    public IKeyFunction getAssigned() {
        return getAssigned("None");
    }

    @Override // com.sony.imaging.app.fw.ICustomKey
    public IKeyFunction getAssigned(String category) {
        IKeyFunction func = getFunctionCode(category);
        if (CustomizableFunction.Custom.equals(func)) {
            return CustomKeyMgr.getInstance().getCustomKeyFunction(this.mKeycode);
        }
        if (CustomizableFunction.Decided_by_Exposure.equals(func)) {
            initTable();
            SparseArray<IKeyFunction> array = current_table.get(category);
            if (array == null) {
                return CustomizableFunction.Unknown;
            }
            return array.get(this.mKeycode, CustomizableFunction.DoNothing);
        }
        return func;
    }

    @Override // com.sony.imaging.app.fw.ICustomKey
    public void setAssigned(IKeyFunction func) {
    }
}
