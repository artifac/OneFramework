package com.one.framework.app.web.plugin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.one.framework.app.web.plugin.model.WebActivityParamsModel;
import com.one.framework.log.Logger;

/**
 * web插件基础类
 * 使用方法： 使用注解WebConfig来指定你声明的WebPlugin的主题（topic）  并且让你的类继承自该类即可。
 * Created by huangqichan on 2015/9/28.
 */
public class BaseWebPlugin implements WebPlugin {

  @Override
  public void onCreate(WebActivityParamsModel webActivityParamsModel) {
  }

  @Override
  public void onStart() {
    Logger.e("ldx", "----->onStart");
  }

  @Override
  public void onReStart() {
    Logger.e("ldx", "----->onReStart");
  }

  @Override
  public void onResume() {
    Logger.e("ldx", "----->onResume");
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    Logger.e("ldx", "----->onSaveInstanceState");
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    Logger.e("ldx", "----->onActivityResult");
  }

  @Override
  public void onPause() {
    Logger.e("ldx", "----->onPause");
  }

  @Override
  public void onStop() {
    Logger.e("ldx", "----->onStop");
  }

  @Override
  public void onDestroy() {
    Logger.e("ldx", "----->onDestroy");
  }

  protected void showErrorDialog(Context context, String content, String btnText) {
  }


  /**
   * 显示加载的对话框
   */
  protected void showLoadingDialog(Context context, int msgId) {
  }
}
