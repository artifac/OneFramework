package com.one.framework.app.web.tool;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Pair;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WebURLWriter {

  /**
   * 添加系统信息
   */
  public static String combineBaseWebInfo(Context context) {
    Map<String, String> paramMap = constructParamMap(context);
    List<Pair<String, String>> paramList = paramMapToSortedPairList(paramMap);
    return pairListToParamString(paramList);
  }


  public static List<Pair<String, String>> combineBaseWebInfoAsPairList(Context context) {
    Map<String, String> paramMap = constructParamMap(context);
    List<Pair<String, String>> paramList = paramMapToSortedPairList(paramMap);
    return paramList;
  }


  private static Map<String, String> constructParamMap(Context context) {
    HashMap<String, String> param = new HashMap<>();
    return param;
  }


  private static List<Pair<String, String>> paramMapToSortedPairList(Map<String, String> urlParam) {
    List<Pair<String, String>> list = getParamList(urlParam);
    Collections.sort(list, new KVPairComparator());
    return list;
  }


  public static List<Pair<String, String>> queryStringToPairList(String encodedQuery) {
    LinkedList list = new LinkedList();
    if (TextUtils.isEmpty(encodedQuery)) {
      return list;
    }

    String[] queryElements = encodedQuery.split("&");
    for (String element : queryElements) {
      if (TextUtils.isEmpty(element)) {
        continue;
      }
      String[] queryPair = element.split("=");
      if (queryPair.length != 2) {
        continue;
      }
      list.add(new Pair(queryPair[0], queryPair[1]));
    }

    return list;
  }


  private static String pairListToParamString(List<Pair<String, String>> paramList) {
    if (paramList == null || paramList.size() == 0) {
      return "";
    }
    StringBuilder sb = new StringBuilder();
    String key;
    String value;
    for (Pair<String, String> basicNameValuePair : paramList) {
      key = basicNameValuePair.first;
      value = basicNameValuePair.second;
      if (sb.length() > 0) {
        sb.append("&");
      }
      sb.append(key).append("=").append(value);
    }
    return sb.toString();
  }

  /**
   * 获取通过Map参数获取url（排序）
   */
//    public static String getSortedUrlParamsString(Map<String, String> urlParam) {
//        List<Pair<String, String>> list = getParamList(urlParam);
//        Collections.sort(list, new KVPairComparator());
//        StringBuilder sb = new StringBuilder();
//        String key;
//        String value;
//        for (Pair<String, String> basicNameValuePair : list) {
//            if (basicNameValuePair.first.startsWith(ServerParam.PREFIX_X))
//                continue;
//            key = basicNameValuePair.first;
//            key = TextUtil.encode(key);
//            value = basicNameValuePair.second;
//            if (TextUtils.isEmpty(value)) {
//                continue;
//            }
//            if (sb.length() > 0) {
//                sb.append("&");
//            }
//            sb.append(key);
//            sb.append("=");
//            sb.append(value);
//        }
//        return sb.toString();
//    }
  public static Uri replaceUriParameter(Uri originUri, String key, String newValue) {
    final Set<String> params = originUri.getQueryParameterNames();
    final Uri.Builder builder = originUri.buildUpon().clearQuery();
    for (String param : params) {
      String value;
      if (param.equals(key)) {
        value = newValue;
      } else {
        value = originUri.getQueryParameter(param);
      }

      builder.appendQueryParameter(param, value);
    }

    return builder.build();
  }


  public static Uri appendUriQuery(Uri originUri, List<Pair<String, String>> queryList) {
    if (queryList == null) {
      return originUri;
    }
    final Uri.Builder builder = originUri.buildUpon();

    //原来的可能encode
    String oldEncodedQuery = originUri.getEncodedQuery();
    StringBuilder query = new StringBuilder();

    //新添加的之前被encode了
    for (Pair<String, String> queryElement : queryList) {
      //builder.appendQueryParameter(queryElement.first, queryElement.second);
      if (0 != query.length()) {
        query.append("&");
      }
      query.append(queryElement.first);
      query.append("=");
      query.append(queryElement.second);
    }
    if (null == oldEncodedQuery || oldEncodedQuery.isEmpty()) {
      builder.encodedQuery(query.toString());
    } else {
      query.append("&");
      query.append(oldEncodedQuery);
      builder.encodedQuery(query.toString());
    }
    return builder.build();
  }


  public static Uri appendEncodedUriQuery(Uri originUri, String queryString) {
    if (TextUtils.isEmpty(queryString)) {
      return originUri;
    }
    String newQuery;
    String originQuery = originUri.getEncodedQuery();
    if (TextUtils.isEmpty(originQuery)) {
      if (queryString.startsWith("&")) {
        newQuery = queryString.substring(1);
      } else {
        newQuery = queryString;
      }
    } else {
      if (queryString.startsWith("&")) {
        newQuery = originQuery + queryString;
      } else {
        newQuery = originQuery + "&" + queryString;
      }
    }
    return originUri.buildUpon().clearQuery().encodedQuery(newQuery).build();
  }


  private static List<Pair<String, String>> getParamList(Map<String, String> urlParam) {
    LinkedList list = new LinkedList();
    if (urlParam == null || urlParam.size() == 0) {
      return list;
    }
    for (Iterator<String> ite = urlParam.keySet().iterator(); ite.hasNext(); ) {
      String key = ite.next();
      if (TextUtils.isEmpty(key)) {
        continue;
      }
//      if (key.startsWith(ServerParam.PREFIX_X)) {
//        continue;
//      }
      String value = urlParam.get(key);
      if (TextUtils.isEmpty(value)) {
        continue;
      }
      list.add(new Pair(key, value));
    }
    return list;
  }


  static class KVPairComparator implements Comparator<Pair<String, String>> {

    public int compare(Pair<String, String> pairA, Pair<String, String> pairB) {
      return pairA.first.compareTo(pairB.first);
    }
  }
}
