package com.one.framework.app.web.jsbridge.functions.image;

import android.content.Context;
import android.content.Intent;
import com.one.framework.app.web.BaseWebView;
import com.one.framework.app.web.jsbridge.JavascriptBridge.Function;
import java.lang.ref.WeakReference;
import org.json.JSONObject;

public class FuncImageLiteratureReviewPhotoLibrary extends Function {

  private WeakReference<BaseWebView> webVieReference;

  public FuncImageLiteratureReviewPhotoLibrary(WeakReference<BaseWebView> webVieReference) {
    this.webVieReference = webVieReference;
  }

  @Override
  public JSONObject execute(JSONObject jsonObject) {
    final BaseWebView webView = webVieReference != null ? webVieReference.get() : null;
    final Context context = webView != null ? webView.getContext() : null;
    if (jsonObject == null || webView == null || context == null) {
      return null;
    }
    final String callback = jsonObject.optString("callback");
    Intent intent = new Intent(context, PicUploadActivity.class);
    intent.putExtra(PicUploadActivity.UPLOAD_URL_KEY, jsonObject.optString("url"));
    intent.putExtra(PicUploadActivity.DATA_PARAMS_KEY, jsonObject.toString());
    intent.putExtra("width", jsonObject.optString("outputWidth"));
    intent.putExtra("height", jsonObject.optString("outputHeight"));
    intent.putExtra("type", "photo");

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
