package com.example.demo.service;

import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 简单线程池服务
 * 
 * 用于处理批量 HTTP 请求或其他 IO 密集型任务
 */
@Service
public class SimpleThreadPoolService {

    /*
     *   IO密集型建议：
     *   核心线程数 = CPU核数 × 2
     *   最大线程数 ≈ CPU核数 × 4 ~ 8
     *   队列容量 = maxThreads × 2 ~ 5
     */
    private static final int CORE_THREADS = 32;
    private static final int MAX_THREADS = 150;
    private static final int QUEUE_CAPACITY = 750; // ⚠️ 有界队列
    private static final int KEEP_ALIVE_SECONDS = 60;

    private final ThreadPoolExecutor executor;

    public SimpleThreadPoolService() {
        this.executor = new ThreadPoolExecutor(
                CORE_THREADS,
                MAX_THREADS,
                KEEP_ALIVE_SECONDS,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(QUEUE_CAPACITY),
                new ThreadFactory() {
                    private final AtomicInteger idx = new AtomicInteger(1);

                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r);
                        t.setName("http-batch-" + idx.getAndIncrement());
                        t.setDaemon(true); // 不阻塞 JVM 退出
                        return t;
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy()
                // 👆 关键：队列满了 → 回调线程自己跑，起到"限流"
        );
    }

    /**
     * 提交任务
     */
    public CompletableFuture<Void> submit(Runnable task) {
        return CompletableFuture.runAsync(task, executor);
    }

    /**
     * 批量提交
     */
    public List<CompletableFuture<Void>> submitBatch(List<Runnable> tasks) {
        return tasks.stream()
                .map(this::submit)
                .collect(Collectors.toList());
    }

    @PreDestroy
    public void shutdown() {
        // 优雅停机：先停止接收新任务，等待现有任务完成
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }

    /**
     * 获取底层线程池执行器（供监控模块使用）
     */
    public ThreadPoolExecutor getExecutor() {
        return executor;
    }
}
