package me.muphy.spring.mvc.servlet;

import com.alibaba.fastjson.JSON;
import me.muphy.spring.annotation.Component;
import me.muphy.spring.annotation.Controller;
import me.muphy.spring.annotation.RequestMapping;
import me.muphy.spring.ioc.factory.SingletonRegistry;
import me.muphy.spring.mvc.HandlerMapping;
import me.muphy.spring.util.FileUtils;
import me.muphy.spring.util.ClassUtils;
import me.muphy.spring.util.LogUtils;
import me.muphy.spring.util.LogFileUtils;
import me.muphy.spring.util.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DispatcherServlet implements Servlet {

    private Properties contextConfig = new Properties();
    private List<String> classNames = new ArrayList<>();
    private static List<HandlerMapping> handlerMappings;
    private static List<String> staticFiles;

    public DispatcherServlet() {
        handlerMappings = new ArrayList<>();
    }

    public static List<HandlerMapping> getHandlerMappings() {
        return handlerMappings;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
        doDispatch(request, response);
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String contextPath = "";// req.getContextPath();
        String url = req.getUrl().replace(contextPath, "").replaceAll("/+", "/");
        HandlerMapping handlerMapping = null;
        for (HandlerMapping mapping : handlerMappings) {
            Matcher matcher = mapping.getUrl().matcher(url);
            if (matcher.matches()) {
                handlerMapping = mapping;
                break;
            }
        }

        //静态资源执行
        if (handlerMapping == null) {
            String file = ("static" + File.separator + url).replaceAll(File.separator + "+", File.separator);
            //String file = ("static/" + url).replaceAll("/+", "/");
            InputStream inputStream = null;
            try {
                inputStream = FileUtils.getHttpStaticFileInputStream(file);
            } catch (Exception e) {
                LogFileUtils.printStackTrace(e);
            }
            if (inputStream == null) {
                String url404 = "/404.html";
                if (url404.equalsIgnoreCase(url)) {
                    resp.sendError(404);
                } else {
                    resp.sendRedirect(url404);
                }
                return;
            }
            String fileExtension = FileUtils.getFileExtension(file);
            String contentType = ContentType.getContentType(fileExtension);
            resp.setContentType(contentType);
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) != -1) {
                resp.write(buf, 0, len);
                //stringBuilder.append(new String(buf, 0, len, "UTF-8"));
            }
            return;
        }


        Class<?>[] paramTypes = handlerMapping.getParamTypes();
        Object[] paramValues = new Object[paramTypes.length];
        Map<String, String[]> params = req.getParameterMap();
        Map<String, Integer> mapping = handlerMapping.getParamIndexMapping();
        for (Map.Entry<String, String[]> param : params.entrySet()) {
            if (!mapping.containsKey(param.getKey())) {
                continue;
            }
            String[] value = param.getValue();
            String val = null;
            if (value == null || value.length == 0) {
                val = value[0];
            }
            int index = mapping.get(param.getKey());
            paramValues[index] = parseObject(paramTypes[index], val);
        }
        if (mapping.containsKey(HttpServletRequest.class.getName())) {
            int reqIdx = mapping.get(HttpServletRequest.class.getName());
            paramValues[reqIdx] = req;
        }
        if (mapping.containsKey(HttpServletResponse.class.getName())) {
            int respIdx = mapping.get(HttpServletResponse.class.getName());
            paramValues[respIdx] = resp;
        }
        for (int i = 0; i < paramValues.length; i++) {
            if (paramValues[i] == null) {
                paramValues[i] = parseObject(paramTypes[i], "");
            }
        }
        Object invoke = handlerMapping.getMethod().invoke(handlerMapping.getController(), paramValues);
        if (invoke != null) {
            if (ClassUtils.isBaseObject(invoke)) {
                resp.setContentType("text/html");
                resp.write(invoke.toString());
                return;
            }
            resp.setContentType("application/json");
            String jsonString = JSON.toJSONString(invoke);
            resp.write(jsonString);
        }
    }

    private Object parseObject(Class<?> paramType, String value) {
        if (Character.class == paramType || char.class == paramType) {
            if (StringUtils.isEmpty(value)) {
                return '\0';
            }
            return value.charAt(0);
        }
        if (CharSequence.class.isAssignableFrom(paramType)) {
            if (StringUtils.isEmpty(value)) {
                return "";
            }
            return value;
        }
        if (Number.class.isAssignableFrom(paramType) || paramType.isPrimitive()) {
            if (StringUtils.isEmpty(value)) {
                value = "0";
            }
        } else if (Collection.class.isAssignableFrom(paramType) || paramType.isArray()) {
            if (StringUtils.isEmpty(value)) {
                value = "[]";
            }
        } else if (Map.class.isAssignableFrom(paramType)) {
            if (StringUtils.isEmpty(value)) {
                value = "{}";
            }
        } else if (StringUtils.isEmpty(value)) {
            value = "{}";
        }
        return JSON.parseObject(value, paramType);
    }

    public void init() {
        initHandlerMapping();
        //initStaticFiles();//考虑到文件可能太多占内存就不加载到内存了
    }

    private void initStaticFiles() {
        staticFiles = FileUtils.getHttpStaticFileList("static");
    }

    private void initHandlerMapping() {
        for (Map.Entry<String, Object> entry : SingletonRegistry.getFactoryMap().entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            if (!clazz.isAnnotationPresent(Controller.class)) {
                continue;
            }
            String baseUrl = "";
            if (clazz.isAnnotationPresent(RequestMapping.class)) {
                baseUrl = clazz.getAnnotation(RequestMapping.class).value();
            }
            for (Method method : clazz.getMethods()) {
                if (method.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                    String url = ("/" + baseUrl + "/" + requestMapping.value()).replaceAll("\\/+", "/");
                    //handlerMappings.put(url.replaceAll("\\/+", "/"), method);
                    Pattern pattern = Pattern.compile(url);
                    handlerMappings.add(new HandlerMapping(pattern, entry.getValue(), method));
                    LogUtils.d(getClass().getSimpleName(), "Mapred url:" + pattern);
                }
            }
        }
    }

}
