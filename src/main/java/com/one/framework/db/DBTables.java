package com.one.framework.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.IntDef;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ludexiang on 2018/6/11.
 */

public class DBTables {

  public static final String BASE_URI = ContentResolver.SCHEME_CONTENT + "://" + DBContentProvider.AUTHORITY;

  public interface BaseAddressTable {
    String DISPLAYNAME = "displayname";
    String ADDRESS = "address";
    String CITY_ID = "city_id";
    String CITY_NAME = "city_name";
    String LNG = "lng";
    String LAT = "lat";
    String NAME = "name";
    String COTYPE = "cotype"; // 坐标类型
  }

  /**
   * 地址
   */
  public interface AddressTable extends BaseAddressTable {
    int START = 1;
    int END = 2;
    int SEARCH_HISTORY = 3;
    int HOME = 4;
    int COMPANY = 5;
    int SEARCH = 6;

    @Target({ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @IntDef({START, END, SEARCH_HISTORY, HOME, COMPANY, SEARCH})
    @interface AddressType {
    }
    String TABLE_NAME = "address";
    Uri CONTENT_URI = Uri.parse(BASE_URI + "/" + TABLE_NAME);

    String TYPE = "type"; // 0 起点 1 终点 2 搜索 3 home 4 company
    String TIMESTAMP = "timestamp";
    String KEY = "key";

    /**
     * srctag
     */
    String SRC_TAG = "srctag";

    /**
     * uid
     */
    String UID = "uid";
  }
}
