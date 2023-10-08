package com.aiurt.common.util;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * redis分布式锁工具类
 * 部分代码是从org.jeecg.boot.starter.lock.client.RedissonLockClient抄的
 * @author 华宜威
 * @date 2023-09-22 09:28:11
 */
@Slf4j
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class RedisLockUtils {

    /**给锁加前缀，不然的话，将key锁住后，就没法获取key对应的value值了*/
    private static final String KEY_PREFIX = "lock::";

    @Value("${spring.redis.host}")
    private String redisHost;
    @Value("${spring.redis.port}")
    private String redisPort;
    @Value("${spring.redis.password}")
    private String redisPassWord;
    @Value("${spring.redis.database}")
    private String redisDatabase;

    private static RedissonClient redissonClient;


    /**
     * PostConstruct 注解是使方法在构造函数与注入依赖后调用
     * 因为构造函数比@Value注解注入数据先调用，所以不能在构造函数里使用@Value修饰的变量
     */
    @PostConstruct
    public void init(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + redisHost +":" + redisPort);
        config.useSingleServer().setPassword(redisPassWord);
        config.useSingleServer().setDatabase(Integer.parseInt(redisDatabase));
        redissonClient = (Redisson) Redisson.create(config);
    }

    /**
     * 获取锁获取锁，加锁的话不要用这个方法
     * 真正的锁的key值要加个前缀
     * @param lockKey redis的key值
     * @return RLock 返回一个RLock锁对象
     */
    public static RLock getLock(String lockKey) {
        return RedisLockUtils.redissonClient.getLock(KEY_PREFIX + lockKey);
    }

    /**
     * 尝试对某个redis的key进行加锁, 默认等待时间10秒，锁的过期时间10秒
     * @param lockName 要加锁的key值
     * @return boolean：返回true加锁成功，返回false加锁失败
     */
    public static boolean tryLock(String lockName){
        return RedisLockUtils.tryLock(lockName, 10, 10);
    }

    /**
     * 尝试对某个redis的key进行加锁
     * @param lockName 要加锁的key值
     * @param waitTime 尝试/等待加锁时间，单位秒
     * @param expireSeconds 锁的过期时间，单位秒
     * @return boolean：返回true加锁成功，返回false加锁失败
     */
    public static boolean tryLock(String lockName, long waitTime, long expireSeconds) {
        RLock rLock = RedisLockUtils.getLock(lockName);
        boolean getLock = false;

        try {
            getLock = rLock.tryLock(waitTime, expireSeconds, TimeUnit.SECONDS);
            if (getLock) {
                log.info("获取锁成功,lockName={}", lockName);
            } else {
                log.info("获取锁失败,lockName={}", lockName);
            }
        } catch (InterruptedException var9) {
            log.error("获取式锁异常，lockName=" + lockName, var9);
            getLock = false;
        }

        return getLock;
    }

    /**
     * 释放某个redis的key的锁
     * @param lockName 要释放锁的key值
     */
    public static void unlock(String lockName) {
        try {
            RedisLockUtils.getLock(lockName).unlock();
        } catch (Exception var3) {
            log.error("解锁异常，lockName=" + lockName, var3);
        }

    }
}
