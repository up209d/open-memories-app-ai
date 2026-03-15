package com.sony.imaging.app.base.menu.layout.list;

import java.util.List;

/* loaded from: classes.dex */
public class DefaultListMenuItem implements FakeCheckable {
    private Integer guideForItemStringId;
    private Integer guideTitleForItemStringId;
    private boolean isChecked;
    private boolean isEnabled;
    private boolean isGuideEnabled;
    private List<ValueSelectorContent> items;
    private int selectedItemIndex;
    private int titleStringId;
    private ItemType type;

    public DefaultListMenuItem(int titleStrId) {
        this(titleStrId, null, null, null);
    }

    public DefaultListMenuItem(int titleStrId, Integer guideTitleStringId, Integer guideStringId) {
        this(titleStrId, null, guideTitleStringId, guideStringId);
    }

    public DefaultListMenuItem(int titleStrId, List<ValueSelectorContent> items) {
        this(titleStrId, items, null, null);
    }

    public DefaultListMenuItem(int titleStrId, List<ValueSelectorContent> items, Integer guideTitleStringId, Integer guideStringId) {
        this.isEnabled = true;
        this.titleStringId = titleStrId;
        this.isChecked = false;
        if (items == null) {
            this.type = ItemType.OPENER;
            this.items = null;
        } else {
            this.type = ItemType.SELECTOR;
            this.items = items;
            this.selectedItemIndex = 0;
        }
        if (guideTitleStringId == null || guideStringId == null) {
            this.isGuideEnabled = false;
            this.guideTitleForItemStringId = null;
            this.guideForItemStringId = null;
        } else {
            this.isGuideEnabled = true;
            this.guideTitleForItemStringId = guideTitleStringId;
            this.guideForItemStringId = guideStringId;
        }
    }

    public List<ValueSelectorContent> getValueSelectorContents() {
        return this.items;
    }

    public final boolean removeValueSelectorItem(int position) {
        if (this.items.size() - 1 < position) {
            return false;
        }
        this.items.remove(position);
        return true;
    }

    public final void addValueSelectorItem(ValueSelectorContent item) {
        this.items.add(item);
    }

    public final boolean addValueSelectorItem(ValueSelectorContent item, int position) {
        if (this.items.size() < position) {
            return false;
        }
        this.items.add(position, item);
        return true;
    }

    public boolean setSelectedItemIndex(int index) {
        switch (this.type) {
            case SELECTOR:
            case NO_PREVIEW_SELECTOR:
                this.selectedItemIndex = index;
                return true;
            default:
                return false;
        }
    }

    public int getSelectedItemIndex() {
        switch (this.type) {
            case SELECTOR:
            case NO_PREVIEW_SELECTOR:
                return this.selectedItemIndex;
            default:
                return -1;
        }
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public boolean setNoPreview(boolean hidePreview) {
        if (ItemType.SELECTOR.equals(this.type) || ItemType.NO_PREVIEW_SELECTOR.equals(this.type)) {
            if (hidePreview) {
                this.type = ItemType.NO_PREVIEW_SELECTOR;
            } else {
                this.type = ItemType.SELECTOR;
            }
            return true;
        }
        return false;
    }

    public int getTitleStrId() {
        return this.titleStringId;
    }

    public boolean setGuideEnabled(boolean bool) {
        if (bool) {
            if (this.guideTitleForItemStringId == null || this.guideForItemStringId == null) {
                return false;
            }
            this.isGuideEnabled = true;
            return true;
        }
        this.isGuideEnabled = false;
        return true;
    }

    public boolean isGuideEnabled() {
        return this.isGuideEnabled;
    }

    public int getGuideTitleStrId() {
        return this.guideTitleForItemStringId.intValue();
    }

    public int getGuideStrId() {
        return this.guideForItemStringId.intValue();
    }

    public ItemType getItemType() {
        return this.type;
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.FakeCheckable
    public void setFakeChecked(boolean checked) {
        this.isChecked = checked;
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.FakeCheckable
    public boolean isFakeChecked() {
        return this.isChecked;
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.FakeCheckable
    public void fakeToggle() {
        setFakeChecked(!isFakeChecked());
    }
}
