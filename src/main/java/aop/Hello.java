package aop;

import java.lang.annotation.*;

/**
 * AOP demo 把 name,age 传给加注解的方法
 * Created by leeton on 9/18/17.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Hello {
    String name() default "";
    int age() default 0;

}
