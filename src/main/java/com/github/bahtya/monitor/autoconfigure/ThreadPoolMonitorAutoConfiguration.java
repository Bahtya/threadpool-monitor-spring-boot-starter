package com.github.bahtya.monitor.autoconfigure;

import com.github.bahtya.monitor.endpoint.ThreadPoolEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 线程池监控自动配置类
 *
 * <p>配置项：
 * <ul>
 *   <li>threadpool.monitor.enabled=true/false - 是否启用监控（默认true）</li>
 * </ul>
 *
 * <p>Actuator 配置示例（application.yml）：
 * <pre>
 * management:
 *   endpoints:
 *     web:
 *       exposure:
 *         include: health,info,threadpool
 *   endpoint:
 *     threadpool:
 *       enabled: true
 * </pre>
 *
 * @author Bahtya
 * @since 1.0.0
 */
@Configuration
@ConditionalOnClass(name = "org.springframework.boot.actuate.endpoint.annotation.Endpoint")
@ConditionalOnProperty(prefix = "threadpool.monitor", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ThreadPoolMonitorAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolMonitorAutoConfiguration.class);

    /**
     * 注册线程池监控端点
     *
     * @param applicationContext Spring 应用上下文
     * @return ThreadPoolEndpoint 实例
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnAvailableEndpoint(endpoint = ThreadPoolEndpoint.class)
    public ThreadPoolEndpoint threadPoolEndpoint(ApplicationContext applicationContext) {
        logger.info("ThreadPool Monitor Endpoint initialized - access via /actuator/threadpool");
        return new ThreadPoolEndpoint(applicationContext);
    }
}
