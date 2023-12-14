package org.devops.data.xjpa.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author GENSEN
 * @date 2022/10/31
 * @description 类型工具
 */
public class PrimitiveTypeUtil {

    /**
     * wrap type -> primitive type
     */
    private static final Map<Class<?>, Class<?>> TYPE_CONVERT_MAP = new HashMap<>();


    static {
        TYPE_CONVERT_MAP.put(String.class, String.class);
        TYPE_CONVERT_MAP.put(Date.class, String.class);
        TYPE_CONVERT_MAP.put(Integer.class, Integer.TYPE);
        TYPE_CONVERT_MAP.put(Long.class, Long.TYPE);
        TYPE_CONVERT_MAP.put(Double.class, Double.TYPE);
        TYPE_CONVERT_MAP.put(Boolean.class, Boolean.TYPE);
        TYPE_CONVERT_MAP.put(Byte.class, Byte.TYPE);
        TYPE_CONVERT_MAP.put(Float.class, Float.TYPE);
        TYPE_CONVERT_MAP.put(Short.class, Short.TYPE);
    }

    public static Set<Class<?>> wrapTypes(){
        return TYPE_CONVERT_MAP.keySet();
    }

    public static Class<?> getPrimitiveType(Class<?> type) {
        return TYPE_CONVERT_MAP.getOrDefault(type, type);
    }


}
