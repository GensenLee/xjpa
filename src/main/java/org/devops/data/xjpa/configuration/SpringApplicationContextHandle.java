package org.devops.data.xjpa.configuration;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

/**
 * @author GENSEN
 * @date 2022/11/16
 * @description spring
 */
public class SpringApplicationContextHandle implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringApplicationContextHandle.applicationContext == null) {
            SpringApplicationContextHandle.applicationContext = applicationContext;
        }
    }


    public static ApplicationContext getApplicationContext() {
        Assert.notNull(applicationContext, "application uninitialized");
        return applicationContext;
    }
}
