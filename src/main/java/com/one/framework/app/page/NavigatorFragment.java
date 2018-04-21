package com.one.framework.app.page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.one.framework.R;
import com.one.framework.adapter.AbsBaseAdapter;
import com.one.framework.adapter.impl.NavigatorOptionsAdapter;
import com.one.framework.app.widget.PullListView;
import com.one.framework.app.widget.PullScrollRelativeLayout;
import com.one.framework.app.widget.PullScrollView;
import com.one.framework.app.widget.SwipeListView;
import com.one.framework.model.NavigatorModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ludexiang on 2018/4/16.
 */

public class NavigatorFragment extends Fragment {

  private PullScrollRelativeLayout mPullRootLayout;
  private PullListView mNavigatorOptionsList;
  private AbsBaseAdapter mAdapter;

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

    mPullRootLayout.setMoveListener(mNavigatorOptionsList);
    mPullRootLayout.setScrollView(mNavigatorOptionsList);

    mAdapter = new NavigatorOptionsAdapter(getContext());
    mNavigatorOptionsList.setAdapter(mAdapter);

    mAdapter.setListData(testDemo());
  }

  private List<NavigatorModel> testDemo() {
    List<NavigatorModel> models = new ArrayList<>();
    for (int i = 0; i < 8; i++) {
      NavigatorModel model = new NavigatorModel();
      model.optionsInfo = "options " + i;
      models.add(model);
    }
    return models;
  }

}
