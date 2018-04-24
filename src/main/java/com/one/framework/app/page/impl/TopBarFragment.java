package com.one.framework.app.page.impl;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.one.framework.R;
import com.one.framework.adapter.impl.TopbarMenuGripAdapter;
import com.one.framework.app.model.TabItem;
import com.one.framework.app.page.ITopbarFragment;
import com.one.framework.app.widget.PopWindow;
import com.one.framework.app.widget.PullGridView;
import com.one.framework.app.widget.PullScrollRelativeLayout;
import com.one.framework.app.widget.TopTitleLayout;
import com.one.framework.app.widget.base.IItemClickListener;
import com.one.framework.app.widget.base.ITabIndicatorListener;
import com.one.framework.app.widget.base.ITabIndicatorListener.IScaleListener;
import com.one.framework.app.widget.base.ITabIndicatorListener.ITabItemListener;
import com.one.framework.app.widget.TabIndicator;
import com.one.framework.app.widget.base.ITopTitleView;
import com.one.framework.app.widget.base.ITopTitleView.ITopTitleListener;
import com.one.framework.utils.UIUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ludexiang on 2018/3/27.
 */

public class TopBarFragment extends Fragment implements ITopbarFragment, IScaleListener,
    OnClickListener, IItemClickListener {

  private FrameLayout mTabParentView;
  private ITabIndicatorListener mTabIndicator;
  private ITopTitleView mTopTitleView;
  private ITabItemListener mTabItemListener;
  private ImageView mMenuView;
  private PopWindow mPopWindow;

  private View mMenuAllView;
  private PullScrollRelativeLayout mPullView;
  private PullGridView mPullGridView;
  private TopbarMenuGripAdapter mMenuAdapter;
  private ImageView mMenuClose;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    View view = inflater.inflate(R.layout.one_top_bar_layout, null);
    initView(view);
    return view;
  }

  private void initView(View view) {
    mTopTitleView = (TopTitleLayout) view.findViewById(R.id.one_top_title_layout);
    mTabParentView = (FrameLayout) view.findViewById(R.id.one_tab_container_parent);
    mTabIndicator = (TabIndicator) view.findViewById(R.id.one_top_bar_tab_indicator);
    mMenuView = (ImageView) view.findViewById(R.id.one_top_bar_tab_menu);

    mMenuAllView = LayoutInflater.from(getContext()).inflate(R.layout.one_menu_all_grid, null);
    mPullView = (PullScrollRelativeLayout) mMenuAllView
        .findViewById(R.id.one_top_bar_menu_pull_view);
    mPullGridView = (PullGridView) mMenuAllView.findViewById(R.id.one_top_bar_menu_grid_view);
    mMenuClose = (ImageView) mMenuAllView.findViewById(R.id.one_menu_close);
    mMenuAdapter = new TopbarMenuGripAdapter(getContext());
    mPullGridView.setAdapter(mMenuAdapter);
    mPullGridView.setItemClickListener(this);
    mPullView.setScrollView(mPullGridView);
    mPullView.setMoveListener(mPullGridView);

    mTabIndicator.setScaleListener(this);
    mMenuClose.setOnClickListener(this);
    mMenuView.setOnClickListener(this);
  }

  @Override
  public void setTabItemListener(ITabItemListener listener) {
    mTabItemListener = listener;
    mTabIndicator.setTabItemListener(mTabItemListener);
  }

  @Override
  public void onClick(View v) {
    /**
     * 如果想让PopUpWindow所包含的view做动画需通过PopupWindow.getContentView()获取对应的View在startAnimation
     */
    if (v.getId() == R.id.one_top_bar_tab_menu) {
      rotate(mMenuView,-45f);
      mPopWindow = new PopWindow.PopupWindowBuilder(getContext())
          .setView(mMenuAllView)//显示的布局
          .size(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
          .setAnimationStyle(R.style.TopBarMenuPopAnimation)
          .setClippingEnable(false)
          .create()//创建PopupWindow
          .showAtLocation(getView(), Gravity.TOP, 0, mTopTitleView.getViewHeight() + UIUtils
              .getStatusbarHeight(getContext()));//显示PopupWindow
      mPopWindow.getView().startAnimation(allOfMenuAnim(UIUtils.getViewHeight(mMenuAllView), true));
    } else if (v.getId() == R.id.one_menu_close) {
      if (mPopWindow != null && mPopWindow.isShowing()) {
        Animation anim = allOfMenuAnim(UIUtils.getViewHeight(mMenuAllView), false);
        anim.setAnimationListener(new AnimationListener() {
          @Override
          public void onAnimationStart(Animation animation) {

          }

          @Override
          public void onAnimationEnd(Animation animation) {
            mPopWindow.dissmiss();
            mPopWindow = null;
            onScaleUp();
          }

          @Override
          public void onAnimationRepeat(Animation animation) {

          }
        });
        mPopWindow.getView().startAnimation(anim);
      }
    }
  }

  private Animation allOfMenuAnim(int viewHeight, boolean isShow) {
    float from = isShow ? -viewHeight : 0f;
    float to = isShow ? 0f : -viewHeight;
    float fromAlpha = isShow ? 0 : 1f;
    float toAlpha = isShow ? 1f : 0f;
    AnimationSet set = new AnimationSet(false);
    AlphaAnimation alpha = new AlphaAnimation(fromAlpha, toAlpha);
    TranslateAnimation transY = new TranslateAnimation(0, 0, from, to);
    set.addAnimation(alpha);
    set.addAnimation(transY);
    set.setDuration(200);
    return set;
  }

  private void rotate(final View view, float rotate) {
    ValueAnimator rotateAnim = ValueAnimator.ofFloat(0f, rotate);
    rotateAnim.addUpdateListener(new AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        view.setRotation((Float) animation.getAnimatedValue());
      }
    });
    rotateAnim.setDuration(200);
    rotateAnim.start();
  }

  @Override
  public void setTabItems(List<TabItem> items) {
    mTabIndicator.setTabItems(items);
  }

  @Override
  public void onScaleMove(float scale) {
    mMenuView.setRotation(scale * 360);
  }

  @Override
  public void onScaleUp() {
    ObjectAnimator rotate = ObjectAnimator
        .ofFloat(mMenuView, "rotation", mMenuView.getRotation(), 0f);
    rotate.addUpdateListener(new AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        mMenuView.setRotation((Float) animation.getAnimatedValue());
      }
    });
    rotate.setDuration(200);
    rotate.start();
  }

  @Override
  public int getTopbarHeight() {
    return mTopTitleView.getViewHeight() + mTabIndicator.getViewHeight();
  }

  @Override
  public View getTabView() {
    return mTabParentView;
  }

  @Override
  public void setTitle(String title) {
    mTopTitleView.setTitle(title);
  }

  @Override
  public void setTitle(int titleResId) {
    mTopTitleView.setTitle(titleResId);
  }

  @Override
  public void setTitleClickListener(ITopTitleListener listener) {
    mTopTitleView.setTopTitleListener(listener);
  }

  @Override
  public void setAllBusiness(List<TabItem> tabs) {
    List<TabItem> tabItems = new ArrayList<TabItem>(tabs);
    // 此处通过GridView.getNumColumns() return -1
    // 修改此问题：参考PullGridView
    int mol = tabs.size() % mPullGridView.getNumColumns();
    int size = mol == 0 ? 0 : mPullGridView.getNumColumns() - mol;
    for (int i = 0; i < size; i++) {
      TabItem model = new TabItem();
      tabItems.add(model);
    }
    mMenuAdapter.setListData(tabItems);
  }

  @Override
  public void onItemClick(AdapterView<?> adapterView, View view, int position) {
    TabItem item = (TabItem) mMenuAdapter.getItem(position);
    if (item != null && mTabItemListener != null) {
      mMenuClose.performClick();
      mTabItemListener.onItemClick(item);
      mTabIndicator.update(position);
    }
  }
}
