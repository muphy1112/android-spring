package me.muphy.spring.mvc.servlet;

import me.muphy.spring.util.StringUtils;
import me.muphy.spring.util.ValidateUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

/**
 * 2019/6/26
 * 莫非
 */
public class HttpServletResponse {

    private final OutputStream outputStream;
    private final Socket socket;

    private String contentType = "text/html;";
    private String charsetName = "UTF-8";
    private int statusCode = 200;
    private boolean isFirstWrite = true;

    public HttpServletResponse(OutputStream outputStream, Socket socket) {
        this.outputStream = outputStream;
        this.socket = socket;
    }

    public void sendRedirect(String url) {
        if (StringUtils.isEmpty(url)) {
            url = "/index.html";
        }
        if (!url.startsWith("/")) {
            url = "/" + url;
        }
        try {
            outputStream.write("HTTP/1.1 302 Found\nExpires: 0\nCache-Control: no-cache\n".getBytes());
            if (ValidateUtils.isUrl(url)) {
                outputStream.write(("Location: " + url + "\n").getBytes());
            } else {
                outputStream.write(("Location: http://" + socket.getLocalAddress().getHostAddress() + ":" + socket.getLocalPort() + url + "\n").getBytes());
            }
            outputStream.write(("Content-Length: 0\n").getBytes());
            outputStream.write(("Content-Type: " + contentType + ";charset=" + charsetName + "\n\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendError(int sc) {
        sendError(sc, "Server internal error!");
    }

    public void sendError(int sc, String msg) {
        if (StringUtils.isEmpty(msg)) {
            msg = "Server internal error!";
        }
        try {
            outputStream.write(("HTTP/1.1 " + sc + " OK\n").getBytes());
            outputStream.write(("Content-Type: " + contentType + ";charset=" + charsetName + "\n\n").getBytes());
            outputStream.write(msg.getBytes(charsetName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(String s) {
        byte[] bytes = new byte[0];
        try {
            if (StringUtils.isNotEmpty(s)) {
                bytes = s.getBytes(charsetName);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        write(bytes);
    }

    public void write(byte[] bytes) {
        write(bytes, 0, bytes.length);
    }

    public void write(byte[] bytes, int off, int len) {
        try {
            if (isFirstWrite) {
                isFirstWrite = false;
                outputStream.write(("HTTP/1.1 " + statusCode + " OK\n").getBytes());
                outputStream.write(("Content-Type: " + contentType + ";charset=" + charsetName + "\n\n").getBytes());
            }
            if (bytes == null || bytes.length == 0) {
                return;
            }
            outputStream.write(bytes, off, len);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }

    public OutputStream getWriter() {
        return outputStream;
    }
}
