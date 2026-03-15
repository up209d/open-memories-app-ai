package com.sony.imaging.app.base.menu.layout.list;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.menu.layout.list.AbstractListMenuAdapter;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class ValueSelectorAdapter extends AbstractListMenuAdapter {
    public ValueSelectorAdapter(Context context, ArrayList<DefaultListMenuItem> items, AbstractListMenuAdapter.ListMenuSelectedListener listener) {
        super(context, items, listener);
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuAdapter
    protected View getYourView(final int position, View view, ViewGroup parent) {
        TextView valueView = (TextView) view.findViewById(R.id.listmenu_selector_value);
        ImageView radio = (ImageView) view.findViewById(R.id.listmenu_selector_radio_button);
        ValueSelectorItem item = (ValueSelectorItem) getMenuItems().get(position);
        valueView.setText(item.getTitleStrId());
        if (item.isSelected()) {
            radio.setImageLevel(1);
        } else {
            radio.setImageLevel(0);
        }
        view.setOnClickListener(new View.OnClickListener() { // from class: com.sony.imaging.app.base.menu.layout.list.ValueSelectorAdapter.1
            @Override // android.view.View.OnClickListener
            public void onClick(View arg0) {
                ValueSelectorAdapter.this.getListMenuSelectedListener().onItemSelected(position, true);
            }
        });
        return view;
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuAdapter
    protected int getRowViewLayoutId() {
        return R.layout.parts_listmenu_selector_row;
    }
}
