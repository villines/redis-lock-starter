package cn.sancimi.starter.redislock.annotation;

import cn.sancimi.starter.redislock.auto.LockSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(LockSelector.class)
public @interface EnableRedisLock {

}
