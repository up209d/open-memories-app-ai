package javax.servlet.http;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/* loaded from: classes.dex */
public class Cookie implements Cloneable {
    private static final String LSTRING_FILE = "javax.servlet.http.LocalStrings";
    private static ResourceBundle lStrings = ResourceBundle.getBundle(LSTRING_FILE);
    private static final String tspecials = ",; ";
    private String comment;
    private String domain;
    private String name;
    private String path;
    private boolean secure;
    private String value;
    private int maxAge = -1;
    private int version = 0;

    public Cookie(String name, String value) {
        if (!isToken(name) || name.equalsIgnoreCase("Comment") || name.equalsIgnoreCase("Discard") || name.equalsIgnoreCase("Domain") || name.equalsIgnoreCase("Expires") || name.equalsIgnoreCase("Max-Age") || name.equalsIgnoreCase("Path") || name.equalsIgnoreCase("Secure") || name.equalsIgnoreCase("Version") || name.startsWith("$")) {
            String errMsg = lStrings.getString("err.cookie_name_is_token");
            Object[] errArgs = {name};
            throw new IllegalArgumentException(MessageFormat.format(errMsg, errArgs));
        }
        this.name = name;
        this.value = value;
    }

    public void setComment(String purpose) {
        this.comment = purpose;
    }

    public String getComment() {
        return this.comment;
    }

    public void setDomain(String pattern) {
        this.domain = pattern.toLowerCase();
    }

    public String getDomain() {
        return this.domain;
    }

    public void setMaxAge(int expiry) {
        this.maxAge = expiry;
    }

    public int getMaxAge() {
        return this.maxAge;
    }

    public void setPath(String uri) {
        this.path = uri;
    }

    public String getPath() {
        return this.path;
    }

    public void setSecure(boolean flag) {
        this.secure = flag;
    }

    public boolean getSecure() {
        return this.secure;
    }

    public String getName() {
        return this.name;
    }

    public void setValue(String newValue) {
        this.value = newValue;
    }

    public String getValue() {
        return this.value;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int v) {
        this.version = v;
    }

    private boolean isToken(String value) {
        int len = value.length();
        for (int i = 0; i < len; i++) {
            char c = value.charAt(i);
            if (c < ' ' || c >= 127 || tspecials.indexOf(c) != -1) {
                return false;
            }
        }
        return true;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
