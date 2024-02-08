package com.example.ssedemo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@RestController
public class HelloController {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HelloService helloService;

    @RequestMapping(value = "sse")
    @ResponseBody
    public String getStreamDataImprove(HttpServletResponse httpServletResponse) throws JsonProcessingException {
        httpServletResponse.setContentType("text/event-stream");
        httpServletResponse.setCharacterEncoding("utf-8");
        String s = "";
        while (true) {
            Map<String, Object> map = new HashMap<>();
            map.put("a", 1);
            map.put("b", LocalDateTime.now());
            String json = objectMapper.writeValueAsString(map);
            s = "data: " + json + "\n\n";
            try {
                PrintWriter pw = httpServletResponse.getWriter();
                Thread.sleep(1000L);
                pw.write(s);
                pw.flush();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @RequestMapping(value = "sse-retry")
    @ResponseBody
    public void getStreamDataImprove1(HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.setContentType("text/event-stream");
        httpServletResponse.setCharacterEncoding("utf-8");
        // 未来的两秒钟没有收到新消息，客户端每两秒就向服务器建立连接
        String s = "retry: 2000\n";
        Map<String, Object> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", LocalDateTime.now());
        String json = objectMapper.writeValueAsString(map);
        s += "data: " + json + "\n\n";
        PrintWriter pw = httpServletResponse.getWriter();
        pw.write(s);
        pw.flush();
    }

    @GetMapping(value = "test/{clientId}", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public SseEmitter test(@PathVariable("clientId") String clientId) {
        final SseEmitter emitter = helloService.getConn(clientId);
        CompletableFuture.runAsync(() -> {
            try {
                helloService.send(clientId);
            } catch (Exception e) {
                throw new RuntimeException("推送数据异常");
            }
        });

        return emitter;
    }

//    @GetMapping("closeConn/{clientId}")
//    public Result<String> closeConn(@PathVariable("clientId") @ApiParam("客户端 id") String clientId) {
//        service.closeConn(clientId);
//        return Result.success("连接已关闭");
//    }

}
