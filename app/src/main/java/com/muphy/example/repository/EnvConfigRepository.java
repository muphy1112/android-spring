package com.muphy.example.repository;

import me.muphy.spring.annotation.Autowired;
import me.muphy.spring.annotation.Remind;
import me.muphy.spring.annotation.Repository;
import me.muphy.spring.platform.PersistentRepository;

@Remind("可以参考me.muphy.spring.platform.android.persistent.PersistentDao写dao层，这里直接使用的PersistentDao统一缓存")
@Repository
public class EnvConfigRepository {
    @Autowired
    private PersistentRepository repository;

    public PersistentRepository getRepository() {
        return repository;
    }
}
