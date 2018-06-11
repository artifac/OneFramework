package com.one.framework.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by ludexiang on 2018/6/11.
 */

public class DBContentProvider extends ContentProvider {
  public static final String AUTHORITY = "com.one.framework.contentprovider";
  private DBHelper mDBHelper;
  private static UriMatcher sUriMatcher;

  private static final int MATCH_ADDRESS = 1;

  static {
    sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    sUriMatcher.addURI(AUTHORITY, DBTables.AddressTable.TABLE_NAME, MATCH_ADDRESS);
  }

  /**
   * Database table select
   *
   * @param uri
   * @return table name
   */
  private String matchTableName(Uri uri) {
    if (uri == null) {
      return null;
    }
    switch (sUriMatcher.match(uri)) {
      case MATCH_ADDRESS:
        return DBTables.AddressTable.TABLE_NAME;
    }

    return "";
  }

  @Override
  public boolean onCreate() {
    mDBHelper = new DBHelper(getContext());
    return true;
  }

  @Nullable
  @Override
  public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
      @Nullable String[] selectionArgs, @Nullable String sortOrder) {
    SQLiteDatabase database = mDBHelper.getReadableDatabase();
    String tableName = matchTableName(uri);
    return database.query(tableName, projection, selection, selectionArgs, null, null, sortOrder);
  }

  @Nullable
  @Override
  public String getType(@NonNull Uri uri) {
    return null;
  }

  @Nullable
  @Override
  public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
    SQLiteDatabase database;
    long result;
    try {
      database = mDBHelper.getWritableDatabase();
    } catch (android.database.sqlite.SQLiteCantOpenDatabaseException e) {
      return null;
    }

    String tableName = matchTableName(uri);
    result = database.insert(tableName, null, values);
    return result >= 0 ? uri : null;
  }

  @Override
  public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
    SQLiteDatabase database;
    try {
      database = mDBHelper.getWritableDatabase();
    } catch (android.database.sqlite.SQLiteCantOpenDatabaseException e) {
      return 0;
    }

    String tableName = matchTableName(uri);
    return database.delete(tableName, selection, selectionArgs);
  }

  @Override
  public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
    SQLiteDatabase database;
    try {
      database = mDBHelper.getWritableDatabase();
    } catch (android.database.sqlite.SQLiteCantOpenDatabaseException e) {
      return 0;
    }

    String tableName = matchTableName(uri);
    return database.update(tableName, values, selection, selectionArgs);
  }

  /**
   * Clear this table and add new data
   *
   * @param table
   * @param db
   * @param values
   * @return
   */
  private int bulkInsert(String table, SQLiteDatabase db,
      ContentValues[] values) {
    int numValues = values.length;
    try {
      // start transaction
      db.beginTransaction();
      try {
        for (int i = 0; i < numValues; i++) {
          if (values[i] != null) {
            this.insert(values[i], table, db);
          }
        }
        // Marks the current transaction as successful
        db.setTransactionSuccessful();
      } finally {
        // Commit or rollback by flag of transaction
        db.endTransaction();
      }
    } catch (SQLException e) {
      return -1;
    }
    return numValues;
  }

  /**
   * Insert implementation
   *
   * @param values
   * @param table
   * @param db
   * @return If updateOrInsert fail, return -1
   */
  private long insert(ContentValues values, String table, SQLiteDatabase db) {
    try {
      return db.insert(table, null, values);
    } catch (SQLException e) {
      return -1;
    }
  }
}
