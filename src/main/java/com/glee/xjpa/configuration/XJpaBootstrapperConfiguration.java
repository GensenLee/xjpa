package com.glee.xjpa.configuration;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

/**
 * @author GENSEN
 * @date 2026/3/24
 * @description
 */
@Configuration
@ComponentScan(basePackages = "com.glee.xjpa")
@ConditionalOnClass(DataSource.class)
@EnableConfigurationProperties(XJpaProperties.class)
@AutoConfigureAfter({DataSourceAutoConfiguration.class, TransactionTemplate.class})
@Import(XJpaProxyRegistrar.class)
public class XJpaBootstrapperConfiguration implements PriorityOrdered {


    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
