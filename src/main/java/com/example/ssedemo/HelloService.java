package com.example.ssedemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class HelloService {
    private static final Map<String, SseEmitter> SSE_CACHE = new ConcurrentHashMap<>();


    public SseEmitter getConn(String clientId) {
        final SseEmitter sseEmitter = SSE_CACHE.get(clientId);

        if (sseEmitter != null) {
            return sseEmitter;
        } else {
            // 设置连接超时时间，需要配合配置项 spring.mvc.async.request-timeout: 600000 一起使用
            final SseEmitter emitter = new SseEmitter(600_000L);
            // 注册超时回调，超时后触发
            emitter.onTimeout(() -> {
                log.info("连接已超时，正准备关闭，clientId = {}", clientId);
                SSE_CACHE.remove(clientId);
            });
            // 注册完成回调，调用 emitter.complete() 触发
            emitter.onCompletion(() -> {
                log.info("连接已关闭，正准备释放，clientId = {}", clientId);
                SSE_CACHE.remove(clientId);
                log.info("连接已释放，clientId = {}", clientId);
            });
            // 注册异常回调，调用 emitter.completeWithError() 触发
            emitter.onError(throwable -> {
                log.error("连接已异常，正准备关闭，clientId = {}", clientId, throwable);
                SSE_CACHE.remove(clientId);
            });

            SSE_CACHE.put(clientId, emitter);

            return emitter;
        }
    }

    /**
     * 模拟类似于 chatGPT 的流式推送回答
     *
     * @param clientId 客户端 id
     * @throws IOException 异常
     */
    public void send(String clientId) throws IOException {
        final SseEmitter emitter = SSE_CACHE.get(clientId);
        // 推流内容到客户端
        emitter.send("此去经年", org.springframework.http.MediaType.APPLICATION_JSON);
        emitter.send("此去经年，应是良辰好景虚设");
        emitter.send("此去经年，应是良辰好景虚设，便纵有千种风情");
        emitter.send("此去经年，应是良辰好景虚设，便纵有千种风情，更与何人说");
        // 结束推流
        emitter.complete();
    }

    public void closeConn(String clientId) {
        final SseEmitter sseEmitter = SSE_CACHE.get(clientId);
        if (sseEmitter != null) {
            sseEmitter.complete();
        }
    }

}
