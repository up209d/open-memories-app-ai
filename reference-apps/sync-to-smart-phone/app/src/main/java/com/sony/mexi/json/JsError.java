package com.sony.mexi.json;

import com.sony.mexi.json.JsValue;

/* loaded from: classes.dex */
public class JsError extends JsObject {
    private static final long serialVersionUID = 1;

    public JsError(String msg) {
        setMessage(msg);
    }

    public JsError(int number, String msg) {
        setNumber(number);
        setMessage(msg);
    }

    public JsError(String name, String msg) {
        setName(name);
        setMessage(msg);
    }

    public void setNumber(int number) {
        put("number", new JsNumber(Integer.valueOf(number)));
    }

    public int getNumber() {
        JsValue number = get("number");
        if (number == null) {
            return 0;
        }
        return number.toJavaInt();
    }

    public void setName(String name) {
        put("name", new JsString(name));
    }

    public String getName() {
        JsValue name = get("name");
        if (name == null) {
            return null;
        }
        return name.toJavaString();
    }

    public void setMessage(String msg) {
        put("message", new JsString(msg));
    }

    public String getMessage() {
        JsValue msg = get("message");
        if (msg == null) {
            return null;
        }
        return msg.toJavaString();
    }

    @Override // com.sony.mexi.json.JsObject, com.sony.mexi.json.JsValue
    public JsValue.Type type() {
        return JsValue.Type.ERROR;
    }
}
