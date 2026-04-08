package com.glee.xjpa.proxy;

import com.glee.xjpa.io.StandardXJpaRepository;

import java.io.Serializable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author GENSEN
 * @date 2022/11/3
 * @description 方法代理
 */
public class RepositoryInvocationHandler<K extends Serializable, E> implements InvocationHandler {

    RepositoryInvocationHandler(StandardXJpaRepository<K, E> repository) {
        this.repository = repository;
    }


    private final StandardXJpaRepository<K, E> repository;


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            if (isDefaultMethod(method)) {
                return invokeViaMethodHandles(proxy, method, args);
            }
            return method.invoke(repository, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }


    /**
     * 判断 Method 是否为接口的 default 方法
     */
    public static boolean isDefaultMethod(Method method) {
        // 方式1：通过修饰符判断
        int modifiers = method.getModifiers();
        if (!Modifier.isAbstract(modifiers) && !Modifier.isStatic(modifiers)) {
            // 获取声明该方法的类
            Class<?> declaringClass = method.getDeclaringClass();
            if (declaringClass.isInterface()) {
                // 接口中的非抽象、非静态方法就是 default 方法
                return true;
            }
        }

        // 方式2：更严格的检查
        return method.isDefault();
    }


    /**
     * 使用 MethodHandles 调用 default 方法
     */
    public static Object invokeViaMethodHandles(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> declaringClass = method.getDeclaringClass();
        MethodHandles.Lookup lookup = MethodHandles.lookup();

        // 创建 MethodHandle
        MethodHandles.Lookup privateLookup = MethodHandles.privateLookupIn(declaringClass, lookup);
        MethodType methodType = MethodType.methodType(method.getReturnType(), method.getParameterTypes());
        MethodHandle methodHandle = privateLookup.findSpecial(
                declaringClass,
                method.getName(),
                methodType,
                declaringClass
        );

        // 调用方法
        return methodHandle.bindTo(proxy).invokeWithArguments(args);
    }

}
