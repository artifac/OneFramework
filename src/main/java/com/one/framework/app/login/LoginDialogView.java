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
    if (count >= 13) {
      mLoginNext.setEnabled(true);
      mLoginNext.setRippleColor(Color.parseColor("#343d4a"), Color.WHITE);
    } else {
      mLoginNext.setEnabled(false);
      mLoginNext.setRippleColor(Color.parseColor("#d3d3d3"), Color.parseColor("#d3d3d3"));
    }
  }

  final class EditWatcher implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
      if (s == null || s.length() == 0) {
        return;
      }
      checkPhoneCount(s.length());
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < s.length(); i++) {
        if (i != 3 && i != 8 && s.charAt(i) == ' ') {
          continue;
        } else {
          sb.append(s.charAt(i));
          if ((sb.length() == 4 || sb.length() == 9)
              && sb.charAt(sb.length() - 1) != ' ') {
            sb.insert(sb.length() - 1, ' ');
          }
        }
      }
      if (!sb.toString().equals(s.toString())) {
        int index = start + 1;
        if (sb.charAt(start) == ' ') {
          if (before == 0) {
            index++;
          } else {
            index--;
          }
        } else {
          if (before == 1) {
            index--;
          }
        }
        String phone = sb.toString();
        mLoginInput.setText(phone);
        mLoginInput.setSelection(index);
      }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
  }
}
