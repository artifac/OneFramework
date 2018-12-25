package com.one.framework.app.web.hybird;

import android.app.Activity;
import com.one.framework.app.base.BaseActivity;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public abstract class BaseHybridActivity extends BaseActivity implements IHybridActivity {

  private HashMap<Class, Object> cachedModuleInstance = new HashMap();

  public Activity getActivity() {
    return this;
  }

  public Object getExportModuleInstance(Class exportClass) {
    Object instance = this.cachedModuleInstance.get(exportClass);
    if (instance == null) {
      try {
        Constructor constructor = exportClass.getConstructor(IHybridActivity.class);
        instance = constructor.newInstance(this);
      } catch (InstantiationException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      }

      if (instance != null) {
        this.cachedModuleInstance.put(exportClass, instance);
      }
    }
    return instance;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    this.cachedModuleInstance.clear();
  }
}
