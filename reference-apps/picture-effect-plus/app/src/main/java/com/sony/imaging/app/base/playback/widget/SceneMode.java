package com.sony.imaging.app.base.playback.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.sony.imaging.app.base.playback.contents.ContentInfo;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class SceneMode extends IconFileInfo {
    private static final int API_VER_SUPPORT_ADDITIONAL_SCENE_FOR_DSC = 4;
    private static final int VAL_EXPOSURE_GAIN_PRIORITY_MODE = 3;
    private static final int VAL_EXPOSURE_MANUAL = 1;
    private static final int VAL_EXPOSURE_NORMAL_PROGRAM = 2;
    private static final int VAL_EXPOSURE_SHUTTER_PRIORITY_MODE = 4;
    private static final long VAL_SCENE_SELMODE_ADVANCED_SPORTS = 29;
    private static final long VAL_SCENE_SELMODE_ANTIBLUR = 20;
    private static final long VAL_SCENE_SELMODE_AUTO_HDR = 33;
    private static final long VAL_SCENE_SELMODE_AUTO_PLUS = 22;
    private static final long VAL_SCENE_SELMODE_AUTO_PLUS_03 = 24;
    private static final long VAL_SCENE_SELMODE_BEACH = 30;
    private static final long VAL_SCENE_SELMODE_FIREWORKS = 26;
    private static final long VAL_SCENE_SELMODE_GOURMET = 27;
    private static final long VAL_SCENE_SELMODE_HIGHSENSITIVITY = 25;
    private static final long VAL_SCENE_SELMODE_IAUTO = 16;
    private static final long VAL_SCENE_SELMODE_LANDSCAPE = 6;
    private static final long VAL_SCENE_SELMODE_MACRO = 8;
    private static final long VAL_SCENE_SELMODE_NIGHTPORTRAIT = 17;
    private static final long VAL_SCENE_SELMODE_NIGHTVIEW = 3;
    private static final long VAL_SCENE_SELMODE_NORMAL = 0;
    private static final long VAL_SCENE_SELMODE_PET = 28;
    private static final long VAL_SCENE_SELMODE_PORTRAIT = 1;
    private static final long VAL_SCENE_SELMODE_SNOW = 31;
    private static final long VAL_SCENE_SELMODE_SOFT_SKIN = 34;
    private static final long VAL_SCENE_SELMODE_SPORTS = 5;
    private static final long VAL_SCENE_SELMODE_SUNSET = 4;
    private static final long VAL_SCENE_SELMODE_SWEEP_PAN = 18;
    private static final long VAL_SCENE_SELMODE_SWEEP_PAN_3D = 23;
    private static final long VAL_SCENE_SELMODE_TWILIGHT = 19;
    private static final long VAL_SCENE_SELMODE_UNDERWATER = 32;

    public SceneMode(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ImageView.ScaleType.CENTER);
    }

    @Override // com.sony.imaging.app.base.playback.widget.IconFileInfo
    public void setContentInfo(ContentInfo info) {
        long sceneMode = -1;
        int exposureProgram = -1;
        if (info != null) {
            sceneMode = info.getLong("MkNoteSceneSelectMode");
            exposureProgram = info.getInt("ExposureProgram");
        }
        setValue(sceneMode, exposureProgram);
    }

    public void setValue(long sceneMode, int exposureProgram) {
        boolean isDisplay = false;
        if (sceneMode == 0) {
            boolean isStillMain = true;
            if (Environment.getVersionPfAPI() >= 2) {
                isStillMain = ScalarProperties.getInt("ui.main.feature") != 1;
            }
            switch (exposureProgram) {
                case 1:
                    setImageResource(isStillMain ? 17304139 : 17305839);
                    isDisplay = true;
                    break;
                case 2:
                    setImageResource(isStillMain ? 17304128 : 17305612);
                    isDisplay = true;
                    break;
                case 3:
                    setImageResource(isStillMain ? 17304129 : R.drawable.stat_notify_gmail);
                    isDisplay = true;
                    break;
                case 4:
                    setImageResource(isStillMain ? 17304130 : 17305758);
                    isDisplay = true;
                    break;
            }
        } else if (sceneMode == VAL_SCENE_SELMODE_SWEEP_PAN) {
            setImageResource(R.drawable.btn_switch_to_on_mtrl_00006);
            isDisplay = true;
        } else if (sceneMode == VAL_SCENE_SELMODE_SWEEP_PAN_3D) {
            setImageResource(R.drawable.btn_toggle_holo_light);
            isDisplay = true;
        } else if (sceneMode == 5) {
            setImageResource(R.drawable.btn_toggle_off_disabled_focused_holo_dark);
            isDisplay = true;
        } else if (sceneMode == VAL_SCENE_SELMODE_PORTRAIT) {
            setImageResource(R.drawable.btn_toggle_material);
            isDisplay = true;
        } else if (sceneMode == 6) {
            setImageResource(17304153);
            isDisplay = true;
        } else if (sceneMode == VAL_SCENE_SELMODE_SUNSET) {
            setImageResource(17304155);
            isDisplay = true;
        } else if (sceneMode == 3) {
            setImageResource(17304158);
            isDisplay = true;
        } else if (sceneMode == VAL_SCENE_SELMODE_TWILIGHT) {
            setImageResource(17304160);
            isDisplay = true;
        } else if (sceneMode == VAL_SCENE_SELMODE_NIGHTPORTRAIT) {
            setImageResource(17304163);
            isDisplay = true;
        } else if (sceneMode == VAL_SCENE_SELMODE_MACRO) {
            setImageResource(17304151);
            isDisplay = true;
        } else if (sceneMode == VAL_SCENE_SELMODE_ANTIBLUR) {
            setImageResource(17306258);
            isDisplay = true;
        } else if (sceneMode == VAL_SCENE_SELMODE_PET) {
            setImageResource(17306272);
            isDisplay = true;
        } else if (sceneMode == VAL_SCENE_SELMODE_GOURMET) {
            setImageResource(17306268);
            isDisplay = true;
        } else if (sceneMode == VAL_SCENE_SELMODE_FIREWORKS) {
            setImageResource(17306275);
            isDisplay = true;
        } else if (sceneMode == VAL_SCENE_SELMODE_HIGHSENSITIVITY) {
            setImageResource(17306266);
            isDisplay = true;
        } else if (sceneMode == VAL_SCENE_SELMODE_BEACH) {
            if (Environment.getVersionPfAPI() >= 4) {
                try {
                    setImageResource(17307846);
                    isDisplay = true;
                } catch (Exception e) {
                }
            } else {
                isDisplay = false;
            }
        } else if (sceneMode == VAL_SCENE_SELMODE_SNOW) {
            if (Environment.getVersionPfAPI() >= 4) {
                try {
                    setImageResource(17307849);
                    isDisplay = true;
                } catch (Exception e2) {
                }
            } else {
                isDisplay = false;
            }
        } else if (sceneMode == VAL_SCENE_SELMODE_UNDERWATER) {
            isDisplay = false;
        } else if (sceneMode == VAL_SCENE_SELMODE_SOFT_SKIN) {
            if (Environment.getVersionPfAPI() >= 4) {
                try {
                    setImageResource(17307848);
                    isDisplay = true;
                } catch (Exception e3) {
                }
            } else {
                isDisplay = false;
            }
        } else if (sceneMode == VAL_SCENE_SELMODE_ADVANCED_SPORTS) {
            if (Environment.getVersionPfAPI() >= 4) {
                try {
                    setImageResource(17307844);
                    isDisplay = true;
                } catch (Exception e4) {
                }
            } else {
                isDisplay = false;
            }
        } else if (sceneMode == VAL_SCENE_SELMODE_AUTO_HDR) {
            if (Environment.getVersionPfAPI() >= 4) {
                try {
                    setImageResource(17307850);
                    isDisplay = true;
                } catch (Exception e5) {
                }
            } else {
                isDisplay = false;
            }
        } else if (sceneMode == VAL_SCENE_SELMODE_IAUTO) {
            setImageResource(17304166);
            isDisplay = true;
        } else if (sceneMode == VAL_SCENE_SELMODE_AUTO_PLUS || sceneMode == VAL_SCENE_SELMODE_AUTO_PLUS_03) {
            setImageResource(17304167);
            isDisplay = true;
        }
        setVisibility(isDisplay ? 0 : 4);
    }
}
