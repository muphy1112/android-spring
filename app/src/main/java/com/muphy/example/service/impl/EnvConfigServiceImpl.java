package com.muphy.example.service.impl;

import com.alibaba.fastjson.JSON;
import com.muphy.example.entity.EnvConfigEntity;
import com.muphy.example.repository.EnvConfigRepository;
import com.muphy.example.service.EnvConfigService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import me.muphy.spring.annotation.Autowired;
import me.muphy.spring.annotation.Remind;
import me.muphy.spring.annotation.Service;
import me.muphy.spring.util.EnvironmentUtils;
import me.muphy.spring.util.HtmlUtils;
import me.muphy.spring.util.LogUtils;
import me.muphy.spring.util.MyCollectionUtils;
import me.muphy.spring.util.StringUtils;
import me.muphy.spring.util.VariableUtils;
import me.muphy.spring.variable.VariableEventArgs;
import me.muphy.spring.variable.VariableListener;
import me.muphy.spring.variable.VariableScope;

@Service
@Remind("云端环境配置配置服务实现")
public class EnvConfigServiceImpl implements EnvConfigService {

    @Autowired
    private EnvConfigRepository envConfigRepository;

    @Remind("只是为了测试，每一行代码都有测试意义，但是没有实际意义")
    @Override
    public List<Map<String, ?>> getEnv() throws Exception {
        // 取缓存
        List<EnvConfigEntity> list = getEnvConfigEntity();
        //String v = HtmlUtils.getTablePageHtml(list);
        List<Map<String, ?>> mapList = new ArrayList<>();
        HashMap<String, String> v = HtmlUtils.getTableHead(EnvConfigEntity.class);
        mapList.add(v);
        if (list != null) {
            for (EnvConfigEntity entity : list) {
                mapList.add(MyCollectionUtils.toMap(entity));
            }
        }
        return mapList;
    }

    @Remind("只是为了测试，每一行代码都有测试意义，但是没有实际意义")
    @Override
    public List<EnvConfigEntity> getEnvConfigEntity() throws Exception {
        List<EnvConfigEntity> list = VariableUtils.getValue(VariableScope.SESSION, "envConfigEntity");
        if (list == null || list.isEmpty()) {
            list = new ArrayList<>();
            Properties properties = EnvironmentUtils.getProperties();
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                EnvConfigEntity entity = new EnvConfigEntity();
                entity.setProperty(StringUtils.valueOf(entry.getKey()));
                entity.setValue(StringUtils.valueOf(entry.getValue()));
                list.add(entity);
            }
            //监听 类似于MutableLiveData  所有变量都可以被监听变化  VariableScope.SESSION
            VariableUtils.registerVariableListeners(VariableScope.SESSION, "envConfigEntity", new VariableListener() {
                @Override
                public boolean onChange(VariableEventArgs event) {
                    LogUtils.d("old", JSON.toJSONString(event.getOldValue()));
                    LogUtils.d("new", JSON.toJSONString(event.getValue()));
                    return false;//true：执行后移除此监听
                }
            });
            //存缓存 内存
            VariableUtils.setSessionVariable("envConfigEntity", list);
            //测试 持久化
            envConfigRepository.getRepository().inserts(list);
            List<EnvConfigEntity> entities = envConfigRepository.getRepository().selectAll(EnvConfigEntity.class);
            list = entities;
        }
        return list;
    }
}
