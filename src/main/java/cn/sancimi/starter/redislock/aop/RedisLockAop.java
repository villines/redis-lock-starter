package cn.sancimi.starter.redislock.aop;

import cn.sancimi.starter.redislock.annotation.DistributedLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

@Aspect
@EnableAspectJAutoProxy
public class RedisLockAop {

    @Autowired
    private RedissonClient redissonclient;

    @Pointcut("@annotation(cn.sancimi.starter.redislock.annotation.DistributedLock)")
    private void pointcut() {
    }

    @Before("pointcut()")
    private void before() {
        System.out.println("before");
    }

    @After("pointcut()")
    private void after() {
        System.out.println("after");
    }

    @Around("pointcut()")
    private Object around(ProceedingJoinPoint joinPoint) throws InterruptedException {
        // 获取方法签名和redis锁的注解等信息
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        DistributedLock annoLock = method.getAnnotation(DistributedLock.class);

        // 执行获取锁的业务逻辑
        RLock lock = lock(getLockKey(method, joinPoint.getArgs(), annoLock), annoLock);
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @AfterReturning("pointcut()")
    private void afterReturn() {
        System.out.println("afterReturn");
    }

    @AfterThrowing("pointcut()")
    private void afterThrowing() {
        System.out.println("afterThrowing");
    }

    /**
     * 获取redis锁
     *
     * @param lockName
     * @param distributedLock
     * @return
     * @throws InterruptedException
     */
    private RLock lock(String lockName, DistributedLock distributedLock) throws InterruptedException {
        RLock lock = redissonclient.getLock(lockName);
        if (distributedLock.tryLock()) {
            lock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());
        } else {
            long leaseTime = distributedLock.leaseTime();
            if (leaseTime > 0) {
                lock.lock(distributedLock.leaseTime(), distributedLock.timeUnit());
            } else {
                lock.lock();
            }
        }
        return lock;
    }

    /**
     * 从业务使用中获取redis锁的key
     *
     * @param method    注解所在的方法名
     * @param distributedLock 注解定义的key表达式
     * @return redis的锁名称
     */
    private String getLockKey(Method method, Object[] args, DistributedLock distributedLock) {
        //获取方法的形参名数组
        LocalVariableTableParameterNameDiscoverer nameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
        String[] parameterNames = nameDiscoverer.getParameterNames(method);

        // 1.方法无参数，使用默认
        if (parameterNames == null) {
            return method.getDeclaringClass().getName() + "." + method.getName() + ":" + distributedLock.lockKey();
        }

        // 2.方法有参数，解析注解参数
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();
        Expression expression = parser.parseExpression(distributedLock.lockKey());
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }
        String newKey = expression.getValue(context, String.class);
        return method.getDeclaringClass().getName() + "." + method.getName() + ":" + newKey;
    }

}
