package com.sony.imaging.app.base.menu.layout;

import android.widget.ImageView;
import java.util.ArrayList;

@Deprecated
/* loaded from: classes.dex */
public interface IMenuAdapter {
    void imageCacheClear();

    void setItemBackgroundImage(int i);

    void setMenuItemList(ArrayList<String> arrayList);

    void setSelectedView(ImageView imageView, int i);
}
