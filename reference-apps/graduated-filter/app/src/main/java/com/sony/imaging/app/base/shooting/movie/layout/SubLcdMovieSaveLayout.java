package com.sony.imaging.app.base.shooting.movie.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.SubLcdManager;
import com.sony.imaging.app.base.shooting.layout.IStableLayout;
import com.sony.imaging.app.base.shooting.layout.StableLayout;
import com.sony.imaging.app.fw.Layout;

/* loaded from: classes.dex */
public class SubLcdMovieSaveLayout extends Layout implements IStableLayout {
    private View mCurrentView = null;
    private SubLcdManager.BlinkHandle mSavingBlink = null;

    @Override // com.sony.imaging.app.base.shooting.layout.IStableLayout
    public void setUserChanging(int whatUserChanging) {
    }

    @Override // com.sony.imaging.app.base.shooting.layout.IStableLayout
    public void setEETappedListener(StableLayout.IEETouchedListener listener) {
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        createView();
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.mSavingBlink = SubLcdManager.getInstance().blinkAll("PTN_SLOW");
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (this.mSavingBlink != null) {
            SubLcdManager.getInstance().stopBlink(this.mSavingBlink);
            this.mSavingBlink = null;
        }
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        destroyView();
        super.onDestroyView();
    }

    private void createView() {
        this.mCurrentView = obtainViewFromPool(R.layout.shooting_base_sid_sublcd_movie_save);
    }

    private void destroyView() {
        this.mCurrentView = null;
    }
}
