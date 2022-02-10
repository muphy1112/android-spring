package me.muphy.spring.mvc.servlet;

import me.muphy.spring.util.LogUtils;
import me.muphy.spring.util.LogFileUtils;

import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 2019/6/26
 * 莫非
 */
public class HttpServletRequest {
    private static Pattern urlPattern = Pattern.compile("(\\w+) ([^\\?\\ ]+)\\??([^\n\\ ]+)? .+");
    private static Pattern headPattern = Pattern.compile("([^:]+): *(.+)");
    private static Pattern hostPattern = Pattern.compile("([\\-\\w\\.]+):?(\\d+)?");
    private static Pattern cookiePattern = Pattern.compile("([^\\=]+)=([^\\;]+);?");
    private static Pattern parameterPattern = Pattern.compile("([^\\=]+)=([^\\;]+)?&?");
    private final Socket socket;

    private boolean parsed = false;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> cookies = new HashMap<>();
    private Map<String, String[]> queryParameterMap = new HashMap<>();
    private Map<String, String> formData = new HashMap<>();
    private String content = "";
    private String url = "";
    private String host = "";
    private int port = 80;
    private String method;
    private String queryString = "";
    private String body = "";

    public HttpServletRequest(InputStream inputStream, Socket socket) {
        this.socket = socket;
        byte[] buf = new byte[1024];
        int len;
        try {
            if ((len = inputStream.read(buf)) > 0) {
                content += new String(buf, 0, len);
            }
            //Log.d(getClass().getSimpleName(), content);
            parse();
        } catch (Exception e) {
            LogFileUtils.printStackTrace(e);
        }
    }

    private void parse() {
        if (parsed) {
            return;
        }
        parsed = true;
        String[] headAndBody = content.split("\r?\n\\?\n");
        String[] head = headAndBody[0].split("\n");
        Matcher matcher = urlPattern.matcher(head[0]);
        if (!matcher.find()) {
            return;
        }
        this.method = matcher.group(1);
        this.url = matcher.group(2);
        LogUtils.d(getClass().getSimpleName(), "发起请求，url：" + matcher.group());
        if (matcher.group(3) != null) {
            this.queryString = matcher.group(3);
            matcher = parameterPattern.matcher(matcher.group(3));
            while (matcher.find()) {
                String[] strings = queryParameterMap.get(matcher.group(1));
                if (strings == null) {
                    queryParameterMap.put(matcher.group(1), new String[]{matcher.group(2)});
                } else {
                    String[] nStrings = new String[strings.length + 1];
                    for (int i = 0; i < strings.length; i++) {
                        nStrings[i] = strings[i];
                    }
                    nStrings[strings.length] = matcher.group(2);
                    queryParameterMap.put(matcher.group(1), nStrings);
                }
            }
        }
        for (int i = 1; i < head.length; i++) {
            matcher = headPattern.matcher(head[1]);
            if (matcher.find()) {
                if (matcher.group(2) != null) {
                    headers.put(matcher.group(1).toLowerCase(), matcher.group(2));
                }
            }
        }
        if (!headers.containsKey("host")) {
            return;
        }
        matcher = hostPattern.matcher(headers.get("host"));
        if (!matcher.find()) {
            return;
        }
        this.host = matcher.group(1);
        if (matcher.group(2) != null) {
            this.port = Integer.parseInt(matcher.group(2));
        }
        headers.remove("host");
        String cookie = headers.get("cookie");
        if (cookie != null) {
            matcher = cookiePattern.matcher(cookie);
            while (matcher.find()) {
                cookies.put(matcher.group(1), matcher.group(2));
            }
            headers.remove("cookie");
        }

        if (headAndBody.length > 1) {
            this.body = headAndBody[1];
        }
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public Map<String, String[]> getParameterMap() {
        return queryParameterMap;
    }

    public Map<String, String> getFormData() {
        return formData;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getBody() {
        return body;
    }
}
