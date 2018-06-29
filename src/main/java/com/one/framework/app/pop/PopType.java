package com.one.framework.app.pop;


import static com.one.framework.app.pop.PopType.MATCH;
import static com.one.framework.app.pop.PopType.WRAP;

import android.support.annotation.IntDef;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ludexiang on 2017/12/25.
 */

@Target({ElementType.LOCAL_VARIABLE, ElementType.PARAMETER})
@Retention(RetentionPolicy.SOURCE)
@IntDef({MATCH, WRAP})
public @interface PopType {
  int MATCH = 0;
  int WRAP = 1;
}
