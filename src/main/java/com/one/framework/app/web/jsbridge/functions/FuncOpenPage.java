package com.one.framework.app.web.jsbridge.functions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import com.one.framework.app.web.BaseWebView;
import com.one.framework.app.web.WebActivity;
import com.one.framework.app.web.WebViewModel;
import com.one.framework.app.web.jsbridge.JavascriptBridge;
import org.json.JSONObject;

public class FuncOpenPage extends JavascriptBridge.Function {

  public static final String TAG = "FuncOpenPage";
  private Context mContext;
  private BaseWebView webView;

  public FuncOpenPage(Context context, BaseWebView webView) {
    this.webView = webView;
    this.mContext = context;
  }

  @Override
  public JSONObject execute(JSONObject params) {
    String target = params.optString("target", "");
    String url = params.optString("url", "");
    String action = params.optString("action", "");
    switch (target) {
      case "self":
        webView.loadUrl(url);
        break;
      case "blank":
        openNewPage(mContext, params);
        break;
      case "native":
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));
        if (!(mContext instanceof Activity)) {
          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        mContext.startActivity(intent);
        break;
      case "order":
        Uri uri = Uri.parse(url);
        final String orderId = uri.getQueryParameter("orderId");
        final String businessId = uri.getQueryParameter("sid");
        if (TextUtils.isEmpty(businessId)) {
        } else {
          Intent requestIntent = new Intent();
          requestIntent.setAction("com.one.trip.ON_THE_WAY");
          requestIntent.setData(Uri.parse("oneFramework://" + businessId + "/ontheway"));
          requestIntent.putExtra("orderId", orderId);
          LocalBroadcastManager.getInstance(mContext).sendBroadcast(requestIntent);
        }
        break;
    }

    return new JSONObject();
  }

  private void openNewPage(Context context, JSONObject params) {
    String url = params.optString("url");
    String title = params.optString("title");
    WebViewModel model = new WebViewModel();
    model.url = url;
    model.title = title;
    Intent intent = new Intent(context, WebActivity.class);
    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }
    intent.putExtra(WebActivity.KEY_WEB_VIEW_MODEL, model);
    context.startActivity(intent);
  }
}
