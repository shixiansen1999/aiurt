package com.aiurt.common.redisson.client;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * RedissonLockClient是一个封装了分布式锁操作的工具类，基于Redisson客户端实现。
 * 通过此类，可以轻松地在分布式环境下使用各种锁，如普通锁、公平锁等，并提供了加锁、解锁和检查锁等操作。
 * 这个工具类依赖于RedissonClient和RedisTemplate实例。
 *
 * @author Mrwei
 * @date 2023-08-22
 */
@Slf4j
@Component
public class RedissonLockClient {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取锁
     *
     * @param lockKey 锁的名称
     */
    public RLock getLock(String lockKey) {
        return redissonClient.getLock(lockKey);
    }

    /**
     * 尝试获取锁操作，如果在指定时间内无法获得锁，则返回失败
     *
     * @param lockName      锁的名称
     * @param expireSeconds 锁的超时时间（秒）
     * @return 是否成功获得锁
     */
    public boolean tryLock(String lockName, long expireSeconds) {
        return tryLock(lockName, 0, expireSeconds);
    }

    /**
     * 尝试获取锁操作，如果在指定等待时间内获得锁，并在锁的超时时间内保持锁状态
     *
     * @param lockName      锁的名称
     * @param waitTime      等待获取锁的最大等待时间（秒）
     * @param expireSeconds 锁的超时时间（秒）
     * @return 是否成功获得锁
     */
    public boolean tryLock(String lockName, long waitTime, long expireSeconds) {
        RLock rLock = getLock(lockName);
        boolean getLock = false;
        try {
            getLock = rLock.tryLock(waitTime, expireSeconds, TimeUnit.SECONDS);
            if (getLock) {
                log.info("获取锁成功,lockName={}", lockName);
            } else {
                log.info("获取锁失败,lockName={}", lockName);
            }
        } catch (InterruptedException e) {
            log.error("获取式锁异常，lockName=" + lockName, e);
            getLock = false;
        }
        return getLock;
    }

    /**
     * 尝试获取公平锁操作，如果锁的名称已存在则返回失败，否则尝试获取锁
     *
     * @param lockKey   锁的名称
     * @param unit      时间单位
     * @param leaseTime 锁的租约时间（单位：unit）
     * @return 是否成功获得锁
     */
    public boolean fairLock(String lockKey, TimeUnit unit, int leaseTime) {
        RLock fairLock = redissonClient.getFairLock(lockKey);
        try {
            boolean existKey = existKey(lockKey);
            // 已经存在了，就直接返回
            if (existKey) {
                return false;
            }
            return fairLock.tryLock(3, leaseTime, unit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 检查给定键是否存在于Redis中
     *
     * @param key 要检查的键
     * @return 如果键存在，则返回 true，否则返回 false
     */
    public boolean existKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 加锁操作，并在获得锁之后立即阻塞，直到手动解锁
     *
     * @param lockKey 锁的名称
     * @return 获取的锁实例
     */
    public RLock lock(String lockKey) {
        RLock lock = getLock(lockKey);
        lock.lock();
        return lock;
    }

    /**
     * 加锁操作，并阻塞直到获得锁，在一定的租约时间内自动解锁
     *
     * @param lockKey   锁的名称
     * @param leaseTime 锁的租约时间（秒）
     * @return 获取的锁实例
     */
    public RLock lock(String lockKey, long leaseTime) {
        RLock lock = getLock(lockKey);
        lock.lock(leaseTime, TimeUnit.SECONDS);
        return lock;
    }

    /**
     * 解锁
     *
     * @param lockName 锁的名称
     */
    public void unlock(String lockName) {
        try {
            redissonClient.getLock(lockName).unlock();
        } catch (Exception e) {
            log.error("解锁异常，lockName=" + lockName, e);
        }
    }

}
