package com.sony.imaging.app.base.menu.layout;

import android.view.View;
import android.widget.AdapterView;

@Deprecated
/* loaded from: classes.dex */
public interface IMenuView {
    int getFirstPosition();

    int getSelectedPosition();

    View getSelectedView();

    boolean moveNext();

    boolean movePrevious();

    void setFocusable(boolean z);

    void setFocusableInTouchMode(boolean z);

    void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener);

    void setOnItemSelectedListener(AdapterView.OnItemSelectedListener onItemSelectedListener);

    void setSelection(int i);

    void setVisibility(int i);
}
