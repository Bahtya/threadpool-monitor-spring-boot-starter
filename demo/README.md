# ThreadPool Monitor Demo

这是一个演示项目，展示如何使用 `threadpool-monitor-spring-boot-starter` 监控模块。

## 快速开始

### 1. 先构建监控模块

```bash
cd ..
mvn clean install
```

### 2. 运行演示项目

```bash
cd demo
mvn spring-boot:run
```

### 3. 测试监控端点

```bash
# 查看所有线程池状态
curl http://localhost:8080/actuator/threadpool

# 查看指定线程池状态
curl http://localhost:8080/actuator/threadpool/simpleThreadPoolService
```

## 演示接口

### 提交快速任务

```bash
# 提交 10 个快速任务（每个任务执行约 100ms）
curl "http://localhost:8080/demo/submit?count=10"
```

### 压力测试

```bash
# 提交 100 个长任务（每个任务休眠 5 秒）
curl "http://localhost:8080/demo/stress?count=1000&sleep=5000"

# 立即查看线程池状态
curl http://localhost:8080/actuator/threadpool
```

### 查看任务统计

```bash
curl http://localhost:8080/demo/stats
```

## 监控返回示例

```json
{
  "success": true,
  "count": 1,
  "pools": [
    {
      "poolName": "simpleThreadPoolService",
      "corePoolSize": 32,
      "maximumPoolSize": 150,
      "activeCount": 50,
      "poolSize": 50,
      "largestPoolSize": 50,
      "completedTaskCount": 100,
      "taskCount": 150,
      "queueSize": 100,
      "queueRemainingCapacity": 650,
      "queueType": "ArrayBlockingQueue",
      "rejectedExecutionHandler": "CallerRunsPolicy",
      "shutdown": false,
      "terminated": false,
      "terminating": false,
      "poolUsageRate": 0.33,
      "queueUsageRate": 0.13,
      "collectTime": "2024-01-15 10:30:00"
    }
  ],
  "message": "OK"
}
```

## 运维脚本示例

```bash
#!/bin/bash
# monitor_threadpool.sh - 线程池监控脚本

HOST="localhost:8080"
ENDPOINT="/actuator/threadpool"

while true; do
    echo "========== $(date) =========="
    curl -s "http://${HOST}${ENDPOINT}" | python -m json.tool
    sleep 5
done
```
