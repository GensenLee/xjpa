package com.glee.xjpa.repository.impl.curd;

import com.glee.xjpa.configuration.RepositoriesConfigurationManager;
import com.glee.xjpa.repository.*;
import com.glee.xjpa.repository.impl.RepositoryContext;
import com.glee.xjpa.repository.impl.RepositoryProxyBeanFactory;
import com.glee.xjpa.repository.impl.RepositoryProxyBeanFactoryFactory;
import com.glee.xjpa.repository.*;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author GENSEN
 * @date 2022/11/3
 * @description 静态模式
 */
@SuppressWarnings({"rawtypes"})
public class StaticRepositoryProxyBeanFactoryFactory implements RepositoryProxyBeanFactoryFactory {

    private final RepositoriesConfigurationManager repositoriesConfigurationManager;

    private final Map<Type, Supplier<RepositoryProxyBeanFactory>> defaultTypeFactories;

    private final DefaultListableBeanFactory beanFactory;

    public StaticRepositoryProxyBeanFactoryFactory(RepositoriesConfigurationManager repositoriesConfigurationManager, DefaultListableBeanFactory beanFactory) {
        this.repositoriesConfigurationManager = repositoriesConfigurationManager;
        this.beanFactory = beanFactory;
        this.defaultTypeFactories = new HashMap<>();
        init();
    }

    private void init() {

        /*
        * 不使用 RegisterStandardJpaRepositoryImplProxyBeanFactory 是因为即使通过spring托管事务，在实现类中的写操作方法上加@Transactional注解控制事务
        * 也可能因为项目中使用了多数据源模式时使用了多个事务管理器，而本组件中无法动态选择事务管理器导致事务无效
        * 所以使用应当由外部调用者控制，保证写操作置于事务中即可
        * */
//        defaultTypeFactories.put(StandardJpaRepository.class, new RegisterStandardJpaRepositoryImplProxyBeanFactory(configurationManager, this));

        /*
        * !!!!!!! 2022-12-08 {{千万千万}} 不可使用静态初始化方式
        * defaultTypeFactories.put(StandardJpaRepository.class, new RegisterStandardJpaRepositoryImplProxyBeanFactory(configurationManager, this));
        * 这种方式导致并发时出现了多种奇奇怪怪的现象，还无法找到原因
        * 千万千万 千万千万，谨慎多线程共用实例
        *
        * */

        defaultTypeFactories.put(StandardJpaRepository.class, () -> new DefaultStandardJpaRepositoryProxyBeanFactory(repositoriesConfigurationManager, beanFactory, this));

        defaultTypeFactories.put(IEnhanceCurdRepository.class, () -> new DefaultStandardCurdRepositoryProxyBeanFactory(repositoriesConfigurationManager, this));
        defaultTypeFactories.put(IDeleteRepository.class, () -> new DefaultDeleteRepositoryProxyBeanFactory(repositoriesConfigurationManager, this));
//        defaultTypeFactories.put(SelectRepositoryProxyImpl.class, new DefaultSelectRepositoryProxyImplBeanFactory(modelConfigurationManager));
        defaultTypeFactories.put(ISelectRepository.class, () -> new SingleColumnSupportSelectRepositoryProxyBeanFactory(repositoriesConfigurationManager));
        defaultTypeFactories.put(IUpdateRepository.class, () -> new DefaultUpdateRepositoryProxyBeanFactory(repositoriesConfigurationManager));
        defaultTypeFactories.put(IInsertRepository.class, () -> new DefaultInsertRepositoryProxyBeanFactory(repositoriesConfigurationManager));
    }


    @Override
    public RepositoryProxyBeanFactory getFactory(Type beanType, RepositoryContext context) {
        Supplier<RepositoryProxyBeanFactory> factorySupplier = defaultTypeFactories.get(beanType);
        if (factorySupplier == null) {
            throw new IllegalArgumentException("not support type : " + beanType.getTypeName());
        }
        RepositoryProxyBeanFactory beanFactory = factorySupplier.get();
        if (beanFactory == null) {
            throw new IllegalArgumentException("not support type : " + beanType.getTypeName());
        }
        beanFactory.bindContext(context);

        return beanFactory;
    }

    public DefaultListableBeanFactory getBeanFactory() {
        return beanFactory;
    }
}
