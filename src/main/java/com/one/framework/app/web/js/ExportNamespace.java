package com.one.framework.app.web.js;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ExportNamespace {

  private static final String TAG = ExportNamespace.class.getName();
  private String mExportName;
  private Class mExportClass;
  private Map<String, Method> mExportMethods;

  public ExportNamespace(String name, Class clz) {
    this.mExportName = name;
    this.mExportClass = clz;
  }

  public String getExportName() {
    return this.mExportName;
  }

  public Class getExportClass() {
    return this.mExportClass;
  }

  public Method getTargetMethod(String exportName) {
    if (this.mExportMethods == null) {
      this.mExportMethods = new HashMap();
      Method[] allMethods = this.mExportClass.getMethods();
      Method[] var3 = allMethods;
      int var4 = allMethods.length;

      for (int var5 = 0; var5 < var4; ++var5) {
        Method method = var3[var5];
        JsInterface exprotAnnotatioin = (JsInterface) method.getAnnotation(JsInterface.class);
        if (exprotAnnotatioin != null) {
          String[] jsFuncNames = exprotAnnotatioin.value();
          String[] var9 = jsFuncNames;
          int var10 = jsFuncNames.length;

          for (int var11 = 0; var11 < var10; ++var11) {
            String name = var9[var11];
            this.mExportMethods.put(name, method);
          }
        }
      }
    }

    Method targetMethod = (Method) this.mExportMethods.get(exportName);
    return targetMethod;
  }
}
