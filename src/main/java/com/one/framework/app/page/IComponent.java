package com.one.framework.app.page;

import android.content.Intent;
import android.os.Bundle;
import com.one.framework.app.model.IBusinessContext;

/**
 * Created by ludexiang on 2018/4/10.
 */

public interface IComponent {
  void setBusinessContext(IBusinessContext businessContext);

  void onFragmentForResult(int requestCode, Bundle args);
  /**
   * @return true 当前组件消费了 false 没有消费 default false
   */
  boolean onBackPressed();

  void onNewIntent(Intent intent);

  void setRootListener(IRootListener listener);

  interface IRootListener {
    void onLeaveHome();
    void onBackToHome();
  }
}
