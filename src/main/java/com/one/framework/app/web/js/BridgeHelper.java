package com.one.framework.app.web.js;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Pair;
import android.webkit.WebView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BridgeHelper {

  private static final String TAG = "BridgeHelper";
  static final String FUSION_SCHEMA = "fusion://";
  static final String BLANK = "";
  static final String BACKSLASH = "/";
  public static final String FUSION_CALL_NATIVE = "fusion://invokeNative";
  public static final String FUSION_JS_RETURN = "fusion://return";
  public static final String CALLBACK_ID_FORMAT = "JAVA_CB_%s";
  public static final String CALL_TO_JS = "javascript:Fusion.invokeJSMethod('%s');";
  public static final String CALLBACK_TO_JS = "javascript:Fusion.callbackJS('%s', %s);";
  public static final String ASSET_FUSION_JS = "fusion/fusion.js";
  public static final String OMEGA_EVENT_BRIDGE = "tone_p_x_fusion_jsbridge";
  public static final String OMEGA_EVENT_CACHE = "tone_p_x_fusion_cache";
  public static final String ERROR_CODE_NO_MODULE = "403";
  public static final String ERROR_CODE_NO_INTERFACE = "400";
  public static final String ERROR_CODE_INVALID_ARGUMENTS = "401";
  public static final String ERROR_CODE_CALLBACK = "402";

  public BridgeHelper() {
  }

  public static Pair<String, String> extractNamespaceAndMethod(String url) {
    String temp = url.replace("fusion://invokeNative", "");
    String[] namespaceAndMethod = temp.split("/");
    return namespaceAndMethod.length >= 2 ? new Pair(namespaceAndMethod[0], namespaceAndMethod[1])
        : null;
  }

  public static InvokeMessage parseInvokeMessage(String url) {
    if (TextUtils.isEmpty(url)) {
      return null;
    } else {
      Uri uri = Uri.parse(url);
      InvokeMessage message = new InvokeMessage();
      message.setModuleName(uri.getQueryParameter("module"));
      message.setFunctionName(uri.getQueryParameter("method"));
      message.setArgs(uri.getQueryParameter("arguments"));
      return message;
    }
  }

  public static void webViewLoadLocalJs(WebView view) {
    File latestFusionJs = null;//HybridResourceManager.getsInstance(view.getContext()).getFusionJsFile();
    String jsContent;
    if (latestFusionJs != null) {
      jsContent = normalFileToString(latestFusionJs);
    } else {
      jsContent = assetFileToString(view.getContext(), "fusion/fusion.js");
    }

//    view.loadUrl("javascript:" + jsContent);
  }

  public static String normalFileToString(File fusionJs) {
    FileInputStream inputStream = null;

    try {
      inputStream = new FileInputStream(fusionJs);
    } catch (FileNotFoundException var3) {
      var3.printStackTrace();
    }

    return inputStreamToString(inputStream);
  }

  public static String assetFileToString(Context c, String urlStr) {
    InputStream in = null;

    try {
      in = c.getAssets().open(urlStr);
    } catch (IOException var4) {
      var4.printStackTrace();
    }

    return inputStreamToString(in);
  }

  private static String inputStreamToString(InputStream in) {
    if (in == null) {
      return "";
    } else {
      try {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        String line = null;
        StringBuilder sb = new StringBuilder();

        do {
          line = bufferedReader.readLine();
          if (line != null && !line.matches("^\\s*\\/\\/.*")) {
            sb.append(line);
          }
        } while (line != null);

        bufferedReader.close();
        in.close();
        String var4 = sb.toString();
        return var4;
      } catch (Exception var14) {
        var14.printStackTrace();
      } finally {
        if (in != null) {
          try {
            in.close();
          } catch (IOException var13) {
          }
        }

      }

      return null;
    }
  }
}
