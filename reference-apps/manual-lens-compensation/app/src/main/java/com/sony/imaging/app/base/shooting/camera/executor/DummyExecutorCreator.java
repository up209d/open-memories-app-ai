package com.sony.imaging.app.base.shooting.camera.executor;

import android.hardware.Camera;
import com.sony.imaging.app.base.shooting.camera.executor.BaseShootingExecutor;
import com.sony.imaging.app.fw.State;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.media.AudioManager;
import com.sony.scalar.media.MediaRecorder;

/* loaded from: classes.dex */
public class DummyExecutorCreator extends ExecutorCreator {
    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected void initializeData() {
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected IProcess getProcess(Camera camera, CameraEx cameraEx) {
        return null;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isSpinal() {
        return false;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isSpinalZoomSetting() {
        return false;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isInheritDigitalZoomSetting() {
        return false;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isImmediatelyEEStart() {
        return false;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isInheritSetting() {
        return false;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean canEnableExecutorWithBooting() {
        return false;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected IAdapter getAdapter(String name) {
        return null;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public AudioManager getAudioManager() {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public BaseShootingExecutor getExecutor(Class<?> clazz) {
        return super.getExecutor(clazz);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public MediaRecorder getMediaRecorder() {
        return null;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected Class<?> getNextExecutor() {
        return StableExecutor.class;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public int getRecordingMode() {
        return super.getRecordingMode();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public int getRecordingModeFromBackup() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public BaseShootingExecutor getSequence() {
        return this.mExecutor;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public int getSupportingRecMode() {
        return 0;
    }

    public void setSupporingRecMode(int mode) {
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public void init() {
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public boolean isAssistApp() {
        return false;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public boolean isEnableDigitalZoom() {
        return false;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected boolean isSpecial() {
        return false;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public boolean isSpinalZoom() {
        return false;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected void reopen(CameraEx.OpenCallback openCallback) {
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public void setExclusiveSetting(BaseExclusiveSetting excClass) {
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public void setMessageReceiver(State state) {
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public void setRecordingMode(int mode, CameraEx.OpenCallback callback) {
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public void setRecordingModeToBackup(int mode) {
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    protected BaseShootingExecutor setup(BaseShootingExecutor executor, IAdapter adapter, IProcess process) {
        this.mExecutor = executor;
        return executor;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public void stableSequence() {
        super.stableSequence();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public void stableSequence(int flg) {
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public void stableSequence(BaseShootingExecutor.ReleasedListener listener) {
        super.stableSequence(listener);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public void stableSequence(BaseShootingExecutor.ReleasedListener listener, int flg) {
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public void updateSequence() {
        super.updateSequence();
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public void updateSequence(int flg) {
    }

    @Override // com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator
    public void waitChangingRecMode() {
    }

    @Override // java.lang.Thread
    public synchronized void start() {
    }
}
