package com.one.framework.app.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import java.io.Serializable;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ludexiang on 2018/6/18.
 */

public class UserProfile {

  /**
   * preference name
   */
  private static final String FILE_PREFERENCE_ACCOUNT = "account";

  private static UserProfile sInstance;

  private final SharedPreferences mPreferences;
  /**
   * user token
   */

  private static final String TAG = "UserProfile";

  private ILogin iLogin;

  /**
   * key for token value of account
   */
  private static final String KEY_TOEKN_VALUE = "token_value";
  private static final String KEY_MOBILE_VALUE = "mobile_value";
  private static final String KEY_USERID_VALUE = "user_id_value";
  private static final String KEY_PUSH_KEY = "push_value";


  public static UserProfile getInstance(Context context, User user) {
    synchronized (UserProfile.class) {
      if (sInstance == null) {
        sInstance = new UserProfile(context.getApplicationContext(), user);
      }
    }
    return sInstance;
  }

  /**
   * get instance of {@linkplain UserProfile}
   *
   * @param context {@link Context}
   * @return instance of {@linkplain UserProfile}
   */
  public static synchronized UserProfile getInstance(Context context) {
    if (sInstance == null) {
      sInstance = new UserProfile(context.getApplicationContext(), null);
    }
    return sInstance;
  }


  public void saveToken(String token) {
    SharedPreferences.Editor editor = mPreferences.edit();
    editor.putString(KEY_TOEKN_VALUE, token);
    editor.commit();
  }

  public void setILogin(ILogin login) {
    iLogin = login;
  }

  public ILogin getLoginInterface() {
    return iLogin;
  }

  /**
   * Constructor
   *
   * @param context {@linkplain Context}
   * @param user {@linkplain User}
   */
  private UserProfile(Context context, User user) {
    mPreferences = context.getSharedPreferences(FILE_PREFERENCE_ACCOUNT, Context.MODE_PRIVATE);
    sync(user);
  }

  /**
   * @return user token
   */
  public String getTokenValue() {
    return mPreferences.getString(KEY_TOEKN_VALUE, "");
  }

  /**
   * @return mobile
   */
  public String getMobile() {
    return mPreferences.getString(KEY_MOBILE_VALUE, "");
  }

  /**
   *
   * @return
   */
  public String getUserId() {
    return mPreferences.getString(KEY_USERID_VALUE, "");
  }


  public String getPushKey() {
    return mPreferences.getString(KEY_PUSH_KEY, "");
  }

  public void savePushToken(String pushKey) {
    SharedPreferences.Editor editor = mPreferences.edit();
    editor.putString(KEY_PUSH_KEY, pushKey);
    editor.commit();
  }

  /**
   * @return had login?
   */
  public boolean isLogin() {
    return !TextUtils.isEmpty(getTokenValue());
  }

  /**
   * 退出登录
   */
  public void logout() {
    SharedPreferences.Editor editor = mPreferences.edit();
    editor.putString(KEY_USERID_VALUE, "");
    editor.putString(KEY_MOBILE_VALUE, "");
    editor.putString(KEY_TOEKN_VALUE, "");
    editor.commit();
  }


  /**
   * @param user sync the user info
   */
  public void sync(User user) {
    if (user == null) {
      return;
    }
    SharedPreferences.Editor editor = mPreferences.edit();
    editor.putString(KEY_USERID_VALUE, user.userId);
    editor.putString(KEY_MOBILE_VALUE, user.mobile);
    editor.putString(KEY_TOEKN_VALUE, user.tokenValue);
    editor.commit();
  }

  public class User implements Serializable {

    /**  */
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    private String mobile;

    private String userId;
    /**
     * the token
     */
    private String tokenValue;


    public User(String mobile, String userId, String token) {
      this.mobile = mobile;
      this.userId = userId;
      this.tokenValue = token;
    }

    @Override
    public String toString() {
      JSONObject json = new JSONObject();
      try {
        json.put("mobile_value", mobile);
        json.put("user_vale", userId);
        json.put("token_value", tokenValue);
      } catch (JSONException e) {
        e.printStackTrace();
      }
      return json.toString();
    }
  }
}
