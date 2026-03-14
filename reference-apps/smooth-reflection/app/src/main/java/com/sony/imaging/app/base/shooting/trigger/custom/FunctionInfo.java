package com.sony.imaging.app.base.shooting.trigger.custom;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.fw.IFunctionTable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public class FunctionInfo implements IFunctionTable.IFunctionInfo {
    protected Class<?> cntlClass;
    protected int imageid;
    protected String itemid;

    public FunctionInfo(int imageid, String itemid, Class<?> cntlClass) {
        this.itemid = itemid;
        this.imageid = imageid;
        this.cntlClass = cntlClass;
    }

    @Override // com.sony.imaging.app.fw.IFunctionTable.IFunctionInfo
    public String getItemId() {
        return this.itemid;
    }

    @Override // com.sony.imaging.app.fw.IFunctionTable.IFunctionInfo
    public int getImageId() {
        return this.imageid;
    }

    @Override // com.sony.imaging.app.fw.IFunctionTable.IFunctionInfo
    public boolean isValid() {
        if (this.cntlClass != null) {
            try {
                Method method = this.cntlClass.getDeclaredMethod("getInstance", new Class[0]);
                IController instance = (IController) method.invoke(this.cntlClass, new Object[0]);
                boolean ret = instance.isAvailable(this.itemid);
                return ret;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return false;
            } catch (IllegalArgumentException e2) {
                e2.printStackTrace();
                return false;
            } catch (NoSuchMethodException e3) {
                e3.printStackTrace();
                return false;
            } catch (SecurityException e4) {
                e4.printStackTrace();
                return false;
            } catch (InvocationTargetException e5) {
                e5.printStackTrace();
                return false;
            }
        }
        return true;
    }
}
