package org.devops.data.xjpa.lifecycle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import java.util.Optional;

/**
 * @author GENSEN
 * @date 2022/11/8
 * @description spring停止监听
 */
@Slf4j
public class XjpaRepositoryAppStopListener implements ApplicationListener<ContextClosedEvent> {

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {

        ObjectProvider<XjpaRepositoryAppLifecycle> beanProvider = event.getApplicationContext().getBeanProvider(XjpaRepositoryAppLifecycle.class);
        Optional<XjpaRepositoryAppLifecycle> optional = beanProvider.stream().findAny();
        if (!optional.isPresent()) {
            return;
        }

        XjpaRepositoryAppLifecycle appLifecycle = optional.get();
        if (appLifecycle.isRunning()) {
            appLifecycle.stop();
        }
    }
}
