package com.one.framework.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.one.framework.db.DBTables.AddressTable;
import com.one.framework.db.DBTables.AddressTable.AddressType;
import com.one.framework.log.Logger;
import com.one.map.model.Address;
import com.one.map.model.LatLng;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ludexiang on 2018/6/11.
 */

public class DBUtil {

  /**
   * @param context
   * @param address
   * @param type 1 起点 2 终点 3 搜索历史 4 home 5 company
   */
  public static void insertDataToAddress(Context context, Address address, @AddressType int type) {
    ContentValues values = new ContentValues();
    values.put(AddressTable.DISPLAYNAME, address.mAdrDisplayName);
    values.put(AddressTable.ADDRESS, address.mAdrFullName);
    values.put(AddressTable.CITY_ID, address.mCityCode);
    values.put(AddressTable.CITY_NAME, address.mCity);
    values.put(AddressTable.LAT, address.mAdrLatLng.latitude);
    values.put(AddressTable.LNG, address.mAdrLatLng.longitude);
    values.put(AddressTable.TIMESTAMP, System.currentTimeMillis());
    values.put(AddressTable.TYPE, type);
    context.getContentResolver().insert(AddressTable.CONTENT_URI, values);
  }

  public static List<Address> queryDataFromAddress(Context context, @AddressType int type) {
    List<Address> searchResult = new ArrayList<>();
    Cursor cursor = context.getContentResolver().query(AddressTable.CONTENT_URI, null,
        AddressTable.TYPE + " = ?", new String[] {String.valueOf(type)}, AddressTable.TIMESTAMP);
    while (cursor != null && cursor.moveToNext()) {
      Address address = new Address();
      address.mAdrDisplayName = cursor.getString(cursor.getColumnIndexOrThrow(AddressTable.DISPLAYNAME));
      double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(AddressTable.LAT));
      double lng = cursor.getDouble(cursor.getColumnIndexOrThrow(AddressTable.LNG));
      address.mAdrLatLng = new LatLng(lat, lng);
      address.mAdrFullName = cursor.getString(cursor.getColumnIndexOrThrow(AddressTable.ADDRESS));
      address.mCityCode = cursor.getString(cursor.getColumnIndexOrThrow(AddressTable.CITY_ID));
      address.mCity = cursor.getString(cursor.getColumnIndexOrThrow(AddressTable.CITY_NAME));
      address.type = cursor.getInt(cursor.getColumnIndexOrThrow(AddressTable.TYPE));
      searchResult.add(address);
    }
    return searchResult;
  }

  public static int updateDataToAddress(Context context, Address address, @AddressType int type) {
    ContentValues values = new ContentValues();
    values.put(AddressTable.DISPLAYNAME, address.mAdrDisplayName);
    values.put(AddressTable.ADDRESS, address.mAdrFullName);
    values.put(AddressTable.CITY_ID, address.mCityCode);
    values.put(AddressTable.CITY_NAME, address.mCity);
    values.put(AddressTable.LAT, address.mAdrLatLng.latitude);
    values.put(AddressTable.LNG, address.mAdrLatLng.longitude);
    values.put(AddressTable.TIMESTAMP, System.currentTimeMillis());
    values.put(AddressTable.TYPE, type);
    return context.getContentResolver().update(AddressTable.CONTENT_URI, values, AddressTable.TYPE + " = ?", new String[]{ String.valueOf(type)});
  }

  /**
   * 清空表数据
   * @param context
   * @return
   */
  public static int deleteTables(Context context) {
    int deleteRow = context.getContentResolver().delete(AddressTable.CONTENT_URI, null, null);
    Logger.e("ldx", "deleteRow >>> " + deleteRow);
    return deleteRow;
  }
}
