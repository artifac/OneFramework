package com.one.framework.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.one.framework.R;
import com.one.framework.adapter.ContactAdapter.ContactHolder;
import com.one.framework.model.ContactModel;

public class ContactAdapter extends AbsBaseAdapter<ContactModel, ContactHolder>{

  public ContactAdapter(Context context) {
    super(context);
  }

  @Override
  protected ContactHolder createHolder() {
    return new ContactHolder();
  }

  @Override
  protected void initView(View view, ContactHolder holder) {
    holder.header = view.findViewById(R.id.one_contact_header);
    holder.name = view.findViewById(R.id.one_contact_name);
  }

  @Override
  protected void bindData(ContactModel model, ContactHolder holder, int position) {
    holder.header.setText(model.getName().substring(0, 1));
    holder.name.setText(model.getName());
  }

  @Override
  protected View createView() {
    return mInflater.inflate(R.layout.one_contact_item_layout, null);
  }

  class ContactHolder {
    TextView header;
    TextView name;
  }
}
