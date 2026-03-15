package com.sony.imaging.app.base.beep;

import android.util.SparseArray;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.BeepUtilityIdTableBase;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class BeepUtilityIdTable extends BeepUtilityIdTableBase {
    private static final int DEFAULT_MAP_SIZE = 32;
    public static final int KEY_BEEP_PATTERN_ALL_EDIT_PLAY_CONFIRM = 13;
    public static final int KEY_BEEP_PATTERN_AUTO_REVIEW = 15;
    public static final int KEY_BEEP_PATTERN_BASIC = 8;
    public static final int KEY_BEEP_PATTERN_BASIC_PLAY = 9;
    public static final int KEY_BEEP_PATTERN_DIALOG = 5;
    public static final int KEY_BEEP_PATTERN_MENU = 0;
    public static final int KEY_BEEP_PATTERN_MULTI_EDIT_PLAY_CONFIRM = 12;
    public static final int KEY_BEEP_PATTERN_MULTI_EDIT_PLAY_INDEX = 11;
    public static final int KEY_BEEP_PATTERN_MULTI_EDIT_PLAY_SINGLE = 10;
    public static final int KEY_BEEP_PATTERN_PLAY_INDEX = 17;
    public static final int KEY_BEEP_PATTERN_PLAY_ROOT = 16;
    public static final int KEY_BEEP_PATTERN_PLAY_SINGLE = 14;
    public static final int KEY_BEEP_PATTERN_PLAY_ZOOM = 18;
    public static final int KEY_BEEP_PATTERN_REC_FUNC = 7;
    public static final int KEY_BEEP_PATTERN_REC_NORMAL = 6;
    public static final int KEY_BEEP_PATTERN_SETTING_COMMON = 1;
    public static final int KEY_BEEP_PATTERN_SETTING_COMMON_NO_WHEEL = 2;
    public static final int KEY_BEEP_PATTERN_SETTING_PLAY = 3;
    public static final int KEY_BEEP_PATTERN_SETTING_PLAY_POPUP = 4;
    private static SparseArray<String> KEY_BEEP_PATTERN_MENU_TBL = null;
    private static SparseArray<String> KEY_BEEP_PATTERN_SETTING_COMMON_TBL = null;
    private static SparseArray<String> KEY_BEEP_PATTERN_SETTING_COMMON_NO_WHEEL_TBL = null;
    private static SparseArray<String> KEY_BEEP_PATTERN_SETTING_PLAY_TBL = null;
    private static SparseArray<String> KEY_BEEP_PATTERN_SETTING_PLAY_POPUP_TBL = null;
    private static SparseArray<String> KEY_BEEP_PATTERN_DIALOG_TBL = null;
    private static SparseArray<String> KEY_BEEP_PATTERN_REC_NORMAL_TBL = null;
    private static SparseArray<String> KEY_BEEP_PATTERN_AUTO_REVIEW_TBL = null;
    private static SparseArray<String> KEY_BEEP_PATTERN_REC_FUNC_TBL = null;
    private static SparseArray<String> KEY_BEEP_PATTERN_BASIC_TBL = null;
    private static SparseArray<String> KEY_BEEP_PATTERN_BASIC_PLAY_TBL = null;
    private static SparseArray<String> KEY_BEEP_PATTERN_PLAY_ROOT_TBL = null;
    private static SparseArray<String> KEY_BEEP_PATTERN_MULTI_EDIT_PLAY_SINGLE_TBL = null;
    private static SparseArray<String> KEY_BEEP_PATTERN_MULTI_EDIT_PLAY_INDEX_TBL = null;
    private static SparseArray<String> KEY_BEEP_PATTERN_MULTI_EDIT_PLAY_CONFIRM_TBL = null;
    private static SparseArray<String> KEY_BEEP_PATTERN_ALL_EDIT_PLAY_CONFIRM_TBL = null;
    private static SparseArray<String> KEY_BEEP_PATTERN_PLAY_SINGLE_TBL = null;
    private static SparseArray<String> KEY_BEEP_PATTERN_PLAY_INDEX_TBL = null;
    private static SparseArray<String> KEY_BEEP_PATTERN_PLAY_ZOOM_TBL = null;

    @Override // com.sony.imaging.app.util.BeepUtilityIdTableBase
    public void init() {
        int i = 32;
        if (this.mKeyBeepPatternTable == null) {
            KEY_BEEP_PATTERN_MENU_TBL = new SparseArray<String>(i) { // from class: com.sony.imaging.app.base.beep.BeepUtilityIdTable.1
                {
                    put(103, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DOWN, "BEEP_ID_WHEEL_DOWN");
                    if (Environment.isNewBizDeviceActionCam()) {
                        put(AppRoot.USER_KEYCODE.LEFT, BeepUtilityRsrcTable.BEEP_ID_MOVE_LEFT);
                        put(AppRoot.USER_KEYCODE.RIGHT, BeepUtilityRsrcTable.BEEP_ID_MOVE_RIGHT);
                    } else {
                        put(AppRoot.USER_KEYCODE.LEFT, "BEEP_ID_WHEEL_DOWN");
                        put(AppRoot.USER_KEYCODE.RIGHT, "BEEP_ID_WHEEL_UP");
                    }
                    put(AppRoot.USER_KEYCODE.CENTER, "BEEP_ID_SWITCH_MODE");
                    put(AppRoot.USER_KEYCODE.SK1, "BEEP_ID_BACK_CANCEL");
                    put(AppRoot.USER_KEYCODE.SK2, "BEEP_ID_OPTION_ON");
                    put(AppRoot.USER_KEYCODE.SHUTTLE_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.SHUTTLE_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.DIAL2_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DIAL2_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.DIAL1_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DIAL1_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.FN, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.MOVIE_REC, "BEEP_ID_SELECT");
                }
            };
            KEY_BEEP_PATTERN_SETTING_COMMON_TBL = new SparseArray<String>(i) { // from class: com.sony.imaging.app.base.beep.BeepUtilityIdTable.2
                {
                    put(103, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DOWN, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.CENTER, "BEEP_ID_SWITCH_MODE");
                    put(AppRoot.USER_KEYCODE.SK1, "BEEP_ID_BACK_CANCEL");
                    put(AppRoot.USER_KEYCODE.SK2, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.SHUTTLE_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.SHUTTLE_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.DIAL2_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DIAL2_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.DIAL1_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DIAL1_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.FN, BeepUtilityRsrcTable.BEEP_ID_NONE);
                }
            };
            KEY_BEEP_PATTERN_SETTING_COMMON_NO_WHEEL_TBL = new SparseArray<String>(i) { // from class: com.sony.imaging.app.base.beep.BeepUtilityIdTable.3
                {
                    put(103, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DOWN, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.CENTER, "BEEP_ID_SWITCH_MODE");
                    put(AppRoot.USER_KEYCODE.MENU, "BEEP_ID_BACK_CANCEL");
                    put(AppRoot.USER_KEYCODE.SK1, "BEEP_ID_BACK_CANCEL");
                    put(AppRoot.USER_KEYCODE.SK2, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.SHUTTLE_RIGHT, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.SHUTTLE_LEFT, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.DIAL1_RIGHT, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.DIAL1_LEFT, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.DIAL2_RIGHT, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.DIAL2_LEFT, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.AEL, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.AF_MF, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.PLAYBACK, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.FN, BeepUtilityRsrcTable.BEEP_ID_NONE);
                }
            };
            KEY_BEEP_PATTERN_SETTING_PLAY_TBL = new SparseArray<String>(i) { // from class: com.sony.imaging.app.base.beep.BeepUtilityIdTable.4
                {
                    put(AppRoot.USER_KEYCODE.PLAYBACK, "BEEP_ID_SWITCH_MODE");
                    put(103, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DOWN, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.LEFT, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.RIGHT, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.CENTER, "BEEP_ID_SWITCH_MODE");
                    put(AppRoot.USER_KEYCODE.SK1, "BEEP_ID_BACK_CANCEL");
                    put(AppRoot.USER_KEYCODE.SK2, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.SHUTTLE_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.SHUTTLE_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.DIAL2_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DIAL2_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.DIAL1_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DIAL1_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.FN, BeepUtilityRsrcTable.BEEP_ID_NONE);
                }
            };
            KEY_BEEP_PATTERN_SETTING_PLAY_POPUP_TBL = new SparseArray<String>(i) { // from class: com.sony.imaging.app.base.beep.BeepUtilityIdTable.5
                {
                    put(AppRoot.USER_KEYCODE.PLAYBACK, "BEEP_ID_SWITCH_MODE");
                    put(103, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DOWN, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.LEFT, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.RIGHT, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.CENTER, "BEEP_ID_SWITCH_MODE");
                    put(AppRoot.USER_KEYCODE.SK1, "BEEP_ID_BACK_CANCEL");
                    put(AppRoot.USER_KEYCODE.SK2, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.SHUTTLE_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.SHUTTLE_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.DIAL2_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DIAL2_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.DIAL1_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DIAL1_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.FN, BeepUtilityRsrcTable.BEEP_ID_NONE);
                }
            };
            KEY_BEEP_PATTERN_DIALOG_TBL = new SparseArray<String>(i) { // from class: com.sony.imaging.app.base.beep.BeepUtilityIdTable.6
                {
                    put(103, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DOWN, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.CENTER, "BEEP_ID_SWITCH_MODE");
                    put(AppRoot.USER_KEYCODE.SK1, "BEEP_ID_BACK_CANCEL");
                    put(AppRoot.USER_KEYCODE.SK2, "BEEP_ID_SWITCH_MODE");
                    put(AppRoot.USER_KEYCODE.SHUTTLE_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.SHUTTLE_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.DIAL2_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DIAL2_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.DIAL1_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DIAL1_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.FN, BeepUtilityRsrcTable.BEEP_ID_NONE);
                }
            };
            KEY_BEEP_PATTERN_REC_NORMAL_TBL = new SparseArray<String>(i) { // from class: com.sony.imaging.app.base.beep.BeepUtilityIdTable.7
                {
                    put(103, "BEEP_ID_SWITCH_MODE");
                    put(AppRoot.USER_KEYCODE.DOWN, "BEEP_ID_SWITCH_MODE");
                    if (Environment.isNewBizDeviceActionCam()) {
                        put(AppRoot.USER_KEYCODE.LEFT, BeepUtilityRsrcTable.BEEP_ID_MOVE_LEFT);
                        put(AppRoot.USER_KEYCODE.RIGHT, BeepUtilityRsrcTable.BEEP_ID_MOVE_RIGHT);
                    } else {
                        put(AppRoot.USER_KEYCODE.LEFT, "BEEP_ID_OPTION_ON");
                        put(AppRoot.USER_KEYCODE.RIGHT, "BEEP_ID_OPTION_ON");
                    }
                    put(AppRoot.USER_KEYCODE.CENTER, "BEEP_ID_SWITCH_MODE");
                    put(AppRoot.USER_KEYCODE.MENU, "BEEP_ID_BACK_CANCEL");
                    put(AppRoot.USER_KEYCODE.SK1, "BEEP_ID_BACK_CANCEL");
                    put(AppRoot.USER_KEYCODE.SK2, "BEEP_ID_OPTION_ON");
                    put(AppRoot.USER_KEYCODE.AEL, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.AF_MF, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.PLAYBACK, "BEEP_ID_SWITCH_MODE");
                    put(AppRoot.USER_KEYCODE.DIAL2_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DIAL2_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.SHUTTLE_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.SHUTTLE_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.DIAL1_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DIAL1_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.FN, "BEEP_ID_SWITCH_MODE");
                }
            };
            KEY_BEEP_PATTERN_AUTO_REVIEW_TBL = new SparseArray<String>(i) { // from class: com.sony.imaging.app.base.beep.BeepUtilityIdTable.8
                {
                    put(103, "BEEP_ID_SWITCH_MODE");
                    put(AppRoot.USER_KEYCODE.DOWN, "BEEP_ID_OPTION_ON");
                    put(AppRoot.USER_KEYCODE.LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.CENTER, "BEEP_ID_OPTION_ON");
                    put(AppRoot.USER_KEYCODE.MENU, "BEEP_ID_OPTION_ON");
                    put(AppRoot.USER_KEYCODE.SK1, "BEEP_ID_OPTION_ON");
                    put(AppRoot.USER_KEYCODE.SK2, "BEEP_ID_OPTION_ON");
                    put(AppRoot.USER_KEYCODE.AEL, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.AF_MF, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.PLAYBACK, "BEEP_ID_SWITCH_MODE");
                    put(AppRoot.USER_KEYCODE.DIAL2_RIGHT, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.DIAL2_LEFT, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.SHUTTLE_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.SHUTTLE_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.DIAL1_RIGHT, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.DIAL1_LEFT, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.FN, BeepUtilityRsrcTable.BEEP_ID_NONE);
                }
            };
            KEY_BEEP_PATTERN_REC_FUNC_TBL = new SparseArray<String>(i) { // from class: com.sony.imaging.app.base.beep.BeepUtilityIdTable.9
                {
                    put(103, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DOWN, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.CENTER, "BEEP_ID_SWITCH_MODE");
                    put(AppRoot.USER_KEYCODE.SK1, "BEEP_ID_BACK_CANCEL");
                    put(AppRoot.USER_KEYCODE.SK2, "BEEP_ID_SWITCH_MODE");
                    put(AppRoot.USER_KEYCODE.SHUTTLE_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.SHUTTLE_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.DIAL2_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DIAL2_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.DIAL1_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DIAL1_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.FN, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.PLAYBACK, "BEEP_ID_SWITCH_MODE");
                }
            };
            KEY_BEEP_PATTERN_BASIC_TBL = new SparseArray<String>(i) { // from class: com.sony.imaging.app.base.beep.BeepUtilityIdTable.10
                {
                    put(103, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DOWN, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.SHUTTLE_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.SHUTTLE_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.DIAL2_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DIAL2_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.DIAL1_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DIAL1_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.FN, BeepUtilityRsrcTable.BEEP_ID_NONE);
                }
            };
            KEY_BEEP_PATTERN_BASIC_PLAY_TBL = new SparseArray<String>(i) { // from class: com.sony.imaging.app.base.beep.BeepUtilityIdTable.11
                {
                    put(103, "BEEP_ID_SWITCH_MODE");
                    put(AppRoot.USER_KEYCODE.DOWN, "BEEP_ID_OPTION_ON");
                    put(AppRoot.USER_KEYCODE.LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.SHUTTLE_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.SHUTTLE_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.DIAL2_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DIAL2_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.DIAL1_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DIAL1_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.FN, BeepUtilityRsrcTable.BEEP_ID_NONE);
                }
            };
            KEY_BEEP_PATTERN_PLAY_ROOT_TBL = new SparseArray<String>(i) { // from class: com.sony.imaging.app.base.beep.BeepUtilityIdTable.12
                {
                    put(AppRoot.USER_KEYCODE.PLAYBACK, "BEEP_ID_SWITCH_MODE");
                    put(AppRoot.USER_KEYCODE.LENS_ATTACH, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.LENS_DETACH, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.S1_ON, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.S2_ON, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.FN, BeepUtilityRsrcTable.BEEP_ID_NONE);
                }
            };
            KEY_BEEP_PATTERN_MULTI_EDIT_PLAY_SINGLE_TBL = new SparseArray<String>(i) { // from class: com.sony.imaging.app.base.beep.BeepUtilityIdTable.13
                {
                    put(AppRoot.USER_KEYCODE.PLAYBACK, "BEEP_ID_BACK_CANCEL");
                    put(AppRoot.USER_KEYCODE.SK1, "BEEP_ID_BACK_CANCEL");
                    put(AppRoot.USER_KEYCODE.MENU, "BEEP_ID_BACK_CANCEL");
                    put(AppRoot.USER_KEYCODE.SK2, "BEEP_ID_SWITCH_MODE");
                    put(AppRoot.USER_KEYCODE.CENTER, "BEEP_ID_SWITCH_MODE");
                    put(103, "BEEP_ID_SWITCH_MODE");
                    put(AppRoot.USER_KEYCODE.DOWN, "BEEP_ID_OPTION_ON");
                    put(AppRoot.USER_KEYCODE.LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.SHUTTLE_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.SHUTTLE_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.DIAL2_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DIAL2_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.DIAL1_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DIAL1_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.FN, BeepUtilityRsrcTable.BEEP_ID_NONE);
                }
            };
            KEY_BEEP_PATTERN_MULTI_EDIT_PLAY_INDEX_TBL = new SparseArray<String>(i) { // from class: com.sony.imaging.app.base.beep.BeepUtilityIdTable.14
                {
                    put(AppRoot.USER_KEYCODE.PLAYBACK, "BEEP_ID_BACK_CANCEL");
                    put(AppRoot.USER_KEYCODE.SK1, "BEEP_ID_BACK_CANCEL");
                    put(AppRoot.USER_KEYCODE.MENU, "BEEP_ID_BACK_CANCEL");
                    put(AppRoot.USER_KEYCODE.SK2, "BEEP_ID_SWITCH_MODE");
                    put(AppRoot.USER_KEYCODE.CENTER, "BEEP_ID_SWITCH_MODE");
                    put(103, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DOWN, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.SHUTTLE_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.SHUTTLE_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.DIAL2_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DIAL2_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.DIAL1_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DIAL1_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.FN, BeepUtilityRsrcTable.BEEP_ID_NONE);
                }
            };
            KEY_BEEP_PATTERN_MULTI_EDIT_PLAY_CONFIRM_TBL = new SparseArray<String>(i) { // from class: com.sony.imaging.app.base.beep.BeepUtilityIdTable.15
                {
                    put(AppRoot.USER_KEYCODE.PLAYBACK, "BEEP_ID_BACK_CANCEL");
                    put(AppRoot.USER_KEYCODE.SK1, "BEEP_ID_BACK_CANCEL");
                    put(AppRoot.USER_KEYCODE.MENU, "BEEP_ID_BACK_CANCEL");
                    put(AppRoot.USER_KEYCODE.SK2, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.CENTER, "BEEP_ID_SWITCH_MODE");
                    put(103, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.DOWN, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.LEFT, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.RIGHT, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.SHUTTLE_RIGHT, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.SHUTTLE_LEFT, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.DIAL2_RIGHT, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.DIAL2_LEFT, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.DIAL1_RIGHT, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.DIAL1_LEFT, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.FN, BeepUtilityRsrcTable.BEEP_ID_NONE);
                }
            };
            KEY_BEEP_PATTERN_ALL_EDIT_PLAY_CONFIRM_TBL = new SparseArray<String>(i) { // from class: com.sony.imaging.app.base.beep.BeepUtilityIdTable.16
                {
                    put(AppRoot.USER_KEYCODE.PLAYBACK, "BEEP_ID_SWITCH_MODE");
                    put(AppRoot.USER_KEYCODE.SK1, "BEEP_ID_BACK_CANCEL");
                    put(AppRoot.USER_KEYCODE.MENU, "BEEP_ID_BACK_CANCEL");
                    put(AppRoot.USER_KEYCODE.SK2, "BEEP_ID_SWITCH_MODE");
                    put(AppRoot.USER_KEYCODE.CENTER, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(103, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.DOWN, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.LEFT, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.RIGHT, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.SHUTTLE_RIGHT, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.SHUTTLE_LEFT, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.DIAL2_RIGHT, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.DIAL2_LEFT, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.DIAL1_RIGHT, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.DIAL1_LEFT, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.FN, BeepUtilityRsrcTable.BEEP_ID_NONE);
                }
            };
            KEY_BEEP_PATTERN_PLAY_SINGLE_TBL = new SparseArray<String>(i) { // from class: com.sony.imaging.app.base.beep.BeepUtilityIdTable.17
                {
                    put(AppRoot.USER_KEYCODE.PLAYBACK, "BEEP_ID_SWITCH_MODE");
                    put(AppRoot.USER_KEYCODE.SK1, "BEEP_ID_SWITCH_MODE");
                    put(AppRoot.USER_KEYCODE.SK2, "BEEP_ID_OPTION_ON");
                    put(AppRoot.USER_KEYCODE.CENTER, "BEEP_ID_OPTION_ON");
                    put(103, "BEEP_ID_SWITCH_MODE");
                    put(AppRoot.USER_KEYCODE.DOWN, "BEEP_ID_OPTION_ON");
                    put(AppRoot.USER_KEYCODE.LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.SHUTTLE_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.SHUTTLE_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.DIAL2_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DIAL2_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.DIAL1_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DIAL1_LEFT, "BEEP_ID_WHEEL_DOWN");
                }
            };
            KEY_BEEP_PATTERN_PLAY_INDEX_TBL = new SparseArray<String>(i) { // from class: com.sony.imaging.app.base.beep.BeepUtilityIdTable.18
                {
                    put(AppRoot.USER_KEYCODE.PLAYBACK, "BEEP_ID_SWITCH_MODE");
                    put(AppRoot.USER_KEYCODE.SK1, "BEEP_ID_SWITCH_MODE");
                    put(AppRoot.USER_KEYCODE.MENU, BeepUtilityRsrcTable.BEEP_ID_NONE);
                    put(AppRoot.USER_KEYCODE.SK2, "BEEP_ID_OPTION_ON");
                    put(AppRoot.USER_KEYCODE.CENTER, "BEEP_ID_SWITCH_MODE");
                    put(103, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DOWN, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.SHUTTLE_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.SHUTTLE_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DIAL2_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.DIAL2_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DIAL1_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.DIAL1_RIGHT, "BEEP_ID_WHEEL_UP");
                }
            };
            KEY_BEEP_PATTERN_PLAY_ZOOM_TBL = new SparseArray<String>(i) { // from class: com.sony.imaging.app.base.beep.BeepUtilityIdTable.19
                {
                    put(AppRoot.USER_KEYCODE.PLAYBACK, "BEEP_ID_BACK_CANCEL");
                    put(AppRoot.USER_KEYCODE.SK1, "BEEP_ID_BACK_CANCEL");
                    put(AppRoot.USER_KEYCODE.MENU, "BEEP_ID_BACK_CANCEL");
                    put(AppRoot.USER_KEYCODE.SK2, "BEEP_ID_OPTION_ON");
                    put(AppRoot.USER_KEYCODE.CENTER, "BEEP_ID_BACK_CANCEL");
                    put(103, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DOWN, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.SHUTTLE_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.SHUTTLE_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DIAL2_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.DIAL2_RIGHT, "BEEP_ID_WHEEL_UP");
                    put(AppRoot.USER_KEYCODE.DIAL1_LEFT, "BEEP_ID_WHEEL_DOWN");
                    put(AppRoot.USER_KEYCODE.DIAL1_RIGHT, "BEEP_ID_WHEEL_UP");
                }
            };
            this.mKeyBeepPatternTable = new SparseArray<SparseArray<String>>(i) { // from class: com.sony.imaging.app.base.beep.BeepUtilityIdTable.20
                {
                    put(0, BeepUtilityIdTable.this.getKeyBeepPatternMenuTbl());
                    put(1, BeepUtilityIdTable.this.getKeyBeepPatternSettingCommonTbl());
                    put(2, BeepUtilityIdTable.this.getKeyBeepPatternSettingCommonNoWheelTbl());
                    put(3, BeepUtilityIdTable.this.getKeyBeepPatternSettingPlayTbl());
                    put(4, BeepUtilityIdTable.this.getKeyBeepPatternSettingPlayPopupTbl());
                    put(5, BeepUtilityIdTable.this.getKeyBeepPatternDialogTbl());
                    put(6, BeepUtilityIdTable.this.getKeyBeepPatternRecNormalTbl());
                    put(7, BeepUtilityIdTable.this.getKeyBeepPatternRecFuncTbl());
                    put(8, BeepUtilityIdTable.this.getKeyBeepPatternBasicTbl());
                    put(9, BeepUtilityIdTable.this.getKeyBeepPatternBasicPlayTbl());
                    put(10, BeepUtilityIdTable.this.getKeyBeepPatternMultiEditPlaySingleTbl());
                    put(11, BeepUtilityIdTable.this.getKeyBeepPatternMultiEditPlayIndexTbl());
                    put(12, BeepUtilityIdTable.this.getKeyBeepPatternMultiEditPlayConfirmTbl());
                    put(13, BeepUtilityIdTable.this.getKeyBeepPatternAllEditPlayConfirmTbl());
                    put(14, BeepUtilityIdTable.this.getKeyBeepPatternPlaySingleTbl());
                    put(15, BeepUtilityIdTable.this.getKeyBeepPatternAutoReviewTbl());
                    put(16, BeepUtilityIdTable.this.getKeyBeepPatternPlayRootTbl());
                    put(17, BeepUtilityIdTable.this.getKeyBeepPatternPlayIndexTbl());
                    put(18, BeepUtilityIdTable.this.getKeyBeepPatternPlayZoomTbl());
                }
            };
        }
    }

    public SparseArray<String> getKeyBeepPatternMenuTbl() {
        return KEY_BEEP_PATTERN_MENU_TBL;
    }

    public SparseArray<String> getKeyBeepPatternSettingCommonTbl() {
        return KEY_BEEP_PATTERN_SETTING_COMMON_TBL;
    }

    public SparseArray<String> getKeyBeepPatternSettingCommonNoWheelTbl() {
        return KEY_BEEP_PATTERN_SETTING_COMMON_NO_WHEEL_TBL;
    }

    public SparseArray<String> getKeyBeepPatternSettingPlayTbl() {
        return KEY_BEEP_PATTERN_SETTING_PLAY_TBL;
    }

    public SparseArray<String> getKeyBeepPatternSettingPlayPopupTbl() {
        return KEY_BEEP_PATTERN_SETTING_PLAY_POPUP_TBL;
    }

    public SparseArray<String> getKeyBeepPatternDialogTbl() {
        return KEY_BEEP_PATTERN_DIALOG_TBL;
    }

    public SparseArray<String> getKeyBeepPatternRecNormalTbl() {
        return KEY_BEEP_PATTERN_REC_NORMAL_TBL;
    }

    public SparseArray<String> getKeyBeepPatternAutoReviewTbl() {
        return KEY_BEEP_PATTERN_AUTO_REVIEW_TBL;
    }

    public SparseArray<String> getKeyBeepPatternRecFuncTbl() {
        return KEY_BEEP_PATTERN_REC_FUNC_TBL;
    }

    public SparseArray<String> getKeyBeepPatternBasicTbl() {
        return KEY_BEEP_PATTERN_BASIC_TBL;
    }

    public SparseArray<String> getKeyBeepPatternBasicPlayTbl() {
        return KEY_BEEP_PATTERN_BASIC_PLAY_TBL;
    }

    public SparseArray<String> getKeyBeepPatternPlayRootTbl() {
        return KEY_BEEP_PATTERN_PLAY_ROOT_TBL;
    }

    public SparseArray<String> getKeyBeepPatternMultiEditPlaySingleTbl() {
        return KEY_BEEP_PATTERN_MULTI_EDIT_PLAY_SINGLE_TBL;
    }

    public SparseArray<String> getKeyBeepPatternMultiEditPlayIndexTbl() {
        return KEY_BEEP_PATTERN_MULTI_EDIT_PLAY_INDEX_TBL;
    }

    public SparseArray<String> getKeyBeepPatternMultiEditPlayConfirmTbl() {
        return KEY_BEEP_PATTERN_MULTI_EDIT_PLAY_CONFIRM_TBL;
    }

    public SparseArray<String> getKeyBeepPatternAllEditPlayConfirmTbl() {
        return KEY_BEEP_PATTERN_ALL_EDIT_PLAY_CONFIRM_TBL;
    }

    public SparseArray<String> getKeyBeepPatternPlaySingleTbl() {
        return KEY_BEEP_PATTERN_PLAY_SINGLE_TBL;
    }

    public SparseArray<String> getKeyBeepPatternPlayIndexTbl() {
        return KEY_BEEP_PATTERN_PLAY_INDEX_TBL;
    }

    public SparseArray<String> getKeyBeepPatternPlayZoomTbl() {
        return KEY_BEEP_PATTERN_PLAY_ZOOM_TBL;
    }
}
