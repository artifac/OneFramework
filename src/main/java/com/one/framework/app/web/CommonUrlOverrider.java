package com.one.framework.app.web;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;

/**
 * 通用URL截获处理
 */
public class CommonUrlOverrider implements OverrideUrlLoader {

  private final static String DRIVER_DOWNLOAD = "https://static.driver.com/gulfstream/webapp/pages/download-page/download.html?type=0";

  @Override
  public boolean shouldOverrideUrlLoading(WebView view, String url) {
    try {
      if (url.startsWith("tel:")) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DIAL); // android.intent.action.DIAL
        intent.setData(Uri.parse(url));
        view.getContext().startActivity(intent);
        return true;
      } else if (url.startsWith("sms:")) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        view.getContext().startActivity(intent);
        return true;
      } else if (url.startsWith("weixin:")) {
        //网页有微信支付，此时的链接为weixin:开头的，需单独处理
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        view.getContext().startActivity(intent);
        return true;
      } else if (url.startsWith("alipays:")) {
        //网页有支付宝支付，此时的链接为alipays:开头的，需单独处理
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        view.getContext().startActivity(intent);
        return true;
      } /*else if (url.startsWith(SchemeDispatcher.SCHEME_DIDITRIPCARD)) {
        //出行卡运营页跳转到钱包界面的充值页
        Intent intent = new Intent();
        intent.setClass(view.getContext(), SchemeDispatcher.class);
        intent.setData(Uri.parse(url));
        view.getContext().startActivity(intent);
        return true;
      }*/ else if (url.startsWith("travel:")) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        view.getContext().startActivity(intent);
        return true;
      } else if (url.contains("passenger:")) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        view.getContext().startActivity(intent);
        return true;
      } /*else if (url.toLowerCase().startsWith(SchemeDispatcher.SCHEME_ONE_TRAVEL.toLowerCase())) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        view.getContext().startActivity(intent);
        return true;
      } */else if (url.startsWith("mailto:")) {
        Intent mailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
        view.getContext().startActivity(mailIntent);
        return true;
      } else if (url.startsWith("driver:")) {
        try {
          Uri uri = Uri.parse(url);
          Intent intent = new Intent(Intent.ACTION_VIEW, uri);
          view.getContext().startActivity(intent);
        } catch (Exception e) {
          //没有安装司机端
          Intent intent = new Intent(view.getContext(), WebActivity.class);
          intent.putExtra(WebActivity.KEY_URL, DRIVER_DOWNLOAD);
          view.getContext().startActivity(intent);
        }
        return true;
      }
    } catch (Exception e) {
      Log.d("CommonUrlOverrider", "CommonUrlOverrider startacivity error");
    }

    return false;
  }
}
