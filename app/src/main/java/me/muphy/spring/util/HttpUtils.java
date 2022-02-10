package me.muphy.spring.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * http工具类
 */
public class HttpUtils {
    private final static String CTYPE_FORM = "application/x-www-form-urlencoded;charset=utf-8";
    private final static String CTYPE_JSON = "application/json; charset=utf-8";
    private final static String charset = "utf-8";

    private static class DefaultTrustManager implements X509TrustManager {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }
    }

    /**
     * 以application/json; charset=utf-8方式传输
     *
     * @param url
     * @return
     * @throws SocketTimeoutException
     * @throws IOException
     */
    public static String postJson(String url, String jsonContent) throws SocketTimeoutException, IOException {
        return doRequest("POST", url, jsonContent, 1000, 2000, CTYPE_JSON, null);
    }

    /**
     * POST 以application/x-www-form-urlencoded;charset=utf-8方式传输
     *
     * @param url
     * @return
     * @throws SocketTimeoutException
     * @throws IOException
     */
    public static String postForm(String url) throws SocketTimeoutException, IOException {
        return doRequest("POST", url, "", 15000, 15000, CTYPE_FORM, null);
    }

    /**
     * POST 以application/x-www-form-urlencoded;charset=utf-8方式传输
     *
     * @param url
     * @return
     * @throws SocketTimeoutException
     * @throws IOException
     */
    public static String postForm(String url, Map<String, String> params) throws SocketTimeoutException, IOException {
        return doRequest("POST", url, buildQuery(params), 15000, 15000, CTYPE_FORM, null);
    }

    /**
     * POST 以application/x-www-form-urlencoded;charset=utf-8方式传输
     *
     * @param url
     * @return
     * @throws SocketTimeoutException
     * @throws IOException
     */
    public static String getForm(String url) throws SocketTimeoutException, IOException {
        return doRequest("GET", url, "", 15000, 15000, CTYPE_FORM, null);
    }

    /**
     * POST 以application/x-www-form-urlencoded;charset=utf-8方式传输
     *
     * @param url
     * @return
     * @throws SocketTimeoutException
     * @throws IOException
     */
    public static String getForm(String url, Map<String, String> params) throws SocketTimeoutException, IOException {
        return doRequest("GET", url, buildQuery(params), 15000, 15000, CTYPE_FORM, null);
    }

    private static String doRequest(String method, String url, String requestContent, int connectTimeout,
                                    int readTimeout, String ctype, Map<String, String> headerMap) throws SocketTimeoutException, IOException {
        AtomicReference<IOException> exception = new AtomicReference<>();
        Future<String> submit = ExecutorUtils.submit(() -> {
            try {
                String innerRequest = doInnerRequest(method, url, requestContent, connectTimeout, readTimeout, ctype, headerMap);
                return innerRequest;
            } catch (IOException e) {
                exception.set(e);
            }
            return null;
        });
        try {
            String res = submit.get(Math.max(connectTimeout, readTimeout), TimeUnit.MILLISECONDS);
            LogUtils.d(HttpUtils.class.getSimpleName(), url);
            LogUtils.d(HttpUtils.class.getSimpleName(), res);
            IOException ioException = exception.get();
            if (ioException != null) {
                throw ioException;
            }
            return res;
        } catch (Exception e) {
            throw new IOException(url, e);
        }
    }

    private static String doInnerRequest(String method, String url, String requestContent, int connectTimeout,
                                         int readTimeout, String ctype, Map<String, String> headerMap) throws SocketTimeoutException, IOException {
        HttpURLConnection conn = null;
        OutputStream out = null;
        String rsp = null;
        try {
            conn = getConnection(new URL(url), method, ctype, headerMap);
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            if (requestContent != null && !requestContent.isEmpty()) {
                out = conn.getOutputStream();
                out.write(requestContent.getBytes(charset));
            }
            rsp = getResponseAsString(conn);
        } catch (Exception e) {
            LogUtils.e(HttpUtils.class.getSimpleName(), url + " err>" + e.getMessage());
            LogFileUtils.printStackTrace(e);
        } finally {
            if (out != null) {
                out.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
            conn = null;
        }
        if (rsp == null || !rsp.contains("true")) {
            LogUtils.e(HttpUtils.class.getSimpleName(), url + " err>" + rsp);
        }
        return rsp;
    }

    private static HttpURLConnection getConnection(URL url, String method, String ctype, Map<String, String> headerMap) throws IOException {
        HttpURLConnection conn;
        if ("https".equals(url.getProtocol())) {
            SSLContext ctx;
            try {
                ctx = SSLContext.getInstance("TLS");
                ctx.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()}, new SecureRandom());
            } catch (Exception e) {
                throw new IOException(e);
            }
            HttpsURLConnection connHttps = (HttpsURLConnection) url.openConnection();
            connHttps.setSSLSocketFactory(ctx.getSocketFactory());
            connHttps.setHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            conn = connHttps;
        } else {
            conn = (HttpURLConnection) url.openConnection();
        }
        conn.setRequestMethod(method);
        conn.setDoInput(true);
        if ("POST".equals(method.toUpperCase())) {
            conn.setDoOutput(true);
        }
        conn.setRequestProperty("Accept", "text/xml,text/javascript,text/html,application/json");
        conn.setRequestProperty("Content-Type", ctype);
        if (headerMap != null) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        return conn;
    }

    private static String getResponseAsString(HttpURLConnection conn)
            throws IOException {
        InputStream es = conn.getErrorStream();
        if (es == null) {
            return getStreamAsString(conn.getInputStream(), charset, conn);
        }
        String msg = getStreamAsString(es, charset, conn);
        if (msg == null || msg.isEmpty()) {
            throw new IOException(conn.getResponseCode() + ":" + conn.getResponseMessage());
        }
        return msg;
    }

    private static String getStreamAsString(InputStream stream, String charset, HttpURLConnection conn) throws IOException {
        try {
            Reader reader = new InputStreamReader(stream, charset);
            StringBuilder response = new StringBuilder();
            final char[] buff = new char[1024];
            int read = 0;
            while ((read = reader.read(buff)) > 0) {
                response.append(buff, 0, read);
            }
            return response.toString();
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    private static String buildQuery(Map<String, String> params) throws IOException {
        if (params == null || params.isEmpty()) {
            return "";
        }
        StringBuilder query = new StringBuilder();
        Set<Map.Entry<String, String>> entries = params.entrySet();
        boolean hasParam = false;
        for (Map.Entry<String, String> entry : entries) {
            String name = entry.getKey();
            String value = entry.getValue();
            if (hasParam) {
                query.append("&");
            } else {
                hasParam = true;
            }
            query.append(name).append("=").append(URLEncoder.encode(value, charset));
        }
        return query.toString();
    }
}
