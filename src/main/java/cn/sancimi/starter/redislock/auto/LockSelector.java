package cn.sancimi.starter.redislock.auto;

import cn.sancimi.starter.redislock.aop.RedisLockAop;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 分布式锁选择器
 */
public class LockSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{RedisLockAop.class.getName()};
    }
}
