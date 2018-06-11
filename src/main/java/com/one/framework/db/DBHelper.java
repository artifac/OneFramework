package com.one.framework.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ludexiang on 2018/6/11.
 */

public class DBHelper extends SQLiteOpenHelper {
  private static final String DB_NAME = "ONE_DATABASE";
  private static final int DB_VERSION = 1;

  public DBHelper(Context context) {
    super(context, DB_NAME, null, DB_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    createAddress(db);
  }

  /**
   * 地址表
   *
   * @param db
   */
  private void createAddress(SQLiteDatabase db) {
    StringBuilder builder = new StringBuilder();
    String sql = builder.append("create table IF NOT EXISTS ").append(DBTables.AddressTable.TABLE_NAME).append("(")
        .append(DBTables.AddressTable.DISPLAYNAME).append(" varchar(80),") //用于展示的名称
        .append(DBTables.AddressTable.ADDRESS).append(" varchar(80),") // 详细地址
        .append(DBTables.AddressTable.KEY).append(" varchar(90),")
        .append(DBTables.AddressTable.CITY_NAME).append(" varchar(50),")
        .append(DBTables.AddressTable.CITY_ID).append(" varchar(10),")
        .append(DBTables.AddressTable.LAT).append(" double,")
        .append(DBTables.AddressTable.LNG).append(" double,")
        .append(DBTables.AddressTable.TYPE).append(" int,")
        .append(DBTables.AddressTable.NAME).append(" varchar(80),")
        .append(DBTables.AddressTable.COTYPE).append(" int,")
        .append(DBTables.AddressTable.TIMESTAMP).append(" long,")
        .append(DBTables.AddressTable.SRC_TAG).append(" varchar(80),")
        .append(DBTables.AddressTable.UID).append(" varchar(100))").toString();
    db.execSQL(sql);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
  }

}
