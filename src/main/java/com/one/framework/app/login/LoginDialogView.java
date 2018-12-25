package com.one.framework.app.login;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.one.framework.R;
import com.one.framework.app.widget.LoadingView;
import com.one.framework.app.widget.TripButton;
import com.one.framework.utils.SystemUtils;
import com.one.framework.utils.ToastUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ludexiang on 2018/6/15.
 */

public class LoginDialogView extends RelativeLayout {

  private ImageView mClose;
  private EditText mLoginInput;
  private TripButton mLoginNext;
  private LoadingView mLoginLoading;
  private EditWatcher mWatcher;

  public LoginDialogView(Context context) {
    this(context, null);
  }

  public LoginDialogView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public LoginDialogView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    mWatcher = new EditWatcher();
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    mClose = findViewById(R.id.one_login_dlg_close);
    mLoginInput = findViewById(R.id.one_login_input);
    mLoginNext = findViewById(R.id.one_login_next);
    mLoginLoading = findViewById(R.id.one_login_dlg_loading);

    mLoginInput.addTextChangedListener(mWatcher);

    SystemUtils.showSoftKeyboard(mLoginInput);

    checkPhoneCount(mLoginInput.getText().length());
  }

  private void checkPhoneCount(int count) {
    if (count >= 13 && checkPhone()) {
      mLoginNext.setEnabled(true);
      mLoginNext.setRippleColor(Color.parseColor("#343d4a"), Color.WHITE);
    } else {
      mLoginNext.setEnabled(false);
      mLoginNext.setRippleColor(Color.parseColor("#d3d3d3"), Color.parseColor("#d3d3d3"));
    }
  }

  private boolean checkPhone() {
    String phoneNumber = mLoginInput.getText().toString();
    String phone = phoneNumber.replaceAll(" ", "");
    if (!isChinaPhoneLegal(phone)) {
      ToastUtils.toast(getContext(), getContext().getString(R.string.one_login_input_right_phone));
      return false;
    }
    return true;
  }

  public static boolean isChinaPhoneLegal(String str) {
    String regExp = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
    Pattern p = Pattern.compile(regExp);
    Matcher m = p.matcher(str);
    return m.matches();
  }

  final class EditWatcher implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
//      if (s == null || s.length() == 0 || !checkS(s)) {
//        return;
//      }
      checkPhoneCount(s.length());
//      StringBuilder sb = new StringBuilder();
//      for (int i = 0; i < s.length(); i++) {
//        if (i != 3 && i != 8 && s.charAt(i) == ' ') {
//          continue;
//        } else {
//          sb.append(s.charAt(i));
//          if ((sb.length() == 4 || sb.length() == 9)
//              && sb.charAt(sb.length() - 1) != ' ') {
//            sb.insert(sb.length() - 1, ' ');
//          }
//        }
//      }
//      if (!sb.toString().equals(s.toString())) {
//        int index = start + 1;
//        if (start < sb.length() && sb.charAt(start) == ' ') {
//          if (before == 0) {
//            index++;
//          } else {
//            index--;
//          }
//        } else {
//          if (before == 1) {
//            index--;
//          }
//        }
//        String phone = sb.toString();
//        mLoginInput.setText(phone.trim());
//        if (phone.trim().length() > 0) {
//          mLoginInput.setSelection(index);
//        }
//      }
    }

    @Override
    public void afterTextChanged(Editable s) {
      //需求是130 1234 4567，中间第4个数字和第5个数字空格前面加空格
      StringBuffer sb = new StringBuffer(s);
      //StringBuffer.length()是长度，所以下标从1开始
      //字符数组第4位如果不是空格字符，就在他前面插一个空格字符
      if (s.length() >= 4) {
        char[] chars = s.toString().toCharArray();
        //数字下标是从0开始
        if (chars[3] != ' ') {
          sb.insert(3, ' ');
          setContent(sb);
        }
      }

      if (s.length() >= 9) {
        char[] chars = s.toString().toCharArray();
        //因为第4位加了一个空格，所以第8位数字，就是字符数组的第9位，下标是8。
        if (chars[8] != ' ') {
          sb.insert(8, ' ');
          setContent(sb);
        }
      }
    }

    /**
     * 添加或删除空格EditText的设置
     */
    private void setContent(StringBuffer sb) {
      if (sb != null) {
        mLoginInput.setText(sb.toString());
        //移动光标到最后面
        mLoginInput.setSelection(sb.length());
        checkPhoneCount(sb.length());
      }
    }

  }
}
