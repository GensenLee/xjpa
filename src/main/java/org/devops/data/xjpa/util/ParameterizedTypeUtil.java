package org.devops.data.xjpa.util;


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
        // 获取泛型参数
        Optional<Type> typeOptional = Arrays.stream(beanType.getGenericInterfaces())
                .filter(t -> superType.isAssignableFrom((Class<?>) ((ParameterizedType) t).getRawType()))
                .findFirst();
        if (!typeOptional.isPresent()) {
            throw new IllegalArgumentException(superType + " register error, parameterizedType is not present");
        }

        ParameterizedType superType = (ParameterizedType) typeOptional.get();
        return superType.getActualTypeArguments()[index];
    }


}
