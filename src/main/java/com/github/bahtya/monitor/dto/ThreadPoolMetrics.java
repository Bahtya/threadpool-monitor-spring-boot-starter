package com.github.bahtya.monitor.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 线程池指标数据传输对象
 * 用于封装线程池的运行时状态信息
 *
 * @author Bahtya
 * @since 1.0.0
 */
public class ThreadPoolMetrics implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 线程池名称/Bean名称
     */
    private String poolName;

    /**
     * 核心线程数
     */
    private int corePoolSize;

    /**
     * 最大线程数
     */
    private int maximumPoolSize;

    /**
     * 当前活跃线程数
     */
    private int activeCount;

    /**
     * 当前线程池中的线程数
     */
    private int poolSize;

    /**
     * 线程池历史最大线程数
     */
    private int largestPoolSize;

    /**
     * 已完成任务数
     */
    private long completedTaskCount;

    /**
     * 总任务数（已提交）
     */
    private long taskCount;

    /**
     * 队列中等待的任务数
     */
    private int queueSize;

    /**
     * 队列剩余容量
     */
    private int queueRemainingCapacity;

    /**
     * 队列类型
     */
    private String queueType;

    /**
     * 拒绝策略类型
     */
    private String rejectedExecutionHandler;

    /**
     * 线程池是否已关闭
     */
    private boolean shutdown;

    /**
     * 线程池是否已终止
     */
    private boolean terminated;

    /**
     * 线程池是否正在终止
     */
    private boolean terminating;

    /**
     * 线程池使用率 (activeCount / maximumPoolSize)
     */
    private double poolUsageRate;

    /**
     * 队列使用率 (queueSize / (queueSize + queueRemainingCapacity))
     */
    private double queueUsageRate;

    /**
     * 采集时间
     */
    private String collectTime;

    public ThreadPoolMetrics() {
        this.collectTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // ==================== Getters and Setters ====================

    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public int getActiveCount() {
        return activeCount;
    }

    public void setActiveCount(int activeCount) {
        this.activeCount = activeCount;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public int getLargestPoolSize() {
        return largestPoolSize;
    }

    public void setLargestPoolSize(int largestPoolSize) {
        this.largestPoolSize = largestPoolSize;
    }

    public long getCompletedTaskCount() {
        return completedTaskCount;
    }

    public void setCompletedTaskCount(long completedTaskCount) {
        this.completedTaskCount = completedTaskCount;
    }

    public long getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(long taskCount) {
        this.taskCount = taskCount;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public int getQueueRemainingCapacity() {
        return queueRemainingCapacity;
    }

    public void setQueueRemainingCapacity(int queueRemainingCapacity) {
        this.queueRemainingCapacity = queueRemainingCapacity;
    }

    public String getQueueType() {
        return queueType;
    }

    public void setQueueType(String queueType) {
        this.queueType = queueType;
    }

    public String getRejectedExecutionHandler() {
        return rejectedExecutionHandler;
    }

    public void setRejectedExecutionHandler(String rejectedExecutionHandler) {
        this.rejectedExecutionHandler = rejectedExecutionHandler;
    }

    public boolean isShutdown() {
        return shutdown;
    }

    public void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }

    public boolean isTerminated() {
        return terminated;
    }

    public void setTerminated(boolean terminated) {
        this.terminated = terminated;
    }

    public boolean isTerminating() {
        return terminating;
    }

    public void setTerminating(boolean terminating) {
        this.terminating = terminating;
    }

    public double getPoolUsageRate() {
        return poolUsageRate;
    }

    public void setPoolUsageRate(double poolUsageRate) {
        this.poolUsageRate = poolUsageRate;
    }

    public double getQueueUsageRate() {
        return queueUsageRate;
    }

    public void setQueueUsageRate(double queueUsageRate) {
        this.queueUsageRate = queueUsageRate;
    }

    public String getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(String collectTime) {
        this.collectTime = collectTime;
    }

    @Override
    public String toString() {
        return "ThreadPoolMetrics{" +
                "poolName='" + poolName + '\'' +
                ", corePoolSize=" + corePoolSize +
                ", maximumPoolSize=" + maximumPoolSize +
                ", activeCount=" + activeCount +
                ", poolSize=" + poolSize +
                ", largestPoolSize=" + largestPoolSize +
                ", completedTaskCount=" + completedTaskCount +
                ", taskCount=" + taskCount +
                ", queueSize=" + queueSize +
                ", queueRemainingCapacity=" + queueRemainingCapacity +
                ", queueType='" + queueType + '\'' +
                ", rejectedExecutionHandler='" + rejectedExecutionHandler + '\'' +
                ", shutdown=" + shutdown +
                ", terminated=" + terminated +
                ", terminating=" + terminating +
                ", poolUsageRate=" + String.format("%.2f%%", poolUsageRate * 100) +
                ", queueUsageRate=" + String.format("%.2f%%", queueUsageRate * 100) +
                ", collectTime='" + collectTime + '\'' +
                '}';
    }
}
