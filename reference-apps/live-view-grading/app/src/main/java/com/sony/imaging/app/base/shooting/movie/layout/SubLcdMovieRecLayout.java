package com.sony.imaging.app.base.shooting.movie.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.widget.AutoSwitchLayout;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.layout.IStableLayout;
import com.sony.imaging.app.base.shooting.layout.StableLayout;
import com.sony.imaging.app.fw.Layout;

/* loaded from: classes.dex */
public class SubLcdMovieRecLayout extends Layout implements IStableLayout {
    private View mCurrentView = null;
    private AutoSwitchLayout mSegmentModeMovieRecView = null;

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
        this.mSegmentModeMovieRecView.init();
        this.mSegmentModeMovieRecView.start();
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        this.mSegmentModeMovieRecView.stop();
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        destroyView();
        super.onDestroyView();
    }

    private void createView() {
        this.mCurrentView = obtainViewFromPool(getLayout());
        this.mSegmentModeMovieRecView = (AutoSwitchLayout) this.mCurrentView.findViewById(R.id.segment_autoswitch);
    }

    private void destroyView() {
        this.mCurrentView = null;
        this.mSegmentModeMovieRecView = null;
    }

    protected int getLayout() {
        switch (ExecutorCreator.getInstance().getRecordingMode()) {
            case 2:
                int layout = R.layout.shooting_base_sid_sublcd_movie_rec;
                return layout;
            case 8:
                int layout2 = R.layout.shooting_base_sid_sublcd_loop_rec;
                return layout2;
            default:
                return -1;
        }
    }
}
