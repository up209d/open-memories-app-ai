package com.sony.imaging.app.base.menu;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes.dex */
public class MenuDataParcelable implements Parcelable {
    public static final Parcelable.Creator<MenuDataParcelable> CREATOR = new Parcelable.Creator<MenuDataParcelable>() { // from class: com.sony.imaging.app.base.menu.MenuDataParcelable.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public MenuDataParcelable createFromParcel(Parcel in) {
            return new MenuDataParcelable(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public MenuDataParcelable[] newArray(int size) {
            return new MenuDataParcelable[size];
        }
    };
    public static final String KEY = "MenuData";

    @Deprecated
    private MenuAccessor mAccessor;
    private String mExecType;
    private String mItemId;

    @Deprecated
    private String mMenuDataFile;
    private String mNextMenuId;
    private String mPreviousMenuLayoutId;
    private BaseMenuService mService;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int arg1) {
        out.writeString(this.mItemId);
        out.writeString(this.mPreviousMenuLayoutId);
        out.writeString(this.mExecType);
        out.writeString(this.mNextMenuId);
        out.writeValue(this.mService);
    }

    private MenuDataParcelable(Parcel in) {
        this.mItemId = in.readString();
        this.mPreviousMenuLayoutId = in.readString();
        this.mExecType = in.readString();
        this.mNextMenuId = in.readString();
        this.mService = (BaseMenuService) in.readValue(null);
    }

    public MenuDataParcelable() {
        this.mItemId = null;
        this.mPreviousMenuLayoutId = null;
        this.mExecType = null;
        this.mNextMenuId = null;
        this.mService = null;
    }

    @Deprecated
    public void setMenuData(String menuDataFile, String itemId, String previousMenuLayoutId, String execType, String nextMenuId, MenuAccessor accessor) {
        this.mItemId = itemId;
        this.mPreviousMenuLayoutId = previousMenuLayoutId;
        this.mExecType = execType;
        this.mNextMenuId = nextMenuId;
        if (accessor != null) {
            this.mAccessor = new MenuAccessor(accessor);
        } else {
            this.mAccessor = null;
        }
    }

    @Deprecated
    public void setMenuData(String menuDataFile, String itemId, String previousMenuLayoutId, String execType, String nextMenuId, BaseMenuService service) {
        setMenuData(itemId, previousMenuLayoutId, execType, nextMenuId, service);
    }

    public void setMenuData(String itemId, String previousMenuLayoutId, String execType, String nextMenuId, BaseMenuService service) {
        this.mItemId = itemId;
        this.mPreviousMenuLayoutId = previousMenuLayoutId;
        this.mExecType = execType;
        this.mNextMenuId = nextMenuId;
        if (service != null) {
            this.mService = new BaseMenuService(service);
        }
    }

    @Deprecated
    public void setMenuDataFile(String menuDataFile) {
        this.mMenuDataFile = menuDataFile;
    }

    @Deprecated
    public String getMenuDataFile() {
        return this.mMenuDataFile;
    }

    public void setItemId(String itemId) {
        this.mItemId = itemId;
    }

    public String getItemId() {
        return this.mItemId;
    }

    public void setPreviousMenuLayoutId(String previousMenuLayoutId) {
        this.mPreviousMenuLayoutId = previousMenuLayoutId;
    }

    public String getPreviousMenuLayoutId() {
        return this.mPreviousMenuLayoutId;
    }

    public void setExecType(String execType) {
        this.mExecType = execType;
    }

    public String getExecType() {
        return this.mExecType;
    }

    public void setNextMenuId(String nextMenuId) {
        this.mNextMenuId = nextMenuId;
    }

    public String getNextMenuId() {
        return this.mNextMenuId;
    }

    @Deprecated
    public void setMenuAccessor(MenuAccessor accessor) {
        this.mAccessor = accessor;
    }

    @Deprecated
    public MenuAccessor getMenuAccessor() {
        return this.mAccessor;
    }

    public BaseMenuService getMenuService() {
        return this.mService;
    }
}
