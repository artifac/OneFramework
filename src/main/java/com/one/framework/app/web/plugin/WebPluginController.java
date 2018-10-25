package com.one.framework.app.web.plugin;

import android.content.Intent;
import android.os.Bundle;
import com.one.framework.app.web.WebActivity;
import com.one.framework.app.web.WebViewModel;
import com.one.framework.app.web.plugin.model.Plugin;
import com.one.framework.app.web.plugin.model.WebActivityParamsModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Web插件的生命周期控制类
 */
public class WebPluginController {

  private static final String TAG = WebPluginController.class.getName();

  private List<WebPlugin> webPlugins = new ArrayList<WebPlugin>();

  private WebActivityParamsModel mWebActivityParamsModel;

  private WebViewModel mWebViewModel;

  private Plugin plugin;

  public WebPluginController() {

  }

  public void initlize(WebActivityParamsModel webActivityParamsModel) {
    mWebActivityParamsModel = webActivityParamsModel;
    Intent intent = webActivityParamsModel.getGetIntent();
    if (intent != null && intent.hasExtra(WebActivity.KEY_WEB_VIEW_MODEL)) {
      mWebViewModel = (WebViewModel) intent.getSerializableExtra(WebActivity.KEY_WEB_VIEW_MODEL);
    }
    if (mWebViewModel == null) {
      return;
    }
    String topic = mWebViewModel.getTopic();
    initPlugin(topic);
  }

  private void initPlugin(String topic) {
//    if (!TextUtils.isEmpty(topic)) {
//      String plugins = WebPluginConfigStore.getInstance().loadConfig(topic);
//      if (TextUtils.isEmpty(plugins)) {
//        try {
//          Class cls = Class.forName(topic);
//          if (cls != null) {
//            plugin = new Plugin(topic, cls);
//          }
//        } catch (ClassNotFoundException e) {
//
//        }
//      } else {
//        String subPlugins = plugins.substring(WebPluginConfigStore.KEY_WEB_PLUGIN.length());
//        plugin = new Gson().fromJson(subPlugins, Plugin.class);
//      }
//    }
  }

  /**
   * 分发Create事件
   */
  public void dispatchCreate() {
    if (mWebActivityParamsModel == null) {
      throw new NullPointerException("请确定你已经做了初始化操作");
    }
    try {
      if (plugin == null) {
        return;
      }
      List<String> pluginClss = plugin.getClss();
      if (pluginClss == null || pluginClss.isEmpty()) {
        return;
      }
      for (String cls : pluginClss) {
        excuteCreate(cls);
      }
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  /**
   * 执行插件的OnCreate方法
   */
  private void excuteCreate(String clsName)
      throws IllegalAccessException, InstantiationException, ClassNotFoundException {
    Class cls = Class.forName(clsName);
    Object object = cls.newInstance();
    if (object instanceof WebPlugin) {
      WebPlugin plugin = (WebPlugin) object;
      webPlugins.add(plugin);
      plugin.onCreate(mWebActivityParamsModel);
    } else {
    }
  }

  public void dispatchStart() {
    startPlugin();
  }

  private void startPlugin() {
    for (WebPlugin webPlugin : webPlugins) {
      webPlugin.onStart();
    }
  }

  public void dispatchReStart() {
    reStartPlugin();
  }

  private void reStartPlugin() {
    for (WebPlugin webPlugin : webPlugins) {
      webPlugin.onReStart();
    }
  }

  public void dispatchResume() {
    resumePlugin();
  }

  private void resumePlugin() {
    for (WebPlugin webPlugin : webPlugins) {
      webPlugin.onResume();
    }
  }

  public void dispatchSaveInstanceState(Bundle outState) {
    saveInstance(outState);
  }

  private void saveInstance(Bundle saveInstance) {
    for (WebPlugin webPlugin : webPlugins) {
      webPlugin.onSaveInstanceState(saveInstance);
    }
  }

  public void dispatchActivityResult(int requestCode, int resultCode, Intent data) {
    activityResult(requestCode, resultCode, data);
  }

  private void activityResult(int requestCode, int resultCode, Intent data) {
    for (WebPlugin webPlugin : webPlugins) {
      webPlugin.onActivityResult(requestCode, resultCode, data);
    }
  }

  public void dispatchPause() {
    pausePlugin();
  }

  private void pausePlugin() {
    for (WebPlugin webPlugin : webPlugins) {
      webPlugin.onPause();
    }
  }

  public void dispatchStop() {
    stopPlugin();
  }

  private void stopPlugin() {
    for (WebPlugin webPlugin : webPlugins) {
      webPlugin.onStop();
    }
  }

  public void dispatchDestroy() {
    destroyPlugin();
  }

  private void destroyPlugin() {
    for (WebPlugin webPlugin : webPlugins) {
      webPlugin.onDestroy();
    }
  }

  public void release() {
    if (mWebViewModel != null) {
//      WebPluginConfigStore.getInstance().releaseConfig(mWebViewModel.getTopic());
    }
  }

}
