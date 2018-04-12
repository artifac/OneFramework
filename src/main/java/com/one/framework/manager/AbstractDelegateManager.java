package com.one.framework.manager;

import com.one.framework.api.annotation.ServiceProvider;
import java.util.ServiceLoader;

/**
 * Created by ludexiang on 2018/3/27.
 */

public abstract class AbstractDelegateManager<T> {

  protected AbstractDelegateManager() {

  }

  protected void loadDelegate(Class<T> clazz, IDelegateListener<T> listener) {
    for (T t : ServiceLoader.load(clazz)) {
      ServiceProvider provider = t.getClass().getAnnotation(ServiceProvider.class);
      listener.onDelegate(provider.alias(), t);
    }
  }

  protected void loadDelegateClass(Class<T> clazz, IDelegateListener<Class<? extends T>> listener) {
    for (T t : ServiceLoader.load(clazz)) {
      ServiceProvider provider = t.getClass().getAnnotation(ServiceProvider.class);
      listener.onDelegate(provider.alias(), (Class<? extends T>) t.getClass());
    }
  }

  public interface IDelegateListener<T> {

    void onDelegate(String id, T clazz);
  }
}
