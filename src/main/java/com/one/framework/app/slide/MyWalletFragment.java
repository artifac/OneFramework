package com.one.framework.app.slide;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.one.framework.R;
import com.one.framework.app.base.BaseFragment;
import com.one.framework.app.login.UserProfile;
import com.one.framework.app.slide.presenter.MyWalletPresenter;
import com.one.framework.app.web.WebActivity;
import com.one.framework.app.web.WebViewModel;
import com.one.framework.app.widget.ListItemWithRightArrowLayout;
import com.one.framework.app.widget.OptionsView;
import com.one.framework.app.widget.PullScrollRelativeLayout;
import com.one.framework.app.widget.PullScrollView;
import com.one.framework.app.widget.ShapeImageView;
import com.one.framework.app.widget.base.IListItemView.IClickCallback;
import com.one.framework.app.widget.base.IOptionView.IOptionChange;
import com.trip.taxi.utils.H5Page;

public class MyWalletFragment extends BaseFragment implements IWalletView, IOptionChange,
    IClickCallback {

  private PullScrollRelativeLayout mRootView;
  private PullScrollView mScrollView;
  private ShapeImageView mWalletImg;
  private ListItemWithRightArrowLayout mBalance;
  private ListItemWithRightArrowLayout mRedBag;
  private OptionsView mWxSecretFree;
  private MyWalletPresenter myWalletPresenter;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    myWalletPresenter = new MyWalletPresenter(getActivity(), this);
  }

  @Override
  protected View onCreateViewImpl(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.one_my_wallet_layout, container, false);
    initView(view);
    return view;
  }

  private void initView(View view) {
    mRootView = view.findViewById(R.id.one_wallet_root);
    mScrollView = view.findViewById(R.id.one_wallet_scroll_view);
    mWalletImg = view.findViewById(R.id.one_wallet_image);

    mBalance = view.findViewById(R.id.one_wallet_balance);
    mRedBag = view.findViewById(R.id.one_wallet_red_bag);
    mWxSecretFree = view.findViewById(R.id.one_wallet_wx_options);

    mRootView.setMoveListener(mScrollView);
    mRootView.setScrollView(mScrollView);

    mTopbarView.setTitleBarBackground(Color.WHITE);
    mTopbarView.setLeft(R.drawable.one_top_bar_back_selector);
    mTopbarView.setTitle(R.string.one_slide_my_wallet);

    mWalletImg.loadImageByUrl(null,
        "http://img12.360buyimg.com/cms/jfs/t799/76/717269560/247006/7915acb9/5540ad7dNe9b60017.jpg",
        "default");

    mBalance.setItemTitle(R.string.one_wallet_balance);
    mRedBag.setItemTitle(R.string.one_wallet_red_bag);
    mWxSecretFree.setOptionChange(this);
    mBalance.setClickCallback(this);
    mRedBag.setClickCallback(this);
  }

  @Override
  public void callback(int id) {
    if (id == R.id.one_wallet_balance) {
      WebViewModel webViewModel = new WebViewModel();
      webViewModel.title = "账户余额";
      webViewModel.url = H5Page.INSTANCE.accountBalance(UserProfile.getInstance(getActivity()).getTokenValue());
      webViewModel.rightTextResId = R.string.one_wallet_detail;
      webViewModel.rightNextUrl = H5Page.INSTANCE.accountBalanceDetail(UserProfile.getInstance(getActivity()).getTokenValue());
      Intent intent = new Intent(getActivity(), WebActivity.class);
      intent.putExtra(WebActivity.KEY_WEB_VIEW_MODEL, webViewModel);
      startActivity(intent);

    } else if (id == R.id.one_wallet_red_bag) {
      WebViewModel webViewModel = new WebViewModel();
      webViewModel.title = "Tencent";
      webViewModel.url = "https://www.tencent.com";
      Intent intent = new Intent(getActivity(), WebActivity.class);
      intent.putExtra(WebActivity.KEY_WEB_VIEW_MODEL, webViewModel);
      startActivity(intent);
    }
  }

  @Override
  public void onChange(String key, int position) {
    if (position == 0) {
      // 开通
      myWalletPresenter.openSecretFree();
    } else {
      // close
      myWalletPresenter.closeSecretFree();
    }
    mWxSecretFree.save();
  }

}
