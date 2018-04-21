package com.one.framework.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.support.v4.app.Fragment;
import android.util.Log;
import com.one.framework.app.model.IBusinessContext;
import com.one.framework.app.page.IComponent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by ludexiang on 2018/3/28.
 */

public class PageDelegateManager extends AbstractDelegateManager<Fragment> {

  private Map<IntentFilter, Class<? extends Fragment>> fragments;
  private static PageDelegateManager sPageDelegateManager;

  private PageDelegateManager() {
    fragments = new HashMap<>();
    loadFragments();
  }

  public static PageDelegateManager getInstance() {
    synchronized (PageDelegateManager.class) {
      if (sPageDelegateManager == null) {
        sPageDelegateManager = new PageDelegateManager();
      }
      return sPageDelegateManager;
    }
  }

  public Fragment getFragment(Intent intent, IBusinessContext businessContext) {
    ArrayList<Class<? extends Fragment>> matchedPages = new ArrayList<>();

    // 先进行ComponentName匹配 如果匹配OK 则直接返回
    boolean isComponentMatched = false;
    ComponentName componentName = intent.getComponent();
    if (componentName != null) {
      String componentClassName = componentName.getClassName();
      try {
        Class pageClass = Class.forName(componentClassName);

        // component是否从Page派生
        boolean isAssignable = Fragment.class.isAssignableFrom(pageClass);

        // 保证只处理Page的跳转
        if (isAssignable) {
          matchedPages.add(pageClass);
          isComponentMatched = true;
        }
      } catch (ClassNotFoundException ex) {
        ex.printStackTrace();
      }

    }

    // 如果component匹配未成功 则进行全方位匹配
    if (!isComponentMatched) {
      fullMatch(businessContext.getContext(), intent, matchedPages);
    }

    // 匹配的个数
    int matchedSize = matchedPages.size();

    if (matchedSize == 0) {
      return null;
    }

    // 目前我们的场景下 是不允许出现多个匹配的
    if (matchedSize > 1) {
      StringBuilder errorMsg = new StringBuilder();
      errorMsg.append("please make sure the intentFilter is unique\n");
      errorMsg.append(String.format("Intent Uri: %s", intent.getData())).append("\n");
      for (Class<? extends Fragment> p : matchedPages) {
        errorMsg.append(String.format("  Page: [%s]", p.getName())).append("\n");
      }
      throw new RuntimeException(errorMsg.toString());
    }

    // 根据class生成对应的Page
    Class<? extends Fragment> target = matchedPages.get(0);
    return cls2Page(target, intent, businessContext);
  }

  private void fullMatch(Context context, Intent intent,
      ArrayList<Class<? extends Fragment>> pageLists) {
    // 全方位匹配
    String action = intent.getAction();
    String type = intent.resolveTypeIfNeeded(context.getApplicationContext().getContentResolver());
    String scheme = intent.getScheme();
    Uri data = intent.getData();
    Set categories = intent.getCategories();

    synchronized (fragments) {
      Set<IntentFilter> filterSet = fragments.keySet();
      for (IntentFilter filter : filterSet) {
        int result = filter.match(action, type, scheme, data, categories, "");
        if (result <= 0) {
          continue;
        }
        pageLists.add(fragments.get(filter));
      }
    }
  }

  /**
   * @param pageClass
   * @param intent
   * @return
   */
  private Fragment cls2Page(Class<? extends Fragment> pageClass, Intent intent,
      IBusinessContext businessContext) {
    Fragment page = null;

    if (page == null) {
      try {
        // 实例化Page 并且传递bundle
        page = pageClass.newInstance();
      } catch (InstantiationException ex) {
        ex.printStackTrace();
      } catch (IllegalAccessException ex) {
        ex.printStackTrace();
      }
    }

    Bundle bundle = intent.getExtras();

    // 无论如何保证 page.getArguments() 不为 null
    bundle = bundle == null ? new Bundle() : bundle;
    if (page != null) {
      if (page instanceof IComponent) {
        IComponent component = (IComponent) page;
        component.setBusinessContext(businessContext);
      }
      bundle.putSerializable("BusinessContext", businessContext);
      Bundle arguments = page.getArguments();
      // 如果设置过arguments了 则append上intent的bundle
      if (arguments != null) {
        arguments.putAll(bundle);
      } else {
        page.setArguments(bundle);
      }
    }

    return page;
  }

  private void registerClasses(IntentFilter filter, Class<? extends Fragment> clazz) {
    synchronized (fragments) {
      fragments.put(filter, clazz);
    }
  }

  private void loadFragments() {
    loadDelegateClass(Fragment.class, new IDelegateListener<Class<? extends Fragment>>() {
      @Override
      public void onDelegate(String id, Class<? extends Fragment> clazz) {
        IntentFilter filter = new IntentFilter();
        filter.addDataScheme("OneFramework");
        filter.addDataAuthority(id, null);
        filter.addDataPath("/entrance", PatternMatcher.PATTERN_LITERAL);
        registerClasses(filter, clazz);
      }
    });
  }
}
