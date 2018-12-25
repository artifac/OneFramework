package com.one.framework.app.web;

import android.text.TextUtils;
import android.view.View;
import com.one.framework.app.web.plugin.annotation.WebConfig;
import com.one.framework.app.web.plugin.model.Plugin;
import com.one.framework.log.Logger;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WebViewModel implements Serializable {

  /**
   * 左侧菜单游戏
   */
  @Deprecated
  public static final String MENU_GAME = "menu_game";

  /**
   * 页面标题
   */
  public String title = "";
  /**
   * 页面 url
   */
  public String url = "";

  /**
   * 是否可以改变标题名称
   */
  public boolean canChangeWebViewTitle = true;

  /**
   * 是否支持缓存
   */
  public boolean isSupportCache = true;

  public boolean isPostBaseParams = true;

  public boolean isCommonModel = false;

  public boolean isSuportJs = true;

  /**
   * 是否支持语音sdk
   */
  public boolean isSuportSpeechJs = true;

  /**
   * 是否添加公共参数，针对端外开启h5
   */
  public boolean isAddCommonParam = false;

  /**
   * WebActivity需要加载的WebPlugin的主题
   * 不加载则为空
   * 参数不建议外部直接引用，请使用injectWebPlugin
   */
  public String topic = "";

  @Deprecated
  public String redirectClassName = WebActivity.class.getName();

  /**
   * 分享时根据来源不同，文案不同 [左侧菜单menu,等待接驾wait]
   */
  @Deprecated
  public String source;

  /**
   * 定义H5界面，右上角按钮文本，如果为空，则隐藏按钮
   */
  public int rightTextResId = -1;

  public int rightIconResId = 0;

  public String rightNextUrl = "";

//  public View.OnClickListener rightClickListener;

  /**
   * 商业变现参数
   */
  @Deprecated
  public String customparams = "";

  @Deprecated
  public boolean isFromBuiness = false;

  public boolean isFromPaypal = false;

  public String jsMethod;

  public String getTopic() {
    return topic;
  }

  /**
   * topic生成。 可以对应多个plugin（class）
   */
  public void injectWebPlugin(String topic, String classes) {
    List<String> cls = new ArrayList<>();
    cls.add(classes);
    injectWebPlugin(topic, cls);
  }

  /**
   * topic生成。 可以对应多个plugin（class）
   */
  public void injectWebPlugin(String topic, List<String> classes) {
    try {
      this.topic = topic;
      List<String> clss = new ArrayList<String>();
      for (int i = 0; i < classes.size(); i++) {
        Class cls = Class.forName(classes.get(i));
        WebConfig webConfig = (WebConfig) cls.getAnnotation(WebConfig.class);
        if (webConfig != null && TextUtils.equals(topic, webConfig.topic())) {
          clss.add(cls.getName());
        } else {
          Logger.e("ldx", " is not have webconfig anntation or webconfig is wrong!");
        }
      }
      Plugin pluginModel = new Plugin(topic, clss);
//      WebPluginConfigStore.getInstance().saveConfig(topic, new Gson().toJson(pluginModel));
    } catch (ClassNotFoundException e) {
      Logger.e("ldx", "pluginclass is not found!");
    }
  }

}
