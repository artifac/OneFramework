package com.one.framework.app.web.plugin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * webplugin配置
 * Created by huangqichan on 2015/9/24.
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface WebConfig {
    String topic() default "";
}
