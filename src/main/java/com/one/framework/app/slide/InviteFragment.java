package com.one.framework.app.slide;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.one.framework.R;
import com.one.framework.app.base.BaseFragment;
import com.one.framework.app.slide.presenter.InvitePresenter;
import com.one.framework.model.InviteModel;
import com.one.framework.utils.UIUtils;
import com.one.share.Platform;
import com.one.share.Platform.ShareType;
import com.one.share.ShareSDK;

public class InviteFragment extends BaseFragment implements View.OnClickListener, IInviteView {

  private TextView mInviteCode;
  private TextView mInviteMax;
  private TextView mWechat;
  private TextView mSms;

  private InvitePresenter mPresenter;

  private InviteModel mInviteModel;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mPresenter = new InvitePresenter(this);
    mPresenter.invite();
  }

  @Override
  protected View onCreateViewImpl(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.one_invite_layout, null);
    initView(view);
    return view;
  }

  private void initView(View view) {
    view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

    mInviteCode = view.findViewById(R.id.one_invite_code);
    mInviteMax = view.findViewById(R.id.one_invite_max);
    mWechat = view.findViewById(R.id.one_invite_wechat);
    mSms = view.findViewById(R.id.one_invite_sms);

    mTopbarView.setTitleBarBackground(Color.WHITE);
    mTopbarView.setLeft(R.drawable.one_top_bar_back_selector);
    mTopbarView.setTitle(R.string.one_slide_invite_friend);
    mTopbarView.setTitleRight(0);

    mWechat.setOnClickListener(this);
    mSms.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();
    if (mInviteModel != null) {
      ShareSDK share = new ShareSDK(getActivity());
      if (id == R.id.one_invite_wechat) {
        share.share(Platform.WECHAT.share(mInviteModel.getInviteTitle(), mInviteModel.getInviteContent(), mInviteModel.getInviteUrl(), "", ShareType.LINK));
      } else if (id == R.id.one_invite_sms) {
        share.share(Platform.SMS.share(mInviteModel.getInviteTitle(), mInviteModel.getInviteContent(), mInviteModel.getInviteUrl(), "", ShareType.LINK));
      }
    }
  }

  @Override
  public void updateView(InviteModel model) {
    mInviteModel = model;
    mInviteMax.setText(UIUtils.highlight(String.format(getString(R.string.one_invite_max), model.getAllCount()), Color.RED));
    char[] code = model.getInviteCode().toCharArray();
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < code.length; i++) {
      if (i != code.length -1) {
        builder.append(code[i]).append(" ");
      } else {
        builder.append(code[i]);
      }
    }
    mInviteCode.setText(builder.toString().toUpperCase());
  }
}
