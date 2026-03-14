package com.sony.imaging.app.photoretouch.playback.layout.contrast;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.photoretouch.Constant;
import com.sony.imaging.app.photoretouch.R;
import com.sony.imaging.app.photoretouch.common.BCSImageAdapter;
import com.sony.imaging.app.photoretouch.common.EVFOffLayout;
import com.sony.imaging.app.photoretouch.common.ImageEditor;
import com.sony.imaging.app.photoretouch.menu.layout.PhotoRetouchSubMenuLayout;
import com.sony.imaging.app.photoretouch.playback.control.LCEControl;
import com.sony.imaging.app.photoretouch.playback.layout.brightness.ControlSavingTask;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.widget.OptimizedImageView;

/* loaded from: classes.dex */
public class ContrastControlLayout extends EVFOffLayout implements SeekBar.OnSeekBarChangeListener {
    private BCSImageAdapter mAdapter;
    private LCEControl mLceControl;
    private SeekBar mSliderBar;
    private final String TAG = ContrastControlLayout.class.getSimpleName();
    private OptimizedImageView mOptimizedImageView = null;
    private int[] mSliderProgressValues = {0, 80, 160, 240, 320, 400, 480, 560};
    private int mCurRotate = 3;
    private PhotoRetouchSubMenuLayout mPhotoRetouchSubMenuLayout = null;
    int[] arr = {7, 98, 189, 280, 372, 463, 554};
    OptimizedImage mEditImage = null;

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View currentView = obtainViewFromPool(R.layout.contrast_control);
        this.mOptimizedImageView = currentView.findViewById(R.id.optimized_image);
        this.mSliderBar = (SeekBar) currentView.findViewById(R.id.slider_bar);
        this.mSliderBar.setFocusable(false);
        ImageView backGround = (ImageView) currentView.findViewById(R.id.contrast_background);
        backGround.setOnTouchListener(PhotoRetouchSubMenuLayout.blockTouchEvent);
        Drawable cursor = getResources().getDrawable(R.drawable.p_16_dd_parts_pr_control_marker);
        this.mSliderBar.setThumbOffset(cursor.getIntrinsicWidth() / 2);
        this.mPhotoRetouchSubMenuLayout = (PhotoRetouchSubMenuLayout) getLayout(Constant.ID_PHOTORETOUCHSUBMENULAYOUT);
        this.mLceControl = ImageEditor.useLceControl();
        this.mSliderBar.setOnSeekBarChangeListener(this);
        this.mSliderBar.setOnTouchListener(new View.OnTouchListener() { // from class: com.sony.imaging.app.photoretouch.playback.layout.contrast.ContrastControlLayout.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        return currentView;
    }

    private void removeAllAllocatedResources() {
        ImageEditor.releaseOptImage(this.mLceControl.getScaledImage());
    }

    public void shuffleGalleryItems(int progress) {
        if (progress >= this.mSliderProgressValues[0] && progress < this.mSliderProgressValues[1]) {
            this.mAdapter.setSelection(0);
            this.mSliderBar.setProgress(7);
            return;
        }
        if (progress >= this.mSliderProgressValues[1] && progress < this.mSliderProgressValues[2]) {
            this.mAdapter.setSelection(1);
            this.mSliderBar.setProgress(98);
            return;
        }
        if (progress >= this.mSliderProgressValues[2] && progress < this.mSliderProgressValues[3]) {
            this.mAdapter.setSelection(2);
            this.mSliderBar.setProgress(189);
            return;
        }
        if (progress >= this.mSliderProgressValues[3] && progress < this.mSliderProgressValues[4]) {
            this.mAdapter.setSelection(3);
            this.mSliderBar.setProgress(280);
            return;
        }
        if (progress >= this.mSliderProgressValues[4] && progress < this.mSliderProgressValues[5]) {
            this.mAdapter.setSelection(4);
            this.mSliderBar.setProgress(372);
        } else if (progress >= this.mSliderProgressValues[5] && progress < this.mSliderProgressValues[6]) {
            this.mAdapter.setSelection(5);
            this.mSliderBar.setProgress(463);
        } else if (progress >= this.mSliderProgressValues[6] && progress <= this.mSliderProgressValues[7]) {
            this.mAdapter.setSelection(6);
            this.mSliderBar.setProgress(554);
        }
    }

    public void changeSliderProgress(int position) {
        this.mSliderBar.setProgress(this.arr[position]);
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onProgressChanged(SeekBar bar, int progress, boolean arg2) {
        if (this.mAdapter.isStable()) {
            shuffleGalleryItems(progress);
        }
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStartTrackingTouch(SeekBar arg0) {
        BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELECT);
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStopTrackingTouch(SeekBar arg0) {
    }

    @Override // com.sony.imaging.app.photoretouch.common.EVFOffLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        Log.d(this.TAG, "==== onResume:optImageCount = " + ImageEditor.optImageCount);
        Log.e("INSIDE", "ON RESUME");
        this.mLceControl.setAspectRatio(ImageEditor.getAspectRatio());
        this.mLceControl.setOrientation(ImageEditor.getOrientationInfo());
        this.mLceControl.setScaledImage(ImageEditor.getImage());
        this.mLceControl.setBrightness(6);
        this.mLceControl.setSaturation(3);
        this.mAdapter = new BCSImageAdapter(this.mLceControl, 7, this.mOptimizedImageView, new BCSImageAdapter.ChangeParameter() { // from class: com.sony.imaging.app.photoretouch.playback.layout.contrast.ContrastControlLayout.2
            @Override // com.sony.imaging.app.photoretouch.common.BCSImageAdapter.ChangeParameter
            public void setParameter(int position, LCEControl lce) {
                lce.setContrast(position);
            }
        });
        this.mAdapter.setSelection(3);
        changeSliderProgress(3);
        setKeyBeepPattern(0);
    }

    private void editImage() {
        ImageEditor.releaseOptImage(this.mLceControl.getScaledImage());
        if (this.mAdapter.getSelection() != 3) {
            Log.d(this.TAG, "==== editImage:optImageCount = " + ImageEditor.optImageCount);
            this.mEditImage = ImageEditor.makeLceImage(this, this.mAdapter.getSelection());
        }
    }

    private void saveImage() {
        if (this.mAdapter.getSelection() != 3) {
            ControlSavingTask task = new ControlSavingTask(this, this.mEditImage);
            ImageEditor.executeResultReflection(task, this.mEditImage);
        }
    }

    @Override // com.sony.imaging.app.photoretouch.common.EVFOffLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        Log.d(this.TAG, "==== onPause");
        this.mAdapter.terminate();
        ImageEditor.releaseLceControl();
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        removeAllAllocatedResources();
        deInitialize();
        Log.d(this.TAG, "==== onDestroyView:optImageCount = " + ImageEditor.optImageCount);
        super.onDestroyView();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        if (isFinder() || !this.mAdapter.isStable()) {
            return -1;
        }
        int keyCode2 = event.getScanCode();
        switch (keyCode2) {
            case AppRoot.USER_KEYCODE.UP /* 103 */:
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
            case AppRoot.USER_KEYCODE.AEL /* 532 */:
            case AppRoot.USER_KEYCODE.AF_MF /* 533 */:
            case AppRoot.USER_KEYCODE.CUSTOM /* 588 */:
            case AppRoot.USER_KEYCODE.AF_RANGE /* 614 */:
            case AppRoot.USER_KEYCODE.CUSTOM1 /* 622 */:
            case AppRoot.USER_KEYCODE.CUSTOM2 /* 623 */:
            case AppRoot.USER_KEYCODE.AF_MF_AEL /* 638 */:
                return -1;
            case AppRoot.USER_KEYCODE.LEFT /* 105 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
            case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
                if (this.mAdapter.getSelection() == 0) {
                    return -1;
                }
                changeSliderProgress(this.mAdapter.getSelection() - 1);
                return 1;
            case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
            case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                if (this.mAdapter.getSelection() == this.mAdapter.getCount() - 1) {
                    return -1;
                }
                changeSliderProgress(this.mAdapter.getSelection() + 1);
                return 1;
            case AppRoot.USER_KEYCODE.SK1 /* 229 */:
            case AppRoot.USER_KEYCODE.MENU /* 514 */:
                openLayout(Constant.ID_PHOTORETOUCHSUBMENULAYOUT, this.data);
                closeLayout();
                return 1;
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                if (PhotoRetouchSubMenuLayout.isRepeat(event) || (ImageEditor.runnableCount > 0 && this.mAdapter.getSelection() != 3)) {
                    return -1;
                }
                this.mAdapter.terminate();
                editImage();
                openLayout(Constant.ID_PHOTORETOUCHSUBMENULAYOUT, this.data);
                closeLayout();
                saveImage();
                return 1;
            case AppRoot.USER_KEYCODE.SK2 /* 513 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                return -1;
            default:
                return 0;
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        int keyCode2 = event.getScanCode();
        switch (keyCode2) {
            case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                Log.d(this.TAG, "S1 ON UP");
                return 0;
            case AppRoot.USER_KEYCODE.S2_ON /* 518 */:
                Log.d(this.TAG, "S2 ON UP");
                return 0;
            case AppRoot.USER_KEYCODE.AEL /* 532 */:
            case AppRoot.USER_KEYCODE.AF_MF /* 533 */:
            case AppRoot.USER_KEYCODE.CUSTOM /* 588 */:
            case AppRoot.USER_KEYCODE.AF_RANGE /* 614 */:
            case AppRoot.USER_KEYCODE.CUSTOM1 /* 622 */:
            case AppRoot.USER_KEYCODE.CUSTOM2 /* 623 */:
            case AppRoot.USER_KEYCODE.AF_MF_AEL /* 638 */:
                Log.d(this.TAG, "UP");
                return -1;
            case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                Log.d(this.TAG, "MODE DIAL UP");
                return -1;
            case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                Log.d(this.TAG, "EV_DIAL_CHANGED UP");
                return 0;
            case AppRoot.USER_KEYCODE.S1_OFF /* 772 */:
                Log.d(this.TAG, "S1 OFF UP");
                return 0;
            case AppRoot.USER_KEYCODE.S2_OFF /* 774 */:
                Log.d(this.TAG, "S2 OFF UP");
                return 0;
            default:
                return 0;
        }
    }

    private void deInitialize() {
        this.mAdapter = null;
        this.mSliderBar = null;
        this.mOptimizedImageView = null;
    }
}
