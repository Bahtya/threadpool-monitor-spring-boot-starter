package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 演示应用启动类
 * 
 * 启动后访问：
 * - http://localhost:8080/actuator/threadpool - 查看所有线程池状态
 * - http://localhost:8080/actuator/threadpool/simpleThreadPoolService - 查看指定线程池
 * - http://localhost:8080/demo/submit?count=10 - 提交任务
 * - http://localhost:8080/demo/stress?count=100&sleep=5000 - 压力测试
 */
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("  ThreadPool Monitor Demo Started!");
        System.out.println("========================================");
        System.out.println("  监控端点: http://localhost:8080/actuator/threadpool");
        System.out.println("  提交任务: http://localhost:8080/demo/submit?count=10");
        System.out.println("  压力测试: http://localhost:8080/demo/stress?count=100&sleep=5000");
        System.out.println("========================================\n");
    }
}
