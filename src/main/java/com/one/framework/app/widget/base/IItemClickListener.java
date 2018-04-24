package com.one.framework.app.widget.base;

import android.view.View;
import android.widget.AdapterView;

/**
 * Created by ludexiang on 2018/4/24.
 */

public interface IItemClickListener {
  void onItemClick(AdapterView<?> adapterView, View view, int position);
}
