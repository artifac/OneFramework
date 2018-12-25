package com.one.framework.app.slide;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.one.framework.R;
import com.one.framework.app.base.BaseFragment;
import com.one.framework.app.slide.IEmergentView.IContactView;
import com.one.framework.app.slide.presenter.ContactPresenter;
import com.one.framework.app.widget.ListItemWithRightArrowLayout;
import com.one.framework.app.widget.OptionsView;
import com.one.framework.app.widget.base.IListItemView.IClickCallback;
import com.one.framework.app.widget.base.IOptionView.IOptionChange;
import com.one.framework.dialog.BottomSheetDialog.ISelectResultListener;
import com.one.framework.dialog.TimePickerDialog;
import com.one.framework.log.Logger;
import com.one.framework.model.ContactModel;
import com.one.framework.provider.HomeDataProvider;
import com.one.framework.utils.PreferenceUtil;
import java.util.List;

/**
 * 联系人
 */
public class EmergentFragment extends BaseFragment implements IContactView,
    OnClickListener, IClickCallback, IOptionChange {

  private int requestAddCode = 0x001;
  private int requestUpdateCode = 0x002;

  private ContactPresenter mPresenter;

  private RelativeLayout mAddEmergentRoot;
  private OptionsView mAutoShareView;
  private ListItemWithRightArrowLayout mAutoTimeLayout;
  private LinearLayout mEmergentContactList;
  private int itemHeight;

  private String mFromTime = "23:00";
  private String mToTime = "05:00";

  private int normalColor = Color.parseColor("#999ba1");

  private int mFromPosition;
  private int mToPosition;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mPresenter = new ContactPresenter(this);
    itemHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60,
        getContext().getResources().getDisplayMetrics());
    mPresenter.autoShareInfo();
  }

  @Override
  protected View onCreateViewImpl(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.one_contact_layout, null);
    initView(view);
    return view;
  }

  private void initView(View view) {
    mAutoShareView = view.findViewById(R.id.emergent_auto_share_toggle);
    mAutoTimeLayout = view.findViewById(R.id.emergent_auto_time);
    mEmergentContactList = view.findViewById(R.id.emergent_add_root);
    mAddEmergentRoot = view.findViewById(R.id.one_add_emergent_root);

    mAutoShareView.setOptionChange(this);
    normalColor = mAutoShareView.getPosition() == 1 ? Color.parseColor("#02040d") : Color.parseColor("#999ba1");
    mAutoTimeLayout.setLeftImgVisible(false);
    mAutoTimeLayout.setLRMargin(0);
    mAutoTimeLayout.setItemTitle(getString(R.string.one_emergent_validate_time), 14, normalColor);
    mAutoTimeLayout.setRightTxt(mFromTime + " - " + mToTime, 14, normalColor);
    mTopbarView.setTitleBarBackground(Color.WHITE);
    mTopbarView.hideRightImage(true);
    mTopbarView.setTitleRight(0);
    mTopbarView.setTitle(R.string.one_settings_urgent_connect);
    mAddEmergentRoot.setOnClickListener(this);
    if (mAutoShareView.getPosition() == 0) { // off
      mAutoTimeLayout.setClickCallback(null);
    } else {
      mAutoTimeLayout.setClickCallback(this);
    }

    mPresenter.queryContact();
  }

  @Override
  public void onChange(String key, int position) {
    normalColor = position == 1 ? Color.parseColor("#02040d") : Color.parseColor("#999ba1");
    mAutoTimeLayout.setItemColor(normalColor);
    mAutoTimeLayout.setRightColor(normalColor);
    switch (position) {
      case 0: {
        // off
        mPresenter.autoShareDisable();
        mAutoTimeLayout.setClickCallback(null);
        break;
      }
      case 1: {
        // on
        showChooseTime();
        mAutoTimeLayout.setClickCallback(this);
        break;
      }
    }
    mAutoShareView.save();
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();
    if (id == R.id.one_add_emergent_root) {
      startFragment(null, requestAddCode);
    }
  }

  private void startFragment(Bundle args, int request) {
    forwardForResult(AddEmergentFragment.class, args, request);
  }

  @Override
  public void onFragmentForResult(int requestCode, Bundle args) {
    super.onFragmentForResult(requestCode, args);
    String name = args.getString(AddEmergentFragment.EMERGENT_NAME);
    String phone = args.getString(AddEmergentFragment.EMERGENT_PHONE);
    long contactId = args.getLong(AddEmergentFragment.EMERGENT_ID);
    if (requestCode == requestAddCode) {
      addContactView(name, phone, contactId);
    } else if (requestCode == requestUpdateCode) {
      updateContactView(name, phone, contactId);
    }
  }

  private void updateContactView(final String name, final String phone, final long contactId) {
    for (int i = 0; i < mEmergentContactList.getChildCount(); i++) {
      ListItemWithRightArrowLayout view = (ListItemWithRightArrowLayout) mEmergentContactList.getChildAt(i);
      if (((Long) view.getTag()).longValue() == contactId) {
        view.setItemTitle(name, 16, true, Color.parseColor("#02040d"));
        view.setRightTxt(phone, 14, Color.parseColor("#373c43"));
        view.setVisibility(View.VISIBLE);
        view.setLRMargin(0);
        break;
      } else {
        continue;
      }
    }
  }

  private void addContactView(final String name, final String phone, final long contactId) {
    if (isAdded() && !isDetached()) {
      mEmergentContactList.setVisibility(View.VISIBLE);
      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight);
      LayoutInflater inflater = LayoutInflater.from(getActivity());
      if (inflater == null) {
        return;
      }
      final ListItemWithRightArrowLayout view = (ListItemWithRightArrowLayout) inflater.inflate(R.layout.one_emergent_item_layout, null);
      view.setItemTitle(name, 16, true, Color.parseColor("#02040d"));
      view.setRightTxt(phone, 14, Color.parseColor("#373c43"));
      view.setVisibility(View.VISIBLE);
      view.setLRMargin(0);
      view.setTag(contactId);
      view.setOnClickListener(v -> {
        Bundle bundle = new Bundle();
        bundle.putLong(AddEmergentFragment.EMERGENT_ID, contactId);
        bundle.putString(AddEmergentFragment.EMERGENT_NAME, name);
        bundle.putString(AddEmergentFragment.EMERGENT_PHONE, phone);
        startFragment(bundle, requestUpdateCode);
      });
      mEmergentContactList.addView(view, mEmergentContactList.getChildCount(), params);
      if (mEmergentContactList.getChildCount() >= 5) {
        mAddEmergentRoot.setVisibility(View.GONE);
      } else {
        mAddEmergentRoot.setVisibility(View.VISIBLE);
      }
    }
  }

  @Override
  public void callback(int id) {
    showChooseTime();
  }

  private void showChooseTime() {
    TimePickerDialog dataPickerDialog = new TimePickerDialog(getContext())
        .setSelectResultListener((time, showTime) -> {
          // time == 0 不需要
//            mPresenter.autoShareEnable(mFromTime, mToTime);
          mPresenter.autoShareEnable(showTime[0], showTime[1]);
          mAutoTimeLayout.setRightTxt(showTime[0] + " - " + showTime[1], 14, normalColor);
        });
    dataPickerDialog.show();
  }

  @Override
  public void updateAutoShareTime(String from, String to) {
    mFromTime = from.substring(0, from.lastIndexOf(":"));
    mToTime = to.substring(0, to.lastIndexOf(":"));

    mFromPosition = Integer.parseInt(mFromTime.split(":")[0]);
    mToPosition = Integer.parseInt(mToTime.split(":")[0]);

    Logger.e("ldx", "mFromPosition " + mFromPosition + " mToPosition " + mToPosition);
    TimePickerDialog.sFromPosition = mFromPosition;
    TimePickerDialog.sToPosition = mToPosition;

    if (!TextUtils.isEmpty(mFromTime) && !TextUtils.isEmpty(mToTime)) {
      mAutoTimeLayout.setRightTxt(mFromTime + " - " + mToTime, 14, normalColor);
    } else {
      mAutoTimeLayout.setRightTxt("23:00 - 05:00", 14, normalColor);
    }
  }

  @Override
  public void fillContacts(List<ContactModel> modelList) {
    for (ContactModel model : modelList) {
      addContactView(model.getName(), model.getPhoneNumber(), model.getContactId());
    }
    HomeDataProvider.getInstance().saveContact(modelList);
  }

  @Override
  public void onError(int code, String message) {

  }

}
