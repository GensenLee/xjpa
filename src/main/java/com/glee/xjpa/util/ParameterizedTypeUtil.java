package com.glee.xjpa.util;


import com.glee.xjpa.exception.XjpaException;
import com.glee.xjpa.repository.StandardJpaRepository;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author GENSEN
 * @date 2022/11/4
 * @description ParameterizedType泛型工具
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ParameterizedTypeUtil {

    private final Class superType;

    public ParameterizedTypeUtil(Class superType) {
        this.superType = superType;
    }

    /**
     * @param beanType
     * @param index
     * @return
     */
    public Type getParameterizedType(Class<?> beanType, int index) {
        Class type = getDefaultImplementType(beanType);

        if (type == null) {
            throw new XjpaException("%s 未继承 %s 接口".formatted(beanType, StandardJpaRepository.class));
        }

        // 获取泛型参数
        Optional<Type> typeOptional = Arrays.stream(type.getGenericInterfaces())
                .filter(t -> superType.isAssignableFrom((Class<?>) ((ParameterizedType) t).getRawType()))
                .findFirst();
        if (typeOptional.isEmpty()) {
            throw new IllegalArgumentException(superType + " register error, parameterizedType is not present");
        }

        ParameterizedType superType = (ParameterizedType) typeOptional.get();
        return superType.getActualTypeArguments()[index];
    }

    public static boolean isDefaultImplementType(Class type) {
        Type[] genericInterfaces = type.getGenericInterfaces();
        if (genericInterfaces.length != 1) {
            return false;
        }

        if (genericInterfaces[0] instanceof ParameterizedType parameterizedType) {
            return parameterizedType.getRawType().equals(StandardJpaRepository.class);
        }
        return false;
    }

    public static Class getDefaultImplementType(Class type) {
        if (isDefaultImplementType(type)) {
            return type;
        }

        Class[] interfaces = type.getInterfaces();
        if (interfaces.length == 0) {
            return null;
        }

        for (Class anInterface : interfaces) {
            Class defaultImplementType = getDefaultImplementType(anInterface);
            if (defaultImplementType != null) {
                return defaultImplementType;
            }
        }

        return null;
    }

}
