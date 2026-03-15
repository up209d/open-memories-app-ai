package com.sony.imaging.app.base.common;

import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import com.sony.imaging.app.base.BaseBackUpKey;
import com.sony.imaging.app.base.common.comparator.ParameterComparator;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.MicRefLevelController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.fw.RunStatus;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationManager;
import com.sony.scalar.media.AudioManager;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class AudioSetting {
    private static SparseArray<String> AUDIO_SETTING_CHANGE_NOTIFY_TAGS = null;
    private static final String KEY_FLIP_LEFT_RIGHT = "flip-left-right";
    private static final String KEY_MICROPHONE_ENABLE = "microphone-enable";
    private static final String KEY_MICROPHONE_REFERENCE_LEVEL = "microphone-reference-level";
    private static final String KEY_MICROPHONE_WIND_NOISE_REDUCTION = "microphone-wind-noise-reduction";
    protected static final String KIND_BACKUP = "backup: ";
    protected static final String KIND_CURRENT = "current: ";
    protected static final String KIND_SUPPORTED = "supported :";
    private static final String LOG_MSG_AUDIO_IS_NULL = "mAudioManager is null.";
    private static final String LOG_MSG_RELEASE = "Release AudioManager";
    private static final String LOG_MSG_SETTINGCHANGEDLISTENER_ONCHANGED = "SettingChangedListener.onChanged ";
    protected static final String TAG = "AudioSetting";
    private static ArrayList<String> mMovieModeOnlyList;
    private List<ParameterComparator<AudioManager.Parameters>> AUDIO_PARAMETER_COMPARATORS;
    private AudioManager.Parameters mAudioBackupParameters;
    private AudioManager mAudioManager;
    private AudioManager.Parameters mAudioParameters;
    private AudioManager.Parameters mSupportedAudioParameters;
    public static boolean DEBUG = false;
    protected static StringBuilderThreadLocal sStringBuilder = new StringBuilderThreadLocal();
    private static AudioSetting sInstance = new AudioSetting();
    private static ArrayList<String> mCameraOpeningList = new ArrayList<>();
    private AudioNotificationManager mNotify = AudioNotificationManager.getInstance();
    private volatile boolean isCameraOpened = false;

    static {
        mCameraOpeningList.add(KEY_MICROPHONE_WIND_NOISE_REDUCTION);
        mCameraOpeningList.add(KEY_FLIP_LEFT_RIGHT);
        mCameraOpeningList.add(KEY_MICROPHONE_REFERENCE_LEVEL);
        mMovieModeOnlyList = new ArrayList<>();
        mMovieModeOnlyList.add(KEY_MICROPHONE_ENABLE);
        AUDIO_SETTING_CHANGE_NOTIFY_TAGS = new SparseArray<>();
        if (MicRefLevelController.isSupportedByPF()) {
            AUDIO_SETTING_CHANGE_NOTIFY_TAGS.append(1, "MicRefLevelChanged");
        }
        AUDIO_SETTING_CHANGE_NOTIFY_TAGS.append(0, "AudioWindNoiseReduction");
    }

    protected void dumpParameters(AudioManager.Parameters params, String prefix) {
        try {
            if (prefix != null) {
                StringBuilder builder = sStringBuilder.get();
                builder.replace(0, builder.length(), prefix).append(params.flatten());
                Log.d(TAG, builder.toString());
            } else {
                Log.d(TAG, params.flatten());
            }
        } catch (Exception e) {
        }
    }

    private AudioSetting() {
        createParameterComparatorList();
    }

    public static AudioSetting getInstance() {
        return sInstance;
    }

    public AudioManager getAudioManager() {
        return this.mAudioManager;
    }

    public void initialize() {
        if (Environment.isMovieAPISupported()) {
            this.mAudioManager = new AudioManager();
            if (Environment.isAudioManagerAvailableInAnyMode()) {
                this.mSupportedAudioParameters = this.mAudioManager.getSupportedParameters();
                if (DEBUG) {
                    dumpParameters(this.mSupportedAudioParameters, KIND_SUPPORTED);
                }
                this.mAudioParameters = this.mAudioManager.getParameters();
                if (DEBUG) {
                    dumpParameters(this.mSupportedAudioParameters, KIND_CURRENT);
                }
                initializeAudioParameters();
                initAudioListeners();
            }
        }
    }

    public void terminate() {
        if (Environment.isMovieAPISupported()) {
            if (Environment.isAudioManagerAvailableInAnyMode()) {
                finalizeAudioListeners();
                finalizeAudioParameters();
            }
            this.mSupportedAudioParameters = null;
            this.mAudioParameters = null;
            this.mAudioBackupParameters = null;
            if (this.mAudioManager != null) {
                Log.i(TAG, LOG_MSG_RELEASE);
                this.mAudioManager.release();
                this.mAudioManager = null;
            }
        }
    }

    public AudioManager.Parameters getAudioParameters() {
        return this.mAudioParameters;
    }

    public AudioManager.Parameters getSupportedAudioParameters() {
        return this.mSupportedAudioParameters;
    }

    private AudioManager.Parameters getBackupAudioParameter() {
        if (this.mAudioBackupParameters == null) {
            this.mAudioBackupParameters = new AudioManager.Parameters();
        }
        return this.mAudioBackupParameters;
    }

    public void setAudioParameters(AudioManager.Parameters params) {
        if (this.mAudioManager == null) {
            Log.e(TAG, LOG_MSG_AUDIO_IS_NULL);
            return;
        }
        if (2 != ExecutorCreator.getInstance().getRecordingMode() && !Environment.isAudioManagerAvailableInAnyMode()) {
            Log.e(TAG, "setAudioParameters() is not available");
            return;
        }
        AudioManager.Parameters current = this.mAudioParameters;
        write(params, getBackupAudioParameter());
        AudioManager.Parameters setParam = form(params);
        if (setParam != null) {
            this.mAudioManager.setParameters(setParam);
            this.mAudioParameters = this.mAudioManager.getParameters();
            notifyAudioParameterChanged(current, this.mAudioParameters);
        }
    }

    private AudioManager.Parameters form(AudioManager.Parameters from) {
        boolean isSet = false;
        AudioManager.Parameters to = new AudioManager.Parameters();
        CameraSetting.ParametersTokenizer fromTokenizer = new CameraSetting.ParametersTokenizer(from);
        while (fromTokenizer.hasMoreElements()) {
            Pair<String, String> kv = fromTokenizer.nextToken();
            if (kv != null) {
                if (DEBUG) {
                    Log.d(TAG, "token: " + ((String) kv.first) + ", " + ((String) kv.second));
                }
                if (!this.isCameraOpened) {
                    if (!mCameraOpeningList.contains(kv.first) && !mMovieModeOnlyList.contains(kv.first)) {
                        to.set((String) kv.first, (String) kv.second);
                        isSet = true;
                    }
                } else {
                    if (2 != ExecutorCreator.getInstance().getRecordingMode() && mMovieModeOnlyList.contains(kv.first)) {
                    }
                    to.set((String) kv.first, (String) kv.second);
                    isSet = true;
                }
            }
        }
        if (DEBUG) {
            try {
                Log.d(TAG, "form: " + to.flatten());
            } catch (Exception e) {
            }
        }
        if (!isSet) {
            Log.d(TAG, "form: all setting are ignored");
            return null;
        }
        return to;
    }

    /* loaded from: classes.dex */
    public abstract class AudioSimpleComparator<T> extends ParameterComparator._SimpleComparator<T, AudioManager.Parameters> {
        public AudioSimpleComparator(String tag) {
            super(tag);
        }

        public AudioSimpleComparator(String tag, CameraSetting.ComparatorCommand command) {
            super(tag, command);
        }

        public AudioSimpleComparator(String tag, List<CameraSetting.ComparatorCommand> commands) {
            super(tag, commands);
        }

        @Override // com.sony.imaging.app.base.common.comparator.ParameterComparator
        protected NotificationManager getNotificationManager() {
            return AudioNotificationManager.getInstance();
        }
    }

    private void createParameterComparatorList() {
        if (this.AUDIO_PARAMETER_COMPARATORS == null) {
            this.AUDIO_PARAMETER_COMPARATORS = new ArrayList();
        } else {
            this.AUDIO_PARAMETER_COMPARATORS.clear();
        }
        if (AudioVolumeController.isSupportedByPF()) {
            this.AUDIO_PARAMETER_COMPARATORS.add(new AudioSimpleComparator<Integer>(AudioNotificationManager.SOUND_VOLUME_CHANGED) { // from class: com.sony.imaging.app.base.common.AudioSetting.1
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // com.sony.imaging.app.base.common.comparator.ParameterComparator._SimpleComparator
                public Integer getValue(AudioManager.Parameters obj) {
                    return Integer.valueOf(obj.getSoundVolume());
                }
            });
        }
    }

    private void notifyAudioParameterChanged(AudioManager.Parameters obj1, AudioManager.Parameters obj2) {
        for (ParameterComparator<AudioManager.Parameters> comparator : this.AUDIO_PARAMETER_COMPARATORS) {
            comparator.compare(obj1, obj2);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class BaseAudioSettingChangedListener implements AudioManager.SettingChangedListener {
        protected BaseAudioSettingChangedListener() {
        }

        public void onChanged(int[] types, AudioManager.Parameters param, AudioManager am) {
            AudioSetting.this.mAudioParameters = AudioSetting.this.mAudioManager.getParameters();
            StringBuilder builder = AudioSetting.sStringBuilder.get();
            for (int type : types) {
                builder.replace(0, builder.length(), AudioSetting.LOG_MSG_SETTINGCHANGEDLISTENER_ONCHANGED).append(", ").append(StringBuilderThreadLocal.MSG_STATUS).append(Integer.toString(type));
                Log.i(AudioSetting.TAG, builder.toString());
                String tag = (String) AudioSetting.AUDIO_SETTING_CHANGE_NOTIFY_TAGS.get(type);
                if (tag != null) {
                    builder.replace(0, builder.length(), AudioSetting.LOG_MSG_SETTINGCHANGEDLISTENER_ONCHANGED).append(StringBuilderThreadLocal.MSG_TYPE).append(type).append(", ").append(StringBuilderThreadLocal.MSG_TAG).append(tag);
                    Log.i(AudioSetting.TAG, builder.toString());
                    AudioSetting.this.mNotify.requestNotify(tag);
                }
            }
        }
    }

    protected static int[] getKeysOfSparseArray(SparseArray<String> array) {
        int c = array.size();
        int[] types = new int[c];
        for (int i = 0; i < c; i++) {
            types[i] = array.keyAt(i);
        }
        return types;
    }

    public void initAudioListeners() {
        Log.i(TAG, "initAudioListeners");
        if (this.mAudioManager != null) {
            int[] types = getKeysOfSparseArray(AUDIO_SETTING_CHANGE_NOTIFY_TAGS);
            this.mAudioManager.enableSettingChangedTypes(types);
            this.mAudioManager.setSettingChangedListener(new BaseAudioSettingChangedListener());
        }
    }

    private void finalizeAudioListeners() {
        Log.i(TAG, "finalizeAudioListeners");
        if (this.mAudioManager != null) {
            this.mAudioManager.setSettingChangedListener((AudioManager.SettingChangedListener) null);
        }
    }

    protected void initializeAudioParameters() {
        AudioManager.Parameters params = new AudioManager.Parameters();
        boolean needUpdateParameter = false;
        String flatten = BackUpUtil.getInstance().getPreferenceString(BaseBackUpKey.ID_AUDIO_PARAMETERS, null);
        if (flatten != null && flatten.length() != 0) {
            if (DEBUG) {
                Log.d(TAG, "flattened: " + flatten);
            }
            AudioManager.Parameters bkup = getBackupAudioParameter();
            needUpdateParameter = true;
            bkup.unflatten(flatten);
            copy(bkup, params);
        }
        if (needUpdateParameter) {
            setAudioParameters(params);
        }
    }

    protected void finalizeAudioParameters() {
        try {
            BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_AUDIO_PARAMETERS, getBackupAudioParameter().flatten());
        } catch (Exception e) {
        }
    }

    private void copy(AudioManager.Parameters from, AudioManager.Parameters to) {
        CameraSetting.ParametersTokenizer tokenizer = new CameraSetting.ParametersTokenizer(from);
        while (tokenizer.hasMoreElements()) {
            Pair<String, String> kv = tokenizer.nextToken();
            if (kv != null) {
                to.set((String) kv.first, (String) kv.second);
            }
        }
    }

    private void write(AudioManager.Parameters from, AudioManager.Parameters to) {
        copy(from, to);
    }

    protected void updateParameters() {
        AudioManager.Parameters oldAudioParams = this.mAudioParameters;
        this.mSupportedAudioParameters = this.mAudioManager.getSupportedParameters();
        if (DEBUG) {
            dumpParameters(this.mSupportedAudioParameters, KIND_SUPPORTED);
        }
        this.mAudioParameters = this.mAudioManager.getParameters();
        if (DEBUG) {
            dumpParameters(this.mSupportedAudioParameters, KIND_CURRENT);
        }
        notifyAudioParameterChanged(oldAudioParams, this.mAudioParameters);
    }

    public void onCameraOpened() {
        this.isCameraOpened = true;
        if (Environment.isAudioManagerAvailableInAnyMode()) {
            finalizeAudioParameters();
            this.mSupportedAudioParameters = this.mAudioManager.getSupportedParameters();
            this.mAudioParameters = this.mAudioManager.getParameters();
            initializeAudioParameters();
            return;
        }
        if (2 == ExecutorCreator.getInstance().getRecordingMode()) {
            this.mSupportedAudioParameters = this.mAudioManager.getSupportedParameters();
            this.mAudioParameters = this.mAudioManager.getParameters();
            initializeAudioParameters();
            initAudioListeners();
            return;
        }
        this.mSupportedAudioParameters = this.mAudioManager.getSupportedParameters();
        this.mAudioParameters = null;
    }

    public void onCameraRemoving() {
        if (Environment.isAudioManagerAvailableInAnyMode()) {
            finalizeAudioParameters();
            return;
        }
        finalizeAudioListeners();
        if (2 == ExecutorCreator.getInstance().getRecordingMode()) {
            finalizeAudioParameters();
        }
        this.mSupportedAudioParameters = null;
        this.mAudioParameters = null;
    }

    public void onCameraRemoved() {
        this.isCameraOpened = false;
        if (Environment.isAudioManagerAvailableInAnyMode() && 4 == RunStatus.getStatus()) {
            this.mSupportedAudioParameters = this.mAudioManager.getSupportedParameters();
            this.mAudioParameters = this.mAudioManager.getParameters();
            initializeAudioParameters();
        }
    }

    public void onCameraReopening() {
        if (!Environment.isAudioManagerAvailableInAnyMode()) {
            int from = CameraSetting.getInstance().getCurrentMode();
            if (from == 2) {
                finalizeAudioListeners();
                finalizeAudioParameters();
                return;
            }
            return;
        }
        finalizeAudioParameters();
    }

    public void onCameraReopened() {
        if (!Environment.isAudioManagerAvailableInAnyMode()) {
            AudioManager.Parameters oldAudioParams = this.mAudioParameters;
            int mode = CameraSetting.getInstance().getCurrentMode();
            if (mode == 2) {
                this.mSupportedAudioParameters = this.mAudioManager.getSupportedParameters();
                this.mAudioParameters = this.mAudioManager.getParameters();
                initializeAudioParameters();
            } else {
                this.mSupportedAudioParameters = this.mAudioManager.getSupportedParameters();
                this.mAudioParameters = null;
            }
            notifyAudioParameterChanged(oldAudioParams, this.mAudioParameters);
            if (mode == 2) {
                initAudioListeners();
                return;
            }
            return;
        }
        this.mSupportedAudioParameters = this.mAudioManager.getSupportedParameters();
        this.mAudioParameters = this.mAudioManager.getParameters();
        initializeAudioParameters();
    }
}
