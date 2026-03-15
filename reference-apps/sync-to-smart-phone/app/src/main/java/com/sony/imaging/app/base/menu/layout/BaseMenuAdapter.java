package com.sony.imaging.app.base.menu.layout;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.menu.BaseMenuService;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;

/* loaded from: classes.dex */
public class BaseMenuAdapter extends BaseAdapter {
    private static final int INVISIBLE_ALPHA = 128;
    private static final String LOG_MSG_NOT_FOUND_RES = "Not found resouce,  item :";
    private static final String MSG_DRAWABLE_UNAVAILABLE = "drawable is not available";
    private static final String RESTAG_BACK = "back";
    private static final String RESTAG_ICON = "icon";
    private static final String RESTAG_TEXT = "text";
    private static final String TAG = "BaseMenuAdapter";
    private static final int VISIBLE_ALPHA = 255;
    private Context mContext;
    private LayoutInflater mInflater;
    private Resources mRes;
    private int mResId;
    protected BaseMenuService mService;
    protected ArrayList<String> mItems = new ArrayList<>();
    private int mItemBackGroundId = 0;
    private StringBuilder builder = new StringBuilder();
    protected ImageCache mIconCache = new ImageCache();
    protected ImageCache mSelectedIconCache = new ImageCache();
    protected ImageCache mFirstLayerIconCache = new ImageCache();

    public BaseMenuAdapter(Context context, int ResId, BaseMenuService service) {
        this.mInflater = null;
        this.mResId = 0;
        this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        this.mResId = ResId;
        this.mRes = context.getResources();
        this.mContext = context;
        this.mService = service;
    }

    public BaseMenuAdapter(Context context, int ResId) {
        this.mInflater = null;
        this.mResId = 0;
        this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        this.mResId = ResId;
        this.mRes = context.getResources();
        this.mContext = context;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Resources getResources() {
        return this.mRes;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Context getContext() {
        return this.mContext;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.mItems.size();
    }

    @Override // android.widget.Adapter
    public String getItem(int position) {
        if (this.mItems == null || this.mItems.isEmpty()) {
            return null;
        }
        return this.mItems.get(position);
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        return position;
    }

    public void setMenuItemList(ArrayList<String> items) {
        this.mItems = items;
    }

    public void setMenuItemListNoInitialize(ArrayList<String> items) {
        this.mItems = items;
    }

    private CharSequence getMenuItemText(int position) {
        String itemId = this.mItems.get(position);
        CharSequence text = this.mService.getMenuItemText(itemId);
        if (text == null) {
            this.builder.replace(0, this.builder.length(), LOG_MSG_NOT_FOUND_RES);
            this.builder.append(this.mItems.get(position));
            Log.e(TAG, this.builder.toString());
        }
        return text;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Drawable getMenuItemDrawable(int position) {
        return getMenuItemDrawable(this.mItems.get(position));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Drawable getMenuItemDrawable(String itemId) {
        Drawable icon = this.mIconCache.getImage(itemId);
        if (icon == null) {
            icon = this.mService.getMenuItemDrawable(itemId);
            this.mIconCache.setImage(itemId, icon);
        }
        if (icon == null) {
            this.builder.replace(0, this.builder.length(), LOG_MSG_NOT_FOUND_RES);
            this.builder.append(itemId);
            Log.e(TAG, this.builder.toString());
        }
        return icon;
    }

    protected Drawable getMenuItemSelectedDrawable(int position) {
        String itemId = this.mItems.get(position);
        Drawable icon = this.mSelectedIconCache.getImage(itemId);
        if (icon == null) {
            icon = this.mService.getMenuItemSelectedDrawable(itemId);
            this.mSelectedIconCache.setImage(itemId, icon);
        }
        if (icon == null) {
            this.builder.replace(0, this.builder.length(), LOG_MSG_NOT_FOUND_RES);
            this.builder.append(this.mItems.get(position));
            Log.e(TAG, this.builder.toString());
        }
        return icon;
    }

    protected Drawable getMenuItemLayer1Drawable(int position) {
        Log.w(TAG, "getMenuItemLayer1Drawable is deprecated");
        return null;
    }

    @Deprecated
    public void setSelectedView(ImageView iView, int position) {
        Log.w(TAG, "setSelectedView is deprecated");
    }

    public void setItemBackgroundImage(int resourceId) {
        this.mItemBackGroundId = resourceId;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Drawable getItemDrawable(int position, boolean isSelected) {
        String itemId = getItem(position);
        Drawable drawable = null;
        if (itemId != null) {
            if (!isSelected) {
                drawable = getMenuItemDrawable(position);
            } else {
                drawable = getMenuItemSelectedDrawable(position);
            }
            Log.v(TAG, "getItemDrawable. item = " + itemId);
        }
        return drawable;
    }

    protected View getInflateView() {
        return this.mInflater.inflate(this.mResId, (ViewGroup) null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setAvailableAlpha(Drawable d, String itemId) {
        if (d == null) {
            Log.w(TAG, MSG_DRAWABLE_UNAVAILABLE);
            return;
        }
        boolean isValid = this.mService.isMenuItemValid(itemId);
        if (!isValid) {
            d.setAlpha(INVISIBLE_ALPHA);
        } else {
            d.setAlpha(VISIBLE_ALPHA);
        }
    }

    protected void setAvailableAlpha(ImageView iv, String itemId) {
        boolean isValid = this.mService.isMenuItemValid(itemId);
        if (!isValid) {
            iv.setAlpha(127);
            iv.setAlpha(INVISIBLE_ALPHA);
        } else {
            iv.setAlpha(254);
            iv.setAlpha(VISIBLE_ALPHA);
        }
    }

    protected void setAvailableAlpha(TextView iv, String itemId) {
        boolean isValid = this.mService.isMenuItemValid(itemId);
        if (!isValid) {
            iv.setTextColor(-8947849);
        } else {
            iv.setTextColor(-2236963);
        }
    }

    public void imageCacheClear() {
        for (SoftReference<Drawable> ref : this.mIconCache.cache.values()) {
            Drawable d = ref.get();
            if (d != null) {
                d.setAlpha(VISIBLE_ALPHA);
            }
        }
        for (SoftReference<Drawable> ref2 : this.mSelectedIconCache.cache.values()) {
            Drawable d2 = ref2.get();
            if (d2 != null) {
                d2.setAlpha(VISIBLE_ALPHA);
            }
        }
        this.mIconCache.clear();
        this.mSelectedIconCache.clear();
        this.mFirstLayerIconCache.clear();
    }

    /* loaded from: classes.dex */
    protected class ViewHolder {
        public ImageView button;
        public ImageView iv;
        public TextView text;

        protected ViewHolder() {
        }
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view = convertView;
        if (view == null) {
            view = getInflateView();
            holder = new ViewHolder();
            holder.iv = (ImageView) view.findViewWithTag(RESTAG_ICON);
            holder.button = (ImageView) view.findViewWithTag(RESTAG_BACK);
            holder.text = (TextView) view.findViewWithTag(RESTAG_TEXT);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        Drawable d = getItemDrawable(position, false);
        setAvailableAlpha(d, getItem(position));
        holder.iv.setImageDrawable(d);
        if (holder.button != null && this.mItemBackGroundId > 0) {
            holder.button.setImageResource(this.mItemBackGroundId);
        }
        if (holder.text != null) {
            setAvailableAlpha(holder.text, getItem(position));
            holder.text.setText(getMenuItemText(position));
        }
        return view;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class ImageCache {
        private HashMap<String, SoftReference<Drawable>> cache = new HashMap<>();

        protected ImageCache() {
        }

        public Drawable getImage(String itemId) {
            SoftReference<Drawable> ref = this.cache.get(itemId);
            if (ref != null) {
                return ref.get();
            }
            return null;
        }

        public void setImage(String itemId, Drawable image) {
            this.cache.put(itemId, new SoftReference<>(image));
        }

        public void clear() {
            this.cache.clear();
        }
    }
}
