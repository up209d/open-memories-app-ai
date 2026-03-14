package com.sony.imaging.app.base.menu.layout;

import android.os.Bundle;
import com.sony.imaging.app.base.menu.MenuState;

/* loaded from: classes.dex */
public interface MenuLayoutListener {
    MenuState getState();

    void onClosed(Bundle bundle);
}
