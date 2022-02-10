package me.muphy.spring.platform.android;

import com.faendir.rhino_android.RhinoAndroidHelper;
import me.muphy.spring.annotation.Bean;
import me.muphy.spring.annotation.Configuration;
import me.muphy.spring.platform.android.log.AndroidPlatformLogListener;
import me.muphy.spring.platform.android.persistent.AndroidPersistentRepository;
import me.muphy.spring.platform.android.shared.AndroidShared;

import org.mozilla.javascript.Context;

@Configuration
public class AndroidBeanInit {

    @Bean
    public Context getContext() {
        return new RhinoAndroidHelper(AndroidContextHolder.getContext()).enterContext();
    }

    @Bean
    public AndroidPlatformLogListener getAndroidPlatformLogListener() {
        return new AndroidPlatformLogListener();
    }

    @Bean
    public AndroidShared getAndroidShared() {
        return AndroidShared.getInstance();
    }

    @Bean
    public AndroidPersistentRepository getAndroidPersistentRepository() {
        return new AndroidPersistentRepository();
    }
}
