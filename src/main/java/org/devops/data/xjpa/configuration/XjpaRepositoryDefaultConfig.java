package org.devops.data.xjpa.configuration;

import org.devops.data.xjpa.table.DefaultTableInitErrorHandler;
import org.devops.data.xjpa.table.TableInitErrorHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author GENSEN
 * @date 2023/1/4
 * @description 默认设置
 */
@Configuration
public class XjpaRepositoryDefaultConfig {

    @ConditionalOnMissingBean
    @Bean
    public TableInitErrorHandler defaultTableInitErrorHandler(){
        return new DefaultTableInitErrorHandler();
    }


}
