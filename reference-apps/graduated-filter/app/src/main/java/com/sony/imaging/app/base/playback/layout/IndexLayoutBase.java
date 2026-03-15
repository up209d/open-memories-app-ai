package com.sony.imaging.app.base.playback.layout;

import android.view.View;
import com.sony.imaging.app.fw.ICustomKey;

/* loaded from: classes.dex */
public abstract class IndexLayoutBase extends PlayLayoutBase {
    public static final int ITEM_KIND_GRID_CELL = 1;
    public static final int ITEM_KIND_GROUP_BAR = 2;
    public static final int ITEM_KIND_MAX = 3;
    public static final int ITEM_KIND_UNKNOWN = 0;
    protected GridListener mGridListener;

    /* loaded from: classes.dex */
    public interface GridListener {
        void onItemSelected(int i);
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver
    protected String getKeyConvCategory() {
        return ICustomKey.CATEGORY_INDEX;
    }

    public void setGridListener(GridListener listener) {
        this.mGridListener = listener;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        highlightedItemChanged(1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void highlightedItemChanged(int itemKind) {
        int[] items;
        int[][] kinds = {getNothingSelectedLayoutResourceId(), getGridCellLayoutResourceId(), getGroupBarLayoutResourceId()};
        View containerView = getView();
        for (int i = 0; i < 3; i++) {
            if (itemKind != i && (items = kinds[i]) != null) {
                for (int item : items) {
                    View view = containerView.findViewById(item);
                    if (view != null) {
                        view.setVisibility(8);
                    }
                }
            }
        }
        int[] items2 = kinds[itemKind];
        if (items2 != null) {
            for (int item2 : items2) {
                View view2 = containerView.findViewById(item2);
                if (view2 != null) {
                    view2.setVisibility(0);
                }
            }
        }
    }

    public int getHighlightedItemKind() {
        return 0;
    }

    protected int[] getGridCellLayoutResourceId() {
        return null;
    }

    protected int[] getGroupBarLayoutResourceId() {
        return null;
    }

    protected int[] getNothingSelectedLayoutResourceId() {
        return null;
    }
}
