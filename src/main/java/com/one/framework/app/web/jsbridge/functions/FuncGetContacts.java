package com.one.framework.app.web.jsbridge.functions;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import com.one.framework.app.web.jsbridge.JavascriptBridge;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FuncGetContacts extends JavascriptBridge.Function {

  /**
   * 应用上下文
   */
  private Context mContext;
  /**
   * 　缓存的联系人列表
   */
  private String[] mContactsCache;

  public FuncGetContacts(Context context) {
    mContext = context;
  }

  @Override
  public JSONObject execute(JSONObject params) {
    JSONObject object = new JSONObject();
    try {
      int offset = params.optInt("offset");
      int size = params.optInt("size");

      String[] contacts;
      if (mContactsCache != null) {
        contacts = mContactsCache;
      } else {
        contacts = getContacts();
        mContactsCache = contacts;
      }

      offset = offset < 0 ? 0 : offset;
      offset = offset > contacts.length ? contacts.length : offset;

      size = offset + size > contacts.length ? contacts.length : offset + size;

      int length = size - offset;

      JSONArray ja = new JSONArray();
      while (length-- > 0) {
        ja.put(new JSONObject(contacts[offset++]));
      }

      object.put("total", contacts.length);
      object.put("datas", ja.toString());

    } catch (JSONException e) {
      e.printStackTrace();
    }

    return object;
  }

  /**
   * 获取联系人列表
   *
   * @return 联系人列表
   */
  private String[] getContacts() {
    String[] result = new String[]{};
    ContentResolver content = mContext.getContentResolver();
    String[] PHONES_PROJECTION = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER,
        ContactsContract.CommonDataKinds.Phone.PHOTO_ID};
    String PHONE_SORT_ORDER = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
    Cursor cur = content
        .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null, null,
            PHONE_SORT_ORDER);
    if (cur != null) {
      if (cur.getCount() > 0) {
        int i = 0;
        result = new String[cur.getCount()];
        while (cur.moveToNext()) {
          String phoneNumber = cur
              .getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
          String phoneName = cur
              .getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
          JSONObject obj = new JSONObject();
          try {
            obj.put(phoneName, phoneNumber.trim());
            result[i++] = obj.toString();
          } catch (JSONException e) {
            e.printStackTrace();
          }
        }
        cur.close();
      }
    }
    return result;
  }
}
