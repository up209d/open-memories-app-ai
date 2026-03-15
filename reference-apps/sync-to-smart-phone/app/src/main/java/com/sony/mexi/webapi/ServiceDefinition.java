package com.sony.mexi.webapi;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

/* loaded from: classes.dex */
public class ServiceDefinition {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static Set<String> defaultCallbackMethods;
    private Class<?> serviceInterface;
    private String serviceName;
    private MethodComparator methodComp = new MethodComparator(this, null);
    private ClassComparator classComp = new ClassComparator(this, 0 == true ? 1 : 0);
    private Set<Method> methodSet = new TreeSet(this.methodComp);
    private Set<Method> userMethodSet = new TreeSet(this.methodComp);
    private Set<Class<?>> enumSet = new TreeSet(this.classComp);
    private Vector<Enum> enumVec = new Vector<>();
    private Set<Class<?>> structureSet = new TreeSet(this.classComp);
    private Set<Class<?>> callbackInterfaceSet = new TreeSet(this.classComp);
    private Set<Class<?>> userCallbackInterfaceSet = new TreeSet(this.classComp);
    private Map<String, String> tags = new HashMap();

    static {
        $assertionsDisabled = !ServiceDefinition.class.desiredAssertionStatus();
        defaultCallbackMethods = new HashSet();
    }

    /* loaded from: classes.dex */
    public static class Function {
        static final /* synthetic */ boolean $assertionsDisabled;
        static Class<?> currentCustomClass;
        static List<String> udsInterfaceNames;
        protected String name;
        protected Vector<String> paramNames;
        protected Class<?> retClass;
        protected Type retType;
        protected Vector<Class<?>> paramClasses = new Vector<>();
        protected Vector<Type> paramTypes = new Vector<>();

        static {
            $assertionsDisabled = !ServiceDefinition.class.desiredAssertionStatus();
            udsInterfaceNames = new ArrayList();
        }

        Function(java.lang.reflect.Method m) {
            this.name = m.getName();
            this.retClass = m.getReturnType();
            this.retType = Type.classToType(this.retClass);
            Class<?>[] paramTs = m.getParameterTypes();
            for (Class<?> c : paramTs) {
                if (!$assertionsDisabled && c == null) {
                    throw new AssertionError();
                }
                this.paramClasses.add(verifyCustomClass(c));
                this.paramTypes.add(Type.classToType(c));
            }
            getParameterNames(m);
        }

        private static Class<?> verifyCustomClass(Class<?> c) {
            if (isCustomClass(c) && Type.classToType(c) != Type.CALLBACKS) {
                String udsname = c.getSimpleName();
                if (c.isArray()) {
                    udsname = c.getComponentType().getSimpleName();
                    currentCustomClass = c.getComponentType();
                } else {
                    currentCustomClass = c;
                }
                if (!udsInterfaceNames.contains(udsname)) {
                    udsInterfaceNames.add(udsname);
                    if (currentCustomClass.getModifiers() == 1) {
                        Field[] fields = currentCustomClass.getDeclaredFields();
                        if (fields.length == 0) {
                            throw new IllegalServiceException("Custom class \"" + currentCustomClass.getName() + "\" has no members");
                        }
                        for (int i = 0; i < fields.length; i++) {
                            if (fields[i].getModifiers() == 1) {
                                if (isInvalidClassField(fields[i].getType())) {
                                    throw new IllegalServiceException("Custom class has illegal member type :" + fields[i].getType().getSimpleName());
                                }
                            } else {
                                throw new IllegalServiceException("Custom class member: \"" + fields[i].getName() + "\" is not public");
                            }
                        }
                        if (currentCustomClass.getDeclaredMethods().length != 0) {
                            throw new IllegalServiceException("Custom class \"" + currentCustomClass.getName() + "\" has methods");
                        }
                    } else if (!currentCustomClass.isEnum()) {
                        throw new IllegalServiceException("Custom class: \"" + currentCustomClass.getName() + "\" is not public");
                    }
                }
            }
            return c;
        }

        private static boolean isCustomClass(Class<?> c) {
            return (c.isPrimitive() || c == String.class || boolean[].class.isAssignableFrom(c) || int[].class.isAssignableFrom(c) || double[].class.isAssignableFrom(c) || String[].class.isAssignableFrom(c)) ? false : true;
        }

        private static boolean isInvalidClassField(Class<?> c) {
            return (c == Integer.TYPE || c == Double.TYPE || c == Boolean.TYPE || c == String.class || c == int[].class || c == double[].class || c == boolean[].class || c == String[].class || c.isEnum() || (c.isArray() && c.getComponentType().isEnum())) ? false : true;
        }

        private void getParameterNames(java.lang.reflect.Method m) {
            try {
                Class<?> extractor = Class.forName("com.sony.mexi.codegen.Doclet");
                try {
                    java.lang.reflect.Method hasParameters = extractor.getMethod("hasParameters", null);
                    try {
                        boolean has = ((Boolean) hasParameters.invoke(null, null)).booleanValue();
                        if (has) {
                            String qualifiedName = String.valueOf(m.getDeclaringClass().getName()) + "." + m.getName();
                            try {
                                java.lang.reflect.Method getParameterNames = extractor.getMethod("getParameterNames", String.class);
                                try {
                                    this.paramNames = (Vector) getParameterNames.invoke(null, qualifiedName);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            } catch (Exception e2) {
                                throw new RuntimeException(e2);
                            }
                        }
                    } catch (Exception e3) {
                        throw new RuntimeException(e3);
                    }
                } catch (Exception e4) {
                    throw new RuntimeException(e4);
                }
            } catch (ClassNotFoundException e5) {
            }
        }

        public String getName() {
            return this.name;
        }

        public Class<?> getReturnClass() {
            return this.retClass;
        }

        public Type getReturnType() {
            return this.retType;
        }

        public Class<?> getParameterClass(int n) {
            return this.paramClasses.get(n);
        }

        public Type getParameterType(int n) {
            return this.paramTypes.get(n);
        }

        public String getParameterName(int i) {
            if (this.paramNames != null) {
                return this.paramNames.get(i);
            }
            return null;
        }

        public int getNumberOfParameters() {
            return this.paramTypes.size();
        }
    }

    /* loaded from: classes.dex */
    public static class Callback extends Function {
        private Class<?> callbackInterface;

        Callback(Class<?> c, java.lang.reflect.Method m) {
            super(m);
            this.callbackInterface = c;
            if (this.retType != Type.VOID) {
                throw new IllegalServiceException("Return type not void: " + m);
            }
            int size = this.paramTypes.size();
            for (int i = 0; i < size; i++) {
                Type t = this.paramTypes.get(i);
                if (t == Type.CALLBACKS) {
                    throw new IllegalServiceException("Callbacks in callback method: " + this.paramClasses.get(i));
                }
                if (t == Type.UNKNOWN) {
                    throw new IllegalServiceException("Unknown type: " + this.paramClasses.get(i));
                }
            }
        }

        public Class<?> getInterface() {
            return this.callbackInterface;
        }

        public boolean isContinuous() {
            return ContinuousCallbacks.class.isAssignableFrom(this.callbackInterface);
        }

        public boolean withHttpHeaderHandler() {
            return HttpHeaderHandler.class.isAssignableFrom(this.callbackInterface);
        }
    }

    public static Callback getResultHandler(Class<?> c) {
        if (defaultCallbackMethods.isEmpty()) {
            for (java.lang.reflect.Method method : Callbacks.class.getMethods()) {
                defaultCallbackMethods.add(method.getName());
            }
        }
        java.lang.reflect.Method[] ms = c.getDeclaredMethods();
        if (ms.length > defaultCallbackMethods.size() + 1) {
            throw new IllegalServiceException("Multiple result handlers: " + c);
        }
        for (java.lang.reflect.Method m : ms) {
            String name = m.getName();
            if (!defaultCallbackMethods.contains(name)) {
                return new Callback(c, m);
            }
        }
        throw new IllegalServiceException("Result handler not found: " + c);
    }

    /* loaded from: classes.dex */
    public static class Method extends Function {
        private Class<?> callbacks;
        private Callback resultHandler;

        Method(java.lang.reflect.Method m) {
            super(m);
            if (this.retType != Type.INT) {
                throw new IllegalServiceException("Return type not int: " + m);
            }
            if (this.paramTypes.isEmpty()) {
                throw new IllegalServiceException("Callbacks not found: " + m);
            }
            if (this.paramTypes.lastElement() != Type.CALLBACKS) {
                throw new IllegalServiceException("Last parameter not callbacks");
            }
            this.callbacks = this.paramClasses.lastElement();
            this.resultHandler = ServiceDefinition.getResultHandler(this.callbacks);
            int size = this.paramTypes.size() - 1;
            for (int i = 0; i < size; i++) {
                if (this.paramTypes.get(i) == Type.CALLBACKS) {
                    throw new IllegalServiceException("Callbacks not last parameter: " + this.paramClasses.get(i));
                }
                if (this.paramTypes.get(i) == Type.UNKNOWN) {
                    throw new IllegalServiceException("Unknown type: " + this.paramClasses.get(i));
                }
            }
        }

        public Class<?> getBasicParameterClass(int n) {
            if (n < this.paramTypes.size() - 1) {
                return this.paramClasses.get(n);
            }
            throw new ArrayIndexOutOfBoundsException(n);
        }

        public Type getBasicParameterType(int n) {
            if (n < this.paramTypes.size() - 1) {
                return this.paramTypes.get(n);
            }
            throw new ArrayIndexOutOfBoundsException(n);
        }

        public int getNumberOfBasicParameters() {
            return this.paramTypes.size() - 1;
        }

        public Callback getResultHandler() {
            return this.resultHandler;
        }

        public Class<?> getCallbacks() {
            return this.callbacks;
        }

        public boolean hasContinuousCallbacks() {
            return ContinuousCallbacks.class.isAssignableFrom(this.callbacks);
        }

        public boolean isUserDefined() {
            java.lang.reflect.Method[] ms = Service.class.getMethods();
            for (java.lang.reflect.Method method : ms) {
                if (method.getName().equals(getName())) {
                    return false;
                }
            }
            return true;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof Method)) {
                return false;
            }
            Method that = (Method) obj;
            return this.name.equals(that.name);
        }

        public int hashCode() {
            return this.name.hashCode();
        }
    }

    /* loaded from: classes.dex */
    public class Enum {
        private Class<?> enumT;
        private Vector<String> names = new Vector<>();
        private boolean useInArray;
        private boolean useInBack;
        private boolean useInForward;

        public Enum(Class<?> e, boolean useInArray, boolean useInCallback) {
            this.enumT = e;
            this.useInArray = useInArray;
            setUseIn(useInCallback);
            Field[] fs = this.enumT.getFields();
            for (Field f : fs) {
                if (f.isEnumConstant()) {
                    this.names.add(f.getName());
                }
            }
        }

        public Class<?> getEnumClass() {
            return this.enumT;
        }

        public String getEnumName() {
            String enumName = this.enumT.getName();
            return enumName.substring(enumName.lastIndexOf(".") + 1);
        }

        public String getEnumConstant(int n) {
            return this.names.get(n);
        }

        public int getNumberOfEnumConstants() {
            return this.names.size();
        }

        public void setUseInArray() {
            this.useInArray = true;
        }

        public boolean useInArray() {
            return this.useInArray;
        }

        public void setUseIn(boolean callback) {
            if (callback) {
                this.useInBack = true;
            } else {
                this.useInForward = true;
            }
        }

        public boolean useInForward() {
            return this.useInForward;
        }

        public boolean useInBack() {
            return this.useInBack;
        }
    }

    /* loaded from: classes.dex */
    private class MethodComparator implements Comparator<Method> {
        private MethodComparator() {
        }

        /* synthetic */ MethodComparator(ServiceDefinition serviceDefinition, MethodComparator methodComparator) {
            this();
        }

        @Override // java.util.Comparator
        public int compare(Method m1, Method m2) {
            return String.CASE_INSENSITIVE_ORDER.compare(m1.name, m2.name);
        }
    }

    /* loaded from: classes.dex */
    private class ClassComparator implements Comparator<Class<?>> {
        private ClassComparator() {
        }

        /* synthetic */ ClassComparator(ServiceDefinition serviceDefinition, ClassComparator classComparator) {
            this();
        }

        @Override // java.util.Comparator
        public int compare(Class<?> c1, Class<?> c2) {
            return String.CASE_INSENSITIVE_ORDER.compare(c1.getName(), c2.getName());
        }
    }

    public Class<?> getInterface() {
        return this.serviceInterface;
    }

    public String getName() {
        return this.serviceName;
    }

    public String getTag(String tagName) {
        return this.tags.get(tagName);
    }

    public void clear() {
        this.methodSet.clear();
        this.userMethodSet.clear();
        this.enumSet.clear();
        this.enumVec.clear();
        this.callbackInterfaceSet.clear();
        this.userCallbackInterfaceSet.clear();
    }

    public Iterator<Method> getMethodIterator() {
        return this.methodSet.iterator();
    }

    public Iterator<Method> getUserMethodIterator() {
        return this.userMethodSet.iterator();
    }

    public int getNumberOfUserMethods() {
        return this.userMethodSet.size();
    }

    public Iterator<Enum> getEnumIterator() {
        return this.enumVec.iterator();
    }

    public Enum getEnum(String name) {
        Enum result = null;
        Iterator<Enum> itr = getEnumIterator();
        while (itr.hasNext() && result == null) {
            Enum e = itr.next();
            if (e.getEnumClass().getName().compareTo(name) == 0) {
                result = e;
            }
        }
        return result;
    }

    public Iterator<Class<?>> getCallbackInterfaceIterator() {
        return this.callbackInterfaceSet.iterator();
    }

    public Iterator<Class<?>> getUserCallbackInterfaceIterator() {
        return this.userCallbackInterfaceSet.iterator();
    }

    public ServiceDefinition(Class<?> cls) {
        if (!cls.isInterface()) {
            throw new IllegalArgumentException(cls.toString());
        }
        if (!Service.class.isAssignableFrom(cls)) {
            throw new IllegalServiceException("Not service: " + cls);
        }
        this.serviceInterface = cls;
        this.serviceName = cls.getName().split("\\.")[r2.length - 1];
        for (java.lang.reflect.Method method : cls.getMethods()) {
            Method method2 = new Method(method);
            if (containsMethod(method2)) {
                throw new IllegalServiceException("Method already exists: " + method);
            }
            addMethod(method2);
        }
        getTags(cls);
    }

    private boolean containsMethod(Method method) {
        return this.methodSet.contains(method);
    }

    private void addMethod(Method m) {
        if (!$assertionsDisabled && m == null) {
            throw new AssertionError();
        }
        if (!$assertionsDisabled && containsMethod(m)) {
            throw new AssertionError();
        }
        this.methodSet.add(m);
        if (m.isUserDefined()) {
            this.userMethodSet.add(m);
        }
        collectUserDefinedTypes(m, false);
        collectUserDefinedTypes(m.getResultHandler(), true);
        addCallbackInterface(m);
    }

    private void collectUserDefinedTypes(Function m, boolean useInCallback) {
        int n = m.getNumberOfParameters();
        for (int i = 0; i < n; i++) {
            Class<?> clazz = m.getParameterClass(i);
            collectUserDefinedTypes(clazz, false, useInCallback);
        }
    }

    private void collectUserDefinedTypes(Class<?> clazz, boolean useInArray, boolean useInCallback) {
        if (clazz != null && !clazz.isPrimitive()) {
            if (clazz.isArray()) {
                collectUserDefinedTypes(clazz.getComponentType(), true, useInCallback);
                return;
            }
            if (!clazz.getName().startsWith("java.")) {
                if (clazz.isEnum()) {
                    if (this.enumSet.add(clazz)) {
                        this.enumVec.add(new Enum(clazz, useInArray, useInCallback));
                        return;
                    }
                    Enum tmp = getEnum(clazz.getName());
                    if (useInArray) {
                        tmp.setUseInArray();
                    }
                    tmp.setUseIn(useInCallback);
                    return;
                }
                if (!this.structureSet.contains(clazz)) {
                    this.structureSet.add(clazz);
                    if (clazz.getInterfaces() != null) {
                        for (Class<?> i : clazz.getInterfaces()) {
                            collectUserDefinedTypes(i, false, useInCallback);
                        }
                    }
                    collectUserDefinedTypes(clazz.getSuperclass(), false, useInCallback);
                    Field[] fields = clazz.getFields();
                    if (fields != null) {
                        for (Field field : fields) {
                            collectUserDefinedTypes(field.getType(), false, useInCallback);
                        }
                    }
                }
            }
        }
    }

    private void addCallbackInterface(Method m) {
        Callback c = m.getResultHandler();
        Class<?> cbIf = c.getInterface();
        this.callbackInterfaceSet.add(cbIf);
        if (!cbIf.equals(VersionHandler.class) && !cbIf.equals(MethodTypeHandler.class)) {
            this.userCallbackInterfaceSet.add(cbIf);
        }
    }

    private void getTags(Class<?> c) {
        try {
            Class<?> extractor = Class.forName("com.sony.mexi.codegen.Doclet");
            try {
                java.lang.reflect.Method hasTags = extractor.getMethod("hasTags", null);
                try {
                    boolean has = ((Boolean) hasTags.invoke(null, null)).booleanValue();
                    if (has) {
                        String name = c.getName();
                        try {
                            java.lang.reflect.Method getTags = extractor.getMethod("getTags", String.class);
                            try {
                                this.tags = (Map) getTags.invoke(null, name);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        } catch (Exception e2) {
                            throw new RuntimeException(e2);
                        }
                    }
                } catch (Exception e3) {
                    throw new RuntimeException(e3);
                }
            } catch (Exception e4) {
                throw new RuntimeException(e4);
            }
        } catch (ClassNotFoundException e5) {
        }
    }
}
