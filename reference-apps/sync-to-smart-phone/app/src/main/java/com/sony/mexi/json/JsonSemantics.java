package com.sony.mexi.json;

import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.mexi.json.mouse.SemanticsBase;

/* loaded from: classes.dex */
public class JsonSemantics extends SemanticsBase {
    private JsValue val;

    public JsValue getValue() {
        return this.val;
    }

    public void clearValue() {
        this.val = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void jsUndefined() {
        lhs().put(JsUndefined.getInstance());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void jsNull() {
        lhs().put(JsNull.getInstance());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void jsBoolean() {
        lhs().put(new JsBoolean(Boolean.valueOf(lhs().text())));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void jsInteger() {
        String text = lhs().text();
        try {
            lhs().put(new JsNumber(Long.valueOf(text)));
        } catch (NumberFormatException e) {
            lhs().put(new JsNumber(Double.valueOf(text)));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void jsDouble() {
        lhs().put(new JsNumber(Double.valueOf(lhs().text())));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void jsNumber() {
        lhs().put(rhs(0).get());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void jsNormalChar() {
        lhs().put(lhs().text());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void jsEscaped() {
        String c = rhs(1).text();
        if (c.equals(ISOSensitivityController.ISO_AUTO)) {
            lhs().put("\u0000");
            return;
        }
        if (c.equals("b")) {
            lhs().put("\b");
            return;
        }
        if (c.equals("t")) {
            lhs().put("\t");
            return;
        }
        if (c.equals("n")) {
            lhs().put("\n");
            return;
        }
        if (c.equals("f")) {
            lhs().put("\f");
        } else if (c.equals("r")) {
            lhs().put("\r");
        } else {
            lhs().put(c);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void jsUnicode() {
        char u = (char) Integer.parseInt(lhs().text().substring(2), 16);
        lhs().put(String.valueOf(u));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void jsChar() {
        lhs().put(rhs(0).get());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void jsCharText() {
        lhs().put(rhs(0).text());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void jsString() {
        int n = rhsSize();
        StringBuffer buffer = new StringBuffer(n);
        for (int i = 1; i < n - 1; i++) {
            buffer.append(rhs(i).get());
        }
        lhs().put(new JsString(buffer.toString()));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void jsArray() {
        int n = rhsSize();
        JsArray a = new JsArray(new JsValue[0]);
        if (n > 3) {
            for (int i = 2; i < n; i += 4) {
                a.add((JsValue) rhs(i).get());
            }
        }
        lhs().put(a);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class JsPair {
        public JsValue name;
        public JsValue value;

        JsPair() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void jsPair() {
        JsPair p = new JsPair();
        p.name = (JsValue) rhs(0).get();
        p.value = (JsValue) rhs(4).get();
        lhs().put(p);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void jsObject() {
        int n = rhsSize();
        JsObject o = new JsObject();
        if (n > 3) {
            for (int i = 2; i < n; i += 4) {
                JsPair p = (JsPair) rhs(i).get();
                o.put(p.name.toJavaString(), p.value);
            }
        }
        lhs().put(o);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void jsValue() {
        lhs().put(rhs(0).get());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void jsonValue() {
        this.val = (JsValue) rhs(1).get();
    }
}
