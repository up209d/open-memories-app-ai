package com.sony.imaging.app.base.playback.layout;

import android.os.Bundle;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.AudioVolumeController;
import java.util.List;

/* loaded from: classes.dex */
public class VolumeAdjustmentLayout extends PlayLayoutBase {
    protected int mMax;
    protected int mOriginalValue = AudioVolumeController.INVALID_VALUE;
    protected AudioVolumeController mController = AudioVolumeController.getInstance();

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mController = AudioVolumeController.getInstance();
        List<String> supported = this.mController.getSupportedValue(AudioVolumeController.TAG_AUDIO_VOLUME);
        this.mMax = Integer.parseInt(supported.get(supported.size() - 1));
        this.mOriginalValue = this.mController.getInt(AudioVolumeController.TAG_AUDIO_VOLUME);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        this.mController = null;
        this.mOriginalValue = AudioVolumeController.INVALID_VALUE;
        super.onDestroy();
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getLayoutResource() {
        return R.layout.pb_layout_cmn_volume_adjustment;
    }

    protected void turnUp() {
        int value = this.mController.getInt(AudioVolumeController.TAG_AUDIO_VOLUME) + 1;
        if (value <= this.mMax) {
            this.mController.setInt(AudioVolumeController.TAG_AUDIO_VOLUME, value);
        }
    }

    protected void turnDown() {
        int value = this.mController.getInt(AudioVolumeController.TAG_AUDIO_VOLUME) - 1;
        if (value >= 0) {
            this.mController.setInt(AudioVolumeController.TAG_AUDIO_VOLUME, value);
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver
    protected String getKeyConvCategory() {
        return "Menu";
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        return transitionSinglePb() ? 1 : -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteFuncKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDispKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        closeLayout();
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        if (Integer.MIN_VALUE != this.mOriginalValue) {
            this.mController.setInt(AudioVolumeController.TAG_AUDIO_VOLUME, this.mOriginalValue);
        }
        closeLayout();
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        turnUp();
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        turnDown();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        turnUp();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        turnDown();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialNext() {
        turnUp();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialPrev() {
        turnDown();
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        turnDown();
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        turnUp();
        return 1;
    }
}
