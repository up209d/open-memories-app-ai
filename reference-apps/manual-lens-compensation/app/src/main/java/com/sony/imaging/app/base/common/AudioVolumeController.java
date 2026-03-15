package com.sony.imaging.app.base.common;

import android.util.Pair;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.media.AudioManager;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class AudioVolumeController implements IController {
    public static final int INVALID_VALUE = Integer.MIN_VALUE;
    protected static final int PF_VERSION_SUPPORTING_SOUNDVOLUME = 7;
    protected static final String TAG = "AudioVolumeController";
    public static final String TAG_AUDIO_VOLUME = "audio_volume";
    private static AudioVolumeController sInstance;

    private static void setController(AudioVolumeController controller) {
        if (sInstance == null) {
            sInstance = controller;
        }
    }

    protected AudioVolumeController() {
        setController(this);
    }

    public static AudioVolumeController getInstance() {
        if (sInstance == null) {
            new AudioVolumeController();
        }
        return sInstance;
    }

    public static boolean isSupportedByPF() {
        return 7 <= Environment.getVersionPfAPI();
    }

    public void setInt(String tag, int value) {
        if (TAG_AUDIO_VOLUME.equals(tag) && isSupportedByPF()) {
            AudioManager.Parameters param = new AudioManager.Parameters();
            AudioManager.Parameters supppoted = AudioSetting.getInstance().getSupportedAudioParameters();
            int min = supppoted.getMinimumSoundVolume();
            param.setSoundVolume(value + min);
            AudioSetting.getInstance().setAudioParameters(param);
        }
    }

    public int getInt(String tag) {
        if (!TAG_AUDIO_VOLUME.equals(tag) || !isSupportedByPF()) {
            return INVALID_VALUE;
        }
        AudioManager.Parameters param = AudioSetting.getInstance().getAudioParameters();
        AudioManager.Parameters supppoted = AudioSetting.getInstance().getSupportedAudioParameters();
        int min = supppoted.getMinimumSoundVolume();
        return param.getSoundVolume() - min;
    }

    public Pair<Integer, Integer> getSupportedRange(String tag) {
        if (!TAG_AUDIO_VOLUME.equals(tag) || !isSupportedByPF()) {
            return null;
        }
        AudioManager.Parameters param = AudioSetting.getInstance().getSupportedAudioParameters();
        int max = param.getMaximumSoundVolume();
        int min = param.getMinimumSoundVolume();
        if (max == 0 || min == 0) {
            return null;
        }
        return new Pair<>(Integer.valueOf(min), Integer.valueOf(max));
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) {
        setInt(tag, Integer.parseInt(value));
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) {
        int iValue = getInt(tag);
        if (Integer.MIN_VALUE == iValue) {
            return null;
        }
        return String.valueOf(iValue);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public Object getDetailValue() {
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setDetailValue(Object obj) {
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        Pair<Integer, Integer> pair = getSupportedRange(tag);
        if (pair == null) {
            return null;
        }
        List<String> list = new ArrayList<>();
        int c = ((Integer) pair.second).intValue() - ((Integer) pair.first).intValue();
        for (int i = 0; i <= c; i++) {
            list.add(String.valueOf(i));
        }
        if (list.size() == 0) {
            return null;
        }
        return list;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        return getSupportedValue(tag);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        return getSupportedRange(tag) != null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public int getCautionIndex(String itemId) {
        return 0;
    }
}
