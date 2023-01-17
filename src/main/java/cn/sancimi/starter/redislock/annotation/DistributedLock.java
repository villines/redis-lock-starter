package cn.sancimi.starter.redislock.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DistributedLock {

    /**
     * 锁的名称: 支持spel表达式 eg:#tenantDo.id
     * 默认为:类全限定名+'.'+方法名+':' + lockKey eg: com.lzkj.up.service.impl.UserService.getUser:10
     */
    String lockKey();

    /**
     * 是否使用尝试锁。
     */
    boolean tryLock() default false;

    /**
     * 最长等待时间。默认3秒
     * 该字段只有当tryLock()返回true才有效。
     */
    long waitTime() default 3L;

    /**
     * 锁超时时间。默认5秒
     * 如果tryLock为false，且leaseTime设置为0及以下，会变成lock()
     */
    long leaseTime() default 5L;

    /**
     * 时间单位。默认为秒。
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

}
