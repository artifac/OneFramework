package com.one.framework.app.page;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.one.framework.R;
import com.one.framework.adapter.AbsBaseAdapter;
import com.one.framework.adapter.impl.NavigatorOptionsAdapter;
import com.one.framework.adapter.impl.NavigatorOptionsGridAdapter;
import com.one.framework.app.widget.PullGridView;
import com.one.framework.app.widget.PullListView;
import com.one.framework.app.widget.PullScrollRelativeLayout;
import com.one.framework.app.widget.ScrollerLayout;
import com.one.framework.app.widget.base.IMovePublishListener;
import com.one.framework.model.NavigatorModel;
import com.one.framework.utils.UIUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ludexiang on 2018/4/16.
 */

public class NavigatorFragment extends Fragment implements IMovePublishListener {

  private PullScrollRelativeLayout mPullRootLayout;
  private PullListView mNavigatorOptionsList;
  private AbsBaseAdapter mListAdapter;
  private AbsBaseAdapter mGridAdapter;
  // 通用
  private ScrollerLayout mGeneralLayout;
  // 默认平移高度
  private int mGeneralDefaultHeight;
  private ImageView mGridArrow;
  private PullGridView mOptionsGrid;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    View view = inflater.inflate(R.layout.one_navigator_layout, null);
    initView(view);
    return view;
  }

  private void initView(View view) {
    mPullRootLayout = (PullScrollRelativeLayout) view.findViewById(R.id.one_nav_pull_view_root);
    mNavigatorOptionsList = (PullListView) view.findViewById(android.R.id.list);
    mGeneralLayout = (ScrollerLayout) view.findViewById(R.id.one_navigator_general);
    mOptionsGrid = (PullGridView) view.findViewById(R.id.one_navigator_grid_view);
    mGridArrow = (ImageView) view.findViewById(R.id.one_navigator_general_arrow);

    mPullRootLayout.setMoveListener(mNavigatorOptionsList);
    mPullRootLayout.setScrollView(mNavigatorOptionsList);

    mListAdapter = new NavigatorOptionsAdapter(getContext());
    mNavigatorOptionsList.setAdapter(mListAdapter);

    mGridAdapter = new NavigatorOptionsGridAdapter(getContext());
    mOptionsGrid.setAdapter(mGridAdapter);

    mGeneralLayout.setMoveListener(this);
    mListAdapter.setListData(testDemo());
    mGridAdapter.setListData(testDemo());

    // default 通用布局向下平移
    int screeHeight = UIUtils.getScreenHeight(getContext());
    int navigatorHeaderHeight = getResources()
        .getDimensionPixelOffset(R.dimen.one_general_header_height);
    int height = getResources().getDimensionPixelOffset(R.dimen.one_general_default_show_height);
    mGeneralDefaultHeight = screeHeight - height - navigatorHeaderHeight;
    mGeneralLayout.setTranslationY(mGeneralDefaultHeight);
  }

  // 通用布局进行滚动
  @Override
  public void onMove(float offsetX, float offsetY) {
    float translationY = mGeneralLayout.getTranslationY() + offsetY + 0.5f;
    float scale = translationY / mGeneralLayout.getHeight();
    mGridArrow.setRotation((1f - scale) * 180);
    mGeneralLayout.setTranslationY(translationY);
  }

  @Override
  public void onUp(boolean bottom2Up, boolean isFling) {
    float translationY = mGeneralLayout.getTranslationY() + 0.5f;
    float scale = 1f - Math.abs(translationY / mGeneralLayout.getHeight());
    float to = isFling ? (bottom2Up ? 0 : mGeneralDefaultHeight) : scale >= 0.5f ? 0f : mGeneralDefaultHeight;
    int duration = (int) (300 * scale);
    goonMove(translationY, to, duration);
  }

  private void goonMove(float from, final float to, long duration) {
    ValueAnimator translate = ValueAnimator.ofFloat(from, to);
    translate.setDuration(duration);
    translate.addUpdateListener(new AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        float animValue = (Float) animation.getAnimatedValue();
        mGridArrow.setRotation(to == 0 ? 180 : 0);
        mGeneralLayout.setTranslationY(animValue);
      }
    });
    translate.start();
  }

  // test options
  private List<NavigatorModel> testDemo() {
    List<NavigatorModel> models = new ArrayList<>();
    for (int i = 0; i < 8; i++) {
      NavigatorModel model = new NavigatorModel();
      model.optionsInfo = "options " + i;
      model.optionsIconId = R.drawable.one_navigator_grid_search;
      models.add(model);
    }
    return models;
  }

}
