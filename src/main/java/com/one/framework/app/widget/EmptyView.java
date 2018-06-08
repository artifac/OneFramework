package com.one.framework.app.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.one.framework.R;

/**
 * Created by ludexiang on 2018/1/9.
 */

public class EmptyView extends LinearLayout implements View.OnClickListener {

  private ImageView imgView;
  private TextView txtView;

  public EmptyView(Context context) {
    this(context, null);
  }

  public EmptyView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public EmptyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    LayoutInflater.from(context).inflate(R.layout.one_empty_layout, this, true);
    imgView = (ImageView) findViewById(R.id.one_empty_img);
    txtView = (TextView) findViewById(R.id.one_empty_txt);
    setOrientation(VERTICAL);

    setOnClickListener(this);
  }

  public void setImgRes(int imgRes) {
    if (imgRes == 0) {
      return;
    }
    imgView.setImageResource(imgRes);
  }

  public void setTxtRes(int txtRes) {
    if (txtRes == 0) {
      return;
    }
    txtView.setText(txtRes);
  }

  @Override
  public void onClick(View v) {

  }
}
