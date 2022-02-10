package me.muphy.spring.ioc.scanner;

import me.muphy.spring.util.LogFileUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 安卓文件扫描器
 */
public interface ClassScanner {
    /**
     * 扫描
     *
     * @return 类
     */
    List<String> doScan(String[] packages, String[] excludeNames);

    default List<Class> doScanClass(String[] packages, String[] excludeNames) {
        List<String> strings = doScan(packages, excludeNames);
        List<Class> classList = new ArrayList<>();
        for (String s : strings) {
            try {
                Class scanClass = Class.forName(s);
                classList.add(scanClass);
            } catch (Exception e) {
                LogFileUtils.printStackTrace(e);
            }
        }
        return classList;
    }

    default List<String> doScan() {
        return doScan(null, null);
    }

    default List<Class> doScanClass() {
        return doScanClass(null, null);
    }
}