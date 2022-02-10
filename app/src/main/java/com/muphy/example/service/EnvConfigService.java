package com.muphy.example.service;

import com.muphy.example.entity.EnvConfigEntity;

import java.util.List;
import java.util.Map;

import me.muphy.spring.annotation.Remind;

@Remind("云端环境配置配置服务")
public interface EnvConfigService {
    List<Map<String, ?>> getEnv() throws Exception;

    List<EnvConfigEntity> getEnvConfigEntity() throws Exception;
}
