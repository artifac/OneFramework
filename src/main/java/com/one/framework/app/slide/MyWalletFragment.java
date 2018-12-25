package com.one.framework.app.slide;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.one.framework.R;
import com.one.framework.app.base.BaseFragment;
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
import com.one.framework.app.widget.base.ITopTitleView.ClickPosition;
import com.one.framework.net.model.MyWalletModel;

public class MyWalletFragment extends BaseFragment implements IWalletView, IOptionChange,
    IClickCallback {

  private PullScrollRelativeLayout mRootView;
  private PullScrollView mScrollView;
  private ShapeImageView mWalletImg;
  private ListItemWithRightArrowLayout mRedBag, mPayFree;
  private OptionsView mWxSecretFree;
  private MyWalletPresenter myWalletPresenter;
  private TextView mBalanceView;

  private String mWalletDetail;
  private String mWalletCoupon;

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
    myWalletPresenter.loadData();
    return view;
  }

  private void initView(View view) {
    mRootView = view.findViewById(R.id.one_wallet_root);
    mScrollView = view.findViewById(R.id.one_wallet_scroll_view);
    mWalletImg = view.findViewById(R.id.one_wallet_image);
    mBalanceView = view.findViewById(R.id.one_wallet_balance_value);

    mRedBag = view.findViewById(R.id.one_wallet_red_bag);
    mPayFree = view.findViewById(R.id.one_wallet_pay_free);
    mWxSecretFree = view.findViewById(R.id.one_wallet_wx_options);

    mRootView.setMoveListener(mScrollView);
    mRootView.setScrollView(mScrollView);

    mTopbarView.setTitleBarBackground(Color.WHITE);
    mTopbarView.setLeft(R.drawable.one_top_bar_back_selector);
    mTopbarView.setTitle("");
    mTopbarView.setTitleRight(R.string.one_wallet_detail, Color.parseColor("#02040d"));

    mWalletImg.setSpecialRadius(false);
    mWalletImg.setImageResource(R.drawable.one_wallet_balance_bg);
//    mWalletImg.loadImageByUrl(null,
//        "http://img12.360buyimg.com/cms/jfs/t799/76/717269560/247006/7915acb9/5540ad7dNe9b60017.jpg",
//        "default");

    // 账户余额
    mRedBag.setLeftImgVisible(false);
    mRedBag.setItemTitle(getString(R.string.one_wallet_red_bag), 16, true, Color.parseColor("#373c43"));
    mRedBag.setLRMargin(0);
    mRedBag.setArrowVisible(false);


    mPayFree.setLeftImgVisible(false);
    mPayFree.setItemTitle(getString(R.string.one_wallet_wechat_no_pwd), 16, true, Color.parseColor("#373c43"));
    mPayFree.setLRMargin(0);
    mPayFree.setArrowVisible(false);
    mPayFree.setRightTxt(getString(R.string.one_wallet_wechat_toggle_off), 16, Color.parseColor("#fe173a"));

    mWxSecretFree.setOptionChange(this);
    mRedBag.setClickCallback(this);
  }

  @Override
  public void updateMyWallet(MyWalletModel myWalletModel) {
    mWalletCoupon = myWalletModel.getCouponDetailViewUrl();
    mWalletDetail = myWalletModel.getUserWalletDetailViewUrl();
    mBalanceView.setText(String.format("%.2f", myWalletModel.getBalanceInfo().getBalance() / 100 * 1f));
    SpannableString count = new SpannableString(String.format(getString(R.string.one_wallet_red_bag_count), myWalletModel.getUserCouponInfo().getValidCount()));
    count.setSpan(new ForegroundColorSpan(Color.parseColor("#02040d")), 0, count.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    count.setSpan(new ForegroundColorSpan(Color.parseColor("#999ba1")), count.length() - 1, count.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    count.setSpan(new RelativeSizeSpan(1.5f), 0, count.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    mRedBag.setRightTxt(count);
  }

  @Override
  public void onTitleItemClick(ClickPosition position) {
    switch (position) {
      case LEFT: {
        super.onTitleItemClick(position);
        break;
      }
      case RIGHT: {
        WebViewModel webViewModel = new WebViewModel();
        webViewModel.url = mWalletDetail;
        Intent intent = new Intent(getActivity(), WebActivity.class);
        intent.putExtra(WebActivity.KEY_WEB_VIEW_MODEL, webViewModel);
        startActivity(intent);
        break;
      }
    }


  }

  @Override
  public void callback(int id) {
    /*if (id == R.id.one_wallet_balance) {
      WebViewModel webViewModel = new WebViewModel();
      webViewModel.title = "账户余额";
      webViewModel.url = H5Page.INSTANCE.accountBalance(UserProfile.getInstance(getActivity()).getTokenValue());
      webViewModel.rightTextResId = R.string.one_wallet_detail;
      webViewModel.rightNextUrl = H5Page.INSTANCE.accountBalanceDetail(UserProfile.getInstance(getActivity()).getTokenValue());
      Intent intent = new Intent(getActivity(), WebActivity.class);
      intent.putExtra(WebActivity.KEY_WEB_VIEW_MODEL, webViewModel);
      startActivity(intent);

    } else*/ if (id == R.id.one_wallet_red_bag) {
      WebViewModel webViewModel = new WebViewModel();
      webViewModel.url = mWalletCoupon;
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
