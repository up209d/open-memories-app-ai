package com.sony.imaging.app.base.menu.layout.list;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.menu.layout.list.AbstractListMenuAdapter;
import com.sony.imaging.app.util.BeepUtility;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class DefaultListMenuAdapter extends AbstractListMenuAdapter {
    public DefaultListMenuAdapter(Context context, ArrayList<DefaultListMenuItem> items, AbstractListMenuAdapter.ListMenuSelectedListener listener) {
        super(context, items, listener);
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuAdapter
    protected View getYourView(final int position, View view, ViewGroup parent) {
        TextView titleView = (TextView) view.findViewById(R.id.list_menu_item_title);
        TextView valueView = (TextView) view.findViewById(R.id.list_menu_item_value);
        ImageView valueImageView = (ImageView) view.findViewById(R.id.list_menu_item_image);
        DefaultListMenuItem item = getMenuItems().get(position);
        titleView.setText(item.getTitleStrId());
        if (item.isEnabled()) {
            titleView.setTextColor(getResources().getColor(R.color.RESID_FONTSTYLE_CMN_WHITE_NO_SKIN));
            switch (item.getItemType()) {
                case SELECTOR:
                    valueView.setVisibility(0);
                    valueImageView.setVisibility(8);
                    valueView.setText(item.getValueSelectorContents().get(item.getSelectedItemIndex()).getValueStringId());
                    valueView.setTextColor(getResources().getColor(R.color.RESID_FONTSTYLE_MENU_ITEM_VALUE));
                    break;
                case OPENER:
                case NO_PREVIEW_SELECTOR:
                    valueView.setVisibility(0);
                    valueImageView.setVisibility(8);
                    valueView.setText(17041730);
                    valueView.setTextColor(getResources().getColor(R.color.RESID_FONTSTYLE_MENU_ITEM_VALUE));
                    break;
            }
            view.setOnClickListener(new View.OnClickListener() { // from class: com.sony.imaging.app.base.menu.layout.list.DefaultListMenuAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View arg0) {
                    if (arg0.isInTouchMode()) {
                        BeepUtility.getInstance().playBeep("BEEP_ID_SELECT");
                    }
                    DefaultListMenuAdapter.this.getListMenuSelectedListener().onItemSelected(position, true);
                }
            });
        } else {
            titleView.setTextColor(getResources().getColor(R.color.RESID_FONTSTYLE_MENU_ITEM_DISABLE));
            valueView.setVisibility(0);
            valueImageView.setVisibility(8);
            valueView.setText(17041730);
            valueView.setTextColor(getResources().getColor(R.color.RESID_FONTSTYLE_MENU_ITEM_VALUE_DISABLE));
            view.setOnClickListener(new View.OnClickListener() { // from class: com.sony.imaging.app.base.menu.layout.list.DefaultListMenuAdapter.2
                @Override // android.view.View.OnClickListener
                public void onClick(View arg0) {
                    DefaultListMenuAdapter.this.getListMenuSelectedListener().onItemSelected(position, false);
                }
            });
        }
        return view;
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuAdapter
    protected int getRowViewLayoutId() {
        return R.layout.parts_listmenu_row;
    }
}
