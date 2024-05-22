package org.devops.data.xjpa.sql.executor.query;

import org.devops.data.xjpa.repository.impl.RepositoryContext;
import org.devops.data.xjpa.util.PrimitiveTypeUtil;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.util.Arrays;

/**
 * @author GENSEN
 * @date 2022/11/7
 * @description QueryRequest 构造
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class QueryRequestBuilder {

    private final Class<? extends AbstractQueryRequest> queryRequestType;

    private final RepositoryContext context;

    private QueryRequestBuilder(Class<? extends AbstractQueryRequest> queryRequestType, RepositoryContext context) {
        this.queryRequestType = queryRequestType;
        this.context = context;
    }

    /**
     * @param queryRequestType
     * @param context
     * @return
     */
    public static QueryRequestBuilder bind(Class queryRequestType, RepositoryContext context) {
        return new QueryRequestBuilder(queryRequestType, context);
    }

    /**
     * @param args
     * @return
     */
    public <T extends AbstractQueryRequest> T create(Object... args) {
        Constructor<?> suitableConstructor = findSuitableConstructor(queryRequestType, args);
        Object[] constructorArgs = new Object[args.length + 1];
        constructorArgs[0] = context;
        System.arraycopy(args, 0, constructorArgs, 1, args.length);
        return (T) BeanUtils.instantiateClass(suitableConstructor, constructorArgs);
    }

    /**
     * @param queryRequestType
     * @param args
     * @return
     */
    private Constructor<?> findSuitableConstructor(Class<? extends AbstractQueryRequest> queryRequestType, Object... args) {
        Constructor<?>[] declaredConstructors = queryRequestType.getDeclaredConstructors();
        Class[] requestParameterTypes = new Class[args.length + 1];
        requestParameterTypes[0] = RepositoryContext.class;
        for (int i = 0; i < args.length; i++) {
            requestParameterTypes[i + 1] = args[i].getClass();
        }
        // 找到参数长度一致，并且参数类型匹配的构造器
        ConstructorLoop:
        for (Constructor<?> declaredConstructor : declaredConstructors) {
            if (declaredConstructor.getParameterTypes().length != requestParameterTypes.length) {
                continue;
            }
            Class<?>[] parameterTypes = declaredConstructor.getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                if (!parameterTypes[i].isAssignableFrom(requestParameterTypes[i]) &&
                        !parameterTypes[i].isAssignableFrom(PrimitiveTypeUtil.getPrimitiveType(requestParameterTypes[i]))) {
                    continue ConstructorLoop;
                }
            }
            return declaredConstructor;
        }

        throw new IllegalArgumentException("no suitable constructor of [" + queryRequestType + "] with parameter type list: " +
                Arrays.toString(requestParameterTypes));
    }

}
