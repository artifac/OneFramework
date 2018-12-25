package com.one.framework.utils;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.webkit.URLUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WebpageQueryHelper {

  private static boolean isHostQualified(@NonNull Uri uri) {
    // taxi must be fix m  / o/ b
    return !TextUtils.isEmpty(uri.getHost()) &&
        (uri.getHost().endsWith("taxi.com") || uri.getHost().endsWith("taxi.io")
            || uri.getHost().endsWith("mobike.com") || uri.getHost().endsWith("mobike.io"));
  }


  public static Uri append(Uri uri, Map<String, String> map) {
    if (uri != null && isHostQualified(uri)) {
      final Uri.Builder builder = uri.buildUpon().clearQuery();
      final List<String> queryList = new ArrayList<>();
      doAppend(builder, map);
      for (String query : uri.getQueryParameterNames()) {
        if (!queryList.contains(query)) {
          builder.appendQueryParameter(query, uri.getQueryParameter(query));
        }
      }
      return builder.build();
    } else {
      return uri;
    }
  }

  public static String append(String url, Map<String, String> map) {
    if (URLUtil.isNetworkUrl(url) && map != null && map.size() > 0) {
      return append(Uri.parse(url), map).toString();
    } else {
      return url;
    }
  }

  private static void doAppend(@NonNull Uri.Builder uri, Map<String, String> map) {
    for (Map.Entry<String, String> entry : map.entrySet()) {
      uri.appendQueryParameter(entry.getKey(), entry.getValue());
    }
  }
}
