package com.muphy.example.controller;

import com.muphy.example.entity.EnvConfigEntity;
import com.muphy.example.service.EnvConfigService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import me.muphy.spring.annotation.Autowired;
import me.muphy.spring.annotation.Controller;
import me.muphy.spring.annotation.RequestMapping;
import me.muphy.spring.annotation.RequestParam;
import me.muphy.spring.common.Dict;
import me.muphy.spring.core.LogListener;
import me.muphy.spring.mvc.HandlerMapping;
import me.muphy.spring.mvc.servlet.DispatcherServlet;
import me.muphy.spring.mvc.servlet.HttpServletRequest;
import me.muphy.spring.mvc.servlet.HttpServletResponse;
import me.muphy.spring.util.HtmlUtils;
import me.muphy.spring.util.StringUtils;

@Controller
@RequestMapping("/test")
public class TestController implements LogListener {

    @Autowired
    private EnvConfigService envConfigService;

    BlockingQueue<String> queue = new ArrayBlockingQueue<>(100);

    @RequestMapping("/")
    public void home(HttpServletResponse response) {
        response.sendRedirect("/index.html");
    }

    @RequestMapping("/urls")
    public List<Dict<String>> url() {
        List<HandlerMapping> mappings = DispatcherServlet.getHandlerMappings();
        List<Dict<String>> dictList = new ArrayList<>();
        for (HandlerMapping mapping : mappings) {
            dictList.add(new Dict(mapping.getController().getClass().getSimpleName() + "#" + mapping.getMethod().getName(), mapping.getUrl().pattern()));
        }
        //String html = HtmlUtils.getTablePageHtml(dictList);
        return dictList;
    }

    @RequestMapping("/log")
    public void log(HttpServletResponse response, @RequestParam("timeout") int timeout) {//, @RequestParam("timeout") int timeout
        int maxTimeout = 30 * 60 * 1000;
        int minTimeout = 60 * 1000;
        if (timeout > maxTimeout) {
            timeout = maxTimeout;
        } else if (timeout < 1) {
            timeout = 5 * minTimeout;//默认
        } else if (timeout < minTimeout) {
            timeout = minTimeout;
        }
        long timeMillis = System.currentTimeMillis() + timeout;
        try {
            int i = 0;
            //response.write("<script>setInterval(function(){var a=document.body.children;while(a.length>200)a[0].remove();}, 1000)</script>");
            response.write("<script>setTimeout(()=>{document.body.style='margin-bottom: 150px; font-size: 14px;'}, 1000)</script>");
            while (true) {
                String poll = queue.poll(10, TimeUnit.SECONDS);
                if (StringUtils.isNotEmpty(poll)) {
                    poll.replaceAll("(error|ERROR|Error)", "<span style='color: red'>$1</span>");
                    String color = "#03a";
                    if (i++ % 2 == 0) {
                        color = "#3a9";
                    }
                    response.write("<div style='color: " + color + "; margin: 10px 0;'>" + poll + "<script>document.body.scrollTop = document.body.scrollHeight;console.log('" + poll + "');</script></div>");
                }
                if (System.currentTimeMillis() > timeMillis) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        response.write("时间超时，请刷新！<br>");
    }

    @Override
    public int printf(Level level, String tag, String msg, Throwable tr) {
        if (StringUtils.isEmpty(msg)) {
            if (tr == null) {
                return 0;
            }
            msg = tr.getMessage();
        } else if (tr != null) {
            msg += "<br>" + tr.getMessage();
        }
        if (StringUtils.isEmpty(tag)) {
            tag = getClass().getSimpleName();
        }
        if (queue.offer(tag + "->" + msg.replaceAll("\\s+", " "))) {
            return 1;
        }
        return 0;
    }

    @RequestMapping("/test1")
    public String test1() {
        System.out.println("test1-----------------------------");
        return "hhhh";
    }

    @RequestMapping("/test2")
    public void test2() {
        System.out.println("test2-----------------------------");
    }

    @RequestMapping("/test3")
    public void test3(HttpServletRequest request, @RequestParam("p") String p) {
        System.out.println("test3-----------------------------");
        System.out.println("p:" + p);
        System.out.println(request.getHost());
    }

    @RequestMapping("/test4")
    public void test4(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("test4-----------------------------");
        response.write("test4!!!");
    }

    @RequestMapping("/test5")
    public void test5(@RequestParam("p") String p, HttpServletRequest request, HttpServletResponse response) {
        System.out.println("test5-----------------------------p:" + p);
        response.write("test5!!!");
        response.write(p);
        response.write(request.getMethod());
    }

    @RequestMapping("/env")
    public List<Map<String, ?>> getEnv() throws Exception {
        List<Map<String, ?>> mapList = envConfigService.getEnv();
        return mapList;
    }

    @RequestMapping("/envEntities")
    public List<EnvConfigEntity> getEnvConfigEntity() throws Exception {
        List<EnvConfigEntity> envConfigEntity = envConfigService.getEnvConfigEntity();
        return envConfigEntity;
    }
}
