package com.sony.imaging.app.base.menu.layout.list;

/* loaded from: classes.dex */
public class ValueSelectorItem extends DefaultListMenuItem {
    private boolean isSelected;

    public ValueSelectorItem(int titleStrId) {
        this(titleStrId, false, null, null);
    }

    public ValueSelectorItem(int titleStrId, boolean isSelected) {
        this(titleStrId, isSelected, null, null);
    }

    public ValueSelectorItem(int titleStrId, boolean isSelected, Integer guideTitleStringId, Integer guideStringId) {
        super(titleStrId, null, guideTitleStringId, guideStringId);
        this.isSelected = false;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    public boolean isSelected() {
        return this.isSelected;
    }
}
