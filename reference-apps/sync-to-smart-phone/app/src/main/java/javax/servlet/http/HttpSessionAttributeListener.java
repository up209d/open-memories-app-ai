package javax.servlet.http;

import java.util.EventListener;

/* loaded from: classes.dex */
public interface HttpSessionAttributeListener extends EventListener {
    void attributeAdded(HttpSessionBindingEvent httpSessionBindingEvent);

    void attributeRemoved(HttpSessionBindingEvent httpSessionBindingEvent);

    void attributeReplaced(HttpSessionBindingEvent httpSessionBindingEvent);
}
