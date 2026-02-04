package com.example.demo.controller;

import com.example.demo.service.SimpleThreadPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 演示控制器
 * 
 * 提供接口用于向线程池提交任务，方便观察监控效果
 */
@RestController
@RequestMapping("/demo")
public class DemoController {

    private static final Logger logger = LoggerFactory.getLogger(DemoController.class);

    private final SimpleThreadPoolService threadPoolService;
    private final AtomicInteger taskCounter = new AtomicInteger(0);

    @Autowired
    public DemoController(SimpleThreadPoolService threadPoolService) {
        this.threadPoolService = threadPoolService;
    }

    /**
     * 提交指定数量的快速任务
     * 
     * @param count 任务数量（默认10）
     * @return 提交结果
     */
    @GetMapping("/submit")
    public Map<String, Object> submitTasks(@RequestParam(defaultValue = "10") int count) {
        Map<String, Object> result = new HashMap<>();
        
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            final int taskId = taskCounter.incrementAndGet();
            CompletableFuture<Void> future = threadPoolService.submit(() -> {
                logger.info("Task-{} started", taskId);
                try {
                    // 模拟短暂工作
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                logger.info("Task-{} completed", taskId);
            });
            futures.add(future);
        }

        result.put("success", true);
        result.put("submitted", count);
        result.put("message", "已提交 " + count + " 个快速任务");
        result.put("tip", "访问 /actuator/threadpool 查看线程池状态");
        
        return result;
    }

    /**
     * 压力测试 - 提交大量长时间任务
     * 
     * @param count 任务数量（默认100）
     * @param sleep 每个任务休眠时间毫秒（默认5000）
     * @return 提交结果
     */
    @GetMapping("/stress")
    public Map<String, Object> stressTest(
            @RequestParam(defaultValue = "100") int count,
            @RequestParam(defaultValue = "5000") int sleep) {
        
        Map<String, Object> result = new HashMap<>();
        
        // 限制最大任务数和休眠时间，防止滥用
        count = Math.min(count, 1000);
        sleep = Math.min(sleep, 60000);
        
        final int finalSleep = sleep;
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            final int taskId = taskCounter.incrementAndGet();
            CompletableFuture<Void> future = threadPoolService.submit(() -> {
                logger.info("StressTask-{} started, will sleep {}ms", taskId, finalSleep);
                try {
                    Thread.sleep(finalSleep);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                logger.info("StressTask-{} completed", taskId);
            });
            futures.add(future);
        }

        result.put("success", true);
        result.put("submitted", count);
        result.put("sleepMs", sleep);
        result.put("message", "已提交 " + count + " 个压力测试任务，每个任务休眠 " + sleep + "ms");
        result.put("tip", "立即访问 /actuator/threadpool 观察线程池状态变化");
        
        return result;
    }

    /**
     * 查看任务计数器
     */
    @GetMapping("/stats")
    public Map<String, Object> stats() {
        Map<String, Object> result = new HashMap<>();
        result.put("totalTasksSubmitted", taskCounter.get());
        return result;
    }
}
