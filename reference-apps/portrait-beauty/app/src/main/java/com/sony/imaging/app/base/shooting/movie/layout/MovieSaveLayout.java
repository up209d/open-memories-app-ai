package com.sony.imaging.app.base.shooting.movie.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.IModableLayout;
import com.sony.imaging.app.base.common.OnLayoutModeChangeListener;
import com.sony.imaging.app.fw.Layout;

/* loaded from: classes.dex */
public class MovieSaveLayout extends Layout implements IModableLayout {
    protected ViewGroup mCurrentLayout = null;
    protected View mMainView = null;
    private OnLayoutModeChangeListener mListener = new OnLayoutModeChangeListener(this, 0);

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        createView();
        return this.mCurrentLayout;
    }

    protected void createView() {
        detachView();
        this.mCurrentLayout = new RelativeLayout(getActivity());
        this.mMainView = obtainViewFromPool(R.layout.movie_save_sample);
        this.mCurrentLayout.addView(this.mMainView);
        TextView savingTextView = (TextView) this.mCurrentLayout.findViewById(R.id.caution_text);
        savingTextView.setText(android.R.string.delete);
    }

    protected void detachView() {
        if (this.mMainView != null) {
            if (this.mCurrentLayout != null) {
                this.mCurrentLayout.removeView(this.mMainView);
            }
            this.mMainView = null;
        }
        this.mCurrentLayout = null;
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        detachView();
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        DisplayModeObserver.getInstance().setNotificationListener(this.mListener);
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        DisplayModeObserver.getInstance().removeNotificationListener(this.mListener);
    }

    @Override // com.sony.imaging.app.base.common.IModableLayout
    public void onLayoutModeChanged(int device, int displayMode) {
        updateView();
    }
}
