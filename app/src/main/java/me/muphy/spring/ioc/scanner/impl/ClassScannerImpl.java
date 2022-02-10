package me.muphy.spring.ioc.scanner.impl;

import me.muphy.spring.ioc.scanner.ClassScanner;
import me.muphy.spring.util.ClassUtils;
import me.muphy.spring.util.PropertyFileUtils;
import me.muphy.spring.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Dex文件扫描类
 */
public class ClassScannerImpl implements ClassScanner {

    @Override
    public List<String> doScan(String[] packages, String[] excludeNames) {
        List<String> scanPackages = new ArrayList<>();
        String[] basePackages = PropertyFileUtils.getProperty("spring.scan.package.basePackages").split(",");
        addToList(packages, scanPackages);
        addToList(basePackages, scanPackages);

        List<String> excludeNameList = new ArrayList<>();
        String[] excludes = PropertyFileUtils.getProperty("spring.scan.package.excludeNames").split(",");
        addToList(excludeNames, excludeNameList);
        addToList(excludes, excludeNameList);

        try {
            List<String> classNames = ClassUtils.getClassNames(scanPackages);
            if (!excludeNameList.isEmpty()) {
                Iterator<String> iterator = classNames.iterator();
                while (iterator.hasNext()) {
                    String next = iterator.next();
                    for (String name : excludeNameList) {
                        if (StringUtils.isNotEmpty(name) && next.startsWith(name)) {
                            iterator.remove();
                            break;
                        }
                    }
                }
            }
            return classNames;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addToList(String[] packages, List<String> scanPackages) {
        if (packages != null) {
            for (String pkg : packages) {
                if (StringUtils.isEmpty(pkg) || scanPackages.contains(pkg)) {
                    continue;
                }
                scanPackages.add(pkg);
            }
        }
    }
}
