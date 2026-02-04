package com.github.bahtya.monitor.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 监控响应包装类
 * <p>
 * 统一的响应格式，包含状态码、消息和数据
 * </p>
 *
 * @author Bahtya
 * @since 1.0.0
 */
public class MonitorResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码：200-成功，500-失败
     */
    private int code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 响应时间
     */
    private String timestamp;

    public MonitorResponse() {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static <T> MonitorResponse<T> success(T data) {
        MonitorResponse<T> response = new MonitorResponse<>();
        response.setCode(200);
        response.setMessage("success");
        response.setData(data);
        return response;
    }

    public static <T> MonitorResponse<T> success(String message, T data) {
        MonitorResponse<T> response = new MonitorResponse<>();
        response.setCode(200);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static <T> MonitorResponse<T> error(String message) {
        MonitorResponse<T> response = new MonitorResponse<>();
        response.setCode(500);
        response.setMessage(message);
        response.setData(null);
        return response;
    }

    public static <T> MonitorResponse<T> error(int code, String message) {
        MonitorResponse<T> response = new MonitorResponse<>();
        response.setCode(code);
        response.setMessage(message);
        response.setData(null);
        return response;
    }

    // ==================== Getters and Setters ====================

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
