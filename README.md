# ThreadPool Monitor Spring Boot Starter

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![JDK](https://img.shields.io/badge/JDK-1.8+-green.svg)](https://www.oracle.com/java/technologies/javase-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)

零侵入式 Spring Boot 线程池监控模块，通过 Actuator 端点暴露线程池运行状态。

## 特性

- **零侵入** - 主项目只需添加依赖，无需修改任何代码
- **自动发现** - 自动扫描所有 `ThreadPoolExecutor` Bean 及包含 `getExecutor()` 方法的服务
- **健壮异常处理** - 监控模块异常不会影响主业务
- **开箱即用** - 基于 Spring Boot AutoConfiguration，引入即生效
- **运维友好** - 支持 curl 直接查询，返回 JSON 格式数据

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.github.bahtya</groupId>
    <artifactId>threadpool-monitor-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置 Actuator 端点

在 `application.yml` 中添加：

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,threadpool
  endpoint:
    threadpool:
      enabled: true
```

或 `application.properties`：

```properties
management.endpoints.web.exposure.include=health,info,threadpool
management.endpoint.threadpool.enabled=true
```

### 3. 访问端点

启动应用后，访问以下端点：

```bash
# 获取所有线程池状态
curl http://localhost:8080/actuator/threadpool

# 获取指定线程池状态（Bean名称）
curl http://localhost:8080/actuator/threadpool/simpleThreadPoolService
```

## 返回示例

```json
{
  "success": true,
  "count": 1,
  "message": "OK",
  "pools": [
    {
      "poolName": "simpleThreadPoolService",
      "corePoolSize": 32,
      "maximumPoolSize": 150,
      "activeCount": 5,
      "poolSize": 32,
      "largestPoolSize": 45,
      "completedTaskCount": 12580,
      "taskCount": 12585,
      "queueSize": 3,
      "queueRemainingCapacity": 747,
      "queueType": "ArrayBlockingQueue",
      "rejectedExecutionHandler": "CallerRunsPolicy",
      "shutdown": false,
      "terminated": false,
      "terminating": false,
      "poolUsageRate": 0.033,
      "queueUsageRate": 0.004,
      "collectTime": "2026-02-04 10:30:00"
    }
  ]
}
```

## 指标说明

| 字段 | 说明 |
|------|------|
| `poolName` | 线程池 Bean 名称 |
| `corePoolSize` | 核心线程数 |
| `maximumPoolSize` | 最大线程数 |
| `activeCount` | 当前活跃线程数 |
| `poolSize` | 当前线程池中的线程数 |
| `largestPoolSize` | 历史最大线程数 |
| `completedTaskCount` | 已完成任务数 |
| `taskCount` | 总提交任务数 |
| `queueSize` | 队列中等待的任务数 |
| `queueRemainingCapacity` | 队列剩余容量 |
| `queueType` | 队列类型 |
| `rejectedExecutionHandler` | 拒绝策略类型 |
| `shutdown` | 线程池是否已关闭 |
| `terminated` | 线程池是否已终止 |
| `terminating` | 线程池是否正在终止 |
| `poolUsageRate` | 线程池使用率 |
| `queueUsageRate` | 队列使用率 |
| `collectTime` | 数据采集时间 |

## 配置项

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `threadpool.monitor.enabled` | `true` | 是否启用监控模块 |

## 运维脚本示例

```bash
#!/bin/bash
# 检查线程池健康状态

RESULT=$(curl -s http://localhost:8080/actuator/threadpool)

# 获取队列使用率
QUEUE_USAGE=$(echo $RESULT | jq '.pools[0].queueUsageRate')

# 队列使用率超过80%告警
if (( $(echo "$QUEUE_USAGE > 0.8" | bc -l) )); then
    echo "WARNING: Queue usage is high: $QUEUE_USAGE"
    exit 1
fi

echo "Thread pool status: OK"
exit 0
```

## 项目结构

```
starring_monitor/
├── pom.xml                                    # 监控模块 Maven 配置
├── README.md                                  # 使用说明
├── src/main/java/com/github/bahtya/monitor/
│   ├── autoconfigure/
│   │   └── ThreadPoolMonitorAutoConfiguration.java  # 自动配置类
│   ├── dto/
│   │   └── ThreadPoolMetrics.java             # 线程池指标 DTO
│   └── endpoint/
│       └── ThreadPoolEndpoint.java            # Actuator 端点
├── src/main/resources/META-INF/
│   └── spring.factories                       # Spring Boot 自动装配
│
└── demo/                                      # 演示项目
    ├── pom.xml
    ├── README.md
    └── src/main/
        ├── java/com/example/demo/
        │   ├── DemoApplication.java           # 启动类
        │   ├── controller/
        │   │   └── DemoController.java        # 演示控制器
        │   └── service/
        │       └── SimpleThreadPoolService.java  # 线程池服务
        └── resources/
            └── application.yml                # 配置文件
```

## 本地构建与运行

### 1. 构建监控模块

```bash
cd starring_monitor
mvn clean install
```

### 2. 运行 Demo 项目

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

# 提交快速任务（10个，每个执行约100ms）
curl "http://localhost:8080/demo/submit?count=10"

# 压力测试（100个任务，每个休眠5秒）
curl "http://localhost:8080/demo/stress?count=100&sleep=5000"

# 立即查看线程池状态变化
curl http://localhost:8080/actuator/threadpool

# 查看任务统计
curl http://localhost:8080/demo/stats
```

## 集成到现有项目

### 方式一：本地安装后依赖

1. 执行 `mvn clean install` 将模块安装到本地仓库
2. 在主项目 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.github.bahtya</groupId>
    <artifactId>threadpool-monitor-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 方式二：作为子模块

在父项目 `pom.xml` 中添加模块：

```xml
<modules>
    <module>starring_monitor</module>
    <module>your-main-project</module>
</modules>
```

### 线程池服务要求

确保你的线程池服务类提供 `getExecutor()` 方法：

```java
@Service
public class YourThreadPoolService {
    private final ThreadPoolExecutor executor;
    
    // ... 构造和业务方法 ...
    
    // 监控模块通过此方法获取线程池实例
    public ThreadPoolExecutor getExecutor() {
        return executor;
    }
}
```

## 兼容性

- JDK 1.8+
- Spring Boot 2.3.x

## 许可证

[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)

## 贡献

欢迎提交 Issue 和 Pull Request！

---

**作者**: [Bahtya](https://github.com/Bahtya)
