package me.muphy.spring.mvc.servlet;

import me.muphy.spring.util.StringUtils;

/**
 * 2019/6/26
 * 莫非
 */
public interface Servlet {

    default void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if(StringUtils.isEmpty(request.getMethod())){
            throw new RuntimeException("当前连接不是有效的http连接");
        }
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            doGet(request, response);
        } else if ("POST".equalsIgnoreCase(request.getMethod())) {
            doPost(request, response);
        } else {
            doPost(request, response);
        }
    }

    void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception;

    void doPost(HttpServletRequest request, HttpServletResponse response) throws Exception;

}
