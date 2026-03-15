package javax.servlet;

import com.sony.imaging.app.base.shooting.camera.parameters.AbstractSupportedChecker;
import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.ResourceBundle;

/* loaded from: classes.dex */
public abstract class GenericServlet implements Servlet, ServletConfig, Serializable {
    private static final String LSTRING_FILE = "javax.servlet.LocalStrings";
    private static ResourceBundle lStrings = ResourceBundle.getBundle(LSTRING_FILE);
    private transient ServletConfig config;

    @Override // javax.servlet.Servlet
    public abstract void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException;

    @Override // javax.servlet.Servlet
    public void destroy() {
    }

    @Override // javax.servlet.ServletConfig
    public String getInitParameter(String name) {
        ServletConfig sc = getServletConfig();
        if (sc == null) {
            throw new IllegalStateException(lStrings.getString("err.servlet_config_not_initialized"));
        }
        return sc.getInitParameter(name);
    }

    @Override // javax.servlet.ServletConfig
    public Enumeration getInitParameterNames() {
        ServletConfig sc = getServletConfig();
        if (sc == null) {
            throw new IllegalStateException(lStrings.getString("err.servlet_config_not_initialized"));
        }
        return sc.getInitParameterNames();
    }

    @Override // javax.servlet.Servlet
    public ServletConfig getServletConfig() {
        return this.config;
    }

    @Override // javax.servlet.ServletConfig
    public ServletContext getServletContext() {
        ServletConfig sc = getServletConfig();
        if (sc == null) {
            throw new IllegalStateException(lStrings.getString("err.servlet_config_not_initialized"));
        }
        return sc.getServletContext();
    }

    @Override // javax.servlet.Servlet
    public String getServletInfo() {
        return "";
    }

    @Override // javax.servlet.Servlet
    public void init(ServletConfig config) throws ServletException {
        this.config = config;
        init();
    }

    public void init() throws ServletException {
    }

    public void log(String msg) {
        getServletContext().log(String.valueOf(getServletName()) + AbstractSupportedChecker.COLON + msg);
    }

    public void log(String message, Throwable t) {
        getServletContext().log(String.valueOf(getServletName()) + AbstractSupportedChecker.COLON + message, t);
    }

    @Override // javax.servlet.ServletConfig
    public String getServletName() {
        ServletConfig sc = getServletConfig();
        if (sc == null) {
            throw new IllegalStateException(lStrings.getString("err.servlet_config_not_initialized"));
        }
        return sc.getServletName();
    }
}
