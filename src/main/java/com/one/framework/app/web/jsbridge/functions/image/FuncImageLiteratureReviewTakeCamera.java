package com.one.framework.app.web.jsbridge.functions.image;

import android.content.Context;
import android.content.Intent;
import com.one.framework.app.web.BaseWebView;
import com.one.framework.app.web.jsbridge.JavascriptBridge;
import java.lang.ref.WeakReference;
import org.json.JSONObject;

public class FuncImageLiteratureReviewTakeCamera extends JavascriptBridge.Function {

  private WeakReference<BaseWebView> webVieReference;

  public FuncImageLiteratureReviewTakeCamera(WeakReference<BaseWebView> webVieReference) {
    this.webVieReference = webVieReference;
  }

  @Override
  public JSONObject execute(JSONObject jsonObject) {
    final BaseWebView webView = webVieReference != null ? webVieReference.get() : null;
    final Context context = webView != null ? webView.getContext() : null;
    if (jsonObject == null || webView == null || context == null) {
      if (context != null) {
      }
      return null;
    }

    final String callback = jsonObject.optString("callback");
    Intent intent = new Intent(context, PicUploadActivity.class);
    intent.putExtra(PicUploadActivity.UPLOAD_URL_KEY, jsonObject.optString("url"));
    intent.putExtra(PicUploadActivity.DATA_PARAMS_KEY, jsonObject.toString());
    intent.putExtra("width", jsonObject.optString("outputWidth"));
    intent.putExtra("height", jsonObject.optString("outputHeight"));
    intent.putExtra("type", "camera");

    PicUploadActivity.setImageUploadCallback(new ImageUploadCallback() {
      @Override
      public void onSuccess(String msg) {
        String finalMsg = "{\"data\":" + msg + "}";
        webView.loadUrl("javascript:" + callback + "('" + finalMsg + "');");
      }
    });
    context.startActivity(intent);
    return null;
  }
}
