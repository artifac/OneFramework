package com.one.framework.app.slide;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import com.one.framework.R;
import com.one.framework.adapter.ContactAdapter;
import com.one.framework.app.base.BaseFragment;
import com.one.framework.app.slide.presenter.ContactPresenter;
import com.one.framework.app.widget.EmptyView;
import com.one.framework.app.widget.PullListView;
import com.one.framework.app.widget.PullScrollRelativeLayout;
import com.one.framework.app.widget.TripButton;
import com.one.framework.app.widget.base.IItemClickListener;
import com.one.framework.app.widget.base.ITopTitleView.ClickPosition;
import com.one.framework.dialog.SupportDialogFragment;
import com.one.framework.log.Logger;
import com.one.framework.model.ContactModel;
import com.one.framework.utils.SystemUtils;
import com.one.framework.utils.ToastUtils;
import com.one.framework.utils.UIUtils;
import java.util.ArrayList;
import java.util.List;

public class AddEmergentFragment extends BaseFragment implements View.OnClickListener,
    IEmergentView, IItemClickListener {

  public static final String EMERGENT_ID = "emergentId";
  public static final String EMERGENT_NAME = "emergentName";
  public static final String EMERGENT_PHONE = "emergentPhone";

  private PullScrollRelativeLayout mScrollView;
  private PullListView mListView;
  private EmptyView mEmptyView;

  private ContactAdapter mAdapter;

  private final int READ_CONTACT = 0x110;

  private ContactPresenter mContactPresenter;

  private EditWatcher mWatcher;

  private EditText mName;
  private EditText mPhone;
  private TextView mContactList;
  private TripButton mSave;

  private long mEmergentId;
  private String mEmergentName;
  private String mEmergentPhone;

  private SupportDialogFragment mDelDlg;

  private int leftMargin;
  private int bottomMargin;

  private int mRequestCode;

  private List<ContactModel> mContacts = new ArrayList<>();
  private List<ContactModel> mCurSelected = new ArrayList<>();

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mContactPresenter = new ContactPresenter(this);
    mWatcher = new EditWatcher();
  }

  @Override
  protected View onCreateViewImpl(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.one_add_emergent_contact_layout, null);
    initView(view);
    return view;
  }

  private void initView(View view) {
    mName = view.findViewById(R.id.one_emergent_name);
    mContactList = view.findViewById(R.id.one_add);
    mPhone = view.findViewById(R.id.one_emergent_phone);
    mSave = view.findViewById(R.id.one_add_emergent_save);

    mContactList.setOnClickListener(this);
    mSave.setOnClickListener(this);

    mTopbarView.setTitleBarBackground(Color.WHITE);
    mTopbarView.hideRightImage(true);
    if (parseBundle()) {
      mTopbarView.setTitle(R.string.one_update_emergent);
      mTopbarView.setTitleRight(R.string.one_delete_emergent, Color.parseColor("#fe173a"));
    } else {
      mTopbarView.setTitle(R.string.one_add_emergent);
    }

    mSave.setRippleColor(Color.parseColor("#f3f3f3"), Color.parseColor("#f3f3f3"));
    mSave.setEnabled(false);

    mName.addTextChangedListener(mWatcher);
    mPhone.addTextChangedListener(mWatcher);

    mName.setText(mEmergentName);
    mPhone.setText(mEmergentPhone);
    leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
    bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());

    mScrollView = view.findViewById(R.id.one_contact_root);
    mListView = view.findViewById(android.R.id.list);
    mEmptyView = view.findViewById(R.id.one_contact_empty_view);

    mScrollView.setMoveListener(mListView);
    mScrollView.setScrollView(mListView);

    mAdapter = new ContactAdapter(getContext());

    mListView.setEmptyView(mEmptyView);
    mAdapter.setListData(mContacts);
    mListView.setAdapter(mAdapter);

    mEmptyView.setImgRes(R.drawable.one_contact_empty);
    mEmptyView.setTxtRes(R.string.one_emergent_empty);
    mListView.setItemClickListener(this);
  }

  private boolean parseBundle() {
    Bundle bundle = getArguments();
    mRequestCode = bundle.getInt(REQUEST_CODE);
    mEmergentId = bundle.getLong(EMERGENT_ID);
    mEmergentName = bundle.getString(EMERGENT_NAME);
    mEmergentPhone = bundle.getString(EMERGENT_PHONE);
    return !TextUtils.isEmpty(mEmergentName) && !TextUtils.isEmpty(mEmergentPhone);
  }

  @Override
  public void onTitleItemClick(ClickPosition position) {
    switch (position) {
      case LEFT: {
        if (mScrollView.getVisibility() == View.VISIBLE) {
          mTopbarView.setLeft(R.drawable.one_top_bar_back_selector);
          mTopbarView.setSamePageBack(false);
          showContactView(false);
        } else {
          super.onTitleItemClick(position);
        }
        break;
      }
      case RIGHT: {
        showDelEmergent();
        break;
      }
    }
  }

  private void showDelEmergent() {
    if (mDelDlg != null && mDelDlg.isVisible()) {
      return;
    }
    final SupportDialogFragment.Builder builder = new SupportDialogFragment.Builder(getContext())
        .setTitle(getString(R.string.one_del_emergent_title))
        .setMessage(getString(R.string.one_del_emergent_msg))
        .setPositiveButton(getString(R.string.one_del_emergent_confirm), v -> {
          mDelDlg.dismiss();
          mContactPresenter.deleteContact(mEmergentId);
        })
        .setPositiveBackgroundMargin(leftMargin, 0, leftMargin, bottomMargin)
        .setPositiveButtonTextColor(Color.parseColor("#ffffff"))
        .setPositiveButtonBackground(R.drawable.one_dlg_btn_bg)
        .setVisibleClose(true);
    mDelDlg = builder.create();
    mDelDlg.show(getFragmentManager(), "");
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();
    ContactModel model = new ContactModel();
    model.setName(mName.getText().toString());
    model.setPhoneNumber(mPhone.getText().toString());
    if (id == R.id.one_add_emergent_save) {
      if (mRequestCode == 1) {
        if (UIUtils.isMobileNO(mPhone.getText().toString())) {
          // 保存添加联系人
          mContactPresenter.addContact(model);
        } else {
          mPhone.requestFocus();
          ToastUtils.toast(getActivity(), getString(R.string.one_login_input_right_phone));
        }
      } else {
        model.setContactId(mEmergentId);
        mContactPresenter.updateContact(model);
      }
    } else if (id == R.id.one_add) {
      // 通讯录
      checkPermission();
    }
  }

  @Override
  public void status(int code, long contactId) {
    // 添加联系人成功
    if (code == 0) {
      Bundle bundle = new Bundle();
      bundle.putLong(EMERGENT_ID, contactId);
      bundle.putString(EMERGENT_NAME, mName.getText().toString());
      bundle.putString(EMERGENT_PHONE, mPhone.getText().toString());
      bundle.putInt(REQUEST_CODE, mRequestCode);
      setArguments(bundle);
      setResult(0);
    }
  }

  @Override
  public void onError(int code, String message) {
    ToastUtils.toast(getActivity(), message);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == READ_CONTACT) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//判断是否给于权限
        fillContactList();
      } else {
        ToastUtils.toast(getContext(), "请开启权限");
      }
    }
  }

  /**
   * 访问通讯录
   */
  private void fillContactList() {
    showContactView(true);
    mTopbarView.setSamePageBack(true);
    mTopbarView.setLeft(R.drawable.one_time_picker_close);
    mContacts.addAll(getContacts());
    mAdapter.refreshData(mContacts);
    mAdapter.notifyDataSetChanged();
  }

  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, int position) {
    ContactModel contact = mAdapter.getItem(position);
    Logger.e("ldx", "ContactModel >>" + contact);
    mName.setText(contact.getName());
    mPhone.setText(contact.getPhoneNumber());

    showContactView(false);
    mTopbarView.setLeft(R.drawable.one_top_bar_back_selector);
    mTopbarView.setSamePageBack(false);
  }

  private void checkPermission() {
    //检测程序是否开启权限
    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) !=
        PackageManager.PERMISSION_GRANTED) {//没有权限需要动态获取
      //动态请求权限
      ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACT);
    } else {
      if (!mContacts.isEmpty()) {
        mContacts.clear();
      }
      fillContactList();
    }
  }


  //查询数据库中手机联系人信息的方法
  private List<ContactModel> getContacts() {
    List<ContactModel> list = new ArrayList<>();

    String[] colums = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER,
        ContactsContract.CommonDataKinds.Phone.CONTACT_ID};
    Cursor cursor = getActivity().getContentResolver()
        .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, colums, null, null, null);
    if (cursor != null) {
      while (cursor.moveToNext()) {
        //获取手机号码
        String phoneNum = cursor
            .getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        //当手机号为空或者没有该字段，跳过循环
        if (TextUtils.isEmpty(phoneNum)) {
          continue;
        }
        if (phoneNum.contains(" ")) {
          phoneNum = phoneNum.replaceAll(" ", "");
        }
        //获取联系人姓名：
        String name = cursor
            .getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
        //获取ID
        int id = cursor
            .getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
        list.add(new ContactModel(id, name, phoneNum, 0L));
      }
      cursor.close();
    }

    return list;
  }

  private class EditWatcher implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
      checkSaveStatus();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
  }

  private void checkSaveStatus() {
    if (!TextUtils.isEmpty(mName.getText().toString()) && !TextUtils
        .isEmpty(mPhone.getText().toString())) {
      mSave.setRippleColor(Color.parseColor("#1364ff"), Color.parseColor("#ffffff"));
      mSave.setEnabled(true);
    } else {
      mSave.setRippleColor(Color.parseColor("#1364ff"), Color.parseColor("#f3f3f3"));
      mSave.setEnabled(false);
    }
  }

  private void showContactView(final boolean show) {
    float from = show ? UIUtils.getScreenHeight(getContext()) : 0f;
    float to = show ? 0f : UIUtils.getScreenHeight(getContext());
    ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mScrollView, "translationY", from, to);
    objectAnimator.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationStart(Animator animation) {
        super.onAnimationStart(animation);
        if (show) {
          mScrollView.setVisibility(View.VISIBLE);
        }
      }

      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        if (!show) {
          mScrollView.setVisibility(View.GONE);
        }
      }
    });
    objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
    objectAnimator.setDuration(300);
    objectAnimator.start();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    SystemUtils.hideSoftKeyboard(getActivity());
  }
}
