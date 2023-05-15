package com.aiurt.common.util;

/**
 * @author:wgp
 * @create: 2023-04-04 19:29
 * @Description:异步线程池
 */

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
public class AsyncThreadPoolExecutorUtil {

    /**
     * 核心线程数
     */
    private static final int CORE_POOL_SIZE = 10;
    /**
     * 最大线程数
     */
    private static final int MAX_POOL_SIZE = 20;
    /**
     * 空闲线程存活时间
     */
    private static final long KEEP_ALIVE_TIME = 60L;
    /**
     * 任务队列
     */
    private static final BlockingQueue<Runnable> TASK_QUEUE = new LinkedBlockingQueue<>(1000);
    /**
     * 单例实例
     */
    private static volatile AsyncThreadPoolExecutorUtil instance = null;
    /**
     * 线程池
     */
    private ThreadPoolExecutor executor;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(AsyncThreadPoolExecutorUtil.class.getName());

    /**
     * 私有构造函数
     *
     * @param corePoolSize                        (int): 核心线程数。线程池在启动后会保持这个数量的线程。如果任务队列已满，线程池可以增加线程数量，直到达到 maximumPoolSize。
     * @param maximumPoolSize(int):               最大线程数。线程池中最多允许的线程数量。当任务队列已满并且当前线程数量小于这个值时，线程池会创建新线程来执行任务。
     * @param keepAliveTime(long):                线程空闲时间。当线程池中的线程数量超过 corePoolSize 时，多余的空闲线程将等待新任务的到来。如果等待时间超过 keepAliveTime，空闲线程将被终止。
     * @param unit                                (TimeUnit): 空闲时间单位。keepAliveTime 参数的时间单位。TimeUnit 是一个枚举类型，包括 NANOSECONDS、MICROSECONDS、MILLISECONDS、SECONDS、MINUTES、HOURS 和 DAYS 等时间单位。
     * @param workQueue(BlockingQueue<Runnable>): 任务队列。用于存放等待执行的任务。可以选择使用 ArrayBlockingQueue、LinkedBlockingQueue、SynchronousQueue 等不同类型的阻塞队列。
     * @param threadFactory                       (ThreadFactory): 线程工厂。用于创建新线程。可以使用 Executors.defaultThreadFactory() 或自定义线程工厂。
     * @param handler                             (RejectedExecutionHandler): 拒绝策略。当任务队列已满且线程数量达到最大值时，线程池如何处理新提交的任务。RejectedExecutionHandler 是一个接口，可以选择使用 ThreadPoolExecutor.AbortPolicy、
     *                                            ThreadPoolExecutor.CallerRunsPolicy、ThreadPoolExecutor.DiscardOldestPolicy 或 ThreadPoolExecutor.DiscardPolicy 等策略，或者自定义拒绝策略。
     */
    private AsyncThreadPoolExecutorUtil(int corePoolSize,
                                        int maximumPoolSize,
                                        long keepAliveTime,
                                        TimeUnit unit,
                                        BlockingQueue<Runnable> workQueue,
                                        ThreadFactory threadFactory,
                                        RejectedExecutionHandler handler) {
        executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    /**
     * 获取线程池对象
     *
     * @return 线程池对象
     */
    public static AsyncThreadPoolExecutorUtil getExecutor() {
        // 双重检查锁定保证线程安全
        if (instance == null) {
            // 创建 AsyncThreadPoolExecutorUtil 实例
            synchronized (AsyncThreadPoolExecutorUtil.class) {
                if (instance == null) {
                    instance = new AsyncThreadPoolExecutorUtil(
                            CORE_POOL_SIZE,
                            MAX_POOL_SIZE,
                            KEEP_ALIVE_TIME,
                            TimeUnit.SECONDS,
                            TASK_QUEUE,
                            new DefaultThreadFactory("custom"),
                            new ThreadPoolExecutor.AbortPolicy()
                    );
                }
            }
        }
        return instance;
    }

    /**
     * 提交任务到线程池并返回 Future。
     *
     * @param task 要执行的任务
     * @return 返回 Future 对象
     */
    public <T> Future<T> submitTask(Callable<T> task) {
        return executor.submit(() -> {
            try {
                return task.call();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Task execution failed", e);
                return null;
            }
        });
    }

    /**
     * 关闭线程池。
     * 阻止新任务提交并等待已提交任务完成。
     */
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    logger.severe("ThreadPool did not terminate");
                }
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 返回当前线程池中正在执行任务的线程数
     *
     * @return
     */
    public int getActiveCount() {
        return executor.getActiveCount();
    }

    /**
     * 返回线程池已完成任务的累计数量
     *
     * @return
     */
    public long getCompletedTaskCount() {
        return executor.getCompletedTaskCount();
    }

    /**
     * 返回线程池已完成任务的累计数量
     *
     * @return
     */
    public int getPoolSize() {
        return executor.getPoolSize();
    }

    /**
     * 返回线程池的任务队列的当前大小
     *
     * @return
     */
    public int getQueueSize() {
        return executor.getQueue().size();
    }

    /**
     * 立即关闭线程池，尝试停止所有正在执行的任务。
     */
    public void shutdownNow() {
        executor.shutdownNow();
    }

    /**
     * 实现了 ThreadFactory 接口的内部类。它用于为线程池创建新的线程。DefaultThreadFactory 类为每个新创建的线程设置一个名字，
     * 以方便调试和监控。线程名的前缀由传入的 poolName 参数构成，后面跟着一个递增的数字。
     * newThread 方法是 ThreadFactory 接口的实现。它接收一个 Runnable 参数，创建一个新的线程，设置线程的名字、优先级和守护状态，并返回新创建的线程。
     */
    public static class DefaultThreadFactory implements ThreadFactory {
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        public DefaultThreadFactory(String poolName) {
            namePrefix = "pool-" + poolName + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, namePrefix + threadNumber.getAndIncrement());
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }

    public static void main(String[] args) {
        // 创建 AsyncThreadPoolExecutorUtil 实例
        AsyncThreadPoolExecutorUtil asyncThreadPoolExecutorUtil = getExecutor();

        // 提交任务到线程池并获取 Future 对象
        Future<String> futureTask = asyncThreadPoolExecutorUtil.submitTask(() -> {
            Thread.sleep(1000);
            // 获取当前线程名称
            String currentThreadName = Thread.currentThread().getName();
            // 打印当前线程名称
            System.out.println("Current thread name: " + currentThreadName);
            return "Task completed!";
        });

        // 获取任务结果
        try {
            String result = futureTask.get();
            System.out.println("Task result: " + result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        // 关闭线程池
        asyncThreadPoolExecutorUtil.shutdown();
    }
}
