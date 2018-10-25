package com.one.framework.app.web.hybird;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import com.one.framework.app.base.BaseActivity;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public abstract class BaseHybridActivity extends BaseActivity implements IHybridActivity {

  private HashMap<Class, Object> cachedModuleInstance = new HashMap();

  public Activity getActivity() {
    return this;
  }

  public abstract FusionWebView getWebView();

  public Object getExportModuleInstance(Class exportClass) {
    Object instance = this.cachedModuleInstance.get(exportClass);
    if (instance == null) {
      try {
        Constructor constructor = exportClass.getConstructor(IHybridActivity.class);
        instance = constructor.newInstance(this);
      } catch (InstantiationException var4) {
        var4.printStackTrace();
      } catch (IllegalAccessException var5) {
        var5.printStackTrace();
      } catch (InvocationTargetException var6) {
        var6.printStackTrace();
      } catch (NoSuchMethodException var7) {
        var7.printStackTrace();
      }

      if (instance != null) {
        this.cachedModuleInstance.put(exportClass, instance);
      }
    }

    return instance;
  }

  @Override
  protected int getStatusBarColor() {
    return Color.parseColor("#e3e3e3");
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    this.cachedModuleInstance.clear();
  }
}
