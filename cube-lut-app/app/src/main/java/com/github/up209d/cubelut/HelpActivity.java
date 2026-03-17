package com.github.up209d.cubelut;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class HelpActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        TextView closeBtn = (TextView) findViewById(R.id.btn_close);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected boolean onEnterKeyDown() {
        finish();
        return true;
    }

    @Override
    protected boolean onDeleteKeyDown() {
        return true;
    }

    @Override
    protected boolean onDeleteKeyUp() {
        finish();
        return true;
    }
}
