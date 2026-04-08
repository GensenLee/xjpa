package com.glee.xjpa.configuration;

import com.glee.xjpa.table.TableFieldProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.List;

/**
 * @author GENSEN
 * @date 2026/3/24
 * @description 表字段加载
 */
public class TableFieldsLoader implements ApplicationRunner {
    private final static Logger log = LoggerFactory.getLogger(TableFieldsLoader.class);

    private final List<TableFieldProvider> tableFieldProviderList;

    public TableFieldsLoader(List<TableFieldProvider> tableFieldProviderList) {
        this.tableFieldProviderList = tableFieldProviderList;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.debug("table load start");
        for (TableFieldProvider provider : tableFieldProviderList) {
            provider.load();
        }
        log.debug("table load finished");
    }
}
