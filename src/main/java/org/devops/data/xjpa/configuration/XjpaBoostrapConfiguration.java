package org.devops.data.xjpa.configuration;

import org.devops.data.xjpa.lifecycle.XjpaRepositoryAppLifecycle;
import org.devops.data.xjpa.lifecycle.XjpaRepositoryAppStopListener;
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
