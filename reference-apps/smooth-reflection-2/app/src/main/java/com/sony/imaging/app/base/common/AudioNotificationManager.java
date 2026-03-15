package com.sony.imaging.app.base.common;

import com.sony.imaging.app.util.NotificationManager;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: classes.dex */
public class AudioNotificationManager extends NotificationManager {
    public static final String AUDIO_WIND_NR = "AudioWindNoiseReduction";
    public static final String BEEP_MODE_CHANGED = "BeepModeChanged";
    public static final String MIC_REF_LEVEL_CHANGED = "MicRefLevelChanged";
    public static final String SOUND_VOLUME_CHANGED = "SoundVolumeChanged";
    private static AudioNotificationManager instance = new AudioNotificationManager(false);
    private Map<String, Object> mValues;

    public void clear() {
        this.mValues.clear();
    }

    private AudioNotificationManager(boolean b) {
        super(b);
        this.mValues = new ConcurrentHashMap();
    }

    public static AudioNotificationManager getInstance() {
        return instance;
    }

    @Override // com.sony.imaging.app.util.NotificationManager
    public Object getValue(String tag) {
        return this.mValues.get(tag);
    }

    public void requestNotify(String tag, Object value) {
        boolean isValueChanged = true;
        Object current = this.mValues.put(tag, value);
        if (this.mValues.containsKey(tag)) {
            if (current != null) {
                isValueChanged = !current.equals(value);
            } else {
                isValueChanged = value != null;
            }
        }
        if (isValueChanged) {
            notify(tag);
        }
    }

    public void requestNotify(String tag) {
        notify(tag);
    }

    public void requestSyncNotify(String tag, Object value) {
        boolean isValueChanged = true;
        Object current = this.mValues.put(tag, value);
        if (this.mValues.containsKey(tag)) {
            if (current != null) {
                isValueChanged = !current.equals(value);
            } else {
                isValueChanged = value != null;
            }
        }
        if (isValueChanged) {
            notifySync(tag);
        }
    }
}
