package com.github.bahtya.monitor.endpoint;

import com.github.bahtya.monitor.dto.ThreadPoolMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池监控 Actuator 端点
 * 
 * <p>访问方式：
 * <ul>
 *   <li>GET /actuator/threadpool - 获取所有线程池状态</li>
 *   <li>GET /actuator/threadpool/{poolName} - 获取指定线程池状态</li>
 * </ul>
 *
 * @author Bahtya
 * @since 1.0.0
 */
@Endpoint(id = "threadpool")
public class ThreadPoolEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolEndpoint.class);

    private final ApplicationContext applicationContext;

    public ThreadPoolEndpoint(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 获取所有线程池的监控指标
     *
     * @return 包含所有线程池指标的响应
     */
    @ReadOperation
    public Map<String, Object> threadPools() {
        Map<String, Object> result = new HashMap<>(4);
        try {
            List<ThreadPoolMetrics> metricsList = collectAllThreadPoolMetrics();
            result.put("success", true);
            result.put("count", metricsList.size());
            result.put("pools", metricsList);
            result.put("message", "OK");
        } catch (Exception e) {
            logger.error("Failed to collect thread pool metrics", e);
            result.put("success", false);
            result.put("count", 0);
            result.put("pools", Collections.emptyList());
            result.put("message", "Error: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取指定线程池的监控指标
     *
     * @param poolName 线程池Bean名称
     * @return 指定线程池的指标
     */
    @ReadOperation
    public Map<String, Object> threadPool(@Selector String poolName) {
        Map<String, Object> result = new HashMap<>(4);
        try {
            ThreadPoolMetrics metrics = collectThreadPoolMetrics(poolName);
            if (metrics != null) {
                result.put("success", true);
                result.put("pool", metrics);
                result.put("message", "OK");
            } else {
                result.put("success", false);
                result.put("pool", null);
                result.put("message", "Thread pool not found: " + poolName);
            }
        } catch (Exception e) {
            logger.error("Failed to collect thread pool metrics for: " + poolName, e);
            result.put("success", false);
            result.put("pool", null);
            result.put("message", "Error: " + e.getMessage());
        }
        return result;
    }

    /**
     * 收集所有线程池的指标
     */
    private List<ThreadPoolMetrics> collectAllThreadPoolMetrics() {
        List<ThreadPoolMetrics> metricsList = new ArrayList<>();

        // 1. 直接注册的 ThreadPoolExecutor Bean
        Map<String, ThreadPoolExecutor> executorBeans = applicationContext.getBeansOfType(ThreadPoolExecutor.class);
        for (Map.Entry<String, ThreadPoolExecutor> entry : executorBeans.entrySet()) {
            try {
                ThreadPoolMetrics metrics = buildMetrics(entry.getKey(), entry.getValue());
                metricsList.add(metrics);
            } catch (Exception e) {
                logger.warn("Failed to collect metrics for executor bean: {}", entry.getKey(), e);
            }
        }

        // 2. 扫描所有Bean，查找包含 getExecutor() 方法返回 ThreadPoolExecutor 的服务
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            // 跳过已处理的 ThreadPoolExecutor Bean
            if (executorBeans.containsKey(beanName)) {
                continue;
            }

            try {
                Object bean = applicationContext.getBean(beanName);
                ThreadPoolExecutor executor = extractExecutorFromBean(bean);
                if (executor != null) {
                    // 避免重复添加同一个 executor 实例
                    boolean alreadyAdded = metricsList.stream()
                            .anyMatch(m -> m.getPoolName().equals(beanName));
                    if (!alreadyAdded) {
                        ThreadPoolMetrics metrics = buildMetrics(beanName, executor);
                        metricsList.add(metrics);
                    }
                }
            } catch (Exception e) {
                // 忽略无法获取的 Bean（如代理、懒加载等情况）
                logger.trace("Skipping bean: {} due to: {}", beanName, e.getMessage());
            }
        }

        return metricsList;
    }

    /**
     * 收集指定线程池的指标
     */
    private ThreadPoolMetrics collectThreadPoolMetrics(String poolName) {
        // 1. 尝试直接获取 ThreadPoolExecutor Bean
        try {
            ThreadPoolExecutor executor = applicationContext.getBean(poolName, ThreadPoolExecutor.class);
            return buildMetrics(poolName, executor);
        } catch (Exception e) {
            // 不是直接的 ThreadPoolExecutor Bean
        }

        // 2. 尝试获取包含 getExecutor() 方法的 Bean
        try {
            Object bean = applicationContext.getBean(poolName);
            ThreadPoolExecutor executor = extractExecutorFromBean(bean);
            if (executor != null) {
                return buildMetrics(poolName, executor);
            }
        } catch (Exception e) {
            logger.debug("Failed to get bean: {}", poolName, e);
        }

        return null;
    }

    /**
     * 从 Bean 中提取 ThreadPoolExecutor
     * 支持 getExecutor() 或 getThreadPoolExecutor() 方法
     */
    private ThreadPoolExecutor extractExecutorFromBean(Object bean) {
        if (bean == null) {
            return null;
        }

        Class<?> clazz = bean.getClass();
        String[] methodNames = {"getExecutor", "getThreadPoolExecutor", "threadPoolExecutor", "executor"};

        for (String methodName : methodNames) {
            try {
                Method method = clazz.getMethod(methodName);
                Object result = method.invoke(bean);
                if (result instanceof ThreadPoolExecutor) {
                    return (ThreadPoolExecutor) result;
                }
            } catch (NoSuchMethodException e) {
                // 方法不存在，尝试下一个
            } catch (Exception e) {
                logger.trace("Failed to invoke {} on {}: {}", methodName, clazz.getSimpleName(), e.getMessage());
            }
        }

        return null;
    }

    /**
     * 构建线程池指标对象
     */
    private ThreadPoolMetrics buildMetrics(String poolName, ThreadPoolExecutor executor) {
        ThreadPoolMetrics metrics = new ThreadPoolMetrics();

        metrics.setPoolName(poolName);
        metrics.setCorePoolSize(executor.getCorePoolSize());
        metrics.setMaximumPoolSize(executor.getMaximumPoolSize());
        metrics.setActiveCount(executor.getActiveCount());
        metrics.setPoolSize(executor.getPoolSize());
        metrics.setLargestPoolSize(executor.getLargestPoolSize());
        metrics.setCompletedTaskCount(executor.getCompletedTaskCount());
        metrics.setTaskCount(executor.getTaskCount());

        // 队列信息
        BlockingQueue<Runnable> queue = executor.getQueue();
        metrics.setQueueSize(queue.size());
        metrics.setQueueRemainingCapacity(queue.remainingCapacity());
        metrics.setQueueType(queue.getClass().getSimpleName());

        // 拒绝策略
        metrics.setRejectedExecutionHandler(executor.getRejectedExecutionHandler().getClass().getSimpleName());

        // 状态
        metrics.setShutdown(executor.isShutdown());
        metrics.setTerminated(executor.isTerminated());
        metrics.setTerminating(executor.isTerminating());

        // 计算使用率
        int maxPoolSize = executor.getMaximumPoolSize();
        if (maxPoolSize > 0) {
            metrics.setPoolUsageRate((double) executor.getActiveCount() / maxPoolSize);
        }

        int totalQueueCapacity = queue.size() + queue.remainingCapacity();
        if (totalQueueCapacity > 0) {
            metrics.setQueueUsageRate((double) queue.size() / totalQueueCapacity);
        }

        return metrics;
    }
}
