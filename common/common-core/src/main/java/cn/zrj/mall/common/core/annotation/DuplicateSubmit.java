package cn.zrj.mall.common.core.annotation;

import java.lang.annotation.*;

/**
 * @author zhaorujie
 * @version v1.0
 * @description 防重复提交
 * @date 2023/7/13
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
//@Inherited
public @interface DuplicateSubmit {

    /**
     * 时间间隔
     * @return
     */
    int interval() default 1;
}
