package me.muphy.spring.mvc;

import me.muphy.spring.common.Constants;
import me.muphy.spring.mvc.servlet.DispatcherServlet;
import me.muphy.spring.mvc.servlet.HttpServletRequest;
import me.muphy.spring.mvc.servlet.HttpServletResponse;
import me.muphy.spring.util.EnvironmentUtils;
import me.muphy.spring.util.IOUtils;
import me.muphy.spring.util.LogUtils;
import me.muphy.spring.util.LogFileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Tomcat extends Thread {
    private static DispatcherServlet servlet;
    private int port;
    private ServerSocket serverSocket;

    public Tomcat() {
        String property = EnvironmentUtils.getPropertyWithDefaultValue(Constants.SERVER_PORT, "8080");
        try {
            port = Integer.parseInt(property);
        } catch (Exception e) {
            port = 8080;
            EnvironmentUtils.setProperty(Constants.SERVER_PORT, String.valueOf(port));
        }
    }

    @Override
    public void run() {
        servlet = new DispatcherServlet();//这里应该要扫描所有的Servlet并全部初始化好，但是想想还是算了
        servlet.init();
        try {
            serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            LogUtils.d(getClass().getSimpleName(), "tomcat 启动失败：" + e.getMessage());
            LogFileUtils.printStackTrace(e);
            return;
        }
        LogUtils.d(getClass().getSimpleName(), "spring mvc 初始化完成");
        LogUtils.d(getClass().getSimpleName(), "tomcat 已启动，监听端口号：" + this.port);
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                new HttpConnectThread(socket).start();
            } catch (IOException e) {
                LogFileUtils.printStackTrace(e);
            }
        }
    }

    private static class HttpConnectThread extends Thread {

        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public HttpConnectThread(Socket socket) throws IOException {
            this.socket = socket;
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        }

        @Override
        public void run() {
            HttpServletRequest request = new HttpServletRequest(inputStream, socket);
            HttpServletResponse response = new HttpServletResponse(outputStream, socket);
            try {
                servlet.service(request, response);
                outputStream.flush();
            } catch (Exception e) {
                //// TODO: 2021/9/11 返回500
                LogFileUtils.printStackTrace(e);
            } finally {
                IOUtils.close(inputStream, outputStream, socket);
            }
        }
    }
}
