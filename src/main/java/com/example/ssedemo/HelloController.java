package com.example.ssedemo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Controller
public class HelloController {
    @Autowired
    private ObjectMapper objectMapper;
    @RequestMapping(value = "sse")
    @ResponseBody
    public String getStreamDataImprove(HttpServletResponse httpServletResponse) throws JsonProcessingException {
        httpServletResponse.setContentType("text/event-stream");
        httpServletResponse.setCharacterEncoding("utf-8");
        String s = "";
        while (true) {
            Map<String, Object> map = new HashMap<>();
            map.put("a",1);
            map.put("b",LocalDateTime.now());
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
        map.put("a",1);
        map.put("b",LocalDateTime.now());
        String json = objectMapper.writeValueAsString(map);
        s += "data: " + json + "\n\n";
        PrintWriter pw = httpServletResponse.getWriter();
        pw.write(s);
        pw.flush();
    }
}
