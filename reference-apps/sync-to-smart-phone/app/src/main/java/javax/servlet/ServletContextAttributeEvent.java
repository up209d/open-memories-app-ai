package javax.servlet;

/* loaded from: classes.dex */
public class ServletContextAttributeEvent extends ServletContextEvent {
    private String name;
    private Object value;

    public ServletContextAttributeEvent(ServletContext source, String name, Object value) {
        super(source);
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public Object getValue() {
        return this.value;
    }
}
