package com.glee.xjpa.configuration;

import com.glee.xjpa.lifecycle.XjpaRepositoryAppLifecycle;
import com.glee.xjpa.lifecycle.XjpaRepositoryAppStopListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        XjpaRepositoryDefaultConfig.class,
        XjpaRepositoryAppLifecycle.class,
        XjpaRepositoryAppStopListener.class,
        SpringApplicationContextHandle.class
})
public class XjpaBoostrapConfiguration {

}
